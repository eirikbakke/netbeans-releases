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

package org.netbeans.modules.apisupport.project.queries;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider.NbModuleType;
import org.netbeans.modules.apisupport.project.suite.SuiteProject;
import org.netbeans.modules.apisupport.project.ui.customizer.SuiteUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.loaders.CreateFromTemplateAttributesProvider;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

/**
 * Specifies license header for module and suite projects.
 */
public class TemplateAttributesProvider implements CreateFromTemplateAttributesProvider {
    private final NbModuleProject project;
    private final AntProjectHelper helper;
    private final boolean netbeansOrg;

    public TemplateAttributesProvider(NbModuleProject p, AntProjectHelper helper, boolean netbeansOrg) {
        this.project = p;
        this.helper = helper;
        this.netbeansOrg = netbeansOrg;
    }

    @Override
    public Map<String,?> attributesFor(DataObject template, DataFolder target, String name) {
        EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String license = props.getProperty("project.license"); // NOI18N

        if (license == null && netbeansOrg) {
            license = "cddl-netbeans-sun"; // NOI18N
        }
        if (license == null && project != null) {
            SuiteProject sp;
            try {
                sp = SuiteUtils.findSuite(project);
                if (sp != null) {
                    TemplateAttributesProvider tap = sp.getLookup().lookup(TemplateAttributesProvider.class);
                    return tap.attributesFor(template, target, name);
                }
            } catch (IOException ex) {
                // OK, ignore
            }
        }
        Map<String, String> values = new HashMap<String, String>();
        if (license != null) {
            values.put("license", license); // NOI18N
        }
        values.put("encoding", "UTF-8"); // NOI18N
        return Collections.singletonMap("project", values); // NOI18N
    }

}
