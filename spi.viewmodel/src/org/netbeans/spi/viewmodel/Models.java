/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.viewmodel;

import com.sun.corba.se.internal.util.IdentityHashtable;
import java.awt.event.ActionEvent;
import java.lang.StringBuffer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import org.netbeans.modules.viewmodel.CompoundModel;
import org.netbeans.modules.viewmodel.TreeModelNode;
import org.netbeans.modules.viewmodel.TreeTable;

import org.netbeans.spi.viewmodel.ColumnModel;
import org.netbeans.spi.viewmodel.ComputingException;
import org.netbeans.spi.viewmodel.NoInformationException;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.NodeModel;
import org.netbeans.spi.viewmodel.NodeModelFilter;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TableModelFilter;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.TreeModelFilter;
import org.netbeans.spi.viewmodel.TreeModelListener;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.nodes.Node;

import org.openide.windows.TopComponent;


/**
 * Contains various utility methods for various models.
 *
 * @author   Jan Jancura
 */
public final class Models {

    public static final TreeModel EMPTY_TREE_MODEL = new EmptyTreeModel ();
    
    public static int MULTISELECTION_TYPE_EXACTLY_ONE = 1;
    public static int MULTISELECTION_TYPE_ALL = 2;
    public static int MULTISELECTION_TYPE_ANY = 3;
    
    private static boolean verbose = 
        System.getProperty ("netbeans.debugger.models") != null;
    
    
    /**
     * Creates a new instances of TreeTableView
     * for given {@link org.netbeans.spi.viewmodel.TreeModel},
     * {@link org.netbeans.spi.viewmodel.TreeModel},
     * {@link org.netbeans.spi.viewmodel.NodeModel},
     * {@link org.netbeans.spi.viewmodel.NodeActionsProvider} and
     * list of {@link org.netbeans.spi.viewmodel.ColumnModel}s.
     *
     * @param treeModel a tree model instance
     * @param nodeModel a node model instance
     * @param tableModel a table model instance
     * @param nodeActionsProvider a node actions provider instance
     * @param columnModels a list of ColumnModel instances
     *
     * @return new instance of complete model
     */
    public static JComponent createView (
        TreeModel treeModel, 
        NodeModel nodeModel, 
        TableModel tableModel,
        NodeActionsProvider nodeActionsProvider,
        List columnModels
    ) {
        CompoundModel cm = new CompoundModel (
            treeModel,
            nodeModel,
            nodeActionsProvider,
            columnModels,
            tableModel
        );
        TreeTable tt = new TreeTable ();
        tt.setModel (cm);
        return tt;
    }
    
