/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bpel.properties.editors;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.text.MessageFormat;
import java.util.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.bpel.model.api.Activity;
import org.netbeans.modules.bpel.model.api.BpelEntity;
import org.netbeans.modules.bpel.model.api.Invoke;
import org.netbeans.modules.bpel.model.api.Process;
import org.netbeans.modules.bpel.model.api.Receive;
import org.netbeans.modules.bpel.model.api.Reply;
import org.netbeans.modules.bpel.model.api.Scope;
import org.netbeans.modules.bpel.model.api.Sequence;
import org.netbeans.modules.bpel.model.impl.BpelBuilderImpl;
import org.netbeans.modules.bpel.model.impl.BpelModelImpl;
import org.netbeans.modules.bpel.nodes.BpelNode;
import org.netbeans.modules.print.api.PrintUtil;
import org.netbeans.modules.soa.mappercore.Mapper;
import org.netbeans.modules.soa.mappercore.model.Graph;
import org.netbeans.modules.soa.mappercore.model.GraphSubset;
import org.netbeans.modules.soa.mappercore.model.Link;
import org.netbeans.modules.soa.mappercore.model.MapperModel;
import org.netbeans.modules.soa.mappercore.model.SourcePin;
import org.netbeans.modules.soa.mappercore.model.TargetPin;
import org.netbeans.modules.soa.mappercore.model.TreeSourcePin;
import org.netbeans.modules.soa.mappercore.model.VertexItem;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

/**
 * @author Alex Petrov (27.12.2007)
 */
public class DefineCorrelationWizard implements WizardProperties {
    private static final long serialVersionUID = 1L;
    
    private static final Dimension LEFT_DIMENSION_VALUE = new Dimension(200, 450);
    private static final Dimension PANEL_DIMENSION_VALUE = new Dimension(600, 450);
    private static final String[] WIZARD_STEP_NAMES = new String[] {
        PrintUtil.i18n(DefineCorrelationWizard.class, "LBL_Wizard_Step_Select_Messaging_Activity"),
        PrintUtil.i18n(DefineCorrelationWizard.class, "LBL_Wizard_Step_Define_Correlation"),
        PrintUtil.i18n(DefineCorrelationWizard.class, "LBL_Wizard_Step_Correlation_Configuration")
    };
    private static final String ATTRIBUTE_NAME = "name"; //NOI18N

    private static Map<Class<? extends Activity>, ActivityChooser> mapActivityChoosers;

    private static Graph TMP_FAKE_GRAPH;
    
    private WizardDescriptor wizardDescriptor;
    private BpelEntity mainBpelEntity;
    private Panel[] wizardPanels;
    
    public DefineCorrelationWizard(BpelNode mainBpelNode) {
        Object mainBpelNodeRef = mainBpelNode.getReference();
        if (mainBpelNodeRef instanceof BpelEntity) {
            this.mainBpelEntity = (BpelEntity) mainBpelNodeRef;
        }
        mapActivityChoosers = createActivityChoosersMap();
        
        wizardPanels = getWizardPanelList().toArray(new Panel[] {});
        wizardDescriptor = new WizardDescriptor(wizardPanels);
        
        wizardDescriptor.putProperty(PROPERTY_AUTO_WIZARD_STYLE, true);
        wizardDescriptor.putProperty(PROPERTY_CONTENT_DISPLAYED, true);
        wizardDescriptor.putProperty(PROPERTY_CONTENT_NUMBERED, true);
        wizardDescriptor.putProperty(PROPERTY_HELP_DISPLAYED, false);
        wizardDescriptor.putProperty(PROPERTY_LEFT_DIMENSION, LEFT_DIMENSION_VALUE);
        wizardDescriptor.putProperty(PROPERTY_CONTENT_DATA, WIZARD_STEP_NAMES);
        
        wizardDescriptor.setTitleFormat(new MessageFormat(
            PrintUtil.i18n(DefineCorrelationWizard.class, 
            "LBL_DefineCorrelation_Wizard_Title_Format")));
        wizardDescriptor.setTitle(PrintUtil.i18n(DefineCorrelationWizard.class, 
            "LBL_DefineCorrelation_Wizard_Title"));
    }
    
    public void showWizardDialog() {
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setPreferredSize(new Dimension(
            LEFT_DIMENSION_VALUE.width + PANEL_DIMENSION_VALUE.width + 50, 
            PANEL_DIMENSION_VALUE.height));
        dialog.pack();
        wizardPanels[0].isValid();
        dialog.setVisible(true);
    }
    
