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
package org.netbeans.modules.javascript2.editor.qaf.cc;

import java.awt.event.InputEvent;
import junit.framework.Test;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.modules.editor.CompletionJListOperator;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.modules.javascript2.editor.qaf.GeneralJavaScript;
import org.openide.util.Exceptions;

/**
 *
 * @author Vladimir Riha
 */
public class TestCC extends GeneralJavaScript {

    static final String TEST_BASE_NAME = "js2_";
    static String currentFile = "";
    static int name_iterator = 0;

    public TestCC(String args) {
        super(args);
    }

    public static Test suite() {
        return NbModuleSuite.create(
                NbModuleSuite.createConfiguration(TestCC.class).addTest(
                "createApplication",
                "testSimplePrototype",
                "testObjectFunction",
                "testObjectLiteral",
                "testPrototypeInheritance",
                "testObjectLiteral",
                "testIssue215394",
                "testIssue215393",
                "testAllCompletionSingleFile",
                "testAllCompletionMultipleFiles",
                "testCallAndApply").enableModules(".*").clusters(".*"));
    }

    public void createApplication() {
        startTest();
        TestCC.name_iterator++;
        createPhpApplication(TEST_BASE_NAME + name_iterator);
        endTest();
    }

    public void testSimplePrototype() {
        startTest();

        TestCC.currentFile = "cc.js";
        EditorOperator eo = createWebFile("cc", TEST_BASE_NAME + name_iterator, "JavaScript File");
        eo.setCaretPositionToLine(5);
        type(eo, "function Foo(){\n this.x=1; \n var foo = 2;");
        eo.setCaretPosition("}", false);
        type(eo, "\n Foo.prototype.add = function(i){\n this.x+=y;");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n obj = new Foo();\n obj.");

        try {
            waitScanFinished();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            evt.waitNoEvent(3000); // fallback
        }

        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(1000);

        
        CompletionInfo completion = getCompletion();
        String[] res = {"add", "x"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        String[] res2 = {"foo"};
        checkCompletionDoesntContainItems(cjo, res2);
        completion.listItself.hideAll();

        cleanFile(eo);
        endTest();
    }

    public void testPrototypeInheritance() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);

        eo.setCaretPositionToLine(1);
        type(eo, "var A = function(){\n this.value=1; ");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n A.prototype.constructor = A; \n A.prototype.test = function () {\n ");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n var B = function () {\n A.call(this);");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n B.prototype = new A; \n B.prototype.constructor = B; \n");
        type(eo, "B.prototype.test = function () {\n  A.prototype.test.call(this);");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n var b = new B(); \n");
        type(eo, "b.\n");// workaround for #215394
        eo.setCaretPosition("b.", false);
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        System.out.println(eo.getText());
        CompletionInfo completion = getCompletion();
        String[] res = {"test", "value"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();

        endTest();
    }

