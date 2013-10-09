/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * <p/>
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 * <p/>
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 * <p/>
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * <p/>
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 * <p/>
 * Contributor(s):
 * <p/>
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.jira;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import org.netbeans.modules.bugtracking.spi.IssueController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.jira.issue.NbJiraIssue;

/**
 *
 * @author Tomas Stupka
 */
public class JiraIssueProvider extends IssueProvider<NbJiraIssue> {

    @Override
    public String getDisplayName(NbJiraIssue data) {
        return data.getDisplayName();
    }

    @Override
    public String getTooltip(NbJiraIssue data) {
        return data.getTooltip();
    }

    @Override
    public String getID(NbJiraIssue data) {
        return data.getKey();
    }

    @Override
    public String[] getSubtasks(NbJiraIssue data) {
        List<String> l = data.getSubtaskID();
        return l.toArray(new String[l.size()]);
    }
    
    @Override
    public String getSummary(NbJiraIssue data) {
        return data.getSummary();
    }

    @Override
    public boolean isNew(NbJiraIssue data) {
        return data.isNew();
    }

    @Override
    public boolean isFinished(NbJiraIssue data) {
        return data.isFinished();
    }
    
    @Override
    public boolean refresh(NbJiraIssue data) {
        return data.refresh();
    }

    @Override
    public void addComment(NbJiraIssue data, String comment, boolean closeAsFixed) {
        data.addComment(comment, closeAsFixed);
    }

    @Override
    public void attachPatch(NbJiraIssue data, File file, String description) {
        data.attachPatch(file, description);
    }

    @Override
    public IssueController getController(NbJiraIssue data) {
        return data.getController();
    }

    @Override
    public void removePropertyChangeListener(NbJiraIssue data, PropertyChangeListener listener) {
        data.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(NbJiraIssue data, PropertyChangeListener listener) {
        data.addPropertyChangeListener(listener);
    }
    
    @Override
    public boolean submit (NbJiraIssue data) {
        return data.submitAndRefresh();
    }

    @Override
    public void discardOutgoing(NbJiraIssue data) {
        data.discardLocalEdits();
    }
}
