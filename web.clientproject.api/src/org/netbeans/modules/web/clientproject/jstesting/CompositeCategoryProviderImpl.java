/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.web.clientproject.jstesting;

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.clientproject.api.jstesting.JsTestingProvider;
import org.netbeans.modules.web.clientproject.api.jstesting.JsTestingProviders;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Pair;

public final class CompositeCategoryProviderImpl implements ProjectCustomizer.CompositeCategoryProvider {

    private volatile Pair<ProjectCustomizer.Category, ProjectCustomizer.CompositeCategoryProvider> jsTestingInfo;


    @NbBundle.Messages("CompositeCategoryProviderImpl.testing.title=JavaScript Testing")
    @Override
    public ProjectCustomizer.Category createCategory(Lookup context) {
        Project project = context.lookup(Project.class);
        if (project == null) {
            throw new IllegalStateException("Project must be found in context: " + context);
        }
        initJsTestingInfo(context, project);
        if (jsTestingInfo == null) {
            // no category at all
            return null;
        }
        ProjectCustomizer.CompositeCategoryProvider categoryProvider = jsTestingInfo.second();
        assert categoryProvider != null : jsTestingInfo;
        return ProjectCustomizer.Category.create(
                JsTestingProviders.CUSTOMIZER_IDENT,
                Bundle.CompositeCategoryProviderImpl_testing_title(),
                null,
                new ProjectCustomizer.Category[] {jsTestingInfo.first()});
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        if (JsTestingProviders.CUSTOMIZER_IDENT.equals(category.getName())) {
            // XXX
            return new JPanel();
        }
        // js testing panel?
        if (jsTestingInfo != null) {
            if (category.equals(jsTestingInfo.first())) {
                ProjectCustomizer.CompositeCategoryProvider categoryProvider = jsTestingInfo.second();
                jsTestingInfo = null;
                return categoryProvider.createComponent(category, context);
            }
        }
        return null;
    }

    @CheckForNull
    private void initJsTestingInfo(Lookup context, Project project) {
        jsTestingInfo = null;
        JsTestingProvider testingProvider = JsTestingProviders.getDefault().getJsTestingProvider(project, false);
        if (testingProvider == null) {
            return;
        }
        ProjectCustomizer.CompositeCategoryProvider categoryProvider = testingProvider.createCustomizer(project);
        if (categoryProvider == null) {
            return;
        }
        jsTestingInfo = Pair.of(categoryProvider.createCategory(context), categoryProvider);
    }

}
