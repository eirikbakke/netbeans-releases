/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.xml.multiview.ui.SectionNodeInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionNodeView;

/**
 * @author pfiala
 */
class CmpRelationShipsNode extends EjbSectionNode {

    CmpRelationShipsNode(SectionNodeView sectionNodeView, EjbJar ejbJar) {
        super(sectionNodeView, true, ejbJar, Utils.getBundleMessage("LBL_CmpRelationships"), Utils.ICON_BASE_MISC_NODE);
        setExpanded(true);
        helpProvider = true;
    }

    protected SectionNodeInnerPanel createNodeInnerPanel() {
        final EjbJarMultiViewDataObject dataObject = (EjbJarMultiViewDataObject) getSectionNodeView().getDataObject();
        final CmpRelationshipsTableModel model = new CmpRelationshipsTableModel(dataObject);
        final InnerTablePanel innerTablePanel = new InnerTablePanel(getSectionNodeView(), model) {
            {
                resolveEnableAddButton();
            }
            public void dataModelPropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {
                if (source == key) {
                    resolveEnableAddButton();
                    scheduleRefreshView();
                }
            }

            protected void editCell(int row, int column) {
                model.editRow(row);
            }
            private void resolveEnableAddButton() {
                getAddButton().setEnabled(dataObject.getEjbJar().getEnterpriseBeans().getEntity().length > 0);
            }
        };
        return innerTablePanel;
    }
}
