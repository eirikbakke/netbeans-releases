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

/*
 * ReminderPanel.java
 *
 * Created on December 11, 2007, 4:01 PM
 */

package org.netbeans.modules.collab.ui;

import java.awt.Cursor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author  Marek Slama
 */
public class ServerWarningPanel extends javax.swing.JPanel {

    private static final Preferences prefs = NbPreferences.forModule(ServerWarningPanel.class);

    /** Creates new form ReminderPanel */
    public ServerWarningPanel() {
        initComponents();
        lblWarningMsgLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setText();
    }
    
    private void setText () {
        lblWarningMsgTop.setText(NbBundle.getMessage(ServerWarningPanel.class, "LBL_ServerWarningMsgTop"));
        lblWarningMsgBottom.setText(NbBundle.getMessage(ServerWarningPanel.class, "LBL_ServerWarningMsgBottom"));
        lblWarningMsgLink.setText(NbBundle.getMessage(ServerWarningPanel.class, "LBL_ServerWarningMsgLink"));

        lblWarningMsgLink.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(ServerWarningAction.class,"ACSN_ServerWarningMsgLink"));
        lblWarningMsgLink.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(ServerWarningAction.class,"ACSD_ServerWarningMsgLink"));

        chbWarning.setText(NbBundle.getMessage(ServerWarningPanel.class, "LBL_ServerWarningCheckBox"));

        chbWarning.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(ServerWarningAction.class,"ACSN_ServerWarningCheckBox"));
        chbWarning.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(ServerWarningAction.class,"ACSD_ServerWarningCheckBox"));
        chbWarning.setSelected(prefs.getBoolean("server.warning.show", Boolean.TRUE)); // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblWarningMsgTop = new javax.swing.JLabel();
        lblWarningMsgBottom = new javax.swing.JLabel();
        lblWarningMsgLink = new javax.swing.JLabel();
        chbWarning = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        lblWarningMsgTop.setText("Service for the share.java.net server will be discontinued as of April 20, 2009."); // NOI18N
        lblWarningMsgTop.setFocusable(false);
        lblWarningMsgTop.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblWarningMsgTop, gridBagConstraints);

        lblWarningMsgBottom.setText("For other options, please visit the"); // NOI18N
        lblWarningMsgBottom.setFocusable(false);
        lblWarningMsgBottom.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        add(lblWarningMsgBottom, gridBagConstraints);

        lblWarningMsgLink.setText("<html>&nbsp;<a href\\=\"info\">NetBeans User FAQ</a>.</html>"); // NOI18N
        lblWarningMsgLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblWarningMsgLinkMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        add(lblWarningMsgLink, gridBagConstraints);

        chbWarning.setText("Show this warning dialog next time"); // NOI18N
        chbWarning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chbWarning.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chbWarning, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void checkBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            prefs.putBoolean("server.warning.show", true); // NOI18N
        } else if (evt.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
            prefs.putBoolean("server.warning.show", false); // NOI18N
        }
    }//GEN-LAST:event_checkBoxItemStateChanged

    private void lblWarningMsgLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWarningMsgLinkMouseClicked
        URL u = null;
        try {
            u = new URL(NbBundle.getMessage(ServerWarningPanel.class, "NB_FAQ_URL")); // NOI18N
        } catch (MalformedURLException exc) {
        }
        if (u != null) {
            org.openide.awt.HtmlBrowser.URLDisplayer.getDefault().showURL(u);
        }
    }//GEN-LAST:event_lblWarningMsgLinkMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chbWarning;
    private javax.swing.JLabel lblWarningMsgBottom;
    private javax.swing.JLabel lblWarningMsgLink;
    private javax.swing.JLabel lblWarningMsgTop;
    // End of variables declaration//GEN-END:variables
    
}
