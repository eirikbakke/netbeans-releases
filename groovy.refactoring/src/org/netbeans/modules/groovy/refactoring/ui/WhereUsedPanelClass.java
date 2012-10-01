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
package org.netbeans.modules.groovy.refactoring.ui;

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.groovy.refactoring.findusages.model.RefactoringElement;

/**
 * Copied from Java Refactoring module and changed with respect to Groovy and
 * CSL specifics.
 *
 * @author Ralph Ruijs <ralphbenjamin@netbeans.org>
 * @author Martin Janicek <mjanicek@netbeans.org>
 */
public class WhereUsedPanelClass extends WhereUsedPanel.WhereUsedInnerPanel {

    public WhereUsedPanelClass() {
        initComponents();
    }

    @Override
    void initialize(final RefactoringElement element) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Dimension preferredSize = label.getPreferredSize();
                label.setText(element.getName());
                label.setIcon(UiUtils.getElementIcon(element.getKind(), element.getModifiers()));
                label.setPreferredSize(preferredSize);
                label.setMinimumSize(preferredSize);
            }
        });
    }

    public boolean isClassSubTypes() {
        return c_subclasses.isSelected();
    }

    public boolean isClassSubTypesDirectOnly() {
        return c_directOnly.isSelected();
    }

    public boolean isClassFindUsages() {
        return c_usages.isSelected();
    }

    @Override
    public boolean isSearchInComments() {
        return searchInComments.isSelected();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        c_subclasses = new javax.swing.JRadioButton();
        c_usages = new javax.swing.JRadioButton();
        c_directOnly = new javax.swing.JRadioButton();
        searchInComments = new javax.swing.JCheckBox();
        label = new javax.swing.JLabel();
        lbl_usagesof = new javax.swing.JLabel();

        buttonGroup.add(c_subclasses);
        org.openide.awt.Mnemonics.setLocalizedText(c_subclasses, org.openide.util.NbBundle.getMessage(WhereUsedPanelClass.class, "LBL_FindAllSubtypes")); // NOI18N

        buttonGroup.add(c_usages);
        c_usages.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(c_usages, org.openide.util.NbBundle.getMessage(WhereUsedPanelClass.class, "LBL_FindUsages")); // NOI18N

        buttonGroup.add(c_directOnly);
        org.openide.awt.Mnemonics.setLocalizedText(c_directOnly, org.openide.util.NbBundle.getMessage(WhereUsedPanelClass.class, "LBL_FindDirectSubtypesOnly")); // NOI18N

        searchInComments.setSelected(((Boolean) RefactoringModule.getOption("searchInComments.whereUsed", Boolean.FALSE)).booleanValue());
        org.openide.awt.Mnemonics.setLocalizedText(searchInComments, org.openide.util.NbBundle.getBundle(WhereUsedPanelClass.class).getString("LBL_SearchInComents")); // NOI18N

        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/groovy/refactoring/resources/warning_16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(label, "<<Element>>"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbl_usagesof, org.openide.util.NbBundle.getMessage(WhereUsedPanelClass.class, "LBL_UsagesOfElement")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lbl_usagesof)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchInComments)
                    .addComponent(c_usages)
                    .addComponent(c_subclasses)
                    .addComponent(c_directOnly)
                    .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_usagesof)
                    .addComponent(label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchInComments)
                .addGap(6, 6, 6)
                .addComponent(c_usages)
                .addGap(6, 6, 6)
                .addComponent(c_subclasses)
                .addGap(6, 6, 6)
                .addComponent(c_directOnly)
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButton c_directOnly;
    private javax.swing.JRadioButton c_subclasses;
    private javax.swing.JRadioButton c_usages;
    private javax.swing.JLabel label;
    private javax.swing.JLabel lbl_usagesof;
    private javax.swing.JCheckBox searchInComments;
    // End of variables declaration//GEN-END:variables
}
