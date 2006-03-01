/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.jellytools.modules.debugger.actions;

import java.awt.event.KeyEvent;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.Action.Shortcut;

/**
 * Used to call "Window|Debugging|Sources" main menu item or Alt+Shift+8
 * shortcut.
 * @see org.netbeans.jellytools.actions.Action
 * @author Jiri.Skrivanek@sun.com 
 */
public class SourcesAction extends Action {
    private static final String menuPath =
            Bundle.getStringTrimmed("org.netbeans.core.Bundle", "Menu/Window") +
            "|"+Bundle.getStringTrimmed(
                                "org.netbeans.modules.debugger.resources.Bundle",
                                "CTL_Debugging_workspace") +
            "|"+Bundle.getStringTrimmed(
                                "org.netbeans.modules.debugger.jpda.ui.actions.Bundle",
                                "CTL_SourcesViewAction");

    private static final Shortcut shortcut = new Shortcut(KeyEvent.VK_8,
                                                          KeyEvent.VK_ALT + KeyEvent.VK_SHIFT); 
    
    /**
     * Creates new SourcesAction instance.
     */    
    public SourcesAction() {
        super(menuPath, null, shortcut);
    }
}
