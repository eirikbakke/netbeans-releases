/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.modules.autoupdate.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JTextPane;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.options.OptionsDisplayer;
import static org.netbeans.modules.autoupdate.ui.Bundle.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 *
 * @author  Jiri Rechtacek
 */
public class ProblemPanel extends javax.swing.JPanel {
    private String problem;
    private JButton [] buttons = null;
    private boolean isWarning = false;
    private OperationException ex;

    public ProblemPanel(OperationException ex, boolean warning, JButton... buttons) {
        this (ex, null, null, warning, buttons);
    }
        
    public ProblemPanel(OperationException ex, UpdateElement culprit, boolean warning, JButton... buttons) {
        this (ex, culprit, null, warning, buttons);
    }
    
    public ProblemPanel (OperationException ex, String problemDescription, boolean warning, JButton... buttons) {
        this (ex, null, problemDescription, true, buttons);
    }
    
    public ProblemPanel (String problemDescription, JButton... buttons) {
        this (null, null, problemDescription, true, buttons);
    }
    
    private ProblemPanel (OperationException ex, UpdateElement culprit, String problemDescription, boolean warning, JButton... buttons) {
        this.ex = ex;
        this.buttons = buttons;
        this.isWarning = warning;
        if (ex == null) {
            initProxyProblem(problemDescription);
        } else {
            switch (ex.getErrorType()) {
                case PROXY:
                    initProxyProblem(problemDescription);
                    break;
                case WRITE_PERMISSION:
                    initWriteProblem(culprit, problemDescription);
                    break;
                case INSTALL:
                    initInstallProblem(ex);
                    break;
                case MODIFIED:
                    initModifiedProblem(ex, problemDescription);
                    break;
                default:
                    assert false : "Unknown type " + ex;
            }
        }
        for (JButton b : buttons) {
            b.getAccessibleContext ().setAccessibleDescription (b.getText ());
        }
    }
    
    @Messages({
        "# {0} - plugin name",
        "modified_taTitle_Text=Module {0} has been modified and cannot be installed.", // Module {0} cannot be installed beacase has been 
        "# {0} - message of exception",
        "modified_taMessage_ErrorText=The installation of download plugins cannot be completed, cause: {0}"})
    private void initModifiedProblem(OperationException ex, String problemDescription) {
        problem = modified_taTitle_Text(problemDescription);
        enhancedInitComponents();
        cbShowAgain.setVisible(false);
        taTitle.setText(problem);
        taTitle.setToolTipText (problem);
        tpMessage.setText(modified_taMessage_ErrorText(ex.getLocalizedMessage())); // NOI18N
    }
    
    @Messages({"proxy_taTitle_Text=Unable to connect to the Update Center",
        "proxy_taMessage_WarningTextWithReload=Check your proxy settings or try again later. The server may be unavailable at the moment. \n\nYou may also want to make sure that your firewall is not blocking network traffic. \n\nYour cache may be out of date. Please click Check for Updates to refresh content.",
        "proxy_taMessage_WarningText=Check your proxy settings or try again later. The server may be unavailable at the moment. \n\nYou may also want to make sure that your firewall is not blocking network traffic.",
        "proxy_taMessage_ErrorText=Not all of the plugins have been successfully downloaded. The server may be unavailable at the moment. Try again later."})
    private void initProxyProblem(String problemDescription) {
        if (ex != null) {
            problemDescription = ex.getLocalizedMessage();
        }
        problem = problemDescription == null ?
            proxy_taTitle_Text() : // NOI18N
            problemDescription;
        enhancedInitComponents();
        cbShowAgain.setVisible(false);
        taTitle.setText(problem);
        taTitle.setToolTipText (problem);
        if (isWarning) {
            if (buttons.length == 2) { // XXX: called from InstallStep
                tpMessage.setText (proxy_taMessage_WarningTextWithReload()); // NOI18N
            } else {
                tpMessage.setText(proxy_taMessage_WarningText()); // NOI18N
            }
        } else {
            tpMessage.setText(proxy_taMessage_ErrorText()); // NOI18N
        }
    }
    
    @Messages({
        "# {0} - plugin_name",
        "write_taTitle_Text=You don''t have permission to install plugin <b>{0}</b> into the installation directory.",
        "write_taMessage_WarningText=To perform installation into the installation directory, you should run the application "
            + "as a user with administrative privilege, i.e. <i>Run as administrator</i> on Windows platform or "
            + "run as <i>sudo</i> command on Unix-like systems."})
    private void initWriteProblem(UpdateElement culprit, String problemDescription) {
        problem = problemDescription == null ?
            write_taTitle_Text(culprit.getDisplayName()) : // NOI18N
            problemDescription;
        enhancedInitComponents();
        cbShowAgain.setVisible(false);
        taTitle.setText(problem);
        taTitle.setToolTipText (problem);
        tpMessage.setText(write_taMessage_WarningText()); // NOI18N
    }
    
