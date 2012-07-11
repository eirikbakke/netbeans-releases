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
package org.netbeans.modules.web.clientproject;

import java.net.URISyntaxException;
import java.net.URL;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.browser.api.BrowserSupport;
import org.netbeans.modules.web.clientproject.spi.ProjectConfigurationCustomizer;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @david
 * @author Jan Becicka
 */
@ProjectServiceProvider(
        service=ActionProvider.class,
        projectType=ProjectConfigurationCustomizer.PATH + "/browser")

public class BrowserActionProvider implements ActionProvider {

    private final ClientSideProject p;

    public BrowserActionProvider(Project p) {
        this.p = (ClientSideProject) p;
    }
    
    @Override
    public String[] getSupportedActions() {
        return new String[] {COMMAND_RUN};
    }

    @Override
    public void invokeAction(String command, Lookup context) throws IllegalArgumentException {
        FileObject fo = null;
        if (COMMAND_RUN.equals(command)) {
            fo = p.getProjectDirectory().getFileObject("index.html");
        } else if (COMMAND_RUN_SINGLE.equals(command)) {
            fo = getFile(context);
        }
        if (fo != null) {
            browseFile(p.getBrowserSupport(), fo);
        }
    }

    
    @Override
    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        Project prj = context.lookup(Project.class);
        ClientSideConfigurationProvider provider = prj.getLookup().lookup(ClientSideConfigurationProvider.class);
        if (provider.getActiveConfiguration()==null || "browser".equals(provider.getActiveConfiguration().getType())) {
            return true;
        }
        return false;
    }
    
    private FileObject getFile(Lookup context) {
        return context.lookup(FileObject.class);
    }

    private boolean isHTMLFile(FileObject fo) {
        return (fo != null && "html".equals(fo.getExt()));
    }
    
    private static void browseFile(BrowserSupport bs, FileObject fo) {
        URL url;
        String urlString;
        try {
            url = fo.toURL();
            urlString = url.toURI().toString();
            // XXXXX:
            urlString = urlString.replaceAll("file:/", "file:///");
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        bs.load(url, fo);
    }
}
