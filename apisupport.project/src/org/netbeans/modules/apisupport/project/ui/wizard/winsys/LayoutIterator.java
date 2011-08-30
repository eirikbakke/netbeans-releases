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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.apisupport.project.ui.wizard.winsys;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modules.apisupport.project.api.LayerHandle;
import org.netbeans.modules.apisupport.project.api.ManifestManager;
import org.netbeans.modules.apisupport.project.api.UIUtil;
import org.netbeans.modules.apisupport.project.api.Util;
import org.netbeans.modules.apisupport.project.layers.LayerUtils;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.netbeans.modules.apisupport.project.ui.wizard.common.BasicWizardIterator;
import org.netbeans.modules.apisupport.project.ui.wizard.common.CreatedModifiedFiles;
import org.netbeans.modules.apisupport.project.ui.wizard.winsys.NewTCIterator.DataModel;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Wizard for redefining layout of windows.
 *
 * @author Jaroslav Tulach
 */
@TemplateRegistration(
    folder=UIUtil.TEMPLATE_FOLDER,
    position=3249,
    displayName="#template_winsys_layout",
    iconBase="org/netbeans/modules/apisupport/project/ui/resources/newLayout.png", 
    description="../../resources/newLayout.html",
    category=UIUtil.TEMPLATE_CATEGORY
)
@Messages("template_winsys_layout=Layout of Windows")
public final class LayoutIterator extends BasicWizardIterator {

    private DataModel data;
    
    @Override
    public Set instantiate() throws IOException {
        CreatedModifiedFiles cmf = data.getCreatedModifiedFiles();
        cmf.run();
        return Collections.singleton(
            LayerHandle.forProject(data.getProject()).getLayerFile()
        );
    }
    
    @Override
    protected BasicWizardIterator.Panel[] createPanels(WizardDescriptor wiz) {
        data = new DataModel(wiz);
        return new BasicWizardIterator.Panel[] {
            new LayoutWarningPanel(wiz, data),
            new LayoutLaunchingPanel(wiz, data),
            new LayoutSummaryPanel(wiz, data)
        };
    }
    
    public @Override void uninitialize(WizardDescriptor wiz) {
        super.uninitialize(wiz);
        data = null;
    }
    
