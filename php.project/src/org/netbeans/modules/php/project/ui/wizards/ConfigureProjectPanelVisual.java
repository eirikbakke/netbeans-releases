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

package org.netbeans.modules.php.project.ui.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.php.project.ui.LocalServer;
import org.netbeans.modules.php.project.ui.LocalServerController;
import org.netbeans.modules.php.project.ui.Utils;
import org.netbeans.modules.php.project.ui.Utils.EncodingModel;
import org.netbeans.modules.php.project.ui.Utils.EncodingRenderer;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

class ConfigureProjectPanelVisual extends JPanel implements DocumentListener, ChangeListener, ActionListener {

    private static final long serialVersionUID = 5104232236346379L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final LocalServerController localServerComponent;

    ConfigureProjectPanelVisual(ConfigureProjectPanel wizardPanel) {
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectNameLocation"));
        putClientProperty("WizardPanel_contentSelectedIndex", 0); // NOI18N
        // Step name (actually the whole list for reference).
        putClientProperty("WizardPanel_contentData", wizardPanel.getSteps()); // NOI18N

        initComponents();
        localServerComponent = LocalServerController.create(localServerComboBox, localServerButton,
                NbBundle.getMessage(LocationPanelVisual.class, "LBL_SelectSourceFolderTitle"));
        init();
    }

    private void init() {
        projectFolderTextField.getDocument().addDocumentListener(this);
        projectNameTextField.getDocument().addDocumentListener(this);
        localServerComponent.addChangeListener(this);
        createIndexCheckBox.addActionListener(this);
        indexNameTextField.getDocument().addDocumentListener(this);

        encodingComboBox.setModel(new EncodingModel());
        encodingComboBox.setRenderer(new EncodingRenderer());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }

    void addConfigureProjectListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    void removeConfigureProjectListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectFolderLabel = new javax.swing.JLabel();
        projectFolderTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        sourcesLabel = new javax.swing.JLabel();
        localServerComboBox = new javax.swing.JComboBox();
        localServerButton = new javax.swing.JButton();
        indexFileLabel = new javax.swing.JLabel();
        indexNameTextField = new javax.swing.JTextField();
        createIndexCheckBox = new javax.swing.JCheckBox();
        encodingLabel = new javax.swing.JLabel();
        encodingComboBox = new javax.swing.JComboBox();
        setAsMainCheckBox = new javax.swing.JCheckBox();
        separator = new javax.swing.JSeparator();

        projectNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectName")); // NOI18N
        projectNameLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        projectFolderLabel.setLabelFor(projectFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectFolderLabel, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_ProjectFolder")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_BrowseProject")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        sourcesLabel.setLabelFor(localServerComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(sourcesLabel, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_Sources")); // NOI18N
        sourcesLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        localServerComboBox.setEditable(true);

        org.openide.awt.Mnemonics.setLocalizedText(localServerButton, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_LocalServerBrowse")); // NOI18N

        indexFileLabel.setLabelFor(indexNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(indexFileLabel, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_IndexFile")); // NOI18N

        indexNameTextField.setText("index.php"); // NOI18N

        createIndexCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createIndexCheckBox, org.openide.util.NbBundle.getBundle(ConfigureProjectPanelVisual.class).getString("LBL_CreateIndexFile")); // NOI18N
        createIndexCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        encodingLabel.setLabelFor(encodingComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(encodingLabel, org.openide.util.NbBundle.getMessage(ConfigureProjectPanelVisual.class, "LBL_Encoding")); // NOI18N

        setAsMainCheckBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/php/project/ui/wizards/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, bundle.getString("LBL_SetAsMain")); // NOI18N
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(separator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .add(projectFolderLabel)
                    .add(setAsMainCheckBox)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(encodingLabel)
                            .add(indexFileLabel)
                            .add(sourcesLabel)
                            .add(projectNameLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(localServerComboBox, 0, 228, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(localServerButton))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(indexNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(createIndexCheckBox))
                                    .add(encodingComboBox, 0, 323, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(browseButton))
                            .add(projectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)))))
        );

        layout.linkSize(new java.awt.Component[] {browseButton, localServerButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectNameLabel)
                    .add(projectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectFolderLabel)
                    .add(projectFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourcesLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(localServerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(localServerButton))
                .add(18, 18, 18)
                .add(separator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(indexFileLabel)
                    .add(indexNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(createIndexCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(encodingLabel)
                    .add(encodingComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(setAsMainCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String newLocation = Utils.browseLocationAction(this, getProjectFolder(),
                NbBundle.getMessage(LocationPanelVisual.class, "LBL_SelectProjectFolder"));
        if (newLocation != null) {
            setProjectFolder(newLocation);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox createIndexCheckBox;
    private javax.swing.JComboBox encodingComboBox;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JLabel indexFileLabel;
    private javax.swing.JTextField indexNameTextField;
    private javax.swing.JButton localServerButton;
    private javax.swing.JComboBox localServerComboBox;
    private javax.swing.JLabel projectFolderLabel;
    private javax.swing.JTextField projectFolderTextField;
    private javax.swing.JLabel projectNameLabel;
    protected javax.swing.JTextField projectNameTextField;
    private javax.swing.JSeparator separator;
    private javax.swing.JCheckBox setAsMainCheckBox;
    private javax.swing.JLabel sourcesLabel;
    // End of variables declaration//GEN-END:variables

    public String getProjectName() {
        return projectNameTextField.getText().trim();
    }

    public String getProjectFolder() {
        return projectFolderTextField.getText().trim();
    }

    /**
     * @return <b>non-normalized</b> {@link File file} for project folder.
     */
    public File getProjectFolderFile() {
        return new File(getProjectFolder());
    }

    public void setProjectName(String projectName) {
        projectNameTextField.setText(projectName);
        projectNameTextField.selectAll();
    }

    public void setProjectFolder(String projectFolder) {
        projectFolderTextField.setText(projectFolder);
    }

    public LocalServer getSourcesLocation() {
        return localServerComponent.getLocalServer();
    }

    public MutableComboBoxModel getLocalServerModel() {
        return localServerComponent.getLocalServerModel();
    }

    public void setLocalServerModel(MutableComboBoxModel localServers) {
        localServerComponent.setLocalServerModel(localServers);
    }

    public void selectSourcesLocation(LocalServer localServer) {
        localServerComponent.selectLocalServer(localServer);
    }

    boolean isCreateIndex() {
        return createIndexCheckBox.isSelected();
    }

    void setCreateIndex(boolean createIndex) {
        createIndexCheckBox.setSelected(createIndex);
    }

    String getIndexName() {
        return indexNameTextField.getText().trim();
    }

    void setIndexName(String indexName) {
        indexNameTextField.setText(indexName);
    }

    Charset getEncoding() {
        return (Charset) encodingComboBox.getSelectedItem();
    }

    void setEncoding(Charset encoding) {
        encodingComboBox.setSelectedItem(encoding);
    }

    boolean isSetAsMain() {
        return setAsMainCheckBox.isSelected();
    }

    void setSetAsMain(boolean setAsMain) {
        setAsMainCheckBox.setSelected(setAsMain);
    }

    // listeners
    public void insertUpdate(DocumentEvent e) {
        processUpdate();
    }

    public void removeUpdate(DocumentEvent e) {
        processUpdate();
    }

    public void changedUpdate(DocumentEvent e) {
        processUpdate();
    }

    private void processUpdate() {
        changeSupport.fireChange();
    }

    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

    public void actionPerformed(ActionEvent e) {
        changeSupport.fireChange();
    }
}
