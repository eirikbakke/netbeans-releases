/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.web.project;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.common.EjbLocalRef;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeAppProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.deployment.plugins.api.*;
import org.netbeans.api.debugger.*;
import org.netbeans.api.debugger.jpda.*;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.modules.j2ee.deployment.devmodules.api.*;
import org.netbeans.modules.web.api.webmodule.RequestParametersQuery;
import org.netbeans.modules.web.project.ui.customizer.WebProjectProperties;
import org.netbeans.modules.web.project.ui.ServletUriPanel;
import org.netbeans.modules.web.project.ui.SetExecutionUriAction;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.modules.web.project.parser.ParserWebModule;
import org.netbeans.modules.web.project.parser.JspNameUtil;

import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.web.api.webmodule.WebModule;

import org.netbeans.modules.web.api.webmodule.WebProjectConstants;

import org.netbeans.jmi.javamodel.*;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import org.netbeans.modules.web.jsps.parserapi.JspParserAPI;
import org.netbeans.modules.web.jsps.parserapi.JspParserFactory;
import org.netbeans.modules.websvc.api.webservices.WebServicesClientSupport;
import org.netbeans.modules.websvc.api.webservices.WebServicesSupport;
import org.netbeans.modules.websvc.api.webservices.WsCompileEditorSupport;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

import org.netbeans.spi.project.support.ant.PropertyUtils;

/** Action provider of the Web project. This is the place where to do
 * strange things to Web actions. E.g. compile-single.
 */
class WebActionProvider implements ActionProvider {
    
    // Definition of commands
    
    private static final String COMMAND_COMPILE = "compile"; //NOI18N
    private static final String COMMAND_VERIFY = "verify"; //NOI18N
        
    // Commands available from Web project
    private static final String[] supportedActions = {
        COMMAND_BUILD, 
        COMMAND_CLEAN, 
        COMMAND_REBUILD, 
        COMMAND_COMPILE_SINGLE, 
        COMMAND_RUN, 
        COMMAND_RUN_SINGLE, 
        COMMAND_DEBUG, 
        WebProjectConstants.COMMAND_REDEPLOY,
        COMMAND_DEBUG_SINGLE, 
        JavaProjectConstants.COMMAND_JAVADOC, 
        COMMAND_TEST, 
        COMMAND_TEST_SINGLE, 
        COMMAND_DEBUG_TEST_SINGLE, 
        JavaProjectConstants.COMMAND_DEBUG_FIX,
        COMMAND_COMPILE,
        COMMAND_VERIFY,
    };
    
    // Project
    WebProject project;
    
    // Ant project helper of the project
    private UpdateHelper updateHelper;

    // Ant project helper of the project
//    private AntProjectHelper antProjectHelper;
//    private ReferenceHelper refHelper;
        
    /** Map from commands to ant targets */
    Map/*<String,String[]>*/ commands;
    
    public WebActionProvider(WebProject project, UpdateHelper updateHelper) {
        
        commands = new HashMap();
            commands.put(COMMAND_BUILD, new String[] {"dist"}); // NOI18N
            commands.put(COMMAND_CLEAN, new String[] {"clean"}); // NOI18N
            commands.put(COMMAND_REBUILD, new String[] {"clean", "dist"}); // NOI18N
            // the target name is compile-single, except for JSPs, where it is compile-single-jsp
            commands.put(COMMAND_COMPILE_SINGLE, new String[] {"compile-single"}); // NOI18N
            commands.put(COMMAND_RUN, new String[] {"run"}); // NOI18N
            // the target name is run, except for Java files with main method, where it is run-main
            commands.put(COMMAND_RUN_SINGLE, new String[] {"run"}); // NOI18N
            commands.put(WebProjectConstants.COMMAND_REDEPLOY, new String[] {"run-deploy"}); // NOI18N
            commands.put(COMMAND_DEBUG, new String[] {"debug"}); // NOI18N
            // the target name is debug, except for Java files with main method, where it is debug-single-main
            commands.put(COMMAND_DEBUG_SINGLE, new String[] {"debug"}); // NOI18N
            commands.put(JavaProjectConstants.COMMAND_JAVADOC, new String[] {"javadoc"}); // NOI18N
            commands.put(COMMAND_TEST, new String[] {"test"}); // NOI18N
            commands.put(COMMAND_TEST_SINGLE, new String[] {"test-single"}); // NOI18N
            commands.put(COMMAND_DEBUG_TEST_SINGLE, new String[] {"debug-test"}); // NOI18N
            commands.put(JavaProjectConstants.COMMAND_DEBUG_FIX, new String[] {"debug-fix"}); // NOI18N
            commands.put(COMMAND_COMPILE, new String[] {"compile"}); // NOI18N
            commands.put(COMMAND_VERIFY, new String[] {"verify"}); // NOI18N
        
        this.updateHelper = updateHelper;
        this.project = project;
    }
    
    private FileObject findBuildXml() {
        return project.getProjectDirectory().getFileObject(project.getBuildXmlName());
    }
    
    public String[] getSupportedActions() {
        return supportedActions;
    }
    
