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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.debugger.common2.debugger;

import java.util.Iterator;
import org.netbeans.modules.cnd.debugger.common2.DbgActionHandler;
import org.netbeans.modules.cnd.debugger.common2.debugger.io.IOPack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.netbeans.modules.cnd.spi.remote.RemoteSyncFactory;

import org.openide.text.Line;

import org.netbeans.api.debugger.DebuggerEngine;

import org.netbeans.spi.debugger.ContextProvider;

import org.netbeans.modules.cnd.api.remote.PathMap;
import org.netbeans.modules.cnd.api.toolchain.CompilerFlavor;
import org.netbeans.modules.cnd.api.toolchain.CompilerSet;
import org.netbeans.modules.cnd.api.toolchain.CompilerSetManager;
import org.netbeans.modules.cnd.api.toolchain.PredefinedToolKind;
import org.netbeans.modules.cnd.api.toolchain.Tool;
import org.netbeans.modules.cnd.api.toolchain.ui.BuildToolsAction;
import org.netbeans.modules.cnd.api.toolchain.ui.LocalToolsPanelModel;
import org.netbeans.modules.cnd.api.toolchain.ui.ToolsPanelModel;
import org.netbeans.modules.cnd.makeproject.api.configurations.Configuration;
import org.netbeans.modules.cnd.makeproject.api.configurations.MakeConfiguration;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;

import org.netbeans.modules.cnd.debugger.common2.utils.options.OptionLayers;
import org.netbeans.modules.cnd.debugger.common2.utils.options.OptionSet;
import org.netbeans.modules.cnd.debugger.common2.utils.ListMap;
import org.netbeans.modules.cnd.debugger.common2.utils.IpeUtils;
import org.netbeans.modules.cnd.debugger.common2.utils.FileMapper;

import org.netbeans.modules.cnd.debugger.common2.debugger.options.DebuggerOption;

import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.NativeBreakpoint;

import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.Handler;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.Context;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.Gen;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.BreakpointManager;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.BreakpointProvider;

import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.DisFragModel;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.Controller;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.BreakpointModel;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.StateModel;

import org.netbeans.modules.cnd.debugger.common2.debugger.remote.Host;

import org.netbeans.modules.cnd.debugger.common2.capture.ExternalStartManager;
import org.netbeans.modules.cnd.debugger.common2.capture.CaptureInfo;
import org.netbeans.modules.cnd.debugger.common2.capture.ExternalStart;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.Disassembly;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.DisassemblyUtils;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.MemoryWindow;
import org.netbeans.modules.cnd.debugger.common2.debugger.assembly.RegistersWindow;
import org.netbeans.modules.cnd.debugger.common2.debugger.breakpoints.types.InstructionBreakpoint;
import org.netbeans.modules.cnd.debugger.common2.utils.Executor;
import org.netbeans.modules.cnd.makeproject.api.configurations.CompilerSet2Configuration;
import org.netbeans.modules.cnd.spi.toolchain.CompilerSetFactory;
import org.openide.util.actions.SystemAction;

/**
 * Stuff that is common to native DebuggerImpl's.
 */
public abstract class NativeDebuggerImpl implements NativeDebugger, BreakpointProvider {

    private final BreakpointManager bm;

    protected ContextProvider ctxProvider;	// for lookup
    protected DebuggerEngine debuggerEngine;	// corresponding engine
    protected NativeSession session;	// corresponding (our) session object
    protected DebuggerSettingsBridge profileBridge;

    // turned on when postKill is called
    protected boolean postedKill = false;

    // turned on when killEngine is issued
    protected boolean postedKillEngine = false;

    protected FileMapper fmap = FileMapper.getDefault();

    protected Location visitedLocation = null;

    protected final DebuggerAnnotation visitMarker;
    protected final DebuggerAnnotation currentPCMarker;
    protected final DebuggerAnnotation currentDisPCMarker;

    private boolean srcOOD;
    private String srcOODMessage = null;
    protected ListMap<WatchVariable> watches = new ListMap<WatchVariable>();

    // local stuff
    protected ModelChangeDelegator localUpdater = new ModelChangeDelegator();
    private boolean showAutos = false;
    protected final ArrayList<Variable> autos = new ArrayList<Variable>();

    // stack stuff
    protected Frame[] guiStackFrames = null;
    protected Frame currentFrame;
    protected ModelChangeDelegator stackUpdater = new ModelChangeDelegator();

    // thread stuff
    protected ModelChangeDelegator threadUpdater = new ModelChangeDelegator();

    // assembly level stuff
    private boolean srcRequested = true;
    private StateModelAdaptor disStateModel = new StateModelAdaptor();
    private InstBreakpointModel breakpointModel = new InstBreakpointModel();

    protected Executor executor;

    protected NativeDebuggerImpl(ContextProvider ctxProvider) {
        this.ctxProvider = ctxProvider;
	this.bm = new BreakpointManager(this, this);
        this.debuggerEngine = ctxProvider.lookupFirst(null, DebuggerEngine.class);

        // Instantiate our session object
        //
        // This will find the
        // org.netbeans.modules.cnd.debugger.common2.debugger.NativeSession
        // registered in META-INF/debugger/netbeans-[Dbx|Gdb]Session
        //
        // We allocate it indirectly via lookup because then it has the
        // side-effect of registering the session object in 'ctxProvider'

        session = ctxProvider.lookupFirst(null, NativeSession.class);

        assert session != null : "NativeDebuggerImpl created session";
        session.setDebugger((NativeDebugger) this);

        currentPCMarker =
                new DebuggerAnnotation(null,
                DebuggerAnnotation.TYPE_CURRENT_PC,
                null,
                true);
        currentDisPCMarker =
                new DebuggerAnnotation(null,
                DebuggerAnnotation.TYPE_CURRENT_PC,
                null,
                true);
        visitMarker =
                new DebuggerAnnotation(null,
                DebuggerAnnotation.TYPE_CALLSITE,
                null,
                true);
    }

