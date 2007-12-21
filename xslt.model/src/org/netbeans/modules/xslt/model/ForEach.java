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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.xslt.model;




/**
 * <pre>
 * &lt;xs:element name="for-each" substitutionGroup="xsl:instruction">
 *      &lt;xs:complexType>
 *          &lt;xs:complexContent mixed="true">
 *              &lt;xs:extension base="xsl:versioned-element-type">
 *                  &lt;xs:sequence>
 *                      &lt;xs:element ref="xsl:sort" minOccurs="0" maxOccurs="unbounded"/>
 *                      &lt;xs:group ref="xsl:sequence-constructor-group" minOccurs="0" maxOccurs="unbounded"/>
 *                  &lt;/xs:sequence>
 *                  &lt;xs:attribute name="select" type="xsl:expression" use="required"/>
 *              &lt;/xs:extension>
 *          &lt;/xs:complexContent>
 *      &lt;/xs:complexType> 
 * &lt;/xs:element>
 * 
 * </pre>
 * @author ads
 *
 */
public interface ForEach extends Instruction, SelectSpec, SequenceConstructor,
    ContentElement, SortContainer
{
}
