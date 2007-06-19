/*
* The contents of this file are subject to the terms of the Common Development
* and Distribution License (the License). You may not use this file except in
* compliance with the License.
*
* You can obtain a copy of the License at http://www.netbeans.org/cddl.html
* or http://www.netbeans.org/cddl.txt.
*
* When distributing Covered Code, include this CDDL Header Notice in each file
* and include the License file at http://www.netbeans.org/cddl.txt.
* If applicable, add the following below the CDDL Header, with the fields
* enclosed by brackets [] replaced by your own identifying information:
* "Portions Copyrighted [year] [name of copyright owner]"
*
* The Original Software is NetBeans. The Initial Developer of the Original
* Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
* Microsystems, Inc. All Rights Reserved.
*/ 

/*
 * PortTypeCustomization.java
 *
 * Created on January 31, 2006, 5:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.websvc.customization.model;

import org.netbeans.modules.xml.wsdl.model.ExtensibilityElement;


/**
 *
 * @author Roderico Cruz
 */
public interface PortTypeCustomization extends ExtensibilityElement{
    public static final String JAVA_CLASS_PROPERTY = "class";
    public static final String ENABLE_WRAPPER_STYLE_PROPERTY = "enableWrapperStyle";
    public static final String ENABLE_ASYNC_MAPPING_PROPERTY = "enableAsyncMapping";

    void setJavaClass(JavaClass javaClass);
    void removeJavaClass(JavaClass javaClass);
    JavaClass getJavaClass();
    
    void setEnableWrapperStyle(EnableWrapperStyle wrapperStyle);
    void removeEnableWrapperStyle(EnableWrapperStyle wrapperStyle);
    EnableWrapperStyle getEnableWrapperStyle();
    
    void setEnableAsyncMapping(EnableAsyncMapping async);
    void removeEnableAsyncMapping(EnableAsyncMapping async);
    EnableAsyncMapping getEnableAsyncMapping();
    

}
