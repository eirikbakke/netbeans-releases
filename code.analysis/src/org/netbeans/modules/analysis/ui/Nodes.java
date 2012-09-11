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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.analysis.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.analysis.AnalysisResult;
import org.netbeans.modules.analysis.DescriptionReader;
import org.netbeans.modules.analysis.SPIAccessor;
import org.netbeans.modules.analysis.spi.Analyzer.AnalyzerFactory;
import org.netbeans.modules.analysis.spi.Analyzer.WarningDescription;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.actions.OpenAction;
import org.openide.cookies.LineCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.text.Line;
import org.openide.text.Line.ShowOpenType;
import org.openide.text.Line.ShowVisibilityType;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lahvac
 */
public class Nodes {

    private static final Logger LOG = Logger.getLogger(Nodes.class.getName());
    
    public static Node constructSemiLogicalView(final AnalysisResult errors, final boolean byCategory) {
        return new AbstractNode(Children.create(new ChildFactory<Node>() {
            @Override protected boolean createKeys(List<Node> toPopulate) {
                constructSemiLogicalView(errors, byCategory, toPopulate);
                return true;
            }
            @Override protected Node createNodeForKey(Node key) {
                return key;
            }
        }, true));
    }
    
    private static void constructSemiLogicalView(AnalysisResult errors, boolean byCategory, List<Node> toPopulate) {
        for (Node n : errors.extraNodes) {
            toPopulate.add(n.cloneNode());
        }
        
        if (!byCategory) {
            toPopulate.addAll(constructSemiLogicalViewNodes(new LogicalViewCache(), sortErrors(errors.provider2Hints, BY_FILE)));
        } else {
            Map<AnalyzerFactory, Map<String, WarningDescription>> analyzerId2Description = new HashMap<AnalyzerFactory, Map<String, WarningDescription>>();
            Map<String, Map<AnalyzerFactory, List<ErrorDescription>>> byCategoryId = sortErrors(errors.provider2Hints, new ByCategoryRetriever(analyzerId2Description));
            List<Node> categoryNodes = new ArrayList<Node>(byCategoryId.size());
            LogicalViewCache lvc = new LogicalViewCache();

            for (Entry<String, Map<AnalyzerFactory, List<ErrorDescription>>> categoryEntry : byCategoryId.entrySet()) {
                Map<String, Map<AnalyzerFactory, List<ErrorDescription>>> byId = sortErrors(categoryEntry.getValue(), BY_ID);
                List<Node> warningTypNodes = new ArrayList<Node>(byId.size());
                long categoryWarnings = 0;

                for (Entry<String, Map<AnalyzerFactory, List<ErrorDescription>>> typeEntry : byId.entrySet()) {
                    AnalyzerFactory analyzer = typeEntry.getValue().keySet().iterator().next();
                    final Image icon = SPIAccessor.ACCESSOR.getAnalyzerIcon(analyzer);

                    WarningDescription wd = typeEntry.getKey() != null ? findWarningDescription(analyzerId2Description, analyzer, typeEntry.getKey()) : null;
                    String typeDisplayName = wd != null ? SPIAccessor.ACCESSOR.getWarningDisplayName(wd) : null;
                    long typeWarnings = 0;

                    for (List<ErrorDescription> v1 : typeEntry.getValue().values()) {
                        typeWarnings += v1.size();
                    }

                    final String typeHtmlDisplayName = (typeDisplayName != null ? translate(typeDisplayName) : "Unknown") + " <b>(" + typeWarnings + ")</b>";
                    AbstractNode typeNode = new AbstractNode(constructSemiLogicalViewChildren(lvc, sortErrors(typeEntry.getValue(), BY_FILE))) {
                        @Override public Image getIcon(int type) {
                            return icon;
                        }
                        @Override public Image getOpenedIcon(int type) {
                            return icon;
                        }
                        @Override public String getHtmlDisplayName() {
                            return typeHtmlDisplayName;
                        }
                        @Override public Action[] getActions(boolean context) {
                            return new Action[0];
                        }
                    };

                    warningTypNodes.add(typeNode);
                    categoryWarnings += typeWarnings;
                }
                
                Collections.sort(warningTypNodes, new Comparator<Node>() {
                    @Override public int compare(Node o1, Node o2) {
                        return o1.getDisplayName().compareTo(o2.getDisplayName());
                    }
                });

                AnalyzerFactory analyzer = categoryEntry.getValue().keySet().iterator().next();//TODO: multiple Analyzers for this category
                final Image icon = SPIAccessor.ACCESSOR.getAnalyzerIcon(analyzer);
                final String categoryHtmlDisplayName = translate(categoryEntry.getKey()) + " <b>(" + categoryWarnings + ")</b>";
                AbstractNode categoryNode = new AbstractNode(new DirectChildren(warningTypNodes)) {
                    @Override public Image getIcon(int type) {
                        return icon;
                    }
                    @Override public Image getOpenedIcon(int type) {
                        return icon;
                    }
                    @Override public String getHtmlDisplayName() {
                        return categoryHtmlDisplayName;
                    }
                    @Override public Action[] getActions(boolean context) {
                        return new Action[0];
                    }
                };

                categoryNodes.add(categoryNode);
            }

            Collections.sort(categoryNodes, new Comparator<Node>() {
                @Override public int compare(Node o1, Node o2) {
                    return o1.getDisplayName().compareTo(o2.getDisplayName());
                }
            });

            toPopulate.addAll(categoryNodes);
        }
    }

