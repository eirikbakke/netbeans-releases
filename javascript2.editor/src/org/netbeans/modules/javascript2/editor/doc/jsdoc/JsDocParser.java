/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript2.editor.doc.jsdoc;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javascript2.editor.doc.jsdoc.model.DescriptionElement;
import org.netbeans.modules.javascript2.editor.doc.jsdoc.model.JsDocElement;
import org.netbeans.modules.javascript2.editor.doc.jsdoc.model.JsDocElement.Type;
import org.netbeans.modules.javascript2.editor.doc.jsdoc.model.JsDocElementUtils;
import org.netbeans.modules.javascript2.editor.lexer.JsTokenId;
import org.netbeans.modules.javascript2.editor.lexer.LexUtilities;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 * Parses jsDoc comment blocks and returns list of these blocks and contained {@code JsDocElement}s.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
public class JsDocParser {

    private static final Logger LOGGER = Logger.getLogger(JsDocParser.class.getName());

    /**
     * Parses given snapshot and returns list of all jsDoc blocks.
     * @param scriptText text to parse
     * @return list of blocks
     */
    public static Map<Integer, JsDocBlock> parse(Snapshot snapshot) {
        Map<Integer, JsDocBlock> blocks = new HashMap<Integer, JsDocBlock>();

        List<CommentBlock> commentBlocks = getCommentBlocks(snapshot);

        for (CommentBlock commentBlock : commentBlocks) {
            JsDocCommentType commentType = getCommentType(commentBlock.getContent());
            LOGGER.log(Level.FINEST, "JsDocParser:comment block offset=[{0}-{1}],type={2},text={3}", new Object[]{
                commentBlock.getBeginOffset(), commentBlock.getEndOffset(), commentType, commentBlock.getContent()});

            if (commentType == JsDocCommentType.DOC_NO_CODE_START
                    || commentType == JsDocCommentType.DOC_NO_CODE_END
                    || commentType == JsDocCommentType.DOC_SHARED_TAG_END) {
                blocks.put(commentBlock.getEndOffset(), new JsDocBlock(commentBlock.getBeginOffset(),
                        commentBlock.getEndOffset(), commentType, Collections.<JsDocElement>emptyList()));
                continue;
            } else {
                blocks.put(commentBlock.getEndOffset(),
                        parseCommentBlock(commentBlock, commentType == JsDocCommentType.DOC_SHARED_TAG_START));
                continue;
            }
        }

        return blocks;
    }

    private static void storeCommentPart(List<CommentStrip> storage, String cleanedPart, int offset, StringBuilder link) {
        if (!"".equals(cleanedPart.trim())) { //NOI18N
            if (cleanedPart.startsWith("@link")) { //NOI18N
                link.insert(0, cleanedPart);
            } else if (link.length() > 0) {
                storage.add(new CommentStrip(cleanedPart + link.toString(), offset));
                link.delete(0, link.toString().length());
            } else {
                storage.add(new CommentStrip(cleanedPart + link.toString(), offset));
            }
        }
    }

    /**
     * Gets particular cleaned parts of comment.
     * @param comment whole comment block string
     * @return array of strings with cleaned
     */
    protected static CommentStrip[] getCleanedCommentStrips(String comment, int commentOffset) {
        List<CommentStrip> strips = new ArrayList<CommentStrip>();

        String processedText = comment;
        StringBuilder linkComment = new StringBuilder();
        int indexOfAt;
        while ((indexOfAt = processedText.lastIndexOf("@")) != -1) { //NOI18N
            String cleaned = cleanElementText(processedText.substring(indexOfAt));
            storeCommentPart(strips, cleaned, commentOffset + indexOfAt, linkComment);
            processedText = processedText.substring(0, indexOfAt);
        }

        // process the rest from string
        String cleaned = cleanElementText(processedText);
        storeCommentPart(strips, cleaned, 0, linkComment);
        Collections.reverse(strips);
        return strips.toArray(new CommentStrip[strips.size()]);
    }

