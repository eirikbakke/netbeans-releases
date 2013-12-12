/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.git.ui.push;

import java.text.MessageFormat;
import org.netbeans.libs.git.GitBranch;
import org.netbeans.libs.git.GitTag;
import org.netbeans.modules.git.options.AnnotationColorProvider;
import org.netbeans.modules.git.ui.selectors.ItemSelector;
import org.netbeans.modules.git.ui.selectors.ItemSelector.Item;
import org.netbeans.modules.git.utils.GitUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author ondra
 */
public abstract class PushMapping extends ItemSelector.Item {

    private final String label;
    private final String tooltip;
    private static final String BRANCH_MAPPING_LABEL = "{0} -> {1} [{2}]"; //NOI18N
    private static final String BRANCH_DELETE_MAPPING_LABEL = "{0} [{1}]"; //NOI18N
    private static final String BRANCH_MAPPING_LABEL_UPTODATE = "{0} -> {1}"; //NOI18N
    private final String localName;
    private final String remoteName;
    private static final String COLOR_NEW = GitUtils.getColorString(AnnotationColorProvider.getInstance().ADDED_FILE.getActualColor());
    private static final String COLOR_MODIFIED = GitUtils.getColorString(AnnotationColorProvider.getInstance().MODIFIED_FILE.getActualColor());
    private static final String COLOR_REMOVED = GitUtils.getColorString(AnnotationColorProvider.getInstance().REMOVED_FILE.getActualColor());
    private static final String COLOR_CONFLICT = GitUtils.getColorString(AnnotationColorProvider.getInstance().CONFLICT_FILE.getActualColor());
    
    protected PushMapping (String localName, String localId, String remoteName, String remoteId, boolean conflict, boolean preselected) {
        super(preselected, localName == null);
        this.localName = localName;
        this.remoteName = remoteName == null ? localName : remoteName;
        if (localName == null) {
            // to remove
            label = MessageFormat.format(BRANCH_DELETE_MAPPING_LABEL, remoteName, "<font color=\"" + COLOR_REMOVED + "\">R</font>"); //NOI18N
            tooltip = NbBundle.getMessage(
                    PushBranchesStep.class,
                    "LBL_PushBranchMapping.description", //NOI18N
                    new Object[]{
                        remoteName,
                        NbBundle.getMessage(PushBranchesStep.class, "LBL_PushBranchMapping.Mode.deleted.description") //NOI18N
                    }); //NOI18N
        } else if (remoteName == null) {
            // added
            label = MessageFormat.format(BRANCH_MAPPING_LABEL, localName, localName, "<font color=\"" + COLOR_NEW + "\">A</font>"); //NOI18N
            tooltip = NbBundle.getMessage(
                    PushBranchesStep.class,
                    "LBL_PushBranchMapping.description", //NOI18N
                    new Object[]{
                        localName,
                        NbBundle.getMessage(PushBranchesStep.class, "LBL_PushBranchMapping.Mode.added.description") //NOI18N
                    }); //NOI18N
        } else if (localId.equals(remoteId)) {
            // up to date
            label = MessageFormat.format(BRANCH_MAPPING_LABEL_UPTODATE, localName, remoteName);
            tooltip = NbBundle.getMessage(PushBranchesStep.class,
                    "LBL_PushBranchMapping.Mode.uptodate.description", //NOI18N
                    remoteName);
        } else if (conflict) {
            // modified
            label = MessageFormat.format(BRANCH_MAPPING_LABEL, localName, remoteName, "<font color=\"" + COLOR_CONFLICT + "\">C</font>"); //NOI18N
            tooltip = NbBundle.getMessage(
                    PushBranchesStep.class,
                    "LBL_PushBranchMapping.Mode.conflict.description", //NOI18N
                    new Object[]{
                        remoteName
                    });
        } else {
            // modified
            label = MessageFormat.format(BRANCH_MAPPING_LABEL, localName, remoteName, "<font color=\"" + COLOR_MODIFIED + "\">U</font>"); //NOI18N
            tooltip = NbBundle.getMessage(
                    PushBranchesStep.class,
                    "LBL_PushBranchMapping.description", //NOI18N
                    new Object[]{
                        remoteName,
                        NbBundle.getMessage(PushBranchesStep.class, "LBL_PushBranchMapping.Mode.updated.description") //NOI18N
                    });
        }
    }


