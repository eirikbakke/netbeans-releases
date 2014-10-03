/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2014 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.modules.profiler.v2.features;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import org.netbeans.lib.profiler.common.Profiler;
import org.netbeans.lib.profiler.ui.components.ProfilerToolbar;
import org.netbeans.lib.profiler.ui.threads.ThreadsPanel;
import org.netbeans.modules.profiler.v2.ui.GrayLabel;
import org.netbeans.modules.profiler.v2.ui.PopupButton;
import org.openide.util.NbBundle;

/**
 *
 * @author Jiri Sedlacek
 */
@NbBundle.Messages({
    "ThreadsFeatureUI_show=Show:",
    "ThreadsFeatureUI_filterAll=All threads",
    "ThreadsFeatureUI_filterLive=Live threads",
    "ThreadsFeatureUI_filterFinished=Finished threads",
    "ThreadsFeatureUI_timeline=Timeline:",
    "ThreadsFeatureUI_threadsFilter=Threads filter"
})
abstract class ThreadsFeatureUI extends FeatureUI {
    
    private ProfilerToolbar toolbar;
    private ThreadsPanel threadsView;
    
    
    // --- External implementation ---------------------------------------------
    
    abstract Profiler getProfiler();
    
    
    // --- API implementation --------------------------------------------------
    
    ProfilerToolbar getToolbar() {
        if (toolbar == null) initUI();
        return toolbar;
    }

    JPanel getResultsUI() {
        if (threadsView == null) initUI();
        return threadsView;
    }
    
    
    void sessionStateChanged(int sessionState) {
        refreshToolbar(sessionState);
        
        if (sessionState == Profiler.PROFILING_INACTIVE || sessionState == Profiler.PROFILING_IN_TRANSITION) {
            if (threadsView != null) threadsView.profilingSessionFinished();
        } else if (sessionState == Profiler.PROFILING_RUNNING) {
            if (threadsView != null) threadsView.profilingSessionStarted();
        }
    }
    
    
    // --- UI ------------------------------------------------------------------
    
    private JLabel shLabel;
    private PopupButton shFilter;
    
    private JLabel tlLabel;
    private Component tlZoomInButton;
    private Component tlZoomOutButton;
    private Component tlFitWidthButton;
    
    
    private void initUI() {
        
        assert SwingUtilities.isEventDispatchThread();
        
        // --- Results ---------------------------------------------------------
        
        threadsView = new ThreadsPanel(getProfiler().getThreadsManager(), null);
        threadsView.threadsMonitoringEnabled();
        
        
        // --- Toolbar ---------------------------------------------------------
        
        shLabel = new GrayLabel(Bundle.ThreadsFeatureUI_show());

        shFilter = new PopupButton(Bundle.ThreadsFeatureUI_filterAll()) {
            protected void populatePopup(JPopupMenu popup) { populateFilters(popup); }
        };
        shFilter.setToolTipText(Bundle.ThreadsFeatureUI_threadsFilter());

        tlLabel = new GrayLabel(Bundle.ThreadsFeatureUI_timeline());


        tlZoomInButton = threadsView.getZoomIn();
        tlZoomOutButton = threadsView.getZoomOut();
        tlFitWidthButton = threadsView.getFitWidth();

        toolbar = ProfilerToolbar.create(true);

        toolbar.addSpace(2);
        toolbar.addSeparator();
        toolbar.addSpace(5);

        toolbar.add(shLabel);
        toolbar.addSpace(2);
        toolbar.add(shFilter);

        toolbar.addSpace(2);
        toolbar.addSeparator();
        toolbar.addSpace(5);

        toolbar.add(tlLabel);
        toolbar.addSpace(2);
        toolbar.add(tlZoomInButton);
        toolbar.add(tlZoomOutButton);
        toolbar.add(tlFitWidthButton);
        
        
        // --- Sync UI ---------------------------------------------------------

        setFilter(ThreadsPanel.Filter.ALL);
        sessionStateChanged(getSessionState());
        
    }
    
    private void refreshToolbar(final int state) {
//        if (toolbar != null) SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//            }
//        });
    }
    
    private void populateFilters(JPopupMenu popup) {
        ThreadsPanel.Filter f = threadsView.getFilter();
        
        popup.add(new JRadioButtonMenuItem(Bundle.ThreadsFeatureUI_filterAll(), f == ThreadsPanel.Filter.ALL) {
            protected void fireActionPerformed(ActionEvent e) { setFilter(ThreadsPanel.Filter.ALL); }
        });
        
        popup.add(new JRadioButtonMenuItem(Bundle.ThreadsFeatureUI_filterLive(), f == ThreadsPanel.Filter.LIVE) {
            protected void fireActionPerformed(ActionEvent e) { setFilter(ThreadsPanel.Filter.LIVE); }
        });
        
        popup.add(new JRadioButtonMenuItem(Bundle.ThreadsFeatureUI_filterFinished(), f == ThreadsPanel.Filter.FINISHED) {
            protected void fireActionPerformed(ActionEvent e) { setFilter(ThreadsPanel.Filter.FINISHED); }
        });
    }

    private void setFilter(ThreadsPanel.Filter filter) {
        threadsView.setFilter(filter);
        
        switch (filter) {
            case ALL:
                shFilter.setText(Bundle.ThreadsFeatureUI_filterAll());
                break;
            case LIVE:
                shFilter.setText(Bundle.ThreadsFeatureUI_filterLive());
                break;
            case FINISHED:
                shFilter.setText(Bundle.ThreadsFeatureUI_filterFinished());
                break;
        }
    }
    
}
