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

package org.netbeans.modules.web.project.ui.customizer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.ErrorManager;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class CustomizerCompile extends JPanel implements J2SECustomizer.Panel {

    // Helper for storing properties
    private WebProjectProperties j2seProperties;
    private VisualPropertySupport vps;

    /** Creates new form CustomizerCompile */
    public CustomizerCompile() {
        initComponents();
    }


    public void initValues( WebProjectProperties j2seProperties ) {

        this.j2seProperties = j2seProperties;

        vps = new VisualPropertySupport( j2seProperties );
        
        vps.register( jCheckBoxDeprecation, WebProjectProperties.JAVAC_DEPRECATION );
        vps.register( jCheckBoxDebugInfo, WebProjectProperties.JAVAC_DEBUG );
        
        VisualClasspathSupport vcs = new VisualClasspathSupport(
            jListClasspath,
            jButtonAddJar,
            jButtonAddLibrary,
            jButtonAddArtifact,
            jButtonEdit,
            jButtonRemove,
            jButtonMoveUp,
            jButtonMoveDown );
        vps.register( vcs, WebProjectProperties.JAVAC_CLASSPATH );

        jButtonEdit.setVisible( false );

    }

    


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jCheckBox14Source = new javax.swing.JCheckBox();
        jCheckBoxDeprecation = new javax.swing.JCheckBox();
        jCheckBoxDebugInfo = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabelClasspath = new javax.swing.JLabel();
        jScrollClasspath = new javax.swing.JScrollPane();
        jListClasspath = new javax.swing.JList();
        jButtonAddJar = new javax.swing.JButton();
        jButtonAddLibrary = new javax.swing.JButton();
        jButtonAddArtifact = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonMoveUp = new javax.swing.JButton();
        jButtonMoveDown = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EtchedBorder(), new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12))));
        jCheckBox14Source.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Src14_JCheckBox"));
        jCheckBox14Source.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBox14Source, gridBagConstraints);

        jCheckBoxDeprecation.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_Deprecation_JCheckBox"));
        jCheckBoxDeprecation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxDeprecation, gridBagConstraints);

        jCheckBoxDebugInfo.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Compiler_DebugInfo_JCheckBox"));
        jCheckBoxDebugInfo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(jCheckBoxDebugInfo, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabelClasspath.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_JLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        jPanel2.add(jLabelClasspath, gridBagConstraints);

        jScrollClasspath.setViewportView(jListClasspath);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel2.add(jScrollClasspath, gridBagConstraints);

        jButtonAddJar.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddJar_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddJar, gridBagConstraints);

        jButtonAddLibrary.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddLibrary_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonAddLibrary, gridBagConstraints);

        jButtonAddArtifact.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_AddArtifact_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonAddArtifact, gridBagConstraints);

        jButtonEdit.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Edit_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonEdit, gridBagConstraints);

        jButtonRemove.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_Remove_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonRemove, gridBagConstraints);

        jButtonMoveUp.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveUp_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jButtonMoveUp, gridBagConstraints);

        jButtonMoveDown.setText(org.openide.util.NbBundle.getMessage(CustomizerCompile.class, "LBL_CustomizeCompile_Classpath_MoveDown_JButton"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        jPanel2.add(jButtonMoveDown, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddArtifact;
    private javax.swing.JButton jButtonAddJar;
    private javax.swing.JButton jButtonAddLibrary;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonMoveDown;
    private javax.swing.JButton jButtonMoveUp;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JCheckBox jCheckBox14Source;
    private javax.swing.JCheckBox jCheckBoxDebugInfo;
    private javax.swing.JCheckBox jCheckBoxDeprecation;
    private javax.swing.JLabel jLabelClasspath;
    private javax.swing.JList jListClasspath;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollClasspath;
    // End of variables declaration//GEN-END:variables



}
