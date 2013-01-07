/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2012 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.profiler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import org.netbeans.lib.profiler.ui.UIConstants;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jiri Sedlacek
 */
public class ProfilerTopComponent extends TopComponent {
    
    public static final String RECENT_FILE_KEY = "nb.recent.file.path"; // NOI18N
    
    private Component lastFocusOwner;
    
    private final PropertyChangeListener focusListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            Component c = evt.getNewValue() instanceof Component ?
                    (Component)evt.getNewValue() : null;
            processFocusedComponent(c);
        }
        private void processFocusedComponent(Component c) {
            Component cc = c;
            while (c != null) {
                if (c == ProfilerTopComponent.this) {
                    lastFocusOwner = cc;
                    return;
                }
                c = c.getParent();
            }
        }
    };
    
    public void componentActivated() {
        super.componentActivated();
        if (lastFocusOwner != null) {
            lastFocusOwner.requestFocus();
        } else {
            Component defaultFocusOwner = defaultFocusOwner();
            if (defaultFocusOwner != null) defaultFocusOwner.requestFocus();
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                addPropertyChangeListener("focusOwner", focusListener); // NOI18N
    }

    public void componentDeactivated() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().
                removePropertyChangeListener("focusOwner", focusListener); // NOI18N
        super.componentDeactivated();
    }
    
    protected Component defaultFocusOwner() {
        return null;
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
    
    public void paintComponent(Graphics g) {
        Color background = UIManager.getColor(UIConstants.PROFILER_PANELS_BACKGROUND);
        if (background != null) {
            g.setColor(background);
            Insets i = getInsets();
            g.fillRect(i.left, i.top, getWidth() - i.left - i.right, getHeight() - i.top - i.bottom);
        } else {
            super.paintComponent(g);
        }
    }
    
}
