/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * CustomizerSign.java
 *
 * Created on 23.Mar 2004, 11:31
 */
package org.netbeans.modules.mobility.project.ui.customizer;

import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import org.netbeans.spi.mobility.project.ui.customizer.CustomizerPanel;

import org.netbeans.spi.mobility.project.ui.customizer.support.VisualPropertySupport;
import org.netbeans.spi.mobility.project.ui.customizer.VisualPropertyGroup;
import org.netbeans.modules.mobility.project.ui.security.*;
import org.netbeans.modules.mobility.project.security.KeyStoreRepository;
import org.openide.util.NbBundle;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.*;
import java.util.List;
import org.netbeans.api.mobility.project.ui.customizer.ProjectProperties;
import org.netbeans.modules.mobility.project.DefaultPropertiesDescriptor;

/**
 *
 * @author  David Kaspar
 */
final public class CustomizerSign extends JPanel implements CustomizerPanel, VisualPropertyGroup, ChangeListener, ItemListener {
    
    static final String[] PROPERTY_GROUP = new String[] { DefaultPropertiesDescriptor.SIGN_ENABLED, DefaultPropertiesDescriptor.SIGN_KEYSTORE, DefaultPropertiesDescriptor.SIGN_ALIAS };
    
    private VisualPropertySupport vps;
    private boolean useDefault;
    private Map<String, Object> props;
    private String configuration;
    
    /** Creates new form CustomizerConfigs */
    public CustomizerSign() {
        initComponents();
        initAccessibility();
        cKeystore.setRenderer(new KeystoreCellRenderer());
        cAlias.setRenderer(new KeyAliasCellRenderer());
        cEnabled.addChangeListener(this);
        cKeystore.addItemListener(this);
        cAlias.addItemListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cDefault = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        cEnabled = new javax.swing.JCheckBox();
        bOpenSecurityManager = new javax.swing.JButton();
        lKeystore = new javax.swing.JLabel();
        cKeystore = new javax.swing.JComboBox();
        bKeyStoreUnlock = new javax.swing.JButton();
        lAlias = new javax.swing.JLabel();
        cAlias = new javax.swing.JComboBox();
        bAliasUnlock = new javax.swing.JButton();
        pDetails = new javax.swing.JPanel();
        lDetails = new javax.swing.JLabel();
        bExport = new javax.swing.JButton();
        pError = new org.netbeans.modules.mobility.project.ui.customizer.ErrorPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(cDefault, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Use_Default")); // NOI18N
        cDefault.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(cDefault, gridBagConstraints);
        cDefault.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_UseDefault")); // NOI18N

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(cEnabled, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_Enabled")); // NOI18N
        cEnabled.setMargin(new java.awt.Insets(0, 0, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 12, 0);
        jPanel1.add(cEnabled, gridBagConstraints);
        cEnabled.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_Sign")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bOpenSecurityManager, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_OpenSecurityManager")); // NOI18N
        bOpenSecurityManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOpenSecurityManagerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 12, 0);
        jPanel1.add(bOpenSecurityManager, gridBagConstraints);
        bOpenSecurityManager.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_OpenManager")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel1, gridBagConstraints);

        lKeystore.setLabelFor(cKeystore);
        org.openide.awt.Mnemonics.setLocalizedText(lKeystore, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_Keystore")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        add(lKeystore, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        add(cKeystore, gridBagConstraints);
        cKeystore.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_Keystore")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bKeyStoreUnlock, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_UnlockKeyStore")); // NOI18N
        bKeyStoreUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bKeyStoreUnlockActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(bKeyStoreUnlock, gridBagConstraints);
        bKeyStoreUnlock.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_UnlockKeystore")); // NOI18N