    static void generateFileChanges(final DataModel model) {
        CreatedModifiedFiles fileChanges = new CreatedModifiedFiles(model.getProject());
        Project project = model.getProject();
        NbModuleProvider moduleInfo = model.getModuleInfo();
        final String name = model.getName();
        final String packageName = model.getPackageName();
        final String mode = model.getMode();

        boolean actionLessTC;
        boolean xmlLessTC;
        try {
            SpecificationVersion current = model.getModuleInfo().getDependencyVersion("org.openide.windows");
            actionLessTC = current == null || current.compareTo(new SpecificationVersion("6.24")) >= 0; // NOI18N
            xmlLessTC = current == null || current.compareTo(new SpecificationVersion("6.37")) >= 0; // NOI18N
        } catch (IOException ex) {
            Logger.getLogger(LayoutIterator.class.getName()).log(Level.INFO, null, ex);
            actionLessTC = false;
            xmlLessTC = false;
        }
        boolean propertiesPersistence;
        try {
            SpecificationVersion current = model.getModuleInfo().getDependencyVersion("org.netbeans.modules.settings");
            propertiesPersistence = current == null || current.compareTo(new SpecificationVersion("1.18")) >= 0; // NOI18N
        } catch (IOException ex) {
            Logger.getLogger(LayoutIterator.class.getName()).log(Level.INFO, null, ex);
            propertiesPersistence = false;
        }

        
        Map<String,String> replaceTokens = new HashMap<String,String>();
        replaceTokens.put("TEMPLATENAME", name);//NOI18N
        replaceTokens.put("PACKAGENAME", packageName);//NOI18N
        replaceTokens.put("MODE", mode); //NOI18N
        replaceTokens.put("OPENED", model.isOpened() ? "true" : "false"); //NOI18N
        replaceTokens.put("WINSYSBEHAVIOR", defineWinSysBehavior( model ) ); //NOI18N

        // 0. move icon file if necessary
        String icon = model.getIcon();
        File fil = null;
        if (icon != null) {
            fil = new File(icon);
            if (!fil.exists()) {
                fil = null;
            }
        }
        String relativeIconPath = null;
        if (fil != null) {
            FileObject fo = FileUtil.toFileObject(fil);
            if (!FileUtil.isParentOf(Util.getResourceDirectory(project), fo)) {
                String iconPath = getRelativePath(moduleInfo.getResourceDirectoryPath(false), packageName, 
                                                "", fo.getNameExt()); //NOI18N
                fileChanges.add(fileChanges.createFile(iconPath, fo));
                relativeIconPath = packageName.replace('.', '/') + "/" + fo.getNameExt(); // NOI18N
            } else {
                relativeIconPath = FileUtil.getRelativePath(Util.getResourceDirectory(project), fo);
            }
            replaceTokens.put("ICONPATH", relativeIconPath);//NOI18N
            replaceTokens.put("COMMENTICON", "");//NOI18N
            
        } else {
            replaceTokens.put("ICONPATH", "SET/PATH/TO/ICON/HERE"); //NOI18N
            replaceTokens.put("COMMENTICON", "//");//NOI18N
        }
        
        
        // 2. update project dependencies
        replaceTokens.put("MODULENAME", moduleInfo.getCodeNameBase()); // NOI18N
        String specVersion = moduleInfo.getSpecVersion();
        replaceTokens.put("SPECVERSION", specVersion != null ? specVersion : "0"); // NOI18N
        fileChanges.add(fileChanges.addModuleDependency("org.openide.windows")); //NOI18N
        fileChanges.add(fileChanges.addManifestToken(ManifestManager.OPENIDE_MODULE_REQUIRES, WindowManager.class.getName()));
        fileChanges.add(fileChanges.addModuleDependency("org.openide.util")); //NOI18N
        fileChanges.add(fileChanges.addModuleDependency("org.openide.util.lookup")); //NOI18N
        fileChanges.add(fileChanges.addModuleDependency("org.openide.awt")); //NOI18N
        if (propertiesPersistence) {
            fileChanges.add(fileChanges.addModuleDependency("org.netbeans.modules.settings")); //NOI18N
        }
        
        // x. generate java classes
        final String tcName = getRelativePath(moduleInfo.getSourceDirectoryPath(), packageName,
                name, "TopComponent.java"); //NOI18N
        FileObject template = CreatedModifiedFiles.getTemplate(
            xmlLessTC ? "templateTopComponent637.java" :    
            (propertiesPersistence ? "templateTopComponentAnno.java" : "templateTopComponent.java")
        );
        fileChanges.add(fileChanges.createFileWithSubstitutions(tcName, template, replaceTokens));
        // x. generate java classes
        final String tcFormName = getRelativePath(moduleInfo.getSourceDirectoryPath(), packageName,
                name, "TopComponent.form"); //NOI18N
        template = CreatedModifiedFiles.getTemplate("templateTopComponent.form");//NOI18N
        fileChanges.add(fileChanges.createFileWithSubstitutions(tcFormName, template, replaceTokens));

        if (!actionLessTC) {
            final String actionName = getRelativePath(moduleInfo.getSourceDirectoryPath(), packageName,
                    name, "Action.java"); //NOI18N
            template = CreatedModifiedFiles.getTemplate("templateAction.java");//NOI18N
            fileChanges.add(fileChanges.createFileWithSubstitutions(actionName, template, replaceTokens));
        }
        
        if (!xmlLessTC) {
            final String settingsName = name + "TopComponent.settings"; //NOI18N
            template = CreatedModifiedFiles.getTemplate("templateSettings.xml");//NOI18N
            fileChanges.add(fileChanges.createLayerEntry("Windows2/Components/" + settingsName, template, replaceTokens, null, null)); // NOI18N
        }
        
        if (!xmlLessTC) {
            final String wstcrefName = name + "TopComponent.wstcref"; //NOI18N
            template = CreatedModifiedFiles.getTemplate("templateWstcref.xml");//NOI18N
            fileChanges.add(fileChanges.createLayerEntry("Windows2/Modes/" + mode + "/" + wstcrefName, // NOI18N
                                 template, replaceTokens, null, null));
        }

        String bundlePath = getRelativePath(moduleInfo.getResourceDirectoryPath(false), packageName, "", "Bundle.properties"); //NOI18N
        if (xmlLessTC) {
            // nothing in layer
        } else if (actionLessTC) {
            String path = "Actions/Window/" + packageName.replace('.','-') + "-" + name + "Action.instance"; // NOI18N
            {
                Map<String,Object> attrs = new HashMap<String,Object>();
                attrs.put("instanceCreate", "methodvalue:org.openide.windows.TopComponent.openAction"); // NOI18N
                attrs.put("component", "methodvalue:" + packageName + '.' + name + "TopComponent.findInstance"); // NOI18N
                if (relativeIconPath != null) {
                    attrs.put("iconBase", relativeIconPath); // NOI18N
                }
                attrs.put("displayName", "bundlevalue:" + packageName + ".Bundle#CTL_" + name + "Action"); // NOI18N
                fileChanges.add(
                    fileChanges.createLayerEntry(
                        path,
                        null,
                        null,
                        null,
                        attrs
                    )
                );
            }

            {
                Map<String,Object> attrs = new HashMap<String,Object>();
                attrs.put("originalFile", path); // NOI18N
                fileChanges.add(
                    fileChanges.createLayerEntry(
                        "Menu/Window/" + name + "Action.shadow", // NOI18N
                        null,
                        null,
                        null,
                        attrs
                    )
                );
            }
        } else {
            fileChanges.add(fileChanges.layerModifications(new CreateActionEntryOperation(name + "Action", packageName), // NOI18N
                                                       Collections.<String>emptySet()));
        }
        // XXX use @Messages where available
        fileChanges.add(fileChanges.bundleKey(bundlePath, "CTL_" + name + "Action",  // NOI18N
                                name)); //NOI18N
        
        fileChanges.add(fileChanges.bundleKey(bundlePath, "CTL_" + name + "TopComponent",  // NOI18N
                                name + " Window")); //NOI18N
        fileChanges.add(fileChanges.bundleKey(bundlePath, "HINT_" + name + "TopComponent",  // NOI18N
                                "This is a " + name + " window")); //NOI18N
        
        final Map<String, String> newModes = model.getNewModes();
        if (newModes != null) {
            Set<String> files = new HashSet<String>();
            FileObject parent;
            try {
                parent = moduleInfo.getEffectiveSystemFilesystem().findResource("Windows2/Modes");
            } catch (IOException x) {
                Exceptions.printStackTrace(x);
                parent = null;
            }
            for (String wsmode : newModes.keySet()) {
                files.add(LayerUtils.findGeneratedName(parent, wsmode + ".wsmode"));
            }
            
            fileChanges.add(fileChanges.layerModifications(new CreatedModifiedFiles.LayerOperation() {
                @Override
                public void run(FileSystem layer) throws IOException {
                    FileObject fo = FileUtil.createFolder(layer.getRoot(), "Windows2/Modes");
                    for (Map.Entry<String, String> entry : newModes.entrySet()) {
                        FileObject wsmode = fo.createData(entry.getKey() + ".wsmode");
                        OutputStream os = wsmode.getOutputStream();
                        try {
                            os.write(entry.getValue().getBytes("UTF-8"));
                        } finally {
                            os.close();
                        }
                    }
                }   
            }, files));
        }
        
        model.setCreatedModifiedFiles(fileChanges);
    }

