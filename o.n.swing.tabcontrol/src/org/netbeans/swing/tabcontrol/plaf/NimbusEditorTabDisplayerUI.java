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
/*
 * NimbusEditorTabDisplayerUI.java
 *
 * Created on 09 December 2003, 16:53
 */

package org.netbeans.swing.tabcontrol.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.netbeans.swing.tabcontrol.TabDisplayer;

import javax.swing.plaf.ComponentUI;

/**
 * Nimbus impl of tabs ui
 *
 * @author Marek Slama
 */
public final class NimbusEditorTabDisplayerUI extends BasicScrollingTabDisplayerUI {
    
    private static Map<Integer, String[]> buttonIconPaths;
    
    /**
     * Creates a new instance of GtkEditorTabDisplayerUI
     */
    public NimbusEditorTabDisplayerUI(TabDisplayer displayer) {
        super (displayer);
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new NimbusEditorTabDisplayerUI ((TabDisplayer) c);
    }

    public Rectangle getTabRect(int idx, Rectangle rect) {
        Rectangle r = super.getTabRect (idx, rect);
        //For win classic, take up the full space, even the insets, to match
        //earlier appearance
        r.y = 0;
        r.height = displayer.getHeight();
        return r;
    }  

    public void install() {
        super.install();
    }

    public Dimension getPreferredSize(JComponent c) {
        int prefHeight = 28;
        Graphics g = BasicScrollingTabDisplayerUI.getOffscreenGraphics();
        if (g != null) {
            FontMetrics fm = g.getFontMetrics(displayer.getFont());
            Insets ins = getTabAreaInsets();
            prefHeight = fm.getHeight() + ins.top + ins.bottom + 12;
        }
        return new Dimension(displayer.getWidth(), prefHeight);
    }
    
    protected void paintBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int w = displayer.getWidth();
        int h = displayer.getHeight();
        if( NimbusEditorTabCellRenderer.IS_JDK_17_OR_18 ) {
            javax.swing.Painter painter = null;
            if (displayer.isActive()) {
                painter = (javax.swing.Painter) UIManager.get("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter");
            }
            if (!displayer.isActive() || null == painter) {
                painter = (javax.swing.Painter) UIManager.get("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter");
            }
            if( null != painter )
                painter.paint(g2d, null, w, h);
        } else {
            com.sun.java.swing.Painter painter = null;
            if (displayer.isActive()) {
                painter = (com.sun.java.swing.Painter) UIManager.get("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter");
            }
            if (!displayer.isActive() || null == painter) {
                painter = (com.sun.java.swing.Painter) UIManager.get("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter");
            }
            if( null != painter )
                painter.paint(g2d, null, w, h);
        }

        Color c = (Color) UIManager.get("nimbusBorder");
        g.setColor(c);
        g.drawLine(0, h-5, 0, h);
        g.drawLine(w-1, h-5, w-1, h);
    }
    
    protected void paintAfterTabs(Graphics g) {
    }

    protected TabCellRenderer createDefaultRenderer() {
        return new NimbusEditorTabCellRenderer();
    }

    private static void initIcons() {
        if( null == buttonIconPaths ) {
            buttonIconPaths = new HashMap<Integer, String[]>(7);
            
            //left button
            String[] iconPaths = new String[4];
            iconPaths[TabControlButton.STATE_DEFAULT] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollleft_enabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_DISABLED] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollleft_disabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_ROLLOVER] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollleft_rollover.png"; // NOI18N
            iconPaths[TabControlButton.STATE_PRESSED] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollleft_pressed.png"; // NOI18N
            buttonIconPaths.put( TabControlButton.ID_SCROLL_LEFT_BUTTON, iconPaths );

            //right button
            iconPaths = new String[4];
            iconPaths[TabControlButton.STATE_DEFAULT] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollright_enabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_DISABLED] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollright_disabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_ROLLOVER] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollright_pressed.png"; // NOI18N
            iconPaths[TabControlButton.STATE_PRESSED] = "org/netbeans/swing/tabcontrol/resources/nimbus_scrollright_pressed.png"; // NOI18N
            buttonIconPaths.put( TabControlButton.ID_SCROLL_RIGHT_BUTTON, iconPaths );

            //drop down button
            iconPaths = new String[4];
            iconPaths[TabControlButton.STATE_DEFAULT] = "org/netbeans/swing/tabcontrol/resources/nimbus_popup_enabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_DISABLED] = "org/netbeans/swing/tabcontrol/resources/nimbus_popup_disabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_ROLLOVER] = "org/netbeans/swing/tabcontrol/resources/nimbus_popup_rollover.png"; // NOI18N
            iconPaths[TabControlButton.STATE_PRESSED] = "org/netbeans/swing/tabcontrol/resources/nimbus_popup_pressed.png"; // NOI18N
            buttonIconPaths.put( TabControlButton.ID_DROP_DOWN_BUTTON, iconPaths );

            //maximize/restore button
            iconPaths = new String[4];
            iconPaths[TabControlButton.STATE_DEFAULT] = "org/netbeans/swing/tabcontrol/resources/nimbus_maximize_enabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_DISABLED] = "org/netbeans/swing/tabcontrol/resources/nimbus_maximize_disabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_ROLLOVER] = "org/netbeans/swing/tabcontrol/resources/nimbus_maximize_rollover.png"; // NOI18N
            iconPaths[TabControlButton.STATE_PRESSED] = "org/netbeans/swing/tabcontrol/resources/nimbus_maximize_pressed.png"; // NOI18N
            buttonIconPaths.put( TabControlButton.ID_MAXIMIZE_BUTTON, iconPaths );

            iconPaths = new String[4];
            iconPaths[TabControlButton.STATE_DEFAULT] = "org/netbeans/swing/tabcontrol/resources/nimbus_restore_enabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_DISABLED] = "org/netbeans/swing/tabcontrol/resources/nimbus_restore_disabled.png"; // NOI18N
            iconPaths[TabControlButton.STATE_ROLLOVER] = "org/netbeans/swing/tabcontrol/resources/nimbus_restore_rollover.png"; // NOI18N
            iconPaths[TabControlButton.STATE_PRESSED] = "org/netbeans/swing/tabcontrol/resources/nimbus_restore_pressed.png"; // NOI18N
            buttonIconPaths.put( TabControlButton.ID_RESTORE_BUTTON, iconPaths );
        }
    }

    public Icon getButtonIcon(int buttonId, int buttonState) {
        Icon res = null;
        initIcons();
        String[] paths = buttonIconPaths.get( buttonId );
        if( null != paths && buttonState >=0 && buttonState < paths.length ) {
            res = TabControlButtonFactory.getIcon( paths[buttonState] );
        }
        return res;
    }
    
    protected Rectangle getControlButtonsRectangle( Container parent ) {
        Component c = getControlButtons();
        return new Rectangle( parent.getWidth()-c.getWidth()-4, 2, c.getWidth(), c.getHeight() );
    }

    public Insets getTabAreaInsets() {
        Insets retValue = super.getTabAreaInsets();
        retValue.right += 4;
        return retValue;
    }
}