    private List<Panel> getWizardPanelList() {
        List<Panel> panelList = new ArrayList<Panel>(WIZARD_STEP_NAMES.length);
        panelList.add(new WizardSelectMessagingActivityPanel());
        panelList.add(new WizardDefineCorrelationPanel());
        panelList.add(new WizardCorrelationConfigurationPanel());
        for (int i = 0; i < panelList.size(); ++i) {
            ((WizardAbstractPanel) panelList.get(i)).setPanelNameIndex(
                WIZARD_STEP_NAMES[i], i);
        }
        return panelList;
    }
    
    private Map<Class<? extends Activity>, ActivityChooser> createActivityChoosersMap() {
        Map<Class<? extends Activity>, ActivityChooser> mapActivityChoosers = 
            new HashMap<Class<? extends Activity>, ActivityChooser>(4);
        mapActivityChoosers.put(Receive.class, new ActivityChooserForReceive());
        mapActivityChoosers.put(Reply.class, new ActivityChooserForReply());
        mapActivityChoosers.put(Invoke.class, new ActivityChooserForInvoke());
        return mapActivityChoosers;
    }
    //========================================================================//
    private interface ActivityChooser {
        List<BpelEntity> getListActivitiesForCorrelation(BpelEntity mainBpelEntity);
    }
    
    private abstract class ActivityChooserImpl implements ActivityChooser {
        protected final 
            Map<Class<? extends Activity>, Set<Class<? extends Activity>>> mapActivityTypes = createActivityTypesMap();
        
        private Map<Class<? extends Activity>, Set<Class<? extends Activity>>> createActivityTypesMap() {
            Map<Class<? extends Activity>, Set<Class<? extends Activity>>> mapActivityTypes = 
                new HashMap<Class<? extends Activity>, Set<Class<? extends Activity>>>(4);
            mapActivityTypes.put(Receive.class, new HashSet(
                Arrays.asList(new Class[] {Reply.class})));
            mapActivityTypes.put(Reply.class, new HashSet(
                Arrays.asList(new Class[] {Receive.class})));
            mapActivityTypes.put(Invoke.class, new HashSet(
                Arrays.asList(new Class[] {Receive.class, Reply.class})));
            return mapActivityTypes;
        }

        protected BpelEntity getActualParentEntity(BpelEntity mainBpelEntity) {
            BpelEntity parentEntity = mainBpelEntity.getParent();
            while ((parentEntity != null) && 
                   (!(parentEntity instanceof Scope)) && 
                   (!(parentEntity instanceof Process))) {
                if (parentEntity == null) return null;
                parentEntity = parentEntity.getParent();
            }
            return parentEntity;    
        }
        
        public List<BpelEntity> getListActivitiesForCorrelation(BpelEntity mainBpelEntity) {
            List<BpelEntity> activityList = new ArrayList<BpelEntity>();
            BpelEntity parentEntity = getActualParentEntity(mainBpelEntity);
            if (parentEntity == null) return activityList;
            
            Set<Class<? extends Activity>> setActivityTypes = mapActivityTypes.get(
                mainBpelEntity.getElementType());
            List<Sequence> sequences = parentEntity.getChildren(Sequence.class);
            for (Sequence sequence : sequences) {
                List<Activity> activities = sequence.getChildren(Activity.class);
                for (Activity activity : activities) {
                    if ((mainBpelEntity.equals(activity)) || 
                        (setActivityTypes.contains(activity.getElementType()))) {
                        activityList.add((BpelEntity) activity);
                    }
                }
            }
            return activityList;
        }
    }
    
    private class ActivityChooserForReceive extends ActivityChooserImpl {
        @Override
        public List<BpelEntity> getListActivitiesForCorrelation(BpelEntity mainBpelEntity) {
            List<BpelEntity> activityList = super.getListActivitiesForCorrelation(mainBpelEntity);
            if (activityList.isEmpty()) return activityList;
            
            // keep only activities below mainBpelEntity (remove all activities 
            // above mainBpelEntity including mainBpelEntity itself)
            BpelEntity activity = null;
            do {
                activity = activityList.remove(0);
            } while (! (activity.equals(mainBpelEntity)));
            return  activityList;
        }
    }
    
