/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.maven.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.progress.aggregate.AggregateProgressFactory;
import org.netbeans.api.progress.aggregate.AggregateProgressHandle;
import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.embedder.EmbedderFactory;
import org.netbeans.modules.maven.embedder.MavenEmbedder;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.options.MavenSettings;
import org.netbeans.modules.maven.options.MavenSettings.DownloadStrategy;
import org.netbeans.modules.maven.spi.PackagingProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import static org.netbeans.modules.maven.api.Bundle.*;
import org.netbeans.modules.maven.modelcache.MavenProjectCache;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * an instance resides in project lookup, allows to get notified on project and 
 * relative path changes.
 * @author mkleint
 */
public final class NbMavenProject {

    /**
     * the only property change fired by the class, means that the pom file
     * has changed.
     */
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N
    /**
     * TODO comment
     * 
     */
    public static final String PROP_RESOURCE = "RESOURCES"; //NOI18N
    
    private NbMavenProjectImpl project;
    private PropertyChangeSupport support;
    private FCHSL listener = new FCHSL();
    private final List<File> files = new ArrayList<File>();
    
    static {
        AccessorImpl impl = new AccessorImpl();
        impl.assign();
    }
    private final RequestProcessor.Task task;
    private static RequestProcessor BINARYRP = new RequestProcessor("Maven projects Binary Downloads", 1);
    private static RequestProcessor NONBINARYRP = new RequestProcessor("Maven projects Source/Javadoc Downloads", 1);

    static class AccessorImpl extends NbMavenProjectImpl.WatcherAccessor {
        
        
         public void assign() {
             if (NbMavenProjectImpl.ACCESSOR == null) {
                 NbMavenProjectImpl.ACCESSOR = this;
             }
         }
    
        @Override
        public NbMavenProject createWatcher(NbMavenProjectImpl proj) {
            return new NbMavenProject(proj);
        }
        
        @Override
        public void doFireReload(NbMavenProject watcher) {
            watcher.doFireReload();
        }

    }


    
    private class FCHSL implements FileChangeListener {


        @Override
        public void fileFolderCreated(FileEvent fe) {
            fireChange(Utilities.toURI(FileUtil.toFile(fe.getFile())));
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            fireChange(Utilities.toURI(FileUtil.toFile(fe.getFile())));
        }

        @Override
        public void fileChanged(FileEvent fe) {
            fireChange(Utilities.toURI(FileUtil.toFile(fe.getFile())));
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            fireChange(Utilities.toURI(FileUtil.toFile(fe.getFile())));
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            fireChange(Utilities.toURI(FileUtil.toFile(fe.getFile())));
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
        }
        
    }
    
    
    /** Creates a new instance of NbMavenProject */
    private NbMavenProject(NbMavenProjectImpl proj) {
        project = proj;
        //TODO oh well, the sources is the actual project instance not the watcher.. a problem?
        support = new PropertyChangeSupport(proj);
        task = createBinaryDownloadTask(BINARYRP);
    }
    
    /**
     * 
     * @return 
     * @since 
     */
    public boolean isUnloadable() {
        return MavenProjectCache.isFallbackproject(getMavenProject());
    }
    

