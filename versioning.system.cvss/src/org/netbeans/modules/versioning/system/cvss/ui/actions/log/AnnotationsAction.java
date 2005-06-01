/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.versioning.system.cvss.ui.actions.log;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.netbeans.modules.versioning.system.cvss.ui.actions.AbstractSystemAction;
import org.netbeans.modules.versioning.system.cvss.ui.actions.annotate.AnnotationBarManager;
import org.netbeans.modules.versioning.system.cvss.FileInformation;
import org.netbeans.modules.versioning.system.cvss.CvsVersioningSystem;
import org.netbeans.lib.cvsclient.command.annotate.AnnotateCommand;
import org.netbeans.lib.cvsclient.command.log.LogCommand;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.AdminHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Performs the CVS 'tag -b' command on selected nodes.
 * 
 * @author Maros Sandor
 */
public class AnnotationsAction extends AbstractSystemAction {

    protected String getBaseName() {
        return "CTL_MenuItem_Annotations";
    }

    public boolean isEnabled() {
        return super.isEnabled() && getFilesToProcess().length == 1;
    }

    protected int getFileEnabledStatus() {
        return FileInformation.STATUS_IN_REPOSITORY;
    }

    protected int getDirectoryEnabledStatus() {
        return 0;
    }

    public void actionPerformed(ActionEvent ev) {
        File [] files = getFilesToProcess();
        CvsVersioningSystem cvss = CvsVersioningSystem.getInstance();
        AdminHandler entries = cvss.getAdminHandler();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FileObject fo = FileUtil.toFileObject(file);
            try {
                DataObject dobj = DataObject.find(fo);
                EditorCookie ec = (EditorCookie) dobj.getCookie(EditorCookie.class);
                if (ec != null) {
                    JEditorPane[] panes = ec.getOpenedPanes();
                    if (panes == null) {
                        ec.open();
                    }
                    panes = ec.getOpenedPanes();
                    if (panes == null) {
                        return;
                    }
                    JEditorPane currentPane = panes[0];
                    LogOutputListener ab = AnnotationBarManager.showAnnotationBar(currentPane);

                    AnnotateCommand annotate = new AnnotateCommand();

                    try {
                        Entry entry = entries.getEntry(file);
                        if (entry == null) {
                            continue;
                        }
                        String revision = entry.getRevision();
                        annotate.setAnnotateByRevision(revision);
                        File[] cmdFiles = new File[] {file};
                        annotate.setFiles(cmdFiles);

                        AnnotationsExecutor executor = new AnnotationsExecutor(cvss, annotate);
                        executor.addLogOutputListener(ab);
                        executor.execute();

                        // get commit message sfrom log  

                        LogCommand log = new LogCommand();
                        log.setFiles(cmdFiles);
                        log.setNoTags(true);

                        LogExecutor lexecutor = new LogExecutor(cvss, log);
                        lexecutor.addLogOutputListener(ab);
                        lexecutor.execute();


                    } catch (IOException e) {
                        ErrorManager err = ErrorManager.getDefault();
                        err.annotate(e, "Can not load revision of " + file);
                        err.notify(e);
                    }
                }
            } catch (DataObjectNotFoundException e) {
                ErrorManager err = ErrorManager.getDefault();
                err.annotate(e, "Can not locate editor for " + fo);
                err.notify(e);
            }
        }
    }
}
