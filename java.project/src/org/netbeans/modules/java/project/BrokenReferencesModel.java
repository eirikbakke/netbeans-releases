/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.java.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.LinkedHashSet;
import java.util.Map;

import java.util.Set;
import javax.swing.AbstractListModel;

import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.util.NbBundle;

public class BrokenReferencesModel extends AbstractListModel {

    private String[] props;
    private String[] platformsProps;
    private String[] brokenReferences;
    private String[] brokenPlatforms;
    private AntProjectHelper helper;
    private ReferenceHelper resolver;

    public BrokenReferencesModel(AntProjectHelper helper, 
            ReferenceHelper resolver, String[] props, String[] platformsProps) {
        this.props = props;
        this.platformsProps = platformsProps;
        this.resolver = resolver;
        this.helper = helper;
        refresh();
    }
    
    public void refresh() {
        brokenReferences = getReferences(helper.getStandardPropertyEvaluator(), props, false);
        brokenPlatforms = getPlatforms(helper.getStandardPropertyEvaluator(), platformsProps, false);
        this.fireContentsChanged(this, 0, getSize());
    }

    public Object getElementAt(int index) {
        if (index < brokenReferences.length) {
            if (isLibrary(index)) {
                return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenLibrary", getLibraryID(index));
            } else {
                if (isProjectReference(index)) {
                    return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenProjectReference", getProjectID(index));
                } else {
                    return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenFileReference", getFileID(index));
                }
            }
        } else {
            return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenPlatform", brokenPlatforms[index-brokenReferences.length]);
        }
    }

    public String getDesciption(int index) {
        if (index < brokenReferences.length) {
            if (isLibrary(index)) {
                // XXX: if library classpath is defined then just WARN user 
                // that library is not defined
                return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenLibraryDesc", getLibraryID(index));
            } else {
                if (isProjectReference(index)) {
                    return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenProjectReferenceDesc", getProjectID(index));
                } else {
                    return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenFileReferenceDesc", getFileID(index));
                }
            }
        } else {
            return NbBundle.getMessage(BrokenReferencesCustomizer.class, "LBL_BrokenLinksCustomizer_BrokenPlatformDesc", brokenPlatforms[index-brokenReferences.length]);
        }
    }

    public boolean isBroken(int index) {
        // XXX: at the moment shows only broken references
        return true;
    }
    
    boolean isLibrary(int index) {
        return index < brokenReferences.length ? brokenReferences[index].startsWith("libs.") : false;
    }
    
    private String getLibraryID(int index) {
        // libs.<name>.classpath
        return brokenReferences[index].substring(5, brokenReferences[index].length()-10);
    }

    boolean isPlatform(int index) {
        return index >= brokenReferences.length && index - brokenReferences.length < brokenPlatforms.length;
    }

    boolean isProjectReference(int index) {
        return brokenReferences[index].startsWith("project.");
    }

    String getProjectID(int index) {
        // project.<name>
        return brokenReferences[index].substring(8);
    }

    String getFileID(int index) {
        // file.reference.<name>
        return brokenReferences[index].substring(15);
    }

    public int getSize() {
        return brokenReferences.length + brokenPlatforms.length;
    }

    public static boolean isBroken(PropertyEvaluator evaluator, String[] props, String[] platformsProps) {
        String[] broken = getReferences(evaluator, props, true);
        if (broken.length > 0) {
            return true;
        }
        broken = getPlatforms(evaluator, platformsProps, true);
        return broken.length > 0;
    }

