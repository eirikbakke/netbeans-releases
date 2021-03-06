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


package org.netbeans.modules.maven.debug;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.classpath.BootClassPathImpl;
import org.netbeans.modules.maven.spi.debug.AdditionalDebuggedProjects;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.FileUtilities;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.netbeans.spi.project.ProjectContainerProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 * various debugger related utility methods.
 * @author mkleint
 */
public class Utils {
    
    /** Creates a new instance of Utils */
    private Utils() {
    }
    
    public static MethodBreakpoint createBreakpoint(String stopClassName) {
        return createBreakpoint(stopClassName, "*");
    }
    
    public static MethodBreakpoint createBreakpoint(String stopClassName, String methodName) {
        MethodBreakpoint breakpoint = MethodBreakpoint.create(
                stopClassName,
                methodName
                );
//        breakpoint.setHidden(true);
        DebuggerManager.getDebuggerManager().addBreakpoint(breakpoint);
        return breakpoint;
    }
    
    
    public static URL[] convertStringsToURL(Collection<String> strings) {
        URL[] fos = new URL[strings.size()];
        int index = 0;
        for (String str : strings) {
            fos[index] = fileToURL(FileUtilities.convertStringToFile(str));
            index++;
        }
        return fos;
    }
    
    private static Set<String> collectClasspaths(Project prj) throws DependencyResolutionRequiredException {
        Set<String> toRet = new HashSet<String>();
        NbMavenProject watcher = prj.getLookup().lookup(NbMavenProject.class);
        MavenProject mproject = watcher.getMavenProject();
        //TODO this ought to be really configurable based on what class gets debugged.
        toRet.addAll(mproject.getTestClasspathElements());
        //for poms also include all module projects recursively..
        boolean isPom = NbMavenProject.TYPE_POM.equals(watcher.getPackagingType());
        if (isPom) {
            ProjectContainerProvider subs = prj.getLookup().lookup(ProjectContainerProvider.class);
            ProjectContainerProvider.Result res = subs.getContainedProjects();
            for (Project pr : res.getProjects()) {
                toRet.addAll(collectClasspaths(pr));
            }
        }
        return toRet;
    }
    private static Set<String> collectSourceRoots(Project prj) {
        Set<String> toRet = new HashSet<String>();
        NbMavenProject watcher = prj.getLookup().lookup(NbMavenProject.class);
        MavenProject mproject = watcher.getMavenProject();
        //TODO this ought to be really configurable based on what class gets debugged.
        toRet.addAll(mproject.getTestCompileSourceRoots());
        //for poms also include all module projects recursively..
        boolean isPom = NbMavenProject.TYPE_POM.equals(watcher.getPackagingType());
        if (isPom) {
            //only for pom is correct use of subprojectprovider
            ProjectContainerProvider subs = prj.getLookup().lookup(ProjectContainerProvider.class);
            ProjectContainerProvider.Result res = subs.getContainedProjects();
            for (Project pr : res.getProjects()) {
                toRet.addAll(collectSourceRoots(pr));
            }
        }
        return toRet;
    }
    
    static ClassPath createSourcePath(Project project) {
        URL[] roots;
        ClassPath cp;
        AdditionalDebuggedProjects adds = project.getLookup().lookup(AdditionalDebuggedProjects.class);
        try {
            Set<String> col = collectClasspaths(project);
            if (adds != null) {
                for (Project prj : adds.getProjects()) {
                    col.addAll(collectClasspaths(prj));
                }
            }
            col.remove(null); // MNG-5209
            roots = convertStringsToURL(col);
            cp = convertToSourcePath(roots);
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
            cp = ClassPathSupport.createClassPath(new FileObject[0]);
        }
        Set<String> col = collectSourceRoots(project);
        if (adds != null) {
            for (Project prj : adds.getProjects()) {
                col.addAll(collectSourceRoots(prj));
            }
        }
        roots = convertStringsToURL(col);
        ClassPath sp = ClassPathSupport.createClassPath(roots);
        
        ClassPath sourcePath = ClassPathSupport.createProxyClassPath(
                new ClassPath[] {cp, sp}
        );
        return sourcePath;
    }
    
    static ClassPath createJDKSourcePath(Project nbproject) {
        String val = nbproject.getLookup().lookup(AuxiliaryProperties.class).get(Constants.HINT_JDK_PLATFORM, true);
        JavaPlatform jp = BootClassPathImpl.getActivePlatform(val);
        if (jp == null) {
            jp = JavaPlatformManager.getDefault().getDefaultPlatform();
        }
        return jp.getSourceFolders();
    }
    
    /**
     * This method uses SourceForBinaryQuery to find sources for each
     * path item and returns them as ClassPath instance. All path items for which
     * the sources were not found are omitted.
     *
     */
    public static ClassPath convertToSourcePath(URL[] fs)  {
        List<PathResourceImplementation> lst = new ArrayList<PathResourceImplementation>();
        Set<URL> existingSrc = new HashSet<URL>();
        for (int i = 0; i < fs.length; i++) {
            URL url = FileUtil.isArchiveFile(fs[i]) ? FileUtil.getArchiveRoot(fs[i]) : fs[i];
            try {
                FileObject[] srcfos = SourceForBinaryQuery.findSourceRoots(url).getRoots();
                for (int j = 0; j < srcfos.length; j++) {
                    if (FileUtil.isArchiveFile(srcfos[j])) {
                        srcfos [j] = FileUtil.getArchiveRoot(srcfos [j]);
                    }
                    try {
                        url = srcfos[j].toURL();
                        if  (!url.toExternalForm().endsWith("/")) {
                            url = new URL(url.toExternalForm() + "/");
                        }
                    } catch (MalformedURLException ex) {
                        ErrorManager.getDefault().notify
                                (ErrorManager.EXCEPTION, ex);
                        continue;
                    }
                    if (url == null)  {
                        continue;
                    }
                    if (!existingSrc.contains(url)) {
                        lst.add(ClassPathSupport.createResource(url));
                        existingSrc.add(url);
                    }
                } // for
            } catch (IllegalArgumentException ex) {
                //TODO??
            }
        }
        return ClassPathSupport.createClassPath(lst);
    }
    
    
    
    static URL fileToURL(File file) {
        try {
            URL url;
            url = Utilities.toURI(file).toURL();
            if (FileUtil.isArchiveFile(url)) {
                url = FileUtil.getArchiveRoot(url);
            }
            if  (!url.toExternalForm().endsWith("/")) { //NOI18N
                url = new URL(url.toExternalForm() + "/"); //NOI18N
            }
            return url;
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            return null;
        }
    }
    
}
