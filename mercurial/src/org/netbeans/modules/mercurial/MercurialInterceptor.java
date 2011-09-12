/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.mercurial;

import java.util.Map;
import javax.swing.SwingUtilities;
import org.netbeans.modules.versioning.spi.VCSInterceptor;
import java.io.File;
import java.io.IOException;
import org.netbeans.modules.mercurial.util.HgCommand;
import org.openide.util.RequestProcessor;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import org.netbeans.modules.mercurial.util.HgUtils;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.netbeans.modules.mercurial.util.HgSearchHistorySupport;
import org.netbeans.modules.versioning.util.DelayScanRegistry;
import org.netbeans.modules.versioning.util.FileUtils;
import org.netbeans.modules.versioning.util.SearchHistorySupport;
import org.netbeans.modules.versioning.util.Utils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileUtil;


/**
 * Listens on file system changes and reacts appropriately, mainly refreshing affected files' status.
 * 
 * @author Maros Sandor
 */
public class MercurialInterceptor extends VCSInterceptor {

    private final FileStatusCache   cache;

    private ConcurrentLinkedQueue<File> filesToRefresh = new ConcurrentLinkedQueue<File>();
    private final Map<File, Set<File>> lockedRepositories = new HashMap<File, Set<File>>(5);

    private final RequestProcessor.Task refreshTask, lockedRepositoryRefreshTask;

    private static final RequestProcessor rp = new RequestProcessor("MercurialRefresh", 1, true);
    private final HgFolderEventsHandler hgFolderEventsHandler;
    private static final boolean AUTOMATIC_REFRESH_ENABLED = !"true".equals(System.getProperty("versioning.mercurial.autoRefreshDisabled", "false")); //NOI18N
    private final Mercurial hg;

    MercurialInterceptor(Mercurial hg, FileStatusCache cache) {
        this.cache = cache;
        this.hg = hg;
        refreshTask = rp.create(new RefreshTask());
        lockedRepositoryRefreshTask = rp.create(new LockedRepositoryRefreshTask());
        hgFolderEventsHandler = new HgFolderEventsHandler();
    }

    @Override
    public boolean beforeDelete(File file) {
        Mercurial.LOG.log(Level.FINE, "beforeDelete {0}", file);
        if (file == null) return false;
        if (HgUtils.isPartOfMercurialMetadata(file)) return false;

        // we don't care about ignored files
        // IMPORTANT: false means mind checking the sharability as this might cause deadlock situations
        if(HgUtils.isIgnored(file, false)) return false; // XXX what about other events?
        return true;
    }

    @Override
    public void doDelete(File file) throws IOException {
        // XXX runnig hg rm for each particular file when removing a whole firectory might no be neccessery:
        //     just delete it via file.delete and call, group the files in afterDelete and schedule a delete
        //     fo the parent or for a bunch of files at once. 
        Mercurial.LOG.log(Level.FINE, "doDelete {0}", file);
        if (file == null) return;
        File root = hg.getRepositoryRoot(file);
        try {
            file.delete();
            HgCommand.doRemove(root, file, null);
        } catch (HgException ex) {
            Mercurial.LOG.log(Level.FINE, "doDelete(): File: {0} {1}", new Object[] {file.getAbsolutePath(), ex.toString()}); // NOI18N
        }  
    }

    @Override
    public void afterDelete(final File file) {
        Mercurial.LOG.log(Level.FINE, "afterDelete {0}", file);
        if (file == null) return;
        // we don't care about ignored files
        // IMPORTANT: false means mind checking the sharability as this might cause deadlock situations
        if(HgUtils.isIgnored(file, false)) {
            if (Mercurial.LOG.isLoggable(Level.FINER)) {
                Mercurial.LOG.log(Level.FINE, "skipping afterDelete(): File: {0} is ignored", new Object[] {file.getAbsolutePath()}); // NOI18N
            }
            return;
        }
        reScheduleRefresh(800, file);
    }

    @Override
    public boolean beforeMove(File from, File to) {
        Mercurial.LOG.log(Level.FINE, "beforeMove {0}->{1}", new Object[]{from, to});
        if (from == null || to == null || to.exists()) return true;
        
        if (hg.isManaged(from)) {
            return hg.isManaged(to);
        }
        return super.beforeMove(from, to);
    }

