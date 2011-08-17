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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.cnd.api.remote.RemoteFileUtil;
import org.netbeans.modules.cnd.makeproject.api.MakeArtifact;
import org.netbeans.modules.cnd.utils.FSPath;
import org.netbeans.modules.cnd.utils.cache.CndFileUtils;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.remote.spi.FileSystemProvider;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

public class MakeArtifactChooser extends JPanel implements PropertyChangeListener {
    public enum ArtifactType {PROJECT, LIBRARY};
    
    private ArtifactType artifactType;
    private static final RequestProcessor RP = new RequestProcessor("MakeArtifactChooser",1); // NOI18N
    private final FSPath baseDir;
    
    /** Creates new form JarArtifactChooser */
    public MakeArtifactChooser( ArtifactType artifactType, JFileChooser chooser , FSPath baseDir) {
        this.artifactType = artifactType;
        this.baseDir = baseDir;
        
        initComponents();
        MyDefaultListModel model = new MyDefaultListModel(null, artifactType, baseDir);
        model.init();
        listArtifacts.setModel( model);
        chooser.addPropertyChangeListener( this );

	//PathPanel pathPanel = new PathPanel();
	//leftPanel.add(pathPanel);
        
        // Accessibility
        listArtifacts.getAccessibleContext().setAccessibleDescription(getString("PROJECT_LIBRARY_FILES_AD")); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        libFilesLabel = new javax.swing.JLabel();
        scrollPane1 = new javax.swing.JScrollPane();
        listArtifacts = new javax.swing.JList();

        setLayout(new java.awt.GridBagLayout());

        projectLabel.setLabelFor(projectTextField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, bundle.getString("PROJECT_NAME_TXT")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        add(projectLabel, gridBagConstraints);

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 6, 0);
        add(projectTextField, gridBagConstraints);

        libFilesLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle").getString("PROJECT_LIBRARY_FILES_MN").charAt(0));
        libFilesLabel.setLabelFor(listArtifacts);
        org.openide.awt.Mnemonics.setLocalizedText(libFilesLabel, bundle.getString("PROJECT_LIBRARY_FILES_TXT")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        add(libFilesLabel, gridBagConstraints);

        scrollPane1.setViewportView(listArtifacts);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(scrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())) {
            // We have to update the Accessory
            JFileChooser chooser = (JFileChooser) e.getSource();
            File dir = chooser.getSelectedFile(); // may be null (#46744)
            if (dir != null) {
                MyDefaultListModel oldModel  =(MyDefaultListModel) listArtifacts.getModel();
                oldModel.cancel();
                final MyDefaultListModel model = new MyDefaultListModel(dir, artifactType, baseDir);
                listArtifacts.setModel(model);
                RP.post(new Runnable() {

                    @Override
                    public void run() {
                        if (SwingUtilities.isEventDispatchThread()) {
                            if (!model.isCanceled()) {
                                projectTextField.setText(model.project == null ? "" : ProjectUtils.getInformation(model.project).getDisplayName()); //NOI18N
                                listArtifacts.setModel(model);
                                if (model.def >=0 ) {
                                    listArtifacts.setSelectionInterval(model.def, model.def);
                                }
                            }
                        } else {
                            model.init();
                            if (!model.isCanceled()) {
                                SwingUtilities.invokeLater(this);
                            }
                        }
                    }
                });
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel libFilesLabel;
    private javax.swing.JList listArtifacts;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JScrollPane scrollPane1;
    // End of variables declaration//GEN-END:variables

    
    /** Shows dialog with the artifact chooser 
     * @return null if canceled selected jars if some jars selected
     */
    public static MakeArtifact[] showDialog(ArtifactType artifactType, Project master, FSPath baseDir, Component parent ) {
        ExecutionEnvironment env = FileSystemProvider.getExecutionEnvironment(baseDir.getFileSystem());
        String seed = RemoteFileUtil.getCurrentChooserFile(env);
        JFileChooser chooser = RemoteFileUtil.createProjectChooser(env,
                getString("ADD_PROJECT_DIALOG_TITLE"), getString("ADD_PROJECT_DIALOG_AD"), getString("ADD_BUTTON_TXT"), seed); // NOI18N
        MakeArtifactChooser accessory = new MakeArtifactChooser( artifactType, chooser, baseDir );
        chooser.setAccessory( accessory );
        chooser.setPreferredSize( new Dimension( 650, 380 ) );

        int option = chooser.showOpenDialog( parent ); // Show the chooser
        if ( option == JFileChooser.APPROVE_OPTION ) {
            RemoteFileUtil.setCurrentChooserFile(chooser.getCurrentDirectory().getAbsolutePath(), env);
            MyDefaultListModel model = (MyDefaultListModel) accessory.listArtifacts.getModel();
            Project selectedProject = model.getProject();

            if ( selectedProject == null ) {
                return null;
            }
            
            if ( selectedProject.getProjectDirectory().equals( master.getProjectDirectory() ) ) {
                DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message( 
                    getString("ADD_ITSELF_ERROR"), // NOI18N
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
                return null;
            }
            
	    // FIXUP: need to check for this
            if ( ProjectUtils.hasSubprojectCycles( master, selectedProject ) ) {
                DialogDisplayer.getDefault().notify( new NotifyDescriptor.Message( 
                    getString("ADD_CYCLIC_ERROR"),  // NOI18N
                    NotifyDescriptor.INFORMATION_MESSAGE ) );
                return null;
            }
            
            Object[] tmp = new Object[model.getSize()];
            int count = 0;
            for(int i = 0; i < tmp.length; i++) {
                if (accessory.listArtifacts.isSelectedIndex(i)) {
                    Object elementAt = model.getElementAt(i);
                    if (elementAt instanceof MakeArtifact) {
                        tmp[count] = elementAt;
                        count++;
                    }
                }
            }
            MakeArtifact artifactItems[] = new MakeArtifact[count];
            System.arraycopy(tmp, 0, artifactItems, 0, count);
            return artifactItems;
        }
        else {
            return null; 
        }
                
    }
    
    private static String getString(String s) {
        return NbBundle.getBundle(MakeArtifactChooser.class).getString(s);
    }

    private static final class MyDefaultListModel extends DefaultListModel {
        private final File dir;
        private final ArtifactType artifactType;
        private Project project;
        private int def = -1;
        private AtomicBoolean canceled = new AtomicBoolean(false);
        private final FSPath baseDir;

        private MyDefaultListModel(File dir, ArtifactType artifactType, FSPath baseDir){
            this.dir = dir;
            this.artifactType = artifactType;
            this.baseDir = baseDir;
            addElement(getString("LOADING_PROJECT")); // NOI18N
        }

        private Project getProject() {
            return project;
        }

        private boolean isCanceled() {
            return canceled.get();
        }
        
        private void cancel() {
            canceled.set(true);
        }

        private void init() {
            project = _getProject(); // may be null
            if (project == null) {
                clear();
                return;
            }
            if (!isCanceled()) {
                populateAccessory(project);
            }
        }

        private Project _getProject() {
            if (dir == null) {
                return null;
            }
            String projectAbsPath = dir.getAbsolutePath();
            if (projectAbsPath == null) { // #46744
                return null;
            }

            try {
                FileObject fo = baseDir.getFileSystem().findResource(CndFileUtils.normalizeAbsolutePath(baseDir.getFileSystem(), projectAbsPath));
                if (fo != null && fo.isValid()) {
                    return ProjectManager.getDefault().findProject(fo);
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                // Return null
            }

            return null;
        }
        /**
         * Set up GUI fields according to the requested project.
         * @param project a subproject, or null
         */
        private void populateAccessory( Project project ) {
            MakeArtifact[] artifacts = MakeArtifact.getMakeArtifacts(project);
            clear();
            if (artifacts == null) {
                return;
            }
            for (int i = 0; i < artifacts.length; i++) {
                if (artifactType == ArtifactType.LIBRARY) {
                    if (artifacts[i].getConfigurationType() == MakeArtifact.TYPE_UNKNOWN &&
                        (artifacts[i].getOutput().endsWith(".a") || // NOI18N
                            artifacts[i].getOutput().endsWith(".so") || // NOI18N
                            artifacts[i].getOutput().endsWith(".dylib") || // NOI18N
                            artifacts[i].getOutput().endsWith(".lib") || // NOI18N
                            artifacts[i].getOutput().endsWith(".dll")) || // NOI18N
                        artifacts[i].getConfigurationType() == MakeArtifact.TYPE_DYNAMIC_LIB ||
                        artifacts[i].getConfigurationType() == MakeArtifact.TYPE_CUSTOM || // <== FIXUP
                        artifacts[i].getConfigurationType() == MakeArtifact.TYPE_STATIC_LIB ||
                        artifacts[i].getConfigurationType() == MakeArtifact.TYPE_QT_DYNAMIC_LIB ||
                        artifacts[i].getConfigurationType() == MakeArtifact.TYPE_QT_STATIC_LIB) {
                        addElement(artifacts[i]);
                    }
                } else if (artifactType == ArtifactType.PROJECT) {
                    addElement(artifacts[i]);
                } else {
                    assert false;
                }
                if (artifacts[i].getActive()) {
                    def = i;
                }
            }
        }
    }
}
