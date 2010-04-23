/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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

package org.netbeans.modules.bugzilla.commands;

import java.io.IOException;
import java.net.MalformedURLException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.netbeans.modules.bugzilla.Bugzilla;

/**
 *
 * @author Tomas Stupka
 */
public class GetTaskDataCommand extends BugzillaCommand {

    private final String id;
    private final TaskRepository taskRepository;
    private TaskData taskData;

    public GetTaskDataCommand(String id, TaskRepository taskRepository) {
        this.id = id;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() throws CoreException, IOException, MalformedURLException {
        taskData = Bugzilla.getInstance().getRepositoryConnector().getTaskData(taskRepository, id, new NullProgressMonitor());
    }

    public TaskData getTaskData() {
        return taskData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GetTaskDataCommand [repository=");                       // NOI18N
        sb.append(taskRepository.getUrl());
        sb.append(",id=");                                                  // NOI18N
        sb.append(id);
        sb.append("]");                                                     // NOI18N
        return  sb.toString();
        
    }

}
