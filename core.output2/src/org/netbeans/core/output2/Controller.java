/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.core.output2;

import java.awt.Font;
import java.io.CharConversionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.IOContainer;
import org.openide.windows.OutputEvent;
import org.openide.xml.XMLUtil;

/**
 * Master controller for an output window, and supplier of the default instance.
 * The controller handles all actions of interest in an output window - the components
 * are merely containers for data which pass events of interest up the component hierarchy
 * to the controller via OutputWindow.getController(), for processing by the master
 * controller.  The controller is fully stateless, and stores information of interest in
 * the components as appropriate.
 */
public class Controller {

    static Controller controller;

    static Controller getDefault() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    private Map<NbIO, OutputTab> ioToTab = new HashMap<NbIO, OutputTab>();

    Controller() {}

    void eventDispatched(IOEvent ioe) {
        if (Controller.LOG) Controller.log ("Event received: " + ioe);
        NbIO io = ioe.getIO();
        int command = ioe.getCommand();
        boolean value = ioe.getValue();
        Object data = ioe.getData();
        OutputTab comp = ioToTab.get(io);
        if (Controller.LOG) Controller.log ("Passing command to controller " + ioe);
        performCommand(comp, io, command, value, data);
        ioe.consume();
    }

    private OutputTab createOutputTab(NbIO io) {
        if (LOG) log ("Create component for nbio " + io);
        OutputTab result = new OutputTab (io);
        result.setName (io.getName() + " ");
        Action[] a = io.getToolbarActions();
        if (a != null) {
            result.setToolbarActions(a);
        }

        if (LOG) log ("Adding new tab " + result);
        ioToTab.put(io, result);
        IOContainer ioContainer = io.getIOContainer();
        ioContainer.add(result, result);
        ioContainer.setToolbarActions(result, a);
        if (io.getIcon() != null) {
            ioContainer.setIcon(result, io.getIcon());
        }
        if (io.getToolTipText() != null) {
            ioContainer.setToolTipText(result, io.getToolTipText());
        }

        //Make sure names are boldfaced for all open streams - if the tabbed
        //pane was just added in, it will just have used the name of the
        //component, which won't contain html
        for (OutputTab tab : ioToTab.values()) {
            updateName(tab);
        }
        return result;
    }

    void removeTab(NbIO io) {
        ioToTab.remove(io);
    }

    private static final String KEY_FONTSIZE = "fontsize";
    void changeFontSizeBy(int change) {
        Font oldFont = ioToTab.values().iterator().next().getOutputPane().getViewFont();
        int fontSize = oldFont.getSize() + change;
        if (fontSize < 7) {
            fontSize = 7;
        } else if (fontSize > 72) {
            fontSize = 72;
        }
        NbPreferences.forModule(Controller.class).putInt(KEY_FONTSIZE, fontSize);
        Font newFont = oldFont.deriveFont((float) fontSize);
        for (OutputTab tab : ioToTab.values()) {
            tab.getOutputPane().setViewFont(newFont);
        }
    }

    public static int getDefaultFontSize() {
        int result = NbPreferences.forModule(Controller.class).getInt(KEY_FONTSIZE, -1);
        if (result == -1) {
            result = UIManager.getInt("uiFontSize");
            if (result < 7) {
                result = -1;
            }
        }
        return result;
    }

    void removeFromUpdater(OutputTab tab) {
        if (nameUpdater != null) {
            nameUpdater.remove(tab);
        }
    }

    /**
     * Boldfaces the name of the output component if its NbIO's stream is open.
     * The update is delayed, and runs subsequently on the event queue - a process may
     * synchronously open and close tabs, all of which affects names, so we use this
     * technique and the CoalescedNameUpdater to coalesce all name changes - otherwise
     * the name change may be delayed.
     *
     * @param tab The component whose name may need adjusting
     */
    void updateName(OutputTab tab) {
        if (nameUpdater == null) {
            if (LOG) log ("Update name for " + tab.getIO() + " dispatching a name updater");
            nameUpdater = new CoalescedNameUpdater();
            SwingUtilities.invokeLater(nameUpdater);
        }
        nameUpdater.add(tab);
    }

