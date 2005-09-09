/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbcore.ui.logicalview.ejb.action;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.modules.j2ee.common.JMIUtils;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.EjbMethodController;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 * Action that can always be invoked and work procedurally.
 * @author Chris Webster
 * @author Martin Adamek
 */
public abstract class AbstractAddMethodAction extends AbstractAction implements Presenter.Popup, ContextAwareAction {
    
    /** Action context. */
    private Lookup context;
    private String name;
    private AbstractAddMethodStrategy strategy;

    public AbstractAddMethodAction(AbstractAddMethodStrategy strategy) {
        super(/*strategy.getTitle()*/);
        this.strategy = strategy;
        this.name = strategy.getTitle();
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        this.context = actionContext;
        boolean enable = enable((Node[])context.lookup(new Lookup.Template (Node.class)).allInstances().toArray(new Node[0]));
        return enable ? this : null;
    }
    
    public String getName(){
        return strategy.getTitle();
    }
    
    protected boolean enable(org.openide.nodes.Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return false;
        }
        JMIUtils.beginJmiTransaction();
        try {
            JavaClass jc = JMIUtils.getJavaClassFromNode(activatedNodes[0]);
            boolean result = false;
            if (jc != null) {
                EjbMethodController c = EjbMethodController.createFromClass(jc);
                result = (c != null) && c.supportsMethodType(strategy.prototypeMethod());
            }
            return result;
        }
        finally {
            JMIUtils.endJmiTransaction();
        }
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
        performAction((Node[])context.lookup (new org.openide.util.Lookup.Template (
                Node.class
            )).allInstances().toArray(new Node[0]));
    }
    
    protected void performAction(org.openide.nodes.Node[] activatedNodes) {
        JavaClass jc = JMIUtils.getJavaClassFromNode(activatedNodes[0]);
        if (jc != null) {
            strategy.addMethod(jc);
        }
    }

    private Element getMemberElement(Node node) {
        return (Element) node.getLookup().lookup(Element.class);
    }

    private boolean isMemberElementNode(Node node) {
        return getMemberElement(node) != null;
    }

    public Object getValue(String key) {
        if (NAME.equals(key)) {
            return getName();
        }
        else {
            return super.getValue(key);
        }
    }

    public JMenuItem getPopupPresenter() {
        return new JMenuItem (this);
    }

}
