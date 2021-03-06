/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.editor.facelets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.modules.web.jsf.editor.index.IndexedFile;
import org.netbeans.modules.web.jsf.editor.index.JsfIndex;
import org.netbeans.modules.web.jsfapi.api.Attribute;
import org.netbeans.modules.web.jsfapi.api.Tag;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Wraps a source LibraryDescriptor and if there's a TLD defined for the
 * original LibraryDescriptor namespace it is used as a backup if the tag
 * metadata are not preset in the source LibraryDescriptor
 *
 * @todo use SAX to parse the descriptor, not DOM!
 *
 * @author marekfukala
 */
public class TldProxyLibraryDescriptor implements LibraryDescriptor {
    private static final Logger LOG = Logger.getLogger(TldProxyLibraryDescriptor.class.getName());

    private LibraryDescriptor source;
    private LibraryDescriptor tld;

    public TldProxyLibraryDescriptor(LibraryDescriptor source, JsfIndex index) {
        this.source = source;
        initTLD(index);
    }

    @Override
    public String getNamespace() {
        return source.getNamespace();
    }

    @Override
    public String getPrefix() {
        return source.getPrefix();
    }

    @Override
    public Map<String, Tag> getTags() {
        if(tld == null) {
            return source.getTags();
        }

        //merge
        Map<String, Tag> s = source.getTags();
        Map<String, Tag> t = tld.getTags();

        Map<String, Tag> result = new HashMap<>();

        Collection<String> allTagNames = new HashSet<>();
        allTagNames.addAll(s.keySet());
        allTagNames.addAll(t.keySet());
        for(String tagName : allTagNames) {
            result.put(tagName, new ProxyTag(s.get(tagName), t.get(tagName)));
        }

        return result;
    }

    private void initTLD(JsfIndex index) {
        // omit libraries with undefined namespaces - #236539
        String namespace = getNamespace();
        if (namespace == null) {
            return;
        }
        IndexedFile file = index.getTagLibraryDescriptor(namespace);
        if (file == null) {
            return;
        }
        try {
            InputStream is = file.getFile().getInputStream();
            tld = parseTLD(is);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    protected LibraryDescriptor parseTLD(InputStream content) throws ParserConfigurationException, SAXException, IOException {
        final Map<String, Tag> tags = new HashMap<>();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        InputSource is = new InputSource(content); //the ecoding should be autodetected
        docBuilder.setEntityResolver(UserCatalog.getDefault().getEntityResolver()); //we count on TaglibCatalog from web.core module
        Document doc = docBuilder.parse(is);

//        //usually the default taglib prefix
//        Node tagLib = FaceletsLibraryDescriptor.getNodeByName(doc, "taglib"); //NOI18N
//        String prefix = getTextContent(tagLib, "short-name"); //NOI18N
//        String uri = getTextContent(tagLib, "uri"); //NOI18N
//        String displayName = getTextContent(tagLib, "display-name"); //NOI18N

        //scan the <tag> nodes content - the tag descriptions
        NodeList tagNodes = doc.getElementsByTagName("tag"); //NOI18N
        if (tagNodes != null) {
            for (int i = 0; i < tagNodes.getLength(); i++) {
                Node tag = tagNodes.item(i);
                String tagName = getTextContent(tag, "name"); //NOI18N
                String tagDescription = getTextContent(tag, "description"); //NOI18N
 
                Map<String, Attribute> attrs = new HashMap<>();
                //find attributes
                for (Node attrNode : FaceletsLibraryDescriptor.getNodesByName(tag, "attribute")) { //NOI18N
                    String aName = getTextContent(attrNode, "name"); //NOI18N
                    String aDescription = getTextContent(attrNode, "description"); //NOI18N
                    boolean aRequired = Boolean.parseBoolean(getTextContent(attrNode, "required")); //NOI18N
                    
                    String aType = null;
                    String aMethodSignature = null;
                    //type
                    Node aDeferredValueNode = FaceletsLibraryDescriptor.getNodeByName(attrNode, "deferred-value"); //NOI18N
                    if(aDeferredValueNode != null) {
                        aType = FaceletsLibraryDescriptor.getTextContent(aDeferredValueNode, "type"); //NOI18N
                    }
                    //method signature
                    Node aDeferredMethodNode = FaceletsLibraryDescriptor.getNodeByName(attrNode, "deferred-method"); //NOI18N
                    if(aDeferredMethodNode != null) {
                        aType = FaceletsLibraryDescriptor.getTextContent(aDeferredMethodNode, "method-signature"); //NOI18N
                    }

                    attrs.put(aName, new Attribute.DefaultAttribute(aName, aDescription, aType, aRequired, aMethodSignature));
                }

                tags.put(tagName, new TagImpl(tagName, tagDescription, attrs));

            }
        }

        return new LibraryDescriptor() {

            @Override
            public String getNamespace() {
                return TldProxyLibraryDescriptor.this.getNamespace();
            }

            @Override
            public String getPrefix() {
                return TldProxyLibraryDescriptor.this.getPrefix();
            }
            
            @Override
            public Map<String, Tag> getTags() {
                return tags;
            }

        };
    }

    private String getTextContent(Node node, String name) {
        return FaceletsLibraryDescriptor.getTextContent(node, name);
    }

}
