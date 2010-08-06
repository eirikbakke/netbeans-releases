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

package org.netbeans.modules.j2ee.sun.ide.j2ee.ui;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.openide.util.NbBundle;

import org.netbeans.modules.j2ee.sun.ide.j2ee.DeploymentManagerProperties;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/** A single panel for a wizard - the GUI portion.
 *
 * @author nityad
 */
public class ConnectionTabVisualPanel extends javax.swing.JPanel {
    
    /** The wizard panel descriptor associated with this GUI panel.
     * If you need to fire state changes or something similar, you can
     * use this handle to do so.
     */
    
    transient private final DeploymentManagerProperties targetData;
    transient private final SunDeploymentManagerInterface dm;
    
    /** Create the wizard panel and set up some basic properties. 
     * @param dm 
     */
    public ConnectionTabVisualPanel( DeploymentManager dm) {
        
        this.dm = (SunDeploymentManagerInterface)dm;
        targetData = new DeploymentManagerProperties(dm);

        initComponents();
        InstanceProperties ips = targetData.getInstanceProperties();
        String url = (String) ips.getProperty("url"); // NOI18N
        int dex = url.indexOf("::");
        if (dex > -1){
            url = url.substring(dex+2);
        }
        socketField.setText(url);
        userNameField.setText(targetData.getUserName());
        passwordField.setText(targetData.getPassword());
        domainField.setText(targetData.getDomainName());
        String loc = targetData.getLocation();
        domainLocField.setText(loc);
        enableHttpMonitor.setSelected(Boolean.valueOf(targetData.getHttpMonitorOn()).booleanValue());
        syncHttpProxies.setSelected(targetData.isSyncHttpProxyOn());
        if (null == loc || loc.trim().length() < 1) {
            enableHttpMonitor.setEnabled(false);
            syncHttpProxies.setEnabled(false);
            // the sync only happens on startup... we don't start a remote instance,
            // so this should be disabled for now.
            syncHttpProxies.setSelected(false);
        }
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ConnectionTabVisualPanel.class, "TITLE_AddUserDefinedLocalServerPanel"));
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        socketField = new javax.swing.JTextField();
        userNameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        domainField = new javax.swing.JTextField();
        domainLocField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        userPasswordLabel = new javax.swing.JLabel();
        domainLabel = new javax.swing.JLabel();
        domainLocLabel = new javax.swing.JLabel();
        msgLabel = new javax.swing.JLabel();
        enableHttpMonitor = new javax.swing.JCheckBox();
        syncHttpProxies = new javax.swing.JCheckBox();

        socketField.setColumns(30);
        socketField.setEditable(false);

        userNameField.setColumns(30);
        userNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userNameFieldKeyReleased(evt);
            }
        });

        passwordField.setColumns(30);
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordFieldKeyReleased(evt);
            }
        });

        domainField.setColumns(30);
        domainField.setEditable(false);

        domainLocField.setColumns(30);
        domainLocField.setEditable(false);

        portLabel.setLabelFor(socketField);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(portLabel, bundle.getString("LBL_AdminSocket")); // NOI18N

        userNameLabel.setLabelFor(userNameField);
        org.openide.awt.Mnemonics.setLocalizedText(userNameLabel, bundle.getString("LBL_Username")); // NOI18N

        userPasswordLabel.setLabelFor(passwordField);
        org.openide.awt.Mnemonics.setLocalizedText(userPasswordLabel, bundle.getString("LBL_Pw")); // NOI18N

        domainLabel.setLabelFor(domainField);
        org.openide.awt.Mnemonics.setLocalizedText(domainLabel, bundle.getString("LBL_Domain")); // NOI18N

        domainLocLabel.setLabelFor(domainLocField);
        org.openide.awt.Mnemonics.setLocalizedText(domainLocLabel, bundle.getString("LBL_DomainRoot")); // NOI18N

        msgLabel.setForeground(new java.awt.Color(89, 79, 191));

        org.openide.awt.Mnemonics.setLocalizedText(enableHttpMonitor, org.openide.util.NbBundle.getBundle(ConnectionTabVisualPanel.class).getString("LBL_EnableHttpMonitor")); // NOI18N
        enableHttpMonitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableHttpMonitorActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(syncHttpProxies, org.openide.util.NbBundle.getBundle(ConnectionTabVisualPanel.class).getString("LBL_SyncHttpProxy")); // NOI18N
        syncHttpProxies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syncHttpProxiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msgLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .addComponent(enableHttpMonitor)
                    .addComponent(syncHttpProxies)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(userNameLabel)
                            .addComponent(portLabel)
                            .addComponent(userPasswordLabel)
                            .addComponent(domainLocLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(domainLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(domainField, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(domainLocField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(socketField, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                            .addComponent(userNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(socketField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userPasswordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(domainLocLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(domainLocField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(domainField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(domainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableHttpMonitor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(syncHttpProxies)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(msgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {passwordField, userNameField});

        socketField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_AdminPort")); // NOI18N
        socketField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_AdminPort")); // NOI18N
        userNameField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Username")); // NOI18N
        userNameField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Username")); // NOI18N
        passwordField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Pw")); // NOI18N
        passwordField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Pw")); // NOI18N
        domainField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Domain")); // NOI18N
        domainField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Domain")); // NOI18N
        domainLocField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_InstallRoot")); // NOI18N
        domainLocField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_InstallRoot")); // NOI18N
        portLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_AdminSocket")); // NOI18N
        portLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ASCD_AdminSocket")); // NOI18N
        userNameLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Username")); // NOI18N
        userNameLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Username")); // NOI18N
        userPasswordLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Pw")); // NOI18N
        userPasswordLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Pw")); // NOI18N
        domainLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_Domain")); // NOI18N
        domainLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_Domain")); // NOI18N
        domainLocLabel.getAccessibleContext().setAccessibleName(bundle.getString("LBL_InstallRoot")); // NOI18N
        domainLocLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_InstallRoot")); // NOI18N
        enableHttpMonitor.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_EnableHttpMonitor")); // NOI18N
        syncHttpProxies.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConnectionTabVisualPanel.class, "ACSD_SyncHttpProxy")); // NOI18N

        getAccessibleContext().setAccessibleName(bundle.getString("Step_ChooseUserDefinedLocalServer")); // NOI18N
        getAccessibleContext().setAccessibleDescription(bundle.getString("AddUserDefinedLocalServerPanel_Desc")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    private void syncHttpProxiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syncHttpProxiesActionPerformed
        targetData.setSyncHttpProxyOn(syncHttpProxies.isSelected());
        if(syncHttpProxies.isSelected()) {
            msgLabel.setText(NbBundle.getMessage(ConnectionTabVisualPanel.class, "Msg_httpProxyStatusChangedAtRestart"));
        }
    }//GEN-LAST:event_syncHttpProxiesActionPerformed
    
    private void enableHttpMonitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableHttpMonitorActionPerformed
        boolean oldValue = !enableHttpMonitor.isSelected();
        if (enableHttpMonitor.isSelected()) {
            // open a message about the scary effects of HTTP monitoring
            NotifyDescriptor dd = new NotifyDescriptor(NbBundle.getMessage(this.getClass(), "TXT_WARNING_HTTP_MONITOR_ON"), // NOI18N
                    NbBundle.getMessage(this.getClass(), "TITLE_WARNING_HTTP_MONITOR_ON"), // NOI18N
                    NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.WARNING_MESSAGE, null, null);
            if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.CANCEL_OPTION)) {
                enableHttpMonitor.setSelected(false);
            }
        } else {
            // open a message about the scary effects of HTTP monitoring
            NotifyDescriptor dd = new NotifyDescriptor(NbBundle.getMessage(this.getClass(), "TXT_WARNING_HTTP_MONITOR_OFF"), // NOI18N
                    NbBundle.getMessage(this.getClass(), "TITLE_WARNING_HTTP_MONITOR_OFF"), // NOI18N
                    NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.WARNING_MESSAGE, null, null);
            if (DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.CANCEL_OPTION)) {
                enableHttpMonitor.setSelected(true);
            }
        }
        if (enableHttpMonitor.isSelected() != oldValue) {
            targetData.setHttpMonitorOn("" + enableHttpMonitor.isSelected());
            msgLabel.setText(NbBundle.getMessage(ConnectionTabVisualPanel.class, "Msg_httpMonitorStatusChangedAtRestart"));
        }
    }//GEN-LAST:event_enableHttpMonitorActionPerformed
    
    private void passwordFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldKeyReleased
//        char[] passWd = passwordField.getPassword();
//        String adminPassword = new String(passWd);
//        targetData.setPassword(adminPassword);
//        dm.setPassword(adminPassword);
        
    }//GEN-LAST:event_passwordFieldKeyReleased
    
    private void userNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userNameFieldKeyReleased
//        String userName = userNameField.getText();
//        targetData.setUserName(userName);
//        dm.setUserName(userName);
        
    }//GEN-LAST:event_userNameFieldKeyReleased
    /** store username and passord in the model
     * called when the tab is gone
     **/
    public void syncUpWithModel(){
        String userName = userNameField.getText();
        targetData.setUserName(userName);
        dm.setUserName(userName);        
        char[] passWd = passwordField.getPassword();
        String adminPassword = new String(passWd);
        targetData.setPassword(adminPassword);
        dm.setPassword(adminPassword);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField domainField;
    private javax.swing.JLabel domainLabel;
    private javax.swing.JTextField domainLocField;
    private javax.swing.JLabel domainLocLabel;
    private javax.swing.JCheckBox enableHttpMonitor;
    private javax.swing.JLabel msgLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField socketField;
    private javax.swing.JCheckBox syncHttpProxies;
    private javax.swing.JTextField userNameField;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JLabel userPasswordLabel;
    // End of variables declaration//GEN-END:variables
    
}
