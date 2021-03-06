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
package org.netbeans.modules.javafx2.editor.parser.processors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.modules.javafx2.editor.ErrorMark;
import org.netbeans.modules.javafx2.editor.ErrorReporter;
import org.netbeans.modules.javafx2.editor.completion.model.FxModel;
import org.netbeans.modules.javafx2.editor.completion.model.FxNewInstance;
import org.netbeans.modules.javafx2.editor.completion.model.FxNode;
import org.netbeans.modules.javafx2.editor.completion.model.FxNodeVisitor;
import org.netbeans.modules.javafx2.editor.completion.model.ImportDecl;
import org.netbeans.modules.javafx2.editor.completion.model.FxTreeUtilities;

import static org.netbeans.modules.javafx2.editor.parser.processors.Bundle.*;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;

/**
 * Collects information about imports and the imported
 * symbols for type evaluation and/or type completion.
 *
 * @author sdedic
 */
public final class ImportProcessor extends FxNodeVisitor.ModelTraversal {
    /**
     * Explicitly imported names. This list has precedence over star imports.
     */
    private Map<String, String>     importedNames = new HashMap<String, String>();
    
    /**
     * For each name introduced by * import, lists one or more package names.
     * The Object value contains String|Set&lt;String>.
     */
    private Map<String, Object>    packageNames = new HashMap<String, Object>();
    
    private Set<String>            allPackages = new HashSet<String>();
    
    /**
     * CompilationInfo used during computation.
     */
    private CompilationInfo info;
    
    private ErrorReporter   errors;
    
    private TokenHierarchy<?>  hierarchy;
    
    private ImportDecl    current;
    
    private FxTreeUtilities nodes;
    
    public ImportProcessor(TokenHierarchy<?> h, ErrorReporter errors, FxTreeUtilities nodes) {
        Parameters.notNull("h", h);
        this.hierarchy = h;
        this.errors = errors;
        this.nodes = nodes;
        
    }
    
    public void load(CompilationInfo info, FxModel source) {
        Parameters.notNull("info", info);
        this.info = info;
        try {
            // initialize java.lang
            handleWildcard("java.lang", false); // NOI18N
            source.accept(this);
        } finally {
            this.info = null;
        }
    }
    
    public Set<String> resolveTypeName(CompilationInfo info, String anyName) {
        int dot = anyName.indexOf('.');
        if (dot == -1) {
            // probably simple name, does not contain any dots
            return resolveName(anyName);
        }
        // try to interpret as a fully qualified name; it's not usual that 
        // people work with inner symbols of imported classes:
        TypeElement el = info.getElements().getTypeElement(anyName);
        if (el != null) {
            return Collections.singleton(el.getQualifiedName().toString());
        }
        // try to interpret the 1st part as a resolvable simple name:
        String firstPart = anyName.substring(0, dot);
        
        Set<String> resolved = resolveName(firstPart);
        if (resolved == null) {
            return null;
        } else if (resolved.size() == 1) {
            String resolvedClass = resolved.iterator().next();
            // add the rest to the resolved name
            String joined = resolvedClass + anyName.substring(dot);
            
            el = info.getElements().getTypeElement(joined);
            if (el != null) {
                return Collections.singleton(el.getQualifiedName().toString());
            } else {
                // while the outer class was resolved, the inner identifier not.
                return null;
            }
        }
        
        Set<String> result = new HashSet<String>();
        for (String prefix : resolved) {
            String joined = prefix + anyName.substring(dot);
            
            el = info.getElements().getTypeElement(joined);
            if (el != null) {
                result.add(el.getQualifiedName().toString());
            }
        }
        return result;
    }
    
