/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.core.startup.layers;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import org.netbeans.ProxyURLStreamHandlerFactory;
import org.netbeans.junit.Log;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.RandomlyFails;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.JarFileSystem;
import org.openide.util.Utilities;
import org.openide.util.test.TestFileUtils;

/**
 * @author  tomas zezula
 */
public class ArchiveURLMapperTest extends NbTestCase {
    
    private static final String RESOURCE = "test.txt";
    private static final String JAR_FILE = "test.jar";
    private Logger LOG;
    
    public ArchiveURLMapperTest(String testName) {
        super(testName);
    }

    protected @Override void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        LOG = Logger.getLogger("test." + getName());
    }

    @Override
    protected Level logLevel() {
        return Level.FINE;
    }
    
    private URL createJarFile () throws IOException {
        File workDir = FileUtil.normalizeFile(this.getWorkDir());
        File jarFile = new File(workDir,JAR_FILE);
        JarOutputStream out = new JarOutputStream ( new FileOutputStream (jarFile));
        ZipEntry entry = new ZipEntry (RESOURCE);        
        out.putNextEntry(entry);
        out.write (RESOURCE.getBytes());
        out.close();
        return Utilities.toURI(jarFile).toURL();
    }
    
    public void testURLMapper () throws Exception {
        URL jarFileURL = createJarFile ();
        assertTrue (jarFileURL != null);
        URL url = new URL (MessageFormat.format("jar:{0}!/{1}", jarFileURL.toExternalForm(), RESOURCE));
        FileObject fo = URLMapper.findFileObject(url);
        assertNotNull("There is one found file object", fo);
        assertTrue(fo.getPath().equals(RESOURCE));
        URL newUrl = URLMapper.findURL(fo, URLMapper.EXTERNAL);
        assertEquals(url, newUrl);
    }

	public void testArchiveToRootURL () throws Exception {
		URL jarFileURL = createJarFile ();
		assertTrue (jarFileURL != null);
        assertTrue (FileUtil.isArchiveFile(jarFileURL));
        URL jarRootURL = FileUtil.getArchiveRoot(jarFileURL);
        assertTrue ("jar".equals(jarRootURL.getProtocol()));
        String path = jarRootURL.getPath();
        int index = path.lastIndexOf ("!/");
        assertTrue (index==path.length()-2);
        URL innerURL = new URL(path.substring(0,index));
        assertTrue (innerURL.equals(jarFileURL));
	}
        
    public void testArchiveToRootFileObject () throws Exception {
        URL jarFileURL = createJarFile ();
        FileObject fo = URLMapper.findFileObject(jarFileURL);
        assertTrue (fo != null);
        assertTrue (FileUtil.isArchiveFile(fo));
        FileObject rootFo = FileUtil.getArchiveRoot (fo);
        assertTrue (rootFo!=null);
        assertTrue ("".equals(rootFo.getPath()));
        assertTrue (rootFo.getFileSystem() instanceof JarFileSystem);
        File jarFile = ((JarFileSystem)rootFo.getFileSystem()).getJarFile();
        assertTrue (jarFileURL.equals(Utilities.toURI(jarFile).toURL()));
    }
    
    public void testFunnyZipEntryNames() throws Exception { // #181671
        doFunnyZipEntryNames("[My Content;hi*();.xml]");
    }

    public void testFunnyZipEntryNames20() throws Exception { // #181671
        doFunnyZipEntryNames("[My%20Content;hi*();.xml]");
    }

    public void testFunnyZipEntryNamesQuestion() throws Exception { // #181671
        doFunnyZipEntryNames("[My?Content;hi*();.xml]");
    }
    
    private void doFunnyZipEntryNames(String file) throws Exception {
        File docx = new File(getWorkDir(), "ms-docx.jar");
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(docx));
        ZipEntry entry = new ZipEntry(file);
        jos.putNextEntry(entry);
        jos.write("content".getBytes());
        jos.close();
        
        FileObject docxFO = URLMapper.findFileObject(Utilities.toURI(docx).toURL());
        assertNotNull(docxFO);
        assertTrue(FileUtil.isArchiveFile(docxFO));
        
        FileObject docxRoot = FileUtil.getArchiveRoot(docxFO);
        assertNotNull("Root found", docxRoot);
        FileObject content = docxRoot.getFileObject(file);
        assertNotNull("content.xml found", content);
        
        assertEquals("Has right bytes", "content", content.asText());
        
        CharSequence log = Log.enable("", Level.WARNING);
        URL u = URLMapper.findURL(content, URLMapper.EXTERNAL);
        InputStream is = u.openStream();
        byte[] arr = new byte[30];
        int len = is.read(arr);
        assertEquals("Len is content", "content".length(), len);
        assertEquals("OK", "content", new String(arr, 0, len));
        
        assertEquals("No warnings:\n" + log, 0, log.length());
        assertEquals(u.toString(), content, URLMapper.findFileObject(u));
    }

    @RandomlyFails
    public void testNestedJars() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JarOutputStream jos = new JarOutputStream(baos);
        ZipEntry entry = new ZipEntry("text");
        jos.putNextEntry(entry);
        jos.write("content".getBytes());
        jos.close();
        File metaJar = new File(getWorkDir(), "meta.jar");
        jos = new JarOutputStream(new FileOutputStream(metaJar));
        entry = new ZipEntry("nested.jar");
        jos.putNextEntry(entry);
        jos.write(baos.toByteArray());
        jos.close();
        FileObject metaJarFO = URLMapper.findFileObject(Utilities.toURI(metaJar).toURL());
        assertNotNull(metaJarFO);
        assertTrue(FileUtil.isArchiveFile(metaJarFO));
        FileObject metaRoot = FileUtil.getArchiveRoot(metaJarFO);
        assertNotNull(metaRoot);
        FileObject nestedJarFO = metaRoot.getFileObject("nested.jar");
        assertNotNull(nestedJarFO);
        assertTrue(FileUtil.isArchiveFile(nestedJarFO));
        FileObject nestedRoot = FileUtil.getArchiveRoot(nestedJarFO);
        assertNotNull(nestedRoot);
        FileObject textFO = nestedRoot.getFileObject("text");
        assertEquals("content", textFO.asText());

        // test refresh of nested jar - #175316

        baos = new ByteArrayOutputStream();
        jos = new JarOutputStream(baos);
        // meta.jar/nested.jar/newInNested
        entry = new ZipEntry("newInNested");
        jos.putNextEntry(entry);
        jos.write("content in newInNested".getBytes());
        jos.close();
        
        LOG.log(Level.INFO, "Writing new {0}", metaJar);
        
        jos = new JarOutputStream(new FileOutputStream(metaJar));
        // meta.jar/nested.jar
        entry = new ZipEntry("nested.jar");
        entry.setTime(System.currentTimeMillis() + 10000);
        jos.putNextEntry(entry);
        jos.write(baos.toByteArray());
        // meta.jar/newFile
        entry = new ZipEntry("newFile");
        jos.putNextEntry(entry);
        jos.write("new content".getBytes());
        jos.close();

        // check meta.jar/newFile
        FileObject newFO = null;
        for (int i = 0; i < 10; i++) {
            TestFileUtils.touch(metaJar, null);  // just for sure

            LOG.info("Before refresh");
            metaJarFO.refresh();
            LOG.info("After refresh");
            
            newFO = metaRoot.getFileObject("newFile");
            if (newFO != null) {
                break;
            }
            Thread.sleep(500);
        }
        LOG.log(Level.INFO, "newFile obtained: {0}", newFO);
        if (newFO == null) {
            fail("newFile should be found among:\n" + Arrays.toString(metaRoot.getChildren()));
        }
        assertEquals("new content", newFO.asText());
        // check meta.jar/nested.jar/newInNested
        nestedRoot = FileUtil.getArchiveRoot(nestedJarFO);
        FileObject newInNestedFO = nestedRoot.getFileObject("newInNested");
        assertNotNull(newInNestedFO);
        assertEquals("content in newInNested", newInNestedFO.asText());
        // check meta.jar/nested.jar/text gone
        textFO = nestedRoot.getFileObject("text");
        assertNull(textFO);
    }

    public void test166708() throws Exception {
        File jar = new File(getWorkDir(), "test.jar");
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar));
        jos.putNextEntry(new ZipEntry("has spaces"));
        jos.close();
        URL source = new URL("jar:" + Utilities.toURI(jar).toURL() + "!/has%20spaces");
        source.toURI();
        FileObject file = URLMapper.findFileObject(source);
        assertNotNull(file);
        assertEquals(source, URLMapper.findURL(file, URLMapper.INTERNAL));
        assertEquals(0, source.openConnection().getContentLength());
        ProxyURLStreamHandlerFactory.register();
        assertEquals(0, new URL("jar:" + Utilities.toURI(jar).toURL() + "!/has%20spaces").openConnection().getContentLength());
    }

}
