/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.terminal.example.dedicatedtc;

import java.io.Serializable;
import java.util.logging.Logger;

import org.openide.util.NbBundle;
// OLD import org.openide.windows.IOContainer;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

// OLD import org.netbeans.modules.terminal.api.TerminalWindow;
// OLD import org.netbeans.modules.terminal.api.TerminalContainer;

/**
 * Top component which displays something.
 */
final class CommandTopComponent extends TopComponent /* OLD implements TerminalWindow */ {

    private static CommandTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";

    private static final String PREFERRED_ID = "CommandTopComponent";

    private CommandTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(CommandTopComponent.class, "CTL_CommandTopComponent"));
        setToolTipText(NbBundle.getMessage(CommandTopComponent.class, "HINT_CommandTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
        initComponents2(getName());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized CommandTopComponent getDefault() {
        if (instance == null) {
            instance = new CommandTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the CommandTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized CommandTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(CommandTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof CommandTopComponent) {
            return (CommandTopComponent) win;
        }
        Logger.getLogger(CommandTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return CommandTopComponent.getDefault();
        }
    }

    /* OLD
    //
    // Stuff in addition to the boilerplate TopComponent code
    //
    private TerminalContainer tc;

    public IOContainer ioContainer() {
        return tc.ioContainer();
    }
     */

    private void initComponents2(String name) {
	/* OLD
        tc = TerminalContainer.create(this, name);
        add(tc);
	 */
    }
}
