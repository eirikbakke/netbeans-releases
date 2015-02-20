/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * https://java.net/projects/gf-tooling/pages/License or LICENSE.TXT.
 * See the License for the specific language governing permissions
 * and limitations under the License.  When distributing the software,
 * include this License Header Notice in each file and include the License
 * file at LICENSE.TXT. Oracle designates this particular file as subject
 * to the "Classpath" exception as provided by Oracle in the GPL Version 2
 * section of the License file that accompanied this code. If applicable,
 * add the following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
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
package org.netbeans.modules.glassfish.tooling.admin;

import org.netbeans.modules.glassfish.tooling.TaskStateListener;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.netbeans.modules.glassfish.tooling.GlassFishIdeException;
import org.netbeans.modules.glassfish.tooling.data.GlassFishServer;

/**
 * GlassFish Server Deploy Command Entity.
 * <p>
 * Holds data for command. Objects of this class are created by API user.
 * <p>
 * @author Tomas Kraus, Peter Benedikovic
 */
@RunnerHttpClass(runner=RunnerHttpDeploy.class)
@RunnerRestClass(runner=RunnerRestDeploy.class)
public class CommandDeploy extends CommandTargetName {

    ////////////////////////////////////////////////////////////////////////////
    // Class attributes                                                       //
    ////////////////////////////////////////////////////////////////////////////

    /** Command string for deploy command. */
    private static final String COMMAND = "deploy";

    /** Error message for administration command execution exception .*/
    private static final String ERROR_MESSAGE
            = "Application deployment failed.";

    ////////////////////////////////////////////////////////////////////////////
    // Static methods                                                         //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Deploy task that deploys application on server.
     * <p/>
     * @param server      GlassFish server entity.
     * @param application File object representing archive or directory
     *                    to be deployed.
     * @param listener    Command execution events listener.
     * @return  Deploy task response.
     * @throws GlassFishIdeException When error occurred during administration
     *         command execution.
     */
    public static ResultString deploy(GlassFishServer server, File application,
            TaskStateListener listener) throws GlassFishIdeException {
        Command command = new CommandDeploy(null, null, application,
                null, null, null);
        Future<ResultString> future =
                ServerAdmin.<ResultString>exec(server, command, listener);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException
                | CancellationException ie) {
            throw new GlassFishIdeException(ERROR_MESSAGE, ie);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Instance attributes                                                    //
    ////////////////////////////////////////////////////////////////////////////

    /** File to deploy. */
    final File path;

    /** Deployed application context root. */
    final String contextRoot;

    /** Deployment properties. */
    final Map<String, String> properties;

    /** Deployment libraries. */
    final File[] libraries;

    /** Is this deployment of a directory? */
    final boolean dirDeploy;

    ////////////////////////////////////////////////////////////////////////////
    // Constructors                                                           //
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an instance of GlassFish server deploy command entity.
     * <p/>
     * @param name        Name of module/cluster/instance to modify.
     * @param target      Target GlassFish instance or cluster where
     *                    <code>name</code> is stored.
     * @param path        File to deploy.
     * @param contextRoot Deployed application context root.
     * @param properties  Deployment properties.
     * @param libraries   Not used in actual deploy command.
     */
    public CommandDeploy(final String name, final String target,
            final File path, final String contextRoot,
            final Map<String,String> properties, final File[] libraries) {
        super(COMMAND, name, target);
        this.path = path;
        this.contextRoot = contextRoot;
        this.properties = properties;
        this.libraries = libraries;
        this.dirDeploy = path.isDirectory();
    }

}
