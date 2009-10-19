/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.remote.fs.ui;

import java.util.List;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.util.NbBundle;

/**
 *
 * @author Vladimir Kvashin
 */
public class NotifierPanel extends javax.swing.JPanel {

    /** Creates new form RemoteFileSystemNotifier */
    public NotifierPanel(ExecutionEnvironment env) {
        initComponents();
        txtFilesList.setBackground(getBackground());
        tfHost.setText(env.getHost());
        tfUser.setText(env.getUser());
        String envString = RemoteFileSystemNotifier.getDisplayName(env);
        lblFiles.setText(NbBundle.getMessage(getClass(), "NotifierPanel.lblFiles.parameterized.text", envString));
    }

    public char[] getPassword() {
        return tfPassword.getPassword();
    }

    public boolean isRememberPassword() {
        return cbRememberPwd.isSelected();
    }

    public void setPendingFiles(List<String> pendingFiles) {
        StringBuilder sb = new StringBuilder();
        for (String file : pendingFiles) {
            if (sb.length() > 0) {
                sb.append('\n'); //NOI18N
            }
            sb.append(file);
        }
        txtFilesList.setText(sb.toString());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFiles = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtFilesList = new javax.swing.JTextArea();
        tfPassword = new javax.swing.JPasswordField();
        tfHost = new javax.swing.JTextField();
        cbRememberPwd = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel0 = new javax.swing.JLabel();
        tfUser = new javax.swing.JTextField();

        lblFiles.setText(org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.lblFiles.text")); // NOI18N

        txtFilesList.setColumns(20);
        txtFilesList.setEditable(false);
        txtFilesList.setRows(5);
        jScrollPane2.setViewportView(txtFilesList);

        tfPassword.setText(null);
        tfPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tfPasswordonPwdFocus(evt);
            }
        });

        tfHost.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background") /*NOI18N*/);
        tfHost.setEditable(false);
        tfHost.setText(null);
        tfHost.setFocusable(false);

        org.openide.awt.Mnemonics.setLocalizedText(cbRememberPwd, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.cbRememberPwd.text")); // NOI18N

        jLabel1.setLabelFor(tfHost);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.jLabel1.text")); // NOI18N

        jLabel2.setLabelFor(tfPassword);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.jLabel2.text")); // NOI18N

        jLabel0.setLabelFor(tfUser);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel0, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.jLabel0.text")); // NOI18N

        tfUser.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background") /*NOI18N*/);
        tfUser.setEditable(false);
        tfUser.setText(null);
        tfUser.setFocusable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel1)
                            .add(jLabel0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tfHost, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                            .add(tfUser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                            .add(tfPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cbRememberPwd)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblFiles))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(lblFiles)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel0)
                    .add(tfUser))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(tfHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(tfPassword))
                .add(8, 8, 8)
                .add(cbRememberPwd)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tfPasswordonPwdFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfPasswordonPwdFocus
        tfPassword.selectAll();
}//GEN-LAST:event_tfPasswordonPwdFocus


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbRememberPwd;
    private javax.swing.JLabel jLabel0;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblFiles;
    private javax.swing.JTextField tfHost;
    private javax.swing.JPasswordField tfPassword;
    private javax.swing.JTextField tfUser;
    private javax.swing.JTextArea txtFilesList;
    // End of variables declaration//GEN-END:variables

}
