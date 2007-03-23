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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 */

package org.netbeans.modules.vmd.screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.modules.vmd.screen.resource.ResourcePanel;
import org.netbeans.modules.vmd.screen.device.DevicePanel;


/**
 * 
 * @author David Kaspar
 */
public class MainPanel extends JPanel {
    
    private static final Font LABEL_FONT = new Font("Dialog", Font.PLAIN, 20);
    private static final Color LABEL_COLOR = ResourcePanel.BACKGROUND_COLOR.darker();
    
    public MainPanel(DevicePanel devicePanel, ResourcePanel resourcePanel) {
        setLayout(new GridBagLayout());
        setBackground(ResourcePanel.BACKGROUND_COLOR);
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(12, 12, 6, 6);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        JLabel devLabel = new JLabel("Device Screen");
        devLabel.setForeground(LABEL_COLOR);
        devLabel.setHorizontalAlignment(JLabel.CENTER);
        devLabel.setFont(LABEL_FONT);
        add(devLabel, constraints);
        
        constraints.insets = new Insets(6, 12, 12, 6);
        constraints.gridy = 1;
        constraints.weighty = 1.0;
        add(devicePanel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(12, 6, 6, 12);
        JLabel resLabel = new JLabel("Assigned Resources");
        resLabel.setForeground(LABEL_COLOR);
        resLabel.setHorizontalAlignment(JLabel.CENTER);
        resLabel.setFont(LABEL_FONT);
        add(resLabel, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(6, 6, 12, 12);
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(resourcePanel, constraints);
    }
    
}
