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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.api.model.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmInheritance;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.api.model.xref.CsmReference;
import org.netbeans.modules.cnd.api.model.xref.CsmTypeHierarchyResolver;
import org.netbeans.modules.cnd.modelutil.AntiLoop;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.netbeans.modules.cnd.utils.cache.CharSequenceKey;
import org.openide.util.Lookup;

/**
 * API to query information about virtuality of method
 * @author Vladimir Voskresensky
 */
public abstract class CsmVirtualInfoQuery {
    public abstract boolean isVirtual(CsmMethod method);
    public abstract Collection<CsmMethod> getTopmostBaseDeclarations(CsmMethod method);
    public abstract Collection<CsmMethod> getFirstBaseDeclarations(CsmMethod method);
    public abstract Collection<CsmMethod> getOverridenMethods(CsmMethod method, boolean searchFromBase);
    private static final CsmVirtualInfoQuery EMPTY = new Empty();
    
    /** default instance */
    private static CsmVirtualInfoQuery defaultQuery;
    
    protected CsmVirtualInfoQuery() {
    }
    
    /** Static method to obtain the resolver.
     * @return the resolver
     */
    public static CsmVirtualInfoQuery getDefault() {
        /*no need for sync synchronized access*/
        if (defaultQuery != null) {
            return defaultQuery;
        }
        defaultQuery = Lookup.getDefault().lookup(CsmVirtualInfoQuery.class);
        return defaultQuery == null ? EMPTY : defaultQuery;
    }
    
    //
    // Implementation of the default query
    //
    private static final class Empty extends CsmVirtualInfoQuery {
        private Empty() {
        }

        @Override
        public boolean isVirtual(CsmMethod method) {
            if (method.isVirtual()) {
                return true;
            }
            return processClass(method.getSignature(), method.getContainingClass(), new AntiLoop());
        }

        private boolean processClass(CharSequence sig, CsmClass cls, AntiLoop antilLoop){
            if (cls == null || antilLoop.contains(cls)) {
                return false;
            }
            antilLoop.add(cls);
            for(CsmMember m : cls.getMembers()){
                if (CsmKindUtilities.isMethod(m)) {
                    CsmMethod met = (CsmMethod) m;
                    if (CharSequenceKey.Comparator.compare(sig, met.getSignature())==0){
                        if (met.isVirtual()){
                            return true;
                        }
                        break;
                    }
                }
            }
            for(CsmInheritance inh : cls.getBaseClasses()){
                if (processClass(sig, CsmInheritanceUtilities.getCsmClass(inh), antilLoop)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<CsmMethod> getTopmostBaseDeclarations(CsmMethod method) {
            return getBaseDeclaration(method, false);
        }

        @Override
        public Collection<CsmMethod> getFirstBaseDeclarations(CsmMethod method) {
            return getBaseDeclaration(method, true);
        }

        private Collection<CsmMethod> getBaseDeclaration(CsmMethod method, boolean first) {
            Set<CharSequence> antilLoop = new HashSet<CharSequence>();
            CharSequence sig = method.getSignature();
            Collection<CsmMethod> result = new ArrayList<CsmMethod>();
            for(CsmInheritance inh : method.getContainingClass().getBaseClasses()) {
                AtomicReference<CsmMethod> branchResult = new AtomicReference<CsmMethod>();
                if (processMethod(sig, CsmInheritanceUtilities.getCsmClass(inh), antilLoop, branchResult, first)) {
                    CsmMethod m = branchResult.get();
                    CndUtils.assertTrue(m != null, "Branch result should not be null"); //NOI18N
                    result.add(m);
                }
            }
            return result;
        }

        /**
         * Searches for method with the given signature in the given class and its ancestors.
         * @param sig signature to search
         * @param cls class to start with
         * @param antilLoop prevents infinite loops
         * @param result if a method with the given signature is found, it's stored in result - even if it is not virtual.
         * @param first if true, returns first found method, otherwise the topmost one
         * @return true if method found and it is virtual, otherwise false
         */
        private boolean processMethod(CharSequence sig, CsmClass cls, Set<CharSequence> antilLoop, AtomicReference<CsmMethod> result, boolean first){
            if (cls == null || antilLoop.contains(cls.getQualifiedName())) {
                return false;
            }
            antilLoop.add(cls.getQualifiedName());
            for(CsmMember member : cls.getMembers()){
                if (CsmKindUtilities.isMethod(member)) {
                    CsmMethod method = (CsmMethod) member;
                    if (CharSequenceKey.Comparator.compare(sig, method.getSignature()) == 0) {
                        if (first) {
                            if (result.get() == null) {
                                result.set(method);
                            }
                        } else {
                            result.set(method);
                        }
                        if (method.isVirtual()) {
                            if (first) {
                                return true;
                            }
                        }
                    }
                }
            }
            for(CsmInheritance inh : cls.getBaseClasses()){
                if( processMethod(sig, CsmInheritanceUtilities.getCsmClass(inh), antilLoop, result, first)) {
                    return true;
                }
            }
            return (result.get() != null && result.get().isVirtual());
        }

        @Override
        public Collection<CsmMethod> getOverridenMethods(CsmMethod method, boolean searchFromBase) {
            Set<CsmMethod> res = new HashSet<CsmMethod>();
            CsmClass cls;
            if (searchFromBase) {
                Iterator<CsmMethod> it = getTopmostBaseDeclarations(method).iterator();
                if (it.hasNext()){
                    method = it.next();
                }
                res.add(method);
            }
            cls = method.getContainingClass();
            if (cls != null){
                CharSequence sig = method.getSignature();
                for(CsmReference ref :CsmTypeHierarchyResolver.getDefault().getSubTypes(cls, false)){
                    CsmClass c = (CsmClass) ref.getOwner();
                    for(CsmMember m : c.getMembers()){
                        if (CsmKindUtilities.isMethod(m)) {
                            CsmMethod met = (CsmMethod) m;
                            if (CharSequenceKey.Comparator.compare(sig, met.getSignature())==0){
                                res.add(met);
                                break;
                            }
                        }
                    }
                }
            }
            return res;
        }
    }    
}