    @Override
    public void doMove(final File from, final File to) throws IOException {
        Mercurial.LOG.log(Level.FINE, "doMove {0}->{1}", new Object[]{from, to});
        if (from == null || to == null || to.exists()) return;
        if (SwingUtilities.isEventDispatchThread()) {
            Mercurial.LOG.log(Level.INFO, "Warning: launching external process in AWT", new Exception().fillInStackTrace()); // NOI18N
        }
        hgMoveImplementation(from, to);
    }

    @Override
    public long refreshRecursively(File dir, long lastTimeStamp, List<? super File> children) {
        long retval = -1;
        if (HgUtils.HG_FOLDER_NAME.equals(dir.getName())) {
            Mercurial.STATUS_LOG.log(Level.FINER, "Interceptor.refreshRecursively: {0}", dir.getAbsolutePath()); //NOI18N
            children.clear();
            retval = hgFolderEventsHandler.handleHgFolderEvent(dir);
        }
        return retval;
    }

    @Override
    public boolean isMutable(File file) {
        return HgUtils.isPartOfMercurialMetadata(file) || super.isMutable(file);
    }

    private void hgMoveImplementation(final File srcFile, final File dstFile) throws IOException {
        final File root = hg.getRepositoryRoot(srcFile);
        final File dstRoot = hg.getRepositoryRoot(dstFile);

        Mercurial.LOG.log(Level.FINE, "hgMoveImplementation(): File: {0} {1}", new Object[] {srcFile, dstFile}); // NOI18N

        boolean result = srcFile.renameTo(dstFile);
        if (!result) {
            Mercurial.LOG.log(Level.INFO, "Cannot rename file {0} to {1}", new Object[] {srcFile, dstFile});
        }
        if (root == null) {
            return;
        }
        // no need to do rename after in a background thread, code requiring the bg thread (see #125673) no more exists
        OutputLogger logger = OutputLogger.getLogger(root.getAbsolutePath());
        try {
            if (root.equals(dstRoot) && !HgUtils.isIgnored(dstFile, false)) {
                // target does not lie under ignored folder and is in the same repo as src
                HgCommand.doRenameAfter(root, srcFile, dstFile, logger);
            } else {
                // just hg rm the old file
                HgCommand.doRemove(root, srcFile, logger);
            }
        } catch (HgException e) {
            Mercurial.LOG.log(Level.FINE, "Mercurial failed to rename: File: {0} {1}", new Object[]{srcFile.getAbsolutePath(), dstFile.getAbsolutePath()}); // NOI18N
        } finally {
            logger.closeLog();
        }
    }

    @Override
    public void afterMove(final File from, final File to) {
        Mercurial.LOG.log(Level.FINE, "afterMove {0}->{1}", new Object[]{from, to});
        if (from == null || to == null || !to.exists()) return;

        File parent = from.getParentFile();
        // There is no point in refreshing the cache for ignored files.
        if (parent != null && !HgUtils.isIgnored(parent, false)) {
            reScheduleRefresh(800, from);
        }
        // target needs to refreshed, too
        parent = to.getParentFile();
        // There is no point in refreshing the cache for ignored files.
        if (parent != null && !HgUtils.isIgnored(parent, false)) {
            reScheduleRefresh(800, to);
        }
    }

    @Override
    public boolean beforeCopy (File from, File to) {
        Mercurial.LOG.log(Level.FINE, "beforeCopy {0}->{1}", new Object[]{from, to});
        if (from == null || to == null || to.exists()) return true;

        return hg.isManaged(from) && hg.isManaged(to);
    }

    @Override
    public void doCopy (final File from, final File to) throws IOException {
        Mercurial.LOG.log(Level.FINE, "doCopy {0}->{1}", new Object[]{from, to});
        if (from == null || to == null || to.exists()) return;

        File root = hg.getRepositoryRoot(from);
        File dstRoot = hg.getRepositoryRoot(to);

        if (from.isDirectory()) {
            FileUtils.copyDirFiles(from, to);
        } else {
            FileUtils.copyFile(from, to);
        }

        if (root == null || HgUtils.isIgnored(to, false)) {
            // target lies under ignored folder, do not add it
            return;
        }
        OutputLogger logger = OutputLogger.getLogger(root.getAbsolutePath());
        try {
            if (root.equals(dstRoot)) {
                HgCommand.doCopy(root, from, to, true, logger);
            }
        } catch (HgException e) {
            Mercurial.LOG.log(Level.FINE, "Mercurial failed to copy: File: {0} {1}", new Object[] { from.getAbsolutePath(), to.getAbsolutePath() } ); // NOI18N
        } finally {
            logger.closeLog();
        }
    }