    @Messages({"Progress_Download=Downloading Maven dependencies", "MSG_Failed=Failed to download - {0}", "MSG_Done=Finished retrieving dependencies from remote repositories."})
    private RequestProcessor.Task createBinaryDownloadTask(RequestProcessor rp) {
        return rp.create(new Runnable() {
            @Override
            public void run() {
                    //#146171 try the hardest to avoid NPE for files/directories that
                    // seemed to have been deleted while the task was scheduled.
                    FileObject fo = project.getProjectDirectory();
                    if (fo == null || !fo.isValid()) {
                        return;
                    }
                    fo = fo.getFileObject("pom.xml"); //NOI18N
                    if (fo == null) {
                        return;
                    }
                    File pomFile = FileUtil.toFile(fo);
                    if (pomFile == null) {
                        return;
                    }
                    MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
                    AggregateProgressHandle hndl = AggregateProgressFactory.createHandle(Progress_Download(),
                            new ProgressContributor[] {
                                AggregateProgressFactory.createProgressContributor("zaloha") },  //NOI18N
                            ProgressTransferListener.cancellable(), null);

                    boolean ok = true;
                    try {
                        ProgressTransferListener.setAggregateHandle(hndl);
                        hndl.start();
                        MavenExecutionRequest req = online.createMavenExecutionRequest();
                        req.setPom(pomFile);
                        req.setTransferListener(ProgressTransferListener.activeListener());
                        MavenExecutionResult res = online.readProjectWithDependencies(req, false); //NOI18N
                        if (res.hasExceptions()) {
                            ok = false;
                            Exception ex = (Exception)res.getExceptions().get(0);
                            StatusDisplayer.getDefault().setStatusText(MSG_Failed(ex.getLocalizedMessage()));
                        }
                    } catch (ThreadDeath d) { // download interrupted
                    } catch (IllegalStateException x) {
                        if (x.getCause() instanceof ThreadDeath) {
                            // #197261: download interrupted
                        } else {
                            throw x;
                        }
                    } catch (RuntimeException exc) {
                        //guard against exceptions that are not processed by the embedder
                        //#136184 NumberFormatException, #214152 InvalidArtifactRTException
                        StatusDisplayer.getDefault().setStatusText(MSG_Failed(exc.getLocalizedMessage()));
                    } finally {
                        hndl.finish();
                        ProgressTransferListener.clearAggregateHandle();
                    }
                    if (ok) {
                        StatusDisplayer.getDefault().setStatusText(MSG_Done());
                    }
                    if (support.hasListeners(NbMavenProject.PROP_PROJECT)) {
                        NbMavenProject.fireMavenProjectReload(project);
                    }
            }
        });
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
    }
    
    /**
     * Returns the current maven project model from the embedder.
     * Should never be kept around for long but always reloaded from here, on 
     * a project change the correct instance changes as the embedder reloads it.
     * Never returns null but check {@link #isErrorPlaceholder} if necessary.
     */ 
    public @NonNull MavenProject getMavenProject() {
        return project.getOriginalMavenProject();
    }
    
    /**
     * a marginally unreliable, non blocking method for figuring if the model is loaded or not.
     */
    public boolean isMavenProjectLoaded() {
        return project.isMavenProjectLoaded();
    }

    public @NonNull MavenProject loadAlternateMavenProject(MavenEmbedder embedder, List<String> activeProfiles, Properties properties) {
        return project.loadMavenProject(embedder, activeProfiles, properties);
    }

    /**
     * 
     * @param test are test resources requested, if false, resources for base sources are returned
     * @return
     */
    public URI[] getResources(boolean test) {
        return project.getResources(test);
    }

    /**
     * Standardized way of finding output directory even for broken projects.
     * @param test true for {@code target/test-classes}, false for {@code target/classes}
     * @return the configured output directory (normalized)
     */
    public File getOutputDirectory(boolean test) {
        Build build = getMavenProject().getBuild();
        String path = build != null ? (test ? build.getTestOutputDirectory() : build.getOutputDirectory()) : null;
        if (path != null) {
            return new File(path);
        } else { // #189092
            return new File(new File(getMavenProject().getBasedir(), "target"), test ? "test-classes" : "classes"); // NOI18N
        }
    }

    /**
     * 
     * @return
     */
    public URI getWebAppDirectory() {
        return project.getWebAppDirectory();
    }
    
    public URI getEarAppDirectory() {
        return project.getEarAppDirectory();
    }
    
    public static final String TYPE_JAR = "jar"; //NOI18N
    public static final String TYPE_WAR = "war"; //NOI18N
    public static final String TYPE_EAR = "ear"; //NOI18N
    public static final String TYPE_EJB = "ejb"; //NOI18N
    public static final String TYPE_APPCLIENT = "app-client"; //NOI18N
    public static final String TYPE_NBM = "nbm"; //NOI18N
    public static final String TYPE_NBM_APPLICATION = "nbm-application"; //NOI18N
    public static final String TYPE_OSGI = "bundle"; //NOI18N
    public static final String TYPE_POM = "pom"; //NOI18N
    
    /**
     * Gets an "effective" packaging for the project.
     * Normally this is just the Maven model's declared packaging.
     * But {@link PackagingProvider}s can affect the decision.
     * The resulting type will be used to control most IDE functions, including packaging-specific lookup.
     */
    public String getPackagingType() {
        for (PackagingProvider pp : Lookup.getDefault().lookupAll(PackagingProvider.class)) {
            String p = pp.packaging(project);
            if (p != null) {
                return p;
            }
        }
        return getMavenProject().getPackaging();
    }
    
    
    public void addWatchedPath(String relPath) {
        addWatchedPath(FileUtilities.getDirURI(project.getProjectDirectory(), relPath));
    } 
    
