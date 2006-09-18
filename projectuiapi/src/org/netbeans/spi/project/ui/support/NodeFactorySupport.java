/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.project.ui.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.FolderLookup;
import org.openide.loaders.InstanceDataObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Support class for creating Project node's children nodes from NodeFactory instances
 * in layers.
 * @author mkleint
 * @since org.netbeans.modules.projectuiapi/1 1.18
 */
public class NodeFactorySupport {
    
    /**
     * Creates a new instance of NodeFactorySupport
     */
    private NodeFactorySupport() {
    }
    
    /**
     * create Node' Children instance that works on top of NodeFactory instances
     * in the layers.
     * @param folderPath the path in the System Filesystem that is used as root for subnode composition.
     *        The content of the folder is assumed to be {@link org.netbeans.spi.project.ui.support.NodeFactory} instances
     * 
     */
    public static Children createCompositeChildren(Project project, String folderPath) {
        return new DelegateChildren(project, folderPath);
    }
    /**
     * Utility method for creating a non variable NodeList instance.
     */
    public static NodeList fixedNodeList(Node[] nodes) {
        return new FixedNodeList(nodes);
    }
    
    /**
     * Utility method to create empty NodeList instance. Useful in case when a particular project instance is not
     * relevant to the NodeFactory
     */
    public static NodeList emptyNodeList() {
        return new FixedNodeList(new Node[0]);
    }
    
    
    private static class FixedNodeList implements  NodeList {
        
        private List nodes;
        
        FixedNodeList(Node[] nds) {
            nodes = Arrays.asList(nds);
        }
        public List keys() {
            return nodes;
        }
        
        public void addChangeListener(ChangeListener l) { }
        
        public void removeChangeListener(ChangeListener l) { }
        
        public Node node(Object key) {
            return (Node)key;
        }
        
        public void addNotify() {
        }
        
        public void removeNotify() {
        }
    }
    
    static class DelegateChildren extends Children.Keys implements LookupListener, ChangeListener {
        
        private String folderPath;
        private Project project;
        private List nodeLists = new ArrayList();
        private List factories = new ArrayList();
        private Lookup.Result result;
        
        public DelegateChildren(Project proj, String path) {
            folderPath = path;
            project = proj;
        }
        
        // protected for tests..
        protected Lookup createLookup() {
            FileObject root = Repository.getDefault().getDefaultFileSystem().findResource(folderPath);
            DataFolder folder = DataFolder.findFolder(root);
            return new FolderLookup(folder).getLookup();
        }
        
        protected Node[] createNodes(Object key) {
            NodeList lst = (NodeList)key;
            Iterator it = lst.keys().iterator();
            List toRet = new ArrayList();
            while (it.hasNext()) {
                Object elem = it.next();
                Node nd = lst.node(elem);
                if (nd != null) {
                    toRet.add(nd);
                }
            }
            return (Node[])toRet.toArray(new Node[toRet.size()]);
        }
        
        protected void addNotify() {
            super.addNotify();
            result = createLookup().lookup(new Lookup.Template(NodeFactory.class));
            Iterator it = result.allInstances().iterator();
            while (it.hasNext()) {
                NodeFactory factory = (NodeFactory) it.next();
                NodeList lst = factory.createNodes(project);
                assert lst != null;
                nodeLists.add(lst);
                lst.addNotify();
                lst.addChangeListener(this);
                factories.add(factory);
            }
            result.addLookupListener(this);
            setKeys(nodeLists);
        }
        
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
            Iterator it = nodeLists.iterator();
            while (it.hasNext()) {
                NodeList elem = (NodeList) it.next();
                elem.removeChangeListener(this);
                elem.removeNotify();
            }
            nodeLists.clear();
            factories.clear();
            if (result != null) {
                result.removeLookupListener(this);
                result = null;
            }
        }
        
        public void stateChanged(ChangeEvent e) {
            refreshKey(e.getSource());
        }

        public void resultChanged(LookupEvent ev) {
            Iterator it = result.allInstances().iterator();
            int index = 0;
            while (it.hasNext()) {
                NodeFactory factory = (NodeFactory) it.next();
                if (!factories.contains(factory)) {
                    factories.add(index, factory);
                    NodeList lst = factory.createNodes(project);
                    assert lst != null;
                    nodeLists.add(index, lst);
                    lst.addNotify();
                    lst.addChangeListener(this);
                } else {
                    while (!factory.equals(factories.get(index))) {
                        factories.remove(index);
                        NodeList lst = (NodeList)nodeLists.remove(index);
                        lst.removeNotify();
                        lst.removeChangeListener(this);                            
                    }
                }
                index++;
            }
            setKeys(nodeLists);
        }
        
    }
    
}
