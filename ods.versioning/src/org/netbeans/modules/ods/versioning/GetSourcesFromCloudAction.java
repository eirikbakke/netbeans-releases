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
package org.netbeans.modules.ods.versioning;

import com.tasktop.c2c.server.scm.domain.ScmLocation;
import com.tasktop.c2c.server.scm.domain.ScmRepository;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.PasswordAuthentication;
import javax.swing.AbstractAction;
import org.netbeans.modules.ods.ui.api.CloudUiServer;
import org.netbeans.modules.ods.versioning.GetSourcesFromCloudPanel.GetSourcesInfo;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.netbeans.modules.ods.versioning.SourceAccessorImpl.ProjectAndRepository;
import org.netbeans.modules.ods.versioning.spi.ApiProvider;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

@ActionID(id = "org.netbeans.modules.ods.versioning.GetSourcesFromCloudAction", category = "Team")
@ActionRegistration(displayName = "#Actions/Team/org-netbeans-modules-ods-versioning-GetSourcesFromCloudAction.instance")
@ActionReference(path="Menu/Versioning/Team/ODS", position=400)
@Messages("Actions/Team/org-netbeans-modules-ods-versioning-GetSourcesFromCloudAction.instance=&Get Sources...")
public final class GetSourcesFromCloudAction extends AbstractAction {

    private ProjectAndRepository prjAndRepository;
    private SourceHandleImpl srcHandle;

    private String dialogTitle = NbBundle.getMessage(GetSourcesFromCloudAction.class, "GetSourcesFromCloudTitle");
    private String getOption = NbBundle.getMessage(GetSourcesFromCloudAction.class, "GetSourcesFromCloudAction.GetFromKenai.option");
    private String cancelOption = NbBundle.getMessage(GetSourcesFromCloudAction.class, "GetSourcesFromCloudAction.Cancel.option");
    
    public GetSourcesFromCloudAction () {
        this(null, null);
    }

    public GetSourcesFromCloudAction(ProjectAndRepository prjRepo, SourceHandleImpl src) {
        prjAndRepository = prjRepo;
        this.srcHandle = src;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object options[] = new Object[2];
        options[0] = getOption;
        options[1] = cancelOption;

        org.netbeans.modules.team.ui.spi.TeamUIUtils.activateTeamDashboard();

        GetSourcesFromCloudPanel getSourcesPanel = new GetSourcesFromCloudPanel(prjAndRepository);

        DialogDescriptor dialogDesc = new DialogDescriptor(getSourcesPanel, dialogTitle,
            true, options, options[0], DialogDescriptor.DEFAULT_ALIGN, null, null);

        Object option = DialogDisplayer.getDefault().notify(dialogDesc);
        
        if (options[0].equals(option)) {
            
            final GetSourcesInfo sourcesInfo = getSourcesPanel.getSelectedSourcesInfo();
            final ApiProvider prov = getSourcesPanel.getProvider();
            
            if (sourcesInfo == null || prov == null) {
                return;
            }

            final ScmRepository repository = sourcesInfo.repository;
            final String url = sourcesInfo.url;
            final PasswordAuthentication passwdAuth = repository.getScmLocation() == ScmLocation.CODE2CLOUD
                    ? CloudUiServer.forServer(sourcesInfo.projectHandle.getTeamProject().getServer()).getPasswordAuthentication()
                    : null;
            // XXX   UIUtils.logKenaiUsage("KENAI_HG_CLONE"); // NOI18N
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    NbPreferences.forModule(GetSourcesFromCloudAction.class).put("repository.scm.provider." + url, prov.getClass().getName()); //NOI18N
                    File cloneDest = prov.getSources(url, passwdAuth);
                    if (cloneDest != null && srcHandle != null) {
                        srcHandle.setWorkingDirectory(url, cloneDest);
                        srcHandle.refresh();
                    }
                }
            });
            // XXX store the project in preferrences, it will be shown as first for next Get From Kenai
        }
    }
    
}