    private class ActivityChooserForReply extends ActivityChooserImpl  {
        @Override
        public List<BpelEntity> getListActivitiesForCorrelation(BpelEntity mainBpelEntity) {
            List<BpelEntity> activityList = super.getListActivitiesForCorrelation(mainBpelEntity);
            if (activityList.isEmpty()) return activityList;

            // keep only activities above mainBpelEntity (remove all activities 
            // below mainBpelEntity including mainBpelEntity itself)
            BpelEntity activity = null;
            do {
                activity = activityList.remove(activityList.size() - 1);
            } while (! (activity.equals(mainBpelEntity)));
            return  activityList;
        }
    }
    
    private class ActivityChooserForInvoke extends ActivityChooserImpl  {
        @Override
        public List<BpelEntity> getListActivitiesForCorrelation(BpelEntity mainBpelEntity) {
            List<BpelEntity> activityList = super.getListActivitiesForCorrelation(mainBpelEntity);
            if (activityList.isEmpty()) return activityList;
            
            // keep all activities excepting mainBpelEntity itself
            activityList.remove(mainBpelEntity);
            return  activityList;
        }
    }
    //========================================================================//
    public abstract class WizardAbstractPanel implements WizardDescriptor.ValidatingPanel {
        protected JPanel wizardPanel = createWizardPanel();
        protected ChangeSupport changeSupport = new ChangeSupport(this);
        protected GridBagConstraints gbc = new GridBagConstraints();
        protected int insetX = 5, insetY = 5;
        
        protected JPanel createWizardPanel() {
            JPanel panel = new JPanel();
            panel.setPreferredSize(PANEL_DIMENSION_VALUE);
            return panel;
        }
        
        protected void setPanelNameIndex(String name, int index) {
            wizardPanel.setName(name);
            wizardPanel.putClientProperty(PROPERTY_CONTENT_SELECTED_INDEX, index);
        }
        
        public Component getComponent() {
            return wizardPanel;
        }
        
