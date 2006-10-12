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

package org.netbeans.modules.web.taglib;

import javax.swing.text.BadLocationException;
import org.openide.ErrorManager;

import org.openide.text.DataEditorSupport;
import org.openide.text.NbDocument;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileLock;
import org.openide.cookies.*;
import org.openide.nodes.Node.Cookie;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import org.netbeans.modules.xml.api.EncodingUtil;
/**
 * Text editor support that handles I/O encoding
 * @author  mkuchtiak
 */
public class TLDEditorSupport extends DataEditorSupport
implements OpenCookie, EditCookie, EditorCookie.Observable, PrintCookie, CloseCookie {
    
    /** SaveCookie for this support instance. The cookie is adding/removing 
     * data object's cookie set depending on if modification flag was set/unset. */
    private final SaveCookie saveCookie = new SaveCookie() {
        /** Implements <code>SaveCookie</code> interface. */
        public void save() throws java.io.IOException {
            saveDocument();
        }
    };
    /*
     * Save document using encoding declared in XML prolog if possible otherwise
     * at UTF-8 (in such case it updates the prolog).
     */
    public void saveDocument () throws java.io.IOException {
        final javax.swing.text.StyledDocument doc = getDocument();
        String defaultEncoding = "UTF-8"; // NOI18N
        // dependency on xml/core
        String enc = EncodingUtil.detectEncoding(doc);
        boolean changeEncodingToDefault = false;
        if (enc == null) enc = defaultEncoding;
        
        //test encoding
        if (!isSupportedEncoding(enc)){
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage (TLDEditorSupport.class, "MSG_BadEncodingDuringSave", //NOI18N
                new Object [] { getDataObject().getPrimaryFile().getNameExt(),
                                enc, 
                                defaultEncoding} ), 
                        NotifyDescriptor.YES_NO_OPTION,
                        NotifyDescriptor.WARNING_MESSAGE);
            nd.setValue(NotifyDescriptor.NO_OPTION);       
            DialogDisplayer.getDefault().notify(nd);
            if(nd.getValue() != NotifyDescriptor.YES_OPTION) return;
            changeEncodingToDefault = true;
        }
        
        if (!changeEncodingToDefault){
            // is it possible to save the document in the encoding?
            try {
                java.nio.charset.CharsetEncoder coder = java.nio.charset.Charset.forName(enc).newEncoder();
                if (!coder.canEncode(doc.getText(0, doc.getLength()))){
                    NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage (TLDEditorSupport.class, "MSG_BadCharConversion", //NOI18N
                    new Object [] { getDataObject().getPrimaryFile().getNameExt(),
                                    enc}),
                            NotifyDescriptor.YES_NO_OPTION,
                            NotifyDescriptor.WARNING_MESSAGE);
                    nd.setValue(NotifyDescriptor.NO_OPTION);
                    DialogDisplayer.getDefault().notify(nd);
                    if(nd.getValue() != NotifyDescriptor.YES_OPTION) return;
                }
            }
            catch (javax.swing.text.BadLocationException e){
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);            
            }
            super.saveDocument();
            //moved from Env.save()
            getDataObject().setModified (false);
        }
        else {
                // update prolog to new valid encoding                

            try {
                final int MAX_PROLOG = 1000;                
                int maxPrologLen = Math.min(MAX_PROLOG, doc.getLength());                
                final char prolog[] = doc.getText(0, maxPrologLen).toCharArray();
                int prologLen = 0;  // actual prolog length

                //parse prolog and get prolog end                
                if (prolog[0] == '<' && prolog[1] == '?' && prolog[2] == 'x') {

                    // look for delimitting ?>
                    for (int i = 3; i<maxPrologLen; i++) {
                        if (prolog[i] == '?' && prolog[i+1] == '>') {
                            prologLen = i + 1;
                            break;
                        }
                    }                                        
                }

                final int passPrologLen = prologLen;

                Runnable edit = new Runnable() {
                     public void run() {
                         try {

                            doc.remove(0, passPrologLen + 1); // +1 it removes exclusive
                            doc.insertString(0, "<?xml version='1.0' encoding='UTF-8' ?> \n<!-- was: " + new String(prolog, 0, passPrologLen + 1) + " -->", null); // NOI18N

                         } catch (BadLocationException e) {
                             if (System.getProperty("netbeans.debug.exceptions") != null) // NOI18N
                                 e.printStackTrace();
                         }
                     }
                };

                NbDocument.runAtomic(doc, edit);

                super.saveDocument();
                //moved from Env.save()
                getDataObject().setModified (false);
            }
            catch (javax.swing.text.BadLocationException e){
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);            
            }
        }
    }
    
    private boolean isSupportedEncoding(String encoding){
        boolean supported;
        try{
            supported = java.nio.charset.Charset.isSupported(encoding);
        }
        catch (java.nio.charset.IllegalCharsetNameException e){
            supported = false;
        }
        
        return supported;
    }
    
    /** Creates a new instance of TLDEditorSupport */
    public TLDEditorSupport(TLDDataObject dobj) {
        super(dobj,new XmlEnv(dobj));
        setMIMEType("text/xml");
    }
    
    /** 
     * Overrides superclass method. Adds adding of save cookie if the document has been marked modified.
     * @return true if the environment accepted being marked as modified
     *    or false if it has refused and the document should remain unmodified
     */
    protected boolean notifyModified () {
        if (!super.notifyModified()) 
            return false;

        addSaveCookie();

        return true;
    }

    /** Overrides superclass method. Adds removing of save cookie. */
    protected void notifyUnmodified () {
        super.notifyUnmodified();

        removeSaveCookie();
    }

    /** Helper method. Adds save cookie to the data object. */
    private void addSaveCookie() {
        TLDDataObject obj = (TLDDataObject)getDataObject();

        // Adds save cookie to the data object.
        if(obj.getCookie(SaveCookie.class) == null) {
            obj.getCookieSet0().add(saveCookie);
            obj.setModified(true);
        }
    }

    /** Helper method. Removes save cookie from the data object. */
    private void removeSaveCookie() {
        TLDDataObject obj = (TLDDataObject)getDataObject();
        
        // Remove save cookie from the data object.
        Cookie cookie = obj.getCookie(SaveCookie.class);

        if(cookie != null && cookie.equals(saveCookie)) {
            obj.getCookieSet0().remove(saveCookie);
            obj.setModified(false);
        }
    }
    

    /** A description of the binding between the editor support and the object.
     * Note this may be serialized as part of the window system and so
     * should be static, and use the transient modifier where needed.
     */
    private static class XmlEnv extends DataEditorSupport.Env {

        private static final long serialVersionUID = -800036748848958489L;
        
        //private static final long serialVersionUID = ...L;

        /** Create a new environment based on the data object.
         * @param obj the data object to edit
         */
        public XmlEnv (TLDDataObject obj) {
            super (obj);
        }

        /** Get the file to edit.
         * @return the primary file normally
         */
        protected FileObject getFile () {
            return getDataObject ().getPrimaryFile ();
        }

        /** Lock the file to edit.
         * Should be taken from the file entry if possible, helpful during
         * e.g. deletion of the file.
         * @return a lock on the primary file normally
         * @throws IOException if the lock could not be taken
         */
        protected FileLock takeLock () throws java.io.IOException {
            return ((TLDDataObject) getDataObject ()).getPrimaryEntry ().takeLock ();
        }

        /** Find the editor support this environment represents.
         * Note that we have to look it up, as keeping a direct
         * reference would not permit this environment to be serialized.
         * @return the editor support
         */
        public org.openide.windows.CloneableOpenSupport findCloneableOpenSupport () {
            return (TLDEditorSupport) getDataObject ().getCookie (TLDEditorSupport.class);
        }
    }
}
