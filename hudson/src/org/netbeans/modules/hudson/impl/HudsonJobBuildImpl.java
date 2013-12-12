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

package org.netbeans.modules.hudson.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.api.HudsonJob.Color;
import org.netbeans.modules.hudson.api.HudsonJobBuild;
import org.netbeans.modules.hudson.api.HudsonMavenModuleBuild;
import org.netbeans.modules.hudson.api.ui.ConsoleDataDisplayer;
import org.netbeans.modules.hudson.api.ui.FailureDataDisplayer;
import org.netbeans.modules.hudson.api.ui.OpenableInBrowser;
import static org.netbeans.modules.hudson.impl.Bundle.*;
import org.netbeans.modules.hudson.impl.HudsonJobImpl.HudsonMavenModule;
import org.netbeans.modules.hudson.spi.BuilderConnector;
import org.netbeans.modules.hudson.spi.ConsoleDataDisplayerImpl;
import org.netbeans.modules.hudson.spi.FailureDataDisplayerImpl;
import org.netbeans.modules.hudson.spi.HudsonJobChangeItem;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle.Messages;

public class HudsonJobBuildImpl implements HudsonJobBuild, OpenableInBrowser {

    private static final Logger LOG = Logger.getLogger(HudsonJobBuildImpl.class.getName());

    private final HudsonJobImpl job;
    private final int build;
    private boolean building;
    private Result result;
    private final BuilderConnector connector;

    HudsonJobBuildImpl(BuilderConnector connector, HudsonJobImpl job, int build, boolean building, Result result) {
        this.connector = connector;
        this.job = job;
        this.build = build;
        this.building = building;
        this.result = result;
    }
    
    public HudsonJob getJob() {
        return job;
    }

    public int getNumber() {
        return build;
    }

    public String getUrl() {
        return job.getUrl() + build + "/"; // NOI18N
    }

    public @Override String toString() {
        return getUrl();
    }

    public @Override boolean isBuilding() {
        getResult();
        return building;
    }
    
    public @Override synchronized Result getResult() {
        if (result == null && !building) {
            AtomicBoolean _building = new AtomicBoolean();
            AtomicReference<Result> _result = new AtomicReference<Result>(Result.NOT_BUILT);
            connector.getJobBuildResult(this, _building, _result);
            building = _building.get();
            result = _result.get();
        }
        return result != null ? result : Result.NOT_BUILT;
    }

    private Collection<? extends HudsonJobChangeItem> changes;
    public Collection<? extends HudsonJobChangeItem> getChanges() {
        if (changes == null || /* #171978 */changes.isEmpty()) {
            changes = connector.getJobBuildChanges(this);
        }
        return changes;
    }

    public FileSystem getArtifacts() {
        return job.getInstance().getArtifacts(this);
    }

    public Collection<? extends HudsonMavenModuleBuild> getMavenModules() {
        List<HudsonMavenModuleBuildImpl> modules = new ArrayList<HudsonMavenModuleBuildImpl>();
        for (HudsonJobImpl.HudsonMavenModule module : job.mavenModules) {
            modules.add(new HudsonMavenModuleBuildImpl(module));
        }
        return modules;
    }

    @Messages({"# {0} - job/module display name", "# {1} - build number", "HudsonJobBuildImpl.display_name={0} #{1,number,#}"})
    public String getDisplayName() {
        return HudsonJobBuildImpl_display_name(job.getDisplayName(), getNumber());
    }

    private ConsoleDataDisplayer createDisplayerFromImpl(
            final ConsoleDataDisplayerImpl displayerImpl) {
        return new ConsoleDataDisplayer() {

            @Override
            public void open() {
                displayerImpl.open();
            }

            @Override
            public boolean writeLine(String line) {
                return displayerImpl.writeLine(line);
            }

            @Override
            public void close() {
                displayerImpl.close();
            }
        };
    }

    private FailureDataDisplayer createDisplayerFromImpl(
            final FailureDataDisplayerImpl displayerImpl) {
        return new FailureDataDisplayer() {

            @Override
            public void open() {
                displayerImpl.open();
            }

            @Override
            public void showSuite(FailureDataDisplayer.Suite suite) {
                displayerImpl.showSuite(suite);
            }

            @Override
            public void close() {
                displayerImpl.close();
            }
        };
    }

    @Override
    public boolean canShowConsole() {
        return connector.getConsoleDataProvider() != null;
    }

    @Override
    public void showConsole(ConsoleDataDisplayerImpl displayer) {
        BuilderConnector.ConsoleDataProvider cd = connector.getConsoleDataProvider();
        if (cd != null) {
            cd.showConsole(this, createDisplayerFromImpl(displayer));
        }
    }

    @Override
    public boolean canShowFailures() {
        return connector.getFailureDataProvider() != null;
    }

    @Override
    public void showFailures(FailureDataDisplayerImpl displayer) {
        BuilderConnector.FailureDataProvider fd = connector.getFailureDataProvider();
        if (fd != null) {
            fd.showFailures(this, createDisplayerFromImpl(displayer));
        }
    }

    private final class HudsonMavenModuleBuildImpl implements HudsonMavenModuleBuild, OpenableInBrowser {

        private final HudsonJobImpl.HudsonMavenModule module;

        HudsonMavenModuleBuildImpl(HudsonMavenModule module) {
            this.module = module;
        }

        public String getName() {
            return module.name;
        }

        public String getDisplayName() {
            return module.displayName;
        }

        public Color getColor() {
            return module.color;
        }

        public String getUrl() {
            return module.url + build + "/"; // NOI18N
        }

        public HudsonJobBuild getBuild() {
            return HudsonJobBuildImpl.this;
        }

        public FileSystem getArtifacts() {
            return job.getInstance().getArtifacts(this);
        }

        public @Override String toString() {
            return getUrl();
        }

        public String getBuildDisplayName() {
            return HudsonJobBuildImpl_display_name(getDisplayName(), getNumber());
        }

        @Override
        public boolean canShowConsole() {
            return getBuild().canShowConsole();
        }

        @Override
        public void showConsole(ConsoleDataDisplayerImpl displayer) {
            BuilderConnector.ConsoleDataProvider cd = connector.getConsoleDataProvider();
            if (cd != null) {
                cd.showConsole(this, createDisplayerFromImpl(displayer));
            }
        }

        @Override
        public boolean canShowFailures() {
            return getBuild().canShowFailures();
        }

        @Override
        public void showFailures(FailureDataDisplayerImpl displayer) {
            BuilderConnector.FailureDataProvider fp = connector.getFailureDataProvider();
            if (fp != null) {
                fp.showFailures(this, createDisplayerFromImpl(displayer));
            }
        }
    }

}
