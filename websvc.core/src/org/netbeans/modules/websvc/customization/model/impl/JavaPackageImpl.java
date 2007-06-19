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
 * JavaPackageImpl.java
 *
 * Created on February 3, 2006, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.websvc.customization.model.impl;


import org.netbeans.modules.websvc.customization.model.JAXWSQName;
import org.netbeans.modules.websvc.customization.model.JavaPackage;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.visitor.WSDLVisitor;
import org.w3c.dom.Element;

/**
 *
 * @author Roderico Cruz
 */
public class JavaPackageImpl extends JavaEntityImpl implements JavaPackage{
    
    /** Creates a new instance of JavaPackageImpl */
    public JavaPackageImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public JavaPackageImpl(WSDLModel model){
        this(model, createPrefixedElement(JAXWSQName.PACKAGE.getQName(), model));
    }

    public void accept(WSDLVisitor visitor) {
        visitor.visit(this);
    }
}
