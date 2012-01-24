/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.j2ee.appclient;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.client.AppClient;
import org.netbeans.modules.j2ee.dd.api.client.AppClientMetadata;
import org.netbeans.modules.j2ee.dd.api.client.DDProvider;
import org.netbeans.modules.j2ee.dd.spi.MetadataUnit;
import org.netbeans.modules.j2ee.dd.spi.client.AppClientMetadataModelFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.maven.api.classpath.ProjectSourcesClassPathProvider;
import org.netbeans.modules.maven.j2ee.BaseEEModuleImpl;
import org.netbeans.modules.maven.j2ee.MavenJavaEEConstants;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Implementation of Application client functionality
 * 
 * @author Martin Janicek
 */
public class AppClientImpl extends BaseEEModuleImpl {
    
    private MetadataModel<AppClientMetadata> appClientMetadataModel;
    
    
    AppClientImpl(Project project, AppClientModuleProviderImpl provider) {
        super(project, provider, "application-client.xml", J2eeModule.CLIENT_XML); // NOI18N
    }
    
       
    @Override
    public J2eeModule.Type getModuleType() {
        return J2eeModule.Type.CAR;
    }
    
    @Override
    public FileObject getArchive() throws IOException {
        return getArchive(Constants.GROUP_APACHE_PLUGINS, "maven-acr-plugin", "acr", "jar"); // NOI18N
    }
    
    public Profile getJ2eeProfile() {
        String version = project.getLookup().lookup(AuxiliaryProperties.class).get(MavenJavaEEConstants.HINT_J2EE_VERSION, true);
        if (version != null) {
            return Profile.fromPropertiesString(version);
        }
        String ver = getModuleVersion();
//        if (AppClient.VERSION_6_0.equals(ver)) {
//            return Profile.JAVA_EE_6_FULL;
//        }
        return Profile.JAVA_EE_6_FULL;
    }
    
    @Override
    public String getModuleVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                AppClient ac = prov.getDDRoot(dd);
                String acVersion = ac.getVersion().toString();
                return acVersion;
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
//        //look in pom's config.
//        String version = PluginPropertyUtils.getPluginProperty(project,
//                Constants.GROUP_APACHE_PLUGINS, PLUGIN_APPCLIENT,
//                "????", "????"); //NOI18N
//        if (version != null) {
//            return version.trim();
//        }

        return AppClient.VERSION_6_0;
    }

    @Override
    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
        if (type == AppClientMetadata.class) {
            @SuppressWarnings("unchecked") // NOI18N
            MetadataModel<T> model = (MetadataModel<T>)getMetadataModel();
            return model;
        }
        return null;
    }
    
    public synchronized MetadataModel<AppClientMetadata> getMetadataModel() {
        if (appClientMetadataModel == null) {
            FileObject ddFO = getDeploymentDescriptor();
            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
            ProjectSourcesClassPathProvider cpProvider = project.getLookup().lookup(ProjectSourcesClassPathProvider.class);
            MetadataUnit metadataUnit = MetadataUnit.create(
                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
                // XXX: add listening on deplymentDescriptor
                ddFile);
            appClientMetadataModel = AppClientMetadataModelFactory.createMetadataModel(metadataUnit);
        }
        return appClientMetadataModel;
    }
}
