/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.editor.verification;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.csl.api.Hint;
import org.netbeans.modules.csl.api.HintSeverity;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.api.ElementQuery.Index;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.elements.BaseFunctionElement;
import org.netbeans.modules.php.editor.api.elements.ElementFilter;
import org.netbeans.modules.php.editor.api.elements.ParameterElement;
import org.netbeans.modules.php.editor.model.Model;
import org.netbeans.modules.php.editor.model.ModelUtils;
import org.netbeans.modules.php.editor.model.TypeScope;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.ASTNode;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayAccess;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.CatchClause;
import org.netbeans.modules.php.editor.parser.astnodes.ConstantDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.ContinueStatement;
import org.netbeans.modules.php.editor.parser.astnodes.DoStatement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FieldsDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.ForEachStatement;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.GlobalStatement;
import org.netbeans.modules.php.editor.parser.astnodes.GotoLabel;
import org.netbeans.modules.php.editor.parser.astnodes.GotoStatement;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.InterfaceDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.ListVariable;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.NamespaceDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocBlock;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocMethodTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocStaticAccessType;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocTypeTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPDocVarTypeTag;
import org.netbeans.modules.php.editor.parser.astnodes.PHPVarComment;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.editor.parser.astnodes.Reference;
import org.netbeans.modules.php.editor.parser.astnodes.SingleFieldDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.StaticFieldAccess;
import org.netbeans.modules.php.editor.parser.astnodes.UseStatement;
import org.netbeans.modules.php.editor.parser.astnodes.UseStatementPart;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Ondrej Brejla <obrejla@netbeans.org>
 */
public class UninitializedVariableHint extends AbstractHint implements PHPRuleWithPreferences {

    private static final String HINT_ID = "Uninitialized.Variable.Hint"; //NOI18N
    private static final String CHECK_VARIABLES_INITIALIZED_BY_REFERENCE = "php.verification.check.variables.initialized.by.reference"; //NOI18N
    private static final List<String> UNCHECKED_VARIABLES = new LinkedList<String>();
    private Preferences preferences;

    static {
        UNCHECKED_VARIABLES.add("this"); //NOI18N
        UNCHECKED_VARIABLES.add("GLOBALS"); //NOI18N
        UNCHECKED_VARIABLES.add("_SERVER"); //NOI18N
        UNCHECKED_VARIABLES.add("_GET"); //NOI18N
        UNCHECKED_VARIABLES.add("_POST"); //NOI18N
        UNCHECKED_VARIABLES.add("_FILES"); //NOI18N
        UNCHECKED_VARIABLES.add("_COOKIE"); //NOI18N
        UNCHECKED_VARIABLES.add("_SESSION"); //NOI18N
        UNCHECKED_VARIABLES.add("_REQUEST"); //NOI18N
        UNCHECKED_VARIABLES.add("_ENV"); //NOI18N
        UNCHECKED_VARIABLES.add("argc"); //NOI18N
        UNCHECKED_VARIABLES.add("argv"); //NOI18N
        UNCHECKED_VARIABLES.add("HTTP_RAW_POST_DATA"); //NOI18N
        UNCHECKED_VARIABLES.add("php_errormsg"); //NOI18N
        UNCHECKED_VARIABLES.add("http_response_header"); //NOI18N
    }

    @Override
    void compute(PHPRuleContext context, List<Hint> hints) {
        PHPParseResult phpParseResult = (PHPParseResult) context.parserResult;
        if (phpParseResult.getProgram() == null) {
            return;
        }
        FileObject fileObject = phpParseResult.getSnapshot().getSource().getFileObject();
        if (fileObject == null) {
            return;
        }
        CheckVisitor checkVisitor = new CheckVisitor(fileObject, phpParseResult.getModel());
        phpParseResult.getProgram().accept(checkVisitor);
        hints.addAll(checkVisitor.getHints());
    }

    private final class CheckVisitor extends DefaultVisitor {

