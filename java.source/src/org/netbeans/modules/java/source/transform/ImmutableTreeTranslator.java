
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.java.source.transform;

import org.netbeans.modules.java.source.query.CommentHandler;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.util.Context;
import javax.lang.model.element.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.java.source.builder.ASTService;
import org.netbeans.modules.java.source.builder.CommentHandlerService;
import org.netbeans.modules.java.source.builder.QualIdentTree;
import org.netbeans.modules.java.source.builder.TreeFactory;
import org.netbeans.modules.java.source.pretty.ImportAnalysis2;

import static org.netbeans.modules.java.source.save.PositionEstimator.*;

/** A subclass of Tree.Visitor, this class defines
 *  a general tree translator pattern. Translation proceeds recursively in
 *  left-to-right order down a tree.  If a node needs to be translated, a
 *  replacement is constructed. There is one visitor method in this class
 *  for every possible kind of tree node.  To obtain a specific
 *  translator, it suffices to override those visitor methods which
 *  do some interesting work. The translator class itself takes care of all
 *  navigational aspects.
 *
 *  All visitor methods must follow the following Pattern:
 *  <br>public Tree visitT(T tree) {
 *  <br>&nbsp;&nbsp;rewriteChildren(tree);
 *  <br>&nbsp;&nbsp;// fetch field values as tree.X
 *  <br>&nbsp;&nbsp;// <i>Do not assign to any fields of tree</i>
 *  <br>&nbsp;&nbsp;return new_tree_to_replace_old
 *  <br>&nbsp;&nbsp;// (returning the original tree leaves it unchanged)
 *  <br>}
 */
public class ImmutableTreeTranslator implements TreeVisitor<Tree,Object> {

    public Element currentSym;

    protected TreeFactory make;
    protected CommentHandler comments;
    protected ASTService model;
    private CompilationUnitTree topLevel;
    private ImportAnalysis2 importAnalysis;
    private Map<Tree, Object> tree2Tag;

    public void attach(Context context, ImportAnalysis2 importAnalysis, CompilationUnitTree topLevel, Map<Tree, Object> tree2Tag) {
        make = TreeFactory.instance(context);
        comments = CommentHandlerService.instance(context);
        model = ASTService.instance(context);
        this.importAnalysis = importAnalysis;
        this.topLevel = topLevel;
        this.tree2Tag = tree2Tag;
    }
    
    public void attach(Context context) {
        attach(context, new ImportAnalysis2(context), null, null);
    }
    
    public void release() {
        make = null;
        comments = null;
        model = null;
        topLevel = null;
        importAnalysis = null;
    }

    /** Visitor method: Translate a single node.
     */
    public Tree translate(Tree tree) {
	if (tree == null)
	    return null;
	else {
	    Tree t = tree.accept(this, null);
            
            if (tree2Tag != null && tree != t) {
                tree2Tag.put(t, tree2Tag.get(tree));
            }
            
            return t;
        }
    }

    public <T extends Tree> T translateClassRef(T tree) {
	return (T)translate(tree);
    }
    
    public final <T extends Tree> List<T> translateClassRef(List<T> trees) {
	if (trees == null || trees.isEmpty()) 
            return trees;
        List<T> newTrees = new ArrayList<T>();
        boolean changed = false;
        for (T t : trees) {
            T newT = translateClassRef(t);
            if (newT != t)
                changed = true;
            if (newT != null)
                newTrees.add(newT);
        }
        return changed ? newTrees : trees;
    }

    /** Visitor method: Translate a single node.
     *  return type is guaranteed to be the same as the input type
     */
    public <T extends Tree> T translateStable(T tree) {
	Tree t2 = translate(tree);
	if(t2!=null && t2.getClass()!=tree.getClass()) {
	    if(t2.getClass()!=tree.getClass()) {
                //possibly import analysis rewrote QualIdentTree->IdentifierTree or QIT->MemberSelectTree:
                if (   tree.getClass() != QualIdentTree.class
                    || (t2.getKind() != Kind.IDENTIFIER) && (t2.getKind() != Kind.MEMBER_SELECT)) {
                    System.err.println("Rewrite stability problem: got "+t2.getClass()
                        +"\n\t\texpected "+tree.getClass());
                    return tree;
                }
	    }
	}
	return (T)t2;
    }
    
