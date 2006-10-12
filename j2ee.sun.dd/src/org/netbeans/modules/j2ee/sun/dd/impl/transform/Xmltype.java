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
 * Software is Sun Microsystems, Inc. Portions Copyright 2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/**
 *	This generated bean class Xmltype matches the schema element 'xmltype'.
 *  The root bean class is Transform
 *
 *	Generated on Sat Aug 13 01:27:36 GMT-08:00 2005
 * @Generated
 */

package org.netbeans.modules.j2ee.sun.dd.impl.transform;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class Xmltype extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String NAME = "Name";	// NOI18N
	static public final String MOD_ELEMENT = "ModElement";	// NOI18N

	public Xmltype() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Xmltype(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("name", 	// NOI18N
			NAME, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("mod-element", 	// NOI18N
			MOD_ELEMENT, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ModElement.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setName(String value) {
		this.setValue(NAME, value);
	}

	//
	public String getName() {
		return (String)this.getValue(NAME);
	}

	// This attribute is an array, possibly empty
	public void setModElement(int index, ModElement value) {
		this.setValue(MOD_ELEMENT, index, value);
	}

	//
	public ModElement getModElement(int index) {
		return (ModElement)this.getValue(MOD_ELEMENT, index);
	}

	// Return the number of properties
	public int sizeModElement() {
		return this.size(MOD_ELEMENT);
	}

	// This attribute is an array, possibly empty
	public void setModElement(ModElement[] value) {
		this.setValue(MOD_ELEMENT, value);
	}

	//
	public ModElement[] getModElement() {
		return (ModElement[])this.getValues(MOD_ELEMENT);
	}

	// Add a new element returning its index in the list
	public int addModElement(ModElement value) {
		int positionOfNewItem = this.addValue(MOD_ELEMENT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeModElement(ModElement value) {
		return this.removeValue(MOD_ELEMENT, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ModElement newModElement() {
		return new ModElement();
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property name
		if (getName() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getName() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "name", this);	// NOI18N
		}
		// Validating property modElement
		for (int _index = 0; _index < sizeModElement(); ++_index) {
			ModElement element = getModElement(_index);
			if (element != null) {
				element.validate();
			}
		}
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Name");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(NAME, 0, str, indent);

		str.append(indent);
		str.append("ModElement["+this.sizeModElement()+"]");	// NOI18N
		for(int i=0; i<this.sizeModElement(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getModElement(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(MOD_ELEMENT, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Xmltype\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:


<!--- Put your DTDDoc comment here. -->
<!ELEMENT transform (xmltype)*>

<!--- xmltype : target version of the server xml -->
<!ELEMENT xmltype (name, mod-element*)>

<!--- Put your DTDDoc comment here. -->
<!ELEMENT name (#PCDATA)>

<!--- mod-element : element that contains sub-elements and attributes that need to be removed 
      ie. sub-elements and attributes that were added in a later version of server xml -->
<!ELEMENT mod-element (name, mod-attribute*, sub-element*)>

<!--- sub-element : sub-elements that need to be removed -->
<!ELEMENT sub-element (name)>

<!--- new-attribute : attribute that need to be removed -->
<!ELEMENT mod-attribute (name)>


*/