    /**
     * Set given models to given view instance.
     *
     * @param view a view instance
     * @param treeModel a tree model instance
     * @param nodeModel a node model instance
     * @param tableModel a table model instance
     * @param nodeActionsProvider a node actions provider instance
     * @param columnModels a list of ColumnModel instances
     */
    public static void setModelsToView (
        JComponent view,
        TreeModel treeModel, 
        NodeModel nodeModel, 
        TableModel tableModel,
        NodeActionsProvider nodeActionsProvider,
        List columnModels
    ) {
        if (verbose) {
            System.out.println ("  TreeModel:");
            if (treeModel instanceof CompoundTreeModel)
                System.out.println (((CompoundTreeModel) treeModel).toString ("    "));
            else
                System.out.println ("    " + treeModel);
            
            System.out.println ("  NodeModel:");
            if (nodeModel instanceof CompoundNodeModel)
                System.out.println (((CompoundNodeModel) nodeModel).toString ("    "));
            else
            if (nodeModel instanceof DelegatingNodeModel)
                System.out.println (((DelegatingNodeModel) nodeModel).toString ("    "));
            else
                System.out.println ("    " + nodeModel);
            
            System.out.println ("  NodeActionsProvider:");
            if (nodeActionsProvider instanceof CompoundNodeActionsProvider)
                System.out.println (((CompoundNodeActionsProvider) nodeActionsProvider).toString ("    "));
            else
            if (nodeActionsProvider instanceof DelegatingNodeActionsProvider)
                System.out.println (((DelegatingNodeActionsProvider) nodeActionsProvider).toString ("    "));
            else
                System.out.println ("    " + nodeActionsProvider);

            System.out.println ("  ColumnModels:");
            int i, k = columnModels.size ();
            for (i = 0; i < k; i++)
                System.out.println ("    " + columnModels.get (i));
            
            System.out.println ("  TableModel:");
            if (tableModel instanceof CompoundTableModel)
                System.out.println (((CompoundTableModel) tableModel).toString ("    "));
            else
            if (tableModel instanceof DelegatingTableModel)
                System.out.println (((DelegatingTableModel) tableModel).toString ("    "));
            else
                System.out.println ("    " + tableModel);
        }
        CompoundModel cm = new CompoundModel (
            treeModel,
            nodeModel,
            nodeActionsProvider,
            columnModels,
            tableModel
        );
        ((TreeTable) view).setModel (cm);
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.TreeModel} for given TreeModel and
     * {@link org.netbeans.spi.viewmodel.TreeModelFilter}.
     * 
     * @param originalTreeModel a original tree model
     * @param treeModelFilter a list of tree model filters
     *
     * @returns compund tree model
     */
    public static TreeModel createCompoundTreeModel (
        TreeModel originalTreeModel,
        List treeModelFilters
    ) {
        TreeModel tm = originalTreeModel;
        int i, k = treeModelFilters.size ();
        for (i = 0; i < k; i++)
            tm = new CompoundTreeModel (
                tm,
                (TreeModelFilter) treeModelFilters.get (i)
            );
        return tm;
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.NodeModel} for given NodeModel and
     * {@link org.netbeans.spi.viewmodel.NodeModelFilter}.
     * 
     * @param originalNodeModel a original node model
     * @param nodeModelFilters a list of node model filters
     *
     * @returns compund tree model
     */
    public static NodeModel createCompoundNodeModel (
        NodeModel originalNodeModel,
        List treeNodeModelFilters
    ) {
        NodeModel nm = originalNodeModel;
        int i, k = treeNodeModelFilters.size ();
        for (i = 0; i < k; i++)
            nm = new CompoundNodeModel (
                nm,
                (NodeModelFilter) treeNodeModelFilters.get (i)
            );
        return nm;
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.TableModel} for given TableModel and
     * {@link org.netbeans.spi.viewmodel.TableModelFilter}.
     * 
     * @param originalTableModel a original table model
     * @param tableModelFilters a list of table model filters
     *
     * @returns compund table model
     */
    public static TableModel createCompoundTableModel (
        TableModel originalTableModel,
        List tableModelFilters
    ) {
        TableModel tm = originalTableModel;
        int i, k = tableModelFilters.size ();
        for (i = 0; i < k; i++)
            tm = new CompoundTableModel (
                tm,
                (TableModelFilter) tableModelFilters.get (i)
            );
        return tm;
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.NodeActionsProvider} for given NodeActionsProvider and
     * {@link org.netbeans.spi.viewmodel.NodeActionsProviderFilter}.
     * 
     * @param originalNodeActionsProvider a original node actions provider
     * @param nodeActionsProviderFilters a list of node actions provider filters
     *
     * @returns compund node actions provider
     */
    public static NodeActionsProvider createCompoundNodeActionsProvider (
        NodeActionsProvider originalNodeActionsProvider,
        List nodeActionsProviderFilters
    ) {
        NodeActionsProvider nap = originalNodeActionsProvider;
        int i, k = nodeActionsProviderFilters.size ();
        for (i = 0; i < k; i++)
            nap = new CompoundNodeActionsProvider (
                nap,
                (NodeActionsProviderFilter) nodeActionsProviderFilters.get (i)
            );
        return nap;
    }
    
//    /**
//     * Creates one {@link org.netbeans.spi.viewmodel.TableModel}
//     * from given array of TableModels. TableModel asks all underlaying 
//     * models for each concrete parameter, and returns first returned value.
//     *
//     * @param originalTableModels a array of original table models
//     */
//    public static TableModel createCompoundTableModel (
//        TableModel[] originalTableModels
//    ) {
//        return new DelegatingTableModel (
//            originalTableModels
//        );
//    }
    
