/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.image;


import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.openide.util.NbBundle;


/** This class define a panel for "Custom zoom" dialog.
 *
 * @author  Lukas Tadial
 */
public class CustomZoomPanel extends JPanel {
    
    /** Creates new form CustomZoomPane */
    public CustomZoomPanel() {
        initComponents();
        initAccessibility();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        enlargeLabel = new javax.swing.JLabel();
        enlargeLabel.setDisplayedMnemonic((NbBundle.getBundle(CustomZoomPanel.class).getString("LBL_EnlargeFactor_Mnem")).charAt(0));
        
        enlargeText = new javax.swing.JTextField();
        decreasingLabel = new javax.swing.JLabel();
        decreasingLabel.setDisplayedMnemonic((NbBundle.getBundle(CustomZoomPanel.class).getString("LBL_DecreaseFactor_Mnem")).charAt(0));
        
        decreaseText = new javax.swing.JTextField();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        enlargeLabel.setText(NbBundle.getBundle(CustomZoomPanel.class).getString("LBL_EnlargeFactor"));
        enlargeLabel.setLabelFor(enlargeText);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(enlargeLabel, gridBagConstraints1);
        
        enlargeText.setDocument(new WholeNumberDocument());
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 11, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        add(enlargeText, gridBagConstraints1);
        
        decreasingLabel.setText(NbBundle.getBundle(CustomZoomPanel.class).getString("LBL_DecreaseFactor"));
        decreasingLabel.setLabelFor(decreaseText);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 11, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(decreasingLabel, gridBagConstraints1);
        
        decreaseText.setDocument(new WholeNumberDocument());
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(5, 11, 11, 11);
        gridBagConstraints1.weightx = 1.0;
        add(decreaseText, gridBagConstraints1);
        
    }//GEN-END:initComponents
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CustomZoomPanel.class).getString("ACSD_CustomZoomPanel"));
        enlargeText.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CustomZoomPanel.class).getString("ACS_EnlargeText"));
        decreaseText.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CustomZoomPanel.class).getString("ACS_DecreaseText"));
        
    }
    
    public int getEnlargeFactor() {
        return Integer.parseInt(enlargeText.getText());
    }
    
    public void setEnlargeFactor(int enlargeFactor) {
        enlargeText.setText("" + enlargeFactor); // NOI18N
    }
    
    public int getDecreaseFactor() {
        return Integer.parseInt(decreaseText.getText());
    } 
    
    public void setDecreaseFactor(int decreaseFactor) {
        decreaseText.setText("" + decreaseFactor); // NOI18N
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel enlargeLabel;
    private javax.swing.JTextField enlargeText;
    private javax.swing.JLabel decreasingLabel;
    private javax.swing.JTextField decreaseText;
    // End of variables declaration//GEN-END:variables
    

    /** Documnet which accepts only digit chars. */
    private static class WholeNumberDocument extends PlainDocument {

        /** Overrides superclass method. */
        public void insertString(int offs, String str, AttributeSet a) 
        throws BadLocationException {
             char[] source = str.toCharArray();
             StringBuffer result = new StringBuffer();
             
             for(int i=0; i<source.length; i++) {
                 if(Character.isDigit(source[i])) {
                     result.append(source[i]);
                 } else { 
                     if(Boolean.getBoolean("netbeans.debug.excpetions")) // NOI18N
                         System.err.println("Image: Trying insert non-digit in custom zoom action."); // NOI18N
                 }
             }
             
             // There has to be some number added.
             if(result.length() == 0)
                 return;
             
             super.insertString(offs, result.toString(), a);
         }
         
     } // End of nested class WholeNumberDocument. 

    
}
