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
import org.netbeans.modules.cnd.remote.support.RemoteUtil;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.PasswordManager;
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
//        cbRememberPwd.setSelected(PasswordManager.getInstance().isRememberPassword(env));
        String envString = RemoteUtil.getDisplayName(env);
        lblFiles.setText(NbBundle.getMessage(getClass(), "NotifierPanel.lblFiles.parameterized.text", envString));
    }

//    public char[] getPassword() {
//        return tfPassword.getPassword();
//    }
//
//    public boolean isRememberPassword() {
//        return cbRememberPwd.isSelected();
//    }

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
        tfHost = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel0 = new javax.swing.JLabel();
        tfUser = new javax.swing.JTextField();

        lblFiles.setText(org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.lblFiles.text")); // NOI18N

        txtFilesList.setColumns(20);
        txtFilesList.setEditable(false);
        txtFilesList.setRows(5);
        jScrollPane2.setViewportView(txtFilesList);

        tfHost.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background") /*NOI18N*/);
        tfHost.setEditable(false);
        tfHost.setText(null);
        tfHost.setFocusable(false);

        jLabel1.setLabelFor(tfHost);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.jLabel1.text")); // NOI18N

        jLabel0.setLabelFor(tfUser);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel0, org.openide.util.NbBundle.getMessage(NotifierPanel.class, "NotifierPanel.jLabel0.text")); // NOI18N

        tfUser.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background") /*NOI18N*/);
        tfUser.setEditable(false);
        tfUser.setText(null);
        tfUser.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel0, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfHost, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                            .addComponent(tfUser, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)))
                    .addComponent(lblFiles, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel0)
                    .addComponent(tfUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel0;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblFiles;
    private javax.swing.JTextField tfHost;
    private javax.swing.JTextField tfUser;
    private javax.swing.JTextArea txtFilesList;
    // End of variables declaration//GEN-END:variables

}