    /**
     * Creates one {@link org.netbeans.spi.viewmodel.TableModel}
     * from given list of TableModels. TableModel asks all underlaying 
     * models for each concrete parameter, and returns first returned value.
     *
     * @param originalTableModels a list of original table models
     */
    public static TableModel createCompoundTableModel (
        List originalTableModels
    ) {
        return new DelegatingTableModel (
            originalTableModels
        );
    }
    
//    /**
//     * Creates one {@link org.netbeans.spi.viewmodel.NodeModel}
//     * from given array of NodeModels. NodeModel asks all underlaying 
//     * models for each concrete parameter, and returns first returned value.
//     *
//     * @param originalNodeModels a array of original node models
//     */
//    public static NodeModel createCompoundNodeModel (
//        NodeModel[] originalNodeModels
//    ) {
//        return new DelegatingNodeModel (
//            originalNodeModels
//        );
//    }
    
    /**
     * Creates one {@link org.netbeans.spi.viewmodel.NodeModel}
     * from given list of NodeModels. NodeModel asks all underlaying 
     * models for each concrete parameter, and returns first returned value.
     *
     * @param originalNodeModels a list of original node models
     */
    public static NodeModel createCompoundNodeModel (
        List originalNodeModels
    ) {
        return new DelegatingNodeModel (
            originalNodeModels
        );
    }
    
//    /**
//     * Creates one {@link org.netbeans.spi.viewmodel.NodeActionsProvider}
//     * from given array of NodeActionsProviders. NodeActionsProvider asks all underlaying 
//     * models for each concrete parameter, and returns first returned value.
//     *
//     * @param originalNodeActionsProviders a array of original node action providers
//     */
//    public static NodeActionsProvider createCompoundNodeActionsProvider (
//        NodeActionsProvider[] originalNodeActionsProviders
//    ) {
//        return new DelegatingNodeActionsProvider (
//            originalNodeActionsProviders
//        );
//    }
    
    /**
     * Creates one {@link org.netbeans.spi.viewmodel.NodeActionsProvider}
     * from given list of NodeActionsProviders. NodeActionsProvider asks all underlaying 
     * models for each concrete parameter, and returns first returned value.
     *
     * @param originalNodeActionsProviders a list of original node action providers
     */
    public static NodeActionsProvider createCompoundNodeActionsProvider (
        List originalNodeActionsProviders
    ) {
        return new DelegatingNodeActionsProvider (
            originalNodeActionsProviders
        );
    }
    
    /**
     * Returns {@link javax.swing.Action} for given parameters.
     *
     * @param displayName a display name for action
     * @param node a node the action should by applied to
     * @param performer a performer for action
     *
     * @return a new instance of {@link javax.swing.Action} for given parameters
     */
    public static Action createAction (
        String displayName, 
        ActionPerformer performer,
        int multiselectionType
    ) {
        return new ActionSupport (
            displayName, 
            performer, 
            multiselectionType
        );
    }
    
    
    // innerclasses ............................................................
    
    /**
     * @author   Jan Jancura
     */
    private static class ActionSupport extends AbstractAction {

        private ActionPerformer     performer;
        private int                 multiselectionType;
        private String              displayName;

 
        ActionSupport (
            String displayName, 
            ActionPerformer performer,
            int multiselectionType
        ) {
            super (displayName);
            this.performer = performer;
            this.displayName = displayName;
            this.multiselectionType = multiselectionType;
        }
        
        public boolean isEnabled () {
            if (multiselectionType == MULTISELECTION_TYPE_ANY)
                return true;
            Node[] ns = TopComponent.getRegistry ().getActivatedNodes ();
            if (multiselectionType == MULTISELECTION_TYPE_EXACTLY_ONE) {
                if (ns.length != 1) return false;
                return performer.isEnabled (
                    ((TreeModelNode) ns [0]).getObject ()
                );
            }
            int i, k = ns.length;
            for (i = 0; i < k; i++)
                if (!performer.isEnabled (
                    ((TreeModelNode) ns [i]).getObject ()
                 )) return false;
            return true;
        }

