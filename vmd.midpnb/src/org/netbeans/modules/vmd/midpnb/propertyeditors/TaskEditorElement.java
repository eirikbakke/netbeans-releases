/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.vmd.midpnb.propertyeditors;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.TypeID;
import org.netbeans.modules.vmd.midp.actions.GoToSourceSupport;
import org.netbeans.modules.vmd.midp.propertyeditors.resource.elements.PropertyEditorResourceElement;
import org.netbeans.modules.vmd.midpnb.components.resources.SimpleCancellableTaskCD;
import org.openide.util.NbBundle;

/**
 *
 * @author Anton Chechel
 */
public class TaskEditorElement extends PropertyEditorResourceElement implements MouseListener {

    private WeakReference<DesignComponent> component;

    public TaskEditorElement() {
        initComponents();
        gotoLabel.addMouseListener(this);
    }

    public JComponent getJComponent() {
        return this;
    }

    public TypeID getTypeID() {
        return SimpleCancellableTaskCD.TYPEID;
    }

    public void setDesignComponentWrapper(final DesignComponentWrapper wrapper) {
        if (wrapper != null) {
            DesignComponent _component = wrapper.getComponent();
            if (_component != null) {
                component = new WeakReference<DesignComponent>(_component);
            }
        } else {
            component = null;
        }
        // UI stuff
        setAllEnabled(wrapper != null);
    }

    public void mouseClicked(MouseEvent e) {
        if (component != null) {
            DesignComponent _component = component.get();
            if (_component != null) {
                GoToSourceSupport.goToSourceOfComponent(_component);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (gotoLabel.isEnabled()) {
            gotoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void mouseExited(MouseEvent e) {
        if (gotoLabel.isEnabled()) {
            gotoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void setAllEnabled(boolean isEnabled) {
        taskLabel.setEnabled(isEnabled);
        gotoLabel.setEnabled(isEnabled);
        gotoLabel.setText(NbBundle.getMessage(TaskEditorElement.class, isEnabled ? "TaskEditorElement.gotoLabel.html.text" : "TaskEditorElement.gotoLabel.text")); // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        taskLabel = new javax.swing.JLabel();
        gotoLabel = new javax.swing.JLabel();

        taskLabel.setLabelFor(gotoLabel);
        org.openide.awt.Mnemonics.setLocalizedText(taskLabel, org.openide.util.NbBundle.getMessage(TaskEditorElement.class, "TaskEditorElement.taskLabel.text")); // NOI18N
        taskLabel.setEnabled(false);

        gotoLabel.setText(org.openide.util.NbBundle.getMessage(TaskEditorElement.class, "TaskEditorElement.gotoLabel.text")); // NOI18N
        gotoLabel.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(taskLabel)
                    .add(gotoLabel))
                .addContainerGap(204, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(taskLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gotoLabel)
                .addContainerGap(116, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gotoLabel;
    private javax.swing.JLabel taskLabel;
    // End of variables declaration//GEN-END:variables
}
