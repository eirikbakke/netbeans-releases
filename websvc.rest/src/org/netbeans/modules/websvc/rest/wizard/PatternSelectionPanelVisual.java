/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.websvc.rest.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.rest.wizard.PatternResourcesSetupPanel.Pattern;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author  Nam Nguyen
 */
public class PatternSelectionPanelVisual extends javax.swing.JPanel implements AbstractPanel.Settings {
    
    private Project project;
    private List<ChangeListener> listeners;
    
    
    /** Creates new form CrudSetupPanel */
    public PatternSelectionPanelVisual(String name) {
        setName(name);
        this.listeners = new ArrayList<ChangeListener>();
        initComponents();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        patternButtons = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        containerRadioButton = new javax.swing.JRadioButton();
        standAloneRadioButton = new javax.swing.JRadioButton();
        clientControlledRadioButton = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionEditorPane = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        patternButtons.add(containerRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(containerRadioButton, org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_ContainerItem")); // NOI18N
        containerRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_ContainerItem")); // NOI18N
        containerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        containerRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                containerRadioButtonActionPerformed(evt);
            }
        });

        patternButtons.add(standAloneRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(standAloneRadioButton, org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_SingletonResource")); // NOI18N
        standAloneRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_GenericResource")); // NOI18N
        standAloneRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        standAloneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standAlonePatternSelected(evt);
            }
        });

        patternButtons.add(clientControlledRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(clientControlledRadioButton, org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_ClientControl")); // NOI18N
        clientControlledRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        clientControlledRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientControlledPatternSelected(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clientControlledRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addComponent(containerRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                    .addComponent(standAloneRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(standAloneRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(containerRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(clientControlledRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        containerRadioButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_ContainerItem")); // NOI18N
        containerRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "ACSD_ContainerResource")); // NOI18N
        standAloneRadioButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_GenericResource")); // NOI18N
        standAloneRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "ACSD_Singleton")); // NOI18N
        clientControlledRadioButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_ClientControl")); // NOI18N
        clientControlledRadioButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "ACSD_ClientControlled")); // NOI18N

        descriptionEditorPane.setEditable(false);
        jScrollPane1.setViewportView(descriptionEditorPane);
        descriptionEditorPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_PatternDescription")); // NOI18N
        descriptionEditorPane.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "DESC_PatternDescription")); // NOI18N

        jLabel1.setLabelFor(jPanel2);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_SelectPattern")); // NOI18N

        jLabel2.setLabelFor(descriptionEditorPane);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_Description")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_SelectDesignPattern")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_SelectPattern")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_Description")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_Select_Pattern")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PatternSelectionPanelVisual.class, "LBL_SelectPattern")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void clientControlledPatternSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientControlledPatternSelected
    setDescription();
    fireChange();
}//GEN-LAST:event_clientControlledPatternSelected

private void standAlonePatternSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standAlonePatternSelected
    setDescription();
    fireChange();    
}//GEN-LAST:event_standAlonePatternSelected

    private void containerRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_containerRadioButtonActionPerformed
        setDescription();
        fireChange();
}//GEN-LAST:event_containerRadioButtonActionPerformed
                
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton clientControlledRadioButton;
    private javax.swing.JRadioButton containerRadioButton;
    private javax.swing.JEditorPane descriptionEditorPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup patternButtons;
    private javax.swing.JRadioButton standAloneRadioButton;
    // End of variables declaration//GEN-END:variables
    
    public boolean valid(WizardDescriptor wizard) {
        AbstractPanel.clearErrorMessage(wizard);
        return true;
    }
    
    public void read(WizardDescriptor settings) {
        Pattern p = (Pattern) settings.getProperty(WizardProperties.PATTERN_SELECTION);
        if (p == Pattern.CONTAINER) {
            patternButtons.setSelected(containerRadioButton.getModel(), true);
        } else if (p == Pattern.CLIENTCONTROLLED) {
            patternButtons.setSelected(clientControlledRadioButton.getModel(), true);
        } else { // default
            patternButtons.setSelected(standAloneRadioButton.getModel(), true);
        }
        setDescription();
    }
    
    public void store(WizardDescriptor settings) {
        Pattern p;
        if (containerRadioButton.isSelected()) {
            p = Pattern.CONTAINER;
        } else if (clientControlledRadioButton.isSelected()) {
            p = Pattern.CLIENTCONTROLLED;
        } else {
            p = Pattern.STANDALONE;
        }
        settings.putProperty(WizardProperties.PATTERN_SELECTION, p);
    }
    
    private void setDescription() {
        descriptionEditorPane.setContentType("text/html");
        String bundleKey;
        if (containerRadioButton.isSelected()) {
            bundleKey = "DESC_Container_Pattern";
        } else if (clientControlledRadioButton.isSelected()) {
            bundleKey = "DESC_Client_Control_Pattern";
        } else {
            bundleKey = "DESC_Singleton_Pattern";
        }
        String text = NbBundle.getMessage(this.getClass(), bundleKey);
        descriptionEditorPane.setText(text);
        descriptionEditorPane.setCaretPosition(0);
    }
    
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }
    
    public void fireChange() {
        ChangeEvent event =  new ChangeEvent(this);
        
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
    
}
