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

package org.netbeans.nbbuild;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import junit.framework.TestCase;

/**
 * Test {@link ModuleListParser}.
 * @author Jesse Glick
 */
public class ModuleListParserTest extends TestCase {
    
    public ModuleListParserTest(String name) {
        super(name);
    }
    
    private File nball;
    
    private File file(File root, String relpath) {
        return new File(root, relpath.replace('/', File.separatorChar));
    }
    
    private String filePath(File root, String relpath) {
        return file(root, relpath).getAbsolutePath();
    }

    protected void setUp() throws Exception {
        super.setUp();
        String prop = System.getProperty("nb_all");
        assertNotNull("${nb_all} defined", prop);
        nball = new File(prop);
    }
    
    public void testScanSourcesInNetBeansOrg() throws Exception {
        Hashtable properties = new Hashtable();
        properties.put("nb_all", nball.getAbsolutePath());
        File build = file(nball, "build");
        properties.put("netbeans.dest.dir", build.getAbsolutePath());
        properties.put("nb.cluster.foo", "beans,clazz");
        properties.put("nb.cluster.foo.dir", "foodir");
        properties.put("nb.cluster.bar", "core");
        properties.put("nb.cluster.bar.dir", "bardir");
        ModuleListParser p = new ModuleListParser(properties, null, null);
        ModuleListParser.Entry e = p.findByCodeNameBase("org.netbeans.modules.beans");
        assertNotNull(e);
        assertEquals("org.netbeans.modules.beans", e.getCnb());
        assertEquals(file(build, "foodir/modules/org-netbeans-modules-beans.jar"), e.getJar());
        assertEquals(Collections.EMPTY_LIST, Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.libs.xerces");
        assertNotNull("found module in a subdir", e);
        assertEquals("org.netbeans.libs.xerces", e.getCnb());
        assertEquals("unknown module put in extra cluster by default", file(build, "extra/modules/org-netbeans-libs-xerces.jar"), e.getJar());
        assertEquals("correct CP extensions (using <binary-origin> and relative paths)", Arrays.asList(new File[] {
            file(nball, "libs/external/xerces-2.6.2.jar"),
            file(nball, "libs/external/xml-commons-dom-ranges-1.0.b2.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("javax.jmi.model");
        assertNotNull(e);
        assertEquals("correct CP extensions (using <binary-origin> and property substitutions #1)", Arrays.asList(new File[] {
            file(nball, "mdr/external/mof.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.modules.css");
        assertNotNull(e);
        assertEquals("correct CP extensions (using <binary-origin> and property substitutions #2)", Arrays.asList(new File[] {
            file(nball, "xml/external/flute.jar"),
            file(nball, "xml/external/sac.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.modules.javanavigation");
        assertNotNull("found module in a subsubdir", e);
        e = p.findByCodeNameBase("org.netbeans.core");
        assertNotNull(e);
        assertEquals("org.netbeans.core", e.getCnb());
        assertEquals("handling special JAR names correctly", file(build, "bardir/core/core.jar"), e.getJar());
        assertEquals(Collections.EMPTY_LIST, Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.modules.xml.tax");
        assertNotNull("found xml/tax", e);
        assertEquals("org.netbeans.modules.xml.tax", e.getCnb());
        assertEquals(file(build, "extra/modules/autoload/xml-tax.jar"), e.getJar());
        assertEquals("correct CP extensions (using runtime-relative-path)", Arrays.asList(new File[] {
            file(build, "extra/modules/autoload/ext/tax.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
    }
    
    public void testScanSourcesAndBinariesForExternalModule() throws Exception {
        Hashtable properties = new Hashtable();
        properties.put("netbeans.dest.dir", filePath(nball, "nbbuild/netbeans"));
        properties.put("basedir", filePath(nball, "apisupport/project/test/unit/data/example-external-projects/suite1/action-project"));
        ModuleListParser p = new ModuleListParser(properties, "action-project", null);
        ModuleListParser.Entry e = p.findByCodeNameBase("org.netbeans.examples.modules.action");
        assertNotNull("found myself", e);
        assertEquals("org.netbeans.examples.modules.action", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/devel/modules/org-netbeans-examples-modules-action.jar"), e.getJar());
        assertEquals(Collections.EMPTY_LIST, Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.examples.modules.lib");
        assertNotNull("found sister project in suite", e);
        assertEquals("org.netbeans.examples.modules.lib", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/devel/modules/org-netbeans-examples-modules-lib.jar"), e.getJar());
        e = p.findByCodeNameBase("org.netbeans.libs.xerces");
        assertNotNull("found netbeans.org module by its binary", e);
        assertEquals("org.netbeans.libs.xerces", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/ide5/modules/org-netbeans-libs-xerces.jar"), e.getJar());
        assertEquals("correct CP extensions (using Class-Path header in manifest)", Arrays.asList(new File[] {
            file(nball, "nbbuild/netbeans/ide5/modules/ext/xerces-2.6.2.jar"),
            file(nball, "nbbuild/netbeans/ide5/modules/ext/xml-commons-dom-ranges-1.0.b2.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.openide.loaders");
        assertNotNull(e);
        assertEquals("org.openide.loaders", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/platform5/core/openide-loaders.jar"), e.getJar());
        assertEquals(Collections.EMPTY_LIST, Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.bootstrap");
        assertNotNull(e);
        assertEquals("org.netbeans.bootstrap", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/platform5/lib/boot.jar"), e.getJar());
        assertEquals(Collections.EMPTY_LIST, Arrays.asList(e.getClassPathExtensions()));
        e = p.findByCodeNameBase("org.netbeans.modules.xml.tax");
        assertNotNull(e);
        assertEquals("org.netbeans.modules.xml.tax", e.getCnb());
        assertEquals(file(nball, "nbbuild/netbeans/ide5/modules/autoload/xml-tax.jar"), e.getJar());
        assertEquals(Arrays.asList(new File[] {
            file(nball, "nbbuild/netbeans/ide5/modules/autoload/ext/tax.jar"),
        }), Arrays.asList(e.getClassPathExtensions()));
    }
    
}
