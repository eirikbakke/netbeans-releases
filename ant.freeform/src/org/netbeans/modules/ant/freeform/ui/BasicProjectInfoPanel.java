/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.ant.freeform.ui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.tools.ant.module.api.support.AntScriptUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 * @author  David Konecny
 */
public class BasicProjectInfoPanel extends javax.swing.JPanel implements HelpCtx.Provider{

    private static final String SET_AS_MAIN_PREF = "setAsMain"; // NOI18N
    
    private DocumentListener documentListener;
    private ChangeListener listener;
    /** Was antScript property edited by user? */
    private boolean antScriptTouched = false;
    /** Was projectFolder property edited by user? */
    private boolean projectFolderTouched = false;
    /** Was projectName property edited by user? */
    private boolean projectNameTouched = false;
    /** Is choosen Ant script a valid one? */
    private boolean antScriptValidityChecked;
    
    public BasicProjectInfoPanel(String projectLocation, String antScript, String projectName, String projectFolder,
            ChangeListener listener) {
        initComponents();
        mainProject.setSelected(NbPreferences.forModule(BasicProjectInfoPanel.class).getBoolean(SET_AS_MAIN_PREF, true));
        this.projectLocation.setText(projectLocation);
        this.antScript.setText(antScript);
        this.projectName.setText(projectName);
        this.projectFolder.setText(projectFolder);
        this.listener = listener;
        documentListener = new DocumentListener() {           
            public void insertUpdate(DocumentEvent e) {
                update(e);
            }

            public void removeUpdate(DocumentEvent e) {
                update(e);
            }

            public void changedUpdate(DocumentEvent e) {
                update(e);
            }
        };
        this.projectLocation.getDocument().addDocumentListener(documentListener);
        this.antScript.getDocument().addDocumentListener(documentListener);
        this.projectName.getDocument().addDocumentListener(documentListener);
        this.projectFolder.getDocument().addDocumentListener(documentListener);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(BasicProjectInfoPanel.class);
    }

    public File getProjectLocation() {
        return getAsFile(projectLocation.getText());
    }

    public File getAntScript() {
        return getAsFile(antScript.getText());
    }

    public String getProjectName() {
        return projectName.getText();
    }

    public File getProjectFolder() {
        return getAsFile(projectFolder.getText());
    }

    public boolean getMainProject() {
        boolean b = mainProject.isSelected();
        NbPreferences.forModule(BasicProjectInfoPanel.class).putBoolean(SET_AS_MAIN_PREF, b);
        return b;
    }

