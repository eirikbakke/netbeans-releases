/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tools.generator;

import java.awt.*;
import javax.swing.*;

/**
 * A panel allowing to customize SAX generator i.e. setting file locations and element mapping.
 * <p>
 * The UI performs name checks and colors appropriate input fields giving a user feedback.
 *
 * @author  Petr Kuzel
 * @version 1.0
 */
public class SAXGeneratorFilePanel extends SAXGeneratorAbstractPanel implements java.awt.event.ActionListener {

    /** Serial Version UID */
    private static final long serialVersionUID =-8950908568784619306L;  
        
    private static final Util.NameCheck check = Util.JAVA_CHECK;
    
    /** Creates new form SAXGeneratorCustomizer */
    public SAXGeneratorFilePanel() {
    }
    
    private final ValidatingTextField.Validator NAME_VALIDATOR = new ValidatingTextField.Validator() {
        public boolean isValid(String value) {
            boolean ret = check.checkName(value);
            if (ret) {
                setValid(checkNames());
            } else {
                setValid(false);
            }
            
            return ret;
        }
        
        public String getReason() {
            return Util.getString("MSG_file_err_1");
        }
    };
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        interfaceLabel = new javax.swing.JLabel();
        interfaceTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        handlerImplLabel = new javax.swing.JLabel();
        handlerImplTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        stubLabel = new javax.swing.JLabel();
        stubTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        parsletLabel = new javax.swing.JLabel();
        parsletTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        parsletImplLabel = new javax.swing.JLabel();
        parsletImplTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        saveCheckBox = new javax.swing.JCheckBox();
        saveLabel = new javax.swing.JLabel();
        saveTextField = new org.netbeans.modules.xml.tools.generator.ValidatingTextField();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        setName(Util.getString("SAXGeneratorFilePanel.Form.name"));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        
        interfaceLabel.setText(Util.getString("SAXGeneratorCustomizer.interfaceLabel.text"));
        interfaceLabel.setForeground(java.awt.Color.black);
        interfaceLabel.setLabelFor(interfaceTextField);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(interfaceLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        add(interfaceTextField, gridBagConstraints1);
        
        handlerImplLabel.setText(Util.getString("SAXGeneratorFilePanel.handlerImplLabel.text"));
        handlerImplLabel.setForeground(java.awt.Color.black);
        handlerImplLabel.setLabelFor(handlerImplTextField);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(handlerImplLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints1.weightx = 1.0;
        add(handlerImplTextField, gridBagConstraints1);
        
        stubLabel.setText(Util.getString("SAXGeneratorCustomizer.stubLabel.text"));
        stubLabel.setForeground(java.awt.Color.black);
        stubLabel.setLabelFor(stubTextField);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(stubLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        add(stubTextField, gridBagConstraints1);
        
        parsletLabel.setText(Util.getString("SAXGeneratorCustomizer.parsletLabel.text"));
        parsletLabel.setForeground(java.awt.Color.black);
        parsletLabel.setLabelFor(parsletTextField);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(parsletLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        add(parsletTextField, gridBagConstraints1);
        
        parsletImplLabel.setText(Util.getString("SAXGeneratorFilePanel.parsletImplLabel.text"));
        parsletImplLabel.setForeground(java.awt.Color.gray);
        parsletImplLabel.setLabelFor(parsletImplTextField);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(parsletImplLabel, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints1.weightx = 1.0;
        add(parsletImplTextField, gridBagConstraints1);
        
        saveCheckBox.setText(Util.getString("PROP_save_it"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(saveCheckBox, gridBagConstraints1);
        
        saveLabel.setText(Util.getString("PROP_bindings_label"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints1.weighty = 1.0;
        add(saveLabel, gridBagConstraints1);
        
        saveTextField.setEditable(false);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints1.weightx = 1.0;
        add(saveTextField, gridBagConstraints1);
        
    }//GEN-END:initComponents
        
    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        updateModel();  //??? does not occur
    }//GEN-LAST:event_formComponentHidden
  
    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
        updateModel();  //??? does not occur
    }//GEN-LAST:event_formComponentRemoved
  
    protected void updateModel() {
        model.setHandler(interfaceTextField.getText());
        model.setParslet(parsletTextField.getText());
        model.setStub(stubTextField.getText());
        model.setParsletImpl(parsletImplTextField.getText());
        model.setHandlerImpl(handlerImplTextField.getText());
        model.setBindnings(saveCheckBox.isSelected() ? saveTextField.getText() : null);
    }
  
  
    protected void initView() {
        initComponents();
      
        //**** set mnemonics
        interfaceLabel.setDisplayedMnemonic(Util.getChar("SAXGeneratorCustomizer.interfaceLabel.mne")); // NOI18N
        stubLabel.setDisplayedMnemonic(Util.getChar("SAXGeneratorCustomizer.stubLabel.mne")); // NOI18N
        handlerImplLabel.setDisplayedMnemonic(Util.getChar("SAXGeneratorFilePanel.handlerImplLabel.mne")); // NOI18N
        parsletLabel.setDisplayedMnemonic(Util.getChar("SAXGeneratorCustomizer.parsletLabel.mne")); // NOI18N
        parsletImplLabel.setDisplayedMnemonic(Util.getChar("SAXGeneratorFilePanel.parsletImplLabel.mne")); // NOI18N
        //****
              
        interfaceTextField.setText(model.getHandler());
        stubTextField.setText(model.getStub());
        handlerImplTextField.setText(model.getHandlerImpl());        
        parsletTextField.setText(model.getParslet());
        parsletImplTextField.setText(model.getParsletImpl());
        saveTextField.setText(model.getBindings());

        interfaceTextField.setValidator(NAME_VALIDATOR);
        stubTextField.setValidator(NAME_VALIDATOR);
        handlerImplTextField.setValidator(NAME_VALIDATOR);
        parsletTextField.setValidator(NAME_VALIDATOR);
        parsletImplTextField.setValidator(NAME_VALIDATOR);
        
        saveCheckBox.setSelected(model.getBindings() != null);
    }
  
    protected void updateView() {
        
        parsletTextField.setVisible(hasParslets());
        parsletLabel.setVisible(hasParslets());
        parsletImplTextField.setVisible(hasParslets());
        parsletImplLabel.setVisible(hasParslets());
        
        doLayout();
        setValid(checkNames());
        
    }
  
    /** Get notified by DialigDescriptor */
    public void actionPerformed(java.awt.event.ActionEvent p1) {
        updateModel();
    }
  
    //
    // Check all entered names
    //
    private boolean checkNames() {

        return  check.checkName(interfaceTextField.getText()) 
                && (not(hasParslets()) || check.checkName(parsletTextField.getText()))
                && check.checkName(stubTextField.getText())
                && (not(hasParslets()) || check.checkName(parsletImplTextField.getText()))
                && check.checkName(handlerImplTextField.getText());
        
    }

    private boolean hasParslets() {
        return (model.getParsletBindings().isEmpty() == false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel interfaceLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField interfaceTextField;
    private javax.swing.JLabel handlerImplLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField handlerImplTextField;
    private javax.swing.JLabel stubLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField stubTextField;
    private javax.swing.JLabel parsletLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField parsletTextField;
    private javax.swing.JLabel parsletImplLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField parsletImplTextField;
    private javax.swing.JCheckBox saveCheckBox;
    private javax.swing.JLabel saveLabel;
    private org.netbeans.modules.xml.tools.generator.ValidatingTextField saveTextField;
    // End of variables declaration//GEN-END:variables

}
