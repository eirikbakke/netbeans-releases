/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.git.ui.repository;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import org.netbeans.modules.git.client.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitRevisionInfo;
import org.netbeans.libs.git.progress.ProgressMonitor;
import org.netbeans.modules.git.Git;
import org.netbeans.modules.git.client.GitClientExceptionHandler;
import org.netbeans.modules.git.utils.GitUtils;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor.Task;

/**
 *
 * @author ondra
 */
public class RevisionInfoPanelController {
    private final RevisionInfoPanel panel;
    private static final String MSG_LOADING = NbBundle.getMessage(RevisionDialogController.class, "MSG_RevisionInfoPanel.loading"); //NOI18N
    private static final String MSG_UNKNOWN = NbBundle.getMessage(RevisionDialogController.class, "MSG_RevisionInfoPanel.unknown"); //NOI18N
    private final LoadInfoWorker loadInfoWorker = new LoadInfoWorker();
    private final Task loadInfoTask;
    private String currentCommit;
    private final File repository;
    private boolean valid;
    private final PropertyChangeSupport support;
    public static final String PROP_VALID = "RevisionInfoPanelController.valid"; //NOI18N

    public RevisionInfoPanelController (File repository) {
        this.repository = repository;
        this.loadInfoTask = Git.getInstance().getRequestProcessor(null).create(loadInfoWorker);
        this.panel = new RevisionInfoPanel();
        this.support = new PropertyChangeSupport(this);
        resetInfoFields();
    }

    public RevisionInfoPanel getPanel () {
        return panel;
    }

    public void loadInfo (String revision) {
        loadInfoTask.cancel();
        loadInfoWorker.monitor.cancel();
        currentCommit = revision;
        setValid(false);
        if (revision == null || revision.isEmpty()) {
            setUnknownRevision();
        } else {
            resetInfoFields();
            loadInfoTask.schedule(100);
        }
    }

    private void resetInfoFields () {
        panel.taMessage.setText(MSG_LOADING);
        panel.tbAuthor.setText(MSG_LOADING);
        panel.tbRevisionId.setText(MSG_LOADING);
    }

    public void updateInfoFields (String revision, GitRevisionInfo info) {
        assert EventQueue.isDispatchThread();
        panel.tbAuthor.setText(info.getAuthor().toString());
        if (!panel.tbAuthor.getText().isEmpty()) {
            panel.tbAuthor.setCaretPosition(0);
        }
        if (revision.equals(info.getRevision())) {
            panel.tbRevisionId.setText(info.getRevision());
        } else {
            if (revision.startsWith(GitUtils.PREFIX_R_HEADS)) { //NOI18N
                revision = revision.substring(GitUtils.PREFIX_R_HEADS.length());
            } else if (revision.startsWith(GitUtils.PREFIX_R_REMOTES)) { //NOI18N
                revision = revision.substring(GitUtils.PREFIX_R_REMOTES.length());
            }
            panel.tbRevisionId.setText(new StringBuilder(revision).append(" (").append(info.getRevision()).append(')').toString()); //NOI18N
        }
        if (!panel.tbRevisionId.getText().isEmpty()) {
            panel.tbRevisionId.setCaretPosition(0);
        }
        panel.taMessage.setText(info.getFullMessage());
        if (!panel.taMessage.getText().isEmpty()) {
            panel.taMessage.setCaretPosition(0);
        }
    }

    private void setUnknownRevision () {
        panel.tbAuthor.setText(MSG_UNKNOWN);
        panel.tbRevisionId.setText(MSG_UNKNOWN);
        panel.taMessage.setText(MSG_UNKNOWN);
    }

    private void setValid (boolean flag) {
        boolean oldValue = valid;
        valid = flag;
        if (oldValue != valid) {
            support.firePropertyChange(PROP_VALID, oldValue, valid);
        }
    }
    
    public void addPropertyChangeListener (PropertyChangeListener list) {
        support.addPropertyChangeListener(list);
    }
    
    public void removePropertyChangeListener (PropertyChangeListener list) {
        support.removePropertyChangeListener(list);
    }

    private class LoadInfoWorker implements Runnable {

        ProgressMonitor.DefaultProgressMonitor monitor = new ProgressMonitor.DefaultProgressMonitor();

        @Override
        public void run () {
            final String revision = currentCommit;
            GitRevisionInfo revisionInfo;
            GitClient client = null;
            try {
                monitor = new ProgressMonitor.DefaultProgressMonitor();
                if (Thread.interrupted()) {
                    return;
                }
                client = Git.getInstance().getClient(repository);
                revisionInfo = client.log(revision, monitor);
            } catch (GitException ex) {
                if (!(ex instanceof GitException.MissingObjectException)) {
                    GitClientExceptionHandler.notifyException(ex, true);
                }
                revisionInfo = null;
            } finally {
                if (client != null) {
                    client.release();
                }
            }
            final GitRevisionInfo info = revisionInfo;
            final ProgressMonitor.DefaultProgressMonitor m = monitor;
            if (!monitor.isCanceled()) {
                Mutex.EVENT.readAccess(new Runnable () {
                    @Override
                    public void run () {
                        if (!m.isCanceled()) {
                            if (info == null) {
                                setUnknownRevision();
                            } else {
                                updateInfoFields(revision, info);
                                setValid(true);
                            }
                        }
                    }
                });
            }
        }
    }
}
