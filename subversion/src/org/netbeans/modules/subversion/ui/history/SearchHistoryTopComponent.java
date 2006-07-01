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
package org.netbeans.modules.subversion.ui.history;

import org.openide.windows.TopComponent;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.netbeans.modules.subversion.util.Context;
import org.netbeans.modules.subversion.ui.diff.DiffSetupSource;
import org.tigris.subversion.svnclientadapter.SVNUrl;

import java.util.*;
import java.io.File;
import java.awt.BorderLayout;

/**
 * @author Maros Sandor
 */
public class SearchHistoryTopComponent extends TopComponent implements DiffSetupSource {

    private SearchHistoryPanel shp;

    public SearchHistoryTopComponent() {
        getAccessibleContext().setAccessibleName(NbBundle.getMessage(SearchHistoryTopComponent.class, "ACSN_SearchHistoryT_Top_Component")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchHistoryTopComponent.class, "ACSD_SearchHistoryT_Top_Component")); // NOI18N
    }
    
    public SearchHistoryTopComponent(Context context) {
        this(context, null, null, null, null);
    }

    public SearchHistoryTopComponent(Context context, String commitMessage, String username, Date from, Date to) {
        this();
        initComponents(context.getRootFiles(), commitMessage, username, from, to);
    }

    public SearchHistoryTopComponent(SVNUrl repositoryUrl, File localRoot, long revision) {
        this();
        initComponents(repositoryUrl, localRoot, revision);
    }

    public void search() {
        shp.executeSearch();
    }
    
    private void initComponents(SVNUrl repositoryUrl, File localRoot, long revision) {
        setLayout(new BorderLayout());
        SearchCriteriaPanel scp = new SearchCriteriaPanel(repositoryUrl);
        scp.setFrom(Long.toString(revision));
        scp.setTo(Long.toString(revision));
        shp = new SearchHistoryPanel(repositoryUrl, localRoot, scp);
        add(shp);
    }

    private void initComponents(File[] roots, String commitMessage, String username, Date from, Date to) {
        setLayout(new BorderLayout());
        SearchCriteriaPanel scp = new SearchCriteriaPanel(roots);
        scp.setCommitMessage(commitMessage);
        scp.setUsername(username);
        if (from != null) scp.setFrom(SearchExecutor.simpleDateFormat.format(from));
        if (to != null) scp.setTo(SearchExecutor.simpleDateFormat.format(to));
        shp = new SearchHistoryPanel(roots, scp);
        add(shp);
    }

    public int getPersistenceType(){
       return TopComponent.PERSISTENCE_NEVER;
    }
    
    protected void componentClosed() {
//       ((DiffMainPanel) getComponent(0)).componentClosed();
       super.componentClosed();
    }
    
    protected String preferredID(){
       return "Svn.SearchHistoryTopComponent";    // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(getClass());
    }

    public Collection getSetups() {
        return shp.getSetups();
    }

    public String getSetupDisplayName() {
        return getDisplayName();
    }
}
