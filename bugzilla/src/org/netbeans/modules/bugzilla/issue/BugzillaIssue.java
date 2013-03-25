/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugzilla.issue;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.netbeans.api.diff.PatchUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.bugzilla.Bugzilla;
import org.netbeans.modules.bugtracking.issuetable.IssueNode;
import org.netbeans.modules.bugtracking.spi.BugtrackingController;
import org.netbeans.modules.bugtracking.spi.IssueProvider;
import org.netbeans.modules.bugtracking.issuetable.ColumnDescriptor;
import org.netbeans.modules.bugtracking.kenai.spi.OwnerInfo;
import org.netbeans.modules.bugtracking.spi.IssueStatusProvider;
import org.netbeans.modules.bugtracking.ui.issue.cache.IssueCache;
import org.netbeans.modules.bugtracking.util.BugtrackingUtil;
import org.netbeans.modules.bugtracking.util.UIUtils;
import org.netbeans.modules.bugzilla.commands.AddAttachmentCommand;
import org.netbeans.modules.bugzilla.repository.BugzillaConfiguration;
import org.netbeans.modules.bugzilla.repository.BugzillaRepository;
import org.netbeans.modules.bugzilla.commands.GetAttachmentCommand;
import org.netbeans.modules.mylyn.util.SubmitCommand;
import org.netbeans.modules.bugzilla.repository.IssueField;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.bugzilla.util.BugzillaUtil;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Stupka
 * @author Jan Stola
 */
public class BugzillaIssue {

    public static final String RESOLVE_FIXED = "FIXED";                                                         // NOI18N
    public static final String RESOLVE_DUPLICATE = "DUPLICATE";                                                 // NOI18N
    public static final String VCSHOOK_BUGZILLA_FIELD = "netbeans.vcshook.bugzilla.";                           // NOI18N
    private static final SimpleDateFormat CC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");            // NOI18N
    private static final SimpleDateFormat MODIFIED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // NOI18N
    private static final SimpleDateFormat CREATED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");       // NOI18N
    private static final int SHORTENED_SUMMARY_LENGTH = 22;

    private TaskData data;
    private BugzillaRepository repository;

    private IssueController controller;
    private IssueNode node;
    private OwnerInfo info;

    static final String LABEL_NAME_ID           = "bugzilla.issue.id";          // NOI18N
    static final String LABEL_NAME_SEVERITY     = "bugzilla.issue.severity";    // NOI18N
    static final String LABEL_NAME_ISSUE_TYPE   = "bugzilla.issue.issue_type";  // NOI18N
    static final String LABEL_NAME_PRIORITY     = "bugzilla.issue.priority";    // NOI18N
    static final String LABEL_NAME_STATUS       = "bugzilla.issue.status";      // NOI18N
    static final String LABEL_NAME_RESOLUTION   = "bugzilla.issue.resolution";  // NOI18N
    static final String LABEL_NAME_ASSIGNED_TO  = "bugzilla.issue.assigned";    // NOI18N
    static final String LABEL_NAME_PRODUCT      = "bugzilla.issue.product";     // NOI18N
    static final String LABEL_NAME_COMPONENT    = "bugzilla.issue.component";   // NOI18N
    static final String LABEL_NAME_VERSION      = "bugzilla.issue.version";     // NOI18N
    static final String LABEL_NAME_OS           = "bugzilla.issue.os";          // NOI18N
    static final String LABEL_NAME_PLATFORM     = "bugzilla.issue.platform";    // NOI18N
    static final String LABEL_NAME_MILESTONE    = "bugzilla.issue.milestone";   // NOI18N
    static final String LABEL_NAME_REPORTER     = "bugzilla.issue.reporter";    // NOI18N
    static final String LABEL_NAME_MODIFICATION = "bugzilla.issue.modified";    // NOI18N
    static final String LABEL_NAME_QA_CONTACT   = "bugzilla.issue.qa_contact";  // NOI18N
    static final String LABEL_NAME_KEYWORDS     = "bugzilla.issue.keywords";    // NOI18N
    static final String LABEL_NAME_WHITEBOARD   = "bugzilla.issue.whiteboard";  // NOI18N

    /**
     * IssueProvider wasn't seen yet
     */
    static final int FIELD_STATUS_IRELEVANT = -1;

    /**
     * Field wasn't changed since the issue was seen the last time
     */
    static final int FIELD_STATUS_UPTODATE = 1;

    /**
     * Field has a value in oposite to the last time when it was seen
     */
    static final int FIELD_STATUS_NEW = 2;

    /**
     * Field was changed since the issue was seen the last time
     */
    static final int FIELD_STATUS_MODIFIED = 4;
    private Map<String, String> seenAtributes;
    private String initialProduct = null;

    private Map<String, String> attributes;
    private Map<String, TaskOperation> availableOperations;

    private static final RequestProcessor parallelRP = new RequestProcessor("BugzillaIssue", 5); //NOI18N
    private final PropertyChangeSupport support;

