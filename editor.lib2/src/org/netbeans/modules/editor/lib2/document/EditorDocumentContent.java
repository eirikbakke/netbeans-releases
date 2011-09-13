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

package org.netbeans.modules.editor.lib2.document;

import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoableEdit;
import org.netbeans.modules.editor.lib2.document.ContentEdit.InsertEdit;
import org.netbeans.modules.editor.lib2.document.ContentEdit.RemoveEdit;

/**
 * Content of the document.
 * <br/>
 * It's similar to swing's GapConent but it is a bit more consistent in terms of position sharing
 * in the following scenario:
 * <ul>
 *   <li> Create position at pos1(off==1) and pos2(off==2) </li>
 *   <li> Remove(1,1) so pos2 is at offset==1. </li>
 *   <li> createPosition(1): GapContent picks either pos1 or pos2 by binary search.
 *    Which one is picked depends on total number of positions so creation of extra positions
 *    on different offsets affects that. EDC always returns pos2 in this scenario regardless
 *    on total number of positions.
 *   </li>
 * </ul>
 * <br/>
 * Both GapContent and EDC may reuse position which returns into a middle of removed area
 * upon undo:
 * <ul>
 *   <li> Create position at pos1(off==2)</li>
 *   <li> Remove(1,2) so pos1 is at offset==1.</li>
 *   <li> createPosition(1): pos1 is returned. </li>
 *   <li> Undo (which means Insert(1,2)) returns pos1 to offset==2 (someone might expect offset==3). </li>
 * </ul>
 *
 * @author Miloslav Metelka
 * @since 1.46
 */

public final class EditorDocumentContent implements AbstractDocument.Content {
    
    // -J-Dorg.netbeans.modules.editor.lib2.document.EditorDocumentContent.level=FINE
    private static final Logger LOG = Logger.getLogger(EditorDocumentContent.class.getName());

    /** Character contents of document. */
    private final CharContent charContent;

    /** Vector holding the marks for the document */
    private final MarkVector markVector;
    
    /** Vector holding backward-bias marks - it's null until at least one backward-bias mark gets created. */
    private MarkVector bbMarkVector;
    
    public EditorDocumentContent() {
        // In compliance with AbstractDocument the content has one extra unmodifiable '\n' at the end
        charContent = new CharContent();
        markVector = new MarkVector(this, false);
    }
    
    /**
     * Perform additional initialization of document by this content (set properties).
     *
     * @param doc non-null document
     */
    public void init(Document doc) {
        doc.putProperty(CharSequence.class, (CharSequence)charContent);
    }
    
    @Override
    public UndoableEdit insertString(int offset, String text) throws BadLocationException {
        if (text.length() == 0) {
            // Empty insert should be eliminated in parent (Document impl).
            // It could break MarkVector.insertUpdate() operation
            throw new IllegalArgumentException("EditorDocumentContent: Empty insert"); // NOI18N
        }
        checkOffsetInDoc(offset);
        InsertEdit insertEdit = new InsertEdit(this, offset, text);
        insertEdit(insertEdit);
        return insertEdit;
    }
    
    synchronized void insertEdit(ContentEdit edit) {
//        checkConsistency(); LOG.fine("insertEdit:" + edit); // NOI18N
//        LOG.fine("markVector-before-insert:\n" + toStringDetail()); // NOI18N
        charContent.insertText(edit.offset, edit.text);
        markVector.insertUpdate(edit.offset, edit.length(), edit.markUpdates);
        edit.markUpdates = null; // Allow GC
        if (bbMarkVector != null) {
            bbMarkVector.insertUpdate(edit.offset, edit.length(), edit.bbMarkUpdates);
            edit.bbMarkUpdates = null; // Allow GC
        }
//        LOG.fine("INSERT-after: DUMP\n"+ markVector.toStringDetail(null)); checkConsistency();
    }
    
    @Override
    public UndoableEdit remove(int offset, int length) throws BadLocationException {
        checkBoundsInDoc(offset, length);
        String removedText = getString(offset, length);
        return remove(offset, removedText);
    }
    
    /**
     * Optimized removal when having removal string and bounds are verified to be ok.
     * 
     * @param removedText string that corresponds to the text being removed.
     */
    public UndoableEdit remove(int offset, String removedText) throws BadLocationException { // optimized version 
        RemoveEdit removeEdit = new RemoveEdit(this, offset, removedText);
        removeEdit(removeEdit);
        return removeEdit;
    }
    
    synchronized void removeEdit(ContentEdit edit) {
//        checkConsistency(); LOG.fine("removeEdit:" + edit); // NOI18N
//        LOG.fine("markVector-before-remove:\n" + toStringDetail()); // NOI18N
        int len = edit.length();
        charContent.removeText(edit.offset, len);
        edit.markUpdates = markVector.removeUpdate(edit.offset, len);
        if (bbMarkVector != null) {
            edit.bbMarkUpdates = bbMarkVector.removeUpdate(edit.offset, len);
        }
//        LOG.fine("REMOVE-after: DUMP\n"+ markVector.toStringDetail(null)); checkConsistency();
    }

