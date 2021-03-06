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
package org.netbeans.modules.j2ee.sun.share.configbean.customizers;

import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.common.SecurityRoleMapping;
import org.netbeans.modules.j2ee.sun.ddloaders.SunDescriptorDataObject;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.BaseSectionNodeInnerPanel;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.TextItemEditorModel;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.common.SecurityRoleMappingNode;
import org.netbeans.modules.j2ee.sun.share.PrincipalNameMapping;
import org.netbeans.modules.xml.multiview.ItemEditorHelper;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;


/**
 *
 * @author Peter Williams
 */
public class SecurityRoleMappingPanel extends BaseSectionNodeInnerPanel
        implements ListSelectionListener, TableModelListener {

    // data model & version
    private SecurityRoleMappingNode mappingNode;

    private SRMPrincipalTableModel principalTableModel;
    private SRMGroupTableModel groupTableModel;

    public SecurityRoleMappingPanel(SectionNodeView sectionNodeView, final SecurityRoleMappingNode mappingNode, final ASDDVersion version) {
        super(sectionNodeView, version);

        this.mappingNode = mappingNode;

        initComponents();
        initUserComponents(sectionNodeView);
    }

    private void initUserComponents(SectionNodeView sectionNodeView) {
        SunDescriptorDataObject dataObject = (SunDescriptorDataObject) sectionNodeView.getDataObject();
        XmlMultiViewDataSynchronizer synchronizer = dataObject.getModelSynchronizer();
        SecurityRoleMapping mapping = getMappingBean();

        principalTableModel = new SRMPrincipalTableModel(synchronizer, mapping, as90FeaturesVisible ? 2 : 1);
        jTblPrincipals.setModel(principalTableModel);

        groupTableModel = new SRMGroupTableModel(synchronizer, mapping);
        jTblGroups.setModel(groupTableModel);

        enablePrincipalButtons();
        enableGroupButtons();

        addRefreshable(new ItemEditorHelper(jTxtSecurityRoleName, new SecurityRoleMappingNameEditorModel(synchronizer)));

        jTxtSecurityRoleName.setEditable(!mappingNode.getBinding().isBound());
        
        addListeners();
    }
    
    private SecurityRoleMapping getMappingBean() {
        return (SecurityRoleMapping) mappingNode.getBinding().getSunBean();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLblSecurityRoleName = new javax.swing.JLabel();
        jTxtSecurityRoleName = new javax.swing.JTextField();
        jLblAssignedPrincipals = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblPrincipals = new org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable();
        jPnlPrincipalButtons = new javax.swing.JPanel();
        jBtnAddPrincipal = new javax.swing.JButton();
        jBtnEditPrincipal = new javax.swing.JButton();
        jBtnRemovePrincipal = new javax.swing.JButton();
        jLblAssignedGroups = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTblGroups = new org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable();
        jPnlGroupButtons = new javax.swing.JPanel();
        jBtnAddGroup = new javax.swing.JButton();
        jBtnEditGroup = new javax.swing.JButton();
        jBtnRemoveGroup = new javax.swing.JButton();

        setAlignmentX(LEFT_ALIGNMENT);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jLblSecurityRoleName.setLabelFor(jTxtSecurityRoleName);
        jLblSecurityRoleName.setText(customizerBundle.getString("LBL_SecurityRoleName_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jLblSecurityRoleName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 5);
        add(jTxtSecurityRoleName, gridBagConstraints);
        jTxtSecurityRoleName.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_SecurityRoleName")); // NOI18N
        jTxtSecurityRoleName.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_SecurityRoleName")); // NOI18N

        jLblAssignedPrincipals.setLabelFor(jTblPrincipals);
        jLblAssignedPrincipals.setText(customizerBundle.getString("LBL_PrincipalsAssigned")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(jLblAssignedPrincipals, gridBagConstraints);

        jScrollPane1.setViewportView(jTblPrincipals);
        jTblPrincipals.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_PrincipalsAssigned")); // NOI18N
        jTblPrincipals.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_PrincipalsAssigned")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 5);
        add(jScrollPane1, gridBagConstraints);

        jPnlPrincipalButtons.setOpaque(false);
        jPnlPrincipalButtons.setLayout(new java.awt.GridBagLayout());

        jBtnAddPrincipal.setText(customizerBundle.getString("LBL_AddPrincipal")); // NOI18N
        jBtnAddPrincipal.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnAddPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddPrincipalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPnlPrincipalButtons.add(jBtnAddPrincipal, gridBagConstraints);
        jBtnAddPrincipal.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_AddPrincipal")); // NOI18N
        jBtnAddPrincipal.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_AddPrincipal")); // NOI18N

        jBtnEditPrincipal.setText(customizerBundle.getString("LBL_EditPrincipal")); // NOI18N
        jBtnEditPrincipal.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnEditPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEditPrincipalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlPrincipalButtons.add(jBtnEditPrincipal, gridBagConstraints);
        jBtnEditPrincipal.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_EditPrincipal")); // NOI18N
        jBtnEditPrincipal.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_EditPrincipal")); // NOI18N

        jBtnRemovePrincipal.setText(customizerBundle.getString("LBL_RemovePrincipals")); // NOI18N
        jBtnRemovePrincipal.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnRemovePrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemovePrincipalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlPrincipalButtons.add(jBtnRemovePrincipal, gridBagConstraints);
        jBtnRemovePrincipal.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_RemovePrincipal")); // NOI18N
        jBtnRemovePrincipal.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_RemovePrincipal")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 5);
        add(jPnlPrincipalButtons, gridBagConstraints);

        jLblAssignedGroups.setLabelFor(jTblGroups);
        jLblAssignedGroups.setText(customizerBundle.getString("LBL_GroupsAssigned")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(jLblAssignedGroups, gridBagConstraints);

        jScrollPane2.setViewportView(jTblGroups);
        jTblGroups.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_GroupsAssigned")); // NOI18N
        jTblGroups.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_GroupsAssigned")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 5);
        add(jScrollPane2, gridBagConstraints);

        jPnlGroupButtons.setOpaque(false);
        jPnlGroupButtons.setLayout(new java.awt.GridBagLayout());

        jBtnAddGroup.setText(customizerBundle.getString("LBL_AddGroup")); // NOI18N
        jBtnAddGroup.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnAddGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddGroupActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPnlGroupButtons.add(jBtnAddGroup, gridBagConstraints);
        jBtnAddGroup.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_AddGroup")); // NOI18N
        jBtnAddGroup.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_AddGroup")); // NOI18N

        jBtnEditGroup.setText(customizerBundle.getString("LBL_EditGroup")); // NOI18N
        jBtnEditGroup.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnEditGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEditGroupActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlGroupButtons.add(jBtnEditGroup, gridBagConstraints);
        jBtnEditGroup.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_EditGroup")); // NOI18N
        jBtnEditGroup.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_EditGroup")); // NOI18N

        jBtnRemoveGroup.setText(customizerBundle.getString("LBL_RemoveGroups")); // NOI18N
        jBtnRemoveGroup.setMargin(new java.awt.Insets(2, 12, 2, 12));
        jBtnRemoveGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemoveGroupActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPnlGroupButtons.add(jBtnRemoveGroup, gridBagConstraints);
        jBtnRemoveGroup.getAccessibleContext().setAccessibleName(customizerBundle.getString("ACSN_RemoveGroup")); // NOI18N
        jBtnRemoveGroup.getAccessibleContext().setAccessibleDescription(customizerBundle.getString("ACSD_RemoveGroup")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 5);
        add(jPnlGroupButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnRemoveGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemoveGroupActionPerformed
        handleRemoveAction(jTblGroups);
    }//GEN-LAST:event_jBtnRemoveGroupActionPerformed

    private void jBtnEditGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEditGroupActionPerformed
        handleEditGroupAction();
    }//GEN-LAST:event_jBtnEditGroupActionPerformed

    private void jBtnAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddGroupActionPerformed
        handleAddGroupAction();
    }//GEN-LAST:event_jBtnAddGroupActionPerformed

    private void jBtnRemovePrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemovePrincipalActionPerformed
        handleRemoveAction(jTblPrincipals);
    }//GEN-LAST:event_jBtnRemovePrincipalActionPerformed

    private void jBtnEditPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEditPrincipalActionPerformed
        handleEditPrincipalAction();
    }//GEN-LAST:event_jBtnEditPrincipalActionPerformed

    private void jBtnAddPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddPrincipalActionPerformed
        handleAddPrincipalAction();
    }//GEN-LAST:event_jBtnAddPrincipalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnAddGroup;
    private javax.swing.JButton jBtnAddPrincipal;
    private javax.swing.JButton jBtnEditGroup;
    private javax.swing.JButton jBtnEditPrincipal;
    private javax.swing.JButton jBtnRemoveGroup;
    private javax.swing.JButton jBtnRemovePrincipal;
    private javax.swing.JLabel jLblAssignedGroups;
    private javax.swing.JLabel jLblAssignedPrincipals;
    private javax.swing.JLabel jLblSecurityRoleName;
    private javax.swing.JPanel jPnlGroupButtons;
    private javax.swing.JPanel jPnlPrincipalButtons;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable jTblGroups;
    private org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable jTblPrincipals;
    private javax.swing.JTextField jTxtSecurityRoleName;
    // End of variables declaration//GEN-END:variables

    /** Popup a dialog that lets the user add a new principal to this mapping.
     *  The new name will not be allowed to match any existing name.  The
     *  new item will be preselected afterwards.  The new name will either come
     *  from the existing master list or will be a new name and automatically added
     *  to the master list.
     */
    private void handleAddPrincipalAction() {
        ListSelectionModel selectionModel = jTblPrincipals.getSelectionModel();
        try {
            selectionModel.setValueIsAdjusting(true);
            SecurityAddPrincipalPanel.addPrincipalName(this, principalTableModel, version);

            int index = principalTableModel.getRowCount()-1;
            selectionModel.setSelectionInterval(index, index);
        } finally {
            selectionModel.setValueIsAdjusting(false);
        }
    }

    /** Popup a dialog that lets the user edit the selected entry.  The changed
     *  name will not be allowed to match any existing name.  The item will
     *  remain selected once the action is completed.
     */
    private void handleEditPrincipalAction() {
        int [] selectedIndices = jTblPrincipals.getSelectedRows();
        if(selectedIndices.length > 0) {
            ListSelectionModel selectionModel = jTblPrincipals.getSelectionModel();
            try {
                PrincipalNameMapping entry = principalTableModel.getElementAt(selectedIndices[0]);
                SecurityEditPrincipalPanel.editPrincipalName(this, entry, principalTableModel, version);
                selectionModel.setSelectionInterval(selectedIndices[0], selectedIndices[0]);
            } finally {
                selectionModel.setValueIsAdjusting(false);
            }
        }
    }

    /** Popup a dialog that lets the user add a new entry to the specifed master
     *  list.  The new name will not be allowed to match any existing name.  The
     *  new item will be preselected afterwards.  The new name will either come
     *  from the existing master list or will be a new name and automatically added
     *  to the master list.
     */
    private void handleAddGroupAction() {
        ListSelectionModel selectionModel = jTblGroups.getSelectionModel();
        try {
            selectionModel.setValueIsAdjusting(true);
            SecurityAddGroupPanel.addGroupName(this, groupTableModel);

            int index = groupTableModel.getRowCount()-1;
            selectionModel.setSelectionInterval(index, index);
        } finally {
            selectionModel.setValueIsAdjusting(false);
        }
    }

    /** Popup a dialog that lets the user edit the selected entry.  The changed
     *  name will not be allowed to match any existing name.  The item will
     *  remain selected once the action is completed.
     */
    private void handleEditGroupAction() {
        int [] selectedIndices = jTblGroups.getSelectedRows();
        if(selectedIndices.length > 0) {
            ListSelectionModel selectionModel = jTblGroups.getSelectionModel();
            try {
                String entry = groupTableModel.getElementAt(selectedIndices[0]);
                SecurityEditGroupPanel.editGroupName(this, entry, groupTableModel);
                selectionModel.setSelectionInterval(selectedIndices[0], selectedIndices[0]);
            } finally {
                selectionModel.setValueIsAdjusting(false);
            }
        }
    }

    /** Remove all selected items from the specified table.
     */
    private void handleRemoveAction(JTable theTable) {
        int [] selectedIndices = theTable.getSelectedRows();
        if(selectedIndices.length > 0) {
            ListSelectionModel selectionModel = theTable.getSelectionModel();
            try {
                SRMBaseTableModel theModel = (SRMBaseTableModel) theTable.getModel();
                selectionModel.setValueIsAdjusting(true);
                theModel.removeElements(selectedIndices);
                int numElements = theTable.getModel().getRowCount();
                if(numElements > 0) {
                    int newSelectedIndex = selectedIndices[0];
                    if(newSelectedIndex >= numElements) {
                        newSelectedIndex = numElements-1;
                    }
                    selectionModel.setSelectionInterval(newSelectedIndex, newSelectedIndex);
                } else {
                    selectionModel.clearSelection();
                }
            } finally {
                selectionModel.setValueIsAdjusting(false);
            }
        }
    }

    /** Enable the edit and remove buttons for the principal table based on how 
     *  many rows are selected.
     *    Add is available always (thus no change here).
     *    Edit is available when exactly one item is selected.
     *    Remove is available when at least one item is selected.
     */
    private void enablePrincipalButtons() {
        int numSelectedRows = jTblPrincipals.getSelectedRowCount();
        jBtnEditPrincipal.setEnabled(numSelectedRows == 1);
        jBtnRemovePrincipal.setEnabled(numSelectedRows > 0);
    }

    /** Enable the edit and remove buttons for the group table based on how 
     *  many rows are selected.
     *    Add is available always (thus no change here).
     *    Edit is available when exactly one item is selected.
     *    Remove is available when at least one item is selected.
     */
    private void enableGroupButtons() {
        int numSelectedRows = jTblGroups.getSelectedRowCount();
        jBtnEditGroup.setEnabled(numSelectedRows == 1);
        jBtnRemoveGroup.setEnabled(numSelectedRows > 0);
    }

    /** Adds selection listeners to the lists for proper button enabling.
     */
    protected void addListeners() {
        jTblPrincipals.getSelectionModel().addListSelectionListener(this);
        principalTableModel.addTableModelListener(this);
        jTblGroups.getSelectionModel().addListSelectionListener(this);
        groupTableModel.addTableModelListener(this);
    }

//    /** Removes previously added selection listeners.
//     */
//    protected void removeListeners() {
//        super.removeListeners();
//        
//        groupTableModel.removeTableModelListener(this);
//        jTblGroups.getSelectionModel().removeListSelectionListener(this);
//        principalTableModel.removeTableModelListener(this);
//        jTblPrincipals.getSelectionModel().removeListSelectionListener(this);
//    }

    /**
     * Implementation of the ListSelectionListener interface
     */
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if(!listSelectionEvent.getValueIsAdjusting()) {
            Object source = listSelectionEvent.getSource();
            if(source instanceof ListSelectionModel) {
                ListSelectionModel selectionModel = (ListSelectionModel) source;
                if(jTblPrincipals.getSelectionModel() == selectionModel) {
                    enablePrincipalButtons();
                } else if(jTblGroups.getSelectionModel() == selectionModel) {
                    enableGroupButtons();
                }
            }
        }
    }	

    /**
     * Implementation of the TableModelListener interface
     */
    public void tableChanged(TableModelEvent tableModelEvent) {
        mappingNode.addVirtualBean();
    }

