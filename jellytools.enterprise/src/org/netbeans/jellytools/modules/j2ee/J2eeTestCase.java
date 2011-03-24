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
package org.netbeans.jellytools.modules.j2ee;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.modules.j2ee.nodes.GlassFishV3ServerNode;
import org.netbeans.jellytools.modules.j2ee.nodes.J2eeServerNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.junit.NbModuleSuite;
import static org.netbeans.jellytools.modules.j2ee.J2eeTestCase.Server.*;
import static org.netbeans.junit.NbModuleSuite.Configuration;

/**
 * Registeres application servers to IDE and adds test cases to suite only
 * if requested server is available.
 * 
 * @author Jindrich Sedek
 * @author Jiri Skrivanek
 */
public class J2eeTestCase extends JellyTestCase {

    private static final String PID_FILE_PREFIX = "J2EE_TEST_CASE_PID_FILE";
    private static final String JBOSS_PATH = "org.netbeans.modules.j2ee.jboss4.installRoot";
    private static final String GLASSFISH_HOME = "glassfish.home";
    private static final String TOMCAT_HOME = "tomcat.home";
    private static final String JBOSS_HOME = "jboss.home";
    private static final Logger LOG = Logger.getLogger(J2eeTestCase.class.getName());
    private static boolean serversLogged = false;
    private static List<Server> alreadyRegistered = new ArrayList<Server>();
    /** Used in self test to verify registration. */
    static boolean isSelfTest = false;

    public J2eeTestCase(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createPid();
    }

