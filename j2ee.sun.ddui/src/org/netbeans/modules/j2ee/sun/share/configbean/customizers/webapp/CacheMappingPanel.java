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
/*
 * CacheMappingPanel.java
 *
 * Created on January 7, 2004, 5:11 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.beans.PropertyVetoException;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.netbeans.modules.j2ee.sun.dd.api.web.CacheMapping;

import org.netbeans.modules.j2ee.sun.share.configbean.WebAppRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.WebAppCache;

import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.GenericTableModel;


/**
 *
 * @author Peter Williams
 */
public class CacheMappingPanel extends javax.swing.JPanel implements TableModelListener, ListSelectionListener {

    private static final ResourceBundle webappBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.webapp.Bundle");	// NOI18N
	
    private static final ResourceBundle commonBundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.Bundle");	// NOI18N
	
	private WebAppRootCustomizer masterPanel;

	private CacheMappingTableModel cacheMappingModel;
	private SelectedCacheMappingPanel selectedCacheMappingPanel;

	/** Creates new form CacheMappingPanel */
	public CacheMappingPanel(WebAppRootCustomizer src) {
		masterPanel = src;

		initComponents();
		initUserComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLblCacheMappingUsageDescription = new javax.swing.JLabel();
        cacheMappingTablePanel = new javax.swing.JPanel();
        jLblCacheMappings = new javax.swing.JLabel();
        jScrlPnCacheMapping = new javax.swing.JScrollPane();
        cacheMappingTable = new org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable();
        cacheMappingButtonPanel = new javax.swing.JPanel();
        jBtnAddMapping = new javax.swing.JButton();
        jBtnRemoveMapping = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_CacheMappingsTab"));
        getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_CacheMappingsTab"));
        jLblCacheMappingUsageDescription.setText(webappBundle.getString("LBL_CacheMappingDescription"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(jLblCacheMappingUsageDescription, gridBagConstraints);

        cacheMappingTablePanel.setLayout(new java.awt.GridBagLayout());

        jLblCacheMappings.setLabelFor(cacheMappingTable);
        jLblCacheMappings.setText(webappBundle.getString("LBL_CacheMappings"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        cacheMappingTablePanel.add(jLblCacheMappings, gridBagConstraints);

        jScrlPnCacheMapping.setViewportView(cacheMappingTable);
        cacheMappingTable.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_CacheMappingTable"));
        cacheMappingTable.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_CacheMappingTable"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        cacheMappingTablePanel.add(jScrlPnCacheMapping, gridBagConstraints);

        cacheMappingButtonPanel.setLayout(new java.awt.GridBagLayout());

        jBtnAddMapping.setText(commonBundle.getString("LBL_Add"));
        jBtnAddMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddMappingActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        cacheMappingButtonPanel.add(jBtnAddMapping, gridBagConstraints);
        jBtnAddMapping.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_AddCacheMapping"));
        jBtnAddMapping.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_AddCacheMapping"));

        jBtnRemoveMapping.setText(commonBundle.getString("LBL_Remove"));
        jBtnRemoveMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnRemoveMappingActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        cacheMappingButtonPanel.add(jBtnRemoveMapping, gridBagConstraints);
        jBtnRemoveMapping.getAccessibleContext().setAccessibleName(webappBundle.getString("ACSN_RemoveCacheMapping"));
        jBtnRemoveMapping.getAccessibleContext().setAccessibleDescription(webappBundle.getString("ACSD_RemoveCacheMapping"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        cacheMappingTablePanel.add(cacheMappingButtonPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(cacheMappingTablePanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

	private void jBtnRemoveMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnRemoveMappingActionPerformed
		// Add your handling code here:
		int row = cacheMappingTable.getSelectedRow();

		if(row != -1) {
			ListSelectionModel listSelectionModel = cacheMappingTable.getSelectionModel();
			boolean savedValueIsAdjusting =  listSelectionModel.getValueIsAdjusting();
			try {
				listSelectionModel.setValueIsAdjusting(true);

				cacheMappingModel.removeRow(row);
				if(row > 0 && row >= cacheMappingModel.getRowCount()) {
					row -= 1;
				}

				listSelectionModel.setSelectionInterval(row, row);
			} finally {
				listSelectionModel.setValueIsAdjusting(savedValueIsAdjusting);
			}

			if(cacheMappingModel.getRowCount() > 0) {
				jBtnRemoveMapping.requestFocusInWindow();
			} else {
				jBtnAddMapping.requestFocusInWindow();
			}
		}
	}//GEN-LAST:event_jBtnRemoveMappingActionPerformed

	private void jBtnAddMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddMappingActionPerformed
		// Add your handling code here:
		ListSelectionModel listSelectionModel = cacheMappingTable.getSelectionModel();
		boolean savedValueIsAdjusting =  listSelectionModel.getValueIsAdjusting();
		try {
			listSelectionModel.setValueIsAdjusting(true);
			cacheMappingModel.addRow();

			int row = cacheMappingModel.getRowCount() - 1;
			listSelectionModel.setSelectionInterval(row, row);
		} finally {
			listSelectionModel.setValueIsAdjusting(savedValueIsAdjusting);
		}

//		jBtnAddMapping.requestFocusInWindow();
	}//GEN-LAST:event_jBtnAddMappingActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cacheMappingButtonPanel;
    private org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.FixedHeightJTable cacheMappingTable;
    private javax.swing.JPanel cacheMappingTablePanel;
    private javax.swing.JButton jBtnAddMapping;
    private javax.swing.JButton jBtnRemoveMapping;
    private javax.swing.JLabel jLblCacheMappingUsageDescription;
    private javax.swing.JLabel jLblCacheMappings;
    private javax.swing.JScrollPane jScrlPnCacheMapping;
    // End of variables declaration//GEN-END:variables

	private void initUserComponents() {
		cacheMappingModel = new CacheMappingTableModel();
		cacheMappingTable.setModel(cacheMappingModel);
		cacheMappingTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		selectedCacheMappingPanel = new SelectedCacheMappingPanel(masterPanel);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 6, 0, 5);
		add(selectedCacheMappingPanel, gridBagConstraints);
	}
	
	public void addListeners() {
		cacheMappingModel.addTableModelListener(this);
		cacheMappingTable.getSelectionModel().addListSelectionListener(this);
	}
	
	public void removeListeners() {
		cacheMappingTable.getSelectionModel().removeListSelectionListener(this);
		cacheMappingModel.removeTableModelListener(this);
	}
	
	/** Initialization of all the fields in this panel from the bean that
	 *  was passed in.
	 */
	public void initFields(WebAppCache cacheBean) {
		// initialize cache mapping table
		List cacheMappings = cacheBean.getCacheMappings();
		cacheMappingModel.setData(cacheMappings, cacheBean.getParent().getAppServerVersion());

		// select the first row of the table, if there is one.
		ListSelectionModel listSelectionModel = cacheMappingTable.getSelectionModel();
		if(cacheMappings != null && cacheMappings.size() > 0) {
			listSelectionModel.setSelectionInterval(0,0);
			handleListSelectionChanged();
		} else {
			enableTableAffectedControls();
		}
	}

	private void enableTableAffectedControls() {
		boolean hasMappings = (cacheMappingModel.getRowCount() > 0) ? true : false;
		boolean hasSelection = (cacheMappingTable.getSelectionModel().getMinSelectionIndex() != -1) ? true : false;

//		jBtnAddMapping.setEnabled(true);
		jBtnRemoveMapping.setEnabled(hasMappings && hasSelection);
		selectedCacheMappingPanel.setContainerEnabled(selectedCacheMappingPanel, hasMappings && hasSelection);
	}
	
	private void handleListSelectionChanged() {
		enableTableAffectedControls();
		selectedCacheMappingPanel.setSelectedCacheMapping(getSelectedMapping());		
	}

	/** -----------------------------------------------------------------------
	 *   TableModelListener implementation
	 */
	public void tableChanged(TableModelEvent tableModelEvent) {
		WebAppRoot bean = masterPanel.getBean();
		if(bean != null) {
			WebAppCache cacheBean = bean.getCacheBean();
			try {
				cacheBean.setCacheMappings(cacheMappingModel.getData());
				bean.setDirty();
			} catch(PropertyVetoException ex) {
				// !PW What to do?
			}
		}

		enableTableAffectedControls();
	}

	/** -----------------------------------------------------------------------
	 *   ListSelectionListener implementation
	 */
	public void valueChanged(ListSelectionEvent listSelectionEvent) {
		if(!listSelectionEvent.getValueIsAdjusting()) {
			handleListSelectionChanged();
		}
	}

	private CacheMapping getSelectedMapping() {
		List mappings = cacheMappingModel.getData();
		int row = cacheMappingTable.getSelectedRow();

		CacheMapping mapping;

		if(row >= 0 && row < mappings.size()) {
			mapping = (CacheMapping) mappings.get(row);
		} else {
			mapping = null;
		}

		return mapping;
	}
}
