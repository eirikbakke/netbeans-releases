/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.web.wizards;

import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;

/**
 * FinishableProxyWizardPanel.java - used decorator pattern to enable to finish
 * the original wizard panel, that is not finishable
 *
 *
 * @author mkuchtiak
 */
public class FinishableProxyWizardPanel implements WizardDescriptor.Panel, WizardDescriptor.FinishablePanel {

    private final WizardDescriptor.Panel original;
    private final HelpCtx helpCtx;
    private boolean isOriginallyValid = true;

    public FinishableProxyWizardPanel(Panel original, HelpCtx helpCtx) {
        this.original = original;
        this.helpCtx = helpCtx;
    }
    
    public FinishableProxyWizardPanel(Panel original, HelpCtx helpCtx, 
            boolean isValid ) 
    {
        this.original = original;
        this.helpCtx = helpCtx;
        isOriginallyValid = isValid;
    }
    
    public FinishableProxyWizardPanel(WizardDescriptor.Panel original) {
        this(original, null);
    }

    public void addChangeListener(javax.swing.event.ChangeListener l) {
        original.addChangeListener(l);
    }

    public void removeChangeListener(javax.swing.event.ChangeListener l) {
        original.removeChangeListener(l);
    }

    public void storeSettings(Object settings) {
        original.storeSettings(settings);
    }

    public void readSettings(Object settings) {
        original.readSettings(settings);
    }

    public boolean isValid() {
        if ( !isOriginallyValid ){
            return false;
        }
        return original.isValid();
    }

    public boolean isFinishPanel() {
        return true;
    }

    public java.awt.Component getComponent() {
        return original.getComponent();
    }

    public org.openide.util.HelpCtx getHelp() {
        if (helpCtx != null) {
            return helpCtx;
        }
        return original.getHelp();
    }
    
}
