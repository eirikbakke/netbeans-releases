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
 */

package org.netbeans.modules.mobility.cldcplatform.customwizard;

import org.openide.WizardDescriptor;
import org.openide.loaders.DataObject;
import org.netbeans.modules.mobility.cldcplatform.*;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.util.*;
import java.io.IOException;

/**
 * @author David Kaspar
 */
public class InstallerIterator implements WizardDescriptor.InstantiatingIterator {
    
    private static InstallerIterator INSTANCE;
    
    ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
    int current;
    private WizardDescriptor.FinishablePanel[] panels;
    private WizardDescriptor wizardDescriptor;
    
    public static synchronized InstallerIterator getDefault() {
        if (INSTANCE == null)
            INSTANCE = new InstallerIterator();
        return INSTANCE;
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        listeners.add(changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        listeners.remove(changeListener);
    }
    
    public void fireChanged() {
        final ChangeEvent e = new ChangeEvent(this);
        for ( final ChangeListener l : listeners ) {
            l.stateChanged(e);
        }
    }
    
    public WizardDescriptor.Panel current() {
        return panels[current];
    }
    
    public String name() {
        return "Add Custom Mobile Platform";
    }
    
    public boolean hasNext() {
        return current < panels.length - 1  &&  current().isValid();
    }
    
    public boolean hasPrevious() {
        return current > 0;
    }
    
    public void nextPanel() {
        assert hasNext();
        current ++;
    }
    
    public void previousPanel() {
        assert hasPrevious();
        current --;
    }
    
    public void initialize(final WizardDescriptor wizardDescriptor) {
        this.wizardDescriptor = wizardDescriptor;
        current = 0;
        panels = new WizardDescriptor.FinishablePanel[] {
            new WizardPanel(new CommandLinesPanel()),
            new WizardPanel(new ClasspathPanel()),
            new SrcDocPathsPanel.WizardPanel(true),
        };
        String[] strs = new String[panels.length];
        for (int i = 0; i < strs.length; i++)
            strs[i] = panels[i].getComponent().getName();
        ((JComponent)panels[0].getComponent()).putClientProperty("WizardPanel_contentData", strs); // NOI18N
    }
    
    
    public void uninitialize(@SuppressWarnings("unused")
	final WizardDescriptor wizardDescriptor) {
        panels = null;
    }
    
    public Set<J2MEPlatform> instantiate() throws IOException {
        final J2MEPlatform platform = (J2MEPlatform) wizardDescriptor.getProperty(DetectPanel.PLATFORM);
        final DataObject dao = platform != null ? J2MEPlatform.createPlatform(platform) : null;
        return dao != null ? Collections.singleton(platform) : Collections.EMPTY_SET;
    }
    
}
