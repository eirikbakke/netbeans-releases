/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.db.mysql;

/**
 * This interface defines an abstraction of an installation of MySQL, which
 * gives you information such as the path/arguments to the start command,
 * stop command, admin command.
 * 
 * Valid installations are loaded through the layer file using the folder
 * Databases/MySQL/Installations
 * 
 * @author David Van Couvering
 */
public interface Installation {
     public enum Command { START, STOP, ADMIN };


    /**
     * @return true if this installation is part of a stack installation
     * like XAMPP or MAMP.  Stack-based installations take preference because 
     * they usually have an admin tool (myphpadmin) and usually don't install 
     * MySQL as a service but are instead manually started and stopped.
     * 
     * Also, standalone installs often come as part of the OS distribution,
     * where a stack based install is explicitly installed by the user, and
     * thus is probably their preference.
     */
    public boolean isStackInstall();
    
    /**
     * Returns true if this installation is valid for the current OS
     */
    public boolean isValidOnCurrentOS();

    /**
     * @return the command to administer this installation.  This is normally
     * phpMyAdmin; rarely does an installation come with the MySQL admin tool.
     * <p>
     * The first element is the path/URL to the command.  
     * The second element is the arguments to the command
     */
    public String[] getAdminCommand();
    
    /**
     * @return the command to stop the server.  The first element is the path
     * to the command. The second element is the arguments to the command
     */
    public String[] getStartCommand();

    /**
     * @return the command to start the server.  The first element is the path
     * to the command. The second element is the arguments to the command
     */
    public String[] getStopCommand();
    
    /**
     * @return the default port number for the server
     */
    public String getDefaultPort();
    
    /**
     * Given a full path to a command, get an installation definition.  
     * This is for when the installation path may not be the default one.  
     * The use case for this
     * is if the user manually specifies a path to the start command; we can
     * then use this to determine the values for the other commands.
     * 
     * @param command the full path to the command
     * @param cmdType the type of command
     * 
     * @return an Installation which is correct based on the given command,
     *   or null if it was not a valid command
     */
    public Installation getInstallation(String command, Command cmdType);
}

