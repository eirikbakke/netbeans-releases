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

//import com.sun.tools.ws.processor.model.Operation;
//import com.sun.tools.ws.processor.model.java.JavaMethod;
import org.netbeans.modules.websvc.jaxwsmodelapi.WSOperation;
import org.netbeans.modules.websvc.jaxwsmodelapi.WSPort;
import org.netbeans.modules.websvc.saas.model.jaxb.Method;

/**
 *
 * @author nam
 */
public class WsdlSaasMethod extends SaasMethod {
    WsdlSaasPort parent;
    WSPort port;
    WSOperation operation;

    public WsdlSaasMethod(WsdlSaas saas, Method method) {
        super(saas, method);
    }

    public WsdlSaasMethod(WsdlSaasPort port, WSOperation operation) {
        super(port.getParentSaas(), null);
        this.parent = port;
        this.port = port.getWsdlPort();
        this.operation = operation;
    }

    public String getName() {
        if (getMethod() != null) {
            return getMethod().getName();
        }
        assert operation != null : "Should have non-null operation when filter method does not exist";
        return operation.getName();
    }

    @Override
    public WsdlSaas getSaas() {
        return (WsdlSaas) super.getSaas();
    }

    public WSOperation getWsdlOperation() {
        init();
        return operation;
    }

    public WSPort getWsdlPort() {
        init();
        return port;
    }

    //FIXME - Refactor
    public Object getJavaMethod() {
//        Operation op = (Operation)getWsdlOperation().getInternalJAXWSOperation();
//        return (op != null) ? op.getJavaMethod() : null;
        return null;
    }

    private void init() {
        if (port == null || operation == null) {
            assert getMethod() != null : "Should have non-null filter method";
            for (Object p : getSaas().getWsdlModel().getPorts()) {
                if (! ((WSPort)p).getName().equals(getMethod().getPortName())) {
                    continue;
                }
                port = (WSPort) p;

                for (Object op : port.getOperations()) {
                    if (((WSOperation)op).getName().equals(getMethod().getOperationName())) {
                        operation = (WSOperation) op;
                        return;
                    }
                }
                break;
            }
        }
    }
}
