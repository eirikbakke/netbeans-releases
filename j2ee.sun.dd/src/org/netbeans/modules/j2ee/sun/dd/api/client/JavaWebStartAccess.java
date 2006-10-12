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
 * JavaWebStartAccess.java
 *
 * Created on February 10, 2006, 11:28 AM
 *
 */

package org.netbeans.modules.j2ee.sun.dd.api.client;

/**
 *
 * @author Nitya Doraisamy
 */
public interface JavaWebStartAccess extends org.netbeans.modules.j2ee.sun.dd.api.CommonDDBean {
    
    public static final String CONTEXT_ROOT = "ContextRoot";	// NOI18N
    public static final String ELIGIBLE = "Eligible";	// NOI18N
    public static final String VENDOR = "Vendor";	// NOI18N
    
    public void setContextRoot(String value);
    
    public String getContextRoot();
    
    public void setEligible(String value);
    
    public String getEligible();
    
    public void setVendor(String value);
    
    public String getVendor();
}
