/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2012 Oracle and/or its affiliates. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2010 Sun
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

package org.netbeans.upgrade;
import java.beans.PropertyVetoException;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.netbeans.upgrade.systemoptions.Importer;

import org.netbeans.util.Util;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

/** pending
 *
 * @author  Jiri Rechtacek, Jiri Skrivanek
 */
public final class AutoUpgrade {

    private static final Logger LOGGER = Logger.getLogger(AutoUpgrade.class.getName());

    public static void main (String[] args) throws Exception {
        String[] version = new String[1];
        File sourceFolder = checkPrevious (version, VERSION_TO_CHECK);
        if (sourceFolder != null) {
            if (!showUpgradeDialog (sourceFolder)) {
                throw new org.openide.util.UserCancelException ();
            }
            if (version[0].compareTo("6.5") < 0) {  //NOI18N
                // less than 6.5

                // TODO - this branch can be removed when only import from 6.5 and newer is supported
                doUpgrade (sourceFolder, version[0]);
                doNonStandardUpgrade(sourceFolder, version[0]);
                //#75324 NBplatform settings are not imported
                upgradeBuildProperties(sourceFolder, version);
                //migrates SystemOptions, converts them as a Preferences
                Importer.doImport();
            } else {
                //equal or greater than 6.5
                
                copyToUserdir(sourceFolder);
                //migrates SystemOptions, converts them as a Preferences
                Importer.doImport();
            }
        }
    }

    //#75324 NBplatform settings are not imported
    private static void upgradeBuildProperties(final File sourceFolder, final String[] version) throws IOException {
        File userdir = new File(System.getProperty("netbeans.user", ""));//NOI18N
        String[] regexForSelection = new String[]{
            "^nbplatform[.](?!default[.]netbeans[.]dest[.]dir).+[.].+=.+$", //NOI18N
            // #161616
            "^var[.].*"  //NOI18N
        };
        Copy.appendSelectedLines(new File(sourceFolder, "build.properties"), //NOI18N
                userdir, regexForSelection);
    }

    // the order of VERSION_TO_CHECK here defines the precedence of imports
    // the first one will be choosen for import
    final static private List<String> VERSION_TO_CHECK = 
            Arrays.asList (new String[] { ".netbeans/7.1", ".netbeans/7.0", ".netbeans/6.9" });//NOI18N

            
    static private File checkPrevious (String[] version, final List<String> versionsToCheck) {        
        String userHome = System.getProperty ("user.home"); // NOI18N
        File sourceFolder = null;
        
        if (userHome != null) {
            File userHomeFile = new File (userHome);
            Iterator<String> it = versionsToCheck.iterator ();
            String ver;
            while (it.hasNext () && sourceFolder == null) {
                ver = it.next ();
                sourceFolder = new File (userHomeFile.getAbsolutePath (), ver);
                
                if (sourceFolder.isDirectory ()) {
                    version[0] = sourceFolder.getName();
                    break;
                }
                sourceFolder = null;
            }
            return sourceFolder;
        } else {
            return null;
        }
    }
    
    private static boolean showUpgradeDialog (final File source) {
        Util.setDefaultLookAndFeel();
        JOptionPane p = new JOptionPane (
            new AutoUpgradePanel (source.getAbsolutePath ()),
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION
        );
        JDialog d = Util.createJOptionDialog(p, NbBundle.getMessage (AutoUpgrade.class, "MSG_Confirmation_Title"));
        d.setVisible (true);

        return new Integer (JOptionPane.YES_OPTION).equals (p.getValue ());
    }

    static void doUpgrade (File source, String oldVersion) 
    throws java.io.IOException, java.beans.PropertyVetoException {        
        File userdir = new File(System.getProperty ("netbeans.user", "")); // NOI18N

        java.util.Set<?> includeExclude;
        try {
            Reader r = new InputStreamReader (
                    AutoUpgrade.class.getResourceAsStream ("copy" + oldVersion), // NOI18N
                    "utf-8"); // NOI18N
            includeExclude = IncludeExclude.create (r);
            r.close ();
        } catch (IOException ex) {
            throw new IOException("Cannot import from version: " + oldVersion, ex);
        }

        ErrorManager.getDefault ().log (
            ErrorManager.USER, "Import: Old version: " // NOI18N
            + oldVersion + ". Importing from " + source + " to " + userdir // NOI18N
        );
        
        File oldConfig = new File (source, "config"); // NOI18N
        org.openide.filesystems.FileSystem old;
        {
            LocalFileSystem lfs = new LocalFileSystem ();
            lfs.setRootDirectory (oldConfig);
            
            XMLFileSystem xmlfs = null;
            try {
                URL url = AutoUpgrade.class.getResource("layer" + oldVersion + ".xml"); // NOI18N
                xmlfs = (url != null) ? new XMLFileSystem(url) : null;
            } catch (SAXException ex) {
                throw new IOException("Cannot import from version: " + oldVersion, ex);
            }
            
            old = (xmlfs != null) ? createLayeredSystem(lfs, xmlfs) : lfs;
        }
        
        Copy.copyDeep (old.getRoot (), FileUtil.getConfigRoot (), includeExclude, PathTransformation.getInstance(oldVersion));
        
    }
    
    /* copy-pasted method doUpgrade and slightly modified to copy files relative
     * to userdir.
     */
    private static void doNonStandardUpgrade (File source,String oldVersion) 
            throws IOException, PropertyVetoException {
        File userdir = new File(System.getProperty("netbeans.user", "")); // NOI18N        
        java.util.Set<?> includeExclude;
        try {
            InputStream is = AutoUpgrade.class.getResourceAsStream("nonstandard" + oldVersion); // NOI18N
            if (is == null) {
                return;
            }
            Reader r = new InputStreamReader(is, "utf-8"); // NOI18N
            includeExclude = IncludeExclude.create(r);
            r.close();
        } catch (IOException ex) {
            throw new IOException("Cannot import from version: " +  oldVersion + "nonstandard", ex);
        }        
        ErrorManager.getDefault ().log (ErrorManager.USER, "Import: Old version: " // NOI18N
            + oldVersion + "nonstandard"  + ". Importing from " + source + " to " + userdir // NOI18N
        );        
        
        LocalFileSystem  old = new LocalFileSystem();
        old.setRootDirectory(source);
        
        LocalFileSystem nfs = new LocalFileSystem();
        nfs.setRootDirectory(userdir);                
        Copy.copyDeep(old.getRoot(), nfs.getRoot(), includeExclude, PathTransformation.getInstance(oldVersion));
    }    
    

    static MultiFileSystem createLayeredSystem(final LocalFileSystem lfs, final XMLFileSystem xmlfs) {
        MultiFileSystem old;
        
        old = new MultiFileSystem (
            new org.openide.filesystems.FileSystem[] { lfs, xmlfs }
        ) {
            {
                setPropagateMasks(true);
            }
        };
        return old;
    }

    /* Copy files from source folder to current userdir according to include/exclude
     * patterns in etc/netbeans.import file. */
    private static void copyToUserdir(File source) throws IOException, PropertyVetoException {
        File userdir = new File(System.getProperty("netbeans.user", "")); // NOI18N
        File netBeansDir = InstalledFileLocator.getDefault().locate("modules", null, false).getParentFile().getParentFile();  //NOI18N
        File importFile = new File(netBeansDir, "etc/netbeans.import");  //NOI18N
        LOGGER.fine("Import file: " + importFile);
        LOGGER.info("Importing from " + source + " to " + userdir); // NOI18N
        CopyFiles.copyDeep(source, userdir, importFile);
    }
}
