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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.openide.text;

import java.awt.Color;
import java.awt.Component;


import javax.swing.JEditorPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import javax.swing.undo.UndoableEdit;
import org.netbeans.modules.openide.text.NbDocumentRefactoringHack;
import org.openide.awt.UndoRedo;
import org.openide.cookies.EditorCookie;


/** Dummy class holding utility methods for working with NetBeans document conventions.
*
* @author Jaroslav Tulach
*/
public final class NbDocument extends Object {
    /** Attribute that signals that a given character is guarded (cannot
    * be modified). Implements {@link javax.swing.text.AttributeSet.CharacterAttribute} to signal that
    * this attribute applies to characters, not paragraphs.
    */
    public static final Object GUARDED = new AttributeSet.CharacterAttribute() {
        };

    /** Attribute set that adds to a part of document guarded flag
    */
    private static final SimpleAttributeSet ATTR_ADD = new SimpleAttributeSet();

    /** Attribute set to remove the guarded flag.
    */
    private static final SimpleAttributeSet ATTR_REMOVE = new SimpleAttributeSet();

    static {
        ATTR_ADD.addAttribute(GUARDED, Boolean.TRUE);
        ATTR_REMOVE.addAttribute(GUARDED, Boolean.FALSE);
    }

    /** Name of style attached to documents to mark a paragraph (line)
    * as a (debugger) breakpoint.
    * @deprecated since 6.29. Use {@link Annotation} instead.
    */
    @Deprecated
    public static final String BREAKPOINT_STYLE_NAME = "NbBreakpointStyle"; // NOI18N

    /** Name of style attached to documents to mark a paragraph (line)
    * as erroneous.
    * @deprecated since 6.29. Use {@link Annotation} instead.
    */
    @Deprecated
    public static final String ERROR_STYLE_NAME = "NbErrorStyle"; // NOI18N

    /** Name of style attached to documents to mark a paragraph (line)
    * as current (in a debugger).
    * @deprecated since 6.29. Use {@link Annotation} instead.
    */
    @Deprecated
    public static final String CURRENT_STYLE_NAME = "NbCurrentStyle"; // NOI18N

    /** Name of style attached to documents to unmark a paragraph (line)
    * as anything special.
    * @deprecated since 6.29. Use {@link Annotation} instead.
    */
    @Deprecated
    public static final String NORMAL_STYLE_NAME = "NbNormalStyle"; // NOI18N
    
    private NbDocument() {
    }

