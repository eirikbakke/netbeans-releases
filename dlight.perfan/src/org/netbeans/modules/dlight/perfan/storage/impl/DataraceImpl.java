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
package org.netbeans.modules.dlight.perfan.storage.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.dlight.core.stack.api.FunctionCall;
import org.netbeans.modules.dlight.core.stack.api.ThreadDump;
import org.netbeans.modules.dlight.core.stack.api.ThreadSnapshot;
import org.netbeans.modules.dlight.core.stack.api.ThreadSnapshot.MemoryAccessType;
import org.netbeans.modules.dlight.core.stack.api.ThreadInfo;
import org.netbeans.modules.dlight.core.stack.api.ThreadState.MSAState;
import org.netbeans.modules.dlight.perfan.stack.impl.FunctionCallImpl;
import org.netbeans.modules.dlight.threads.api.Datarace;

/**
 * @author Alexey Vladykin
 */
public final class DataraceImpl implements Datarace {

    private final int id;
    private final long address;
    private String stringAddress;
    private final List<ThreadDump> dumps;

    public DataraceImpl(int id, long address, List<ThreadDump> dumps) {
        this.id = id;
        this.address = address;
        this.dumps = dumps;
    }

    void setStringAddress(String address){
        this.stringAddress = address;
    }

    public long getAddress() {
        return address;
    }

    public String stringAddress() {
        return stringAddress;
    }

    
    

    public List<ThreadDump> getThreadDumps() {
        return dumps;
    }

    @Override
    public String toString() {
        return "Datarace #" + id; // NOI18N
    }


    private static final Pattern RACE_PATTERN = Pattern.compile("Race\\s+#(\\d+),\\s+Vaddr:\\s+(0x(.+)|\\(Multiple\\s+Addresses\\)|Multiple\\s+addresses)"); // NOI18N
    private static final Pattern DUMP_PATTERN = Pattern.compile("\\s+.*Trace\\s+\\d+"); // NOI18N
    private static final Pattern SNAPSHOT_PATTERN = Pattern.compile(" *Access +\\d+: +(Read|Write) *"); // NOI18N

    public static List<DataraceImpl> fromErprint(String[] lines) {
        List<DataraceImpl> dataraces = new ArrayList<DataraceImpl>();
        ListIterator<String> it = Arrays.asList(lines).listIterator();
        while (it.hasNext()) {
            Matcher m = RACE_PATTERN.matcher(it.next());
            if (m.matches()) {
                dataraces.add(parseDatarace(it, m));
            }
        }
        return dataraces;
    }

    private static DataraceImpl parseDatarace(ListIterator<String> it, Matcher firstLineMatch) {
        int id = -1;
        try {
            id = Integer.parseInt(firstLineMatch.group(1));
        } catch (NumberFormatException ex) {
        }

        long vaddr = -1;
        String stringAddress = firstLineMatch.group(2);
        if (firstLineMatch.group(3) != null) {
            try {
                vaddr = Long.parseLong(firstLineMatch.group(3), 16);
            } catch (NumberFormatException ex) {                
            }
        }

        List<ThreadDump> dumps = new ArrayList<ThreadDump>();
        while (it.hasNext()) {
            String line = it.next();
            Matcher m = DUMP_PATTERN.matcher(line);
            if (m.matches()) {
                dumps.add(parseThreadDump(it, m));
            } else if (!dumps.isEmpty()) {
                break;
            }
        }
        DataraceImpl dataRace = new DataraceImpl(id, vaddr, dumps);
        dataRace.setStringAddress(stringAddress);
        return dataRace;
    }

    private static ThreadDump parseThreadDump(ListIterator<String> it, Matcher firstLineMatch) {
        List<ThreadSnapshot> threads = new ArrayList<ThreadSnapshot>();
        while (it.hasNext()) {
            String line = it.next();
            Matcher m = SNAPSHOT_PATTERN.matcher(line);
            if (m.matches()) {
                threads.add(parseThreadSnapshot(it, m));
            } else {
                it.previous();
                break;
            }
        }
        return new ThreadDumpImpl(threads);
    }

    private static ThreadSnapshot parseThreadSnapshot(ListIterator<String> it, Matcher firstLineMatch) {
        MemoryAccessType memAccessType = MemoryAccessType.valueOf(firstLineMatch.group(1).toUpperCase());
        List<FunctionCall> stack = FunctionCallImpl.parseStack(it);
        return new ThreadSnapshotImpl(stack, null, memAccessType);
    }

    private static class ThreadDumpImpl implements ThreadDump {

        private final List<ThreadSnapshot> threads;

        public ThreadDumpImpl(List<ThreadSnapshot> threads) {
            this.threads = threads;
        }

        public long getTimestamp() {
            return -1;
        }

        public List<ThreadSnapshot> getThreadStates() {
            return threads;
        }
    }

    private static class ThreadSnapshotImpl implements ThreadSnapshot {

        private final List<FunctionCall> stack;
        private final ThreadInfo threadInfo;
        private final MemoryAccessType memoryAccessType;

        public ThreadSnapshotImpl(List<FunctionCall> stack, ThreadInfo threadInfo, MemoryAccessType memoryAccessType) {
            this.stack = stack;
            this.threadInfo = threadInfo;
            this.memoryAccessType = memoryAccessType;
        }

        public List<FunctionCall> getStack() {
            return stack;
        }

        public ThreadInfo getThreadInfo() {
            return threadInfo;
        }

        public MSAState getState() {
            return MSAState.Running;
        }

        public MemoryAccessType getMemoryAccessType() {
            return memoryAccessType;
        }

        public long getTimestamp() {
            return 0;
        }
        }
    }
