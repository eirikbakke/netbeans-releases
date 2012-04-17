/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.ui;

import static org.netbeans.modules.java.ui.FmtOptions.*;
import static org.netbeans.modules.java.ui.FmtOptions.CategorySupport.OPTION_ID;
import org.netbeans.modules.options.editor.spi.PreferencesCustomizer;

/**
 *
 * @author Dusan Balek
 */
public class FmtComments extends javax.swing.JPanel implements Runnable {

    /** Creates new form FmtComments */
    public FmtComments() {
        initComponents();
        enableCommentFormatCheckBox.putClientProperty(OPTION_ID, enableCommentFormatting);
        enableBlockCommentFormatCheckBox.putClientProperty(OPTION_ID, enableBlockCommentFormatting);
        addLeadingStarCheckBox.putClientProperty(OPTION_ID, addLeadingStarInComment);
        wrapCommentTextCheckBox.putClientProperty(OPTION_ID, wrapCommentText);
        wrapOneLineCheckBox.putClientProperty(OPTION_ID, wrapOneLineComment);
        preserveNewLinesCheckBox.putClientProperty(OPTION_ID, preserveNewLinesInComments);
        blankLineAfterDescCheckBox.putClientProperty(OPTION_ID, blankLineAfterJavadocDescription);
        blankLineAfterParamsCheckBox.putClientProperty(OPTION_ID, blankLineAfterJavadocParameterDescriptions);
        blankLineAfterReturnCheckBox.putClientProperty(OPTION_ID, blankLineAfterJavadocReturnTag);
        generatePCheckBox.putClientProperty(OPTION_ID, generateParagraphTagOnBlankLines);
        alignParamsCheckBox.putClientProperty(OPTION_ID, alignJavadocParameterDescriptions);
        alignReturnCheckBox.putClientProperty(OPTION_ID, alignJavadocReturnDescription);
        alignExceptionsCheckBox.putClientProperty(OPTION_ID, alignJavadocExceptionDescriptions);
    }

    public static PreferencesCustomizer.Factory getController() {
        return new CategorySupport.Factory("comments", FmtComments.class, //NOI18N
                org.openide.util.NbBundle.getMessage(FmtComments.class, "SAMPLE_Comments"), // NOI18N
                new String[] { FmtOptions.rightMargin, "45" }, //NOI18N
                new String[] { FmtOptions.blankLinesBeforeClass, "0" }); //NOI18N
    }    

