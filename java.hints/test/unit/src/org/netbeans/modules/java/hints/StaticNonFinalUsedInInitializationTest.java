/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints;

import org.netbeans.modules.java.hints.jackpot.code.spi.TestBase;

/**
 *
 * @author David Strupl
 */
public class StaticNonFinalUsedInInitializationTest extends TestBase {

    public StaticNonFinalUsedInInitializationTest(String name) {
        super(name, StaticNonFinalUsedInInitialization.class);
    }

    public void testDoNotReport() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "public class Test {\n" +
                            "    private static final int A = 5;\n" +
                            "    public static final int B = A + 10;\n" +
                            "}"
                            );
    }
    public void testDoNotReport2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "public class Test {\n" +
                            "    private static int A = 5;\n" +
                            "    public int B = A + 10;\n" +
                            "    public int C;\n" +
                            "    {\n" +
                            "        C = A + 10;" +
                            "    }\n" +
                            "}"
                            );
    }
    public void testReportIt() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "public class Test {\n" +
                            "    static int A = 5;\n" +
                            "    static int B = A + 10;\n" +
                            "}",
                            "3:19-3:20:verifier:StaticNonFinalUsedInInitialization"
                            );
    }
    public void testReportIt2() throws Exception {
        performAnalysisTest("test/Test.java",
                            "package test;\n" +
                            "public class Test {\n" +
                            "    static int A = 5;\n" +
                            "    static int B;\n" +
                            "    static {\n" +
                            "        B = A + 10;\n" +
                            "    }\n" +
                            "}",
                            "5:12-5:13:verifier:StaticNonFinalUsedInInitialization"
                            );
    }
}
