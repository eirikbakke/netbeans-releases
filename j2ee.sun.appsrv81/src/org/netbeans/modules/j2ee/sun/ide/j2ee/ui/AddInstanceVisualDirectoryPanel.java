/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.sun.ide.j2ee.ui;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.openide.util.NbBundle;

public final class AddInstanceVisualDirectoryPanel extends JPanel {
    
    private boolean createPersonalInstance;

    /**
     * Creates new form AddInstanceVisualDirectoryPanel
     */
    public AddInstanceVisualDirectoryPanel(boolean createPersonalInstance) {
        initComponents();
        setAdminPort(AddDomainWizardIterator.BLANK);
        this.createPersonalInstance = createPersonalInstance;
        if (createPersonalInstance) {
            description.setText(
                    NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, 
                    "TXT_instanceDirectoryDescription2"));
        }
        adminPortLabel.setVisible(!createPersonalInstance);
        adminPortDisplay.setVisible(!createPersonalInstance);
            
        instanceDirectory.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                locationChanged();
            }
            public void insertUpdate(DocumentEvent e) {
                locationChanged();
            }
            public void removeUpdate(DocumentEvent e) {
                locationChanged();
            }                    
        });
    }

    public String getName() {
        return NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, 
                "StepName_EnterDomainDirectory");                                // NOI18N
    }
    
    void setAdminPort(String uri) {
        if (!createPersonalInstance) {
            adminPortDisplay.setText(uri);
            detectedLabel.setVisible(uri.length() > 0);
        }
    }
    
    String getInstanceDirectory() {
        return instanceDirectory.getText();
    }
    
    // Event handling
    //
    private final Set/*<ChangeListener>*/ listeners = new HashSet/*<ChangeListener>*/(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator/*<ChangeListener>*/ it;
        synchronized (listeners) {
            it = new HashSet/*<ChangeListener>*/(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    void locationChanged() {
        fireChangeEvent();
    }
    
    
    private String browseInstallLocation(){
        String insLocation = null;
        JFileChooser chooser = Util.getJFileChooser(new DirFilter());
        int returnValue = chooser.showDialog(this,
                NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class,
                "LBL_Choose_Button"));                                          //NOI18N
        
        if(returnValue == JFileChooser.APPROVE_OPTION){
            insLocation = chooser.getSelectedFile().getAbsolutePath();
        }
        return insLocation;
    }
    
    /** Class to filter possible directories.  It isn't complete.
     */
    private class DirFilter extends javax.swing.filechooser.FileFilter {
        
        /** Accept files that are existing writable directories
         */
        public boolean accept(File f) {
            if(!f.exists() || !f.canRead() || !f.isDirectory() ) {
                return false;
            }else{
                return true;
            }
        }
        
        public String getDescription() {
            return NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class,
                    "LBL_DomainDirType");                                            // NOI18N
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        description = new javax.swing.JLabel();
        instanceDirectoryLabel = new javax.swing.JLabel();
        instanceDirectory = new javax.swing.JTextField();
        openInstanceDirectorySelector = new javax.swing.JButton();
        adminPortLabel = new javax.swing.JLabel();
        adminPortDisplay = new javax.swing.JTextField();
        detectedLabel = new javax.swing.JLabel();
        spaceHack = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        description.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle").getString("TXT_instanceDirectoryDescription1"));
        description.setEnabled(false);
        description.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(description, gridBagConstraints);

        instanceDirectoryLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, "MNM_instanceDirectoryLabel").charAt(0));
        instanceDirectoryLabel.setLabelFor(instanceDirectory);
        instanceDirectoryLabel.setText(org.openide.util.NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, "LBL_instanceDirectoryLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 6, 6);
        add(instanceDirectoryLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 6, 6);
        add(instanceDirectory, gridBagConstraints);
        instanceDirectory.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle").getString("DSC_instanceDirectory"));

        openInstanceDirectorySelector.setMnemonic(org.openide.util.NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, "MNM_openInstanceDirectorySelector").charAt(0));
        openInstanceDirectorySelector.setText(org.openide.util.NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, "LBL_openInstanceDirectorySelector"));
        openInstanceDirectorySelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openInstanceDirectorySelectorActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 6, 0);
        add(openInstanceDirectorySelector, gridBagConstraints);
        openInstanceDirectorySelector.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle").getString("DSC_openInstanceDirectorySelector"));

        adminPortLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle").getString("MNM_adminPortLabel").charAt(0));
        adminPortLabel.setLabelFor(adminPortDisplay);
        adminPortLabel.setText(org.openide.util.NbBundle.getMessage(AddInstanceVisualDirectoryPanel.class, "LBL_adminlPortLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 6, 6);
        add(adminPortLabel, gridBagConstraints);

        adminPortDisplay.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 6);
        add(adminPortDisplay, gridBagConstraints);

        detectedLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ide/j2ee/ui/Bundle").getString("LBL_detectedLabel"));
        detectedLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 6);
        add(detectedLabel, gridBagConstraints);

        spaceHack.setEnabled(false);
        spaceHack.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        add(spaceHack, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void openInstanceDirectorySelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openInstanceDirectorySelectorActionPerformed
        String val = browseInstallLocation();
        if (null != val && val.length() >=1)
            instanceDirectory.setText(val);
    }//GEN-LAST:event_openInstanceDirectorySelectorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adminPortDisplay;
    private javax.swing.JLabel adminPortLabel;
    private javax.swing.JLabel description;
    private javax.swing.JLabel detectedLabel;
    private javax.swing.JTextField instanceDirectory;
    private javax.swing.JLabel instanceDirectoryLabel;
    private javax.swing.JButton openInstanceDirectorySelector;
    private javax.swing.JLabel spaceHack;
    // End of variables declaration//GEN-END:variables

}

