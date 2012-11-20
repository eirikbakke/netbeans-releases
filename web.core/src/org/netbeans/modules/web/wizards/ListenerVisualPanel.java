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

package org.netbeans.modules.web.wizards;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import org.netbeans.api.j2ee.core.Profile;
import org.openide.util.NbBundle;
import org.netbeans.modules.web.api.webmodule.WebModule;

/** A single panel for a wizard - the GUI portion.
 *
 * @author  mk115033
 */
public class ListenerVisualPanel extends javax.swing.JPanel {

    /** The wizard panel descriptor associated with this GUI panel.
     * If you need to fire state changes or something similar, you can
     * use this handle to do so.
     */
    private ListenerPanel wizardPanel;
    /** Create the wizard panel and set up some basic properties. */
    public ListenerVisualPanel(ListenerPanel wizardPanel, Profile j2eeVersion) {
        this.wizardPanel=wizardPanel;
        initComponents();
        
        if (!Profile.JAVA_EE_6_FULL.equals(j2eeVersion)&& 
                !Profile.JAVA_EE_6_WEB.equals(j2eeVersion))
        {
            remove( jCheckBox1 );
        }
        
        
        // Provide a name in the title bar.
        setName(NbBundle.getMessage(ListenerVisualPanel.class, "TITLE_listenerWizardPanel"));
        /*
        // Optional: provide a special description for this pane.
        // You must have turned on WizardDescriptor.WizardPanel_helpDisplayed
        // (see descriptor in standard iterator template for an example of this).
        try {
            putClientProperty (WizardDescriptor.PROP_HELP_URL, // NOI18N
                new URL ("nbresloc:/org/netbeans/modules/web/wizards/ListenerPanelVisualHelp.html")); // NOI18N
        } catch (MalformedURLException mfue) {
            throw new IllegalStateException (mfue.toString ());
        }
         */
        // a11y part
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(ListenerVisualPanel.class, "A11Y_DESC_listenerPanel"));
        jCheckBox1.getAccessibleContext().setAccessibleName(jCheckBox1.getText());
        cb1.getAccessibleContext().setAccessibleName(cb1.getText());
        cb2.getAccessibleContext().setAccessibleName(cb2.getText());
        cb3.getAccessibleContext().setAccessibleName(cb3.getText());
        cb4.getAccessibleContext().setAccessibleName(cb4.getText());
        
        // disable request listeners in j2ee1.3
        // TODO PetrS Remove this once 1.3 is dropped!
        if (Profile.J2EE_13.equals(j2eeVersion)) {
            cb5.setEnabled(false);
            cb6.setEnabled(false);
        }

        if (j2eeVersion == Profile.JAVA_EE_6_FULL || j2eeVersion == Profile.JAVA_EE_6_WEB) {
            jCheckBox1.setSelected(false);
        }

        jCheckBox1.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                ListenerVisualPanel.this.wizardPanel.fireChangeEvent();
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

        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        labSelectionTitle = new javax.swing.JLabel();
        labDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        cb1 = new javax.swing.JCheckBox();
        cb2 = new javax.swing.JCheckBox();
        cb3 = new javax.swing.JCheckBox();
        cb4 = new javax.swing.JCheckBox();
        cb5 = new javax.swing.JCheckBox();
        cb6 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();

        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        jCheckBox1.setMnemonic(org.openide.util.NbBundle.getMessage(ListenerVisualPanel.class, "LBL_addToDD_Mnem").charAt(0));
        jCheckBox1.setSelected(true);
        jCheckBox1.setText(org.openide.util.NbBundle.getMessage(ListenerVisualPanel.class, "LBL_addtodd")); // NOI18N
        jCheckBox1.setMargin(new java.awt.Insets(0, 2, 0, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(jCheckBox1, gridBagConstraints);
        jCheckBox1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ListenerVisualPanel.class, "A11Y_DESC_addListenerToDD")); // NOI18N

        jPanel2.setLayout(new java.awt.GridBagLayout());

        labSelectionTitle.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTL_listenerSelection")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(labSelectionTitle, gridBagConstraints);

