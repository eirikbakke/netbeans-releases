/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.spi.xml.cookies;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.ProtectionDomain;
import java.security.CodeSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.swing.text.Document;

import org.openide.cookies.*;
import org.openide.util.*;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.ErrorManager;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import org.netbeans.api.xml.cookies.*;
import org.netbeans.api.xml.services.*;
import org.netbeans.api.xml.parsers.*;


/**
 * <code>CheckXMLCookie</code> and <code>ValidateXMLCookie</code> cookie 
 * implementation support simplifing cookie providers based on 
 * <code>InputSource</code>s representing XML documents and entities.
 *
 * @author      Petr Kuzel
 * @see         CheckXMLSupport
 * @see         ValidateXMLSupport
 */
class SharedXMLSupport {

    // it will viasualize our results
    private CookieObserver console;
    
    // associated input source
    private final InputSource inputSource;
    
    // one of above modes CheckXMLSupport modes
    private final int mode;
    
    // error locator or null
    private Locator locator;

    // fatal error counter
    private int fatalErrors;
    
    // error counter
    private int errors;
    
    /**
     * Xerces parser tries to search for every namespace declaration
     * related Schema. It causes trouble it DTD like XHTML defines
     * default xmlns attribute. It is then inherited by all descendants
     * and grammar is loaded again and again. Entity resolver set this
     * flag once it spots (null, null) resolution request that is typical
     * for bogus Schema location resolution requests.
     */
    private boolean bogusSchemaRequest;
    
    // If true then the first bogust schema grammar request is reported
    // all subsequent ones are supressed.
    private boolean reportBogusSchemaRequest = 
        Boolean.getBoolean("netbeans.xml.reportBogusSchemaLocation");           // NOI18N
    
    /** 
     * Create new CheckXMLSupport for given InputSource in DOCUMENT_MODE.
     * @param inputSource Supported InputSource.
     */    
    public SharedXMLSupport(InputSource inputSource) {
        this(inputSource, CheckXMLSupport.DOCUMENT_MODE);
    }    
    
    /** 
     * Create new CheckXMLSupport for given data object
     * @param inputSource Supported InputSource.
     * @param mode one of <code>*_MODE</code> constants
     */
    public SharedXMLSupport(InputSource inputSource, int mode) {

        if (inputSource == null) throw new NullPointerException();
        if (mode < CheckXMLSupport.CHECK_ENTITY_MODE || mode > CheckXMLSupport.DOCUMENT_MODE) {
            throw new IllegalArgumentException();
        }
        
        this.inputSource = inputSource;
        this.mode = mode;
    }

    // inherit JavaDoc
    boolean checkXML(CookieObserver l) {
        try {
            console = l;

            parse(false);

            return fatalErrors == 0;
        } finally {
            console = null;
            locator = null;            
        }
    }
    
    // inherit JavaDoc
    boolean validateXML(CookieObserver l) {
        try {
            console = l;

            if (mode != CheckXMLSupport.DOCUMENT_MODE) {
                sendMessage(Util.THIS.getString("MSG_not_a_doc"));
                return false;
            } else {        
                parse(true);
                return errors == 0 && fatalErrors == 0;
            }
        } finally {
            console = null;
            locator = null;
        }
    }
                

    
    /**
     * Perform parsing in current thread.
     */
    private void parse (boolean validate) {
        
        fatalErrors = 0;
        errors = 0;
                
        String checkedFile = inputSource.getSystemId();
        sendMessage(Util.THIS.getString("MSG_checking", checkedFile));

        Handler handler = new Handler();
 

        InputSource input = null;
        
        try {
            // set up parser
            XMLReader parser = createParser(validate);
            if (parser == null) {
                fatalErrors++;
                console.receive(new CookieMessage(
                    Util.THIS.getString("MSG_cannot_create_parser"),
                    CookieMessage.FATAL_ERROR_LEVEL
                ));
                return;
            }
            
            if (validate) {
                // get all naemspaces for the parser
                input=ShareableInputSource.create(createInputSource());
                String[] schemaLocations=getSchemaLocations(input);
                ((ShareableInputSource)input).reset();
                if (schemaLocations!=null && schemaLocations.length>0) {
                    boolean first=true;
                    StringBuffer sb = new StringBuffer();
                    for (int i=0;i<schemaLocations.length;i++) {
                        sb.append(first?schemaLocations[i]:" "+schemaLocations[i]);
                        first=false;
                    }
                    parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", sb.toString()); //NOI18N
                }
            } else input = createInputSource();
            
            parser.setErrorHandler(handler);           
            parser.setContentHandler(handler);
            
            if ( Util.THIS.isLoggable()) {
                Util.THIS.debug(checkedFile + ":" + parserDescription(parser));
            }

            // parse  
            if (mode == CheckXMLSupport.CHECK_ENTITY_MODE) {
                new SAXEntityParser(parser, true).parse(input);
            } else if (mode == CheckXMLSupport.CHECK_PARAMETER_ENTITY_MODE) {
                new SAXEntityParser(parser, false).parse(input);
            } else {
                parser.parse (input);
            }

        } catch (SAXException ex) {

            // same as one catched by ErrorHandler
            // because we do not have content handler

        } catch (FileStateInvalidException ex) {

            // bad luck report as fatal error
            handler.fatalError(new SAXParseException(ex.getLocalizedMessage(), locator, ex));

        } catch (IOException ex) {

            // bad luck probably because cannot resolve entity
            // report as error at -1,-1 if we do not have Locator
            handler.fatalError(new SAXParseException (ex.getLocalizedMessage(), locator, ex));

        } catch (RuntimeException ex) {

            handler.runtimeError(ex);
        } finally {
	    if (input instanceof ShareableInputSource)
            try {
                ((ShareableInputSource)input).closeAll();
            } catch (IOException ex) {}
        }
        
    }

