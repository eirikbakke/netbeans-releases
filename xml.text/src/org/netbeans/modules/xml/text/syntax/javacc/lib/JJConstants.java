/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.text.syntax.javacc.lib;

/**
 * //constants used by JJ bridge
 *
 * @author  Petr Kuze;
 * @version 1.0
 */
public interface JJConstants {
    
    /** jj reached end of buffer. */
    public static final int JJ_EOF = -10;
    
    /** jj reached NL */
    public static final int JJ_EOL = -11;
    
    /** jj reached an internal error. e.g. invalid regexp. */
    public static final int JJ_ERR = -13;    
    
}
