/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.windows;

import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionEvent;
import javax.swing.text.Keymap;
import java.lang.reflect.*;

import org.openide.actions.ActionManager;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.ErrorManager;


/**
 * this class registers itself to the KeyboardFocusManager as a key event
 * post-processor as well as a key event dispatcher.  It invokes the action
 * bound to the key stroke, or routes unconsumed key events to the menu bar.
 * If a menu is already shown, all key events are routed to the main menu bar.
 * 
 * @author Tran Duc Trung
 */
final class ShortcutAndMenuKeyEventProcessor implements KeyEventDispatcher, KeyEventPostProcessor {
    
    private static ShortcutAndMenuKeyEventProcessor defaultInstance;
    
    private static boolean installed = false;
    
    
    private  ShortcutAndMenuKeyEventProcessor() {
    }
    

    private static synchronized ShortcutAndMenuKeyEventProcessor getDefault() {
        if(defaultInstance == null) {
            defaultInstance = new ShortcutAndMenuKeyEventProcessor();
        }
        
        return defaultInstance;
    }
    
    
    public static synchronized void install() {
        if(installed) {
            return;
        }
        
        ShortcutAndMenuKeyEventProcessor instance = getDefault();
        
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.addKeyEventDispatcher(instance);
        keyboardFocusManager.addKeyEventPostProcessor(instance);
    }
    
    public static synchronized void uninstall() {
        if(!installed) {
            return;
        }
        
        ShortcutAndMenuKeyEventProcessor instance = getDefault();
        
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.removeKeyEventDispatcher(instance);
        keyboardFocusManager.removeKeyEventPostProcessor(instance);
    }
    


    private boolean wasPopupDisplayed;
    private KeyEvent lastKeyPressed; // which posted a popup from main menu
    
    public boolean postProcessKeyEvent(KeyEvent ev) {
        if (ev.isConsumed())
            return false;

        if (processShortcut(ev))
            return true;

        Window w = SwingUtilities.windowForComponent(ev.getComponent());        
        if (w instanceof Dialog)
            return false;
        
        JFrame mw = (JFrame)WindowManagerImpl.getInstance().getMainWindow();
        if (w == mw) {
            return false;
        }

        JMenuBar mb = mw.getJMenuBar();
        if (mb == null)
            return false;
        boolean pressed = (ev.getID() == KeyEvent.KEY_PRESSED);
        //#38810 start - focusing the main window in case it's not active and the 
        // key binding should be processed.
        if (pressed && !mw.isActive())
        {
            mw.toFront();
        }
        //#38810 end
        boolean res = invokeProcessKeyBindingsForAllComponents(ev, mw, pressed);
        
        if (res)
            ev.consume();
        return res;
    }