        private final FileObject fileObject;
        private final Stack<ASTNode> parentNodes = new Stack<ASTNode>();
        private final Map<ASTNode, List<Variable>> initializedVariablesAll = new HashMap<ASTNode, List<Variable>>();
        private final Map<ASTNode, List<Variable>> uninitializedVariablesAll = new HashMap<ASTNode, List<Variable>>();
        private final List<Hint> hints = new LinkedList<Hint>();
        private final Model model;
        private final Map<String, Set<BaseFunctionElement>> invocationCache = new HashMap<String, Set<BaseFunctionElement>>();

        private CheckVisitor(FileObject fileObject, Model model) {
            this.fileObject = fileObject;
            this.model = model;
        }

        private Collection<? extends Hint> getHints() {
            for (ASTNode scopeNode : uninitializedVariablesAll.keySet()) {
                createHints(getUninitializedVariables(scopeNode));
            }
            return hints;
        }

        @Messages({
            "# {0} - Name of the variable",
            "UninitializedVariableVariableHintCustom=Variable ${0} seems to be uninitialized"
        })
        private void createHints(List<Variable> uninitializedVariables) {
            for (Variable variable : uninitializedVariables) {
                int start = variable.getStartOffset() + 1;
                int end = variable.getEndOffset();
                OffsetRange offsetRange = new OffsetRange(start, end);
                hints.add(new Hint(UninitializedVariableHint.this, Bundle.UninitializedVariableVariableHintCustom(getVariableName(variable)), fileObject, offsetRange, null, 500));
            }
        }

        @Override
        public void visit(Program node) {
            parentNodes.push(node);
            super.visit(node);
            parentNodes.pop();
        }

        @Override
        public void visit(NamespaceDeclaration node) {
            parentNodes.push(node);
            super.visit(node);
            parentNodes.pop();
        }

        @Override
        public void visit(FunctionDeclaration node) {
            parentNodes.push(node);
            super.visit(node);
            parentNodes.pop();
        }

        @Override
        public void visit(Assignment node) {
            VariableBase leftHandSide = node.getLeftHandSide();
            initializeVariableBase(leftHandSide);
            scan(node.getRightHandSide());
        }

        @Override
        public void visit(CatchClause node) {
            initializeVariable(node.getVariable());
            scan(node.getClassName());
            scan(node.getBody());
        }

        @Override
        public void visit(DoStatement node) {
            scan(node.getBody());
            scan(node.getCondition());
        }

        @Override
        public void visit(ForEachStatement node) {
            scan(node.getExpression());
            initializeExpression(node.getKey());
            initializeExpression(node.getValue());
            scan(node.getStatement());
        }

        @Override
        public void visit(FormalParameter node) {
            Expression expression = node.getParameterName();
            if (expression instanceof Reference) {
                Reference reference = (Reference) expression;
                expression = reference.getExpression();
            }
            initializeExpression(expression);
        }

        @Override
        public void visit(GlobalStatement node) {
            for (Variable variable : node.getVariables()) {
                initializeVariable(variable);
            }
        }

        @Override
        public void visit(Variable node) {
            if (isProcessableVariable(node)) {
                addUninitializedVariable(node);
            }
        }

        @Override
        public void visit(FunctionInvocation node) {
            if (checkVariablesInitializedByReference(preferences)) {
                List<Expression> invocationParametersExp = node.getParameters();
                String functionName = CodeUtils.extractFunctionName(node);
                if (functionName != null) {
                    Set<BaseFunctionElement> allFunctions = invocationCache.get(functionName);
                    if (allFunctions == null) {
                        allFunctions = new HashSet<BaseFunctionElement>(model.getIndexScope().getIndex().getFunctions(NameKind.create(functionName, QuerySupport.Kind.EXACT)));
                        invocationCache.put(functionName, allFunctions);
                    }
                    processAllFunctions(allFunctions, invocationParametersExp);
                }
                scan(node.getFunctionName());
            } else {
                super.visit(node);
            }
        }

