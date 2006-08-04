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

/*
 * ApplicationDescriptorHandler.java
 *
 * Created on 2. prosinec 2004, 12:56
 */
package org.netbeans.modules.mobility.project;

import java.io.IOException;
import java.util.*;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.configurations.ProjectConfiguration;
import org.netbeans.modules.mobility.project.ui.customizer.J2MEProjectProperties;
import org.netbeans.spi.mobility.project.support.DefaultPropertyParsers;
import org.netbeans.modules.mobility.project.ProjectConfigurationsHelper;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Adam Sotona
 */
public class ApplicationDescriptorHandler {
    
    private static ApplicationDescriptorHandler handler;
    
    public static synchronized ApplicationDescriptorHandler getDefault() {
        if (handler == null) handler = new ApplicationDescriptorHandler();
        return handler;
    }
    
    /** Creates a new instance of ApplicationDescriptorHandler */
    private ApplicationDescriptorHandler() {
        /* Create just as a singleton */
    }
    
    public void handleRename(final FileObject file, final String newName) {
        if (!isJava(file)) return;
        final Project p = findProject(file);
        final String className = calculateClassName(getSrcRoot(p), file);
        if (className != null) postChangeRequest(p, className, className.substring(0, className.length() - file.getName().length()) + newName);
    }
    
    public void handleMove(final FileObject file, final FileObject newFolder) {
        if (!isJava(file)) return;
        final Project p = findProject(file);
        final FileObject srcRoot = getSrcRoot(p);
        final String className = calculateClassName(getSrcRoot(p), file);
        if (className == null) return;
        String newClassName = newFolder == null ? null : FileUtil.getRelativePath(srcRoot, newFolder);
        if (newClassName != null) {
            if (newClassName.length() > 0) newClassName = newClassName.replace('/', '.') + '.';
            newClassName += file.getName();
        }
        postChangeRequest(p, className, newClassName);
    }
    
    public void handleDelete(final FileObject file) {
        if (!isJava(file)) return;
        final Project p = findProject(file);
        final String className = calculateClassName(getSrcRoot(p), file);
        if (className != null) postChangeRequest(p, className, null);
    }
    
    private boolean isJava(final FileObject file) {
        return file != null && file.getExt().equals("java"); //NOI18N
    }
    
    private Project findProject(final FileObject file) {
        final Project p = FileOwnerQuery.getOwner(file);
        return p instanceof J2MEProject ? p : null;
    }
    
    private FileObject getSrcRoot(final Project p) {
        if (p == null) return null;
        final AntProjectHelper helper = p.getLookup().lookup(AntProjectHelper.class);
        if (helper == null) return null;
        final String srcDir = helper.getStandardPropertyEvaluator().getProperty("src.dir"); //NOI18N
        return srcDir == null ? null : helper.resolveFileObject(srcDir);
    }
    
    private String calculateClassName(final FileObject root, final FileObject file) {
        String path;
        return root == null || file == null || (path = FileUtil.getRelativePath(root, file)) == null ? null : path.substring(0, path.length()-5).replace('/', '.');
    }
    
    private void postChangeRequest(final Project project, final String oldClassName, final String newClassName) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                handleChangeRequest(project, oldClassName, newClassName);
            }
        });
    }
    
    protected void handleChangeRequest(final Project project, final String oldClassName, final String newClassName) {
        final AntProjectHelper helper = project.getLookup().lookup(AntProjectHelper.class);
        final ProjectConfigurationsHelper pch = project.getLookup().lookup(ProjectConfigurationsHelper.class);
        if (helper == null || pch == null) return;
        final EditableProperties props = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        final ProjectConfiguration cfg[] = pch.getConfigurations();
        boolean modifiedProj = false;
        final HashMap<String,String> newMidlets = new HashMap<String,String>();
        for (int i=0; i<cfg.length; i++) {
            boolean modifiedProp = false;
            final String propName = pch.getDefaultConfiguration().equals(cfg[i]) ? DefaultPropertiesDescriptor.MANIFEST_MIDLETS : J2MEProjectProperties.CONFIG_PREFIX + cfg[i].getName() + '.' + DefaultPropertiesDescriptor.MANIFEST_MIDLETS;
            final String value = props.getProperty(propName);
            if (value != null) {
                final HashMap<String,String> midlets = (HashMap<String,String>)DefaultPropertyParsers.MANIFEST_PROPERTY_PARSER.decode(value, null, null);
                if (midlets != null) {
                    int m2 = 1;
                    newMidlets.clear();
                    for(int m=1; !midlets.isEmpty() && m<10000; m++) {
                        final String record = midlets.remove("MIDlet-" + String.valueOf(m));
                        if (record != null) {
                            final String s[] = record.split(",", -1); //NOI18N
                            if (s.length == 3 && s[2].trim().equals(oldClassName)) {//.equals(s[2].trim())) {
                                modifiedProp = true;
                                if (newClassName != null) newMidlets.put("MIDlet-" + String.valueOf(m2++), s[0] + ',' + s[1] + ',' + newClassName); //NOI18N
                            } else {
                                newMidlets.put("MIDlet-" + String.valueOf(m2++), record); //NOI18N
                            }
                        }
                    }
                    if (modifiedProp) {
                        props.setProperty(propName, DefaultPropertyParsers.MANIFEST_PROPERTY_PARSER.encode(newMidlets, null, null));
                        modifiedProj = true;
                    }
                }
            }
        }
        if (modifiedProj) try {
            helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
            ProjectManager.getDefault().saveProject(project);
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
        }
    }
}
