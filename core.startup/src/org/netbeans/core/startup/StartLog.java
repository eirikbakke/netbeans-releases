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

package org.netbeans.core.startup;

import java.util.Stack;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.netbeans.TopSecurityManager;

/** Logger that will enable the logging of important events during the startup
 * annotated with real time and possibly time differences.
 *
 * @author Petr Nejedly, Jesse Glick
 */
public class StartLog {
    private static final Logger LOG = Logger.getLogger("org.netbeans.log.startup"); // NOI18N
    private static final Stack<String> actions = new Stack<String>();
    private static final Stack<Throwable> places = new Stack<Throwable>();
    private static final boolean DEBUG_NESTING = Boolean.getBoolean("org.netbeans.log.startup.debug"); // NOI18N
    
    private static final String logProp; 
    private static final String logFileProp;
    static final Handler impl;
    
    static {
        logProp = System.getProperty( "org.netbeans.log.startup" ); // NOI18N
        //if you want to create file with measured values, we do expect the property org.netbeans.log.startup=print
        logFileProp = System.getProperty( "org.netbeans.log.startup.logfile" ); // NOI18N

        if(logProp == null)
            impl = new StartImpl();
        else if("print".equals(logProp))
            impl = new PrintImpl();
        else if("tests".equals(logProp))
            impl = new PerformanceTestsImpl();
        else
            throw new Error("Unknown org.netbeans.log.startup value [" + logProp + "], it should be (print or tests) !"); // NOI18N
        register();
    }
    
    static void register() {
        LOG.setUseParentHandlers(false);
        LOG.addHandler(impl);
        LOG.setLevel(impl.getLevel());
    }
    
    /** Start running some interval action.
     * @param action some identifying string
     */
    public static void logStart( String action ) {
        if (willLog()) {
            LOG.log(Level.FINE, "start", action); // NOI18N
            actions.push(action);
            if (DEBUG_NESTING) {
                places.push(new Throwable("logStart called here:")); // NOI18N
            }
        }
    }

    /** Note that something happened, but not an interval.
     * The log will note only the time elapsed since the last interesting event.
     * @param note some identifying string
     */
    public static void logProgress( String note ) {
        if( willLog() ) {
            LOG.log(Level.FINE, "progress", note); // NOI18N
        }
    }

    /** Stop running some interval action.
     * The log will note the time elapsed since the start of the action.
     * Actions <em>must</em> be properly nested.
     * @param action some identifying string
     */
    public static void logEnd( String action ) {
        if (willLog()) {
            String old = actions.empty() ? null : actions.pop();
            Throwable oldplace = DEBUG_NESTING && !places.empty() ? places.pop() : null;
            if (!action.equals(old)) {
                // Error, not ISE; don't want this caught and reported
                // with ErrorManager, for then you get a wierd cycle!
                if (oldplace != null) {
                    oldplace.printStackTrace();
                } else {
                    System.err.println("Either ending too soon, or no info about caller of unmatched start log."); // NOI18N
                    System.err.println("Try running with -J-Dorg.netbeans.log.startup.debug=true"); // NOI18N
                }
                Error e = new Error("StartLog mismatch: ending '" + action + "' but expecting '" + old + "'; rest of stack: " + actions); // NOI18N
                // Print stack trace since you can get strange situations
                // when ErrorManager tries to report it - may need to initialize
                // ErrorManager, etc.
                e.printStackTrace();
                // Presumably you did want to keep on going at this point.
                System.err.flush();
                LOG.setLevel(Level.OFF);
            }
            LOG.log(Level.FINE, "end", action); // NOI18N
        }
    }

    public static boolean willLog() {
        return LOG.isLoggable(Level.FINE);
    }
    
    /** Logs the startup time. The begining is tracked by this class. 
     *  The end is passed as argument.
     */
    public static void logMeasuredStartupTime(long end){
        LOG.log(Level.FINE, "finish", end);
        if("tests".equals(logProp)) {
            impl.flush();
        }            
    }
    
    /** The dummy, no-op implementation */
    private static class StartImpl extends Handler {
        final long zero = System.nanoTime()/1000000;
        
        StartImpl() {}
        void start( String action, long time ) {}
        void progress( String note, long time ) {}
        void end( String action, long time ) {}
        boolean willLog() {
            return false;
        }

        @Override
        public Level getLevel() {
            return willLog() ? Level.FINEST : Level.OFF;
        }


