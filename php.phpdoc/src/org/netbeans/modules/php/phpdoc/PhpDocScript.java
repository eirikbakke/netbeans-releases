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

package org.netbeans.modules.php.phpdoc;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.phpdoc.ui.PhpDocPreferences;
import org.netbeans.modules.php.phpdoc.ui.options.PhpDocOptions;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class PhpDocScript extends PhpProgram {
    public static final String SCRIPT_NAME = "phpdoc"; // NOI18N
    public static final String SCRIPT_NAME_LONG = SCRIPT_NAME + FileUtils.getScriptExtension(true);

    public static final String OPTIONS_SUB_PATH = "PhpDoc"; // NOI18N

    private PhpDocScript(String command) {
        super(command);
    }

    /**
     * Get the default, <b>valid only</b> PhpDoc script.
     * @return the default, <b>valid only</b> PhpDoc script.
     * @throws InvalidPhpProgramException if PhpDoc script is not valid.
     */
    public static PhpDocScript getDefault() throws InvalidPhpProgramException {
        return getCustom(PhpDocOptions.getInstance().getPhpDoc());
    }

    /**
     * Get the default, <b>valid only</b> PhpDoc script.
     * @return the default, <b>valid only</b> PhpDoc script.
     * @throws InvalidPhpProgramException if PhpDoc script is not valid.
     */
    private static PhpDocScript getCustom(String command) throws InvalidPhpProgramException {
        String error = validate(command);
        if (error != null) {
            throw new InvalidPhpProgramException(error);
        }
        return new PhpDocScript(command);
    }

    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + OPTIONS_SUB_PATH; // NOI18N
    }

    @Override
    public String validate() {
        return FileUtils.validateScript(getProgram(), NbBundle.getMessage(PhpDocScript.class, "LBL_PhpDocScript"));
    }

    public void generateDocumentation(final PhpModule phpModule) {
        String phpDocTarget = PhpDocPreferences.getPhpDocTarget(phpModule, true);
        if (phpDocTarget == null) {
            // canceled
            return;
        }

        ExternalProcessBuilder processBuilder = getProcessBuilder()
                // from
                .addArgument("-d") // NOI18N
                .addArgument(FileUtil.toFile(phpModule.getSourceDirectory()).getAbsolutePath())
                // to
                .addArgument("-t") // NOI18N
                .addArgument(phpDocTarget)
                // title
                .addArgument("-ti") // NOI18N
                .addArgument(PhpDocPreferences.getPhpDocTitle(phpModule));
        ExecutionDescriptor executionDescriptor = getExecutionDescriptor()
                .frontWindow(false)
                .optionsPath(getOptionsPath());

        try {
            int status = executeAndWait(
                    processBuilder,
                    executionDescriptor,
                    NbBundle.getMessage(PhpDocScript.class, "LBL_GeneratingPhpDocForProject", phpModule.getDisplayName()));
            if (status == 0) {
                File index = new File(phpDocTarget, "index.html"); // NOI18N
                if (index.isFile()) {
                    // false for pdf e.g.
                    HtmlBrowser.URLDisplayer.getDefault().showURL(index.toURI().toURL());
                }
            }
        } catch (ExecutionException ex) {
            UiUtils.processExecutionException(ex, getOptionsPath());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
    }

    public static String validate(String command) {
        return new PhpDocScript(command).validate();
    }
}
