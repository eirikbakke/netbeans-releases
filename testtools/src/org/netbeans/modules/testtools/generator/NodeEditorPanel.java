/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.testtools.generator;

/*
 * ComponentsEditorPanel.java
 *
 * Created on March 12, 2002, 11:16 AM
 */
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.openide.DialogDescriptor;
import org.openide.TopManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import org.openide.nodes.Node;
import org.openide.nodes.BeanNode;
import java.beans.IntrospectionException;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import java.io.InputStream;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.event.KeyEvent;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import java.util.Collection;
import java.awt.Component;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeNode;

/** class with panel used for edit found components before generation
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class NodeEditorPanel extends javax.swing.JPanel implements ChangeListener {
    
    static ImageIcon rootIcon;
    static ImageIcon existIcon;
    static ImageIcon newIcon;
    static ImageIcon inlineIcon;
    
    static {
        try {
            InputStream in = NodeEditorPanel.class.getClassLoader().getResourceAsStream("org/netbeans/modules/testtools/generator/class.gif"); // NOI18N
            byte b[] = new byte[in.available()];
            in.read(b);
            in.close();
            rootIcon = new ImageIcon(b);
            existIcon=rootIcon;
            in = NodeEditorPanel.class.getClassLoader().getResourceAsStream("org/netbeans/modules/testtools/generator/new.gif"); // NOI18N
            b = new byte[in.available()];
            in.read(b);
            in.close();
            newIcon = new ImageIcon(b);
            in = NodeEditorPanel.class.getClassLoader().getResourceAsStream("org/netbeans/modules/testtools/generator/inline.gif"); // NOI18N
            b = new byte[in.available()];
            in.read(b);
            in.close();
            inlineIcon = new ImageIcon(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    List records;

    static class MyCellRenderer extends DefaultTreeCellRenderer {
        public MyCellRenderer() {
            super();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,hasFocus);
            try {
                Object o=((DefaultMutableTreeNode)value).getUserObject();
                if (o instanceof NodeGenerator.NewActionRecord) {
                    if (((NodeGenerator.NewActionRecord)o).isInline()) {
                        setIcon(inlineIcon);
                    } else {
                        setIcon(newIcon);
                    }
                }
            } catch (Exception e) {};
            return this;
        }

    }
    
    /** Creates new form ComponentsEditorPanel
     * @param gen ComponentGenerator instance */
    public NodeEditorPanel(NodeGenerator gen) {
        DefaultMutableTreeNode rootNode=gen.getNodeDelegate();
        records = gen.getActionRecords();
        Iterator it=records.iterator();
        while (it.hasNext()) {
            ((NodeGenerator.ActionRecord)it.next()).getNodeDelegate();
        }
        gen.addChangeListener(this);
        initComponents();
        if ((rootIcon!=null)&&(existIcon!=null)&&(newIcon!=null)&&(inlineIcon!=null)) {
            MyCellRenderer rend = new MyCellRenderer();
            rend.setClosedIcon(rootIcon);
            rend.setOpenIcon(rootIcon);
            rend.setLeafIcon(existIcon);
            tree.setCellRenderer(rend);
        }
        tree.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION); 
        tree.setModel(new DefaultTreeModel(rootNode));
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                nodeChanged(e.getNewLeadSelectionPath());
            }
        });
    }
    
    void nodeChanged(TreePath path) {
        if (path==null) {
            propertySheet.setNodes(new Node[0]);
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            try {
                propertySheet.setNodes(new Node[]{new BeanNode(node.getUserObject())});
            } catch (IntrospectionException ex) {
                propertySheet.setNodes(new Node[0]);
            }
        }
    }        
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        propertySheet = new org.openide.explorer.propertysheet.PropertySheet();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(415);
        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.5);
        splitPane.setPreferredSize(new java.awt.Dimension(800, 400));
        tree.setShowsRootHandles(true);
        tree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                treeKeyReleased(evt);
            }
        });

        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });

        scrollPane.setViewportView(tree);

        splitPane.setLeftComponent(scrollPane);

        propertySheet.setDisplayWritableOnly(true);
        splitPane.setRightComponent(propertySheet);

        add(splitPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void treeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeKeyReleased
        if ((evt.getKeyCode()==KeyEvent.VK_DELETE)&&(evt.getModifiers()==0)&&(tree.getSelectionCount()==1)&&(tree.getSelectionRows()[0]>0)) {
            DeleteActionPerformed();
        }
    }//GEN-LAST:event_treeKeyReleased

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        if ((evt.getModifiers()==evt.BUTTON3_MASK)&&(tree.getSelectionCount()==1)&&(tree.getSelectionRows()[0]>0)) {
            JPopupMenu menu=new JPopupMenu();
            menu.add("Delete").addActionListener(new java.awt.event.ActionListener() { // NOI18N
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    DeleteActionPerformed();
                }
            });
            menu.show(tree,evt.getX(),evt.getY());
        }
    }//GEN-LAST:event_treeMouseClicked
    
    void DeleteActionPerformed() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
        Enumeration enum=node.postorderEnumeration();
        while (enum.hasMoreElements()) {
            records.remove(((DefaultMutableTreeNode)enum.nextElement()).getUserObject());
        }
        ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(node);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    private javax.swing.JScrollPane scrollPane;
    private org.openide.explorer.propertysheet.PropertySheet propertySheet;
    // End of variables declaration//GEN-END:variables
    
    /** shows Component Editor modal dialog
     * @param gen ComponentGenerator instance
     * @return boolean false when operation canceled */    
    public static boolean showDialog(NodeGenerator gen) {
        DialogDescriptor desc = new DialogDescriptor(new NodeEditorPanel(gen), "Node Editor", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null); // NOI18N
        TopManager.getDefault().createDialog(desc).show();
        return desc.getValue()==DialogDescriptor.OK_OPTION;
    }
    
    /** implementation of StateListener
     * @param changeEvent ChangeEvent */    
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        DefaultTreeModel model=(DefaultTreeModel)tree.getModel();
        Object o=changeEvent.getSource();
        if (o instanceof NodeGenerator) {
            model.nodeChanged(((NodeGenerator)o).getNodeDelegate());
        } else {
            model.nodeChanged(((NodeGenerator.ActionRecord)o).getNodeDelegate());
        }            
        nodeChanged(tree.getSelectionPath());
    }
    
}
