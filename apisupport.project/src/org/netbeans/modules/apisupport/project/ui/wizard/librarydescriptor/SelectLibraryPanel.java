/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.apisupport.project.ui.wizard.librarydescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.project.libraries.LibrariesCustomizer;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.modules.apisupport.project.ui.platform.LibrariesModel;
import org.netbeans.modules.apisupport.project.ui.wizard.BasicWizardIterator;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Represents <em>Libraries</em> panel in J2SE Library Descriptor Wizard.
 *
 * @author Radek Matous
 */
final class SelectLibraryPanel extends BasicWizardIterator.Panel {
    
    private NewLibraryDescriptor.DataModel data;
    
    /**
     * Creates new form SelectLibraryPanel
     */
    public SelectLibraryPanel(WizardDescriptor setting, NewLibraryDescriptor.DataModel data) {
        super(setting);
        this.data = data;
        initComponents();
        initAccessibility();
        putClientProperty("NewFileWizard_Title", getMessage("LBL_LibraryWizardTitle"));
        
    }
    
    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(getMessage("ACS_SelectLibraryPanel"));
        manageLibrariessButton.getAccessibleContext().setAccessibleDescription(getMessage("ACS_CTL_ManageLibraries"));
        librariesValue.getAccessibleContext().setAccessibleDescription(getMessage("ACS_LBL_Library"));
    }
    
    private void checkValidity() {
        //TODO: 
        markValid();
    }
    
    Library getSelectedLibrary() {
        return (librariesValue != null) ?(Library)librariesValue.getSelectedItem() : null;
    }
    
    protected void storeToDataModel() {
        data.setLibrary(getSelectedLibrary());
    }
    
    protected void readFromDataModel() {
        checkValidity();
    }
    
    protected String getPanelName() {
        return getMessage("LBL_SelectLibraryPanel_Title");
    }
    
    protected HelpCtx getHelp() {
        return new HelpCtx(SelectLibraryPanel.class);
    }
    
    private static String getMessage(String key) {
        return NbBundle.getMessage(SelectLibraryPanel.class, key);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        platformPanel = new javax.swing.JPanel();
        library = new javax.swing.JLabel();
        librariesValue = LibrariesModel.getComboBox();
        manageLibrariessButton = new javax.swing.JButton();
        fillerPanel = new javax.swing.JPanel();
        fillerLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        platformPanel.setLayout(new java.awt.GridBagLayout());

        library.setLabelFor(librariesValue);
        org.openide.awt.Mnemonics.setLocalizedText(library, org.openide.util.NbBundle.getMessage(SelectLibraryPanel.class, "LBL_Library"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        platformPanel.add(library, gridBagConstraints);

        librariesValue.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                librariesValueItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        platformPanel.add(librariesValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(manageLibrariessButton, org.openide.util.NbBundle.getMessage(SelectLibraryPanel.class, "CTL_ManageLibraries"));
        manageLibrariessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageLibraries(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        platformPanel.add(manageLibrariessButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(platformPanel, gridBagConstraints);

        fillerPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        fillerPanel.add(fillerLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(fillerPanel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void librariesValueItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_librariesValueItemStateChanged
        checkValidity();
    }//GEN-LAST:event_librariesValueItemStateChanged
    
    private void manageLibraries(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageLibraries
        LibrariesModel model = (LibrariesModel) librariesValue.getModel();
        Collection oldLibraries = Arrays.asList(model.getLibraries());
        LibrariesCustomizer.showCustomizer((Library)librariesValue.getSelectedItem());
        List currentLibraries = Arrays.asList(model.getLibraries());
        Collection newLibraries = new ArrayList(currentLibraries);
        
        newLibraries.removeAll(oldLibraries);
        int indexes[] = new int [newLibraries.size()];
        
        int i=0;
        for (Iterator it = newLibraries.iterator(); it.hasNext();i++) {
            Library lib = (Library) it.next();
            indexes[i] = currentLibraries.indexOf(lib);
        }
        if (indexes.length > 0 && i > 0) {
            librariesValue.setSelectedIndex(indexes[i-1]);
        }
    }//GEN-LAST:event_manageLibraries
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fillerLabel;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JComboBox librariesValue;
    private javax.swing.JLabel library;
    private javax.swing.JButton manageLibrariessButton;
    private javax.swing.JPanel platformPanel;
    // End of variables declaration//GEN-END:variables
    
}
