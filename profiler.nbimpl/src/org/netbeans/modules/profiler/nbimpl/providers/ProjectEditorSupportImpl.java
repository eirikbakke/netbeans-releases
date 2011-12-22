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
package org.netbeans.modules.profiler.nbimpl.providers;

import com.sun.source.tree.Scope;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.profiler.api.EditorContext;
import org.netbeans.modules.profiler.spi.EditorSupportProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.UserQuestionException;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Jaroslav Bachorik
 * @author Tomas Hurka
 */
@ServiceProvider(service = EditorSupportProvider.class)
public class ProjectEditorSupportImpl extends EditorSupportProvider {

    private <T> T performOnAWT(final Callable<T> action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            return action.call();
        } else {
            final T[] rslt = (T[]) new Object[]{null};
            final Exception[] exc = new Exception[1];
            final CountDownLatch latch = new CountDownLatch(1);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        rslt[0] = action.call();
                    } catch (Exception ex) {
                        exc[0] = ex;
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if (exc[0] != null) {
                throw exc[0];
            }
            return rslt[0];
        }
    }
    
    @Override
    public boolean currentlyInJavaEditor() {
        // Get focused TopComponent
        TopComponent top1 = WindowManager.getDefault().getRegistry().getActivated();

        if (top1 == null) {
            return false;
        }

        // Get most active editor
        JTextComponent editor = EditorRegistry.lastFocusedComponent();

        if (editor == null) {
            return false;
        }

        // Check if Java source
        Document document = editor.getDocument();

        if (document == null) {
            return false;
        }

        FileObject fileObject = NbEditorUtilities.getFileObject(document);

        if ((fileObject == null) || !fileObject.getExt().equalsIgnoreCase("java")) { // NOI18N
            return false;
        }

        // Get editor TopComponent
        TopComponent top2 = NbEditorUtilities.getOuterTopComponent(editor);

        if (top2 == null) {
            return false;
        }

        // Return whether focused TopComponent == editor TopComponent
        return top1 == top2;
    }
    
    @Override
    public EditorContext getMostActiveJavaEditorContext() {
        for (JTextComponent component : EditorRegistry.componentList()) {
            Document document = component.getDocument();
            FileObject fileObject = NbEditorUtilities.getFileObject(document);

            if ((fileObject != null) && fileObject.getExt().equalsIgnoreCase("java")) { // NOI18N
                return new EditorContext(component, document, fileObject);
            }
        }

        return null;
    }

    @Override
    public FileObject getCurrentFile() {
        try {
            return performOnAWT(new Callable<FileObject>() {

                @Override
                public FileObject call() throws Exception {
                    TopComponent tc = TopComponent.getRegistry().getActivated();

                    if (tc != null) {
                        return tc.getLookup().lookup(FileObject.class);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    @Override
    public int getCurrentOffset() {
        try {
            return performOnAWT(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {
                    JTextComponent mostActiveEditor = EditorRegistry.lastFocusedComponent();

                    if ((mostActiveEditor != null) && (mostActiveEditor.getCaret() != null)) {
                        return mostActiveEditor.getCaretPosition();
                    }

                    return -1;
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public int getLineForOffset(final FileObject file, final int offset) {
        try {
            if (offset == -1) {
                return -1;
            }

            DataObject dobj = DataObject.find(file);

            if (dobj == null) {
                return -1;
            }
            final EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);

            if (ec == null) {
                return -1;
            }
            return performOnAWT(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {
                    StyledDocument doc = getDocument(ec);
                    if (doc == null) {
                        return -1;
                    }
                    return NbDocument.findLineNumber(doc, offset);
                }

            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return -1;
    }
    
    @Override
    public int getOffsetForLine(final FileObject file, final int line) {
        try {
            if (line == -1) {
                return -1;
            }

            DataObject dobj = DataObject.find(file);

            if (dobj == null) {
                return -1;
            }
            final EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);

            if (ec == null) {
                return -1;
            }
            return performOnAWT(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {
                    StyledDocument doc = getDocument(ec);
                    if (doc == null) {
                        return -1;
                    }
                    return NbDocument.findLineOffset(doc, line);
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return -1;
    }

    @Override
    public boolean isOffsetValid(final FileObject file, final int offset) {
        try {
            return performOnAWT(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    if (file == null) {
                        return false;
                    }

                    return validateOffset(file, offset) != -1;
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }

        return false;
    }

    @Override
    public Lookup.Provider getCurrentProject() {
        try {
            return performOnAWT(new Callable<Lookup.Provider>() {

                @Override
                public Lookup.Provider call() throws Exception {
                    TopComponent tc = TopComponent.getRegistry().getActivated();

                    if (tc != null) {
                        return tc.getLookup().lookup(Project.class);
                    }

                    return null;
                }
            });
        } catch (Exception e)  {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    @Override
    public int[] getSelectionOffsets() {
        try {
            return performOnAWT(new Callable<int[]>() {

                @Override
                public int[] call() throws Exception {
                    int[] indexes = new int[]{-1, -1};
                    TopComponent tc = TopComponent.getRegistry().getActivated();

                    if (tc != null) {
                        EditorCookie ec = tc.getLookup().lookup(EditorCookie.class);

                        if (ec != null) {
                            for (JEditorPane pane : ec.getOpenedPanes()) {
                                int selStart = pane.getSelectionStart();

                                if (selStart > -1) {
                                    indexes[0] = selStart;
                                    indexes[1] = pane.getSelectionEnd();

                                    break;
                                }
                            }
                        }
                    }

                    return indexes;
                }
            });
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        
        return new int[]{-1, -1};
    }

    private static int validateOffset(FileObject editorDoc, final int toValidate) {
        final int[] validated = new int[]{-1};

        JavaSource js = JavaSource.forFileObject(editorDoc);

        if (js != null) {
            try {
                js.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller)
                            throws Exception {
                        controller.toPhase(JavaSource.Phase.RESOLVED);
                        validated[0] = -1; // non-validated default

                        Scope sc = controller.getTreeUtilities().scopeFor(toValidate);

                        if (sc.getEnclosingClass() != null) {
                            validated[0] = toValidate;
                        }

                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        return validated[0];
    }

    @NbBundle.Messages("TXT_Question=Question")
    private static StyledDocument getDocument(EditorCookie ec) throws IOException {
        StyledDocument doc = null;
        try {
            doc = ec.openDocument();
        } catch (UserQuestionException uqe) {
            final Object value = DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Confirmation(uqe.getLocalizedMessage(),
                    Bundle.TXT_Question(),
                    NotifyDescriptor.YES_NO_OPTION));
            if (value != NotifyDescriptor.YES_OPTION) {
                return null;
            }
            uqe.confirmed();
            doc = ec.openDocument();
        }
        return doc;

    }
}
