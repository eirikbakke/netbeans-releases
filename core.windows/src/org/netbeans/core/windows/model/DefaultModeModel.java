/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.core.windows.model;



import java.awt.*;
import java.util.List;

import org.netbeans.core.windows.ModeImpl;
import org.netbeans.core.windows.SplitConstraint;
import org.openide.windows.TopComponent;


/**
 *
 * @author  Peter Zavadsky
 */
final class DefaultModeModel implements ModeModel {


    /** Programatic name of mode. */
    private final String name;
    
    private final Rectangle bounds = new Rectangle();
    
    private final Rectangle boundsSeparetedHelp = new Rectangle();

    /** State of mode: split or separate. */
    private /*final*/ int state;
    /** Kind of mode: editor or view. */
    private final int kind;
    
    /** Frame state. */
    private int frameState;

    /** Permanent property. */
    private final boolean permanent;

    /** Sub model which manages TopComponents stuff. */
    private final TopComponentSubModel topComponentSubModel;
    
    /** Context of tcx. Lazy initialization, because this will be used only by
     * sliding kind of modes */
    private TopComponentContextSubModel topComponentContextSubModel = null;

    // Locks>>
    /** */
    private final Object LOCK_STATE = new Object();
    /** */
    private final Object LOCK_BOUNDS = new Object();
    /** */
    private final Object LOCK_BOUNDS_SEPARATED_HELP = new Object();
    /** Locks frameState. */
    private final Object LOCK_FRAMESTATE = new Object();
    /** Locks top components. */
    private final Object LOCK_TOPCOMPONENTS = new Object();
    /** Locks tc contexts */
    private final Object LOCK_TC_CONTEXTS = new Object();
    
    
    public DefaultModeModel(String name, int state, int kind, boolean permanent) {
        this.name = name;
        this.state = state;
        this.kind = kind;
        this.permanent = permanent;
        this.topComponentSubModel = new TopComponentSubModel(kind);
    }

    /////////////////////////////////////
    // Mutator methods >>
    /////////////////////////////////////
    public void setState(int state) {
        synchronized(LOCK_STATE) {
            this.state = state;
        }
    }
    
    public void removeTopComponent(TopComponent tc) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.removeTopComponent(tc);
        }
    }
    
    // XXX
    public void removeClosedTopComponentID(String tcID) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.removeClosedTopComponentID(tcID);
        }
    }
    
    /** Adds opened TopComponent. */
    public void addOpenedTopComponent(TopComponent tc) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.addOpenedTopComponent(tc);
        }
    }
    
    public void insertOpenedTopComponent(TopComponent tc, int index) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.insertOpenedTopComponent(tc, index);
        }
    }
    
    public void addClosedTopComponent(TopComponent tc) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.addClosedTopComponent(tc);
        }
    }
    
    public void addUnloadedTopComponent(String tcID) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.addUnloadedTopComponent(tcID);
        }
    }
    
    public void setUnloadedSelectedTopComponent(String tcID) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.setUnloadedSelectedTopComponent(tcID);
        }
    }
    
    /** Sets seleted TopComponent. */
    public void setSelectedTopComponent(TopComponent selected) {
        synchronized(LOCK_TOPCOMPONENTS) {
            topComponentSubModel.setSelectedTopComponent(selected);
        }
    }

    /** Sets frame state */
    public void setFrameState(int frameState) {
        synchronized(LOCK_FRAMESTATE) {
            this.frameState = frameState;
        }
    }

    public void setBounds(Rectangle bounds) {
        if(bounds == null) {
            return;
        }
        
        synchronized(LOCK_BOUNDS) {
            this.bounds.setBounds(bounds);
        }
    }
    
    public void setBoundsSeparatedHelp(Rectangle boundsSeparatedHelp) {
        if(bounds == null) {
            return;
        }
        
        synchronized(LOCK_BOUNDS_SEPARATED_HELP) {
            this.boundsSeparetedHelp.setBounds(boundsSeparatedHelp);
        }
    }
    /////////////////////////////////////
    // Mutator methods <<
    /////////////////////////////////////


    /////////////////////////////////////
    // Accessor methods >>
    /////////////////////////////////////
    public String getName() {
        return name;
    }
    
    public Rectangle getBounds() {
        synchronized(LOCK_BOUNDS) {
            return (Rectangle)this.bounds.clone();
        }
    }
    
    public Rectangle getBoundsSeparatedHelp() {
        synchronized(LOCK_BOUNDS_SEPARATED_HELP) {
            return (Rectangle)this.boundsSeparetedHelp.clone();
        }
    }
    
    public int getState() {
        synchronized(LOCK_STATE) {
            return this.state;
        }
    }
    
    public int getKind() {
        return this.kind;
    }
    
    /** Gets frame state. */
    public int getFrameState() {
        synchronized(LOCK_FRAMESTATE) {
            return this.frameState;
        }
    }
    
    public boolean isPermanent() {
        return this.permanent;
    }
    
    public boolean isEmpty() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.isEmpty();
        }
    }
    
    public boolean containsTopComponent(TopComponent tc) {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.containsTopComponent(tc);
        }
    }

    /** Gets list of top components in this workspace. */
    public List getTopComponents() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getTopComponents();
        }
    }


    /** Gets selected TopComponent. */
    public TopComponent getSelectedTopComponent() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getSelectedTopComponent();
        }
    }

    /** Gets list of top components. */
    public List getOpenedTopComponents() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getOpenedTopComponents();
        }
    }
    
    // XXX
    public List getOpenedTopComponentsIDs() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getOpenedTopComponentsIDs();
        }
    }
    
    public List getClosedTopComponentsIDs() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getClosedTopComponentsIDs();
        }
    }
    
    public List getTopComponentsIDs() {
        synchronized(LOCK_TOPCOMPONENTS) {
            return topComponentSubModel.getTopComponentsIDs();
        }
    }
    
    public SplitConstraint[] getTopComponentPreviousConstraints(String tcID) {
        synchronized(LOCK_TC_CONTEXTS) {
            return getContextSubModel().getTopComponentPreviousConstraints(tcID);
        }
    }
    
    public ModeImpl getTopComponentPreviousMode(String tcID) {
        synchronized(LOCK_TC_CONTEXTS) {
            return getContextSubModel().getTopComponentPreviousMode(tcID);
        }
    }
    
    public void setTopComponentPreviousConstraints(String tcID, SplitConstraint[] constraints) {
        synchronized(LOCK_TC_CONTEXTS) {
            getContextSubModel().setTopComponentPreviousConstraints(tcID, constraints);
        }
    }
    
    public void setTopComponentPreviousMode(String tcID, ModeImpl mode) {
        synchronized(LOCK_TC_CONTEXTS) {
            getContextSubModel().setTopComponentPreviousMode(tcID, mode);
        }
    }
    
    /////////////////////////////////////
    // Accessor methods <<
    /////////////////////////////////////
    
    private TopComponentContextSubModel getContextSubModel() {
        if (topComponentContextSubModel == null) {
            topComponentContextSubModel = new TopComponentContextSubModel();
        }
        return topComponentContextSubModel;
    }
    
}

