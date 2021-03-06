/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009-2010 Sun Microsystems, Inc.
 */
package org.netbeans.modules.refactoring.java.test;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.refactoring.api.CopyRefactoring;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.RefactoringSession;
import org.netbeans.modules.refactoring.api.SingleCopyRefactoring;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Ralph Ruijs
 */
public class CopyClassTest extends RefactoringTestBase {

    public CopyClassTest(String name) {
        super(name);
    }
    
    public void test239213() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } static class Inner { void m() { CopyClass.Inner.this.m(); } } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkgdst/"), "CopyClassRen");
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkgdst/CopyClassRen.java", "package copypkgdst; import copypkg.*; public class CopyClassRen { public CopyClassRen() { } static class Inner { void m() { CopyClassRen.Inner.this.m(); } } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } static class Inner { void m() { CopyClass.Inner.this.m(); } } }"));
    }

    public void test179333() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("t/package-info.java", "package t;"),
                new File("u/A.java", "package u; public class A { }"));
        performCopyClass(src.getFileObject("t/package-info.java"), new URL(src.getURL(), "u/"), "package-info");
        verifyContent(src,
                new File("t/package-info.java", "package t;"),
                new File("u/package-info.java", "package u;"),
                new File("u/A.java", "package u; public class A { }"));
    }

    public void testCopyClass() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkgdst/"), "CopyClass");
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkgdst/CopyClass.java", "package copypkgdst; import copypkg.*; public class CopyClass { public CopyClass() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }
    
    public void testCopyMultipleClass() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClasses(new FileObject[] {src.getFileObject("copypkg/CopyClass.java"), src.getFileObject("copypkg/CopyClass1.java")}, new URL(src.getURL(), "copypkgdst/"));
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkgdst/CopyClass1.java", "package copypkgdst; import copypkg.*; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("copypkgdst/CopyClass.java", "package copypkgdst; import copypkg.*; public class CopyClass { public CopyClass() { } }"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyClassToSamePackage() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkg/"), "CopyClass1");
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyClassWithRename() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkgdst/"), "CopyClassRen");
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkgdst/CopyClassRen.java", "package copypkgdst; import copypkg.*; public class CopyClassRen { public CopyClassRen() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyToDefault() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), src.getURL(), "CopyClass");
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("CopyClass.java", " import copypkg.*; public class CopyClass { public CopyClass() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyMultipleToDefault() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClasses(new FileObject[] {src.getFileObject("copypkg/CopyClass.java"), src.getFileObject("copypkg/CopyClass1.java")}, src.getURL());
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("CopyClass1.java", " import copypkg.*; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("CopyClass.java", " import copypkg.*; public class CopyClass { public CopyClass() { } }"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } class Nested { public Nested() { } } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyInvalid1() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkg/"), "CopyClass1", new Problem(true, "ERR_ClassToMoveClashes"));
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass1.java", "package copypkg; public class CopyClass1 { public CopyClass1() { } }"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    public void testCopyInvalid2() throws Exception {
        writeFilesAndWaitForScan(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
        performCopyClass(src.getFileObject("copypkg/CopyClass.java"), new URL(src.getURL(), "copypkg/"), "CopyClass", new Problem(true, "ERR_ClassToMoveClashes"));
        verifyContent(src,
                new File("copypkgdst/package-info.java", "package copypkgdst;"),
                new File("copypkg/CopyClass.java", "package copypkg; public class CopyClass { public CopyClass() { } }"));
    }

    private void performCopyClass(FileObject source, URL target, String newname, Problem... expectedProblems) throws Exception {
        final SingleCopyRefactoring[] r = new SingleCopyRefactoring[1];

        r[0] = new SingleCopyRefactoring(Lookups.singleton(source));
        r[0].setTarget(Lookups.singleton(target));
        r[0].setNewName(newname);

        RefactoringSession rs = RefactoringSession.create("Session");
        List<Problem> problems = new LinkedList<Problem>();

        addAllProblems(problems, r[0].preCheck());
        addAllProblems(problems, r[0].prepare(rs));
        addAllProblems(problems, rs.doRefactoring(true));

        assertProblems(Arrays.asList(expectedProblems), problems);
    }

    private void performCopyClasses(FileObject[] source, URL target, Problem... expectedProblems) throws Exception {
        final CopyRefactoring[] r = new CopyRefactoring[1];

        r[0] = new CopyRefactoring(Lookups.fixed((Object[]) source));
        r[0].setTarget(Lookups.singleton(target));

        RefactoringSession rs = RefactoringSession.create("Session");
        List<Problem> problems = new LinkedList<Problem>();

        addAllProblems(problems, r[0].preCheck());
        addAllProblems(problems, r[0].prepare(rs));
        addAllProblems(problems, rs.doRefactoring(true));

        assertProblems(Arrays.asList(expectedProblems), problems);
    }
}