        labDescription.setDisplayedMnemonic(org.openide.util.NbBundle.getMessage(ListenerVisualPanel.class, "A11Y_Description_mnem").charAt(0));
        labDescription.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_description")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(labDescription, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        description.setEditable(false);
        description.setLineWrap(true);
        description.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_contextListener")); // NOI18N
        description.setWrapStyleWord(true);
        description.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        description.setFocusable(false);
        description.setOpaque(false);
        jScrollPane1.setViewportView(description);
        description.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_description")); // NOI18N
        description.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ListenerVisualPanel.class, "A11Y_DESC_Description")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        cb1.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_ContextListener_Mnemonic").charAt(0));
        cb1.setSelected(true);
        cb1.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_contextListener")); // NOI18N
        cb1.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_contextListener")); // NOI18N
        cb1.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cb1StateChanged(evt);
            }
        });
        cb1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb1FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb1, gridBagConstraints);
        cb1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_contextListener")); // NOI18N
        cb1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_contextListener")); // NOI18N

        cb2.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_ContextAttrListener_Mnemonic").charAt(0));
        cb2.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_contextAttrListener")); // NOI18N
        cb2.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_contextAttrListener")); // NOI18N
        cb2.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cb2StateChanged(evt);
            }
        });
        cb2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb2FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb2, gridBagConstraints);
        cb2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_contextAttrListener")); // NOI18N
        cb2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_contextAttrListener")); // NOI18N

        cb3.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_SessionListener_Mnemonic").charAt(0));
        cb3.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_sessionListener")); // NOI18N
        cb3.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_sessionListener")); // NOI18N
        cb3.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cb3StateChanged(evt);
            }
        });
        cb3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb3FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb3, gridBagConstraints);
        cb3.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_sessionListener")); // NOI18N
        cb3.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_sessionListener")); // NOI18N

        cb4.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_SessionAttrListener_Mnemonic").charAt(0));
        cb4.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_sessionAttrListener")); // NOI18N
        cb4.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_sessionAttrListener")); // NOI18N
        cb4.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cb4StateChanged(evt);
            }
        });
        cb4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb4FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb4, gridBagConstraints);
        cb4.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_sessionAttrListener")); // NOI18N
        cb4.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_sessionAttrListener")); // NOI18N

        cb5.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestListener_Mnemonic").charAt(0));
        cb5.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestListener")); // NOI18N
        cb5.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_requestListener_short")); // NOI18N
        cb5.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb5FocusGained(evt);
            }
        });
        cb5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb5ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb5, gridBagConstraints);
        cb5.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestListener")); // NOI18N
        cb5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_requestListener")); // NOI18N

        cb6.setMnemonic(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestAttrListener_Mnemonic").charAt(0));
        cb6.setText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestAttrListener")); // NOI18N
        cb6.setToolTipText(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_requestAttrListener_short")); // NOI18N
        cb6.setMargin(new java.awt.Insets(0, 2, 0, 2));
        cb6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cb6FocusGained(evt);
            }
        });
        cb6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb6ItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(cb6, gridBagConstraints);
        cb6.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("LBL_RequestAttrListener")); // NOI18N
        cb6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(ListenerVisualPanel.class).getString("TTT_requestAttrListener")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cb6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb6ItemStateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb6ItemStateChanged

    private void cb5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cb5ItemStateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb5ItemStateChanged

    private void cb6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb6FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_requestAttrListener"));
    }//GEN-LAST:event_cb6FocusGained

    private void cb5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb5FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_requestListener"));
    }//GEN-LAST:event_cb5FocusGained

    private void cb4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb4FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_sessionAttrListener"));
    }//GEN-LAST:event_cb4FocusGained

    private void cb3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb3FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_sessionListener"));
    }//GEN-LAST:event_cb3FocusGained

    private void cb2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb2FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_contextAttrListener"));
    }//GEN-LAST:event_cb2FocusGained

    private void cb1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cb1FocusGained
        description.setText(NbBundle.getMessage(ListenerVisualPanel.class,"TTT_contextListener"));
    }//GEN-LAST:event_cb1FocusGained

    private void cb3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cb3StateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb3StateChanged

    private void cb2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cb2StateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb2StateChanged

    private void cb1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cb1StateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb1StateChanged

    private void cb4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cb4StateChanged
        wizardPanel.fireChangeEvent();
    }//GEN-LAST:event_cb4StateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cb1;
    private javax.swing.JCheckBox cb2;
    private javax.swing.JCheckBox cb3;
    private javax.swing.JCheckBox cb4;
    private javax.swing.JCheckBox cb5;
    private javax.swing.JCheckBox cb6;
    private javax.swing.JTextArea description;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labDescription;
    private javax.swing.JLabel labSelectionTitle;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = 8091384420711061719L;    
    
    boolean createElementInDD (){
        return jCheckBox1.isSelected();
    }
    
    boolean isContextListener() {
        return cb1.isSelected();
    }
    
    boolean isContextAttrListener() {
        return cb2.isSelected();
    }
    
    boolean isSessionListener() {
        return cb3.isSelected();
    }
    
    boolean isSessionAttrListener() {
        return cb4.isSelected();
    }
    
    boolean isRequestListener() {
        return cb5.isSelected();
    }
    
    boolean isRequestAttrListener() {
        return cb6.isSelected();
    }
}
