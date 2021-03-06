/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.remote.ui.setup;

import java.awt.Component;
import java.awt.Dialog;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.cnd.api.remote.ServerList;
import org.netbeans.modules.cnd.api.remote.ServerRecord;
import org.netbeans.modules.cnd.remote.server.RemoteServerRecord;
import org.netbeans.modules.cnd.spi.remote.RemoteSyncFactory;
import org.netbeans.modules.cnd.spi.remote.setup.HostSetupProvider;
import org.netbeans.modules.cnd.spi.remote.setup.HostSetupWorker;
import org.netbeans.modules.cnd.api.toolchain.ToolsCacheManager;
import org.netbeans.modules.cnd.spi.remote.setup.HostValidatorFactory;
import org.netbeans.modules.cnd.spi.remote.ui.HostSetupWorkerUI;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

public final class CreateHostWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor>, ChangeListener {

    private int index;
    private WizardDescriptor.Panel<WizardDescriptor>[] panels;
    private final ToolsCacheManager cacheManager;
    private final List<HostSetupProvider> providers;
    private final CreateHostWizardPanel0 panel0;
    final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();

    private CreateHostWizardIterator(List<HostSetupProvider> providers, ToolsCacheManager cacheManager) {
        this.providers = providers;
        this.cacheManager = cacheManager;
        this.panel0 = new CreateHostWizardPanel0(this, providers, cacheManager);
    }

    private HostSetupWorker getSelectedWorker() {
        return panel0.getSelectedWorker();
    }

    /** to be called when provider is changed */
    @Override
    public void stateChanged(ChangeEvent e) {
        panels = null;
        getPanels();
        fireStateChanged();
    }

    @SuppressWarnings("unchecked")
    private WizardDescriptor.Panel<WizardDescriptor>[] getPanelsUnchecked() {

        List<WizardDescriptor.Panel<WizardDescriptor>> pList = new ArrayList<>();
        HostSetupWorker worker;
        if (providers.size() > 1) {
            pList.add(panel0);
        }
        // even in the case we don't show panel0
        worker = panel0.getSelectedWorker();
        //i am not sure what is all his about
        if (HostSetupWorkerUI.class.isAssignableFrom(worker.getClass())) {
            pList.addAll(((HostSetupWorkerUI)worker).getWizardPanels(HostValidatorFactory.create(cacheManager)));
        }

        WizardDescriptor.Panel<WizardDescriptor>[] result =
                (WizardDescriptor.Panel<WizardDescriptor>[]) (new WizardDescriptor.Panel[pList.size()]);
        pList.toArray(result);
        return result;
    }

    private WizardDescriptor.Panel<WizardDescriptor>[] getPanels() {
        if (panels == null) {
            panels = getPanelsUnchecked();
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels()[index];
    }

    @Override
    public String name() {
        return "";//index + 1 + ". from " + getPanels().length;
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }

    public static ServerRecord invokeMe(ToolsCacheManager cacheManager) {

        List<HostSetupProvider> providers = new ArrayList<>();
        for (HostSetupProvider provider : Lookup.getDefault().lookupAll(HostSetupProvider.class)) {
            if (provider.isApplicable()) {
                providers.add(provider);
            }
        }

        if (providers.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    NbBundle.getMessage(CreateHostWizardIterator.class, "NoProviders_Message"),
                    NbBundle.getMessage(CreateHostWizardIterator.class, "NoProviders_Title"),
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        CreateHostWizardIterator iterator = new CreateHostWizardIterator(providers, cacheManager);

        WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator);
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}")); //NOI18N
        wizardDescriptor.setTitle(NbBundle.getMessage(CreateHostWizardIterator.class, "CreateNewHostWizardTitle"));

        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();

        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;

        if (cancelled) {
            return null;
        }

        HostSetupWorker worker = iterator.getSelectedWorker();

        if (worker == null) {
            return null;
        }

        HostSetupWorker.Result result = worker.getResult();
        return finishHostSetup(result);
    }

    private static ServerRecord finishHostSetup(HostSetupWorker.Result result) {
        if (result == null) {
            return null;
        }
        Runnable r = result.getRunOnFinish();
        // CndUtils.assertFalse(r == null); // can be null - fixing #248752 - Some UI control item is needed in order to skip "Search Compliers"
        if (r != null) {
            r.run();
        }
        ExecutionEnvironment execEnv = result.getExecutionEnvironment();
        String displayName = result.getDisplayName();
        if (displayName == null) {
            displayName = execEnv.getDisplayName();
        }
        final RemoteSyncFactory syncFactory = result.getSyncFactory();
        final ServerRecord record = ServerList.get(execEnv);
        RemoteServerRecord rsr = (RemoteServerRecord) record;
        rsr.setSyncFactory(syncFactory);
        rsr.setDisplayName(displayName);
        return record;
    }

    public static void applyHostSetup(ToolsCacheManager cacheManager, HostSetupWorker.Result createHostData) {
        ServerRecord newServerRecord = CreateHostWizardIterator.finishHostSetup(createHostData);
        if (newServerRecord != null) {
            List<ServerRecord> hosts = new ArrayList<ServerRecord>(ServerList.getRecords());
            if (!hosts.contains(newServerRecord)) {
                hosts.add(newServerRecord);
                cacheManager.setHosts(hosts);
                cacheManager.setDefaultRecord(ServerList.getDefaultRecord());
                cacheManager.applyChanges();
            }
        }
    }
}
