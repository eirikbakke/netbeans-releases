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
/*
 * JMSWizardVisualPanel.java
 *
 * Created on November 17, 2003, 11:23 AM
 */

package org.netbeans.modules.j2ee.sun.ide.sunresources.wizards;

import java.util.Vector;
import org.openide.util.NbBundle;
import java.util.ResourceBundle;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;

import org.netbeans.modules.j2ee.sun.sunresources.beans.Field;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldGroup;
import org.netbeans.modules.j2ee.sun.sunresources.beans.WizardConstants;
import org.netbeans.modules.j2ee.sun.sunresources.beans.FieldHelper;

/**
 *
 * @author  nityad
 */
public class JMSWizardVisualPanel extends javax.swing.JPanel implements ChangeListener, WizardConstants{
    
    protected ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.sunresources.wizards.Bundle"); //NOI18N
    
    protected final JMSWizardPanel panel;
    protected ResourceConfigHelper helper;
    
    protected Field fields[] = null;    
    protected FieldGroup[] groups;
           
    protected ArrayList beans = new ArrayList();
    protected boolean createNewResource = false;
    private FieldGroup adminObjPropGroup;
    
      /** Creates new form JMSWizardVisualPanel */
    public JMSWizardVisualPanel(JMSWizardPanel panel, FieldGroup[] groups) {
        this.panel = panel;
        this.helper = panel.getHelper();
        this.groups = groups;
        this.adminObjPropGroup = panel.getFieldGroup(__Properties2);  //NOI18N
        
        initComponents();
        refreshFields();
        
        setName(NbBundle.getMessage(JMSWizardVisualPanel.class, "LBL_GeneralAttributes_JMS")); //NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        chooseResLabel = new javax.swing.JLabel();
        queueConnectionRadioButton = new javax.swing.JRadioButton();
        topicConnectionRadioButton = new javax.swing.JRadioButton();
        queueRadioButton = new javax.swing.JRadioButton();
        topicRadioButton = new javax.swing.JRadioButton();
        adObjLabel = new javax.swing.JLabel();
        connLabel = new javax.swing.JLabel();
        connectionFactoryRadioButton = new javax.swing.JRadioButton();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jndiNameField = new javax.swing.JTextField();
        enabledComboBox = new javax.swing.JComboBox();
        descField = new javax.swing.JTextField();
        jndiNameLabel = new javax.swing.JLabel();
        enabledLabel = new javax.swing.JLabel();
        descLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/sunresources/wizards/Bundle"); // NOI18N
        chooseResLabel.setText(bundle.getString("LBL_resource-type")); // NOI18N
        chooseResLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel3.add(chooseResLabel, gridBagConstraints);
        chooseResLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_resource-type")); // NOI18N

        buttonGroup1.add(queueConnectionRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(queueConnectionRadioButton, bundle.getString("LBL_Connector_QueueConnectionFactory")); // NOI18N
        queueConnectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queueConnectionRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 30);
        jPanel3.add(queueConnectionRadioButton, gridBagConstraints);
        queueConnectionRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_Connector_QueueConnectionFactory")); // NOI18N

        buttonGroup1.add(topicConnectionRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(topicConnectionRadioButton, bundle.getString("LBL_Connector_TopicConnectionFactory")); // NOI18N
        topicConnectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topicConnectionRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 30);
        jPanel3.add(topicConnectionRadioButton, gridBagConstraints);
        topicConnectionRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_Connector_TopicConnectionFactory")); // NOI18N

        buttonGroup1.add(queueRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(queueRadioButton, bundle.getString("LBL_AdminObject_Queue")); // NOI18N
        queueRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queueRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 30);
        jPanel3.add(queueRadioButton, gridBagConstraints);
        queueRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_AdminObject_Queue")); // NOI18N

        buttonGroup1.add(topicRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(topicRadioButton, bundle.getString("LBL_AdminObject_Topic")); // NOI18N
        topicRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topicRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 10, 30);
        jPanel3.add(topicRadioButton, gridBagConstraints);
        topicRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_AdminObject_Topic")); // NOI18N

        adObjLabel.setText(bundle.getString("LBL_jms-admin-object")); // NOI18N
        adObjLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel3.add(adObjLabel, gridBagConstraints);
        adObjLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("jms-admin-object_Description")); // NOI18N

        connLabel.setText(bundle.getString("LBL_jms-connector")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel3.add(connLabel, gridBagConstraints);
        connLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("jms-connector_Description")); // NOI18N

