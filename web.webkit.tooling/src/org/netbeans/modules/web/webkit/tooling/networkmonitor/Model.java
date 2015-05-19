/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.webkit.tooling.networkmonitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.netbeans.api.project.Project;
import org.netbeans.modules.web.browser.api.BrowserFamilyId;
import org.netbeans.modules.web.webkit.debugging.api.console.ConsoleMessage;
import org.netbeans.modules.web.webkit.debugging.api.network.Network;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

class Model extends AbstractTableModel {

    private static final int MAX_NUMBER_OF_REQUESTS = 1000;

    private final List<ModelItem> visibleRequests = Collections.synchronizedList(new ArrayList<ModelItem>());
    private volatile boolean passive = true;
    private final Project project;
    private final BrowserFamilyId browserFamilyId;

    public Model(Lookup projectContext) {
        this.project = projectContext.lookup(Project.class);
        this.browserFamilyId = projectContext.lookup(BrowserFamilyId.class);
    }

    Project getProject() {
        for (Iterator<ModelItem> iterator = visibleRequests.iterator(); iterator.hasNext();) {
            return iterator.next().getProject();
        }
        return null;
    }

    void passivate() {
        passive = true;
    }

    void activate() {
        passive = false;
    }

    public void add(Network.Request r) {
        if (passive) {
            return;
        }
        r.addPropertyChangeListener(new PropertyChangeListenerImpl(r, browserFamilyId, project));
    }

    public void add(Network.WebSocketRequest r) {
        if (passive) {
            return;
        }
        addVisibleItem(new ModelItem(null, r, browserFamilyId, project));
    }

    void addVisibleItem(final ModelItem modelItem) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                visibleRequests.add(modelItem);
                assert modelItem.canBeShownToUser() : modelItem.toString();
                int index = visibleRequests.size() - 1;
                fireTableRowsInserted(index, index);
                cleanUp();
            }
        });
    }

    void reset() {
        assert SwingUtilities.isEventDispatchThread();
        visibleRequests.clear();
        fireTableDataChanged();
    }

    void console(ConsoleMessage message) {
        if (passive) {
            return;
        }
        // handle case of following message:
        //
        // event {"method":"Console.messageAdded","params":{"message":{"text":
        //   "XMLHttpRequest cannot load http:\/\/localhost:8080\/SampleDBrest
        //   \/resources\/aaa.manXXXufacturer\/. Origin http:\/\/localhost:8383
        //   is not allowed by Access-Control-Allow-Origin.","level":"error",
        //   "source":"javascript","line":0,"repeatCount":1,"type":"log","url"
        //   :"http:\/\/localhost:8383\/nb-rest-test\/knockout-approach\/index-ko.html"}}}

        // #247672
        List<ModelItem> visibleRequestsCopy = new ArrayList<>(visibleRequests);
        if (message.getText().contains("Access-Control-Allow-Origin") && !visibleRequestsCopy.isEmpty()) {
            ModelItem mi = visibleRequestsCopy.get(visibleRequestsCopy.size()-1);
            // XXX: perhaps I should match requests here with a timestamp???
            if (mi.getRequest() != null) {
                mi.setFailureCause(message.getText());
            }
        }
    }

    void cleanUp() {
        assert SwingUtilities.isEventDispatchThread();
        int removed = 0;
        while (visibleRequests.size() > MAX_NUMBER_OF_REQUESTS) {
            visibleRequests.remove(0);
            removed++;
        }
        if (removed > 0) {
            fireTableRowsDeleted(0, removed);
        }
    }

    /**
     * Returns model item at the specified position.
     * 
     * @param index position of the requested model item.
     * @return model item at the specified position or {@code null}
     * (when there is no such item).
     */
    ModelItem getItem(int index) {
        return (index == -1) ? null : visibleRequests.get(index);
    }

    @Override
    public int getRowCount() {
        return visibleRequests.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    @NbBundle.Messages({
        "RequestTable.ColumnName.URL=URL",
        "RequestTable.ColumnName.Type=Type",
    })
    public String getColumnName(int column) {
        String name;
        switch (column) {
            case 0: name = Bundle.RequestTable_ColumnName_URL(); break;
            case 1: name = Bundle.RequestTable_ColumnName_Type(); break;
            default: throw new IllegalArgumentException();
        }
        return name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ModelItem item = getItem(rowIndex);
        Object value;
        switch (columnIndex) {
            case 0: value = item.getURL(); break;
            case 1: value = item.getType(); break;
            default: throw new IllegalArgumentException();
        }
        return value;
    }

    //~ Inner classes

    private final class PropertyChangeListenerImpl implements PropertyChangeListener {

        private final Network.Request request;
        private final BrowserFamilyId browserFamilyId;
        private final Project project;


        public PropertyChangeListenerImpl(Network.Request request, BrowserFamilyId browserFamilyId, Project project) {
            this.request = request;
            this.browserFamilyId = browserFamilyId;
            this.project = project;
        }


        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() == request : evt.getSource() + " != " + request;
            if (Network.Request.PROP_RESPONSE.equals(evt.getPropertyName())) {
                request.removePropertyChangeListener(this);
                ModelItem modelItem = new ModelItem(request, null, browserFamilyId, project);
                if (modelItem.canBeShownToUser()) {
                    addVisibleItem(modelItem);
                }
            }
        }

    }

}