//    public void partitionStateChanged(ErrorMessageDB.PartitionState oldState, ErrorMessageDB.PartitionState newState) {
//        if(newState.getPartition() == getPartition()) {
//            showErrors();
//        }
//
//        if(oldState.hasMessages() != newState.hasMessages()) {
//            securityTabbedPanel.setIconAt(newState.getPartition().getTabIndex(), newState.hasMessages() ? panelErrorIcon : null);
//        }
//    }	

    /** Retrieve the partition that should be associated with the current 
     *  selected tab.
     *
     *  @return ValidationError.Partition
     */
//    public ValidationError.Partition getPartition() {
//        switch(securityTabbedPanel.getSelectedIndex()) {
//            case 1:
//                return ValidationError.PARTITION_SECURITY_MASTER;
//            default:
//                return ValidationError.PARTITION_SECURITY_ASSIGN;
//        }
//    }

    public String getHelpId() {
        return "AS_CFG_SecurityRoleMapping";	// NOI18N
    }

    /** Return correct preferred size.  The tables in this are a bit territorial.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension minSize = getMinimumSize();
        Dimension maxSize = getMaximumSize();
        int preferredWidth = Math.min(Math.max(200, maxSize.width), minSize.width);
        return new Dimension(preferredWidth, minSize.height);
    }

    private class SecurityRoleMappingNameEditorModel extends TextItemEditorModel {

        public SecurityRoleMappingNameEditorModel(XmlMultiViewDataSynchronizer synchronizer) {
            super(synchronizer, true, true);
        }

        protected String getValue() {
            return getMappingBean().getRoleName();
        }

        protected void setValue(String value) {
            getMappingBean().setRoleName(value);
            
            if(mappingNode.addVirtualBean()) {
                // update if necessary
            }
        }
    }
}
