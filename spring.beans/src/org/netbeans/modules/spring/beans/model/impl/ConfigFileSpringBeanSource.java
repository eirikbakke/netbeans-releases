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

package org.netbeans.modules.spring.beans.model.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.spring.api.beans.SpringConstants;
import org.netbeans.modules.spring.api.beans.model.Location;
import org.netbeans.modules.spring.api.beans.model.SpringBean;
import org.netbeans.modules.spring.beans.editor.SpringXMLConfigEditorUtils;
import org.netbeans.modules.spring.beans.model.SpringBeanSource;
import org.netbeans.modules.spring.beans.utils.StringUtils;
import org.netbeans.modules.xml.text.syntax.dom.Tag;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.CloneableEditorSupport;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@link SpringBeanSource} delegating to
 * a file or a its document in the editor.
 *
 * @author Andrei Badea
 */
public class ConfigFileSpringBeanSource implements SpringBeanSource {

    private static final Logger LOGGER = Logger.getLogger(ConfigFileSpringBeanSource.class.getName());

    private final Map<String, ConfigFileSpringBean> id2Bean = new HashMap<String, ConfigFileSpringBean>();
    private final Map<String, ConfigFileSpringBean> name2Bean = new HashMap<String, ConfigFileSpringBean>();
    private final List<ConfigFileSpringBean> beans = new ArrayList<ConfigFileSpringBean>();

    /**
     * Parses a given bean file (or its document in available).
     * Currently the implementation expects it to be a {@link BaseDocument} or null.
     *
     * @param  document the document to parse.
     */
    public void parse(FileObject fo) throws IOException {
        LOGGER.log(Level.FINE, "Parsing {0}", fo);
        File file = FileUtil.toFile(fo);
        if (file == null) {
            LOGGER.log(Level.WARNING, "{0} resolves to a null File, aborting", fo);
            return;
        }
        BaseDocument doc = getOpenedDocument(fo);
        if (doc != null) {
            LOGGER.log(Level.FINE, "Parsing editor document for {0}", fo);
        } else {
            doc = getAsDocument(fo);
            LOGGER.log(Level.FINE, "Creating fake BaseDocument for {0}", fo);
        }
        parse(file, doc);
        LOGGER.log(Level.FINE, "Parsed {0}", fo);
    }

    private BaseDocument getOpenedDocument(FileObject fo) throws DataObjectNotFoundException {
        DataObject dataObject = DataObject.find(fo);
        EditorCookie ec = dataObject.getCookie(EditorCookie.class);
        if (ec == null) {
            return null;
        }
        Document doc = ec.getDocument();
        if (doc instanceof BaseDocument) {
            return (BaseDocument)doc;
        }
        return null;
    }

    // XXX This is just a very very ugly hack. We should be able to parse
    // the file without going through a document. But we have to for
    // now, since we need to use the XML parser in xml/text-edit.
    private BaseDocument getAsDocument(FileObject fo) throws IOException {
        StringBuilder builder = new StringBuilder();
        Charset charset = FileEncodingQuery.getEncoding(fo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fo.getInputStream(), charset));
        try {
            for (;;) {
                String line = reader.readLine();
                if (line != null) {
                    builder.append(line).append('\n');
                } else {
                    break;
                }
            }
        } finally {
            reader.close();
        }
        Class<?> kitClass = CloneableEditorSupport.getEditorKit(SpringConstants.CONFIG_MIME_TYPE).getClass();
        BaseDocument doc = new BaseDocument(kitClass, false);
        try {
            doc.insertString(0, builder.toString(), null);
        } catch (BadLocationException e) {
            // Unlikely to happen.
            LOGGER.log(Level.FINE, null, e);
        }
        return doc;
    }

    private void parse(File file, Document document) {
        document.render(new DocumentParser(file, document));
    }

    public List<SpringBean> getBeans() {
        return Collections.<SpringBean>unmodifiableList(beans);
    }

    public SpringBean findBeanByID(String id) {
        return id2Bean.get(id);
    }

    public SpringBean findBeanByIDOrName(String name) {
        SpringBean bean = findBeanByID(name);
        if (bean == null) {
            bean = name2Bean.get(name);
        }
        return bean;
    }

    /**
     * This is the actual document parser.
     */
    private final class DocumentParser implements Runnable {

        private final File file;
        private final Document document;

        public DocumentParser(File file, Document document) {
            this.file = file;
            this.document = document;
        }

        public void run() {
            id2Bean.clear();
            name2Bean.clear();
            beans.clear();
            Node rootNode = SpringXMLConfigEditorUtils.getDocumentRoot(document);
            NodeList childNodes = rootNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (!"bean".equals(node.getNodeName())) { // NOI18N
                    continue;
                }
                parseBean(node);
            }
        }

        private void parseBean(Node node) {
            String clazz = SpringXMLConfigEditorUtils.getAttribute(node, "class"); // NOI18N
            String id = SpringXMLConfigEditorUtils.getAttribute(node, "id"); // NOI18N
            String nameAttr = SpringXMLConfigEditorUtils.getAttribute(node, "name"); // NOI18N
            List<String> names;
            if (nameAttr != null) {
                names = Collections.unmodifiableList(StringUtils.tokenize(nameAttr, SpringXMLConfigEditorUtils.BEAN_NAME_DELIMITERS));
            } else {
                names = Collections.<String>emptyList();
            }
            Tag tag = (Tag)node;
            Location location = new ConfigFileLocation(file, tag.getElementOffset());
            ConfigFileSpringBean bean = new ConfigFileSpringBean(id, names, clazz, location);
            if (id != null) {
                addBeanID(id, bean);
            }
            for (String name : names) {
                addBeanName(name, bean);
            }
            beans.add(bean);
        }

        private void addBeanID(String id, ConfigFileSpringBean bean) {
            if (id2Bean.get(id) == null) {
                id2Bean.put(id, bean);
            }
        }

        private void addBeanName(String name, ConfigFileSpringBean bean) {
            if (name2Bean.get(name) == null) {
                name2Bean.put(name, bean);
            }
        }
    }
}
