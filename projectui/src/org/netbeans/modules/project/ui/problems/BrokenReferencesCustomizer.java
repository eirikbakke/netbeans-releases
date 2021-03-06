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

package org.netbeans.modules.project.ui.problems;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ui.ProjectProblemsProvider;
import org.openide.NotificationLineSupport;

import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;

/**
 *
 * @author David Konecny
 * @author Tomas Zezula
 */
public class BrokenReferencesCustomizer extends javax.swing.JPanel {

    private static final RequestProcessor RP = new RequestProcessor(BrokenReferencesCustomizer.class);

    private final BrokenReferencesModel model;
    //@GuardedBy("this")
    private NotificationLineSupport nls;
    
    /** Creates new form BrokenReferencesCustomizer */
    public BrokenReferencesCustomizer(BrokenReferencesModel model) {
        initComponents();
        this.model = model;
        errorList.setModel(model);
        errorList.setSelectedIndex(0);
        errorList.setCellRenderer(new ListCellRendererImpl());
        errorList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NonNull final MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    final int index = errorList.locationToIndex(evt.getPoint());
                    if (index != -1 && fix.isEnabled()) {
                        fixActionImpl(errorList.getModel().getElementAt(index));
                    }
                }
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        errorListLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorList = new javax.swing.JList();
        fix = new javax.swing.JButton();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(550, 350));
        setLayout(new java.awt.GridBagLayout());

        errorListLabel.setLabelFor(errorList);
        org.openide.awt.Mnemonics.setLocalizedText(errorListLabel, org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_List")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 3, 0);
        add(errorListLabel, gridBagConstraints);
        errorListLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "ACSD_BrokenLinksCustomizer_List")); // NOI18N

        errorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        errorList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                errorListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(errorList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(fix, org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_Fix")); // NOI18N
        fix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 12);
        add(fix, gridBagConstraints);
        fix.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "ACSD_BrokenLinksCustomizer_Fix")); // NOI18N

        descriptionLabel.setLabelFor(description);
        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_Description")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 3, 0);
        add(descriptionLabel, gridBagConstraints);
        descriptionLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "ACSD_BrokenLinksCustomizer_Description")); // NOI18N

        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        jScrollPane2.setViewportView(description);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 60;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(jScrollPane2, gridBagConstraints);

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "ACSN_BrokenReferencesCustomizer")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(BrokenReferencesCustomizer.class, "ACSD_BrokenReferencesCustomizer")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void errorListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_errorListValueChanged
        updateSelection();
    }//GEN-LAST:event_errorListValueChanged

    
    private void fixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixActionPerformed
        fixActionImpl(errorList.getSelectedValue());
    }//GEN-LAST:event_fixActionPerformed

    private void fixActionImpl(@NonNull final Object value) {
        if (!(value instanceof BrokenReferencesModel.ProblemReference)) {
            return;
        }
        final BrokenReferencesModel.ProblemReference or = (BrokenReferencesModel.ProblemReference) value;
        errorList.setEnabled(false);
        fix.setEnabled(false);
        Future<ProjectProblemsProvider.Result> becomesResult = null;
        try {
            becomesResult = or.problem.resolve();
            assert becomesResult != null;
        } finally {
            if (becomesResult == null) {
                updateAfterResolve(null);
            } else if (becomesResult.isDone()) {
                ProjectProblemsProvider.Result result = null;
                try {
                    result = becomesResult.get();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    updateAfterResolve(result);
                }
            } else {
                final Future<ProjectProblemsProvider.Result> becomesResultFin = becomesResult;
                RP.post(new Runnable() {
                    @Override
                    public void run() {
                        final AtomicReference<ProjectProblemsProvider.Result> result = new AtomicReference<ProjectProblemsProvider.Result>();
                        try {
                            result.set(becomesResultFin.get());
                        } catch (InterruptedException ie) {
                            Exceptions.printStackTrace(ie);
                        } catch (ExecutionException ee) {
                            Exceptions.printStackTrace(ee);
                        } finally {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    updateAfterResolve(result.get());
                                }
                            });
                        }
                    }
                });
            }
        }
        
    }

    private void updateAfterResolve(@NullAllowed final ProjectProblemsProvider.Result result) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException();
        }
        model.refresh();
        errorList.setEnabled(true);
        updateSelection();
        if (result != null) {
            notify(result.getStatus(), result.getMessage());
        }
    }

    @Messages("LBL_BrokenLinksCustomizer_Problem_Was_Resolved=This problem was resolved")
    private void updateSelection() {
        final Object value = errorList.getSelectedValue();
        if (value instanceof BrokenReferencesModel.ProblemReference) {
            final BrokenReferencesModel.ProblemReference reference = (BrokenReferencesModel.ProblemReference) value;
            if (!reference.resolved) {
                description.setText(reference.problem.getDescription());                
                fix.setEnabled(reference.problem.isResolvable());
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                   public void run() {
                       jScrollPane2.getVerticalScrollBar().setValue(0);
                   }
                });
            } else {
                description.setText(Bundle.LBL_BrokenLinksCustomizer_Problem_Was_Resolved());
                // Leave the button always enabled so that user can alter 
                // resolved reference. Especially needed for automatically
                // resolved JAR references.
                fix.setEnabled(reference.problem.isResolvable());
            }
        } else {
            description.setText("");
            fix.setEnabled(false);
        }
        clearNotification();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea description;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JList errorList;
    private javax.swing.JLabel errorListLabel;
    private javax.swing.JButton fix;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    private static final @StaticResource String BROKEN_REF = "org/netbeans/modules/project/ui/resources/broken-reference.gif";
    private static final @StaticResource String RESOLVED_REF = "org/netbeans/modules/project/ui/resources/resolved-reference.gif";

    synchronized void setNotificationLineSupport(@NullAllowed final NotificationLineSupport notificationLineSupport) {
        Parameters.notNull("notificationLineSupport", notificationLineSupport); //NOI18N
        nls = notificationLineSupport;
    }

    private synchronized void notify (
        @NonNull final ProjectProblemsProvider.Status status,
        @NullAllowed final String message) {
        if (message != null) {
            switch (status) {
                case RESOLVED:
                    nls.setInformationMessage(message);
                    break;
                case RESOLVED_WITH_WARNING:
                    nls.setWarningMessage(message);
                    break;
                case UNRESOLVED:
                    nls.setErrorMessage(message);
                    break;
                default:
                    throw new IllegalStateException(status.name());
            }
        }
    }

    private synchronized void clearNotification() {
        //May be null when called from the constructor, before initialization
        if (nls != null) {
            nls.clearMessages();
        }
    }

    private static class ListCellRendererImpl extends DefaultListCellRenderer {                        
        
        public @Override Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof BrokenReferencesModel.ProblemReference) {
                BrokenReferencesModel.ProblemReference problemRef = (BrokenReferencesModel.ProblemReference) value;
                super.getListCellRendererComponent( list, problemRef.getDisplayName(), index, isSelected, cellHasFocus );
                if (problemRef.resolved) {
                    setIcon(ImageUtilities.loadImageIcon(RESOLVED_REF, false));
                } else {
                    setIcon(ImageUtilities.loadImageIcon(BROKEN_REF, false));
                }
            }
            return this;
        }
    }
    
}
