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
package org.netbeans.modules.cnd.modelimpl.parser;

import java.util.*;
import org.netbeans.modules.cnd.antlr.Token;
import org.netbeans.modules.cnd.api.model.CsmClassifier;
import org.netbeans.modules.cnd.api.model.CsmDeclaration.Kind;
import org.netbeans.modules.cnd.api.model.CsmEnumerator;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmObject;
import org.netbeans.modules.cnd.api.model.CsmOffsetable;
import org.netbeans.modules.cnd.api.model.CsmType;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmReferenceKind;
import org.netbeans.modules.cnd.apt.support.APTPreprocHandler;
import org.netbeans.modules.cnd.apt.support.APTPreprocHandler.State;
import org.netbeans.modules.cnd.apt.support.APTToken;
import org.netbeans.modules.cnd.apt.utils.APTUtils;
import org.netbeans.modules.cnd.modelimpl.csm.ClassImpl;
import org.netbeans.modules.cnd.modelimpl.csm.ClassImpl.ClassBuilder;
import org.netbeans.modules.cnd.modelimpl.csm.CsmObjectBuilder;
import org.netbeans.modules.cnd.modelimpl.csm.EnumImpl;
import org.netbeans.modules.cnd.modelimpl.csm.EnumImpl.EnumBuilder;
import org.netbeans.modules.cnd.modelimpl.csm.EnumeratorImpl.EnumeratorBuilder;
import org.netbeans.modules.cnd.modelimpl.csm.TypeFactory;
import org.netbeans.modules.cnd.modelimpl.csm.NamespaceDefinitionImpl.NamespaceBuilder;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.debug.TraceFlags;
import org.netbeans.modules.cnd.modelimpl.parser.ParserProviderImpl.Antlr3NewCppParser.MyToken;
import org.netbeans.modules.cnd.modelimpl.parser.generated.CPPTokenTypes;
import org.netbeans.modules.cnd.modelimpl.parser.symtab.*;
import org.openide.util.CharSequences;

/**
 * @author Nikolay Krasilnikov (nnnnnk@netbeans.org)
 */
public class CppParserAction3Impl implements CppParserAction3 {

    CppParserAction orig;
    
    private final SymTabStack globalSymTab;
    private Pair currentContext;
    private final Deque<Pair> contexts;
    private static final class Pair {
        final Map<Integer, CsmObject> objects = new HashMap<Integer, CsmObject>();
        final FileImpl file;

        public Pair(CsmFile file) {
            this.file = (FileImpl)file;
        }
        
    }

    CppParserBuilderContext builderContext;
    
    public CppParserActionImpl(FileImpl startFile) {
        this.contexts = new ArrayDeque<Pair>();
        currentContext = new Pair(startFile);
        this.contexts.push(currentContext);
        this.globalSymTab = createGlobal();
        this.builderContext = new CppParserBuilderContext();
    }
    
    @Override
    public void enum_declaration(Token token) {        
        //System.out.println("enum_declaration " + ((APTToken)token).getOffset());
        
        EnumBuilder enumBuilder = new EnumBuilder();
        enumBuilder.setFile(currentContext.file);
        if(token instanceof APTToken) {
            enumBuilder.setStartOffset(((APTToken)token).getOffset());
        }
        builderContext.push(enumBuilder);
    }

    @Override
    public void enum_name(Token token) {
        orig.enum_name(((MyToken)token).t);
    }

    @Override
    public void enum_body(Token token) {
        orig.enum_body(((MyToken)token).t);
    }
    
    @Override
    public void enumerator(Token token) {
        EnumBuilder enumBuilder = builderContext.getEnumBuilder();
        
        APTToken aToken = (APTToken) token;
        final CharSequence name = aToken.getTextID();
        SymTabEntry enumeratorEntry = globalSymTab.lookupLocal(name);
        if (enumeratorEntry == null) {
            enumeratorEntry = globalSymTab.enterLocal(name);
//            enumeratorEntry.setAttribute(CppAttributes.SYM_TAB, globalSymTab.getLocal());
        } else {
            // ERROR redifinition
        }
        if(enumBuilder != null) {
            EnumeratorBuilder builder2 = new EnumeratorBuilder();
            builder2.setName(name);
            builder2.setFile(currentContext.file);
            builder2.setStartOffset(aToken.getOffset());
            builder2.setEndOffset(aToken.getEndOffset());
            enumBuilder.addEnumerator(builder2);
        }
    }
    
    @Override
    public void end_enum_body(Token token) {
        orig.end_enum_body(((MyToken)token).t);
    }

