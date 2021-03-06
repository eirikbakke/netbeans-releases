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

package org.netbeans.modules.xml.jaxb.actions;

import org.netbeans.api.project.Project;
import org.netbeans.modules.xml.jaxb.cfg.schema.Schema;
import org.netbeans.modules.xml.jaxb.ui.JAXBWizardSchemaNode;
import org.netbeans.modules.xml.jaxb.util.ProjectHelper;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.actions.NodeAction;


/**
 * @author gmpatil
 * @author lgao
 */
public class JAXBDeleteSchemaAction extends NodeAction {
    
    public JAXBDeleteSchemaAction() {
    }

    public void performAction(Node[] nodes) {
        JAXBWizardSchemaNode schemaNode = 
                nodes[0].getLookup().lookup(JAXBWizardSchemaNode.class);
        if (schemaNode != null){
            Schema schema = schemaNode.getSchema();
            final Project prj = schemaNode.getProject();
            ProjectHelper.deleteSchemaFromModel(prj, schema);
            ProjectHelper.cleanupLocalSchemaDir(prj, schema);
            ProjectHelper.cleanCompileXSDs(prj, false, new TaskListener() {
                
                @Override
                public void taskFinished( Task arg0 ) {
                    ProjectHelper.checkAndDeregisterScript(prj);
                }
            });
        }        
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName() {
        return NbBundle.getMessage(this.getClass(), "LBL_DeleteSchema");//NOI18N
    }
    
    @Override
    protected boolean asynchronous() {
        return false;
    }
        
    @Override
    protected boolean enable(Node[] node) {
        return true;
    }
}
