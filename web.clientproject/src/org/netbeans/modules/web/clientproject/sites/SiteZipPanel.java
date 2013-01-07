/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.sites;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;

@NbBundle.Messages({"LBL_SiteZipPanel_Select=Select",
        "LBL_SiteZipPanel_Title=Select Template Archive File"})
public class SiteZipPanel extends javax.swing.JPanel implements DocumentListener {

    private SiteZip.Customizer cust;

    public SiteZipPanel(SiteZip.Customizer cust) {
        this.cust = cust;
        initComponents();
        List<String> templates = new ArrayList<String>(SiteZip.getUsedTemplates());
        if (templates.size() > 0 && templates.get(0).length() > 0) {
            templates.add(0, ""); //NOI18N
        }
        archiveComboBox.setModel(new DefaultComboBoxModel(templates.toArray(new String[templates.size()])));
        ((JTextField)(archiveComboBox.getEditor().getEditorComponent())).getDocument().addDocumentListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        archiveLabel = new javax.swing.JLabel();
        archiveComboBox = new javax.swing.JComboBox();
        browseButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();

        archiveLabel.setLabelFor(archiveComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(archiveLabel, org.openide.util.NbBundle.getMessage(SiteZipPanel.class, "SiteZipPanel.archiveLabel.text")); // NOI18N

        archiveComboBox.setEditable(true);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(SiteZipPanel.class, "SiteZipPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        infoLabel.setFont(infoLabel.getFont().deriveFont(infoLabel.getFont().getSize()-1f));
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(SiteZipPanel.class, "SiteZipPanel.infoLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(archiveLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(archiveComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseButton))
                    .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(archiveLabel)
                    .addComponent(browseButton)
                    .addComponent(archiveComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public String getTemplate() {
        return ((JTextField)(archiveComboBox.getEditor().getEditorComponent())).getText();
    }

    // only to be called from a unit test:
    public void setTemplate(String template) {
        ((JTextField)(archiveComboBox.getEditor().getEditorComponent())).setText(template);
    }

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File file = new FileChooserBuilder(SiteZipPanel.class.getName())
                .setTitle(Bundle.LBL_SiteZipPanel_Title())
                .setFilesOnly(true)
                .setApproveText(Bundle.LBL_SiteZipPanel_Select())
                .showOpenDialog();
        if (file != null) {
            ((JTextField)(archiveComboBox.getEditor().getEditorComponent())).setText(file.getAbsolutePath());
        }

    }//GEN-LAST:event_browseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox archiveComboBox;
    private javax.swing.JLabel archiveLabel;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel infoLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        cust.fireChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        cust.fireChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        cust.fireChange();
    }
}
