/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.cnd.otool.debugger.api.registry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.modules.cnd.otool.debugger.api.OtoolNativeDebugger;
import org.netbeans.spi.debugger.ContextProvider;


import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;

/** Processor to hide all the complexities of settings layer registration.
 *
 * @author Martin Entlicher
 */
@ServiceProvider(service=Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DebuggerProcessor extends LayerGeneratingProcessor {

    public static final String SERVICE_NAME = "serviceName"; // NOI18N

    public @Override Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(
                OtoolNativeDebugger.Registration.class.getCanonicalName()
        ));
    }

    @Override
    protected boolean handleProcess(
        Set<? extends TypeElement> annotations,
        RoundEnvironment env
    ) throws LayerGenerationException {
        if (env.processingOver()) {
            return false;
        }

        int cnt = 0;
        for (Element e : env.getElementsAnnotatedWith(OtoolNativeDebugger.Registration.class)) {
            OtoolNativeDebugger.Registration reg = e.getAnnotation(OtoolNativeDebugger.Registration.class);

            final String path = reg.path();
            handleProviderRegistration(e, OtoolNativeDebugger.class, path);
            cnt++;
        }      
        return cnt == annotations.size();
    }

    private void handleProviderRegistration(Element e, Class providerClass, String path) throws IllegalArgumentException, LayerGenerationException {
        String className = instantiableClassOrMethod(e);
        if (!isClassOf(e, providerClass)) {
            throw new IllegalArgumentException("Annotated element "+e+" is not an instance of " + providerClass); // NOI18N
        }
        System.out.println("Will handle &&&&&&&&&&&&&&&&&&&"); // NOI18N
        if (path != null && path.length() > 0) {
            path = "Debugger/"+path; // NOI18N
        } else {
            path = "Debugger"; // NOI18N
        }
        LayerBuilder lb = layer(e);
        String basename = className.replace('.', '-');
        LayerBuilder.File f = lb.file(path + "/" + basename + ".instance"); // NOI18N
        f.stringvalue(SERVICE_NAME, className).
          stringvalue("serviceClass", providerClass.getName()). // NOI18N
          stringvalue("instanceOf", providerClass.getName()). // NOI18N
          methodvalue("instanceCreate", providerClass.getName()+"$ContextAware", "createService"). // NOI18N
          write();
    }


    private boolean isClassOf(Element e, Class providerClass) {
        switch (e.getKind()) {
            case CLASS: {
                TypeElement te = (TypeElement) e;
                TypeMirror superType = te.getSuperclass();
                if (superType.getKind().equals(TypeKind.NONE)) {
                    return false;
                } else {
                    e = ((DeclaredType) superType).asElement();
                    String clazz = processingEnv.getElementUtils().getBinaryName((TypeElement) e).toString();
                    if (clazz.equals(providerClass.getName())) {
                        return true;
                    } else {
                        return isClassOf(e, providerClass);
                    }
                }
            }
            case METHOD: {
                TypeMirror retType = ((ExecutableElement) e).getReturnType();
                if (retType.getKind().equals(TypeKind.NONE)) {
                    return false;
                } else {
                    e = ((DeclaredType) retType).asElement();
                    String clazz = processingEnv.getElementUtils().getBinaryName((TypeElement) e).toString();
                    if (clazz.equals(providerClass.getName())) {
                        return true;
                    } else {
                        return isClassOf(e, providerClass);
                    }
                }
            }
            default:
                throw new IllegalArgumentException("Annotated element is not loadable as an instance: " + e); // NOI18N
        }
    }

   

    private String instantiableClassOrMethod(Element e) throws IllegalArgumentException, LayerGenerationException {
        switch (e.getKind()) {
            case CLASS: {
                String clazz = processingEnv.getElementUtils().getBinaryName((TypeElement) e).toString();
                if (e.getModifiers().contains(Modifier.ABSTRACT)) {
                    throw new LayerGenerationException(clazz + " must not be abstract", e); // NOI18N
                }
                {
                    boolean hasDefaultCtor = false;
                    boolean hasContextCtor = false;
                    for (ExecutableElement constructor : ElementFilter.constructorsIn(e.getEnclosedElements())) {
                        List<? extends VariableElement> params = constructor.getParameters();
                        if (params.isEmpty()) {
                            hasDefaultCtor = true;
                            break;
                        }
                        if (params.size() == 1) {
                            String type = params.get(0).asType().toString();
                            //System.err.println("Param type = "+type);
                            //TypeElement te = processingEnv.getElementUtils().getTypeElement(params.get(0).asType().toString());
                            //if (te != null && isClassOf(te, ContextProvider.class)) {
                            if (ContextProvider.class.getName().equals(type)) {
                                hasContextCtor = true;
                                break;
                            }
                        }
                    }
                    if (!(hasDefaultCtor || hasContextCtor)) {
                        throw new LayerGenerationException(clazz + " must have a no-argument constructor or constuctor taking "+ContextProvider.class.getName()+" as a parameter.", e); // NOI18N
                    }
                }
                /*propType = processingEnv.getElementUtils().getTypeElement("java.util.Properties").asType();
                        if (
                            m.getParameters().size() == 1 &&
                            m.getSimpleName().contentEquals("writeProperties") &&
                            m.getParameters().get(0).asType().equals(propType) &&
                            m.getReturnType().getKind() == TypeKind.VOID
                        ) {
                            hasWrite = true;
                        }
                }
                 * */
                return clazz;
            }
            case METHOD: {
                ExecutableElement ee = (ExecutableElement) e;
                String methodName = ee.getSimpleName().toString();
                String clazz = processingEnv.getElementUtils().getBinaryName((TypeElement) ee.getEnclosingElement()).toString();
                if (!e.getModifiers().contains(Modifier.STATIC)) {
                    throw new LayerGenerationException(ee + " must be static", e); // NOI18N
                }
                if (ee.getParameters().size() > 0) {
                    throw new LayerGenerationException(ee + " must not have any parameters", e); // NOI18N
                }
                return clazz+"."+methodName+"()"; // NOI18N
            }
            default:
                throw new IllegalArgumentException("Annotated element is not loadable as an instance: " + e); // NOI18N
        }
    }
}
