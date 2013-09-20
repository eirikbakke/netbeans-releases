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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.jpa.verification;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.TypeElement;
import org.netbeans.modules.j2ee.jpa.verification.common.Rule;
import org.netbeans.modules.j2ee.jpa.verification.common.RulesEngine;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.ConsistentAccessType;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.HasNoArgConstructor;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.IdClassOverridesEqualsAndHashCode;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.IdDefinedInHierarchy;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.JPAAnnotsOnlyOnAccesor;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.JPQLValidation;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.LegalCombinationOfAnnotations;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.NoIdClassOnEntitySubclass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.NonFinalClass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.OnlyEntityOrMappedSuperclassCanUseIdClass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.PersistenceUnitPresent;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.PublicClass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.QueriesProperlyDefined;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.SerializableClass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.TopLevelClass;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.UniqueEntityName;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.ValidAttributes;
import org.netbeans.modules.j2ee.jpa.verification.rules.entity.ValidPrimaryTableName;

/**
 *
 * @author Tomasz.Slota@Sun.COM
 */
public class JPARulesEngine extends RulesEngine {
    private static final List<Rule<TypeElement>> classRules = new LinkedList<>();
    
    static{
        //classRules.add(new PersistenceUnitPresent());
        classRules.add(new ConsistentAccessType());
        classRules.add(new IdDefinedInHierarchy());
        //classRules.add(new HasNoArgConstructor());
        classRules.add(new ValidPrimaryTableName());
        classRules.add(new SerializableClass());
        classRules.add(new PublicClass());
        classRules.add(new NonFinalClass());
        classRules.add(new TopLevelClass());
        classRules.add(new IdClassOverridesEqualsAndHashCode());
        classRules.add(new NoIdClassOnEntitySubclass());
        classRules.add(new ValidAttributes());
        classRules.add(new UniqueEntityName());
        classRules.add(new LegalCombinationOfAnnotations());
        classRules.add(new JPAAnnotsOnlyOnAccesor());
        classRules.add(new QueriesProperlyDefined());
        //classRules.add(new JPQLValidation());
        classRules.add(new OnlyEntityOrMappedSuperclassCanUseIdClass());
    }
    
    protected Collection<Rule<TypeElement>> getClassRules() {
        return classRules;
    }
    
}
