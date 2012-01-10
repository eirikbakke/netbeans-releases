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
package org.netbeans.modules.javascript2.editor.model.impl;

import java.util.Collections;
import java.util.Iterator;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.javascript2.editor.JsTestBase;
import org.netbeans.modules.javascript2.editor.model.*;
import org.netbeans.modules.javascript2.editor.parser.JsParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;

/**
 *
 * @author Petr Pisl
 */
public class ModelTest extends JsTestBase {
    
    public ModelTest(String testName) {
        super(testName);
    }
        
    private Model getModel(String file) throws Exception {
        final Model[] globals = new Model[1];
        Source source = getTestSource(getTestFile(file));
        
        ParserManager.parse(Collections.singleton(source), new UserTask() {
            public @Override void run(ResultIterator resultIterator) throws Exception {
                JsParserResult parameter = (JsParserResult) resultIterator.getParserResult();
                Model model = parameter.getModel();
                globals[0] = model;
            }
        });        
        return globals[0];
    }
  
    
    
    public void testObjectName01() throws Exception {
        Model model = getModel("testfiles/model/objectNames01.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        JsObject ridic = global.getPropery("Ridic");
        assertEquals("Ridic", ridic.getDeclarationName().getName());
        assertEquals("Ridic", ridic.getName());
        assertEquals(3, ridic.getProperties().size());
    }
    
    public void testMethodsInFunction() throws Exception {
        Model model = getModel("testfiles/model/objectNames01.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        JsObject ridic = global.getPropery("Ridic");
        JsObject method = ridic.getPropery("getName");
        assertEquals(JsElement.Kind.METHOD, method.getJSKind());
    }
    
    public void testMethodsOuterFunction() throws Exception {
        Model model = getModel("testfiles/model/objectMethods01.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        JsObject ridic = global.getPropery("Ridic");
        JsObject method = ridic.getPropery("prototype").getPropery("getInfo");
        assertEquals(JsElement.Kind.METHOD, method.getJSKind());
        assertFalse(method.getModifiers().contains(Modifier.STATIC));
    }
    
    public void testStaticMethod01() throws Exception {
        Model model = getModel("testfiles/model/staticMethods01.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        JsObject ridic = global.getPropery("Ridic");
        JsObject method = ridic.getPropery("getFormula");
        assertEquals(JsElement.Kind.METHOD, method.getJSKind());
        assertTrue(method.getModifiers().contains(Modifier.STATIC));
    }
    
    public void testParameters01() throws Exception {
        Model model = getModel("testfiles/model/simpleFunction.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        JsFunction function = (JsFunction)global.getPropery("createInfo");
        assertEquals(3, function.getParameters().size());
        final Iterator<? extends Identifier> iterator = function.getParameters().iterator();
        Identifier param = iterator.next();
        assertEquals("text", param.getName());
        param = iterator.next();
        assertEquals("name", param.getName());
        param = iterator.next();
        assertEquals("description", param.getName());
    }
    
    
    public void testVariables01() throws Exception {
        Model model = getModel("testfiles/model/variables01.js");
        assertNotNull(model);
        JsObject  global = model.getGlobalObject();
        assertEquals(6, global.getProperties().size());
        
        JsObject variable = global.getPropery("address");
        assertEquals("address", variable.getName());
        assertEquals(true, variable.isDeclared());
        assertEquals(JsElement.Kind.VARIABLE, variable.getJSKind());

        variable = global.getPropery("country");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.VARIABLE, variable.getJSKind());
       
        variable = global.getPropery("telefon");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.VARIABLE, variable.getJSKind());

        JsObject object = global.getPropery("formatter");
        //assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        // needs to be object at the end
        assertEquals(JsElement.Kind.VARIABLE, object.getJSKind());
        assertEquals(false, variable.isDeclared());
        
        JsObject address = global.getPropery("Address");
        assertEquals(JsElement.Kind.CONSTRUCTOR, address.getJSKind());
        assertEquals(true, address.isDeclared());
        assertEquals(5, address.getProperties().size());
        
        variable = address.getPropery("city");
        assertEquals(true, variable.isDeclared());
        assertEquals(JsElement.Kind.VARIABLE, variable.getJSKind());
        assertEquals(true, variable.getModifiers().contains(Modifier.PRIVATE));
        
        variable = address.getPropery("zip");
        assertEquals(true, variable.isDeclared());
        assertEquals(JsElement.Kind.VARIABLE, variable.getJSKind());
        assertEquals(true, variable.getModifiers().contains(Modifier.PRIVATE));

        variable = address.getPropery("id");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        assertEquals(false, variable.getModifiers().contains(Modifier.PRIVATE));
        assertEquals(true, variable.getModifiers().contains(Modifier.PUBLIC));
        
        variable = address.getPropery("street");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        assertEquals(false, variable.getModifiers().contains(Modifier.PRIVATE));
        assertEquals(true, variable.getModifiers().contains(Modifier.PUBLIC));
        
        variable = address.getPropery("print");
        assertEquals(true, variable.isDeclared());
        assertEquals(JsElement.Kind.METHOD, variable.getJSKind());
        assertEquals(false, variable.getModifiers().contains(Modifier.PRIVATE));
        assertEquals(true, variable.getModifiers().contains(Modifier.PUBLIC));

        JsObject myApp = global.getPropery("MyApp");
        assertEquals(JsElement.Kind.OBJECT, myApp.getJSKind());
        assertEquals(true, myApp.isDeclared());
        assertEquals(1, myApp.getProperties().size());
        
        variable = myApp.getPropery("country");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        assertEquals(false, variable.getModifiers().contains(Modifier.PRIVATE));
        assertEquals(true, variable.getModifiers().contains(Modifier.PUBLIC));
        
    }
    
    
    public void testNamesapces01() throws Exception {
        Model model = getModel("testfiles/model/namespaces01.js");
        assertNotNull(model);
        
        JsObject  global = model.getGlobalObject();
        assertEquals(4, global.getProperties().size());
        
        JsObject object = global.getPropery("MyContext");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(true, object.isDeclared());
        assertEquals(3, object.getProperties().size());
        assertEquals(134, object.getOffset());
        
        JsObject variable = object.getPropery("id");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        variable = object.getPropery("test");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        object = object.getPropery("User");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(true, object.isDeclared());
        assertEquals(4, object.getProperties().size());
        assertEquals(180, object.getOffset());

        variable = object.getPropery("firstName");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        variable = object.getPropery("lastName");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());

        variable = object.getPropery("session");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        object = object.getPropery("Address");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(true, object.isDeclared());
        assertEquals(2, object.getProperties().size());
        assertEquals(278, object.getOffset());
        
        variable = object.getPropery("street");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        variable = object.getPropery("town");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());

        object = global.getPropery("Ns1");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(false, object.isDeclared());
        assertEquals(1, object.getProperties().size());
        
        object = object.getPropery("Ns2");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(false, object.isDeclared());
        assertEquals(1, object.getProperties().size());
        
        object = object.getPropery("Ns3");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(false, object.isDeclared());
        assertEquals(1, object.getProperties().size());
        
        variable = object.getPropery("fix");
        assertEquals(false, variable.isDeclared());
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
    }
    
    public void testProperties01() throws Exception {
        Model model = getModel("testfiles/model/property01.js");
        assertNotNull(model);
        
        JsObject  global = model.getGlobalObject();
        assertEquals(1, global.getProperties().size());
        
        JsObject object = global.getPropery("fruit");
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(true, object.isDeclared());
        assertEquals(3, object.getProperties().size());
        assertEquals(4, object.getOffset());
        
        JsObject variable = object.getPropery("color");
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        variable = object.getPropery("size");
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
        
        variable = object.getPropery("quality");
        assertEquals(JsElement.Kind.PROPERTY, variable.getJSKind());
    }
    
    public void testObjectLiterarThis() throws Exception {
        Model model = getModel("testfiles/model/kolo.js");
        assertNotNull(model);
        
        JsObject  global = model.getGlobalObject();
        assertEquals(3, global.getProperties().size());
        
        JsObject object = global.getPropery("Kolo");
        assertEquals(JsElement.Kind.CONSTRUCTOR, object.getJSKind());
        assertEquals(true, object.isDeclared());
        assertEquals(4, object.getProperties().size());
        
        object = object.getPropery("data");
        assertEquals(3, object.getProperties().size());
        assertEquals(JsElement.Kind.OBJECT, object.getJSKind());
        assertEquals(true, object.isDeclared());
    }
    
//    public void testPrivateMethod01() throws Exception {
//        Model model = getModel("testfiles/model/privateMethod.js");
//        assertNotNull(model);
//        
//        FileScope fScope = model.getFileScope();
//        assertEquals(3, fScope.getElements().size());
//        
//        ObjectScope object = (ObjectScope)ModelUtils.find(fScope.getElements(), JsElement.Kind.OBJECT, "MyClass");
//        assertEquals("MyClass", object.getName());
//        assertEquals(1, object.getElements().size());
//        FunctionScope constructor = (FunctionScope)ModelUtils.find(object.getElements(), JsElement.Kind.CONSTRUCTOR, "MyClass");
//        assertEquals(3, constructor.getElements().size());
//        FunctionScope method = (FunctionScope)ModelUtils.find(constructor.getElements(), JsElement.Kind.METHOD, "method1");
//        assertTrue(method.getModifiers().contains(Modifier.PUBLIC));
//        method = (FunctionScope)ModelUtils.find(constructor.getElements(), JsElement.Kind.METHOD, "method2");
//        assertTrue(method.getModifiers().contains(Modifier.PRIVATE));
//        
//        assertNotNull(ModelUtils.find(constructor.getElements(), JsElement.Kind.FIELD, "method2"));
//    }
}