    public void testCallAndApply() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);

        eo.setCaretPositionToLine(1);
        type(eo, "function f(){\n alert(this.msg); \n");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n \n");
        eo.setCaretPositionToLine(eo.getLineNumber() - 1);
        type(eo, "f.");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"call", "apply"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();

        endTest();
    }

    public void testDOMReferences() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);

        eo.setCaretPositionToLine(1);
        type(eo, " document.");
        type(eo, "\n"); // workaround for #215394
        eo.setCaretPosition("document.", false);
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"firstChild", "removeChild"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();

        endTest();
    }

    public void testIssue215394() {
        try {
            startTest();

            EditorOperator eo = new EditorOperator(TestCC.currentFile);
            cleanFile(eo);

            eo.setCaretPositionToLine(1);
            type(eo, " document.");
            eo.typeKey(' ', InputEvent.CTRL_MASK);
            evt.waitNoEvent(100);

            CompletionInfo completion = getCompletion();
            CompletionJListOperator cjo = completion.listItself;
            assertTrue("", (cjo.getCompletionItems().size() > 2 ? true : false));
            completion.listItself.hideAll();

            endTest();
        } catch (Exception ex) {
            fail("Fail 215394 " + ex.getMessage());
        }
    }

    public void testObjectLiteral() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);
        type(eo, "var foo = {\n value:0,\nincrement: function(inc){\nthis.value += typeof inc === 'number' ? inc : 1;");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 2);
        type(eo, ";\n\n");
        eo.setCaretPositionToLine(eo.getLineNumber() - 1);
        type(eo, "foo.");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"value", "increment"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();


        endTest();
    }

    public void testObjectFunction() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);
        type(eo, "function Foo(param1){\n this.name = ");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        // cc for parameters
        CompletionInfo completion = getCompletion();
        String[] res = {"param1"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();

        type(eo, "param1;\n var pr = 1;\n this.start = function(){\n");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, ";\n function secret(){\n");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n ");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        // cc inside function
        completion = getCompletion();
        String[] res5 = {"name", "start", "pr", "param1", "secret"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res5);
        completion.listItself.hideAll();

        type(eo, "\n Foo.prototype.setName = function(n){\n this.");

        // cc inside function's prototype
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        completion = getCompletion();
        String[] res4 = {"name", "start", "setName"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res4);
        completion.listItself.hideAll();
        type(eo, "name;");

        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 2);
        type(eo, "\n\n");
        eo.setCaretPositionToLine(eo.getLineNumber() - 1);
        type(eo, "var o = new ");

        // constructor function
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        completion = getCompletion();
        String[] res6 = {"Foo"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res6);
        completion.listItself.hideAll();

        type(eo, "Foo();\n o.");
        // public variable & method & prototype
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        completion = getCompletion();
        String[] res2 = {"name", "start", "setName"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res2);
        completion.listItself.hideAll();

        // private variable & method
        String[] res3 = {"secret", "pr"};
        checkCompletionDoesntContainItems(cjo, res3);
        completion.listItself.hideAll();

        endTest();
    }

    public void testIssue215393() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);

        eo.setCaretPositionToLine(1);
        type(eo, "var panel = document.getElementById('panel'+course); \n");
        type(eo, " panel.\n");
        eo.setCaretPosition("panel.", false);// workaround for #215394
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"insertBefore"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionItems(cjo, res);
        completion.listItself.hideAll();

        endTest();
    }

    public void testAllCompletionSingleFile() {
        startTest();

        EditorOperator eo = new EditorOperator(TestCC.currentFile);
        cleanFile(eo);

        eo.setCaretPositionToLine(1);
        type(eo, "var aa = 1; \nvar bb = 2;\n function A(){\n");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n\n");
        eo.setCaretPositionToLine(eo.getLineNumber() - 1);
        type(eo, "var c = new A(); \n c.");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"aa", "bb"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionDoesntContainItems(cjo, res);

        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        completion = getCompletion();
        String[] res2 = {"aa", "bb"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res2);
        completion.listItself.hideAll();
        type(eo, "aa");
        eo.save();

        endTest();
    }

    public void testAllCompletionMultipleFiles() {
        startTest();
        TestCC.currentFile = "other.js";
        EditorOperator eo = createWebFile("other", TEST_BASE_NAME + name_iterator, "JavaScript File");
        cleanFile(eo);
        eo.setCaretPositionToLine(1);
        type(eo, "var cc = 1; \nvar dd = 2;\n function AA(){\n");
        eo.setCaretPositionToEndOfLine(eo.getLineNumber() + 1);
        type(eo, "\n\n");
        eo.setCaretPositionToLine(eo.getLineNumber() - 1);
        type(eo, "var ccc = new AA(); \n ccc.");
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        CompletionInfo completion = getCompletion();
        String[] res = {"aa", "cc"};
        CompletionJListOperator cjo = completion.listItself;
        checkCompletionDoesntContainItems(cjo, res);

        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);
        eo.typeKey(' ', InputEvent.CTRL_MASK);
        evt.waitNoEvent(100);

        completion = getCompletion();
        String[] res2 = {"aa", "bb", "cc", "dd"};
        cjo = completion.listItself;
        checkCompletionItems(cjo, res2);
        completion.listItself.hideAll();

        endTest();
    }
}
