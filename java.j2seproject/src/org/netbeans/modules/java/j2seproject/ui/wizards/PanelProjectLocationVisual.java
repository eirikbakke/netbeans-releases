/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.j2seproject.ui.wizards;

import java.io.File;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.java.j2seproject.ui.FoldersListSettings;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Petr Hrebejk
 */

public class PanelProjectLocationVisual extends SettingsPanel implements DocumentListener {
    
    public static final String PROP_PROJECT_NAME = "projectName";      //NOI18N
    
    private PanelConfigureProject panel;
    private int type;
        
    /** Creates new form PanelProjectLocationVisual */
    public PanelProjectLocationVisual( PanelConfigureProject panel, int type ) {
        initComponents();
        this.panel = panel;
        this.type = type;
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener( this );
        projectLocationTextField.getDocument().addDocumentListener( this );        
    }
    
    
    public String getProjectName () {
        return this.projectNameTextField.getText ();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        Button = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        projectNameLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/wizards/Bundle").getString("LBL_NWP1_ProjectName_LabelMnemonic").charAt(0));
        projectNameLabel.setLabelFor(projectNameTextField);
        projectNameLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectName_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(projectNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(projectNameTextField, gridBagConstraints);

        projectLocationLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/wizards/Bundle").getString("LBL_NWP1_ProjectLocation_LabelMnemonic").charAt(0));
        projectLocationLabel.setLabelFor(projectLocationTextField);
        projectLocationLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_ProjectLocation_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(projectLocationLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(projectLocationTextField, gridBagConstraints);

        Button.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_BrowseLocation_Button"));
        Button.setActionCommand("BROWSE");
        Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseLocationAction(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 0);
        add(Button, gridBagConstraints);

        createdFolderLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/java/j2seproject/ui/wizards/Bundle").getString("LBL_NWP1_CreatedProjectFolder_LablelMnemonic").charAt(0));
        createdFolderLabel.setLabelFor(createdFolderTextField);
        createdFolderLabel.setText(org.openide.util.NbBundle.getMessage(PanelProjectLocationVisual.class, "LBL_NWP1_CreatedProjectFolder_Lablel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(createdFolderLabel, gridBagConstraints);

        createdFolderTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(createdFolderTextField, gridBagConstraints);

    }//GEN-END:initComponents

    private void browseLocationAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseLocationAction
        String command = evt.getActionCommand();        
        if ( "BROWSE".equals( command ) ) { // NOI18N                
            JFileChooser chooser = new JFileChooser ();
            chooser.setDialogTitle(NbBundle.getMessage(PanelSourceFolders.class,"LBL_NWP1_SelectProjectLocation"));
            chooser.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File (path);
                if (f.exists ()) {
                    chooser.setSelectedFile(f);
                }
            }
            if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) { //NOI18N
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText( projectDir.getAbsolutePath() );
            }            
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_browseLocationAction
    
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }
    
    boolean valid( WizardDescriptor wizardDescriptor ) {
        
        if ( projectNameTextField.getText().length() == 0 ) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
            NbBundle.getMessage(PanelProjectLocationVisual.class,"MSG_IllegalProjectName"));
            return false; // Display name not specified
        }
        
        File destFolder = new File( createdFolderTextField.getText() );
        File[] kids = destFolder.listFiles();
        if ( destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
            NbBundle.getMessage(PanelProjectLocationVisual.class,"MSG_ProjectFolderExists"));
            return false;
        }
        
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", "" );
        return true;
    }
    
    void store( WizardDescriptor d ) {        
        
        String name = projectNameTextField.getText().trim();
        String location = projectLocationTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();
        
        d.putProperty( /*XXX Define somewhere */ "projdir", new File( folder )); // NOI18N
        d.putProperty( /*XXX Define somewhere */ "displayName", name ); // NOI18N      
        d.putProperty( /*XXX Define somewhere */ "codename", PropertyUtils.getUsablePropertyName(name)); // NOI18N
        File projectsDir = new File(this.projectLocationTextField.getText());
        if (projectsDir.isDirectory()) {
            ProjectChooser.setProjectsFolder (projectsDir);
        }
    }
    
    void read (WizardDescriptor settings) {
        String projectName = (String) settings.getProperty ("displayName"); //NOI18N
        if (projectName == null) {
            if (this.type == NewJ2SEProjectWizardIterator.TYPE_APP) {
                projectName = MessageFormat.format (NbBundle.getMessage(PanelSourceFolders.class,"TXT_JavaApplication"), new Object[]{
                    new Integer (FoldersListSettings.getDefault().getNewApplicationCount()+1)});
            }
            else {
                projectName = MessageFormat.format (NbBundle.getMessage(PanelSourceFolders.class,"TXT_JavaLibrary"), new Object[]{
                    new Integer (FoldersListSettings.getDefault().getNewLibraryCount()+1)});                
            }            
        }
        this.projectNameTextField.setText (projectName);
        
        File projectLocation = (File) settings.getProperty ("projdir");  //NOI18N
        if (projectLocation == null) {
            projectLocation = ProjectChooser.getProjectsFolder();
        }
        else {
            projectLocation = projectLocation.getParentFile();
        }
        this.projectLocationTextField.setText (projectLocation.getAbsolutePath());
    }
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    // End of variables declaration//GEN-END:variables
    
    
    // Private methods ---------------------------------------------------------
    
    private static JFileChooser createChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        chooser.setAcceptAllFileFilterUsed( false );
        chooser.setName( "Select Project Directory" ); // XXX
        return chooser;
    }

    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void insertUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void removeUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange (PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts( DocumentEvent e ) {
        
        Document doc = e.getDocument();
                
        if ( doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument() ) {
            // Change in the project name
        
            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText(); 

            //if ( projectFolder.trim().length() == 0 || projectFolder.equals( oldName )  ) {                
            createdFolderTextField.setText( projectFolder + File.separatorChar + projectName );
            //}
            
        }                
        panel.fireChangeEvent(); // Notify that the panel changed        
    }    

    
}
