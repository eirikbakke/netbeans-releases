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

package org.netbeans.modules.j2ee.ejbjarproject.ui;

import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.ejbjarproject.EjbJarProject;
import org.netbeans.modules.j2ee.spi.ejbjar.support.J2eeProjectView;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Node;

/**
 * NodeFactory to create EJB nodes.
 * 
 * @author gpatil
 */
@NodeFactory.Registration(projectType="org-netbeans-modules-j2ee-ejbjarproject", position=150)
public class EjbsNodeFactory implements NodeFactory {

    public EjbsNodeFactory() {
    }

    public NodeList createNodes(Project p) {
        EjbJarProject project = p.getLookup().lookup(EjbJarProject.class);
        assert project != null;
        return new EjbNodeList(project);
    }
    
    private static class EjbNodeList implements NodeList<String> {
        private static final String KEY_EJBS = "ejbKey"; //NOI18N

        private final EjbJarProject project;

        EjbNodeList(EjbJarProject proj) {
            this.project = proj;
        }
        
        public List<String> keys() {
            return Collections.singletonList(KEY_EJBS);
        }

        public void addChangeListener(ChangeListener l) {
            // Ignore, will not generate any change event.
        }

        public void removeChangeListener(ChangeListener l) {
            // Ignore, will not generate any change event.
        }

        public Node node(String key) {
            if (KEY_EJBS.equals(key)) {
                EjbJar ejbModule = project.getAPIEjbJar(); 
                return J2eeProjectView.createEjbsView(ejbModule, project);
            }
            assert false: "No node for key: " + key; //NO18N
            return null;
        }

        public void addNotify() {
        }

        public void removeNotify() {
        }
    }           
}
