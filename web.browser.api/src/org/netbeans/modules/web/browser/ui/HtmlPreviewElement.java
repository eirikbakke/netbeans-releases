/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.browser.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.LayerUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.netbeans.modules.web.browser.api.WebBrowser;
import org.netbeans.modules.web.browser.api.WebBrowsers;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.UndoRedo;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * A quick prototype of a Preview tab in HTML document Multiview window.
 *
 * Note: It is disabled by default. Use system property nb.html.preview.enabled=true to turn it on.
 *
 * @author S. Aubrecht
 */
public class HtmlPreviewElement implements MultiViewElement {

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLayer<JPanel> layer;
    private final DeveloperToolbar toolbar = DeveloperToolbar.create();
    private HtmlBrowser.Impl browser = null;

    private final EditorCookie.Observable editorCookie;
    private final DocumentListener documentListener;
    private final PropertyChangeListener editorListener;

    private final Lookup lookup;

    private Method methodSetBrowserContent;

    private static final Logger LOG = Logger.getLogger( HtmlPreviewElement.class.getName() );

    public HtmlPreviewElement( Lookup lkp ) {
        editorCookie = lkp.lookup( EditorCookie.Observable.class );
        layer = new JLayer<JPanel>( panel, new NoLeftClickLayerUI() );
        layer.setLayerEventMask( MouseEvent.MOUSE_EVENT_MASK );
        this.lookup = lkp;

        documentListener = new DocumentListener() {

            @Override
            public void insertUpdate( DocumentEvent e ) {
                reloadFromDocument();
            }

            @Override
            public void removeUpdate( DocumentEvent e ) {
                reloadFromDocument();
            }

            @Override
            public void changedUpdate( DocumentEvent e ) {
                reloadFromDocument();
            }
        };

        editorListener = new PropertyChangeListener() {

            @Override
            public void propertyChange( PropertyChangeEvent evt ) {
                if( EditorCookie.Observable.PROP_OPENED_PANES.equals( evt.getPropertyName() ) ) {
                    dettach();
                    attach();
                } else if( EditorCookie.Observable.PROP_MODIFIED.equals( evt.getPropertyName() ) ) {
                    if( !editorCookie.isModified() ) {
                        reloadFromFile();
                    }
                }
            }
        };
    }

    static MultiViewDescription createMultiViewDescription(Map map) {
        if( Boolean.getBoolean( "nb.html.preview.enabled" ) ) { //NOI18N
            try {
                Method m = MultiViewFactory.class.getDeclaredMethod( "createMultiViewDescription", Map.class ); //NOI18N
                m.setAccessible( true );
                return ( MultiViewDescription ) m.invoke( null, map );
            } catch( Exception e ) {
                LOG.log( Level.INFO, "Cannot create multiview description.", e );
            }
        }
        return null;
    }

    @Override
    public JComponent getVisualRepresentation() {
        return panel;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return ( JComponent ) toolbar.getComponent();
    }

    @Override
    public void setMultiViewCallback( MultiViewElementCallback callback ) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public Action[] getActions() {
        return null;
    }

    @Override
    public Lookup getLookup() {
        return Lookup.EMPTY;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
        panel.removeAll();
        browser = null;
        methodSetBrowserContent = null;
        dettach();
    }

    @Override
    public void componentShowing() {
        initBrowser();
        attach();
    }

    @Override
    public void componentHidden() {
        dettach();
    }

    @Override
    public void componentActivated() {
        if( null != browser ) {
            browser.reloadDocument();
        }
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    private void initBrowser() {
        if( null != browser )
            return;

        WebBrowser web = WebBrowsers.getInstance().getPreferred();
        if( null != web && !web.isEmbedded() ) {
            for( WebBrowser wb : WebBrowsers.getInstance().getAll(false, false, false) ) {
                if( wb.isEmbedded() ) {
                    web = wb;
                    break;
                }
            }
        }
        panel.removeAll();
        if( null == web || !web.isEmbedded() ) {
            panel.add( new JLabel("No embedded browser available"), BorderLayout.CENTER );
        } else {
            browser = web.getHtmlBrowserFactory().createHtmlBrowserImpl();
            toolbar.intialize( browser.getLookup() );
            panel.add( browser.getComponent(), BorderLayout.CENTER );
            methodSetBrowserContent = null;
            try {
                methodSetBrowserContent = browser.getClass().getDeclaredMethod( "setContent", String.class); //NOI18N
            } catch( Exception e ) {
                LOG.log( Level.INFO, null, e );
            }
        }
    }

    private void attach() {
        if( !SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    attach();
                }
            });
            return;
        }
        if( null != editorCookie ) {
            editorCookie.addPropertyChangeListener( editorListener );
            JEditorPane[] panes = editorCookie.getOpenedPanes();
            if( null != panes && panes.length > 0 ) {
                panes[0].getDocument();
            }
            Document doc = editorCookie.getDocument();
            if( null != doc )
                doc.addDocumentListener( documentListener );
        }
        reloadFromDocument();
    }

    private void reloadFromDocument() {
        if( null == editorCookie )
            return;
        Document doc = editorCookie.getDocument();
        refresh( doc );
    }

    private void reloadFromFile() {
        if( null == browser )
            return;

        final FileObject fileObject = lookup.lookup(FileObject.class);
        if( fileObject != null ) {
            URL url = fileObject.toURL();
            browser.setURL( url );
        }
    }

    private void dettach() {
        if( !SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    dettach();
                }
            });
            return;
        }
        if( null != editorCookie ) {
            editorCookie.removePropertyChangeListener( editorListener );
            Document doc = editorCookie.getDocument();
            if( null != doc )
                doc.removeDocumentListener( documentListener );
        }
    }

    private void refresh( Document doc ) {
        if( null == doc )
            return;
        try {
            String text = doc.getText( 0, doc.getLength() );
            if( null != browser && null != methodSetBrowserContent ) {
                try {
                    methodSetBrowserContent.invoke( browser, text );
                } catch( Exception ex ) {
                    LOG.log( Level.INFO, null, ex );
                }
            }
        } catch( BadLocationException ex ) {
            Exceptions.printStackTrace( ex );
        }
    }

    class NoLeftClickLayerUI extends LayerUI<JPanel> {

        @Override
        protected void processMouseEvent( MouseEvent e, JLayer<? extends JPanel> panel ) {
            super.processMouseEvent( e, panel );
            if( e.getButton() == MouseEvent.BUTTON1 )
                e.consume();
        }
    }
}