    @Override
    public void afterCopy (final File from, final File to) {
        Mercurial.LOG.log(Level.FINE, "afterCopy {0}->{1}", new Object[]{from, to});
        if (to == null) return;

        File parent = to.getParentFile();
        // There is no point in refreshing the cache for ignored files.
        if (parent != null && !HgUtils.isIgnored(parent, false)) {
            reScheduleRefresh(800, to);
        }
    }
    
    @Override
    public boolean beforeCreate(final File file, boolean isDirectory) {
        Mercurial.LOG.log(Level.FINE, "beforeCreate {0} {1}", new Object[]{file, isDirectory});
        if (HgUtils.isPartOfMercurialMetadata(file)) return false;
        if (!isDirectory && !file.exists()) {
            File root = hg.getRepositoryRoot(file);
            FileInformation info = null;
            try {
                Map<File, FileInformation> statusMap = HgCommand.getStatus(root, Arrays.asList(file), null, null);
                info = statusMap != null ? statusMap.get(file) : null;
            } catch (HgException ex) {
                Mercurial.LOG.log(Level.FINE, "beforeCreate(): getStatus failed for file: {0} {1}", new Object[]{file.getAbsolutePath(), ex.toString()}); // NOI18N
            }
            if (info != null && info.getStatus() == FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY) {
                Mercurial.LOG.log(Level.FINE, "beforeCreate(): LocallyDeleted: {0}", file); // NOI18N
                if (root == null) return false;
                final OutputLogger logger = hg.getLogger(root.getAbsolutePath());
                try {
                    List<File> revertFiles = new ArrayList<File>();
                    revertFiles.add(file);
                    HgCommand.doRevert(root, revertFiles, null, false, logger);
                } catch (HgException ex) {
                    Mercurial.LOG.log(Level.FINE, "beforeCreate(): File: {0} {1}", new Object[]{file.getAbsolutePath(), ex.toString()}); // NOI18N
                }
                Mercurial.LOG.log(Level.FINE, "beforeCreate(): afterWaitFinished: {0}", file); // NOI18N
                logger.closeLog();
                file.delete();
            }
        }
        return false;
    }

    @Override
    public void doCreate(File file, boolean isDirectory) throws IOException {
        Mercurial.LOG.log(Level.FINE, "doCreate {0} {1}", new Object[]{file, isDirectory});
        super.doCreate(file, isDirectory);
    }

    @Override
    public void afterCreate(final File file) {
        Mercurial.LOG.log(Level.FINE, "afterCreate {0}", file);
        // There is no point in refreshing the cache for ignored files.
        if (!HgUtils.isIgnored(file, false)) {
            reScheduleRefresh(800, file);
        }
    }
    
    @Override
    public void afterChange(final File file) {
        if (file.isDirectory()) return;
        Mercurial.LOG.log(Level.FINE, "afterChange(): {0}", file);      //NOI18N
        // There is no point in refreshing the cache for ignored files.
        if (!HgUtils.isIgnored(file, false)) {
            reScheduleRefresh(800, file);
        }
    }

    @Override
    public Object getAttribute(final File file, String attrName) {
        if("ProvidedExtensions.RemoteLocation".equals(attrName)) {
            return getRemoteRepository(file);
        } else if("ProvidedExtensions.Refresh".equals(attrName)) {
            return new Runnable() {
                @Override
                public void run() {
                    FileStatusCache cache = hg.getFileStatusCache();
                    cache.refresh(file);
                }
            };
        } else if (SearchHistorySupport.PROVIDED_EXTENSIONS_SEARCH_HISTORY.equals(attrName)){
            return new HgSearchHistorySupport(file);
        } else {
            return super.getAttribute(file, attrName);
        }
    }

    private String getRemoteRepository(File file) {
        return HgUtils.getRemoteRepository(file);
    }

