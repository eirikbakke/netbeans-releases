/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.phpunit.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import org.netbeans.junit.NbTestCase;

/**
 * @author Tomas Mysik
 */
public class PhpUnitCoverageLogParserTest extends NbTestCase {

    public PhpUnitCoverageLogParserTest(String name) {
        super(name);
    }

    public void testParseLog() throws Exception {
        Reader reader = new BufferedReader(new FileReader(getCoverageLog("phpunit-coverage.xml")));
        CoverageImpl coverage = new CoverageImpl();

        PhpUnitCoverageLogParser.parse(reader, coverage);

        assertEquals(1233352238, coverage.getGenerated());
        assertEquals("3.3.1", coverage.getPhpUnitVersion());
        assertEquals(2, coverage.getFiles().size());

        CoverageImpl.FileImpl file = (CoverageImpl.FileImpl) coverage.getFiles().get(0);
        assertEquals("/home/gapon/NetBeansProjects/PhpProject01/src/hola/Calculator2.php5", file.getPath());
        assertEquals(2, file.getClasses().size());

        CoverageImpl.ClassImpl clazz = file.getClasses().get(0);
        assertEquals("Calculator2", clazz.getName());
        assertEquals("global", clazz.getNamespace());

        ClassMetricsImpl classMetrics = clazz.getMetrics();
        assertNotNull(classMetrics);
        assertEquals(11, classMetrics.getMethods());
        assertEquals(5, classMetrics.getCoveredMethods());
        assertEquals(3, classMetrics.getStatements());
        assertEquals(2, classMetrics.getCoveredStatements());
        assertEquals(7, classMetrics.getElements());
        assertEquals(6, classMetrics.getCoveredElements());

        assertEquals(4, file.getLines().size());
        CoverageImpl.LineImpl line = (CoverageImpl.LineImpl) file.getLines().get(0);
        assertEquals(11, line.getNumber());
        assertEquals("method", line.getType());
        assertEquals(1, line.getHitCount());

        FileMetricsImpl fileMetrics = (FileMetricsImpl) file.getMetrics();
        assertNotNull(fileMetrics);
        assertEquals(32, fileMetrics.getLineCount());
        assertEquals(18, fileMetrics.getNcloc());
        assertEquals(4, fileMetrics.getClasses());
        assertEquals(2, fileMetrics.getMethods());
        assertEquals(1, fileMetrics.getCoveredMethods());
        assertEquals(5, fileMetrics.getStatements());
        assertEquals(3, fileMetrics.getCoveredStatements());
        assertEquals(43, fileMetrics.getElements());
        assertEquals(25, fileMetrics.getCoveredElements());

        file = (CoverageImpl.FileImpl) coverage.getFiles().get(1);
        assertEquals("/home/gapon/NetBeansProjects/PhpProject01/src/Calculator.php", file.getPath());
        assertEquals(1, file.getClasses().size());
        assertEquals(6, file.getLines().size());

        line = (CoverageImpl.LineImpl) file.getLines().get(0);
        assertEquals(10, line.getNumber());
        assertEquals("method", line.getType());
        assertEquals(3, line.getHitCount());
        line = (CoverageImpl.LineImpl) file.getLines().get(5);
        assertEquals(19, line.getNumber());
        assertEquals("stmt", line.getType());
        assertEquals(0, line.getHitCount());

        CoverageMetricsImpl coverageMetrics = coverage.getMetrics();
        assertNotNull(coverageMetrics);
        assertEquals(2, coverageMetrics.getFiles());
        assertEquals(50, coverageMetrics.getLineCount());
        assertEquals(30, coverageMetrics.getNcloc());
        assertEquals(33, coverageMetrics.getClasses());
        assertEquals(1717, coverageMetrics.getMethods());
        assertEquals(665, coverageMetrics.getCoveredMethods());
        assertEquals(532, coverageMetrics.getStatements());
        assertEquals(443, coverageMetrics.getCoveredStatements());
        assertEquals(2344, coverageMetrics.getElements());
        assertEquals(1234, coverageMetrics.getCoveredElements());
    }

    public void testParseLogIssue180254() throws Exception {
        Reader reader = new BufferedReader(new FileReader(getCoverageLog("phpunit-coverage-issue180254.xml")));
        CoverageImpl coverage = new CoverageImpl();

        PhpUnitCoverageLogParser.parse(reader, coverage);

        assertEquals(1265274750, coverage.getGenerated());
        assertEquals("3.4.6", coverage.getPhpUnitVersion());
        assertEquals(20, coverage.getFiles().size());

        CoverageImpl.FileImpl file = (CoverageImpl.FileImpl) coverage.getFiles().get(0);
        assertEquals("/usr/local/zend/apache2/htdocs/mysgc/plugins/mcJobqueuePlugin/lib/jobhandler/McJobqueueTestjobHandler.php", file.getPath());
        assertEquals(1, file.getClasses().size());

        CoverageImpl.ClassImpl clazz = file.getClasses().get(0);
        assertEquals("McJobqueueTestjobHandler", clazz.getName());
        assertEquals("global", clazz.getNamespace());

        ClassMetricsImpl classMetrics = clazz.getMetrics();
        assertNotNull(classMetrics);
        assertEquals(1, classMetrics.getMethods());
        assertEquals(1, classMetrics.getCoveredMethods());
        assertEquals(2, classMetrics.getStatements());
        assertEquals(2, classMetrics.getCoveredStatements());
        assertEquals(3, classMetrics.getElements());
        assertEquals(3, classMetrics.getCoveredElements());

        assertEquals(5, file.getLines().size());
        CoverageImpl.LineImpl line = (CoverageImpl.LineImpl) file.getLines().get(0);
        assertEquals(10, line.getNumber());
        assertEquals("stmt", line.getType());
        assertEquals(1, line.getHitCount());

        FileMetricsImpl fileMetrics = (FileMetricsImpl) file.getMetrics();
        assertNotNull(fileMetrics);
        assertEquals(13, fileMetrics.getLineCount());
        assertEquals(7, fileMetrics.getNcloc());
        assertEquals(1, fileMetrics.getClasses());
        assertEquals(1, fileMetrics.getMethods());
        assertEquals(1, fileMetrics.getCoveredMethods());
        assertEquals(4, fileMetrics.getStatements());
        assertEquals(4, fileMetrics.getCoveredStatements());
        assertEquals(5, fileMetrics.getElements());
        assertEquals(5, fileMetrics.getCoveredElements());
    }

    private File getCoverageLog(String filename) throws Exception {
        File coverageLog = new File(getDataDir(), filename);
        assertTrue(coverageLog.isFile());
        return coverageLog;
    }

}
