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
package org.netbeans.modules.j2ee.weblogic9.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.weblogic9.dd.model.BaseDescriptorModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;

/**
 *
 * @author Petr Hejl
 */
public class ConfigurationModifier<T extends BaseDescriptorModel> {

    /**
     * Perform webLogicWebApp changes defined by the webLogicWebApp modifier. Update editor
     * content and save changes, if appropriate.
     *
     * @param modifier
     */
    public final void modify(DescriptorModifier<T> modifier, DataObject dataObject, File file) throws ConfigurationException {
        Parameters.notNull("dataObject", dataObject);
        try {
            // get the document
            EditorCookie editor = (EditorCookie) dataObject.getCookie(EditorCookie.class);
            StyledDocument doc = editor.getDocument();
            if (doc == null) {
                doc = editor.openDocument();
            }

            // get the up-to-date model
            T newWeblogicWebApp = null;
            try {
                // try to create a graph from the editor content
                byte[] docString = doc.getText(0, doc.getLength()).getBytes();
                newWeblogicWebApp = modifier.load(docString);
            } catch (RuntimeException e) {
                T oldWeblogicWebApp = modifier.load();
                if (oldWeblogicWebApp == null) {
                    // neither the old graph is parseable, there is not much we can do here
                    // TODO: should we notify the user?
                    String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_configFileCannotParse", file.getPath());
                    throw new ConfigurationException(msg);
                }
                // current editor content is not parseable, ask whether to override or not
                NotifyDescriptor notDesc = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_descriptorNotValid", file.getPath()),
                        NotifyDescriptor.OK_CANCEL_OPTION);
                Object result = DialogDisplayer.getDefault().notify(notDesc);
                if (result == NotifyDescriptor.CANCEL_OPTION) {
                    // keep the old content
                    return;
                }
                // use the old graph
                newWeblogicWebApp = oldWeblogicWebApp;
            }

            // perform changes
            modifier.modify(newWeblogicWebApp);

            // save, if appropriate
            boolean modified = dataObject.isModified();
            replaceDocument(doc, newWeblogicWebApp);
            if (!modified) {
                SaveCookie cookie = (SaveCookie)dataObject.getCookie(SaveCookie.class);
                if (cookie != null) {
                    cookie.save();
                }
            }
            modifier.save(newWeblogicWebApp);
        } catch (BadLocationException ble) {
            // this should not occur, just log it if it happens
            Exceptions.printStackTrace(ble);
        } catch (IOException ioe) {
            String msg = NbBundle.getMessage(WarDeploymentConfiguration.class, "MSG_CannotUpdateFile", file.getPath());
            throw new ConfigurationException(msg, ioe);
        }
    }

    /**
     * Replace the content of the document by the graph.
     */
    private void replaceDocument(final StyledDocument doc, T graph) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            graph.write(out);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        NbDocument.runAtomic(doc, new Runnable() {
            public void run() {
                try {
                    doc.remove(0, doc.getLength());
                    doc.insertString(0, out.toString(), null);
                } catch (BadLocationException ble) {
                    Exceptions.printStackTrace(ble);
                }
            }
        });
    }

    public  interface DescriptorModifier<T extends BaseDescriptorModel> {

        void modify(T context);

        T load() throws IOException;

        T load(byte[] source) throws IOException;

        void save(T context);
    }
}