    private static String defineWinSysBehavior( DataModel model ) {
        StringBuffer res = new StringBuffer();
        if( model.isClosingNotAllowed() ) {
            res.append("\tputClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);\n");
        }
        if( model.isDraggingNotAllowed() ) {
            res.append("\tputClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);\n");
        }
        if( model.isMaximizationNotAllowed() ) {
            res.append("\tputClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);\n");
        }
        if( model.isSlidingNotAllowed() ) {
            res.append("\tputClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);\n");
        }
        if( model.isUndockingNotAllowed() ) {
            res.append("\tputClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);\n");
        }
        if( model.isKeepPrefSize() ) {
            res.append("\tputClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);\n");
        }
        return res.toString();
    }
    
    private static String getRelativePath(String rootpath, String fullyQualifiedPackageName,
            String prefix, String postfix) {
        StringBuffer sb = new StringBuffer();
        
        sb.append(rootpath).append('/').append(fullyQualifiedPackageName.replace('.','/'))
                        .append('/').append(prefix).append(postfix);
        
        return sb.toString();
    }
    
    static class CreateActionEntryOperation implements CreatedModifiedFiles.LayerOperation {
        private String name;
        private String packageName;
        
        public CreateActionEntryOperation(String actionname, String packageName) {
            this.packageName = packageName;
            this.name = actionname;
        }
        
        public void run(FileSystem layer) throws IOException {
            FileObject folder = layer.getRoot().getFileObject("Actions/Window");// NOI18N
            if (folder == null) {
                folder = FileUtil.createFolder(layer.getRoot(), "Actions/Window"); // NOI18N
            }
            String instance = packageName.replace('.','-') + "-" + name; // NOI18N
            FileObject file = folder.createData(instance, "instance"); // NOI18N
            folder = layer.getRoot().getFileObject("Menu/Window");// NOI18N
            if (folder == null) {
                folder = FileUtil.createFolder(layer.getRoot(), "Menu/Window"); // NOI18N
            }
            file = folder.createData(name, "shadow"); // NOI18N
            file.setAttribute("originalFile", "Actions/Window/" + instance + ".instance"); // NOI18N
        }
    }
    
}
