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

package org.netbeans.spi.tasklist;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.tasklist.trampoline.TaskGroupFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.tasklist.trampoline.Accessor;
import org.netbeans.modules.tasklist.trampoline.TaskGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 * A class holding the description of a single Task that will appear in TaskList's window.
 * 
 * @author S. Aubrecht
 */
public final class Task {
    
    private final URL url;
    private final FileObject file;
    private final TaskGroup group;
    private final String description;
    private final int line;
    private final ActionListener defaultAction;
    private final Action[] actions;
    
    static {
        Accessor.DEFAULT = new AccessorImpl();
    }
    

    /**
     * Create a new Task
     *
     * @param resource Resource which the Task applies to, cannot be null.
     * @param groupName Name of the group this task belongs to ({@code nb-tasklist-error}, {@code nb-tasklist-warning}, {@code nb-tasklist-todo}, etc).
     * @param description A brief summary of the task (one line if possible), cannot be null.
     *
     * @return New task.
     * @since 1.6
     */
    public static Task create( URL resource, String groupName, String description ) {
        return new Task( null, resource, getTaskGroup( groupName ), description, -1, null, null );
    }

    /**
     * Create a new Task
     * <p>Since version 1.4 the Task List implementation uses Indexing API to persist
     * tasks created by FileTaskScanners. If a file hasn't changed since the last scan
     * then the tasks associated with that file are loaded from cache to improve
     * Task List performance. Therefore task's ActionListener and popup Actions
     * aren't available when the task is restored from cache. Task providers must
     * switch to PushTaskScanner if ActionListener and popup actions are required
     * to be available at all times.</p>
     *
     * @param resource Resource which the Task applies to, cannot be null.
     * @param groupName Name of the group this task belongs to ({@code nb-tasklist-error}, {@code nb-tasklist-warning}, {@code nb-tasklist-todo}, etc).
     * @param description A brief summary of the task (one line if possible), cannot be null.
     * @param defaultAction Task's default action, e.g. double-click or Enter key in the Task List window.
     * @param popupActions Actions to show in task's popup menu.
     *
     * @return New task.
     * @since 1.6
     */
    public static Task create( URL resource, String groupName, String description, ActionListener defaultAction, Action[] popupActions ) {
        return new Task( null, resource, getTaskGroup( groupName ), description, -1, defaultAction, popupActions );
    }

    /**
     * Create a new Task
     * 
     * @param resource File or folder which the Task applies to, cannot be null.
     * @param groupName Name of the group this task belongs to ({@code nb-tasklist-error}, {@code nb-tasklist-warning}, {@code nb-tasklist-todo}, etc).
     * @param description A brief summary of the task (one line if possible), cannot be null.
     * @param line Line number in a text file, use negative value if line number is not applicable.
     * 
     * @return New task.
     */
    public static Task create( FileObject resource, String groupName, String description, int line ) {
        assert null != resource;
        return new Task(resource, null, getTaskGroup(groupName), description, line, null, null);
    }
    
    /**
     * <p>Create a new Task</p>
     * <p>Since version 1.4 the Task List implementation uses Indexing API to persist
     * tasks created by FileTaskScanners. If a file hasn't changed since the last scan
     * then the tasks associated with that file are loaded from cache to improve
     * Task List performance. Therefore task's ActionListener isn't available when
     * the task is restored from cache. Task providers must switch to PushTaskScanner
     * if ActionListener is required to be available at all times.</p>
     * 
     * @param resource File or folder which the Task applies to, cannot be null.
     * @param groupName Name of the group this task belongs to ({@code nb-tasklist-error}, {@code nb-tasklist-warning}, {@code nb-tasklist-todo}, etc).
     * @param description A brief summary of the task (one line if possible), cannot be null.
     * @param al Task's default action, e.g. double-click or Enter key in the Task List window.
     * 
     * @return New task.
     */
    public static Task create( FileObject resource, String groupName, String description, ActionListener al ) {
        assert null != resource;
        return new Task(resource, null, getTaskGroup(groupName), description, -1, al, null);
    }
    
    /** Creates a new instance of Task */
    private Task( FileObject file, URL url, TaskGroup group, String description, int line, ActionListener defaultAction, Action[] actions ) {
        assert null != group;
        assert null != description;
        assert null == file || null == url;
        
        this.file = file;
        this.url = url;
        this.group = group;
        this.description = description;
        this.line = line;
        this.defaultAction = defaultAction;
        this.actions = actions;
    }

    /**
     * Resource this taks applies to.
     * @return Resource this taks applies to.
     */
    URL getURL() {
        return url;
    }

    /**
     * Resource this taks applies to.
     * @return Resource this taks applies to.
     */
    FileObject getFile() {
        return file;
    }
    
    /**
     * The group this task belongs to (error, warning, todo, etc)
     * @return The group this task belongs to (error, warning, todo, etc)
     */
    TaskGroup getGroup() {
        return group;
    }
    
    /**
     * Task description.
     * @return Task description.
     */
    String getDescription() {
        return description;
    }
    
    /**
     * One-based line number in a text file this task applies to, -1 if the line number is not applicable. 
     * @return One-based line number in a text file this task applies to, -1 if the line number is not applicable. 
     */
    int getLine() {
        return line;
    }
    
    /**
     * Action to be invoked when user double-clicks the task in the Task List window.
     * @return Task's default action or null if not available.
     */
    ActionListener getDefaultAction() {
        return defaultAction;
    }

    Action[] getActions() {
        return actions;
    }
    
    /**
     * Create a new TaskGroup, called from XML layer.
     */
    static TaskGroup createGroup( Map<String,String> attrs ) {
        return TaskGroupFactory.create( attrs );
    }
    
    private static Set<String> unknownTaskGroups;
    
    private static TaskGroup getTaskGroup( String groupName ) {
        TaskGroup group = TaskGroupFactory.getDefault().getGroup( groupName );
        if( null == group ) {
            if( null == unknownTaskGroups || !unknownTaskGroups.contains( groupName ) ) {
                //show only one warning that the group name is not supported
                Logger.getLogger( Task.class.getName() ).log( Level.INFO, 
                        NbBundle.getMessage( Task.class, "Err_UnknownGroupName" ), groupName ); //NOI18N
                if( null == unknownTaskGroups )
                    unknownTaskGroups = new HashSet<String>(10);
                unknownTaskGroups.add( groupName );
            }
            
            group = TaskGroupFactory.getDefault().getDefaultGroup();
        }
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        final Task test = (Task) o;

        if (this.line != test.line)
            return false;
        if (this.description != test.description && this.description != null &&
            !this.description.equals(test.description))
            return false;
        if (this.group != test.group && this.group != null &&
            !this.group.equals(test.group))
            return false;
        if (this.url != test.url && this.url != null &&
            !this.url.equals(test.url))
            return false;
        if (this.file != test.file && this.file != null &&
            !this.file.equals(test.file))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 17 * hash + this.line;
        hash = 17 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 17 * hash + (this.group != null ? this.group.hashCode() : 0);
        hash = 17 * hash + (this.file != null ? this.file.hashCode() : 0);
        hash = 17 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[" ); 
        buffer.append( null == url ? getFile() : getURL() );
        buffer.append( ", " ); 
        buffer.append( getLine() );
        buffer.append( ", " ); 
        buffer.append( getDescription() );
        buffer.append( ", " ); 
        buffer.append( getGroup() );
        buffer.append( "]" ); 
        return buffer.toString();
    }
}
