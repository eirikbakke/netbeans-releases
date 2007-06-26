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

package org.netbeans.modules.mobility.e2e.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.mobility.e2e.classdata.ClassData;
import org.netbeans.modules.mobility.javon.JavonMapping;
import org.netbeans.modules.mobility.javon.JavonSerializer;
import org.netbeans.modules.mobility.javon.Traversable;

/**
 * Serializer for primitive types but without float or double
 * 
 * <p>Supported types are:<br>
 * boolean, byte, char, int, short, String
 * 
 * @author Michal Skvor
 */
public class PrimitiveTypeSerializer implements JavonSerializer {
    
    private final static ClassData voidClassData        = new ClassData( "", "void", true, false );
    private final static ClassData booleanClassData     = new ClassData( "", "boolean", true, false );
    private final static ClassData BooleanClassData     = new ClassData( "java.lang", "Boolean", false, false );
    private final static ClassData byteClassData        = new ClassData( "", "byte", true, false );
    private final static ClassData ByteClassData        = new ClassData( "java.lang", "Byte", false, false );
    private final static ClassData charClassData        = new ClassData( "", "char", true, false );
    private final static ClassData CharClassData        = new ClassData( "java.lang", "Character", true, false );
    private final static ClassData intClassData         = new ClassData( "", "int", true, false );
    private final static ClassData IntClassData         = new ClassData( "java.lang", "Integer", false, false );
    private final static ClassData longClassData        = new ClassData( "", "long", true, false );
    private final static ClassData LongClassData        = new ClassData( "java.lang", "Long", false, false );
    private final static ClassData shortClassData       = new ClassData( "", "short", true, false );
    private final static ClassData ShortClassData       = new ClassData( "java.lang", "Short", false, false );
    
    private final static ClassData stringClassData      = new ClassData( "java.lang", "String", false, false );
    
    private final static Set<String> supportedDeclaredTypes = new HashSet<String>();
    
    /** Creates a new instance of PrimitiveTypeSerializer */
    public PrimitiveTypeSerializer() {        
    }
    
    public String getName() {
        return "Primitive type serializer";
    }

    public boolean isTypeSupported( Traversable traversable, TypeMirror type, Map<String, ClassData> typeCache ) {
        if( TypeKind.VOID == type.getKind()) {
            return true;
        } else if( TypeKind.BOOLEAN == type.getKind()) {
            return true;
        } else if( TypeKind.BYTE == type.getKind()) {
            return true;
        } else if( TypeKind.CHAR == type.getKind()) {
            return true;
        } else if( TypeKind.INT == type.getKind()) {
            return true;
        } else if( TypeKind.LONG == type.getKind()) {
            return true;
        } else if( TypeKind.SHORT == type.getKind()) {
            return true;
        } else if( TypeKind.DECLARED == type.getKind()) {
            TypeElement clazz = (TypeElement)((DeclaredType) type).asElement();
            String classFullQualifiedName = clazz.getQualifiedName().toString();
            if( "java.lang.String".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Boolean".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Byte".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Character".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Integer".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Long".equals( classFullQualifiedName )) {
                return true;
            } else if( "java.lang.Short".equals( classFullQualifiedName )) {
                return true;
            }
        }
        
        return false;
    }
    
    public ClassData getType( Traversable traversable, TypeMirror type, Map<String, ClassData> typeCache ) {
        if( TypeKind.VOID == type.getKind()) {
            return voidClassData;
        } else if( TypeKind.BOOLEAN == type.getKind()) {
            return booleanClassData;
        } else if( TypeKind.BYTE == type.getKind()) {
            return byteClassData;
        } else if( TypeKind.CHAR == type.getKind()) {
            return charClassData;
        } else if( TypeKind.INT == type.getKind()) {
            return intClassData;
        } else if( TypeKind.LONG == type.getKind()) {
            return longClassData;
        } else if( TypeKind.SHORT == type.getKind()) {
            return shortClassData;
        } else if( TypeKind.DECLARED == type.getKind()) {
            TypeElement clazz = (TypeElement)((DeclaredType) type).asElement();
            String classFullQualifiedName = clazz.getQualifiedName().toString();
            if( "java.lang.String".equals( classFullQualifiedName )) {
                return stringClassData;
            } else if( "java.lang.Boolean".equals( classFullQualifiedName )) {
                return BooleanClassData;
            } else if( "java.lang.Byte".equals( classFullQualifiedName )) {
                return ByteClassData;
            } else if( "java.lang.Character".equals( classFullQualifiedName )) {
                return CharClassData;
            } else if( "java.lang.Integer".equals( classFullQualifiedName )) {
                return IntClassData;
            } else if( "java.lang.Long".equals( classFullQualifiedName )) {
                return LongClassData;
            } else if( "java.lang.Short".equals( classFullQualifiedName )) {
                return ShortClassData;
            }
        }

        return null;
    }

    public String instanceOf( ClassData type ) {
        if( booleanClassData.equals( type ) || BooleanClassData.equals( type )) {
            return "Boolean";
        } else if( byteClassData.equals( type ) || ByteClassData.equals( type )) {
            return "Byte";
        } else if( charClassData.equals( type ) || CharClassData.equals( type )) {
            return "Char";
        } else if( intClassData.equals( type ) || IntClassData.equals( type )) {
            return "Integer";
        } else if( longClassData.equals( type ) || LongClassData.equals( type )) {
            return "Long";
        } else if( shortClassData.equals( type ) || ShortClassData.equals( type )) {
            return "Short";
        } else if( stringClassData.equals( type )) {
            return "String";
        } else if( voidClassData.equals( type )) {
            return "Void";
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());
    }
    
