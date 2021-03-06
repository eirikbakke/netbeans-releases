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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.editor.facelets;

import com.sun.faces.spi.ConfigurationResourceProvider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelAction;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModelException;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 * provider URLs of libraries defined in javax.faces.FACELETS_LIBRARIES context param of deployment descriptor
 *
 * @author marekfukala
 */
public class WebFaceletTaglibResourceProvider implements ConfigurationResourceProvider {

    private WebModule wm;

    private static final String FACELETS_LIBRARIES_OLD_PROPERTY_NAME = "facelets.LIBRARIES"; //NOI18N
    private static final String FACELETS_LIBRARIES_PROPERTY_NAME = "javax.faces.FACELETS_LIBRARIES"; //NOI18N

    public WebFaceletTaglibResourceProvider(WebModule wm) {
        this.wm = wm;
    }

    @Override
    public Collection<URI> getResources(ServletContext ignored) {
        try {
            MetadataModel<WebAppMetadata> model = wm.getMetadataModel();
            String faceletsLibrariesList = null;
            if(model != null && model.isReady()) {
                // This is executed just in case that the model is ready. Otherwise it leads to uncancelable work.
                // Another reports against non-consistents result from the first and second invocation should be
                // consulted with tzetula for better options how to fix this. Related issue is bug #232878.
                faceletsLibrariesList = model.runReadAction(new MetadataModelAction<WebAppMetadata, String>() {
                    @Override
                    public String run(WebAppMetadata metadata) throws Exception {
                        //TODO can be init param specified by some annotation or the dd must be present?
                        WebApp ddRoot = metadata.getRoot();
                        if (ddRoot != null) {
                            InitParam[] contextParams = ddRoot.getContextParam();
                            for (InitParam param : contextParams) {
                                if (FACELETS_LIBRARIES_PROPERTY_NAME.equals(param.getParamName()) ||
                                        FACELETS_LIBRARIES_OLD_PROPERTY_NAME.equals(param.getParamName())) {
                                    return param.getParamValue();
                                }
                            }
                        }
                        return null;
                    }
                });
            }

            FileObject webModuleRoot = wm.getDocumentBase();
            FileObject webInfBase = wm.getWebInf() == null ? null : wm.getWebInf().getParent();
            Collection<URI> librariesURIs = new ArrayList<>();
            if(faceletsLibrariesList != null) {
                StringTokenizer st = new StringTokenizer(faceletsLibrariesList, ";");
                while(st.hasMoreTokens()) {
                    String libraryPath = st.nextToken();
                    FileObject libraryFO = null;
                    if(webInfBase != null) {
                        //try to resolve according to the web-inf parent
                        libraryFO = webInfBase.getFileObject(libraryPath);
                    }
                    if(libraryFO == null) {
                        //try to resolve according to the web module root,
                        //in most cases a folder identical to the web-inf's parent folder
                        //but may not always be true
                        libraryFO = webModuleRoot != null ? webModuleRoot.getFileObject(libraryPath) : null; //WebModule may have no root if broken
                    }
                    if(libraryFO != null) {
                        URL url = URLMapper.findURL(libraryFO, URLMapper.INTERNAL);
                        if(url != null) {
                            try {
                                librariesURIs.add(new URI(url.toExternalForm()));
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(WebFaceletTaglibResourceProvider.class.getName()).log(Level.INFO, null, ex);
                            }
                        }
                    }
                }
            }
            return librariesURIs;

        } catch (MetadataModelException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
