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

package org.netbeans.beaninfo.editors;

import java.awt.Rectangle;
import java.util.ResourceBundle;

import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;

/**
*
* @author   Ian Formanek
* @version  1.00, 01 Sep 1998
*/
public class RectangleCustomEditor extends javax.swing.JPanel implements EnhancedCustomPropertyEditor {

    // the bundle to use
    static ResourceBundle bundle = NbBundle.getBundle (
                                       RectangleCustomEditor.class);

    static final long serialVersionUID =-9015667991684634296L;
    /** Initializes the Form */
    public RectangleCustomEditor(RectangleEditor editor) {
        initComponents ();
        this.editor = editor;
        Rectangle rectangle = (Rectangle)editor.getValue ();
        if (rectangle == null) rectangle = new Rectangle (0, 0, 0, 0);
        xField.setText (""+rectangle.x); // NOI18N
        yField.setText (""+rectangle.y); // NOI18N
        widthField.setText (""+rectangle.width); // NOI18N
        heightField.setText (""+rectangle.height); // NOI18N

        setBorder (new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5)));
        jPanel2.setBorder (new javax.swing.border.CompoundBorder (
                               new javax.swing.border.TitledBorder (
                                   new javax.swing.border.EtchedBorder (),
                                   " " + bundle.getString ("CTL_Rectangle") + " "),
                               new javax.swing.border.EmptyBorder (new java.awt.Insets(5, 5, 5, 5))));

        xLabel.setText (bundle.getString ("CTL_X"));
        yLabel.setText (bundle.getString ("CTL_Y"));
        widthLabel.setText (bundle.getString ("CTL_Width"));
        heightLabel.setText (bundle.getString ("CTL_Height"));
        HelpCtx.setHelpIDString (this, RectangleCustomEditor.class.getName ());
    }

    public java.awt.Dimension getPreferredSize () {
        return new java.awt.Dimension (280, 160);
    }

    public Object getPropertyValue () throws IllegalStateException {
        try {
            int x = Integer.parseInt (xField.getText ());
            int y = Integer.parseInt (yField.getText ());
            int width = Integer.parseInt (widthField.getText ());
            int height = Integer.parseInt (heightField.getText ());
            if ((x < 0) || (y < 0) || (width < 0) || (height < 0)) {
                IllegalStateException ise = new IllegalStateException();
                TopManager.getDefault().getErrorManager().annotate(
                    ise, ErrorManager.ERROR, null, 
                    bundle.getString("CTL_NegativeSize"), null, null);
                throw ise;
            }
            return new Rectangle (x, y, width, height);
        } catch (NumberFormatException e) {
            IllegalStateException ise = new IllegalStateException();
            TopManager.getDefault().getErrorManager().annotate(
                ise, ErrorManager.ERROR, null, 
                bundle.getString("CTL_InvalidValue"), null, null);
            throw ise;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        setLayout (new java.awt.BorderLayout ());

        jPanel2 = new javax.swing.JPanel ();
        jPanel2.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        xLabel = new javax.swing.JLabel ();
        xLabel.setText (null);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add (xLabel, gridBagConstraints1);

        xField = new javax.swing.JTextField ();
        xField.addActionListener (new java.awt.event.ActionListener () {
                                      public void actionPerformed (java.awt.event.ActionEvent evt) {
                                          updateRectangle (evt);
                                      }
                                  }
                                 );

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
        gridBagConstraints1.weightx = 1.0;
        jPanel2.add (xField, gridBagConstraints1);

        yLabel = new javax.swing.JLabel ();
        yLabel.setText (null);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add (yLabel, gridBagConstraints1);

        yField = new javax.swing.JTextField ();
        yField.addActionListener (new java.awt.event.ActionListener () {
                                      public void actionPerformed (java.awt.event.ActionEvent evt) {
                                          updateRectangle (evt);
                                      }
                                  }
                                 );

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
        gridBagConstraints1.weightx = 1.0;
        jPanel2.add (yField, gridBagConstraints1);

        widthLabel = new javax.swing.JLabel ();
        widthLabel.setText (null);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add (widthLabel, gridBagConstraints1);

        widthField = new javax.swing.JTextField ();
        widthField.addActionListener (new java.awt.event.ActionListener () {
                                          public void actionPerformed (java.awt.event.ActionEvent evt) {
                                              updateRectangle (evt);
                                          }
                                      }
                                     );

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
        gridBagConstraints1.weightx = 1.0;
        jPanel2.add (widthField, gridBagConstraints1);

        heightLabel = new javax.swing.JLabel ();
        heightLabel.setText (null);

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add (heightLabel, gridBagConstraints1);

        heightField = new javax.swing.JTextField ();
        heightField.addActionListener (new java.awt.event.ActionListener () {
                                           public void actionPerformed (java.awt.event.ActionEvent evt) {
                                               updateRectangle (evt);
                                           }
                                       }
                                      );

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 4, 0);
        gridBagConstraints1.weightx = 1.0;
        jPanel2.add (heightField, gridBagConstraints1);


        add (jPanel2, "Center"); // NOI18N

    }//GEN-END:initComponents


    private void updateRectangle (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRectangle
        try {
            int x = Integer.parseInt (xField.getText ());
            int y = Integer.parseInt (yField.getText ());
            int width = Integer.parseInt (widthField.getText ());
            int height = Integer.parseInt (heightField.getText ());
            editor.setValue (new Rectangle (x, y, width, height));
        } catch (NumberFormatException e) {
            // [PENDING beep]
        }
    }//GEN-LAST:event_updateRectangle


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel xLabel;
    private javax.swing.JTextField xField;
    private javax.swing.JLabel yLabel;
    private javax.swing.JTextField yField;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JTextField widthField;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JTextField heightField;
    // End of variables declaration//GEN-END:variables

    private RectangleEditor editor;

}

