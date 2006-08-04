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
 * Created on July 25, 2005, 10:37 AM
 */
package org.netbeans.modules.mobility.jsr172.multiview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.mobility.end2end.classdata.WSDLService;
import org.netbeans.modules.xml.multiview.Error;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.mobility.end2end.E2EDataObject;
import org.netbeans.modules.mobility.end2end.client.config.ClassDescriptor;
import org.netbeans.modules.mobility.end2end.client.config.ClientConfiguration;
import org.netbeans.modules.mobility.end2end.client.config.Configuration;
import org.openide.util.NbBundle;

/**
 *
 * @author  Michal Skvor,Sigal Duek
 *
 */

public class JSR172ServicePanel extends SectionInnerPanel implements PropertyChangeListener {
    
    private transient E2EDataObject dataObject;
    private Configuration configuration;
    private WSDLService wsdlService;
    private WsdlUpdater updater;
    
    public JSR172ServicePanel(){
        this(null,null,null);
    }
    
    
    
    /** Creates new form GeneralInfoPanel */
    
    public JSR172ServicePanel( SectionView sectionView, E2EDataObject dataObject , Configuration configuration) {
        
        super( sectionView );
        this.dataObject = dataObject;
        this.configuration = configuration;
        initComponents();
        if (configuration != null){
            initValues();
        }
        
        dataObject.addPropertyChangeListener(new PropertyChangeListener() {
            @SuppressWarnings("synthetic-access")
			public void propertyChange(final PropertyChangeEvent evt) {
                if (E2EDataObject.PROP_GENERATING.equals(evt.getPropertyName())){
                    buttonGenerate.setEnabled(!((Boolean)evt.getNewValue()).booleanValue());
                }
            }
        });
    }
    private void initValues() {
        wsdlService = (WSDLService)configuration.getServices().get(0);
        final String url = wsdlService.getUrl();
        if(  url == null || url.equals( "" )) {
            System.err.println("ERR_UrlIsNotValid");
            getSectionView().getErrorPanel().setError(
                    new Error( Error.TYPE_FATAL, Error.ERROR_MESSAGE,
                    NbBundle.getMessage( JSR172ServicePanel.class, "ERR_UrlIsNotValid" ), textUrl ));
        }
        textUrl.setText( url );
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
    
    
    
    public void documentChanged( @SuppressWarnings("unused")
	final JTextComponent comp, @SuppressWarnings("unused")
	final String value) {
    }
    
    
    
    /** This method is called from within the constructor to
     *
     * initialize the form.
     *
     * WARNING: Do NOT modify this code. The content of this method is
     *
     * always regenerated by the Form Editor.
     *
     */
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        textUrl = new javax.swing.JTextField();
        refreshButton = new javax.swing.JButton();
        buttonGenerate = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(JSR172ServicePanel.class, "WSDL_URL_Label"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        add(jLabel1, gridBagConstraints);

        textUrl.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 10);
        add(textUrl, gridBagConstraints);

        refreshButton.setText(org.openide.util.NbBundle.getMessage(JSR172ServicePanel.class, "Label_Refresh_WSDL"));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @SuppressWarnings("synthetic-access")
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        add(refreshButton, gridBagConstraints);

        buttonGenerate.setMnemonic(org.openide.util.NbBundle.getMessage(JSR172ServicePanel.class, "MNM_Generate").charAt(0));
        buttonGenerate.setText(org.openide.util.NbBundle.getMessage(JSR172ServicePanel.class, "LBL_Generate"));
        buttonGenerate.addActionListener(new java.awt.event.ActionListener() {
            @SuppressWarnings("synthetic-access")
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGenerateActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        add(buttonGenerate, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void buttonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGenerateActionPerformed
        buttonGenerate.setEnabled(false);
        dataObject.generate(false);
    }//GEN-LAST:event_buttonGenerateActionPerformed
    
    
    
    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        
        //download wsdl and compare with checksum
        //if there is a differnece notify the user to update
        refreshButton.setEnabled(false);
        final String url = wsdlService.getUrl();
        //find wsdl file
        final ClientConfiguration configuration = dataObject.getConfiguration().getClientConfiguration();
        final ClassDescriptor cd = configuration.getClassDescriptor();
        final WSDLService service = (WSDLService) dataObject.getConfiguration().getServices().get(0);
        final String fileName = service.getFile();
        
        updater = new WsdlUpdater(url, cd.getPackageName().replace('.','/'), fileName, dataObject);
        updater.addPropertyChangeListener(this);
    }//GEN-LAST:event_refreshButtonActionPerformed
    
    
    
    public void propertyChange(final PropertyChangeEvent evt) {
        if (WsdlUpdater.PROP_UPDATE_FINISHED.equals(evt.getPropertyName())){
            refreshButton.setEnabled(true);
            updater.removePropertyChangeListener(this);
            updater = null;
        }
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonGenerate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTextField textUrl;
    // End of variables declaration//GEN-END:variables
    
}

