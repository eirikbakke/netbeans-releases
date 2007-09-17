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

import java.util.Collections;
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
 *
 * @author Michal Skvor
 */
public class RealTypeSerializer implements JavonSerializer {
    
    private final ClassData floatClassData  = new ClassData( "", "float", true, false, this );
    private final ClassData doubleClassData = new ClassData( "", "double", true, false, this );
    
    private final ClassData FloatClassData  = new ClassData( "java.lang", "Float", false, false, this );
    private final ClassData DoubleClassData = new ClassData( "java.lang", "Double", false, false, this );
    
    /** Creates a new instance of RealTypeSerializer */
    public RealTypeSerializer() {
    }
    
    public String getName() {
        return "Real numbers serializer";
    }

    public boolean isTypeSupported( Traversable traversable, TypeMirror type, Map<String, ClassData> typeCache ) {
        if( TypeKind.ARRAY == type.getKind()) return false;
        if( TypeKind.FLOAT == type.getKind()) {
            return true;
        } else if( TypeKind.DOUBLE == type.getKind()) {
            return true;
        } else if( TypeKind.DECLARED == type.getKind()) {
            TypeElement clazz = (TypeElement)((DeclaredType) type).asElement();
            String fqn = clazz.getQualifiedName().toString();
            if( "java.lang.Float".equals( fqn )) {
                return true;
            } else if( "java.lang.Double".equals( fqn )) {
                return true;
            }
        }
        
        return false;
    }

    public ClassData getType( Traversable traversable, TypeMirror type, Map<String, ClassData> typeCache ) {
        if( TypeKind.FLOAT == type.getKind()) {
            return floatClassData;
        } else if( TypeKind.DOUBLE == type.getKind()) {
            return doubleClassData;
        } else if( TypeKind.DECLARED == type.getKind()) {
            TypeElement clazz = (TypeElement)((DeclaredType) type).asElement();
            String fqn = clazz.getQualifiedName().toString();
            if( "java.lang.Float".equals( fqn )) {
                return FloatClassData;
            } else if( "java.lang.Double".equals( fqn )) {
                return DoubleClassData;
            }
        }
        
        return null;
    }

