/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.connections.sftp;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.project.connections.ConfigManager.Configuration;
import org.netbeans.modules.php.project.connections.common.RemoteUtils;
import org.netbeans.modules.php.project.connections.common.RemoteValidator;
import org.netbeans.modules.php.project.connections.spi.RemoteConfigurationPanel;
import org.netbeans.modules.php.project.ui.LastUsedFolders;
import org.netbeans.modules.php.project.ui.Utils;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 * @author Tomas Mysik
 */
public class SftpConfigurationPanel extends JPanel implements RemoteConfigurationPanel {
    private static final long serialVersionUID = 2815423138730L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private String error = null;
    private String warning = null;
    private boolean passwordRead = false;


    public SftpConfigurationPanel() {
        initComponents();

        // listeners
        registerListeners();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean isValidConfiguration() {
        String err = RemoteValidator.validateHost(hostTextField.getText());
        if (err != null) {
            setError(err);
            return false;
        }

        err = RemoteValidator.validatePort(portTextField.getText());
        if (err != null) {
            setError(err);
            return false;
        }

        err = RemoteValidator.validateUser(userTextField.getText());
        if (err != null) {
            setError(err);
            return false;
        }

        if (!validateIdentityFile()) {
            return false;
        }

        if (!validateKnownHostsFile()) {
            return false;
        }

        if (!validateInitialDirectory()) {
            return false;
        }

        err = RemoteValidator.validateTimeout(timeoutTextField.getText());
        if (err != null) {
            setError(err);
            return false;
        }

        err = RemoteValidator.validateKeepAliveInterval(keepAliveTextField.getText());
        if (err != null) {
            setError(err);
            return false;
        }

        // ok
        setError(null);
        return true;
    }

    @Override
    public String getError() {
        return error;
    }

    protected void setError(String error) {
        this.error = error;
    }

    @Override
    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    private void registerListeners() {
        DocumentListener documentListener = new DefaultDocumentListener();
        hostTextField.getDocument().addDocumentListener(documentListener);
        portTextField.getDocument().addDocumentListener(documentListener);
        userTextField.getDocument().addDocumentListener(documentListener);
        passwordTextField.getDocument().addDocumentListener(documentListener);
        knownHostsFileTextField.getDocument().addDocumentListener(documentListener);
        identityFileTextField.getDocument().addDocumentListener(documentListener);
        initialDirectoryTextField.getDocument().addDocumentListener(documentListener);
        timeoutTextField.getDocument().addDocumentListener(documentListener);
        keepAliveTextField.getDocument().addDocumentListener(documentListener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    private boolean validateIdentityFile() {
        return validateFile(getIdentityFile(), NbBundle.getMessage(SftpConfiguration.class, "LBL_IdentityFile"));
    }

    private boolean validateKnownHostsFile() {
        return validateFile(getKnownHostsFile(), NbBundle.getMessage(SftpConfiguration.class, "LBL_KnownHostsFile"));
    }

    private boolean validateFile(String path, String label) {
        if (!StringUtils.hasText(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.isFile()) {
            setError(NbBundle.getMessage(SftpConfigurationPanel.class, "MSG_FileNotFile", label));
            return false;
        }
        if (!file.isAbsolute()) {
            setError(NbBundle.getMessage(SftpConfigurationPanel.class, "MSG_FileNotAbsolute", label));
            return false;
        }
        return true;
    }

    private boolean validateInitialDirectory() {
        String err = RemoteValidator.validateUploadDirectory(getInitialDirectory());
        if (err != null) {
            setError(err);
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostLabel = new JLabel();
        knownHostsFileLabel = new JLabel();
        hostTextField = new JTextField();
        portLabel = new JLabel();
        portTextField = new JTextField();
        userLabel = new JLabel();
        userTextField = new JTextField();
        passwordLabel = new JLabel();
        passwordTextField = new JPasswordField();
        passwordLabelInfo = new JLabel();
        identityFileLabel = new JLabel();
        identityFileTextField = new JTextField();
        identityFileBrowseButton = new JButton();
        knownHostsFileTextField = new JTextField();
        knownHostsFileBrowseButton = new JButton();
        initialDirectoryLabel = new JLabel();
        initialDirectoryTextField = new JTextField();
        timeoutLabel = new JLabel();
        timeoutTextField = new JTextField();
        keepAliveLabel = new JLabel();
        keepAliveTextField = new JTextField();
        keepAliveInfoLabel = new JLabel();

        hostLabel.setLabelFor(hostTextField);
        Mnemonics.setLocalizedText(hostLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.hostLabel.text")); // NOI18N

        knownHostsFileLabel.setLabelFor(knownHostsFileTextField);

        Mnemonics.setLocalizedText(knownHostsFileLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileLabel.text")); // NOI18N
        hostTextField.setMinimumSize(new java.awt.Dimension(150, 19));

        hostTextField.setMinimumSize(new Dimension(150, 19));

        portLabel.setLabelFor(portTextField);
        Mnemonics.setLocalizedText(portLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.portLabel.text")); // NOI18N

        userLabel.setLabelFor(userTextField);
        Mnemonics.setLocalizedText(userLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.userLabel.text")); // NOI18N

        passwordLabel.setLabelFor(passwordTextField);
        Mnemonics.setLocalizedText(passwordLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabel.text")); // NOI18N

        passwordLabelInfo.setLabelFor(this);
        Mnemonics.setLocalizedText(passwordLabelInfo, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabelInfo.text")); // NOI18N

        identityFileLabel.setLabelFor(identityFileTextField);
        Mnemonics.setLocalizedText(identityFileLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(identityFileBrowseButton, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileBrowseButton.text")); // NOI18N
        identityFileBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                identityFileBrowseButtonActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(knownHostsFileBrowseButton, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileBrowseButton.text")); // NOI18N
        knownHostsFileBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                knownHostsFileBrowseButtonActionPerformed(evt);
            }
        });

        initialDirectoryLabel.setLabelFor(initialDirectoryTextField);
        Mnemonics.setLocalizedText(initialDirectoryLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.initialDirectoryLabel.text")); // NOI18N

        timeoutLabel.setLabelFor(timeoutTextField);

        Mnemonics.setLocalizedText(timeoutLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.timeoutLabel.text")); // NOI18N
        timeoutTextField.setMinimumSize(new java.awt.Dimension(20, 19));

        timeoutTextField.setMinimumSize(new Dimension(20, 19));

        keepAliveLabel.setLabelFor(keepAliveTextField);
        Mnemonics.setLocalizedText(keepAliveLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveLabel.text")); // NOI18N
        Mnemonics.setLocalizedText(keepAliveInfoLabel, NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveInfoLabel.text")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                .addComponent(passwordLabel)
                .addContainerGap()).addGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(hostLabel).addComponent(userLabel).addComponent(initialDirectoryLabel).addComponent(timeoutLabel).addComponent(identityFileLabel).addComponent(knownHostsFileLabel).addComponent(keepAliveLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()
                        .addComponent(keepAliveInfoLabel)

                        .addGap(0, 0, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup()

                        .addComponent(passwordLabelInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()).addGroup(Alignment.TRAILING, layout.createSequentialGroup()

                        .addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(knownHostsFileTextField).addComponent(identityFileTextField, Alignment.LEADING).addComponent(userTextField, Alignment.LEADING).addComponent(hostTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(timeoutTextField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE).addComponent(passwordTextField).addComponent(initialDirectoryTextField).addComponent(keepAliveTextField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, layout.createParallelGroup(Alignment.LEADING, false).addGroup(layout.createSequentialGroup()
                                    .addComponent(portLabel)

                                    .addPreferredGap(ComponentPlacement.RELATED).addComponent(portTextField)).addComponent(identityFileBrowseButton)).addComponent(knownHostsFileBrowseButton, Alignment.TRAILING)))))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {identityFileBrowseButton, knownHostsFileBrowseButton});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {keepAliveTextField, timeoutTextField});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(hostLabel).addComponent(portLabel).addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(hostTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(userLabel).addComponent(userTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(passwordLabel).addComponent(passwordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addComponent(passwordLabelInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(identityFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(identityFileLabel).addComponent(identityFileBrowseButton)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(knownHostsFileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(knownHostsFileBrowseButton).addComponent(knownHostsFileLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(initialDirectoryLabel).addComponent(initialDirectoryTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(timeoutTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(timeoutLabel)).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(keepAliveLabel).addComponent(keepAliveTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addComponent(keepAliveInfoLabel).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        hostLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.hostLabel.AccessibleContext.accessibleName"));         hostLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.hostLabel.AccessibleContext.accessibleDescription"));         knownHostsFileLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileLabel.AccessibleContext.accessibleName"));         knownHostsFileLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileLabel.AccessibleContext.accessibleDescription"));         hostTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.hostTextField.AccessibleContext.accessibleName"));         hostTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.hostTextField.AccessibleContext.accessibleDescription"));         portLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.portLabel.AccessibleContext.accessibleName"));         portLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.portLabel.AccessibleContext.accessibleDescription"));         portTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.portTextField.AccessibleContext.accessibleName"));         portTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.portTextField.AccessibleContext.accessibleDescription"));         userLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.userLabel.AccessibleContext.accessibleName"));         userLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.userLabel.AccessibleContext.accessibleDescription"));         userTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.userTextField.AccessibleContext.accessibleName"));         userTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.userTextField.AccessibleContext.accessibleDescription"));         passwordLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabel.AccessibleContext.accessibleName"));         passwordLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabel.AccessibleContext.accessibleDescription"));         passwordTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordTextField.AccessibleContext.accessibleName"));         passwordTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordTextField.AccessibleContext.accessibleDescription"));         passwordLabelInfo.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabelInfo.AccessibleContext.accessibleName"));         passwordLabelInfo.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.passwordLabelInfo.AccessibleContext.accessibleDescription"));         identityFileLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileLabel.AccessibleContext.accessibleName"));         identityFileLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileLabel.AccessibleContext.accessibleDescription"));         identityFileTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileTextField.AccessibleContext.accessibleName"));         identityFileTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileTextField.AccessibleContext.accessibleDescription"));         identityFileBrowseButton.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileBrowseButton.AccessibleContext.accessibleName"));         identityFileBrowseButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.identityFileBrowseButton.AccessibleContext.accessibleDescription"));         knownHostsFileTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileTextField.AccessibleContext.accessibleName"));         knownHostsFileTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileTextField.AccessibleContext.accessibleDescription"));         knownHostsFileBrowseButton.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileBrowseButton.AccessibleContext.accessibleName"));         knownHostsFileBrowseButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.knownHostsFileBrowseButton.AccessibleContext.accessibleDescription"));         initialDirectoryLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.initialDirectoryLabel.AccessibleContext.accessibleName"));         initialDirectoryLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.initialDirectoryLabel.AccessibleContext.accessibleDescription"));         initialDirectoryTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.initialDirectoryTextField.AccessibleContext.accessibleName"));         initialDirectoryTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.initialDirectoryTextField.AccessibleContext.accessibleDescription"));         timeoutLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.timeoutLabel.AccessibleContext.accessibleName"));         timeoutLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.timeoutLabel.AccessibleContext.accessibleDescription"));         timeoutTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.timeoutTextField.AccessibleContext.accessibleName"));         timeoutTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.timeoutTextField.AccessibleContext.accessibleDescription"));         keepAliveLabel.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveLabel.AccessibleContext.accessibleName"));         keepAliveLabel.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveLabel.AccessibleContext.accessibleDescription"));         keepAliveTextField.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveTextField.AccessibleContext.accessibleName"));         keepAliveTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.keepAliveTextField.AccessibleContext.accessibleDescription")); 
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.AccessibleContext.accessibleName"));         getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SftpConfigurationPanel.class, "SftpConfigurationPanel.AccessibleContext.accessibleDescription")); // NOI18N
    } //</editor-fold>//GEN-END:initComponents

    private void identityFileBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_identityFileBrowseButtonActionPerformed
        File newFile = Utils.browseFileAction(this, LastUsedFolders.getPrivateKey(), NbBundle.getMessage(SftpConfigurationPanel.class, "LBL_SelectIdentityFile"));
        if (newFile != null) {
            setIdentityFile(newFile.getAbsolutePath());
            LastUsedFolders.setPrivateKey(newFile);
        }
    }//GEN-LAST:event_identityFileBrowseButtonActionPerformed

    private void knownHostsFileBrowseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_knownHostsFileBrowseButtonActionPerformed
        File newFile = Utils.browseFileAction(this, LastUsedFolders.getKnownHosts(), NbBundle.getMessage(SftpConfigurationPanel.class, "LBL_SelectKnownHostsFile"));
        if (newFile != null) {
            setKnownHostsFile(newFile.getAbsolutePath());
            LastUsedFolders.setKnownHosts(newFile);
        }
    }//GEN-LAST:event_knownHostsFileBrowseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel hostLabel;
    private JTextField hostTextField;
    private JButton identityFileBrowseButton;
    private JLabel identityFileLabel;
    private JTextField identityFileTextField;
    private JLabel initialDirectoryLabel;
    private JTextField initialDirectoryTextField;
    private JLabel keepAliveInfoLabel;
    private JLabel keepAliveLabel;
    private JTextField keepAliveTextField;
    private JButton knownHostsFileBrowseButton;
    private JLabel knownHostsFileLabel;
    private JTextField knownHostsFileTextField;
    private JLabel passwordLabel;
    private JLabel passwordLabelInfo;
    private JPasswordField passwordTextField;
    private JLabel portLabel;
    private JTextField portTextField;
    private JLabel timeoutLabel;
    private JTextField timeoutTextField;
    private JLabel userLabel;
    private JTextField userTextField;
    // End of variables declaration//GEN-END:variables

    public String getHostName() {
        return hostTextField.getText();
    }

    public void setHostName(String hostName) {
        hostTextField.setText(hostName);
    }

    public String getPort() {
        return portTextField.getText();
    }

    public void setPort(String port) {
        portTextField.setText(port);
    }

    public String getUserName() {
        return userTextField.getText();
    }

    public void setUserName(String userName) {
        userTextField.setText(userName);
    }

    public String getPassword() {
        return new String(passwordTextField.getPassword());
    }

    public void setPassword(String password) {
        passwordTextField.setText(password);
    }

    public String getKnownHostsFile() {
        return knownHostsFileTextField.getText();
    }

    public void setKnownHostsFile(String knownHostsFile) {
        knownHostsFileTextField.setText(knownHostsFile);
    }

    public String getIdentityFile() {
        return identityFileTextField.getText();
    }

    public void setIdentityFile(String identityFile) {
        identityFileTextField.setText(identityFile);
    }

    public String getInitialDirectory() {
        return initialDirectoryTextField.getText();
    }

    public void setInitialDirectory(String initialDirectory) {
        initialDirectoryTextField.setText(initialDirectory);
    }

    public String getTimeout() {
        return timeoutTextField.getText();
    }

    public void setTimeout(String timeout) {
        timeoutTextField.setText(timeout);
    }

    public String getKeepAliveInterval() {
        return keepAliveTextField.getText();
    }

    public void setKeepAliveInterval(String keepAliveInterval) {
        keepAliveTextField.setText(keepAliveInterval);
    }

    @Override
    public void read(Configuration cfg) {
        setHostName(cfg.getValue(SftpConnectionProvider.HOST));
        setPort(cfg.getValue(SftpConnectionProvider.PORT));
        setUserName(cfg.getValue(SftpConnectionProvider.USER));
        setPassword(readPassword(cfg));
        setKnownHostsFile(cfg.getValue(SftpConnectionProvider.KNOWN_HOSTS_FILE));
        setIdentityFile(cfg.getValue(SftpConnectionProvider.IDENTITY_FILE));
        setInitialDirectory(cfg.getValue(SftpConnectionProvider.INITIAL_DIRECTORY));
        setTimeout(cfg.getValue(SftpConnectionProvider.TIMEOUT));
        setKeepAliveInterval(cfg.getValue(SftpConnectionProvider.KEEP_ALIVE_INTERVAL));
    }

    @Override
    public void store(Configuration cfg) {
        cfg.putValue(SftpConnectionProvider.HOST, getHostName());
        cfg.putValue(SftpConnectionProvider.PORT, getPort());
        cfg.putValue(SftpConnectionProvider.USER, getUserName());
        cfg.putValue(SftpConnectionProvider.PASSWORD, getPassword(), true);
        cfg.putValue(SftpConnectionProvider.KNOWN_HOSTS_FILE, getKnownHostsFile());
        cfg.putValue(SftpConnectionProvider.IDENTITY_FILE, getIdentityFile());
        cfg.putValue(SftpConnectionProvider.INITIAL_DIRECTORY, RemoteUtils.sanitizeUploadDirectory(getInitialDirectory(), false));
        cfg.putValue(SftpConnectionProvider.TIMEOUT, getTimeout());
        cfg.putValue(SftpConnectionProvider.KEEP_ALIVE_INTERVAL, getKeepAliveInterval());
    }

    // #200530
    /**
     * Read password from keyring, once it is needed.
     * @return password
     */
    private String readPassword(Configuration cfg) {
        if (!passwordRead) {
            passwordRead = true;
            return new SftpConfiguration(cfg).getPassword();
        }
        return cfg.getValue(SftpConnectionProvider.PASSWORD, true);
    }

    private final class DefaultDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }
        private void processUpdate() {
            fireChange();
        }
    }
}
