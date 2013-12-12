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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.test.git.operators;

import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.ExploreFromHereAction;
import org.netbeans.jellytools.actions.FindAction;
import org.netbeans.jellytools.nodes.Node;

/** Node representing Source packages node under project node.
 * @author Jiri.Skrivanek@sun.com
 */
public class SourcePackagesNode extends Node {

    private static final String SOURCE_PACKAGES_LABEL = Bundle.getString(
                                "org.netbeans.modules.java.j2seproject.Bundle",
                                "NAME_src.dir");
    static final ExploreFromHereAction exploreFromHereAction = new ExploreFromHereAction();
    static final FindAction findAction = new FindAction();
    
    /** Finds Source Packages node under project with given name
     * @param projectName display name of project
     */
    public SourcePackagesNode(String projectName) {
        super(new ProjectsTabOperator().getProjectRootNode(projectName), SOURCE_PACKAGES_LABEL);
    }

    /** Finds Source Packages node under given project node.
     * @param projectNode project node in the Projects view
     */
    public SourcePackagesNode(Node projectNode) {
        super(projectNode, SOURCE_PACKAGES_LABEL);
    }

    /** tests popup menu items for presence */    
    public void verifyPopup() {
        verifyPopup(new Action[]{
            exploreFromHereAction,
            findAction,
        });
    }
    
    /** performs ExploreFromHereAction with this node */    
    public void exploreFromHere() {
        exploreFromHereAction.perform(this);
    }
    
    /** performs FindAction with this node */    
    public void find() {
        findAction.perform(this);
    }
}
