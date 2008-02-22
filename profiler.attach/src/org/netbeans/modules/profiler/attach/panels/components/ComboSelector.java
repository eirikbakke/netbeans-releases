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

package org.netbeans.modules.profiler.attach.panels.components;

import java.awt.Color;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class ComboSelector extends javax.swing.JPanel implements ListDataListener {
  private String emptyModelMessage;
  private ComboBoxModel customModel;
  private boolean isEmptyModel;

  final private ComboBoxModel emptyModel = new ComboBoxModel() {
    public void addListDataListener(ListDataListener l) {
    }
    public Object getElementAt(int index) {
      return getEmptyModelMessage();
    }
    public Object getSelectedItem() {
      return getEmptyModelMessage();
    }
    public int getSize() {
      return 1;
    }
    public void removeListDataListener(ListDataListener l) {
    }
    public void setSelectedItem(Object anItem) {
    }
  };
  private boolean emptySelectorDisabled;
  
  /** Creates new form ComboSelector */
  public ComboSelector() {
    isEmptyModel = true;
    initComponents();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        selector = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelHint = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        selector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(selector, java.awt.BorderLayout.NORTH);
        selector.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComboSelector.class, "ComboSelector.selector.AccessibleContext.accessibleName")); // NOI18N
        selector.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ComboSelector.class, "ComboSelector.selector.AccessibleContext.accessibleDescription")); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        labelHint.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        labelHint.setColumns(20);
        labelHint.setEditable(false);
        labelHint.setForeground(javax.swing.UIManager.getDefaults().getColor("Label.disabledForeground"));
        labelHint.setLineWrap(true);
        labelHint.setRows(5);
        labelHint.setText(org.openide.util.NbBundle.getMessage(ComboSelector.class, "ComboSelector.labelHint.text")); // NOI18N
        labelHint.setWrapStyleWord(true);
        labelHint.setEnabled(false);
        labelHint.setFocusable(false);
        labelHint.setRequestFocusEnabled(false);
        labelHint.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(labelHint);
        labelHint.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ComboSelector.class, "ComboSelector.labelHint.AccessibleContext.accessibleName")); // NOI18N

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea labelHint;
    private javax.swing.JComboBox selector;
    // End of variables declaration//GEN-END:variables
  
  // <editor-fold defaultstate="collapsed" desc="ListDataListener implementation">
  public void intervalAdded(ListDataEvent e) {
    updateModelProxy();
  }
  
  public void intervalRemoved(ListDataEvent e) {
    updateModelProxy();
  }
  
  public void contentsChanged(ListDataEvent e) {
    updateModelProxy();
  }
  // </editor-fold>
  
  public String getHint() {
    return labelHint.getText();
  }
  public void setHint(String hint) {
    labelHint.setText(hint);
  }
  
  public ComboBoxModel getModel() {
    return this.customModel;
  }
  public void setModel(ComboBoxModel model) {
    this.customModel = model;
    updateModelProxy();
  }
  
  public Object getSelectedItem() {
    if (isEmptyModel)
      return null;
    if (getModel().getSelectedItem() == null && getModel().getSize() > 0)
      getModel().setSelectedItem(getModel().getElementAt(0));
    return getModel().getSelectedItem();
  }
  
  public String getEmptyModelMessage() {
    return emptyModelMessage;
  }
  public void setEmptyModelMessage(String message) {
    emptyModelMessage = message;
  }
  
  public boolean isDisableEmptySelector() {
    return emptySelectorDisabled;
  }
  public void setDisableEmptySelector(boolean value) {
    emptySelectorDisabled = value;
  }
  
  public Color getHintForeground() {
    return labelHint.getDisabledTextColor();
  }
  public void setHintForeground(Color color) {
    labelHint.setDisabledTextColor(color);
  }
  
  public Color getHintBackground() {
    return labelHint.getBackground();
  }
  public void setHintBackground(Color bgcolor) {
    labelHint.setBackground(bgcolor);
  }
  
  private void updateModelProxy() {
    if (customModel.getSize() > 0) {
      isEmptyModel = false;
      if (emptySelectorDisabled) {
        selector.setEnabled(true);
      }
      selector.setModel(customModel);
    } else {
      isEmptyModel = true;
      if (emptySelectorDisabled) {
        selector.setEnabled(false);
      }
      selector.setModel(emptyModel);
    }
    selector.invalidate();
  }
}
