/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.clientproject.ui.customizer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.netbeans.modules.editor.indent.project.api.Customizers;
import org.netbeans.modules.web.clientproject.ClientSideProjectType;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibraries;
import org.netbeans.modules.web.clientproject.api.jslibs.JavaScriptLibrarySelectionPanel;
import org.netbeans.modules.web.common.api.CssPreprocessors;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Becicka
 */
public class CompositePanelProviderImpl implements ProjectCustomizer.CompositeCategoryProvider {

    public static final String SOURCES = "SOURCES"; // NOI18N
    public static final String RUN = "RUN"; // NOI18N

    private final String name;

    public CompositePanelProviderImpl(String name) {
        this.name = name;
    }

    @NbBundle.Messages({
        "CompositePanelProviderImpl.sources.title=Sources",
        "CompositePanelProviderImpl.run.title=Run"
    })
    @Override
    public Category createCategory(Lookup context) {
        ProjectCustomizer.Category category = null;
        if (SOURCES.equals(name)) {
            category = ProjectCustomizer.Category.create(
                    SOURCES,
                    Bundle.CompositePanelProviderImpl_sources_title(),
                    null);
        } else if (RUN.equals(name)) {
            category = ProjectCustomizer.Category.create(
                    RUN,
                    Bundle.CompositePanelProviderImpl_run_title(),
                    null);
        }
        assert category != null : "No category for name: " + name; //NOI18N
        return category;
    }

    @Override
    public JComponent createComponent(Category category, Lookup context) {
        String categoryName = category.getName();
        final ClientSideProjectProperties uiProperties = context.lookup(ClientSideProjectProperties.class);
        if (SOURCES.equals(categoryName)) {
            return new SourcesPanel(category, uiProperties);
        } else if (RUN.equals(categoryName)) {
            return new RunPanel(category, uiProperties);
        }
        assert false : "No component found for " + category.getDisplayName(); //NOI18N
        return new JPanel();
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = ClientSideProjectType.TYPE,
            position = 100)
    public static CompositePanelProviderImpl createSources() {
        return new CompositePanelProviderImpl(SOURCES);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = ClientSideProjectType.TYPE,
            position = 150)
    public static ProjectCustomizer.CompositeCategoryProvider createCssPreprocessors() {
        return CssPreprocessors.getDefault().createCustomizer();
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = ClientSideProjectType.TYPE,
            position = 300)
    public static CompositePanelProviderImpl createRunConfigs() {
        return new CompositePanelProviderImpl(RUN);
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
            projectType = ClientSideProjectType.TYPE,
            position = 200)
    public static ProjectCustomizer.CompositeCategoryProvider createJavaScriptFiles() {
        return JavaScriptLibraries.createCustomizer(new JavaScriptLibraries.CustomizerSupport() {
            @Override
            public File getWebRoot(Lookup context) {
                ClientSideProjectProperties projectProperties = context.lookup(ClientSideProjectProperties.class);
                assert projectProperties != null;
                return projectProperties.getResolvedSiteRootFolder();
            }
            @Override
            public void setLibrariesFolder(Lookup context, String librariesFolder) {
                assert librariesFolder != null;
                ClientSideProjectProperties projectProperties = context.lookup(ClientSideProjectProperties.class);
                assert projectProperties != null;
                projectProperties.setJsLibFolder(librariesFolder);
            }
            @Override
            public void setSelectedLibraries(Lookup context, List<JavaScriptLibrarySelectionPanel.SelectedLibrary> selectedLibraries) {
                assert selectedLibraries != null;
                ClientSideProjectProperties projectProperties = context.lookup(ClientSideProjectProperties.class);
                assert projectProperties != null;
                projectProperties.setNewJsLibraries(selectedLibraries);
            }
        });
    }

    @ProjectCustomizer.CompositeCategoryProvider.Registration(
        projectType = ClientSideProjectType.TYPE,
        position = 1000
    )
    public static ProjectCustomizer.CompositeCategoryProvider createFormatting() {
        return Customizers.createFormattingCategoryProvider(Collections.singletonMap(
                "allowedMimeTypes", "text/html,text/css,text/javascript,text/x-json")); // NOI18N
    }

}
