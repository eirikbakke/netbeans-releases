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

package org.netbeans.modules.j2ee.sun.share.configbean;

import java.beans.*;
import org.openide.util.Exceptions;

public class ServletRefBeanInfo extends SimpleBeanInfo {
	
	/** Return an appropriate icon (currently, only 16x16 color is available)
	 */
	public java.awt.Image getIcon(int iconKind) {
		return loadImage("resources/ServletRefIcon16.gif");	// NOI18N
	}
	
        /**
         * Gets the bean's <code>BeanDescriptor</code>s.
         *
         * @return BeanDescriptor describing the editable
         * properties of this bean.  May return null if the
         * information should be obtained by automatic analysis.
         */
        public BeanDescriptor getBeanDescriptor() {
            BeanDescriptor beanDescriptor = new BeanDescriptor  ( ServletRef.class , org.netbeans.modules.j2ee.sun.share.configbean.customizers.ServletRefCustomizer.class );//GEN-HEADEREND:BeanDescriptor
            
            // Here you can add code for customizing the BeanDescriptor.
            
            return beanDescriptor;
        }
	
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = new PropertyDescriptor[3];
        int PROPERTY_identity = 0;
        int PROPERTY_principalName = 1;
        int PROPERTY_servletName = 2;
    
        try {
            properties[PROPERTY_identity] = new PropertyDescriptor ( "identity", ServletRef.class, "getIdentity", "setIdentity" );
            properties[PROPERTY_principalName] = new PropertyDescriptor ( "principalName", ServletRef.class, "getPrincipalName", "setPrincipalName" );
            properties[PROPERTY_servletName] = new PropertyDescriptor ( "servletName", ServletRef.class, "getServletName", null );
        }
        catch( IntrospectionException e) {}//GEN-HEADEREND:Properties
		
		// Here you can add code for customizing the properties array.
		
        return properties;         
    }
	
	/**
	 * Gets the bean's <code>EventSetDescriptor</code>s.
	 *
	 * @return  An array of EventSetDescriptors describing the kinds of
	 * events fired by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public EventSetDescriptor[] getEventSetDescriptors() {
            int EVENT_propertyChangeListener = 0;
            int EVENT_vetoableChangeListener = 1;
            
            EventSetDescriptor[] eventSets = new EventSetDescriptor[2];
            
            try {
                eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( org.netbeans.modules.j2ee.sun.share.configbean.ServletRef.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" );
                eventSets[EVENT_vetoableChangeListener] = new EventSetDescriptor ( org.netbeans.modules.j2ee.sun.share.configbean.ServletRef.class, "vetoableChangeListener", java.beans.VetoableChangeListener.class, new String[] {"vetoableChange"}, "addVetoableChangeListener", "removeVetoableChangeListener" );
            }
            catch( IntrospectionException e) {
                Exceptions.printStackTrace(e);
            }
            return eventSets;
	}
	
	/**
	 * Gets the bean's <code>MethodDescriptor</code>s.
	 *
	 * @return  An array of MethodDescriptors describing the methods
	 * implemented by this bean.  May return null if the information
	 * should be obtained by automatic analysis.
	 */
	public MethodDescriptor[] getMethodDescriptors() {
		return new MethodDescriptor[0];
	}
}

