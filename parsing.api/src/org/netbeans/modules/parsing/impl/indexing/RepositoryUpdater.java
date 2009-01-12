/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.parsing.impl.indexing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.modules.parsing.impl.Utilities;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;
import org.openide.util.TopologicalSortException;

/**
 *
 * @author Tomas Zezula
 */
public class RepositoryUpdater implements PathRegistryListener, FileChangeListener {

    private static RepositoryUpdater instance;

    private static final Logger LOGGER = Logger.getLogger(RepositoryUpdater.class.getName());
    private static final Logger TEST_LEGGER = Logger.getLogger(RepositoryUpdater.class.getName()+".tests"); //NOI18N

    private final Set<URL>scannedRoots = Collections.synchronizedSet(new HashSet<URL>());
    private final Set<URL>scannedBinaries = Collections.synchronizedSet(new HashSet<URL>());
    private final Set<URL>scannedUnknown = Collections.synchronizedSet(new HashSet<URL>());
    private final PathRegistry regs = PathRegistry.getDefault();
    private final PathRecognizerRegistry recognizers = PathRecognizerRegistry.getDefault();
    private volatile State state = State.CREATED;
    private volatile Task worker;

    private RepositoryUpdater () {
        init ();
    }

    /**
     * Used by unit tests
     * @return
     */
    State getState () {
        return state;
    }

    private synchronized void init () {
        if (state == State.CREATED) {
            LOGGER.fine("Initializing...");
            regs.addPathRegistryListener(this);
            registerFileSystemListener();
            final Work w = new RootsWork (WorkType.COMPILE_BATCH);
            submit (w);
            state = State.INITIALIZED;
        }
    }
    //where
    private void registerFileSystemListener  () {
        FileUtil.addFileChangeListener(this);
    }

    public synchronized void close () {
        state = State.CLOSED;
        LOGGER.fine("Closing...");
        this.regs.removePathRegistryListener(this);
        this.unregisterFileSystemListener();
    }
    //where
    private void unregisterFileSystemListener () {
        FileUtil.removeFileChangeListener(this);
    }


    private void submit (final Work  work) {
        Task t = getWorker ();
        assert t != null;
        LOGGER.fine("Scheduling " + work);
        t.schedule (work);
    }
    //where
    private Task getWorker () {
        Task t = this.worker;
        if (t == null) {
            synchronized (this) {
                if (this.worker == null) {
                    this.worker = new Task ();
                }
                t = this.worker;
            }
        }
        return t;
    }

    public void pathsChanged(PathRegistryEvent event) {
        assert event != null;
        if (LOGGER.isLoggable(Level.FINE)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Paths changed:\n");
            for(PathRegistryEvent.Change c : event.getChanges()) {
                sb.append(" event=").append(c.getEventKind());
                sb.append(" pathKind=").append(c.getPathKind());
                sb.append(" pathType=").append(c.getPathType());
                sb.append(" affected paths:\n");
                for(ClassPath cp : c.getAffectedPaths()) {
                    sb.append("  ");
                    sb.append(cp.toString(ClassPath.PathConversionMode.PRINT));
                    sb.append("\n");
                }
                sb.append("--");
            }
            sb.append("====");
            LOGGER.fine(sb.toString());
        }
        final Work w = new RootsWork (WorkType.COMPILE_BATCH);
        submit (w);
    }
    
    public static synchronized RepositoryUpdater getDefault() {
        if (instance == null) {
            instance = new RepositoryUpdater();
        }
        return instance;
    }

