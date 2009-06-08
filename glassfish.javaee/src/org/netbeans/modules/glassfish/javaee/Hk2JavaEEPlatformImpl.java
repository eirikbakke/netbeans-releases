/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.glassfish.javaee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.modules.j2ee.deployment.common.api.J2eeLibraryTypeProvider;
import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
import org.netbeans.modules.glassfish.spi.ServerUtilities;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Profile;
import org.netbeans.modules.j2ee.deployment.plugins.spi.support.LookupProviderSupport;
import org.netbeans.spi.project.libraries.LibraryImplementation;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
    
/**
 *
 * @author Ludo
 */
public class Hk2JavaEEPlatformImpl extends J2eePlatformImpl {
    
    private Hk2DeploymentManager dm;
    private LibraryImplementation[] libraries;
    private Hk2JavaEEPlatformFactory pf;

    /**
     * 
     * @param dm 
     */
    public Hk2JavaEEPlatformImpl(Hk2DeploymentManager dm, Hk2JavaEEPlatformFactory pf) {
        this.dm = dm;
        this.pf = pf;
        initLibraries();
    }
    
    // Persistence provider strings
    private static final String PERSISTENCE_PROV_ECLIPSELINK = "org.eclipse.persistence.jpa.PersistenceProvider"; //NOI18N

    // WEB SERVICES PROPERTIES 
    // TODO - shall be removed and usages replaced by values from j2eeserver or websvc apis after api redesign
    private static final String TOOL_WSCOMPILE = "wscompile";
    private static final String TOOL_JSR109 = "jsr109";
    private static final String TOOL_WSIMPORT = "wsimport";
    private static final String TOOL_WSGEN = "wsgen";
    private static final String TOOL_KEYSTORE = "keystore";
    private static final String TOOL_KEYSTORECLIENT = "keystoreClient";
    private static final String TOOL_TRUSTSTORE = "truststore";
    private static final String TOOL_TRUSTSTORECLIENT = "truststoreClient";
    private static final String TOOL_WSIT = "wsit";
    private static final String TOOL_JAXWSTESTER = "jaxws-tester";
    private static final String TOOL_APPCLIENTRUNTIME = "appClientRuntime";
    private static final String KEYSTORE_LOCATION = "config/keystore.jks";
    private static final String TRUSTSTORE_LOCATION = "config/cacerts.jks";

    /**
     * 
     * @param toolName 
     * @return 
     */
    public boolean isToolSupported(String toolName) {
        // Persistence Providers
        if(PERSISTENCE_PROV_ECLIPSELINK.equals(toolName)) {
            return true;
        }

        if("org.hibernate.ejb.HibernatePersistence".equals(toolName) || //NOI18N
                "oracle.toplink.essentials.PersistenceProvider".equals(toolName) || // NOI18N
                "kodo.persistence.PersistenceProviderImpl".equals(toolName) || // NOI18N
                "org.apache.openjpa.persistence.PersistenceProviderImpl".equals(toolName)) { //NOI18N
            return true;
        }
        
        if("defaultPersistenceProviderJavaEE5".equals(toolName)) {  //NOI18N
            return true;
        }
        if("eclipseLinkPersistenceProviderIsDefault".equals(toolName)) {
            return true;
        }        

        File wsLib = null;
        
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        if (gfRootStr != null) {
            wsLib = ServerUtilities.getJarName(gfRootStr, "webservices" + ServerUtilities.GFV3_VERSION_MATCHER);
        }

        // WEB SERVICES SUPPORT
        if ((wsLib != null) && (wsLib.exists())) {      // existence of webservice libraries
            if (TOOL_WSGEN.equals(toolName)) {         //NOI18N
                return true;
            }
            if (TOOL_WSIMPORT.equals(toolName)) {      //NOI18N
                return true;
            }
            if (TOOL_WSIT.equals(toolName)) {          //NOI18N
                return true;
            }
            if (TOOL_JAXWSTESTER.equals(toolName)) {  //NOI18N
                return true;
            }
            if (TOOL_JSR109.equals(toolName)) {        //NOI18N
                return true;
            }
            if (TOOL_KEYSTORE.equals(toolName)) {      //NOI18N
                return true;
            }
            if (TOOL_KEYSTORECLIENT.equals(toolName)) {//NOI18N
                return true;
            }
            if (TOOL_TRUSTSTORE.equals(toolName)) {    //NOI18N
                return true;
            }
            if (TOOL_TRUSTSTORECLIENT.equals(toolName)) {  //NOI18N
                return true;
            }
            if (TOOL_WSCOMPILE.equals(toolName)) {     //NOI18N
                return true;   // TODO - the support is there - need to find the right classpath then change to true
            }
            if (TOOL_APPCLIENTRUNTIME.equals(toolName)) { //NOI18N
                return false;    //TODO - when the support becomes available, change to true
            }
        }
        
        return false;     
    }
    
