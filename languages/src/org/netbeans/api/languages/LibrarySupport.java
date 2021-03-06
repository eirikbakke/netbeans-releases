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

package org.netbeans.api.languages;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.xml.XMLUtil;


/**
 * Support for definition of libraries. Used for code completion.
 * 
 * @author Jan Jancura
 */
public abstract class LibrarySupport {

    /**
     * Crates new Instance of LibrarySupport and reads library definition from give resource file.
     * 
     * @param resourceName a name of resource file
     */
    public static LibrarySupport create (String resourceName) {
        return new LibraryImpl (resourceName);
    }

    /**
     * Crates new Instance of LibrarySupport and reads library definition from give resource files.
     * 
     * @param resourceNames names of resource files
     */
    public static LibrarySupport create (List<String> resourceNames) {
        return new DelegatingLibrarySupport (resourceNames);
    }

    /**
     * Returns list of items for given context (e.g. list of static methods 
     * for fiven class name).
     * 
     * @param context
     * @return list of items for given context
     */
    public abstract List<String> getItems (String context);
    
    public abstract List<CompletionItem> getCompletionItems (String context);
    
    /**
     * Returns property for given item, context and property name.
     * 
     * @param context a context
     * @param item an item
     * @param propertyName a name of property
     */
    public abstract String getProperty (String context, String item, String propertyName);
    
    
    
    private static class LibraryImpl extends LibrarySupport {
        
        private String resourceName;

        LibraryImpl (String resourceName) {
            this.resourceName = resourceName;
        }


        private Map<String,List<String>> keys = new HashMap<String,List<String>> ();

        public List<String> getItems (String context) {
            List<String> k = keys.get (context);
            if (k == null) {
                Map<String,List<Map<String,String>>> m = getItems ().get (context);
                if (m == null) return null;
                k = new ArrayList<String> (m.keySet ());
                Collections.<String>sort (k);
                k = Collections.<String>unmodifiableList (k);
                keys.put (context, k);
            }
            return k;
        }
    
        public List<CompletionItem> getCompletionItems (String context) {
            List<CompletionItem> result = new ArrayList<CompletionItem> ();
            Map<String,List<Map<String,String>>> items = getItems ().get (context);
            if (items == null) return result;
            Iterator<String> it = items.keySet ().iterator ();
            while (it.hasNext ()) {
                String name =  it.next();
                List<Map<String,String>> items2 = items.get (name);
                Iterator<Map<String,String>> it2 = items2.iterator ();
                while (it2.hasNext()) {
                    Map<String, String> properties =  it2.next();
                    CompletionItem.Type type = null;
                    String stype = properties.get ("type");
                    if ("class".equals (stype))
                        type = CompletionItem.Type.CLASS;
                    else
                    if ("field".equals (stype))
                        type = CompletionItem.Type.FIELD;
                    else
                    if ("constant".equals (stype))
                        type = CompletionItem.Type.CONSTANT;
                    else
                    if ("constructor".equals (stype))
                        type = CompletionItem.Type.CONSTRUCTOR;
                    else
                    if ("interface".equals (stype))
                        type = CompletionItem.Type.INTERFACE;
                    else
                    if ("keyword".equals (stype))
                        type = CompletionItem.Type.KEYWORD;
                    else
                    if ("local".equals (stype))
                        type = CompletionItem.Type.LOCAL;
                    else
                    if ("method".equals (stype))
                        type = CompletionItem.Type.METHOD;
                    else
                    if ("parameter".equals (stype))
                        type = CompletionItem.Type.PARAMETER;
                    result.add (CompletionItem.create (
                        name,
                        properties.get ("description"),
                        properties.get ("library"),
                        type,
                        200
                    ));
                }
            }
            return result;
        }

        /**
         * Returns property for given item, context and property name.
         * 
         * @param context a context
         * @param item an item
         * @param propertyName a name of property
         */
        public String getProperty (String context, String item, String propertyName) {
            Map<String,List<Map<String,String>>> m = getItems ().get (context);
            if (m == null) return null;
            List<Map<String,String>> l = m.get (item);
            if (l == null) return null;
            if (l.size () > 1)
                throw new IllegalArgumentException ();
            return l.get (0).get (propertyName);
        }


        // generics support methods ................................................