    private static <A> Map<A, Map<AnalyzerFactory, List<ErrorDescription>>> sortErrors(Map<AnalyzerFactory, List<ErrorDescription>> errs, AttributeRetriever<A> attributeRetriever) {
        Map<A, Map<AnalyzerFactory, List<ErrorDescription>>> sorted = new HashMap<A, Map<AnalyzerFactory, List<ErrorDescription>>>();

        for (Entry<AnalyzerFactory, List<ErrorDescription>> e : errs.entrySet()) {
            for (ErrorDescription ed : e.getValue()) {
                if (ed == null) {
                    //XXX:
                    LOG.log(Level.FINE, "null ErrorDescription produced by {0} ({1})", new Object[] {SPIAccessor.ACCESSOR.getAnalyzerDisplayName(e.getKey()), e.getKey().getClass()});
                    continue;
                }
                
                A attribute = attributeRetriever.getAttribute(e.getKey(), ed);
                Map<AnalyzerFactory, List<ErrorDescription>> errorsPerAttributeValue = sorted.get(attribute);

                if (errorsPerAttributeValue == null) {
                    sorted.put(attribute, errorsPerAttributeValue = new HashMap<AnalyzerFactory, List<ErrorDescription>>());
                }

                List<ErrorDescription> errors = errorsPerAttributeValue.get(e.getKey());

                if (errors == null) {
                    errorsPerAttributeValue.put(e.getKey(), errors = new ArrayList<ErrorDescription>());
                }

                errors.add(ed);
            }
        }

        return sorted;
    }

    private static interface AttributeRetriever<A> {
        public A getAttribute(AnalyzerFactory a, ErrorDescription ed);
    }

    private static final AttributeRetriever<FileObject> BY_FILE = new AttributeRetriever<FileObject>() {
        @Override public FileObject getAttribute(AnalyzerFactory a, ErrorDescription ed) {
            return ed.getFile();
        }
    };

    private static final AttributeRetriever<String> BY_ID = new AttributeRetriever<String>() {
        @Override public String getAttribute(AnalyzerFactory a, ErrorDescription ed) {
            return ed.getId();
        }
    };

    private static final class ByCategoryRetriever implements AttributeRetriever<String> {
        private final Map<AnalyzerFactory, Map<String, WarningDescription>> analyzerId2Description;
        public ByCategoryRetriever(Map<AnalyzerFactory, Map<String, WarningDescription>> analyzerId2Description) {
            this.analyzerId2Description = analyzerId2Description;
        }
        @Override public String getAttribute(AnalyzerFactory a, ErrorDescription ed) {
            String id = ed.getId();

            if (id == null) {
                Logger.getLogger(Nodes.class.getName()).log(Level.FINE, "No ID for: {0}", ed.toString());
                return "Unknown";
            }

            WarningDescription wd = findWarningDescription(analyzerId2Description, a, id);

            if (wd == null) return "Unknown";
            else return SPIAccessor.ACCESSOR.getWarningCategoryDisplayName(wd);
        }
    }