    public void fileFolderCreated(FileEvent fe) {
        //In ideal case this should do nothing,
        //but in Netbeans newlly created folder may
        //already contain files
        final FileObject fo = fe.getFile();
        final URL root = getOwningSourceRoot(fo);
        if ( root != null && VisibilityQuery.getDefault().isVisible(fo)) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Folder created: "+FileUtil.getFileDisplayName(fo)+" Owner: " + root);
            }
            final Work w = new FileListWork(WorkType.COMPILE,root,fo);
            submit(w);
        }
    }
    
    public void fileDataCreated(FileEvent fe) {
        final FileObject fo = fe.getFile();
        final URL root = getOwningSourceRoot (fo);
        if (root != null &&  VisibilityQuery.getDefault().isVisible(fo) && FileUtil.getMIMEType(fo, recognizers.getMimeTypes())!=null) {
            final Work w = new FileListWork(WorkType.COMPILE,root,fo);
            submit(w);
        }
    }

    public void fileChanged(FileEvent fe) {
        final FileObject fo = fe.getFile();
        final URL root = getOwningSourceRoot (fo);
        if (root != null &&  VisibilityQuery.getDefault().isVisible(fo) && FileUtil.getMIMEType(fo, recognizers.getMimeTypes())!=null) {
            final Work w = new FileListWork(WorkType.COMPILE,root,fo);
            submit(w);
        }
    }

    public void fileDeleted(FileEvent fe) {
    }

    public void fileRenamed(FileRenameEvent fe) {
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
    }

    private URL getOwningSourceRoot (final FileObject fo) {
        if (fo == null) {
            return null;
        }
        List<URL> clone = new ArrayList<URL> (this.scannedRoots);
        for (URL root : clone) {
            FileObject rootFo = URLMapper.findFileObject(root);
            if (rootFo != null && FileUtil.isParentOf(rootFo,fo)) {
                return root;
            }
        }
        return null;
    }

    //Unit test method
    Set<URL> getScannedBinaries () {
        return this.scannedBinaries;
    }

    //Unit test method
    Set<URL> getScannedSources () {
        return this.scannedRoots;
    }

    //Unit test method
    Set<URL> getScannedUnknowns () {
        return this.scannedUnknown;
    }

    enum State {CREATED, INITIALIZED, INITIALIZED_AFTER_FIRST_SCAN, CLOSED};

    private enum WorkType {COMPILE_BATCH, COMPILE};

    private static class Work {
        
        private final WorkType type;

        public Work (final WorkType type) {
            assert type != null;
            this.type = type;
        }

        public WorkType getType () {
            return this.type;
        }
        
    }

    private static class FileListWork extends Work {

        private final URL root;
        private final List<? extends FileObject> files;

        public FileListWork (final WorkType type, final URL root, final FileObject file) {
            super (type);
            assert root != null;
            assert file != null;
            this.root = root;
            this.files = Collections.singletonList(file);
        }

        public URL getRoot () {
            return root;
        }

        public List<? extends FileObject> getFiles () {
            return this.files;
        }

    }

    private static class RootsWork extends Work {
        
        public RootsWork (final WorkType type) {
            super (type);
        }
    }

    private class Task extends ParserResultTask {

        private final List<Work> todo = new LinkedList<Work>();
        private boolean active;
        
        public synchronized void schedule (Work work) {
            assert work != null;
            todo.add(work);
            if (!active) {
                active = true;
                Utilities.scheduleSpecialTask(this);
            }
        }

        public synchronized int getSubmittedCount () {
            return this.todo.size();
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public Class<? extends Scheduler> getSchedulerClass() {
            return null;
        }

        @Override
        public void cancel() {
            
        }

        @Override
        public void run(Result nil, final SchedulerEvent nothing) {
            do {
                final Work work = getWork();
                final WorkType type = work.getType();
                switch (type) {
                    case COMPILE_BATCH:
                        LOGGER.fine("Batch compile: " + work);
                        batchCompile();
                        break;
                    case COMPILE:
                        LOGGER.fine("Compile: " + work);
                        final FileListWork sfw = (FileListWork) work;
                        compile (sfw.getFiles(),sfw.root);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } while (removeWork());
        }
        //where
        private synchronized Work getWork () {
            return todo.get(0);
        }

        private synchronized boolean  removeWork () {
            todo.remove(0);
            final boolean empty = todo.isEmpty();
            if (empty) {
                active = false;
            }
            return !empty;
        }

        private void compile (final List<? extends FileObject> affected, final URL root) {
            final FileObject rootFo = URLMapper.findFileObject(root);
            if (rootFo == null) {
                return;
            }
            final Map<String,Collection<Indexable>> resources = new HashMap<String, Collection<Indexable>>();
            //todo: timestamps
            for (FileObject af : affected) {
                if (af.isFolder()) {
                    //todo:
                }
                else {
                    String mimeType = FileUtil.getMIMEType(af);
                    if (mimeType != null) {
                        Collection<Indexable> indexables = resources.get(mimeType);
                        if (indexables == null) {
                            indexables = new LinkedList<Indexable>();
                            resources.put(mimeType, indexables);
                        }
                        indexables.add(SPIAccessor.getInstance().create(new FileObjectIndexable(rootFo, af)));
                    }
                }
            }
            try {
                index(resources, Collections.<Indexable>emptySet(), root);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        private void batchCompile () {
            try {
                final DependenciesContext ctx = new DependenciesContext(scannedRoots, scannedBinaries, true);
                final List<URL> newRoots = new LinkedList<URL>();
                newRoots.addAll (regs.getSources());
                newRoots.addAll (regs.getUnknownRoots());                
                ctx.newBinaries.addAll(regs.getBinaries());
                for (Iterator<URL> it = ctx.newBinaries.iterator(); it.hasNext();) {
                    if (ctx.oldBinaries.remove(it.next())) {
                        it.remove();
                    }
                }
                ctx.newBinaries.removeAll(ctx.oldBinaries);
                final Map<URL,List<URL>> depGraph = new HashMap<URL,List<URL>> ();
                
                for (URL url : newRoots) {
                    findDependencies (url, depGraph, ctx, recognizers.getBinaryIds());
                }
                ctx.newRoots.addAll(org.openide.util.Utilities.topologicalSort(depGraph.keySet(), depGraph));
                scanBinaries(ctx);
                scanSources(ctx);
                ctx.scannedRoots.removeAll(ctx.oldRoots);
                ctx.scannedBinaries.removeAll(ctx.oldBinaries);
            } catch (final TopologicalSortException tse) {
                final IllegalStateException ise = new IllegalStateException ();
                throw (IllegalStateException) ise.initCause(tse);
            }
            finally {
                if (state == State.INITIALIZED) {
                    synchronized (RepositoryUpdater.this) {
                        if (state == State.INITIALIZED) {
                            state = State.INITIALIZED_AFTER_FIRST_SCAN;
                        }
                    }
                }
            }
        }

        private void scanBinaries (final DependenciesContext ctx) {
            assert ctx != null;
            TEST_LEGGER.log(Level.FINEST, "scanBinary", ctx.newBinaries);       //NOI18N
            for (URL binary : ctx.newBinaries) {
                scanBinary (binary);
                ctx.scannedBinaries.add(binary);
            }
        }

        private void scanBinary (URL root) {
            LOGGER.fine("Scanning binary root: " + root);
        }

        private void scanSources  (final DependenciesContext ctx) {
            assert ctx != null;            
            for (URL source : ctx.newRoots) {
                try {
                    scanSource (source);
                    ctx.scannedRoots.add(source);
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
                
            }
            TEST_LEGGER.log(Level.FINEST, "scanSources", ctx.newRoots);         //NOI18N
        }

        private void scanSource (URL root) throws IOException {
            LOGGER.fine("Scanning sources root: " + root);

            //todo: optimize for java.io.Files
            final FileObject rootFo = URLMapper.findFileObject(root);
            if (rootFo != null) {               
                final Crawler crawler = new FileObjectCrawler(rootFo);
                final Map<String,Collection<Indexable>> resources = crawler.getResources();
                final Collection<Indexable> deleted = crawler.getDeletedResources();
                index (resources, deleted, root);
            }
        }

        private void index (final Map<String,Collection<Indexable>> resources, final Collection<Indexable> deleted, final URL root) throws IOException {
            SupportAccessor.getInstance().beginTrans();
            try {
                final FileObject cacheRoot = CacheFolder.getDataFolder(root);
                //First use all custom indexers
                for (Iterator<Map.Entry<String,Collection<Indexable>>> it = resources.entrySet().iterator(); it.hasNext();) {
                    final Map.Entry<String,Collection<Indexable>> entry = it.next();
                    final Collection<? extends CustomIndexerFactory> factories = MimeLookup.getLookup(entry.getKey()).lookupAll(CustomIndexerFactory.class);
                    LOGGER.fine("Using CustomIndexerFactories(" + entry.getKey() + "): " + factories);
                    
                    boolean supportsEmbeddings = true;
                    try {
                        for (CustomIndexerFactory factory : factories) {
                            boolean b = factory.supportsEmbeddedIndexers();
                            LOGGER.fine("CustomIndexerFactory: " + factory + ", supportsEmbeddedIndexers=" + b);
                            
                            supportsEmbeddings &= b;
                            final Context ctx = SPIAccessor.getInstance().createContext(cacheRoot, root, factory.getIndexerName(), factory.getIndexVersion(), null);
                            factory.filesDeleted(deleted, ctx);
                            final CustomIndexer indexer = factory.createIndexer();
                            SPIAccessor.getInstance().index(indexer, Collections.unmodifiableCollection(entry.getValue()), ctx);
                        }
                    } finally {
                        if (!supportsEmbeddings) {
                            LOGGER.fine("Removing roots for " + entry.getKey() + ", indexed by custom indexers, embedded indexers forbidden");
                            it.remove();
                        }
                    }
                }
                //Then use slow gsf like indexers
                final List<Indexable> toIndex = new LinkedList<Indexable>();
                for (Collection<Indexable> data : resources.values()) {
                    toIndex.addAll(data);
                }

                LOGGER.fine("Using EmbeddingIndexers for " + toIndex);
                
                final SourceIndexer si = new SourceIndexer(root,cacheRoot);
                si.index(toIndex, deleted);
            } finally {
                SupportAccessor.getInstance().endTrans();
            }
        }

        private void findDependencies(final URL rootURL, final Map<URL, List<URL>> depGraph, DependenciesContext ctx, final Set<String> binaryClassPathIds) {
            if (ctx.useInitialState && ctx.scannedRoots.contains(rootURL)) {
                ctx.oldRoots.remove(rootURL);
                return;
            }
            if (depGraph.containsKey(rootURL)) {
                return;
            }
            final FileObject rootFo = URLMapper.findFileObject(rootURL);
            if (rootFo == null) {
                return;
            }
            ctx.cycleDetector.push(rootURL);
            final List<ClassPath> pathToResolve = new ArrayList<ClassPath>(binaryClassPathIds.size());
            for (String binaryClassPathId : binaryClassPathIds) {
                ClassPath cp = ClassPath.getClassPath(rootFo, binaryClassPathId);
                if (cp != null) {
                    pathToResolve.add(cp);
                }
            }
            final List<URL> deps = new LinkedList<URL>();
            for (ClassPath cp : pathToResolve) {
                for (ClassPath.Entry entry : cp.entries()) {
                    final URL url = entry.getURL();
                    final URL[] sourceRoots = regs.sourceForBinaryQuery(url, cp, false);
                    if (sourceRoots != null) {
                        for (URL sourceRoot : sourceRoots) {
                            if (!sourceRoot.equals(rootURL) && !ctx.cycleDetector.contains(sourceRoot)) {
                                deps.add(sourceRoot);
                                findDependencies(sourceRoot, depGraph, ctx, binaryClassPathIds);
                            }
                        }
                    }
                    else {
                        //What does it mean?
                        if (ctx.useInitialState) {
                            if (!ctx.scannedBinaries.contains(url)) {
                                ctx.newBinaries.add (url);
                            }
                            ctx.oldBinaries.remove(url);
                        }
                    }
                }
            }
            depGraph.put(rootURL, deps);
            ctx.cycleDetector.pop();
        }

    }

    private static class DependenciesContext {
        private final Set<URL> oldRoots;
        private final Set<URL> oldBinaries;
        private final Set<URL> scannedRoots;
        private final Set<URL> scannedBinaries;
        private final Stack<URL> cycleDetector;
        private final List<URL> newRoots;
        private final Set<URL> newBinaries;
        private final boolean useInitialState;

        public DependenciesContext (final Set<URL> scannedRoots, final Set<URL> scannedBinaries, boolean useInitialState) {
            assert scannedRoots != null;
            assert scannedBinaries != null;
            this.scannedRoots = scannedRoots;
            this.scannedBinaries = scannedBinaries;
            this.useInitialState = useInitialState;
            cycleDetector = new Stack<URL>();
            oldRoots = new HashSet<URL> (scannedRoots);
            oldBinaries = new HashSet<URL> (scannedBinaries);
            this.newRoots = new ArrayList<URL>();
            this.newBinaries = new HashSet<URL>();
        }


    }

}