        // context>name>property>value
        private Map<String,Map<String,List<Map<String,String>>>> items;

        private Map<String,Map<String,List<Map<String,String>>>> getItems () {
            if (items == null)
                try {
                    XMLReader reader = XMLUtil.createXMLReader ();
                    Handler handler = new Handler ();
                    reader.setEntityResolver (handler);
                    reader.setContentHandler (handler);
                    ClassLoader loader = (ClassLoader) Lookup.getDefault ().
                        lookup (ClassLoader.class);
                    InputStream is = loader.getResourceAsStream (resourceName);
                    try {
                        reader.parse (new InputSource (is));
                    } finally {
                        is.close ();
                    }
                    items = handler.result;
                } catch (Exception ex) {
                    ErrorManager.getDefault ().notify (ex);
                    items = Collections.<String,Map<String,List<Map<String,String>>>> emptyMap ();
                }
            return items;
        }
    }
    
    static class Handler extends DefaultHandler {
        
        //context>key>propertyname>propertyvalue
        Map<String,Map<String,List<Map<String,String>>>> result = new HashMap<String,Map<String,List<Map<String,String>>>> ();
        
        public void startElement (
            String uri, 
            String localName,
            String name, 
            Attributes attributes
        ) throws SAXException {
            try {
                if (name.equals ("node")) {
                    String contexts = attributes.getValue ("context");
                    String key = attributes.getValue ("key");
                    Map<String,String> properties = null;
                    if (attributes.getLength () > 2) {
                        properties = new HashMap<String,String> ();
                        int i, k = attributes.getLength ();
                        for (i = 0; i < k; i++) {
                            String propertyName = attributes.getQName (i);
                            if ("context".equals (propertyName)) continue;
                            if ("key".equals (propertyName)) continue;
                            properties.put (propertyName, attributes.getValue (i));
                        }
                    }
                    while (true) {
                        int i = contexts.indexOf (',');
                        String context = i >= 0 ? 
                            contexts.substring (0, i).trim () : contexts;
                        Map<String,List<Map<String,String>>> names = result.get (context);
                        if (names == null) {
                            names = new HashMap<String,List<Map<String,String>>> ();
                            result.put (context, names);
                        }
                        List<Map<String,String>> entries = names.get (key);
                        if (entries == null) {
                            entries = new ArrayList<Map<String,String>> ();
                            names.put (key, entries);
                        }
                        entries.add (properties);
                        if (i < 0) break;
                        contexts = contexts.substring (i + 1);
                    }
                } 
            } catch (Exception ex) {
                ErrorManager.getDefault ().notify (ex);
            }
        }
        
        public InputSource resolveEntity (String pubid, String sysid) {
            return new InputSource (
                new java.io.ByteArrayInputStream (new byte [0])
            );
        }
    }
    
    static class DelegatingLibrarySupport extends LibrarySupport {
        
        private List<LibrarySupport> libraries = new ArrayList<LibrarySupport> ();
        
        DelegatingLibrarySupport (
            List<String> resources
        ) {
            Iterator<String> it = resources.iterator ();
            while (it.hasNext ()) {
                String resource =  it.next();
                libraries.add (new LibraryImpl (resource));
            }
        }
    
        public List<String> getItems (String context) {
            List<String> result = new ArrayList<String> ();
            Iterator<LibrarySupport> it = libraries.iterator ();
            while (it.hasNext ()) {
                LibrarySupport librarySupport =  it.next();
                result.addAll (librarySupport.getItems (context));
            }
            return result;
        }
    
        public List<CompletionItem> getCompletionItems (String context) {
            List<CompletionItem> result = new ArrayList<CompletionItem> ();
            Iterator<LibrarySupport> it = libraries.iterator ();
            while (it.hasNext ()) {
                LibrarySupport librarySupport =  it.next();
                result.addAll (librarySupport.getCompletionItems (context));
            }
            return result;
        }

        public String getProperty (
            String context, 
            String item,
            String propertyName
        ) {
            Iterator<LibrarySupport> it = libraries.iterator ();
            while (it.hasNext ()) {
                LibrarySupport librarySupport =  it.next();
                String result = librarySupport.getProperty (context, item, propertyName);
                if (result != null) 
                    return result;
            }
            return null;
        }
    }
}
