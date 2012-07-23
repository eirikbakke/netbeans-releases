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
package org.netbeans.modules.javafx2.editor.completion.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.text.Document;
import junit.framework.TestSuite;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.javafx2.editor.FXMLCompletionTestBase;
import org.netbeans.modules.javafx2.editor.completion.impl.ErrorMark;
import org.netbeans.modules.javafx2.editor.completion.impl.XMLLexerParserTest;
import org.netbeans.modules.javafx2.editor.completion.impl.XmlLexerParser;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author sdedic
 */
public class FxmlBuilderTest extends FXMLCompletionTestBase {
    DataObject sourceDO;
    Document document;
    TokenHierarchy hierarchy;
    FxModelBuilder builder;
    String fname;
    
    public FxmlBuilderTest(String testName) {
        super(testName);
    }
    
//    public static TestSuite suite() {
//        TestSuite ts = new TestSuite();
//        ts.addTest(new FxmlBuilderTest("testIncompletePi"));
//        return ts;
//    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File dataDir = getDataDir();
        fname = getName().replace("test", "");
        File f = new File(dataDir, FxmlBuilderTest.class.getName().
                replaceAll("\\.", "/") + "/" + fname + ".fxml");
        
        File w = new File(getWorkDir(), f.getName());
        InputStream is = new FileInputStream(f);
        OutputStream os = new FileOutputStream(w);
        FileUtil.copy(is, os);
        os.close();
        is.close();
        FileObject fo = FileUtil.toFileObject(w);
        sourceDO = DataObject.find(fo);
        document = ((EditorCookie)sourceDO.getCookie(EditorCookie.class)).openDocument();
        hierarchy = TokenHierarchy.get(document);
    }

    public void testBaseLoad() throws Exception {
        XmlLexerParser parser = new XmlLexerParser(hierarchy);
        builder = new FxModelBuilder();
        parser.setContentHandler(builder);
        parser.parse();
        
        PrintVisitor v = new PrintVisitor();
        builder.getModel().accept(v);
        
    }

    private static final String PADDING = "                ";
    
    private static class PrintVisitor extends FxNodeVisitor.ModelTraversal {
        private StringBuilder out = new StringBuilder();
        int indent = 0;
        
        @Override
        protected void scan(FxNode node) {
            indent += 2;
            if (node != null) {
                String s = String.format("[%d - %d]  ", node.i().getStart(), node.i().getEnd());
                out.append(s).append(PADDING, 0, 16 - s.length());
            }
            super.scan(node);
            indent -= 2;
        }
        
        private StringBuilder i() {
            for (int i = 0; i < indent; i++) {
                out.append(" ");
            }
            return out;
        }

        @Override
        public void visitInstance(FxNewInstance decl) {
            i().append(String.format("instance: id=%s, className=%s\n",
                    decl.getId(), decl.getSourceName()));
            super.visitInstance(decl);
        }

        @Override
        public void visitPropertySetter(PropertySetter p) {
            i().append(String.format(
                "setter: name=%s, content=%s\n", p.getName(), p.getContent()
            ));
            super.visitPropertySetter(p);
        }

        @Override
        public void visitLanguage(LanguageDecl decl) {
            i().append(String.format(
                "language: %s\n", decl.getLanguage()
            ));
            super.visitLanguage(decl);
        }

        @Override
        public void visitImport(ImportDecl decl) {
            i().append(String.format(
                "import: name=%s, wildcard=%b\n", decl.getImportedName(), decl.isWildcard()
            ));
            super.visitImport(decl);
        }

        @Override
        public void visitMapProperty(MapProperty p) {
            i().append(String.format(
                "map: name=%s, content=%s\n", p.getName(), p.getValueMap()
            ));
            super.visitMapProperty(p);
        }

        @Override
        public void visitStaticProperty(StaticProperty p) {
            i().append(String.format(
                "attached: name=%s, source=%s, content=%s\n", p.getName(), p.getSourceClassName(), p.getContent()
            ));
            super.visitStaticProperty(p);
        }
        
    }


    /**
     * Tests recovery in half-written processing instructions
     * @throws Exception 
     */
    public void testIncompletePi() throws Exception {
        defaultTestContents();
    }
    
    /**com
     * Tests recovery when writing attributes.
     * 
     * @throws Exception Test
     */
    public void testBrokenAttributes() throws Exception {
        defaultTestContents();
    }

    public void testBrokenElements() throws Exception {
        defaultTestContents();
    }
    
    public void testBrokenHierarchy() throws Exception {
        defaultTestContents();
    }

    private void assertContents(StringBuilder sb) throws IOException {
        File out = new File(getWorkDir(), fname + ".parsed");
        FileWriter wr = new FileWriter(out);
        wr.append(sb);
        wr.close();
        
        assertFile(out, getGoldenFile(fname + ".pass"), new File(getWorkDir(), fname + ".diff"));
    }
    

    private void defaultTestContents() throws Exception {
        XmlLexerParser parser = new XmlLexerParser(hierarchy);
        builder = new FxModelBuilder();
        parser.setContentHandler(builder);
        parser.parse();

        PrintVisitor v = new PrintVisitor();
        builder.getModel().accept(v);
        
        StringBuilder sb = v.out;
        sb.append("\n\n");
        for (ErrorMark em : parser.getErrors()) {
            sb.append(em).append("\n");
        }
        for (ErrorMark em : builder.getErrors()) {
            sb.append(em).append("\n");
        }
        
        assertContents(v.out);
    }
    
}