    public String[] getError() {
        if (projectLocation.getText().length() == 0) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_1"), WizardDescriptor.PROP_INFO_MESSAGE };  //NOI18N
        }
        if (!getProjectLocation().exists()) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_2"), WizardDescriptor.PROP_ERROR_MESSAGE };  //NOI18N
        }
        if (antScript.getText().length() == 0) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_3"), WizardDescriptor.PROP_INFO_MESSAGE };  //NOI18N
        }
        if (!getAntScript().exists()) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_4"), WizardDescriptor.PROP_ERROR_MESSAGE };  //NOI18N
        }
        if (!antScriptValidityChecked) {
            FileObject fo = FileUtil.toFileObject(getAntScript());
            if (fo != null) {
                try {
                    AntScriptUtils.getCallableTargetNames(fo);
                    antScriptValidityChecked = true;
                } catch (IOException x) {/* failed */}
            }
            if (!antScriptValidityChecked) {
                return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_5"), WizardDescriptor.PROP_ERROR_MESSAGE };  //NOI18N
            }
        }
        if (getProjectName().length() == 0) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_6"), WizardDescriptor.PROP_INFO_MESSAGE };  //NOI18N
        }
        if (projectFolder.getText().length() == 0) {
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_7"), WizardDescriptor.PROP_INFO_MESSAGE };  //NOI18N
        }
        if (getAsFile(projectFolder.getText() + File.separatorChar + "nbproject").exists()){ // NOI18N
            return new String[] { org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_8"), WizardDescriptor.PROP_ERROR_MESSAGE };  //NOI18N
        }
        
        Project p;
        File prjFolder = getProjectFolder();
        
        assert prjFolder != null;
        
        if ((p = FileOwnerQuery.getOwner(prjFolder.toURI())) != null && prjFolder.equals(FileUtil.toFile(p.getProjectDirectory()))) {
            ProjectInformation pi = p.getLookup().lookup(ProjectInformation.class);
            String displayName = (pi == null ? "" : pi.getDisplayName());   //NOI18N
            return new String[] { MessageFormat.format(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_9"),  //NOI18N
                new Object[] {displayName}), WizardDescriptor.PROP_ERROR_MESSAGE };
        }
        
        File prjLocation = getProjectLocation();
        
        assert prjLocation != null;
        
        if ((p = FileOwnerQuery.getOwner(prjLocation.toURI())) != null && prjLocation.equals(FileUtil.toFile(p.getProjectDirectory()))) {
            ProjectInformation pi = p.getLookup().lookup(ProjectInformation.class);
            String displayName = (pi == null ? "" : pi.getDisplayName());   //NOI18N
            return new String[] { MessageFormat.format(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_Error_10"),  //NOI18N
                new Object[] {displayName}), WizardDescriptor.PROP_ERROR_MESSAGE };
        }
        return null;
    }

    private File getAsFile(String filename) {
        return FileUtil.normalizeFile(new File(filename));
    }

    private boolean ignoreEvent = false;

    private void update(DocumentEvent e) {
        if (ignoreEvent) {
            // side-effect of changes done in this handler
            return;
        }

        // start ignoring events
        ignoreEvent = true;

        if (projectLocation.getDocument() == e.getDocument()) {
            antScriptValidityChecked = false;
            updateAntScriptLocation();
            updateProjectName();
            updateProjectFolder();
        }
        if (antScript.getDocument() == e.getDocument()) {
            antScriptValidityChecked = false;
            updateProjectName();
        }

        // stop ignoring events
        ignoreEvent = false;

        if (projectFolder.getDocument() == e.getDocument()) {
            projectFolderTouched = !"".equals(projectFolder.getText());  //NOI18N
        }
        if (antScript.getDocument() == e.getDocument()) {
            antScriptTouched = !"".equals(antScript.getText());  //NOI18N
        }
        if (projectName.getDocument() == e.getDocument()) {
            projectNameTouched = !"".equals(projectName.getText());  //NOI18N
        }

        listener.stateChanged(null);
    }

    private boolean isValidProjectLocation() {
        return (getProjectLocation().exists() && getProjectLocation().isDirectory() &&
                projectLocation.getText().length() > 0 && (!projectLocation.getText().endsWith(":"))); // NOI18N
    }

    private void updateAntScriptLocation() {
        if (antScriptTouched) {
            return;
        }
        if (isValidProjectLocation()) {
            File as = new File(getProjectLocation().getAbsolutePath() + File.separatorChar + "build.xml"); // NOI18N
            if (as.exists()) {
                antScript.setText(as.getAbsolutePath());
                return;
            }
        }
        antScript.setText(""); // NOI18N
    }

    private void updateProjectName() {
        if (projectNameTouched) {
            return;
        }
        if (getAntScript().exists()) {
            File as = new File(getAntScript().getAbsolutePath());
            if (as.exists()) {
                FileObject fo = FileUtil.toFileObject(as);
                assert fo != null : as;
                String name = AntScriptUtils.getAntScriptName(fo);
                if (name != null) {
                    projectName.setText(name);
                    return;
                }
            }
        }
        projectName.setText(""); // NOI18N
    }

    private void updateProjectFolder() {
        if (projectFolderTouched) {
            return;                                                                
        }
        if (isValidProjectLocation()) {
            projectFolder.setText(getProjectLocation().getAbsolutePath());
        } else {
            projectFolder.setText(""); // NOI18N
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        antScript = new javax.swing.JTextField();
        projectName = new javax.swing.JTextField();
        projectFolder = new javax.swing.JTextField();
        browseAntScript = new javax.swing.JButton();
        browseProjectFolder = new javax.swing.JButton();
        projectLocation = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        browseProjectLocation = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        mainProject = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextArea2 = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(400, 360));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel1")); // NOI18N

        jLabel2.setLabelFor(antScript);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel2")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 12);
        add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel2")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel3")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 10, 0);
        add(jLabel3, gridBagConstraints);
        jLabel3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel3")); // NOI18N

        jLabel4.setLabelFor(projectName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel4")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jLabel4, gridBagConstraints);
        jLabel4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel4")); // NOI18N

        jLabel5.setLabelFor(projectFolder);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel5")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 12);
        add(jLabel5, gridBagConstraints);
        jLabel5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel5")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 12);
        add(antScript, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(projectName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 12);
        add(projectFolder, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseAntScript, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "BTN_BasicProjectInfoPanel_browseAntScript")); // NOI18N
        browseAntScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseAntScriptActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(browseAntScript, gridBagConstraints);
        browseAntScript.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_browseAntScript")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseProjectFolder, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "BTN_BasicProjectInfoPanel_browseProjectFolder")); // NOI18N
        browseProjectFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseProjectFolderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(browseProjectFolder, gridBagConstraints);
        browseProjectFolder.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_browseProjectFolder")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(projectLocation, gridBagConstraints);

        jLabel6.setLabelFor(projectLocation);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_jLabel6")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jLabel6, gridBagConstraints);
        jLabel6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_jLabel6")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseProjectLocation, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "BTN_BasicProjectInfoPanel_browseProjectLocation")); // NOI18N
        browseProjectLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseProjectLocationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(browseProjectLocation, gridBagConstraints);
        browseProjectLocation.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_browseProjectLocation")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 10, 0);
        add(jSeparator1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(mainProject, org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_BasicProjectInfoPanel_mainProject")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(mainProject, gridBagConstraints);
        mainProject.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_BasicProjectInfoPanel_mainProject")); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Label.disabledForeground")));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/ant/freeform/resources/alert_32.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 0);
        jPanel2.add(jLabel7, gridBagConstraints);

        jTextArea2.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextArea2.setEditable(false);
        jTextArea2.setLineWrap(true);
        jTextArea2.setText(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "Freeform_Warning_Message")); // NOI18N
        jTextArea2.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 4, 4);
        jPanel2.add(jTextArea2, gridBagConstraints);
        jTextArea2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSN_Freeform_Warning_Message")); // NOI18N
        jTextArea2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BasicProjectInfoPanel.class, "ACSD_Freeform_Warning_Message")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void browseProjectLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseProjectLocationActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        if (projectLocation.getText().length() > 0 && getProjectLocation().exists()) {
            chooser.setSelectedFile(getProjectLocation());
        } else {
            chooser.setSelectedFile(ProjectChooser.getProjectsFolder());
        }
        chooser.setDialogTitle(NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_Browse_Location"));  //NOI18N
        if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectLoc = FileUtil.normalizeFile(chooser.getSelectedFile());
            projectLocation.setText(projectLoc.getAbsolutePath());
        }
    }//GEN-LAST:event_browseProjectLocationActionPerformed

    private void browseProjectFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseProjectFolderActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
        if (projectFolder.getText().length() > 0 && getProjectFolder().exists()) {
            chooser.setSelectedFile(getProjectFolder());
        } else if (projectLocation.getText().length() > 0 && getProjectLocation().exists()) {
            chooser.setSelectedFile(getProjectLocation());
        } else {
            chooser.setSelectedFile(ProjectChooser.getProjectsFolder());
        }
        chooser.setDialogTitle(NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_Browse_Project_Folder"));  //NOI18N
        if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File projectDir = FileUtil.normalizeFile(chooser.getSelectedFile());
            projectFolder.setText(projectDir.getAbsolutePath());
        }                    
    }//GEN-LAST:event_browseProjectFolderActionPerformed

    private void browseAntScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseAntScriptActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode (JFileChooser.FILES_ONLY);
        if (antScript.getText().length() > 0 && getAntScript().exists()) {
            chooser.setSelectedFile(getAntScript());
        } else if (projectLocation.getText().length() > 0 && getProjectLocation().exists()) {
            chooser.setSelectedFile(getProjectLocation());
        } else {
            chooser.setSelectedFile(ProjectChooser.getProjectsFolder());
        }
        chooser.setDialogTitle(NbBundle.getMessage(BasicProjectInfoPanel.class, "LBL_Browse_Build_Script"));  //NOI18N
        if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File script = FileUtil.normalizeFile(chooser.getSelectedFile());
            antScript.setText(script.getAbsolutePath());
        }            
    }//GEN-LAST:event_browseAntScriptActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField antScript;
    private javax.swing.JButton browseAntScript;
    private javax.swing.JButton browseProjectFolder;
    private javax.swing.JButton browseProjectLocation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JCheckBox mainProject;
    private javax.swing.JTextField projectFolder;
    private javax.swing.JTextField projectLocation;
    private javax.swing.JTextField projectName;
    // End of variables declaration//GEN-END:variables
    
}
