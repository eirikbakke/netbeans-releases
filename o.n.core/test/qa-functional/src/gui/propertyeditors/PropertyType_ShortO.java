/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package gui.propertyeditors;

import org.netbeans.junit.NbTestSuite;

/**
 * Tests of Short Property Editor.
 *
 * @author  Marian.Mirilovic@Sun.Com
 */
public class PropertyType_ShortO extends PropertyEditorsTest {
    
    public String propertyName_L;
    public String propertyValue_L;
    public String propertyValueExpectation_L;
    
    public boolean waitDialog = false;
    
    /** Creates a new instance of PropertyType_ShortO */
    public PropertyType_ShortO(String testName) {
        super(testName);
    }
    
    
    public void setUp(){
        propertyName_L = "Short";
        super.setUp();
    }
    
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new PropertyType_ShortO("testByInPlace"));
        suite.addTest(new PropertyType_ShortO("testByInPlaceInvalid"));
        return suite;
    }
    
    public void testByInPlace(){
        propertyValue_L = "12345";
        propertyValueExpectation_L = propertyValue_L;
        waitDialog = false;
        setByInPlace(propertyName_L, propertyValue_L, true);
    }
    
    public void testByInPlaceInvalid(){
        propertyValue_L = "1.2345";
        propertyValueExpectation_L = propertyValue_L;
        waitDialog = true;
        setByInPlace(propertyName_L, propertyValue_L, false);
    }
    
    public void verifyPropertyValue(boolean expectation) {
        verifyExpectationValue(propertyName_L,expectation, propertyValueExpectation_L, propertyValue_L, waitDialog);
    }
    
    public void setCustomizerValue(){}
    public void verifyCustomizerLayout(){}
    
    /** Test could be executed internaly in Forte without XTest
     * @param args arguments from command line
     */
    public static void main(String[] args) {
        //junit.textui.TestRunner.run(new NbTestSuite(PropertyType_ShortO.class));
        junit.textui.TestRunner.run(suite());
    }
}
