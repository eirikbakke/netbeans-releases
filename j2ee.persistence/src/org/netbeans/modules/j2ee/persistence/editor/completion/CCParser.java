/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.j2ee.persistence.editor.completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 * Builds an annotations tree containg NN name and attribs map. Supports nested annotations.
 *
 * @author Marek Fukala
 */

public class CCParser {
    
    //parser states
    private static final int INIT = 0;
    private static final int NN = 1; //@
    private static final int ERROR = 2;
    private static final int NNNAME = 3; //@Table
    private static final int INNN = 4; //@Table(
    private static final int ATTRNAME = 5; //@Table(name
    private static final int EQ = 6; //@Table(name=
    private static final int ATTRVALUE = 7; //@Table(name="hello" || @Table(name=@
    
    private final CompilationController controller;

    CCParser(CompilationController controller) {
        this.controller = controller;
    }
    
    public CC parseAnnotation(int offset) {
        int nnStart = findAnnotationStart(offset);
        if(nnStart == -1) {
            return null;
        } else {
            return parseAnnotationOnOffset(nnStart);
        }
    }
    
    /** very simple annotations parser */
    private CC parseAnnotationOnOffset(int offset) {
            int state = INIT;
            TokenSequence<JavaTokenId> ts = controller.getTokenHierarchy().tokenSequence(JavaTokenId.language());
            ts.moveStart();
            if (ts.move(offset) == 0)if(!ts.moveNext())ts.movePrevious();
            Token<JavaTokenId> titk = ts.token();
            JavaTokenId id = titk.id();
            
            assert id == JavaTokenId.AT;
            
            int nnstart = ts.offset();
            int nnend = -1;
            String nnName = null;
            String currAttrName = null;
            String currAttrValue = null;
            List<NNAttr> attrs = new ArrayList<NNAttr>(5);
            //helper var
            int eqOffset = -1;
            
            do {
                id = titk.id();
                //ignore whitespaces
                if(id == JavaTokenId.WHITESPACE || id==JavaTokenId.LINE_COMMENT || id==JavaTokenId.BLOCK_COMMENT || id==JavaTokenId.JAVADOC_COMMENT) {
                    ts.moveNext();
                    titk = ts.token();
                    continue;
                }
                
                switch(state) {
                    case INIT:
                        switch(id) {
                            case AT:
                                state = NN;
                                break;
                            default:
                                state = ERROR;
                        }
                        break;
                    case NN:
                        switch(id) {
                            case IDENTIFIER:
                                state = NNNAME;
                                nnName = titk.text().toString();
                                break;
                            default:
                                state = ERROR;
                        }
                        break;
                    case NNNAME:
                        switch(id) {
                            case LPAREN:
                                state = INNN;
                                break;
                            case DOT:
                            case IDENTIFIER:
                                //add the token image to the NN name
                                nnName += titk.text().toString();
                                break;
                            default:
                                //we are in NN name, but no parenthesis came
                                //this mean either error or annotation without parenthesis like @Id
                                nnend = nnstart + "@".length() + nnName.length();
                                CC newNN = new CC(nnName, attrs, nnstart, nnend);
                                return newNN;
                        }
                        break;
                    case INNN:
                        switch(id) {
                            case IDENTIFIER:
                                currAttrName = titk.text().toString();
                                state = ATTRNAME;
                                break;
                                //case JavaTokenContext.RPAREN_ID:
                            case COMMA:
                                //just consume, still in INNN
                                break;
                            default:
                                //we reached end of the annotation, or error
                                state = ERROR;
                                break;
                        }
                        break;
                    case ATTRNAME:
                        switch(id) {
                            case EQ:
                                state = EQ;
                                eqOffset = ts.offset();
                                break;
                            default:
                                state = ERROR;
                        }
                        break;
                    case EQ:
                        switch(id) {
                            case STRING_LITERAL:
                                state = INNN;
                                currAttrValue = Utils.unquote(titk.text().toString());
                                attrs.add(new NNAttr(currAttrName, currAttrValue, ts.offset(), true));
                                break;
                            case IDENTIFIER:
                                state = INNN;
                                currAttrValue = titk.text().toString();
                                attrs.add(new NNAttr(currAttrName, currAttrValue, ts.offset(), false));
                                break;
                            case AT:
                                //nested annotation
                                CC nestedNN = parseAnnotationOnOffset(ts.offset());
                                attrs.add(new NNAttr(currAttrName, nestedNN, ts.offset(), false));
                                state = INNN;
                                //I need to skip what was parsed in the nested annotation in this parser
                                if (ts.move(nestedNN.getEndOffset()) == 0)if(!ts.moveNext())ts.movePrevious();
                                titk = ts.token();
                                continue; //next loop
                            default:
                                //ERROR => recover
                                //set the start offset of the value to the offset of the equator + 1
                                attrs.add(new NNAttr(currAttrName, "", eqOffset + 1, false));
                                state = INNN;
                                break;
                        }
                }
                
                if(state == ERROR) {
                    //return what we parser so far to be error recovery as much as possible
                    nnend = ts.offset() + titk.text().toString().length();
                    CC newNN = new CC(nnName, attrs, nnstart, nnend);
                    return newNN;
                }
                if(!ts.moveNext()) break;
                titk = ts.token();//get next token
                
            } while(titk != null);
            

        
        return null;
    }
    
    
    private int  findAnnotationStart(int offset) {
        int parentCount = -100;

        TokenSequence<JavaTokenId> ts = controller.getTokenHierarchy().tokenSequence(JavaTokenId.language());
        if (ts.move(offset) == 0 || !ts.moveNext())
            ts.movePrevious();
        int len = offset - ts.offset();
        if (len > 0 && (ts.token().id() == JavaTokenId.IDENTIFIER ||
                ts.token().id().primaryCategory().startsWith("keyword") || //NOI18N
                ts.token().id().primaryCategory().startsWith("string") || //NOI18N
                ts.token().id().primaryCategory().equals("literal")) //NOI18N
                && ts.token().length() >= len) { //TODO: Use isKeyword(...) when available
            String prefix = ts.token().toString().substring(0, len);
            offset = ts.offset();
        }
        Token<JavaTokenId> titk = ts.token();
        while(titk != null) {
                JavaTokenId id = titk.id();

            if(id == JavaTokenId.RPAREN) {
                if(parentCount == -100) parentCount = 0;
                parentCount++;
            } else if(id == JavaTokenId.LPAREN) {
                if(parentCount == -100) parentCount = 0;
                parentCount--;
            } else if(id == JavaTokenId.AT) {
                if(parentCount == -1 || parentCount == -100) { //needed if offset is not within annotation content
                    return ts.offset();
                }
            }
            if (!ts.movePrevious()) {
                break;
            }
            titk = ts.token();
        }
        
        return -1;
    }
    
