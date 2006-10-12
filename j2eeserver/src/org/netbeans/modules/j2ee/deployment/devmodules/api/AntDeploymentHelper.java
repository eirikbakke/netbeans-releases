/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.j2ee.deployment.devmodules.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.deploy.shared.ModuleType;
import org.netbeans.modules.j2ee.deployment.impl.ServerInstance;
import org.netbeans.modules.j2ee.deployment.impl.ServerRegistry;
import org.netbeans.modules.j2ee.deployment.plugins.api.AntDeploymentProvider;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Helps to generate Ant deployment build scripts.
 *
 * @author sherold
 *
 * @since 1.18
 */
public final class AntDeploymentHelper {
    
    /**
     * Generates the Ant deployment build script for the given module type for 
     * the specified server instance to the specified file. If the specified 
     * serverInstanceID is null or no server instance of the specified ID exists 
     * a default deployment build script will be generated.
     * <p>
     * The Ant deployment build script requires the following properties to be
     * defined.
     * <ul>
     * <li><code>deploy.ant.properties.file</code> - Path to the server instance 
     * specific deployment properties file, see {@link #getDeploymentPropertiesFile}.
     * <li><code>deploy.ant.archive</code> - The deployable archive.
     * <li><code>deploy.ant.resource.dir</code> - The server resources directory.
     * <li><code>deploy.ant.enabled</code> - The Ant deployment targets should be
     * executed only if this property has been set.
     * </ul>
     * <p>
     * The Ant deployment build script is bound to provide the following targets.
     * <ul>
     * <li><code>-deploy-ant</code> - Deploys the deployable archive defined by the 
     * <code>deploy.ant.archive</code> property. If the deployable archive is a web 
     * module or an enterprise application with a web module the 
     * <code>deploy.ant.client.url</code> property is set by this target.
     * <li><code>-undeploy-ant</code> - Undeploys the deployable archive defined 
     * by the <code>deploy.ant.archive</code> property.
     * </ul>
     *
     * @param file the file to which the deployment build script will be generated.
     *             If the file does not exist, it will be created.
     * @param moduleType the module type the build script should handle. Use the
     *                   constants defined in the {@link org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule}.
     * @param serverInstanceID the server instance for which the build script will
     *                         be generated.
     * @throws IOException if a problem during generating the build script occurs.
     */
    public static void writeDeploymentScript(File file, Object moduleType, String serverInstanceID) 
    throws IOException {
        AntDeploymentProvider provider = null;
        if (serverInstanceID != null) {
            ServerInstance si = ServerRegistry.getInstance().getServerInstance(serverInstanceID);
            if (si != null) {
                provider = si.getAntDeploymentProvider();
            }
        }
        file.createNewFile();
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(file));
        FileLock lock = fo.lock();
        try {
            OutputStream os = fo.getOutputStream(lock);
            try {
                if (provider == null) {
                    InputStream is = ServerInstance.class.getResourceAsStream("resources/default-ant-deploy.xml"); // NOI18N
                    try {
                        FileUtil.copy(is, os);
                    } finally {
                        is.close();
                    }
                } else {
                    provider.writeDeploymentScript(os, moduleType);
                }
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    /**
     * Returns the server instance specific deployment properties file used by 
     * the deployment build script generated by the {@link #writeDeploymentScript(File,Object,String)}. 
     *
     * @param serverInstanceID specifies the server instance.
     *
     * @return the deployment properties file for the specified server instance, 
     *         if such instance exists and supports Ant deployment, null otherwise.
     *
     * @throws NullPointerException if the specified serverInstanceID is null.
     */
    public static File getDeploymentPropertiesFile(String serverInstanceID) {
        if (serverInstanceID == null) {
            throw new NullPointerException("The serverInstanceID must not be null"); // NOI18N
        }
        ServerInstance si = ServerRegistry.getInstance().getServerInstance(serverInstanceID);
        if (si == null) {
            return null;
        }
        AntDeploymentProvider sup = si.getAntDeploymentProvider();
        return sup == null ? null : sup.getDeploymentPropertiesFile();
    }
}
