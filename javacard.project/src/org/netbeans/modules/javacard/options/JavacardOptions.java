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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2009 Sun
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
package org.netbeans.modules.javacard.options;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.javacard.common.GuiUtils;
import org.netbeans.modules.javacard.common.Utils;
import org.netbeans.modules.options.java.api.JavaOptions;
import org.netbeans.spi.options.OptionsPanelController;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.conversion.Converter;
import org.netbeans.validation.api.ui.ValidationUI;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.openide.util.NbBundle;

@OptionsPanelController.Keywords(keywords={"#KW_JavaCardOptions"}, location=JavaOptions.JAVA, tabTitle= "#JavaCardOptionsCategory_Title")
final class JavacardOptions extends javax.swing.JPanel implements ValidationUI, DocumentListener {

    private final JavacardOptionsPanelController controller;
    private final SwingValidationGroup group = SwingValidationGroup.create(this);

    @SuppressWarnings("unchecked") //NOI18N
    JavacardOptions(JavacardOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        GuiUtils.filterNonHexadecimalKeys(jTextField1);
        
        Validator<Document> v = Converter.find(String.class, Document.class).convert(
                StringValidators.REQUIRE_NON_EMPTY_STRING.trim(),
                StringValidators.NO_WHITESPACE.trim(),
                StringValidators.VALID_HEXADECIMAL_NUMBER.trim(),
                StringValidators.trimString(StringValidators.maxLength(10)),
                StringValidators.trimString(StringValidators.minLength(10)));

        group.add(jTextField1, v);
        jTextField1.setText (jLabel1.getText());
    }

    boolean valid;
    boolean inChange;

    private void change() {
        if (inChange) {
            return;
        }
        inChange = true;
        controller.changed();
        inChange = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        problemLabel = (JLabel) group.createProblemLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(JavacardOptions.class, "JavacardOptions.jLabel1.text")); // NOI18N

        jTextField1.setText(NbBundle.getMessage(JavacardOptions.class, "JavacardOptions.jTextField1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(JavacardOptions.class, "JavacardOptions.jLabel2.text")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        problemLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        org.openide.awt.Mnemonics.setLocalizedText(problemLabel, NbBundle.getMessage(JavacardOptions.class, "JavacardOptions.problemLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(problemLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                .addComponent(problemLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    void load() {
        jTextField1.setText(Utils.getDefaultRIDasString());
    }

    void store() {
        if (valid()) {
            Utils.setDefaultRID(jTextField1.getText());
        }
    }

    boolean valid() {
        return valid;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel problemLabel;
    // End of variables declaration//GEN-END:variables

    public void insertUpdate(DocumentEvent e) {
        change();
    }

    public void removeUpdate(DocumentEvent e) {
        change();
    }

    public void changedUpdate(DocumentEvent e) {
        change();
    }

    @Override public void clearProblem() {
        valid = true;
    }

    @Override public void showProblem(Problem arg0) {
        valid = false;
    }
}
