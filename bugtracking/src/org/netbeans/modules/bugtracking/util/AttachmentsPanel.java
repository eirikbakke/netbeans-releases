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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.bugtracking.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.bugtracking.BugtrackingManager;
import org.netbeans.modules.team.ide.spi.IDEServices;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Jan Stola
 * // XXX merge with bugzilla and jira
 */
public class AttachmentsPanel extends JPanel {
    private static final Logger LOG = Logger.getLogger(AttachmentsPanel.class.getName());
    private boolean hadNoAttachments = true;
    private List<AttachmentPanel> newAttachments;
    private JLabel noneLabel;
    private LinkButton createNewButton;
    private LinkButton attachLogFileButton;
    private JLabel dummyCreateLabel = new JLabel();
    private JLabel dummyAttachLabel = new JLabel();
    private Method maxMethod;
    private JComponent parentPanel;
    
    public interface NBBugzillaCallback {
        public String getLogFilePath();
        public String getLogFileContentType();
        public String getLogFileDescription();
        public void showLogFile();
    }
    
    public AttachmentsPanel(JComponent parentPanel) {
        this.parentPanel = parentPanel;
        setBackground(UIManager.getColor("TextArea.background")); // NOI18N
        ResourceBundle bundle = NbBundle.getBundle(AttachmentsPanel.class);
        noneLabel = new JLabel(bundle.getString("AttachmentsPanel.noneLabel.text")); // NOI18N
        createNewButton = new LinkButton(new CreateNewAction());
        createNewButton.getAccessibleContext().setAccessibleDescription(bundle.getString("AttachmentPanels.createNewButton.AccessibleContext.accessibleDescription")); // NOI18N
        try {
            maxMethod = GroupLayout.Group.class.getDeclaredMethod("calculateMaximumSize", int.class); // NOI18N
            maxMethod.setAccessible(true);
        } catch (NoSuchMethodException nsmex) {
            LOG.log(Level.INFO, nsmex.getMessage(), nsmex);
        }
    }

    public void setAttachments(List<? extends Attachment> attachments) {
        setAttachments(attachments, null);
    }
    
