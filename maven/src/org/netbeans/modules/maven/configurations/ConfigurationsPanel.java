/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.configurations;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author  mkleint
 */
@SuppressWarnings("serial")
public class ConfigurationsPanel extends javax.swing.JPanel {
    private NbMavenProjectImpl project;
    private ModelHandle2 handle;
    List<ModelHandle2.Configuration> lastNonProfileList = new ArrayList<ModelHandle2.Configuration>();
    /** Creates new form ConfigurationsPanel */
    private ConfigurationsPanel() {
        initComponents();
    }

    ConfigurationsPanel(ModelHandle2 handle, NbMavenProjectImpl project) {
        this();
        this.handle = handle;
        this.project = project;
        
//        btnAdd.setVisible(false);
//        btnEdit.setVisible(false);
//        btnRemove.setVisible(false);
//        addProfileConfigurations();
        
        initListUI();
        checkButtonEnablement();
        lstConfigurations.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component supers = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelHandle2.Configuration conf = (ModelHandle2.Configuration)value;
                if (conf == ConfigurationsPanel.this.handle.getActiveConfiguration()) {
                    supers.setFont(supers.getFont().deriveFont(Font.BOLD));
                }
                return supers;
            }
        });
        
        lstConfigurations.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                checkButtonEnablement();
            }

        });
        createListModel();
    }

    private void checkButtonEnablement() {
        ModelHandle2.Configuration conf = (ModelHandle2.Configuration) lstConfigurations.getSelectedValue();
        if (conf == null || conf.isProfileBased() || conf.isDefault()) {
            btnEdit.setEnabled(false);
            btnRemove.setEnabled(false);
        } else {
            btnEdit.setEnabled(true);
            btnRemove.setEnabled(true);
        }
    }

    private void createListModel() {
//        boolean isProfile = false;
        DefaultListModel model = new DefaultListModel();
        if (handle.getConfigurations() != null) {
            for (ModelHandle2.Configuration hndl : handle.getConfigurations()) {
                model.addElement(hndl);
//                if (hndl.isProfileBased()) {
//                    isProfile = true;
//                }
            }
        }
        lstConfigurations.setModel(model);
        lstConfigurations.setSelectedValue(handle.getActiveConfiguration(), true);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConfigurations = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstConfigurations = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnActivate = new javax.swing.JButton();

        lblConfigurations.setLabelFor(lstConfigurations);
        org.openide.awt.Mnemonics.setLocalizedText(lblConfigurations, org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.lblConfigurations.text")); // NOI18N

        lstConfigurations.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstConfigurations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstConfigurations);
        lstConfigurations.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.lstConfigurations.AccessibleContext.accessibleDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnAdd, org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnAdd.text")); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnEdit, org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnEdit.text")); // NOI18N
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnActivate, org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnActivate.text")); // NOI18N
        btnActivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConfigurations)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAdd)
                        .addComponent(btnActivate)
                        .addComponent(btnEdit))
                    .addComponent(btnRemove))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnActivate, btnAdd, btnEdit, btnRemove});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConfigurations)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnActivate)
                        .addGap(18, 18, 18)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove)
                        .addContainerGap(81, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)))
        );

        btnAdd.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnAdd.AccessibleContext.accessibleDescription")); // NOI18N
        btnEdit.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnEdit.AccessibleContext.accessibleDescription")); // NOI18N
        btnRemove.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnRemove.AccessibleContext.accessibleDescription")); // NOI18N
        btnActivate.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ConfigurationsPanel.class, "ConfigurationsPanel.btnActivate.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
    NewConfigurationPanel pnl = new NewConfigurationPanel();
    pnl.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigurationsPanel.class, "ACSD_Add_Config"));
    DialogDescriptor dd = new DialogDescriptor(pnl, NbBundle.getMessage(ConfigurationsPanel.class, "TIT_Add_Config"));
    pnl.attachDescriptor(dd);
    Object ret = DialogDisplayer.getDefault().notify(dd);
    if (ret == DialogDescriptor.OK_OPTION) {
        ModelHandle2.Configuration conf = ModelHandle2.createCustomConfiguration(pnl.getConfigurationId());
        conf.setShared(pnl.isShared());
        conf.setActivatedProfiles(pnl.getProfiles());
        handle.addConfiguration(conf);
        handle.markConfigurationsAsModified();
        createListModel();
        lstConfigurations.setSelectedValue(conf, true);
    }
}//GEN-LAST:event_btnAddActionPerformed

