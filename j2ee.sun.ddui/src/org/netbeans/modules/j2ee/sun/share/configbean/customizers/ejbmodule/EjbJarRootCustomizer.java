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
 * EjbJarRootCustomizer.java
 *
 * Created on October 1, 2003, 3:40 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule;

import java.beans.PropertyVetoException;
import java.util.ResourceBundle;

import org.netbeans.modules.j2ee.sun.share.configbean.EjbJarRoot;
import org.netbeans.modules.j2ee.sun.share.configbean.EjbJarVersion;
import org.netbeans.modules.j2ee.sun.share.configbean.ErrorMessageDB;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.ValidationError;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.BaseCustomizer;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.CustomizerTitlePanel;


/**
 *
 * @author  Rajeshwar Patil
 */
public class EjbJarRootCustomizer extends BaseCustomizer {

    static final ResourceBundle bundle = ResourceBundle.getBundle(
            "org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule.Bundle"); // NOI18N
    
    private EjbJarRoot theBean;
    
    private EjbJarPmDescriptorsPanel pmDescriptorPanel;
    private EjbJarMessagesPanel messagesPanel;
    private EjbJarCmpResourcePanel cmpResourcePanel;
    
    // true during initialization
    private boolean fieldInit;

    // true for j2ee 1.4 onward.
    private boolean ejb20FeaturesVisible;
    
    /** Creates new customizer EjbJarRootCustomizer */
    public EjbJarRootCustomizer() {
        initComponents();
        initUserComponents();
    }
    
	public EjbJarRoot getBean() {
		return theBean;
	}
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        tabbedPanel = new javax.swing.JTabbedPane();

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Name").charAt(0));
        nameLabel.setLabelFor(nameTextField);
        nameLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Name_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        add(nameLabel, gridBagConstraints);
        nameLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Name_Acsbl_Name"));
        nameLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Module_Name_Acsbl_Desc"));

        nameTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Module_Name_Tool_Tip"));
        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(nameTextField, gridBagConstraints);
        nameTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Name_Acsbl_Name"));
        nameTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Module_Name_Acsbl_Desc"));

        tabbedPanel.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanelStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void tabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanelStateChanged
        showErrors();
    }//GEN-LAST:event_tabbedPanelStateChanged

    private void nameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyReleased
        String newName = nameTextField.getText();
        String oldName = theBean.getName();

        try {
            if(Utils.notEmpty(newName)) {
                theBean.setName(newName);
            } else {
                theBean.setName(null);
            }
            
            validateField(EjbJarRoot.FIELD_EJBJAR_NAME);        
        } catch (PropertyVetoException ex) {
            // this should never happen.
        }
    }//GEN-LAST:event_nameTextFieldKeyReleased
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    protected javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables

    private void initUserComponents() {
        ejb20FeaturesVisible = true;
        
		// Add title panel
		CustomizerTitlePanel titlePanel = new CustomizerTitlePanel();
		titlePanel.setCustomizerTitle(bundle.getString("LBL_SunEjbJar")); //NOI18N
		add(titlePanel, titlePanel.getConstraints(), 0);		
		
        // add CMP Resource panel
        cmpResourcePanel = new EjbJarCmpResourcePanel(this);
        tabbedPanel.addTab(bundle.getString("CMP_RESOURCE_TAB"), cmpResourcePanel); // NOI18N
        
        // add PM Descriptors panel
        pmDescriptorPanel = new EjbJarPmDescriptorsPanel(this);
        tabbedPanel.addTab(bundle.getString("PM_DESCRIPTORS_TAB"), pmDescriptorPanel); // NOI18N
        
        // add Messages panel
        messagesPanel = new EjbJarMessagesPanel(this);
        tabbedPanel.addTab(bundle.getString("MESSAGES_TAB"), messagesPanel); // NOI18N
        
		// Add error panel
		addErrorPanel();
    }

    protected void initFields() {
        try {
            fieldInit = true;
            nameTextField.setText(theBean.getName());

            cmpResourcePanel.initFields(theBean);
            pmDescriptorPanel.initFields(theBean);
            
            if(theBean.getJ2EEModuleVersion().compareTo(EjbJarVersion.EJBJAR_2_0) >= 0) {
                showEjb20Panels();
                messagesPanel.initFields(theBean);
            } else {
                hideEjb20Panels();
            }
        } finally {
            fieldInit = false;
        }
    }
    
	private void showEjb20Panels() {
		if(!ejb20FeaturesVisible) {
			tabbedPanel.insertTab(bundle.getString("MESSAGES_TAB"),	// NOI18N
				null, messagesPanel, null, getMessagesTabIndex());
			ejb20FeaturesVisible = true;
		}
	}
	
	private void hideEjb20Panels() {
		if(ejb20FeaturesVisible) {
			tabbedPanel.removeTabAt(getMessagesTabIndex());	// Remove messages panel
			ejb20FeaturesVisible = false;
		}
	}
    
    protected void addListeners() {
        super.addListeners();
        
        cmpResourcePanel.addListeners();
        pmDescriptorPanel.addListeners();
        messagesPanel.addListeners();
    }
    
    protected void removeListeners() {
        super.removeListeners();
        
        messagesPanel.removeListeners();
        pmDescriptorPanel.removeListeners();
        cmpResourcePanel.removeListeners();
    }

	public void partitionStateChanged(ErrorMessageDB.PartitionState oldState, ErrorMessageDB.PartitionState newState) {
		if(newState.getPartition() == getPartition()) {
			showErrors();
		}
		
		if(oldState.hasMessages() != newState.hasMessages()) {
			tabbedPanel.setIconAt(newState.getPartition().getTabIndex(), newState.hasMessages() ? panelErrorIcon : null);
		}
	}
	
    protected boolean setBean(Object bean) {
		boolean result = super.setBean(bean);
		
		if(bean instanceof EjbJarRoot) {
			theBean = (EjbJarRoot) bean;
			result = true;
		} else {
			// if bean is not a EjbJarRoot, then it shouldn't have passed Base either.
			assert (result == false) : 
				"EjbJarRootCustomizer was passed wrong bean type in setBean(Object bean)";	// NOI18N
				
			theBean = null;
			result = false;
		}
		
		return result;
    }

    private int getMessagesTabIndex() {
        return 3;
    }
    
    /** Returns the help ID for sun-ejb-jar root customizer, based on current 
     *  selected tab.
     *
     * @return String representing the current active help ID for this customizer 
     */
    public String getHelpId() {
        // Determine which tab has focus and return help context for that tab.
        switch(tabbedPanel.getSelectedIndex()) {
            case 2:
                return "AS_CFG_EjbJarMessages"; // NOI18N
            case 1:
                return "AS_CFG_EjbJarPmDescriptors"; // NOI18N
            case 0:
                return "AS_CFG_EjbJarCmpResource"; // NOI18N
            default:
                return "AS_CFG_EjbJarRoot"; // NOI18N
        }
    }

    /** Retrieve the partition that should be associated with the current 
     *  selected tab.
     *
     *  @return ValidationError.Partition
     */
    public ValidationError.Partition getPartition() {
        switch(tabbedPanel.getSelectedIndex()) {
            case 2:
                return ValidationError.PARTITION_EJBJAR_MESSAGES;
            case 1:
                return ValidationError.PARTITION_EJBJAR_PM_DESCRIPTORS;
            case 0:
                return ValidationError.PARTITION_EJBJAR_CMP_RESOURCE;
            default:
                return ValidationError.PARTITION_GLOBAL;
        }
    }
}