    /**
     * Returns true if the tree is an empty block or statement.
     */
    public static boolean isEmpty(Tree t) {
        if(t==null) return true;
        switch(t.getKind()) {
            default:
                return false;
            case BLOCK:
                for (StatementTree stat : ((BlockTree)t).getStatements())
                    if (!isEmpty(stat))
                        return false;
                return true;
            case EMPTY_STATEMENT:
                return true;
        }
    }

    /** Visitor method: translate a list of nodes.
     */
    public <T extends Tree> List<T> translate(List<T> trees) {
	if (trees == null || trees.isEmpty()) 
            return trees;
        List<T> newTrees = new ArrayList<T>();
        boolean changed = false;
        for (T t : trees) {
            T newT = (T)translate(t);
            if (newT != t)
                changed = true;
            if (newT != null)
                newTrees.add(newT);
        }
        return changed ? newTrees : trees;
    }
    
    /** Visitor method: translate a list of nodes.
     *  List type is guaranteed to be the same as the input type.
     */
    public <T extends Tree> List<T> translateStable(List<T> trees) {
	if (trees == null || trees.isEmpty()) return trees;
        List<T> newTrees = new ArrayList<T>();
        boolean changed = false;
        for (T t : trees) {
            T newT = translateStable(t);
            if (newT != t)
                changed = true;
            if (newT != null)
                newTrees.add(newT);
        }
        return changed ? newTrees : trees;
    }
    
    /**
     * Remove any empty statements, chop unreachable statements, and 
     * inline block statements (if possible).
     *
     * Note:  this method should only be called on lists which were 
     * transformed, to avoid adding deltas not in the changes set.
     */
    protected <T extends Tree> List<T> optimize(List<T> trees) {
	if (trees == null || trees.isEmpty()) 
            return trees;
        List<T> newTrees = new ArrayList<T>();
        for (T t : trees) {
            if (t == null || isEmpty(t))
                continue;
            switch (t.getKind()) {
                case BLOCK: {
// #pf: the following code was commented out because it is not always reasonable
// to inline functionality. -- Sometimes there is conflict between minimal 
// changes and this optimization. -- Consider rename refactoring in case --
// User expects identifier change, but optimization replaces whole block. 
// See BodyStatementTest.java:testRenameInCase() for details.
//                    BlockTree bt = (BlockTree)t;
//                    boolean canInline = !bt.isStatic(); // don't inline static initializers
//                    if (canInline)
//                        for (StatementTree st : bt.getStatements())
//                            if (st instanceof VariableTree) {
//                                canInline = false;
//                                break;
//                            }
//                    if (canInline) {
//                        // add statements instead of block
//                        for (StatementTree st : bt.getStatements())
//                            newTrees.add((T)st);
//                    }
//                    else
//                        newTrees.add(t); // just add block
//                    break;
                }
                case RETURN:
                case THROW:
                case BREAK:
                case CONTINUE:
                    newTrees.add(t);
                    // any subsequent statements are chopped
                    return equals(trees, newTrees) ? trees : newTrees;
                default:
                    newTrees.add(t);
                    break;
            }
        }
        return equals(trees, newTrees) ? trees : newTrees;
    }
    
    /**
     * Implements the equals contract for List, which javac's List doesn't 
     * support.  Two Lists are considered equal if they are the same length,
     * and have the same objects in the same order.
     */
    private static <T extends Tree> boolean equals(List<T> list1, List<T> list2) {
        int n = list1.size();
        if (n != list2.size())
            return false;
        for (int i = 0; i < n; i++)
            if (list1.get(i) != list2.get(i))
                return false;
        return true;
    }
    
    public final void copyCommentTo(Tree from1, Tree from2, Tree to) {
	copyCommentTo(from1,to);
	if(from1 != from2) copyCommentTo(from2,to);
    }
    
    public final void copyCommentTo(Tree from, Tree to) {
        comments.copyComments(from, to);
    }

