/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seproject.ui.customizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.modules.java.j2seproject.J2SEProjectUtil;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/** Customizer for general project attributes.
 *
 * @author  phrebejk
 */
public class CustomizerJar extends JPanel implements J2SECustomizer.Panel, HelpCtx.Provider {
    
    private VisualPropertySupport vps;
        
    /** Creates new form CustomizerCompile */
    public CustomizerJar(  J2SEProjectProperties j2seProperties  ) {
        initComponents();        
        vps = new VisualPropertySupport( j2seProperties );
    }
    
    public void initValues() {
        
        vps.register( jTextFieldDistDir, J2SEProjectProperties.DIST_JAR );
        vps.register( jTextFieldExcludes, J2SEProjectProperties.BUILD_CLASSES_EXCLUDES );
        vps.register( jCheckBoxCommpress, J2SEProjectProperties.JAR_COMPRESS ); 
    } 
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx( CustomizerJar.class );
    }
        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelDistDir = new javax.swing.JLabel();
        jTextFieldDistDir = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldExcludes = new javax.swing.JTextField();
        jCheckBoxCommpress = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EtchedBorder());
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDistDir, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_DistDir_JTextField"));
        jLabelDistDir.setLabelFor(jTextFieldDistDir);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jLabelDistDir, gridBagConstraints);

        jTextFieldDistDir.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        add(jTextFieldDistDir, gridBagConstraints);
        jTextFieldDistDir.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/customizer/Bundle").getString("AD_jTextFieldDistDir"));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Excludes_JTextField"));
        jLabel2.setLabelFor(jTextFieldExcludes);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jTextFieldExcludes, gridBagConstraints);
        jTextFieldExcludes.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/customizer/Bundle").getString("AD_jTextFieldExcludes"));

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxCommpress, org.openide.util.NbBundle.getMessage(CustomizerJar.class, "LBL_CustomizeJar_Commpres_JCheckBox"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(jCheckBoxCommpress, gridBagConstraints);
        jCheckBoxCommpress.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/customizer/Bundle").getString("AD_jCheckBoxCompress"));

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxCommpress;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDistDir;
    private javax.swing.JTextField jTextFieldDistDir;
    private javax.swing.JTextField jTextFieldExcludes;
    // End of variables declaration//GEN-END:variables
        
    // Storing methods ---------------------------------------------------------
    
    /** Stores the value according to the src component into the helper
     */
    private void store( JComponent src ) {
    
        /*
        if ( src == jTextFieldSrcDir ) {
            j2seProperties.put( J2SEProjectProperties.SRC_DIR, jTextFieldSrcDir.getText() );
        }
        else if ( src == jTextFieldBuildDir ) {
            j2seProperties.put( J2SEProjectProperties.BUILD_DIR, jTextFieldBuildDir.getText() );
        }
        else if ( src == jListClasspath ) {
            
            List elements = new ArrayList( classpathModel.size() );
            
            for ( Enumeration e = classpathModel.elements(); e.hasMoreElements(); ) {
                elements.add( e.nextElement() );
            }
            j2seProperties.put( J2SEProjectProperties.JAVAC_CLASSPATH, elements );
        }
        
        assert true : "CustomizerCompile - Unknown component : " + src; // NOI18N
        */
    } 
    
    
    
    // Private methods for classpath data manipulation -------------------------
        
}