        public void actionPerformed (ActionEvent e) {
            Node[] ns = TopComponent.getRegistry ().getActivatedNodes ();
            int i, k = ns.length;
            IdentityHashMap h = new IdentityHashMap ();
            for (i = 0; i < k; i++) {
                Object node = ((TreeModelNode) ns [i]).getObject ();
                Action[] as = ns [i].getActions (false);
                int j, jj = as.length;
                for (j = 0; j < jj; j++)
                    if (equals (as [j])) {
                        ArrayList l = (ArrayList) h.get (as [j]);
                        if (l == null) {
                            l = new ArrayList ();
                            h.put (as [j], l);
                        }
                        l.add (node);
                    }
            }
            Iterator it = h.keySet ().iterator ();
            while (it.hasNext ()) {
                ActionSupport a = (ActionSupport) it.next ();
                a.performer.perform (
                    ((ArrayList) h.get (a)).toArray ()
                );
            }
        }
        
        public int hashCode () {
            return displayName.hashCode ();
        }
        
        public boolean equals (Object o) {
            return (o instanceof ActionSupport) && 
                displayName.equals (((ActionSupport) o).displayName);
        }
    }

    /**
     * Support interface for 
     * {@link #createAction(String,Object,Models.ActionPerformer)} method.
     */
    public static interface ActionPerformer {

        /**
         * Returns enabled property state for given set of nodes.
         *
         * @param nodes nodes the action shouuld be applied to
         * @return enabled property state for given set of nodes
         *
         * @see #createAction(String,Models.ActionPerformer,int)
         */
        public boolean isEnabled (Object node);

        /**
         * Called when action <code>action</code> is performed for 
         * nodes.
         *
         * @param nodes nodes the action shouuld be applied to
         *
         * @see #createAction(String,Models.ActionPerformer,int)
         */
        public void perform (Object[] nodes);
    }

    /**
     * Creates {@link org.netbeans.spi.viewmodel.TreeModel} for given TreeModel and
     * {@link org.netbeans.spi.viewmodel.TreeModelFilter}.
     * 
     * @author   Jan Jancura
     */
    final static class CompoundTreeModel implements TreeModel {


        private TreeModel model;
        private TreeModelFilter filter;

        
        /**
         * Creates {@link org.netbeans.spi.viewmodel.TreeModel} for given TreeModel and
         * {@link org.netbeans.spi.viewmodel.TreeModelFilter}.
         */
        CompoundTreeModel (TreeModel model, TreeModelFilter filter) {
            this.model = model;
            this.filter = filter;
        }

        /** 
         * Returns the root node of the tree or null, if the tree is empty.
         *
         * @return the root node of the tree or null
         */
        public Object getRoot () {
            return filter.getRoot (model);
        }

        /** 
         * Returns children for given parent on given indexes.
         *
         * @param   parent a parent of returned nodes
         * @throws  NoInformationException if the set of children can not be 
         *          resolved
         * @throws  ComputingException if the children resolving process 
         *          is time consuming, and will be performed off-line 
         * @throws  UnknownTypeException if this TreeModel implementation is not
         *          able to resolve dchildren for given node type
         *
         * @return  children for given parent on given indexes
         */
        public Object[] getChildren (Object parent, int from, int to) 
            throws NoInformationException, ComputingException, UnknownTypeException {

            return filter.getChildren (model, parent, from, to);
        }

        /**
         * Returns true if node is leaf.
         * 
         * @throws  UnknownTypeException if this TreeModel implementation is not
         *          able to resolve dchildren for given node type
         * @return  true if node is leaf
         */
        public boolean isLeaf (Object node) throws UnknownTypeException {
            return filter.isLeaf (model, node);
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            filter.addTreeModelListener (l);
            model.addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            filter.removeTreeModelListener (l);
            model.removeTreeModelListener (l);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            if (model instanceof CompoundTreeModel)
                return n + filter + "\n" +
                    ((CompoundTreeModel) model).toString (n + "  ");
            return n + filter + "\n" + 
                   n + "  " + model;
        }
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.TreeModel} for given TreeModel and
     * {@link org.netbeans.spi.viewmodel.TreeModelFilter}.
     * 
     * @author   Jan Jancura
     */
    final static class CompoundNodeModel implements NodeModel {