    private void reScheduleRefresh(int delayMillis, File fileToRefresh) {
        // refresh all at once
        Mercurial.STATUS_LOG.log(Level.FINE, "reScheduleRefresh: adding {0}", fileToRefresh.getAbsolutePath());
        if (!HgUtils.isPartOfMercurialMetadata(fileToRefresh)) {
            filesToRefresh.add(fileToRefresh);
        }
        refreshTask.schedule(delayMillis);
    }

    private void reScheduleRefresh (int delayMillis, Set<File> filesToRefresh) {
        // refresh all at once
        Mercurial.STATUS_LOG.log(Level.FINE, "reScheduleRefresh: adding {0}", filesToRefresh);
        this.filesToRefresh.addAll(filesToRefresh);
        refreshTask.schedule(delayMillis);
    }

    /**
     * Checks if administrative folder for a repository with the file is registered.
     * @param file
     */
    void pingRepositoryRootFor(final File file) {
        if (!AUTOMATIC_REFRESH_ENABLED) {
            return;
        }
        hgFolderEventsHandler.initializeFor(file);
    }

    /**
     * Runs a given callable and disable listening for external repository events for the time the callable is running.
     * Refreshes cached modification timestamp of hg administrative folder file for the given repository after.
     * @param callable code to run
     * @param repository
     */
    <T> T runWithoutExternalEvents(File repository, Callable<T> callable) throws Exception {
        assert repository != null;
        try {
            if (repository != null) {
                hgFolderEventsHandler.enableEvents(repository, false);
            }
            return callable.call();
        } finally {
            if (repository != null) {
                hgFolderEventsHandler.enableEvents(repository, true);
                File hgFolder = HgUtils.getHgFolderForRoot(repository);
                hgFolderEventsHandler.refreshHgFolderTimestamp(hgFolder, hgFolder.lastModified());
            }
        }
    }

    /**
     * Returns a set of known repository roots (those visible or open in IDE)
     * @param repositoryRoot
     * @return
     */
    Set<File> getSeenRoots (File repositoryRoot) {
        return hgFolderEventsHandler.getSeenRoots(repositoryRoot);
    }

    private class RefreshTask implements Runnable {
        @Override
        public void run() {
            Thread.interrupted();
            if (DelayScanRegistry.getInstance().isDelayed(refreshTask, Mercurial.STATUS_LOG, "MercurialInterceptor.refreshTask")) { //NOI18N
                return;
            }
            // fill a fileset with all the modified files
            Collection<File> files = new HashSet<File>(filesToRefresh.size());
            File file;
            while ((file = filesToRefresh.poll()) != null) {
                files.add(file);
            }
            if (!"false".equals(System.getProperty("versioning.mercurial.delayStatusForLockedRepositories"))) {
                files = checkLockedRepositories(files, false);
            }
            if (!files.isEmpty()) {
                cache.refreshAllRoots(files);
            }
            if (!lockedRepositories.isEmpty()) {
                lockedRepositoryRefreshTask.schedule(5000);
            }
        }
    }

    private Collection<File> checkLockedRepositories (Collection<File> additionalFilesToRefresh, boolean keepCached) {
        List<File> retval = new LinkedList<File>();
        // at first sort the files under repositories
        Map<File, Set<File>> sortedFiles = sortByRepository(additionalFilesToRefresh);
        for (Map.Entry<File, Set<File>> e : sortedFiles.entrySet()) {
            Set<File> alreadyPlanned = lockedRepositories.get(e.getKey());
            if (alreadyPlanned == null) {
                alreadyPlanned = new HashSet<File>();
                lockedRepositories.put(e.getKey(), alreadyPlanned);
            }
            alreadyPlanned.addAll(e.getValue());
        }
        // return all files that do not belong to a locked repository 
        for (Iterator<Map.Entry<File, Set<File>>> it = lockedRepositories.entrySet().iterator(); it.hasNext();) {
            Map.Entry<File, Set<File>> entry = it.next();
            File repository = entry.getKey();
            if (!repository.exists()) {
                // repository does not exist, no need to keep it
                it.remove();
            } else if (HgUtils.isRepositoryLocked(repository)) {
                Mercurial.STATUS_LOG.log(Level.FINE, "checkLockedRepositories(): Repository {0} locked, status refresh delayed", repository); //NOI18N
            } else {
                // repo not locked, add all files into the returned collection
                retval.addAll(entry.getValue());
                if (!keepCached) {
                    it.remove();
                }
            }
        }
        return retval;
    }

