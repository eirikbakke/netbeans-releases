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

package org.netbeans.modules.web.project;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.api.web.dd.DDProvider;
import org.netbeans.api.web.dd.EjbLocalRef;
import org.netbeans.api.web.dd.EjbRef;
import org.netbeans.api.web.dd.MessageDestinationRef;
import org.netbeans.api.web.dd.ResourceRef;
import org.netbeans.api.web.dd.WebApp;
import org.netbeans.modules.j2ee.api.ejbjar.EnterpriseReferenceContainer;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.web.project.ui.customizer.VisualClassPathItem;
import org.netbeans.modules.web.project.ui.customizer.WebProjectProperties;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Chris Webster
 */
class WebContainerImpl extends EnterpriseReferenceContainer {
    private Project webProject;
    private ReferenceHelper helper;
    private AntProjectHelper antHelper;
    private static final String J2EE_14_LIBRARY = "j2ee14"; //NOI18N
    
    public WebContainerImpl(Project p, ReferenceHelper helper, AntProjectHelper antHelper) {
        webProject = p;
        this.helper = helper;
        this.antHelper = antHelper;
    }
    
    public String addEjbLocalReference(EjbLocalRef localRef, String referencedClassName, AntArtifact target) throws java.io.IOException {
        return addReference(localRef, target);
    }
    
    public String addEjbReferernce(EjbRef ref, String referencedClassName, AntArtifact target) throws IOException {
         return addReference(ref, target);
    }
    
    
    private String addReference(Object ref, AntArtifact target) throws IOException {
         BaseBean bb = findDD();
         // Using basebean here as the web dd implementation classes 
         // perform downcasting. Pavel / Milan can this be resolved
         // this idiom will be used for many other enterprise resources 
         String refName = null;
         if (ref instanceof EjbRef) {
            EjbRef ejbRef = (EjbRef) ref;
            refName = getUniqueName(getWebApp(), "EjbRef", "EjbRefName", 
                    ejbRef.getEjbRefName());
            ejbRef.setEjbRefName(refName);
            bb.addValue("EjbRef", ref);
         } else {
            EjbLocalRef ejbRef = (EjbLocalRef) ref;
            refName = getUniqueName(getWebApp(), "EjbLocalRef", "EjbRefName", 
                    ejbRef.getEjbRefName());
            ejbRef.setEjbRefName(refName);  
            bb.addValue("EjbLocalRef", ref);
         }
         
         if(helper.addReference(target)) {
                EditableProperties ep =
                    antHelper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                String s = ep.getProperty(WebProjectProperties.JAVAC_CLASSPATH);
                s += File.pathSeparatorChar + helper.createForeignFileReference(target);
		ep.setProperty(WebProjectProperties.JAVAC_CLASSPATH, s);
                antHelper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        }
        addJ2eeLibrary();
         
        writeDD(bb);
        return refName;
    }
    
    public String getServiceLocatorName() {
        return null;
    }
    
    private void addJ2eeLibrary() {
//        Library lib = LibraryManager.getDefault().getLibrary(J2EE_14_LIBRARY);
//        
//        WebProjectProperties wp = new WebProjectProperties(
//            webProject, 
//            new UpdateHelper (webProject, antHelper, antHelper.createAuxiliaryConfiguration(), new GeneratedFilesHelper(antHelper), UpdateHelper.createDefaultNotifier()),
//            ((WebProject) webProject).evaluator(),
//            helper);
//        
//        List vcpis = (List) wp.get (WebProjectProperties.JAVAC_CLASSPATH);
//        VisualClassPathItem vcpi = VisualClassPathItem.create (lib, VisualClassPathItem.PATH_IN_WAR_NONE);
//        vcpis.add (vcpi);
//        wp.put (WebProjectProperties.JAVAC_CLASSPATH, vcpis); 
//        wp.store();
    }
    
    private BaseBean findDD() throws IOException {
        WebApp wa = getWebApp();
        return DDProvider.getDefault().getBaseBean(wa);
    }
    
    private WebApp getWebApp() throws IOException {
        WebModuleImplementation jp = (WebModuleImplementation) webProject.getLookup().lookup(WebModuleImplementation.class);
        FileObject fo = jp.getDeploymentDescriptor();
        fo.refresh();
        return DDProvider.getDefault().getDDRootCopy(fo);
    }
    
    private void writeDD(BaseBean bb) throws IOException {
        WebModuleImplementation jp = (WebModuleImplementation) webProject.getLookup().lookup(WebModuleImplementation.class);
        File f = FileUtil.toFile(jp.getDeploymentDescriptor());
        bb.write(f);
    }

    public String addResourceRef(ResourceRef ref, String referencingClass) throws IOException {
         BaseBean bb = findDD();
         // Using basebean here as the web dd implementation classes 
         // perform downcasting. Pavel / Milan can this be resolved
         // this idiom will be used for many other enterprise resources 
         String resourceRefName = getUniqueName(getWebApp(), "ResourceRef", "ResRefName", //NOI18N
                                               ref.getResRefName());
         ref.setResRefName(resourceRefName);
         bb.addValue("ResourceRef", ref);
         writeDD(bb);
         return resourceRefName;
    }

    public ResourceRef createResourceRef(String className) throws IOException {
        ResourceRef ref = null;
        try {
         ref = (ResourceRef) getWebApp().createBean("ResourceRef");
        } catch (ClassNotFoundException cnfe) {
            IOException ioe = new IOException();
            ioe.initCause(cnfe);
            throw ioe;
        }
        return ref;
    }
    
    private String getUniqueName(WebApp wa, String beanName, 
                                 String property, String originalValue) {
        String proposedValue = originalValue;
        int index = 1;
        while (wa.findBeanByName(beanName, property, proposedValue) != null) {
            proposedValue = originalValue+Integer.toString(index++);
        }
        return proposedValue;
    }

    public String addDestinationRef(MessageDestinationRef ref, String referencingClass) throws IOException {
        BaseBean bb = findDD();
        // Using basebean here as the web dd implementation classes
        // perform downcasting. Pavel / Milan can this be resolved
        // this idiom will be used for many other enterprise resources
        String refName = getUniqueName(getWebApp(), "MessageDestinationRef", "MessageDestinationRefName", //NOI18N
                                ref.getMessageDestinationRefName());
        ref.setMessageDestinationRefName(refName);
        bb.addValue("MessageDestinationRef", ref);
        writeDD(bb);
        return refName;
    }

    public MessageDestinationRef createDestinationRef(String className) throws IOException {
        MessageDestinationRef ref = null;
        try {
         ref = (MessageDestinationRef) getWebApp().createBean("MessageDestinationRef");
        } catch (ClassNotFoundException cnfe) {
            IOException ioe = new IOException();
            ioe.initCause(cnfe);
            throw ioe;
        }
        return ref;
    }
}
