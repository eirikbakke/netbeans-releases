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

package org.netbeans.modules.profiler.attach.panels;
import java.awt.Font;
import java.text.MessageFormat;
import javax.swing.UIManager;
import org.openide.util.NbBundle;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class SelectIntegrationTypePanelUI extends javax.swing.JPanel {
  private SelectIntegrationTypePanel.Model model = null;
  private final String INTEGRATION_TYPE_HELP=NbBundle.getMessage(this.getClass(), "IntegrationTypeWizardPanelUI_IntegrationHintMsg"); // NOI18N
  /**
   * Creates new form SelectIntegrationTypePanelUI
   */
  SelectIntegrationTypePanelUI(SelectIntegrationTypePanel.Model model) {
    this.model = model;
    initComponents();
    loadModel();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupIntegrationType = new javax.swing.ButtonGroup();
        selectionPanel = new javax.swing.JPanel();
        buttonAutomatic = new javax.swing.JRadioButton();
        buttonManual = new javax.swing.JRadioButton();
        hintPanel = new org.netbeans.modules.profiler.attach.panels.components.ResizableHintPanel();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(400, 300));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/attach/panels/Bundle"); // NOI18N
        selectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("IntegrationTypeWizardPanelUI_ChooseIntegrationTypeString"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, UIManager.getFont("TitledBorder.font").deriveFont(Font.BOLD))); // NOI18N

        groupIntegrationType.add(buttonAutomatic);
        buttonAutomatic.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(buttonAutomatic, bundle.getString("IntegrationTypeWizardPanelUI_AutomaticString")); // NOI18N
        buttonAutomatic.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonAutomatic.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonAutomatic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAutomaticActionPerformed(evt);
            }
        });

        groupIntegrationType.add(buttonManual);
        org.openide.awt.Mnemonics.setLocalizedText(buttonManual, bundle.getString("IntegrationTypeWizardPanelUI_ManualString")); // NOI18N
        buttonManual.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonManual.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonManualActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout selectionPanelLayout = new org.jdesktop.layout.GroupLayout(selectionPanel);
        selectionPanel.setLayout(selectionPanelLayout);
        selectionPanelLayout.setHorizontalGroup(
            selectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(selectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(selectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(buttonAutomatic)
                    .add(buttonManual))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        selectionPanelLayout.setVerticalGroup(
            selectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(selectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(buttonAutomatic)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonManual)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonAutomatic.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectIntegrationTypePanelUI.class, "SelectIntegrationTypePanelUI.buttonAutomatic.AccessibleContext.accessibleDescription")); // NOI18N
        buttonManual.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(SelectIntegrationTypePanelUI.class, "SelectIntegrationTypePanelUI.buttonManual.AccessibleContext.accessibleDescription")); // NOI18N

        hintPanel.setMaximumSize(new java.awt.Dimension(500, 100));
        hintPanel.setMinimumSize(new java.awt.Dimension(500, 40));
        hintPanel.setPreferredSize(new java.awt.Dimension(500, 80));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(selectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(hintPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(selectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 125, Short.MAX_VALUE)
                .add(hintPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        hintPanel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SelectIntegrationTypePanelUI.class, "SelectIntegrationTypePanelUI.hintPanel.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
  
    private void buttonManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonManualActionPerformed
      this.model.setIntegrationType(SelectIntegrationTypePanel.Model.MANUAL_INTEGRATION);
    }//GEN-LAST:event_buttonManualActionPerformed
    
    private void buttonAutomaticActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAutomaticActionPerformed
      this.model.setIntegrationType(SelectIntegrationTypePanel.Model.AUTOMATIC_INTEGRATION);
    }//GEN-LAST:event_buttonAutomaticActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton buttonAutomatic;
    private javax.swing.JRadioButton buttonManual;
    private javax.swing.ButtonGroup groupIntegrationType;
    private org.netbeans.modules.profiler.attach.panels.components.ResizableHintPanel hintPanel;
    private javax.swing.JPanel selectionPanel;
    // End of variables declaration//GEN-END:variables
  
  private void loadModel() {
    this.model.setIntegrationType(buttonAutomatic.isSelected()?SelectIntegrationTypePanel.Model.AUTOMATIC_INTEGRATION:SelectIntegrationTypePanel.Model.MANUAL_INTEGRATION);
  }
  
  public void refresh() {
    switch(this.model.getIntegrationType()) {
      case SelectIntegrationTypePanel.Model.AUTOMATIC_INTEGRATION:
        buttonAutomatic.setSelected(true);
        break;
      case SelectIntegrationTypePanel.Model.MANUAL_INTEGRATION:
        buttonManual.setSelected(true);
        break;
      default:
        buttonAutomatic.setSelected(true);
    }
    
    final String providerName = model.getProviderName();
    hintPanel.setHint(MessageFormat.format(INTEGRATION_TYPE_HELP, new Object[]{providerName != null ? providerName : ""})); // NOI18N
  }
}

