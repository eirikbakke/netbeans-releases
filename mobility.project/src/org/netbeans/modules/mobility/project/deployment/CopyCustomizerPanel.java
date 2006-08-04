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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * CopyCustomizerPanel.java
 *
 * Created on 9. prosinec 2004, 17:27
 */
package org.netbeans.modules.mobility.project.deployment;

import org.netbeans.modules.mobility.project.ui.wizard.Utils;
import org.openide.util.NbBundle;

/**
 *
 * @author  Adam
 */
public class CopyCustomizerPanel extends javax.swing.JPanel {
    
    /** Creates new form CopyCustomizerPanel */
    public CopyCustomizerPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelTarget = new javax.swing.JLabel();
        jTextFieldTarget = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabelTarget.setDisplayedMnemonic(NbBundle.getMessage(CopyCustomizerPanel.class, "MNM_Copy_Target").charAt(0));
        jLabelTarget.setLabelFor(jTextFieldTarget);
        jLabelTarget.setText(NbBundle.getMessage(CopyCustomizerPanel.class, "LBL_Copy_Target"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 0);
        add(jLabelTarget, gridBagConstraints);

        jTextFieldTarget.setName(CopyDeploymentPlugin.PROP_TARGET);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 12, 0);
        add(jTextFieldTarget, gridBagConstraints);
        jTextFieldTarget.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CopyCustomizerPanel.class, "ACSD_Copy_Target"));

        jButtonBrowse.setMnemonic(NbBundle.getMessage(CopyCustomizerPanel.class, "MNM_Copy_Browse").charAt(0));
        jButtonBrowse.setText(NbBundle.getMessage(CopyCustomizerPanel.class, "LBL_Copy_Browse"));
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 12, 12);
        add(jButtonBrowse, gridBagConstraints);
        jButtonBrowse.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CopyCustomizerPanel.class, "ACSD_Copy_Browse"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        final String result = Utils.browseFolder(this, jTextFieldTarget.getText(), NbBundle.getMessage(CopyCustomizerPanel.class, "Title_Copy_SelectFolder"));//NOI18N
        if (result != null) jTextFieldTarget.setText(result);
    }//GEN-LAST:event_jButtonBrowseActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton jButtonBrowse;
    javax.swing.JLabel jLabelTarget;
    private javax.swing.JPanel jPanel1;
    javax.swing.JTextField jTextFieldTarget;
    // End of variables declaration//GEN-END:variables
    
}