    public abstract String getRefSpec ();

    String getInfoMessage () {
        return null;
    }

    @Override
    public String getText () {
        return label;
    }

    @Override
    public String getTooltipText () {
        return tooltip;
    }
    
    public final String getLocalName () {
        return localName;
    }

    @Override
    public int compareTo (Item t) {
        if (t == null) {
            return 1;
        }
        if (t instanceof PushMapping) {
            PushMapping other = (PushMapping) t;
            if (isDeletion() && other.isDeletion()) {
                return remoteName.compareTo(other.remoteName);
            } else if (isDeletion() && !other.isDeletion()) {
                // deleted branches should be at the bottom
                return 1;
            } else if (!isDeletion() && other.isDeletion()) {
                // deleted branches should be at the bottom
                return -1;
            } else {
                return localName.compareTo(other.localName);
            }
        }
        return 0;
    }

    String getRemoteName () {
        return remoteName;
    }

    abstract boolean isCreateBranchMapping ();
    
    public static final class PushBranchMapping extends PushMapping {
        private final GitBranch localBranch;
        private final String remoteBranchName;
        private final String remoteBranchId;
        
        /**
         * Denotes a branch to be deleted in a remote repository
         */
        public PushBranchMapping (String remoteBranchName, String remoteBranchId, boolean preselected) {
            super(null, null, remoteBranchName, remoteBranchId, false, preselected);
            this.localBranch = null;
            this.remoteBranchName = remoteBranchName;
            this.remoteBranchId = remoteBranchId;
        }
        
        public PushBranchMapping (String remoteBranchName, String remoteBranchId, GitBranch localBranch, boolean conflict, boolean preselected) {
            super(localBranch.getName(), localBranch.getId(), 
                    remoteBranchName, 
                    remoteBranchId,
                    conflict,
                    preselected);
            this.localBranch = localBranch;
            this.remoteBranchName = remoteBranchName;
            this.remoteBranchId = remoteBranchId;
        }

        public String getRemoteRepositoryBranchName () {
            return remoteBranchName == null ? localBranch.getName() : remoteBranchName;
        }

        public String getRemoteRepositoryBranchHeadId () {
            return remoteBranchId;
        }

        public String getLocalRepositoryBranchHeadId () {
            return localBranch == null ? null : localBranch.getId();
        }

        @Override
        public String getRefSpec () {
            if (isDeletion()) {
                return GitUtils.getPushDeletedRefSpec(remoteBranchName);
            } else {
                return GitUtils.getPushRefSpec(localBranch.getName(), remoteBranchName == null ? localBranch.getName() : remoteBranchName);
            }
        }
        
        @Override
        @NbBundle.Messages({
            "# {0} - branch name",
            "MSG_PushMapping.toBeDeletedBranch=Branch {0} will be permanently removed from the remote repository."
        })
        String getInfoMessage () {
            if (isDeletion()) {
                return Bundle.MSG_PushMapping_toBeDeletedBranch(remoteBranchName);
            } else {
                return super.getInfoMessage();
            }
        }

        @Override
        boolean isCreateBranchMapping () {
            return localBranch != null && remoteBranchName == null;
        }
    }
    
    public static final class PushTagMapping extends PushMapping {
        private final GitTag tag;
        
        public PushTagMapping (GitTag tag) {
            super("tags/" + tag.getTagName(), tag.getTaggedObjectId(), null, null, false, false); //NOI18N
            this.tag = tag;
        }

        @Override
        public String getRefSpec () {
            return GitUtils.getPushTagRefSpec(tag.getTagName());
        }

        @Override
        boolean isCreateBranchMapping () {
            return false;
        }
    }
}