        lAlias.setLabelFor(cAlias);
        org.openide.awt.Mnemonics.setLocalizedText(lAlias, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_Alias")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(lAlias, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(cAlias, gridBagConstraints);
        cAlias.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_Alias")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bAliasUnlock, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_UnlockAlias")); // NOI18N
        bAliasUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAliasUnlockActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        add(bAliasUnlock, gridBagConstraints);
        bAliasUnlock.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_UnlockAlias")); // NOI18N

        pDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_Details"))); // NOI18N
        pDetails.setEnabled(false);
        pDetails.setLayout(new java.awt.GridBagLayout());

        lDetails.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pDetails.add(lDetails, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(bExport, org.openide.util.NbBundle.getMessage(CustomizerSign.class, "LBL_Sign_Export")); // NOI18N
        bExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        pDetails.add(bExport, gridBagConstraints);
        bExport.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_CustSign_Export")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 0, 0);
        add(pDetails, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pError, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void initAccessibility() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(CustomizerSign.class, "ACSN_Sign"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CustomizerSign.class, "ACSD_Sign"));
    }
    
    private void bExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bExportActionPerformed
        final KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
        final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem();
        ExportPanel.showExportKeyIntoPlatform(bean, alias, null, null); // !!! to do - replace nulls
    }//GEN-LAST:event_bExportActionPerformed
    
    private void bAliasUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAliasUnlockActionPerformed
        final KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
        final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem();
        if (EnterPasswordPanel.getAliasPassword(bean, alias) != null) {
            loadAliasUnlock();
            loadDetails();
        }
    }//GEN-LAST:event_bAliasUnlockActionPerformed
    
    private void bKeyStoreUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bKeyStoreUnlockActionPerformed
        final KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
        if (EnterPasswordPanel.getKeystorePassword(bean) != null) {
            loadKeystoreUnlock();
            loadAliasses();
        }
    }//GEN-LAST:event_bKeyStoreUnlockActionPerformed
    
    private void bOpenSecurityManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOpenSecurityManagerActionPerformed
        SecurityManagerPanel.showSecurityManager((KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem(), (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem());
        loadKeystores();
    }//GEN-LAST:event_bOpenSecurityManagerActionPerformed
    
    public void initValues(ProjectProperties props, String configuration) {
        this.props = props;
        String keystorePath = (String)props.get(DefaultPropertiesDescriptor.SIGN_KEYSTORE);
        if (keystorePath.startsWith("${") && keystorePath.endsWith("}")) {
            String propName = keystorePath.substring(2, keystorePath.length() - 1);
            keystorePath = System.getProperty(propName);
            props.put(DefaultPropertiesDescriptor.SIGN_KEYSTORE, keystorePath);
        }

        this.configuration = configuration;
        vps = VisualPropertySupport.getDefault(props);
        vps.register(cDefault, configuration, this);
    }
    
    public String[] getGroupPropertyNames() {
        return PROPERTY_GROUP;
    }
    
    public void initGroupValues(final boolean useDefault) {
        this.useDefault = useDefault;
        vps.register(cEnabled, DefaultPropertiesDescriptor.SIGN_ENABLED, useDefault);
        cEnabled.setEnabled(!useDefault);
        bOpenSecurityManager.setEnabled(!useDefault);
        loadKeystores();
    }
    
    public void stateChanged(@SuppressWarnings("unused")
	final ChangeEvent e) {
        loadKeystores();
    }
    
    public void itemStateChanged(final ItemEvent e) {
        if (e.getSource() == cKeystore) {
            final KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
            props.put(VisualPropertySupport.translatePropertyName(configuration, DefaultPropertiesDescriptor.SIGN_KEYSTORE, useDefault), bean != null ? bean.getKeyStorePath() : null);
            loadKeystoreUnlock();
            loadAliasses();
        } else if (e.getSource() == cAlias) {
            final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem();
            props.put(VisualPropertySupport.translatePropertyName(configuration, DefaultPropertiesDescriptor.SIGN_ALIAS, useDefault), alias != null ? alias.getAlias() : null);
            loadAliasUnlock();
            loadDetails();
        }
    }
    
    private boolean isEditEnabled() {
        return cEnabled.isEnabled() && cEnabled.isSelected();
    }
    
    private void loadKeystores() {
        final boolean editEnabled = isEditEnabled();
        cKeystore.setEnabled(editEnabled);
        lKeystore.setEnabled(editEnabled);
        lAlias.setEnabled(editEnabled);
        registerKeystore();
        loadKeystoreUnlock();
        loadAliasses();
    }
    
    private void loadAliasses() {
        cAlias.setEnabled(isEditEnabled());
        registerAlias();
        loadAliasUnlock();
        loadDetails();
    }
    
    private void loadKeystoreUnlock() {
        final KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
        bKeyStoreUnlock.setEnabled(bean != null ? (isEditEnabled() && bean.isValid() && !bean.isOpened()) : false);
    }
    
    private void loadAliasUnlock() {
        final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem();
        bAliasUnlock.setEnabled(alias != null ? (isEditEnabled()  &&  alias.isValid()  &&  !alias.isOpened()) : false);
        bExport.setEnabled(alias != null ? (isEditEnabled()  &&  alias.isValid()  &&  alias.isOpened()) : false);
    }
    
    private void loadDetails() {
        final KeyStoreRepository.KeyStoreBean.KeyAliasBean alias = (KeyStoreRepository.KeyStoreBean.KeyAliasBean) cAlias.getSelectedItem();
        lDetails.setText(alias != null ? KeyAliasCellRenderer.getHtmlFormattedText(alias) : ""); // NOI18N
        final Color color = UIManager.getDefaults().getColor(isEditEnabled() ? "Label.foreground" : "Label.disabledForeground");
        lDetails.setForeground(color);
        final Border b = pDetails.getBorder();
        if (b instanceof TitledBorder)
            ((TitledBorder) b).setTitleColor(color);
    }
    
    public void registerKeystore() {
        final List<KeyStoreRepository.KeyStoreBean> list = KeyStoreRepository.getDefault().getKeyStores();
        register(cKeystore, list != null ? list.toArray() : null, DefaultPropertiesDescriptor.SIGN_KEYSTORE);
    }
    
    public void registerAlias() {
        KeyStoreRepository.KeyStoreBean bean = (KeyStoreRepository.KeyStoreBean) cKeystore.getSelectedItem();
        if (bean != null && (!bean.isValid() || !bean.isOpened()))
            bean = null;
        register(cAlias, bean != null ? bean.aliasses().toArray() : null, DefaultPropertiesDescriptor.SIGN_ALIAS);
    }
    
    private void register(final JComboBox component, final Object[] items, final String propertyName) {
        cKeystore.removeItemListener(this);
        cAlias.removeItemListener(this);
        final Object value = props.get(VisualPropertySupport.translatePropertyName(configuration, propertyName, useDefault));
        component.removeAllItems();
        boolean selected = false;
        if (items != null) for (int i = 0; i < items.length; i++) {
            Object o = items[i];
            component.addItem(o);
            if (o != null  &&  o.equals(value)) {
                selected = true;
                component.setSelectedIndex(i);
            }
        }
        if (!selected  &&  items != null  &&  items.length > 0) {
            Object o = items[0];
            if (o != null) {
                if (o instanceof KeyStoreRepository.KeyStoreBean)
                    o = ((KeyStoreRepository.KeyStoreBean) o).getKeyStorePath();
                else if (o instanceof KeyStoreRepository.KeyStoreBean.KeyAliasBean)
                    o = ((KeyStoreRepository.KeyStoreBean.KeyAliasBean) o).getAlias();
            }
            component.setSelectedIndex(0);
            props.put(VisualPropertySupport.translatePropertyName(configuration, propertyName, useDefault), o);
        }
        cKeystore.addItemListener(this);
        cAlias.addItemListener(this);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAliasUnlock;
    private javax.swing.JButton bExport;
    private javax.swing.JButton bKeyStoreUnlock;
    private javax.swing.JButton bOpenSecurityManager;
    private javax.swing.JComboBox cAlias;
    private javax.swing.JCheckBox cDefault;
    private javax.swing.JCheckBox cEnabled;
    private javax.swing.JComboBox cKeystore;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lAlias;
    private javax.swing.JLabel lDetails;
    private javax.swing.JLabel lKeystore;
    private javax.swing.JPanel pDetails;
    private org.netbeans.modules.mobility.project.ui.customizer.ErrorPanel pError;
    // End of variables declaration//GEN-END:variables
    
}
