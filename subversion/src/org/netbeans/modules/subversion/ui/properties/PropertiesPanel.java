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
package org.netbeans.modules.subversion.ui.properties;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.netbeans.modules.subversion.SvnModuleConfig;
import org.netbeans.modules.versioning.util.ListenersSupport;

/**
 *
 * @author  Peter Pis
 */
public class PropertiesPanel extends javax.swing.JPanel implements PreferenceChangeListener, TableModelListener {

    private static final Object EVENT_SETTINGS_CHANGED = new Object();
    private PropertiesTable propertiesTable;
    private ListenersSupport listenerSupport = new ListenersSupport(this);
    
    /** Creates new form PropertiesPanel */
    public PropertiesPanel() {
        initComponents();
    }
    
    void setPropertiesTable(PropertiesTable propertiesTable){
        this.propertiesTable = propertiesTable;
    }
    
    public void addNotify() {
        super.addNotify();
        SvnModuleConfig.getDefault().getPreferences().addPreferenceChangeListener(this);        
        propertiesTable.getTableModel().addTableModelListener(this);
        listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
        txtAreaValue.selectAll();
    }

    public void removeNotify() {
        propertiesTable.getTableModel().removeTableModelListener(this);
        SvnModuleConfig.getDefault().getPreferences().removePreferenceChangeListener(this);
        super.removeNotify();
    }
    
    public void preferenceChange(PreferenceChangeEvent evt) {
        if (evt.getKey().startsWith(SvnModuleConfig.PROP_COMMIT_EXCLUSIONS)) {
            propertiesTable.dataChanged();
            listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
        }
    }

    public void tableChanged(TableModelEvent e) {
        listenerSupport.fireVersioningEvent(EVENT_SETTINGS_CHANGED);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        propsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSeparator1 = new javax.swing.JSeparator();
        labelForTable = new javax.swing.JLabel();

        jLabel2.setLabelFor(comboName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel2.text")); // NOI18N

        jLabel1.setLabelFor(txtAreaValue);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnBrowse, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnBrowse.text")); // NOI18N
        btnBrowse.setActionCommand(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnBrowse.actionCommand")); // NOI18N

        org.jdesktop.layout.GroupLayout propsPanelLayout = new org.jdesktop.layout.GroupLayout(propsPanel);
        propsPanel.setLayout(propsPanelLayout);
        propsPanelLayout.setHorizontalGroup(
            propsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 421, Short.MAX_VALUE)
        );
        propsPanelLayout.setVerticalGroup(
            propsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 101, Short.MAX_VALUE)
        );

        txtAreaValue.setColumns(20);
        txtAreaValue.setRows(5);
        jScrollPane1.setViewportView(txtAreaValue);
        txtAreaValue.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "txtAreaValue.AccessibleContext.accessibleName")); // NOI18N
        txtAreaValue.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "txtAreaValue.AccessibleContext.accessibleDescription")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnRefresh, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnRefresh.text")); // NOI18N
        btnRefresh.setActionCommand(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnRefresh.actionCommand")); // NOI18N
        btnRefresh.setMaximumSize(new java.awt.Dimension(75, 23));
        btnRefresh.setMinimumSize(new java.awt.Dimension(75, 23));

        org.openide.awt.Mnemonics.setLocalizedText(btnRemove, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnRemove.text")); // NOI18N
        btnRemove.setActionCommand(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnRemove.actionCommand")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnAdd, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.btnAdd.text")); // NOI18N
        btnAdd.setMaximumSize(new java.awt.Dimension(75, 23));
        btnAdd.setMinimumSize(new java.awt.Dimension(75, 23));

        org.openide.awt.Mnemonics.setLocalizedText(cbxRecursively, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "PropertiesPanel.cbxRecursively.text")); // NOI18N
        cbxRecursively.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbxRecursively.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(labelForTable, org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "jLabel3.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, propsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, comboName, 0, 315, Short.MAX_VALUE)
                            .add(btnBrowse)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cbxRecursively)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 73, Short.MAX_VALUE)
                        .add(btnRemove)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRefresh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, labelForTable))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {btnAdd, btnBrowse, btnRefresh, btnRemove}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(comboName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnBrowse)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnRefresh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnRemove)
                    .add(btnAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbxRecursively))
                .add(18, 18, 18)
                .add(labelForTable)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(propsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "jLabel2.AccessibleContext.accessibleDescription")); // NOI18N
        comboName.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "comboName.AccessibleContext.accessibleName")); // NOI18N
        comboName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "comboName.AccessibleContext.accessibleDescription")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "jLabel1.AccessibleContext.accessibleDescription")); // NOI18N
        btnBrowse.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnBrowse.AccessibleContext.accessibleDescription")); // NOI18N
        btnRefresh.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnRefresh.AccessibleContext.accessibleDescription")); // NOI18N
        btnRemove.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnRemove.AccessibleContext.accessibleDescription")); // NOI18N
        btnAdd.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "btnAdd.AccessibleContext.accessibleDescription")); // NOI18N
        cbxRecursively.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "cbxRecursively.AccessibleContext.accessibleDescription")); // NOI18N
        labelForTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PropertiesPanel.class, "labelForTable.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton btnAdd = new javax.swing.JButton();
    final javax.swing.JButton btnBrowse = new javax.swing.JButton();
    final javax.swing.JButton btnRefresh = new javax.swing.JButton();
    final javax.swing.JButton btnRemove = new javax.swing.JButton();
    final javax.swing.JCheckBox cbxRecursively = new javax.swing.JCheckBox();
    final javax.swing.JComboBox comboName = new javax.swing.JComboBox();
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public javax.swing.JLabel labelForTable;
    public javax.swing.JPanel propsPanel;
    final javax.swing.JTextArea txtAreaValue = new javax.swing.JTextArea();
    // End of variables declaration//GEN-END:variables
    
}
