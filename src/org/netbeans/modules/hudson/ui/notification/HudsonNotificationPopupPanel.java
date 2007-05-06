/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.hudson.ui.notification;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.netbeans.modules.hudson.api.HudsonJob;
import org.netbeans.modules.hudson.ui.HudsonJobView;
import org.netbeans.modules.hudson.ui.HudsonLinkButton;
import org.openide.util.Mutex;
import org.openide.windows.TopComponent;

/**
 * Hudson notification popup panel
 * 
 * @author  Michal Mocnak
 */
public class HudsonNotificationPopupPanel extends javax.swing.JPanel {
    
    private final ImageIcon RED_ICON = new ImageIcon(getClass().getResource("/org/netbeans/modules/hudson/ui/resources/red.png"));
    
    /** Creates new form HudsonNotificationPopupPanel */
    public HudsonNotificationPopupPanel() {
        initComponents();
        
        setName("HudsonNotificationPopupPanel");
    }
    
    public synchronized int setContent(Collection<HudsonJob> content) {
        // Sort content
        Collections.sort(Arrays.asList(content.toArray(new HudsonJob[] {})));
        
        // Prepare contraints
        GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridy = 0;
        
        // Clean container
        buildsPanel.removeAll();
        
        // Change content
        for (final HudsonJob job : content) {
            // Set constraints
            gbc.gridx = 0;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.anchor = java.awt.GridBagConstraints.WEST;
            gbc.weightx = 1.0;
            gbc.insets = new java.awt.Insets(0, 0, 5, 0);
            
            buildsPanel.add(new JLabel(RED_ICON), gbc);
            
            // Set constraints
            gbc.gridx = 1;
            gbc.anchor = java.awt.GridBagConstraints.WEST;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.weightx = 42.0;
            gbc.insets = new java.awt.Insets(0, 5, 5, 5);
            
            HudsonLinkButton link = new HudsonLinkButton(false);
            
            link.setCursor(new Cursor(Cursor.HAND_CURSOR));
            link.setText(job.getName());
            link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final TopComponent win = HudsonJobView.getInstance(job);
                    
                    Mutex.EVENT.postReadRequest(new Runnable() {
                        public void run() {
                            win.open();
                            win.requestActive();
                        }
                    });
                }
            });
            
            buildsPanel.add(link, gbc);
            
            gbc.gridy++;
        }
        
        // Repaint and revalidate
        buildsPanel.repaint();
        buildsPanel.revalidate();
        
        return content.size();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        contentPane = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        buildsPanel = new javax.swing.JPanel();

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setLayout(new java.awt.GridBagLayout());

        contentPane.setBorder(null);
        contentPane.setViewportBorder(null);
        contentPane.setOpaque(false);

        contentPanel.setBorder(null);
        contentPanel.setLayout(new java.awt.GridBagLayout());

        titlePanel.setLayout(new java.awt.GridBagLayout());

        titleLabel.setBackground(javax.swing.UIManager.getDefaults().getColor("PropSheet.selectionBackground"));
        titleLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        titleLabel.setForeground(new java.awt.Color(254, 254, 254));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(org.openide.util.NbBundle.getMessage(HudsonNotificationPopupPanel.class, "titleLabel.text")); // NOI18N
        titleLabel.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("PropSheet.disabledForeground")));
        titleLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        titlePanel.add(titleLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        contentPanel.add(titlePanel, gridBagConstraints);

        buildsPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        contentPanel.add(buildsPanel, gridBagConstraints);

        contentPane.setViewportView(contentPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(contentPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buildsPanel;
    private javax.swing.JScrollPane contentPane;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
    
}
