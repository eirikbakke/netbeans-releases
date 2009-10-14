/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.mobility.cldcplatform.customwizard;
import org.netbeans.modules.mobility.cldcplatform.DetectPanel;
import org.netbeans.modules.mobility.cldcplatform.J2MEPlatform;
import org.netbeans.modules.mobility.cldcplatform.UEIEmulatorConfiguratorImpl;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.mobility.cldcplatform.ArchiveFilter;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author  dave
 */
public class ClasspathPanel extends javax.swing.JPanel implements WizardPanel.ComponentDescriptor {
    
    private static final String PROP_CLASSPATH = "Classpath"; // NOI18N
    
    final private DefaultListModel classpathListModel = new DefaultListModel();
    private WizardPanel wizardPanel;
    private J2MEPlatform.J2MEProfile[] profiles;
    
    /** Creates new form ClasspathPanel */
    public ClasspathPanel() {
        initComponents();
        infoPanel.setEditorKitForContentType("text/html", new HTMLEditorKit()); // NOI18N
        infoPanel.setContentType("text/html;charset=UTF-8"); // NOI18N
        classpathList.addListSelectionListener(new ListSelectionListener() {
            @SuppressWarnings("synthetic-access")
			public void valueChanged(@SuppressWarnings("unused")
			final ListSelectionEvent e) {
                final Object[] selectedValues = classpathList.getSelectedValues();
                removeButton.setEnabled(selectedValues != null  &&  selectedValues.length > 0);
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("synthetic-access")
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        classpathList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        infoPanel = new javax.swing.JTextPane();

        setName(NbBundle.getMessage(ClasspathPanel.class, "Title_CPPanel_Bootstrap_Libraries")); // NOI18N
        setPreferredSize(new java.awt.Dimension(600, 400));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setLabelFor(classpathList);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Classpath")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Classpath")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Classpath")); // NOI18N

        classpathList.setModel(classpathListModel);
        jScrollPane1.setViewportView(classpathList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Add")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(addButton, gridBagConstraints);
        addButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Add")); // NOI18N
        addButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Add")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Remove")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(removeButton, gridBagConstraints);
        removeButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Remove")); // NOI18N
        removeButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Remove")); // NOI18N

        jLabel2.setLabelFor(infoPanel);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Detected_APIs")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(jLabel2, gridBagConstraints);
        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Detected_APIs")); // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ClasspathPanel.class, "ACSD_CPPanel_Detected_APIs")); // NOI18N

        infoPanel.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        infoPanel.setEditable(false);
        jScrollPane2.setViewportView(infoPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 12);
        add(jScrollPane2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        final File[] values = browse(NbBundle.getMessage(ClasspathPanel.class, "TITLE_ClasspathPanel_SelectClasspath"));//NOI18N
        if (values == null)
            return;
        for (int i = 0; i < values.length; i++) {
            final String value = values[i].getAbsolutePath();
            if (! classpathListModel.contains(value))
                classpathListModel.addElement(value);
        }
        updateStatus();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        final Object[] values = classpathList.getSelectedValues();
        if (values != null)
            for (int i = 0; i < values.length; i++)
                classpathListModel.removeElement(values[i]);
        updateStatus();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList classpathList;
    private javax.swing.JTextPane infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
    
    public void setWizardPanel(final WizardPanel wizardPanel) {
        this.wizardPanel = wizardPanel;
    }
    
    public void readSettings(final WizardDescriptor wizardDescriptor) {
        final J2MEPlatform platform = (J2MEPlatform) wizardDescriptor.getProperty(DetectPanel.PLATFORM);
        final J2MEPlatform.Device previous = platform.getDevices()[0];
        final J2MEPlatform.J2MEProfile[] profiles = previous.getProfiles();
        if (profiles.length > 0) {
            final List<String> classpath = (List<String>) wizardDescriptor.getProperty(ClasspathPanel.PROP_CLASSPATH);
            stringsToModel(classpath);
        } else {
            final ArrayList<String> list = new ArrayList<String>();
            searchForClasspath(list, new File(platform.getHomePath()), 6);
            stringsToModel(list);
        }
    }
    
    public void storeSettings(final WizardDescriptor wizardDescriptor) {
        final J2MEPlatform platform = (J2MEPlatform) wizardDescriptor.getProperty(DetectPanel.PLATFORM);
        final J2MEPlatform.Device previous = platform.getDevices()[0];
        final J2MEPlatform.Device device = new J2MEPlatform.Device(previous.getName(), null, null, profiles, null);
        platform.setDevices(new J2MEPlatform.Device[] { device });
        wizardDescriptor.putProperty(ClasspathPanel.PROP_CLASSPATH, modelToStrings());
    }
    
    public JComponent getComponent() {
        return this;
    }
    
    public boolean isPanelValid() {
        boolean prof = false;
        boolean conf = false;
        
        for (int i = 0; i < profiles.length; i++) {
            final J2MEPlatform.J2MEProfile profile = profiles[i];
            if (J2MEPlatform.J2MEProfile.TYPE_PROFILE.equals(profile.getType()))
                prof = true;
            else if (J2MEPlatform.J2MEProfile.TYPE_CONFIGURATION.equals(profile.getType()))
                conf = true;
        }
        if (! prof)
            wizardPanel.setErrorMessage(ClasspathPanel.class, "ERR_NoProfile");//NOI18N
        else if (! conf)
            wizardPanel.setErrorMessage(ClasspathPanel.class, "ERR_NoConfiguration");//NOI18N
        else
            wizardPanel.setErrorMessage(ClasspathPanel.class, null);
        return prof && conf;
    }
    
    public boolean isFinishPanel() {
        return true;
    }
    
    private void updateStatus() {
        final J2MEPlatform platform = (J2MEPlatform) wizardPanel.getProperty(DetectPanel.PLATFORM);
        final String string = stringsToString(Arrays.asList(classpathListModel.toArray()));
        final List<J2MEPlatform.J2MEProfile> profilesList = UEIEmulatorConfiguratorImpl.analyzePath(new File(platform.getHomePath()), string, string);
        profiles = profilesList.toArray(new J2MEPlatform.J2MEProfile[profilesList.size()]);
        
        final StringBuffer recognizedProfiles = new StringBuffer();
        final StringBuffer recognizedConfigurations = new StringBuffer();
        final StringBuffer recognizedOptionals = new StringBuffer();
        for (int i = 0; i < profiles.length; i++) {
            final J2MEPlatform.J2MEProfile profile = profiles[i];
            if (profile.isNameIsJarFileName())
                continue;
            if (J2MEPlatform.J2MEProfile.TYPE_PROFILE.equals(profile.getType())) {
                if (recognizedProfiles.length() > 0)
                    recognizedProfiles.append(", "); //NOI18N
                recognizedProfiles.append(NbBundle.getMessage(ClasspathPanel.class, "MSG_OutputTextBold", profile.getName(), profile.getVersion()));//NOI18N
            } else if (J2MEPlatform.J2MEProfile.TYPE_CONFIGURATION.equals(profile.getType())) {
                if (recognizedConfigurations.length() > 0)
                    recognizedConfigurations.append(", ");//NOI18N
                recognizedConfigurations.append(NbBundle.getMessage(ClasspathPanel.class, "MSG_OutputTextBold", profile.getName(), profile.getVersion()));//NOI18N
            } else if (J2MEPlatform.J2MEProfile.TYPE_OPTIONAL.equals(profile.getType())) {
                if (recognizedOptionals.length() > 0)
                    recognizedOptionals.append(", ");//NOI18N
                recognizedOptionals.append(NbBundle.getMessage(ClasspathPanel.class, "MSG_OutputText", profile.getName(), profile.getVersion()));//NOI18N
            }
        }
        
        final StringBuffer sb = new StringBuffer(200);
        sb.append("<html><font face=\"dialog\" size=\"-1\">");//NOI18N
        boolean shown = false;
        if (recognizedProfiles.length() > 0) {
            sb.append(NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Profiles")).append(recognizedProfiles).append("<br>");//NOI18N
            shown = true;
        }
        if (recognizedConfigurations.length() > 0) {
            sb.append(NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanel_Configurations")).append(recognizedConfigurations).append("<br>");//NOI18N
            shown = true;
        }
        if (recognizedOptionals.length() > 0) {
            sb.append(NbBundle.getMessage(ClasspathPanel.class, "LBL_CPPanle_Optional_APIs")).append(recognizedOptionals).append("<br>");//NOI18N
            shown = true;
        }
        sb.append(shown ? "<font size=\"-2\">"+NbBundle.getMessage(ClasspathPanel.class, "MSG_CPPanel_Recognized_Packages_Shown_Only") : NbBundle.getMessage(ClasspathPanel.class, "MSG_CPPanel_Details_Not_Available"));//NOI18N
        infoPanel.setText(sb.toString());
        infoPanel.setCaretPosition(0);
        
        wizardPanel.fireChanged();
    }
    
    private void stringsToModel(final List<String> strings) {
        classpathListModel.clear();
        for (String str : strings )
            classpathListModel.addElement(str);
        updateStatus();
    }
    
    private List<String> modelToStrings() {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < classpathListModel.size(); i ++)
            list.add((String)classpathListModel.get(i));
        return list;
    }
    
    private String stringsToString(final List<Object> list) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0)
                sb.append(", ");//NOI18N
            sb.append(list.get(i));
        }
        return sb.toString();
    }
    
    private void searchForClasspath(final ArrayList<String> list, final File directory, final int level) {
        final File[] files = directory.listFiles();
        if (files != null && files.length < 40 && list.size() < 100)
            for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            final String name = file.getName().toLowerCase();
            if (level >0 && file.isDirectory()) {
                searchForClasspath(list, file, level-1);
            } else if (file.isFile()) {
                if (name.endsWith(".jar")  ||  name.endsWith(".zip"))//NOI18N
                    list.add(file.getAbsolutePath());
            }
            }
    }
    
    private File[] browse(final String title) {
        return new FileChooserBuilder(ClasspathPanel.class).setTitle(title).
                setFileFilter(new ArchiveFilter()).showMultiOpenDialog();
    }
}
