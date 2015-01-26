/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.api.codemodel.providers;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.cnd.api.codemodel.CMDiagnostic;
import org.netbeans.modules.cnd.api.codemodel.CMIndex;
import org.netbeans.modules.cnd.api.codemodel.CMModel;
import org.netbeans.modules.cnd.api.codemodel.visit.CMDeclaration;
import org.netbeans.modules.cnd.api.codemodel.visit.CMEntityReference;
import org.netbeans.modules.cnd.api.codemodel.visit.CMInclude;
import org.netbeans.modules.cnd.api.codemodel.visit.CMReference;
import org.netbeans.modules.cnd.api.codemodel.visit.CMReferenceQuery;
import org.netbeans.modules.cnd.api.codemodel.visit.CMVisitQuery;
import org.netbeans.modules.cnd.api.project.NativeProject;
import org.netbeans.modules.cnd.codemodel.bridge.impl.NativeProjectCompilationDataBase;
import org.netbeans.modules.cnd.codemodel.storage.api.CMStorageManager;
import org.netbeans.modules.cnd.codemodel.storage.spi.CMStorage;
import org.netbeans.modules.cnd.spi.codemodel.providers.CMCompilationDataBase;
import org.netbeans.modules.cnd.spi.codemodel.support.SPIUtilities;
import org.netbeans.modules.cnd.spi.codemodel.trace.CMTraceUtils;
import org.netbeans.modules.cnd.test.CndBaseTestCase;
import org.netbeans.modules.nativeexecution.test.NativeExecutionTestSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mtishkov
 */
public class StorageProjectTest extends CndBaseTestCase {

    public StorageProjectTest(String test) {
        super(test);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int timeOut() {
        return Integer.MAX_VALUE;
    }

    public void testProject() throws Exception {
        //CMDataBase.compact();     
//        String nbProject = NativeExecutionTestSupport.getRcFile().get("new_codemodel", "cursor_visitor_test_project", "/export/home/masha/ssd/work/clucene-core-0.9.21b/");
        String nbProject = "/tmp/NetBeansProjects/Quote_1";
        //String nbProject = "/export/home/masha/ssd/work/clucene-core-0.9.21b/";        
        final File file = new File(nbProject);
        assertTrue("File " + file.getAbsolutePath() + " does not exist", file.exists());
        final Object key = file.getName();
        final String storageName = "CursorVisitorProjectTest.References." + key;
        String odbcUrl = NativeExecutionTestSupport.getRcFile().get("new_codemodel", "cursor_visitor_test_odbc_url", "jdbc:hsqldb");
        final CMStorage storage = CMStorageManager.getInstance(storageName, odbcUrl);
        
        try {
            long startQuertyTime = System.currentTimeMillis();
            storage.query("dd");
            long endQuertyTime = System.currentTimeMillis();
            long queryDuration = endQuertyTime - startQuertyTime;
            System.out.println("query duration=" + (queryDuration / 1000) + "." + (queryDuration % 1000) + " s ");
        }finally {
            storage.shutdown();            
        }

    }
}
