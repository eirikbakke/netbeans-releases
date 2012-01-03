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

package org.netbeans.libs.git.jgit.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitClient;
import org.netbeans.libs.git.GitException;
import org.netbeans.libs.git.GitMergeResult;
import org.netbeans.libs.git.GitMergeResult.MergeStatus;
import org.netbeans.libs.git.GitRevisionInfo;
import org.netbeans.libs.git.GitTransportUpdate;
import org.netbeans.libs.git.SearchCriteria;
import org.netbeans.libs.git.jgit.AbstractGitTestCase;
import org.netbeans.libs.git.progress.ProgressMonitor;

/**
 *
 * @author ondra
 */
public class MergeTest extends AbstractGitTestCase {
    private File workDir;
    private static final String BRANCH_NAME = "new_branch";
    private Repository repo;

    public MergeTest (String testName) throws IOException {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workDir = getWorkingDirectory();
        repo = getRepository(getLocalGitRepository());
    }

    public void testMergeNoChange () throws Exception {
        File f = new File(workDir, "file");
        write(f, "init");
        add(f);
        commit(f);
        
        GitClient client = getClient(workDir);
        GitBranch branch = client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        GitMergeResult result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.ALREADY_UP_TO_DATE, result.getMergeStatus());
        result = client.merge(branch.getId(), ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.ALREADY_UP_TO_DATE, result.getMergeStatus());
    }
    
    public void testMergeFastForward () throws Exception {
        File f = new File(workDir, "file");
        write(f, "init");
        add(f);
        commit(f);
        
        GitClient client = getClient(workDir);
        client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, BRANCH_NAME);
        add(f);
        GitRevisionInfo info = client.commit(new File[] { f }, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals("init", read(f));
        
        GitMergeResult result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAST_FORWARD, result.getMergeStatus());
        assertEquals(BRANCH_NAME, read(f));
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        GitRevisionInfo[] logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(2, logs.length);
        assertEquals(logs[0].getRevision(), info.getRevision());
        
        // continue working on branch
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        remove(false, f);
        info = client.commit(new File[] { f }, "delete on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals(BRANCH_NAME, read(f));
        
        result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAST_FORWARD, result.getMergeStatus());
        assertFalse(f.exists());
        
        crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, logs.length);
        assertEquals(logs[0].getRevision(), info.getRevision());
    }
    
    public void testMergeRevision () throws Exception {
        File f = new File(workDir, "file");
        write(f, "init");
        add(f);
        commit(f);
        
        GitClient client = getClient(workDir);
        client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, BRANCH_NAME);
        add(f);
        GitRevisionInfo info = client.commit(new File[] { f }, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, "another change");
        add(f);
        GitRevisionInfo info2 = client.commit(new File[] { f }, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals("init", read(f));
        
        GitMergeResult result = client.merge(info.getRevision(), ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAST_FORWARD, result.getMergeStatus());
        assertEquals(BRANCH_NAME, read(f));
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        GitRevisionInfo[] logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(2, logs.length);
        assertEquals(logs[0].getRevision(), info.getRevision());
        
        // merge the rest
        result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAST_FORWARD, result.getMergeStatus());
        assertEquals("another change", read(f));
        
        crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(3, logs.length);
        assertEquals(logs[0].getRevision(), info2.getRevision());
    }
    
    public void testConflicts () throws Exception {
        File f = new File(workDir, "file");
        write(f, "init");
        add(f);
        commit(f);
        
        GitClient client = getClient(workDir);
        client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, Constants.MASTER);
        add(f);
        GitRevisionInfo masterInfo = client.commit(new File[] { f }, "change on master", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, BRANCH_NAME);
        add(f);
        GitRevisionInfo branchInfo = client.commit(new File[] { f }, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals(Constants.MASTER, read(f));
        
        GitMergeResult result = client.merge(branchInfo.getRevision(), ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.CONFLICTING, result.getMergeStatus());
        assertEquals("<<<<<<< HEAD\nmaster\n=======\nnew_branch\n>>>>>>> " + branchInfo.getRevision(), read(f));
        assertNull(result.getNewHead());
        assertEquals(Arrays.asList(f), result.getConflicts());
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        GitRevisionInfo[] logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(2, logs.length);
        assertEquals(logs[0].getRevision(), masterInfo.getRevision());
        
        // try merge with branch as revision
        client.reset(Constants.MASTER, GitClient.ResetType.HARD, ProgressMonitor.NULL_PROGRESS_MONITOR);
        result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.CONFLICTING, result.getMergeStatus());
        assertEquals("<<<<<<< HEAD\nmaster\n=======\nnew_branch\n>>>>>>> " + BRANCH_NAME, read(f));
        assertNull(result.getNewHead());
        assertEquals(Arrays.asList(f), result.getConflicts());
        assertEquals("Merge new_branch\n\nConflicts:\n\tfile\n", repo.readMergeCommitMsg());
        
        crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(2, logs.length);
        assertEquals(logs[0].getRevision(), masterInfo.getRevision());
        
        // test obstructing paths
        client.reset(Constants.MASTER, GitClient.ResetType.HARD, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, "local change");
        result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAILED, result.getMergeStatus());
        assertEquals("local change", read(f));
        assertNull(result.getNewHead());
        assertEquals(Arrays.asList(f), result.getFailures());
        assertNull(repo.readMergeCommitMsg());
    }
    
    public void testResolveConflicts () throws Exception {
        File f = new File(workDir, "file");
        String[] contents = { "aaaaa\nbbbbb\nccccc", "xxxxx\nbbbbb\nccccc", "aaaaa\nbbbbb\nyyyyy", "xxxxx\nbbbbb\nyyyyy" };
        write(f, contents[0]);
        add(f);
        commit(f);
        
        GitClient client = getClient(workDir);
        client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, contents[1]);
        add(f);
        GitRevisionInfo masterInfo = client.commit(new File[] { f }, "change on master", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        Thread.sleep(1100);
        write(f, contents[2]);
        add(f);
        GitRevisionInfo branchInfo = client.commit(new File[] { f }, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals(contents[1], read(f));
        
        GitMergeResult result = client.merge(BRANCH_NAME, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.MERGED, result.getMergeStatus());
        assertEquals(contents[3], read(f));
        assertEquals(0, result.getConflicts().size());
        assertEquals(Arrays.asList(new String[] { masterInfo.getRevision(), branchInfo.getRevision() }), Arrays.asList(result.getMergedCommits()));
        
        SearchCriteria crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        GitRevisionInfo[] logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(logs[0].getRevision(), result.getNewHead());
        assertEquals(logs[1].getRevision(), branchInfo.getRevision());
        assertEquals(logs[2].getRevision(), masterInfo.getRevision());
        String logFileContent[] = read(new File(workDir, ".git/logs/HEAD")).split("\\n");
        assertEquals("commit: Merge new_branch", logFileContent[logFileContent.length - 1].substring(logFileContent[logFileContent.length - 1].indexOf("commit: ")));
        
        client.reset("master~1", GitClient.ResetType.HARD, ProgressMonitor.NULL_PROGRESS_MONITOR);
        result = client.merge(branchInfo.getRevision(), ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.MERGED, result.getMergeStatus());
        assertEquals(contents[3], read(f));
        assertEquals(0, result.getConflicts().size());
        assertEquals(Arrays.asList(new String[] { masterInfo.getRevision(), branchInfo.getRevision() }), Arrays.asList(result.getMergedCommits()));
        
        crit = new SearchCriteria();
        crit.setRevisionTo(Constants.MASTER);
        logs = client.log(crit, ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(logs[0].getRevision(), result.getNewHead());
        assertEquals(logs[1].getRevision(), branchInfo.getRevision());
        assertEquals(logs[2].getRevision(), masterInfo.getRevision());
        logFileContent = read(new File(workDir, ".git/logs/HEAD")).split("\\n");
        assertEquals("commit: Merge commit '" + branchInfo.getRevision() + "'", logFileContent[logFileContent.length - 1].substring(logFileContent[logFileContent.length - 1].indexOf("commit: ")));
    }
    
    public void testMergeFailOnLocalChanges () throws Exception {
        File f = new File(workDir, "file");
        write(f, "init");
        File f2 = new File(workDir, "file2");
        write(f2, "init");
        File[] files = { f, f2 };
        add(files);
        commit(files);
        
        GitClient client = getClient(workDir);
        client.createBranch(BRANCH_NAME, Constants.MASTER, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(BRANCH_NAME, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        write(f, BRANCH_NAME);
        add(f);
        write(f2, BRANCH_NAME);
        add(f2);
        GitRevisionInfo branchInfo = client.commit(files, "change on branch", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        client.checkoutRevision(Constants.MASTER, true, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        assertEquals("init", read(f));
        assertEquals("init", read(f2));
        
        write(f, Constants.MASTER);
        write(f2, Constants.MASTER);
        
        try {
            client.merge(branchInfo.getRevision(), ProgressMonitor.NULL_PROGRESS_MONITOR);
            fail("Should fail");
        } catch (GitException.CheckoutConflictException ex) {
            // OK
            assertEquals(Arrays.asList(new String[] { f.getName(), f2.getName() }), Arrays.asList(ex.getConflicts()));
        }
    }
    
    public void testMergeBranchNoHeadYet_196837 () throws Exception {
        StoredConfig cfg = getRemoteRepository().getConfig();
        cfg.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_BARE, false);
        cfg.save();
        File otherRepo = getRemoteRepository().getWorkTree();
        File original = new File(otherRepo, "f");
        GitClient clientOtherRepo = getClient(otherRepo);
        write(original, "initial content");
        clientOtherRepo.add(new File[] { original }, ProgressMonitor.NULL_PROGRESS_MONITOR);
        clientOtherRepo.commit(new File[] { original }, "initial commit", null, null, ProgressMonitor.NULL_PROGRESS_MONITOR);
        
        GitClient client = getClient(workDir);
        Map<String, GitTransportUpdate> updates = client.fetch(otherRepo.toURI().toString(), Arrays.asList(new String[] { "+refs/heads/master:refs/remotes/origin/master" }), ProgressMonitor.NULL_PROGRESS_MONITOR);
        GitMergeResult result = client.merge("origin/master", ProgressMonitor.NULL_PROGRESS_MONITOR);
        assertEquals(MergeStatus.FAST_FORWARD, result.getMergeStatus());
        assertEquals(Arrays.asList(new String[] { ObjectId.zeroId().getName(), updates.get("origin/master").getNewObjectId() }), Arrays.asList(result.getMergedCommits()));
    }
}
