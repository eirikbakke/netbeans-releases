/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.uml.codegen.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.DefaultComboBoxModel;
import org.netbeans.modules.uml.codegen.dataaccess.DomainTemplatesRetriever;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;

/**
 *
 * @author  IBM USER
 */
public class TemplatesTableRowPanel extends javax.swing.JPanel 
    implements ActionListener
{
    public TemplatesTableRowPanel()
    {
        this("", "", "", "<None Selected>");
    }
    
    public TemplatesTableRowPanel(
        String filenameFormat, 
        String extension,
        String folder,
        String templateFilename)
    {
        initComponents();
        populateTemplateFilesComboBox();
        
        filenameFormatText.setText(filenameFormat);
        extensionText.setText(extension);
        folderText.setText(folder);
        templateFileCombo.setSelectedItem(templateFilename);
    }
    
    
    private void populateTemplateFilesComboBox()
    {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem ();
	FileObject root = fs.getRoot().getFileObject(
            DomainTemplatesRetriever.TEMPLATES_BASE_FOLDER); // NOI18N
        
        // FileObject[] templateFiles = root.getChildren();
        
        DefaultComboBoxModel selectionModel = 
            ((DefaultComboBoxModel)templateFileCombo.getModel());
        
        selectionModel.addElement(NbBundle.getMessage(
            TemplatesTableRowPanel.class, "VAL_ElementType_NodeSelected")); // NOI18N
        
        Enumeration templateFiles = root.getChildren(true);
        
        while (templateFiles.hasMoreElements())
        {
            FileObject template = (FileObject)templateFiles.nextElement();

            if (!template.isFolder())
            {
                selectionModel.addElement(template.getPath().substring(
                    DomainTemplatesRetriever.TEMPLATES_BASE_FOLDER.length()+1));
            }
        }
    }
    
    public void actionPerformed(ActionEvent event)
    {
        
    }

    @Override
    public void requestFocus()
    {
        filenameFormatText.requestFocus();
    }
    

    public String getFilenameFormat()
    {
        return filenameFormatText.getText();
    }
    
    public String getExtension()
    {
        return extensionText.getText();
    }
    
    public String getFolder()
    {
        return folderText.getText();
    }
    
    public String getTemplateFilename()
    {
        return templateFileCombo.getSelectedItem().toString();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        filenameFormatLabel = new javax.swing.JLabel();
        filenameFormatText = new javax.swing.JTextField();
        extensionLabel = new javax.swing.JLabel();
        extensionText = new javax.swing.JTextField();
        folderLabel = new javax.swing.JLabel();
        folderText = new javax.swing.JTextField();
        templateFileLabel = new javax.swing.JLabel();
        templateFileCombo = new javax.swing.JComboBox();

        filenameFormatLabel.setLabelFor(filenameFormatText);
        org.openide.awt.Mnemonics.setLocalizedText(filenameFormatLabel, org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "filenameFormatLabel.text")); // NOI18N

        extensionLabel.setLabelFor(extensionText);
        org.openide.awt.Mnemonics.setLocalizedText(extensionLabel, org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "extensionLabel.text")); // NOI18N

        folderLabel.setLabelFor(folderText);
        org.openide.awt.Mnemonics.setLocalizedText(folderLabel, org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "folderLabel.text")); // NOI18N

        templateFileLabel.setLabelFor(templateFileCombo);
        org.openide.awt.Mnemonics.setLocalizedText(templateFileLabel, org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "templateFileLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(filenameFormatLabel)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(folderLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(extensionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(templateFileLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, extensionText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, folderText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, filenameFormatText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .add(templateFileCombo, 0, 219, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(filenameFormatLabel)
                    .add(filenameFormatText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extensionText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(extensionLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(folderLabel))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(templateFileCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(templateFileLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        filenameFormatText.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSN_FilenameFormat")); // NOI18N
        filenameFormatText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSD_FilenameFormat")); // NOI18N
        extensionText.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSN_Extension")); // NOI18N
        extensionText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSD_Extension")); // NOI18N
        folderText.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSN_Folder")); // NOI18N
        folderText.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSD_Folder")); // NOI18N
        templateFileCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSN_TemplateFile")); // NOI18N
        templateFileCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSD_TemplateFile")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSN_TemplatesTableRowPanel")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(TemplatesTableRowPanel.class, "ACSD_TemplatesTableRowPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel extensionLabel;
    private javax.swing.JTextField extensionText;
    private javax.swing.JLabel filenameFormatLabel;
    private javax.swing.JTextField filenameFormatText;
    private javax.swing.JLabel folderLabel;
    private javax.swing.JTextField folderText;
    private javax.swing.JComboBox templateFileCombo;
    private javax.swing.JLabel templateFileLabel;
    // End of variables declaration//GEN-END:variables
    
}