    // interface NativeDebugger
    public final NativeSession session() {
        return session;
    }

    // interface NativeDebugger
    public final DebuggerManager manager() {
        return DebuggerManager.get();
    }

    // interface NativeDebugger
    public final BreakpointManager bm() {
	return bm;
    }

    /**
     * Are we the current session?
     */

    // interface NativeDebugger
    public boolean isCurrent() {
        return manager().currentNativeDebugger() == this;
    }


    // interface NativeDebugger
    public DebuggerSettingsBridge profileBridge() {
        return profileBridge;
    }

    public Context context() {
        final String executable = session().getTarget();
        final String hostname = session().getSessionHost();
        return new Context(executable, hostname);
    }

    private IOPack ioPack;

    protected void setIOPack(IOPack ioPack) {
        this.ioPack = ioPack;
    }

    public IOPack getIOPack() {
        return ioPack;
    }

    private Host host = null;

    public ExecutionEnvironment getExecutionEnvironment() {
        return executor.getExecutionEnvironment();
    }

    public Host getHost() {
        if (host == null) {
            host = Host.byName(getNDI().getHostName());
        }
        return host;
    }

    private PathMap cachedPathMap;
    private boolean lookedPathMap;

    private PathMap getPathMap() {
	if (!lookedPathMap) {
	    lookedPathMap = true;
	    Configuration conf = getNDI().getConfiguration();
	    if (! (conf instanceof MakeConfiguration)) {
		cachedPathMap = null;
	    } else {
		MakeConfiguration mc = (MakeConfiguration) conf;
		ExecutionEnvironment ee = mc.getDevelopmentHost().getExecutionEnvironment();                
                RemoteSyncFactory syncFactory = mc.getRemoteSyncFactory();                
		cachedPathMap = (syncFactory == null) ? null : syncFactory.getPathMap(ee);
	    }
	}
	return cachedPathMap;
    }

    public final String localToRemote(String who, String path) {
	String mapped;
	if (path == null) {
	    mapped = path;
	} else if (DebuggerManager.isStandalone()) {
	    mapped = path;
	} else {
	    PathMap pm = getPathMap();
	    if (pm == null) {
		mapped = path;
	    } else {
		mapped = pm.getRemotePath(path);
		if (mapped == null) {
		    // no mapping was found
		    mapped = path;
		}
	    }
	}
	if (Log.PathMap.debug) {
	    System.out.printf("localToRemote - %s\n", who); // NOI18N
	    System.out.printf("    from: %s\n", path); // NOI18N
	    System.out.printf("      to: %s\n", mapped); // NOI18N
	}
	return mapped;
    }

    public final String remoteToLocal(String who, String path) {
	String mapped;
	if (path == null) {
	    mapped = path;
	} else if (DebuggerManager.isStandalone()) {
	    mapped = path;
	} else {
	    PathMap pm = getPathMap();
	    if (pm == null) {
		mapped = path;
	    } else {
		mapped = pm.getLocalPath(path);
		if (mapped == null) {
		    // no mapping was found
		    mapped = path;
		}
	    }
	}
	if (Log.PathMap.debug) {
	    System.out.printf("remoteToLocal - %s\n", who); // NOI18N
	    System.out.printf("    from: %s\n", path); // NOI18N
	    System.out.printf("      to: %s\n", mapped); // NOI18N
	}
	return mapped;
    }

    /*
     * Return true if we will be loading a program after a successful 
     * connection to the engine.
     * If not, the progress dialog has to be brought down as soon as we have a 
     * connection.
     */
    public final boolean willBeLoading() {
        if (getNDI() == null) {
            return false;
        } else if (IpeUtils.isEmpty(getNDI().getTarget()) &&
                IpeUtils.isEmpty(getNDI().getCorefile()) &&
                getNDI().getPid() == -1) {
            return false;
        } else {
            return true;
        }
    }

    //
    // Session stuff
    // 
    /**
     * Update session data for session associated with this engine
     */

    // interface NativeDebugger
    public void invalidateSessionData() {
        if (session != null) // FIXUP: Ivan, check this....
        {
            session.update();
        }
    }

    /**
     * Trampoline to DebuggerManager.statusDisplayer.setStatusText()
     */
    public void setStatusText(String text) {
        manager().setStatusText(text);
    }

    /*
     * Action sensitization and state stuff
     *
     * Compared to sierra/orion & rainier ...
     * - we're banking that the actual cost of action enabling is low
     *   enough that we can afford redundant calls and forego the
     *   bitset delta detection that we used to do.
     * - debuggercore now multiplexes action enabledness between sessions
     *   for us so all the accomodation and worries about that are gone.
     */
    protected final List<StateListener> actions = new LinkedList<StateListener>();
    protected javax.swing.Timer runTimer;		// see stateSetRunning()
    protected int runDelay = -1;	// -1 == first time through

    private final State state = new State();	// individual state values are mostly
    					// set by Dbx

    private ActionEnabler actionEnabler() {
        return DebuggerManager.actionEnabler();
    }

    public State state() {
        return state;
    }

    public void addStateListener(StateListener sl) {
        actions.add(sl);
    }

    protected void removeStateListener(StateListener sl) {
        actions.remove(sl);
    }