        public HelpCtx getHelp() {
            return null;
        }
        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }
        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }
        
        public void readSettings(Object settings) {}
        public void storeSettings(Object settings) {}

        public void validate() throws WizardValidationException {}

        public boolean isValid() {
            return true;
        }
        
        protected void initializeGridBagConstraints() {
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 1; gbc.gridheight = 1;
            gbc.ipadx = 0; gbc.ipady = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(insetY, insetX, insetY, insetX);
            gbc.weightx = 1.0; gbc.weighty = 1.0;
        }
    }
    //========================================================================//
    public class WizardSelectMessagingActivityPanel extends WizardAbstractPanel {
        private final Dimension COMBOBOX_DIMENSION = new Dimension(220, 20);
        private final int COMBOBOX_MAX_ROW_COUNT = 16;
        private final JComboBox activityComboBox = new JComboBox();
        private BpelEntity previousSelectedActivity, currentSelectedActivity;
            
        public WizardSelectMessagingActivityPanel() {
            super();
            wizardPanel.setLayout(new FlowLayout(FlowLayout.LEFT, insetX, insetY));
            wizardPanel.add(new JLabel(PrintUtil.i18n(
                WizardSelectMessagingActivityPanel.class, "LBL_Initiated_Messaging_Activities")));

            fillActivityComboBox();
            activityComboBox.setRenderer(new ComboBoxRenderer());
            activityComboBox.setMaximumRowCount(COMBOBOX_MAX_ROW_COUNT);
            activityComboBox.setEditable(false);
            activityComboBox.setMinimumSize(COMBOBOX_DIMENSION);
            activityComboBox.setPreferredSize(activityComboBox.getMinimumSize());
            wizardPanel.add(activityComboBox);
        }

        private void fillActivityComboBox() {
            ActivityChooser activityChooser = mapActivityChoosers.get(mainBpelEntity.getElementType());
            if (activityChooser == null) {
                String errMsg = "Activity Chooser isn't defined for Bpel Entity of type [" +
                    mainBpelEntity.getElementType().getName() + "]";
                System.err.println(errMsg);
                System.out.println(errMsg);
                return;
            }
            List<BpelEntity> activityEntityList = activityChooser.getListActivitiesForCorrelation(mainBpelEntity);
            if (activityEntityList != null) {
                ((DefaultComboBoxModel) activityComboBox.getModel()).removeAllElements();
                for (BpelEntity activityEntity : activityEntityList) {
                    activityComboBox.addItem(activityEntity);
                }
            }
        }
        
        @Override
        public boolean isValid() {
            boolean isOK = ((activityComboBox.getItemCount() > 0) &&
                            (activityComboBox.getSelectedItem() != null));
            
            wizardDescriptor.putProperty(PROPERTY_ERROR_MESSAGE, isOK ? null :
                PrintUtil.i18n(WizardSelectMessagingActivityPanel.class, "LBL_ErrMsg_No_Activity_For_Correlation"));                              
            return isOK;
        }

        @Override
        public void validate() throws WizardValidationException {
            previousSelectedActivity = currentSelectedActivity;
            currentSelectedActivity = (BpelEntity) activityComboBox.getSelectedItem();
            WizardDefineCorrelationPanel wizardDefineCorrelationPanel = 
                ((WizardDefineCorrelationPanel) wizardPanels[1]);
            if (previousSelectedActivity == null) { // this panel is shown for the 1st time
                wizardDefineCorrelationPanel.buildCorrelationMapper(mainBpelEntity, currentSelectedActivity);
            } else { // this panel is shown after clicking of the button "Back"
                if (! previousSelectedActivity.equals(currentSelectedActivity)) {
                    wizardDefineCorrelationPanel.buildCorrelationMapper(null, currentSelectedActivity);
                }
            }
        }
        //====================================================================//
        private class ComboBoxRenderer extends BasicComboBoxRenderer.UIResource {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if ((value != null) && (value instanceof BpelEntity) &&
                    (component != null) && (component instanceof JLabel)) {
                    String itemText = null;
                    try {
                        itemText = ((BpelEntity) value).getAttribute(
                            new AbstractDocumentComponent.PrefixAttribute(ATTRIBUTE_NAME));
                    } catch (Exception e) {
                        itemText = value.toString();
                    }
                    ((JLabel) component).setText(itemText);
                }
                return component;
            }
        }
    }
    //========================================================================//
    public class WizardDefineCorrelationPanel extends WizardAbstractPanel {
        private Mapper correlationMapper;
        
        public WizardDefineCorrelationPanel() {
            super();
        }
        
        public void buildCorrelationMapper(BpelEntity leftBpelEntity, BpelEntity rightBpelEntity) {
            CorrelationMapperTreeModel leftTreeModel = null, rightTreeModel = null;
            if (leftBpelEntity != null) {
                leftTreeModel = new CorrelationMapperTreeModel(leftBpelEntity,
                    PrintUtil.i18n(WizardDefineCorrelationPanel.class, "LBL_Left_Mapper_Top_Tree_Node_Name_Pattern"));
            }
            if (rightBpelEntity != null) {
                rightTreeModel = new CorrelationMapperTreeModel(rightBpelEntity,
                    PrintUtil.i18n(WizardDefineCorrelationPanel.class, "LBL_Right_Mapper_Top_Tree_Node_Name_Pattern"));
            }
            
            MapperModel mapperModel = new CorrelationMapperModel(leftTreeModel, rightTreeModel);
            if (correlationMapper == null) {
                correlationMapper = new Mapper(mapperModel);
                correlationMapper.setPreferredSize(PANEL_DIMENSION_VALUE);
                wizardPanel.add(correlationMapper);
                wizardPanel.revalidate();
            } else {
                correlationMapper.setModel(mapperModel);
            }
        }

        @Override
        public boolean isValid() {
            boolean isOK = false;
            // allows moving to the next panel if the right tree 
            // in mapper contains at least 1 link
            Map<TreePath, Graph> mapTreePathGraphs = ((CorrelationMapperModel) correlationMapper.getModel()).getMapTreePathGraphs();
            for (Graph graph : mapTreePathGraphs.values()) {
                isOK = graph.hasIngoingLinks();
                if (isOK) break;
            }
            wizardDescriptor.putProperty(PROPERTY_ERROR_MESSAGE, isOK ? null :
                PrintUtil.i18n(WizardDefineCorrelationPanel.class, "LBL_ErrMsg_No_Links_For_Correlation"));                              
            
            return isOK;
        }

        @Override
        public void validate() throws WizardValidationException {
        }
        //====================================================================//
        private class CorrelationMapperTreeModel extends DefaultTreeModel {
            public CorrelationMapperTreeModel(BpelEntity topBpelEntity) {
                this(topBpelEntity, null);
            }
            public CorrelationMapperTreeModel(BpelEntity topBpelEntity, String nodeNamePattern) {
                super(new CorrelationMapperTreeNode(new BpelBuilderImpl(
                    (BpelModelImpl) topBpelEntity.getBpelModel()).createEmpty()));
                ((CorrelationMapperTreeNode) getRoot()).add(
                    new CorrelationMapperTreeNode(topBpelEntity, nodeNamePattern));
            }
            
            public BpelEntity getTopBpelEntity() {
                CorrelationMapperTreeNode topTreeNode = (CorrelationMapperTreeNode) ((CorrelationMapperTreeNode) getRoot()).getChildAt(0);
                return (BpelEntity) topTreeNode.getUserObject();
            }
            
            public void fireTreeChanged(Object source, TreePath treePath) {
                Object[] listeners = listenerList.getListenerList(); // guaranteed to return a non-null array
                TreeModelEvent treeModelEvent = null;
                // go through the listener list from the last to the first, 
                // notifying those ones which are interested in this event
                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == TreeModelListener.class) {
                        if (treeModelEvent == null) { // lazily create the event:
                            TreePath parentTreePath = treePath.getParentPath();
                            if (parentTreePath != null) {
                                Object treeNode = treePath.getLastPathComponent();
                                // reload cached user object
                                ((CorrelationMapperTreeNode) treeNode).setUserObject(
                                    ((CorrelationMapperTreeNode) treeNode).getUserObject());

                                int childIndex = getIndexOfChild(
                                    parentTreePath.getLastPathComponent(), treeNode);

                                treeModelEvent = new TreeModelEvent(source, parentTreePath, 
                                    new int[] {childIndex}, new Object[] {treeNode});
                            } else {
                                treeModelEvent = new TreeModelEvent(source, treePath, 
                                    new int[] {}, new Object[] {});
                            }
                        }
                        ((TreeModelListener) listeners[i+1]).treeNodesChanged(treeModelEvent);
                    }          
                }
            }
        }
        //====================================================================//
        private class CorrelationMapperTreeNode extends DefaultMutableTreeNode {
            private String nodeNamePattern;
            
            public CorrelationMapperTreeNode(BpelEntity bpelEntity) {
                this(bpelEntity, null);
            }
            public CorrelationMapperTreeNode(BpelEntity bpelEntity, String nodeNamePattern) {
                super(bpelEntity);
                this.nodeNamePattern = nodeNamePattern;
            }

            @Override
            public String toString() {
                String bpelEntityName = null;
                BpelEntity bpelEntity = (BpelEntity) getUserObject();
                try {
                    bpelEntityName = bpelEntity.getAttribute(
                        new AbstractDocumentComponent.PrefixAttribute(ATTRIBUTE_NAME));
                } catch (Exception e) {
                    bpelEntityName = bpelEntity.toString();
                }
                String nodeName = (nodeNamePattern == null ? bpelEntityName : 
                    MessageFormat.format(nodeNamePattern, new Object[] {bpelEntityName}));
                return nodeName;
            }
        }
        //====================================================================//
        private class CorrelationMapperModel implements MapperModel {
            private TreeModel leftTreeModel, rightTreeModel;
            private Map<TreePath, Graph> mapTreePathGraphs = new HashMap<TreePath, Graph>();

            public CorrelationMapperModel(TreeModel leftTreeModel, TreeModel rightTreeModel) {
                this.leftTreeModel = leftTreeModel;
                this.rightTreeModel = rightTreeModel;
                TMP_FAKE_GRAPH = new Graph(this);
            }

            public Map<TreePath, Graph> getMapTreePathGraphs() {
                return mapTreePathGraphs;
            }

            public boolean canConnect(TreePath treePath, SourcePin source, TargetPin target) {
                boolean result = false;
                CorrelationMapperTreeNode treeNode = null;
                if ((source != null) && (source instanceof TreeSourcePin)) {
                    TreePath sourceTreePath = ((TreeSourcePin) source).getTreePath();
                    treeNode = (CorrelationMapperTreeNode) sourceTreePath.getLastPathComponent();
                    result = treeNode.isLeaf();
                    result &= ! treeNode.getUserObject().equals(
                        ((CorrelationMapperTreeModel) leftTreeModel).getTopBpelEntity());
                }
                treeNode = (CorrelationMapperTreeNode) treePath.getLastPathComponent();
                result &= treeNode.isLeaf();
                result &= ! treeNode.getUserObject().equals(
                    ((CorrelationMapperTreeModel) rightTreeModel).getTopBpelEntity());

                if (target instanceof Graph) {
                    Graph targetGraph = (Graph) target;
                    if (targetGraph.hasIngoingLinks() || targetGraph.hasOutgoingLinks()) {
                        result = false; // the target tree node already has a connected link
                    }
                }
                return result;
            }

            public void connect(TreePath treePath, SourcePin source, TargetPin target) {
                Graph graph = getGraph(treePath);
                if ((graph == null) || (graph == TMP_FAKE_GRAPH)) {
                    graph = createNewGraph(treePath);
                }
                Link newLink = new Link(source, target);
                graph.addLink(newLink);
                ((CorrelationMapperTreeModel) rightTreeModel).fireTreeChanged(this, treePath);
            }

            protected Graph createNewGraph(TreePath treePath) {
                Graph treePathGraph = new Graph(this);
                mapTreePathGraphs.put(treePath, treePathGraph);
                ((CorrelationMapperTreeModel) rightTreeModel).fireTreeChanged(this, treePath);
                return treePathGraph;
            }
            
            public Graph getGraph(TreePath treePath) {
                Graph treePathGraph = mapTreePathGraphs.get(treePath);
                return (treePathGraph != null ? treePathGraph : TMP_FAKE_GRAPH);
            }

            public GraphSubset getGraphSubset(Transferable transferable) {
                return null;
            }

            public TreeModel getLeftTreeModel() {
                return leftTreeModel;
            }

            public TreeSourcePin getTreeSourcePin(TreePath treePath) {
                return new TreeSourcePin(treePath);
            }

            public boolean searchGraphsInside(TreePath treePath) {
                Object treeNode = treePath.getLastPathComponent();
                for (TreePath graphTreePath : mapTreePathGraphs.keySet()) {
                    while (true) {
                        graphTreePath = graphTreePath.getParentPath();
                        if (graphTreePath == null) {
                            break;
                        }
                        Object parentTreeNode = graphTreePath.getLastPathComponent();
                        if (parentTreeNode == treeNode) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public void valueChanged(TreePath treePath, VertexItem vertexItem, Object newValue) {}

            public boolean canCopy(TreePath treePath, GraphSubset graphSubset) {
                return false;
            }

            public boolean canMove(TreePath treePath, GraphSubset graphSubset) {
                return false;
            }

            public void copy(TreePath treePath, GraphSubset graphGroup, int x, int y) {}

            public void move(TreePath treePath, GraphSubset graphGroup, int x, int y) {}
            
            public void addTreeModelListener(TreeModelListener l) {
                rightTreeModel.addTreeModelListener(l);
            }

            public Object getChild(Object parent, int index) {
                return rightTreeModel.getChild(parent, index);
            }

            public int getChildCount(Object parent) {
                return rightTreeModel.getChildCount(parent);
            }

            public int getIndexOfChild(Object parent, Object child) {
                return rightTreeModel.getIndexOfChild(parent, child);
            }

            public Object getRoot() {
                return rightTreeModel.getRoot();
            }

            public boolean isLeaf(Object node) {
                return rightTreeModel.isLeaf(node);
            }

            public void removeTreeModelListener(TreeModelListener l) {
                rightTreeModel.removeTreeModelListener(l);
            }

            public void valueForPathChanged(TreePath path, Object newValue) {
                rightTreeModel.valueForPathChanged(path, newValue);
            }
        }
    }
    //========================================================================//
    public class WizardCorrelationConfigurationPanel extends WizardAbstractPanel {
        public WizardCorrelationConfigurationPanel() {
            super();
        }
    }
}

interface WizardProperties {
    String
        PROPERTY_AUTO_WIZARD_STYLE = "WizardPanel_autoWizardStyle", // NOI18N
        PROPERTY_CONTENT_DISPLAYED = "WizardPanel_contentDisplayed", // NOI18N
        PROPERTY_CONTENT_NUMBERED = "WizardPanel_contentNumbered", // NOI18N
        PROPERTY_LEFT_DIMENSION = "WizardPanel_leftDimension", // NOI18N

        PROPERTY_CONTENT_SELECTED_INDEX = "WizardPanel_contentSelectedIndex", // NOI18N
        PROPERTY_CONTENT_DATA = "WizardPanel_contentData", // NOI18N
        PROPERTY_ERROR_MESSAGE = "WizardPanel_errorMessage", // NOI18N
        PROPERTY_CONTENT_BACK_COLOR = "WizardPanel_contentBackColor", // NOI18N
        PROPERTY_CONTENT_FOREGROUND_COLOR = "WizardPanel_contentForegroundColor", // NOI18N
        PROPERTY_IMAGE = "WizardPanel_image", // NOI18N
        PROPERTY_IMAGE_ALIGNMENT = "WizardPanel_imageAlignment", // NOI18N

        PROPERTY_HELP_DISPLAYED = "WizardPanel_helpDisplayed", // NOI18N
        PROPERTY_HELP_URL = "WizardPanel_helpURL"; // NOI18N
}