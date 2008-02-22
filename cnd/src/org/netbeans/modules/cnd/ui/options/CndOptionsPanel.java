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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.cnd.ui.options;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;


/** Implementation of C/C++ Options panel */
public final class CndOptionsPanel extends JPanel {

    private JTabbedPane tabbedPane = new JTabbedPane();
    
    public CndOptionsPanel() {
        // no logic in constructor to speedup Tools->Options display time
    }
    
    /*package*/ void updateControllers(OptionsPanelController[] comps) {
        String name;
        char mnem;
        tabbedPane.removeAll();
	for (int i = 0; i < comps.length; i++) {
	    comps[i].getComponent(null).setBorder(new EmptyBorder(8, 8, 8, 8));
            name = NbBundle.getMessage(comps[i].getClass(), comps[i].getComponent(null).getName());
	    tabbedPane.addTab(name, comps[i].getComponent(null));
            // it's impossible to support unique mnemonics for tabs due to expansive growth of options panes and their's content.
            // So lets remove them at all as most NetBeans components did. (IZ127911)
            // mnem = NbBundle.getMessage(comps[i].getClass(), comps[i].getComponent(null).getName() + "_Mnemonic").charAt(0); // NOI18N
	    // tabbedPane.setMnemonicAt(i, mnem);
	}      
        
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);        
    }
}


