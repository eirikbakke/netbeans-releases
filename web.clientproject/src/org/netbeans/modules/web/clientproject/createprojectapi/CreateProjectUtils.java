/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.createprojectapi;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.clientproject.ui.wizard.ClientSideProjectWizardIterator;
import org.netbeans.modules.web.clientproject.ui.wizard.NewClientSideProjectPanel;
import org.netbeans.modules.web.clientproject.ui.wizard.ToolsPanel;
import org.netbeans.modules.web.common.spi.ProjectWebRootQuery;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.util.Pair;

/**
 * Support class for creating new HTML5 projects.
 * @since 1.65
 */
public final class CreateProjectUtils {

    /**
     * Constant for project directory, stored as {@link java.io.File} (the file does not need to exist).
     * @see #createBaseWizardPanel(String)
     */
    public static final String PROJECT_DIRECTORY = ClientSideProjectWizardIterator.Wizard.PROJECT_DIRECTORY;
    /**
     * Constant for project name, stored as {@link String}.
     * @see #createBaseWizardPanel(String)
     */
    public static final String PROJECT_NAME = ClientSideProjectWizardIterator.Wizard.NAME;


    private CreateProjectUtils() {
    }

    /**
     * Create base wizard panel for new HTML5 projects. This panel contains the base information
     * about project, e.g. name, location etc. These properties are stored in the given {@link WizardDescriptor}.
     * @param projectNameTemplate default project name, e.g. "JsLibrary"
     * @return base wizard panel for new HTML5 projects together with its default display name
     * @see #PROJECT_DIRECTORY
     * @see #PROJECT_NAME
     */
    @NbBundle.Messages("CreateProjectUtils.nameLocation.displayName=Name and Location")
    public static Pair<WizardDescriptor.FinishablePanel<WizardDescriptor>, String> createBaseWizardPanel(String projectNameTemplate) {
        return Pair.<WizardDescriptor.FinishablePanel<WizardDescriptor>, String>of(new NewClientSideProjectPanel(projectNameTemplate),
                Bundle.CreateProjectUtils_nameLocation_displayName());
    }

    /**
     * Create wizard panel for "Tools" (Bower, NPM, Grunt). All
     * these tools are enabled by default.
     * <p>
     * Currently, this panel is always finishable.
     * @param tools information about tools
     * @return panel for "Tools" (Bower, NPM, Grunt) together with its default display name
     */
    @NbBundle.Messages("CreateProjectUtils.tools.displayName=Tools")
    public static Pair<WizardDescriptor.FinishablePanel<WizardDescriptor>, String> createToolsWizardPanel(Tools tools) {
        return Pair.<WizardDescriptor.FinishablePanel<WizardDescriptor>, String>of(new ToolsPanel(new Tools(tools)),
                Bundle.CreateProjectUtils_tools_displayName());
    }

    /**
     * Instantiate "Tools" support. In other words, generate proper files.
     * <p>
     * This method is typically used in <code>WizardIterator</code>.
     * @param project project to be used
     * @param toolsPanel panel, created by {@link #createToolsWizardPanel()}
     * @return set of generated files; can be empty but never {@code null}
     * @throws IOException if any error occurs
     * @see #createToolsWizardPanel()
     * @since 1.68
     */
    public static Set<FileObject> instantiateTools(Project project, WizardDescriptor.FinishablePanel<WizardDescriptor> toolsPanel) throws IOException {
        if (!(toolsPanel instanceof ToolsPanel)) {
            throw new IllegalArgumentException("toolsPanel must be created by #createToolsWizardPanel() method");
        }
        Set<FileObject> files = new HashSet<>();
        FileObject folder = project.getProjectDirectory();
        assert folder != null;
        Tools tools = ((ToolsPanel) toolsPanel).getTools();
        if (tools.isBower()) {
            files.add(createFile(folder, "bower.json", "Templates/ClientSide/bower.json")); // NOI18N
            // #251608
            String webRootPath = getWebRootPath(project);
            if (webRootPath != null) {
                files.add(createFile(folder, ".bowerrc", "Templates/ClientSide/.bowerrc", // NOI18N
                        Collections.<String, Object>singletonMap("project", Collections.singletonMap("webRootPath", webRootPath)))); // NOI18N
            }
        }
        if (tools.isNpm()) {
            files.add(createFile(folder, "package.json", "Templates/ClientSide/package.json")); // NOI18N
        }
        if (tools.isGrunt()) {
            files.add(createFile(folder, "Gruntfile.js", "Templates/ClientSide/Gruntfile.js")); // NOI18N
        }
        if (tools.isGulp()) {
            files.add(createFile(folder, "gulpfile.js", "Templates/ClientSide/gulpfile.js")); // NOI18N
        }
        return files;
    }