        @Override
        public void publish(LogRecord rec) {
            Object[] args = rec.getParameters();
            String msg = (args.length >= 1 && args[0] instanceof String) ? (String)args[0] : ""; // NOI18N
            long time = System.nanoTime()/1000000;
            if ("start".equals(rec.getMessage())) { // NOI18N
                start(msg, time);
                return;
            }
            if ("end".equals(rec.getMessage())) { // NOI18N
                end(msg, time);
                return;
            }
            if ("progress".equals(rec.getMessage())) { // NOI18N
                progress(msg, time);
                return;
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public final void close() throws SecurityException {
        }
    }

    private static class PrintImpl extends StartImpl {
        PrintImpl() {}
        private Stack<Long> starts = new Stack<Long>();
        long prog;
        private int indent = 0;
        
        @Override
        synchronized void start( String action, long time ) {
            starts.push(time);
            prog=time;
            System.err.println( getIndentString(indent) + "@" + 
                    (time - zero) + " - " + action + " started" // NOI18N
            );
            indent += 2;
        }
        
        @Override
        synchronized void progress( String note, long time ) {
            System.err.println( getIndentString(indent) + "@" + 
                    (time - zero) + " - " + note + " dT=" + (time - prog) // NOI18N
            );
            prog = time;
        }
        
        @Override
        synchronized void end( String action, long time ) {
            indent -= 2;
            long start = starts.pop();
            prog = time;
            System.err.println( getIndentString(indent) + "@" + 
                    (time - zero) + " - " + action + " finished, took " + // NOI18N
                    (time - start) + "ms" // NOI18N
            );
        }
        
        @Override
        boolean willLog() {
            return true;
        }
        
        private char[] spaces = new char[0];
        private String getIndentString( int indent ) {
            if( spaces.length < indent ) {
                spaces = new char[Math.max( spaces.length*2, indent+10 )];
                Arrays.fill( spaces, ' ');
            }
            return new String(spaces,0, indent);
        }
    }

    private static class PerformanceTestsImpl extends StartImpl {
        private StringBuffer logs = new StringBuffer();
        private Stack<Long> starts = new Stack<Long>();
        long prog;
        private int indent = 0;
        
        PerformanceTestsImpl() {}
        
        @Override
        synchronized void start( String action, long time ) {
            starts.push(time);
            prog=time;
            log(getIndentString(indent) + "@" + 
                    (time - zero) + " - " + action + " started" // NOI18N
            );
            indent += 2;
        }
        
        @Override
        synchronized void progress( String note, long time ) {
            log(getIndentString(indent) + "@" + 
                    (time - zero) + " - " + note + " dT=" + (time - prog) // NOI18N
            );
            prog = time;
        }
        
        @Override
        synchronized void end( String action, long time ) {
            indent -= 2;
            long start = starts.pop();
            prog = time;
            log(getIndentString(indent) + "@" + 
                    (time - zero) + " - " + action + " finished, took " + // NOI18N
                    (time - start) + "ms" // NOI18N
            );
        }
        
        @Override
        boolean willLog() {
            return true;
        }
        
        private char[] spaces = new char[0];
        private String getIndentString( int indent ) {
            if( spaces.length < indent ) {
                spaces = new char[Math.max( spaces.length*2, indent+10 )];
                Arrays.fill( spaces, ' ');
            }
            return new String(spaces,0, indent);
        }
        
        synchronized void log(String log){
            logs.append("\n" + log);
        }

        @Override
        public void publish(LogRecord rec) {
            super.publish(rec);
            if ("finish".equals(rec.getMessage())) { // NOI18N
                long end = (Long)rec.getParameters()[0];
                log("IDE starts t = " + Long.toString(zero) + "\nIDE is running t=" + Long.toString(end) + "\n");
            }
        }
        
        @Override
        public synchronized void flush(){
            if(logFileProp!=null){
                try{
                    java.io.File logFile = new java.io.File(logFileProp);
                    java.io.FileWriter writer = new java.io.FileWriter(logFile);
                    writer.write(logs.toString());
                    writer.close();
                }catch (Exception exc){
                    System.err.println("EXCEPTION rises during startup logging:");
                    exc.printStackTrace(System.err);
                }
            } else
                throw new IllegalStateException("You are trying to log startup logs to unexisting file. You have to set property org.netbeans.log.startup.logfile.");
        }

    }
}