    public class NNAttr {
        private String name;
        private Object value;
        private int valueOffset;
        private boolean quoted;
        
        NNAttr(String name, Object value, int valueOffset, boolean quoted) {
            this.name = name;
            this.value = value;
            this.valueOffset = valueOffset;
            this.quoted = quoted;
        }
        
        public String getName() {
            return name;
        }
        
        public Object getValue() {
            return value;
        }
        
        public int getValueOffset() {
            return valueOffset;
        }
        
        public boolean isValueQuoted() {
            return quoted;
        }
        
    }
    
    public class CC {
        
        private String name;
        private List<NNAttr> attributes;
        private int startOffset, endOffset;
        
        public CC(String name, List<NNAttr> attributes, int startOffset, int endOffset) {
            this.name = name;
            this.attributes = attributes;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
        
        public String getName() {
            return name;
        }
        
        public List<NNAttr> getAttributesList() {
            return attributes;
        }
        
        public Map<String,Object> getAttributes() {
            HashMap<String,Object> map = new HashMap<String,Object>();
            for(NNAttr nnattr : getAttributesList()) {
                map.put(nnattr.getName(), nnattr.getValue());
            }
            return map;
        }
        
        public NNAttr getAttributeForOffset(int offset) {
            NNAttr prevnn = null;
            for(NNAttr nnattr : getAttributesList()) {
                if(nnattr.getValueOffset() >= offset) {
                    prevnn = nnattr;
                    break;
                }
                prevnn = nnattr;
            }
            
            if(prevnn == null) return null;
            
            int nnEndOffset = prevnn.getValueOffset() + prevnn.getValue().toString().length() + (prevnn.isValueQuoted() ? 2 : 0);
            if(nnEndOffset >= offset && prevnn.getValueOffset() <= offset) {
                return prevnn;
            } else {
                return null;
            }
        }
        
        public int getStartOffset() {
            return startOffset;
        }
        
        public int getEndOffset() {
            return endOffset;
        }
        
        @Override
        public String toString() {
            //just debug purposes -> no need for superb performance
            String text = "@" + getName() + " [" + getStartOffset() + " - " + getEndOffset() + "](";
            for(NNAttr nnattr : getAttributesList()) {
                String key = nnattr.getName();
                String value = nnattr.getValue().toString();
                text+=key+"="+value+ " (" + nnattr.getValueOffset() + ") ,";
            }
            text = text.substring(0, text.length() -1);
            text+=")";
            return text;
        }
    }
}
