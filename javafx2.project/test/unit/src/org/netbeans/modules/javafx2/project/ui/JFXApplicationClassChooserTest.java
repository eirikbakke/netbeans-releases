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

package org.netbeans.modules.javafx2.project.ui;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import junit.framework.Test;
import org.netbeans.api.project.Project;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.ide.FXProjectSupport;
import org.netbeans.modules.java.j2seproject.api.J2SEPropertyEvaluator;
import org.netbeans.modules.parsing.api.indexing.IndexingManager;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.test.MockLookup;

/**
 *
 * @author Tomas Zezula
 */
public class JFXApplicationClassChooserTest extends NbTestCase {

    private static final int TIMEOUT = 100_000;

    private Project prj;
    private FileObject srcRoot;
    private Logger log;
    private Level level;
    private TestHandler handler;

    public JFXApplicationClassChooserTest(String name) {
        super(name);
    }

    public static Test suite() {
        return NbModuleSuite.
                emptyConfiguration().
                addTest(JFXApplicationClassChooserTest.class).
                clusters("javafx"). //NOI18N
                gui(false).
                suite();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        MockLookup.setLayersAndInstances();
        final File workDir = this.getWorkDir();
        prj = (Project)FXProjectSupport.createProject(workDir, "Test"); //NOI18N
        assertNotNull(prj);
        srcRoot = prj.getProjectDirectory().getFileObject("src");
        assertNotNull(srcRoot);
        log = Logger.getLogger(JFXApplicationClassChooser.class.getName());
        handler = new TestHandler();
        level = log.getLevel();
        log.setLevel(Level.FINE);
        log.addHandler(handler);
    }

    @Override
    protected void tearDown() throws Exception {
        if (log != null) {
            log.removeHandler(handler);
            log.setLevel(level);
            level = null;
            log = null;
        }
        super.tearDown(); //To change body of generated methods, choose Tools | Templates.
    }



    public void testSomeMethodNoScan() throws Exception {
        final J2SEPropertyEvaluator evalProvider = prj.getLookup().lookup(J2SEPropertyEvaluator.class);
        final PropertyEvaluator eval = evalProvider == null ? null : evalProvider.evaluator();
        assertNotNull(eval);
        createApplication("foo.A1");    //NOI18N
        IndexingManager.getDefault().refreshIndexAndWait(srcRoot.toURL(), null, true);
        handler.expect(new String[]{"foo.A1"});
        final JFXApplicationClassChooser panel = new JFXApplicationClassChooser(prj, eval);
        assertTrue(handler.await(TIMEOUT));
    }

    public void testSomeMethodScan() throws Exception {
        final J2SEPropertyEvaluator evalProvider = prj.getLookup().lookup(J2SEPropertyEvaluator.class);
        final PropertyEvaluator eval = evalProvider == null ? null : evalProvider.evaluator();
        assertNotNull(eval);
        createApplication("foo.A1");    //NOI18N
        IndexingManager.getDefault().refreshIndexAndWait(srcRoot.toURL(), null, true);
        handler.expect(new String[]{"foo.A1"}, new String[] {"foo.A1", "foo.A2"});
        try (final TestInterceptor ti = new TestInterceptor()) {
            handler.setInterceptor(ti);
            final JFXApplicationClassChooser panel = new JFXApplicationClassChooser(prj, eval);
            assertTrue(handler.await(TIMEOUT));
        }
    }


    private FileObject createApplication(String name) throws IOException {        
        int index = name.lastIndexOf('.');
        final String pkg;
        final String sn;
        if (index >= 0) {
            pkg = name.substring(0, index);
            sn = name.substring(index+1);
        } else {
            pkg = null;
            sn = name;
        }
        final FileObject file = FileUtil.createData(srcRoot, name.replace('.', '/') + ".java"); //NOI18N
        final FileLock lock = file.lock();
        try (final PrintWriter out = new PrintWriter(new OutputStreamWriter(
                file.getOutputStream(lock),
                FileEncodingQuery.getEncoding(file)))) {
            out.println(
                pkg == null ? "" :"package " + pkg +";\n" +             //NOI18N
                "import javafx.application.Application;\n" +            //NOI18N
                "import javafx.stage.Stage;\n" +                        //NOI18N
                "public class " + sn + " extends Application {\n" +     //NOI18N
                "    public void start(Stage primaryStage) {}\n"+       //NOI18N
                "}"                                                     //NOI18N
            );
        } finally {
            lock.releaseLock();
        }
        return file;
    }

