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

package org.netbeans.modules.groovy.refactoring.utils;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.groovy.editor.api.AstUtilities;

/**
 *
 * @author Martin Janicek
 */
public class OccurrencesUtil {

    private OccurrencesUtil() {
    }


    public static ASTNode findCurrentNode(ASTNode leaf, BaseDocument doc, int caret) {
        if (leaf instanceof FieldNode) {
            if (!OccurrencesUtil.isCaretOnFieldType(((FieldNode) leaf), doc, caret)) {
                return leaf;
            }
        } else if (leaf instanceof PropertyNode) {
            if (!OccurrencesUtil.isCaretOnFieldType(((PropertyNode) leaf).getField(), doc, caret)) {
                return leaf;
            }
        } else if (leaf instanceof MethodNode) {
            MethodNode method = ((MethodNode) leaf);
            if (!OccurrencesUtil.isCaretOnReturnType(method, doc, caret)) {
                return leaf;
            }

            for (Parameter param : method.getParameters()) {
                if (!OccurrencesUtil.isCaretOnParamType(param, doc, caret)) {
                    return param;
                }
            }
        } else if (leaf instanceof Parameter) {
            if (!OccurrencesUtil.isCaretOnParamType(((Parameter) leaf), doc, caret)) {
                return leaf;
            }
        } else if (leaf instanceof DeclarationExpression) {

        }

        ClassNode currentType = ElementUtils.getType(leaf);
        if (currentType != null) {
            return currentType;
        }
        return leaf;
    }

    public static boolean isCaretOnReturnType(MethodNode method, BaseDocument doc, int cursorOffset) {
        if (getMethodReturnType(method, doc, cursorOffset) != OffsetRange.NONE) {
            return true;
        }
        return false;
    }

    private static OffsetRange getMethodReturnType(MethodNode method, BaseDocument doc, int cursorOffset) {
        if (!method.isDynamicReturnType()) {
            OffsetRange range = AstUtilities.getNextIdentifierByName(doc, method.getReturnType().getNameWithoutPackage(), getOffset(method, doc));
            if (range.containsInclusive(cursorOffset)) {
                return range;
            }
        }
        return OffsetRange.NONE;
    }

    public static boolean isCaretOnFieldType(FieldNode field, BaseDocument doc, int cursorOffset) {
        if (getFieldRange(field, doc, cursorOffset) != OffsetRange.NONE) {
            return true;
        }
        return false;
    }

    private static OffsetRange getFieldRange(FieldNode field, BaseDocument doc, int cursorOffset) {
        if (!field.isDynamicTyped()) {
            OffsetRange range = AstUtilities.getNextIdentifierByName(doc, field.getType().getNameWithoutPackage(), getOffset(field, doc));
            if (range.containsInclusive(cursorOffset)) {
                return range;
            }
        }
        return OffsetRange.NONE;
    }

    public static boolean isCaretOnParamType(Parameter param, BaseDocument doc, int cursorOffset) {
        if (getParameterRange(param, doc, cursorOffset) != OffsetRange.NONE) {
            return true;
        }
        return false;
    }

    private static OffsetRange getParameterRange(Parameter param, BaseDocument doc, int cursorOffset) {
        if (!param.isDynamicTyped()) {
            OffsetRange range = AstUtilities.getNextIdentifierByName(doc, param.getType().getNameWithoutPackage(), getOffset(param, doc));
            if (range.containsInclusive(cursorOffset)) {
                return range;
            }
        }
        return OffsetRange.NONE;
    }

    private static int getOffset(ASTNode node, BaseDocument doc) {
        return AstUtilities.getOffset(doc, node.getLineNumber(), node.getColumnNumber());
    }
}
