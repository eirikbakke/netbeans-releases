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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.editor.index;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.IndexDocument;
import org.netbeans.modules.gsf.api.IndexDocumentFactory;
import org.netbeans.modules.php.editor.PHPLanguage;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.ExpressionStatement;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Identifier;
import org.netbeans.modules.php.editor.parser.astnodes.Program;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Statement;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Index Ruby structure into the persistent store for retrieval by
 * {@link JsIndex}.
 * 
 * @todo Index methods as func.in and then distinguish between exact completion and multi-completion.
 * @todo Ensure that all the stub files are compileable!
 * @todo Should I perhaps store globals and functions using the same query prefix (since I typically
 *    have to search for both anyway) ? Or perhaps not - not when doing inherited checks...
 * @todo Index file inclusion dependencies! (Uh oh - that means I -do- have to do models for HTML, etc. right?
 *     Or can I perhaps only compute that stuff live?
 * @todo Use the JsCommentLexer to pull out relevant attributes -- @private and such -- and set these
 *     as function attributes.
 * @todo There are duplicate elements -- why???
 * 
 * @author Tor Norbye
 */
public class PHPIndexer implements Indexer {
    static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");
    
    // I need to be able to search several things:
    // (1) by function root name, e.g. quickly all functions that start
    //    with "f" should find unknown.foo.
    // (2) by namespace, e.g. I should be able to quickly find all
    //    "foo.bar.b*" functions
    // (3) constructors
    // (4) global variables, preferably in the same way
    // (5) extends so I can do inheritance inclusion!

    // Solution: Store the following:
    // class:name for each class
    // extend:old:new for each inheritance? Or perhaps do this in the class entry
    // fqn: f.q.n.function/global;sig; for each function
    // base: function;fqn;sig
    // The signature should look like this:
    // ;flags;;args;offset;docoffset;browsercompat;types;
    // (between flags and args you have the case sensitive name for flags)

    static final String FIELD_FQN = "fqn"; //NOI18N
    static final String FIELD_BASE = "base"; //NOI18N
    static final String FIELD_EXTEND = "extend"; //NOI18N
    static final String FIELD_CLASS = "clz"; //NOI18N
    static final String FIELD_CONST = "constant"; //NOI18N
    
    public boolean isIndexable(ParserFile file) {
        // Cannot call file.getFileObject().getMIMEType() here for several reasons:
        // (1) when cleaning up the index for deleted files, file.getFileObject().getMIMEType()
        //   may return "content/unknown", and in some cases, file.getFileObject() returns null
        // (2) file.getFileObject() can be expensive during startup indexing when we're
        //   rapidly scanning through lots of directories to determine which files are
        //   indexable. This is done using the java.io.File API rather than the more heavyweight
        //   FileObject, and each file.getFileObject() will perform a FileUtil.toFileObject() call.
        // Since the mime resolver for PHP is simple -- it's just based on the file extension,
        // we perform the same check here:
        //if (PHPLanguage.PHP_MIME_TYPE.equals(file.getFileObject().getMIMEType())) { // NOI18N
        if (PHPLanguage.PHP_FILE_EXTENSION.equals(file.getExtension())) { // NOI18N

            // Skip Gem versions; Rails copies these files into the project anyway! Don't want
            // duplicate entries.
//            if (PREINDEXING) {
//                try {
//                    //if (file.getRelativePath().startsWith("action_view")) {
//                    if (file.getFileObject().getURL().toExternalForm().indexOf("/gems/") != -1) {
//                        return false;
//                    }
//                } catch (FileStateInvalidException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
            return true;
        }
        
        return false;
    }

    public String getPersistentUrl(File file) {
        String url;
        try {
            url = file.toURI().toURL().toExternalForm();
            // Make relative URLs for urls in the libraries
            return PHPIndex.getPreindexUrl(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return file.getPath();
        }

    }

    public List<IndexDocument> index(ParserResult result, IndexDocumentFactory factory) throws IOException {
        PHPParseResult r = (PHPParseResult)result;
        
        TreeAnalyzer analyzer = new TreeAnalyzer(r, factory);
        analyzer.analyze();
        
        return analyzer.getDocuments();
    }
    
    public String getIndexVersion() {
        return "0.1.4"; // NOI18N
    }

    public String getIndexerName() {
        return "php"; // NOI18N
    }
    
    private static class TreeAnalyzer {
        private final ParserFile file;
        private String url;
        private final PHPParseResult result;
        private final BaseDocument doc;
        private IndexDocumentFactory factory;
        private List<IndexDocument> documents = new ArrayList<IndexDocument>();
        
        private TreeAnalyzer(PHPParseResult result, IndexDocumentFactory factory) {
            this.result = result;
            this.file = result.getFile();
            this.factory = factory;

            FileObject fo = file.getFileObject();

            if (fo != null) {
                this.doc = NbUtilities.getBaseDocument(fo, true);
            } else {
                this.doc = null;
            }

            try {
                url = file.getFileObject().getURL().toExternalForm();

                // Make relative URLs for urls in the libraries
                url = PHPIndex.getPreindexUrl(url);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }

        List<IndexDocument> getDocuments() {
            return documents;
        }

        public void analyze() throws IOException {
            
            IndexDocument document = factory.createDocument(40); // TODO - measure!
            documents.add(document);
            
            Program program = result.getProgram();
            
            for (Statement statement : program.getStatements()){
                if (statement instanceof FunctionDeclaration){
                    indexFunction((FunctionDeclaration)statement, document);
                } else if (statement instanceof ExpressionStatement){
                    indexConstant(statement, document);
                } else if (statement instanceof ClassDeclaration){
                    indexClass((ClassDeclaration)statement, document);
                }
            }
            
            
//            AnalysisResult ar = result.getStructure();
//            List<?extends AstElement> children = ar.getElements();
//
//            if ((children == null) || (children.size() == 0)) {
//                return;
//            }
//
//            IndexDocument document = factory.createDocument(40); // TODO - measure!
//            documents.add(document);
//
//            // Add the fields, etc.. Recursively add the children classes or modules if any
//            for (AstElement child : children) {
//                ElementKind childKind = child.getKind();
//                if (childKind == ElementKind.CONSTRUCTOR || childKind == ElementKind.METHOD) {
//                    String signature = computeSignature(child);
//                    indexFuncOrProperty(child, document, signature);
//                    String name = child.getName();
//                    if (Character.isUpperCase(name.charAt(0))) {
//                        indexClass(child, document, signature);
//                    }
//                } else if (childKind == ElementKind.GLOBAL || childKind == ElementKind.PROPERTY) {
//                    indexFuncOrProperty(child, document, computeSignature(child));
//                } else if (childKind == ElementKind.CLASS) {
//                    // Nothing to be stored; inferred from methods or properties  
//                } else {
//                    assert false : childKind;
//                }
//                // XXX what about fields, constants, attributes?
//                
//                assert child.getChildren().size() == 0;
//            }
//
//            Map<String,String> classExtends = ar.getExtendsMap();
//            if (classExtends != null) {
//                for (Map.Entry<String,String> entry : classExtends.entrySet()) {
//                    String clz = entry.getKey();
//                    String superClz = entry.getValue();
//                    document.addPair(FIELD_EXTEND, clz.toLowerCase() + ";" + clz + ";" + superClz, true); // NOI18N
//                }
//            }
        }

        private void indexClass(ClassDeclaration classDeclaration, IndexDocument document) {
            StringBuilder signature = new StringBuilder();
            signature.append(classDeclaration.getName().getName() + ";"); //NOI18N
            document.addPair(FIELD_CLASS, signature.toString(), true);
        }

        private String computeSignature(AstElement element) {
            // Look up compatibility
//            int index = IndexedElement.FLAG_INDEX;
//            
//            int docOffset = getDocumentationOffset(element);
//            
//            String compatibility = "";
//            if (file.getNameExt().startsWith("stub_")) {
//                int astOffset = element.getNode().getSourceStart();
//                int lexOffset = astOffset;
//                TranslatedSource source = result.getTranslatedSource();
//                if (source != null) {
//                    lexOffset = source.getLexicalOffset(astOffset);
//                }
//            }
//
//            assert index == IndexedElement.FLAG_INDEX;
//            StringBuilder sb = new StringBuilder();
//            int flags = IndexedElement.getFlags(element);
//            if (docOffset != -1) {
//                flags = flags | IndexedElement.DOCUMENTED;
//            }
//            sb.append(IndexedElement.encode(flags));
//            
//            // Parameters
//            sb.append(";");
//            index++;
//            assert index == IndexedElement.ARG_INDEX;
//            if (element instanceof FunctionAstElement) {
//                FunctionAstElement func = (FunctionAstElement)element;            
//
//                int argIndex = 0;
//                for (String param : func.getParameters()) {
//                    if (argIndex == 0 && "$super".equals(param)) { // NOI18N
//                        // Prototype inserts these as the first param to handle inheritance/super
//                        argIndex++;
//                        continue;
//                    } 
//                    if (argIndex > 0) {
//                        sb.append(",");
//                    }
//                    sb.append(param);
//                    argIndex++;
//                }
//            }
//
//            // Node offset
//            sb.append(';');
//            index++;
//            assert index == IndexedElement.NODE_INDEX;
//            sb.append("0");
//            //sb.append(IndexedElement.encode(element.getNode().getSourceStart()));
//            
//            // Documentation offset
//            sb.append(';');
//            index++;
//            assert index == IndexedElement.DOC_INDEX;
//            if (docOffset != -1) {
//                sb.append(IndexedElement.encode(docOffset));
//            }
//
//            // Browser compatibility
//            sb.append(";");
//            index++;
//            assert index == IndexedElement.BROWSER_INDEX;
//            sb.append(compatibility);
//            
//            // Types
//            sb.append(";");
//            index++;
//            assert index == IndexedElement.TYPE_INDEX;
//            if (element.getKind() == ElementKind.GLOBAL) {
//                String type = ((GlobalAstElement)element).getType();
//                if (type != null) {
//                    sb.append(type);
//                }
//            }
//            // TBD
//
//            sb.append(';');
//            String signature = sb.toString();
//            return signature;
            return null;
        }

//        private void indexFuncOrProperty(FunctionDeclaration element, IndexDocument document, String signature) {
//            String in = null;//element.getIn();
//            String name = element.getName();
//            StringBuilder base = new StringBuilder();
//            base.append(name.toLowerCase());
//            base.append(';');                
//            if (in != null) {
//                base.append(in);
//            }
//            base.append(';');
//            base.append(name);
//            base.append(';');
//            base.append(signature);
//            document.addPair(FIELD_BASE, base.toString(), true);
//            
//            StringBuilder fqn = new StringBuilder();
//            if (in != null && in.length() > 0) {
//                fqn.append(in.toLowerCase());
//                fqn.append('.');
//            }
//            fqn.append(name.toLowerCase());
//            fqn.append(';');
//            fqn.append(';');
//            if (in != null && in.length() > 0) {
//                fqn.append(in);
//                fqn.append('.');
//            }
//            fqn.append(name);
//            fqn.append(';');
//            fqn.append(signature);
//            document.addPair(FIELD_FQN, fqn.toString(), true);
//        }
        
        private int getDocumentationOffset(AstElement element) {
//            int offset = element.getNode().getSourceStart();
//            try {
//                if (offset > doc.getLength()) {
//                    return -1;
//                }
//                offset = Utilities.getRowStart(doc, offset);
//            } catch (BadLocationException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            OffsetRange range = LexUtilities.getCommentBlock(doc, offset, true);
//            if (range != OffsetRange.NONE) {
//                return range.getStart();
//            } else {
//                return -1;
//            }
            return -1;
        }

        private void indexConstant(Statement statement, IndexDocument document) {
            ExpressionStatement exprStmt = (ExpressionStatement) statement;
            Expression expr = exprStmt.getExpression();

            if (expr instanceof FunctionInvocation) {
                FunctionInvocation invocation = (FunctionInvocation) expr;

                if (invocation.getFunctionName().getName() instanceof Identifier) {
                    Identifier id = (Identifier) invocation.getFunctionName().getName();

                    if ("define".equals(id.getName())) {
                        if (invocation.getParameters().size() == 2) {
                            Expression paramExpr = invocation.getParameters().get(0);

                            if (paramExpr instanceof Scalar) {
                                String constName = ((Scalar) paramExpr).getStringValue();
                                char firstChar = constName.charAt(0);
                                
                                // check if const name is really quoted
                                if (firstChar == constName.charAt(constName.length() - 1) 
                                        && firstChar == '\'' || firstChar == '\"') {
                                    String defineVal = PHPIndex.dequote(constName);

                                    document.addPair(FIELD_CONST, defineVal + ";" + invocation.getStartOffset(), false);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void indexFunction(FunctionDeclaration functionDeclaration, IndexDocument document) {
            StringBuilder signature = new StringBuilder();
            signature.append(functionDeclaration.getFunctionName().getName() + ";");

            for (Iterator<FormalParameter> it = functionDeclaration.getFormalParameters().iterator(); it.hasNext();) {

                FormalParameter param = it.next();
                Expression paramNameExpr = param.getParameterName();
                String paramName = null;

                if (paramNameExpr instanceof Variable) {
                    Variable var = (Variable) paramNameExpr;
                    Identifier id = (Identifier) var.getName();
                    paramName = id.getName();
                }

                signature.append(paramName);

                if (it.hasNext()) {
                    signature.append(",");
                }
            }
            signature.append(";");
            signature.append(functionDeclaration.getStartOffset());

            document.addPair(FIELD_BASE, signature.toString(), true);
        }
    }
    
    public File getPreindexedData() {
        return null;
    }

    private static FileObject preindexedDb;

    /** For testing only */
    public static void setPreindexedDb(FileObject preindexedDb) {
        PHPIndexer.preindexedDb = preindexedDb;
    }
    
    public FileObject getPreindexedDb() {
        return null;
    }
    
    /**
     * {@inheritDoc}
     * 
     * As the above documentation states, this is a temporary solution / hack
     * for 6.1 only.
     */
    public boolean acceptQueryPath(String url) {
        // Filter out JavaScript stuff
        return url.indexOf("jsstubs") == -1 && // NOI18N
                // Filter out Ruby stuff
                url.indexOf("/ruby2/") == -1 &&  // NOI18N
                url.indexOf("/gems/") == -1 &&  // NOI18N
                url.indexOf("lib/ruby/") == -1; // NOI18N
    }
}
