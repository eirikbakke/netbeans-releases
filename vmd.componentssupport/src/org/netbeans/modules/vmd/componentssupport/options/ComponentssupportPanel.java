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
package org.netbeans.modules.vmd.componentssupport.options;

import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.modules.options.java.api.JavaOptions;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbPreferences;

/**
 *
 * @author Karol Harezlak
 */
@OptionsPanelController.Keywords(keywords={"#JavaME_Option_DisplayName_Componentssupport_TITLE", "#KW_JavaMEOptions"}, location=JavaOptions.JAVA, tabTitle= "#JavaME_Option_DisplayName_Componentssupport_TITLE")
final class ComponentssupportPanel extends javax.swing.JPanel {

    private static final String VMD_STRUCTURE_SHOW = "vmd.structure.show"; // NOI18N
    private static final String VMD_DIRECT_ROUTING = "vmd.direct.routing"; // NOI18N
    private final ComponentssupportOptionsPanelController controller;

    ComponentssupportPanel(ComponentssupportOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
    // TODO listen to changes in form fields and call controller.changed()
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(ComponentssupportPanel.class, "ComponentssupportPanel.jCheckBox1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ComponentssupportPanel.class, "ComponentssupportPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox2, org.openide.util.NbBundle.getMessage(ComponentssupportPanel.class, "ComponentssupportPanel.jCheckBox2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel1))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        jCheckBox1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComponentssupportPanel.class, "ComponentssupportPanel.jCheckBox1.text")); // NOI18N
        jCheckBox2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComponentssupportPanel.class, "ComponentssupportPanel.jCheckBox2.text")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        jCheckBox1.setSelected(NbPreferences.forModule(ComponentssupportPanel.class).getBoolean(VMD_STRUCTURE_SHOW, false));
        jCheckBox2.setSelected(NbPreferences.forModule(RouterFactory.class).getBoolean(VMD_DIRECT_ROUTING, false));
    }

    void store() {
        if (jCheckBox1.isSelected()) {
            System.getProperties().setProperty(VMD_STRUCTURE_SHOW, "true"); //NOI18N
        } else {
            System.getProperties().setProperty(VMD_STRUCTURE_SHOW, "false"); //NOI18N
        }
        NbPreferences.forModule(ComponentssupportPanel.class).putBoolean(VMD_STRUCTURE_SHOW, jCheckBox1.isSelected());
        NbPreferences.forModule(RouterFactory.class).putBoolean(VMD_DIRECT_ROUTING, jCheckBox2.isSelected());
    }

    boolean valid() {
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
