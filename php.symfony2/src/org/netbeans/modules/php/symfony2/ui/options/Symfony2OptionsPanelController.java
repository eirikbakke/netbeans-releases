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
package org.netbeans.modules.php.symfony2.ui.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.symfony2.options.Symfony2Options;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Symfony2 IDE options.
 */
@OptionsPanelController.SubRegistration(
    location=UiUtils.OPTIONS_PATH,
    id=Symfony2OptionsPanelController.OPTIONS_SUBPATH,
    displayName="#LBL_OptionsName",
//    toolTip="#LBL_OptionsTooltip"
    position=190
)
public class Symfony2OptionsPanelController extends OptionsPanelController implements ChangeListener {

    public static final String OPTIONS_SUBPATH = "Symfony2"; // NOI18N

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private Symfony2OptionsPanel symfony2OptionsPanel = null;
    private volatile boolean changed = false;


    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + OPTIONS_SUBPATH; // NOI18N
    }

    @Override
    public void update() {
        symfony2OptionsPanel.setSandbox(getOptions().getSandbox());
        symfony2OptionsPanel.setIgnoreCache(getOptions().getIgnoreCache());

        changed = false;
    }

    @Override
    public void applyChanges() {
        getOptions().setSandbox(symfony2OptionsPanel.getSandbox());
        getOptions().setIgnoreCache(symfony2OptionsPanel.getIgnoreCache());

        changed = false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public boolean isValid() {
        // warnings
        String warning = validateSandbox(symfony2OptionsPanel.getSandbox());
        if (warning != null) {
            symfony2OptionsPanel.setWarning(warning);
            return true;
        }

        // everything ok
        symfony2OptionsPanel.setError(" "); // NOI18N
        return true;
    }

    @NbBundle.Messages({
        "Symfony2OptionsPanelController.symfony.sandbox=Symfony",
        "Symfony2OptionsPanelController.symfony.notZip=Symfony is not Zip file."
    })
    public static String validateSandbox(String sandbox) {
        String warning = FileUtils.validateFile(Bundle.Symfony2OptionsPanelController_symfony_sandbox(), sandbox, false);
        if (warning == null) {
            if (!new File(sandbox).getName().toLowerCase().endsWith(".zip")) { // NOI18N
                warning = Bundle.Symfony2OptionsPanelController_symfony_notZip();
            }
        }
        return warning;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        if (symfony2OptionsPanel == null) {
            symfony2OptionsPanel = new Symfony2OptionsPanel();
            symfony2OptionsPanel.addChangeListener(this);
        }
        return symfony2OptionsPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(Symfony2Options.class.getName());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private Symfony2Options getOptions() {
        return Symfony2Options.getInstance();
    }

}
