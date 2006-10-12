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

package org.netbeans.modules.j2ee.jboss4.nodes;

import java.awt.Image;
import javax.enterprise.deploy.shared.ModuleType;
import javax.swing.Action;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport.ServerIcon;
import org.netbeans.modules.j2ee.jboss4.nodes.actions.UndeployModuleAction;
import org.netbeans.modules.j2ee.jboss4.nodes.actions.UndeployModuleCookieImpl;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 * Node which describes Web Module.
 *
 * @author Michal Mocnak
 */
public class JBEjbModuleNode extends AbstractNode {
    
    public JBEjbModuleNode(String fileName, Lookup lookup) {
        this(fileName, lookup, false);
    }

    public JBEjbModuleNode(String fileName, Lookup lookup, boolean isEJB3) {
        super(Children.LEAF);
        setDisplayName(fileName.substring(0, fileName.indexOf('.')));
        if (isEJB3) {
            getCookieSet().add(new UndeployModuleCookieImpl(fileName, lookup));
        }
        else {
            getCookieSet().add(new UndeployModuleCookieImpl(fileName, ModuleType.EJB, lookup));
        }
    }
    
    public Action[] getActions(boolean context){
        if(getParentNode() instanceof JBEarApplicationNode)
            return new SystemAction[] {};
        else
            return new SystemAction[] {
                SystemAction.get(UndeployModuleAction.class)
            };
    }
    
    public Image getIcon(int type) {
        return UISupport.getIcon(ServerIcon.EJB_ARCHIVE);
    }

    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}