    @CheckForNull
    private static String getWebRootPath(Project project) {
        for (FileObject webRoot : ProjectWebRootQuery.getWebRoots(project)) {
            return FileUtil.getRelativePath(project.getProjectDirectory(), webRoot);
        }
        return null;
    }

    private static FileObject createFile(FileObject root, String file, String template) throws IOException {
        return createFile(root, file, template, Collections.<String, Object>emptyMap());
    }

    private static FileObject createFile(FileObject root, String file, String template, Map<String, Object> parameters) throws IOException {
        assert root != null;
        assert root.isFolder() : root;
        FileObject target = root.getFileObject(file);
        if (target != null
                && target.isValid()) {
            return target;
        }
        FileObject templateFile = FileUtil.getConfigFile(template);
        DataFolder dataFolder = DataFolder.findFolder(root);
        DataObject dataIndex = DataObject.find(templateFile);
        return dataIndex.createFromTemplate(dataFolder, null, parameters).getPrimaryFile();
    }

    //~ Inner classes

    /**
     * Information about external tools.
     * @since 1.68
     */
    public static final class Tools {

        private volatile boolean npm;
        private volatile boolean bower;
        private volatile boolean grunt;
        private volatile boolean gulp;


        /**
         * Creates new tools with all tools disabled.
         */
        public Tools() {
        }

        Tools(Tools tools) {
            npm = tools.npm;
            bower = tools.bower;
            grunt = tools.grunt;
            gulp = tools.gulp;
        }

        /**
         * Creates external tools with all tools enabled.
         * @return external tools with all tools enabled
         */
        public static Tools all() {
            return new Tools()
                    .setNpm(true)
                    .setBower(true)
                    .setGrunt(true)
                    .setGulp(true);
        }

        /**
         * Is NPM tool enabled?
         * @return {@code true} if NPM tool is enabled
         */
        public boolean isNpm() {
            return npm;
        }

        /**
         * Set NPM tool.
         * @param npm {@code true} if NPM tool is enabled
         * @return self
         */
        public Tools setNpm(boolean npm) {
            this.npm = npm;
            return this;
        }

        /**
         * Is Bower tool enabled?
         * @return {@code true} if Bower tool is enabled
         */
        public boolean isBower() {
            return bower;
        }

        /**
         * Set Bower tool.
         * @param bower {@code true} if Bower tool is enabled
         * @return self
         */
        public Tools setBower(boolean bower) {
            this.bower = bower;
            return this;
        }

        /**
         * Is Grunt tool enabled?
         * @return {@code true} if Grunt tool is enabled
         */
        public boolean isGrunt() {
            return grunt;
        }

        /**
         * Set Grunt tool.
         * @param grunt {@code true} if Grunt tool is enabled
         * @return self
         */
        public Tools setGrunt(boolean grunt) {
            this.grunt = grunt;
            return this;
        }

        /**
         * Is Gulp tool enabled?
         * @return {@code true} if Gulp tool is enabled
         * @since 1.77
         */
        public boolean isGulp() {
            return gulp;
        }

        /**
         * Set Gulp tool.
         * @param gulp {@code true} if Gulp tool is enabled
         * @return self
         * @since 1.77
         */
        public Tools setGulp(boolean gulp) {
            this.gulp = gulp;
            return this;
        }

        @Override
        public String toString() {
            return "Tools{" + "npm=" + npm + ", bower=" + bower + ", grunt=" + grunt + ", gulp=" + gulp + '}'; // NOI18N
        }

    }

}