    public BugzillaIssue(TaskData data, BugzillaRepository repo) {
        this.data = data;
        this.repository = repo;
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Notify listeners on this issue that its data were changed
     */
    protected void fireDataChanged() {
        support.firePropertyChange(IssueProvider.EVENT_ISSUE_REFRESHED, null, null);
    }
    
    private void fireSeenChanged(boolean wasSeen, boolean seen) {
        support.firePropertyChange(IssueStatusProvider.EVENT_SEEN_CHANGED, wasSeen, seen);
    }

    public boolean isNew() {
        return data == null || data.isNew();
    }

    void opened() {
        if(Bugzilla.LOG.isLoggable(Level.FINE)) Bugzilla.LOG.log(Level.FINE, "issue {0} open start", new Object[] {getID()});
        if(!data.isNew()) {
            // 1.) to get seen attributes makes no sense for new issues
            // 2.) set seenAtributes on issue open, before its actuall
            //     state is written via setSeen().
            seenAtributes = repository.getIssueCache().getSeenAttributes(getID());
        }
        String refresh = System.getProperty("org.netbeans.modules.bugzilla.noIssueRefresh"); // NOI18N
        if(refresh != null && refresh.equals("true")) {                                      // NOI18N
            return;
        }
        repository.scheduleForRefresh(getID());
        if(Bugzilla.LOG.isLoggable(Level.FINE)) Bugzilla.LOG.log(Level.FINE, "issue {0} open finish", new Object[] {getID()});
    }

    void closed() {
        if(Bugzilla.LOG.isLoggable(Level.FINE)) Bugzilla.LOG.log(Level.FINE, "issue {0} close start", new Object[] {getID()});
        repository.stopRefreshing(getID());
        seenAtributes = null;
        if(Bugzilla.LOG.isLoggable(Level.FINE)) Bugzilla.LOG.log(Level.FINE, "issue {0} close finish", new Object[] {getID()});
    }

    public String getDisplayName() {
        return getDisplayName(data);
    }

    public static String getDisplayName(TaskData taskData) {
        return taskData.isNew() ?
                NbBundle.getMessage(BugzillaIssue.class, "CTL_NewIssue") : // NOI18N
                NbBundle.getMessage(BugzillaIssue.class, "CTL_Issue", new Object[] {getID(taskData), getSummary(taskData)}); // NOI18N
    }
    
    // XXX not the same as in Issue.getShortenedDisplayName()

//    public String getShortenedDisplayName() {
//        if (data.isNew()) {
//            return getDisplayName();
//        }
//
//        String shortSummary = TextUtils.shortenText(getSummary(),
//                                                    2,    //try at least 2 words
//                                                    SHORTENED_SUMMARY_LENGTH);
//        return NbBundle.getMessage(BugzillaIssue.class,
//                                   "CTL_Issue",                         //NOI18N
//                                   new Object[] {getID(), shortSummary});
//    }

    public String getTooltip() {
        return getDisplayName();
    }

    public static ColumnDescriptor[] getColumnDescriptors(BugzillaRepository repository) {
        ResourceBundle loc = NbBundle.getBundle(BugzillaIssue.class);
        JTable t = new JTable();
        List<ColumnDescriptor<String>> ret = new LinkedList<ColumnDescriptor<String>>();
        
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_ID, String.class,
                                              loc.getString("CTL_Issue_ID_Title"),                // NOI18N
                                              loc.getString("CTL_Issue_ID_Desc"),                 // NOI18N
                                              UIUtils.getColumnWidthInPixels(6, t)));
            ret.add(new ColumnDescriptor<String>(IssueNode.LABEL_NAME_SUMMARY, String.class,
                                              loc.getString("CTL_Issue_Summary_Title"),           // NOI18N
                                              loc.getString("CTL_Issue_Summary_Desc")));          // NOI18N
            ret.add(BugzillaUtil.isNbRepository(repository)
                                        ?
                                              new ColumnDescriptor<String>(LABEL_NAME_ISSUE_TYPE, String.class,
                                              loc.getString("CTL_Issue_Issue_Type_Title"),        // NOI18N
                                              loc.getString("CTL_Issue_Issue_Type_Desc"),         // NOI18N
                                              0)
                                        :
                                              new ColumnDescriptor<String>(LABEL_NAME_SEVERITY, String.class,
                                              loc.getString("CTL_Issue_Severity_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Severity_Desc"),           // NOI18N
                                              0));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_PRIORITY, String.class,
                                              loc.getString("CTL_Issue_Priority_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Priority_Desc"),           // NOI18N
                                              0));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_STATUS, String.class,
                                              loc.getString("CTL_Issue_Status_Title"),            // NOI18N
                                              loc.getString("CTL_Issue_Status_Desc"),             // NOI18N
                                              0));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_RESOLUTION, String.class,
                                              loc.getString("CTL_Issue_Resolution_Title"),        // NOI18N
                                              loc.getString("CTL_Issue_Resolution_Desc"),         // NOI18N
                                              0));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_ASSIGNED_TO, String.class,
                                              loc.getString("CTL_Issue_Assigned_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Assigned_Desc"),           // NOI18N
                                              0));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_PRODUCT, String.class,
                                              loc.getString("CTL_Issue_Product_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Product_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_COMPONENT, String.class,
                                              loc.getString("CTL_Issue_Component_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Component_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_VERSION, String.class,
                                              loc.getString("CTL_Issue_Version_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Version_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_OS, String.class,
                                              loc.getString("CTL_Issue_OS_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_OS_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_PLATFORM, String.class,
                                              loc.getString("CTL_Issue_Platform_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Platform_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_MILESTONE, String.class,
                                              loc.getString("CTL_Issue_Milestone_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Milestone_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_REPORTER, String.class,
                                              loc.getString("CTL_Issue_Reporter_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Reporter_Desc"),           // NOI18N
                                              0, false));
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_MODIFICATION, String.class,
                                              loc.getString("CTL_Issue_Modification_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Modification_Desc"),           // NOI18N
                                              0, false));
            if(BugzillaUtil.showQAContact(repository)) {
                ret.add(new ColumnDescriptor<String>(LABEL_NAME_QA_CONTACT, String.class,
                                              loc.getString("CTL_Issue_QA_Contact_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_QA_Contact_Desc"),           // NOI18N
                                              0, false));
            }
            ret.add(new ColumnDescriptor<String>(LABEL_NAME_KEYWORDS, String.class,
                                              loc.getString("CTL_Issue_Keywords_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Keywords_Desc"),           // NOI18N
                                              0, false));
            if(BugzillaUtil.showStatusWhiteboard(repository)) {
                ret.add(new ColumnDescriptor<String>(LABEL_NAME_WHITEBOARD, String.class,
                                              loc.getString("CTL_Issue_Whiteboard_Title"),          // NOI18N
                                              loc.getString("CTL_Issue_Whiteboard_Desc"),           // NOI18N
                                              0, false));
            }
        return ret.toArray(new ColumnDescriptor[ret.size()]);
    }


    public BugtrackingController getController() {
        if (controller == null) {
            controller = new IssueController(this);
        }
        return controller;
    }

    @Override
    public String toString() {
        String str = getID() + " : "  + getSummary(); // NOI18N
        return str;
    }

    public IssueNode getNode() {
        if(node == null) {
            node = createNode();
        }
        return node;
    }

    public void setOwnerInfo(OwnerInfo info) {
        this.info = info;
    }
    
    public OwnerInfo getOwnerInfo() {
        return info;
    }

    public Map<String, String> getAttributes() {
        if(attributes == null) {
            attributes = new HashMap<String, String>();
            String value;
            for (IssueField field : getRepository().getConfiguration().getFields()) {
                value = getFieldValue(field);
                if(value != null && !value.trim().equals("")) {                 // NOI18N
                    attributes.put(field.getKey(), value);
                }
            }
        }
        return attributes;
    }

    public void setSeen(boolean seen) throws IOException {
        repository.getIssueCache().setSeen(getID(), seen);
    }

    private boolean wasSeen() {
        return repository.getIssueCache().wasSeen(getID());
    }

    public Date getLastModifyDate() {
        String value = getFieldValue(IssueField.MODIFICATION);
        if(value != null && !value.trim().equals("")) {
            try {
                return MODIFIED_DATE_FORMAT.parse(value);
            } catch (ParseException ex) {
                Bugzilla.LOG.log(Level.WARNING, value, ex);
            }
        }
        return null;
    }

    public long getLastModify() {
        Date lastModifyDate = getLastModifyDate();
        if(lastModifyDate != null) {
            return lastModifyDate.getTime();
        } else {
            return -1;
        }
    }

    public Date getCreatedDate() {
        String value = getFieldValue(IssueField.CREATION);
        if(value != null && !value.trim().equals("")) {
            try {
                return CREATED_DATE_FORMAT.parse(value);
            } catch (ParseException ex) {
                Bugzilla.LOG.log(Level.WARNING, value, ex);
            }
        }
        return null;
    }

    public long getCreated() {
        Date createdDate = getCreatedDate();
        if (createdDate != null) {
            return createdDate.getTime();
        } else {
            return -1;
        }
    }

    public String getRecentChanges() {
        if(wasSeen()) {
            return "";                                                          // NOI18N
        }
        int status = repository.getIssueCache().getStatus(getID());
        if(status == IssueCache.ISSUE_STATUS_NEW) {
            return NbBundle.getMessage(BugzillaIssue.class, "LBL_NEW_STATUS");
        } else if(status == IssueCache.ISSUE_STATUS_MODIFIED) {
            List<IssueField> changedFields = new ArrayList<IssueField>();
            assert getSeenAttributes() != null;
            for (IssueField f : getRepository().getConfiguration().getFields()) {
                if (f==IssueField.MODIFICATION
                        || f==IssueField.REPORTER_NAME
                        || f==IssueField.QA_CONTACT_NAME
                        || f==IssueField.ASSIGNED_TO_NAME) {
                    continue;
                }
                String value = getFieldValue(f);
                String seenValue = getSeenValue(f);
                if(!value.trim().equals(seenValue)) {
                    changedFields.add(f);
                }
            }
            int changedCount = changedFields.size();
            if(changedCount == 1) {
                String ret = null;
                for (IssueField changedField : changedFields) {
                    if (changedField == IssueField.SUMMARY) {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_SUMMARY_CHANGED_STATUS"); // NOI18N
                    } else if (changedField == IssueField.CC) {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_CC_FIELD_CHANGED_STATUS"); // NOI18N
                    } else if (changedField == IssueField.KEYWORDS) {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_KEYWORDS_CHANGED_STATUS"); // NOI18N
                    } else if (changedField == IssueField.DEPENDS_ON || changedField == IssueField.BLOCKS) {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_DEPENDENCE_CHANGED_STATUS"); // NOI18N
                    } else if (changedField == IssueField.COMMENT_COUNT) {
                        String value = getFieldValue(changedField);
                        String seenValue = getSeenValue(changedField);
                        if(seenValue.equals("")) { // NOI18N
                            seenValue = "0"; // NOI18N
                        }
                        int count = 0;
                        try {
                            count = Integer.parseInt(value) - Integer.parseInt(seenValue);
                        } catch(NumberFormatException ex) {
                            Bugzilla.LOG.log(Level.WARNING, ret, ex);
                        }
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_COMMENTS_CHANGED", new Object[] {count}); // NOI18N
                    } else if (changedField == IssueField.ATTACHEMENT_COUNT) {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_ATTACHMENTS_CHANGED"); // NOI18N
                    } else {
                        ret = NbBundle.getMessage(BugzillaIssue.class, "LBL_CHANGED_TO", new Object[] {changedField.getDisplayName(), getFieldValue(changedField)}); // NOI18N
                    }
                }
                return ret;
            } else {
                String ret = null;
                for (IssueField changedField : changedFields) {
                    String key;
                    if (changedField == IssueField.SUMMARY) {
                        key = "LBL_CHANGES_INCL_SUMMARY"; // NOI18N
                    } else if (changedField == IssueField.PRIORITY) {
                        key = "LBL_CHANGES_INCL_PRIORITY"; // NOI18N
                    } else if (changedField == IssueField.SEVERITY) {
                        key = "LBL_CHANGES_INCL_SEVERITY"; // NOI18N
                    } else if (changedField == IssueField.ISSUE_TYPE) {
                        key = "LBL_CHANGES_INCL_ISSUE_TYPE"; // NOI18N
                    } else if (changedField == IssueField.PRODUCT) {
                        key = "LBL_CHANGES_INCL_PRODUCT"; // NOI18N
                    } else if (changedField == IssueField.COMPONENT) {
                        key = "LBL_CHANGES_INCL_COMPONENT"; // NOI18N
                    } else if (changedField == IssueField.PLATFORM) {
                        key = "LBL_CHANGES_INCL_PLATFORM"; // NOI18N
                    } else if (changedField == IssueField.VERSION) {
                        key = "LBL_CHANGES_INCL_VERSION"; // NOI18N
                    } else if (changedField == IssueField.MILESTONE) {
                        key = "LBL_CHANGES_INCL_MILESTONE"; // NOI18N
                    } else if (changedField == IssueField.KEYWORDS) {
                        key = "LBL_CHANGES_INCL_KEYWORDS"; // NOI18N
                    } else if (changedField == IssueField.URL) {
                        key = "LBL_CHANGES_INCL_URL"; // NOI18N
                    } else if (changedField == IssueField.ASSIGNED_TO) {
                        key = "LBL_CHANGES_INCL_ASSIGNEE"; // NOI18N
                    } else if (changedField == IssueField.QA_CONTACT) {
                        key = "LBL_CHANGES_INCL_QA_CONTACT"; // NOI18N
                    } else if (changedField == IssueField.DEPENDS_ON || changedField == IssueField.BLOCKS) {
                        key = "LBL_CHANGES_INCLUSIVE_DEPENDENCE"; // NOI18N
                    } else {
                        key = "LBL_CHANGES"; // NOI18N
                    }
                    return NbBundle.getMessage(BugzillaIssue.class, key, new Object[] {changedCount});
                }
            }
        }
        return "";                                                              // NOI18N
    }

    /**
     * Returns the id from the given taskData or null if taskData.isNew()
     * @param taskData
     * @return id or null
     */
    public static String getID(TaskData taskData) {
        if(taskData.isNew()) {
            return null;
        }
        return taskData.getTaskId();
    }

    /**
     * Returns the id from the given taskData or null if taskData.isNew()
     * @param taskData
     * @return id or null
     */
    public static String getSummary(TaskData taskData) {
        if(taskData.isNew()) {
            return null;
        }
        return getFieldValue(IssueField.SUMMARY, taskData);
    }

    public BugzillaRepository getRepository() {
        return repository;
    }

    public String getID() {
        return getID(data);
    }

    public String getSummary() {
        return getFieldValue(IssueField.SUMMARY);
    }

    public void setTaskData(TaskData taskData) {
        assert !taskData.isPartial();
        data = taskData;
        attributes = null; // reset
        availableOperations = null;
        Bugzilla.getInstance().getRequestProcessor().post(new Runnable() {
            @Override
            public void run() {
                ((BugzillaIssueNode)getNode()).fireDataChanged();
                fireDataChanged();
                refreshViewData(false);
            }
        });
    }

    TaskData getTaskData() {
        return data;
    }

    /**
     * Returns the value represented by the given field
     *
     * @param f
     * @return
     */
    public String getFieldValue(IssueField f) {
        return getFieldValue(f, data);
    }

    private static String getFieldValue(IssueField f, TaskData taskData) {
        if(f.isSingleAttribute()) {
            TaskAttribute a = taskData.getRoot().getMappedAttribute(f.getKey());
            if(a != null && a.getValues().size() > 1) {
                return listValues(a);
            }
            return a != null ? a.getValue() : ""; // NOI18N
        } else {
            List<TaskAttribute> attrs = taskData.getAttributeMapper().getAttributesByType(taskData, f.getKey());
            // returning 0 would set status MODIFIED instead of NEW
            return "" + ( attrs != null && attrs.size() > 0 ?  attrs.size() : ""); // NOI18N
        }
    }

    /**
     * Returns a comma separated list created
     * from the values returned by TaskAttribute.getValues()
     *
     * @param a
     * @return
     */
    private static String listValues(TaskAttribute a) {
        if(a == null) {
            return "";                                                          // NOI18N
        }
        StringBuilder sb = new StringBuilder();
        List<String> l = a.getValues();
        for (int i = 0; i < l.size(); i++) {
            String s = l.get(i);
            sb.append(s);
            if(i < l.size() -1) {
                sb.append(",");                                                 // NOI18N
            }
        }
        return sb.toString();
    }

    void setFieldValue(IssueField f, String value) {
        if(f.isReadOnly()) {
            assert false : "can't set value into IssueField " + f.getKey();       // NOI18N
            return;
        }
        TaskAttribute a = data.getRoot().getMappedAttribute(f.getKey());
        if(a == null) {
            a = new TaskAttribute(data.getRoot(), f.getKey());
        }
        if(f == IssueField.PRODUCT) {
            handleProductChange(a);
        }
        Bugzilla.LOG.log(Level.FINER, "setting value [{0}] on field [{1}]", new Object[]{value, f.getKey()}) ;
        a.setValue(value);
    }

    void setFieldValues(IssueField f, List<String> ccs) {
        TaskAttribute a = data.getRoot().getMappedAttribute(f.getKey());
        if(a == null) {
            a = new TaskAttribute(data.getRoot(), f.getKey());
        }
        a.setValues(ccs);
    }

    public List<String> getFieldValues(IssueField f) {
        if(f.isSingleAttribute()) {
            TaskAttribute a = data.getRoot().getMappedAttribute(f.getKey());
            if(a != null) {
                return a.getValues();
            } else {
                return Collections.emptyList();
            }
        } else {
            List<String> ret = new ArrayList<String>();
            ret.add(getFieldValue(f));
            return ret;
        }
    }

    /**
     * Returns a status value for the given field<br>
     * <ul>
     *  <li>{@link #FIELD_STATUS_IRELEVANT} - issue wasn't seen yet
     *  <li>{@link #FIELD_STATUS_UPTODATE} - field value wasn't changed
     *  <li>{@link #FIELD_STATUS_MODIFIED} - field value was changed
     *  <li>{@link #FIELD_STATUS_NEW} - field has a value for the first time since it was seen
     * </ul>
     * @param f IssueField
     * @return a status value
     */
    int getFieldStatus(IssueField f) {
        String seenValue = getSeenValue(f);
        if(seenValue.equals("") && !seenValue.equals(getFieldValue(f))) {       // NOI18N
            return FIELD_STATUS_NEW;
        } else if (!seenValue.equals(getFieldValue(f))) {
            return FIELD_STATUS_MODIFIED;
        }
        return FIELD_STATUS_UPTODATE;
    }

    private IssueNode createNode() {
        return new BugzillaIssueNode(this);
    }

    private void handleProductChange(TaskAttribute a) {
        if(!data.isNew() && initialProduct == null) {
            initialProduct = a.getValue();
        }
    }

    void resolve(String resolution) {
        assert !data.isNew();

        String value = getFieldValue(IssueField.STATUS);
        if(!value.equals("RESOLVED")) {                                         // NOI18N
            setOperation(BugzillaOperation.resolve);
            TaskAttribute rta = data.getRoot();
            TaskAttribute ta = rta.getMappedAttribute(BugzillaOperation.resolve.getInputId());
            if(ta != null) { // ta can be null when changing status from CLOSED to RESOLVED
                ta.setValue(resolution);
            }
        }
    }

    void accept() {
        setOperation(BugzillaOperation.accept);
    }

    void duplicate(String id) {
        setOperation(BugzillaOperation.duplicate);
        TaskAttribute rta = data.getRoot();
        TaskAttribute ta = rta.getMappedAttribute(BugzillaOperation.duplicate.getInputId());
        ta.setValue(id);
    }

    boolean canReassign() {
        BugzillaConfiguration rc = getRepository().getConfiguration();
        final BugzillaVersion installedVersion = rc != null ? rc.getInstalledVersion() : null;
        boolean oldRepository = installedVersion != null ? installedVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_2) < 0 : false;
        if (oldRepository) {
            TaskAttribute rta = data.getRoot();
            TaskAttribute ta = rta.getMappedAttribute(BugzillaOperation.reassign.getInputId());
            return (ta != null);
        } else {
            return true;
        }
    }
    
    boolean canAssignToDefault() {
        BugzillaConfiguration rc = getRepository().getConfiguration();
        final BugzillaVersion installedVersion = rc != null ? rc.getInstalledVersion() : null;
        boolean pre4 = installedVersion != null ? installedVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_3_0) <= 0 : false;
        if(pre4) {
            return BugzillaOperation.reassignbycomponent.getInputId() != null ? 
                        data.getRoot().getMappedAttribute(BugzillaOperation.reassignbycomponent.getInputId()) != null :
                        false;
        } else {
            boolean before4 = installedVersion != null ? installedVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0 : false;
            TaskAttribute ta = data.getRoot().getAttribute(BugzillaAttribute.SET_DEFAULT_ASSIGNEE.getKey()); 
            if(before4) {
                return ta != null;
            } else {
                BugzillaAttribute key = BugzillaAttribute.SET_DEFAULT_ASSIGNEE;
                TaskAttribute operationAttribute = data.getRoot().createAttribute(key.getKey());
                operationAttribute.getMetaData().defaults().setReadOnly(key.isReadOnly()).setKind(key.getKind()).setLabel(key.toString()).setType(key.getType());
                operationAttribute.setValue("0"); // NOI18N
                return true;
            }     
        }
    }
    
    boolean hasTimeTracking() {
        return data.getRoot().getMappedAttribute(BugzillaAttribute.ACTUAL_TIME.getKey()) != null; // XXX dummy
    }

    void reassign(String user) {
        setOperation(BugzillaOperation.reassign);
        TaskAttribute rta = data.getRoot();
        TaskAttribute ta = rta.getMappedAttribute(BugzillaOperation.reassign.getInputId());
        if(ta != null) ta.setValue(user);
        ta = rta.getMappedAttribute(BugzillaAttribute.ASSIGNED_TO.getKey());
        if(ta != null) ta.setValue(user);
    }

    
    void assignToDefault() {
        BugzillaAttribute key = BugzillaAttribute.SET_DEFAULT_ASSIGNEE;
        TaskAttribute operationAttribute = data.getRoot().getAttribute(key.getKey());
        if (operationAttribute == null) {
            setOperation(BugzillaOperation.reassignbycomponent);
        } else {
            operationAttribute.setValue("1");                                   // NOI18N
        }
    }

    void verify() {
        setOperation(BugzillaOperation.verify);
    }

    void close() {
        setOperation(BugzillaOperation.close);
    }

    void reopen() {
        setOperation(BugzillaOperation.reopen);
    }

    private void setOperation(BugzillaOperation operation) {
        TaskAttribute rta = data.getRoot();
        TaskAttribute ta = rta.getMappedAttribute(TaskAttribute.OPERATION);
        ta.setValue(operation.toString());
    }

    List<Attachment> getAttachments() {
        List<TaskAttribute> attrs = data.getAttributeMapper().getAttributesByType(data, TaskAttribute.TYPE_ATTACHMENT);
        if (attrs == null) {
            return Collections.emptyList();
        }
        List<Attachment> attachments = new ArrayList<Attachment>(attrs.size());
        for (TaskAttribute taskAttribute : attrs) {
            attachments.add(new Attachment(taskAttribute));
        }
        return attachments;
    }

    public void addAttachment(File file, final String comment, final String desc, String contentType, final boolean patch) {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N
        final FileTaskAttachmentSource attachmentSource = new FileTaskAttachmentSource(file);
        if (contentType == null) {
            file = FileUtil.normalizeFile(file);
            String ct = FileUtil.getMIMEType(FileUtil.toFileObject(file));
            if ((ct != null) && (!"content/unknown".equals(ct))) { // NOI18N
                contentType = ct;
            } else {
                contentType = FileTaskAttachmentSource.getContentTypeFromFilename(file.getName());
            }
        }
        attachmentSource.setContentType(contentType);

        final TaskAttribute attAttribute = new TaskAttribute(data.getRoot(),  TaskAttribute.TYPE_ATTACHMENT);
        TaskAttributeMapper mapper = attAttribute.getTaskData().getAttributeMapper();
        TaskAttribute a = attAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_DESCRIPTION);
        a.setValue(desc);
        a = attAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
        mapper.setBooleanValue(a, patch);
        a = attAttribute.createMappedAttribute(TaskAttribute.ATTACHMENT_CONTENT_TYPE);
        a.setValue(contentType);

        refresh(); // refresh might fail, but be optimistic and still try to force add attachment
        AddAttachmentCommand cmd = new AddAttachmentCommand(getID(), repository, comment, attachmentSource, file, attAttribute);
        repository.getExecutor().execute(cmd);
        if(!cmd.hasFailed()) {
            refresh(getID(), true); // XXX to much refresh - is there no other way?
        }
    }

    Comment[] getComments() {
        List<TaskAttribute> attrs = data.getAttributeMapper().getAttributesByType(data, TaskAttribute.TYPE_COMMENT);
        if (attrs == null) {
            return new Comment[0];
        }
        List<Comment> comments = new ArrayList<Comment>();
        for (TaskAttribute taskAttribute : attrs) {
            comments.add(new Comment(taskAttribute));
        }
        return comments.toArray(new Comment[comments.size()]);
    }

    // XXX carefull - implicit refresh
    public void addComment(String comment, boolean close) {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N
        if(comment == null && !close) {
            return;
        }
        refresh();

        // resolved attrs
        if(close) {
            Bugzilla.LOG.log(Level.FINER, "resolving issue #{0} as fixed", new Object[]{getID()});
            resolve(RESOLVE_FIXED); // XXX constant?

            if(BugzillaUtil.isNbRepository(repository)) {
                // check for other preselections
                Properties p = System.getProperties();
                Enumeration<Object> keys = p.keys();
                List<String> keyList = new LinkedList<String>();
                while(keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    if(key.toString().startsWith(VCSHOOK_BUGZILLA_FIELD)) {
                        keyList.add(key.toString());
                    }
                }
                for (String key : keyList) {
                    String fieldName = key.substring(VCSHOOK_BUGZILLA_FIELD.length());
                    String value = p.getProperty(key);
                    IssueField issueField = repository.getConfiguration().getField(fieldName);
                    if(issueField != null) {
                        if(issueField.isReadOnly()) {
                            Bugzilla.LOG.log(Level.WARNING, "field [{0}] is read-only.", new Object[]{repository.getUrl(), fieldName});
                        } else {
                            setFieldValue(issueField, value);
                        }
                    } else {
                        Bugzilla.LOG.log(Level.WARNING, "Repsitory [{0}] has no field [{1}]", new Object[]{repository.getUrl(), fieldName});
                    }
                }
            }

        }
        if(comment != null) {
            addComment(comment);
        }        

        submitAndRefresh();
    }

    public void addComment(String comment) {
        if(comment != null) {
            Bugzilla.LOG.log(Level.FINER, "adding comment [{0}] to issue #{1}", new Object[]{comment, getID()});
            TaskAttribute ta = data.getRoot().createMappedAttribute(TaskAttribute.COMMENT_NEW);
            ta.setValue(comment);
        }
    }

    public void attachPatch(File file, String description) {
        boolean isPatch = true;
        // HACK for attaching hg bundles - they are NOT patches
        isPatch = !file.getName().endsWith(".hg"); // NOI18N
        addAttachment(file, null, description, null, isPatch);
    }

    private void prepareSubmit() {
        if (initialProduct != null) {
            // product change
            TaskAttribute ta = data.getRoot().getMappedAttribute(BugzillaAttribute.CONFIRM_PRODUCT_CHANGE.getKey());
            if (ta == null) {
                ta = BugzillaTaskDataHandler.createAttribute(data.getRoot(), BugzillaAttribute.CONFIRM_PRODUCT_CHANGE);
            }
            ta.setValue("1");                                                   // NOI18N
        }
    }

    boolean submitAndRefresh() {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N

        prepareSubmit();
        final boolean wasNew = data.isNew();
        final boolean wasSeenAlready = wasNew || repository.getIssueCache().wasSeen(getID());
        
        SubmitCommand submitCmd = 
            new SubmitCommand(
                Bugzilla.getInstance().getRepositoryConnector(),
                getRepository().getTaskRepository(), 
                data);
        repository.getExecutor().execute(submitCmd);

        if (!wasNew) {
            refresh();
        } else {
            RepositoryResponse rr = submitCmd.getRepositoryResponse();
            if(!submitCmd.hasFailed()) {
                assert rr != null;
                String id = rr.getTaskId();
                Bugzilla.LOG.log(Level.FINE, "created issue #{0}", id);
                refresh(id, true);
            } else {
                Bugzilla.LOG.log(Level.FINE, "submiting failed");
                if(rr != null) {
                    Bugzilla.LOG.log(Level.FINE, "repository response {0}", rr.getReposonseKind());
                } else {
                    Bugzilla.LOG.log(Level.FINE, "no repository response available");
                }
            }
        }

        if(submitCmd.hasFailed()) {
            return false;
        }

        // it was the user who made the changes, so preserve the seen status if seen already
        if (wasSeenAlready) {
            try {
                repository.getIssueCache().setSeen(getID(), true);
                // it was the user who made the changes, so preserve the seen status if seen already
            } catch (IOException ex) {
                Bugzilla.LOG.log(Level.SEVERE, null, ex);
            }
        }
        if(wasNew) {
            // a new issue was created -> refresh all queries
            repository.refreshAllQueries();
        }

        try {
            seenAtributes = null;
            setSeen(true);
        } catch (IOException ex) {
            Bugzilla.LOG.log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean refresh() {
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N
        return refresh(getID(), false);
    }

    private boolean refresh(String id, boolean afterSubmitRefresh) { // XXX cacheThisIssue - we probalby don't need this, just always set the issue into the cache
        assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N
        try {
            Bugzilla.LOG.log(Level.FINE, "refreshing issue #{0}", id);
            TaskData td = BugzillaUtil.getTaskData(repository, id);
            if(td == null) {
                return false;
            }
            getRepository().getIssueCache().setIssueData(this, td); // XXX
            getRepository().ensureConfigurationUptodate(this);
            refreshViewData(afterSubmitRefresh);
        } catch (IOException ex) {
            Bugzilla.LOG.log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private void refreshViewData(boolean force) {
        if (controller != null) {
            // view might not exist yet and we won't unnecessarily create it
            controller.refreshViewData(force);
        }
    }

    /**
     * Returns available operations for this issue
     * @return
     */
    Map<String, TaskOperation> getAvailableOperations () {
        if (availableOperations == null) {
            HashMap<String, TaskOperation> operations = new HashMap<String, TaskOperation>(5);
            List<TaskAttribute> allOperations = data.getAttributeMapper().getAttributesByType(data, TaskAttribute.TYPE_OPERATION);
            for (TaskAttribute operation : allOperations) {
                // the test must be here, 'operation' (applying writable action) is also among allOperations
                if (operation.getId().startsWith(TaskAttribute.PREFIX_OPERATION)) {
                    operations.put(operation.getId().substring(TaskAttribute.PREFIX_OPERATION.length()), TaskOperation.createFrom(operation));
                }
            }
            availableOperations = operations;
        }

        return availableOperations;
    }

    boolean isResolveAvailable () {
        Map<String, TaskOperation> operations = getAvailableOperations();
        return operations.containsKey(BugzillaOperation.resolve.toString());
    }

    private Map<String, String> getSeenAttributes() {
        if(seenAtributes == null) {
            seenAtributes = repository.getIssueCache().getSeenAttributes(getID());
            if(seenAtributes == null) {
                seenAtributes = new HashMap<String, String>();
            }
        }
        return seenAtributes;
    }

    String getSeenValue(IssueField f) {
        Map<String, String> attr = getSeenAttributes();
        String seenValue = attr != null ? attr.get(f.getKey()) : null;
        if(seenValue == null) {
            seenValue = "";                                                     // NOI18N
        }
        return seenValue;
    }

    private String getMappedValue(TaskAttribute a, String key) {
        TaskAttribute ma = a.getMappedAttribute(key);
        if(ma != null) {
            return ma.getValue();
        }
        return null;
    }

    public boolean isFinished() {
        String value = getFieldValue(IssueField.STATUS);
        return "RESOLVED".equals(value);
    }

    public IssueStatusProvider.Status getStatus() {
        int status = getRepository().getIssueCache().getStatus(getID());
        switch(status) {
            case IssueCache.ISSUE_STATUS_NEW:
                return IssueStatusProvider.Status.NEW;
            case IssueCache.ISSUE_STATUS_MODIFIED:
                return IssueStatusProvider.Status.MODIFIED;
            case IssueCache.ISSUE_STATUS_SEEN:
                return IssueStatusProvider.Status.SEEN;
        }
        return null;
    }

    public void setUpToDate(boolean seen) {
        try {
            final IssueCache<BugzillaIssue, TaskData> issueCache = getRepository().getIssueCache();
            boolean wasSeen = issueCache.wasSeen(getID());
            if(seen != wasSeen) {
                issueCache.setSeen(getID(), seen);
                fireSeenChanged(wasSeen, seen);
            }
        } catch (IOException ex) {
            Bugzilla.LOG.log(Level.WARNING, null, ex);
        }
    }

    class Comment {
        private final Date when;
        private final String author;
        private final String authorName;
        private final Long number;
        private final String text;
        private final Double worked;

        public Comment(TaskAttribute a) {
            Date d = null;
            String s = "";
            try {
                s = getMappedValue(a, TaskAttribute.COMMENT_DATE);
                if(s != null && !s.trim().equals("")) {                         // NOI18N
                    d = CC_DATE_FORMAT.parse(s);
                }
            } catch (ParseException ex) {
                Bugzilla.LOG.log(Level.SEVERE, s, ex);
            }
            when = d;
            TaskAttribute authorAttr = a.getMappedAttribute(TaskAttribute.COMMENT_AUTHOR);
            if (authorAttr != null) {
                author = authorAttr.getValue();
                TaskAttribute nameAttr = authorAttr.getMappedAttribute(TaskAttribute.PERSON_NAME);
                authorName = nameAttr != null ? nameAttr.getValue() : null;
            } else {
                author = authorName = null;
            }
            String n = getMappedValue(a, TaskAttribute.COMMENT_NUMBER);
            number = n != null ? Long.parseLong(n) : null;
            text = getMappedValue(a, TaskAttribute.COMMENT_TEXT);
            String workedString = getMappedValue(a, BugzillaAttribute.WORK_TIME.getKey());
            
            double dbWorked = 0;
            if(workedString == null || workedString.isEmpty()) {
                dbWorked = 0.0;
            } else {
                try {
                    dbWorked = Double.parseDouble(workedString);
                } catch (NumberFormatException e) {
                    Bugzilla.LOG.log(Level.WARNING, "WORK_TIME time for comment " + number + " is " + workedString , e);
                    dbWorked = 0;
                }
            }
            worked = dbWorked;
        }

        public Long getNumber() {
            return number;
        }

        public String getText() {
            return text;
        }

        public Date getWhen() {
            return when;
        }

        public String getAuthor() {
            return author;
        }

        public String getAuthorName() {
            return authorName;
        }

        public Double getWorked() {
            return worked;
        }
    }

    class Attachment {
        private final String desc;
        private final String filename;
        private final String author;
        private final String authorName;
        private final Date date;
        private final String id;
        private String contentType;
        private String isDeprected;
        private String size;
        private String isPatch;
        private String url;


        public Attachment(TaskAttribute ta) {
            id = ta.getValue();
            Date d = null;
            String s = "";
            try {
                s = getMappedValue(ta, TaskAttribute.ATTACHMENT_DATE);
                if(s != null && !s.trim().equals("")) {                         // NOI18N
                    d = CC_DATE_FORMAT.parse(s);
                }
            } catch (ParseException ex) {
                Bugzilla.LOG.log(Level.SEVERE, s, ex);
            }
            date = d;
            filename = getMappedValue(ta, TaskAttribute.ATTACHMENT_FILENAME);
            desc = getMappedValue(ta, TaskAttribute.ATTACHMENT_DESCRIPTION);

            TaskAttribute authorAttr = ta.getMappedAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
            if(authorAttr != null) {
                author = authorAttr.getValue();
                TaskAttribute nameAttr = authorAttr.getMappedAttribute(TaskAttribute.PERSON_NAME);
                authorName = nameAttr != null ? nameAttr.getValue() : null;
            } else {
                authorAttr = data.getRoot().getMappedAttribute(IssueField.REPORTER.getKey()); 
                if(authorAttr != null) {
                    author = authorAttr.getValue();
                    TaskAttribute nameAttr = authorAttr.getMappedAttribute(TaskAttribute.PERSON_NAME);
                    authorName = nameAttr != null ? nameAttr.getValue() : null;
                } else {
                    author = authorName = null;
                }
            }
            contentType = getMappedValue(ta, TaskAttribute.ATTACHMENT_CONTENT_TYPE);
            isDeprected = getMappedValue(ta, TaskAttribute.ATTACHMENT_IS_DEPRECATED);
            isPatch = getMappedValue(ta, TaskAttribute.ATTACHMENT_IS_PATCH);
            size = getMappedValue(ta, TaskAttribute.ATTACHMENT_SIZE);
            url = getMappedValue(ta, TaskAttribute.ATTACHMENT_URL);
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getAuthor() {
            return author;
        }

        public Date getDate() {
            return date;
        }

        public String getDesc() {
            return desc;
        }

        public String getFilename() {
            return filename;
        }

        public String getContentType() {
            return contentType;
        }

        public String getId() {
            return id;
        }

        public String getIsDeprected() {
            return isDeprected;
        }

        public String getIsPatch() {
            return isPatch;
        }

        public String getSize() {
            return size;
        }

        public String getUrl() {
            return url;
        }

        public void getAttachementData(final OutputStream os) {
            assert !SwingUtilities.isEventDispatchThread() : "Accessing remote host. Do not call in awt"; // NOI18N            
            repository.getExecutor().execute(new GetAttachmentCommand(repository, id, os));
        }

        void open() {
            String progressFormat = NbBundle.getMessage(
                                        DefaultAttachmentAction.class,
                                        "Attachment.open.progress");    //NOI18N
            String progressMessage = MessageFormat.format(progressFormat, getFilename());
            final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
            handle.start();
            handle.switchToIndeterminate();
            parallelRP.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = saveToTempFile();
                        String contentType = getContentType();
                        if ("image/png".equals(contentType)             //NOI18N
                                || "image/gif".equals(contentType)      //NOI18N
                                || "image/jpeg".equals(contentType)) {  //NOI18N
                            HtmlBrowser.URLDisplayer.getDefault().showURL(file.toURI().toURL());
                        } else {
                            file = FileUtil.normalizeFile(file);
                            FileObject fob = FileUtil.toFileObject(file);
                            DataObject dob = DataObject.find(fob);
                            OpenCookie open = dob.getCookie(OpenCookie.class);
                            if (open != null) {
                                open.open();
                            } else {
                                // PENDING
                            }
                        }
                    } catch (DataObjectNotFoundException dnfex) {
                        Bugzilla.LOG.log(Level.INFO, dnfex.getMessage(), dnfex);
                    } catch (IOException ioex) {
                        Bugzilla.LOG.log(Level.INFO, ioex.getMessage(), ioex);
                    } finally {
                        handle.finish();
                    }
                }
            });
        }

        void saveToFile() {
            final File file = new FileChooserBuilder(AttachmentsPanel.class)
                    .setFilesOnly(true).showSaveDialog();
            if (file != null) {
                String progressFormat = NbBundle.getMessage(
                                            SaveAttachmentAction.class,
                                            "Attachment.saveToFile.progress"); //NOI18N
                String progressMessage = MessageFormat.format(progressFormat, getFilename());
                final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
                handle.start();
                handle.switchToIndeterminate();
                parallelRP.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            try {
                                getAttachementData(fos);
                            } finally {
                                fos.close();
                            }
                        } catch (IOException ioex) {
                            Bugzilla.LOG.log(Level.INFO, ioex.getMessage(), ioex);
                        } finally {
                            handle.finish();
                        }
                    }
                });
            }
        }

        void applyPatch() {
            final File context = BugtrackingUtil.selectPatchContext();
            if (context != null) {
                String progressFormat = NbBundle.getMessage(
                                            ApplyPatchAction.class,
                                            "Attachment.applyPatch.progress"); //NOI18N
                String progressMessage = MessageFormat.format(progressFormat, getFilename());
                final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
                handle.start();
                handle.switchToIndeterminate();
                parallelRP.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = saveToTempFile();
                            PatchUtils.applyPatch(file, context);
                        } catch (IOException ioex) {
                            Bugzilla.LOG.log(Level.INFO, ioex.getMessage(), ioex);
                        } finally {
                            handle.finish();
                        }
                    }
                });
            }
        }

        private File saveToTempFile() throws IOException {
            int index = filename.lastIndexOf('.'); // NOI18N
            String prefix = (index == -1) ? filename : filename.substring(0, index);
            String suffix = (index == -1) ? null : filename.substring(index);
            if (prefix.length()<3) {
                prefix = prefix + "tmp";                                //NOI18N
            }
            File file = File.createTempFile(prefix, suffix);
            FileOutputStream fos = new FileOutputStream(file);
            try {
                getAttachementData(fos);
            } finally {
                fos.close();
            }
            return file;
        }

        class DefaultAttachmentAction extends AbstractAction {

            public DefaultAttachmentAction() {
                putValue(NAME, NbBundle.getMessage(
                                   DefaultAttachmentAction.class,
                                   "Attachment.DefaultAction.name"));   //NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Attachment.this.open();
            }
        }

        class SaveAttachmentAction extends AbstractAction {

            public SaveAttachmentAction() {
                putValue(NAME, NbBundle.getMessage(
                                   SaveAttachmentAction.class,
                                   "Attachment.SaveAction.name"));      //NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Attachment.this.saveToFile();
            }
        }

        class ApplyPatchAction extends AbstractAction {

            public ApplyPatchAction() {
                putValue(NAME, NbBundle.getMessage(
                                   ApplyPatchAction.class,
                                   "Attachment.ApplyPatchAction.name"));//NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Attachment.this.applyPatch();
            }
        }

    }

}
