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
package org.netbeans.modules.xml.text.syntax;

import org.netbeans.editor.BaseTokenCategory;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

import org.netbeans.modules.xml.text.syntax.javacc.lib.*;

/**
 * Token-ids and token-categories for DTD
 *
 * @author  Miloslav Metelka
 * @author  Petr Kuzel
 * @version 1.0
 */
public class DTDTokenContext extends TokenContext {

    public static final int COMMENT_ID = 1;
    public static final int ERROR_ID = 2;
    public static final int KW_ID = 3;
    public static final int PLAIN_ID = 4;
    public static final int REF_ID = 5;
    public static final int STRING_ID = 6;
    public static final int SYMBOL_ID = 7;
    public static final int TARGET_ID = 8;
    public static final int EOL_ID = 9;


    public static final JJTokenID COMMENT = new JJTokenID("comment", COMMENT_ID); // NOI18N
    public static final JJTokenID ERROR = new JJTokenID("error", ERROR_ID, true); // NOI18N

    // <!declatarion + "SYSTEM"/"PUBLIC" // NOI18N
    public static final JJTokenID KW = new JJTokenID("keyword", KW_ID); // NOI18N
    public static final JJTokenID PLAIN = new JJTokenID("plain", PLAIN_ID); // NOI18N
    //  &aref; &#x000;
    public static final JJTokenID REF = new JJTokenID("ref", REF_ID); // NOI18N
    // <home id="attrvalue" > // NOI18N
    public static final JJTokenID STRING = new JJTokenID("string", STRING_ID); // NOI18N
    // <>!
    public static final JJTokenID SYMBOL = new JJTokenID("symbol", SYMBOL_ID); // NOI18N
    // <? target ...>
    public static final JJTokenID TARGET = new JJTokenID("target", TARGET_ID); // NOI18N
    public static final JJTokenID EOL = new JJTokenID("eol", EOL_ID); // NOI18N

    // Context instance declaration
    public static final DTDTokenContext context = new DTDTokenContext();

    public static final TokenContextPath contextPath = context.getContextPath();

    private DTDTokenContext() {
        super("dtd-"); // NOI18N

        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                e.printStackTrace();
            }
        }

    }


}
