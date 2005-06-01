/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.versioning.system.cvss.ui.actions.tag;

import org.netbeans.lib.cvsclient.command.tag.TagCommand;

/**
 * Settings panel for the Tag command.
 *
 * @author Maros Sandor
 */
public class TagSettings extends javax.swing.JPanel {
    
    public TagSettings() {
        initComponents();
    }
    
    public void setCommand(TagCommand cmd) {
        cbMoveTag.setSelected(cmd.isOverrideExistingTag());
        cbCheckModified.setSelected(cmd.isCheckThatUnmodified());
        tfName.setText(cmd.getTag());
    }

    public void updateCommand(TagCommand cmd) {
        cmd.setOverrideExistingTag(cbMoveTag.isSelected());
        cmd.setCheckThatUnmodified(cbCheckModified.isSelected());
        cmd.setTag(tfName.getText());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        cbCheckModified = new javax.swing.JCheckBox();
        cbMoveTag = new javax.swing.JCheckBox();
        nameLabel = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        cbCheckModified.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("MNE_TagForm_EnsureUptodate").charAt(0));
        org.openide.awt.Mnemonics.setLocalizedText(cbCheckModified, java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("CTL_TagForm_EnsureUptodate"));
        cbCheckModified.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCheckModifiedActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(cbCheckModified, gridBagConstraints);

        cbMoveTag.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("MNE_TagForm_MoveExisting").charAt(0));
        org.openide.awt.Mnemonics.setLocalizedText(cbMoveTag, java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("CTL_TagForm_MoveExisting"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(cbMoveTag, gridBagConstraints);

        nameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("MNE_TagForm_TagName").charAt(0));
        nameLabel.setLabelFor(tfName);
        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, java.util.ResourceBundle.getBundle("org/netbeans/modules/versioning/system/cvss/ui/actions/tag/Bundle").getString("CTL_TagForm_TagName"));
        nameLabel.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(nameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(tfName, gridBagConstraints);

    }//GEN-END:initComponents

    private void cbCheckModifiedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCheckModifiedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbCheckModifiedActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbCheckModified;
    private javax.swing.JCheckBox cbMoveTag;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables
}