    /**
     * Declare that a bunch of state variables have changed
     */
    public final void stateChanged() {

        //System.out.println("STATE CHANGED @@@@@@@@@ " + state);
        if (isCurrent()) {
            updateActions();
        } else {
            // When user switches sessions by hand then we'll adjust
            // the actions.
        }
    }

    /**
     * Declare that the pid of the process being debugged changed
     */
    public final void pidChanged() {
        // Cause bpt context info to get re-pulled
        NativeBreakpoint[] bpts = bm().breakpointBag().getBreakpoints();
        for (NativeBreakpoint b : bpts) {
            b.updateFor(this);
        }
    }

    /**
     * Sensitive/desensitize all actions according to state.
     * Each action decides on it's own what it needs to do.
     */
    protected void updateActions() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // update explicitly registered actions
                for (StateListener action : actions) {
                    action.update(state);
                }

                // update actions managed by ActionEnabler
                actionEnabler().update(state);
            }
        });
    }

    /**
     * Get the delay (in milliseconds) to use for GUI refreshes.
     * Can be overriden by the property "spro.rundelay".
     */
    private int getRunDelay() {
        if (runDelay == -1) {
            runDelay = 250; // 0.25 seconds
            String delay = System.getProperty("spro.rundelay"); // NOI18N
            if (delay != null) {
                try {
                    runDelay = Integer.parseInt(delay);
                } catch (java.lang.NumberFormatException e) {
                }
            }
        }
        return runDelay;
    }

    /*
     * Clients don't modify State.isRunning directly, but rather through
     * this method which uses a delay mechanism so as to not cause
     * gui button flashing if resumption is of short duration.
     */
     public final void stateSetRunning(boolean running) {

        if (!running) {
            // cancel timer
            if (runTimer != null) {
                runTimer.stop();
            }
            // really set the state and update
            state.isRunning = false;
            updateActions();

        } else {
            // start timer
            if (runTimer == null) {
                runTimer = new javax.swing.Timer(getRunDelay(),
                        new ActionListener() {

                            public void actionPerformed(ActionEvent evt) {
                                // set state and update when timer expires
                                state.isRunning = true;
                                updateActions();
                            }
                        });
                runTimer.setRepeats(false);
                runTimer.setCoalesce(true);
                runTimer.start();
            } else {
                runTimer.restart();
            }
        }
    }

    protected final boolean stateFromProg() {
        return !state.isCore && !state.isAttach;
    }


    // interface NativeDebugger as well.
    public abstract String debuggerType();


    protected static class WatchJob {

        private Kind kind;
        private int routingToken;
        private WatchVariable target;
        private NativeWatch template;
        private boolean primaryChange;

        public enum Kind {

            NEW,
            RESTORE,
            REPLACE,
            SPONTANEOUS,
            DELETE
        };

        public WatchJob(Kind kind, WatchVariable target, NativeWatch template) {
            this.kind = kind;
            this.target = target;
            this.template = template;
        }

        public void print() {
            if (!Log.Watch.pathway) {
                return;
            }
            System.out.printf("WatchJob %d %s: [%s]<[%s]\n" + // NOI18N
                    "spread %b\n", // NOI18N
                    routingToken, kind, target, template,
                    primaryChange);
        }

        public Kind kind() {
            return kind;
        }

        public void setKind(Kind k) {
            kind = k;
        }

        public WatchVariable target() {
            return target;
        }

        public NativeWatch template() {
            return template;
        }

        public void setPrimaryChange(boolean primaryChange) {
            this.primaryChange = primaryChange;
        }

        public boolean isPrimaryChange() {
            return primaryChange;
        }

        public void setRoutingToken(int routingToken) {
            this.routingToken = routingToken;
        }

        public int getRoutingToken() {
            return routingToken;
        }
    }

    private static class WatchJobs extends HashMap<Integer, WatchJob> {

        // @Override
        public void put(int rt, WatchJob wj) {
            wj.print();
            super.put(rt, wj);
        }

        // @Override
        public WatchJob get(int rt) {
            WatchJob wj;
            if (rt == 0) {
                if (Log.Watch.pathway) {
                    System.out.printf("WatchJobs.get(): rt == 0\n"); // NOI18N
                }
                wj = new WatchJob(WatchJob.Kind.SPONTANEOUS, null, null);
                return wj;
            } else {
                wj = super.get(rt);
                assert wj != null :
                        "WatchJobs.get(): no watch for rt " + rt; // NOI18N
                remove(rt);
                return wj;
            }
        }
    }

    WatchJobs watchJobs = new WatchJobs();

    protected WatchJob getWatchJob(int rt) {
        return watchJobs.get(rt);
    }

    protected void noteNewWatch(NativeWatch template, int rt) {
        if (Log.Watch.pathway) {
            System.out.printf("noteNewWatch(%d)\n", rt); // NOI18N
        }
        WatchJob wj = new WatchJob(WatchJob.Kind.NEW, null, template);
        wj.setRoutingToken(rt);
        wj.setPrimaryChange(true);
        watchJobs.put(rt, wj);
    }

    protected void noteDeletedWatch(WatchVariable target, boolean spread) {
        int rt = target.getRoutingToken();
        WatchJob wj = new WatchJob(WatchJob.Kind.DELETE, target, null);
        wj.setPrimaryChange(spread);
        wj.setRoutingToken(rt);
        watchJobs.put(rt, wj);
    }

    protected void noteRestoredWatch(NativeWatch template, int rt) {
        WatchJob wj = new WatchJob(WatchJob.Kind.RESTORE, null, template);
        wj.setRoutingToken(rt);
        watchJobs.put(rt, wj);
    }

    // interface NativeDebugger
    public WatchVariable[] getWatches() {
        return watches.toArray(new WatchVariable[watches.size()]);
    }

    protected WatchBag watchBag() {
        return manager().watchBag();
    }

    protected ModelChangeDelegator watchUpdater() {
        return manager().watchUpdater();
    }

    public void restoreWatches(WatchBag wb) {
        NativeWatch[] watchesToRestore = wb.getWatches();
        for (int i = 0; i < watchesToRestore.length; i++) {
            NativeWatch template = watchesToRestore[i];
	    if (template != null) {
		restoreWatch(template);
            }
        }
    }

    protected void deleteWatch(WatchVariable w, boolean spreading) {
        // LATER w.cleanup();

        // take out of our list
        watches.remove(w);

        w.removeAllDescendantFromOpenList(false);
        watchUpdater().treeChanged();	// causes a pull

        final NativeWatch parent = w.getNativeWatch();

        if (!postedKill) {
            // if not finishing session
            parent.removeSubWatch(w, (NativeDebugger) this);
            if (parent.nChildren() == 0) {
		parent.delete();
            } else {
                if (!spreading) {
                    parent.postDelete(true);
                }
            }
        } else {
            // finishing session. Just remove this sub-watch
            parent.removeSubWatch(w, (NativeDebugger) this);
        }
    }

    public final void deleteWatchById(int rt, int id) {
        WatchVariable w = watches.byKey(id);
        if (w != null) {
            WatchJob wj = getWatchJob(rt);
            deleteWatch(w, wj.isPrimaryChange());
        }
    }

    public void spreadWatchCreation(NativeWatch w) {
        // very similar to restoreWatches
        restoreWatch(w);
    }

    public abstract void replaceWatch(NativeWatch template, String replacewith);
    protected abstract void restoreWatch(NativeWatch template);

    protected final void clearFiredEvents() {
        for (Handler h : bm().getHandlers()) {
            h.setFired(false);
        }
    }

    private static class VarContinuations extends HashMap<Integer, VarContinuation> {

        // @Override
        public void put(int rt, VarContinuation vc) {
            vc.print();
            super.put(rt, vc);
        }

        // @Override
        public VarContinuation get(int rt) {
            VarContinuation vc = super.remove(rt);
            return vc;
        }
    }

    private VarContinuations varContinuations = new VarContinuations();

    protected abstract void postVarContinuation(int rt, VarContinuation vc);

    // interface NativeDebugger
    public void postVarContinuation(VarContinuation vc) {
        int rt = RoutingToken.VAR.getUniqueRoutingTokenInt();
        varContinuations.put(rt, vc);
        postVarContinuation(rt, vc);
    }

    public void handleVarContinuation(int rt, String result) {
        VarContinuation vc = varContinuations.get(rt);
        if (vc != null) {
            vc.run(result);
        }
    }

    public void setShowAutos(boolean showAutos) {
	// System.out.printf("NativeDebuggerImpl.setShowAutos(%b)\n", showAutos);
	this.showAutos = showAutos;
    }

    protected final boolean isShowAutos() {
	return showAutos;
    }

    /**
     * Clear out mark locations.
     *
     * Possibly relevant comment from the equivalent function in WorkShop:
     * Clear out currentPCFile and visitedPCFile (don't have to mess with
     * func and line since these are not consulted when I decide whether or
     * not to reinstate marks on session switching). This is done as part
     * of the fix for 4101911 to ensure that marks are not reinstated on
     * session switching when they have been removed.
     */
    protected final void deleteMarkLocations() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setCurrentLine(null, false, false, ShowMode.NONE);
            }
        });
    }

    protected final void resetCurrentLine() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setCurrentLine(null, false, false, ShowMode.NONE);
            }
        });
    }
    
    public Location getVisitedLocation() {
        return visitedLocation;
    }
    
    public void annotateDis(boolean andShow) {
        if (visitedLocation != null) {
            DisassemblyUtils.annotatePC(visitedLocation, currentDisPCMarker, andShow);
        }
    }
   
    protected static enum ShowMode {
        SOURCE, // show source
        DIS,    // show disassembly
        AUTO,   // decide based on where we are now
        NONE;   // do not show at all
    }

    
    protected void setCurrentLine(Line l, boolean visited, boolean srcOOD, ShowMode showMode) {

        if (l != null) {
	    if (showMode == ShowMode.SOURCE || (showMode == ShowMode.AUTO && !Disassembly.isInDisasm())) {
		EditorBridge.showInEditor(l);
            }

            if (visited) {
                visitMarker.setLine(l, isCurrent());
                currentPCMarker.setLine(null, isCurrent());
                currentDisPCMarker.setLine(null, isCurrent());
            } else {
                visitMarker.setLine(null, isCurrent());
		// CR 7009746
		if (!state.isRunning)
		    currentPCMarker.setLine(l, isCurrent());
            }
        } else {
            visitMarker.setLine(null, isCurrent());
            currentPCMarker.setLine(null, isCurrent());
            currentDisPCMarker.setLine(null, isCurrent());
        }
        
        if (!visited) {
            // Annotate dis
            annotateDis(showMode == ShowMode.DIS || (showMode == ShowMode.AUTO && Disassembly.isInDisasm()));
        }

        // Arrange for DebuggerManager.error_sourceModified()
        // to emit something.
        this.srcOOD = srcOOD;

        if (srcOOD) {
            // Dbx emits "source-modified" errors once per "context", so
            // while we might get OOD we might not have a good error message.
            // So put a placeholder.
            // error_sourceModified() might enhance it "shortly".
            setSrcOODMessage(Catalog.get("SourceOODWarn")); // NOI18N
        } else {
            // error_sourceModified() doesn't get called when there's
            // _no_ out of date src, so we need to clear
            setSrcOODMessage(null);
        }
    }


    // A kind of a HACK which allows registerDisassemblerWindow to know whether
    // it was called by clicking tabs or via other switching actions.
    protected volatile boolean viaShowLocation = false;

    /**
     * Show the current visiting location in the editor area.
     * If user has requested source and it's available show source code in an
     * editor pane.
     * Else, if user has requested disassembly or no source information is
     * available, bring up the disassembler.
     */
    private void updateLocation(final boolean andShow, final ShowMode showModeOverride) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isSrcRequested() && haveSource()) {
                    // Locations should already be in local path form.
		    final String mFileName = fmap.engineToWorld(getVisitedLocation().src());
		    Line l = EditorBridge.getLine(mFileName, getVisitedLocation().line(), 
                            NativeDebuggerImpl.this);
		    if (l != null) {
                        ShowMode showMode = ShowMode.NONE;
                        if (andShow) {
                            showMode = showModeOverride;
                            NativeBreakpoint breakpoint = getVisitedLocation().getBreakpoint();
                            if (breakpoint != null) {
                                if (breakpoint instanceof InstructionBreakpoint) {
                                    showMode = ShowMode.DIS;
                                } else {
                                    showMode = ShowMode.SOURCE;
                                }
                            }
                        }
			setCurrentLine(l, getVisitedLocation().visited(), getVisitedLocation().srcOutOfdate(), showMode);
                    }
	    } else {
		    setCurrentLine(null, false, false, ShowMode.NONE);

                    if (getVisitedLocation() != null) {
                        disStateModel().updateStateModel(getVisitedLocation(), true);
                        // see IZ 198496: we do not want to show dis if it was not requested explicitly
//			if (getVisitedLocation().pc() != 0) {
//			    Disassembly.open();
//                        }
                    }
                }
            }
        });
    }
    
    private void setSrcRequested(boolean srcRequested) {
	this.srcRequested = srcRequested;
    }

    protected final boolean isSrcRequested() {
	return srcRequested;
    }

    private boolean haveSource() {
	return getVisitedLocation() != null &&
	       getVisitedLocation().hasSource();
    }

    public void requestDisassembly() {
        Disassembly.open();
//	setSrcRequested(false);
//	updateLocation(true);
//	// CR 6986846
//	disassemblerWindow().requestActive();
    }

    public void showCurrentSource() {
	if (!haveSource()) {
	    DebuggerManager.warning(Catalog.get("Dis_MSG_NoSource"));
	    return;
	}
	setSrcRequested(true);
	updateLocation(true, ShowMode.SOURCE);
    }
    
    public void showCurrentDis() {
        Disassembly.open();
        updateLocation(true, ShowMode.DIS);
    }

    public final void setVisitedLocation(Location loc) {
	this.visitedLocation = loc;
	requestAutos();
        getDisassembly().stateUpdated();
	updateLocation(true, ShowMode.AUTO);
    }

    public FileMapper fmap() {
	return fmap;
    }
    
    public void setSrcOODMessage(String msg) {
        if (msg != null && !srcOOD) {
            // If srcOOD is not set it's quite likley that this is a
            // spurious setting, so ignore it.
            return;
        }
        // remember for when we switch sessions (activate() and deactivate())
        srcOODMessage = msg;
        EditorBridge.setStatus(msg);
    }
    /**
     * When not -1, the next resume should deliver the given signal
     * number to the process.
     */
    protected int deliverSignal = -1;
    protected OptionLayers optionLayers = null;

    public OptionLayers optionLayersInit() {
        // we don't want to create one, if optionLayers is null
        return optionLayers;
    }

    protected void addExtraOptions(OptionLayers optionLayers) {
    }

    public final OptionLayers optionLayers() {
        // We want to create one, if optionLayers is null

        if (optionLayers == null) {
            OptionSet globalOptions = manager().globalOptions();
            optionLayers = new OptionLayers(globalOptions);

            // We may get a null profile on a botched engine startup
            // See bug 6307423.
            // Probably SHOULD do more work.
            OptionSet profileOptions = null;
            if (profileBridge.currentDbgProfile() != null) {
                profileOptions = profileBridge.currentDbgProfile().getOptions();
                assert profileOptions != null : "debug profile must have options";
                optionLayers.push(profileOptions);
            }

            addExtraOptions(optionLayers);
        }
        return optionLayers;
    }

    // interface NativeDebugger
    public void optionLayersReset() {
        // arrange so we re-create optionLayers
        optionLayers = null;
    }

    /**
     * Called when this session has been switched to
     * 'redundant' is true when we double-click on the same session.
     */
    public void activate(boolean redundant) {

        ioPack.switchTo();

        //moved to dbx, gdb uses new disassembly
//	disassemblerWindow().setDebugger(this);
//	disassemblerWindow().getView().setModelController(disModel(),
//							  disController(),
//							  disStateModel(),
//							  breakpointModel());
	updateLocation(true, ShowMode.AUTO);
	// CR 6986846
	//if (!(isSrcRequested() || haveSource())) {
	if ((getVisitedLocation() != null) && !haveSource()) {
	   Disassembly.open();
	}

        visitMarker.attach(true);
        currentPCMarker.attach(true);
        currentDisPCMarker.attach(true);
        EditorBridge.setStatus(srcOODMessage);

        updateActions();

        if (redundant) {
            return;
        }

        bm().breakpointUpdater().treeChanged();

	for (NativeBreakpoint bpt : bm().breakpointBag().getBreakpoints())
	    bpt.showAnnotationsFor(true, this);
        
        registerRegistersWindow(RegistersWindow.getDefault().isShowing() ? RegistersWindow.getDefault() : null);
        
        if (MemoryWindow.getDefault().isShowing()) {
            registerMemoryWindow(MemoryWindow.getDefault());
            MemoryWindow.getDefault().setDebugger(this);
        } else {
            registerMemoryWindow(null);
        }
        
        if (Disassembly.isOpened()) {
            registerDisassembly(getDisassembly());
            getDisassembly().stateUpdated();
        } else {
            registerDisassembly(null);
        }
    }

    /**
     * Called when this session has been switched away from
     * 'redundant' is true when we double-click on the same session.
     */
    public void deactivate(boolean redundant) {

        if (redundant) {
            return;
        }

        // The following was causing actions to be disabled as we
        // switched between sessions. I can't recall or think now of
        // why we needed to do this.
        // TMP actionEnabler().update(null);

        bm().breakpointUpdater().treeChanged();

	for (NativeBreakpoint bpt : bm().breakpointBag().getBreakpoints())
	    bpt.showAnnotationsFor(false, this);

        visitMarker.detach();
        currentPCMarker.detach();
        currentDisPCMarker.detach();        
        EditorBridge.setStatus(null);
    }

    public boolean getVerboseStack() {
        if ("on".equals(DebuggerOption.STACK_VERBOSE.getCurrValue(optionLayers()))) { // NOI18N
            return true;
        } else {
            return false;
        }
    // SHOULD cause the StackView actions provider to pull?
    // or somehow tickle the action?
    }

    /**
     * Common code to be called by subclass when engine goes away.
     */
    protected final void preKill() {
        // May be a bit redundant as it is usually set in postKill(),
        // but we might get here just by the user typing 'quit' or
        // the engine dying on us.
        postedKill = true;

        // DEBUG System.out.println("NativeDebuggerImpl.kill()");

        // SHOULD disable all (most) actions so we don't end up sending
        // stuff to engine.


        if ( /* OLD ConsoleTopComponent.getDefault() != null && */
                getIOPack() != null &&
                // getIOPack().console() != null &&
                getIOPack().console().getTerm() != null) {

            String warn = debuggerType() + " terminated"; // NOI18N
            char[] warnArray = warn.toCharArray();
            getIOPack().console().getTerm().putChars(warnArray,
                    0, warnArray.length);
        }
        // Close if no more sessions
        NativeSession[] sessions = DebuggerManager.get().getSessions();
        if (sessions.length <= 1) {
            Disassembly.close();
        }

        // Go through the array conversion otherwise we'll get
        // ConcurrentModificatonExpcetions.

        for (Handler h : bm().getHandlers()) {
            bm().deleteHandler(h, Gen.secondary(null), true);
        }

        for (WatchVariable w : getWatches()) {
            deleteWatch(w, false);
        }


        if (DebuggerManager.isPerTargetBpts()) {
            bm().breakpointBag().cleanupBpts();
        }
        DbgActionHandler dah = getNDI().getDah();
        if (dah != null) {
            dah.executionFinished(0);
        }
    }

    /*
     * Track if the disassembly window is "showing".
     */
    private boolean asmVisible;

    protected final boolean isAsmVisible() {
        return asmVisible;
    }

    protected final void setAsmVisible(boolean asmVisible) {
	this.asmVisible = asmVisible;
    }

    protected static enum CaptureState {

        NONE, INITIAL, FINAL
    }

    private CaptureState captureState = CaptureState.NONE;
    private CaptureInfo captureInfo;

    abstract protected void stopUpdates();
    abstract protected void startUpdates();

    protected final void setCaptureState(CaptureState captureState) {
        if (Log.Capture.state) {
            System.out.printf("setCaptureState(): %s -> %s\n", // NOI18N
                    this.captureState, captureState);
        }
        this.captureState = captureState;
    }

    protected final CaptureState getCaptureState() {
        return captureState;
    }

    protected final void setCaptureInfo(CaptureInfo captureInfo) {
        if (Log.Capture.info) {
            System.out.printf("setCaptureInfo(): %s\n", captureInfo); // NOI18N
        }
        this.captureInfo = captureInfo;
    }

    protected final CaptureInfo getCaptureInfo() {
	return captureInfo;
    }

    protected final void captureFailed() {
        ExternalStart xstart = ExternalStartManager.getXstart(getHost());

        if (captureState != CaptureState.NONE) {
            captureState = CaptureState.NONE;
            startUpdates();
            if (captureState == CaptureState.FINAL) {
                DebuggerManager.warning(String.format("ss_attach failed to exec %s",
                        captureInfo.executable));
            } else {
                if (xstart != null) {
		    xstart.fail();
                }
                DebuggerManager.warning(String.format("%s failed during capture", debuggerType()));
            }
        } else {
            // If we cancel the startup of an engine captureState
            // will be NONE so we test isCaptured() here as well.
            NativeDebuggerInfo ndi = getNDI();
            if (xstart != null && ndi.isCaptured()) {
                xstart.fail();
            }
        }
    }


    //
    // Assembly level debugging support
    //

    protected abstract DisFragModel disModel();

    public abstract Controller disController();

    protected InstBreakpointModel breakpointModel() {
	return breakpointModel;
    }

    protected final StateModelAdaptor disStateModel() {
	return disStateModel;
    }

    protected class DisModelSupport implements DisFragModel {

        private ArrayList<Line> current_addrs = new ArrayList<Line>();
        private ArrayList<Listener> listeners = new ArrayList<Listener>();

        // implement DisFragModel
        public void clear() {
            current_addrs.clear();
        }

        // implement DisFragModel
        public Line getItem(int i) {
            return current_addrs.get(i);
        }

        // implement DisFragModel
        public int size() {
            return current_addrs.size();
        }

        // interface DisFragModel
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        // interface DisFragModel
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        protected final void add(String memaddr, String memvalue) {
	    // careful!
	    // DisView.addrFromLine is sensitive to this number of spaces
            current_addrs.add(new Line(memaddr, memvalue));
        }
	
	protected final void update() {
            for (Listener listener : listeners) {
                listener.fragUpdated();
            }
	}

        public Iterator<Line> iterator() {
            return current_addrs.iterator();
        }
    }

    protected abstract class ControllerSupport implements Controller {

        private StateListener runToCursorListener = null;

        // interface Controller
        abstract public void requestDis(boolean withSource);

        // interface Controller
        abstract public void requestDis(String start, int count, boolean withSource);


	protected abstract void setBreakpointHelp(String address);

        /*
         * Add or Remove Instruction breakpoint by address
         */

	// interface Controller
        public void setBreakpoint(String address, boolean add) {
            if (add) {
		setBreakpointHelp(address);
            } else {
		long addr = Address.parseAddr(address);
                InstBreakpointModel.Bpt foundBpt = breakpointModel().findBptByAddr(addr);
                if (foundBpt != null && foundBpt.bpt != null) {
                    foundBpt.bpt.dispose();
                }
            }
        }

        /*
         * Toggle Instruction breakpoint by address
         */

	// interface Controller
        public void toggleBreakpoint(String address) {
	    long addr = Address.parseAddr(address);
            InstBreakpointModel.Bpt foundBpt = breakpointModel().findBptByAddr(addr);
            if (foundBpt == null) {
		setBreakpointHelp(address);
            } else {
                if (foundBpt.bpt != null) {
                    foundBpt.bpt.dispose();
                }
            }
        }
        /*
         * Enable or disable Instruction Breakpoint Annotation
         */

        // interface Controller
        public final void enableBreakpoint(long address, NativeBreakpoint bpt,
                boolean enable) {

            if (enable) {
                breakpointModel().enable(address, bpt);
            } else {
                InstBreakpointModel.Bpt foundBpt = breakpointModel().findBptByAddr(address);
                breakpointModel().disable(foundBpt);
            }
        }
        /*
         * Add or Remove Instruction Breakpoint Annotation
         * interface Controller
         */

        // interface Controller
        public final void updateBreakpoint(long address, NativeBreakpoint bpt,
                boolean add) {

            if (add) {
                breakpointModel().add(address, bpt);
            } else {
                InstBreakpointModel.Bpt foundBpt = breakpointModel().findBptByAddr(address);
                breakpointModel().remove(foundBpt);
            }
        }

        // interface Controller
        public final void runToCursor(String addr) {
            runToCursorInst(addr);
        }

        // interface Controller
        public final void addStateListenerInst(StateListener sl) {
            if (runToCursorListener == null) {
                runToCursorListener = sl;
                addStateListener(sl);
            }
        }

        // interface Controller
        public final void removeStateListenerInst(StateListener sl) {
            // 6567570
            if (runToCursorListener != null) {
                runToCursorListener = null;
                removeStateListener(sl);
            }
        }
    }

    protected final class InstBreakpointModel implements BreakpointModel {

        public class Bpt {

            public long addr;
            public NativeBreakpoint bpt;

            Bpt(long a, NativeBreakpoint b) {
                addr = a;
                bpt = b;
            }
        }
        private ArrayList<Bpt> breakpoints_List = new ArrayList<Bpt>();
        private ArrayList<Listener> listeners = new ArrayList<Listener>();

	public InstBreakpointModel() {
	}

        // interface BreakpointModel
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        // interface BreakpointModel
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        /*
         * Return bpt at this address.
         */
        public Bpt findBptByAddr(long address) {
            for (Bpt bpt : breakpoints_List) {
                if (address == bpt.addr) {
                    return bpt;
                }
            }
            return null;
        }

        /*
         * Return number of disabled bpts at this address.
         */
        // interface BreakpointModel
        public int findDisabled(long address) {
            int count = 0;
            for (Bpt bpt : breakpoints_List) {
                if (address == bpt.addr && !bpt.bpt.isEnabled()) {
                    count++;
                }
            }
            return count;
        }

        /*
         * Return number of bpts at this address.
         */
        // interface BreakpointModel
        public int find(long address) {
            int count = 0;
            for (Bpt bpt : breakpoints_List) {
                if (address == bpt.addr) {
                    count++;
                }
            }
            return count;
        }

        public void enable(long address, NativeBreakpoint bpt) {
            for (Listener l : listeners) {
                l.bptUpdated();
            }
        }

        public void disable(Bpt bpt) {
            for (Listener l : listeners) {
                l.bptUpdated();
            }
        }

        public void add(long address, NativeBreakpoint bpt) {
            breakpoints_List.add(new Bpt(address, bpt));
            for (Listener l : listeners) {
                l.bptUpdated();
            }
        }

        public void remove(Bpt bpt) {
            breakpoints_List.remove(bpt);
            for (Listener l : listeners) {
                l.bptUpdated();
            }
        }

        public NativeBreakpoint[] getBreakpoints() {
            NativeBreakpoint[] res = new NativeBreakpoint[breakpoints_List.size()];
            int idx = 0;
            for (Bpt bpt : breakpoints_List) {
                res[idx++] = bpt.bpt;
            }
            return res;
        }
    }

    protected final class StateModelAdaptor implements StateModel {

	private Location location = Location.EMPTY;

        private HashMap<Long, Integer> addrHashMap;

        private final ArrayList<Listener> listeners = new ArrayList<Listener>();

	public StateModelAdaptor() {
	}

        // interface StateModel
        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        // interface StateModel
        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        // interface StateModel
        public void setAddrHashMap(HashMap<Long, Integer> hashmap) {
            addrHashMap = hashmap;
        }


        // interface StateModel
        public boolean isVisited() {
            return location.visited();
        }

        // interface StateModel
        public long getPC() {
            return location.pc();
        }

        // interface StateModel
        public String getFunction() {
            return location.func();
        }

        // interface StateModel
        public String getFile() {
            return location.src();
        }

        // interface StateModel
        public int getLine() {
            return location.line();
        }

        private boolean inRange(long addr) {
	    if (addrHashMap == null)
		return false;
            Integer pos = addrHashMap.get(addr);
            if (pos != null) {
                return true;
            }
            return false;
        }

        public void updateStateModel(Location location, boolean retrieve) {
	    /* 
	    System.out.printf("updateStateModel()\n");
	    System.out.printf("%s\n", location);
	    */

	    this.location = location;

            if (retrieve && !inRange(location.pc())) {
		disController().requestDis(true);
            } else {
                for (Listener l : listeners) {
                    l.stateUpdated();
                }
            }
        }
    }

    // interface NativeDebugger
    public final void InstBptEnabled(long addr, NativeBreakpoint bpt) {
	disController().enableBreakpoint(addr, bpt, true);
    }

    // interface NativeDebugger
    public final void InstBptDisabled(long addr, NativeBreakpoint bpt) {
	disController().enableBreakpoint(addr, bpt, false);
    }

    // interface NativeDebugger
    public final void InstBptAdded(long addr, NativeBreakpoint bpt) {
	disController().updateBreakpoint(addr, bpt, true);
    }

    // interface NativeDebugger
    public final void InstBptRemoved(long addr, NativeBreakpoint bpt) {
	disController().updateBreakpoint(addr, bpt, false);
    }

    public static String getDebuggerString(MakeConfiguration conf) {
        // Figure out dbx command
        // Copied from GdbProfile
        CompilerSet2Configuration csconf = conf.getCompilerSet();

	/* OLD
        if (csconf.isValid()) {
            cs = CompilerSetManager.get(conf.getDevelopmentHost().getExecutionEnvironment()).getCompilerSet(csconf.getOption());
        } else {
            cs = CompilerSet.getCompilerSet(conf.getDevelopmentHost().getExecutionEnvironment(), csconf.getOldName(), conf.getPlatformInfo().getPlatform());
            CompilerSetManager.get(conf.getDevelopmentHost().getExecutionEnvironment()).add(cs);
            csconf.setValid();
        }
        Tool debuggerTool = cs.getTool(Tool.DebuggerTool);
        if (debuggerTool != null) {
            return debuggerTool.getPath();
        }
	 */

        CompilerSet cs = csconf.getCompilerSet();
        ExecutionEnvironment exEnv = conf.getDevelopmentHost().getExecutionEnvironment();
        String csname = csconf.getOption();
        if (cs == null) {
            final int platform = conf.getPlatformInfo().getPlatform();
            CompilerFlavor flavor = CompilerFlavor.toFlavor(csname, platform);
            flavor = flavor == null ? CompilerFlavor.getUnknown(platform) : flavor;
            cs = CompilerSetFactory.getCompilerSet(exEnv, flavor, csname);
        }
        Tool debuggerTool = cs.getTool(PredefinedToolKind.DebuggerTool);
        if (debuggerTool != null) {
            String path = debuggerTool.getPath();
            if (path != null && !path.isEmpty()) {
                return path;
            }
        }
        // ask for debugger, IZ 192540
        ToolsPanelModel model = new LocalToolsPanelModel();
        model.setCRequired(false);
        model.setCppRequired(false);
        model.setFortranRequired(false);
        model.setMakeRequired(false);
        model.setDebuggerRequired(true);
        model.setShowRequiredBuildTools(false);
        model.setShowRequiredDebugTools(true);
        model.setCompilerSetName(null); // means don't change
        model.setSelectedCompilerSetName(csname);
        model.setSelectedDevelopmentHost(exEnv);
        model.setEnableDevelopmentHostChange(false);
        BuildToolsAction bt = SystemAction.get(BuildToolsAction.class);
        bt.setTitle(Catalog.get("LBL_ResolveMissingDebugger_Title")); // NOI18N
        if (bt.initBuildTools(model, new ArrayList<String>(), cs)) {
            conf.getCompilerSet().setValue(model.getSelectedCompilerSetName());
            cs = CompilerSetManager.get(exEnv).getCompilerSet(model.getSelectedCompilerSetName());
            return cs.getTool(PredefinedToolKind.DebuggerTool).getPath();
        }
        return null;
    }
    
    public void registerMemoryWindow(MemoryWindow w) {
    }

    public Set<String> requestAutos() {
	autos.clear();

	if (!isShowAutos()) {
	    return Collections.emptySet();
        }

	Location location = getVisitedLocation();
	if (location == null ||  ! location.hasSource()) {
	    localUpdater.batchOffForce();	// cause a pull to clear view
	    return Collections.emptySet();
	}

	return Autos.get(EditorBridge.documentFor(location.src(), this), location.line()-1);
    }
    
    public int getAutosCount() {
	return autos.size();
    }
    
    public Variable[] getAutos() {
	Variable array[] = autos.toArray(new Variable[autos.size()]);
	return array;
    }
}