private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
    ModelHandle2.Configuration conf = (ModelHandle2.Configuration) lstConfigurations.getSelectedValue();
    if (conf != null) {
        NewConfigurationPanel pnl = new NewConfigurationPanel();
        pnl.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ConfigurationsPanel.class, "ACSD_Edit_Config"));
        pnl.setConfigurationId(conf.getId());
        pnl.setProfiles(conf.getActivatedProfiles());
        pnl.setShared(conf.isShared());
        DialogDescriptor dd = new DialogDescriptor(pnl, NbBundle.getMessage(ConfigurationsPanel.class, "TIT_Edit_Config"));
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == DialogDescriptor.OK_OPTION) {
            conf.setShared(pnl.isShared());
            conf.setActivatedProfiles(pnl.getProfiles());
            handle.markConfigurationsAsModified();
            createListModel();
            lstConfigurations.setSelectedValue(conf, true);
        }
    }
}//GEN-LAST:event_btnEditActionPerformed

private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
    ModelHandle2.Configuration conf = (ModelHandle2.Configuration) lstConfigurations.getSelectedValue();
    if (conf != null) {
        handle.removeConfiguration(conf);
        createListModel();
    }
}//GEN-LAST:event_btnRemoveActionPerformed

private void btnActivateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivateActionPerformed
    ModelHandle2.Configuration conf = (ModelHandle2.Configuration) lstConfigurations.getSelectedValue();
    if (conf != null) {
        handle.setActiveConfiguration(conf);
    }
    lstConfigurations.repaint();
    
}//GEN-LAST:event_btnActivateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivate;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblConfigurations;
    private javax.swing.JList lstConfigurations;
    // End of variables declaration//GEN-END:variables

    private void initListUI() {
        lstConfigurations.setEnabled(true);
        btnActivate.setEnabled(true);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(true);
        btnRemove.setEnabled(true);
    }
    // End of variables declaration


//    private void addProfileConfigurations() {
//        ArrayList<ModelHandle.Configuration> lst = new ArrayList<ModelHandle.Configuration>(handle.getConfigurations());
//        lastNonProfileList.clear();
//        for (ModelHandle.Configuration conf : lst) {
//            if (!conf.isProfileBased() && !conf.isDefault()) {
//                handle.removeConfiguration(conf);
//                lastNonProfileList.add(conf);
//                handle.markAsModified(handle.getConfigurations());
//            }
//        }
//        //currently profile based are mutually exclusive to non-profile based..
//        for (String profile : ProfileUtils.retrieveAllProfiles(handle.getProject())) {
//            handle.addConfiguration(ModelHandle.createProfileConfiguration(profile));
//            handle.markAsModified(handle.getConfigurations());
//        }
//        createListModel();
//    }
    
//    private void removeProfileConfigurations() {
//        ArrayList<ModelHandle.Configuration> lst = new ArrayList<ModelHandle.Configuration>(handle.getConfigurations());
//        for (ModelHandle.Configuration conf : lst) {
//            if (conf.isProfileBased() && !conf.isDefault()) {
//                handle.removeConfiguration(conf);
//                handle.markAsModified(handle.getConfigurations());
//            }
//        }
//        //currently profile based are mutually exclusive to non-profile based..
//        for (ModelHandle.Configuration conf : lastNonProfileList) {
//            handle.addConfiguration(conf);
//            handle.markAsModified(handle.getConfigurations());
//        }
//        createListModel();
//    }

}
