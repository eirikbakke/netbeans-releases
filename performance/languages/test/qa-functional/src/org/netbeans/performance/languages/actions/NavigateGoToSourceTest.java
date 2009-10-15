/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.performance.languages.actions;

import org.netbeans.modules.performance.utilities.PerformanceTestCase;
import org.netbeans.modules.performance.guitracker.LoggingRepaintManager;
import org.netbeans.performance.languages.Projects;
import org.netbeans.performance.languages.ScriptingUtilities;
import org.netbeans.performance.languages.setup.ScriptingSetup;

import java.awt.Rectangle;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.junit.NbModuleSuite;

/**
 *
 * @author mrkam@netbeans.org
 */

public class NavigateGoToSourceTest extends PerformanceTestCase {

    protected Node fileToBeOpened;
    protected String testProject;
    protected String docName, openedDocName;
    protected String pathName;
    protected String textToFind, openedText;
    protected static ProjectsTabOperator projectsTab = null;    
    private EditorOperator editorOperator;
    private int caretBlinkRate;
    

    public NavigateGoToSourceTest(String testName) {
        super(testName);
        WAIT_AFTER_OPEN=2000;
    }

    public NavigateGoToSourceTest(String testName, String performanceDataName) {
        super(testName, performanceDataName);
        WAIT_AFTER_OPEN=2000;
    }

    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(NbModuleSuite.create(NbModuleSuite.createConfiguration(ScriptingSetup.class)
             .addTest(NavigateGoToSourceTest.class)
             .enableModules(".*").clusters(".*")));
        return suite;
    }

    protected Node getProjectNode(String projectName) {
        if(projectsTab==null)
            projectsTab = ScriptingUtilities.invokePTO();
        return projectsTab.getProjectRootNode(projectName);
    }
    
    @Override
    public void initialize() {
        closeAllModal();
        String path = pathName+docName;
        fileToBeOpened = new Node(getProjectNode(testProject),path);        
        new OpenAction().performAPI(fileToBeOpened);
        editorOperator = EditorWindowOperator.getEditor(docName);
        caretBlinkRate =  editorOperator.txtEditorPane().getCaret().getBlinkRate();
        editorOperator.txtEditorPane().getCaret().setBlinkRate(0);
        repaintManager().addRegionFilter(LoggingRepaintManager.EDITOR_FILTER);
    }
    
    @Override
    public void prepare() {
        try {
            editorOperator = EditorWindowOperator.getEditor(docName);
            editorOperator.setCaretPosition(textToFind, false);
            Rectangle r = editorOperator.txtEditorPane().getUI().modelToView((JTextComponent) editorOperator.txtEditorPane().getSource(), editorOperator.txtEditorPane().getCaretPosition());
            editorOperator.txtEditorPane().clickForPopup((int) r.getCenterX(), (int) r.getCenterY());
        } catch (BadLocationException ex) {
            throw new RuntimeException("Failed to obtain caret position", ex);
        }
    }

    @Override
    public ComponentOperator open() {
        new JPopupMenuOperator().pushMenu("Navigate|Go To Declaration");
        EditorOperator op = new EditorOperator(openedDocName);
        return null;
    }
    
    public void test_PHP_NavigateGoToSourceInTheCurrentClass() {
        testProject = Projects.PHP_PROJECT;
        pathName = "Source Files|classes|";
        docName = "guestitinerary.php";
        openedDocName = "guestitinerary.php";
        textToFind = "return $this->fNam";
        openedText = "private $fName;";
        expectedTime = 1000;
        doMeasurement();
    }

    public void test_PHP_NavigateGoToSourceInTheParentClass() {
        testProject = Projects.PHP_PROJECT;
        pathName = "Source Files|classes|";
        docName = "guestitinerary.php";
        openedDocName = "guest.php";
        textToFind = "return $this->get_firstNam";
        openedText = "function get_firstName(){";
        expectedTime = 1000;
        doMeasurement();
    }

    @Override
    public void shutdown() {
        editorOperator.txtEditorPane().getCaret().setBlinkRate(caretBlinkRate);
        repaintManager().resetRegionFilters();
    }

}