    int size(List<?> list) {
        return list == null ? 0 : list.size();
    }
    
    private void copyPosTo(Tree from, Tree to) {
        model.setPos(to, model.getPos(from));
    }
    
    private boolean safeEquals(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

/* ***************************************************************************
 * Visitor methods
 ****************************************************************************/

    public Tree visitCompilationUnit(CompilationUnitTree tree, Object p) {
	topLevel = tree;
	CompilationUnitTree result = rewriteChildren(topLevel);
	topLevel = null;
        return result;
    }
    public Tree visitImport(ImportTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitClass(ClassTree tree, Object p) {
        Element oldSym = currentSym;
        currentSym = model.getElement(tree);
        importAnalysis.classEntered(tree);
	ClassTree result = rewriteChildren(tree);
        importAnalysis.classLeft();
        currentSym = oldSym;
        return result;
    }
    public Tree visitMethod(MethodTree tree, Object p) {
        Element oldSym = currentSym;
        currentSym = model.getElement(tree);
	MethodTree result = rewriteChildren(tree);
        currentSym = oldSym;
        return result;
    }
	
    public Tree visitVariable(VariableTree tree, Object p) {
        Element oldSym = currentSym;
        currentSym = model.getElement(tree);
	VariableTree result = rewriteChildren(tree);
        currentSym = oldSym;
        return result;
    }
	
    public Tree visitEmptyStatement(EmptyStatementTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitBlock(BlockTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitDoWhileLoop(DoWhileLoopTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitWhileLoop(WhileLoopTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitForLoop(ForLoopTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitEnhancedForLoop(EnhancedForLoopTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitLabeledStatement(LabeledStatementTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitSwitch(SwitchTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitCase(CaseTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitSynchronized(SynchronizedTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitTry(TryTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitCatch(CatchTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitConditionalExpression(ConditionalExpressionTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitIf(IfTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitExpressionStatement(ExpressionStatementTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitBreak(BreakTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitContinue(ContinueTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitReturn(ReturnTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitThrow(ThrowTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitAssert(AssertTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitMethodInvocation(MethodInvocationTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitNewClass(NewClassTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitNewArray(NewArrayTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitParenthesized(ParenthesizedTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitAssignment(AssignmentTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitCompoundAssignment(CompoundAssignmentTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitUnary(UnaryTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitBinary(BinaryTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitTypeCast(TypeCastTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitInstanceOf(InstanceOfTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitArrayAccess(ArrayAccessTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitMemberSelect(MemberSelectTree tree, Object p) {
        if (tree instanceof QualIdentTree) {
            Element el = ((QualIdentTree) tree).sym;
            
            return importAnalysis.resolveImport(tree, el);
        } else {
            return rewriteChildren(tree);
        }
    }
    public Tree visitIdentifier(IdentifierTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitLiteral(LiteralTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitPrimitiveType(PrimitiveTypeTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitArrayType(ArrayTypeTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitParameterizedType(ParameterizedTypeTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitTypeParameter(TypeParameterTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitWildcard(WildcardTree tree, Object p) {
        return rewriteChildren(tree);
    }
    public Tree visitAnnotation(AnnotationTree tree, Object p) {
        return rewriteChildren(tree);
    }
    public Tree visitModifiers(ModifiersTree tree, Object p) {
        return rewriteChildren(tree);
    }
    public Tree visitErroneous(ErroneousTree tree, Object p) {
	return rewriteChildren(tree);
    }
    public Tree visitOther(Tree tree, Object p) {
	throw new Error("Tree not overloaded: "+tree);
    }

    /* * * * * * * * * * * * Visitor helpers* * * * * * * * * * * * * */
    protected final CompilationUnitTree rewriteChildren(CompilationUnitTree tree) {
	ExpressionTree pid = (ExpressionTree)translate(tree.getPackageName());
        
        importAnalysis.setCompilationUnit(tree);
        importAnalysis.setPackage(tree.getPackageName());
        importAnalysis.setImports(translate(tree.getImports()));
        
        List<? extends Tree> types = translate(tree.getTypeDecls());
        List<? extends ImportTree> imps = importAnalysis.getImports();
        
	if (pid!=tree.getPackageName() || !imps.equals(tree.getImports()) ||
            !types.equals(tree.getTypeDecls())) {
	    CompilationUnitTree n = make.CompilationUnit(pid, imps, types, tree.getSourceFile());
            model.setElement(n, model.getElement(tree));
	    copyCommentTo(tree,n);
            model.setPos(n, model.getPos(tree));
	    tree = n;
	}
	return tree;
    }

    protected final ImportTree rewriteChildren(ImportTree tree) {
	Tree qualid = translateClassRef(tree.getQualifiedIdentifier());
        if (qualid == tree.getQualifiedIdentifier())
            qualid = translate(tree.getQualifiedIdentifier());
	if (qualid!=tree.getQualifiedIdentifier()) {
	    ImportTree n = make.Import(qualid, tree.isStatic());
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final ClassTree rewriteChildren(ClassTree tree) {
        ModifiersTree mods = (ModifiersTree)translate(tree.getModifiers());
	List<? extends TypeParameterTree> typarams = translateStable(tree.getTypeParameters());
	Tree extending = translateClassRef(tree.getExtendsClause());
	List<? extends ExpressionTree> implementing = 
            translateClassRef((List<? extends ExpressionTree>)tree.getImplementsClause());
	List<? extends Tree> defs = translate(tree.getMembers());
        boolean typeChanged = !typarams.equals(tree.getTypeParameters()) || 
            extending != tree.getExtendsClause() ||
            !implementing.equals(tree.getImplementsClause());
	if (typeChanged || mods != tree.getModifiers() || 
            !defs.equals(tree.getMembers())) {
	    ClassTree n = make.Class(mods, tree.getSimpleName(), typarams,
                                     extending, implementing, defs);
            if (!typeChanged) {
                model.setElement(n, model.getElement(tree));
                model.setType(n, model.getType(tree));
            }
	    copyCommentTo(tree,n);
            if (tree.getMembers().size() == defs.size())
                model.setPos(n, model.getPos(tree));
            else
                copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final MethodTree rewriteChildren(MethodTree tree) {
        ModifiersTree mods = (ModifiersTree)translate(tree.getModifiers());
	ExpressionTree restype = (ExpressionTree)translateClassRef(tree.getReturnType());
	List<? extends TypeParameterTree> typarams = translateStable(tree.getTypeParameters());
	List<? extends VariableTree> params = translateStable(tree.getParameters());
	List<? extends ExpressionTree> thrown = translateStable(tree.getThrows());
        ExpressionTree defaultValue = (ExpressionTree)translate(tree.getDefaultValue());
	BlockTree body = (BlockTree)translate(tree.getBody());
        
	if (restype!=tree.getReturnType() || !typarams.equals(tree.getTypeParameters()) || 
            !params.equals(tree.getParameters()) || !thrown.equals(tree.getThrows()) || 
            mods!=tree.getModifiers() || defaultValue!=tree.getDefaultValue() || 
            body!=tree.getBody()) {
            MethodTree n  = make.Method(mods, tree.getName().toString(), restype, typarams,
                                        params, thrown, body, defaultValue);
            
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
	
    protected final VariableTree rewriteChildren(VariableTree tree) {
        ModifiersTree mods = (ModifiersTree)translate(tree.getModifiers());
	ExpressionTree vartype = (ExpressionTree)translateClassRef(tree.getType());
	ExpressionTree init = (ExpressionTree)translate(tree.getInitializer());
	if (vartype!=tree.getType() || mods!=tree.getModifiers() || init!=tree.getInitializer()) {
	    VariableTree n = make.Variable(mods, tree.getName().toString(), vartype, init);
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
	
    protected final EmptyStatementTree rewriteChildren(EmptyStatementTree tree) {
	return tree;
    }

    protected final BlockTree rewriteChildren(BlockTree tree) {
        List<? extends StatementTree> oldStats = tree.getStatements();
	List<? extends StatementTree> newStats = translate(oldStats);
	if (!newStats.equals(oldStats)) {
            //the newStates can contain elements from oldStats, so optimization could introduce new deltas:
//            newStats = optimize(newStats);
	    BlockTree n = make.Block(newStats, tree.isStatic());
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            if (newStats.size() != oldStats.size() ||
                (oldStats.size() > 0 && newStats.size() > 0 &&
                 oldStats.get(0) != newStats.get(0) &&
                 (model.getPos(oldStats.get(0)) < 0 || model.getPos(newStats.get(0)) < 0))) {
		/* Don't set the block's pos, because if it is a method body
		 * with a synthetic first statement (ie. "super()"), then
		 * pretty printers will print it since its pos is different
		 * from its parent's.  Instead, just mark the topLevel so
		 * Commit knows to save it.
		 */
                assert topLevel != null;
//                if (topLevel == null)
//                    topLevel = model.getTopLevel(tree);
                model.setPos(topLevel, NOPOS);
            } else
                copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final DoWhileLoopTree rewriteChildren(DoWhileLoopTree tree) {
	StatementTree body = (StatementTree)translate(tree.getStatement());
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	if (body!=tree.getStatement() || cond!=tree.getCondition()) {
	    DoWhileLoopTree n = make.DoWhileLoop(cond, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final WhileLoopTree rewriteChildren(WhileLoopTree tree) {
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	StatementTree body = (StatementTree)translate(tree.getStatement());
	if (cond!=tree.getCondition() || body!=tree.getStatement()) {
	    WhileLoopTree n = make.WhileLoop(cond, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final ForLoopTree rewriteChildren(ForLoopTree tree) {
	List<? extends StatementTree> init = translate(tree.getInitializer());
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	List<? extends ExpressionStatementTree> step = translate(tree.getUpdate());
	StatementTree body = (StatementTree)translate(tree.getStatement());
	if (!init.equals(tree.getInitializer()) || cond!=tree.getCondition() || 
            !step.equals(tree.getUpdate()) || body!=tree.getStatement()) {
            if (init != tree.getInitializer())
                init = optimize(init);
            if (step != tree.getUpdate())
                step = optimize(step);
	    ForLoopTree n = make.ForLoop(init, cond, step, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            if (tree.getInitializer().size() != init.size() || 
                tree.getUpdate().size() != step.size())
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final EnhancedForLoopTree rewriteChildren(EnhancedForLoopTree tree) {
	VariableTree var = (VariableTree)translate(tree.getVariable());
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	StatementTree body = (StatementTree)translate(tree.getStatement());
	if (var!=tree.getVariable() || expr!=tree.getExpression() ||
            body!=tree.getStatement()) {
	    EnhancedForLoopTree n = make.EnhancedForLoop(var, expr, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final LabeledStatementTree rewriteChildren(LabeledStatementTree tree) {
	StatementTree body = (StatementTree)translate(tree.getStatement());
	if (body!=tree.getStatement()) {
	    LabeledStatementTree n = make.LabeledStatement(tree.getLabel(), body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final SwitchTree rewriteChildren(SwitchTree tree) {
	ExpressionTree selector = (ExpressionTree)translate(tree.getExpression());
	List<? extends CaseTree> cases = translateStable(tree.getCases());
	if (selector!=tree.getExpression() || !cases.equals(tree.getCases())) {
	    SwitchTree n = make.Switch(selector, cases);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final CaseTree rewriteChildren(CaseTree tree) {
	ExpressionTree pat = (ExpressionTree)translate(tree.getExpression());
	List<? extends StatementTree> stats = translate(tree.getStatements());
	if (pat!=tree.getExpression() || !stats.equals(tree.getStatements())) {
            if (stats != tree.getStatements())
                stats = optimize(stats);
	    CaseTree n = make.Case(pat, stats);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final SynchronizedTree rewriteChildren(SynchronizedTree tree) {
	ExpressionTree lock = (ExpressionTree)translate(tree.getExpression());
	BlockTree body = (BlockTree)translate(tree.getBlock());
	if (lock!=tree.getExpression() || body!=tree.getBlock()) {
	    SynchronizedTree n = make.Synchronized(lock, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }

    protected final TryTree rewriteChildren(TryTree tree) {
	BlockTree body = (BlockTree)translate(tree.getBlock());
	List<? extends CatchTree> catches = translateStable(tree.getCatches());
	BlockTree finalizer = (BlockTree)translate(tree.getFinallyBlock());
	if (body!=tree.getBlock() || !catches.equals(tree.getCatches()) || 
            finalizer!=tree.getFinallyBlock()) {
	    TryTree n = make.Try(body, catches, finalizer);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final CatchTree rewriteChildren(CatchTree tree) {
	VariableTree param = translateStable(tree.getParameter());
	BlockTree body = (BlockTree)translate(tree.getBlock());
	if (param!=tree.getParameter() || body!=tree.getBlock()) {
	    CatchTree n = make.Catch(param, body);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ConditionalExpressionTree rewriteChildren(ConditionalExpressionTree tree) {
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	ExpressionTree truepart = (ExpressionTree)translate(tree.getTrueExpression());
	ExpressionTree falsepart = (ExpressionTree)translate(tree.getFalseExpression());
	if (cond!=tree.getCondition() || truepart!=tree.getTrueExpression() || 
            falsepart!=tree.getFalseExpression()) {
	    ConditionalExpressionTree n = make.ConditionalExpression(cond, truepart, falsepart);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final IfTree rewriteChildren(IfTree tree) {
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	StatementTree thenpart = (StatementTree)translate(tree.getThenStatement());
	StatementTree elsepart = (StatementTree)translate(tree.getElseStatement());
	if (cond!=tree.getCondition() || thenpart!=tree.getThenStatement() || 
            elsepart!=tree.getElseStatement()) {
	    IfTree n = make.If(cond, thenpart, elsepart);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ExpressionStatementTree rewriteChildren(ExpressionStatementTree tree) {
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	if (expr!=tree.getExpression()) {
	    ExpressionStatementTree n = make.ExpressionStatement(expr);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final BreakTree rewriteChildren(BreakTree tree) {
	return tree;
    }
    
    protected final ContinueTree rewriteChildren(ContinueTree tree) {
	return tree;
    }
    
    protected final ReturnTree rewriteChildren(ReturnTree tree) {
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	if (expr!=tree.getExpression()) {
	    ReturnTree n = make.Return(expr);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ThrowTree rewriteChildren(ThrowTree tree) {
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	if (expr!=tree.getExpression()) {
	    ThrowTree n = make.Throw(expr);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final AssertTree rewriteChildren(AssertTree tree) {
	ExpressionTree cond = (ExpressionTree)translate(tree.getCondition());
	ExpressionTree detail = (ExpressionTree)translate(tree.getDetail());
	if (cond!=tree.getCondition() || detail!=tree.getDetail()) {
	    AssertTree n = make.Assert(cond, detail);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final MethodInvocationTree rewriteChildren(MethodInvocationTree tree) {
        List<? extends ExpressionTree> typeargs = 
            (List<? extends ExpressionTree>)translate(tree.getTypeArguments());
	ExpressionTree meth = (ExpressionTree)translate(tree.getMethodSelect());
	List<? extends ExpressionTree> args = translate(tree.getArguments());
	if (!typeargs.equals(tree.getTypeArguments()) || meth!=tree.getMethodSelect() || 
                !args.equals(tree.getArguments())) {
            if (args != tree.getArguments())
                args = optimize(args);
            if (typeargs != tree.getTypeArguments())
                typeargs = optimize(typeargs);
	    MethodInvocationTree n = make.MethodInvocation(typeargs, meth, args);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
	    tree = n;
            if (size(tree.getTypeArguments()) != size(typeargs) && 
                    size(tree.getArguments()) != size(args))
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	}
	return tree;
    }
    
    protected final NewClassTree rewriteChildren(NewClassTree tree) {
	ExpressionTree encl = (ExpressionTree)translate(tree.getEnclosingExpression());
        List<? extends ExpressionTree> typeargs = 
            (List<? extends ExpressionTree>)translate(tree.getTypeArguments());
	ExpressionTree clazz = translateClassRef(tree.getIdentifier());
	List<? extends ExpressionTree> args = translate(tree.getArguments());
	ClassTree def = translateStable(tree.getClassBody());
	if (encl!=tree.getEnclosingExpression() || 
                !typeargs.equals(tree.getTypeArguments()) || clazz!=tree.getIdentifier() || 
                !args.equals(tree.getArguments()) || def!=tree.getClassBody()) {
            if (args != tree.getArguments())
                args = optimize(args);
            if (typeargs != tree.getTypeArguments())
                typeargs = optimize(typeargs);
	    NewClassTree n = make.NewClass(encl, typeargs, clazz, args, def);
            model.setElement(n, model.getElement(tree));
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            if ((size(tree.getTypeArguments()) != size(typeargs) && 
                    size(tree.getArguments()) != size(args)) ||
                    def != tree.getClassBody())
                model.setPos(n, NOPOS);
            else
                copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final NewArrayTree rewriteChildren(NewArrayTree tree) {
	ExpressionTree elemtype = (ExpressionTree)translateClassRef(tree.getType());
	List<? extends ExpressionTree> dims = translate(tree.getDimensions());
	List<? extends ExpressionTree> elems = translate(tree.getInitializers());
	if (elemtype!=tree.getType() || !safeEquals(dims, tree.getDimensions()) || 
                !safeEquals(elems, tree.getInitializers())) {
            if (elems != tree.getInitializers())
                elems = optimize(elems);
            if (dims != tree.getDimensions())
                dims = optimize(dims);
	    NewArrayTree n = make.NewArray(elemtype, dims, elems);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
	    tree = n;
            if (size(tree.getDimensions()) != size(dims) || 
                    size(tree.getInitializers()) != size(elems))
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	}
	return tree;
    }
    
    protected final ParenthesizedTree rewriteChildren(ParenthesizedTree tree) {
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	if (expr!=tree.getExpression()) {
	    ParenthesizedTree n = make.Parenthesized(expr);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final AssignmentTree rewriteChildren(AssignmentTree tree) {
	ExpressionTree lhs = (ExpressionTree)translate(tree.getVariable());
	ExpressionTree rhs = (ExpressionTree)translate(tree.getExpression());
	if (lhs!=tree.getVariable() || rhs!=tree.getExpression()) {
	    AssignmentTree n = make.Assignment(lhs, rhs);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final CompoundAssignmentTree rewriteChildren(CompoundAssignmentTree tree) {
	ExpressionTree lhs = (ExpressionTree)translate(tree.getVariable());
	ExpressionTree rhs = (ExpressionTree)translate(tree.getExpression());
	if (lhs!=tree.getVariable() || rhs!=tree.getExpression()) {
	    CompoundAssignmentTree n = make.CompoundAssignment(tree.getKind(), lhs, rhs);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final UnaryTree rewriteChildren(UnaryTree tree) {
	ExpressionTree arg = (ExpressionTree)translate(tree.getExpression());
	if (arg!=tree.getExpression()) {
	    UnaryTree n = make.Unary(tree.getKind(), arg);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final BinaryTree rewriteChildren(BinaryTree tree) {
	ExpressionTree lhs = (ExpressionTree)translate(tree.getLeftOperand());
	ExpressionTree rhs = (ExpressionTree)translate(tree.getRightOperand());
	if (lhs!=tree.getLeftOperand() || rhs!=tree.getRightOperand()) {
	    BinaryTree n = make.Binary(tree.getKind(), lhs, rhs);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final TypeCastTree rewriteChildren(TypeCastTree tree) {
	Tree clazz = translateClassRef(tree.getType());
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	if (clazz!=tree.getType() || expr!=tree.getExpression()) {
	    TypeCastTree n = make.TypeCast(clazz, expr);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final InstanceOfTree rewriteChildren(InstanceOfTree tree) {
	ExpressionTree expr = (ExpressionTree)translate(tree.getExpression());
	Tree clazz = translateClassRef(tree.getType());
	if (expr!=tree.getExpression() || clazz!=tree.getType()) {
	    InstanceOfTree n = make.InstanceOf(expr, clazz);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ArrayAccessTree rewriteChildren(ArrayAccessTree tree) {
	ExpressionTree array = (ExpressionTree)translate(tree.getExpression());
	ExpressionTree index = (ExpressionTree)translate(tree.getIndex());
	if (array!=tree.getExpression() || index!=tree.getIndex()) {
	    ArrayAccessTree n = make.ArrayAccess(array, index);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final MemberSelectTree rewriteChildren(MemberSelectTree tree) {
	ExpressionTree selected = translateClassRef(tree.getExpression());
	if (selected!=tree.getExpression()) {
	    MemberSelectTree n = make.MemberSelect(selected, tree.getIdentifier());
            model.setElement(n, model.getElement(tree));
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final IdentifierTree rewriteChildren(IdentifierTree tree) {
	return tree;
    }
    
    protected final LiteralTree rewriteChildren(LiteralTree tree) {
	return tree;
    }
    
    protected final PrimitiveTypeTree rewriteChildren(PrimitiveTypeTree tree) {
	return tree;
    }
    
    protected final ArrayTypeTree rewriteChildren(ArrayTypeTree tree) {
	ExpressionTree elemtype = (ExpressionTree)translateClassRef(tree.getType());
	if (elemtype!=tree.getType()) {
	    ArrayTypeTree n = make.ArrayType(elemtype);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ParameterizedTypeTree rewriteChildren(ParameterizedTypeTree tree) {
	ExpressionTree clazz = (ExpressionTree)translateClassRef(tree.getType());
	List<? extends ExpressionTree> arguments = 
                (List<? extends ExpressionTree>)translate(tree.getTypeArguments());
	if (clazz!=tree.getType() || !arguments.equals(tree.getTypeArguments())) {
            if (arguments != tree.getTypeArguments())
                arguments = optimize(arguments);
	    ParameterizedTypeTree n = make.ParameterizedType(clazz, arguments);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
	    tree = n;
            if (tree.getTypeArguments().size() != arguments.size())
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	}
	return tree;
    }
    
    protected final TypeParameterTree rewriteChildren(TypeParameterTree tree) {
	List<? extends ExpressionTree> bounds = 
                (List<? extends ExpressionTree>)translate(tree.getBounds());
	if (!bounds.equals(tree.getBounds())) {
            bounds = optimize(bounds);
	    TypeParameterTree n = make.TypeParameter(tree.getName(), bounds);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
	    tree = n;
            if (tree.getBounds().size() != bounds.size())
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	}
	return tree;
    }
    
    protected final WildcardTree rewriteChildren(WildcardTree tree) {
        Tree type = translateClassRef(tree.getBound());
	if (type != tree.getBound()) {
	    WildcardTree n = make.Wildcard(tree.getKind(), type);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
        return tree;
    }
    
    protected final AnnotationTree rewriteChildren(AnnotationTree tree) {
        Tree annotationType = translate(tree.getAnnotationType());
	List<? extends ExpressionTree> args = translate(tree.getArguments());
	if (annotationType!=tree.getAnnotationType() || !args.equals(tree.getArguments())) {
            if (args != tree.getArguments())
                args = optimize(args);
	    AnnotationTree n = make.Annotation(annotationType, args);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
	    tree = n;
            if (tree.getArguments().size() != args.size())
                model.setPos(tree, NOPOS);
            else
                copyPosTo(tree,n);
	}
	return tree;
    }
    
    protected final ModifiersTree rewriteChildren(ModifiersTree tree) {
	List<? extends AnnotationTree> annotations = translateStable(tree.getAnnotations());
	if (!annotations.equals(tree.getAnnotations())) {
	    ModifiersTree n = make.Modifiers(tree, annotations);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }
    
    protected final ErroneousTree rewriteChildren(ErroneousTree tree) {
        List<? extends Tree> oldErrs = tree.getErrorTrees();
	List<? extends Tree> newErrs = translate(oldErrs);
	if (!newErrs.equals(oldErrs)) {
	    ErroneousTree n = make.Erroneous(newErrs);
            model.setType(n, model.getType(tree));
	    copyCommentTo(tree,n);
            copyPosTo(tree,n);
	    tree = n;
	}
	return tree;
    }


}
