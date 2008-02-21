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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.cnd.makeproject.configurations.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import org.netbeans.api.project.Project;
import org.netbeans.modules.cnd.makeproject.api.MakeCustomizerProvider;
import org.netbeans.modules.cnd.makeproject.api.configurations.ConfigurationDescriptor;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfigurationDescriptor;
import org.netbeans.modules.cnd.makeproject.ui.utils.DirectoryChooserInnerPanel;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class ProjectPropPanel extends javax.swing.JPanel implements ActionListener {

    private SourceRootChooser sourceRootChooser;
    private Project project;
    private MakeConfigurationDescriptor makeConfigurationDescriptor;

    /** Creates new form ProjectPropPanel */
    public ProjectPropPanel(Project project, ConfigurationDescriptor configurationDescriptor) {
        this.project = project;
        makeConfigurationDescriptor = (MakeConfigurationDescriptor) configurationDescriptor;
        initComponents();
        projectTextField.setText(FileUtil.toFile(project.getProjectDirectory()).getPath());
        sourceRootPanel.add(sourceRootChooser = new SourceRootChooser(configurationDescriptor.getBaseDir(), makeConfigurationDescriptor.getSourceRootsAsArray()));

        MakeCustomizerProvider makeCustomizerProvider = (MakeCustomizerProvider) project.getLookup().lookup(MakeCustomizerProvider.class);
        makeCustomizerProvider.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (sourceRootChooser.isChanged()) {
            Vector list = sourceRootChooser.getListData();
            makeConfigurationDescriptor.setSourceRootsList(new ArrayList(list));
        }
        MakeCustomizerProvider makeCustomizerProvider = (MakeCustomizerProvider) project.getLookup().lookup(MakeCustomizerProvider.class);
        makeCustomizerProvider.removeActionListener(this);
    }

    class SourceRootChooser extends DirectoryChooserInnerPanel {

        public SourceRootChooser(String baseDir, Object[] feed) {
            super(baseDir, feed);
            getCopyButton().setVisible(false);
            getEditButton().setVisible(false);
        }

        @Override
        public String getListLabelText() {
            return getString("ProjectPropPanel.sourceRootLabel.text");
        }

        @Override
        public char getListLabelMnemonic() {
            return getString("ProjectPropPanel.sourceRootLabel.mn").charAt(0);
        }

        @Override
        public char getAddButtonMnemonics() {
            return getString("ADD_BUTTON_MN").charAt(0);
        }

        @Override
        public String getAddButtonText() {
            return getString("ADD_BUTTON_TXT");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        projectLabel = new javax.swing.JLabel();
        projectTextField = new javax.swing.JTextField();
        sourceRootPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        projectLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/cnd/makeproject/configurations/ui/Bundle").getString("ProjectPropPanel.projectLabel.mn").charAt(0));
        projectLabel.setLabelFor(projectTextField);
        projectLabel.setText(org.openide.util.NbBundle.getMessage(ProjectPropPanel.class, "ProjectPropPanel.projectLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(projectLabel, gridBagConstraints);
        projectLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ProjectPropPanel.class, "ProjectPropPanel.projectLabel.ad")); // NOI18N

        projectTextField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(projectTextField, gridBagConstraints);

        sourceRootPanel.setBackground(new java.awt.Color(255, 255, 255));
        sourceRootPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(sourceRootPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel projectLabel;
    private javax.swing.JTextField projectTextField;
    private javax.swing.JPanel sourceRootPanel;
    // End of variables declaration//GEN-END:variables
    private static String getString(String key) {
        return NbBundle.getMessage(ProjectPropPanel.class, key);
    }
}
