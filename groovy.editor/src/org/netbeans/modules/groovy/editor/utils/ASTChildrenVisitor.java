/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.groovy.editor.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;

/**
 * Visitor for finding direct chidren of AST node
 * 
 * @author Martin Adamek
 */
public class ASTChildrenVisitor implements GroovyCodeVisitor {

    private List<ASTNode> children = new ArrayList<>();
    
    public List<ASTNode> children() {
        return children;
    }
    
    @Override
    public void visitBlockStatement(BlockStatement block) {
        List statements = block.getStatements();
        for (Iterator iter = statements.iterator(); iter.hasNext(); ) {
            Statement statement = (Statement) iter.next();
            children.add(statement);
        }
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        children.add(forLoop.getCollectionExpression());
        children.add(forLoop.getLoopBlock());
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        children.add(loop.getBooleanExpression());
        children.add(loop.getLoopBlock());
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        children.add(loop.getLoopBlock());
        children.add(loop.getBooleanExpression());
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        children.add(ifElse.getBooleanExpression());
        children.add(ifElse.getIfBlock());
        children.add(ifElse.getElseBlock());
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        children.add(statement.getExpression());
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        children.add(statement.getExpression());
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        children.add(statement.getBooleanExpression());
        children.add(statement.getMessageExpression());
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        children.add(statement.getTryStatement());
        List list = statement.getCatchStatements();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            CatchStatement catchStatement = (CatchStatement) iter.next();
            children.add(catchStatement);
        }
        children.add(statement.getFinallyStatement());
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        children.add(statement.getExpression());
        List list = statement.getCaseStatements();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            CaseStatement caseStatement = (CaseStatement) iter.next();
            children.add(caseStatement);
        }
        children.add(statement.getDefaultStatement());
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        children.add(statement.getExpression());
        children.add(statement.getCode());
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        children.add(statement.getExpression());
        children.add(statement.getCode());
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        children.add(statement.getExpression());
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        children.add(call.getObjectExpression());
        children.add(call.getMethod());
        children.add(call.getArguments());
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        children.add(call.getArguments());
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        children.add(call.getArguments());
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        children.add(expression.getLeftExpression());
        children.add(expression.getRightExpression());
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        children.add(expression.getBooleanExpression());
        children.add(expression.getTrueExpression());
        children.add(expression.getFalseExpression());
    }
    
    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        visitTernaryExpression(expression);
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        children.add(expression.getCode());
    }
    
    @Override
    public void visitTupleExpression(TupleExpression expression) {
        visitListOfExpressions(expression.getExpressions());
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        visitListOfExpressions(expression.getExpressions());
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        visitListOfExpressions(expression.getExpressions());
        visitListOfExpressions(expression.getSizeExpression());
    }
    
    @Override
    public void visitMapExpression(MapExpression expression) {
        visitListOfExpressions(expression.getMapEntryExpressions());
        
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        children.add(expression.getKeyExpression());
        children.add(expression.getValueExpression());
        
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        children.add(expression.getFrom());
        children.add(expression.getTo());
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        children.add(expression.getExpression());
    }
 
    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        children.add(expression.getExpression());
        children.add(expression.getMethodName());
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        children.add(expression.getExpression());
    }
    
    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        children.add(expression.getExpression());
    }
    
    @Override
    public void visitCastExpression(CastExpression expression) {
        children.add(expression.getExpression());
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        visitBinaryExpression(expression);
    }
    
    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
    	children.add(expression.getObjectExpression());
    	children.add(expression.getProperty());
    }

    @Override
    public void visitAttributeExpression(AttributeExpression expression) {
    	children.add(expression.getObjectExpression());
    	children.add(expression.getProperty());
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        visitListOfExpressions(expression.getStrings());
        visitListOfExpressions(expression.getValues());
    }

    private void visitListOfExpressions(List list) {
        if (list==null) return;
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Expression expression = (Expression) iter.next();
            if (expression instanceof SpreadExpression) {
                Expression spread = ((SpreadExpression) expression).getExpression();
                children.add(spread);
            } else {
                children.add(expression);
            }
        }
    }
    
    @Override
    public void visitCatchStatement(CatchStatement statement) {
    	children.add(statement.getCode());
    }
    
    @Override
    public void visitArgumentlistExpression(ArgumentListExpression ale) {
    	visitTupleExpression(ale);
    }
    
    @Override
    public void visitClosureListExpression(ClosureListExpression cle) {
        visitListOfExpressions(cle.getExpressions());
    }

    // added in Groovy 1.6
    // TODO check this
    @Override
    public void visitBytecodeExpression(BytecodeExpression bce) {
    }
}
