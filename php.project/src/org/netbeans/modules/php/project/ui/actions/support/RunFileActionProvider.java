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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.project.ui.actions.support;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.project.util.PhpProjectUtils;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

// heavily inspired from ruby :)
@ServiceProvider(service = ActionProvider.class)
public class RunFileActionProvider implements ActionProvider {

    // store for one session
    private static final Map<File, RunFileArgs> ARGS_FOR_FILE = new HashMap<File, RunFileArgs>();

    @Override
    public String[] getSupportedActions() {
        return new String[] {ActionProvider.COMMAND_RUN_SINGLE, ActionProvider.COMMAND_DEBUG_SINGLE};
    }

    @Override
    public boolean isActionEnabled(String command, Lookup context) {
        Project p = context.lookup(Project.class);
        if (p != null) {
            return false;
        }
        Collection<? extends DataObject> files = context.lookupAll(DataObject.class);
        if (files.isEmpty()) {
            return false;
        }
        for (DataObject d : files) {
            if (!FileUtils.isPhpFile(d.getPrimaryFile())) {
                return false;
            }
        }
        if (ActionProvider.COMMAND_DEBUG_SINGLE.equals(command)
                && XDebugStarterFactory.getInstance() == null) {
            return false;
        }
        return true;
    }

    @Override
    public void invokeAction(String command, Lookup context) {
        boolean debug = ActionProvider.COMMAND_DEBUG_SINGLE.equals(command);
        for (DataObject dataObject : context.lookupAll(DataObject.class)) {
            File file = FileUtil.toFile(dataObject.getPrimaryFile());
            if (file != null) {
                PhpProjectUtils.saveFile(dataObject);
                runFile(file, debug);
            }
        }
    }

    private void runFile(File file, boolean debug) {
        RunFileArgs existing = ARGS_FOR_FILE.get(file);
        if (existing != null && !existing.displayDialog) {
            doRun(file, existing, debug);
            return;
        }
        if (existing == null) {
            // init work dir
            existing = new RunFileArgs(null, file.getParent(), null, true);
        }

        RunFileArgs runFileArgs = RunFilePanel.open(existing, file, debug);
        if (runFileArgs == null) {
            return;
        }
        ARGS_FOR_FILE.put(file, runFileArgs);
        doRun(file, runFileArgs, debug);
    }

    private void doRun(File file, RunFileArgs args, boolean debug) {
        RunFileScriptProvider provider = new RunFileScriptProvider(file, args);
        if (debug) {
            new DebugScript(provider).run();
        } else {
            new RunScript(provider).run();
        }
    }

    private static final class RunFileScriptProvider extends DefaultScriptProvider {
        private final RunFileArgs args;

        public RunFileScriptProvider(File file, RunFileArgs args) {
            super(file);
            this.args = args;
        }

        @Override
        public ExternalProcessBuilder getProcessBuilder() {
            ExternalProcessBuilder builder = program.getProcessBuilder();
            for (String param : Utilities.parseParameters(args.getPhpOpts())) {
                builder = builder.addArgument(param);
            }
            builder = builder.addArgument(file.getAbsolutePath());
            for (String param : Utilities.parseParameters(args.getRunArgs())) {
                builder = builder.addArgument(param);
            }
            builder = builder.workingDirectory(new File(args.getWorkDir()));
            return builder;
        }
    }

    /**
     * Holds the args last given for running (file specific).
     */
    static final class RunFileArgs {
        private final String runArgs;
        private final String workDir;
        private final String phpOpts;
        private final boolean displayDialog;

        public RunFileArgs(String runArgs, String workDir, String phpOpts, boolean displayDialog) {
            this.runArgs = runArgs;
            this.workDir = workDir;
            this.phpOpts = phpOpts;
            this.displayDialog = displayDialog;
        }

        public String getRunArgs() {
            return runArgs;
        }

        public boolean displayDialog() {
            return displayDialog;
        }

        public String getWorkDir() {
            return workDir;
        }

        public String getPhpOpts() {
            return phpOpts;
        }
    }
}
