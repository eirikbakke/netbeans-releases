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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.text.EnhancedChangeEvent;

import java.io.*;

import java.util.*;

import javax.swing.event.*;
import javax.swing.text.*;


/** Implementation of a line in a {@link StyledDocument}.
* One object
* of this class represents a line in the document by holding
* a {@link PositionRef}, which can represent a position in an open or
* closed document.
*
* @author Jaroslav Tulach, David Konecny
*/
public abstract class DocumentLine extends Line {
    /** weak map that assignes to editor supports whether they have current or error line
    * selected. (EditorSupport, DocumentLine[2]), where Line[0] is current and Line[1] is error */
    private static Map<CloneableEditorSupport,DocumentLine[]> assigned = new WeakHashMap<CloneableEditorSupport,DocumentLine[]>(5);
    static final long serialVersionUID = 3213776466939427487L;

    /** reference to one position on the line */
    protected PositionRef pos;

    /** is breakpoint there - presistent state
     @deprecated since 1.20 */
    @Deprecated
    private boolean breakpoint;

    /** error line  - transient state
     @deprecated since 1.20 */
    @Deprecated
    private transient boolean error;

    /** current line - transient state
     @deprecated since 1.20 */
    @Deprecated
    private transient boolean current;

    /** listener for changes of state of the document */
    private transient LR listener;

    /** weak document listener assigned to the document or null */
    private transient DocumentListener docL;

    /** List of Line.Part which exist for this line*/
    private List<Part> lineParts = new ArrayList<Part>(3);

    /** Constructor.
    * @param obj context we belong to
    * @param pos position on the line
     *
     * @since 4.3
    */
    public DocumentLine(org.openide.util.Lookup obj, PositionRef pos) {
        super(obj);
        this.pos = pos;
    }

    /** Init listeners
    */
    void init() {
        listener = new LR();
        pos.getCloneableEditorSupport().addChangeListener(
            org.openide.util.WeakListeners.change(listener, pos.getCloneableEditorSupport())
        );
    }

    /* Get the line number.
     * The number may change if the
    * text is modified.
    *
    * @return Returns current line number.
    */
    public int getLineNumber() {
        try {
            return pos.getLine();
        } catch (IOException ex) {
            // what else?
            return 0;
        }
    }

    /* Shows the line.
    * @param kind one of SHOW_XXX constants.
    * @column the column of this line which should be selected
    * @deprecated Deprecated since 6.21. Use {@link #show(ShowOpenType, ShowVisibilityType, int)} instead.
    */
    @Deprecated
    public abstract void show(int kind, int column);

    /* Sets the breakpoint. */
    @SuppressWarnings("deprecation")
    public void setBreakpoint(boolean b) {
        if (breakpoint != b) {
            breakpoint = b;
            refreshState();
        }
    }

    /* Tests if the breakpoint is set. */
    @SuppressWarnings("deprecation")
    public boolean isBreakpoint() {
        return breakpoint;
    }

    /* Marks the error. */
    @SuppressWarnings("deprecation")
    public void markError() {
        DocumentLine previous = registerLine(1, this);

        if (previous != null) {
            previous.error = false;
            previous.refreshState();
        }

        error = true;

        refreshState();
    }

    /* Unmarks error at this line. */
    @SuppressWarnings("deprecation")
    public void unmarkError() {
        error = false;
        registerLine(1, null);

        refreshState();
    }

    /* Marks this line as current. */
    @SuppressWarnings("deprecation")
    public void markCurrentLine() {
        DocumentLine previous = registerLine(0, this);

        if (previous != null) {
            previous.current = false;
            previous.refreshState();
        }

        current = true;
        refreshState();
    }

    /* Unmarks this line as current. */
    @SuppressWarnings("deprecation")
    public void unmarkCurrentLine() {
        current = false;
        registerLine(0, null);

        refreshState();
    }