    private CoalescedNameUpdater nameUpdater = null;
    /**
     * Calls to methods invoked on NbIO done on the EQ are invoked synchronously
     * (this avoids a delay in the output window appearing, so output starts
     * immediately).  However, we want to avoid multiple name changes being
     * propagated up to the window system because one tab was removed, another
     * was added, and so forth - the result is the title won't be updated until
     * the output run is nearly done, otherwise.  Also, the call to update the
     * TopComponent name is not terribly quick, so we don't want to do it any
     * more times than we need to.
     * <p>
     * This class coalesces name changes, which are run afterward on the event
     * queue.
     */
    private class CoalescedNameUpdater implements Runnable {
        private Set<OutputTab> components = new HashSet<OutputTab>();
        CoalescedNameUpdater() {
        }

        public void add(OutputTab tab) {
            components.add(tab);
        }
        
        public void remove(OutputTab tab) {
            components.remove(tab);
        }

        public void run() {
            for (OutputTab t : components) {
                NbIO io = t.getIO();
                if (!ioToTab.containsKey(io)) {
                    continue;
                }
                if (LOG) {
                    log ("Update name for " + io.getName() + " stream " +
                        "closed is " + io.isStreamClosed());
                }
                String escaped;
                try {
                    escaped = XMLUtil.toAttributeValue(io.getName());
                } catch (CharConversionException e) {
                    escaped = io.getName();
                }
                String name = io.isStreamClosed() ?  io.getName() + " " : "<html><b>" + escaped + " </b>&nbsp;</html>";  //NOI18N

                if (LOG) {
                    log("  set name to " + name);
                }
                //#88204 apostophes are escaped in xm but not html
                io.getIOContainer().setTitle(t, name.replace("&apos;", "'"));
            }
            nameUpdater = null;
        }
    }

    /**
     * Handles IOEvents posted into the AWT Event Queue by NbIO instances whose methods have
     * been called, as received by an OutputTab which has identified the event as being
     * intended for it.
     *
     * @param tab The output component associated with this IO, if any
     * @param io The IO which originated the event
     * @param command The ID, one of those defined in IOEvent, of the command
     * @param value The boolean value of the command, if pertinent
     * @param data The data associated with the command, if pertinent
     */
    void performCommand(OutputTab tab, NbIO io, int command, boolean value, Object data) {

        if (LOG) {
            log ("PERFORMING: " +  IOEvent.cmdToString(command) + " value=" + value + " on " + io + " tob " + tab);
        }

        IOContainer ioContainer = io.getIOContainer();

        switch (command) {
            case IOEvent.CMD_CREATE :
                createOutputTab(io);
                break;
            case IOEvent.CMD_INPUT_VISIBLE :
                if (value && tab == null) {
                    tab = createOutputTab(io);
                }
                if (tab != null) {
                    tab.setInputVisible(value);
                    ioContainer.select(tab);
                }
                break;
            case IOEvent.CMD_SELECT :
                if (tab == null) {
                    tab = createOutputTab(io);
                }
                ioContainer.open();
                if (io.isFocusTaken()) {
                    ioContainer.requestActive();
                } else {
                    ioContainer.requestVisible();
                }
                ioContainer.select(tab);
                break;
            case IOEvent.CMD_SET_TOOLBAR_ACTIONS :
                if (tab != null) {
                    Action[] a = (Action[]) data;
                    tab.setToolbarActions(a);
                    tab.getIO().getIOContainer().setToolbarActions(tab, a);
                }
                break;
            case IOEvent.CMD_CLOSE :
                if (tab != null) {
                    ioContainer.remove(tab);
                } else {
                    io.dispose();
                }
                break;
            case IOEvent.CMD_STREAM_CLOSED :
                if (value) {
                    if (tab == null) {
                        //The tab was already closed, throw away the storage.
                        if (io.out() != null) {
                            io.out().dispose();
                        }
                    } else {
                        updateName(tab);
                        if (tab.getIO().out() != null && tab.getIO().out().getLines().firstListenerLine() == -1) {
                            tab.getOutputPane().ensureCaretPosition();
                        }
                        if (tab == ioContainer.getSelected()) {
                            tab.updateActions();
                        }
                    }
                } else {
                    if (tab != null && tab.getParent() != null) {
                        updateName(tab);
                    }
                }
                break;
            case IOEvent.CMD_RESET :
                if (tab == null) {
                    if (LOG) log ("Got a reset on an io with no tab.  Creating a tab.");
                    createOutputTab(io);
                    ioContainer.requestVisible();
                    return;
                }
                if (LOG) log ("Setting io " + io + " on tab " + tab);
                tab.reset();
                updateName(tab);
                if (LOG) log ("Reset on " + tab + " tab displayable " + tab.isDisplayable() + " io " + io + " io.out " + io.out());
                break;

            case IOEvent.CMD_SET_ICON:
                if (tab != null) {
                    tab.getIO().getIOContainer().setIcon(tab, (Icon) data);
                }
                break;

            case IOEvent.CMD_SET_TOOLTIP:
                if (tab != null) {
                    tab.getIO().getIOContainer().setToolTipText(tab, (String) data);
                }
                break;
        }
    }