    /**
     * Resolves a simple name, according to the imports.
     * Returns null, if the identifier is not found, single-item collection
     * that contains the resolved fully qualified name, or a collection of
     * alternative names, if the identifier is ambiguous.
     */
    @SuppressWarnings("unchecked")
    public Set<String> resolveName(String simpleName) {
        String res = importedNames.get(simpleName);
        if (res != null) {
            return Collections.singleton(res);
        }
            
        Object o = packageNames.get(simpleName);
        if (o instanceof String) {
            String n = (String)o;
            return Collections.singleton(n + "." + simpleName);
        }
        
        if (o == null) {
            return null;
        }
        Set<String> packs = (Set<String>)o;
        Set<String> result = new HashSet<String>();
        for (String pn : packs) {
            result.add(pn + "." + simpleName);
        }
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Returns the set of all imported packages
     */
    public Set<String> getImportedPackages() {
        return Collections.unmodifiableSet(allPackages);
    }
    
    @Override
    public void visitImport(ImportDecl decl) {
        this.current = decl;
        if (!decl.isWildcard()) {
            handleSingleImport(decl.getImportedName());
        } else {
            handleWildcard(decl.getImportedName(), true);
        }
    }
    
    @NbBundle.Messages({
        "# {0} - package name",
        "ERR_importPackageNotExists=Package {0} does not exist."
    })
    private void handleWildcard(String packName, boolean add) {
        PackageElement el = info.getElements().getPackageElement(packName);
        if (el == null) {
            if (current == null) {
                return;
            }
            int[] offsets = findPiContentOffsets(current);
            addError(
                new ErrorMark(offsets[0], offsets[1] - offsets[0], 
                "import-package-not-exists",
                ERR_importPackageNotExists(packName),
                packName)
            );
            return;
        }
        if (add) {
            allPackages.add(packName);
        }
        List<TypeElement> types = ElementFilter.typesIn(el.getEnclosedElements());

        for (TypeElement t : types) {
            addType(t.getSimpleName().toString(), packName);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void addType(String sn, String pack) {
        Object o = packageNames.get(sn);
        Collection<String> packs;
        
        if (o == null) {
            packageNames.put(sn, pack);
            return;
        } else if (o instanceof String) {
            packs = new HashSet<String>();
            packs.add((String)o);
            packageNames.put(sn, packs);
        } else if (o instanceof Collection) {
            packs = (Collection<String>)o;
        } else {
            throw new IllegalStateException(sn);
        }
        packs.add(pack);
    }
    
    private void addError(ErrorMark mark) {
        if (errors == null) {
            return;
        }
        errors.addError(mark);
    }

    @NbBundle.Messages({
        "# {0} - class name",
        "ERR_importIdentifierNotExists=Class {0} does not exist."
    })
    private void handleSingleImport(String name) {
        // check that the element actually exists
        TypeElement tel = info.getElements().getTypeElement(name);
        if (tel == null) {
            int[] offsets = findPiContentOffsets(current);
            addError(
                new ErrorMark(offsets[0], offsets[1] - offsets[0], 
                "import-identifier-not-exists",
                ERR_importIdentifierNotExists(name),
                name)
            );
        }
        int dotIndex = name.lastIndexOf('.');
        String simpleName = dotIndex == -1 ? name : name.substring(dotIndex + 1);
        importedNames.put(simpleName, name);
    }

    @SuppressWarnings("unchecked")
    private int[] findPiContentOffsets(FxNode node) {
        TokenSequence<XMLTokenId> seq = (TokenSequence<XMLTokenId>)hierarchy.tokenSequence();
        int start = -1;
        int end = -1;
        
        int s = nodes.positions(node).getStart();
        seq.move(s);
        boolean cont = true;
        while (cont && seq.moveNext()) {
            Token<XMLTokenId> token = seq.token();
            switch (token.id()) {
                case PI_TARGET:
                case PI_START:
                case WS:
                    break;
                case PI_CONTENT:
                    if (start == -1) {
                        start = seq.offset();
                    }
                    end = seq.offset() + token.length();
                    break;
                default:
                    cont = false;
            }
        }
        return new int[] { start, end };
    }

    @Override
    public void visitInstance(FxNewInstance decl) {
        // dummy. Stop processing.
    }
    
}
