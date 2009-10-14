/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
/*
 * Method.java
 *
 * Created on November 18, 2004, 11:51 AM
 */

package org.netbeans.modules.j2ee.sun.dd.api.ejb;

import org.netbeans.modules.j2ee.sun.dd.api.common.MethodParams;

/**
 *
 * @author  Nitya Doraisamy
 */
public interface Method extends org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean {

    public static final String DESCRIPTION = "Description";	// NOI18N
    public static final String METHOD_INTF = "MethodIntf";	// NOI18N
    public static final String METHOD_NAME = "MethodName";	// NOI18N
    public static final String METHOD_PARAMS = "MethodParams";	// NOI18N
    public static final String EJB_NAME = "EjbName";	// NOI18N
    
    /** Setter for description property
     * @param value property value
     */
    public void setDescription(java.lang.String value);
    /** Getter for description property.
     * @return property value
     */
    public java.lang.String getDescription();
    /** Setter for method-intf property
     * @param value property value
     */
    public void setMethodIntf(java.lang.String value);
    /** Getter for method-intf property.
     * @return property value
     */
    public java.lang.String getMethodIntf();
    /** Setter for method-name property
     * @param value property value
     */
    public void setMethodName(java.lang.String value);
    /** Getter for method-name property.
     * @return property value
     */
    public java.lang.String getMethodName();
    /** Setter for method-params property
     * @param value property value
     */
    public void setMethodParams(MethodParams value);
    /** Getter for method-params property.
     * @return property value
     */
    public MethodParams getMethodParams();
    
    public MethodParams newMethodParams(); 
    
    /** Setter for ejb-name property
     * @param value property value
     */
    public void setEjbName(java.lang.String value);
    /** Getter for ejb-name property.
     * @return property value
     */
    public java.lang.String getEjbName();
    
}