    @Override
    public synchronized Position createPosition(int offset) throws BadLocationException {
        checkOffsetInContent(offset);
//        checkConsistency(); LOG.fine("createPosition(" + offset + ")-before\n"); // NOI18N
        EditorPosition pos = markVector.position(offset);
//        LOG.fine("createPosition(" + offset + ")=" + pos.getMark().toStringDetail() + "\n"); checkConsistency(); // NOI18N
        return pos;
    }

    public synchronized Position createBackwardBiasPosition(int offset) throws BadLocationException {
        checkOffsetInContent(offset);
        if (bbMarkVector == null) {
            bbMarkVector = new MarkVector(this, true);
        }
        return bbMarkVector.position(offset);
    }

    @Override
    public int length() { // Not synchronized => only subtracts vars
        return charContent.length();
    }
    
    private int docLen() {
        return length() - 1;
    }
    
    public CharSequence getText() {
        return charContent;
    }

    public int getCharContentGapStart() { // Used by BaseDocument impl
        return charContent.gapStart();
    }

    @Override
    public synchronized void getChars(int offset, int length, Segment txt) throws BadLocationException {
        checkBoundsInContent(offset, length);
        charContent.getChars(offset, length, txt);
    }

    @Override
    public synchronized String getString(int offset, int length) throws BadLocationException {
        checkBoundsInContent(offset, length);
        return charContent.getString(offset, length);
    }
    
    public synchronized void compact() {
        charContent.compact();
        markVector.compact();
        if (bbMarkVector != null) {
            bbMarkVector.compact();
        }
    }
    
    public synchronized String consistencyError() {
        String err = charContent.consistencyError();
        if (err == null) {
            err = markVector.consistencyError(length());
        }
        if (err == null && bbMarkVector != null) {
            err = bbMarkVector.consistencyError(length());
        }
        return err;
    }
    
    public void checkConsistency() { // Not synced (dumpConsistency() is synced)
        String err = consistencyError();
        if (err != null) {
            throw new IllegalStateException("Content inconsistency: " + err + "\nContent:\n" + // NOI18N
                    toStringDetail());
        }
    }
    
    private void checkOffsetNonNegative(int offset) throws BadLocationException {
        if (offset < 0) {
            throw new BadLocationException("Invalid offset=" + offset // NOI18N
                + " < 0; docLen=" + docLen(), offset); // NOI18N
        }
    }

    private void checkOffsetInDoc(int offset) throws BadLocationException {
        checkOffsetNonNegative(offset);
        if (offset > docLen()) {
            throw new BadLocationException("Invalid offset=" + offset // NOI18N
                + " > docLen=" + docLen(), offset); // NOI18N
        }
    }

    private void checkOffsetInContent(int offset) throws BadLocationException {
        checkOffsetNonNegative(offset);
        if (offset > length()) {
            throw new BadLocationException("Invalid offset=" + offset // NOI18N
                + " > (docLen+1)=" + docLen(), offset); // NOI18N
        }
    }

    private void checkLengthNonNegative(int length) throws BadLocationException {
        if (length < 0) {
            throw new BadLocationException("Invalid length=" + length + " < 0", length); // NOI18N
        }
    }

    private void checkBoundsInDoc(int offset, int length) throws BadLocationException {
        checkOffsetNonNegative(offset);
        checkLengthNonNegative(length);
	if (offset + length > docLen()) {
	    throw new BadLocationException("Invalid (offset=" + offset + " + length=" + length + // NOI18N
                    ")=" + (offset+length) + " > docLen=" + docLen(), offset + length); // NOI18N
	}
    }

    private void checkBoundsInContent(int offset, int length) throws BadLocationException {
        checkOffsetNonNegative(offset);
        checkLengthNonNegative(length);
	if (offset + length > length()) {
	    throw new BadLocationException("Invalid (offset=" + offset + " + length=" + length + // NOI18N
                    ")=" + (offset+length) + " > (docLen+1)=" + length(), offset + length); // NOI18N
	}
    }

    @Override
    public synchronized String toString() {
        return "chars: " + charContent.toStringDescription() + // NOI18N
                ", marks: " + markVector + // NOI18N
                ", bbMarks: " + ((bbMarkVector != null) ? bbMarkVector : "<NULL>"); // NOI18N
    }
    
    public synchronized String toStringDetail() {
        return  "marks: " + markVector.toStringDetail(null) + // NOI18N
                ", bbMarks: " + ((bbMarkVector != null) ? bbMarkVector.toStringDetail(null) : "<NULL>"); // NOI18N
    }
    
}
