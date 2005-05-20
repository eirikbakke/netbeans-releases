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
package org.netbeans.modules.j2ee.jboss4;

import org.netbeans.modules.j2ee.jboss4.ide.ui.JBInstantiatingIterator;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginProperties;
import org.netbeans.modules.j2ee.jboss4.ide.ui.JBPluginUtils;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import java.util.Vector;
import java.io.File;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;
import javax.enterprise.deploy.spi.status.ClientConfiguration;
import javax.enterprise.deploy.spi.status.DeploymentStatus;
import org.openide.util.RequestProcessor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.j2ee.jboss4.ide.JBDeploymentStatus;
import javax.enterprise.deploy.shared.ActionType;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import java.util.Hashtable;
import javax.naming.*;
import javax.management.*;
import java.net.URLClassLoader;

/**
 *
 * @author Ivan Sidorkin
 */
public class JBDeployer implements ProgressObject, Runnable {
    
    Target[] target;
    File file;
    File file2;
    String uri;
    
    /** Creates a new instance of JBDeployer */
    public JBDeployer(String serverUri) {
        uri = serverUri;
    }
    
    
    public ProgressObject deploy(Target[] target, File file, File file2){
        
        
        this.target = target;
        this.file = file;
        this.file2 = file2;
        
        fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, ""));
        RequestProcessor.getDefault().post(this, 0, Thread.NORM_PRIORITY);
        return this;
    }
    
    
    public void run(){
        
        
        String deployDir = InstanceProperties.getInstanceProperties(uri).getProperty(JBInstantiatingIterator.PROPERTY_DEPLOY_DIR);
        FileObject foIn = FileUtil.toFileObject(file);
        FileObject foDestDir = FileUtil.toFileObject(new File(deployDir));
        String fileName = file.getName();
        
        File toDeploy = new File(deployDir+File.separator+fileName);
        if(toDeploy.exists())
            toDeploy.delete();
        
        fileName = fileName.substring(0,fileName.lastIndexOf('.'));
        
        fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, ""));
        
        try{
            wait(2000);
        }catch(Exception e){
            
        }
        
        fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.RUNNING, ""));
        
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader loader = ((JBDeploymentFactory)JBDeploymentFactory.create()).getJBClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        try{
            //org.openide.filesystems.FileUtil.copyFile(foIn, foDestDir, fileName); // copy version
            
            // mainDeployer version
            Hashtable env = new Hashtable();
            
            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            env.put(Context.PROVIDER_URL, "jnp://localhost:"+JBPluginUtils.getJnpPort(InstanceProperties.getInstanceProperties(uri).getProperty(JBInstantiatingIterator.PROPERTY_SERVER_DIR)));
            env.put(Context.OBJECT_FACTORIES, "org.jboss.naming");
            env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces" );
            
            ObjectName mainDeployer = new ObjectName("jboss.system:service=MainDeployer");
            
            Object[] args = {file.toURL().toString()};
            
            String[] sig = {String.class.getName()};
            
            InitialContext ctx = new InitialContext(env);
            
            MBeanServerConnection srv = (MBeanServerConnection)ctx.lookup("/jmx/invoker/RMIAdaptor");
            
            srv.invoke(mainDeployer, "deploy", args, sig);
            
        }catch(Exception e){
            fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.FAILED, ""));
        }finally{
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
        
        fireHandleProgressEvent(null, new JBDeploymentStatus(ActionType.EXECUTE, CommandType.DISTRIBUTE, StateType.COMPLETED, "Applicaton Deployed"));
        
    }
    
    
    // ----------  Implementation of ProgressObject interface
    private Vector listeners = new Vector();
    private DeploymentStatus deploymentStatus;
    
    public void addProgressListener(ProgressListener pl) {
        listeners.add(pl);
    }
    
    public void removeProgressListener(ProgressListener pl) {
        listeners.remove(pl);
    }
    
    public void stop() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public boolean isStopSupported() {
        return false;
    }
    
    public void cancel() throws OperationUnsupportedException {
        throw new OperationUnsupportedException("");
    }
    
    public boolean isCancelSupported() {
        return false;
    }
    
    public ClientConfiguration getClientConfiguration(TargetModuleID targetModuleID) {
        return null;
    }
    
    public TargetModuleID[] getResultTargetModuleIDs() {
        return new TargetModuleID[]{ new TargetModuleIDImpl(target[0],"") };
    }
    
    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }
    
    /** Report event to any registered listeners. */
    public void fireHandleProgressEvent(TargetModuleID targetModuleID, DeploymentStatus deploymentStatus) {
        ProgressEvent evt = new ProgressEvent(this, targetModuleID, deploymentStatus);
        
        this.deploymentStatus = deploymentStatus;
        
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }
        
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                ProgressListener target = (ProgressListener)targets.elementAt(i);
                target.handleProgressEvent(evt);
            }
        }
    }
    
    
}



class TargetModuleIDImpl implements TargetModuleID{
    private Target target;
    private String module_id;
    TargetModuleIDImpl(Target target, String module_id){
        this.target = target;
        this.module_id = module_id;
    }
    public TargetModuleID[]     getChildTargetModuleID(){
        return null;
    }
    //Retrieve a list of identifiers of the children of this deployed module.
    public java.lang.String     getModuleID(){
        return module_id;
    }
    //         Retrieve the id assigned to represent the deployed module.
    public TargetModuleID     getParentTargetModuleID(){

        return null;
    }
    //Retrieve the identifier of the parent object of this deployed module.
    public Target     getTarget(){
        return target;
    }
    //Retrieve the name of the target server.
    public java.lang.String     getWebURL(){
        return "http://" + module_id; //NOI18N
    }
    //If this TargetModulID represents a web module retrieve the URL for it.
    public java.lang.String     toString() {
        return module_id;
    }
}