    private static String[] getReferences(PropertyEvaluator evaluator, String[] ps, boolean abortAfterFirstProblem) {
        Set set = new LinkedHashSet();
        StringBuffer all = new StringBuffer();
        for (int i=0; i<ps.length; i++) {
            // evaluate given property and tokenize it
            
            String prop = evaluator.getProperty(ps[i]);
            if (prop == null) {
                continue;
            }
            String[] vals = PropertyUtils.tokenizePath(prop);
            
            // XXX: perhaps I could check here also that correctly resolved
            // path point to an existing file? For foreign file references it
            // make sence.
            
            // no check whether after evaluating there are still some 
            // references which could not be evaluated
            for (int j=0; j<vals.length; j++) {
                // we are checking only: project reference, file reference, library reference
                if (!(vals[j].startsWith("${file.reference.") || vals[j].startsWith("${project.") || vals[j].startsWith("${libs."))) {
                    all.append(vals[j]);
                    continue;
                }
                if (vals[j].startsWith("${project.")) {
                    // something in the form: "${project.<projID>}/dist/foo.jar"
                    String val = vals[j].substring(2, vals[j].indexOf('}'));
                    set.add(val);
                } else {
                    set.add(vals[j].substring(2, vals[j].length()-1));
                }
                if (abortAfterFirstProblem) {
                    break;
                }
            }
            if (set.size() > 0 && abortAfterFirstProblem) {
                break;
            }
        }
        
        // Check also that all referenced project really exist and are reachable.
        // If they are not report them as broken reference.
        // XXX: there will be API in PropertyUtils for listing of Ant 
        // prop names in String. Consider using it here.
        Iterator it = evaluator.getProperties().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (key.startsWith("project.")) { // NOI18N
                File f = new File(value);
                if (f.exists()) {
                    continue;
                }
                // Check that the value is really used by some property.
                // If it is not then ignore such a project.
                if (all.indexOf(value) == -1) {
                    continue;
                }
                set.add(key);
            }
        }
        
        
        return (String[])set.toArray(new String[set.size()]);
    }

    private static String[] getPlatforms(PropertyEvaluator evaluator, String[] platformsProps, boolean abortAfterFirstProblem) {
        Set set = new LinkedHashSet();
        for (int i=0; i<platformsProps.length; i++) {
            String prop = evaluator.getProperty(platformsProps[i]);
            if (prop == null) {
                continue;
            }
            if (!existPlatform(prop)) {
                
                // XXX: the J2ME stores in project.properties also platform 
                // display name and so show this display name instead of just
                // prop ID if available.
                if (evaluator.getProperty(platformsProps[i]+".description") != null) {
                    prop = evaluator.getProperty(platformsProps[i]+".description");
                }
                
                set.add(prop);
            }
            if (set.size() > 0 && abortAfterFirstProblem) {
                break;
            }
        }
        return (String[])set.toArray(new String[set.size()]);
    }
    
    private static boolean existPlatform(String platform) {
        if (platform.equals("default_platform")) { // NOI18N
            return true;
        }
        JavaPlatform plats[] = JavaPlatformManager.getDefault().getInstalledPlatforms();
        for (int i=0; i<plats.length; i++) {
            // XXX: this should be defined as PROPERTY somewhere
            if (platform.equals(plats[i].getProperties().get("platform.ant.name"))) { // NOI18N
                return true;
            }
        }
        return false;
    }

    // XXX: perhaps could be moved to ReferenceResolver. 
    // But nobody should need it so it is here for now.
    void updateReference(int index, File file) {
        final String reference = brokenReferences[index];
        FileObject myProjDirFO = helper.getProjectDirectory();
        File myProjDir = FileUtil.toFile(myProjDirFO);
        final String propertiesFile;
        final String path;
        if (CollocationQuery.areCollocated(myProjDir, file)) {
            propertiesFile = AntProjectHelper.PROJECT_PROPERTIES_PATH;
            path = PropertyUtils.relativizeFile(myProjDir, file);
            assert path != null : "expected relative path from " + myProjDir + " to " + file; // NOI18N
        } else {
            propertiesFile = AntProjectHelper.PRIVATE_PROPERTIES_PATH;
            path = file.getAbsolutePath();
        }
        Project p;
        try {
            p = ProjectManager.getDefault().findProject(myProjDirFO);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            p = null;
        }
        final Project proj = p;
        ProjectManager.mutex().postWriteRequest(new Runnable() {
                public void run() {
                    EditableProperties props = helper.getProperties(propertiesFile);
                    if (!path.equals(props.getProperty(reference))) {
                        props.setProperty(reference, path);
                        helper.putProperties(propertiesFile, props);
                    }
                    if (proj != null) {
                        try {
                            ProjectManager.getDefault().saveProject(proj);
                        } catch (IOException ex) {
                            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                        }
                    }
                }
            });
    }
    
}
