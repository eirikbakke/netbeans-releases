/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xml.jaxb.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author  gpatil
 */
public class JAXBBindingInfoPnl extends javax.swing.JPanel {

    /** Creates new form JAXBBindingInfoPnl */
    public JAXBBindingInfoPnl(JAXBWizBindingCfgPanel parent) {
        this.wizPanel = parent;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        schemaSource = new javax.swing.ButtonGroup();
        lblSchemaName = new javax.swing.JLabel();
        lblPrjName = new javax.swing.JLabel();
        lblSchemaFile = new javax.swing.JLabel();
        rdoSelectFromFileSys = new javax.swing.JRadioButton();
        txtSchemaName = new javax.swing.JTextField();
        txtPrjName = new javax.swing.JTextField();
        txtFilePath = new javax.swing.JTextField();
        btnBrowseFile = new javax.swing.JButton();
        rdoSelectURL = new javax.swing.JRadioButton();
        txtURL = new javax.swing.JTextField();
        lblPackageName = new javax.swing.JLabel();
        txtPackageName = new javax.swing.JTextField();
        lblOptions = new javax.swing.JLabel();
        chkbxNv = new javax.swing.JCheckBox();
        chkbxReadOnly = new javax.swing.JCheckBox();
        chkbxNpa = new javax.swing.JCheckBox();
        chkbxVerbose = new javax.swing.JCheckBox();
        chkbxQuiet = new javax.swing.JCheckBox();
        lblSchemaType = new javax.swing.JLabel();
        cmbSchemaType = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        chkbxUseBindingFile = new javax.swing.JCheckBox();
        chkbxUseCatalogFile = new javax.swing.JCheckBox();
        chkbxUseExtension = new javax.swing.JCheckBox();
        btnSelectBindingFile = new javax.swing.JButton();
        cmbBindingFiles = new javax.swing.JComboBox();
        txtCatalogFile = new javax.swing.JTextField();
        txtExtension = new javax.swing.JTextField();
        btnBrowseCatalogFile = new javax.swing.JButton();

        lblSchemaName.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_SchemaName")); // NOI18N

        lblPrjName.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Project")); // NOI18N

        lblSchemaFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_SchamaFile")); // NOI18N

        schemaSource.add(rdoSelectFromFileSys);
        rdoSelectFromFileSys.setSelected(true);
        rdoSelectFromFileSys.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_SelectFromLocalFileSystem")); // NOI18N
        rdoSelectFromFileSys.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdoSelectFromFileSys.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdoSelectFromFileSys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        txtSchemaName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeEvent(evt);
            }
        });
        txtSchemaName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                JAXBBindingInfoPnl.this.focusLost(evt);
            }
        });

        txtPrjName.setEditable(false);

        txtFilePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeEvent(evt);
            }
        });
        txtFilePath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                JAXBBindingInfoPnl.this.focusLost(evt);
            }
        });

        btnBrowseFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Browse")); // NOI18N
        btnBrowseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        schemaSource.add(rdoSelectURL);
        rdoSelectURL.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_SelectFromURL")); // NOI18N
        rdoSelectURL.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdoSelectURL.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdoSelectURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        txtURL.setEditable(false);
        txtURL.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_URL_Filler")); // NOI18N
        txtURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeEvent(evt);
            }
        });
        txtURL.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                JAXBBindingInfoPnl.this.focusLost(evt);
            }
        });

        lblPackageName.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_PackageName")); // NOI18N

        lblOptions.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Options")); // NOI18N

        chkbxNv.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_NV")); // NOI18N
        chkbxNv.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxNv.setLabel(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_NV")); // NOI18N
        chkbxNv.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkbxReadOnly.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_ReadOnly")); // NOI18N
        chkbxReadOnly.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxReadOnly.setLabel(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_ReadOnly")); // NOI18N
        chkbxReadOnly.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkbxNpa.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_NPA")); // NOI18N
        chkbxNpa.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxNpa.setLabel(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_NPA")); // NOI18N
        chkbxNpa.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkbxVerbose.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxVerbose.setLabel(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Verbose")); // NOI18N
        chkbxVerbose.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxVerbose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeEvent(evt);
            }
        });

        chkbxQuiet.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxQuiet.setLabel(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "chkbxQuiet.label")); // NOI18N
        chkbxQuiet.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxQuiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeEvent(evt);
            }
        });

        lblSchemaType.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_SchemaType")); // NOI18N

        cmbSchemaType.setModel(getSchemaTypeComboBoxModel());

        chkbxUseBindingFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_UseBindingFile")); // NOI18N
        chkbxUseBindingFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxUseBindingFile.setEnabled(false);
        chkbxUseBindingFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxUseBindingFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        chkbxUseCatalogFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_UseCatalogFile")); // NOI18N
        chkbxUseCatalogFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxUseCatalogFile.setEnabled(false);
        chkbxUseCatalogFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxUseCatalogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        chkbxUseExtension.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_UseExtension")); // NOI18N
        chkbxUseExtension.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxUseExtension.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxUseExtension.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectionHandler(evt);
            }
        });

        btnSelectBindingFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Configure")); // NOI18N
        btnSelectBindingFile.setEnabled(false);

        cmbBindingFiles.setEnabled(false);
        cmbBindingFiles.setPreferredSize(new java.awt.Dimension(275, 20));

        txtCatalogFile.setEditable(false);

        txtExtension.setEditable(false);
        txtExtension.setPreferredSize(new java.awt.Dimension(190, 20));

        btnBrowseCatalogFile.setText(org.openide.util.NbBundle.getMessage(JAXBBindingInfoPnl.class, "LBL_Browse")); // NOI18N
        btnBrowseCatalogFile.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(lblSchemaFile)
                                .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(lblSchemaName)
                                        .add(lblPrjName))
                                    .add(35, 35, 35)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(txtSchemaName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, txtPrjName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))))
                            .add(21, 21, 21))
                        .add(layout.createSequentialGroup()
                            .add(10, 10, 10)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createSequentialGroup()
                                    .add(rdoSelectFromFileSys)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 311, Short.MAX_VALUE))
                                .add(layout.createSequentialGroup()
                                    .add(rdoSelectURL)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(layout.createSequentialGroup()
                                            .add(txtFilePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 349, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(18, 18, 18)
                                            .add(btnBrowseFile))
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtExtension, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                                            .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                    .add(cmbBindingFiles, 0, 282, Short.MAX_VALUE)
                                                    .add(txtCatalogFile, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                                    .add(btnBrowseCatalogFile)
                                                    .add(btnSelectBindingFile)))
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cmbSchemaType, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtPackageName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                                            .add(layout.createSequentialGroup()
                                                .add(chkbxNv)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(chkbxReadOnly)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(chkbxNpa)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(chkbxVerbose)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(chkbxQuiet)))
                                        .add(txtURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 445, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(21, 21, 21)))))
                    .add(lblPackageName)
                    .add(lblOptions)
                    .add(lblSchemaType)
                    .add(chkbxUseCatalogFile)
                    .add(chkbxUseExtension)
                    .add(chkbxUseBindingFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 474, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSchemaName)
                    .add(txtSchemaName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPrjName)
                    .add(txtPrjName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblSchemaFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rdoSelectFromFileSys)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnBrowseFile)
                    .add(txtFilePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rdoSelectURL)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblPackageName)
                    .add(txtPackageName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOptions)
                    .add(chkbxNv)
                    .add(chkbxReadOnly)
                    .add(chkbxNpa)
                    .add(chkbxVerbose)
                    .add(chkbxQuiet))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSchemaType)
                    .add(cmbSchemaType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkbxUseBindingFile)
                    .add(btnSelectBindingFile)
                    .add(cmbBindingFiles, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkbxUseCatalogFile)
                    .add(txtCatalogFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnBrowseCatalogFile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chkbxUseExtension)
                    .add(txtExtension, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void focusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_focusLost
    this.wizPanel.fireChangeEvent();
}//GEN-LAST:event_focusLost