    public String toObject( ClassData type, String variable ) {
        if( booleanClassData.equals( type )) {
            return "new java.lang.Boolean(" + variable + ")";
        } else if( byteClassData.equals( type )) {
            return "new java.lang.Byte(" + variable + ")";
        } else if( charClassData.equals( type )) {
            return "new java.lang.Char(" + variable + ")";
        } else if( intClassData.equals( type )) {
            return "new java.lang.Integer(" + variable + ")";
        } else if( longClassData.equals( type )) {
            return "new java.lang.Long(" + variable + ")";
        } else if( shortClassData.equals( type )) {
            return "new java.lang.Short(" + variable + ")";
        } else if( stringClassData.equals( type )) {
            return "(String)" + variable;
        } else if( voidClassData.equals( type )) {
            return "void";
        } else if( BooleanClassData.equals( type )) {
            return "(Boolean)" + variable;
        } else if( ByteClassData.equals( type )) {
            return "(Byte)" + variable;
        } else if( CharClassData.equals( type )) {
            return "(Char)" + variable;
        } else if( IntClassData.equals( type )) {
            return "(Integer)" + variable;
        } else if( LongClassData.equals( type )) {
            return "(Long)" + variable;
        } else if( ShortClassData.equals( type )) {
            return "(Short)" + variable;
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());        
    }

    public String fromObject( ClassData type, String object) {
        if( booleanClassData.equals( type )) {
            return "((Boolean)" + object + ").booleanValue()";
        } else if( byteClassData.equals( type )) {
            return "((Byte)" + object + ").byteValue()";
        } else if( charClassData.equals( type )) {
            return "((Char)" + object + ").charValue()";
        } else if( intClassData.equals( type )) {
            return "((Integer)" + object + ").intValue()";
        } else if( longClassData.equals( type )) {
            return "((Long)" + object + ").longValue()";
        } else if( shortClassData.equals( type )) {
            return "((Short)" + object + ").shortValue()";
        } else if( stringClassData.equals( type )) {
            return "(String)" + object;
        } else if( voidClassData.equals( type )) {
            return "void";
        } else if( BooleanClassData.equals( type )) {
            return "(Boolean)" + object;
        } else if( ByteClassData.equals( type )) {
            return "(Byte)" + object;
        } else if( CharClassData.equals( type )) {
            return "(Char)" + object;
        } else if( IntClassData.equals( type )) {
            return "(Integer)" + object;
        } else if( LongClassData.equals( type )) {
            return "(Long)" + object;
        } else if( ShortClassData.equals( type )) {
            return "(Short)" + object;
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());        
    }

    public String toStream( JavonMapping mapping, ClassData type, String stream, String object ) {
        if( booleanClassData.equals( type )) {
            return stream + ".writeBoolean(" + object + ");";
        } else if( byteClassData.equals( type )) {
            return stream + ".writeByte(" + object + ");";
        } else if( charClassData.equals( type )) {
            return stream + ".writeChar(" + object + ");";
        } else if( intClassData.equals( type )) {
            return stream + ".writeInt(" + object + ");";
        } else if( longClassData.equals( type )) {
            return stream + ".writeLong(" + object + ");";
        } else if( shortClassData.equals( type )) {
            return stream + ".writeShort(" + object + ");";
        } else if( stringClassData.equals( type )) {
            return stream + ".writeUTF(" + fromObject( type, object ) + ");";
        } else if( voidClassData.equals( type )) {
        } else if( BooleanClassData.equals( type )) {
            return stream + ".writeBoolean(" + fromObject( booleanClassData, object ) + ");";
        } else if( ByteClassData.equals( type )) {
            return stream + ".writeByte(" + fromObject( byteClassData, object ) + ");";
        } else if( CharClassData.equals( type )) {
            return stream + ".writeChar(" + fromObject( charClassData, object ) + ");";
        } else if( IntClassData.equals( type )) {
            return stream + ".writeInt(" + fromObject( intClassData, object ) + ");";
        } else if( LongClassData.equals( type )) {
            return stream + ".writeLong(" + fromObject( longClassData, object ) + ");";
        } else if( ShortClassData.equals( type )) {
            return stream + ".writeShort(" + fromObject( shortClassData, object ) + ");";
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());        
    }

    public String fromStream( JavonMapping mapping, ClassData type, String stream, String object ) {
        String result = "";
        if( object != null ) result = object + " = ";
        if( voidClassData.equals( type )) {
            return object + " = _;";
        } else if( BooleanClassData.equals( type ) || booleanClassData.equals( type)) {
            result += toObject( booleanClassData, stream + ".readBoolean()" );
        } else if( ByteClassData.equals( type ) || byteClassData.equals( type)) {
            result += toObject( byteClassData, stream + ".readByte()" );
        } else if( CharClassData.equals( type ) || charClassData.equals( type)) {
            result += toObject( charClassData, stream + ".readChar()" );
        } else if( IntClassData.equals( type ) || intClassData.equals( type)) {
            result += toObject( intClassData, stream + ".readInt()" );
        } else if( LongClassData.equals( type ) || longClassData.equals( type)) {
            result += toObject( longClassData, stream + ".readLong()" );
        } else if( ShortClassData.equals( type ) || shortClassData.equals( type)) {
            result += toObject( shortClassData, stream + ".readShort()" );
        } else if( stringClassData.equals( type )) {
            result += stream + ".readUTF()";
        }
        if( "".equals( result ))
            throw new IllegalArgumentException( "Invalid type: " + type.getName());
        
        if( object != null ) result += ";";
        
        return result;
    }
}
