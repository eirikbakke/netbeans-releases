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

package org.netbeans.modules.j2ee.jboss4.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.common.api.MessageDestination;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.deployment.plugins.spi.MessageDestinationDeployment;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

/**
 *
 * @author Petr Hejl
 */
public final class JBossMessageDestinationManager implements MessageDestinationDeployment {

    private static final Logger LOGGER = Logger.getLogger(JBossMessageDestinationManager.class.getName());

    private final FileObject serverDir;

    private final boolean isAs7;

    public JBossMessageDestinationManager(String serverUrl, boolean isAs7) {
        String serverDirPath = InstanceProperties.getInstanceProperties(serverUrl).getProperty(
                JBPluginProperties.PROPERTY_SERVER_DIR);
        serverDir = FileUtil.toFileObject(new File(serverDirPath));
        this.isAs7 = isAs7;
    }

    @Override
    public Set<MessageDestination> getMessageDestinations() throws ConfigurationException {
        Set<MessageDestination> messageDestinations = new HashSet<MessageDestination>();
        if (isAs7) {
            FileObject config = serverDir.getFileObject("configuration/standalone.xml");
            if (config == null) {
                config = serverDir.getFileObject("configuration/domain.xml");
            }
            if (config == null || !config.isData()) {
                LOGGER.log(Level.WARNING, NbBundle.getMessage(JBossDatasourceManager.class, "ERR_WRONG_JMS_CONFIG_FILE"));
                return messageDestinations;
            }
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                JB7MessageDestinationHandler handler = new JB7MessageDestinationHandler();
                InputStream is = new BufferedInputStream(config.getInputStream());
                try {
                    parser.parse(is, handler);
                } finally {
                    is.close();
                }
                messageDestinations.addAll(handler.getMessageDestinations());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING,
                        NbBundle.getMessage(JBossDatasourceManager.class, "ERR_WRONG_JMS_CONFIG_FILE"), ex);
            } catch (SAXException ex) {
                LOGGER.log(Level.WARNING,
                        NbBundle.getMessage(JBossDatasourceManager.class, "ERR_WRONG_JMS_CONFIG_FILE"), ex);
            } catch (ParserConfigurationException ex) {
                LOGGER.log(Level.WARNING,
                        NbBundle.getMessage(JBossDatasourceManager.class, "ERR_WRONG_JMS_CONFIG_FILE"), ex);
            }

            return messageDestinations;
        }

        return messageDestinations;
    }

    @Override
    public void deployMessageDestinations(Set<MessageDestination> destinations) throws ConfigurationException {
    }

}