        buttonGroup1.add(connectionFactoryRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(connectionFactoryRadioButton, bundle.getString("LBL_Connector_ConnectionFactory")); // NOI18N
        connectionFactoryRadioButton.setToolTipText(bundle.getString("ToolTip_Connector_ConnectionFactory")); // NOI18N
        connectionFactoryRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionFactoryRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 10, 30);
        jPanel3.add(connectionFactoryRadioButton, gridBagConstraints);
        connectionFactoryRadioButton.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_Connector_ConnectionFactory")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        add(jPanel3, gridBagConstraints);
        jPanel3.getAccessibleContext().setAccessibleName(bundle.getString("LBL_GeneralAttributes_JMS")); // NOI18N
        jPanel3.getAccessibleContext().setAccessibleDescription(bundle.getString("jms-resource_Description")); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setText(bundle.getString("jms-resource_Description")); // NOI18N
        jTextArea1.setFocusable(false);
        jTextArea1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jTextArea1, gridBagConstraints);
        jTextArea1.getAccessibleContext().setAccessibleName(bundle.getString("LBL_GeneralAttributes_JMS")); // NOI18N
        jTextArea1.getAccessibleContext().setAccessibleDescription(bundle.getString("jms-resource_Description")); // NOI18N

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jndiNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jndiNameFieldActionPerformed(evt);
            }
        });
        jndiNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jndiNameFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(jndiNameField, gridBagConstraints);
        jndiNameField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_jndi-name")); // NOI18N
        jndiNameField.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_jndi-name")); // NOI18N

        initFields();
        enabledComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(enabledComboBox, gridBagConstraints);
        enabledComboBox.getAccessibleContext().setAccessibleName(bundle.getString("LBL_enabled")); // NOI18N
        enabledComboBox.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_enabled")); // NOI18N

        descField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descFieldActionPerformed(evt);
            }
        });
        descField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                descFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(descField, gridBagConstraints);
        descField.getAccessibleContext().setAccessibleName(bundle.getString("LBL_description")); // NOI18N
        descField.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_description")); // NOI18N

        jndiNameLabel.setLabelFor(jndiNameField);
        org.openide.awt.Mnemonics.setLocalizedText(jndiNameLabel, bundle.getString("LBL_jndi-name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(jndiNameLabel, gridBagConstraints);
        jndiNameLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_jndi-name")); // NOI18N

        enabledLabel.setLabelFor(enabledComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(enabledLabel, bundle.getString("LBL_enabled")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(enabledLabel, gridBagConstraints);
        enabledLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_enabled")); // NOI18N

        descLabel.setLabelFor(descField);
        org.openide.awt.Mnemonics.setLocalizedText(descLabel, bundle.getString("LBL_description")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel2.add(descLabel, gridBagConstraints);
        descLabel.getAccessibleContext().setAccessibleDescription(bundle.getString("ToolTip_description")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        add(jPanel2, gridBagConstraints);
        jPanel2.getAccessibleContext().setAccessibleName(bundle.getString("LBL_GeneralAttributes_JMS")); // NOI18N
        jPanel2.getAccessibleContext().setAccessibleDescription(bundle.getString("jms-resource_Description")); // NOI18N

        getAccessibleContext().setAccessibleName(bundle.getString("LBL_GeneralAttributes_JMS")); // NOI18N
        getAccessibleContext().setAccessibleDescription(bundle.getString("jms-resource_Description")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void connectionFactoryRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionFactoryRadioButtonActionPerformed
        this.helper.getData().setString(__ResType, bundle.getString("LBL_Connector_ConnectionFactory")); //NOI18N
        this.helper.getData().setProperties(new Vector());
        this.panel.fireChange(evt.getSource());    
    }//GEN-LAST:event_connectionFactoryRadioButtonActionPerformed

    private void topicConnectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topicConnectionRadioButtonActionPerformed
        this.helper.getData().setString(__ResType, bundle.getString("LBL_Connector_TopicConnectionFactory")); //NOI18N
        this.helper.getData().setProperties(new Vector());
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_topicConnectionRadioButtonActionPerformed

    private void queueConnectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queueConnectionRadioButtonActionPerformed
        this.helper.getData().setString(__ResType, bundle.getString("LBL_Connector_QueueConnectionFactory")); //NOI18N
        this.helper.getData().setProperties(new Vector());
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_queueConnectionRadioButtonActionPerformed

    private void topicRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topicRadioButtonActionPerformed
        this.helper.getData().setString(__ResType, bundle.getString("LBL_AdminObject_Topic")); //NOI18N
        setPropsForAdminObj();
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_topicRadioButtonActionPerformed

    private void queueRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queueRadioButtonActionPerformed
        this.helper.getData().setString(__ResType, bundle.getString("LBL_AdminObject_Queue")); //NOI18N
        setPropsForAdminObj();
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_queueRadioButtonActionPerformed

    private void descFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_descFieldKeyReleased
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__Description);
        String newValue = descField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__Description, newValue);
        }
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_descFieldKeyReleased

    private void descFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descFieldActionPerformed
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__Description);
        String newValue = descField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__Description, newValue);
            this.panel.fireChange(evt.getSource());
        }
        
        if((this.getRootPane().getDefaultButton() != null) && (this.getRootPane().getDefaultButton().isEnabled())){
            this.getRootPane().getDefaultButton().doClick();
        }
    }//GEN-LAST:event_descFieldActionPerformed

        
    private void enabledComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledComboBoxActionPerformed
        String newValue = (String)enabledComboBox.getSelectedItem();
        this.helper.getData().setString(__Enabled, newValue);
    }//GEN-LAST:event_enabledComboBoxActionPerformed

    private void jndiNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jndiNameFieldKeyReleased
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__JndiName);
        String newValue = jndiNameField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__JndiName, newValue);
        }
        this.panel.fireChange(evt.getSource());
    }//GEN-LAST:event_jndiNameFieldKeyReleased

    private void jndiNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jndiNameFieldActionPerformed
        ResourceConfigData data = this.helper.getData();
        String value = data.getString(__JndiName);
        String newValue = jndiNameField.getText();
        if (!value.equals(newValue)) {
            this.helper.getData().setString(__JndiName, newValue);
            this.panel.fireChange(evt.getSource());
        }
        
        if((this.getRootPane().getDefaultButton() != null) && (this.getRootPane().getDefaultButton().isEnabled())){
            this.getRootPane().getDefaultButton().doClick();
        }
    }//GEN-LAST:event_jndiNameFieldActionPerformed

    public void stateChanged(javax.swing.event.ChangeEvent e) {
    }    
    
     public void refreshFields() {
        ResourceConfigData data = this.helper.getData();
        String jndiNameVal = (String)jndiNameField.getText();
        String descVal = (String)descField.getText();
        String enabledVal = (String)enabledComboBox.getSelectedItem();
        
        fields = groups[0].getField();
        for(int j=0; j<fields.length; j++){
            String fieldName = fields[j].getName();
            Object value = data.get(fieldName);
            if (value == null) {
                value = FieldHelper.getDefaultValue(fields[j]);
                data.set(fieldName, value);
            }
            
            String defValue = (String)value;
            if(FieldHelper.isList(fields[j])){
               if(! enabledVal.equals(defValue)){
                    enabledComboBox.setSelectedItem(defValue);
                }
            }else{
                if(fieldName.equals("jndi-name") && (! jndiNameVal.equals(defValue)) ){ //NOI18N
                    String targetFile = data.getTargetFile();
                    if(targetFile != null){
                        jndiNameField.setText(targetFile);
                    }else
                        jndiNameField.setText(defValue);
                }else if(! jndiNameVal.equals(defValue)){
                    descField.setText(defValue);
                }
            }
        }
        
        String isResTypeSelected = this.helper.getData().getString(__ResType);
        if(isResTypeSelected == null || isResTypeSelected.trim().equals("") ){//NOI18N
            this.queueRadioButton.setSelected(true);
            this.helper.getData().setString(__ResType, bundle.getString("LBL_AdminObject_Queue")); //NOI18N
            setPropsForAdminObj();
        }
     }
     
     public JMSWizardVisualPanel setFirstTime(boolean first) {
         //this.firstTime = first;
         return this;
     }
     
     private void initFields(){
         fields = groups[0].getField();
         for(int j=0; j<fields.length; j++){
             if(FieldHelper.isList(fields[j])){
                 String tags[] = FieldHelper.getTags(fields[j]);
                 for (int h = 0; h < tags.length; h++) {
                     enabledComboBox.addItem(tags[h]);
                 }
             }
         }//for
     }
     
     public void setHelper(ResourceConfigHelper helper){
         this.helper = helper;
         this.helper.getData().setString("jndi-name", helper.getData().getTargetFile()); //NOI18N
         refreshFields();
     }
     
     private void setPropsForAdminObj() {
        ResourceConfigData data = this.helper.getData();
        data.setProperties(new Vector());
        data.addProperty(__AdminObjPropertyName, ""); //NOI18N
    }
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adObjLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel chooseResLabel;
    private javax.swing.JLabel connLabel;
    private javax.swing.JRadioButton connectionFactoryRadioButton;
    private javax.swing.JTextField descField;
    private javax.swing.JLabel descLabel;
    private javax.swing.JComboBox enabledComboBox;
    private javax.swing.JLabel enabledLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jndiNameField;
    private javax.swing.JLabel jndiNameLabel;
    private javax.swing.JRadioButton queueConnectionRadioButton;
    private javax.swing.JRadioButton queueRadioButton;
    private javax.swing.JRadioButton topicConnectionRadioButton;
    private javax.swing.JRadioButton topicRadioButton;
    // End of variables declaration//GEN-END:variables
    
}