    public void setAttachments(List<? extends Attachment> attachments, NBBugzillaCallback nbCallback) {
        
        if(nbCallback != null) {
            attachLogFileButton = new LinkButton(new CreateNewAction(nbCallback));
            attachLogFileButton.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AttachmentsPanel.class).getString("AttachmentPanels.attachLogFileButton.AccessibleContext.accessibleDescription")); // NOI18N            
        }
        
        hadNoAttachments = attachments.isEmpty();
        newAttachments = new LinkedList<AttachmentPanel>();
        removeAll();

        GroupLayout layout = new GroupLayout(this);
        GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
        ResourceBundle bundle = NbBundle.getBundle(AttachmentsPanel.class);
        GroupLayout.SequentialGroup newVerticalGroup = layout.createSequentialGroup();

        boolean noAttachments = hadNoAttachments;
        
        if(attachLogFileButton != null) {
            SequentialGroup sg = layout.createSequentialGroup();
            sg.addComponent(noneLabel)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(noAttachments ? createNewButton : dummyCreateLabel)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(noAttachments ? attachLogFileButton : dummyAttachLabel);
            horizontalGroup.addGroup(sg);
        } else {
            horizontalGroup.addGroup(layout.createSequentialGroup()
            .addComponent(noneLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(noAttachments ? createNewButton : dummyCreateLabel));
        }
        
        if(attachLogFileButton != null) {
            ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            pg.addComponent(noneLabel)
              .addComponent(noAttachments ? createNewButton : dummyCreateLabel)
              .addComponent(noAttachments ? attachLogFileButton : dummyAttachLabel);
            verticalGroup.addGroup(pg);
        } else {
            verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(noneLabel)
                .addComponent(noAttachments ? createNewButton : dummyCreateLabel));
        }       
        
        dummyCreateLabel.setVisible(false);
        dummyAttachLabel.setVisible(false);
        noneLabel.setVisible(noAttachments);
        updateButtonText(noAttachments);
        if (noAttachments) {
            // noneLabel + createNewButton
            verticalGroup.addGroup(newVerticalGroup);
        } else {
            List<JPanel> panels = new ArrayList<JPanel>();
            JLabel descriptionLabel = new JLabel(bundle.getString("AttachmentsPanel.table.description")); // NOI18N
            JLabel filenameLabel = new JLabel(bundle.getString("AttachmentsPanel.table.filename")); // NOI18N
            JLabel dateLabel =  new JLabel(bundle.getString("AttachmentsPanel.table.date")); // NOI18N
            JLabel authorLabel = new JLabel(bundle.getString("AttachmentsPanel.table.author")); // NOI18N
            makeBold(descriptionLabel);
            makeBold(filenameLabel);
            makeBold(dateLabel);
            makeBold(authorLabel);
            GroupLayout.ParallelGroup descriptionGroup = layout.createParallelGroup();
            GroupLayout.ParallelGroup filenameGroup = layout.createParallelGroup();
            GroupLayout.ParallelGroup dateGroup = layout.createParallelGroup();
            GroupLayout.ParallelGroup authorGroup = layout.createParallelGroup();
            int descriptionWidth = Math.max(descriptionLabel.getPreferredSize().width, 150);
            descriptionGroup.addComponent(descriptionLabel, descriptionWidth, descriptionWidth, descriptionWidth);
            filenameGroup.addComponent(filenameLabel);
            dateGroup.addComponent(dateLabel);
            authorGroup.addComponent(authorLabel);
            JPanel panel = createHighlightPanel();
            panels.add(panel);
            horizontalGroup.addGroup(layout.createSequentialGroup()
                    .addGroup(descriptionGroup)
                    .addPreferredGap(descriptionLabel, filenameLabel, LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(filenameGroup)
                    .addPreferredGap(filenameLabel, dateLabel, LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(dateGroup)
                    .addPreferredGap(dateLabel, authorLabel, LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(authorGroup));
            verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(panel, 0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionLabel)
                    .addComponent(filenameLabel)
                    .addComponent(dateLabel)
                    .addComponent(authorLabel)));
            for (Attachment attachment : attachments) {
                boolean isPatch = attachment.isPatch() && hasPatchUtils(); 
                String description = attachment.getDesc();
                String filename = attachment.getFilename();
                Date date = attachment.getDate();
                String author = attachment.getAuthor();
                String authorName = attachment.getAuthorName();
                authorName = ((authorName != null) && (authorName.trim().length() > 0)) ? authorName : author;
                descriptionLabel = new JLabel(description);
                LinkButton filenameButton = new LinkButton();
                LinkButton patchButton = null;
                JLabel lBrace = null;
                JLabel rBrace = null;
                GroupLayout.SequentialGroup hPatchGroup = null;
                if (isPatch) {
                    patchButton = new LinkButton();
                    lBrace = new JLabel("("); // NOI18N
                    rBrace = new JLabel(")"); // NOI18N
                    hPatchGroup = layout.createSequentialGroup()
                            .addComponent(filenameButton)
                            .addPreferredGap(filenameButton, lBrace, LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lBrace)
                            .addComponent(patchButton)
                            .addComponent(rBrace);
                }
                JPopupMenu menu = menuFor(attachment, patchButton);
                filenameButton.setAction(attachment.getOpenAction());
                filenameButton.setText(filename);
                filenameButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AttachmentsPanel.class, "AttachmentPanels.filenameButton.AccessibleContext.accessibleDescription")); // NOI18N
                dateLabel = new JLabel(date != null ? DateFormat.getDateInstance().format(date) : ""); // NOI18N
                
                JComponent authorComponent;
                if(author.indexOf("@") > -1) { // NOI18N
                    authorComponent = new LinkButton.MailtoButton(authorName, NbBundle.getMessage(AttachmentsPanel.class, "AttachmentPanel.authorButton.AccessibleContext.accessibleDescription"), author); // NOI18N
                } else {
                    authorComponent = new JLabel(authorName);
                }
                descriptionLabel.setComponentPopupMenu(menu);
                filenameButton.setComponentPopupMenu(menu);
                dateLabel.setComponentPopupMenu(menu);
                authorComponent.setComponentPopupMenu(menu);
                descriptionGroup.addComponent(descriptionLabel, 0, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
                if (isPatch) {
                    lBrace.setComponentPopupMenu(menu);
                    patchButton.setComponentPopupMenu(menu);
                    rBrace.setComponentPopupMenu(menu);
                    filenameGroup.addGroup(hPatchGroup);
                } else {
                    filenameGroup.addComponent(filenameButton);
                }
                dateGroup.addComponent(dateLabel);
                authorGroup.addComponent(authorComponent);
                panel = createHighlightPanel();
                panel.addMouseListener(new MouseAdapter() {}); // Workaround for bug 6272233
                panel.setComponentPopupMenu(menu);
                panels.add(panel);
                GroupLayout.ParallelGroup pGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
                pGroup.addComponent(descriptionLabel);
                pGroup.addComponent(filenameButton);
                if (isPatch) {
                    pGroup.addComponent(lBrace);
                    pGroup.addComponent(patchButton);
                    pGroup.addComponent(rBrace);
                }
                pGroup.addComponent(dateLabel);
                pGroup.addComponent(authorComponent);
                verticalGroup
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(panel, 0, 0, Short.MAX_VALUE)
                        .addGroup(pGroup));
            }
            verticalGroup.addGroup(newVerticalGroup);
            int groupWidth = 0;
            if (maxMethod != null) {
                try {
                    groupWidth = (Integer)maxMethod.invoke(horizontalGroup, 0);
                } catch (Exception ex) {
                    LOG.log(Level.INFO, ex.getMessage(), ex);
                }
            }
            for (JPanel p : panels) {
                horizontalGroup.addComponent(p, 0, 0, groupWidth);
            }
        }
        
        if(attachLogFileButton != null) {
            SequentialGroup sg = layout.createSequentialGroup();
            sg.addComponent(noAttachments ? dummyCreateLabel : createNewButton)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(noAttachments ? dummyAttachLabel : attachLogFileButton)
              .addGap(0, 0, Short.MAX_VALUE);
            horizontalGroup.addGroup(sg);
        } else {
             horizontalGroup.addGroup(layout.createSequentialGroup()
                .addComponent(noAttachments ? dummyCreateLabel : createNewButton)
                .addGap(0, 0, Short.MAX_VALUE));
        }        
        
        if(attachLogFileButton != null) {
            ParallelGroup pg = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            pg.addComponent(noAttachments ? dummyCreateLabel : createNewButton)
              .addComponent(noAttachments ? dummyAttachLabel : attachLogFileButton);
            verticalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            verticalGroup.addGroup(pg);
        } else {
            verticalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            verticalGroup.addComponent(noAttachments ? dummyCreateLabel : createNewButton);
        }
        
        layout.setHorizontalGroup(horizontalGroup);
        layout.setVerticalGroup(verticalGroup);
        
        ((CreateNewAction)createNewButton.getAction()).setLayoutGroups(horizontalGroup, newVerticalGroup);
        if(attachLogFileButton != null) {
            ((CreateNewAction)attachLogFileButton.getAction()).setLayoutGroups(horizontalGroup, newVerticalGroup);
        }
        
        setLayout(layout);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, super.getMinimumSize().height);
    }

    private JPopupMenu menuFor(Attachment attachment, LinkButton patchButton) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(attachment.getOpenAction());
        menu.add(attachment.getSaveAction());
        if (attachment.isPatch() && hasPatchUtils()) {
            Action action = attachment.getApplyPatchAction();
            menu.add(action);
            patchButton.setAction(action);
            // Lower the first letter
            String label = patchButton.getText();
            patchButton.setText(label.substring(0,1).toLowerCase()+label.substring(1));
            patchButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AttachmentsPanel.class, "AttachmentPanels.patchButton.AccessibleContext.accessibleDescription")); // NOI18N
        }
        return menu;
    }

    private void updateButtonText(boolean noAttachments) {
        String txt = NbBundle.getMessage(AttachmentsPanel.class, "AttachmentsPanel.createNewButton.text"); // NOI18N
        createNewButton.setText(noAttachments ? ('('+txt+')') : txt); // NOI18N
        
        if(attachLogFileButton != null) {
            txt = NbBundle.getMessage(AttachmentsPanel.class, "AttachmentsPanel.attachLogFileButton.text"); // NOI18N
            attachLogFileButton.setText(noAttachments ? ('('+txt+')') : txt); // NOI18N
        }
    }
    
    private void makeBold(JLabel label) {
        Font font = label.getFont().deriveFont(Font.BOLD);
        label.setFont(font);
    }

    private JPanel createHighlightPanel() {
        JPanel panel = new JPanel();
        // PENDING what color (e.g. what key from UIDefaults) should I use?
        panel.setBackground(UIUtils.getSectionPanelBackground());
        add(panel);
        return panel;
    }

    private PropertyChangeListener deletedListener;
    PropertyChangeListener getDeletedListener() {
        if (deletedListener == null) {
            deletedListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (AttachmentPanel.PROP_DELETED.equals(evt.getPropertyName())) {
                        for (AttachmentPanel panel : newAttachments) {
                            if (!panel.isDeleted()) {
                                return;
                            }
                        }
                        // The last attachment deleted
                        noneLabel.setVisible(true);
                        switchHelper();
                        updateButtonText(true);
                    }
                }
            };
        }
        return deletedListener;
    }

    private void switchHelper() {
        JLabel temp = new JLabel();
        GroupLayout layout = (GroupLayout)getLayout();
        layout.replace(dummyCreateLabel, temp);
        layout.replace(createNewButton, dummyCreateLabel);
        layout.replace(temp, createNewButton);
        
        if(attachLogFileButton != null) {
            layout.replace(dummyAttachLabel, temp);
            layout.replace(attachLogFileButton, dummyAttachLabel);
            layout.replace(temp, attachLogFileButton);
        }
    }

    @NbBundle.Messages("IssuePanel.attachment.noDescription=<no description>")
    public List<AttachmentInfo> getNewAttachments() {
        List<AttachmentInfo> infos = new ArrayList<AttachmentInfo>(newAttachments.size());
        for (AttachmentPanel attachment : newAttachments) {
            if (!attachment.isDeleted()) {
                AttachmentInfo info = new AttachmentInfo();
                info.file = attachment.getFile();
                info.description = attachment.getDescription();
                info.contentType = attachment.getContentType();
                info.isPatch = attachment.isPatch();
                if (info.description.trim().isEmpty()) {
                    info.description = Bundle.IssuePanel_attachment_noDescription();
                }
                infos.add(info);
            }
        }
        return infos;
    }

    public final class AttachmentInfo {
        private File file;
        private String description;
        private String contentType;
        private boolean isPatch;

        public String getContentType () {
            return contentType;
        }

        public String getDescription () {
            return description;
        }

        public File getFile () {
            return file;
        }

        public boolean isPatch () {
            return isPatch;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    class CreateNewAction extends AbstractAction {
        private final NBBugzillaCallback nbCallback;

        public CreateNewAction() {
            this(null);
        }
        
        public CreateNewAction(NBBugzillaCallback nbCallback) {
            this.nbCallback = nbCallback;
        }
        
        private GroupLayout.ParallelGroup horizontalGroup;
        private GroupLayout.SequentialGroup verticalGroup;

        void setLayoutGroups(GroupLayout.ParallelGroup horizontalGroup,
                GroupLayout.SequentialGroup verticalGroup) {
            this.horizontalGroup = horizontalGroup;
            this.verticalGroup = verticalGroup;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AttachmentPanel attachment = new AttachmentPanel(nbCallback);
            attachment.setBackground(UIUtils.getSectionPanelBackground());
            horizontalGroup.addComponent(attachment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            verticalGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            verticalGroup.addComponent(attachment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
            if (noneLabel.isVisible()) {
                noneLabel.setVisible(false);
                switchHelper();
                updateButtonText(false);
            }
            if (hadNoAttachments) {
                attachment.addPropertyChangeListener(getDeletedListener());
            }
            
            if(nbCallback != null) {
                File f = new File(Places.getUserDirectory(), nbCallback.getLogFilePath()); 
                if(f.exists()) {
                    attachment.setAttachment(f, nbCallback.getLogFileDescription(), nbCallback.getLogFileContentType()); // NOI18N
                }
                attachment.browseButton.setEnabled(false);
                attachment.fileField.setEnabled(false);
                attachment.fileTypeCombo.setEnabled(false);
                attachment.patchChoice.setEnabled(false);
            } else {
                attachment.viewButton.setVisible(false);
            }

            newAttachments.add(attachment);
            UIUtils.keepFocusedComponentVisible(attachment, parentPanel);
            revalidate();
        }

    }
    
    private static boolean hasPatchUtils() {
        IDEServices ideServices = BugtrackingManager.getInstance().getIDEServices();
        return ideServices != null && ideServices.providesPatchUtils();
    }

    public static interface Attachment {

        public boolean isPatch ();

        public Action getOpenAction ();

        public Action getApplyPatchAction ();

        public Action getSaveAction ();

        public String getDesc ();

        public String getFilename ();

        public Date getDate ();

        public String getAuthor ();

        public String getAuthorName ();

    }

    public static abstract class AbstractAttachment implements Attachment {
        private OpenAttachmentAction openAttachmentAction;
        private SaveAttachmentAction saveAttachmentAction;
        private ApplyPatchAction applyPatchAction;
        @Override
        public Action getOpenAction () {
            if (openAttachmentAction == null) {
                openAttachmentAction = new OpenAttachmentAction();
            }
            return openAttachmentAction;
        }

        @Override
        public Action getApplyPatchAction () {
            if(hasPatchUtils()) {
                if (applyPatchAction == null) {
                    applyPatchAction = new ApplyPatchAction();
                }
                return applyPatchAction;
            } else {
                return null;
            }
        }

        @Override
        public Action getSaveAction () {
            if (saveAttachmentAction == null) {
                saveAttachmentAction = new SaveAttachmentAction();
            }
            return saveAttachmentAction;
        }

        protected abstract void getAttachementData (OutputStream os);
        
        protected abstract String getContentType ();

        public void open() {
            // XXX
            String progressFormat = NbBundle.getMessage(OpenAttachmentAction.class, "Attachment.open.progress");    //NOI18N
            String progressMessage = MessageFormat.format(progressFormat, getFilename());
            final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
            handle.start();
            handle.switchToIndeterminate();
            BugtrackingUtil.getParallelRP().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = saveToTempFile();
                        String contentType = getContentType();
                        if ("image/png".equals(contentType)             //NOI18N
                                || "image/gif".equals(contentType)      //NOI18N
                                || "image/jpeg".equals(contentType)) {  //NOI18N
                            HtmlBrowser.URLDisplayer.getDefault().showURL(Utilities.toURI(file).toURL());
                        } else {
                            file = FileUtil.normalizeFile(file);
                            FileObject fob = FileUtil.toFileObject(file);
                            DataObject dob = DataObject.find(fob);
                            OpenCookie open = dob.getCookie(OpenCookie.class);
                            if (open != null) {
                                open.open();
                            } else {
                                // PENDING
                            }
                        }
                    } catch (DataObjectNotFoundException dnfex) {
                        LOG.log(Level.INFO, dnfex.getMessage(), dnfex);
                    } catch (IOException ioex) {
                        LOG.log(Level.INFO, ioex.getMessage(), ioex);
                    } finally {
                        handle.finish();
                    }
                }
            });
        }

        private void saveToFile() {
            final File file = new FileChooserBuilder(AttachmentsPanel.class)
                    .setFilesOnly(true).showSaveDialog();
            if (file != null) {
                String progressFormat = NbBundle.getMessage(
                                            SaveAttachmentAction.class,
                                            "Attachment.saveToFile.progress"); //NOI18N
                String progressMessage = MessageFormat.format(progressFormat, getFilename());
                final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
                handle.start();
                handle.switchToIndeterminate();
                BugtrackingUtil.getParallelRP().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getAttachmentData(file);
                        } catch (IOException ioex) {
                            LOG.log(Level.INFO, ioex.getMessage(), ioex);
                        }
                    }
                });
            }
        }

        private void applyPatch() {
            String progressFormat = NbBundle.getMessage(AttachmentsPanel.class,"Attachment.applyPatch.progress"); //NOI18N
            String progressMessage = MessageFormat.format(progressFormat, getFilename());
            final ProgressHandle handle = ProgressHandleFactory.createHandle(progressMessage);
            handle.start();
            handle.switchToIndeterminate();
            BugtrackingUtil.getParallelRP().post(
                new Runnable() {
                    @Override
                    public void run() {
                        IDEServices ideServices = BugtrackingManager.getInstance().getIDEServices();
                        if(ideServices != null) {
                            try {
                                ideServices.applyPatch(saveToTempFile());
                            } catch (IOException ex) {
                                LOG.log(Level.WARNING, ex.getMessage(), ex);
                            } finally {
                                handle.finish();
                            }
                        }            
                    }
                });
        }

        private File saveToTempFile () throws IOException {
            String filename = getFilename();
            int index = filename.lastIndexOf('.'); // NOI18N
            String prefix = (index == -1) ? filename : filename.substring(0, index);
            String suffix = (index == -1) ? null : filename.substring(index);
            if (prefix.length()<3) {
                prefix = prefix + "tmp";                                //NOI18N
            }
            File file = File.createTempFile(prefix, suffix);
            getAttachmentData(file);
            return file;
        }

        private void getAttachmentData (File file) throws IOException {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                getAttachementData(fos);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        private class OpenAttachmentAction extends AbstractAction {

            public OpenAttachmentAction() {
                putValue(NAME, NbBundle.getMessage(
                                   OpenAttachmentAction.class,
                                   "Attachment.OpenAction.name"));   //NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        }

        private class SaveAttachmentAction extends AbstractAction {

            public SaveAttachmentAction() {
                putValue(NAME, NbBundle.getMessage(
                                   SaveAttachmentAction.class,
                                   "Attachment.SaveAction.name"));      //NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        }

        private class ApplyPatchAction extends AbstractAction {

            public ApplyPatchAction() {
                putValue(NAME, NbBundle.getMessage(
                                   ApplyPatchAction.class,
                                   "Attachment.ApplyPatchAction.name"));//NOI18N
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                applyPatch();
            }
        }
    }
}
