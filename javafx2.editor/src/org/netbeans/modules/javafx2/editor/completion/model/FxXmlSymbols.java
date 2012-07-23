/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javafx2.editor.completion.model;

import java.util.StringTokenizer;
import org.openide.util.Utilities;

/**
 *
 * @author sdedic
 */
public final class FxXmlSymbols {
    /**
     * Import processing instruction
     */
    public static final String FX_IMPORT = "import";
    
    /**
     * Star marker at the end of the import
     */
    public static final String FX_IMPORT_STAR = "*"; 
    
    /**
     * Include processing instruction
     */
    public static final String FX_INCLUDE = "include";
    
    
    public static final String FX_DEFINITIONS = "definitions";
    
    public static final String FX_COPY = "copy";
    
    public static final String FX_REFERENCE = "reference";
    
    public static final String FX_ATTR_ID = "id";
    
    public static final String FX_LANGUAGE = "language";
    
    public static final String FX_ATTR_REFERENCE_SOURCE = "source";
    
    /**
     * The fx:value attribute
     */
    public static final String FX_VALUE = "value";
    
    /**
     * The fx:id attribute
     */
    public static final String FX_ID = "id";
    
    /**
     * The fx:factory attribute
     */
    public static final String FX_FACTORY = "factory";
    /**
     * Name of the value-of factory method, as per FXML guide
     */
    public static final String NAME_VALUE_OF = "valueOf"; // NOI18N
    public static final String SETTER_PREFIX = "set"; // NOI18N

    /**
     * Determines, whether a character sequence (local tag name) is a classname tag.
     * FXML guide <b>specifies</b> that class tags must start with uppercase (or their
     * last part must start with uppercase) letter.
     * 
     * @param s
     * @return 
     */
    public static boolean isClassTagName(CharSequence s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '.') {
                if (i < s.length() - 1) {
                    char c = s.charAt(i + 1);
                    return Character.isUpperCase(c);
                }
            }
        }
        return Character.isUpperCase(s.charAt(0));
    }
    
    public static int findStaticProperty(CharSequence s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '.') {
                // check that the starting subsequence forms a class name
                if (!isClassTagName(s.subSequence(0, i))) {
                    return -2;
                }
                if (i >= s.length() - 1) {
                    return -2;
                }
                return Character.isLowerCase(s.charAt(i + 1)) ? i : -2;
            }
        }
        return -1;
    }
    
    public CharSequence[] splitPackageAndName(CharSequence qn) {
        for (int i = qn.length() - 1 ; i > 0; i--) {
            if (qn.charAt(i) == '.') {
                return new CharSequence[] { qn.subSequence(0, i), qn.subSequence(i + 1, qn.length()) };
            }
        }
        return null;
    }
    
    public static boolean isQualifiedIdentifier(String qn) {
        StringTokenizer tukac = new StringTokenizer(qn, ".");
        if (!tukac.hasMoreTokens()) {
            return false;
        }
        while (tukac.hasMoreTokens()) {
            if (!Utilities.isJavaIdentifier(tukac.nextToken())) {
                return false;
            }
        }
        return true;
    }
}
