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
package org.netbeans.modules.xml.catalog.impl;

import java.beans.*;

/**
 * Customizer for Netbeans IDE catalog is read only because the catalog
 * can be modified just by modules using OpenIDE API.
 *
 * @author  Petr Kuzel
 * @version 1.0
 */
public class SystemCatalogCustomizer extends javax.swing.JPanel implements Customizer {

    /** Serial Version UID */
    private static final long serialVersionUID = -7117054881250295623L;    

    /** Creates new form CatalogCustomizer */
    public SystemCatalogCustomizer() {
        initComponents ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jTextArea1 = new javax.swing.JTextArea(){
            public boolean isFocusTraversable(){
                return false;
            }
        };
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        jTextArea1.setEditable(false);
        jTextArea1.setText(Util.getString("SystemCatalogCustomizer.readOnly.text"));
        jTextArea1.setBorder(null);
        jTextArea1.setOpaque(false);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jTextArea1, gridBagConstraints1);
        
    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables


    public void setObject(final java.lang.Object peer) {
    }
    
    public void addPropertyChangeListener(final java.beans.PropertyChangeListener p1) {
    }
    
    public void removePropertyChangeListener(final java.beans.PropertyChangeListener p1) {
    }
}