private void fireChangeEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireChangeEvent
    this.wizPanel.fireChangeEvent();
}//GEN-LAST:event_fireChangeEvent

    private void localFileSelected(boolean selected){
            this.txtFilePath.setEnabled(selected);
            this.txtFilePath.setEditable(selected);
            this.txtURL.setEnabled(!selected);
            this.txtURL.setEditable(!selected);
            this.btnBrowseFile.setEnabled(selected);
    }

    private void useBindingFileSelection(boolean selected){
        this.cmbBindingFiles.setEnabled(selected);
        this.btnSelectBindingFile.setEnabled(selected);
    }

    private void useCatalogFileSelection(boolean selected){
        this.txtCatalogFile.setEditable(selected);
        this.btnBrowseCatalogFile.setEnabled(selected);
    }

    private void useExtensionSelection(boolean selected){
        this.txtExtension.setEditable(selected);
    }

    private void btnSelectionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectionHandler
        if (evt.getSource() == this.rdoSelectFromFileSys){
            if (this.rdoSelectFromFileSys.isSelected()){
                localFileSelected(true);
            }
        }

        if (evt.getSource() == this.rdoSelectURL){
            if (this.rdoSelectURL.isSelected()){
                //urlSelected();
                localFileSelected(false);
            }
        }

        if (evt.getSource() == this.chkbxUseBindingFile){
            if (this.chkbxUseBindingFile.isSelected()){
                useBindingFileSelection(true);
            } else {
                useBindingFileSelection(false);
            }
        }

        if (evt.getSource() == this.chkbxUseCatalogFile){
            if (this.chkbxUseCatalogFile.isSelected()){
                useCatalogFileSelection(true);
            } else {
                useCatalogFileSelection(false);
            }
        }

        if (evt.getSource() == this.chkbxUseExtension){
            if (this.chkbxUseExtension.isSelected()){
                useExtensionSelection(true);
            } else {
                useExtensionSelection(false);
            }
        }

        if (evt.getSource() == this.btnBrowseFile){
            String filePath = selectFileFromFileSystem(this,
                                                       LAST_BROWSED_SCHEMA_DIR);
            if (filePath != null){
                this.txtFilePath.setText(filePath);
            }
        }

        this.wizPanel.fireChangeEvent();
}//GEN-LAST:event_btnSelectionHandler



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseCatalogFile;
    private javax.swing.JButton btnBrowseFile;
    private javax.swing.JButton btnSelectBindingFile;
    private javax.swing.JCheckBox chkbxNpa;
    private javax.swing.JCheckBox chkbxNv;
    private javax.swing.JCheckBox chkbxQuiet;
    private javax.swing.JCheckBox chkbxReadOnly;
    private javax.swing.JCheckBox chkbxUseBindingFile;
    private javax.swing.JCheckBox chkbxUseCatalogFile;
    private javax.swing.JCheckBox chkbxUseExtension;
    private javax.swing.JCheckBox chkbxVerbose;
    private javax.swing.JComboBox cmbBindingFiles;
    private javax.swing.JComboBox cmbSchemaType;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblOptions;
    private javax.swing.JLabel lblPackageName;
    private javax.swing.JLabel lblPrjName;
    private javax.swing.JLabel lblSchemaFile;
    private javax.swing.JLabel lblSchemaName;
    private javax.swing.JLabel lblSchemaType;
    private javax.swing.JRadioButton rdoSelectFromFileSys;
    private javax.swing.JRadioButton rdoSelectURL;
    private javax.swing.ButtonGroup schemaSource;
    private javax.swing.JTextField txtCatalogFile;
    private javax.swing.JTextField txtExtension;
    private javax.swing.JTextField txtFilePath;
    private javax.swing.JTextField txtPackageName;
    private javax.swing.JTextField txtPrjName;
    private javax.swing.JTextField txtSchemaName;
    private javax.swing.JTextField txtURL;
    // End of variables declaration//GEN-END:variables


    // Custom code
    private static final String LAST_BROWSED_SCHEMA_DIR = "last.browsed.schema.dir" ;
    private static final String LAST_BROWSED_BINDING_DIR = "last.browsed.binding.dir" ;
    private static final String LAST_BROWSED_CATALOG_DIR = "last.browsed.catalog.dir" ;
    private static final String WIZ_ERROR_MSG = "WizardPanel_errorMessage" ;

    private static java.util.Vector<ComboElement<String, String>> schemaTypes = null;
    private static java.util.Map<String, String> LAST_BROWSED_DIRS = new java.util.HashMap<String, String>();
    private JAXBWizBindingCfgPanel wizPanel = null;

    private static synchronized java.util.Vector<ComboElement<String, String>>
            getSchemaTypes(){
        if (schemaTypes == null){
            schemaTypes = new java.util.Vector<ComboElement<String, String>>();
        schemaTypes.add(new JAXBBindingInfoPnl.ComboElement<String, String>(
                "XML Schema", "-xmlschema")); // No I18N
        schemaTypes.add(new JAXBBindingInfoPnl.ComboElement<String, String>(
                "Relax NG", "-relaxng")); // No I18N
        schemaTypes.add(new JAXBBindingInfoPnl.ComboElement<String, String>(
                "Relax NG Compact", "-relaxng-compact")); // No I18N
        schemaTypes.add(new JAXBBindingInfoPnl.ComboElement<String, String>(
                "XML DTD", "-dtd")); // No I18N
        schemaTypes.add(new JAXBBindingInfoPnl.ComboElement<String, String>(
                "WSDL", "-wsdl")); // No I18N
        }
        return schemaTypes;
    }

    private static synchronized String getLastBrowsedDir(String type){
        return LAST_BROWSED_DIRS.get(type);
    }

    private static synchronized void setLastBrowsedDir(String type, String dir){
        LAST_BROWSED_DIRS.put(type, dir);
    }

    private static String selectFileFromFileSystem(JPanel panel, String type){
        File file = null;
        String ret = null;
        String lastBrowsed = getLastBrowsedDir(type);
        JFileChooser jfc = new JFileChooser();
        if (lastBrowsed != null){
            jfc.setCurrentDirectory(new File(lastBrowsed));
        }

        jfc.setMultiSelectionEnabled(false);
        jfc.setFileSelectionMode( jfc.FILES_ONLY );
        int iRt = jfc.showOpenDialog(panel);
        if ( iRt == jfc.APPROVE_OPTION ) {
            file = jfc.getSelectedFile();
        }
        File currDir = jfc.getCurrentDirectory();
        if (currDir != null){
            setLastBrowsedDir(type, currDir.getAbsolutePath());
        }

        if (file != null){
            ret = file.getAbsolutePath();
        }

        return ret;
    }

    private javax.swing.DefaultComboBoxModel getSchemaTypeComboBoxModel(){
        javax.swing.DefaultComboBoxModel ret =
                new javax.swing.DefaultComboBoxModel(getSchemaTypes());
        return ret;
    }

    // Getter and Setter for UI clients - start
    public void setSchemaName(String name){
        this.txtSchemaName.setText(name);
    }

    public void setProjectName(String pn){
        this.txtPrjName.setText(pn);
    }

    public void setLocalSchemaFile(String fileLoc){
        this.txtFilePath.setText(fileLoc);
    }

    public void setSchemaURL(String url){
        this.txtURL.setText(url);
        this.rdoSelectURL.setSelected(true);
    }

    public void setPackageName(String pkgName){
        this.txtPackageName.setText(pkgName);
    }

    public void setOptions(java.util.Map<String, Boolean> options){
        if (Boolean.TRUE.equals(options.get("-nv"))){ // No I18N
            this.chkbxNv.setSelected(true);
        }

        if (Boolean.TRUE.equals(options.get("-readOnly"))){// No I18N
            this.chkbxReadOnly.setSelected(true);
        }

        if (Boolean.TRUE.equals(options.get("-npa"))){// No I18N
            this.chkbxNpa.setSelected(true);
        }

        if (Boolean.TRUE.equals(options.get("-verbose"))){// No I18N
            this.chkbxVerbose.setSelected(true);
        }

        if (Boolean.TRUE.equals(options.get("-quiet"))){// No I18N
            this.chkbxQuiet.setSelected(true);
        }
        
        if (Boolean.TRUE.equals(options.get("-extension"))){// No I18N
            this.chkbxUseExtension.setSelected(true);
        }
    }

    public void setSchemaType(String value){
        java.util.Vector<ComboElement<String, String>> st = getSchemaTypes();
        ComboElement<String, String> ce =
                                new ComboElement<String, String>(value, value);
        int index = st.indexOf(ce);
        this.cmbSchemaType.setSelectedIndex(index);
    }

    public void setSchemaFile(String schemaFile){
        this.txtFilePath.setText(schemaFile);
    }

    public String getSchemaName(){
        return this.txtSchemaName.getText();
    }

    public String getSchemaFile(){
        String ret = null;
        if (this.rdoSelectFromFileSys.isSelected()){
            ret = this.txtFilePath.getText();
        }
        return ret;
    }

    public String getSchemaURL(){
        String ret = null;
        if (this.rdoSelectURL.isSelected()){
            ret = this.txtURL.getText();
        }
        return ret;
    }

    public String getPackageName(){
        return this.txtPackageName.getText();
    }

    public  java.util.Map<String, Boolean> getOptions(){
        java.util.Map<String, Boolean> ret =
                new java.util.HashMap<String, Boolean>();
        if (this.chkbxNv.isSelected()){
            ret.put("-nv", Boolean.TRUE); // No I18N
        }

        if (this.chkbxReadOnly.isSelected()){
            ret.put("-readOnly", Boolean.TRUE); // No I18N
        }

        if (this.chkbxNpa.isSelected()){
            ret.put("-npa", Boolean.TRUE); // No I18N
        }

        if (this.chkbxVerbose.isSelected()){
            ret.put("-verbose", Boolean.TRUE); // No I18N
        }

        if (this.chkbxQuiet.isSelected()){
            ret.put("-quiet", Boolean.TRUE); // No I18N
        }
        
        if (this.chkbxUseExtension.isSelected()){
            ret.put("-extension", Boolean.TRUE); // No I18N
        }

        return ret;
    }

    public String getSchemaType(){
        String ret = null;
        ComboElement<String, String> elem = (ComboElement<String, String>)
                                           this.cmbSchemaType.getSelectedItem();
        if (elem != null){
            ret = elem.getValue();
        }
        return ret;
    }

    // Getter and Setter for UI clients - end
    private boolean isEmpty(String str){
        boolean ret = true;
        if ((str != null) && (!"".equals(str.trim()))){
            ret = false;
        }
        return ret;
    }

    public static class  ComboElement<T1, T2> {
        private T1 display;
        private T2 value;
        int hash = -1 ;

        ComboElement(T1 d, T2 v){
            this.display = d;
            this.value = v;
        }

        public T2 getValue(){
            return this.value;
        }

        public String toString(){
            return this.display.toString();
        }

        @Override
        public int hashCode() {
            // Only consider value
            if (hash == -1){
                hash = this.value.hashCode();
            }
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            // Only consider value
            boolean ret = false;
            if (obj instanceof ComboElement){
                ComboElement other = (ComboElement) obj;
                if (other.getValue().equals(this.value)){
                    //if (other.display.equals(this.display)){
                        ret = true;
                    //}
                }
            }
            return ret;
        }
    }
}