        @Override
        public void visit(MethodInvocation node) {
            if (checkVariablesInitializedByReference(preferences)) {
                List<Expression> invocationParametersExp = node.getMethod().getParameters();
                if (invocationParametersExp.size() > 0) {
                    String functionName = CodeUtils.extractFunctionName(node.getMethod());
                    if (functionName != null) {
                        Set<BaseFunctionElement> allFunctions = invocationCache.get(functionName);
                        if (allFunctions == null) {
                            Collection<? extends TypeScope> resolvedTypes = ModelUtils.resolveType(model, node);
                            if (resolvedTypes.size() > 0) {
                                TypeScope resolvedType = ModelUtils.getFirst(resolvedTypes);
                                Index index = model.getIndexScope().getIndex();
                                allFunctions = new HashSet<BaseFunctionElement>(ElementFilter.forName(NameKind.exact(functionName)).filter(index.getAllMethods(resolvedType)));
                                invocationCache.put(functionName, allFunctions);
                            }
                        }
                        processAllFunctions(allFunctions, invocationParametersExp);
                    }
                }
            } else {
                super.visit(node);
            }
        }

        private void processAllFunctions(Set<BaseFunctionElement> allFunctions, List<Expression> invocationParametersExp) {
            if (allFunctions != null && !allFunctions.isEmpty()) {
                BaseFunctionElement methodElement = ModelUtils.getFirst(allFunctions);
                if (methodElement != null) {
                    List<ParameterElement> methodParameters = methodElement.getParameters();
                    if (methodParameters.size() == invocationParametersExp.size()) {
                        for (int i = 0; i < methodParameters.size(); i++) {
                            Expression invocationParameterExp = invocationParametersExp.get(i);
                            if (methodParameters.get(i).isReference()) {
                                initializeExpression(invocationParameterExp);
                            } else {
                                scan(invocationParameterExp);
                            }
                        }
                    } else {
                        scan(invocationParametersExp);
                    }
                }
            } else {
                scan(invocationParametersExp);
            }
        }

        @Override
        public void visit(ConstantDeclaration node) {
            // intentionally
        }

        @Override
        public void visit(ContinueStatement node) {
            // intentionally
        }

        @Override
        public void visit(FieldsDeclaration node) {
            // intentionally
        }

        @Override
        public void visit(GotoLabel node) {
            // intentionally
        }

        @Override
        public void visit(GotoStatement node) {
            // intentionally
        }

        @Override
        public void visit(InterfaceDeclaration node) {
            // intentionally
        }

        @Override
        public void visit(PHPDocBlock node) {
            // intentionally
        }

        @Override
        public void visit(PHPDocTypeTag node) {
            // intentionally
        }

        @Override
        public void visit(PHPDocMethodTag node) {
            // intentionally
        }

        @Override
        public void visit(PHPDocVarTypeTag node) {
            // intentionally
        }

        @Override
        public void visit(PHPDocStaticAccessType node) {
            // intentionally
        }

        @Override
        public void visit(PHPVarComment node) {
            // intentionally
        }

        @Override
        public void visit(SingleFieldDeclaration node) {
            // intentionally
        }

        @Override
        public void visit(StaticFieldAccess node) {
            // intetionally
        }

        @Override
        public void visit(UseStatement node) {
            // intentionally
        }

        @Override
        public void visit(UseStatementPart node) {
            // intentionally
        }

        private boolean isProcessableVariable(Variable node) {
            Identifier identifier = getIdentifier(node);
            return identifier != null && !UNCHECKED_VARIABLES.contains(identifier.getName()) && !isInitialized(node) && !isUninitialized(node);
        }


        private void initializeVariable(Variable variable) {
            if (!isInitialized(variable) && !isUninitialized(variable)) {
                addInitializedVariable(variable);
            }
        }

        private boolean isInitialized(Variable node) {
            return contains(getInitializedVariables(parentNodes.peek()), node);
        }

        private boolean isUninitialized(Variable node) {
            return contains(getUninitializedVariables(parentNodes.peek()), node);
        }