    @Override
    public void end_enum_declaration(Token token) {
        EnumBuilder enumBuilder = builderContext.getEnumBuilder();
        
        EnumImpl e = enumBuilder.create(true);
        if(e != null) {
            currentContext.objects.put(e.getStartOffset(), e);
            SymTabEntry enumEntry = globalSymTab.lookupLocal(e.getName());
            enumEntry.setAttribute(CppAttributes.DEFINITION, e);
            for (CsmEnumerator csmEnumerator : e.getEnumerators()) {
                SymTabEntry enumeratorEntry = globalSymTab.lookupLocal(csmEnumerator.getName());
                assert enumeratorEntry != null;
                enumeratorEntry.setAttribute(CppAttributes.DEFINITION, csmEnumerator);
            }
        }

    @Override
    public void class_declaration(Token token) {
        ClassBuilder classBuilder = new ClassBuilder();
        classBuilder.setParent(builderContext.top());
        classBuilder.setFile(currentContext.file);
        if(token instanceof APTToken) {
            classBuilder.setStartOffset(((APTToken)token).getOffset());
        }
        builderContext.push(classBuilder);
    }

    @Override
    public void class_kind(Token token) {
        orig.class_kind(((MyToken)token).t);
    }
    
    @Override
    public void class_name(Token token) {
        orig.class_name(((MyToken)token).t);
    }

    @Override
    public void class_body(Token token) {
        orig.class_body(((MyToken)token).t);
    }

    @Override
    public void end_class_body(Token token) {
        orig.end_class_body(((MyToken)token).t);
    }
    
    @Override
    public void end_class_declaration(Token token) {
        orig.end_class_declaration(((MyToken)token).t);
    }

            ClassImpl cls = classBuilder.create();
            if(cls != null) {
                currentContext.objects.put(cls.getStartOffset(), cls);
                SymTabEntry classEntry = globalSymTab.lookupLocal(cls.getName());
                if(classEntry != null) {
                    classEntry.setAttribute(CppAttributes.DEFINITION, cls);
                } else {
//                    System.out.println("classEntry is empty " + cls);
                }
            }
            builderContext.pop();
        }
    }    

    @Override
    public void namespace_declaration(Token token) {
        NamespaceBuilder nsBuilder = new NamespaceBuilder();
        nsBuilder.setParentNamespace(builderContext.getNamespaceBuilderIfExist());
        nsBuilder.setFile(currentContext.file);
        if(token instanceof APTToken) {
            nsBuilder.setStartOffset(((APTToken)token).getOffset());
        }
        builderContext.push(nsBuilder);
    }

    @Override
    public void namespace_name(Token token) {
        orig.namespace_name(((MyToken)token).t);
    }
    
    @Override
    public void namespace_body(Token token) {
        orig.namespace_body(((MyToken)token).t);
    }

    @Override
    public void end_namespace_body(Token token) {
        orig.end_namespace_body(((MyToken)token).t);
    }

    @Override
    public void end_namespace_declaration(Token token) {
        orig.end_namespace_declaration(((MyToken)token).t);
    }
    
    @Override
    public void compound_statement(Token token) {
        orig.compound_statement(((MyToken)token).t);
    }

    @Override
    public void end_compound_statement(Token token) {
        orig.end_compound_statement(((MyToken)token).t);
    }

    @Override
    public void id(Token token) {
        orig.id(((MyToken)token).t);
    }
    
    @Override
    public void simple_type_id(Token token) {
        orig.simple_type_id(((MyToken)token).t);
    }
            
            if(token instanceof APTToken && CsmKindUtilities.isClassifier(def)) {
                CsmType type = TypeFactory.createSimpleType((CsmClassifier)def, currentContext.file, ((APTToken)token).getOffset(), ((APTToken)token).getEndOffset());
                currentContext.objects.put(type.getStartOffset(), type);
            }
            
        }
        
    }

    @Override
    public boolean isType(String name) {
        return orig.isType(name);
    }

    @Override
    public void onInclude(CsmFile inclFile, APTPreprocHandler.State stateBefore) {
        if (TraceFlags.PARSE_HEADERS_WITH_SOURCES) {
            assert inclFile instanceof FileImpl;
            ((FileImpl)inclFile).parseOnInclude(stateBefore, this);
        }
    }
     
    @Override
    public void pushFile(CsmFile file) {
        this.contexts.push(currentContext);
        currentContext = new Pair(file);
    }

    @Override
    public CsmFile popFile() {
        assert !contexts.isEmpty();
        CsmFile out = currentContext.file;
        currentContext = contexts.pop();
        return out;
    }

    @Override
    public Map<Integer, CsmObject> getObjectsMap() {
        return currentContext.objects;
    }
    
    private SymTabStack createGlobal() {
        SymTabStack out = SymTabStack.create();
        // TODO: need to push symtab for predefined types
        
    }

    private static final boolean TRACE = false;
    private void addReference(Token token, final CsmObject definition, final CsmReferenceKind kind) {
        if (definition == null) {
//            assert false;
            if (TRACE) System.err.println("no definition for " + token + " in " + currentContext.file);
            return;
        }
        assert token instanceof APTToken : "token is incorrect " + token;
        if (APTUtils.isMacroExpandedToken(token)) {
            if (TRACE) System.err.println("skip registering macro expanded " + token + " in " + currentContext.file);
            return;
        }
        APTToken aToken = (APTToken) token;
        final CharSequence name = aToken.getTextID();
        final int startOffset = aToken.getOffset();
        final int endOffset = aToken.getEndOffset();
        CsmReference ref = new CsmReference() {

            @Override
            public CsmReferenceKind getKind() {
                return kind;
            }

            @Override
            public CsmObject getReferencedObject() {
                return definition;
            }

            @Override
            public CsmObject getOwner() {
                return null;
            }

            @Override
            public CsmFile getContainingFile() {
                return currentContext.file;
            }

            @Override
            public int getStartOffset() {
                return startOffset;
            }

            @Override
            public int getEndOffset() {
                return endOffset;
            }

            @Override
            public CsmOffsetable.Position getStartPosition() {
                throw new UnsupportedOperationException("Not supported yet."); // NOI18N
            }

            @Override
            public CsmOffsetable.Position getEndPosition() {
                throw new UnsupportedOperationException("Not supported yet."); // NOI18N
            }

            @Override
            public CharSequence getText() {
                return name;
            }

            @Override
            public CsmObject getClosestTopLevelObject() {
                return null;
            }
        };
        currentContext.file.addReference(ref, definition);
    }   
}
