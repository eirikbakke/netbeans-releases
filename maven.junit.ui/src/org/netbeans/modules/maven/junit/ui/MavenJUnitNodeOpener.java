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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.maven.junit.ui;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementFilter;
import javax.swing.Action;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.extexecution.print.LineConvertors.FileLocator;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.gsf.testrunner.api.CommonUtils;
import org.netbeans.modules.gsf.testrunner.ui.api.TestMethodNode;
import org.netbeans.modules.gsf.testrunner.ui.api.TestsuiteNode;
import org.netbeans.modules.junit.ui.api.JUnitTestMethodNode;
import org.netbeans.modules.java.testrunner.ui.api.NodeOpener;
import org.netbeans.modules.java.testrunner.ui.api.UIJavaUtils;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Marian Petras
 */
@NodeOpener.Registration(projectType = CommonUtils.MAVEN_PROJECT_TYPE, testingFramework = CommonUtils.JUNIT_TF)
public final class MavenJUnitNodeOpener extends NodeOpener {

    private static final Logger LOG = Logger.getLogger(MavenJUnitNodeOpener.class.getName());

    static final Action[] NO_ACTIONS = new Action[0];

    public void openTestsuite(TestsuiteNode node) {
        Children childrens = node.getChildren();
        if (childrens != null) {
            Node child = childrens.getNodeAt(0);
            if ((child != null) && (child instanceof MavenJUnitTestMethodNode)) {
                final FileObject fo = ((MavenJUnitTestMethodNode) child).getTestcaseFileObject();
                if (fo != null) {
                    final long[] line = new long[]{0};
                    JavaSource javaSource = JavaSource.forFileObject(fo);
                    if (javaSource != null) {
                        try {
                            javaSource.runUserActionTask(new Task<CompilationController>() {
                                @Override
                                public void run(CompilationController compilationController) throws Exception {
                                    compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
                                    Trees trees = compilationController.getTrees();
                                    CompilationUnitTree compilationUnitTree = compilationController.getCompilationUnit();
                                    List<? extends Tree> typeDecls = compilationUnitTree.getTypeDecls();
                                    for (Tree tree : typeDecls) {
                                        Element element = trees.getElement(trees.getPath(compilationUnitTree, tree));
                                        if (element != null && element.getKind() == ElementKind.CLASS && element.getSimpleName().contentEquals(fo.getName())) {
                                            long pos = trees.getSourcePositions().getStartPosition(compilationUnitTree, tree);
                                            line[0] = compilationUnitTree.getLineMap().getLineNumber(pos);
                                            break;
                                        }
                                    }
                                }
                            }, true);
                        } catch (IOException ioe) {
                            ErrorManager.getDefault().notify(ioe);
                        }
                    }
                    UIJavaUtils.openFile(fo, (int) line[0]);
                }
            }
        }
    }

    public void openTestMethod(final TestMethodNode node) {
        if (!(node instanceof MavenJUnitTestMethodNode)) {
            return;
        }
        final FileObject fo = ((MavenJUnitTestMethodNode) node).getTestcaseFileObject();
        if (fo != null) {
            final FileObject[] fo2open = new FileObject[]{fo};
            final long[] line = new long[]{0};
            JavaSource javaSource = JavaSource.forFileObject(fo2open[0]);
            if (javaSource != null) {
                try {
                    javaSource.runUserActionTask(new Task<CompilationController>() {
                        @Override
                        public void run(CompilationController compilationController) throws Exception {
                            compilationController.toPhase(Phase.ELEMENTS_RESOLVED);
                            Trees trees = compilationController.getTrees();
                            CompilationUnitTree compilationUnitTree = compilationController.getCompilationUnit();
                            List<? extends Tree> typeDecls = compilationUnitTree.getTypeDecls();
                            for (Tree tree : typeDecls) {
                                Element element = trees.getElement(trees.getPath(compilationUnitTree, tree));
                                if (element != null && element.getKind() == ElementKind.CLASS && element.getSimpleName().contentEquals(fo2open[0].getName())) {
                                    List<? extends ExecutableElement> methodElements = ElementFilter.methodsIn(element.getEnclosedElements());
                                    for (Element child : methodElements) {
                                        if (child.getSimpleName().contentEquals(node.getTestcase().getName())) {
                                            long pos = trees.getSourcePositions().getStartPosition(compilationUnitTree, trees.getTree(child));
                                            line[0] = compilationUnitTree.getLineMap().getLineNumber(pos);
                                            break;
                                        }
                                    }
                                    // method not found in this FO, so try to find where this method belongs
                                    if (line[0] == 0) {
                                        UIJavaUtils.searchAllMethods(node, fo2open, line, compilationController, element);
                                    }
                                    break;
                                }
                            }
                        }
                    }, true);

                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify(ioe);
                }
            }
            UIJavaUtils.openFile(fo2open[0], (int) line[0]);
        }
    }

    public void openCallstackFrame(Node node, @NonNull String frameInfo) {
        if (!(node instanceof JUnitTestMethodNode)) {
            return;
        }
        JUnitTestMethodNode methodNode = (JUnitTestMethodNode) UIJavaUtils.getTestMethodNode(node);
        if (!(methodNode instanceof MavenJUnitTestMethodNode)) {
            return;
        }
        FileLocator locator = methodNode.getTestcase().getSession().getFileLocator();
        if (locator == null) {
            return;
        }
        FileObject testfo = ((MavenJUnitTestMethodNode) methodNode).getTestcaseFileObject();
        if (testfo == null) {
            //#221053 more logging
            StringBuilder stack = new StringBuilder();
            if (methodNode.getTestcase().getTrouble() != null) {
                String[] st = methodNode.getTestcase().getTrouble().getStackTrace();
                if (st != null) {
                    stack.append("\n");
                    for (String s : st) {
                        stack.append(s).append("\n");
                    }
                } else {
                    stack.append("<none>");
                }
            } else {
                stack.append("<none>");
            }
            LOG.log(Level.INFO, "#221053: unknown location: {0} classname:{1}, stacktrace:", new Object[]{methodNode.getTestcase().getLocation(), methodNode.getTestcase().getClassName(), stack});
        }
        final int[] lineNumStorage = new int[1];
        FileObject file = UIJavaUtils.getFile(frameInfo, lineNumStorage, locator);
        //lineNumStorage -1 means no regexp for stacktrace was matched.
        if (testfo != null && file == null && methodNode.getTestcase().getTrouble() != null && lineNumStorage[0] == -1) {
                //213935 we could not recognize the stack trace line and map it to known file
            //if it's a failure text, grab the testcase's own line from the stack.
            String[] st = methodNode.getTestcase().getTrouble().getStackTrace();
            if ((st != null) && (st.length > 0)) {
                int index = st.length - 1;
                    //213935 we need to find the testcase linenumber to jump to.
                // and ignore the infrastructure stack lines in the process
                while (!testfo.equals(file) && index != -1) {
                    file = UIJavaUtils.getFile(st[index], lineNumStorage, locator);
                    index = index - 1;
                }
            }
        }
        // Is this a @Test(expected = *Exception.class) test method that failed?
        if (file == null && lineNumStorage[0] == -1 && node instanceof MavenJUnitTestMethodNode) {
            openTestMethod((MavenJUnitTestMethodNode) node);
        }
        UIJavaUtils.openFile(file, lineNumStorage[0]);
    }

}
