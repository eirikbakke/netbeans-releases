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
 * EjbJarRootCustomizer.java        October 1, 2003, 3:40 PM
 *
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.netbeans.modules.j2ee.sun.share.configbean.BaseEjb;
import org.netbeans.modules.j2ee.sun.share.configbean.ErrorMessageDB;
import org.netbeans.modules.j2ee.sun.share.configbean.Utils;
import org.netbeans.modules.j2ee.sun.share.configbean.ValidationError;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.BaseCustomizer;
import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.CustomizerTitlePanel;


/**F
 *
 * @author  Rajeshwar Patil
 * @version %I%, %G%
 */
public abstract class EjbCustomizer extends BaseCustomizer implements TableModelListener {

    static final ResourceBundle bundle = ResourceBundle.getBundle(
            "org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule.Bundle"); // NOI18N
    
    /** Tabbed panels on the ejb customizer are very dynamic, so we'll use a property
     *  bound to the panels added to the tab control of which partition they are in.
     */
    static final String PARTITION_KEY = "validationPartition";
    
    private BaseEjb theBean;
    private IorSecurityConfigPanel iorSecurityConfigPanel;
//    protected boolean initializing = false;


    /** Creates new customizer EjbCustomizer */
    public EjbCustomizer() {
        initComponents();
        initUserComponents();
    }
    
    public BaseEjb getBean() {
        return theBean;
    }

    
    //get the bean specific panel
    protected abstract javax.swing.JPanel getBeanPanel();

    //initialize all the elements in the bean specific panel
    protected abstract void initializeBeanPanel(BaseEjb theBean);

    //add bean sepcific tabbed panels
    protected abstract void addTabbedBeanPanels();

    //initialize bean specific panels in tabbed pane
    protected abstract void initializeTabbedBeanPanels(BaseEjb theBean);


     /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panel1 = new java.awt.Panel();
        generalPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jndiNameLabel = new javax.swing.JLabel();
        jndiNameTextField = new javax.swing.JTextField();
        passByRefLabel = new javax.swing.JLabel();
        passByRefComboBox = new javax.swing.JComboBox();
        tabbedPanel = new javax.swing.JTabbedPane();

        setLayout(new java.awt.GridBagLayout());

        generalPanel.setLayout(new java.awt.GridBagLayout());

        generalPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        nameLabel.setLabelFor(nameTextField);
        nameLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Name_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        generalPanel.add(nameLabel, gridBagConstraints);
        nameLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Name_Acsbl_Name"));
        nameLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Name_Acsbl_Desc"));

        nameTextField.setEditable(false);
        nameTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Name_Tool_Tip"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        generalPanel.add(nameTextField, gridBagConstraints);
        nameTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Name_Acsbl_Name"));
        nameTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Name_Acsbl_Desc"));

        jndiNameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Jndi_Name").charAt(0));
        jndiNameLabel.setLabelFor(jndiNameTextField);
        jndiNameLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Jndi_Name_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        generalPanel.add(jndiNameLabel, gridBagConstraints);
        jndiNameLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Jndi_Name_Acsbl_Name"));
        jndiNameLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Jndi_Name_Acsbl_Desc"));

        jndiNameTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Jndi_Name_Tool_Tip"));
        jndiNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jndiNameKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        generalPanel.add(jndiNameTextField, gridBagConstraints);
        jndiNameTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Jndi_Name_Acsbl_Name"));
        jndiNameTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Ejb_Jndi_Name_Acsbl_Desc"));

        passByRefLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Pass_By_Reference").charAt(0));
        passByRefLabel.setLabelFor(passByRefComboBox);
        passByRefLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Pass_By_Reference_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 5, 0);
        generalPanel.add(passByRefLabel, gridBagConstraints);
        passByRefLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pass_By_Reference_Acsbl_Name"));
        passByRefLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pass_By_Reference_Acsbl_Desc"));

        passByRefComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "true", "false" }));
        passByRefComboBox.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pass_By_Reference_Tool_Tip"));
        passByRefComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passByRefComboBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        generalPanel.add(passByRefComboBox, gridBagConstraints);
        passByRefComboBox.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pass_By_Reference_Acsbl_Name"));
        passByRefComboBox.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Pass_By_Reference_Acsbl_Desc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 5);
        add(generalPanel, gridBagConstraints);

        tabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanelStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(tabbedPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void tabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanelStateChanged
        showErrors();
    }//GEN-LAST:event_tabbedPanelStateChanged

    private void passByRefComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passByRefComboBoxActionPerformed
        if(theBean != null) {
            String newPassByRef = (String) passByRefComboBox.getSelectedItem();
            String oldPassByRef = theBean.getPassByReference();
        
            if(!Utils.strEquivalent(oldPassByRef, newPassByRef)) {
                try {
                    if(Utils.notEmpty(newPassByRef)) {
                        theBean.setPassByReference(newPassByRef);
                    } else {
                        theBean.setPassByReference(null);
                    }

//                    theBean.firePropertyChange("passByReference", oldPassByRef, newPassByRef); // NOI18N
                    validateField(BaseEjb.FIELD_PASS_BY_REFERENCE);
                } catch (PropertyVetoException ex) {
                }
            }
        }
    }//GEN-LAST:event_passByRefComboBoxActionPerformed

    private void jndiNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jndiNameKeyReleased
        if(theBean != null) {
            String newJndiName = (String) jndiNameTextField.getText();
            String oldJndiName = theBean.getJndiName();
        
            if(!Utils.strEquivalent(oldJndiName, newJndiName)) {
                try {
                    if(Utils.notEmpty(newJndiName)) {
                        theBean.setJndiName(newJndiName);
                    } else {
                        theBean.setJndiName(null);
                    }

//                    theBean.firePropertyChange("jndiName", oldJndiName, newJndiName); // NOI18N
                    validateField(BaseEjb.FIELD_JNDI_NAME);
                } catch (PropertyVetoException ex) {
                    ex.printStackTrace();
                }
            }
        }        
    }//GEN-LAST:event_jndiNameKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel generalPanel;
    private javax.swing.JLabel jndiNameLabel;
    private javax.swing.JTextField jndiNameTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private java.awt.Panel panel1;
    private javax.swing.JComboBox passByRefComboBox;
    private javax.swing.JLabel passByRefLabel;
    protected javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables

    private void initUserComponents() {
		// Add title panel
		CustomizerTitlePanel titlePanel = new CustomizerTitlePanel();
		titlePanel.setCustomizerTitle(bundle.getString("EJB_TITLE")); //NOI18N
		add(titlePanel, titlePanel.getConstraints(), 0);		

        // Add bean specific non-tabbed panel.
        JPanel beanSpecificPanel = getBeanPanel();
        if(beanSpecificPanel != null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            beanSpecificPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(6, 6, 0, 5);
            add(beanSpecificPanel, gridBagConstraints, 2);
        }

        // ior-security-config is support for all types of EJB's.
        iorSecurityConfigPanel = new IorSecurityConfigPanel(this);
        iorSecurityConfigPanel.getAccessibleContext().setAccessibleName(bundle.getString("IorSecurityConfig_Acsbl_Name")); // NOI18N
        iorSecurityConfigPanel.getAccessibleContext().setAccessibleDescription(bundle.getString("IorSecurityConfig_Acsbl_Desc")); // NOI18N  
        tabbedPanel.addTab(bundle.getString("LBL_IorSecurityConfig"), iorSecurityConfigPanel); // NOI18N

        // Add bean specific tabbed panels
        addTabbedBeanPanels();
        
		// Add error panel
		addErrorPanel();
    }
    
    protected void initFields() {
        // Initialize all the elements in the general panel
        nameTextField.setText(theBean.getEjbName());
        jndiNameTextField.setText(theBean.getJndiName());

        // !PW FIXME probably bug here since passByRefComboBox is not always initialized.
        String passByRef = theBean.getPassByReference();
        if(passByRef != null) {
            passByRefComboBox.setSelectedItem(passByRef);
        }

        // Initialize all the elements in the bean specific panel
        initializeBeanPanel(theBean);  // abstract

        // Initialize IorSecurityConfig
        iorSecurityConfigPanel.initFields(theBean.getIorSecurityConfig());

        // Initialize bean specific panels in tabbed pane
        initializeTabbedBeanPanels(theBean); // abstract
    }
    
//    protected void addListeners() {
//        super.addListeners();
//    }
//    
//    protected void removeListeners() {
//        super.removeListeners();
//    }     

	public void partitionStateChanged(ErrorMessageDB.PartitionState oldState, ErrorMessageDB.PartitionState newState) {
		if(newState.getPartition() == getPartition() || 
                newState.getPartition() == getGlobalPartition()) {
			showErrors();
		}
		
		if(oldState.hasMessages() != newState.hasMessages()) {
            int tabIndex = getTabIndex(newState.getPartition());
			if(tabIndex != -1) {
                tabbedPanel.setIconAt(tabIndex, newState.hasMessages() ? panelErrorIcon : null);
            }
		}
	}
    
    protected boolean setBean(Object bean) {
		boolean result = super.setBean(bean);
		
		if(bean instanceof BaseEjb) {
			theBean = (BaseEjb) bean;
			result = true;
		} else {
			// if bean is not a BaseEjb, then it shouldn't have passed Base either.
			assert (result == false) : 
				"EjbCustomizer was passed wrong bean type in setBean(Object bean)";	// NOI18N
				
			theBean = null;
			result = false;
		}
		
		return result;
    }
    
    /** Retrieve the partition that should be associated with the current 
     *  selected tab.
     *
     *  @return ValidationError.Partition
     */
    public ValidationError.Partition getPartition() {
        return getPartition(tabbedPanel.getSelectedComponent());
    } 

    /** Retrieve the partition used for global error messages.
     */
    public ValidationError.Partition getGlobalPartition() {
        return ValidationError.PARTITION_EJB_GLOBAL;
    }

    private ValidationError.Partition getPartition(Component c) {
        ValidationError.Partition panelPartition = ValidationError.PARTITION_EJB_GLOBAL;
        if(c instanceof JPanel) {
            JPanel p = (JPanel) c;
            panelPartition = (ValidationError.Partition) p.getClientProperty(PARTITION_KEY);
        }
        return panelPartition;
    }
    
    private HashMap partitionTabIndexMap;
    
    private int getTabIndex(ValidationError.Partition partition) {
        if(partitionTabIndexMap == null) {
            partitionTabIndexMap = new HashMap();
            int ntabs = tabbedPanel.getTabCount();
            for(int tab = 0; tab < ntabs; tab++) {
                ValidationError.Partition tabPartition = getPartition(tabbedPanel.getComponentAt(tab));
                if(!ValidationError.PARTITION_EJB_GLOBAL.equals(tabPartition)) {
                    partitionTabIndexMap.put(tabPartition, new Integer(tab));
                }
            }
        }
        Integer tabIndex = (Integer) partitionTabIndexMap.get(partition);
        return (tabIndex != null) ? tabIndex.intValue() : -1;
    }
    
    public void tableChanged(TableModelEvent e) {
        // Placeholder so super.tableChanged has a place to go.
    }    
}
