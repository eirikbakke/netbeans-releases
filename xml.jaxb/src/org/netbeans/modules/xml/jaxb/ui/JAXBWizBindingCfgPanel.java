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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.jaxb.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardValidationException;

import org.openide.util.HelpCtx;
import org.openide.WizardDescriptor;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 * @author $Author$
 */
public class JAXBWizBindingCfgPanel implements WizardDescriptor.Panel,
                                        WizardDescriptor.ValidatingPanel, 
                                        WizardDescriptor.FinishablePanel{
    public static final int MODE_WIZARD  = 0;
    public static final int MODE_EDITING = 1; 
    public static final String SRC_LOC_TYPE_URL = "url" ; // No I18N
    public static final String SCHEMA_NAME = "schema.name"; // No I18N
    public static final String PROJECT_NAME = "project.name"; // No I18N
    public static final String XSD_FILE_LIST = "xsd.file.list"; // No I18N
    public static final String SOURCE_LOCATION_TYPE = "xsd.locatiom.type"; // No I18N
    public static final String PACKAGE_NAME = "xsd.package.name"; // No I18N
    public static final String SCHEMA_TYPE = "jaxb.schema.type"; // No I18N
    public static final String XJC_OPTIONS = "jaxb.xjc.options" ; // No I18N
    public static final String JAXB_BINDING_FILES = "jaxb.binding.files" ; // No I18N
    public static final String CATALOG_FILE = "jaxb.catalog.file" ; // No I18N
    private static final String WIZ_ERROR_MSG = "WizardPanel_errorMessage" ;    

    private WizardDescriptor wd = null;
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();  
    private JAXBBindingInfoPnl bindingInfoPnl = null;
    private Logger logger;
    
    public JAXBWizBindingCfgPanel() {
        logger = Logger.getLogger(this.getClass().getName());
        initUI();
    }
        
    private void initUI() {
        bindingInfoPnl = new JAXBBindingInfoPnl(this);
        bindingInfoPnl.setName(NbBundle.getMessage(this.getClass(), 
                                    "LBL_JAXBWizTitle")); // No I18N        
    }
        
    public void removeChangeListener(ChangeListener cl) {
        this.listeners.remove( cl );
    }
    
    public void addChangeListener(ChangeListener cl) {
        this.listeners.add( cl );
    }

    public void validate() throws WizardValidationException {
        logger.log(Level.FINEST, "validate() called.");
    }

    private boolean isEmpty(String str){
        boolean ret = true;
        if ((str != null) && (!"".equals(str.trim()))){
            ret = false;
        }
        return ret;
    }
    
    private void setError(String msg){
        this.wd.putProperty(WIZ_ERROR_MSG, msg);  
    }
    
    private boolean isValid(StringBuffer sb){
        logger.log(Level.FINEST, "isValidate() called.");        
        boolean valid = true;
        
        if (isEmpty(this.bindingInfoPnl.getSchemaName())){
            sb.append(NbBundle.getMessage(this.getClass(), 
                                            "MSG_EnterSchemaName")); // NoI18N
            valid = false;
            setError(sb.toString());
            return valid;
        } else {
            // XXX TODO make sure schema name is unique and does not contain white spaces
            // and OS file and path separaters.
        }
        
        if ( isEmpty(this.bindingInfoPnl.getSchemaFile())
                && isEmpty(this.bindingInfoPnl.getSchemaURL())){
            sb.append(NbBundle.getMessage(this.getClass(), 
                                         "MSG_EnterSchemaFileOrURL")); // NoI18N
            valid = false;
            setError(sb.toString());
            return valid;
        }
        
        if (!isEmpty(this.bindingInfoPnl.getPackageName())){
            // TODO make sure path name is valid.
        }
        
        Map<String, Boolean> options = this.bindingInfoPnl.getOptions();
        if (Boolean.TRUE.equals(options.get("-quiet")) && // No I18N
                Boolean.TRUE.equals(options.get("-verbose"))){ // No I18N
            valid = false;
            sb.append(NbBundle.getMessage(this.getClass(), 
                                  "MSG_CanNotSelectQuietAndVerbose")); // NoI18N
            setError(sb.toString());
            return valid;
        }
        
        if (valid){
            setError(null);           
            return valid;
        } else{
            setError(sb.toString());
            return valid;
        }
    }
    
    public boolean isValid() {
        StringBuffer sb = new StringBuffer();  
        return isValid(sb);
    }
    
    public boolean isFinishPanel() {
        return true;
    } 
    
    public HelpCtx getHelp() {
        return null;
    }
    
    public Component getComponent() {
        return bindingInfoPnl;
    }
        
    public void setMode(int mode) {
        switch ( mode ) {
            case MODE_EDITING:
                break;
            default:
            case MODE_WIZARD:
                break;
        }
    }
    
    public void fireChangeEvent() {
        ChangeEvent che = new ChangeEvent(this);
        try {
            for ( ChangeListener cl : listeners ) {
                cl.stateChanged( che );
            }
        } catch (Exception ex){
            this.logger.log(Level.WARNING, "fireChangeEvent()", ex);  
        }
    }
    
    public void storeSettings(Object settings) {
        WizardDescriptor wd = (WizardDescriptor) settings;              
        wd.putProperty("NewFileWizard_Title", null); // NOI18N        

        if (this.bindingInfoPnl.getSchemaName() != null) {
            wd.putProperty(SCHEMA_NAME, this.bindingInfoPnl.getSchemaName());    
        }

        if (this.bindingInfoPnl.getPackageName() != null) {
            wd.putProperty(PACKAGE_NAME, this.bindingInfoPnl.getPackageName());    
        }

        if (this.bindingInfoPnl.getSchemaType() != null) {
            wd.putProperty(SCHEMA_TYPE, this.bindingInfoPnl.getSchemaType());    
        }
        
        Map<String, Boolean> options =  this.bindingInfoPnl.getOptions();
        wd.putProperty(XJC_OPTIONS, options);
        
        List<String> xsdFileList = new ArrayList<String>();
        String schemaLoc = this.bindingInfoPnl.getSchemaFile();
        if (schemaLoc == null){
            xsdFileList.add(this.bindingInfoPnl.getSchemaURL());            
            wd.putProperty(SOURCE_LOCATION_TYPE, SRC_LOC_TYPE_URL);
        } else {
            xsdFileList.add(this.bindingInfoPnl.getSchemaFile());            
        }
        
        wd.putProperty(XSD_FILE_LIST, xsdFileList);            
    }
        
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor)settings;

        if (wd.getProperty(SCHEMA_NAME) != null) {
            this.bindingInfoPnl.setSchemaName((String)
                                                wd.getProperty(SCHEMA_NAME));    
        }

        if (wd.getProperty(PROJECT_NAME) != null) {
            this.bindingInfoPnl.setProjectName((String)
                                                wd.getProperty(PROJECT_NAME));    
        }
        
        if (wd.getProperty(PACKAGE_NAME) != null) {
            this.bindingInfoPnl.setPackageName((String) 
                                                wd.getProperty(PACKAGE_NAME));    
        }

        if (wd.getProperty(SCHEMA_TYPE) != null){
            this.bindingInfoPnl.setSchemaType((String)
                                                wd.getProperty(SCHEMA_TYPE));
        }
        
        Map<String, Boolean> options =  (Map<String, Boolean>)
                                                   wd.getProperty(XJC_OPTIONS);
        if (options != null){
            this.bindingInfoPnl.setOptions(options);
        }
        
        String origSrcLocType = (String) wd.getProperty(SOURCE_LOCATION_TYPE);  
        List<String> xsdFileList = (List<String>) wd.getProperty(XSD_FILE_LIST);
        
        if ((origSrcLocType != null) && 
                (SRC_LOC_TYPE_URL.equals(origSrcLocType))){
            if ((xsdFileList != null) && (xsdFileList.size() > 0)){
                Iterator<String> itr = xsdFileList.iterator();
                String file = itr.next();
                this.bindingInfoPnl.setSchemaURL(file);
            }                                
        } else {
            if ((xsdFileList != null) && (xsdFileList.size() > 0)){
                Iterator<String> itr = xsdFileList.iterator();
                String file = itr.next();
                this.bindingInfoPnl.setSchemaFile(file);
            }                    
        }
        
        // XXX hack, TemplateWizard in final setTemplateImpl() forces new 
        // wizard's title this name is used in NewFileWizard to modify the title
        if (wd instanceof TemplateWizard){
            wd.putProperty("NewFileWizard_Title",        // No I18N
                            NbBundle.getMessage(this.getClass(), 
                            "LBL_TemplateWizardTitle")); // No I18N
        }
    }    
}