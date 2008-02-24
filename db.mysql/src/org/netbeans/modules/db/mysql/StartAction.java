/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.modules.db.mysql.DatabaseUtils.ConnectStatus;
import org.netbeans.modules.db.mysql.ui.PropertiesDialog;
import org.netbeans.modules.db.mysql.ui.PropertiesDialog.Tab;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 * @author David Van Couvering
 */
public class StartAction extends CookieAction {
    private static final Class[] COOKIE_CLASSES = 
            new Class[] { ServerInstance.class };
    
    public StartAction() {
        putValue("noIconInMenu", Boolean.TRUE);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return NbBundle.getBundle(StartAction.class).
                getString("LBL_StartAction");
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(StartAction.class);
    }

    @Override
    public boolean enable(Node[] activatedNodes) {
        if ( activatedNodes == null || activatedNodes.length == 0 ) {
            return false;
        }
        
        ServerInstance server = activatedNodes[0].getCookie(ServerInstance.class);

        return ( ! server.isRunning() );

    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        ServerInstance server = activatedNodes[0].getCookie(ServerInstance.class);
                String path = server.getStartPath();
        String message = NbBundle.getMessage(AdministerAction.class,
                "MSG_NoStartPath");
        PropertiesDialog dialog = new PropertiesDialog(server);


        while ( path == null || path.equals("")) {
            
            if ( ! Utils.displayConfirmDialog(message) ) {
                return;
            }  
            
            if ( ! dialog.displayDialog(Tab.ADMIN) ) {
                return;
            }
            
            path = server.getAdminPath();
        }

        try { 
            server.start();
        } catch ( DatabaseException dbe ) {
            Utils.displayError(NbBundle.getMessage(StartAction.class,
                        "MSG_UnableToStartServer"), 
                    dbe);
        }
    }
    
    @Override
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return COOKIE_CLASSES;
    }
}
