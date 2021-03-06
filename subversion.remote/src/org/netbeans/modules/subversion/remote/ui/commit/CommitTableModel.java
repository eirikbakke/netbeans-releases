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

package org.netbeans.modules.subversion.remote.ui.commit;

import org.openide.util.NbBundle;
import javax.swing.table.AbstractTableModel;
import java.util.*;
import org.netbeans.modules.subversion.remote.FileInformation;
import org.netbeans.modules.subversion.remote.SvnFileNode;
import org.netbeans.modules.subversion.remote.SvnModuleConfig;
import org.netbeans.modules.subversion.remote.util.SvnUtils;
import org.netbeans.modules.remotefs.versioning.api.VCSFileProxySupport;
import org.netbeans.modules.versioning.core.api.VCSFileProxy;

/**
 * Table model for the Commit dialog table.
 *
 * @author Maros Sandor
 */
public class CommitTableModel extends AbstractTableModel {

    public static final String COLUMN_NAME_COMMIT    = "commit"; // NOI18N
    public static final String COLUMN_NAME_NAME    = "name"; // NOI18N
    public static final String COLUMN_NAME_STATUS  = "status"; // NOI18N
    public static final String COLUMN_NAME_ACTION  = "action"; // NOI18N
    public static final String COLUMN_NAME_PATH    = "path"; // NOI18N
    public static final String COLUMN_NAME_BRANCH  = "branch"; // NOI18N

    private final int STATUS_DELETED = FileInformation.STATUS_VERSIONED_DELETEDLOCALLY | FileInformation.STATUS_VERSIONED_REMOVEDLOCALLY;
    private final int STATUS_NEW = FileInformation.STATUS_VERSIONED_ADDEDLOCALLY | FileInformation.STATUS_NOTVERSIONED_NEWLOCALLY;

    private static class RootFile {
        String repositoryPath;
        String rootLocalPath;
    }
    private RootFile rootFile;

    /**
     * Defines labels for Versioning view table columns.
     */ 
    private static final Map<String, String[]> columnLabels = new HashMap<>(4);   

    static {
        ResourceBundle loc = NbBundle.getBundle(CommitTableModel.class);
        columnLabels.put(COLUMN_NAME_COMMIT, new String [] {
                                          loc.getString("CTL_CommitTable_Column_Commit"),  // NOI18N
                                          loc.getString("CTL_CommitTable_Column_Description")}); // NOI18N
        columnLabels.put(COLUMN_NAME_NAME, new String [] {
                                          loc.getString("CTL_CommitTable_Column_File"), 
                                          loc.getString("CTL_CommitTable_Column_File")});
        columnLabels.put(COLUMN_NAME_BRANCH, new String [] { 
                                          loc.getString("CTL_CommitTable_Column_Branch"), 
                                          loc.getString("CTL_CommitTable_Column_Branch")});
        columnLabels.put(COLUMN_NAME_STATUS, new String [] {
                                          loc.getString("CTL_CommitTable_Column_Status"), 
                                          loc.getString("CTL_CommitTable_Column_Status")});
        columnLabels.put(COLUMN_NAME_ACTION, new String [] {
                                          loc.getString("CTL_CommitTable_Column_Action"), 
                                          loc.getString("CTL_CommitTable_Column_Action")});
        columnLabels.put(COLUMN_NAME_PATH, new String [] {
                                          loc.getString("CTL_CommitTable_Column_Folder"), 
                                          loc.getString("CTL_CommitTable_Column_Folder")});
    }
    
    private CommitOptions []    commitOptions;
    private SvnFileNode []      nodes;
    private Index index;
    
    private String [] columns;

    /**
     * Create stable with name, status, action and path columns
     * and empty nodes {@link #setNodes model}.
     */
    public CommitTableModel(String[] columns) {
        setColumns(columns);
        setNodes(new SvnFileNode[0]);
    }

    void setNodes(SvnFileNode [] nodes) {
        this.nodes = nodes;
        this.index = new Index();
        defaultCommitOptions();
        fireTableDataChanged();
    }
    
    void setColumns(String [] cols) {
        if (Arrays.equals(cols, columns)) {
            return;
        }
        columns = cols;
        fireTableStructureChanged();
    }

