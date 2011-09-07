/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

/*
 * CategoryPanelVisual.java
 *
 * Created on Jul 25, 2011, 3:39:52 PM
 */
package org.netbeans.modules.debugger.jpda.visual.options;

import org.netbeans.api.debugger.Properties;
import org.netbeans.modules.debugger.jpda.ui.options.StorablePanel;

/**
 *
 * @author martin
 */
public class CategoryPanelVisual extends StorablePanel {

    /** Creates new form CategoryPanelVisual */
    public CategoryPanelVisual() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uploadAgentCheckBox = new javax.swing.JCheckBox();
        uploadAgentDescriptionLabel1 = new javax.swing.JLabel();
        uploadAgentDescriptionLabel2 = new javax.swing.JLabel();
        componentBreakpointsCheckBox = new javax.swing.JCheckBox();

        uploadAgentCheckBox.setText(org.openide.util.NbBundle.getMessage(CategoryPanelVisual.class, "CategoryPanelVisual.AgentCheckBox.text")); // NOI18N

        uploadAgentDescriptionLabel1.setText(org.openide.util.NbBundle.getMessage(CategoryPanelVisual.class, "CategoryPanelVisual.uploadAgentDescriptionLabel1.text")); // NOI18N

        uploadAgentDescriptionLabel2.setText(org.openide.util.NbBundle.getMessage(CategoryPanelVisual.class, "CategoryPanelVisual.uploadAgentDescriptionLabel2.text")); // NOI18N

        componentBreakpointsCheckBox.setText(org.openide.util.NbBundle.getMessage(CategoryPanelVisual.class, "CategoryPanelVisual.componentBreakpointsCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(uploadAgentCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(uploadAgentDescriptionLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(uploadAgentDescriptionLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(componentBreakpointsCheckBox)
                .addContainerGap(411, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(uploadAgentCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uploadAgentDescriptionLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uploadAgentDescriptionLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(componentBreakpointsCheckBox)
                .addContainerGap(208, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox componentBreakpointsCheckBox;
    private javax.swing.JCheckBox uploadAgentCheckBox;
    private javax.swing.JLabel uploadAgentDescriptionLabel1;
    private javax.swing.JLabel uploadAgentDescriptionLabel2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void load() {
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA.visual");
        uploadAgentCheckBox.setSelected(p.getBoolean("UploadAgent", true));
        componentBreakpointsCheckBox.setSelected(p.getBoolean("TrackComponentChanges", false));
    }

    @Override
    public void store() {
        Properties p = Properties.getDefault().getProperties("debugger.options.JPDA.visual");
        p.setBoolean("UploadAgent", uploadAgentCheckBox.isSelected());
        p.setBoolean("TrackComponentChanges", componentBreakpointsCheckBox.isSelected());
    }
}
