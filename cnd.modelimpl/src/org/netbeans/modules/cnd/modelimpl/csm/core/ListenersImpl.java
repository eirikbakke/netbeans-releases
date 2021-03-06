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

package org.netbeans.modules.cnd.modelimpl.csm.core;

import java.util.Collection;
import org.netbeans.modules.cnd.api.model.CsmChangeEvent;
import org.netbeans.modules.cnd.api.model.CsmListeners;
import org.netbeans.modules.cnd.api.model.CsmModelListener;
import org.netbeans.modules.cnd.api.model.CsmModelState;
import org.netbeans.modules.cnd.api.model.CsmModelStateListener;
import org.netbeans.modules.cnd.api.model.CsmProgressListener;
import org.netbeans.modules.cnd.api.model.CsmProject;
import org.netbeans.modules.cnd.modelimpl.debug.DiagnosticExceptoins;
import org.netbeans.modules.cnd.modelimpl.util.WeakList;
import org.openide.util.Lookup;

/**
 * CsmListeners implementation
 * @author Vladimir Kvashin
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.cnd.api.model.CsmListeners.class)
public class ListenersImpl extends CsmListeners {

    private final WeakList<CsmModelListener> modelListeners = new WeakList<>();
    private final WeakList<CsmModelStateListener> modelStateListeners = new WeakList<>();
    
    @Override
    public void addModelListener(CsmModelListener listener) {
	modelListeners.add(listener);
    }

    @Override
    public void removeModelListener(CsmModelListener listener) {
	modelListeners.remove(listener);
    }

    @Override
    public void addProgressListener(CsmProgressListener listener) {
	ProgressSupport.instance().addProgressListener(listener);
    }

    @Override
    public void removeProgressListener(CsmProgressListener listener) {
	ProgressSupport.instance().removeProgressListener(listener);
    }
    
    @Override
    public void addModelStateListener(CsmModelStateListener listener) {
	modelStateListeners.add(listener);
    }

    @Override
    public void removeModelStateListener(CsmModelStateListener listener) {
	modelStateListeners.remove(listener);
    }
    
    //package-local
    static ListenersImpl getImpl() {
	return (ListenersImpl) CsmListeners.getDefault();
    }
    
    private Iterable<? extends CsmModelListener> getModelListeners() {
	Collection<? extends CsmModelListener> services = Lookup.getDefault().lookupAll(CsmModelListener.class);
	return (services.isEmpty()) ? modelListeners : modelListeners.join(services);
    }
    
    private Iterable<? extends CsmModelStateListener> getModelStateListeners() {
	Collection<? extends CsmModelStateListener> services = Lookup.getDefault().lookupAll(CsmModelStateListener.class);
	return (services.isEmpty()) ? modelStateListeners : modelStateListeners.join(services);
    }
    
    //package-local
    void fireProjectOpened(final ProjectBase csmProject) {
        csmProject.onAddedToModel();
        for ( CsmModelListener listener : getModelListeners() ) {
            try {
                listener.projectOpened(csmProject);
            } catch (AssertionError ex){
                DiagnosticExceptoins.register(ex);
            } catch (Exception ex) {
                DiagnosticExceptoins.register(ex);
            }
        }
    }
    
    //package-local
    void fireProjectClosed(CsmProject csmProject) {
        for ( CsmModelListener listener : getModelListeners() ) {
            try {
                listener.projectClosed(csmProject);
            } catch (AssertionError ex){
                DiagnosticExceptoins.register(ex);
            } catch (Exception ex) {
                DiagnosticExceptoins.register(ex);
            }
        }
    }
    
    //package-local
    void fireModelChanged(CsmChangeEvent e) {
        for ( CsmModelListener listener : getModelListeners() ) {
            try {
                listener.modelChanged(e);
            } catch (AssertionError ex){
                DiagnosticExceptoins.register(ex);
            } catch (Exception ex) {
                DiagnosticExceptoins.register(ex);
            }
        }
    }
    
    //package-local
    void fireModelStateChanged(CsmModelState newState, CsmModelState oldState) {
        for ( CsmModelStateListener listener : getModelStateListeners() ) {
            try {
                listener.modelStateChanged(newState, oldState);
            } catch (AssertionError ex){
                DiagnosticExceptoins.register(ex);
            } catch (Exception ex) {
                DiagnosticExceptoins.register(ex);
            }
        }
    }
    
}
