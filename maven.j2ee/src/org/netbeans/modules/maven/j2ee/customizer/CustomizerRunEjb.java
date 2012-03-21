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

package org.netbeans.modules.maven.j2ee.customizer;

import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.maven.api.customizer.ModelHandle2;
import org.netbeans.modules.maven.j2ee.utils.LoggingUtils;


public class CustomizerRunEjb extends BaseRunCustomizer {

    private EjbJar module;


    public CustomizerRunEjb(ModelHandle2 handle, Project project) {
        super(handle, project);
        initComponents();
        
        module = EjbJar.getEjbJar(project.getProjectDirectory());
        if (module != null) {
            loadServerModel(comServer, J2eeModule.Type.EJB, module.getJ2eeProfile());
            txtJ2EEVersion.setText(module.getJ2eeProfile().getDisplayName());
        }
        
        initDeployOnSaveComponent(jCheckBoxDeployOnSave, dosDescription);
        initServerComponent(comServer, lblServer);
    }
    
    @Override
    public void applyChangesInAWT() {
        Object obj = comServer.getSelectedItem();
        if (obj != null) {
            LoggingUtils.logUsage(CustomizerRunEjb.class, "USG_PROJECT_CONFIG_MAVEN_SERVER", new Object[] { obj.toString() }, "maven"); //NOI18N
        }
    }

    @Override
    public void applyChanges() {
        changeServer(comServer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblServer = new javax.swing.JLabel();
        comServer = new javax.swing.JComboBox();
        lblJ2EEVersion = new javax.swing.JLabel();
        txtJ2EEVersion = new javax.swing.JTextField();
        jCheckBoxDeployOnSave = new javax.swing.JCheckBox();
        dosDescription = new javax.swing.JLabel();

        lblServer.setText(org.openide.util.NbBundle.getMessage(CustomizerRunEjb.class, "LBL_Server")); // NOI18N

        lblJ2EEVersion.setText(org.openide.util.NbBundle.getMessage(CustomizerRunEjb.class, "LBL_J2EE_Version")); // NOI18N

        txtJ2EEVersion.setEditable(false);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBoxDeployOnSave, org.openide.util.NbBundle.getMessage(CustomizerRunEjb.class, "EjbRunCustomizerPanel.jCheckBoxDeployOnSave.text")); // NOI18N

        dosDescription.setText(org.openide.util.NbBundle.getMessage(CustomizerRunEjb.class, "EjbRunCustomizerPanel.dosDescription.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblJ2EEVersion)
                                    .addComponent(lblServer))
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comServer, javax.swing.GroupLayout.Alignment.TRAILING, 0, 280, Short.MAX_VALUE)
                                    .addComponent(txtJ2EEVersion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)))
                            .addComponent(jCheckBoxDeployOnSave)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(dosDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServer)
                    .addComponent(comServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJ2EEVersion)
                    .addComponent(txtJ2EEVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDeployOnSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dosDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comServer;
    private javax.swing.JLabel dosDescription;
    private javax.swing.JCheckBox jCheckBoxDeployOnSave;
    private javax.swing.JLabel lblJ2EEVersion;
    private javax.swing.JLabel lblServer;
    private javax.swing.JTextField txtJ2EEVersion;
    // End of variables declaration//GEN-END:variables

}