    /**
     * 
     * @param toolName 
     * @return 
     */
    public File[] getToolClasspathEntries(String toolName) {
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        if (TOOL_WSGEN.equals(toolName) || TOOL_WSIMPORT.equals(toolName)) {
            String[] entries = new String[] {"webservices", //NOI18N
                                             "javax.activation", //NOI18N
                                             "jaxb"}; //NOI18N
            List<File> cPath = new ArrayList<File>();
            
            for (String entry : entries) {
                File f = ServerUtilities.getJarName(gfRootStr, entry + ServerUtilities.GFV3_VERSION_MATCHER);
                if ((f != null) && (f.exists())) {
                    cPath.add(f);
                }
            }
            return cPath.toArray(new File[cPath.size()]);
        }

        if (TOOL_WSCOMPILE.equals(toolName)) {
            String[] entries = new String[] {"webservices", //NOI18N
                                             "javax.activation"}; //NOI18N
            List<File> cPath = new ArrayList<File>();

            for (String entry : entries) {
                File f = ServerUtilities.getJarName(gfRootStr, entry + ServerUtilities.GFV3_VERSION_MATCHER);
                if ((f != null) && (f.exists())) {
                    cPath.add(f);
                }
            }
            return cPath.toArray(new File[cPath.size()]);
        }

        File domainDir = null;
        File gfRoot = new File(gfRootStr);
        if ((gfRoot != null) && (gfRoot.exists())) {
            String domainDirName = dm.getProperties().getDomainDir();
            domainDir = new File(domainDirName);
        }
        
        if (TOOL_KEYSTORE.equals(toolName) || TOOL_KEYSTORECLIENT.equals(toolName)) {
            return new File[] {
                new File(domainDir, KEYSTORE_LOCATION)  //NOI18N
            };
        }
                
        if (TOOL_TRUSTSTORE.equals(toolName) || TOOL_TRUSTSTORECLIENT.equals(toolName)) {
            return new File[] {
                new File(domainDir, TRUSTSTORE_LOCATION)  //NOI18N
            };
        }
        
        return new File[0];
    }

    /**
     * 
     * @return 
     */
    public Set getSupportedSpecVersions() {
        Logger.getLogger("glassfish-javaee").log(Level.INFO,"programmer calling deprecated API", new Exception("deprectaed API usage")); // NOI18N
        return pf.getSupportedSpecVersions();
    }

    @Override
    public Set<Profile> getSupportedProfiles() {
        return pf.getSupportedProfiles();
    }
    
    @Override
    public Set<Profile> getSupportedProfiles(Object o) {
        return pf.getSupportedProfiles();
    }
    /**
     * 
     * @return 
     */
    public Set getSupportedModuleTypes() {
        return pf.getSupportedModuleTypes();
    }
    
    /**
     * 
     * @return 
     */
    public java.io.File[] getPlatformRoots() {
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        File returnedElement;
        File[] retVal = new File[0];
        if (gfRootStr != null) {
            returnedElement = new File(gfRootStr);
            if (returnedElement.exists()) {
                retVal = new File[] { returnedElement };
            }
        }
        return retVal;
    }
    
    /**
     * 
     * @return 
     */
    public LibraryImplementation[] getLibraries() {
        return libraries.clone();
    }
    
    /**
     * 
     * @return 
     */
    public java.awt.Image getIcon() {
        return ImageUtilities.loadImage("org/netbeans/modules/j2ee/hk2/resources/server.gif"); // NOI18N
    }
    
    /**
     * 
     * @return 
     */
    public String getDisplayName() {
        return pf.getDisplayName();
    }
    
    /**
     * 
     * @return 
     */
    public Set getSupportedJavaPlatformVersions() {
        return pf.getSupportedJavaPlatforms();
    }
    
    /**
     * 
     * @return 
     */
    public JavaPlatform getJavaPlatform() {
        return pf.getJavaPlatform();
    }
    
    /**
     * 
     */
    public void notifyLibrariesChanged() {
        initLibraries();
        firePropertyChange(PROP_LIBRARIES, null, libraries.clone());
    }
    
    private void initLibraries() {
        LibraryImplementation lib = new J2eeLibraryTypeProvider().createLibrary();
        lib.setName(pf.getLibraryName()); 
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_CLASSPATH, dm.getProperties().getClasses());
        lib.setContent(J2eeLibraryTypeProvider.VOLUME_TYPE_JAVADOC, dm.getProperties().getJavadocs());
        libraries = new LibraryImplementation[] {lib};
    }
    
    @Override
    public Lookup getLookup() {
        String gfRootStr = dm.getProperties().getGlassfishRoot();
        Lookup baseLookup = Lookups.fixed(gfRootStr);
        return LookupProviderSupport.createCompositeLookup(baseLookup, pf.getLookupKey()); // "J2EE/DeploymentPlugins/gfv3/Lookup"); //NOI18N
//
//        WSStackSPI metroStack = new GlassfishJaxWsStack(gfRootStr);
//        return Lookups.fixed(WSStackFactory.createWSStack(metroStack));
    }
}
