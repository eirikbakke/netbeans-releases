/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.docker.ui.build2;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.docker.api.DockerAction;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class BuildOptionsPanel implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private BuildOptionsVisual component;

    private WizardDescriptor wizard;

    public BuildOptionsPanel() {
        super();
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public BuildOptionsVisual getComponent() {
        if (component == null) {
            component = new BuildOptionsVisual();
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @NbBundle.Messages({
        "MSG_NonExistingDockerfile=The Dockerfile does not exist.",
        "MSG_NonRelativeDockerfile=The Dockerfile must be inside the build context."
    })
    @Override
    public boolean isValid() {
        // clear the error message
        wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        wizard.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);

        String buildContext = (String) wizard.getProperty(BuildImageWizard.BUILD_CONTEXT_PROPERTY);
        String dockerfile = component.getDockerfile();
        if (dockerfile == null) {
            dockerfile = buildContext+"/"+DockerAction.DOCKER_FILE;
        } else {
            dockerfile = buildContext+"/"+dockerfile;
        }
        FileSystem fs = (FileSystem) wizard.getProperty(BuildImageWizard.FILESYSTEM_PROPERTY);
        FileObject fo = fs.getRoot().getFileObject(dockerfile);

        // the last check avoids entires like Dockerfile/ to be considered valid files
        if (fo == null || !fo.isData() || !dockerfile.endsWith(fo.getNameExt())) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, Bundle.MSG_NonExistingDockerfile());
            return false;
        }
        FileObject build = fs.getRoot().getFileObject(buildContext);
        if (build == null || !FileUtil.isParentOf(build, fo)) {
            wizard.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, Bundle.MSG_NonRelativeDockerfile());
            return false;
        }
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        if (wizard == null) {
            wizard = wiz;
        }

        component.setBuildContext((String) wiz.getProperty(BuildImageWizard.BUILD_CONTEXT_PROPERTY));
        String dockerfile = (String) wiz.getProperty(BuildImageWizard.DOCKERFILE_PROPERTY);
        if (dockerfile == null) {
            dockerfile = DockerAction.DOCKER_FILE;
        }
        component.setDockerfile(dockerfile);
        Boolean pull = (Boolean) wiz.getProperty(BuildImageWizard.PULL_PROPERTY);
        component.setPull(pull != null ? pull : BuildImageWizard.PULL_DEFAULT);
        Boolean noCache = (Boolean) wiz.getProperty(BuildImageWizard.NO_CACHE_PROPERTY);
        component.setPull(noCache != null ? noCache : BuildImageWizard.NO_CACHE_DEFAULT);

        // XXX revalidate; is this bug?
        changeSupport.fireChange();
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(BuildImageWizard.DOCKERFILE_PROPERTY, component.getDockerfile());
        wiz.putProperty(BuildImageWizard.PULL_PROPERTY, component.isPull());
        wiz.putProperty(BuildImageWizard.NO_CACHE_PROPERTY, component.isNoCache());
        wiz.putProperty(BuildImageWizard.BUILD_ARGUMENTS_PROPERTY, component.getBuildargs());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        changeSupport.fireChange();
    }

}