    /** Find the root element of all lines.
    * All conforming NetBeans documents
    * should return a valid element.
    *
    * @param doc styled document (expecting NetBeans document)
    * @return the root element
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static Element findLineRootElement(StyledDocument doc) {
        checkDocParameter(doc);

        Element e = doc.getParagraphElement(0).getParentElement();

        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement();
        }

        return e;
    }

    /** For given document and an offset, find the line number.
    * @param doc the document
    * @param offset offset in the document
    * @return the line number for that offset
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static int findLineNumber(StyledDocument doc, int offset) {
        /* pre-33165
        Element paragraphsParent = findLineRootElement (doc);
        return paragraphsParent.getElementIndex (offset);
         */
        return new DocumentRenderer(DocumentRenderer.FIND_LINE_NUMBER, doc, offset).renderToInt();
    }

    /** Finds column number given an offset.
    * @param doc the document
    * @param offset offset in the document
    * @return column within the line of that offset (counting starts from zero)
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static int findLineColumn(StyledDocument doc, int offset) {
        /*
        Element paragraphsParent = findLineRootElement (doc);
        int indx = paragraphsParent.getElementIndex (offset);
        return offset - paragraphsParent.getElement (indx).getStartOffset ();
         */
        return new DocumentRenderer(DocumentRenderer.FIND_LINE_COLUMN, doc, offset).renderToInt();
    }

    /** Finds offset of the beginning of a line.
    * @param doc the document
    * @param lineNumber number of the line to find the start of (zero-based)
    * @return offset
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
     * @exception IndexOutOfBoundsException when incorrect
     * <code>lineNumber</code> value is inserted
    */
    public static int findLineOffset(StyledDocument doc, int lineNumber) {
        /*
        Element paragraphsParent = findLineRootElement (doc);
        Element line = paragraphsParent.getElement (lineNumber);

        if(line == null) {
            throw new IndexOutOfBoundsException(
                "Index=" + lineNumber + " is out of bounds."); // NOI18N
        }

        return line.getStartOffset ();
         */
        return new DocumentRenderer(DocumentRenderer.FIND_LINE_OFFSET, doc, lineNumber).renderToInt();
    }

    /** Creates position with a bias. If the bias is {@link javax.swing.text.Position.Bias#Backward}
    * then if an insert occures at the position, the text is inserted
    * after the position. If the bias is {@link javax.swing.text.Position.Bias#Forward <code>Forward</code>}, then the text is
    * inserted before the position.
    * <P>
    * The method checks if the document implements {@link PositionBiasable},
    * and if so, {@link PositionBiasable#createPosition <code>createPosition</code>} is called.
    * Otherwise an attempt is made to provide a <code>Position</code> with the correct behavior.
    *
    * @param doc document to create position in
    * @param offset the current offset for the position
    * @param bias the bias to use for the position
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    * @exception BadLocationException if the offset is invalid
    */
    public static Position createPosition(Document doc, int offset, Position.Bias bias)
    throws BadLocationException {
        checkDocParameter(doc);

        if (doc instanceof PositionBiasable) {
            return ((PositionBiasable) doc).createPosition(offset, bias);
        } else {
            if (bias == Position.Bias.Forward) {
                // default behaviour
                return doc.createPosition(offset);
            } else {
                // use our special position
                return BackwardPosition.create(doc, offset);
            }
        }
    }

    /** Mark part of a document as guarded (immutable to the user).
    * @param doc styled document
    * @param offset offset to start at
    * @param len length of text to mark as guarded
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static void markGuarded(StyledDocument doc, int offset, int len) {
        checkDocParameter(doc);
        doc.setCharacterAttributes(offset, len, ATTR_ADD, false);
    }

    /** Remove guarded mark on a block of a document.
    * @param doc styled document
    * @param offset offset to start at
    * @param len length of text to mark as unguarded
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static void unmarkGuarded(StyledDocument doc, int offset, int len) {
        checkDocParameter(doc);
        doc.setCharacterAttributes(offset, len, ATTR_REMOVE, false);
    }

    /** Inserts a text into given offset and marks it guarded.
    * @param doc document to insert to
    * @param offset offset of insertion
    * @param txt string text to insert
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static void insertGuarded(StyledDocument doc, int offset, String txt)
    throws BadLocationException {
        checkDocParameter(doc);
        doc.insertString(offset, txt, ATTR_ADD);
    }

    /** Attach a breakpoint to a line in the document.
    * If the document has a defined style named {@link #BREAKPOINT_STYLE_NAME}, it is used.
    * Otherwise, a new style is defined.
    *
    * @param doc the document
    * @param offset identifies the line to set breakpoint to
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    *
    * @deprecated since 1.20. Use addAnnotation() instead
    */
    @Deprecated
    public static void markBreakpoint(StyledDocument doc, int offset) {
        checkDocParameter(doc);

        Style bp = doc.getStyle(BREAKPOINT_STYLE_NAME);

        if (bp == null) {
            // create the style
            bp = doc.addStyle(BREAKPOINT_STYLE_NAME, null);

            if (bp == null) {
                return;
            }

            bp.addAttribute(StyleConstants.ColorConstants.Background, Color.red);
            bp.addAttribute(StyleConstants.ColorConstants.Foreground, Color.white);
        }

        doc.setLogicalStyle(offset, bp);
    }

    /** Mark a line as erroneous (e.g.&nbsp;by the compiler).
    * If the document has a defined style named {@link #ERROR_STYLE_NAME}, it is used.
    * Otherwise, a new style is defined.
    *
    * @param doc the document
    * @param offset identifies the line to mark
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    *
    * @deprecated since 1.20. Use addAnnotation() instead
    */
    @Deprecated
    public static void markError(StyledDocument doc, int offset) {
        checkDocParameter(doc);

        Style bp = doc.getStyle(ERROR_STYLE_NAME);

        if (bp == null) {
            // create the style
            bp = doc.addStyle(ERROR_STYLE_NAME, null);

            if (bp == null) {
                return;
            }

            bp.addAttribute(StyleConstants.ColorConstants.Background, Color.green);
            bp.addAttribute(StyleConstants.ColorConstants.Foreground, Color.black);
        }

        doc.setLogicalStyle(offset, bp);
    }

    /** Marks a line as current (e.g.&nbsp;for the debugger).
    * If the document has a defined style named {@link #CURRENT_STYLE_NAME}, it is used.
    * Otherwise, a new style is defined.
    *
    * @param doc the document
    * @param offset identifies the line to mark
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    *
    * @deprecated since 1.20. Use addAnnotation() instead
    */
    @Deprecated
    public static void markCurrent(StyledDocument doc, int offset) {
        checkDocParameter(doc);

        Style bp = doc.getStyle(CURRENT_STYLE_NAME);

        if (bp == null) {
            // create the style
            bp = doc.addStyle(CURRENT_STYLE_NAME, null);

            if (bp == null) {
                return;
            }

            bp.addAttribute(StyleConstants.ColorConstants.Background, Color.blue);
            bp.addAttribute(StyleConstants.ColorConstants.Foreground, Color.white);
        }

        doc.setLogicalStyle(offset, bp);
    }

    /**
     * Mark a line as normal (no special attributes).
     * This uses the dummy style named {@link #NORMAL_STYLE_NAME}.
     * This method should be used to undo the effect of {@link #markBreakpoint}, {@link #markError} and {@link #markCurrent}.
     * @param doc the document
     * @param offset identified the line to unmark
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
     *
     * @deprecated since 1.20. Use addAnnotation() instead
     */
    @Deprecated
    public static void markNormal(StyledDocument doc, int offset) {
        checkDocParameter(doc);

        Style st = doc.getStyle(NORMAL_STYLE_NAME);

        if (st == null) {
            st = doc.addStyle(NORMAL_STYLE_NAME, null);
        }

        if (st != null) {
            doc.setLogicalStyle(offset, st);
        }
    }
    
    /**
     * Gets recently selected editor pane opened by editor cookie
     * Can be called from AWT event thread only.
     *
     * @param ec EditorCookie used to find out recently selected editor pane
     * @return pane or null
     *
     * @since 6.24
     *
     */
    public static JEditorPane findRecentEditorPane (EditorCookie ec) {
        // expected in AWT only
        assert SwingUtilities.isEventDispatchThread()
                : "NbDocument.findInitializedPaneForActiveTC must be called from AWT thread only"; // NOI18N
        if (ec instanceof CloneableEditorSupport) {
            // find if initialized or return null immediately
            JEditorPane pane = ((CloneableEditorSupport) ec).getRecentPane();
            return pane;
        } else {
            JEditorPane [] panes = ec.getOpenedPanes();
            if (panes != null) {
                return panes[0];
            }
            return null;
        }
    }
    
    /** Locks the document to have exclusive access to it.
     * Documents implementing {@link Lockable} can specify exactly how to do this.
    *
    * @param doc document to lock
    * @param run the action to run
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    */
    public static void runAtomic(StyledDocument doc, Runnable run) {
        checkDocParameter(doc);

        if (doc instanceof WriteLockable) {
            // use the method
            ((WriteLockable) doc).runAtomic(run);
        } else {
            // transfer the runnable to event dispatch thread
            synchronized (doc) {
                run.run();
            }
        }
    }

    /** Executes given runnable in "user mode" does not allowing any modifications
    * to parts of text marked as guarded. The actions should be run as "atomic" so
    * either happen all at once or none at all (if a guarded block should be modified).
    *
    * @param doc document to modify
    * @param run runnable to run in user mode that will have exclusive access to modify the document
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>.
    * @exception BadLocationException if a modification of guarded text occured
    *   and that is why no changes to the document has been done.
    */
    public static void runAtomicAsUser(StyledDocument doc, Runnable run)
    throws BadLocationException {
        checkDocParameter(doc);

        if (doc instanceof WriteLockable) {
            // use the method
            ((WriteLockable) doc).runAtomicAsUser(run);
        } else {
            // transfer the runnable to event dispatch thread
            synchronized (doc) {
                run.run();
            }
        }
    }

    /** Helper method for checking document parameter validity.
     * @exception NullPointerException If the <code>doc</code> parameter
     *                                 is <code>null</code>. */
    private static void checkDocParameter(Document doc) {
        if (doc == null) {
            throw new NullPointerException("Invalid doc parameter. Document may not be null!"); // NOI18N
        }
    }

    /** Find a way to print a given document.
     * If the document implements the correct interface(s) then the document is returned,
     * else a default {@link java.awt.print.Printable} is used as a wrapper. In this last case it is useful
     * to implement {@link NbDocument.Printable} to describe how to print in terms of
     * attributed characters, rather than specifying the complete page layout from scratch.
     *
     * @param doc the document to find printing support for
     * @return an object that is instance of eith {@link java.awt.print.Printable} or {@link java.awt.print.Pageable}
     */
    public static Object findPageable(StyledDocument doc) {
        if (doc instanceof java.awt.print.Pageable) {
            return doc;
        } else if (doc instanceof java.awt.print.Printable) {
            return doc;
        } else {
            return new DefaultPrintable(doc);
        }
    }

    /**
     * Add annotation to the document. For annotation of whole line
     * the length parameter can be ignored (specify value -1).
     * <br/>
     * Note: since 6.35 the requests (delegated to document) are no longer replanned to EDT.
     * @param doc the document which will be annotated
     * @param startPos position which represent begining
     * of the annotated text
     * @param length length of the annotated text. If -1 is specified
     * the whole line will be annotated
     * @param annotation annotation which is attached to this text
     * @since 1.20
     */
    public static void addAnnotation(
        final StyledDocument doc, final Position startPos, final int length, final Annotation annotation
    ) {
        if (!(doc instanceof Annotatable)) {
            return;
        }
        ((Annotatable) doc).addAnnotation(startPos, length, annotation);
    }

    /**
     * Removal of added annotation.
     * <br/>
     * Note: since 6.35 the requests (delegated to document) are no longer replanned to EDT.
     * @param doc the annotated document
     * @param annotation annotation which is going to be removed
     * @since 1.20
     */
    public static void removeAnnotation(final StyledDocument doc, final Annotation annotation) {
        if (!(doc instanceof Annotatable)) {
            return;
        }
        ((Annotatable) doc).removeAnnotation(annotation);
    }
    
    /**
     * TODO: will be removed after API review
     */
    
    static {
        NbDocumentRefactoringHack.APIAccessor.DEFAULT = new APIAccessorImpl();
    }
    
    private static class APIAccessorImpl extends NbDocumentRefactoringHack.APIAccessor {

        @Override
        public <T> T getEditToBeUndoneOfType(EditorCookie ec, Class<T> type) {
            return NbDocument.getEditToBeUndoneOfType(ec, type);
        }

        @Override
        public <T> T getEditToBeRedoneOfType(EditorCookie ec, Class<T> type) {
            return NbDocument.getEditToBeRedoneOfType(ec, type);
        }
        
    }

    static <T> T getEditToBeUndoneOfType(EditorCookie ec, Class<T> type) {
        return getEditToBeUndoneRedoneOfType(ec, type, false);
    }
    
    static <T> T getEditToBeRedoneOfType(EditorCookie ec, Class<T> type) {
        return getEditToBeUndoneRedoneOfType(ec, type, true);
    }
    
    private static <T> T getEditToBeUndoneRedoneOfType(EditorCookie ec, Class<T> type, boolean redone) {
        UndoRedo ur;
        if (ec instanceof CloneableEditorSupport &&
                ((ur = ((CloneableEditorSupport)ec).getUndoRedo()) instanceof UndoRedoManager))
        {
            UndoRedoManager urManager = (UndoRedoManager) ur;
            UndoableEdit edit = urManager.editToBeUndoneRedone(redone);
            if (type.isInstance(edit)) {
                @SuppressWarnings("unchecked") T inst = (T) edit;
                return inst;
            }
        }
        return null;
    }
    
    //End of TODO

    /** Specialized version of document that knows how to lock the document
    * for complex modifications.
    */
    public interface WriteLockable extends Document {
        /** Executes given runnable in lock mode of the document.
        * In this mode, all redrawing is stopped and the caller has exclusive
        * access to all modifications to the document.
        * <P>
        * By definition there should be only one locker at a time. Sample implementation, if you are extending {@link AbstractDocument}:
        *
        * <p><code>
        * writeLock();<br>
        * try {<br>
        * &nbsp;&nbsp;r.run();<br>
        * } finally {<br>
        * &nbsp;&nbsp;writeUnlock();<br>
        * }
        * </code>
        *
        * @param r runnable to run while locked
        *
        * @see NbDocument#runAtomic
        */
        public void runAtomic(Runnable r);

        /** Executes given runnable in "user mode" does not allowing any modifications
        * to parts of text marked as guarded. The actions should be run as "atomic" so
        * either happen all at once or none at all (if a guarded block should be modified).
        *
        * @param r runnable to run in user mode that will have exclusive access to modify the document
        * @exception BadLocationException if a modification of guarded text occured
        *   and that is why no changes to the document has been done.
        */
        public void runAtomicAsUser(Runnable r) throws BadLocationException;
    }

    /** Document which may support styled text printing.
    * Any document that wishes to support special formatting while printing
    * can implement this interface and provide a <code>AttributedCharacterIterator</code>
    * specifying colors, fonts, etc.
    */
    public interface Printable extends Document {
        /** Get an attributed character iterator for the document, so that it may be printed.
         * <p>For a convenient way to do this, you may use {@link AttributedCharacters#iterator a simple implementation}
         * of an
         * attributed character list.
        * @return list of <code>AttributedCharacterIterator</code>s to be used for printing
        *
        * @see NbDocument#findPageable */
        public java.text.AttributedCharacterIterator[] createPrintIterators();
    }

    /** Enhanced version of document that provides better support for
    * holding and working with biased positions. It adds one new method
    * {@link #createPosition} that creates
    * a position that moves either to the left or to the right when an insertion
    * is performed at it.
    * <P>
    * If a document implements this interface, the new method is
    * used in {@link NbDocument#createPosition}.
    * If not, special support for the position is created.
    */
    public interface PositionBiasable extends Document {
        /** Creates position with a bias. If the bias is {@link javax.swing.text.Position.Bias#Backward}
        * then if an insert occures at the position, the text is inserted
        * after the position. If the bias is {@link javax.swing.text.Position.Bias#Forward <code>Forward</code>}, then the text is
        * inserted before the position.
        *
        * @param offset the offset for the position
        * @param bias the bias to use for the position
        * @exception BadLocationException if the offset is invalid
        *
        * @see NbDocument#createPosition
        */
        public Position createPosition(int offset, Position.Bias bias)
        throws BadLocationException;
    }

    /** Enabled documents to add special UI components to their Editor pane.
    * If this interface is implemented by the Editor document, it can be used
    * to add other components (such as toolbars) to the pane.
    */
    public interface CustomEditor extends Document {
        /** Create a whole editor component over the given <code>JEditorPane</code>.
         * The implementation should generally add some kind of scrolling
         * support to the given <code>JEditorPane</code> (typically with scrollbars),
         * possibly some other components
         * according to the desired layout,
         * and return the resulting container.
         * @param j editor pane over which the resulting component
         *   will be built
         * @return component encapsulating the pane and all other
         *   custom UI components
         */
        public Component createEditor(JEditorPane j);
    }

    /**
     * Enabled documents to add special UI toolbar components to their Editor pane.
     */
    public interface CustomToolbar extends Document {
        /**
         * Implementation shall return a toolbar for the document. Preferably non-floatable.
         */
        public JToolBar createToolbar(JEditorPane j);
    }

    /** Enhanced version of document which is capable of
     * attaching/detaching of annotations. It is guaranteed that
     * annotations are added/removed to document only in AWT thread.
     * @since 1.20
     */
    public interface Annotatable extends Document {
        /** Add annotation to the document. For annotation of whole line
         * the length parameter can be ignored (specify value -1).
         * @param startPos position which represent begining
         * of the annotated text
         * @param length length of the annotated text. If -1 is specified
         * the whole line will be annotated
         * @param annotation annotation which is attached to this text */
        public void addAnnotation(Position startPos, int length, Annotation annotation);

        /** Removal of added annotation.
         * @param annotation annotation which is going to be removed */
        public void removeAnnotation(Annotation annotation);
    }
    
    private static final class DocumentRenderer implements Runnable {
        private static final int FIND_LINE_NUMBER = 0;
        private static final int FIND_LINE_COLUMN = 1;
        private static final int FIND_LINE_OFFSET = 2;
        private StyledDocument doc;
        private int opCode;
        private int argInt;
        private int retInt;

        DocumentRenderer(int opCode, StyledDocument doc, int argInt) {
            this.opCode = opCode;
            this.doc = doc;
            this.argInt = argInt;
        }

        int renderToInt() {
            if (doc != null) {
                doc.render(this);
            }

            return retInt;
        }

        public void run() {
            switch (opCode) {
            case FIND_LINE_NUMBER: {
                Element paragraphsParent = findLineRootElement(doc);
                retInt = paragraphsParent.getElementIndex(argInt); // argInt is offset

                break;
            }

            case FIND_LINE_COLUMN: {
                Element paragraphsParent = findLineRootElement(doc);
                int indx = paragraphsParent.getElementIndex(argInt); // argInt is offset
                retInt = argInt - paragraphsParent.getElement(indx).getStartOffset();

                break;
            }

            case FIND_LINE_OFFSET: {
                Element paragraphsParent = findLineRootElement(doc);
                Element line = paragraphsParent.getElement(argInt); // argInt is lineNumber

                if (line == null) {
                    throw new IndexOutOfBoundsException("Index=" + argInt + " is out of bounds."); // NOI18N
                }

                retInt = line.getStartOffset();

                break;
            }

            default:
                throw new IllegalStateException();
            }
        }
    }
}