    @Messages({
        "install_taTitle_Text=Cannot complete the validation of download plugins",
        "# {0} - message of exception",
        "install_taMessage_ErrorText=The validation of download plugins cannot be completed, cause: {0}"})
    private void initInstallProblem(OperationException ex) {
        problem = install_taTitle_Text();
        enhancedInitComponents();
        cbShowAgain.setVisible(false);
        taTitle.setText(problem);
        taTitle.setToolTipText (problem);
        tpMessage.setText(install_taMessage_ErrorText(ex.getLocalizedMessage())); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbShowAgain = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpMessage = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        taTitle = new javax.swing.JTextPane();

        org.openide.awt.Mnemonics.setLocalizedText(cbShowAgain, org.openide.util.NbBundle.getMessage(ProblemPanel.class, "ProblemPanel.cbShowAgain.text")); // NOI18N
        cbShowAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbShowAgainActionPerformed(evt);
            }
        });

        tpMessage.setEditable(false);
        tpMessage.setContentType("text/html"); // NOI18N
        tpMessage.setOpaque(false);
        tpMessage.setPreferredSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setViewportView(tpMessage);

        taTitle.setEditable(false);
        taTitle.setContentType("text/html"); // NOI18N
        taTitle.setOpaque(false);
        taTitle.setPreferredSize(new java.awt.Dimension(200, 100));
        jScrollPane2.setViewportView(taTitle);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbShowAgain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbShowAgain))
        );

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProblemPanel.class, "NetworkProblemPanel_ACD")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void cbShowAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbShowAgainActionPerformed
        // TODO add your handling code here:
        getPreferences().putBoolean(Utilities.PLUGIN_MANAGER_DONT_CARE_WRITE_PERMISSION, cbShowAgain.isSelected());
    }//GEN-LAST:event_cbShowAgainActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox cbShowAgain;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JTextPane taTitle;
    javax.swing.JTextPane tpMessage;
    // End of variables declaration//GEN-END:variables
    
    public Object showNetworkProblemDialog () {
        DialogDescriptor dd = getNetworkProblemDescriptor ();
        DialogDisplayer.getDefault ().createDialog (dd).setVisible (true);
        return dd.getValue ();
    }
    
    public Object showWriteProblemDialog () {
        DialogDescriptor dd = getWriteProblemDescriptor();
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        return dd.getValue();
    }
    
    public Object showModifiedProblemDialog(String detail) {
        DialogDescriptor dd = getModifiedProblemDescriptor();
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        return dd.getValue();
    }
    
    private DialogDescriptor getNetworkProblemDescriptor() {
        DialogDescriptor descriptor = getProblemDesriptor(NbBundle.getMessage(ProblemPanel.class, "CTL_ShowProxyOptions"));

        JButton showProxyOptions = new JButton ();
        Mnemonics.setLocalizedText (showProxyOptions, NbBundle.getMessage(ProblemPanel.class, "CTL_ShowProxyOptions"));
        
        showProxyOptions.getAccessibleContext ().setAccessibleDescription (NbBundle.getMessage(ProblemPanel.class, "ACSD_ShowProxyOptions"));
        showProxyOptions.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed (ActionEvent arg0) {
                OptionsDisplayer.getDefault ().open ("General"); // NOI18N
            }
        });
                
        if (isWarning) {
            descriptor.setAdditionalOptions(new Object [] {showProxyOptions});
        }
        
        return descriptor;
    }
    
    @Messages("CTL_WriteError=Write Permissions Problem")
    private DialogDescriptor getWriteProblemDescriptor() {
        return getProblemDesriptor(CTL_WriteError());
    }
    
    @Messages("CTL_ModifiedError=Module Archive Modified")
    private DialogDescriptor getModifiedProblemDescriptor() {
        return getProblemDesriptor(CTL_ModifiedError());
    }
    
    private DialogDescriptor getProblemDesriptor(String message) {
        Object [] options;
        if (buttons == null || buttons.length == 0) {
            options = new Object [] { DialogDescriptor.OK_OPTION };
        } else {
            options = buttons;
        }
        DialogDescriptor descriptor = new DialogDescriptor(
             this,
             isWarning ? NbBundle.getMessage(ProblemPanel.class, "CTL_Warning") : message,
             true,                              // Modal
             options,                           // Option list
             null,                              // Default
             DialogDescriptor.DEFAULT_ALIGN,    // Align
             null,                              // Help
             null
        );

        descriptor.setMessageType (isWarning ? NotifyDescriptor.WARNING_MESSAGE : NotifyDescriptor.ERROR_MESSAGE);
        descriptor.setClosingOptions(null);
        return descriptor;
    }
    
    private static Preferences getPreferences() {
        return NbPreferences.forModule(Utilities.class);
    }
    
    private void enhancedInitComponents() {
        initComponents();
        taTitle.setBackground(new Color(0, 0, 0, 0));
        taTitle.setOpaque(false);
        taTitle.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        tpMessage.setBackground(new Color(0, 0, 0, 0));
        tpMessage.setOpaque(false);
        tpMessage.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
}
}
