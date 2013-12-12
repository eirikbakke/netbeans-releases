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

package org.netbeans.spi.project.ui.support;

import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.project.uiapi.Utilities;
import org.netbeans.spi.project.ui.LogicalViewProvider;

/**
 * Factory for commonly needed generic project actions.
 * @author Jesse Glick, Petr Hrebejk
 */
public class CommonProjectActions {

    /**
     * {@link org.openide.filesystems.FileObject} value honored by {@link #newProjectAction}
     * that defines initial value for existing sources directory choosers.
     *
     * @since org.netbeans.modules.projectuiapi/1 1.3
     */
    public static final String EXISTING_SOURCES_FOLDER = "existingSourcesFolder";
    
    /**
     * {@link java.applet.File} value honored by {@link #newProjectAction}
     * that defines initial value for parent folder
     *
     * @since 1.67
     */
    public static final String PROJECT_PARENT_FOLDER = "projdir";
    
    private CommonProjectActions() {}
        
    /**
     * Create an action "Set As Main Project".
     * It should be invoked with an action context containing
     * one {@link org.netbeans.api.project.Project}.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     */
    public static Action setAsMainProjectAction() {
        return Utilities.getActionsFactory().setAsMainProjectAction();
    }
    
    /**
     * Create an action "Customize Project".
     * It should be invoked with an action context containing
     * one {@link org.netbeans.api.project.Project}.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     */
    public static Action customizeProjectAction() {
        return Utilities.getActionsFactory().customizeProjectAction();
    }
    
    /**
     * Create an action "Open Subprojects".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     * @see org.netbeans.spi.project.SubprojectProvider
     * @see org.netbeans.spi.project.ProjectContainerProvider
     */
    public static Action openSubprojectsAction() {
        return Utilities.getActionsFactory().openSubprojectsAction();
    }
    
    /**
     * Create an action "Close Project".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     */
    public static Action closeProjectAction() {
        return Utilities.getActionsFactory().closeProjectAction();
    }
    
    /**
     * Create an action project dependent "New File" action.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     * @see org.netbeans.spi.project.ui.PrivilegedTemplates
     * @see org.netbeans.spi.project.ui.RecommendedTemplates
     */
    public static Action newFileAction() {
        return Utilities.getActionsFactory().newFileAction();
    }
    
    /**
     * Create an action "Delete Project".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @since 1.8
     * @return an action
     */
    public static Action deleteProjectAction() {
        return Utilities.getActionsFactory().deleteProjectAction();
    }

    /**
     * Create an action "Copy Project".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @since 1.10
     * @return an action
     */
    public static Action copyProjectAction() {
        return Utilities.getActionsFactory().copyProjectAction();
    }
    
    /**
     * Create an action "Move Project".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @since 1.10
     * @return an action
     */
    public static Action moveProjectAction() {
        return Utilities.getActionsFactory().moveProjectAction();
    }
    
    /**
     * Create an action "Rename Project".
     * It should be invoked with an action context containing
     * one or more {@link org.netbeans.api.project.Project}s.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @since 1.10
     * @return an action
     */
    public static Action renameProjectAction() {
        return Utilities.getActionsFactory().renameProjectAction();
    }
    
    /**
     * Creates action that invokes <b>New Project</b> wizard.
     * 
     * <p>{@link #EXISTING_SOURCES_FOLDER} keyed action
     * value can carry {@link org.openide.filesystems.FileObject} that points
     * to existing sources folder. {@link Action#putValue Set this value}
     * if you open the wizard and you know user
     * expectations about initial value for wizard
     * choosers that refers to existing sources location.
     * 
     * @return an action
     *
     * @since org.netbeans.modules.projectuiapi/1 1.3
     */
    public static Action newProjectAction() {
        return Utilities.getActionsFactory().newProjectAction();
    }    

    /**
     * Creates an action that sets the configuration of the selected project.
     * It should be displayed with an action context containing
     * exactly one {@link org.netbeans.api.project.Project}.
     * The action itself should not be invoked but you may use its popup presenter.
     * <p class="nonnormative">
     * You might include this in the context menu of a logical view.
     * </p>
     * @return an action
     * @since org.netbeans.modules.projectuiapi/1 1.17
     * @see org.netbeans.spi.project.ProjectConfigurationProvider
     */
    public static Action setProjectConfigurationAction() {
        return Utilities.getActionsFactory().setProjectConfigurationAction();
    }

    /**
     * Loads actions to be displayed in the context menu of {@link LogicalViewProvider#createLogicalView}.
     * The current implementation simply loads actions from {@code Projects/<projectType>/Actions}
     * but in the future it may merge in actions from another location as well.
     * <p>The folder is recommended to contain a link to {@code Projects/Actions} at some position
     * in order to pick up miscellaneous actions applicable to all project types.
     * @param projectType a type token, such as {@code org-netbeans-modules-java-j2seproject}
     * @return a list of actions
     * @since org.netbeans.modules.projectuiapi/1 1.43
     */
    public static Action[] forType(String projectType) {
        List<? extends Action> actions = org.openide.util.Utilities.actionsForPath("Projects/" + projectType + "/Actions");
        return actions.toArray(new Action[actions.size()]);
    }

}
