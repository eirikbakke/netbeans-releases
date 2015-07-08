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

package org.netbeans.modules.j2ee.weblogic9.ui.wizard;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author Petr Hejl
 */
public class ServerRemotePropertiesPanel implements WizardDescriptor.Panel, ChangeListener {

    private final List<ChangeListener> listeners = new CopyOnWriteArrayList<ChangeListener>();

    private final AtomicBoolean isValidating = new AtomicBoolean();

    private ServerRemotePropertiesVisual component;

    private WizardDescriptor wizard;

    private transient WLInstantiatingIterator instantiatingIterator;

    public ServerRemotePropertiesPanel (WLInstantiatingIterator instantiatingIterator) {
        this.instantiatingIterator = instantiatingIterator;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new ServerRemotePropertiesVisual(instantiatingIterator);
            component.addChangeListener(this);
        }
        return component;
    }

    public  ServerRemotePropertiesVisual getVisual() {
        return (ServerRemotePropertiesVisual) getComponent();
    }

    @Override
    public HelpCtx getHelp() {
         return new HelpCtx("j2eeplugins_registering_app_server_weblogic_properties_remote"); // NOI18N
    }

    @Override
    public boolean isValid() {
        if (isValidating.compareAndSet(false, true)) {
            try {
                return getVisual().valid(wizard);
            } finally {
                isValidating.set(false);
            }
        }
        return true;
    }

    @Override
    public void readSettings(Object settings) {
        if (wizard == null) {
            wizard = (WizardDescriptor) settings;
        }
    }

    @Override
    public void storeSettings(Object settings) {

    }

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    @Override
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a registered listener
     *
     * @param listener the listener to be removed
     */
    @Override
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        fireChangeEvent(event);
    }

    private void fireChangeEvent(ChangeEvent event) {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }
}