        private boolean contains(List<Variable> scopeVariables, Variable node) {
            boolean retval = false;
            String currentVariableName = getVariableName(node);
            for (Variable variable : scopeVariables) {
                if (currentVariableName.equals(getVariableName(variable))) {
                    retval = true;
                    break;
                }
            }
            return retval;
        }

        private String getVariableName(Variable variable) {
            String retval = "";
            Identifier identifier = getIdentifier(variable);
            if (identifier != null) {
                retval = identifier.getName();
            }
            return retval;
        }

        private void initializeExpression(Expression expression) {
            if (expression instanceof Variable) {
                initializeVariable((Variable) expression);
            } else if (expression instanceof Reference) {
                initializeReference((Reference) expression);
            }
        }

        private void initializeReference(Reference node) {
            initializeExpression(node.getExpression());
        }

        private void initializeVariableBase(VariableBase variableBase) {
            if (variableBase instanceof ArrayAccess) {
                initializeArrayAccessVariable((ArrayAccess) variableBase);
            } else if (variableBase instanceof Variable) {
                initializeVariable((Variable) variableBase);
            } else if (variableBase instanceof ListVariable) {
                initializeListVariable((ListVariable) variableBase);
            } else {
                super.visit(variableBase);
            }
        }

        private void initializeArrayAccessVariable(ArrayAccess node) {
            VariableBase name = node.getName();
            if (name instanceof Variable) {
                initializeVariable((Variable) name);
            }
        }

        private void initializeListVariable(ListVariable node) {
            List<VariableBase> variables = node.getVariables();
            for (VariableBase variableBase : variables) {
                initializeVariableBase(variableBase);
            }
        }

        private void addInitializedVariable(Variable node) {
            List<Variable> scopeVariables = getInitializedVariables(parentNodes.peek());
            scopeVariables.add(node);
        }

        private void addUninitializedVariable(Variable node) {
            List<Variable> scopeVariables = getUninitializedVariables(parentNodes.peek());
            scopeVariables.add(node);
        }

        private List<Variable> getInitializedVariables(ASTNode parent) {
            List<Variable> scopeVariables = initializedVariablesAll.get(parent);
            if (scopeVariables == null) {
                scopeVariables = new LinkedList<Variable>();
                initializedVariablesAll.put(parent, scopeVariables);
            }
            return scopeVariables;
        }

        private List<Variable> getUninitializedVariables(ASTNode parent) {
            List<Variable> scopeVariables = uninitializedVariablesAll.get(parent);
            if (scopeVariables == null) {
                scopeVariables = new LinkedList<Variable>();
                uninitializedVariablesAll.put(parent, scopeVariables);
            }
            return scopeVariables;
        }

        @CheckForNull
        private Identifier getIdentifier(Variable variable) {
            Identifier retval = null;
            if (variable != null && variable.isDollared()) {
                if (variable.getName() instanceof Identifier) {
                    retval = (Identifier) variable.getName();
                }
            }
            return retval;
        }

    }

    @Override
    public String getId() {
        return HINT_ID;
    }

    @Override
    @Messages("UninitializedVariableHintDesc=Detects variables which are used, but not initialized.<br><br>Every variable should be initialized before its first use.")
    public String getDescription() {
        return Bundle.UninitializedVariableHintDesc();
    }

    @Override
    @Messages("UninitializedVariableHintDispName=Uninitialized Variables")
    public String getDisplayName() {
        return Bundle.UninitializedVariableHintDispName();
    }

    @Override
    public HintSeverity getDefaultSeverity() {
        return HintSeverity.WARNING;
    }

    @Override
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public JComponent getCustomizer(Preferences preferences) {
        return new UninitializedVariableCustomizer(preferences, this);
    }

    public void setCheckVariablesInitializedByReference(Preferences preferences, boolean isEnabled) {
        preferences.putBoolean(CHECK_VARIABLES_INITIALIZED_BY_REFERENCE, isEnabled);
    }

    public boolean checkVariablesInitializedByReference(Preferences preferences) {
        return preferences.getBoolean(CHECK_VARIABLES_INITIALIZED_BY_REFERENCE, false);
    }

}
