/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.commons;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import org.netbeans.modules.team.ide.spi.IDEServices;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Stupka
 */
public class UIUtils {

    public static void setWaitCursor(final boolean on) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                JFrame mainWindow = (JFrame) WindowManager.getDefault().getMainWindow();
                mainWindow
                    .getGlassPane()
                    .setCursor(Cursor.getPredefinedCursor(
                        on ?
                        Cursor.WAIT_CURSOR :
                        Cursor.DEFAULT_CURSOR));
                mainWindow.getGlassPane().setVisible(on);
            }
        };
        if(EventQueue.isDispatchThread()) {
           r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }
    
    public static void keepFocusedComponentVisible(JComponent component) {
        keepFocusedComponentVisible(component, component);
    }
    
    public static void keepFocusedComponentVisible(Component component, JComponent container) {
        FocusListener listener;
        if(component instanceof JComponent ) {
            listener = getNotShowingFieldsFocusListener(container);
        } else {
            listener = getScrollingFocusListener(); // legacy fallback
        }
        keepFocusedComponentVisible(component, listener);
    }

    public static Color getSectionPanelBackground() {
        //force initialization of PropSheet look'n'feel values
        UIManager.get( "nb.propertysheet" );

        Color res = UIManager.getColor( "PropSheet.setBackground" );//NOI18N
        if( null == res ) {
            res = new Color( 224, 224, 224 );
        }
        return res;
    }
    
    public static Color getCollapsedPanelBackground() {
        //force initialization of PropSheet look'n'feel values
        UIManager.get( "nb.propertysheet" );

        Color res = UIManager.getColor( "PropSheet.selectedSetBackground" );//NOI18N
        if( null == res ) {
            res = new Color( 224, 224, 224 );
        }
        return res;
    }

    public static Color getLinkColor() {
        Color res = UIManager.getColor( "nb.html.link.foreground" );//NOI18N
        if( null == res ) {
            res = Color.blue;
        }
        return res;
    }

    public static boolean isNimbus() {
        return "Nimbus".equals( UIManager.getLookAndFeel().getID() ); //NOI18N
    }
    
    public static String getColorString (Color c) {
        return "#" + getHex(c.getRed()) + getHex(c.getGreen()) + getHex(c.getBlue()); //NOI18N
    }

    public static Color getTaskNewColor () {
        Color c = UIManager.getColor("nb.bugtracking.new.color"); //NOI18N
        if (c == null) {
            c = new Color(0, 180, 0);
        }
        return c;
    }

    public static Color getTaskModifiedColor () {
        Color c = UIManager.getColor("nb.bugtracking.modified.color"); //NOI18N
        if (c == null) {
            c = new Color(0, 0, 255);
        }
        return c;
    }

    public static Color getTaskConflictColor () {
        Color c = UIManager.getColor("nb.bugtracking.conflict.color"); //NOI18N
        if (c == null) {
            c = new Color(255, 0, 0);
        }
        return c;
    }

    public static Color getTaskObsoleteColor () {
        Color c = UIManager.getColor("nb.bugtracking.obsolete.color"); //NOI18N
        if (c == null) {
            c = new Color(153, 153, 153);
        }
        return c;
    }

    private static String getHex (int i) {
        String hex = Integer.toHexString(i & 0x000000FF);
        if (hex.length() == 1) {
            hex = "0" + hex; //NOI18N
        }
        return hex;
    }

    private static void keepFocusedComponentVisible(Component component, FocusListener l) {
        component.removeFocusListener(l); // Making sure that it is not added twice
        component.addFocusListener(l);
        if (component instanceof Container) {
            for (Component subComponent : ((Container)component).getComponents()) {
                keepFocusedComponentVisible(subComponent, l);
            }
        }
    }

    private static FocusListener scrollingFocusListener;
    
    private static FocusListener getNotShowingFieldsFocusListener(JComponent container) {
        String key = "notShowingFieldFocusListener";                            // NOI18N
        Object l = container.getClientProperty(key);
        if (l == null) {
            l = new NotShowingFieldsFocusListener(container);
            container.putClientProperty(key, l);
        }
        return (FocusListener) l;
    }
    
    private static FocusListener getScrollingFocusListener() {
        if (scrollingFocusListener == null) {
            scrollingFocusListener = new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (!e.isTemporary()) {
                        Component comp = e.getComponent();
                        Container cont = comp.getParent();
                        if (cont instanceof JViewport) {
                            // comp is JViewport's view;
                            // we want the viewport itself to be shown in this case
                            comp = cont;
                            cont = cont.getParent();
                        }
                        if (cont instanceof JComponent) {
                            ((JComponent)cont).scrollRectToVisible(comp.getBounds());
                        }
                    }
                }
            };
        }
        return scrollingFocusListener;
    }

    public interface SizeController {
        public void setWidth(int width);
    }
        
    public static void keepComponentsWidthByVisibleArea(final JPanel panel, final SizeController sc) {
        panel.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                final JViewport v = getViewport(panel);
                assert v != null;
                if(v == null) {
                    return;
                }
                sc.setWidth(computeWidth(v));
                v.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        sc.setWidth(computeWidth(v));
                    }
                });
                v.addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        sc.setWidth(computeWidth(v));
                    }
                    @Override public void componentMoved(ComponentEvent e) { }
                    @Override public void componentShown(ComponentEvent e) { }
                    @Override public void componentHidden(ComponentEvent e) { }
                });
            }
            private int computeWidth(JViewport v) {
                Rectangle vr = v.getViewRect();
                return vr.width + vr.x;
            }
            @Override public void ancestorRemoved(AncestorEvent event) { }
            @Override public void ancestorMoved(AncestorEvent event) { }
        });
    }
    
    private static JViewport getViewport(Container c) {
        if(c == null) {
            return null;
        }
        if(c instanceof JScrollPane) {
            return ((JScrollPane) c).getViewport();
        }
        return getViewport(c.getParent());
    }
    
    private static class NotShowingFieldsFocusListener implements FocusListener {
        private final JComponent container;
        
        public NotShowingFieldsFocusListener(JComponent container) {
            this.container = container;
        }
        
        @Override
        public void focusGained(FocusEvent e) {
            if (e.isTemporary()) {
                return;
            }
            Component cmp = e.getComponent();
            if(cmp instanceof JComponent) {
                JViewport vp = getViewport(container);
                if(vp == null) {
                    return;
                }
                Rectangle vr = vp.getViewRect();
                Point p = SwingUtilities.convertPoint(cmp.getParent(), cmp.getLocation(), container);
                final Rectangle r = new Rectangle(p, cmp.getSize());
                if(vr.intersects(r)) {
                    return; 
                }
                container.scrollRectToVisible(r);
            }
        }

        @Override
        public void focusLost(FocusEvent e) { }

    }

    // A11Y - Issues 163597 and 163598
    public static void fixFocusTraversalKeys(JComponent component) {
        Set<AWTKeyStroke> set = component.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        set = new HashSet<AWTKeyStroke>(set);
        set.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
        component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
    }

    public static void issue163946Hack(final JScrollPane scrollPane) {
        MouseWheelListener listener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (scrollPane.getVerticalScrollBar().isShowing()) {
                    if (e.getSource() != scrollPane) {
                        e.setSource(scrollPane);
                        scrollPane.dispatchEvent(e);
                    }
                } else {
                    scrollPane.getParent().dispatchEvent(e);
                }
            }
        };
        scrollPane.addMouseWheelListener(listener);
        scrollPane.getViewport().getView().addMouseWheelListener(listener);
    }

    public static int getColumnWidthInPixels(int widthInLeters, JComponent comp) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < widthInLeters; i++, sb.append("w"));                // NOI18N
        return getColumnWidthInPixels(sb.toString(), comp);
    }

    public static int getColumnWidthInPixels(String str, JComponent comp) {
        FontMetrics fm = comp.getFontMetrics(comp.getFont());
        return fm.stringWidth(str);
    }

    public static int getLongestWordWidth(String header, List<String> values, JComponent comp) {
        return getLongestWordWidth(header, values, comp, false);
    }

    public static int getLongestWordWidth(String header, List<String> values, JComponent comp, boolean regardIcon) {
        String[] valuesArray = values.toArray(new String[values.size()]);
        return getLongestWordWidth(header, valuesArray, comp, regardIcon);
    }

    public static int getLongestWordWidth(String header, String[] values, JComponent comp) {
        return getLongestWordWidth(header, values, comp, false);
    }

    public static int getLongestWordWidth(String header, String[] values, JComponent comp, boolean regardIcon) {
        int size = header.length();
        for (String s : values) {
            if(size < s.length()) {
                size = s.length();
            }
        }
        return getColumnWidthInPixels(size, comp) + (regardIcon ? 16 : 0);
    }
    
    public static IDEServices.DatePickerComponent createDatePickerComponent () {
        IDEServices.DatePickerComponent picker = null;
        IDEServices services = Lookup.getDefault().lookup(IDEServices.class);
        if (services != null) {
            picker = services.createDatePicker();
        }
        if (picker == null) {
            picker = new DummyDatePickerComponent();
        }
        return picker;
    }

    private static class DummyDatePickerComponent extends JFormattedTextField implements IDEServices.DatePickerComponent {

        private static final DateFormatter formatter = new javax.swing.text.DateFormatter() {
            
            @Override
            public Object stringToValue (String text) throws java.text.ParseException {
                if (text == null || text.trim().isEmpty()) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        private final ChangeSupport support;

        DummyDatePickerComponent () {
            super(new DefaultFormatterFactory(formatter));
            support = new ChangeSupport(this);
        }

        @Override
        public JComponent getComponent () {
            return this;
        }

        @Override
        public void setDate (Date date) {
            try {
                setText(formatter.valueToString(date));
            } catch (ParseException ex) {
                Support.LOG.log(Level.INFO, null, ex);
            }
        }

        @Override
        public Date getDate () {
            try {
                return (Date) formatter.stringToValue(getText());
            } catch (ParseException ex) {
                Support.LOG.log(Level.INFO, null, ex);
                return null;
            }
        }

        @Override
        public void addChangeListener (ChangeListener listener) {
            support.addChangeListener(listener);
        }

        @Override
        public void removeChangeListener (ChangeListener listener) {
            support.removeChangeListener(listener);
        }

        @Override
        public boolean allowsOpeningDaySelector () {
            return false;
        }
        
        @Override
        public boolean openDaySelector () {
            return false;
        }
    }
}