    private Map<File, Set<File>> sortByRepository (Collection<File> files) {
        Map<File, Set<File>> sorted = new HashMap<File, Set<File>>(5);
        for (File f : files) {
            File repository = hg.getRepositoryRoot(f);
            if (repository != null) {
                Set<File> repoFiles = sorted.get(repository);
                if (repoFiles == null) {
                    repoFiles = new HashSet<File>();
                    sorted.put(repository, repoFiles);
                }
                repoFiles.add(f);
            }
        }
        return sorted;
    }

    private class LockedRepositoryRefreshTask implements Runnable {
        @Override
        public void run() {
            if (!checkLockedRepositories(Collections.<File>emptySet(), true).isEmpty()) {
                // there are some newly unlocked repositories to refresh
                refreshTask.schedule(0);
            } else if (!lockedRepositories.isEmpty()) {
                lockedRepositoryRefreshTask.schedule(5000);
            }
        }
    }

    private class HgFolderEventsHandler {
        private final HashMap<File, Long> hgFolders = new HashMap<File, Long>(5);
        private final HashMap<File, FileChangeListener> hgFolderRLs = new HashMap<File, FileChangeListener>(5);
        private final HashMap<File, Set<File>> seenRoots = new HashMap<File, Set<File>>(5);
        private final HashSet<File> disabledEvents = new HashSet<File>(5);

        private final HashSet<File> filesToInitialize = new HashSet<File>();
        private RequestProcessor rp = new RequestProcessor("MercurialInterceptorEventsHandlerRP", 1); //NOI18N
        private RequestProcessor.Task initializingTask = rp.create(new Runnable() {
            @Override
            public void run() {
                initializeFiles();
            }
        });
        private RequestProcessor.Task refreshOpenFilesTask = rp.create(new Runnable() {
            @Override
            public void run() {
                Set<File> openFiles = Utils.getOpenFiles();
                for (File file : openFiles) {
                    hg.notifyFileChanged(file);
                }
            }
        });

        /**
         *
         * @param hgFolder
         * @param timestamp new timestamp, value 0 will remove the item from the cache
         */
        private void refreshHgFolderTimestamp(File hgFolder, long timestamp) {
            boolean exists = timestamp > 0 || hgFolder.exists();
            synchronized (hgFolders) {
                Long ts;
                if (exists && (ts = hgFolders.get(hgFolder)) != null && ts >= timestamp) {
                    // do not enter the filesystem module unless really need to
                    return;
                }
            }
            synchronized (hgFolders) {
                hgFolders.remove(hgFolder);
                FileChangeListener list = hgFolderRLs.remove(hgFolder);
                if (exists) {
                    hgFolders.put(hgFolder, Long.valueOf(timestamp));
                    if (list == null) {
                        FileUtil.addRecursiveListener(list = new FileChangeAdapter(), hgFolder);
                    }
                    hgFolderRLs.put(hgFolder, list);
                } else {
                    if (list != null) {
                        FileUtil.removeRecursiveListener(list, hgFolder);
                    }
                    Mercurial.STATUS_LOG.log(Level.FINE, "refreshHgFolderTimestamp: {0} no longer exists", hgFolder.getAbsolutePath()); //NOI18N
                }
            }
        }

        private long handleHgFolderEvent(File hgFolder) {
            long lastModified = 0;
            if (AUTOMATIC_REFRESH_ENABLED && !"false".equals(System.getProperty("mercurial.handleDirstateEvents", "true"))) { //NOI18N
                hgFolder = FileUtil.normalizeFile(hgFolder);
                Mercurial.STATUS_LOG.log(Level.FINER, "handleHgFolderEvent: special FS event handling for {0}", hgFolder.getAbsolutePath()); //NOI18N
                lastModified = hgFolder.lastModified();
                Long lastCachedModified = null;
                synchronized (hgFolders) {
                    lastCachedModified = hgFolders.get(hgFolder);
                    if (lastCachedModified == null || lastCachedModified < lastModified || lastModified == 0) {
                        if (!isEnabled(hgFolder)) {
                            return lastModified;
                        }
                        refreshHgFolderTimestamp(hgFolder, lastModified);
                        lastCachedModified = null;
                    }
                }
                if (lastCachedModified == null) {
                    File repository = hgFolder.getParentFile();
                    Mercurial.STATUS_LOG.log(Level.FINE, "handleDirstateEvent: planning repository scan for {0}", repository.getAbsolutePath()); //NOI18N
                    reScheduleRefresh(3000, getSeenRoots(repository)); // scan repository root
                    refreshOpenFilesTask.schedule(3000);
                    WorkingCopyInfo.refreshAsync(repository);
                }
            }
            return lastModified;
        }