    /**
     * Parametrizes default parser creatin process. Default implementation
     * takes user's catalog entity resolver.
     * @return EntityResolver entity resolver or <code>null</code>
     */
    protected EntityResolver createEntityResolver() {
        UserCatalog catalog = UserCatalog.getDefault();
        return catalog == null ? null : catalog.getEntityResolver();
    }
    
    /**
     * Create InputSource to be checked.
     * @throws IOException if I/O error occurs.
     * @return InputSource never <code>null</code>
     */
    protected InputSource createInputSource() throws IOException {        
        return inputSource;
    }

    /** 
     * Create and preconfigure new parser. Default implementation uses JAXP.
     * @param validate true if validation module is required
     * @return SAX reader that is used for command performing or <code>null</code>
     * @see #createEntityResolver
     */
    protected XMLReader createParser(boolean validate) {
       
        XMLReader ret = null;
        final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";         // NOI18N
        final String XERCES_PROPERTY_PREFIX = "http://apache.org/xml/properties/";      // NOI18N
        
       // JAXP plugin parser (bastarded by core factories!)
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(validate);

        //??? It is Xerces specifics, but no general API for XML Schema based validation exists
        if (validate) {
            try {
                factory.setFeature(XERCES_FEATURE_PREFIX + "validation/schema", validate); // NOI18N
            } catch (Exception ex) {
                sendMessage(Util.THIS.getString("MSG_parser_no_schema"));
            }                
        }
	
        try {
            SAXParser parser = factory.newSAXParser();
            ret = parser.getXMLReader();
        } catch (Exception ex) {
            sendMessage(Util.THIS.getString("MSG_parser_err_1"));
            return null;
        }


        if (ret != null) {
            EntityResolver res = createEntityResolver();
            if (res != null) ret.setEntityResolver(new VerboseEntityResolver(res));
        }
        
        return ret;
        
    }

    /**
     * It may be helpfull for tracing down some oddities.
     */
    private String parserDescription(XMLReader parser) {

        // report which parser implementation is used
        
        Class klass = parser.getClass();
        try {
            ProtectionDomain domain = klass.getProtectionDomain();
            CodeSource source = domain.getCodeSource();
            
            if (source == null && (klass.getClassLoader() == null || klass.getClassLoader().equals(Object.class.getClassLoader()))) {
                return Util.THIS.getString("MSG_platform_parser");
            } else if (source == null) {
                return Util.THIS.getString("MSG_unknown_parser", klass.getName());
            } else {
                URL location = source.getLocation();
                return Util.THIS.getString("MSG_parser_plug", location.toExternalForm());
            }
            
        } catch (SecurityException ex) {
            return Util.THIS.getString("MSG_unknown_parser", klass.getName());
        }
        
    }
    
    // Content & ErrorHandler implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private class Handler extends DefaultHandler {
    
        public void warning (SAXParseException ex) {
            
            // heuristics to detect bogus schema loading requests
            
            String msg = ex.getLocalizedMessage();
            if (bogusSchemaRequest) {
                bogusSchemaRequest = false;
                if (msg != null && msg.indexOf("schema_reference.4") != -1) {   // NOI18N
                    if (reportBogusSchemaRequest) {
                        reportBogusSchemaRequest = false;
                    } else {                    
                        return;                    
                    }
                }
            }
            
            CookieMessage message = new CookieMessage(
                msg, 
                CookieMessage.WARNING_LEVEL,
                new DefaultXMLProcessorDetail(ex)
            );
            if (console != null) console.receive(message);
        }

        /**
         * Report maximally getMaxErrorCount() errors then stop the parser.
         */
        public void error (SAXParseException ex) throws SAXException {
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug ("Just diagnostic exception", ex); // NOI18N
            if (errors++ == getMaxErrorCount()) {
                String msg = Util.THIS.getString("MSG_too_many_errs");
                sendMessage(msg);
                throw ex; // stop the parser                
            } else {
                CookieMessage message = new CookieMessage(
                    ex.getLocalizedMessage(), 
                    CookieMessage.ERROR_LEVEL,
                    new DefaultXMLProcessorDetail(ex)
                );
                if (console != null) console.receive(message);
            }
        }

        /**
         * Log runtime exception cause
         */
        private void runtimeError (RuntimeException ex) {
            Util.THIS.debug("Parser runtime exception", ex );

            // probably an internal parser error
            String msg = Util.THIS.getString("EX_parser_ierr", ex.getMessage());
            fatalError(new SAXParseException (msg, SharedXMLSupport.this.locator, ex));
        }
        