        private NodeModel model;
        private NodeModelFilter filter;


        /**
         * Creates {@link org.netbeans.spi.viewmodel.TreeModel} for given TreeModel and
         * {@link org.netbeans.spi.viewmodel.TreeModelFilter}.
         */
        CompoundNodeModel (NodeModel model, NodeModelFilter filter) {
            this.model = model;
            this.filter = filter;
        }
    
        /**
         * Returns display name for given node.
         *
         * @throws  ComputingException if the display name resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve display name for given node type
         * @return  display name for given node
         */
        public String getDisplayName (Object node) 
        throws ComputingException, UnknownTypeException {
            return filter.getDisplayName (model, node);
        }

        /**
         * Returns icon for given node.
         *
         * @throws  ComputingException if the icon resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve icon for given node type
         * @return  icon for given node
         */
        public String getIconBase (Object node) 
        throws ComputingException, UnknownTypeException {
            return filter.getIconBase (model, node);
        }

        /**
         * Returns tooltip for given node.
         *
         * @throws  ComputingException if the tooltip resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve tooltip for given node type
         * @return  tooltip for given node
         */
        public String getShortDescription (Object node) 
        throws ComputingException, UnknownTypeException {
            return filter.getShortDescription (model, node);
        }


        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            filter.addTreeModelListener (l);
            model.addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            filter.removeTreeModelListener (l);
            model.removeTreeModelListener (l);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            if (model instanceof CompoundNodeModel)
                return n + filter + "\n" +
                    ((CompoundNodeModel) model).toString (n + "  ");
            if (model instanceof DelegatingNodeModel)
                return n + filter + "\n" +
                    ((DelegatingNodeModel) model).toString (n + "  ");
            return n + filter + "\n" + 
                   n + "  " + model;
        }
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.TableModel} for given TableModel and
     * {@link org.netbeans.spi.viewmodel.TableModelFilter}.
     * 
     * @author   Jan Jancura
     */
    final static class CompoundTableModel implements TableModel {


        private TableModel model;
        private TableModelFilter filter;


        /**
         * Creates {@link org.netbeans.spi.viewmodel.TableModel} for given TableModel and
         * {@link org.netbeans.spi.viewmodel.TableModelFilter}.
         */
        CompoundTableModel (TableModel model, TableModelFilter filter) {
            this.model = model;
            this.filter = filter;
        }
    
        /**
         * Returns value to be displayed in column <code>columnID</code>
         * and row <code>node</code>. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @throws ComputingException if the value is not known yet and will 
         *         be computed later
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         *
         * @return value of variable representing given position in tree table.
         */
        public Object getValueAt (Object node, String columnID) throws 
        ComputingException, UnknownTypeException {
            return filter.getValueAt (model, node, columnID);
        }

        /**
         * Returns true if value displayed in column <code>columnID</code>
         * and row <code>node</code> is read only. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         *
         * @return true if variable on given position is read only
         */
        public boolean isReadOnly (Object node, String columnID) throws 
        UnknownTypeException {
            return filter.isReadOnly (model, node, columnID);
        }

        /**
         * Changes a value displayed in column <code>columnID</code>
         * and row <code>node</code>. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @param value a new value of variable on given position
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         */
        public void setValueAt (Object node, String columnID, Object value) 
        throws UnknownTypeException {
            filter.setValueAt (model, node, columnID, value);
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            filter.addTreeModelListener (l);
            model.addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            filter.removeTreeModelListener (l);
            model.removeTreeModelListener (l);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            if (model instanceof CompoundTableModel)
                return n + filter + "\n" +
                    ((CompoundTableModel) model).toString (n + "  ");
            if (model instanceof DelegatingTableModel)
                return n + filter + "\n" +
                    ((DelegatingTableModel) model).toString (n + "  ");
            return n + filter + "\n" + 
                   n + "  " + model;
        }
    }
    
    /**
     * Creates {@link org.netbeans.spi.viewmodel.NodeActionsProvider} for given NodeActionsProvider and
     * {@link org.netbeans.spi.viewmodel.NodeActionsProviderFilter}.
     * 
     * @author   Jan Jancura
     */
    final static class CompoundNodeActionsProvider implements NodeActionsProvider {


        private NodeActionsProvider model;
        private NodeActionsProviderFilter filter;


        /**
         * Creates {@link org.netbeans.spi.viewmodel.NodeActionsProvider} for given NodeActionsProvider and
         * {@link org.netbeans.spi.viewmodel.NodeActionsProviderFilter}.
         */
        CompoundNodeActionsProvider (NodeActionsProvider model, NodeActionsProviderFilter filter) {
            this.model = model;
            this.filter = filter;
        }
    
        /**
         * Performs default action for given node.
         *
         * @throws  UnknownTypeException if this NodeActionsProvider implementation 
         *          is not able to resolve actions for given node type
         * @return  display name for given node
         */
        public void performDefaultAction (Object node) 
        throws UnknownTypeException {
            filter.performDefaultAction (model, node);
        }

        /**
         * Returns set of actions for given node.
         *
         * @throws  UnknownTypeException if this NodeActionsProvider implementation 
         *          is not able to resolve actions for given node type
         * @return  display name for given node
         */
        public Action[] getActions (Object node) 
        throws UnknownTypeException {
            return filter.getActions (model, node);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            if (model instanceof CompoundNodeActionsProvider)
                return n + filter + "\n" +
                    ((CompoundNodeActionsProvider) model).toString (n + "  ");
            if (model instanceof DelegatingNodeActionsProvider)
                return n + filter + "\n" +
                    ((DelegatingNodeActionsProvider) model).toString (n + "  ");
            return n + filter + "\n" + 
                   n + "  " + model;
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            filter.addTreeModelListener (l);
            model.addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            filter.removeTreeModelListener (l);
            model.removeTreeModelListener (l);
        }
    }

    /**
     * Creates one {@link org.netbeans.spi.viewmodel.TableModel}
     * from given list of TableModels. DelegatingTableModel asks all underlaying 
     * models for each concrete parameter, and returns first returned value.
     *
     * @author   Jan Jancura
     */
    final static class DelegatingTableModel implements TableModel {

        private TableModel[] models;
        private HashMap classNameToModel = new HashMap ();


        /**
         * Creates new instance of DelegatingTableModel for given list of 
         * TableModels.
         *
         * @param models a list of TableModels
         */
        DelegatingTableModel (List models) {
            this (convert (models));
        }

        private static TableModel[] convert (List l) {
            TableModel[] models = new TableModel [l.size ()];
            return (TableModel[]) l.toArray (models);
        }

        /**
         * Creates new instance of DelegatingTableModel for given array of 
         * TableModels.
         *
         * @param models a array of TableModels
         */
        DelegatingTableModel (TableModel[] models) {
            this.models = models;        
        }

        /**
         * Returns value to be displayed in column <code>columnID</code>
         * and row <code>node</code>. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @throws ComputingException if the value is not known yet and will 
         *         be computed later
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         *
         * @return value of variable representing given position in tree table.
         */
        public Object getValueAt (Object node, String columnID)
        throws ComputingException, UnknownTypeException {
            TableModel model = (TableModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.getValueAt (node, columnID);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    Object v = models [i].getValueAt (node, columnID);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return v;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }    

        /**
         * Returns true if value displayed in column <code>columnID</code>
         * and row <code>node</code> is read only. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         *
         * @return true if variable on given position is read only
         */
        public boolean isReadOnly (Object node, String columnID) throws 
        UnknownTypeException {
            TableModel model = (TableModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.isReadOnly (node, columnID);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    boolean ro = models [i].isReadOnly (node, columnID);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return ro;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /**
         * Changes a value displayed in column <code>columnID</code>
         * and row <code>node</code>. Column ID is defined in by 
         * {@link ColumnModel#getID}, and rows are defined by values returned from 
         * {@TreeModel#getChildren}.
         *
         * @param node a object returned from {@TreeModel#getChildren} for this row
         * @param columnID a id of column defined by {@link ColumnModel#getID}
         * @param value a new value of variable on given position
         * @throws UnknownTypeException if there is no TableModel defined for given
         *         parameter type
         */
        public void setValueAt (Object node, String columnID, Object value)
        throws UnknownTypeException {
            TableModel model = (TableModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    model.setValueAt (node, columnID, value);
                    return;
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    models [i].setValueAt (node, columnID, value);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].removeTreeModelListener (l);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            int i, k = models.length - 1;
            if (k == -1) return "";
            StringBuffer sb = new StringBuffer ();
            for (i = 0; i < k; i++) {
                sb.append (n);
                sb.append (models [i]);
                sb.append ('\n');
            }
            sb.append (n);
            sb.append (models [i]);
            return new String (sb);
        }
    }

    /**
     * Creates one {@link org.netbeans.spi.viewmodel.NodeModel}
     * from given list of NodeModels. DelegatingNodeModel asks all underlaying 
     * models for each concrete parameter, and returns first returned value.
     *
     * @author   Jan Jancura
     */
    static final class DelegatingNodeModel implements NodeModel {

        private NodeModel[] models;
        private HashMap classNameToModel = new HashMap ();


        /**
         * Creates new instance of DelegatingNodeModel for given list of 
         * NodeModels.
         *
         * @param models a list of NodeModels
         */
        DelegatingNodeModel (
            List models
        ) {
            this (convert (models));
        }

        private static NodeModel[] convert (List l) {
            NodeModel[] models = new NodeModel [l.size ()];
            return (NodeModel[]) l.toArray (models);
        }

        /**
         * Creates new instance of DelegatingNodeModel for given array of 
         * NodeModels.
         *
         * @param models a array of NodeModels
         */
        DelegatingNodeModel (
            NodeModel[] models
        ) {
            this.models = models;

        }

        /**
         * Returns display name for given node.
         *
         * @throws  ComputingException if the display name resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve display name for given node type
         * @return  display name for given node
         */
        public String getDisplayName (Object node) 
        throws ComputingException, UnknownTypeException {
            NodeModel model = (NodeModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.getDisplayName (node);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    String dn = models [i].getDisplayName (node);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return dn;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /**
         * Returns tooltip for given node.
         *
         * @throws  ComputingException if the tooltip resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve tooltip for given node type
         * @return  tooltip for given node
         */
        public String getShortDescription (Object node) 
        throws ComputingException, UnknownTypeException {
            NodeModel model = (NodeModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.getShortDescription (node);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    String dn = models [i].getShortDescription (node);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return dn;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /**
         * Returns icon for given node.
         *
         * @throws  ComputingException if the icon resolving process 
         *          is time consuming, and the value will be updated later
         * @throws  UnknownTypeException if this NodeModel implementation is not
         *          able to resolve icon for given node type
         * @return  icon for given node
         */
        public String getIconBase (Object node) 
        throws ComputingException, UnknownTypeException {
            NodeModel model = (NodeModel) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.getIconBase (node);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    String dn = models [i].getIconBase (node);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return dn;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].removeTreeModelListener (l);
        }

        public String toString () {
            return toString ("    ");
        }
        
        public String toString (String n) {
            int i, k = models.length - 1;
            if (k == -1) return "";
            StringBuffer sb = new StringBuffer ();
            for (i = 0; i < k; i++) {
                sb.append (n);
                sb.append (models [i]);
                sb.append ('\n');
            }
            sb.append (n);
            sb.append (models [i]);
            return new String (sb);
        }
    }

    /**
     * Empty impleemntation of {@link org.netbeans.spi.viewmodel.TreeModel}.
     *
     * @author   Jan Jancura
     */
    static final class EmptyTreeModel implements TreeModel {

        /** 
         * Returns {@link org.netbeans.spi.viewmodel.TreeModel#ROOT}.
         *
         * @return {@link org.netbeans.spi.viewmodel.TreeModel#ROOT}
         */
        public Object getRoot () {
            return ROOT;
        }

        /** 
         * Returns empty array.
         *
         * @return empty array
         */
        public Object[] getChildren (Object parent, int from, int to) {
            return new Object [0];
        }

        /**
         * Returns false.
         *
         * @return false
         */
        public boolean isLeaf (Object node) {
            return false;
        }

        /** 
         * Do nothing.
         *
         * @param l the listener to be added
         */
        public void addTreeModelListener (TreeModelListener l) {
        }

        /** 
         * Do nothing.
         *
         * @param l the listener to be removed
         */
        public void removeTreeModelListener (TreeModelListener l) {
        }
    }
    
    /**
     * Creates one {@link org.netbeans.spi.viewmodel.NodeActionsProvider}
     * from given list of NodeActionsProviders. DelegatingNodeActionsProvider asks all 
     * underlaying models for each concrete parameter, and returns first 
     * returned value.
     *
     * @author   Jan Jancura
     */
    static final class DelegatingNodeActionsProvider implements NodeActionsProvider {

        private NodeActionsProvider[] models;
        private HashMap classNameToModel = new HashMap ();


        /**
         * Creates new instance of DelegatingNodeActionsProvider for given list of 
         * NodeActionsProvider.
         *
         * @param models a list of NodeActionsProvider
         */
        public DelegatingNodeActionsProvider (
            List models
        ) {
            this (convert (models));
        }

        private static NodeActionsProvider[] convert (List l) {
            NodeActionsProvider[] models = new NodeActionsProvider [l.size ()];
            return (NodeActionsProvider[]) l.toArray (models);
        }

        /**
         * Creates new instance of DelegatingNodeActionsProvider for given array of 
         * NodeActionsProvider.
         *
         * @param models a array of NodeActionsProvider
         */
        public DelegatingNodeActionsProvider (NodeActionsProvider[] models) {
            this.models = models;
        }

        /**
         * Returns set of actions for given node.
         *
         * @throws  UnknownTypeException if this NodeActionsProvider implementation 
         *          is not able to resolve actions for given node type
         * @return  display name for given node
         */
        public Action[] getActions (Object node) 
        throws UnknownTypeException {
            NodeActionsProvider model = (NodeActionsProvider) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    return model.getActions (node);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    Action[] dn = models [i].getActions (node);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return dn;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        /**
         * Performs default action for given node.
         *
         * @throws  UnknownTypeException if this NodeActionsProvider implementation 
         *          is not able to resolve actions for given node type
         * @return  display name for given node
         */
        public void performDefaultAction (Object node) throws UnknownTypeException {
            NodeActionsProvider model = (NodeActionsProvider) classNameToModel.get (
                node.getClass ().getName ()
            );
            if (model != null) 
                try {
                    model.performDefaultAction (node);
                } catch (UnknownTypeException e) {
                }
            int i, k = models.length;
            for (i = 0; i < k; i++) {
                try {
                    models [i].performDefaultAction (node);
                    classNameToModel.put (node.getClass ().getName (), models [i]);
                    return;
                } catch (UnknownTypeException e) {
                }
            }
            throw new UnknownTypeException (node);
        }

        public String toString () {
            return super.toString () + "\n" + toString ("  ");
        }
        
        public String toString (String n) {
            int i, k = models.length - 1;
            if (k == -1) return "";
            StringBuffer sb = new StringBuffer ();
            for (i = 0; i < k; i++) {
                sb.append (n);
                sb.append (models [i]);
                sb.append ('\n');
            }
            sb.append (n);
            sb.append (models [i]);
            return new String (sb);
        }

        /** 
         * Registers given listener.
         * 
         * @param l the listener to add
         */
        public void addTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].addTreeModelListener (l);
        }

        /** 
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeTreeModelListener (TreeModelListener l) {
            int i, k = models.length;
            for (i = 0; i < k; i++)
                models [i].removeTreeModelListener (l);
        }
    }
}
