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
 * Customizer.java
 *
 * Created on 23.Mar 2004, 11:31
 */
package org.netbeans.modules.mobility.project.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.mobility.project.ui.customizer.ProjectProperties;
import org.netbeans.modules.mobility.cldcplatform.J2MEPlatform;
import org.netbeans.modules.mobility.project.DefaultPropertiesDescriptor;
import org.netbeans.spi.mobility.project.ui.customizer.CustomizerPanel;
import org.netbeans.spi.mobility.project.ui.customizer.support.VisualPropertySupport;
import org.netbeans.spi.mobility.project.ui.customizer.VisualPropertyGroup;
import org.openide.util.NbBundle;

/**
 *
 * @author  Adam Sotona
 */
public class CustomizerRun extends JPanel implements CustomizerPanel, VisualPropertyGroup, ActionListener {
    
    private static final String[] PROPERTY_GROUP = new String[] {DefaultPropertiesDescriptor.RUN_METHOD, DefaultPropertiesDescriptor.RUN_SECURITY_DOMAIN, DefaultPropertiesDescriptor.RUN_USE_SECURITY_DOMAIN, DefaultPropertiesDescriptor.RUN_CMD_OPTIONS};
    
    private VisualPropertySupport vps;
    String[] securitydomains;
    
    /** Creates new form CustomizerConfigs */
    public CustomizerRun() {
        initComponents();
        initAccessibility();
        standardRadio.setActionCommand("STANDARD"); // NOI18N
        OTARadio.setActionCommand("OTA"); // NOI18N
        jCheckBoxUseSecurity.addActionListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        runMethodGroup = new javax.swing.ButtonGroup();
        defaultCheck = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        cmdOptionsText = new javax.swing.JTextField();
        standardRadio = new javax.swing.JRadioButton();
        jCheckBoxUseSecurity = new javax.swing.JCheckBox();
        domainsCombo = new javax.swing.JComboBox();
        OTARadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        defaultCheck.setMnemonic(org.openide.util.NbBundle.getBundle(CustomizerRun.class).getString("MNM_Use_Default").charAt(0));
        defaultCheck.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_Use_Default"));
        defaultCheck.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(defaultCheck, gridBagConstraints);
        defaultCheck.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_UseDefault"));

        jLabel1.setDisplayedMnemonic(NbBundle.getMessage(CustomizerRun.class, "MNM_CustRun_CmdOptions").charAt(0));
        jLabel1.setLabelFor(cmdOptionsText);
        jLabel1.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_CmdOptions"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 5);
        add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(cmdOptionsText, gridBagConstraints);
        cmdOptionsText.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_CmdOptions"));

        runMethodGroup.add(standardRadio);
        standardRadio.setMnemonic(org.openide.util.NbBundle.getBundle(CustomizerRun.class).getString("MNM_CustRun_RegularExecution").charAt(0));
        standardRadio.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_RegularExecution"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(standardRadio, gridBagConstraints);
        standardRadio.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_Standard"));

        jCheckBoxUseSecurity.setMnemonic(org.openide.util.NbBundle.getBundle(CustomizerRun.class).getString("MNM_CustRun_SecurityDomain").charAt(0));
        jCheckBoxUseSecurity.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_SecurityDomain"));
        jCheckBoxUseSecurity.setActionCommand(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_SecurityDomain"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(jCheckBoxUseSecurity, gridBagConstraints);
        jCheckBoxUseSecurity.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_UseSecurity"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(domainsCombo, gridBagConstraints);
        domainsCombo.getAccessibleContext().setAccessibleName(NbBundle.getMessage(CustomizerRun.class, "ACSN_CustRun_Domain"));
        domainsCombo.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_Domain"));

        runMethodGroup.add(OTARadio);
        OTARadio.setMnemonic(org.openide.util.NbBundle.getBundle(CustomizerRun.class).getString("MNM_CustRun_OTA").charAt(0));
        OTARadio.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_OTA"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(OTARadio, gridBagConstraints);
        OTARadio.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun_OTA"));

        jLabel2.setText(NbBundle.getMessage(CustomizerRun.class, "LBL_CustRun_CmdHint"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(jLabel2, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CustomizerRun.class, "ACSN_CustRun"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerRun.class, "ACSD_CustRun"));
    }
    
    public void initValues(ProjectProperties props, String configuration) {
        this.vps = VisualPropertySupport.getDefault(props);
        String activePlatform = (String) props.get(VisualPropertySupport.translatePropertyName(configuration, "platform.active", false));//NOI18N
        if (activePlatform == null)
            activePlatform = (String) props.get("platform.active");//NOI18N
        String activeDevice = (String) props.get(VisualPropertySupport.translatePropertyName(configuration, "platform.device", false));//NOI18N
        if (activeDevice == null)
            activeDevice = (String) props.get("platform.device");//NOI18N
        final JavaPlatform[] platforms = JavaPlatformManager.getDefault().getPlatforms(null, new Specification(J2MEPlatform.SPECIFICATION_NAME, null));
        securitydomains = new String[0];
        if (activePlatform != null  &&  activeDevice != null  &&  platforms != null) for (int a = 0; a < platforms.length; a ++) {
            if (platforms[a] instanceof J2MEPlatform) {
                final J2MEPlatform platform = (J2MEPlatform) platforms[a];
                if (activePlatform.equals(platform.getDisplayName())) {
                    final J2MEPlatform.Device[] devices = platform.getDevices();
                    if (devices != null) for (int b = 0; b < devices.length; b ++) {
                        final J2MEPlatform.Device device = devices[b];
                        if (activeDevice.equals(device.getName())) {
                            if (device.getSecurityDomains() != null)
                                securitydomains = device.getSecurityDomains();
                            break;
                        }
                    }
                }
            }
        }
        vps.register(defaultCheck, configuration, this);
    }
    
    public String[] getGroupPropertyNames() {
        return PROPERTY_GROUP;
    }
    
    public void initGroupValues(final boolean useDefault) {
        vps.register(standardRadio, DefaultPropertiesDescriptor.RUN_METHOD, useDefault);
        vps.register(domainsCombo, securitydomains, DefaultPropertiesDescriptor.RUN_SECURITY_DOMAIN, useDefault);
        vps.register(OTARadio, DefaultPropertiesDescriptor.RUN_METHOD, useDefault);
        vps.register(jCheckBoxUseSecurity, DefaultPropertiesDescriptor.RUN_USE_SECURITY_DOMAIN, useDefault);
        vps.register(cmdOptionsText, DefaultPropertiesDescriptor.RUN_CMD_OPTIONS, useDefault);
        actionPerformed(null);
    }
    
    public void actionPerformed(@SuppressWarnings("unused")
	final ActionEvent e) {
        domainsCombo.setEnabled(jCheckBoxUseSecurity.isEnabled() && jCheckBoxUseSecurity.isSelected());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton OTARadio;
    private javax.swing.JTextField cmdOptionsText;
    private javax.swing.JCheckBox defaultCheck;
    private javax.swing.JComboBox domainsCombo;
    private javax.swing.JCheckBox jCheckBoxUseSecurity;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.ButtonGroup runMethodGroup;
    private javax.swing.JRadioButton standardRadio;
    // End of variables declaration//GEN-END:variables
    
}
