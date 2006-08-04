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
 * GeneralInfoPanel.java
 *
 * Created on July 25, 2005, 10:37 AM
 */
package org.netbeans.modules.mobility.jsr172.multiview;

import org.netbeans.modules.xml.multiview.Error;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import javax.swing.JComponent;
import org.netbeans.modules.mobility.end2end.E2EDataObject;
import org.netbeans.modules.mobility.end2end.client.config.ClientConfiguration;
import org.netbeans.modules.mobility.end2end.client.config.Configuration;
import org.openide.util.NbBundle;

/**
 *
 * @author  Michal Skvor
 */
public class ClientGeneralInfoPanel extends SectionInnerPanel {
    
    final private transient E2EDataObject dataObject;
    
    /** Creates new form GeneralInfoPanel */
    public ClientGeneralInfoPanel( SectionView sectionView, E2EDataObject dataObject ) {
        super( sectionView );
        
        this.dataObject = dataObject;
        
        initComponents();
        
        initValues();
    }
    
    private void initValues() {
        final Configuration config = dataObject.getConfiguration();
        
        final ClientConfiguration cc = config.getClientConfiguration();
        final String projectName = cc.getProjectName();
        if( "".equals( projectName )) {
            getSectionView().getErrorPanel().setError(
                    new Error( Error.TYPE_FATAL, Error.ERROR_MESSAGE,
                    NbBundle.getMessage( ClientGeneralInfoPanel.class, "ERR_ProjectNameIsNotValid" ), textProject ));
        }
        textProject.setText( projectName );
        textPackage.setText( cc.getClassDescriptor().getPackageName() );
        final String clientName = cc.getClassDescriptor().getLeafClassName();
        if( "".equals( clientName )) {
            getSectionView().getErrorPanel().setError(
                    new Error( Error.TYPE_FATAL, Error.ERROR_MESSAGE,
                    NbBundle.getMessage( ClientGeneralInfoPanel.class, "ERR_ClientNameIsNotValid" ), textClientName ));
        }
        textClientName.setText( clientName );
    }
    
    public JComponent getErrorComponent( @SuppressWarnings("unused")
	final String errorId ) {
        return null;
    }
    
    public void linkButtonPressed( @SuppressWarnings("unused")
	final Object ddBean, @SuppressWarnings("unused")
	final String ddProperty ) {
    }
    
    public void setValue( @SuppressWarnings("unused")
	final JComponent source, @SuppressWarnings("unused")
	final Object value ) {
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        textPackage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        textClientName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        textProject = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "MNM_ClientPackage").charAt(0));
        jLabel1.setLabelFor(textPackage);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "LABEL_Package"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(jLabel1, gridBagConstraints);

        textPackage.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 10);
        add(textPackage, gridBagConstraints);

        jLabel2.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "MNM_Client_Name").charAt(0));
        jLabel2.setLabelFor(textClientName);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "LABEL_Client_Name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(jLabel2, gridBagConstraints);

        textClientName.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 10);
        add(textClientName, gridBagConstraints);

        jLabel3.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "MNM_ClientProject").charAt(0));
        jLabel3.setLabelFor(textProject);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(ClientGeneralInfoPanel.class, "LABEL_Project"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        add(jLabel3, gridBagConstraints);

        textProject.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 5, 10);
        add(textProject, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField textClientName;
    private javax.swing.JTextField textPackage;
    private javax.swing.JTextField textProject;
    // End of variables declaration//GEN-END:variables
    
}