    /**
     * Create a temp file starting with J2EE_TEST_CASE_PID_FILE and ending with
     * the pid of the test process. The pid is used by hudson to print stacktrace
     * before aborting build because of timeout.
     *
     * @throws java.io.IOException
     */
    private void createPid() throws IOException {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        pid = pid.substring(0, pid.indexOf('@'));
        String tmpDirPath = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpDirPath);
        for (String file : tmpDir.list()) {
            if (file.startsWith(PID_FILE_PREFIX)) {
                if (!(new File(tmpDir, file).delete())) {
                    LOG.log(Level.WARNING, "File ''{0}{1}{2}'' not successfully deleted!", new Object[]{tmpDirPath, File.pathSeparator, file});
                }
            }
        }
        if (!(new File(tmpDir, PID_FILE_PREFIX + pid).createNewFile())) {
            LOG.log(Level.WARNING, "File ''{0}{1}" + PID_FILE_PREFIX + "{2}'' not successfully created!", new Object[]{tmpDirPath, File.pathSeparator, pid});
        }
    }

    /** 
     * TODO - check if it is still functional.
     */
    private static void registerJBoss() {
        String jbossPath = getServerHome(JBOSS);
        if (isValidPath(jbossPath)) {
            LOG.log(Level.INFO, "Setting server path {0}", jbossPath);
            System.setProperty(JBOSS_PATH, jbossPath);
            alreadyRegistered.add(JBOSS);
        }
    }

    /** Returns paths to server home set by system property or null if not defined.
     * @param server requested server
     * @return path to server home or null if not defined
     */
    private static String getServerHome(Server server) {
        switch (server) {
            case JBOSS:
                return System.getProperty(JBOSS_HOME);
            case GLASSFISH:
                return System.getProperty(GLASSFISH_HOME);
            case TOMCAT:
                return System.getProperty(TOMCAT_HOME);
        }
        return null;
    }

    /** Returns true if given path points to existing folder.
     * @param path path to validate
     * @return true if given path points to existing folder, false otherwise.
     */
    private static boolean isValidPath(String path) {
        if (path == null) {
            return false;
        }
        File f = new File(path);
        if (f.isDirectory()) {
            LOG.log(Level.INFO, "{0} - is valid directory", path);
            return true;
        } else {
            if (!f.exists()) {
                LOG.log(Level.INFO, "{0} - does not exists!", path);
            } else {
                LOG.log(Level.INFO, "{0} - exists, but it is not a directory!", path);
            }
            return false;
        }
    }

    /**
     *
     * Create all modules suite.
     *
     * @param server server needed for the suite
     * @param clazz class object to create suite for
     * @param testNames test names to add into suite
     * @return executable Test instance
     */
    protected static Test createAllModulesServerSuite(Server server, Class<? extends TestCase> clazz, String... testNames) {
        Configuration result = NbModuleSuite.createConfiguration(clazz);
        result = addServerTests(server, result, testNames).enableModules(".*").clusters(".*");
        return NbModuleSuite.create(result);
    }

    /**
     * Add tests into configuration. Tests are added only if it's sure there
     * is some server registered in the IDE.
     *
     * @param conf test configuration
     * @param testNames names of added tests
     * @return clone of the test configuration
     */
    protected static Configuration addServerTests(Configuration conf, String... testNames) {
        return addServerTests(ANY, conf, testNames);
    }

    /**
     * Add tests into configuration.
     * Tests are added only if there is the server instance registered in the
     * IDE.
     *
     * @param server server that is needed by tests
     * @param conf test configuration
     * @param testNames names of added tests
     * @return clone of the test configuration
     */
    protected static Configuration addServerTests(Server server, Configuration conf, String... testNames) {
        return addServerTests(server, conf, null, testNames);
    }

    /**
     * Add tests into configuration.
     * Tests are added only if there is the server instance registered in the
     * IDE.
     *
     * @param server server that is needed by tests
     * @param conf test configuration
     * @param clazz tested class
     * @param testNames names of added tests
     * @return clone of the test configuration
     */
    protected static Configuration addServerTests(Server server, Configuration conf, Class<? extends TestCase> clazz, String... testNames) {
        if (isRegistered(server)) {
            LOG.info("adding server tests");
            return addTest(conf, clazz, testNames);
        } else {
            if (server.equals(GLASSFISH) || server.equals(ANY)) {
                registerGlassFish();
                if (isRegistered(GLASSFISH)) {
                    return addTest(conf, clazz, testNames);
                }
            }
            if (server.equals(TOMCAT) || server.equals(ANY)) {
                registerTomcat();
                if (isRegistered(TOMCAT)) {
                    return addTest(conf, clazz, testNames);
                }
            }
            if (server.equals(JBOSS) || server.equals(ANY)) {
                registerJBoss();
                if (isRegistered(JBOSS)) {
                    return addTest(conf, clazz, testNames);
                }
            }
            LOG.info("no server to add tests");
            if (!serversLogged) {
                serversLogged = true;
                LOG.log(Level.INFO, "{0}={1}", new String[]{JBOSS_HOME, getServerHome(JBOSS)});
                LOG.log(Level.INFO, "{0}={1}", new String[]{TOMCAT_HOME, getServerHome(TOMCAT)});
                LOG.log(Level.INFO, "{0}={1}", new String[]{GLASSFISH_HOME, getServerHome(GLASSFISH)});
            }
            try {
                return conf.addTest("testEmpty");
            } catch (IllegalStateException exc) {
                //empty configuration
                return conf.addTest(J2eeTestCase.class, "testEmpty");
            }
        }
    }

    /**
     * Returns <code>true</code> if given server is already registered in the IDE,
     * <code>false</code> otherwise
     * @param server server to decide about
     * @return <code>true</code> if the <code>server</code> is registered
     */
    protected static boolean isRegistered(Server server) {
        if (server.equals(ANY)) {
            return !alreadyRegistered.isEmpty();
        } else {
            return alreadyRegistered.contains(server);
        }
    }

    /**
     * Returns J2eeServerNode for given server
     *
     * @param server
     * @return J2eeServerNode for given server
     */
    protected J2eeServerNode getServerNode(Server server) {
        if (!isRegistered(server)) {
            throw new IllegalArgumentException("Server is not registred in IDE");
        }
        switch (server) {
            case GLASSFISH:
                return GlassFishV3ServerNode.invoke();
            case JBOSS:
                return J2eeServerNode.invoke("JBoss");
            case TOMCAT:
                return J2eeServerNode.invoke("Tomcat");
            case ANY:
                for (Server serv : Server.values()) {
                    if (serv.equals(ANY)) {
                        continue;
                    }
                    if (isRegistered(serv)) {
                        return getServerNode(serv);
                    }
                }
                throw new IllegalArgumentException("No server is registred in IDE");
            default:
                throw new IllegalArgumentException("Unsupported server");
        }
    }

    public static enum Server {

        TOMCAT, GLASSFISH, GLASSFISH_V3, JBOSS, ANY
    }

    /**
     * Empty test is executed while there is missing server and other tests would
     * fail because of missing server.
     */
    public void testEmpty() {
        // nothing to do
    }

    private static Configuration addTest(Configuration conf, Class<? extends TestCase> clazz, String... testNames) {
        if ((testNames == null) || (testNames.length == 0)) {
            return conf;
        }
        if (clazz == null) {
            return conf.addTest(testNames);
        } else {
            return conf.addTest(clazz, testNames);
        }
    }

    /**
     * Resolve missing server. This method should be called after opening some project.
     * If the Missing server dialog appears, it's closed and first server from
     * project properties is used to resolve the missing server problem.
     *
     * Project build script regeneration dialog is closed as well if it appears.
     * @param projectName name of project
     */
    protected void resolveServer(String projectName) {
        waitScanFinished();
        String openProjectTitle = Bundle.getString("org.netbeans.modules.j2ee.common.ui.Bundle", "MSG_Broken_Server_Title");
        if (JDialogOperator.findJDialog(openProjectTitle, true, true) != null) {
            new NbDialogOperator(openProjectTitle).close();
            LOG.info("Resolving server");
            // open project properties
            ProjectsTabOperator.invoke().getProjectRootNode(projectName).properties();
            // "Project Properties"
            String projectPropertiesTitle = Bundle.getStringTrimmed("org.netbeans.modules.web.project.ui.customizer.Bundle", "LBL_Customizer_Title");
            NbDialogOperator propertiesDialogOper = new NbDialogOperator(projectPropertiesTitle);
            // select "Run" category
            new Node(new JTreeOperator(propertiesDialogOper), "Run").select();
            // set default server
            new JComboBoxOperator(propertiesDialogOper).setSelectedIndex(0);
            propertiesDialogOper.ok();
            // if setting default server, it scans server jars; otherwise it continues immediatelly
            waitScanFinished();
        }
        String editPropertiesTitle = Bundle.getStringTrimmed("org.netbeans.modules.web.project.Bundle", "TXT_BuildImplRegenerateTitle");
        int count = 0;
        while ((JDialogOperator.findJDialog(editPropertiesTitle, true, true) != null) && (count < 10)) {
            count++;
            JDialogOperator dialog = new NbDialogOperator(editPropertiesTitle);
            String regenerateButtonTitle = Bundle.getStringTrimmed("org.netbeans.modules.web.project.Bundle", "CTL_Regenerate");
            JButtonOperator butt = new JButtonOperator(dialog, regenerateButtonTitle);
            butt.push();
            LOG.info("Closing buildscript regeneration");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exc) {
                LOG.log(Level.INFO, "interrupt exception", exc);
            }
            if (dialog.isVisible()) {
                dialog.close();
            }
        }
    }

    /* Registers a GlassFish instance by creating instance file in cluster config.
     * <br>
     * It can be called from ant script the following way:
     * <pre>
     * <target name="register-glassfish" if="glassfish.home">
     *     <echo message="GlassFish Root Directory: ${glassfish.home}"/>
     *     <java classname="org.netbeans.modules.glassfish.common.registration.AutomaticRegistration" fork="true">
     *         <arg value="${netbeans.dest.dir}/enterprise"/>
     *         <arg value="${glassfish.home}/glassfish"/>
     *         <classpath>
     *             <fileset dir="${netbeans.dest.dir}">
     *                 <include name="platform/core/core.jar"/>
     *                 <include name="platform/lib/boot.jar"/>
     *                 <include name="platform/lib/org-openide-modules.jar"/>
     *                 <include name="platform/core/org-openide-filesystems.jar"/>
     *                 <include name="platform/lib/org-openide-util.jar"/>
     *                 <include name="platform/lib/org-openide-util-lookup.jar"/>
     *                 <include name="enterprise/modules/org-netbeans-modules-j2eeapis.jar"/>
     *                 <include name="enterprise/modules/org-netbeans-modules-j2eeserver.jar"/>
     *                 <include name="ide/modules/org-netbeans-modules-glassfish-common.jar"/>
     *             </fileset>
     *         </classpath>
     *     </java>
     * </target>
     * </pre>
     */
    private static void registerGlassFish() {
        String glassFishHome = getServerHome(GLASSFISH);
        if (!isValidPath(glassFishHome) || !isValidPath(glassFishHome + "/glassfish/domains/domain1")) {
            LOG.log(Level.WARNING, "Valid GlassFish server not found at path {0}", glassFishHome);
            return;
        }
        LOG.log(Level.INFO, "Registering GlassFish server at {0}", glassFishHome);
        if (isSelfTest) {
            alreadyRegistered.add(GLASSFISH);
            return;
        }
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            System.out.println("loader=" + loader);
            if (loader == null) {
                loader = J2eeTestCase.class.getClassLoader();
                System.out.println("loader=" + loader);
            }
            Class<?> regClass = Class.forName("org.netbeans.modules.glassfish.common.registration.AutomaticRegistration", true, loader);
            Method method = regClass.getDeclaredMethod("autoregisterGlassFishInstance", String.class, String.class);
            method.setAccessible(true);
            String enterpriseCluster = findEnterpriseCluster();
            int result = (Integer) method.invoke(null, enterpriseCluster, glassFishHome + "/glassfish");
            if (result == 0) {
                alreadyRegistered.add(GLASSFISH);
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Registering GlassFish server failed.", ex);
        }
    }

    /* Registers a Tomcat instance by creating instance file in cluster config.
     * <br>
     * It can be called from ant script the following way:
     * <pre>
     * <target name="register-tomcat" if="tomcat.home">
     *     <echo message="Tomcat Root Directory: ${tomcat.home}"/>
     *     <java classname="org.netbeans.modules.tomcat5.registration.AutomaticRegistration" fork="true">
     *         <arg value="--add"/>
     *         <arg value="${netbeans.dest.dir}/enterprise"/>
     *         <arg value="${tomcat.home}"/>
     *         <classpath>
     *             <fileset dir="${netbeans.dest.dir}">
     *                 <include name="platform/core/core.jar"/>
     *                 <include name="platform/lib/boot.jar"/>
     *                 <include name="platform/lib/org-openide-modules.jar"/>
     *                 <include name="platform/core/org-openide-filesystems.jar"/>
     *                 <include name="platform/lib/org-openide-util.jar"/>
     *                 <include name="platform/lib/org-openide-util-lookup.jar"/>
     *                 <include name="enterprise/modules/org-netbeans-modules-j2eeapis.jar"/>
     *                 <include name="enterprise/modules/org-netbeans-modules-j2eeserver.jar"/>
     *                 <include name="enterprise/modules/org-netbeans-modules-tomcat5.jar"/>
     *             </fileset>
     *         </classpath>
     *     </java>
     * </target>
     * </pre>
     */
    private static void registerTomcat() {
        String tomcatHome = getServerHome(TOMCAT);
        if (!isValidPath(tomcatHome)) {
            LOG.log(Level.WARNING, "Valid Tomcat server not found at path {0}", tomcatHome);
            return;
        }
        LOG.log(Level.INFO, "Registering Tomcat server at {0}", tomcatHome);
        if (isSelfTest) {
            alreadyRegistered.add(TOMCAT);
            return;
        }
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = J2eeTestCase.class.getClassLoader();
            }
            Class<?> regClass = Class.forName("org.netbeans.modules.tomcat5.registration.AutomaticRegistration", true, loader);
            Method method = regClass.getDeclaredMethod("registerTomcatInstance", String.class, String.class);
            method.setAccessible(true);
            String enterpriseCluster = findEnterpriseCluster();
            int result = (Integer) method.invoke(null, enterpriseCluster, tomcatHome);
            if (result == 0) {
                alreadyRegistered.add(TOMCAT);
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Registering Tomcat server failed.", ex);
        }
    }

    /** Returns absolute path to enterprise cluster.
     * @return absolute path to enterprise cluster.
     */
    private static String findEnterpriseCluster() {
        String clusters = System.getProperty("cluster.path.final");
        assert clusters != null : "cluster.path.final must be set.";
        for (String cluster : tokenizePath(clusters)) {
            if (cluster.endsWith("enterprise")) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * Split an Ant-style path specification into components.
     * Tokenizes on <code>:</code> and <code>;</code>, paying
     * attention to DOS-style components such as <samp>C:\FOO</samp>.
     * Also removes any empty components.
     * Copied from org.netbeans.spi.project.support.ant.PropertyUtils.
     * @param path an Ant-style path (elements arbitrary) using DOS or Unix separators
     * @return a tokenization of that path into components
     */
    private static String[] tokenizePath(String path) {
        List<String> l = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(path, ":;", true); // NOI18N
        char dosHack = '\0';
        char lastDelim = '\0';
        int delimCount = 0;
        while (tok.hasMoreTokens()) {
            String s = tok.nextToken();
            if (s.length() == 0) {
                // Strip empty components.
                continue;
            }
            if (s.length() == 1) {
                char c = s.charAt(0);
                if (c == ':' || c == ';') {
                    // Just a delimiter.
                    lastDelim = c;
                    delimCount++;
                    continue;
                }
            }
            if (dosHack != '\0') {
                // #50679 - "C:/something" is also accepted as DOS path
                if (lastDelim == ':' && delimCount == 1 && (s.charAt(0) == '\\' || s.charAt(0) == '/')) {
                    // We had a single letter followed by ':' now followed by \something or /something
                    s = "" + dosHack + ':' + s;
                    // and use the new token with the drive prefix...
                } else {
                    // Something else, leave alone.
                    l.add(Character.toString(dosHack));
                    // and continue with this token too...
                }
                dosHack = '\0';
            }
            // Reset count of # of delimiters in a row.
            delimCount = 0;
            if (s.length() == 1) {
                char c = s.charAt(0);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    // Probably a DOS drive letter. Leave it with the next component.
                    dosHack = c;
                    continue;
                }
            }
            l.add(s);
        }
        if (dosHack != '\0') {
            //the dosHack was the last letter in the input string (not followed by the ':')
            //so obviously not a drive letter.
            //Fix for issue #57304
            l.add(Character.toString(dosHack));
        }
        return l.toArray(new String[l.size()]);
    }
}
