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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.explorer.dlg;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public final class ChoosingDriverInterUI extends JPanel implements DocumentListener {
    
    private String driverFileName;
    private String driverPath;
    private String downloadFrom;
    private final ChoosingDriverPanel panel;

    /** Creates new form ChoosingDriverInterUI */
    @SuppressWarnings("LeakingThisInConstructor")
    public ChoosingDriverInterUI(ChoosingDriverPanel panel, String driverFileName, String driverPath, String downloadFrom, boolean found) {
        this.panel = panel;
        this.driverFileName = driverFileName;
        this.driverPath = driverPath;
        this.downloadFrom = downloadFrom;
        initComponents();
        if (found) {
            tfLocation.setText(driverPath + File.separator + driverFileName);
        } else {
            tfLocation.setText(driverPath);
        }
        updateComponents(false);
        if (found) {
            lFound1.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lFound1.text", driverFileName)); // NOI18N
        } else {
            lFound1.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lFound1.text.not.found", driverFileName)); // NOI18N
        }
        lFound2.setVisible(found);
        lFound2.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lFound2.text", tfLocation.getText())); // NOI18N
        lDownloadInfo1.setVisible(downloadFrom != null && ! downloadFrom.isEmpty());
        lDownloadInfo2.setVisible(downloadFrom != null && ! downloadFrom.isEmpty());
        bDownload.setVisible(downloadFrom != null && ! downloadFrom.isEmpty());
        lUrl.setVisible(downloadFrom != null && ! downloadFrom.isEmpty());
        tfLocation.getDocument().addDocumentListener(this);
    }
    
    private void updateComponents(boolean notifyNow) {
        if (driverFound()) {
            this.putClientProperty(NotifyDescriptor.PROP_WARNING_NOTIFICATION, NbBundle.getMessage(ChoosingDriverInterUI.class,
                    "ChoosingDriverInterUI.errorMessage.DriverNotFound", driverFileName)); // NOI18N
            lDownloadInfo1.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lDownloadInfo1.text.found", driverFileName)); // NOI18N
        } else {
            lDownloadInfo1.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lDownloadInfo1.text", driverFileName)); // NOI18N
        }
        if (lDownloadInfo2.isVisible()) {
            lDownloadInfo2.setText(NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lDownloadInfo2.text", tfLocation.getText())); // NOI18N
        }
        if (notifyNow) {
            panel.fireChangeEvent();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    panel.fireChangeEvent();
                }
            });
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lFound1 = new javax.swing.JLabel();
        lLocation = new javax.swing.JLabel();
        tfLocation = new javax.swing.JTextField();
        bBrowse = new javax.swing.JButton();
        lDownloadInfo1 = new javax.swing.JLabel();
        bDownload = new javax.swing.JButton();
        lUrl = new javax.swing.JLabel();
        lDownloadInfo2 = new javax.swing.JLabel();
        lFound2 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(lFound1, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lFound1.text", new Object[] {driverFileName})); // NOI18N

        lLocation.setLabelFor(tfLocation);
        org.openide.awt.Mnemonics.setLocalizedText(lLocation, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lLocation.text")); // NOI18N

        tfLocation.setText(org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.tfLocation.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bBrowse, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.bBrowse.text")); // NOI18N
        bBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBrowseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lDownloadInfo1, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lDownloadInfo1.text", new Object[] {driverFileName})); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(bDownload, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.bDownload.text")); // NOI18N
        bDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDownloadActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lUrl, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lUrl.text", new Object[] {downloadFrom})); // NOI18N
        lUrl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lUrlMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lDownloadInfo2, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lDownloadInfo2.text", new Object[] {driverPath})); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lFound2, org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.lFound2.text", new Object[] {driverPath})); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lFound1)
                    .addComponent(lDownloadInfo1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bDownload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .addComponent(lDownloadInfo2)
                    .addComponent(lFound2)
                    .addComponent(lLocation)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bBrowse)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lFound1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lFound2)
                .addGap(18, 18, 18)
                .addComponent(lLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBrowse))
                .addGap(27, 27, 27)
                .addComponent(lDownloadInfo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lDownloadInfo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bDownload)
                    .addComponent(lUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void bDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDownloadActionPerformed
        // TODO add your handling code here:
        lUrlMouseClicked(null);
    }//GEN-LAST:event_bDownloadActionPerformed

    private void bBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBrowseActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(tfLocation.getText()));
        chooser.setDialogTitle(org.openide.util.NbBundle.getMessage(ChoosingDriverInterUI.class, "ChoosingDriverInterUI.locateDriver", driverFileName)); // NOI18N
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        String path = this.tfLocation.getText();
        if (path.length() > 0) {
            File f = new File(path);
            if (f.exists()) {
                chooser.setSelectedFile(f);
            }
        }
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            File driverFile = chooser.getSelectedFile();
            tfLocation.setText(FileUtil.normalizeFile(driverFile).getAbsolutePath());
            updateComponents(true);
        }
        panel.fireChangeEvent();
    }//GEN-LAST:event_bBrowseActionPerformed

    private void lUrlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lUrlMouseClicked
        URI uri = URI.create(downloadFrom);
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(ChoosingDriverInterUI.class.getName()).log(Level.INFO, ex.getLocalizedMessage(), ex);
        }
    }//GEN-LAST:event_lUrlMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBrowse;
    private javax.swing.JButton bDownload;
    private javax.swing.JLabel lDownloadInfo1;
    private javax.swing.JLabel lDownloadInfo2;
    private javax.swing.JLabel lFound1;
    private javax.swing.JLabel lFound2;
    private javax.swing.JLabel lLocation;
    private javax.swing.JLabel lUrl;
    private javax.swing.JTextField tfLocation;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateComponents(true);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateComponents(true);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateComponents(true);
    }

    boolean driverFound() {
        return new File(tfLocation.getText()).exists() && tfLocation.getText().endsWith(driverFileName);
    }
    
    String getDriverLocation() {
        if (driverFound()) {
            return tfLocation.getText();
        } else {
            return null;
        }
    }
}
