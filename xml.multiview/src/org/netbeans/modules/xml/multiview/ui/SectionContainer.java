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

package org.netbeans.modules.xml.multiview.ui;

import java.awt.*;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.openide.nodes.Node;

/**
 *
 * @author  mkuchtiak
 */
public class SectionContainer extends javax.swing.JPanel implements NodeSectionPanel {
    
    //private HashMap map = new HashMap();
    //private JScrollPane scrollPane;
    private Node activeNode=null;
    private SectionView sectionView;
    private String title;
    private Node root;
    private boolean active;
    private int sectionCount=0;
    /** Creates new form SectionContainer */
    
    public SectionContainer(SectionView sectionView, Node root, String title) {
        this.sectionView = sectionView;
        this.title=title;
        this.root=root;
        initComponents();
        setBackground(SectionVisualTheme.getDocumentBackgroundColor());
        titleButton.setBackground(SectionVisualTheme.getDocumentBackgroundColor());
        filler.setBackground(SectionVisualTheme.getFillerColor());
        titleButton.setText(title);
        
        titleButton.addMouseListener(new org.openide.awt.MouseUtils.PopupMouseAdapter() {
            protected void showPopup(java.awt.event.MouseEvent e) {
                JPopupMenu popup = getNode().getContextMenu();
                popup.show(foldButton,e.getX(), e.getY());
            }
        });
    }
    
    /** Method from NodeSectionPanel interface */
    public Node getNode() {
        return root; 
    }
    
    /** Method from NodeSectionPanel interface */
    public void open(){
        foldButton.setSelected(false);
        contentPanel.setVisible(true);
        filler.setVisible(true);
    }

    /** Method from NodeSectionPanel interface */
    public void scroll(javax.swing.JScrollPane scrollPane) {
        Point location = SwingUtilities.convertPoint(this, getLocation(),scrollPane);
        location.x=0;
        scrollPane.getViewport().setViewPosition(location);
    }
    
    /** Method from NodeSectionPanel interface */
    public void setActive(boolean active) {
        titleButton.setBackground(active?SectionVisualTheme.getSectionHeaderActiveColor():SectionVisualTheme.getSectionHeaderColor());
        if (active && !this.equals(sectionView.getActivePanel())) {
            sectionView.sectionSelected(true);
            sectionView.setActivePanel(this);
            sectionView.selectNode(root);
        }
        this.active=active;
    }
    
    /** Method from NodeSectionPanel interface */
    public boolean isActive() {
        return active;
    }

    /** Maps section to a node
    */
    public void mapSection(Node key, NodeSectionPanel panel){
        sectionView.mapSection(key,panel);
    }
    
    /** Gets section for a node
    */
    public NodeSectionPanel getSection(Node key){
        return sectionView.getSection(key);
    }
    
    /** Adds new section and maps it to a node
    */
    public void addSection(NodeSectionPanel section){
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = sectionCount;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        contentPanel.add((JPanel)section,gridBagConstraints);  
        
        mapSection(section.getNode(), section);
        sectionCount++;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        foldButton = new javax.swing.JToggleButton();
        titleButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        contentPanel = new javax.swing.JPanel();
        filler = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        foldButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/xml/multiview/resources/arrowbottom.gif")));
        foldButton.setBorder(null);
        foldButton.setBorderPainted(false);
        foldButton.setContentAreaFilled(false);
        foldButton.setFocusPainted(false);
        foldButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/xml/multiview/resources/arrowright.gif")));
        foldButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foldButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 6);
        add(foldButton, gridBagConstraints);

        titleButton.setFont(new java.awt.Font("Dialog", 1, 14));
        titleButton.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        titleButton.setBorderPainted(false);
        titleButton.setFocusPainted(false);
        titleButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        titleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(titleButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(jSeparator1, gridBagConstraints);

        contentPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        add(contentPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 2, 0, 6);
        add(filler, gridBagConstraints);

    }//GEN-END:initComponents

    private void titleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleButtonActionPerformed
        // TODO add your handling code here:
        setActive(true);
    }//GEN-LAST:event_titleButtonActionPerformed

    private void foldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foldButtonActionPerformed
        // TODO add your handling code here:
        contentPanel.setVisible(!foldButton.isSelected());
        filler.setVisible(!foldButton.isSelected());
    }//GEN-LAST:event_foldButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel filler;
    private javax.swing.JToggleButton foldButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton titleButton;
    // End of variables declaration//GEN-END:variables
    
}