    public boolean dispatchKeyEvent(KeyEvent ev) {
        // XXX(-ttran) Sun JDK 1.4 on Linux: pressing Alt key produces
        // KeyEvent.VK_ALT, but Alt+<key> produces Meta+<key>
        
        if (Utilities.getOperatingSystem() == Utilities.OS_LINUX) {
            int mods = ev.getModifiers();
            if (mods == InputEvent.META_MASK) {
                mods = (mods & ~ InputEvent.META_MASK) | InputEvent.ALT_MASK;
                ev.setModifiers(mods);
            }
        }

        if (ev.getID() == KeyEvent.KEY_PRESSED
            && ev.getModifiers() == (InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK)
            && (ev.getKeyCode() == KeyEvent.VK_PAUSE
                || ev.getKeyCode() == KeyEvent.VK_CANCEL)
            ) {
            Object source = ev.getSource();
            if (source instanceof Component) {
                Window w = SwingUtilities.windowForComponent((Component)source);
                Component focused = SwingUtilities.findFocusOwner(w);
                System.err.println("*** ShortcutAndMenuKeyEventProcessor: current focus owner = " + focused); // NOI18N
            }
            ev.consume();
            return true;
        }

        if (ev.getID() == KeyEvent.KEY_PRESSED) {
            lastKeyPressed = ev;
        }
        
        MenuElement[] arr = MenuSelectionManager.defaultManager().getSelectedPath();
        if (arr == null || arr.length == 0) {
            wasPopupDisplayed = false;

            // XXX(-ttran) special case for Shift+F10 on braindead Windoze.
            // Shortcuts are handled in postProcessKeyEvent() so that the
            // focused components can decide to handle and consume the key
            // event itself.  Buttons' and labels' mnemonics in components will
            // work even though they conflict with shortcuts.  But if we do so
            // for Shift+F10 on Windoze then for some mysterious reason the
            // system menu (left-upper icon in the native window caption) will
            // be invoked, no matter how hard we try to consume the event.

            if (Utilities.isWindows()
                && ev.getModifiers() == InputEvent.SHIFT_MASK
                && ev.getKeyCode() == KeyEvent.VK_F10
                ) {
                return processShortcut(ev);
            }
            return false;
        }
        
        if (!wasPopupDisplayed
            && lastKeyPressed != null
            && ev.getID() == KeyEvent.KEY_TYPED
            && lastKeyPressed.getModifiers() == InputEvent.ALT_MASK
            && ev.getModifiers() == InputEvent.ALT_MASK
            && lastKeyPressed.getKeyChar() == ev.getKeyChar()
            ) {
            wasPopupDisplayed = true;
            ev.consume();
            return true;
        }

        wasPopupDisplayed = true;
        
        MenuSelectionManager.defaultManager().processKeyEvent(ev);
        
        if (!ev.isConsumed() && arr[0] instanceof JMenuBar) {
            ev.setSource(WindowManagerImpl.getInstance().getMainWindow());
        }
        return ev.isConsumed();
    }

    private boolean processShortcut(KeyEvent ev) {
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(ev);
        Window w = SwingUtilities.windowForComponent(ev.getComponent());
        
        // don't let action keystrokes to propagate from both
        // modal and nonmodal dialogs
        if (!isTransmodalAction(ks) && (w instanceof Dialog)) {
            return false;
        }
        
        // Provide a reasonably useful action event that identifies what was focused
        // when the key was pressed, as well as what keystroke ran the action.
        ActionEvent aev = new ActionEvent(
            ev.getSource(), ActionEvent.ACTION_PERFORMED, Utilities.keyToString(ks));
            
        Keymap root = (Keymap)Lookup.getDefault().lookup(Keymap.class);
        Action a = root.getAction (ks);
        if (a != null && a.isEnabled()) {
            ActionManager am = (ActionManager)Lookup.getDefault().lookup(ActionManager.class);
            am.invokeAction(a, aev);
            ev.consume();
            return true;
        }
        return false;
    }

    private static boolean invokeProcessKeyBindingsForAllComponents(
        KeyEvent e, Container container, boolean pressed)
    {
        try {
            Method m = JComponent.class.getDeclaredMethod(
                "processKeyBindingsForAllComponents", // NOI18N
                new Class[] { KeyEvent.class, Container.class, Boolean.TYPE });
            if (m == null)
                return false;

            m.setAccessible(true);
            Boolean b = (Boolean) m.invoke(null, new Object[] { e, container, pressed ? Boolean.TRUE : Boolean.FALSE });
            return b.booleanValue();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        
        return false;
    }

    /**
     * Checks to see if a given keystroke is bound to an action which should
     * function on all focused components.  This includes the Main Window,
     * dialogs, popup menus, etc.  Otherwise only the Main Window and
     * TopComponents will receive the keystroke.  By default, off, unless the
     * action has a property named <code>OpenIDE-Transmodal-Action</code> which
     * is set to {@link Boolean#TRUE}.
     * @param key the keystroke to check
     * @return <code>true</code> if transmodal; <code>false</code> if a normal
     * action, or the key is not bound to anything in the global keymap
     */
    private static boolean isTransmodalAction (KeyStroke key) {
        Keymap root = (Keymap)Lookup.getDefault().lookup(Keymap.class);
        Action a = root.getAction (key);
        if (a == null) return false;
        Object val = a.getValue ("OpenIDE-Transmodal-Action"); // NOI18N
        return val != null && val.equals (Boolean.TRUE);
    }
}