    /**
     * An OutputEvent implementation with a settable line index so it can be
     * reused.
     */
    static class ControllerOutputEvent extends OutputEvent {
        private int line;
        ControllerOutputEvent (NbIO io, int line) {
            super (io);
            this.line = line;
        }

        void setLine (int line) {
            this.line = line;
        }

        public String getLine() {
            NbIO io = (NbIO) getSource();
            OutWriter out = io.out();
            try {
                if (out != null) {
                    String s = out.getLines().getLine(line);
                    //#46892 - newlines should not be appended to returned strings
                    if (s.endsWith("\n")) { //NOI18N
                        s = s.substring(0, s.length()-1);
                    }
                    //#70008 on windows \r can also be there..
                    if (s.endsWith("\r")) { //NOI18N
                        s = s.substring(0, s.length()-1);
                    }
                    return s;
                }
            } catch (IOException ioe) {
                IOException nue = new IOException ("Could not fetch line " + line + " on " + io.getName()); //NOI18N
                nue.initCause(ioe);
                Exceptions.printStackTrace(ioe);
            }
            return null;
        }
    }

    public static final boolean LOG = Boolean.getBoolean("nb.output.log") || Boolean.getBoolean("nb.output.log.verbose"); //NOI18N
    public static final boolean VERBOSE = Boolean.getBoolean("nb.output.log.verbose");
    static final boolean logStdOut = Boolean.getBoolean("nb.output.log.stdout"); //NOI18N
    public static void log (String s) {
        s = Long.toString(System.currentTimeMillis()) + ":" + s + "(" + Thread.currentThread() + ")  ";
        if (logStdOut) {
            System.out.println(s);
            return;
        }
        OutputStream os = getLogStream();
        byte b[] = new byte[s.length() + 1];
        char[] c = s.toCharArray();
        for (int i=0; i < c.length; i++) {
            b[i] = (byte) c[i];
        }
        b[b.length-1] = (byte) '\n';
        try {
            os.write(b);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(s);
        }
        try {
            os.flush();
        } catch (Exception e ) {}
    }
    
    public static void logStack() {
        if (logStdOut) {
            new Exception().printStackTrace();
            return;
        }
        Exception e = new Exception();
        e.fillInStackTrace();
        StackTraceElement[] ste = e.getStackTrace();

        for (int i=1; i < Math.min (22, ste.length); i++) {
            log ("   *   " + ste[i]);
        }
    }

    private static OutputStream logStream = null;
    private static OutputStream getLogStream() {
        if (logStream == null) {
            String spec = System.getProperty ("java.io.tmpdir") + File.separator + "outlog.txt";
            synchronized (Controller.class) {
                try {
                    File f = new File (spec);
                    if (f.exists()) {
                        f.delete();
                    }
                    f.createNewFile();
                    logStream = new FileOutputStream(f);
                } catch (Exception e) {
                    e.printStackTrace();
                    logStream = System.err;
                }
            }
        }
        return logStream;
    }
}