    public void invokeAction( String command, Lookup context ) throws IllegalArgumentException {
        Properties p;
        String[] targetNames = (String[])commands.get(command);
        
        // RUN-SINGLE
        if (command.equals(COMMAND_RUN_SINGLE)) {
            p = new Properties();
            FileObject[] files = findTestSources(context, false);
            if (files != null) {
                targetNames = setupTestSingle(p, files);
            } else {
                if (!isSelectedServer ()) {
                    return;
                }
                if (isDebugged()) {
                    NotifyDescriptor nd;
                    String text;
                    if (command.equals (COMMAND_RUN)) {
                        ProjectInformation pi = (ProjectInformation)project.getLookup().lookup(ProjectInformation.class);
                        text = pi.getDisplayName();
                    } else { //COMMAND_RUN_SINGLE
                        files = ActionUtils.findSelectedFiles(context, null, null, false);
                        text = (files == null) ? "?" : files[0].getNameExt(); // NOI18N
                    }
                    nd = new NotifyDescriptor.Confirmation(
                                NbBundle.getMessage(WebActionProvider.class, "MSG_SessionRunning", text),
                                NotifyDescriptor.OK_CANCEL_OPTION);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    if (o.equals(NotifyDescriptor.OK_OPTION)) {            
                        DebuggerManager.getDebuggerManager().getCurrentSession().kill();
                    } else {
                        return;
                    }
                }
                // 51462 - if there's an ejb reference, but no j2ee app, run/deploy will not work
                if (isEjbRefAndNoJ2eeApp(project)) {
                    NotifyDescriptor nd;                
                    nd = new NotifyDescriptor.Message(NbBundle.getMessage(WebActionProvider.class, "MSG_EjbRef"), NotifyDescriptor.INFORMATION_MESSAGE);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                if (command.equals (WebProjectConstants.COMMAND_REDEPLOY)) {
                    p.setProperty("forceRedeploy", "true"); //NOI18N
                } else {
                    p.setProperty("forceRedeploy", "false"); //NOI18N
                }
                // run a JSP
                files = findJsps( context );
                if (files!=null && files.length>0) {
                    // possibly compile the JSP, if we are not compiling all of them
                    String raw = updateHelper.getAntProjectHelper().getStandardPropertyEvaluator ().getProperty (WebProjectProperties.COMPILE_JSPS);
                    boolean compile = decodeBoolean(raw);
                    if (!compile) {
                        setAllPropertiesForSingleJSPCompilation(p, files);
                    }

                    String requestParams = RequestParametersQuery.getFileAndParameters(files [0]);
                    if (requestParams != null) {
                        p.setProperty("client.urlPart", requestParams); //NOI18N
                    } else {
                        return;
                    }
                } else {
                    // run HTML file
                    FileObject[] htmlFiles = findHtml(context);
                    if ((htmlFiles != null) && (htmlFiles.length>0)) {
                        String url = "/" + FileUtil.getRelativePath(WebModule.getWebModule (htmlFiles[0]).getDocumentBase (), htmlFiles[0]); // NOI18N
                        if (url != null) {
                            url = org.openide.util.Utilities.replaceString(url, " ", "%20");
                            p.setProperty("client.urlPart", url); //NOI18N
                        } else {
                            return;
                        }
                    } else {
                        // run Java
                        FileObject[] javaFiles = findJavaSources(context);
                        if ((javaFiles != null) && (javaFiles.length>0)) {
                            FileObject javaFile = javaFiles[0];
                            
                            if (hasMainMethod(javaFile)) {
                                // run Java with Main method
                                String clazz = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),javaFile), javaFile);
                                p = new Properties();
                                p.setProperty("javac.includes", clazz); // NOI18N
                                // Convert foo/FooTest.java -> foo.FooTest
                                if (clazz.endsWith(".java")) { // NOI18N
                                    clazz = clazz.substring(0, clazz.length() - 5);
                                }
                                clazz = clazz.replace('/','.');
                                
                                p.setProperty("run.class", clazz); // NOI18N
                                targetNames = new String [] {"run-main"};
                            }
                            else {
                                // run servlet
                                // PENDING - what about servlets with main method? servlet should take precedence
                                String executionUri = (String)javaFile.getAttribute(SetExecutionUriAction.ATTR_EXECUTION_URI);
                                if (executionUri!=null) {
                                    p.setProperty("client.urlPart", executionUri); //NOI18N
                                } else {
                                    WebModule webModule = WebModule.getWebModule(javaFile);
                                    String[] urlPatterns = SetExecutionUriAction.getServletMappings(webModule,javaFile);
                                    if (urlPatterns!=null && urlPatterns.length>0) {
                                        ServletUriPanel uriPanel = new ServletUriPanel(urlPatterns,null,true);
                                        DialogDescriptor desc = new DialogDescriptor(uriPanel,
                                            NbBundle.getMessage (SetExecutionUriAction.class, "TTL_setServletExecutionUri"));
                                        Object res = DialogDisplayer.getDefault().notify(desc);
                                        if (res.equals(NotifyDescriptor.YES_OPTION)) {
                                            p.setProperty("client.urlPart", uriPanel.getServletUri()); //NOI18N
                                            try {
                                                javaFile.setAttribute(SetExecutionUriAction.ATTR_EXECUTION_URI,uriPanel.getServletUri());
                                            } catch (IOException ex){}
                                        } else return;
                                    } else {
                                        String mes = java.text.MessageFormat.format (
                                                NbBundle.getMessage (SetExecutionUriAction.class, "TXT_missingServletMappings"),
                                                new Object [] {javaFile.getName()});
                                        NotifyDescriptor desc = new NotifyDescriptor.Message(mes,NotifyDescriptor.Message.ERROR_MESSAGE);
                                        DialogDisplayer.getDefault().notify(desc);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        // RUN, REDEPLOY
        } else if (command.equals(COMMAND_RUN) || command.equals (WebProjectConstants.COMMAND_REDEPLOY)) {
            p = new Properties();
            FileObject[] files = findTestSources(context, false);
            if (files != null) {
                targetNames = setupTestSingle(p, files);
            } else {
                if (!isSelectedServer ()) {
                    return;
                }
                if (isDebugged()) {
                    NotifyDescriptor nd;
                    String text;
                    if (command.equals (COMMAND_RUN)) {
                        ProjectInformation pi = (ProjectInformation)project.getLookup().lookup(ProjectInformation.class);
                        text = pi.getDisplayName();
                    } else { //COMMAND_RUN_SINGLE
                        files = ActionUtils.findSelectedFiles(context, null, null, false);
                        text = (files == null) ? "?" : files[0].getNameExt(); // NOI18N
                    }
                    nd = new NotifyDescriptor.Confirmation(
                                NbBundle.getMessage(WebActionProvider.class, "MSG_SessionRunning", text),
                                NotifyDescriptor.OK_CANCEL_OPTION);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    if (o.equals(NotifyDescriptor.OK_OPTION)) {            
                        DebuggerManager.getDebuggerManager().getCurrentSession().kill();
                    } else {
                        return;
                    }
                }
                // 51462 - if there's an ejb reference, but no j2ee app, run/deploy will not work
                if (isEjbRefAndNoJ2eeApp(project)) {
                    NotifyDescriptor nd;                
                    nd = new NotifyDescriptor.Message(NbBundle.getMessage(WebActionProvider.class, "MSG_EjbRef"), NotifyDescriptor.INFORMATION_MESSAGE);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                if (command.equals (WebProjectConstants.COMMAND_REDEPLOY)) {
                    p.setProperty("forceRedeploy", "true"); //NOI18N
                } else {
                    p.setProperty("forceRedeploy", "false"); //NOI18N
                }
            }

        // DEBUG-SINGLE
        } else if (command.equals(COMMAND_DEBUG_SINGLE)) {
            p = new Properties();
            FileObject[] files = findTestSources(context, false);
            if (files != null) {
                targetNames = setupDebugTestSingle(p, files);
            } else {
                if (!isSelectedServer ()) {
                    return;
                }
                if (isDebugged()) {
                    NotifyDescriptor nd;
                    nd = new NotifyDescriptor.Confirmation(
                                NbBundle.getMessage(WebActionProvider.class, "MSG_FinishSession"),
                                NotifyDescriptor.OK_CANCEL_OPTION);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    if (o.equals(NotifyDescriptor.OK_OPTION)) {            
                        DebuggerManager.getDebuggerManager().getCurrentSession().kill();
                    } else {
                        return;
                    }
                }
                // 51462 - if there's an ejb reference, but no j2ee app, debug will not work
                if (isEjbRefAndNoJ2eeApp(project)) {
                    NotifyDescriptor nd;                
                    nd = new NotifyDescriptor.Message(NbBundle.getMessage(WebActionProvider.class, "MSG_EjbRef"), NotifyDescriptor.INFORMATION_MESSAGE);
                    Object o = DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                p = new Properties();

                files = findJsps( context );
                if ((files != null) && (files.length>0)) {
                    // debug jsp
                    // possibly compile the JSP, if we are not compiling all of them
                    String raw = updateHelper.getAntProjectHelper().getStandardPropertyEvaluator ().getProperty (WebProjectProperties.COMPILE_JSPS);
                    boolean compile = decodeBoolean(raw);
                    if (!compile) {
                        setAllPropertiesForSingleJSPCompilation(p, files);
                    }

                    String requestParams = RequestParametersQuery.getFileAndParameters(files [0]);
                    if (requestParams != null) {
                        p.setProperty("client.urlPart", requestParams); //NOI18N
                    } else {
                        return;
                    }
                } else {
                    // debug HTML file
                    FileObject[] htmlFiles = findHtml(context);
                    if ((htmlFiles != null) && (htmlFiles.length>0)) {
                        String url = "/" + FileUtil.getRelativePath(WebModule.getWebModule (htmlFiles[0]).getDocumentBase (), htmlFiles[0]); // NOI18N
                        if (url != null) {
                            url = org.openide.util.Utilities.replaceString(url, " ", "%20");
                            p.setProperty("client.urlPart", url); //NOI18N
                        } else {
                            return;
                        }
                    } else {
                        // debug Java
                        // debug servlet
                        FileObject[] javaFiles = findJavaSources(context);
                        if ((javaFiles != null) && (javaFiles.length>0)) {
                            FileObject javaFile = javaFiles[0];

                            if (hasMainMethod(javaFile)) {
                                // debug Java with Main method
                                String clazz = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),javaFile), javaFile);
                                p = new Properties();
                                p.setProperty("javac.includes", clazz); // NOI18N
                                // Convert foo/FooTest.java -> foo.FooTest
                                if (clazz.endsWith(".java")) { // NOI18N
                                    clazz = clazz.substring(0, clazz.length() - 5);
                                }
                                clazz = clazz.replace('/','.');

                                p.setProperty("debug.class", clazz); // NOI18N
                                targetNames = new String [] {"debug-single-main"};
                            }
                            else {
                                // run servlet
                                // PENDING - what about servlets with main method? servlet should take precedence
                                String executionUri = (String)javaFile.getAttribute(SetExecutionUriAction.ATTR_EXECUTION_URI);
                                if (executionUri!=null) {
                                    p.setProperty("client.urlPart", executionUri); //NOI18N
                                } else {
                                    WebModule webModule = WebModule.getWebModule(javaFile);
                                    String[] urlPatterns = SetExecutionUriAction.getServletMappings(webModule,javaFile);
                                    if (urlPatterns!=null && urlPatterns.length>0) {
                                        ServletUriPanel uriPanel = new ServletUriPanel(urlPatterns,null,true);
                                        DialogDescriptor desc = new DialogDescriptor(uriPanel,
                                            NbBundle.getMessage (SetExecutionUriAction.class, "TTL_setServletExecutionUri"));
                                        Object res = DialogDisplayer.getDefault().notify(desc);
                                        if (res.equals(NotifyDescriptor.YES_OPTION)) {
                                            p.setProperty("client.urlPart", uriPanel.getServletUri()); //NOI18N
                                            try {
                                                javaFile.setAttribute(SetExecutionUriAction.ATTR_EXECUTION_URI,uriPanel.getServletUri());
                                            } catch (IOException ex){}
                                        } else return;
                                    } else {
                                        String mes = java.text.MessageFormat.format (
                                                NbBundle.getMessage (SetExecutionUriAction.class, "TXT_missingServletMappings"),
                                                new Object [] {javaFile.getName()});
                                        NotifyDescriptor desc = new NotifyDescriptor.Message(mes,NotifyDescriptor.Message.ERROR_MESSAGE);
                                        DialogDisplayer.getDefault().notify(desc);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        //DEBUG
        } else if (command.equals (COMMAND_DEBUG)) {
            if (!isSelectedServer ()) {
                return;
            }
            if (isDebugged()) {
                NotifyDescriptor nd;
                nd = new NotifyDescriptor.Confirmation(
                            NbBundle.getMessage(WebActionProvider.class, "MSG_FinishSession"),
                            NotifyDescriptor.OK_CANCEL_OPTION);
                Object o = DialogDisplayer.getDefault().notify(nd);
                if (o.equals(NotifyDescriptor.OK_OPTION)) {            
                    DebuggerManager.getDebuggerManager().getCurrentSession().kill();
                } else {
                    return;
                }
            }
            // 51462 - if there's an ejb reference, but no j2ee app, debug will not work
            if (isEjbRefAndNoJ2eeApp(project)) {
                NotifyDescriptor nd;                
                nd = new NotifyDescriptor.Message(NbBundle.getMessage(WebActionProvider.class, "MSG_EjbRef"), NotifyDescriptor.INFORMATION_MESSAGE);
                Object o = DialogDisplayer.getDefault().notify(nd);
                return;
            }
            p = new Properties();
            
            WebServicesClientSupport wscs = WebServicesClientSupport.getWebServicesClientSupport(project.getProjectDirectory());
            if (wscs != null) { //project contains ws reference
                List serviceClients = wscs.getServiceClients();
                //we store all ws client names into hash set for later fast searching
                HashSet scNames = new HashSet();
                for (Iterator scIt = serviceClients.iterator(); scIt.hasNext(); ) {
                    WsCompileEditorSupport.ServiceSettings serviceClientSettings = 
                            (WsCompileEditorSupport.ServiceSettings)scIt.next();
                    scNames.add(serviceClientSettings.getServiceName());
                }
                
                StringBuffer clientDCP = new StringBuffer();//additional debug.classpath
                StringBuffer clientWDD = new StringBuffer();//additional web.docbase.dir

                //we find all projects containg a web service            
                Set globalPath = GlobalPathRegistry.getDefault().getSourceRoots();
                HashSet serverNames = new HashSet();
                //iteration through all source roots
                for (Iterator iter = globalPath.iterator(); iter.hasNext(); ) {
                    FileObject sourceRoot = (FileObject)iter.next();
                    Project serverProject = FileOwnerQuery.getOwner(sourceRoot);
                    if (serverProject != null) {
                        if (!serverNames.add(serverProject.getProjectDirectory().getName())) //project was already visited
                            continue;
                        WebServicesSupport wss = WebServicesSupport.getWebServicesSupport(serverProject.getProjectDirectory());
                        if (wss != null) { //project contains ws
                            List services = wss.getServices();
                            boolean match = false;
                            for (Iterator sIt = services.iterator(); sIt.hasNext(); ) {
                                WsCompileEditorSupport.ServiceSettings serviceSettings =
                                        (WsCompileEditorSupport.ServiceSettings)sIt.next();
                                String serviceName = serviceSettings.getServiceName();
                                if (scNames.contains(serviceName)) { //matching ws name found
                                    match = true;
                                    break; //no need to continue
                                }
                            }
                            if (match) { //matching ws name found in project
                                //we need to add project's source folders onto a debugger's search path
                                AntProjectHelper serverHelper = wss.getAntProjectHelper();
                                String dcp = serverHelper.getStandardPropertyEvaluator().getProperty(WebProjectProperties.DEBUG_CLASSPATH);
                                if (dcp != null) {
                                    String[] pathTokens = PropertyUtils.tokenizePath(dcp);
                                    for (int i = 0; i < pathTokens.length; i++) {
                                        try {
                                            File f = new File(pathTokens[i]);
                                            if (!f.isAbsolute()) 
                                                pathTokens[i] = serverProject.getProjectDirectory().getPath() + "/" + pathTokens[i];
                                        }
                                        catch (Exception e) {/*no action should be taken*/}
                                        clientDCP.append(pathTokens[i] + ":");
                                    }
                                }
                                
                                String wdd = serverHelper.getStandardPropertyEvaluator().getProperty(WebProjectProperties.WEB_DOCBASE_DIR);
                                if (wdd != null) {
                                    String[] pathTokens = PropertyUtils.tokenizePath(wdd);
                                    for (int i = 0; i < pathTokens.length; i++) {
                                        try {
                                            File f = new File(pathTokens[i]);
                                            if (!f.isAbsolute()) 
                                                pathTokens[i] = serverProject.getProjectDirectory().getPath() + "/" + pathTokens[i];
                                        }
                                        catch (Exception e) {/*no action should be taken*/}
                                        clientWDD.append(pathTokens[i] + ":");
                                    }
                                }
                            }
                        }
                    }
                }
                p.setProperty(WebProjectProperties.WS_DEBUG_CLASSPATHS, clientDCP.toString());
                p.setProperty(WebProjectProperties.WS_WEB_DOCBASE_DIRS, clientWDD.toString());
            }
        
        } else if (command.equals(JavaProjectConstants.COMMAND_DEBUG_FIX)) {
            FileObject[] files = findJavaSources(context);
            String path = null;
            p = new Properties();
            if (files != null) {
                path = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(),files[0]), files[0]);
                targetNames = new String[] {"debug-fix"}; // NOI18N
            } else {
                return;
            }
            // Convert foo/FooTest.java -> foo/FooTest
            if (path.endsWith(".java")) { // NOI18N
                path = path.substring(0, path.length() - 5);
            }
            p.setProperty("fix.includes", path); // NOI18N
            
        //COMPILATION PART
        } else if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            FileObject[] sourceRoots = project.getSourceRoots().getRoots();
            FileObject[] files = findJavaSourcesAndPackages( context, sourceRoots);
            p = new Properties();
            if (files != null) {
                p.setProperty("javac.includes", ActionUtils.antIncludesList(files, getRoot(sourceRoots, files[0]))); // NOI18N
            } else {
                files = findJsps (context);
                if (files != null) {
                    for (int i=0; i < files.length; i++) {
                        FileObject jsp = files[i];
                        if (areIncludesModified(jsp)) {
                            invalidateClassFile(project, jsp);
                        }
                    }
                    setAllPropertiesForSingleJSPCompilation(p, files);
                    targetNames = new String [] {"compile-single-jsp"};
                } else {
                    return;
                }
            }
            
        //TEST PART
        } else if ( command.equals( COMMAND_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            p = new Properties();
            targetNames = setupTestSingle(p, files);
        } else if ( command.equals( COMMAND_DEBUG_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            p = new Properties();
            targetNames = setupDebugTestSingle(p, files);
        } else {
            p = null;
            if (targetNames == null) {
                throw new IllegalArgumentException(command);
            }
        }

        try {
            ActionUtils.runTarget(findBuildXml(), targetNames, p);
        } 
        catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    private String[] setupTestSingle(Properties p, FileObject[] files) {
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        FileObject root = getRoot(testSrcPath, files[0]);
        p.setProperty("test.includes", ActionUtils.antIncludesList(files, root)); // NOI18N
        p.setProperty("javac.includes", ActionUtils.antIncludesList(files, root)); // NOI18N
        return new String[] {"test-single"}; // NOI18N
    }

    private String[] setupDebugTestSingle(Properties p, FileObject[] files) {
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        FileObject root = getRoot(testSrcPath, files[0]);
        String path = FileUtil.getRelativePath(root, files[0]);
        // Convert foo/FooTest.java -> foo.FooTest
        p.setProperty("test.class", path.substring(0, path.length() - 5).replace('/', '.')); // NOI18N
        return new String[] {"debug-test"}; // NOI18N
    }

    /* Deletes translated class/java file to force recompilation of the page with all includes
     */
    public void invalidateClassFile(WebProject wp, FileObject jsp) {
        String dir = updateHelper.getAntProjectHelper().getStandardPropertyEvaluator ().getProperty (WebProjectProperties.BUILD_GENERATED_DIR);
        if (dir == null) {
            return;
        }
        dir = dir + "/src"; //NOI18N
        WebModule wm = WebModule.getWebModule(jsp);
        if (wm == null) {
            return;
        }
        String name = JspNameUtil.getServletName(wm.getDocumentBase(), jsp);
        if (name == null) {
            return;
        }
        String filePath = name.substring(0, name.lastIndexOf('.')).replace('.', '/');
        
        String fileClass = dir + '/' + filePath + ".class"; //NOI18N
        String fileJava = dir + '/' + filePath + ".java"; //NOI18N
        
        File fC = updateHelper.getAntProjectHelper().resolveFile(fileClass);
        File fJ = updateHelper.getAntProjectHelper().resolveFile(fileJava);
        if ((fJ != null) && (fJ.exists())) {
            fJ.delete();
        }
        if ((fC != null) && (fC.exists())) {
            fC.delete();
        }
    }
    
    /* checks if timestamp of any of the included pages is higher than the top page
     */
    public boolean areIncludesModified(FileObject jsp){
        boolean modified = false;
        WebModule wm = WebModule.getWebModule(jsp);
        JspParserAPI jspParser = JspParserFactory.getJspParser();
        JspParserAPI.ParseResult result = jspParser.analyzePage(jsp, new ParserWebModule(wm), JspParserAPI.ERROR_IGNORE);
        if (!result.isParsingSuccess()) {
            modified = true;
        } else {
            List includes = result.getPageInfo().getDependants();
            if ((includes != null) && (includes.size() > 0)) {
                long jspTS = jsp.lastModified().getTime();
                int size = includes.size();
                for (int i=0; i<size; i++) {
                    String filename = (String)includes.get(i);
                    filename = FileUtil.toFile(wm.getDocumentBase()).getPath() + filename;
                    File f = new File(filename);
                    if (f != null) {
                        long incTS = f.lastModified();
                        if (incTS > jspTS) {
                            modified = true;
                            break;
                        }
                    }
                }
            }
        }
        return modified;
    }
    
    // PENDING - should not this be in some kind of an API?
    private boolean decodeBoolean(String raw) {
        if ( raw != null ) {
           String lowecaseRaw = raw.toLowerCase();
               
           if ( lowecaseRaw.equals( "true") || // NOI18N
                lowecaseRaw.equals( "yes") || // NOI18N
                lowecaseRaw.equals( "enabled") ) // NOI18N
               return true;
        }
            
        return false;
    }    
    
    private void setAllPropertiesForSingleJSPCompilation(Properties p, FileObject[] files) {
        p.setProperty("jsp.includes", getBuiltJspFileNamesAsPath(files)); // NOI18N
         /*ActionUtils.antIncludesList(files, project.getWebModule ().getDocumentBase ())*/
        
        p.setProperty("javac.jsp.includes", getCommaSeparatedGeneratedJavaFiles(files)); // NOI18N
        
    }
    
    public String getCommaSeparatedGeneratedJavaFiles(FileObject[] jspFiles) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < jspFiles.length; i++) {
            String jspRes = getJspResource(jspFiles[i]);
            if (i > 0) {
                b.append(',');
            }
            b.append(Utils.getGeneratedJavaResource(jspRes));
        }
        return b.toString();
    }
    
    /** Returns a resource name for a given JSP separated by / (does not start with a /).
     */
    private String getJspResource(FileObject jsp) {
        ProjectWebModule pwm = project.getWebModule ();
        FileObject webDir = pwm.getDocumentBase ();
        return FileUtil.getRelativePath(webDir, jsp);
    }
    
    public File getBuiltJsp(FileObject jsp) {
        ProjectWebModule pwm = project.getWebModule ();
        FileObject webDir = pwm.getDocumentBase ();
        String relFile = FileUtil.getRelativePath(webDir, jsp).replace('/', File.separatorChar);
        File webBuildDir = pwm.getContentDirectoryAsFile();
        return new File(webBuildDir, relFile);
    }
    
    public String getBuiltJspFileNamesAsPath(FileObject[] files) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < files.length; i++) {
            String path = getBuiltJsp(files[i]).getAbsolutePath();
            if (i > 0) {
                b.append(File.pathSeparator);
            }
            b.append(path);
        }
        return b.toString();
    }
    
    
    // THIS METHOD IS (almost) COPIED FROM org.netbeans.modules.java.j2seproject.J2SEProjectUtil
    /** Checks if given file object contains the main method.
     *
     * @param classFO file object represents java 
     * @return false if parameter is null or doesn't contain SourceCookie
     * or SourceCookie doesn't contain the main method
     */    
    final public static boolean hasMainMethod (FileObject fo) {
        // support for unit testing
        /*if (MainClassChooser.unitTestingSupport_hasMainMethodResult != null) {
            return MainClassChooser.unitTestingSupport_hasMainMethodResult.booleanValue ();
        }
        */
        if (fo == null) {
            // ??? maybe better should be thrown IAE
            return false;
        }
        Resource res = JavaModel.getResource (fo);
        assert res != null : "Resource found for FileObject " + fo;
        return hasMainMethod (res);
    }
    
    // copied from JavaNode.hasMain
    private static boolean hasMainMethod (Resource res) {
        if (res != null && res.containsIdentifier ("main")) { //NOI18N
            for (Iterator i = res.getClassifiers ().iterator (); i.hasNext (); ) {
                JavaClass clazz = (JavaClass) i.next ();
                // now it is only important top-level class with the same 
                // name as file. Continue if the file name differs
                // from top level class name.
                if (!clazz.getSimpleName ().equals (JavaModel.getFileObject (res).getName ()))
                    continue;

                for (Iterator j = clazz.getFeatures ().iterator(); j.hasNext ();) {
                    Object o = j.next ();
                    // if it is not a method, continue with next feature
                    if (!(o instanceof Method))
                        continue;

                    Method m = (Method) o;
                    int correctMods = (Modifier.PUBLIC | Modifier.STATIC);
                    // check that method is named 'main' and has set public 
                    // and static modifiers! Method has to also return
                    // void type.
                    if (!"main".equals (m.getName()) || // NOI18N
                       ((m.getModifiers () & correctMods) != correctMods) ||
                       (!"void".equals (m.getType().getName ())))
                       continue;

                    // check parameters - it has to be one of type String[]
                    // or String...
                    if (m.getParameters ().size ()==1) {
                        Parameter par = ((Parameter) m.getParameters ().get (0));
                        String typeName = par.getType ().getName ();
                        if (par.isVarArg () && ("java.lang.String".equals (typeName) || "String".equals (typeName))) { // NOI18N
                            // Main methods written with variable arguments parameter:
                            // public static main(String... args) {
                            // }
                            return true; 
                        } else if (typeName.equals ("String[]") || typeName.equals ("java.lang.String[]")) { // NOI18N
                            // Main method written with array parameter:
                            // public static main(String[] args) {
                            // }
                            return true;
                        }

                    } // end if parameters
                } // end features cycle
            }
        }
        return false;
    }
    
    public boolean isActionEnabled( String command, Lookup context ) {
        if ( findBuildXml() == null ) {
            return false;
        }
        if ( command.equals( COMMAND_DEBUG_SINGLE ) ) {
            return findJavaSources(context) != null || findJsps(context) != null || findHtml(context) != null || findTestSources(context, false) != null;
        }
        else if ( command.equals( COMMAND_COMPILE_SINGLE ) ) {
            return findJavaSourcesAndPackages( context, project.getSourceRoots().getRoots() ) != null || findJsps (context) != null;
        }
        else if ( command.equals( COMMAND_VERIFY ) ) {
            return project.getWebModule().hasVerifierSupport();
        }
        else if ( command.equals( COMMAND_RUN_SINGLE ) ) {
            // test for jsps
            FileObject files [] = findJsps (context);
            if (files != null && files.length >0) return true;
            // test for html pages
            files = findHtml(context);
            if (files != null && files.length >0) return true;
            // test for servlets
            FileObject[] javaFiles = findJavaSources(context);
            if (javaFiles!=null && javaFiles.length > 0) {
                if (javaFiles[0].getAttribute(SetExecutionUriAction.ATTR_EXECUTION_URI)!=null)
                    return true;
                else if (Boolean.TRUE.equals(javaFiles[0].getAttribute("org.netbeans.modules.web.IsServletFile"))) //NOI18N
                    return true;
                else if (isDDServlet(context, javaFiles[0])) {
                    try {
                        javaFiles[0].setAttribute("org.netbeans.modules.web.IsServletFile",Boolean.TRUE); //NOI18N
                    } catch (IOException ex){}
                    return true;
                } else return true; /* because of java main classes, otherwise we would return false */
            } 
            javaFiles = findTestSources(context,false);
            if ((javaFiles != null) && (javaFiles.length > 0)) {
                return true;
            }
            return false;
        }
        else if ( command.equals( COMMAND_TEST_SINGLE )) {
            return findTestSourcesForSources(context) != null;
        }
        else if ( command.equals( COMMAND_DEBUG_TEST_SINGLE ) ) {
            FileObject[] files = findTestSourcesForSources(context);
            return files != null && files.length == 1;
        }
        else {
            // other actions are global
            return true;
        }

        
    }
    
    // Private methods -----------------------------------------------------
    
    private static final String SUBST = "Test.java"; // NOI18N

    /*
     * copied from ActionUtils and reworked so that it checks for mimeType of files, and DOES NOT include files with suffix 'suffix'
     */
    private static FileObject[] findSelectedFilesByMimeType(Lookup context, FileObject dir, String mimeType, String suffix, boolean strict) {
        if (dir != null && !dir.isFolder()) {
            throw new IllegalArgumentException("Not a folder: " + dir); // NOI18N
        }
        List/*<FileObject>*/ files = new ArrayList();
        Iterator it = context.lookup(new Lookup.Template(DataObject.class)).allInstances().iterator();
        while (it.hasNext()) {
            DataObject d = (DataObject)it.next();
            FileObject f = d.getPrimaryFile();
            boolean matches = FileUtil.toFile(f) != null;
            if (dir != null) {
                matches &= (FileUtil.isParentOf(dir, f) || dir == f);
            }
            if (mimeType != null) {
                matches &= f.getMIMEType().equals(mimeType);
            }
            if (suffix != null) {
                matches &= !f.getNameExt().endsWith(suffix);
            }
            // Generally only files from one project will make sense.
            // Currently the action UI infrastructure (PlaceHolderAction)
            // checks for that itself. Should there be another check here?
            if (matches) {
                files.add(f);
            } else if (strict) {
                return null;
            }
        }
        if (files.isEmpty()) {
            return null;
        }
        return (FileObject[])files.toArray(new FileObject[files.size()]);
    }
        
    private static final Pattern SRCDIRJAVA = Pattern.compile("\\.java$"); // NOI18N
    
    /** Find selected java sources 
     */
    private FileObject[] findJavaSources(Lookup context) {
        FileObject[] srcPath = project.getSourceRoots().getRoots();
        for (int i=0; i< srcPath.length; i++) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcPath[i], ".java", true); // NOI18N
            if (files != null) {
                return files;
            }
        }
        return null;
    }
    
    private FileObject[] findJavaSourcesAndPackages (Lookup context, FileObject srcDir) {
        if (srcDir != null) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcDir, null, true); // NOI18N
            //Check if files are either packages of java files
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].isFolder() && !"java".equals(files[i].getExt())) {
                        return null;
                    }
                }
            }
            return files;
        } else {
            return null;
        }
    }
    
    private FileObject[] findJavaSourcesAndPackages (Lookup context, FileObject[] srcRoots) {
        for (int i=0; i<srcRoots.length; i++) {
            FileObject[] result = findJavaSourcesAndPackages(context, srcRoots[i]);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    private FileObject[] findHtml(Lookup context) {
        FileObject webDir = project.getWebModule ().getDocumentBase ();
        FileObject[] files = null;
        if (webDir != null) {
            files = findSelectedFilesByMimeType(context, webDir, "text/html", null, true);
        }
        return files;
    }
    
    /** Find selected jsps
     */
    private FileObject[] findJsps(Lookup context) {
        FileObject webDir = project.getWebModule ().getDocumentBase ();
        FileObject[] files = null;
        if (webDir != null) {
            files = findSelectedFilesByMimeType(context, webDir, "text/x-jsp", ".jspf", true);
        }
        return files;
    }
 
    /** Find either selected tests or tests which belong to selected source files
     */
    private FileObject[] findTestSources(Lookup context, boolean checkInSrcDir) {
        //XXX: Ugly, should be rewritten
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        for (int i=0; i< testSrcPath.length; i++) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, testSrcPath[i], ".java", true); // NOI18N
            if (files != null) {
                return files;
            }
        }
        if (checkInSrcDir && testSrcPath.length>0) {
            FileObject[] files = findSources (context);
            if (files != null) {
                //Try to find the test under the test roots
                FileObject srcRoot = getRoot(project.getSourceRoots().getRoots(),files[0]);
                for (int i=0; i<testSrcPath.length; i++) {
                    FileObject[] files2 = ActionUtils.regexpMapFiles(files,srcRoot, SRCDIRJAVA, testSrcPath[i], SUBST, true);
                    if (files2 != null) {
                        return files2;
                    }
                }
            }
        }
        return null;
    }    
    
    private boolean isEjbRefAndNoJ2eeApp(Project p) {

        WebModule wmod = WebModule.getWebModule(p.getProjectDirectory());
        if (wmod != null) {
            WebApp webXml = null;
            try {
                webXml = DDProvider.getDefault().getDDRoot(wmod.getDeploymentDescriptor());
            } catch (IOException ioe) {
                // ignore
            }
            EjbLocalRef[] ejbLocalRefs = webXml.getEjbLocalRef();
            if ((ejbLocalRefs != null) && (ejbLocalRefs.length > 0)) { // there's an ejb reference in this module                
                if (!isInJ2eeApp(p)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isInJ2eeApp(Project p) {
        Set globalPath = GlobalPathRegistry.getDefault().getSourceRoots();
        Iterator iter = globalPath.iterator();
        while (iter.hasNext()) {
            FileObject sourceRoot = (FileObject)iter.next();
            Project project = FileOwnerQuery.getOwner(sourceRoot);
            if (project != null) {
                Object j2eeAppProvider = project.getLookup().lookup(J2eeAppProvider.class);
                if (j2eeAppProvider != null) { // == it is j2ee app
                    J2eeAppProvider j2eeApp = (J2eeAppProvider)j2eeAppProvider;
                    J2eeModuleProvider[] j2eeModules = j2eeApp.getChildModuleProviders();
                    if ((j2eeModules != null) && (j2eeModules.length > 0)) { // == there are some modules in the j2ee app
                        J2eeModuleProvider affectedPrjProvider = 
                                (J2eeModuleProvider)p.getLookup().lookup(J2eeModuleProvider.class);
                        if (affectedPrjProvider != null) {
                            if (Arrays.asList(j2eeModules).contains(affectedPrjProvider)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    private boolean isDebugged() {
        J2eeModuleProvider jmp = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
        Session[] sessions = DebuggerManager.getDebuggerManager().getSessions();
        ServerDebugInfo sdi = null;
        
        if (sessions != null && sessions.length > 0) {
            sdi = jmp.getServerDebugInfo ();
            if (sdi == null)
                return false;
        }
        
        for (int i=0; i < sessions.length; i++) {
            Session s = sessions[i];
            if (s != null) {
                Object o = s.lookupFirst(null, AttachingDICookie.class);
                if (o != null) {
                    AttachingDICookie attCookie = (AttachingDICookie)o;
                    if (sdi.getTransport().equals(ServerDebugInfo.TRANSPORT_SHMEM)) {
                        String shmem = attCookie.getSharedMemoryName();
                        if (shmem == null) {
                            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Shared memory name is null.");
                            return false;
                        }
                        if (shmem.equalsIgnoreCase(sdi.getShmemName())) {
                            return true;
                        }
                    } else {
                        String hostname = attCookie.getHostName();
                        if (hostname == null) {
                            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Hostname is null.");
                            return false;
                        }
                        if (hostname.equalsIgnoreCase(sdi.getHost())) {
                            if (attCookie.getPortNumber() == sdi.getPort()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isSelectedServer () {
        String instance = updateHelper.getAntProjectHelper().getStandardPropertyEvaluator ().getProperty (WebProjectProperties.J2EE_SERVER_INSTANCE);
        if (instance != null) {
            J2eeModuleProvider jmp = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
            String sdi = jmp.getServerInstanceID();
            if (sdi != null) {
                String id = Deployment.getDefault().getServerID(sdi);
                if (id != null) {
                    return true;
                }
            }
        }
        
        // if there is some server instance of the type which was used
        // previously do not ask and use it
        String serverType = updateHelper.getAntProjectHelper().getStandardPropertyEvaluator ().getProperty (WebProjectProperties.J2EE_SERVER_TYPE);
        if (serverType != null) {
            String[] servInstIDs = Deployment.getDefault().getInstancesOfServer(serverType);
            if (servInstIDs.length > 0) {
                setServerInstance(servInstIDs[0]);
                return true;
            }
        }
        
        // no selected server => warning
        String msg = NbBundle.getMessage(WebActionProvider.class, "MSG_No_Server_Selected"); //  NOI18N
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.WARNING_MESSAGE));
        return false;
    }
    
    private void setServerInstance(String serverInstanceId) {
        WebProjectProperties wpp = new WebProjectProperties (project, updateHelper, project.evaluator(), project.getReferenceHelper());
        wpp.setServerInstance(serverInstanceId);
        wpp.store ();
    }
    
    private boolean isDDServlet(Lookup context, FileObject javaClass) {
        FileObject webDir = project.getWebModule ().getDocumentBase ();
        if (webDir==null) return false;
        FileObject fo = webDir.getFileObject("WEB-INF/web.xml"); //NOI18N
        String className = FileUtil.getRelativePath(getRoot(project.getSourceRoots().getRoots(), javaClass), javaClass);
        if (fo==null) return false;
        try {
            WebApp webApp = DDProvider.getDefault().getDDRoot(fo);
            Servlet servlet = (Servlet)webApp.findBeanByName("Servlet","ServletClass",className); //NOI18N
            if (servlet!=null) return true;
            else return false;
        } catch (IOException ex) {return false;}  
    }
    
    /** Find tests corresponding to selected sources.
     */
    private FileObject[] findTestSourcesForSources(Lookup context) {
        FileObject[] sourceFiles = findSources(context);
        if (sourceFiles == null) {
            return null;
        }
        FileObject[] testSrcPath = project.getTestSourceRoots().getRoots();
        if (testSrcPath.length == 0) {
            return null;
        }
        FileObject[] srcPath = project.getSourceRoots().getRoots();
        FileObject srcDir = getRoot(srcPath, sourceFiles[0]);
        for (int i=0; i<testSrcPath.length; i++) {
            FileObject[] files2 = ActionUtils.regexpMapFiles(sourceFiles, srcDir, SRCDIRJAVA, testSrcPath[i], SUBST, true);
            if (files2 != null) {
                return files2;
            }
        }
        return null;
    }      

    private FileObject getRoot (FileObject[] roots, FileObject file) {
        FileObject srcDir = null;
        for (int i=0; i< roots.length; i++) {
            if (FileUtil.isParentOf(roots[i],file)) {
                srcDir = roots[i];
                break;
            }
        }
        return srcDir;
    }

    /** Find selected sources, the sources has to be under single source root,
     *  @param context the lookup in which files should be found
     */
    private FileObject[] findSources(Lookup context) {
        FileObject[] srcPath = project.getSourceRoots().getRoots();
        for (int i=0; i< srcPath.length; i++) {
            FileObject[] files = ActionUtils.findSelectedFiles(context, srcPath[i], ".java", true); // NOI18N
            if (files != null) {
                return files;
            }
        }
        return null;
    }

    
    
    
}
