/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.parsing.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.test.MockMimeLookup;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.parsing.api.IndexingAwareTestCase;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tomas Zezula
 */
public class SchedulerEventTest extends IndexingAwareTestCase {

    private static final Logger LOG = Logger.getLogger(SchedulerEventTest.class.getName());
    private static final String EXT_FOO = "foo";            //NOI18N
    private static final String MIME_FOO = "text/x-foo";    //NOI18N
    private static final long TIMEOUT = Long.getLong("SchedulerEventTest.timeout", 5000);   //NOI18N

    private FileObject sourceFile;
    private Source source;

    public SchedulerEventTest(final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        MockServices.setServices(Scheduler1.class, Scheduler2.class);
        MockMimeLookup.setInstances(
                MimePath.get(MIME_FOO),
                new FooParser.Factory(),
                new Task1.Factory(),
                new Task2.Factory());        
        FileUtil.setMIMEType(EXT_FOO, MIME_FOO);
        final File wd = getWorkDir();
        sourceFile = FileUtil.createData(new File(wd, "source.foo"));   //NOI18N
        assertNotNull(sourceFile);
        assertEquals(MIME_FOO, sourceFile.getMIMEType());
        source = Source.create(sourceFile);
        assertNotNull(source);
        Modification.getDefault().setSource(source);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown(); //To change body of generated methods, choose Tools | Templates.
    }


    public void testScheduleEventsNonNull_218756() throws Exception {
        final Modification mod = Modification.getDefault();
        mod.expect(Task1.class, 0);
        mod.expect(Task2.class, 0);
        for (Scheduler scheduler : Schedulers.getSchedulers ()) {
            if (scheduler instanceof BaseScheduler) {
                ((BaseScheduler) scheduler).schedule (source);
            }
        }
        assertTrue(mod.await());        
        mod.expect(Task1.class, 1);
        mod.expect(Task2.class, 1);
        mod.doChange();
        assertTrue(mod.await());
        
    }

    private static final class Modification {

        static final String PROP_ID = "id"; //NOI18N
        private static Modification instance;

        private final PropertyChangeSupport support;
        private final Map<Class<? extends ParserResultTask<? extends Parser.Result>>,Long> toExpect;
        private long id;
        private Source source;

        private Modification() {
            this.id = 0;
            this.toExpect = new HashMap<Class<? extends ParserResultTask<? extends Parser.Result>>, Long>();
            this.support = new PropertyChangeSupport(this);
        }

        public void addPropertChangeListener(PropertyChangeListener l) {
            support.addPropertyChangeListener(l);
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
            support.removePropertyChangeListener(l);
        }

        void doChange() {
            id++;
            support.firePropertyChange(PROP_ID, null, id);
        }

        void setSource(Source source) {
            this.source = source;
        }

        Source getSource() {
            return source;
        }

        synchronized void reset() {
            toExpect.clear();
        }

        synchronized void expect(
            final Class<? extends ParserResultTask<? extends Parser.Result>> task,
            final long id) {
           toExpect.put(task,id);
        }

        synchronized boolean await() throws InterruptedException {
            long st = System.currentTimeMillis();
            while (!toExpect.isEmpty()) {
                wait(TIMEOUT);
                if (System.currentTimeMillis() - st >= TIMEOUT) {
                    return false;
                }
            }
            return true;
        }

        synchronized void update(
            final Class<? extends ParserResultTask<? extends Parser.Result>> task,
            final SchedulerEvent event) {
            LOG.log(
               Level.FINE,
               "UPDATE: {0} {1}",   //NOI18N
               new Object[]{
                   task.getSimpleName(),
                   event
               });
            if (event instanceof BaseScheduler.Event) {
                final Long expectedId = toExpect.get(task);
                if (expectedId != null && expectedId == ((BaseScheduler.Event)event).getId()) {
                    toExpect.remove(task);
                    notify();
                }
            }
        }



        static synchronized Modification getDefault() {
            if (instance == null) {
                instance = new Modification();
            }
            return instance;
        }

    }

    public abstract static class BaseScheduler extends Scheduler implements PropertyChangeListener {

        protected long id;

        public BaseScheduler() {
            final Modification m = Modification.getDefault();
            m.addPropertChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (Modification.PROP_ID.equals(evt.getPropertyName())) {
                id = (Long) evt.getNewValue();
                schedule(Modification.getDefault().getSource(), createSchedulerEvent(null));
            }
        }

        void schedule (Source source) {
            schedule (source, createSchedulerEvent(null));
        }


        protected static final class Event extends SchedulerEvent {

            private final long id;

            Event(Object source, long id) {
                super(source);
                this.id = id;
            }

            long getId() {
                return id;
            }

            @Override
            public String toString() {
                return String.format("BaseScheduler.Event[id=%d]", id); //NOI18N
            }
        }

    }

    public static final class Scheduler1 extends BaseScheduler  {
        
        @Override
        protected SchedulerEvent createSchedulerEvent(SourceModificationEvent event) {
            return new Event(this, id);
        }
    }

    public static final class Scheduler2 extends BaseScheduler {

        @Override
        protected SchedulerEvent createSchedulerEvent(SourceModificationEvent event) {
            return new Event(this, id);
        }        
    }

    public static class FooParser extends Parser {

        private R result;

        private FooParser() {}

        @Override
        public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException {
            this.result = new R(snapshot);
        }

        @Override
        public Result getResult(Task task) throws ParseException {
            assert result != null;
            return result;
        }

        @Override
        public void addChangeListener(ChangeListener changeListener) {
        }

        @Override
        public void removeChangeListener(ChangeListener changeListener) {
        }

        private static class R extends Parser.Result {

            R (Snapshot s) {
                super(s);
            }

            @Override
            protected void invalidate() {
            }

        }

        public static class Factory extends ParserFactory {
            @Override
            public Parser createParser(Collection<Snapshot> snapshots) {
                return new FooParser();
            }

        }

    }

    public static class Task1 extends ParserResultTask<FooParser.R> {

        private Task1() {}

        @Override
        public void run(FooParser.R result, SchedulerEvent event) {
            Modification.getDefault().update(this.getClass(), event);
        }

        @Override
        public int getPriority() {
            return 10;
        }

        @Override
        public Class<? extends Scheduler> getSchedulerClass() {
            return Scheduler1.class;
        }

        @Override
        public void cancel() {
        }

        public static class Factory extends TaskFactory {
            @Override
            public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
                return Collections.<SchedulerTask>singleton(new Task1());
            }

        }
    }

    public static class Task2 extends ParserResultTask<FooParser.R> {

        private Task2() {}

        @Override
        public void run(FooParser.R result, SchedulerEvent event) {
            Modification.getDefault().update(this.getClass(), event);
        }

        @Override
        public int getPriority() {
            return 10;
        }

        @Override
        public Class<? extends Scheduler> getSchedulerClass() {
            return Scheduler2.class;
        }

        @Override
        public void cancel() {
        }

        public static class Factory extends TaskFactory {
            @Override
            public Collection<? extends SchedulerTask> create(Snapshot snapshot) {
                return Collections.<SchedulerTask>singleton(new Task2());
            }
        }
    }
}
