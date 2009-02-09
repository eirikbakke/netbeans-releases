/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.hudson.ui;

import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.ui.HudsonJobBuildPanel;
import org.openide.util.NbBundle;

/**
 * Hudson Job's Permalinks
 * 
 * @author  Michal Mocnak
 */
public class HudsonJobPermalinksPanel extends javax.swing.JPanel {
    
    private HudsonJob job;
    private HudsonJobBuildPanel.ActionProvider actionProvider;
    
    /** Creates new form HudsonJobPermalinksPanel */
    public HudsonJobPermalinksPanel(HudsonJobBuildPanel.ActionProvider actionProvider) {
        this.actionProvider = actionProvider;
        
        initComponents();
    }
    
    public void refreshContent(HudsonJob job) {
        // Set job
        this.job = job;
        
        // Set data
        lastBuildLabel.setText(NbBundle.getMessage(HudsonJobPermalinksPanel.class,
                "LBL_LastBuild", job.getLastBuild()));
        lastFailedBuildLabel.setText(NbBundle.getMessage(HudsonJobPermalinksPanel.class,
                "LBL_LastFailedBuild", job.getLastFailedBuild()));
        lastSuccessfulBuildLabel.setText(NbBundle.getMessage(HudsonJobPermalinksPanel.class,
                "LBL_LastSuccessfulBuild", job.getLastSuccessfulBuild()));
        lastStableBuildLabel.setText(NbBundle.getMessage(HudsonJobPermalinksPanel.class,
                "LBL_LastStableBuild", job.getLastStableBuild()));
        
        // Set labels
        lastBuildLabel.setEnabled(job.getLastBuild() >= 0);
        lastFailedBuildLabel.setEnabled(job.getLastFailedBuild() >= 0);
        lastSuccessfulBuildLabel.setEnabled(job.getLastSuccessfulBuild() >= 0);
        lastStableBuildLabel.setEnabled(job.getLastStableBuild() >= 0);
        
        // Set buttons
        lastBuildButton.setEnabled(job.getLastBuild() >= 0);
        lastFailedBuildButton.setEnabled(job.getLastFailedBuild() >= 0);
        lastSuccessfulBuildButton.setEnabled(job.getLastSuccessfulBuild() >= 0);
        lastStableBuildButton.setEnabled(job.getLastStableBuild() >= 0);
        
        // Show last build
        actionProvider.showBuild(job, job.getLastBuild());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lastBuildLabel = new javax.swing.JLabel();
        lastFailedBuildLabel = new javax.swing.JLabel();
        lastSuccessfulBuildLabel = new javax.swing.JLabel();
        lastStableBuildLabel = new javax.swing.JLabel();
        lastBuildButton = new HudsonLinkButton(false);
        lastFailedBuildButton = new HudsonLinkButton(false);
        lastSuccessfulBuildButton = new HudsonLinkButton(false);
        lastStableBuildButton = new HudsonLinkButton(false);

        setOpaque(false);

        jPanel1.setOpaque(false);

        lastBuildLabel.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_LastBuild")); // NOI18N

        lastFailedBuildLabel.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_LastFailedBuild")); // NOI18N

        lastSuccessfulBuildLabel.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_LastSuccessfulBuild")); // NOI18N

        lastStableBuildLabel.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_LastStableBuild")); // NOI18N

        lastBuildButton.setFont(new java.awt.Font("Dialog", 1, 10));
        lastBuildButton.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_Show")); // NOI18N
        lastBuildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastBuildButtonActionPerformed(evt);
            }
        });

        lastFailedBuildButton.setFont(new java.awt.Font("Dialog", 1, 10));
        lastFailedBuildButton.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_Show")); // NOI18N
        lastFailedBuildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastFailedBuildButtonActionPerformed(evt);
            }
        });

        lastSuccessfulBuildButton.setFont(new java.awt.Font("Dialog", 1, 10));
        lastSuccessfulBuildButton.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_Show")); // NOI18N
        lastSuccessfulBuildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastSuccessfulBuildButtonActionPerformed(evt);
            }
        });

        lastStableBuildButton.setFont(new java.awt.Font("Dialog", 1, 10));
        lastStableBuildButton.setText(org.openide.util.NbBundle.getMessage(HudsonJobPermalinksPanel.class, "LBL_Show")); // NOI18N
        lastStableBuildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastStableBuildButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lastStableBuildLabel)
                    .add(lastBuildLabel)
                    .add(lastFailedBuildLabel)
                    .add(lastSuccessfulBuildLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 72, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(lastBuildButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lastStableBuildButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lastSuccessfulBuildButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lastFailedBuildButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastBuildButton)
                    .add(lastBuildLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastStableBuildLabel)
                    .add(lastStableBuildButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastSuccessfulBuildButton)
                    .add(lastSuccessfulBuildLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastFailedBuildButton)
                    .add(lastFailedBuildLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void lastFailedBuildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastFailedBuildButtonActionPerformed
    actionProvider.showBuild(job, job.getLastFailedBuild());
}//GEN-LAST:event_lastFailedBuildButtonActionPerformed

private void lastSuccessfulBuildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastSuccessfulBuildButtonActionPerformed
    actionProvider.showBuild(job, job.getLastSuccessfulBuild());
}//GEN-LAST:event_lastSuccessfulBuildButtonActionPerformed

private void lastStableBuildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastStableBuildButtonActionPerformed
    actionProvider.showBuild(job, job.getLastStableBuild());
}//GEN-LAST:event_lastStableBuildButtonActionPerformed

private void lastBuildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastBuildButtonActionPerformed
    actionProvider.showBuild(job, job.getLastBuild());
}//GEN-LAST:event_lastBuildButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton lastBuildButton;
    private javax.swing.JLabel lastBuildLabel;
    private javax.swing.JButton lastFailedBuildButton;
    private javax.swing.JLabel lastFailedBuildLabel;
    private javax.swing.JButton lastStableBuildButton;
    private javax.swing.JLabel lastStableBuildLabel;
    private javax.swing.JButton lastSuccessfulBuildButton;
    private javax.swing.JLabel lastSuccessfulBuildLabel;
    // End of variables declaration//GEN-END:variables
    
}
