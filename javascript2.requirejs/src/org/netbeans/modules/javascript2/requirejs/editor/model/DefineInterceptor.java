/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.requirejs.editor.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.javascript2.editor.api.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.api.lexer.LexUtilities;
import org.netbeans.modules.javascript2.editor.model.DeclarationScope;
import org.netbeans.modules.javascript2.editor.model.JsArray;
import org.netbeans.modules.javascript2.editor.model.JsFunction;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.model.TypeUsage;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionArgument;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.netbeans.modules.javascript2.requirejs.RequireJsPreferences;
import org.netbeans.modules.javascript2.requirejs.editor.EditorUtils;
import org.netbeans.modules.javascript2.requirejs.editor.FSCompletionUtils;
import org.netbeans.modules.javascript2.requirejs.editor.index.RequireJsIndex;
import org.netbeans.modules.javascript2.requirejs.editor.index.RequireJsIndexer;
import org.netbeans.modules.parsing.api.Source;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Pisl
 */
@FunctionInterceptor.Registration(priority = 350)
public class DefineInterceptor implements FunctionInterceptor {

    private static final Pattern PATTERN = Pattern.compile("define|requirejs|require");  //NOI18N
    private static final String CODE_COMPLETION_THREAD_NAME = "Code Completion";

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    public void intercept(String name, JsObject globalObject, DeclarationScope scope, ModelElementFactory factory, Collection<FunctionArgument> args) {
        FunctionArgument fArg = null;
        FunctionArgument modules = null;

        for (Iterator<FunctionArgument> it = args.iterator(); it.hasNext();) {
            FunctionArgument arg = it.next();
            switch (arg.getKind()) {
                case ANONYMOUS_OBJECT:
                case REFERENCE:
                    fArg = arg;
                    break;
                case ARRAY:
                    modules = arg;
                    break;
                case STRING:
                    if (args.size() == 1) {
                        modules = arg;
                    }
                    break;
                default:
            }
        }

        FileObject fo = globalObject.getFileObject();
        if (fo == null) {
            // no action
            return;
        }

        if (fArg != null) {
            if (fArg.getKind() == FunctionArgument.Kind.ANONYMOUS_OBJECT) {
                if (RequireJsIndexer.Factory.isScannerThread() && EditorUtils.DEFINE.equals(name)) {
                    JsObject anonym = (JsObject) fArg.getValue();
                    RequireJsIndexer.addTypes(fo.toURI(), Collections.singletonList(factory.newType(anonym.getFullyQualifiedName(), anonym.getOffset(), true)));

                }
            } else {
                List<String> fqn = (List<String>) fArg.getValue();
                JsObject posibleFunc = findJsObjectByName(globalObject, fqn);

                if (posibleFunc != null && posibleFunc instanceof JsFunction) {
                    JsFunction defFunc = (JsFunction) posibleFunc;
                    Source source = Source.create(fo);
                    List<String> paths = new ArrayList<String>();
                    if (modules != null) {
                        TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(source.createSnapshot().getTokenHierarchy(), modules.getOffset());
                        if (ts == null) {
                            return;
                        }
                        ts.move(modules.getOffset());
                        if (ts.moveNext()) {
                            Token<? extends JsTokenId> token = ts.token();
                            int index = 0;
                            while (ts.moveNext() && token.id() != JsTokenId.BRACKET_RIGHT_BRACKET) {
                                token = LexUtilities.findNext(ts, Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT, JsTokenId.LINE_COMMENT,
                                        JsTokenId.STRING_BEGIN, JsTokenId.STRING_END));
                                if (token.id() == JsTokenId.STRING) {
                                    while (index > paths.size()) {
                                        paths.add("");
                                    }
                                    paths.add(token.text().toString());
                                } else if (token.id() == JsTokenId.OPERATOR_COMMA) {
                                    index++;
                                }
                            }
                        }
                    }
                    if (saveToIndex()) {
                        if (EditorUtils.DEFINE.equals(name)) {
                            // save to the index the return types
                            Collection<? extends TypeUsage> returnTypes = defFunc.getReturnTypes();
                            RequireJsIndexer.addTypes(fo.toURI(), returnTypes);
                            if (!paths.isEmpty()) {
                                HashSet<String> plugins = new HashSet<>();
                                for (String path : paths) {
                                    String plugin = FSCompletionUtils.containsPlugin(path)
                                            ? path.substring(0, path.length() - FSCompletionUtils.removePlugin(path).length() - 1) : ""; //NOI18N
                                    if (!plugin.isEmpty() && !plugins.contains(plugin)) {
                                        plugins.add(plugin);
                                    }
                                }
                                if (!plugins.isEmpty()) {
                                    // save plugins to the index
                                    RequireJsIndexer.addUsedPlugings(fo.toURI(), plugins);
                                }
                            }
                        }
                    } else if (modules != null && modules.getValue() instanceof JsArray) {
                        Project project = FileOwnerQuery.getOwner(fo);
                        if (project == null || !RequireJsPreferences.getBoolean(project, RequireJsPreferences.ENABLED)) {
                            return;
                        }
                        // add assignments for the parameters
                        if (!paths.isEmpty()) {
                            RequireJsIndex rIndex = null;
                            try {
                                rIndex = RequireJsIndex.get(project);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            if (rIndex != null) {
                                Iterator<? extends JsObject> paramIterator = defFunc.getParameters().iterator();
                                for (String module : paths) {
                                    module = FSCompletionUtils.removePlugin(module);
                                    FileObject fileObject = FSCompletionUtils.findMappedFileObject(module, fo);
                                    if (fileObject != null) {
                                        module = fileObject.getName();
                                    }
                                    Collection<? extends TypeUsage> exposedTypes = rIndex.getExposedTypes(module, factory);
                                    if (paramIterator.hasNext()) {
                                        JsObject param = paramIterator.next();
                                        for (TypeUsage typeUsage : exposedTypes) {
                                            param.addAssignment(typeUsage, -1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (modules != null && modules.getValue() instanceof String) {
            Source source = Source.create(fo);
            Project project = FileOwnerQuery.getOwner(fo);
            if (project == null || !RequireJsPreferences.getBoolean(project, RequireJsPreferences.ENABLED)) {
                return;
            }
            TokenSequence<? extends JsTokenId> ts = LexUtilities.getJsTokenSequence(source.createSnapshot().getTokenHierarchy(), modules.getOffset());
            if (ts == null) {
                return;
            }
            ts.move(modules.getOffset());
            if (ts.movePrevious()) {
                Token<? extends JsTokenId> token = ts.token();
                token = LexUtilities.findPrevious(ts, Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT, JsTokenId.LINE_COMMENT,
                        JsTokenId.STRING_BEGIN, JsTokenId.BRACKET_LEFT_PAREN));
                if (token.id() == JsTokenId.IDENTIFIER && EditorUtils.REQUIRE.equals(token.text().toString()) && ts.movePrevious()) {
                    token = LexUtilities.findPrevious(ts, Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT, JsTokenId.LINE_COMMENT));
                    if (token.id() == JsTokenId.OPERATOR_ASSIGNMENT && ts.movePrevious()) {
                        token = LexUtilities.findPrevious(ts, Arrays.asList(JsTokenId.WHITESPACE, JsTokenId.EOL, JsTokenId.BLOCK_COMMENT, JsTokenId.LINE_COMMENT));
                        if (token.id() == JsTokenId.IDENTIFIER) {
                            // now we have the name of the object, that contains the expoert from module
                            RequireJsIndex rIndex = null;
                            try {
                                rIndex = RequireJsIndex.get(project);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            if (rIndex != null) {
                                FileObject fileObject = FSCompletionUtils.findMappedFileObject(modules.getValue().toString(), fo);
                                if (fileObject != null) {
                                    Collection<? extends TypeUsage> exposedTypes = rIndex.getExposedTypes(fileObject.getName(), factory);
                                    DeclarationScope declarationScope = getDeclarationScope(globalObject, modules.getOffset());
                                    JsObject object = null;
                                    String objectName = token.text().toString();
                                    while (declarationScope != null && object == null) {
                                        object = ((JsObject) declarationScope).getProperty(objectName);
                                        declarationScope = declarationScope.getParentScope();
                                    }
                                    if (object != null) {
                                        for (TypeUsage typeUsage : exposedTypes) {
                                            object.addAssignment(typeUsage, modules.getOffset());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private boolean saveToIndex() {
        return RequireJsIndexer.Factory.isScannerThread() && !CODE_COMPLETION_THREAD_NAME.equals(Thread.currentThread().getName());
    }

    public static JsObject findJsObjectByName(JsObject global, List<String> fqn) {
        JsObject result = global;
        JsObject property = result;
        for (Iterator<String> it = fqn.iterator(); it.hasNext();) {
            String token = it.next();
            property = result.getProperty(token);
            if (property == null) {
                result = (result instanceof JsFunction)
                        ? ((JsFunction) result).getParameter(token)
                        : null;
                if (result == null) {
                    break;
                }
            } else {
                result = property;
            }
        }
        return result;
    }

    public static DeclarationScope getDeclarationScope(JsObject global, int offset) {
        DeclarationScope dScope = (DeclarationScope) global;
        DeclarationScope result = null;
        if (result == null) {
            if (((JsObject) dScope).getOffsetRange().containsInclusive(offset)) {
                result = dScope;
                boolean deep = true;
                while (deep) {
                    deep = false;
                    for (DeclarationScope innerScope : result.getChildrenScopes()) {
                        if (((JsObject) innerScope).getOffsetRange().containsInclusive(offset)) {
                            result = innerScope;
                            deep = true;
                            break;
                        }

                    }
                }
            }
        }
        return result;
    }
}