    private static WarningDescription findWarningDescription(Map<AnalyzerFactory, Map<String, WarningDescription>> analyzerId2Description, AnalyzerFactory a, String id) {
        Map<String, WarningDescription> warnings = analyzerId2Description.get(a);
        
        if (warnings == null) {
            analyzerId2Description.put(a, warnings = new HashMap<String, WarningDescription>());
            for (WarningDescription wd : a.getWarnings()) {
                warnings.put(SPIAccessor.ACCESSOR.getWarningId(wd), wd);
            }
        }

        return warnings.get(id);
    }

    private static Children constructSemiLogicalViewChildren(final LogicalViewCache lvc, final Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>> errors) {
        return Children.create(new ChildFactory<Node>() {
            @Override protected boolean createKeys(List<Node> toPopulate) {
                toPopulate.addAll(constructSemiLogicalViewNodes(lvc, errors));
                return true;
            }
            @Override protected Node createNodeForKey(Node key) {
                return key;
            }
        }, true);
    }

    private static List<Node> constructSemiLogicalViewNodes(LogicalViewCache lvc, Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>> errors) {
        Map<Project, Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>>> projects = new HashMap<Project, Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>>>();
        
        for (FileObject file : errors.keySet()) {
            Project project = FileOwnerQuery.getOwner(file);
            
            if (project == null) {
                Logger.getLogger(Nodes.class.getName()).log(Level.WARNING, "Cannot find project for: {0}", FileUtil.getFileDisplayName(file));
            }
            
            Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>> projectErrors = projects.get(project);
            
            if (projectErrors == null) {
                projects.put(project, projectErrors = new HashMap<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>>());
            }
            
            projectErrors.put(file, errors.get(file));
        }
        
        projects.remove(null);
        
        List<Node> nodes = new ArrayList<Node>(projects.size());
        
        for (Project p : projects.keySet()) {
            nodes.add(constructSemiLogicalView(p, lvc, projects.get(p)));
        }

        Collections.sort(nodes, new Comparator<Node>() {
            @Override public int compare(Node o1, Node o2) {
                return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
            }
        });
        
//        Children.Array subNodes = new Children.Array();
//        
//        subNodes.add(nodes.toArray(new Node[0]));
        
        return nodes;
    }
    
    private static Node constructSemiLogicalView(final Project p, LogicalViewCache lvc, final Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>> errors) {
        Node view = lvc.project2LogicalViewRootNode.get(p.getProjectDirectory());
        final LogicalViewProvider lvp = p.getLookup().lookup(LogicalViewProvider.class);
        
        if (view == null) {
            if (lvp != null) {
                view = lvp.createLogicalView();
            } else {
                try {
                    view = DataObject.find(p.getProjectDirectory()).getNodeDelegate();
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                    return new AbstractNode(Children.LEAF);
                }
            }
            
            lvc.project2LogicalViewRootNode.put(p.getProjectDirectory(), view);
        }

        int warnings = 0;

        for (Map<AnalyzerFactory, List<ErrorDescription>> v1 : errors.values()) {
            for (List<ErrorDescription> v2 : v1.values()) {
                warnings += v2.size();
            }
        }
        
        Map<Node, Map<AnalyzerFactory, List<ErrorDescription>>> foundNodes = resolveFileNodes(lvp, lvc, view, errors);

        if (foundNodes == null) {
            final Node viewNode = view;
            AbstractNode pnsNode = new AbstractNode(Children.LEAF) {
                @Override public Image getIcon(int type) {
                    return viewNode.getIcon(type);
                }
                @Override public Image getOpenedIcon(int type) {
                    return viewNode.getOpenedIcon(type);
                }
            };
            pnsNode.setDisplayName(NbBundle.getMessage(Nodes.class, "ERR_ProjectNotSupported", view.getDisplayName()));

            return pnsNode;
        }
        
        return new Wrapper(view, warnings, foundNodes);
    }