    public void addWatchedPath(URI uri) {
        //#110599
        boolean addListener = false;
        File fil = Utilities.toFile(uri);
        synchronized (files) {
            if (!files.contains(fil)) {
                addListener = true;
            }
            files.add(fil);
        }
        if (addListener) {
            FileUtil.addFileChangeListener(listener, fil);
        }
    } 

    /**
     * asynchronous dependency download, scheduled to some time in the future. Useful
     * for cases when a 3rd party codebase calls maven classes and can do so repeatedly in one sequence.
     */
    public void triggerDependencyDownload() {
        synchronized (task) {
            task.schedule(1000);
        }
    }

    /**
     * Not to be called from AWT, will wait til the project binary dependency resolution finishes.
     */
    public void synchronousDependencyDownload() {
        assert !SwingUtilities.isEventDispatchThread() : " Not to be called from AWT, can take significant amount ot time to download dependencies from the network."; //NOI18N
        synchronized (task) {
            task.schedule(0);
            task.waitFinished();
        }
    }

    /**
     * @deprecated Use {@link #downloadDependencyAndJavadocSource(boolean) with {@code true}.
     */
    @Deprecated
    public void downloadDependencyAndJavadocSource() {
        downloadDependencyAndJavadocSource(true);
    }
    /**
     * Download binaries and then trigger dependency javadoc/source download (in async mode) if download strategy is not DownloadStrategy.NEVER in options
     * Not to be called from AWT thread in synch mode; the current thread will continue after downloading binaries and firing project change event.
     * @param synch true to download dependencies binaries (not source/Javadoc) synchronously; false to download everything asynch
     */
    public void downloadDependencyAndJavadocSource(boolean synch) {
        if (synch) {
            synchronousDependencyDownload();
        } else {
            triggerDependencyDownload();
        }
        //see Bug 189350 : honer global  maven settings
        if (MavenSettings.getDefault().getJavadocDownloadStrategy() != DownloadStrategy.NEVER) {
            triggerSourceJavadocDownload(true);
        }
        if (MavenSettings.getDefault().getSourceDownloadStrategy() != DownloadStrategy.NEVER) {
            triggerSourceJavadocDownload(false);
        }
    }


    @Messages({"Progress_Javadoc=Downloading Javadoc", "Progress_Source=Downloading Sources"})
    public void triggerSourceJavadocDownload(final boolean javadoc) {
        NONBINARYRP.post(new Runnable() {
            @Override
            public void run() {
                Set<Artifact> arts = project.getOriginalMavenProject().getArtifacts();
                ProgressContributor[] contribs = new ProgressContributor[arts.size()];
                for (int i = 0; i < arts.size(); i++) {
                    contribs[i] = AggregateProgressFactory.createProgressContributor("multi-" + i); //NOI18N
                }
                String label = javadoc ? Progress_Javadoc() : Progress_Source();
                AggregateProgressHandle handle = AggregateProgressFactory.createHandle(label,
                        contribs, ProgressTransferListener.cancellable(), null);
                handle.start();
                try {
                    ProgressTransferListener.setAggregateHandle(handle);
                    int index = 0;
                    for (Artifact a : arts) {
                        downloadOneJavadocSources(contribs[index], project, a, javadoc);
                        index++;
                    }
                } catch (ThreadDeath d) { // download interrupted
                } finally {
                    handle.finish();
                    ProgressTransferListener.clearAggregateHandle();
                    fireProjectReload();
                }
            }
        });
    }


