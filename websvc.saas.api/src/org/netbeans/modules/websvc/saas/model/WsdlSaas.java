/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.websvc.saas.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlPort;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlService;
import org.netbeans.modules.websvc.saas.model.jaxb.Method;
import org.netbeans.modules.websvc.saas.model.jaxb.SaasMetadata;
import org.netbeans.modules.websvc.saas.model.jaxb.SaasMetadata.CodeGen;
import org.netbeans.modules.websvc.saas.model.jaxb.SaasServices;
import org.netbeans.modules.websvc.saas.spi.websvcmgr.WsdlData;
import org.netbeans.modules.websvc.saas.util.WsdlUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 *
 * @author nam
 */
public class WsdlSaas extends Saas implements PropertyChangeListener {
    //TODO consolidate and remove
    private WsdlData wsData;
    
    private List<WsdlSaasPort> ports;

    public WsdlSaas(SaasGroup parentGroup, SaasServices services) {
        super(parentGroup, services);
    }

    public WsdlSaas(SaasGroup parentGroup, String displayName, String url, String packageName) {
        this(parentGroup, new SaasServices());
        this.getDelegate().setDisplayName(displayName);
        this.getDelegate().setUrl(url);
        SaasMetadata m = this.getDelegate().getSaasMetadata();
        if (m == null) {
            m = new SaasMetadata();
            this.getDelegate().setSaasMetadata(m);
        }
        CodeGen cg = m.getCodeGen();
        if (cg == null) {
            cg = new CodeGen();
            m.setCodeGen(cg);
        }
        cg.setPackageName(packageName);
    }
    
    public WsdlData getWsdlData() {
        if (getState() != State.READY) {
            throw new IllegalStateException("Current state: " + getState() + ", expect: " + State.READY);
        }
        return wsData;
    }

    public String getDefaultServiceName() {
        if (getMethods().size() > 0) {
            return getMethods().get(0).getMethod().getServiceName();
        }
        return ""; //NOI18N
    }
    
    @Override
    public void toStateReady() {
        if (wsData == null) {
            wsData = WsdlUtil.getWsdlDataAsynchronously(getUrl(), getDefaultServiceName()); //NOI18N
            if (wsData != null) {
                wsData.addPropertyChangeListener(WeakListeners.propertyChange(this, wsData));
                if (wsData.isReady()) {
                    setState(State.READY);
                }
            }
        }
    }
    
    private List<WsdlPort> filterNonSoapPorts(List<WsdlPort> ports) {
        List<WsdlPort> filterPorts = new java.util.ArrayList<WsdlPort>(ports.size());
        
        for (WsdlPort port : ports) {
            if (port.getAddress() != null) {
                filterPorts.add(port);
            }
        }
        
        return filterPorts;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("resolved")) { //NOI18N
            Object newValue = evt.getNewValue();
            if (newValue instanceof Boolean) {
                boolean resolved = ((Boolean) newValue).booleanValue();
                if (resolved) {
                    setState(State.READY);
                    assert wsData.getName().equals(wsData.getWsdlService().getName());
                } else {
                    setState(State.UNINITIALIZED);
                }

            }
            
        }
    }

    public WsdlService getWsdlModel() {
        return getWsdlData().getWsdlService();
    }

    public FileObject getLocalWsdlFile() {
        return FileUtil.toFileObject(new File(getWsdlData().getWsdlFile()));
    }
    
    /**
     * Either return methods, if filtering methods exists, or return ports.
     * 
     * @return list of either all filtered methods or all ports.
     */
    public List<Object> getPortsOrMethods() {
        List<Object> result = new ArrayList<Object>();
        List<SaasMethod> methods = getMethods();
        if (methods != null && methods.size() > 0) {
            result.addAll(methods);
        } else {
            result.addAll(getPorts());
        }
        return result;
    }

    public List<WsdlSaasPort> getPorts() {
        if (ports == null) {
            ports = new ArrayList<WsdlSaasPort>();
            for (WsdlPort p : filterNonSoapPorts(getWsdlModel().getPorts())) {
                ports.add(new WsdlSaasPort(this, p));
            }
        }
        return ports;
    }
    
    @Override
    protected WsdlSaasMethod createSaasMethod(Method method) {
        return new WsdlSaasMethod(this, method);
    }
    
    
    @Override
    public FileObject getSaasFolder() {
        if (saasFolder == null) {
            int begin = getUrl().lastIndexOf('/')+1;
            int end = getUrl().lastIndexOf('?');
            if (end <= begin) {
                end = getUrl().lastIndexOf('.');
            }

            String folderName = (end <= begin) ? getUrl().substring(begin) : getUrl().substring(begin, end);
            FileObject home = FileUtil.toFileObject(new File(SaasServicesModel.getInstance().WEBSVC_HOME));
            saasFolder = home.getFileObject(folderName);
            if (saasFolder == null) {
                try {
                    saasFolder = home.createFolder(folderName);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return saasFolder;
    }
}