    public String instanceOf( JavonMapping mapping, ClassData type  ) {        
        if( mapping.getProperty( JavonMapping.FLOATING_POINT_SUPPORT ).equals( Boolean.FALSE ) &&
                mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.CLIENT )) 
        {
            // For no Float and Double support on client side
            if( floatClassData.equals( type ) || FloatClassData.equals( type )) {
                return "String";
            } else if( doubleClassData.equals( type ) || DoubleClassData.equals(( type ))) {
                return "String";
            } 
        } else {
            if( floatClassData.equals( type )) {
                return "float";
            } else if( FloatClassData.equals( type )) {
                return "Float";
            } else if( doubleClassData.equals( type )) {
                return "double";
            } else if( DoubleClassData.equals( type )) {
                return "Double";
            }
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());        
    }
    
    public String toObject( JavonMapping mapping, ClassData type, String variable  ) {
        if( mapping.getProperty( JavonMapping.FLOATING_POINT_SUPPORT ).equals( Boolean.FALSE ) &&
                mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.CLIENT )) 
        {
            if( floatClassData.equals( type ) || doubleClassData.equals( type ) ||
                FloatClassData.equals( type ) || DoubleClassData.equals( type )) 
            {
                return "(String)" + variable;
            }
        } else {
            if( floatClassData.equals( type )) {
                return "new Float(" + variable + ")";
            } else if( doubleClassData.equals( type )) {
                return "new Double(" + variable + ")";
            } else if( FloatClassData.equals( type )) {
                return "(Float)" + variable;
            } else if( DoubleClassData.equals( type )) {
                return "(Double)" + variable;
            }
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());
    }

    public String fromObject( JavonMapping mapping, ClassData type, String object  ) {
        if( mapping.getProperty( JavonMapping.FLOATING_POINT_SUPPORT ).equals( Boolean.FALSE ) &&
                mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.CLIENT )) 
        {
            if( floatClassData.equals( type ) || doubleClassData.equals( type ) ||
                FloatClassData.equals( type ) || DoubleClassData.equals( type )) 
            {
                return "(String)" + object;
            }
        } else {        
            if( floatClassData.equals( type )) {
                return "((Float)" + object + ").floatValue()";
            } else if( doubleClassData.equals( type )) {
                return "((Double)" + object + ").doubleValue()";
            } else if( FloatClassData.equals( type )) {
                return "(Float)" + object;
            } else if( DoubleClassData.equals( type )) {
                return "(Double)" + object;
            }
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());
    }

    public String toStream( JavonMapping mapping, ClassData type, String stream, String object ) {
        if( mapping.getProperty( JavonMapping.FLOATING_POINT_SUPPORT ).equals( Boolean.TRUE )) {
            if( floatClassData.equals( type )) {
                return stream + ".writeFloat( " + object + " );";
            } else if( FloatClassData.equals( type )) {
                return stream + ".writeFloat( " + fromObject( mapping, floatClassData, object  ) + " );";
            } else if( doubleClassData.equals( type )) {
                return stream + ".writeDouble( " + object + " );";
            } else if( DoubleClassData.equals( type )) {
                return stream + ".writeDouble( " + fromObject( mapping, doubleClassData, object  ) + " );";
            }
        } else {
            if( mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.SERVER )) {
                if( floatClassData.equals( type )) {
                    return stream + ".writeUTF(new Float(" + object + ").toString());";
                } else if( FloatClassData.equals( type )) {
                    return stream + ".writeUTF(" + object + ".toString());";
                } else if( doubleClassData.equals( type )) {
                    return stream + ".writeUTF(new Double(" + object + ").toString());";
                } else if( DoubleClassData.equals( type )) {
                    return stream + ".writeUTF( " + object + ".toString());";
                }
            } else if( mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.CLIENT )){
                if( floatClassData.equals( type )) {
                    return stream + ".writeUTF( " + object + " );";
                } else if( FloatClassData.equals( type )) {
                    return stream + ".writeUTF( " + object + " );";
                } else if( doubleClassData.equals( type )) {
                    return stream + ".writeUTF( " + object + " );";
                } else if( DoubleClassData.equals( type )) {
                    return stream + ".writeUTF( " + object + " );";
                }
            }
                    
        }
        throw new IllegalArgumentException( "Invalid type: " + type.getName());        
    }

    public String fromStream( JavonMapping mapping, ClassData type, String stream, String object ) {
        String result = "";
        if( object != null ) result = object + " = ";
        if( mapping.getProperty( JavonMapping.FLOATING_POINT_SUPPORT ).equals( Boolean.TRUE )) {
            if( floatClassData.equals( type )) {
                result += stream + ".readFloat()";
            } else if( FloatClassData.equals( type )) {
                result += toObject( null,floatClassData, stream + ".readFloat()"  );
            } else if ( doubleClassData.equals( type )) {
                result += stream + ".readDouble()";
            } else if( DoubleClassData.equals( type )) {
                result += toObject( null,doubleClassData, stream + ".readDouble()"  );
            }
        } else {
            if( mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.SERVER )) {
                if( floatClassData.equals( type )) {
                    result += "new Float(" + stream + ".readUTF())";
                } else if( FloatClassData.equals( type )) {
                    result += "new Float(" + stream + ".readUTF())";
                } else if ( doubleClassData.equals( type )) {
                    result += "new Double(" + stream + ".readUTF())";
                } else if( DoubleClassData.equals( type )) {
                    result += "new Double(" + stream + ".readUTF())";
                }
            } else if( mapping.getProperty( JavonMapping.TARGET ).equals( JavonMapping.CLIENT )){
                if( floatClassData.equals( type )) {
                    result += stream + ".readUTF()";
                } else if( FloatClassData.equals( type )) {
                    result += stream + ".readUTF()";
                } else if ( doubleClassData.equals( type )) {
                    result += stream + ".readUTF()";
                } else if( DoubleClassData.equals( type )) {
                    result += stream + ".readUTF()";
                }
            }
        }
        if( "".equals( result ))
            throw new IllegalArgumentException( "Invalid type: " + type.getName());
        
        if( object != null ) result += ";";
        
        return result;
    }

    public Set<ClassData> getReferencesTypes( ClassData rootClassData, Set<ClassData> usedTypes ) {
        return Collections.singleton( rootClassData );
    }
}