        public void initializeFor (File file) {
            if (addFileToInitialize(file)) {
                initializingTask.schedule(500);
            }
        }

        private Set<File> getSeenRoots (File repositoryRoot) {
            Set<File> retval = new HashSet<File>();
            Set<File> seenRootsForRepository = getSeenRootsForRepository(repositoryRoot);
            synchronized (seenRootsForRepository) {
                 retval.addAll(seenRootsForRepository);
            }
            return retval;
        }

        private File addSeenRoot (File repositoryRoot, File rootToAdd) {
            File addedRoot = null;
            Set<File> seenRootsForRepository = getSeenRootsForRepository(repositoryRoot);
            synchronized (seenRootsForRepository) {
                if (!seenRootsForRepository.contains(repositoryRoot)) {
                    // try to add the file only when the repository root is not yet registered
                    rootToAdd = FileUtil.normalizeFile(rootToAdd);
                    addedRoot = HgUtils.prepareRootFiles(repositoryRoot, seenRootsForRepository, rootToAdd);
                }
            }
            return addedRoot;
        }

        private Set<File> getSeenRootsForRepository (File repositoryRoot) {
            synchronized (seenRoots) {
                 Set<File> seenRootsForRepository = seenRoots.get(repositoryRoot);
                 if (seenRootsForRepository == null) {
                     seenRoots.put(repositoryRoot, seenRootsForRepository = new HashSet<File>());
                 }
                 return seenRootsForRepository;
            }
        }

        private boolean addFileToInitialize(File file) {
            synchronized (filesToInitialize) {
                return filesToInitialize.add(file);
            }
        }

        private File getFileToInitialize () {
            File nextFile = null;
            synchronized (filesToInitialize) {
                Iterator<File> iterator = filesToInitialize.iterator();
                if (iterator.hasNext()) {
                    nextFile = iterator.next();
                    iterator.remove();
                }
            }
            return nextFile;
        }
        
        private void initializeFiles () {
            File file = null;
            while ((file = getFileToInitialize()) != null) {
                // select repository root for the file and finds it's .hg folder
                File repositoryRoot = hg.getRepositoryRoot(file);
                if (repositoryRoot != null) {
                    File hgFolder = FileUtil.normalizeFile(HgUtils.getHgFolderForRoot(repositoryRoot));
                    File newlyAddedRoot = addSeenRoot(repositoryRoot, file);
                    if (newlyAddedRoot != null) {
                        synchronized (hgFolders) {
                            // this means the repository has not yet been scanned, so scan it
                            Mercurial.STATUS_LOG.log(Level.FINE, "pingRepositoryRootFor: planning a scan for {0} - {1}", new Object[]{repositoryRoot.getAbsolutePath(), file.getAbsolutePath()}); //NOI18N
                            reScheduleRefresh(4000, newlyAddedRoot);
                            if (!hgFolders.containsKey(hgFolder)) {
                                if (hgFolder.isDirectory()) {
                                    // however there might be NO .hg folder, especially for just initialized repositories
                                    // so keep the reference only for existing and valid .hg folders
                                    hgFolders.put(hgFolder, null);
                                    refreshHgFolderTimestamp(hgFolder, hgFolder.lastModified());
                                }
                            }
                        }
                    }
                }
            }
        }

        private void enableEvents (File repository, boolean enabled) {
            File hgFolder = FileUtil.normalizeFile(HgUtils.getHgFolderForRoot(repository));
            synchronized (disabledEvents) {
                if (enabled) {
                    disabledEvents.remove(hgFolder);
                } else {
                    disabledEvents.add(hgFolder);
                }
            }
        }

        private boolean isEnabled (File hgFolder) {
            synchronized (disabledEvents) {
                return !disabledEvents.contains(hgFolder);
            }
        }
    }
}
