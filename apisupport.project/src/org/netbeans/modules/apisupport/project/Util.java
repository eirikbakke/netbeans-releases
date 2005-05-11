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

package org.netbeans.modules.apisupport.project;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Utility methods for the module.
 * @author Jesse Glick
 */
class Util {
    
    private Util() {}
    
    // XXX openide split hack data, remove when openide.jar is gone and deps are precise:
    static final String[] OPENIDE_SUBMODULES = {
        "actions", // NOI18N
        "dialogs", // NOI18N
        "explorer", // NOI18N
        "fs", // NOI18N
        "nodes", // NOI18N
        "modules", // NOI18N
        "options", // NOI18N
        "text", // NOI18N
        "util", // NOI18N
        "util/enum", // NOI18N
        "windows", // NOI18N
        "awt", // NOI18N
        "compat", // NOI18N
    };
    static final String[] OPENIDE_SUBMODULE_CNBS = {
        "org.openide.actions",
        "org.openide.dialogs",
        "org.openide.explorer",
        "org.openide.filesystems",
        "org.openide.nodes",
        "org.openide.modules",
        "org.openide.options",
        "org.openide.text",
        "org.openide.util",
        "org.openide.util.enumerations", 
        "org.openide.windows",
        "org.openide.awt",
        "org.openide.compat",
    };

    public static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.modules.apisupport.project"); // NOI18N
    
    // COPIED FROM org.netbeans.modules.project.ant:
    // (except for namespace == null support in findElement)
    // (and support for comments in findSubElements)
    
    /**
     * Search for an XML element in the direct children of a parent.
     * DOM provides a similar method but it does a recursive search
     * which we do not want. It also gives a node list and we want
     * only one result.
     * @param parent a parent element
     * @param name the intended local name
     * @param namespace the intended namespace (or null)
     * @return the one child element with that name, or null if none or more than one
     */
    public static Element findElement(Element parent, String name, String namespace) {
        Element result = null;
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element)l.item(i);
                if ((namespace == null && name.equals(el.getTagName())) ||
                    (namespace != null && name.equals(el.getLocalName()) &&
                                          namespace.equals(el.getNamespaceURI()))) {
                    if (result == null) {
                        result = el;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Search for an XML element in the direct children of a parent.
     * DOM provides a similar method but it does a recursive search
     * which we do not want. It also gives a node list and we want
     * only one result.
     * @param parent a parent element
     * @param name the intended local name
     * @param namespace a list of possible intended namespaces (not null or empty, but one element may be null)
     * @return the one child element with that name, or null if none or more than one
     */
    public static Element findElement(Element parent, String name, String[] namespaces) {
        Collection/*<String>*/ _namespaces = Arrays.asList(namespaces);
        Element result = null;
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element)l.item(i);
                if ((_namespaces.contains(null) && name.equals(el.getTagName())) ||
                    (name.equals(el.getLocalName()) &&
                     _namespaces.contains(el.getNamespaceURI()))) {
                    if (result == null) {
                        result = el;
                    } else {
                        return null;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Extract nested text from an element.
     * Currently does not handle coalescing text nodes, CDATA sections, etc.
     * @param parent a parent element
     * @return the nested text, or null if none was found
     */
    public static String findText(Element parent) {
        NodeList l = parent.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getNodeType() == Node.TEXT_NODE) {
                Text text = (Text)l.item(i);
                return text.getNodeValue();
            }
        }
        return null;
    }
    
    /**
     * Find all direct child elements of an element.
     * More useful than {@link Element#getElementsByTagNameNS} because it does
     * not recurse into recursive child elements.
     * Children which are all-whitespace text nodes or comments are ignored; others cause
     * an exception to be thrown.
     * @param parent a parent element in a DOM tree
     * @return a list of direct child elements (may be empty)
     * @throws IllegalArgumentException if there are non-element children besides whitespace
     */
    public static List/*<Element>*/ findSubElements(Element parent) throws IllegalArgumentException {
        NodeList l = parent.getChildNodes();
        List/*<Element>*/ elements = new ArrayList(l.getLength());
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element)n);
            } else if (n.getNodeType() == Node.TEXT_NODE) {
                String text = ((Text)n).getNodeValue();
                if (text.trim().length() > 0) {
                    throw new IllegalArgumentException("non-ws text encountered in " + parent + ": " + text); // NOI18N
                }
            } else if (n.getNodeType() == Node.COMMENT_NODE) {
                // OK, ignore
            } else {
                throw new IllegalArgumentException("unexpected non-element child of " + parent + ": " + n); // NOI18N
            }
        }
        return elements;
    }
    
    // CANDIDATES FOR FileUtil:
    
    /**
     * Creates a URL for a directory on disk.
     * Works correctly even if the directory does not currently exist.
     */
    public static URL urlForDir(File dir) {
        try {
            URL u = FileUtil.normalizeFile(dir).toURI().toURL();
            String s = u.toExternalForm();
            if (s.endsWith("/")) {
                return u;
            } else {
                return new URL(s + "/");
            }
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates a URL for the root of a JAR on disk.
     */
    public static URL urlForJar(File jar) {
        try {
            return FileUtil.getArchiveRoot(FileUtil.normalizeFile(jar).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    /**
     * Creates a URL for a directory on disk or the root of a JAR.
     * Works correctly whether or not the directory or JAR currently exists.
     * Detects whether the file is supposed to be a directory or a JAR.
     */
    public static URL urlForDirOrJar(File location) {
        try {
            URL u = FileUtil.normalizeFile(location).toURI().toURL();
            if (FileUtil.isArchiveFile(u)) {
                u = FileUtil.getArchiveRoot(u);
            } else {
                String us = u.toExternalForm();
                if (!us.endsWith("/")) { // NOI18N
                    u = new URL(us + "/"); // NOI18N
                }
            }
            return u;
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
}