    private static interface Interceptor {
        void onInit();
        void onAction();
    }

    private static final class TestHandler extends Handler {
        
        //@GuardedBy("this")
        private Deque<String[]> mainClasses;
        //@GuardedBy("this")
        private String failure;
        //@GuardedBy("this")
        private Interceptor interceptor;

        synchronized void expect(String[]... mainClasses) {
            this.mainClasses = new ArrayDeque<>(Arrays.asList(mainClasses));
            this.failure = null;
            this.interceptor = null;
        }

        synchronized void setInterceptor(Interceptor i) {
            interceptor = i;
        }

        synchronized boolean await(final long timeout) throws InterruptedException {
            long st = System.currentTimeMillis();
            while (!this.mainClasses.isEmpty()) {
                long time = System.currentTimeMillis() - st;
                if (time > timeout) {
                    if (failure != null) {
                        throw new AssertionError(failure);
                    } else {
                        return false;
                    }
                }
                wait(timeout - time);
            }
            if (failure != null) {
                throw new AssertionError(failure);
            } else {
                return true;
            }
        }

        @Override
        public synchronized void publish(LogRecord record) {
            final String message = record.getMessage();
            if (JFXApplicationClassChooser.LOG_INIT.equals(message)) {
                callInterceptor(0);
            } else if (JFXApplicationClassChooser.LOG_MAIN_CLASSES.equals(message)) {
                callInterceptor(1);
                final String[] result = ((Set<String>)record.getParameters()[0]).toArray(new String[0]);
                final boolean scan = (Boolean) record.getParameters()[1];
                final String[] expected = mainClasses.removeFirst();
                Arrays.sort(result);
                Arrays.sort(expected);
                final boolean last = mainClasses.isEmpty();
                if (last == scan) {
                    failure = "Expected " + (!last) + ", Result: " + scan;  //NOI18N
                    notifyAll();
                } else if (!Arrays.equals(expected, result)) {
                    failure = "Expected " + Arrays.toString(expected) + ", Result: " + Arrays.toString(result); //NOI18N
                    notifyAll();
                } else if (last) {
                    notifyAll();
                }                
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        private void callInterceptor(int type) {
            assert Thread.holdsLock(this);
            if (interceptor != null) {
                switch (type) {
                    case 0:
                        interceptor.onInit();
                        break;
                    case 1:
                        interceptor.onAction();
                        break;
                    default:
                        throw new IllegalArgumentException(Integer.toString(type));
                }
            }
        }
    }

    private final class TestInterceptor extends Handler implements Interceptor, Closeable {

        private final Logger RUL;
        private final Level level;

        //@GuardedBy("this")
        private boolean sleeeep;


        TestInterceptor() {
            RUL = Logger.getLogger("org.netbeans.ui.indexing");   //NOI18N
            level = RUL.getLevel();
            RUL.setLevel(Level.INFO);
            RUL.addHandler(this);
        }

        @Override
        public synchronized void onInit() {
            try {
                sleeeep = true;
                createApplication("foo.A2");    //NOI18N
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public synchronized void onAction() {
            sleeeep = false;
            notifyAll();
        }

        @Override
        public synchronized void publish(LogRecord record) {
            if ("INDEXING_STARTED".equals(record.getMessage())) {   //NOI18N
                try {
                    while (sleeeep) {
                        wait();
                    }
                } catch (InterruptedException ie) {
                    Exceptions.printStackTrace(ie);
                }
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
            RUL.removeHandler(this);
            RUL.setLevel(level);
        }
    }

}