    private static JsDocBlock parseCommentBlock(CommentBlock block, boolean sharedTag) {
        String commentText = block.getContent();
        List<JsDocElement> jsDocElements = new ArrayList<JsDocElement>();
        CommentStrip[] commentStrips = getCleanedCommentStrips(commentText, block.getBeginOffset());
        boolean afterDescription = false;
        for (CommentStrip commentStrip : commentStrips) {
            String stripeText = commentStrip.getStrip();

            //TODO - clean shared tag comments
            if (!stripeText.startsWith("@")) { //NOI18N
                if (!afterDescription) {
                    //TODO - distinguish description and inline comments
                    jsDocElements.add(DescriptionElement.create(Type.CONTEXT_SENSITIVE, stripeText));
                }
            } else {
                Type type;
                int firstSpace = stripeText.indexOf(" "); //NOI18N
                if (firstSpace == -1) {
                    type = Type.fromString(stripeText);
                    jsDocElements.add(JsDocElementUtils.createElementForType(type, "", -1));
                } else {
                    type = Type.fromString(stripeText.substring(0, firstSpace));
                    String tagDescription = stripeText.substring(firstSpace).trim();
                    int offset = commentStrip.getOffset() + stripeText.length() - tagDescription.length();
                    jsDocElements.add(JsDocElementUtils.createElementForType(type, tagDescription, offset));
                }
            }
            // after description is after first sentence or after any keyword
            afterDescription = true;
        }

        return new JsDocBlock(
                block.getBeginOffset(),
                block.getEndOffset(),
                sharedTag ? JsDocCommentType.DOC_SHARED_TAG_START : JsDocCommentType.DOC_COMMON,
                jsDocElements);
    }

    private static List<CommentBlock> getCommentBlocks(Snapshot snapshot) {
        List<CommentBlock> blocks = new LinkedList<CommentBlock>();
        TokenSequence tokenSequence = snapshot.getTokenHierarchy().tokenSequence();
        if (tokenSequence == null) {
            return blocks;
        }

        while (tokenSequence.moveNext()) {
            Token<? extends JsTokenId> token = tokenSequence.token();
            if (token.id() == JsTokenId.DOC_COMMENT) {
                int startOffset = token.offset(snapshot.getTokenHierarchy());
                int endOffset = startOffset + token.length();
                blocks.add(new CommentBlock(startOffset, endOffset, token.toString()));
            }
        }
        return blocks;
    }

    private static JsDocCommentType getCommentType(String commentBlock) {
        //TODO - move that into some constatns holder
        if (commentBlock.startsWith("/**#")) { //NOI18N
            if ("/**#nocode+*/".equals(commentBlock)) { //NOI18N
                return JsDocCommentType.DOC_NO_CODE_START;
            } else if ("/**#nocode-*/".equals(commentBlock)) {
                return JsDocCommentType.DOC_NO_CODE_END;
            } else if (commentBlock.startsWith("/**#@+")) { //NOI18N
                return JsDocCommentType.DOC_SHARED_TAG_START;
            } else if ("/**#@-*/".equals(commentBlock)) { //NOI18N
                return JsDocCommentType.DOC_SHARED_TAG_END;
            }
        }
        return JsDocCommentType.DOC_COMMON;
    }

    protected static String cleanElementText(String comment) {
        String cleaned = comment.trim();

        // clean " /** ", " */", " * " on all rows
        cleaned = cleaned.replaceAll("[\\s&&[^\r\n]]*(\\*)+(\\s)*|[/]$|(\\s)*[/*](\\*)+(\\s)*", ""); //NOI18N
        // less accurate but probably a little bit faster
//        cleaned = cleaned.replaceAll("^[/]|[/]$|[\\s&&[^\r\n]]*(\\*)+(\\s)*", ""); //NOI18N

        // replace enters by spaces
        cleaned = cleaned.replaceAll("\r?\n", " ").trim(); //NOI18N

        return cleaned;
    }

    protected static class CommentStrip {

        private final String strip;
        private final int offset;

        public CommentStrip(String strip, int offset) {
            this.strip = strip;
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }

        public String getStrip() {
            return strip;
        }
    }

    private static class CommentBlock {

        private final int beginOffset;
        private final int endOffset;
        private final String content;

        public CommentBlock(int beginOffset, int endOffset, String content) {
            this.beginOffset = beginOffset;
            this.endOffset = endOffset;
            this.content = content;
        }

        public int getBeginOffset() {
            return beginOffset;
        }

        public String getContent() {
            return content;
        }

        public int getEndOffset() {
            return endOffset;
        }
    }

}
