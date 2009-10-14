/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.j2ee.sun.ddloaders.multiview.ejb;

import org.netbeans.modules.j2ee.sun.dd.api.ASDDVersion;
import org.netbeans.modules.j2ee.sun.dd.api.ejb.EnterpriseBeans;
import org.netbeans.modules.j2ee.sun.dd.api.ejb.SunEjbJar;
import org.netbeans.modules.j2ee.sun.ddloaders.SunDescriptorDataObject;
import org.netbeans.modules.j2ee.sun.ddloaders.Utils;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.BaseSectionNodeInnerPanel;
import org.netbeans.modules.j2ee.sun.ddloaders.multiview.TextItemEditorModel;
import org.netbeans.modules.xml.multiview.ItemEditorHelper;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;
import org.openide.util.NbBundle;


/**
 * 
 * @author Peter Williams
 */
public class SunEjbJarDetailsPanel extends BaseSectionNodeInnerPanel {

    private SunEjbJar sunEjbJar;

    public SunEjbJarDetailsPanel(SectionNodeView sectionNodeView, final SunEjbJar sunEjbJar, final ASDDVersion version) {
        super(sectionNodeView, version);
        this.sunEjbJar = sunEjbJar;

        initComponents();
        initUserComponents(sectionNodeView);
    }

    private void initUserComponents(SectionNodeView sectionNodeView) {
        SunDescriptorDataObject dataObject = (SunDescriptorDataObject) sectionNodeView.getDataObject();
        XmlMultiViewDataSynchronizer synchronizer = dataObject.getModelSynchronizer();
        addRefreshable(new ItemEditorHelper(nameText, new NameEditorModel(synchronizer)));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        nameText = new javax.swing.JTextField();

        setAlignmentX(LEFT_ALIGNMENT);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        nameLabel.setLabelFor(nameText);
        nameLabel.setText(NbBundle.getMessage(SunEjbJarDetailsPanel.class, "LBL_Name_1")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 0);
        add(nameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 5, 5);
        add(nameText, gridBagConstraints);
        nameText.getAccessibleContext().setAccessibleName(NbBundle.getMessage(SunEjbJarDetailsPanel.class, "ACSN_Name")); // NOI18N
        nameText.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SunEjbJarDetailsPanel.class, "ACSD_Name")); // NOI18N

        getAccessibleContext().setAccessibleName(NbBundle.getMessage(SunEjbJarDetailsPanel.class, "ACSN_SunEjbJarDetailsPanel")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SunEjbJarDetailsPanel.class, "ACSD_SunEjbJarDetailsPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameText;
    // End of variables declaration//GEN-END:variables

    // Model classes for handling updates to the fields (is there a better or
    // more generic way to do this?)
    private class NameEditorModel extends TextItemEditorModel {

        public NameEditorModel(XmlMultiViewDataSynchronizer synchronizer) {
            super(synchronizer, true, true);
        }

        protected String getValue() {
            EnterpriseBeans eb = sunEjbJar.getEnterpriseBeans();
            return (eb != null) ? eb.getName() : null;
        }

        protected void setValue(String value) {
            EnterpriseBeans eb = sunEjbJar.getEnterpriseBeans();
            value = Utils.notEmpty(value) ? value : null;

            if(eb == null && value != null) {
                eb = sunEjbJar.newEnterpriseBeans();
                sunEjbJar.setEnterpriseBeans(eb);
            }

            if(eb != null) {
                eb.setName(value);
            }
        }
    }

}
