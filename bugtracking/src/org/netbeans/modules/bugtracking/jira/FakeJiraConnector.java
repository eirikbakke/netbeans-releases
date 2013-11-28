/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.jira;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.bugtracking.DelegatingConnector;
import org.netbeans.modules.bugtracking.api.Repository;
import org.netbeans.modules.bugtracking.spi.BugtrackingConnector;
import org.netbeans.modules.bugtracking.spi.BugtrackingSupport;
import org.netbeans.modules.bugtracking.spi.RepositoryController;
import org.netbeans.modules.bugtracking.spi.RepositoryInfo;
import org.netbeans.modules.bugtracking.spi.RepositoryProvider;
import org.netbeans.modules.bugtracking.commons.JiraUpdater;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Stupka
 */
public class FakeJiraConnector {
    private static DelegatingConnector connector;
    
    /**
     * Returns a fake {@link BugtrackingConnector} to be shown in the create
     * repository dialog. The repository controller panel notifies a the missing
     * JIRA plugin and comes with a button to download it from the Update Center.
     *
     * @return
     */
    public static synchronized DelegatingConnector getConnector() {
        if(connector == null) {
            connector =  new DelegatingConnector(
                    new JiraProxyConnector(), 
                    "fake.jira.connector",                                              // NOI18N
                    NbBundle.getMessage(FakeJiraConnector.class, "LBL_FakeJiraName"),         // NOI18N
                    NbBundle.getMessage(FakeJiraConnector.class, "LBL_FakeJiraNameTooltip"),  // NOI18N
                    null);
        }
        return connector;
    }
    
    private static class JiraProxyConnector implements BugtrackingConnector {
        private BugtrackingSupport<Object, Object, Object> f = new BugtrackingSupport<Object, Object, Object>(new JiraProxyRepositoryProvider(), null, null);
        @Override
        public Repository createRepository() {
            return f.createRepository(f, null, null, null, null);
        }
        @Override
        public Repository createRepository(RepositoryInfo info) {
            throw new UnsupportedOperationException("Not supported yet.");      // NOI18N
        }
    }
    private static class JiraProxyRepositoryProvider implements RepositoryProvider<Object,Object,Object> {
        @Override
        public Image getIcon(Object r) {
            return null;
        }
        @Override
        public RepositoryInfo getInfo(Object r) {
            return null;
        }
        @Override
        public Collection<Object> getIssues(Object r, String... id) {
            throw new UnsupportedOperationException("Not supported yet.");      // NOI18N
        }
        @Override
        public void removed(Object r) { }
        @Override
        public RepositoryController getController(Object r) {
            return new JiraProxyController();
        }
        @Override
        public Object createIssue(Object r) {
            throw new UnsupportedOperationException("Not supported yet.");      // NOI18N
        }
        @Override
        public Object createQuery(Object r) {
            throw new UnsupportedOperationException("Not supported yet.");      // NOI18N
        }
        @Override
        public Collection<Object> getQueries(Object r) {
            return Collections.emptyList();
        }
        @Override
        public Collection<Object> simpleSearch(Object r, String criteria) {
            return Collections.emptyList();
        }
        @Override
        public void removePropertyChangeListener(Object r, PropertyChangeListener listener) {
            // do nothing
        }
        @Override
        public void addPropertyChangeListener(Object r, PropertyChangeListener listener) {
            // do nothing
        }
        @Override
        public Object createIssue(Object r, String summary, String description) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public boolean canAttachFiles(Object r) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class JiraProxyController implements RepositoryController {
        private JPanel panel;
        @Override
        public JComponent getComponent() {
            if(panel == null) {
                panel = createControllerPanel();
            }
            return panel;
        }
        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx(this.getClass());
        }
        @Override
        public boolean isValid() {
            return false;
        }
        private JPanel createControllerPanel() {
            JPanel controllerPanel = new JPanel();

            JLabel pane = new JLabel();
            pane.setText(NbBundle.getMessage(FakeJiraConnector.class, "MSG_NOT_YET_INSTALLED")); // NOI18N

            JButton downloadButton = new JButton();
            downloadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JiraUpdater.getInstance().downloadAndInstall(null);
                }
            });
            
            org.openide.awt.Mnemonics.setLocalizedText(downloadButton, org.openide.util.NbBundle.getMessage(FakeJiraConnector.class, "MissingJiraSupportPanel.downloadButton.text")); // NOI18N

            GroupLayout layout = new GroupLayout(controllerPanel);
            controllerPanel.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(pane, GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(downloadButton))
            );
            layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadButton))
                .addContainerGap())
            );

            return controllerPanel;
        }

        @Override public String getErrorMessage() { return null; }
        @Override public void applyChanges() { }        
        @Override public void cancelChanges() { }
        @Override public void populate() {}
        @Override public void addChangeListener(ChangeListener l) {}
        @Override public void removeChangeListener(ChangeListener l) {}
    }
}
