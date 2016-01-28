/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docker.ui.build2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.docker.api.DockerInstance;
import org.netbeans.modules.docker.api.DockerIntegration;
import org.netbeans.modules.docker.ui.wizard.AddDockerInstanceWizard;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Petr Hejl
 */
public class BuildInstanceVisual extends javax.swing.JPanel {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    private final DefaultComboBoxModel<DockerInstanceWrapper> model = new DefaultComboBoxModel<>();

    /**
     * Creates new form BuildInstance
     */
    public BuildInstanceVisual() {
        initComponents();

        for (DockerInstance i : DockerIntegration.getDefault().getInstances()) {
            model.addElement(new DockerInstanceWrapper(i));
        }
        instanceComboBox.setModel(model);

        instanceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeSupport.fireChange();
            }
        });
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    public DockerInstance getInstance() {
        Object item = instanceComboBox.getSelectedItem();
        if (item == null) {
            return null;
        }
        return ((DockerInstanceWrapper) item).getInstance();
    }

    public void setInstance(DockerInstance instance) {
        if (instance == null) {
            return;
        }
        for (int i = 0; i < model.getSize(); i++) {
            DockerInstanceWrapper w = model.getElementAt(i);
            if (instance.equals(w.getInstance())) {
                instanceComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @NbBundle.Messages("LBL_BuildInstance=Build Instance")
    @Override
    public String getName() {
        return Bundle.LBL_BuildInstance();
    }

    private static class DockerInstanceWrapper {

        private final DockerInstance instance;

        public DockerInstanceWrapper(DockerInstance instance) {
            this.instance = instance;
        }

        public DockerInstance getInstance() {
            return instance;
        }

        @Override
        public String toString() {
            return instance.getDisplayName();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instanceLabel = new javax.swing.JLabel();
        instanceComboBox = new javax.swing.JComboBox<>();
        addButton = new javax.swing.JButton();

        instanceLabel.setLabelFor(instanceComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(instanceLabel, org.openide.util.NbBundle.getMessage(BuildInstanceVisual.class, "BuildInstanceVisual.instanceLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(BuildInstanceVisual.class, "BuildInstanceVisual.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(instanceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instanceComboBox, 0, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(instanceLabel)
                .addComponent(instanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(addButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        AddDockerInstanceWizard wizard = new AddDockerInstanceWizard();
        wizard.show();
    }//GEN-LAST:event_addButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox<DockerInstanceWrapper> instanceComboBox;
    private javax.swing.JLabel instanceLabel;
    // End of variables declaration//GEN-END:variables
}