    @Messages({"MSG_Checking_Javadoc=Checking Javadoc for {0}", "MSG_Checking_Sources=Checking Sources for {0}"})
    private static void downloadOneJavadocSources(ProgressContributor progress,
                                               NbMavenProjectImpl project, Artifact art, boolean isjavadoc) {
        MavenEmbedder online = EmbedderFactory.getOnlineEmbedder();
        progress.start(2);
        if ( Artifact.SCOPE_SYSTEM.equals(art.getScope())) {
            progress.finish();
            return;
        }
        try {
            if (isjavadoc) {
                Artifact javadoc = project.getEmbedder().createArtifactWithClassifier(
                    art.getGroupId(),
                    art.getArtifactId(),
                    art.getVersion(),
                    art.getType(),
                    "javadoc"); //NOI18N
                progress.progress(MSG_Checking_Javadoc(art.getId()), 1);
                online.resolve(javadoc, project.getOriginalMavenProject().getRemoteArtifactRepositories(), project.getEmbedder().getLocalRepository());
            } else {
                Artifact sources = project.getEmbedder().createArtifactWithClassifier(
                    art.getGroupId(),
                    art.getArtifactId(),
                    art.getVersion(),
                    art.getType(),
                    "sources"); //NOI18N
                progress.progress(MSG_Checking_Sources(art.getId()), 1);
                online.resolve(sources, project.getOriginalMavenProject().getRemoteArtifactRepositories(), project.getEmbedder().getLocalRepository());
            }
        } catch (ThreadDeath td) {
        } catch (IllegalStateException ise) { //download interrupted in dependent thread. #213812
            if (!(ise.getCause() instanceof ThreadDeath)) {
                throw ise;
            }   
        } catch (ArtifactNotFoundException ex) {
            // just ignore..ex.printStackTrace();
        } catch (ArtifactResolutionException ex) {
            // just ignore..ex.printStackTrace();
        } catch (InvalidArtifactRTException ex) { //214152 InvalidArtifactRTException
            // just ignore..ex.printStackTrace();
        } finally {
            progress.finish();
        }
    }

    
    public void removeWatchedPath(String relPath) {
        removeWatchedPath(FileUtilities.getDirURI(project.getProjectDirectory(), relPath));
    }
    public void removeWatchedPath(URI uri) {
        //#110599
        boolean removeListener = false;
        File fil = Utilities.toFile(uri);
        synchronized (files) {
            boolean rem = files.remove(fil);
            if (rem && !files.contains(fil)) {
                removeListener = true;
            }
        }
        if (removeListener) {
            FileUtil.removeFileChangeListener(listener, fil);
        }
    } 
    
    
    //TODO better do in ReqProcessor to break the listener chaining??
    private void fireChange(URI uri) {
        support.firePropertyChange(PROP_RESOURCE, null, uri);
    }
    
    /**
     * 
     */ 
    private void fireProjectReload() {
        project.fireProjectReload();
    }
    
    private void doFireReload() {
        FileUtil.refreshFor(FileUtil.toFile(project.getProjectDirectory()));
        NbMavenProjectImpl.refreshLocalRepository(project);
        support.firePropertyChange(PROP_PROJECT, null, null);
    }
    
    /**
     * utility method for triggering a maven project reload. 
     * if the project passed in is a Maven based project, will
     * fire reload of the project, otherwise will do nothing.
     */ 
    
    public static void fireMavenProjectReload(Project prj) {
        if (prj != null) {
            NbMavenProject watcher = prj.getLookup().lookup(NbMavenProject.class);
            if (watcher != null) {
                watcher.fireProjectReload();
            }
        }
    }

    public static void addPropertyChangeListener(Project prj, PropertyChangeListener listener) {
        if (prj != null && prj instanceof NbMavenProjectImpl) {
            // cannot call getLookup() -> stackoverflow when called from NbMavenProjectImpl.createBasicLookup()..
            NbMavenProject watcher = ((NbMavenProjectImpl)prj).getProjectWatcher();
            watcher.addPropertyChangeListener(listener);
        } else {
            assert false : "Attempted to add PropertyChangeListener to project " + prj; //NOI18N
        }
    }
    
    public static void removePropertyChangeListener(Project prj, PropertyChangeListener listener) {
        if (prj != null && prj instanceof NbMavenProjectImpl) {
            // cannot call getLookup() -> stackoverflow when called from NbMavenProjectImpl.createBasicLookup()..
            NbMavenProject watcher = ((NbMavenProjectImpl)prj).getProjectWatcher();
            watcher.removePropertyChangeListener(listener);
        } else {
            assert false : "Attempted to remove PropertyChangeListener from project " + prj; //NOI18N
        }
    }

    /**
     * Checks whether a given project is just an error placeholder.
     * @param project a project loaded by e.g. {@link #getMavenProject}
     * @return true if it was loaded as an error fallback, false for a normal project
     * @since 2.24
     */
    public static boolean isErrorPlaceholder(@NonNull MavenProject project) {
        return project.getId().equals("error:error:pom:0"); // see NbMavenProjectImpl.getFallbackProject
    }

    @Override public String toString() {
        return project.toString();
    }
    
}