    /**
     * @return Map&lt;SvnFileNode, CommitOptions>
     */
    public Map<SvnFileNode, CommitOptions> getCommitFiles() {
        Map<SvnFileNode, CommitOptions> ret = new HashMap<>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            ret.put(nodes[i], commitOptions[i]);
        }
        return ret;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnLabels.get(columns[column])[0];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public int getRowCount() {
        return nodes.length;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        String col = columns[columnIndex];
        if (col.equals(COLUMN_NAME_COMMIT)) {
            return Boolean.class;
        } else if (col.equals(COLUMN_NAME_ACTION)) {
            return CommitOptions.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String col = columns[columnIndex];
        return col.equals(COLUMN_NAME_COMMIT);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SvnFileNode node;
        String col = columns[columnIndex];
        if (col.equals(COLUMN_NAME_COMMIT)) {
            return commitOptions[rowIndex] != CommitOptions.EXCLUDE;
        } else if (col.equals(COLUMN_NAME_NAME)) {
            return nodes[rowIndex].getName();
        } else if (col.equals(COLUMN_NAME_BRANCH)) {
            String branch = nodes[rowIndex].getCopy();
            return branch == null ? "" : branch; // NOI18N
        } else if (col.equals(COLUMN_NAME_STATUS)) {
            node = nodes[rowIndex];
            FileInformation finfo =  node.getInformation();
            // this should not be required any more, getEntry is called in preparation pahse, see SvnFileNode#initializeProperties
//            finfo.getEntry(node.getFile());  // HACK returned value is not interesting, point is side effect, it loads ISVNStatus structure
            return finfo.getStatusText();
        } else if (col.equals(COLUMN_NAME_ACTION)) {
            return commitOptions[rowIndex];
        } else if (col.equals(COLUMN_NAME_PATH)) {
            String shortPath = null;
            // XXX this is a mess
            if(rootFile != null) {
                // must convert from native separators to slashes
                shortPath = rootFile.repositoryPath + '/';
            } else {
                shortPath = nodes[rowIndex].getLocation();
                if (shortPath == null) {
                    shortPath = org.openide.util.NbBundle.getMessage(CommitTableModel.class, "CTL_CommitForm_NotInRepository"); // NOI18N
                }
            }
            return shortPath;
        }
        throw new IllegalArgumentException("Column index out of range: " + columnIndex); // NOI18N
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String col = columns[columnIndex];
        if (col.equals(COLUMN_NAME_ACTION)) {
            commitOptions[rowIndex] = (CommitOptions) aValue;
        } else if (col.equals(COLUMN_NAME_COMMIT)) {
            commitOptions[rowIndex] = getCommitOptions(rowIndex, ((Boolean) aValue));
        } else {
            throw new IllegalArgumentException("Column index out of range: " + columnIndex); // NOI18N
        }
        includeExcludeTree(new int[] {rowIndex}, commitOptions[rowIndex] != CommitOptions.EXCLUDE, false);
        fireTableRowsUpdated(0, getRowCount() - 1);
    }

    private void defaultCommitOptions() {
        if (nodes.length == 0) {
            return;
        }
        boolean excludeNew = SvnModuleConfig.getDefault(VCSFileProxySupport.getFileSystem(nodes[0].getFile())).getExludeNewFiles();
        commitOptions = SvnUtils.createDefaultCommitOptions(nodes, excludeNew);
        ensureFilesExcluded();
    }

    public SvnFileNode getNode(int row) {
        return nodes[row];
    }

    public CommitOptions getOptions(int row) {
        return commitOptions[row];
    }

    void setRootFile(String repositoryPath, String rootLocalPath) {
        rootFile = new RootFile();
        rootFile.repositoryPath = repositoryPath;
        rootFile.rootLocalPath = rootLocalPath;
    }

    void setIncluded (int[] rows, boolean include, boolean recursively) {
        for (int rowIndex : rows) {
            commitOptions[rowIndex] = getCommitOptions(rowIndex, include);
        }
        includeExcludeTree(rows, include, recursively);
        fireTableRowsUpdated(0, getRowCount() - 1);
    }

    void setAdded (int[] rows, CommitOptions addOption) {
        for (int rowIndex : rows) {
            commitOptions[rowIndex] = addOption;
        }
        includeExcludeTree(rows, true, false);
        fireTableRowsUpdated(0, getRowCount() - 1);
    }

    private void includeExcludeTree (int[] rows, boolean include, boolean recursively) {
        LinkedList<Integer> rowList = new LinkedList<>();
        for (int row : rows) {
            rowList.add(row);
        }
        if (include) {
            includeExcludeChildren(rowList, recursively ? FileInformation.STATUS_ALL : STATUS_DELETED, true);
            includeExcludeParents(rowList, STATUS_NEW, true);
        } else {
            includeExcludeChildren(rowList, recursively ? FileInformation.STATUS_ALL : STATUS_NEW, false);
            includeExcludeParents(rowList, STATUS_DELETED, false);
        }
    }

    private CommitOptions getCommitOptions (int rowIndex, boolean include) {
        return include ? getCommitOptions(rowIndex) : CommitOptions.EXCLUDE;
    }

    private CommitOptions getCommitOptions (int rowIndex) {
        SvnFileNode node = nodes[rowIndex];
        return SvnUtils.getDefaultCommitOptions(node, false);
    }

    private void ensureFilesExcluded () {
        LinkedList<Integer> newFilesExcluded = new LinkedList<>();
        LinkedList<Integer> deletedFilesExcluded = new LinkedList<>();
        for (int i = 0; i < nodes.length; ++i) {
            SvnFileNode node = nodes[i];
            if (CommitOptions.EXCLUDE.equals(commitOptions[i])
                    && (node.getInformation().getStatus() & (STATUS_NEW)) != 0) {
                newFilesExcluded.add(i);
            } else if (CommitOptions.EXCLUDE.equals(commitOptions[i])
                    && (node.getInformation().getStatus() & (STATUS_DELETED)) != 0) {
                deletedFilesExcluded.add(i);
            }
        }
        includeExcludeChildren(newFilesExcluded, STATUS_NEW, false);
        includeExcludeParents(deletedFilesExcluded, STATUS_DELETED, false);
    }

    private void includeExcludeParents (Collection<Integer> nodeIndexes, int statusMask, boolean include) {
        boolean includeExcludeWholeTree = include && (statusMask & (STATUS_DELETED)) != 0
                || !include && (statusMask & (STATUS_NEW)) != 0;
        HashSet<Integer> toCheck = new HashSet<>();
        boolean[] checkedNodes = new boolean[nodes.length];
        outer:
        for (int nodeIndex : nodeIndexes) {
            checkedNodes[nodeIndex] = true;
            Integer parentIndex = nodeIndex;
            while (parentIndex != null && (nodes[parentIndex].getInformation().getStatus() & statusMask) != 0) {
                nodeIndex = parentIndex;
                if (!includeExcludeWholeTree
                        && includeExcludeEnabled(nodeIndex, include)) { // do not include already included file, which could reset Add as Binary to Add as Text and vice versa
                    commitOptions[nodeIndex] = getCommitOptions(nodeIndex, include);
                }
                parentIndex = index.getParent(nodeIndex);
                if (parentIndex != null && checkedNodes[parentIndex]) {
                    continue outer;
                }
            }
            toCheck.add(nodeIndex);
        }
        if (includeExcludeWholeTree) {
            includeExcludeChildren(toCheck, statusMask, include);
        }
    }

    private void includeExcludeChildren (Collection<Integer> nodeIndexes, int statusMask, boolean include) {
        boolean[] checkedNodes = new boolean[nodes.length];
        HashSet<Integer> toCheck = new HashSet<>();
        for (int nodeIndex : nodeIndexes) {
            toCheck.add(nodeIndex);
        }
        while (!toCheck.isEmpty()) {
            Iterator<Integer> it = toCheck.iterator();
            Integer nodeIndex = it.next();
            it.remove();
            if (checkedNodes[nodeIndex]) {
                continue;
            }
            checkedNodes[nodeIndex] = true;
            SvnFileNode node = nodes[nodeIndex];
            if ((node.getInformation().getStatus() & statusMask) !=  0) {
                if (includeExcludeEnabled(nodeIndex, include)) { // do not include already included file, which could reset Add as Binary to Add as Text and vice versa
                    commitOptions[nodeIndex] = getCommitOptions(nodeIndex, include);
                }
                Integer[] childrenIndexes = index.getChildren(nodeIndex);
                if (childrenIndexes != null) {
                    toCheck.addAll(Arrays.asList(childrenIndexes));
                }
            }
        }
    }

    private boolean includeExcludeEnabled (int nodeIndex, boolean include) {
        return !include || commitOptions[nodeIndex] == CommitOptions.EXCLUDE;
    }

    private class Index {

        private HashMap<VCSFileProxy, Value> fileToIndex;

        public Index() {
            constructIndex();
        }

        private void constructIndex () {
            fileToIndex = new HashMap<>(nodes.length);
            for (int i = 0; i < nodes.length; ++i) {
                Value value = new Value(i);
                fileToIndex.put(nodes[i].getFile(), value);
            }
            for (int i = 0; i < nodes.length; ++i) {
                VCSFileProxy parentFile = nodes[i].getFile().getParentFile();
                if (parentFile != null) {
                    Value value = fileToIndex.get(parentFile);
                    if (value != null) {
                        value.addChild(i);
                    }
                }
            }
        }

        private Integer getParent (int nodeIndex) {
            VCSFileProxy parentFile = nodes[nodeIndex].getFile().getParentFile();
            Value parentValue = parentFile == null ? null : fileToIndex.get(parentFile);
            return parentValue == null ? null : parentValue.nodeIndex;
        }

        private Integer[] getChildren (int nodeIndex) {
            Value value = fileToIndex.get(nodes[nodeIndex].getFile());
            return value == null || value.childrenIndexes == null ? null : value.childrenIndexes.toArray(new Integer[value.childrenIndexes.size()]);
        }

        private class Value {
            private Integer nodeIndex;
            private Set<Integer> childrenIndexes;

            private Value(int index) {
                this.nodeIndex = index;
            }

            private void addChild(int childIndex) {
                if (childrenIndexes == null) {
                    childrenIndexes = new HashSet<>();
                }
                childrenIndexes.add(childIndex);
            }
        }
    }
}
