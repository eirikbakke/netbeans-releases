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
package org.netbeans.modules.hudson.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.hudson.api.HudsonInstance;
import org.netbeans.modules.hudson.api.HudsonJobBuild;
import org.netbeans.modules.hudson.api.HudsonMavenModuleBuild;
import org.netbeans.modules.hudson.impl.HudsonInstanceImpl;
import org.netbeans.modules.hudson.spi.BuilderConnector;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Action to display test failures.
 */
public class ShowFailures extends AbstractAction implements ContextAwareAction {

    private static ShowFailures INSTANCE = null;
    private final Lookup context;

    /**
     * Get the shared instance from system filesystem.
     */
    public static ShowFailures getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShowFailures();
        }
        return INSTANCE;
    }

    public ShowFailures() {
        this(Utilities.actionsGlobalContext());
    }

    @Messages("ShowFailures.label=Show Test Failures")
    private ShowFailures(Lookup context) {
        putValue(NAME, Bundle.ShowFailures_label());
        this.context = context;
    }

    /**
     * Check if failures for job build or maven module build can be shown. The
     * build has to be unstable, and a failure displayer has to be available.
     */
    private boolean canShowFailures(HudsonJobBuild build,
            HudsonMavenModuleBuild module) {
        HudsonInstance instance = build.getJob().getInstance();
        if (instance instanceof HudsonInstanceImpl) {
            if (module == null && !HudsonJobBuild.Result.UNSTABLE.equals(
                    build.getResult())) {
                return false;
            } else if (module != null) {
                boolean failed = false;
                switch (module.getColor()) {
                    case yellow:
                    case yellow_anime:
                        failed = true;
                }
                if (!failed) {
                    return false;
                }
            }
            BuilderConnector builderClient =
                    ((HudsonInstanceImpl) instance).getBuilderConnector();
            return builderClient.getFailureDisplayer() != null;
        }
        return false;
    }

    /**
     * Find maven module builds that are part of a job build but are not already
     * included in the context. It's used for prevention of duplicate opening of
     * test results.
     */
    private List<HudsonMavenModuleBuild> getExtraModuleBuilds(
            HudsonJobBuild build) {
        List<HudsonMavenModuleBuild> result =
                new LinkedList<HudsonMavenModuleBuild>();
        Collection<? extends HudsonMavenModuleBuild> alreadyIncludedBuilds =
                context.lookupAll(HudsonMavenModuleBuild.class);
        Set<String> alreadyIncludedURLs =
                new HashSet<String>(alreadyIncludedBuilds.size());
        for (HudsonMavenModuleBuild b : alreadyIncludedBuilds) {
            alreadyIncludedURLs.add(b.getUrl());
        }
        for (HudsonMavenModuleBuild m : build.getMavenModules()) {
            if (!alreadyIncludedURLs.contains(m.getUrl())) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * If there is at least one unstable build in the context, this action will
     * be enabled.
     */
    @Override
    public boolean isEnabled() {
        for (HudsonJobBuild job : context.lookupAll(HudsonJobBuild.class)) {
            if (canShowFailures(job, null)) {
                return true;
            }
        }
        for (HudsonMavenModuleBuild module
                : context.lookupAll(HudsonMavenModuleBuild.class)) {
            if (canShowFailures(module.getBuild(), module)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (HudsonJobBuild job : context.lookupAll(HudsonJobBuild.class)) {
            showFailures(job, null);
        }
        for (HudsonMavenModuleBuild module
                : context.lookupAll(HudsonMavenModuleBuild.class)) {
            showFailures(module.getBuild(), module);
        }
    }

    /**
     * Show failures in a job build or maven module build. If a maven module
     * build is not specified, but the job build contains some module build,
     * failures from all its module builds will be shown.
     */
    private void showFailures(HudsonJobBuild build,
            HudsonMavenModuleBuild moduleBuild) {

        if (!canShowFailures(build, moduleBuild)) {
            return;
        }
        HudsonInstance hudsonInstance = build.getJob().getInstance();
        if (hudsonInstance instanceof HudsonInstanceImpl) {
            HudsonInstanceImpl hudsonInstanceImpl =
                    (HudsonInstanceImpl) hudsonInstance;
            BuilderConnector builderClient =
                    hudsonInstanceImpl.getBuilderConnector();
            BuilderConnector.FailureDisplayer failureDisplayer =
                    builderClient.getFailureDisplayer();
            if (failureDisplayer != null) {
                if (moduleBuild != null) {
                    failureDisplayer.showFailures(moduleBuild);
                } else {
                    if (build.getMavenModules().isEmpty()) {
                        failureDisplayer.showFailures(build);
                    } else {
                        for (HudsonMavenModuleBuild extraModule
                                : getExtraModuleBuilds(build)) {
                            failureDisplayer.showFailures(extraModule);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new ShowFailures(actionContext);
    }
}
