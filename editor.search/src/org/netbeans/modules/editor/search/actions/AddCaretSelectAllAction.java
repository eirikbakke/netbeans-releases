/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.editor.search.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.EditorActionRegistration;
import org.netbeans.api.editor.caret.EditorCaret;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.search.EditorFindSupport;
import org.netbeans.spi.editor.AbstractEditorAction;
import org.openide.util.Exceptions;
import org.openide.util.Pair;

/**
 * Select
 */
// NOI18N
@EditorActionRegistration(
        name = "addCaretSelectAll")
public class AddCaretSelectAllAction extends AbstractEditorAction {

    private static final Logger LOGGER = Logger.getLogger(AddCaretSelectAllAction.class.getName());

    public AddCaretSelectAllAction() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target != null) {
            Caret caret = target.getCaret();
            if (!Utilities.isSelectionShowing(caret)) {
                try {
                    int[] identifierBlock = Utilities.getIdentifierBlock((BaseDocument) target.getDocument(), caret.getDot());
                    if (identifierBlock != null) {
                        caret.setDot(identifierBlock[0]);
                        caret.moveDot(identifierBlock[1]);
                    }
                } catch (BadLocationException e) {
                    LOGGER.log(Level.WARNING, null, e);
                }
            }

            EditorFindSupport findSupport = EditorFindSupport.getInstance();
            HashMap<String, Object> props = new HashMap<>(findSupport.createDefaultFindProperties());
            String searchWord = target.getSelectedText();
            int n = searchWord.indexOf('\n');
            if (n >= 0) {
                searchWord = searchWord.substring(0, n);
            }
            props.put(EditorFindSupport.FIND_WHAT, searchWord);
            Document doc = target.getDocument();
            EditorUI eui = org.netbeans.editor.Utilities.getEditorUI(target);
            if (eui.getComponent().getClientProperty("AsTextField") == null) { //NOI18N
                findSupport.setFocusedTextComponent(eui.getComponent());
            }
            findSupport.putFindProperties(props);
            findSupport.find(null, false);

            if (caret instanceof EditorCaret) {
                EditorCaret editorCaret = (EditorCaret) caret;
                try {
                    int[] blocks = findSupport.getBlocks(new int[]{-1, -1}, doc, 0, doc.getLength());
                    if (blocks[0] >= 0 && blocks.length % 2 == 0) {
                        List<Position> newCarets = new ArrayList<>();

                        for (int i = 0; i < blocks.length; i += 2) {
                            int start = blocks[i];
                            int end = blocks[i + 1];
                            if (start == -1 || end == -1) {
                                break;
                            }
                            Position startPos = doc.createPosition(start);
                            Position endPos = doc.createPosition(end);
                            newCarets.add(endPos);
                            newCarets.add(startPos);
                        }

                        editorCaret.replaceCarets(newCarets);
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }
    }
}
