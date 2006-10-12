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
 * OneOneFinderDialogPanel.java        November 3, 2003, 1:58 PM
 *
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.ejbmodule;

import java.util.ResourceBundle;

import org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.ValidationSupport;
import org.netbeans.modules.j2ee.sun.share.Constants;

//AWT
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 *
 * @author  Rajeshwar Patil
 * @version %I%, %G%
 */
public class OneOneFinderDialogPanel extends javax.swing.JPanel {

    String methodName;
    String queryParams;
    String queryFilter;
    String queryVariables;
    String queryOrdering;
    
  
    // Validation support
    ValidationSupport validationSupport;

    /** Creates new form OneOneFinderDialogPanel */
    public OneOneFinderDialogPanel() {
        initComponents();

        validationSupport = new ValidationSupport();
        ///markRequiredFields();
    }


    public OneOneFinderDialogPanel(Object[] values) {
        initComponents();

        validationSupport = new ValidationSupport();
        ///markRequiredFields();

        methodName = (String)values[0];
        queryParams = (String)values[1];
        queryFilter = (String)values[2];
        queryVariables = (String)values[3];
        queryOrdering = (String)values[4];
        setComponentValues();
    }


    private void setComponentValues(){
        methodNameTextField.setText(methodName);
        queryParamsTextField.setText(queryParams);
        queryFilterTextField.setText(queryFilter);
        queryVariablesTextField.setText(queryVariables);
        queryOrderingTextField.setText(queryOrdering);
    }


    public String getMethodName(){
        return methodName;
    }


    public String getQueryParams(){
        return queryParams;
    }


    public String getQueryFilter(){
        return queryFilter;
    }


    public String getQueryVariables(){
        return queryVariables;
    }


    public String getQueryOrdering(){
        return queryOrdering;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        methodNameReqFlag = new javax.swing.JLabel();
        methodNameLabel = new javax.swing.JLabel();
        methodNameTextField = new javax.swing.JTextField();
        queryParamsLabel = new javax.swing.JLabel();
        queryParamsTextField = new javax.swing.JTextField();
        queryFilterLabel = new javax.swing.JLabel();
        queryFilterTextField = new javax.swing.JTextField();
        queryVariablesLabel = new javax.swing.JLabel();
        queryVariablesTextField = new javax.swing.JTextField();
        queryOrderingLabel = new javax.swing.JLabel();
        queryOrderingTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        methodNameReqFlag.setLabelFor(methodNameTextField);
        methodNameReqFlag.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/common/Bundle").getString("LBL_RequiredMark"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(methodNameReqFlag, gridBagConstraints);

        methodNameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Method_Name").charAt(0));
        methodNameLabel.setLabelFor(methodNameTextField);
        methodNameLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Method_Name_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(methodNameLabel, gridBagConstraints);
        methodNameLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Method_Name_Acsbl_Name"));
        methodNameLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Method_Name_Acsbl_Desc"));

        methodNameTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Method_Name_Tool_Tip"));
        methodNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                methodNameKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(methodNameTextField, gridBagConstraints);
        methodNameTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Method_Name_Acsbl_Name"));
        methodNameTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Method_Name_Acsbl_Desc"));

        queryParamsLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Query_Params").charAt(0));
        queryParamsLabel.setLabelFor(queryParamsTextField);
        queryParamsLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Query_Params_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(queryParamsLabel, gridBagConstraints);
        queryParamsLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Params_Acsbl_Name"));
        queryParamsLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Params_Acsbl_Desc"));

        queryParamsTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Params_Tool_Tip"));
        queryParamsTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                queryParamsKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 72;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(queryParamsTextField, gridBagConstraints);
        queryParamsTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Params_Acsbl_Name"));
        queryParamsTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Params_Acsbl_Desc"));

        queryFilterLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Query_Filter").charAt(0));
        queryFilterLabel.setLabelFor(queryFilterTextField);
        queryFilterLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Query_Filter_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(queryFilterLabel, gridBagConstraints);
        queryFilterLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Filter_Acsbl_Name"));
        queryFilterLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Filter_Acsbl_Desc"));

        queryFilterTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Filter_Tool_Tip"));
        queryFilterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                queryFilterKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 72;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(queryFilterTextField, gridBagConstraints);
        queryFilterTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Filter_Acsbl_Name"));
        queryFilterTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Filter_Acsbl_Desc"));

        queryVariablesLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Query_Variables").charAt(0));
        queryVariablesLabel.setLabelFor(queryVariablesTextField);
        queryVariablesLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Query_Variables_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        add(queryVariablesLabel, gridBagConstraints);
        queryVariablesLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Variables_Acsbl_Name"));
        queryVariablesLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Variables_Acsbl_Desc"));

        queryVariablesTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Variables_Tool_Tip"));
        queryVariablesTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                queryVariablesKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 72;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 5);
        add(queryVariablesTextField, gridBagConstraints);
        queryVariablesTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Variables_Acsbl_Name"));
        queryVariablesTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Variables_Acsbl_Desc"));

        queryOrderingLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("MNC_Query_Ordering").charAt(0));
        queryOrderingLabel.setLabelFor(queryOrderingTextField);
        queryOrderingLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("LBL_Query_Ordering_1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        add(queryOrderingLabel, gridBagConstraints);
        queryOrderingLabel.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Ordering_Acsbl_Name"));
        queryOrderingLabel.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Ordering_Acsbl_Desc"));

        queryOrderingTextField.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Ordering_Tool_Tip"));
        queryOrderingTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                queryOrderingKeyReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 72;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(queryOrderingTextField, gridBagConstraints);
        queryOrderingTextField.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Ordering_Acsbl_Name"));
        queryOrderingTextField.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/share/configbean/customizers/ejbmodule/Bundle").getString("Query_Ordering_Acsbl_Desc"));

    }// </editor-fold>//GEN-END:initComponents

    private void queryOrderingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_queryOrderingKeyReleased
        // Add your handling code here:
        // get the text from the field
        queryOrdering = queryOrderingTextField.getText();
        firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_queryOrderingKeyReleased

    private void queryVariablesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_queryVariablesKeyReleased
        // Add your handling code here:
        // get the text from the field
        queryVariables = queryVariablesTextField.getText();
        firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_queryVariablesKeyReleased

    private void queryFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_queryFilterKeyReleased
        // Add your handling code here:
        // get the text from the field
        queryFilter = queryFilterTextField.getText();
        firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_queryFilterKeyReleased

    private void queryParamsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_queryParamsKeyReleased
        // Add your handling code here:
        // get the text from the field
        queryParams = queryParamsTextField.getText();
        firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_queryParamsKeyReleased

    private void methodNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_methodNameKeyReleased
        // Add your handling code here:
        // get the text from the field
        methodName = methodNameTextField.getText();
        firePropertyChange(Constants.USER_DATA_CHANGED, null, null);
    }//GEN-LAST:event_methodNameKeyReleased
    
    //This method appends "*  "  to the label of the field, if it is a mandatory field.
    private void markRequiredFields(){
        if(validationSupport.isRequiredProperty("/sun-ejb-jar/enterprise-beans/ejb/cmp/one-one-finders/finder/method-name")){  //NOI18N
            methodNameLabel.setText(validationSupport.getMarkedLabel(methodNameLabel.getText()));
        }

        if(validationSupport.isRequiredProperty("/sun-ejb-jar/enterprise-beans/ejb/cmp/one-one-finders/finder/query-params")){  //NOI18N
            queryParamsLabel.setText(validationSupport.getMarkedLabel(queryParamsLabel.getText()));
        }

        if(validationSupport.isRequiredProperty("/sun-ejb-jar/enterprise-beans/ejb/cmp/one-one-finders/finder/query-filter")){  //NOI18N
            queryFilterLabel.setText(validationSupport.getMarkedLabel(queryFilterLabel.getText()));
        }

        if(validationSupport.isRequiredProperty("/sun-ejb-jar/enterprise-beans/ejb/cmp/one-one-finders/finder/query-variables")){  //NOI18N
            queryVariablesLabel.setText(validationSupport.getMarkedLabel(queryVariablesLabel.getText()));
        }

        if(validationSupport.isRequiredProperty("/sun-ejb-jar/enterprise-beans/ejb/cmp/one-one-finders/finder/query-ordering")){  //NOI18N
            queryOrderingLabel.setText(validationSupport.getMarkedLabel(queryOrderingLabel.getText()));
        }
    }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel methodNameLabel;
    private javax.swing.JLabel methodNameReqFlag;
    private javax.swing.JTextField methodNameTextField;
    private javax.swing.JLabel queryFilterLabel;
    private javax.swing.JTextField queryFilterTextField;
    private javax.swing.JLabel queryOrderingLabel;
    private javax.swing.JTextField queryOrderingTextField;
    private javax.swing.JLabel queryParamsLabel;
    private javax.swing.JTextField queryParamsTextField;
    private javax.swing.JLabel queryVariablesLabel;
    private javax.swing.JTextField queryVariablesTextField;
    // End of variables declaration//GEN-END:variables
    
}
