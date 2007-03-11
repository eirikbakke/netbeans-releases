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

package org.netbeans.modules.vmd.midp.screen.display;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import org.netbeans.modules.vmd.api.screen.display.ScreenDeviceInfo;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.items.StringItemCD;

/**
 *
 * @author Anton Chechel
 * @version 1.0
 */
public class StringItemDisplayPresenter extends ItemDisplayPresenter {
    
    private JLabel label;

    public StringItemDisplayPresenter() {
        label = new JLabel ();
        getPanel().add(label, BorderLayout.SOUTH);
    }
    
    public void reload (ScreenDeviceInfo deviceInfo) {
        super.reload(deviceInfo);
        label.setText(MidpValueSupport.getHumanReadableString(getComponent().readProperty(StringItemCD.PROP_TEXT)));
    }
}