        public void fatalError (SAXParseException ex) {        
            if ( Util.THIS.isLoggable() ) /* then */ Util.THIS.debug("Just diagnostic exception", ex); // NOI18N
            fatalErrors++;
            CookieMessage message = new CookieMessage(
                ex.getLocalizedMessage(), 
                CookieMessage.FATAL_ERROR_LEVEL,
                new DefaultXMLProcessorDetail(ex)
            );
            if (console != null) console.receive(message);
        }
        
        public void setDocumentLocator(Locator locator) {
            SharedXMLSupport.this.locator = locator;
        }

        private int getMaxErrorCount() {
            return 20;  //??? load from option
        }    
        
    }


    /**
     * EntityResolver that reports unresolved entities.
     */
    private class VerboseEntityResolver implements EntityResolver {
        
        private final EntityResolver peer;
        
        public VerboseEntityResolver(EntityResolver res) {
            if (res == null) throw new NullPointerException();
            peer = res;
        }
        
        public InputSource resolveEntity(String pid, String sid) throws SAXException, IOException {
                                    
            InputSource result = peer.resolveEntity(pid, sid);
            
            // null result may be suspicious, may be no Schema location found etc.
            
            if (result == null) {
                bogusSchemaRequest = pid == null && sid == null;
                if (bogusSchemaRequest) return null;
                
                String warning;
                String pidLabel = pid != null ? pid : Util.THIS.getString("MSG_no_pid");
                try {
                    String file = new URL(sid).getFile();
                    if (file != null) {
                        warning = Util.THIS.getString("MSG_resolver_1", pidLabel, sid);
                    } else {  // probably NS id
                        warning = Util.THIS.getString("MSG_resolver_2", pidLabel, sid);
                    }
                } catch (MalformedURLException ex) {
                    warning = Util.THIS.getString("MSG_resolver_3", pidLabel, sid);
                }
                sendMessage(warning);
            }
            return result;
        }
        
    }
    
    private void sendMessage(String message) {
        if (console != null) {
            console.receive(new CookieMessage(message));
        }
    }

    private String[] getSchemaLocations(InputSource is) {
        EntityResolver res = createEntityResolver();
        if (res==null) return null;
        NsHandler nsHandler = getNamespaces(is); 
        String[] namespaces = nsHandler.getNamespaces();
        List loc = new ArrayList();
        for (int i=0;i<namespaces.length;i++) {
            String ns = namespaces[i];
            if (nsHandler.mapping.containsKey(ns)) {
                loc.add(ns + " " + nsHandler.mapping.get(ns)); //NOI18N
            } else {
                try {
                    javax.xml.transform.Source src = ((javax.xml.transform.URIResolver)res).resolve(ns, null);
                    if (src!=null) loc.add(ns+" "+src.getSystemId()); //NOI18N
                } catch (Exception ex) {}
            }
        }
        String[] schemaLocations = new String[loc.size()];
        loc.toArray(schemaLocations);
        return schemaLocations;
    }
    
    private NsHandler getNamespaces(InputSource is) {
        NsHandler handler = new NsHandler();
        try {
            XMLReader xmlReader = org.openide.xml.XMLUtil.createXMLReader(false, true);
            xmlReader.setContentHandler(handler);

            // XXX dumb resolver always returning empty stream would be better but
            // parsing could fail on resolving general entities defined in DTD.
            // Check XML spec if non-validation parser must resolve general entities
            // Ccc: I think so, there is Xerces property to relax it but we get here Crimson
            UserCatalog userCatalog = UserCatalog.getDefault();
            if (userCatalog != null) {
                EntityResolver resolver = userCatalog.getEntityResolver();
                if (resolver != null) {
                    xmlReader.setEntityResolver(resolver);
                }
            }
            xmlReader.parse(is);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        } catch (SAXException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        return handler;
    }
    
    private static class NsHandler extends org.xml.sax.helpers.DefaultHandler {
        Set namespaces;
        private Map mapping;

        NsHandler() {
            namespaces=new HashSet();
            mapping = new HashMap();
        }
        
        public void startElement(String uri, String localName, String rawName, Attributes atts) throws SAXException {
            if (atts.getLength()>0) { //NOI18N
                // parse XMLSchema location attribute
                String locations = atts.getValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");  // NOI18N
                if (locations != null) {
                    StringTokenizer tokenizer = new StringTokenizer(locations);
                    if ((tokenizer.countTokens() % 2) == 0) {
                        while (tokenizer.hasMoreElements()) {
                            String nsURI = tokenizer.nextToken();
                            String nsLocation = tokenizer.nextToken();
                            mapping.put(nsURI, nsLocation);
                        }
                    }
                }
            }
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            if ("http://www.w3.org/2001/XMLSchema-instance".equals(uri)) {  // NOIi8N
                return; // it's build in into parser
            }
            namespaces.add(uri);
        }

        String[] getNamespaces() {
            String[] ns = new String[namespaces.size()];
            namespaces.toArray(ns);
            return ns;
        }

    }

}