    private static Map<Node, Map<AnalyzerFactory, List<ErrorDescription>>> resolveFileNodes(LogicalViewProvider lvp, LogicalViewCache lvc, final Node view, Map<FileObject, Map<AnalyzerFactory, List<ErrorDescription>>> errors) {
        Map<Node, Map<AnalyzerFactory, List<ErrorDescription>>> fileNodes = new HashMap<Node, Map<AnalyzerFactory, List<ErrorDescription>>>();

        for (FileObject file : errors.keySet()) {
            Map<AnalyzerFactory, List<ErrorDescription>> eds = errors.get(file);
            Node foundChild = lvc.file2FileNode.get(file);
            
            if (foundChild == null) {
                foundChild = locateChild(view, lvp, file);
                if (foundChild != null) {
                    lvc.file2FileNode.put(file, foundChild);
                }
            }

            if (foundChild == null) {
                return null;
            }

            fileNodes.put(foundChild, eds);
        }

        return fileNodes;
    }
    
    private static Node locateChild(Node parent, LogicalViewProvider lvp, FileObject file) {
        if (lvp != null) {
            return lvp.findPath(parent, file);
        }

        throw new UnsupportedOperationException("Not done yet");
    }

    private static class Wrapper extends FilterNode {

        private final int warningsCount;

        public Wrapper(Node orig, int warningsCount, Map<Node, Map<AnalyzerFactory, List<ErrorDescription>>> fileNodes) {
            super(orig, new WrapperChildren(orig, fileNodes), Lookup.EMPTY);
            this.warningsCount = warningsCount;
        }
        
        public Wrapper(Node orig, Map<AnalyzerFactory, List<ErrorDescription>> errors, boolean file) {
            super(orig, new ErrorDescriptionChildren(errors), lookupForFileNode(orig, errors));
            int warnings = 0;
            for (List<ErrorDescription> e : errors.values()) {
                warnings += e.size();
            }
            this.warningsCount = warnings;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }

        private String displayName;

        @Override
        public String getDisplayName() {
            if (displayName == null) {
                return getOriginal().getDisplayName();
            }
            return displayName;
        }

        private String htmlDisplayName;
        
        @Override
        public String getHtmlDisplayName() {
            if (htmlDisplayName == null) {
                if (getOriginal().getHtmlDisplayName() == null) {
                    return translate(getOriginal().getDisplayName()) + " <b>(" + warningsCount + ")</b>";
                }
                return getOriginal().getHtmlDisplayName() + " <b>(" + warningsCount + ")</b>";
            }
            return htmlDisplayName;
        }

        private void fireDisplayNameChange() {
            fireDisplayNameChange(null, getDisplayName());
        }

    }

    private static Lookup lookupForFileNode(Node n, Map<AnalyzerFactory, List<ErrorDescription>> errors) {
        return Lookups.fixed();
    }
    
    private static boolean isParent(Node parent, Node child) {
        if (NodeOp.isSon(parent, child)) {
            return true;
        }

        Node p = child.getParentNode();

        if (p == null) {
            return false;
        }

        return isParent(parent, p);
    }
        
    private static class WrapperChildren extends FilterNode.Children {

        private final java.util.Map<Node, java.util.Map<AnalyzerFactory, List<ErrorDescription>>> fileNodes;

        public WrapperChildren(Node orig, java.util.Map<Node, java.util.Map<AnalyzerFactory, List<ErrorDescription>>> fileNodes) {
            super(orig);
            this.fileNodes = fileNodes;
        }
        
        @Override
        protected synchronized Node[] createNodes(Node key) {
            if (fileNodes.containsKey(key)) {
                
                return new Node[] {new Wrapper(key, fileNodes.get(key), true)};
            }
            final java.util.Map<Node, java.util.Map<AnalyzerFactory, List<ErrorDescription>>> fileNodesInside = new HashMap<Node, java.util.Map<AnalyzerFactory, List<ErrorDescription>>>();
            int warnings = 0;
            for (Entry<Node, java.util.Map<AnalyzerFactory, List<ErrorDescription>>> e : fileNodes.entrySet()) {
                if (isParent(key, e.getKey())) {
                    fileNodesInside.put(e.getKey(), e.getValue());
                    for (List<ErrorDescription> w : e.getValue().values()) {
                        warnings += w.size();
                    }
                }
            }
            if (fileNodesInside.isEmpty()) {
                return new Node[0];
            }
            return new Node[] {new Wrapper(key, warnings, fileNodesInside)};
        }
        
    }
    
