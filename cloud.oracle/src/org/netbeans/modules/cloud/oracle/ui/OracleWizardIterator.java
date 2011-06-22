/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cloud.oracle.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cloud.oracle.OracleInstance;
import org.netbeans.modules.cloud.oracle.OracleInstanceManager;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.ChangeSupport;

/**
 *
 */
public class OracleWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private ChangeSupport listeners;
    private WizardDescriptor wizard;
    private OracleWizardPanel panel;
    boolean first = true;

    public OracleWizardIterator() {
        listeners = new ChangeSupport(this);
    }
    
    public static final String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N

    @Override
    public Set instantiate() throws IOException {
        String username = (String)wizard.getProperty(OracleWizardPanel.TENANT_USERNAME);
        assert username != null;
        String pwd = (String)wizard.getProperty(OracleWizardPanel.TENANT_PASSWORD);
        assert pwd != null;
        String url = (String)wizard.getProperty(OracleWizardPanel.URL_ENDPOINT);
        assert url != null;
        String name = (String)wizard.getProperty(PROP_DISPLAY_NAME);
        assert name != null;
        
        OracleInstanceManager.getDefault().add(new OracleInstance(name, username, pwd, url));
        
        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panel = null;
    }

    @Override
    public Panel current() {
        if (first) {
            if (panel == null) {
                panel = new OracleWizardPanel();
            }
            return panel;
        } else {
//            if (panel2 == null) {
//                panel2 = new AmazonWizardPanel2();
//            }
//            return panel2;
            return null;
        }
    }

    @Override
    public String name() {
        return "Oracle Cloud 9";
    }

    @Override
    public boolean hasNext() {
        return /*first*/ false;
    }

    @Override
    public boolean hasPrevious() {
        return !first;
    }

    @Override
    public void nextPanel() {
        first = false;
    }

    @Override
    public void previousPanel() {
        first = true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.removeChangeListener(l);
    }
    
}