    /** Refreshes the current line.
     *
     * @deprecated since 1.20. */
    @Deprecated
    synchronized void refreshState() {
        StyledDocument doc = pos.getCloneableEditorSupport().getDocument();

        if (doc != null) {
            // the document is in memory, mark the state
            if (docL != null) {
                doc.removeDocumentListener(docL);
            }

            // error line
            if (error) {
                NbDocument.markError(doc, pos.getOffset());

                doc.addDocumentListener(docL = org.openide.util.WeakListeners.document(listener, doc));

                return;
            }

            // current line
            if (current) {
                NbDocument.markCurrent(doc, pos.getOffset());

                return;
            }

            // breakpoint line
            if (breakpoint) {
                NbDocument.markBreakpoint(doc, pos.getOffset());

                return;
            }

            NbDocument.markNormal(doc, pos.getOffset());

            return;
        }
    }

    public int hashCode() {
        return pos.getCloneableEditorSupport().hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof DocumentLine) {
            DocumentLine dl = (DocumentLine) o;

            if (dl.pos.getCloneableEditorSupport() == pos.getCloneableEditorSupport()) {
                return dl.getLineNumber() == getLineNumber();
            }
        }

        return false;
    }

    //
    // Work with global hash table
    //

    /** Register this line as the one stored
    * under indx-index (0 = current, 1 = error).
    *
    * @param indx index to register
    * @param line value to add (this or null)
    * @return the previous value
    *
    * @deprecated since 1.20 */
    @Deprecated
    private DocumentLine registerLine(int indx, DocumentLine line) {
        DocumentLine prev;

        CloneableEditorSupport es = pos.getCloneableEditorSupport();

        DocumentLine[] arr = assigned.get(es);

        if (arr != null) {
            // remember the previous
            prev = arr[indx];
        } else {
            // create new array
            arr = new DocumentLine[2];
            assigned.put(es, arr);
            prev = null;
        }

        arr[indx] = line;

        return prev;
    }

    //
    // Serialization
    //

    /** Write fields.
    */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // do not do default read/write object
        oos.writeObject(pos);
        oos.writeBoolean(breakpoint);
    }

    /** Read important fields.
    */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        pos = (PositionRef) ois.readObject();
        setBreakpoint(ois.readBoolean());
        lineParts = new ArrayList<Part>(3);
    }

    /** Register line.
    */
    Object readResolve() throws ObjectStreamException {
        //        return Set.registerLine (this);
        //Set.registerPendingLine(this);
        return this.pos.getCloneableEditorSupport().getLineSet().registerLine(this);
    }

    /** Add annotation to this Annotatable class
     * @param anno annotation which will be attached to this class */
    protected void addAnnotation(Annotation anno) {
        super.addAnnotation(anno);

        StyledDocument doc = pos.getCloneableEditorSupport().getDocument();

        // document is not opened and so the annotation will be added to document later
        if (doc == null) {
            return;
        }

        pos.getCloneableEditorSupport().prepareDocument().waitFinished();

        try {
            if (!anno.isInDocument()) {
                anno.setInDocument(true);

                // #33165 - find position that is surely at begining of line
                FindAnnotationPosition fap = new FindAnnotationPosition(doc, pos.getPosition());
                doc.render(fap);
                NbDocument.addAnnotation(doc, fap.getAnnotationPosition(), -1, anno);
            }
        } catch (IOException ex) {
            Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    /** Remove annotation to this Annotatable class
     * @param anno annotation which will be detached from this class  */
    protected void removeAnnotation(Annotation anno) {
        super.removeAnnotation(anno);

        StyledDocument doc = pos.getCloneableEditorSupport().getDocument();

        // document is not opened and so no annotation is attached to it
        if (doc == null) {
            return;
        }

        pos.getCloneableEditorSupport().prepareDocument().waitFinished();

        if (anno.isInDocument()) {
            anno.setInDocument(false);
            NbDocument.removeAnnotation(doc, anno);
        }
    }

    /** When document is opened or closed the annotations must be added or
     * removed.
     * @since 1.27 */
    void attachDetachAnnotations(StyledDocument doc, boolean closing) {
        // #33165 - find position that is surely at begining of line
        Position annoPos = null;

        if (!closing) {
            try {
                annoPos = pos.getPosition();

                FindAnnotationPosition fap = new FindAnnotationPosition(doc, annoPos);
                doc.render(fap);
                annoPos = fap.getAnnotationPosition();
            } catch (IOException ex) {
                Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        List<? extends Annotation> list = getAnnotations();
        synchronized(list) {
            for (Annotation anno : list) {
                if (!closing) {
                    if (!anno.isInDocument()) {
                        anno.setInDocument(true);
                        NbDocument.addAnnotation(doc, annoPos, -1, anno);
                    }
                } else {
                    if (anno.isInDocument()) {
                        anno.setInDocument(false);
                        NbDocument.removeAnnotation(doc, anno);
                    }
                }
            }
        }

        // notify also all Line.Part attached to this Line
        for (Part p : lineParts) {
            p.attachDetachAnnotations(doc, closing);
        }
    }

    public String getText() {
        final StyledDocument doc = pos.getCloneableEditorSupport().getDocument();

        // document is not opened
        if (doc == null) {
            return null;
        }

        final String[] retStringArray = new String[1];
        doc.render(
            new Runnable() {
                public void run() {
                    // Part of #33165 - the following code is wrapped by doc.render()
                    int lineNumber = getLineNumber();
                    int lineStart = NbDocument.findLineOffset(doc, lineNumber);

                    // #24434: Check whether the next line exists
                    // (the current one could be the last one).
                    int lineEnd;

                    if ((lineNumber + 1) >= NbDocument.findLineRootElement(doc).getElementCount()) {
                        lineEnd = doc.getLength();
                    } else {
                        lineEnd = NbDocument.findLineOffset(doc, lineNumber + 1);
                    }

                    try {
                        retStringArray[0] = doc.getText(lineStart, lineEnd - lineStart);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
                        retStringArray[0] = null;
                    }

                    // End of the code wrapped by doc.render()
                }
            }
        );

        return retStringArray[0];
    }

    /** Attach created Line.Part to the parent Line */
    void addLinePart(DocumentLine.Part linePart) {
        lineParts.add(linePart);
    }

    /** Move Line.Part from this Line to a new one*/
    void moveLinePart(DocumentLine.Part linePart, DocumentLine newLine) {
        lineParts.remove(linePart);
        newLine.addLinePart(linePart);
        linePart.changeLine(newLine);
    }

    /** Notify Line.Part(s) that content of the line was changed and that Line.Part(s) may be affected by that*/
    void notifyChange(DocumentEvent p0, DocumentLine.Set set, StyledDocument doc) {
        DocumentLine.Part part;

        for (int i = 0; i < lineParts.size();) {
            part = lineParts.get(i);

            // notify Line.Part about the change
            part.handleDocumentChange(p0);

            // if necessary move Line.Part to new Line
            if (NbDocument.findLineNumber(doc, part.getOffset()) != part.getLine().getLineNumber()) {
                DocumentLine line = (DocumentLine) set.getCurrent(NbDocument.findLineNumber(doc, part.getOffset()));
                moveLinePart(part, line);
            } else {
                i++;
            }
        }

        // #33165 - fix the position in the positionRef in case this line changes
        // and reattach the annotations.
        // The fix of #32764 in notifyMove() would only reattach
        // the annotations in case the position does not go at a line begining
        // after the modification but that is not enough
        // to fix undo-related issues.
        Position p;

        try {
            p = pos.getPosition();
        } catch (IOException ex) {
            Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
            p = null;
        }

        if (p != null) {
            int lineStartOffset = NbDocument.findLineOffset(doc, NbDocument.findLineNumber(doc, p.getOffset()));
            CloneableEditorSupport support = pos.getCloneableEditorSupport();

            // Recreate positionRef unconditionally to avoid undo problems
            pos = new PositionRef(support.getPositionManager(), lineStartOffset, Position.Bias.Forward);

            List<? extends Annotation> annos = getAnnotations();
            synchronized(annos) {
                if (annos.size() > 0) {
                    try {
                        p = pos.getPosition();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(); // should not fail
                    }

                    for (Annotation anno : annos) {

                        if (anno.isInDocument()) {
                            anno.setInDocument(false);
                            NbDocument.removeAnnotation(support.getDocument(), anno);
                        }
                        
                        if (!anno.isInDocument()) {
                            anno.setInDocument(true);
                            NbDocument.addAnnotation(support.getDocument(), p, -1, anno);
                        }
                    }
                }
            }
        }
    }

    /** Notify Line.Part(s) that line was moved. */
    void notifyMove() {
        updatePositionRef();

        for (int i = 0; i < lineParts.size(); i++) {
            ((DocumentLine.Part) lineParts.get(i)).firePropertyChange(Line.Part.PROP_LINE, null, null);
        }
    }

    /** Updates <code>pos</code> the way it points at the start of line. */
    private void updatePositionRef() {
        // #33165 - Moved handling that follows into notifyChange()
        //  due to problems with undo operations.
        // Rest of notifyMove() should work as the code in notifyChange()
        // in fact includes the same work as done here and notifyChange()
        // is called before notifyMove()
        // (see linesChanged()/linesMoved() in LineListener).

        /*        CloneableEditorSupport support = pos.getCloneableEditorSupport();
                int startOffset = NbDocument.findLineOffset(support.getDocument(),
                    getLineNumber());

                if(pos.getOffset() != startOffset) {
                    pos = new PositionRef(
                        support.getPositionManager(), startOffset, Position.Bias.Forward
                    );

                    // fix of #32764
                    List annos = getAnnotations();
                    for (int i=0; i<annos.size(); i++) {
                        Annotation anno = (Annotation)annos.get(i);
                        if (anno.isInDocument()) {
                            anno.setInDocument(false);
                            NbDocument.removeAnnotation(support.getDocument(), anno);
                        }

                        try {
                            if (!anno.isInDocument()) {
                                anno.setInDocument(true);
                                NbDocument.addAnnotation (support.getDocument(), pos.getPosition(), -1, anno);
                            }
                        }  catch (IOException ex) {
                            ErrorManager.getDefault ().notify ( ErrorManager.EXCEPTION, ex);
                        }
                    }
                }
         */
    }

    /**
     * Runnable used to find a position where the annotation should be attached.
     * It is intended to be run under document's readlock.
     */
    private static final class FindAnnotationPosition implements Runnable {
        private StyledDocument doc;
        private Position annoPos;

        FindAnnotationPosition(StyledDocument doc, Position pos) {
            this.doc = doc;
            this.annoPos = pos; // by default assume the given one is correct
        }

        public void run() {
            int offset = annoPos.getOffset();
            int lineStartOffset = doc.getParagraphElement(offset).getStartOffset();

            if (offset != lineStartOffset) { // not at line start -> correct

                try {
                    annoPos = doc.createPosition(lineStartOffset);
                } catch (BadLocationException e) {
                    throw new IllegalArgumentException(); // should never fail
                }
            }
        }

        Position getAnnotationPosition() {
            return annoPos;
        }
    }

    /** Implementation of Line.Part abstract class*/
    static class Part extends Line.Part {
        /** Reference of this part to the document*/
        private PositionRef position;

        /** Reference to Line to which this part belongs*/
        private Line line;

        /** Length of the annotated text*/
        private int length;

        /** Offset of this Part before the modification. This member is used in
         * listener on document changes and it is updated after each change. */
        private int previousOffset;

        public Part(Line line, PositionRef position, int length) {
            this.position = position;
            this.line = line;
            previousOffset = position.getOffset();
            this.length = limitLength(length);
        }

        private int limitLength(int suggestedLength) {
            Document d = position.getCloneableEditorSupport().getDocument();
            if (d == null) {
                // Can happen when closing a document, don't know why.
                return suggestedLength;
            }
            int end = position.getOffset() + suggestedLength;

            if (end > d.getLength()) {
                end = d.getLength();
            }

            if (end < position.getOffset()) {
                return 0;
            }

            try {
                String text = d.getText(position.getOffset(), end - position.getOffset());
                int newLine = text.indexOf('\n');

                return (newLine == -1) ? text.length() : (newLine + 1);
            } catch (BadLocationException ex) {
                IllegalStateException i = new IllegalStateException(ex.getMessage());
                i.initCause(ex);
                throw i;
            }
        }

        /** Start column of annotation */
        public int getColumn() {
            try {
                return position.getColumn();
            } catch (IOException ex) {
                return 0; //TODO: change this
            }
        }

        /** Length of the annotated text. The length does not cross line end. If the annotated text is
         * split during the editing, the annotation is shorten till the end of the line. Modules can listen on
         * changes of this value*/
        public int getLength() {
            return length;
        }

        /** Line can change during editting*/
        public Line getLine() {
            return line;
        }

        /** Offset of the Line.Part*/
        int getOffset() {
            return position.getOffset();
        }

        /** Line can change during editting*/
        void changeLine(Line line) {
            this.line = line;

            // TODO: check whether there is really some change
            firePropertyChange(PROP_LINE_NUMBER, null, line);
        }

        /** Add annotation to this Annotatable class
         * @param anno annotation which will be attached to this class */
        protected void addAnnotation(Annotation anno) {
            super.addAnnotation(anno);

            StyledDocument doc = position.getCloneableEditorSupport().getDocument();

            // document is not opened and so the annotation will be added to document later
            if (doc == null) {
                return;
            }

            position.getCloneableEditorSupport().prepareDocument().waitFinished();

            try {
                if (!anno.isInDocument()) {
                    anno.setInDocument(true);
                    NbDocument.addAnnotation(doc, position.getPosition(), length, anno);
                }
            } catch (IOException ex) {
                Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        /** Remove annotation to this Annotatable class
         * @param anno annotation which will be detached from this class  */
        protected void removeAnnotation(Annotation anno) {
            super.removeAnnotation(anno);

            StyledDocument doc = position.getCloneableEditorSupport().getDocument();

            // document is not opened and so no annotation is attached to it
            if (doc == null) {
                return;
            }

            position.getCloneableEditorSupport().prepareDocument().waitFinished();

            if (anno.isInDocument()) {
                anno.setInDocument(false);
                NbDocument.removeAnnotation(doc, anno);
            }
        }

        public String getText() {
            final StyledDocument doc = position.getCloneableEditorSupport().getDocument();

            // document is not opened
            if (doc == null) {
                return null;
            }

            final String[] retStringArray = new String[1];
            doc.render(
                new Runnable() {
                    public void run() {
                        // Part of #33165 - the following code is wrapped by doc.render()
                        try {
                            int p = position.getOffset();

                            if (p >= doc.getLength()) {
                                retStringArray[0] = "";
                            } else {
                                retStringArray[0] = doc.getText(position.getOffset(), getLength());
                            }
                        } catch (BadLocationException ex) {
                            Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
                            retStringArray[0] = null;
                        }

                        // End of the code wrapped by doc.render()
                    }
                }
            );

            return retStringArray[0];
        }

        /** When document is opened or closed the annotations must be added or
         * removed.*/
        void attachDetachAnnotations(StyledDocument doc, boolean closing) {
            List<? extends Annotation> list = getAnnotations();

            synchronized(list) {
                for (Annotation anno : list) {

                    if (!closing) {
                        try {
                            if (!anno.isInDocument()) {
                                anno.setInDocument(true);
                                NbDocument.addAnnotation(doc, position.getPosition(), getLength(), anno);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(DocumentLine.class.getName()).log(Level.WARNING, null, ex);
                        }
                    } else {
                        if (anno.isInDocument()) {
                            anno.setInDocument(false);
                            NbDocument.removeAnnotation(doc, anno);
                        }
                    }
                }
            }
        }

        /** Handle DocumentChange event. If the change affect this Part, fire
         * the PROP_TEXT event. */
        void handleDocumentChange(DocumentEvent p0) {
            if (p0.getType().equals(DocumentEvent.EventType.INSERT)) {
                if ((p0.getOffset() >= previousOffset) && (p0.getOffset() < (previousOffset + getLength()))) {
                    firePropertyChange(Annotatable.PROP_TEXT, null, null);
                }
            }

            if (p0.getType().equals(DocumentEvent.EventType.REMOVE)) {
                if (
                    ((p0.getOffset() >= previousOffset) && (p0.getOffset() < (previousOffset + getLength()))) ||
                        ((p0.getOffset() < previousOffset) && ((p0.getOffset() + p0.getLength()) > previousOffset))
                ) {
                    length = limitLength(length);
                    firePropertyChange(Annotatable.PROP_TEXT, null, null);
                }
            }

            if (
                (p0.getType().equals(DocumentEvent.EventType.INSERT) ||
                    p0.getType().equals(DocumentEvent.EventType.REMOVE)) && (p0.getOffset() < previousOffset)
            ) {
                firePropertyChange(Line.Part.PROP_COLUMN, null, null);
            }

            previousOffset = position.getOffset();
        }
    }

    /** Definition of actions performed in Listener */
    private final class LR implements Runnable, ChangeListener, DocumentListener {
        private static final int REFRESH = 0;
        private static final int UNMARK = 1;
        private static final int ATTACH_DETACH = 2;
        private int actionId;
        private EnhancedChangeEvent ev;

        public LR() {
        }

        public LR(int actionId) {
            this.actionId = actionId;
        }

        public LR(EnhancedChangeEvent ev) {
            this.actionId = ATTACH_DETACH;
            this.ev = ev;
        }

        public void run() {
            switch (actionId) {
            case REFRESH:
                refreshState();

                break;

            case UNMARK:
                unmarkError();

                break;

            case ATTACH_DETACH:
                attachDetachAnnotations(ev.getDocument(), ev.isClosingDocument());
                ev = null;

                break;
            }
        }

        private void invoke(int op) {
            // part of #33165 - done synchronously not invoking into EQ
            //SwingUtilities.invokeLater(new LR(op));
            new LR(op).run();
        }

        private void invoke(EnhancedChangeEvent ev) {
            // part of #33165 - done synchronously not invoking into EQ
            //SwingUtilities.invokeLater(new LR(ev));
            new LR(ev).run();
        }

        public void stateChanged(ChangeEvent ev) {
            invoke(REFRESH);
            invoke((EnhancedChangeEvent) ev);
        }

        public void removeUpdate(final javax.swing.event.DocumentEvent p0) {
            invoke(UNMARK);
        }

        public void insertUpdate(final javax.swing.event.DocumentEvent p0) {
            invoke(UNMARK);
        }

        public void changedUpdate(final javax.swing.event.DocumentEvent p0) {
        }
    }

    /** Abstract implementation of {@link Line.Set}.
     *  Defines
    * ways to obtain a line set for documents following
    * NetBeans conventions.
    */
    public static abstract class Set extends Line.Set {
        /** listener on document changes, accessed from LazyLines */
        final LineListener listener;

        /** all lines in the set or null */
        private List<Line> list;

        /** Constructor.
        * @param doc document to work on
        */
        public Set(StyledDocument doc) {
            this(doc, null);
        }

        Set(StyledDocument doc, CloneableEditorSupport support) {
            listener = new LineListener(doc, support);
        }

        /** Find the line given as parameter in list of all lines attached to this set
         * and if the line exist in the list, notify it about being edited. */
        void linesChanged(int startLineNumber, int endLineNumber, DocumentEvent p0) {
            List changedLines = getLinesFromRange(startLineNumber, endLineNumber);
            StyledDocument doc = listener.support.getDocument();

            for (Iterator it = changedLines.iterator(); it.hasNext();) {
                Line line = (Line) it.next();

                line.firePropertyChange(Annotatable.PROP_TEXT, null, null);

                // revalidate all parts attached to this line
                // that they are still part of the line
                if (doc != null && line instanceof DocumentLine) {
                    ((DocumentLine) line).notifyChange(p0, this, doc);
                }
            }
        }

        /** Find the line given as parameter in list of all lines attached to this set
         * and if the line exist in the list, notify it about being moved. */
        void linesMoved(int startLineNumber, int endLineNumber) {
            List movedLines = getLinesFromRange(startLineNumber, endLineNumber);

            for (Iterator it = movedLines.iterator(); it.hasNext();) {
                Line line = (Line) it.next();
                line.firePropertyChange(Line.PROP_LINE_NUMBER, null, null);

                // notify all parts attached to this line
                // that they were moved
                if (line instanceof DocumentLine) {
                    ((DocumentLine) line).notifyMove();
                }
            }
        }

        /** Gets the lines with line number whitin the range inclusive.
         * @return <code>List</code> of lines from range inclusive */
        private List<Line> getLinesFromRange(int startLineNumber, int endLineNumber) {
            List<Line> linesInRange = new ArrayList<Line>(10);

            synchronized (findWeakHashMap()) {
                for (Line line : findWeakHashMap().keySet()) {
                    int lineNumber = line.getLineNumber();

                    if ((startLineNumber <= lineNumber) && (lineNumber <= endLineNumber)) {
                        linesInRange.add(line);
                    }
                }
            }

            return linesInRange;
        }

        /* Returns an unmodifiable set of Lines sorted by their
        * line numbers that contains all lines holded by this
        * Line.Set.
        *
        * @return list of Line objects
        */
        public synchronized List<? extends Line> getLines() {
            if (list == null) {
                list = new LazyLines(this);
            }

            return list;
        }

        /* Finder method that for the given line number finds right
        * Line object that represent as closely as possible the line number
        * in the time when the Line.Set has been created.
        *
        * @param line is a number of the line (text line) we want to acquire
        * @exception IndexOutOfBoundsException if <code>line</code> is invalid.
        */
        public Line getOriginal(int line) throws IndexOutOfBoundsException {
            int newLine = listener.getLine(line);
            return safelyRegisterLine(createLine(offset(newLine)));
        }

        public int getOriginalLineNumber(Line line) {
            Line find = findLine(line);

            if (find != null) {
                return listener.getOld(find.getLineNumber());
            } else {
                return -1;
            }
        }

        /* Creates current line.
        *
        * @param line is a number of the line (text line) we want to acquire
        * @exception IndexOutOfBoundsException if <code>line</code> is invalid.
        */
        public Line getCurrent(int line) throws IndexOutOfBoundsException {

            return safelyRegisterLine(createLine(offset(line)));
        }
        
        private int offset(int line) {
            StyledDocument doc = listener.support.getDocument();
            int offset = doc == null ? 0 : NbDocument.findLineOffset(doc, line);
            return offset;
        }

        /** Creates a {@link Line} for a given offset.
        * @param offset the beginning offset of the line
        * @return line object representing the line at this offset
        */
        protected abstract Line createLine(int offset);

        /** Registers line, but only after obtaining the lock of the document.
         * This is a fix to issue 37767 as this creates ordering of locks (first
         * of all obtain documentrenderer, then ask for any other locks like
         * Line.Set.lines.
         *
         * @param line line we want to register
         * @return the line or some line that already was registered
         */
        private Line safelyRegisterLine(final Line line) {
            assert line != null;
            class DocumentRenderer implements Runnable {
                public Line result;

                public void run() {
                    result = DocumentLine.Set.super.registerLine(line);
                }
            }
            StyledDocument doc = listener.support.getDocument();
            DocumentRenderer renderer = new DocumentRenderer();
            if (doc != null) {
                doc.render(renderer);
            } else {
                renderer.run();
            }

            return renderer.result;
        }
    }
}
