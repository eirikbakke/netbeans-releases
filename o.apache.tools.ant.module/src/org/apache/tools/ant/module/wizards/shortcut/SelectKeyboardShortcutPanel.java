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

package org.apache.tools.ant.module.wizards.shortcut;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

final class SelectKeyboardShortcutPanel extends javax.swing.JPanel implements KeyListener {

    private KeyStroke stroke = null;
    
    private SelectKeyboardShortcutWizardPanel wiz;
    
    /** Create the wizard panel component and set up some basic properties. */
    public SelectKeyboardShortcutPanel (SelectKeyboardShortcutWizardPanel wiz) {
        this.wiz = wiz;
        initComponents ();
	initAccessibility ();
        // Provide a name in the title bar.
        setName (NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "SKSP_LBL_select_shortcut_to_add"));
        testField.addKeyListener (this);
    }

    
    private void initAccessibility () {        
        testField.getAccessibleContext().setAccessibleName(NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "ACSN_LBL_type_here")); 
        testField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage (SelectKeyboardShortcutPanel.class, "ACSD_LBL_type_here")); 
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_TEXT_press_any_key_seq"));
    }
    
    // --- VISUAL DESIGN OF PANEL ---

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        hintsArea = new javax.swing.JTextArea();
        mainPanel = new javax.swing.JPanel();
        testField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        hintsArea.setBackground(new java.awt.Color(204, 204, 204));
        hintsArea.setEditable(false);
        hintsArea.setFont(javax.swing.UIManager.getFont ("Label.font"));
        hintsArea.setForeground(new java.awt.Color(102, 102, 153));
        hintsArea.setLineWrap(true);
        hintsArea.setText(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_TEXT_press_any_key_seq"));
        hintsArea.setWrapStyleWord(true);
        hintsArea.setDisabledTextColor(javax.swing.UIManager.getColor ("Label.foreground"));
        hintsArea.setEnabled(false);
        hintsArea.setOpaque(false);
        add(hintsArea, java.awt.BorderLayout.NORTH);

        testField.setColumns(15);
        testField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        testField.setText(NbBundle.getMessage(SelectKeyboardShortcutPanel.class, "SKSP_LBL_type_here"));
        mainPanel.add(testField);

        add(mainPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea hintsArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField testField;
    // End of variables declaration//GEN-END:variables
    
    // KeyListener:

    public void keyPressed (KeyEvent e) {
        // XXX ideally make TAB switch focus, rather than be handled...
        stroke = KeyStroke.getKeyStroke (e.getKeyCode (), e.getModifiers ());
        testField.setText (Utilities.keyToString (stroke));
        wiz.fireChangeEvent ();
        e.consume ();
    }
    public void keyReleased (KeyEvent e) {
        e.consume ();
    }
    public void keyTyped (KeyEvent e) {
        e.consume ();
    }
    
    public static class SelectKeyboardShortcutWizardPanel implements WizardDescriptor.Panel<ShortcutWizard> {

        private SelectKeyboardShortcutPanel panel = null;
        private FileObject shortcutsFolder = null;
        
        public Component getComponent () {
            return getPanel(); 
        }
        
        private SelectKeyboardShortcutPanel getPanel() {
            if (panel == null) {
                panel = new SelectKeyboardShortcutPanel(this);
            }
            return panel;
        }

        public HelpCtx getHelp () {
            return HelpCtx.DEFAULT_HELP;
        }

        public boolean isValid () {
            if (shortcutsFolder == null)
                shortcutsFolder = Repository.getDefault ().getDefaultFileSystem ().findResource ("Shortcuts"); // NOI18N
            return (getPanel().stroke != null) &&
                   (shortcutsFolder.getFileObject(Utilities.keyToString(getPanel().stroke), "instance") == null) && // NOI18N
                   (shortcutsFolder.getFileObject(Utilities.keyToString(getPanel().stroke), "xml") == null); // NOI18N
        }

        private final ChangeSupport cs = new ChangeSupport(this);
        public final void addChangeListener (ChangeListener l) {
            cs.addChangeListener(l);
        }
        public final void removeChangeListener (ChangeListener l) {
            cs.removeChangeListener(l);
        }
        protected final void fireChangeEvent () {
            cs.fireChange();
        }

        public void readSettings(ShortcutWizard wiz) {
            // XXX later...
        }
        public void storeSettings(ShortcutWizard wiz) {
            wiz.putProperty(ShortcutWizard.PROP_STROKE, getPanel().stroke);
        }
    }
    
}
