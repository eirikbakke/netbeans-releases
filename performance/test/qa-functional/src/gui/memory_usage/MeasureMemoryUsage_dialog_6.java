/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package gui.memory_usage;

import org.netbeans.junit.NbTestSuite;
import gui.menu.*;
import gui.window.*;
import gui.action.*;

/**
 * Measure UI-RESPONSIVENES and WINDOW_OPENING.
 *
 * @author  mmirilovic@netbeans.org
 */
public class MeasureMemoryUsage_dialog_6 {
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        
        // dialogs and windows which first open a file in the editor
        suite.addTest(new GotoClassDialog("doMeasurement", "Go To Class dialog open"));
        suite.addTest(new OverrideMethods("doMeasurement", "Override and Implement Methods dialog open"));
        suite.addTest(new GotoLineDialog("doMeasurement", "Go to Line dialog open"));
        suite.addTest(new AutoCommentWindow("doMeasurement", "Auto Comment Tool open"));
        suite.addTest(new FindInSourceEditor("doMeasurement", "Find in Source Editor dialog open"));
        suite.addTest(new InternationalizeDialog("doMeasurement", "Internationalize dialog open"));
        
        return suite;
    }
    
}