    private static final class DirectChildren extends Children.Keys<Node> {

        public DirectChildren(Collection<Node> nodes) {
            setKeys(nodes);
        }
        
        @Override
        protected Node[] createNodes(Node key) {
            return new Node[] {key};
        }
    }

    private static final class ErrorDescriptionChildren extends Children.Keys<ErrorDescription> {

        private final java.util.Map<ErrorDescription, AnalyzerFactory> error2Analyzer = new HashMap<ErrorDescription, AnalyzerFactory>();
        public ErrorDescriptionChildren(java.util.Map<AnalyzerFactory, List<ErrorDescription>> errors) {
            List<ErrorDescription> eds = new ArrayList<ErrorDescription>();

            for (Entry<AnalyzerFactory, List<ErrorDescription>> e : errors.entrySet()) {
                for (ErrorDescription ed : e.getValue()) {
                    error2Analyzer.put(ed, e.getKey());
                    eds.add(ed);
                }
            }

            Collections.sort(eds, new Comparator<ErrorDescription>() {
                @Override public int compare(ErrorDescription o1, ErrorDescription o2) {
                    try {
                        return o1.getRange().getBegin().getLine() - o2.getRange().getBegin().getLine();
                    } catch (IOException ex) {
                        throw new IllegalStateException(ex); //XXX
                    }
                }
            });

            setKeys(eds);
        }

        @Override
        protected Node[] createNodes(ErrorDescription key) {
            return new Node[] {new ErrorDescriptionNode(error2Analyzer.get(key), key)};
        }

    }

    private static final class ErrorDescriptionNode extends AbstractNode {

        private final Image icon;

        public ErrorDescriptionNode(AnalyzerFactory provider, final ErrorDescription ed) {
            super(Children.LEAF, Lookups.fixed(ed, new OpenErrorDescription(provider, ed), new DescriptionReader() {
                @Override public CharSequence getDescription() {
                    return ed.getDetails();
                }
            }));
            int line = -1;
            try {
                line = ed.getRange().getBegin().getLine();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            setDisplayName((line != (-1) ? (line + 1 + ":") : "") + ed.getDescription());
            icon = SPIAccessor.ACCESSOR.getAnalyzerIcon(provider);
        }

        @Override
        public Action getPreferredAction() {
            return OpenAction.get(OpenAction.class);
        }

        @Override
        public Image getIcon(int type) {
            return icon;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }
    }

    private static final class OpenErrorDescription implements OpenCookie {

        private final AnalyzerFactory analyzer;
        private final ErrorDescription ed;

        public OpenErrorDescription(AnalyzerFactory analyzer, ErrorDescription ed) {
            this.analyzer = analyzer;
            this.ed = ed;
        }

        @Override
        public void open() {
            openErrorDescription(analyzer, ed);
        }
        
    }

    static void openErrorDescription(AnalyzerFactory analyzer, ErrorDescription ed) throws IndexOutOfBoundsException {
        try {
            DataObject od = DataObject.find(ed.getFile());
            LineCookie lc = od.getLookup().lookup(LineCookie.class);

            if (lc != null) {
                Line line = lc.getLineSet().getCurrent(ed.getRange().getBegin().getLine());

                line.show(ShowOpenType.OPEN, ShowVisibilityType.FOCUS);
                analyzer.warningOpened(ed);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static String[] c = new String[] {"&", "<", ">", "\n", "\""}; // NOI18N
    private static String[] tags = new String[] {"&amp;", "&lt;", "&gt;", "<br>", "&quot;"}; // NOI18N

    private static String translate(String input) {
        for (int cntr = 0; cntr < c.length; cntr++) {
            input = input.replaceAll(c[cntr], tags[cntr]);
        }

        return input;
    }
    
    interface FutureValue<T> {
        T get();
    }
    
    public static final class LogicalViewCache {
        private final Map<FileObject, Node> project2LogicalViewRootNode = new HashMap<FileObject, Node>();
        private final Map<FileObject, Node> file2FileNode = new HashMap<FileObject, Node>();
    }

}