    @Override
    public void run() {
        enableControls(enableCommentFormatCheckBox.isSelected());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enableCommentFormatCheckBox = new javax.swing.JCheckBox();
        enableBlockCommentFormatCheckBox = new javax.swing.JCheckBox();
        generalLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        addLeadingStarCheckBox = new javax.swing.JCheckBox();
        wrapCommentTextCheckBox = new javax.swing.JCheckBox();
        wrapOneLineCheckBox = new javax.swing.JCheckBox();
        preserveNewLinesCheckBox = new javax.swing.JCheckBox();
        javadocLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        blankLineAfterDescCheckBox = new javax.swing.JCheckBox();
        blankLineAfterParamsCheckBox = new javax.swing.JCheckBox();
        blankLineAfterReturnCheckBox = new javax.swing.JCheckBox();
        generatePCheckBox = new javax.swing.JCheckBox();
        alignParamsCheckBox = new javax.swing.JCheckBox();
        alignReturnCheckBox = new javax.swing.JCheckBox();
        alignExceptionsCheckBox = new javax.swing.JCheckBox();

        setName(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_Comments")); // NOI18N
        setOpaque(false);

        enableCommentFormatCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_enableCommentFormat")); // NOI18N
        enableCommentFormatCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableCommentFormatCheckBoxActionPerformed(evt);
            }
        });

        enableBlockCommentFormatCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_enableBlockCommentFormat")); // NOI18N

        generalLabel.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_generalLabel")); // NOI18N

        addLeadingStarCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_addLeadingStar")); // NOI18N

        wrapCommentTextCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_wrapCommentText")); // NOI18N

        wrapOneLineCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_wrapOneLineCheckBox")); // NOI18N

        preserveNewLinesCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_preserveNewLinesCheckBox")); // NOI18N

        javadocLabel.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_javadocLabel")); // NOI18N

        blankLineAfterDescCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_blankLineAfterDescCheckBox")); // NOI18N

        blankLineAfterParamsCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_blankLineAfterParamsCheckBox")); // NOI18N

        blankLineAfterReturnCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_blankLineAfterReturnCheckBox")); // NOI18N

        generatePCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_generatePCheckBox")); // NOI18N

        alignParamsCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_alignParamsCheckBox")); // NOI18N

        alignReturnCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_alignReturnCheckBox")); // NOI18N

        alignExceptionsCheckBox.setText(org.openide.util.NbBundle.getMessage(FmtComments.class, "LBL_doc_alignExceptionsCheckBox")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(javadocLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enableCommentFormatCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(wrapCommentTextCheckBox)
                                    .addComponent(addLeadingStarCheckBox)
                                    .addComponent(wrapOneLineCheckBox)
                                    .addComponent(preserveNewLinesCheckBox)
                                    .addComponent(blankLineAfterParamsCheckBox)
                                    .addComponent(blankLineAfterDescCheckBox)
                                    .addComponent(blankLineAfterReturnCheckBox)
                                    .addComponent(generatePCheckBox)
                                    .addComponent(alignParamsCheckBox)
                                    .addComponent(alignReturnCheckBox)
                                    .addComponent(alignExceptionsCheckBox)
                                    .addComponent(enableBlockCommentFormatCheckBox))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(generalLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(enableCommentFormatCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableBlockCommentFormatCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generalLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLeadingStarCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wrapCommentTextCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wrapOneLineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preserveNewLinesCheckBox)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(javadocLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blankLineAfterDescCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blankLineAfterParamsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blankLineAfterReturnCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generatePCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignParamsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignReturnCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alignExceptionsCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void enableCommentFormatCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableCommentFormatCheckBoxActionPerformed
    enableControls(enableCommentFormatCheckBox.isSelected());
}//GEN-LAST:event_enableCommentFormatCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox addLeadingStarCheckBox;
    private javax.swing.JCheckBox alignExceptionsCheckBox;
    private javax.swing.JCheckBox alignParamsCheckBox;
    private javax.swing.JCheckBox alignReturnCheckBox;
    private javax.swing.JCheckBox blankLineAfterDescCheckBox;
    private javax.swing.JCheckBox blankLineAfterParamsCheckBox;
    private javax.swing.JCheckBox blankLineAfterReturnCheckBox;
    private javax.swing.JCheckBox enableBlockCommentFormatCheckBox;
    private javax.swing.JCheckBox enableCommentFormatCheckBox;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JCheckBox generatePCheckBox;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel javadocLabel;
    private javax.swing.JCheckBox preserveNewLinesCheckBox;
    private javax.swing.JCheckBox wrapCommentTextCheckBox;
    private javax.swing.JCheckBox wrapOneLineCheckBox;
    // End of variables declaration//GEN-END:variables

    private void enableControls(boolean b) {
        enableBlockCommentFormatCheckBox.setEnabled(b);
        addLeadingStarCheckBox.setEnabled(b);
        alignExceptionsCheckBox.setEnabled(b);
        alignParamsCheckBox.setEnabled(b);
        alignReturnCheckBox.setEnabled(b);
        blankLineAfterDescCheckBox.setEnabled(b);
        blankLineAfterParamsCheckBox.setEnabled(b);
        blankLineAfterReturnCheckBox.setEnabled(b);
        generatePCheckBox.setEnabled(b);
        preserveNewLinesCheckBox.setEnabled(b);
        wrapCommentTextCheckBox.setEnabled(b);
        wrapOneLineCheckBox.setEnabled(b);
    }
}
