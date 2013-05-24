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

package org.netbeans.modules.cnd.editor.options;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.cnd.editor.api.CodeStyle;
import org.netbeans.modules.cnd.utils.MIMENames;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Alexander Simon
 */
public class ManageStylesPanel extends javax.swing.JPanel
             implements ListSelectionListener, KeyListener, MouseListener {
    private CodeStyle.Language language;
    private Map<String, PreviewPreferences> preferences;

    /** Creates new form ManageStylesPanel */
    public ManageStylesPanel(CodeStyle.Language language,
            Map<String,PreviewPreferences> allPreferences) {
        this.language = language;
        this.preferences = allPreferences;
        initComponents();
        initList();
    }

    private void initList(){
        initListModel();
        stylesList.addListSelectionListener(this);
        stylesList.addKeyListener(this);
	stylesList.addMouseListener(this);
	stylesList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	// Set focus.
        stylesList.setSelectedIndex(0);
	stylesList.requestFocus();
        checkSelection();
    }
    
    private void initListModel(){
        List<MyListItem> objects = new ArrayList<MyListItem>();
        for(String style : preferences.keySet()){
            objects.add(new MyListItem(style, EditorOptions.getStyleDisplayName(language,style)));
        }
        Collections.sort(objects);
        stylesList.setModel(new MyListModel(objects));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        stylesList = new javax.swing.JList();
        jSeparator1 = new javax.swing.JSeparator();
        newButton = new javax.swing.JButton();
        duplicateButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(200, 150));
        setPreferredSize(new java.awt.Dimension(250, 150));
        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(stylesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 0);
        add(jScrollPane1, gridBagConstraints);

        jSeparator1.setForeground(java.awt.SystemColor.activeCaptionBorder);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        add(jSeparator1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(newButton, org.openide.util.NbBundle.getMessage(ManageStylesPanel.class, "ManageStylesPanel.newButton.text")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        add(newButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(duplicateButton, org.openide.util.NbBundle.getMessage(ManageStylesPanel.class, "ManageStylesPanel.duplicateButton.text")); // NOI18N
        duplicateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                duplicateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        add(duplicateButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(ManageStylesPanel.class, "ManageStylesPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 6, 6);
        add(removeButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
    String prefix = "Default"; // NOI18N
    PreviewPreferences pp = preferences.get(prefix);
    String id = nextId(prefix);
    String displayName = getString("Custom_Name"); // NOI18N
    String res[] = getDisplayName(displayName, prefix, id);
    if (res != null && checkUniqueStyleName(prefix, res)) {
        String styleId = prefix+"_"+res[1]; // NOI18N
        String resourceId = styleId+"_Style_Name"; // NOI18N
        CodeStylePreferencesProvider.INSTANCE.forDocument(null, MIMENames.C_MIME_TYPE).node(EditorOptions.CODE_STYLE_NODE).put(resourceId, res[0]); // NOI18N
        PreviewPreferences np = new PreviewPreferences(pp, language, styleId);
        np.makeAllKeys(pp);
        preferences.put(styleId, np);
        initListModel();
    }
}//GEN-LAST:event_newButtonActionPerformed

private void duplicateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplicateButtonActionPerformed
    int i = stylesList.getSelectedIndex();
    if (i >= 0) {
        MyListItem item = (MyListItem) stylesList.getModel().getElementAt(i);
        String prefix = item.id; // NOI18N
        PreviewPreferences pp = preferences.get(prefix);
        String id = nextId(prefix); // NOI18N
        String displayName = NbBundle.getMessage(ManageStylesPanel.class, "CopyOfStyle", item.name); // NOI18N
        String res[] = getDisplayName(displayName, prefix, id);
        if (res != null && checkUniqueStyleName(prefix, res)) {
            String styleId = prefix+"_"+res[1]; // NOI18N
            String resourceId = styleId+"_Style_Name"; // NOI18N
            CodeStylePreferencesProvider.INSTANCE.forDocument(null, MIMENames.C_MIME_TYPE).node(EditorOptions.CODE_STYLE_NODE).put(resourceId, res[0]); // NOI18N
            PreviewPreferences np = new PreviewPreferences(pp, language, styleId);
            np.makeAllKeys(pp);
            preferences.put(styleId, np);
            initListModel();
        }
    }    
}//GEN-LAST:event_duplicateButtonActionPerformed


private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
    int i = stylesList.getSelectedIndex();
    if (i >= 0) {
        MyListItem item = (MyListItem) stylesList.getModel().getElementAt(i);
        preferences.remove(item.id);
        initListModel();
    }
}//GEN-LAST:event_removeButtonActionPerformed

    private static String getString(String key) {
        return NbBundle.getMessage(ManageStylesPanel.class, key);
    }

    private String[] getDisplayName(String previous, String prefix, String id){
        NewStyleName namePanel = new NewStyleName(previous, prefix, id);
        DialogDescriptor dd = new DialogDescriptor(namePanel, getString("EDIT_DIALOG_TITLE_TXT")); // NOI18N
        DialogDisplayer.getDefault().notify(dd);
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            return namePanel.getResult();
        }        
        return null;
    }

    private String nextId(String prefix){
        int maxId = 0;
        try {
            for (String key : CodeStylePreferencesProvider.INSTANCE.forDocument(null, MIMENames.C_MIME_TYPE).node(EditorOptions.CODE_STYLE_NODE).keys()) {// NOI18N
                if (key.endsWith("_Style_Name") && key.startsWith(prefix+"_")) { // NOI18N
                    String v = key.substring(6, key.length() - 11);
                    int n = 0;
                    try {
                        n = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        e.printStackTrace(System.err);
                    }
                    if (maxId <= n) {
                        maxId = n + 1;
                    }
                }
            }
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "" + maxId; // NOI18N
    }

    private boolean checkUniqueStyleName(String prefix, String res[]) {
        for (String key : preferences.keySet()) {
            String name = EditorOptions.getStyleDisplayName(language, key);
            if (name.equals(res[0])) {
                NotifyDescriptor descriptor = new NotifyDescriptor.Message(
                        NbBundle.getMessage(ManageStylesPanel.class, "Duplicate_Style_Warning", res[0]), // NOI18N
                        NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(descriptor);
                return false;
            }
            if (key.equals(prefix+"_"+res[1])) { // NOI18N
                NotifyDescriptor descriptor = new NotifyDescriptor.Message(
                        NbBundle.getMessage(ManageStylesPanel.class, "Duplicate_Style_Warning", res[0]), // NOI18N
                        NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(descriptor);
                return false;
            }
        }
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton duplicateButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JList stylesList;
    // End of variables declaration//GEN-END:variables

    private void checkSelection() {
        int i = stylesList.getSelectedIndex();
        if (i >= 0) {
            MyListItem item = (MyListItem) stylesList.getModel().getElementAt(i);
            newButton.setEnabled(true);
            duplicateButton.setEnabled(true);
            removeButton.setEnabled(!item.isPredefined());
        } else {
            duplicateButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        checkSelection();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        processKeyEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object ob[] = stylesList.getSelectedValues();
        if (ob.length != 1) {
            return;
        }
        if (e.getClickCount() == 2) {
            e.consume();
            //editObjectAction();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private static class MyListModel extends AbstractListModel {
        private List<MyListItem> objects;
        private MyListModel(List<MyListItem> objects){
            this.objects = objects;
        }

        @Override
        public int getSize() {
            return objects.size();
        }

        @Override
        public MyListItem getElementAt(int index) {
            return objects.get(index);
        }
    }
    
    private static class MyListItem implements Comparable<MyListItem> {
        private String id;
        private String name;
        
        private MyListItem(String id, String name){
            this.id = id;
            this.name = name;
        }

        private boolean isPredefined(){
            for(String s : EditorOptions.PREDEFINED_STYLES){
                if (s.equals(id)){
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(ManageStylesPanel.MyListItem o) {
            return name.compareTo(o.name);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MyListItem other = (MyListItem) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }
        
    }
}
