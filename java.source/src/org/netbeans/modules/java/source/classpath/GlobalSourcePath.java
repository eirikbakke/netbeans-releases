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

package org.netbeans.modules.java.source.classpath;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.classpath.GlobalPathRegistryEvent;
import org.netbeans.api.java.classpath.GlobalPathRegistryListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Tomas Zezula
 */
public class GlobalSourcePath {
    
    private static GlobalSourcePath instance;
    
    private final GlobalPathRegistry gpr;
    private List<? extends PathResourceImplementation> resources;
    private List<? extends PathResourceImplementation> binaryResources;
    private Set<ClassPath> activeCps;
    private Map<URL, SourceForBinaryQuery.Result> sourceResults;
    private Map<URL, URL[]> translatedRoots;
    private Map<URL, WeakValue> unknownRoots;
    private long timeStamp;             //Lamport event ordering
    private Runnable debugCallBack;
    
    private final SourcePathImplementation sourcePath;
    private final BinaryPathImplementation binaryPath;
    
    private final Listener listener;

    /** Creates a new instance of GlobalSourcePath */
    private GlobalSourcePath() {
        this.listener = new Listener ();
        this.sourcePath = new SourcePathImplementation ();
        this.binaryPath = new BinaryPathImplementation ();
        this.timeStamp = -1;
        this.gpr = GlobalPathRegistry.getDefault();
        this.activeCps = Collections.emptySet();
        this.sourceResults = Collections.emptyMap();
        this.unknownRoots = new HashMap<URL, WeakValue>();
        this.translatedRoots = new HashMap<URL, URL[]> ();
        this.gpr.addGlobalPathRegistryListener ((GlobalPathRegistryListener)WeakListeners.create(GlobalPathRegistryListener.class,this.listener,this.gpr));        
    }
    
    public URL[] getSourceRootForBinaryRoot (final URL binaryRoot, final ClassPath definingClassPath, final boolean fire) {
        URL[] result = this.translatedRoots.get(binaryRoot);
        if (result != null) {
            if (result.length > 0) {
                return result;
            }
            else {
                return null;
            }
        } 
        else {
            List<URL> cacheRoots = new ArrayList<URL> ();
            Collection<? extends PathResourceImplementation> unknownRes = getSources(SourceForBinaryQuery.findSourceRoots(binaryRoot).getRoots(),cacheRoots,null);
            if (unknownRes.isEmpty()) {
                return null;
            }
            else {                
                result = new URL[cacheRoots.size()];
                synchronized (this) {
                    Iterator<URL> it = cacheRoots.iterator();
                    for (int i=0; it.hasNext(); i++) {
                        result[i] = it.next ();
                        unknownRoots.put(result[i],new WeakValue(definingClassPath,result[i]));
                    }
                }
                if (fire) {
                    this.resetCacheAndFire();
                }
                return result;
            }
        }
    }
    
    public ClassPathImplementation getSourcePath () {
        return this.sourcePath;
    }
    
    public ClassPathImplementation getBinaryPath () {
        return this.binaryPath;
    }
    
    private void resetCacheAndFire () {
        synchronized (this) {
            this.resources = null;
            this.binaryResources = null;
            this.timeStamp++;
        }
        this.sourcePath.firePropertyChange ();
        this.binaryPath.firePropertyChange ();
    }
    
    private static Result createResources (final Request r) {
        assert r != null;
        Set<PathResourceImplementation> result = new HashSet<PathResourceImplementation> ();
        Set<PathResourceImplementation> binaryResult = new HashSet<PathResourceImplementation> ();
        final Map<URL,URL[]> translatedRoots = new HashMap<URL, URL[]>();
        Set<ClassPath> newCps = new HashSet<ClassPath> ();
        for (ClassPath cp : r.sourceCps) {
            boolean isNew = !r.oldCps.remove(cp);
            for (ClassPath.Entry entry : cp.entries()) {
                result.add(ClassPathSupport.createResource(entry.getURL()));
            }
            boolean notContained = newCps.add (cp);
            if (isNew && notContained) {
                cp.addPropertyChangeListener(r.propertyListener);
            }
        }
        Map<URL,SourceForBinaryQuery.Result> newSR = new HashMap<URL,SourceForBinaryQuery.Result> ();
        for (ClassPath cp : r.bootCps) {
            boolean isNew = !r.oldCps.remove(cp);
            for (ClassPath.Entry entry : cp.entries()) {
                URL url = entry.getURL();
                if (!translatedRoots.containsKey(url)) {
                    SourceForBinaryQuery.Result sr = r.oldSR.remove (url);
                    boolean isNewSR;
                    if (sr == null) {
                        sr = SourceForBinaryQuery.findSourceRoots(url);
                        isNewSR = true;                    
                    }
                    else {
                        isNewSR = false;
                    }    
                    assert !newSR.containsKey(url);
                    newSR.put(url,sr);
                    List<URL> cacheURLs = new ArrayList<URL> ();
                    Collection<? extends PathResourceImplementation> srcRoots = getSources (sr.getRoots(), cacheURLs, r.unknownRoots);
                    if (srcRoots.isEmpty()) {
                        binaryResult.add (ClassPathSupport.createResource(url));
                    }
                    else {
                        result.addAll(srcRoots);
                    }
                    translatedRoots.put(url, cacheURLs.toArray(new URL[cacheURLs.size()]));
                    if (isNewSR) {
                        sr.addChangeListener(r.changeListener);
                    }
                }                
            }
            boolean notContained = newCps.add (cp);
            if (isNew && notContained) {
                cp.addPropertyChangeListener(r.propertyListener);
            }
        }
        
        for (ClassPath cp : r.compileCps) {
            boolean isNew = !r.oldCps.remove(cp);
            for (ClassPath.Entry entry : cp.entries()) {
                URL url = entry.getURL();
                if (!translatedRoots.containsKey(url)) {
                    SourceForBinaryQuery.Result sr = r.oldSR.remove (url);
                    boolean isNewSR;
                    if (sr == null) {
                        sr = SourceForBinaryQuery.findSourceRoots(url);
                        isNewSR = true;                    
                    }
                    else {
                        isNewSR = false;
                    }
                    assert !newSR.containsKey(url);
                    newSR.put(url,sr);
                    List<URL> cacheURLs = new ArrayList<URL> ();
                    Collection<? extends PathResourceImplementation> srcRoots = getSources(sr.getRoots(),cacheURLs, r.unknownRoots);
                    if (srcRoots.isEmpty()) {
                        binaryResult.add(ClassPathSupport.createResource(url));
                    }
                    else {
                        result.addAll(srcRoots);
                    }
                    translatedRoots.put(url, cacheURLs.toArray(new URL[cacheURLs.size()]));
                    if (isNewSR) {
                        sr.addChangeListener(r.changeListener);
                    }
                }
            }
            boolean notContained = newCps.add (cp);
            if (isNew && notContained) {
                cp.addPropertyChangeListener(r.propertyListener);
            }
        }
        
        for (ClassPath cp : r.oldCps) {
            cp.removePropertyChangeListener(r.propertyListener);
        }
        
        for (Map.Entry<URL,SourceForBinaryQuery.Result> entry : r.oldSR.entrySet()) {
            entry.getValue().removeChangeListener(r.changeListener);
        }        
        for (URL unknownRoot : r.unknownRoots.keySet()) {
            result.add (ClassPathSupport.createResource(unknownRoot));
        }        
        return new Result (r.timeStamp, new ArrayList<PathResourceImplementation> (result), new ArrayList(binaryResult), newCps,newSR,translatedRoots);
    }    
    
    /**
     * Unit test method, used to set a callback which is called
     * form getResources to emulate a race condition.
     * The access to debugCallBack is not synchronized, it should be
     * set before test and unset after test.
     * @param callBack to be called
     *
     **/
    void setDebugCallBack (final Runnable callBack) {
        this.debugCallBack = callBack;
    }
    
    private static Collection <? extends PathResourceImplementation> getSources (final FileObject[] roots, final List<URL> cacheDirs, final Map<URL, WeakValue> unknownRoots) {
        assert roots != null;        
        URL[] urls = new URL[roots.length];
        boolean add = true;
        for (int i=0; i<roots.length; i++) {
            try {
                URL url = roots[i].getURL();
                if (!"file".equals(url.getProtocol())) {     //NOI18N
                    add = false;
                    break;
                }
                urls[i] = url;
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
            }
        }        
        if (add) {
            List<PathResourceImplementation> result = new ArrayList<PathResourceImplementation> (roots.length);
            for (int i=0; i<urls.length; i++) {
                if (cacheDirs != null) {
                    cacheDirs.add (urls[i]);                        
                }
                if (unknownRoots != null) {
                    unknownRoots.remove (urls[i]);
                }
                result.add(ClassPathSupport.createResource(urls[i]));
            }
            return result;
        }
        return Collections.<PathResourceImplementation>emptySet();
    }
       
    private class WeakValue extends WeakReference<ClassPath> implements Runnable {
        
        private URL key;
        
        public WeakValue (ClassPath ref, URL key) {
            super (ref, Utilities.activeReferenceQueue());
            assert key != null;
            this.key = key;                        
        }                
        
        public void run () {
            boolean fire = false;
            synchronized (GlobalSourcePath.this) {                
                fire = (GlobalSourcePath.this.unknownRoots.remove (key) != null);                
            }
            if (fire) {                
                GlobalSourcePath.this.resetCacheAndFire();
            }
        }        
    }
    
    private long getTimeStamp () {
        return this.timeStamp;        
    }    
    
    private static class Request {
        
        final long timeStamp;
        final Set<ClassPath> sourceCps;
        final Set<ClassPath> bootCps;
        final Set<ClassPath> compileCps;
        final Set<ClassPath> oldCps;
        final Map <URL, SourceForBinaryQuery.Result> oldSR;
        final Map<URL, WeakValue> unknownRoots;
        final PropertyChangeListener propertyListener;
        final ChangeListener changeListener;
        
        public Request (final long timeStamp, final Set<ClassPath> sourceCps, final Set<ClassPath> bootCps, final Set<ClassPath> compileCps,
            final Set<ClassPath> oldCps, final Map <URL, SourceForBinaryQuery.Result> oldSR, final Map<URL, WeakValue> unknownRoots,
            final PropertyChangeListener propertyListener, final ChangeListener changeListener) {
            assert sourceCps != null;
            assert bootCps != null;
            assert compileCps != null;
            assert oldCps != null;
            assert oldSR != null;
            assert unknownRoots != null;
            assert propertyListener != null;
            assert changeListener != null;
            
            this.timeStamp = timeStamp;
            this.sourceCps = sourceCps;
            this.bootCps = bootCps;
            this.compileCps = compileCps;
            this.oldCps = oldCps;
            this.oldSR = oldSR;
            this.unknownRoots = unknownRoots;
            this.propertyListener = propertyListener;
            this.changeListener = changeListener;
        }
    }
    
    private static class Result {
     
        final long timeStamp;
        final List<? extends PathResourceImplementation> resources;
        final List<? extends PathResourceImplementation> binaryResources;
        final Set<ClassPath> newCps;
        final Map<URL, SourceForBinaryQuery.Result> newSR;
        final Map<URL, URL[]> translatedRoots;
        
        public Result (final long timeStamp, final List<? extends PathResourceImplementation> resources,
            final List<? extends PathResourceImplementation> binaryResources, final Set<ClassPath> newCps,
            final Map<URL, SourceForBinaryQuery.Result> newSR, final Map<URL, URL[]> translatedRoots) {
            assert resources != null;
            assert binaryResources != null;
            assert newCps != null;
            assert newSR  != null;
            assert translatedRoots != null;
            this.timeStamp = timeStamp;
            this.resources = resources;
            this.binaryResources = binaryResources;
            this.newCps = newCps;
            this.newSR = newSR;
            this.translatedRoots = translatedRoots;
        }
    }
    
    private class SourcePathImplementation implements ClassPathImplementation {
        
        private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
        
    
        public List<? extends PathResourceImplementation> getResources() {
            Request request;
            synchronized (GlobalSourcePath.this) {
                if (GlobalSourcePath.this.resources != null) {
                    return GlobalSourcePath.this.resources;
                }
                request = new Request (
                    GlobalSourcePath.this.getTimeStamp(),
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.SOURCE),
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.BOOT), 
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.COMPILE),
                    GlobalSourcePath.this.activeCps,
                    GlobalSourcePath.this.sourceResults,
                    GlobalSourcePath.this.unknownRoots,
                    GlobalSourcePath.this.listener,
                    GlobalSourcePath.this.listener);
            }
            Result res = createResources (request);
            if (GlobalSourcePath.this.debugCallBack != null) {
                GlobalSourcePath.this.debugCallBack.run();
            }
            synchronized (this) {            
                if (GlobalSourcePath.this.getTimeStamp() == res.timeStamp) {
                    if (GlobalSourcePath.this.resources == null) {
                        GlobalSourcePath.this.resources = res.resources;
                        GlobalSourcePath.this.binaryResources = res.binaryResources;
                        GlobalSourcePath.this.activeCps = res.newCps;
                        GlobalSourcePath.this.sourceResults = res.newSR;
                        GlobalSourcePath.this.translatedRoots = res.translatedRoots;                    
                    }
                    return GlobalSourcePath.this.resources;
                }            
                else {
                    return res.resources;
                }
            }
        }
    
        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            assert listener != null;
            if (this.listeners == null) {
                this.listeners = new ArrayList<PropertyChangeListener> ();
            }
            this.listeners.add (listener);        
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            assert listener != null;
            if (this.listeners == null) {
                return;
            }
            this.listeners.remove(listener);
        }
        
        void firePropertyChange () {        
            PropertyChangeListener[] _listeners;
            synchronized (this) {
                if (this.listeners == null) {
                    return;
                }
                _listeners = this.listeners.toArray(new PropertyChangeListener[this.listeners.size()]);            
            }
            PropertyChangeEvent event = new PropertyChangeEvent (this,PROP_RESOURCES,null,null);
            for (PropertyChangeListener l : _listeners) {
                l.propertyChange (event);
            }
        }
    }
    
    private class BinaryPathImplementation implements ClassPathImplementation {
        private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
    
        public List<? extends PathResourceImplementation> getResources() {
            Request request;
            synchronized (GlobalSourcePath.this) {
                if (GlobalSourcePath.this.binaryResources != null) {
                    return GlobalSourcePath.this.binaryResources;
                }
                request = new Request (
                    GlobalSourcePath.this.getTimeStamp(),
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.SOURCE),
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.BOOT), 
                    GlobalSourcePath.this.gpr.getPaths(ClassPath.COMPILE),
                    GlobalSourcePath.this.activeCps,
                    GlobalSourcePath.this.sourceResults,
                    GlobalSourcePath.this.unknownRoots,
                    GlobalSourcePath.this.listener,
                    GlobalSourcePath.this.listener);
            }
            Result res = createResources (request);
            if (GlobalSourcePath.this.debugCallBack != null) {
                GlobalSourcePath.this.debugCallBack.run();
            }
            synchronized (this) {            
                if (GlobalSourcePath.this.getTimeStamp() == res.timeStamp) {
                    if (GlobalSourcePath.this.binaryResources == null) {
                        GlobalSourcePath.this.resources = res.resources;
                        GlobalSourcePath.this.binaryResources = res.binaryResources;
                        GlobalSourcePath.this.activeCps = res.newCps;
                        GlobalSourcePath.this.sourceResults = res.newSR;
                        GlobalSourcePath.this.translatedRoots = res.translatedRoots;                    
                    }
                    return GlobalSourcePath.this.binaryResources;
                }            
                else {
                    return res.binaryResources;
                }
            }
        }
        
        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            assert listener != null;
            if (this.listeners == null) {
                this.listeners = new ArrayList<PropertyChangeListener> ();
            }
            this.listeners.add (listener);        
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            assert listener != null;
            if (this.listeners == null) {
                return;
            }
            this.listeners.remove(listener);
        }
        
        void firePropertyChange () {        
            PropertyChangeListener[] _listeners;
            synchronized (this) {
                if (this.listeners == null) {
                    return;
                }
                _listeners = this.listeners.toArray(new PropertyChangeListener[this.listeners.size()]);            
            }
            PropertyChangeEvent event = new PropertyChangeEvent (this,PROP_RESOURCES,null,null);
            for (PropertyChangeListener l : _listeners) {
                l.propertyChange (event);
            }
        }
    }
    
    
    private class Listener implements GlobalPathRegistryListener, PropertyChangeListener, ChangeListener {
        
            public void pathsAdded(GlobalPathRegistryEvent event) {
                resetCacheAndFire ();
            }

            public void pathsRemoved(GlobalPathRegistryEvent event) {
                resetCacheAndFire ();
            }        

            public void propertyChange(PropertyChangeEvent evt) {
                if (ClassPath.PROP_ENTRIES.equals(evt.getPropertyName())) {
                    resetCacheAndFire ();
                }
            }
    
            public void stateChanged (ChangeEvent event) {
                resetCacheAndFire();
            }
    }
    
    public static synchronized GlobalSourcePath getDefault () {
        if (instance == null) {
            instance = new GlobalSourcePath ();
        }
        return instance;
    }
    
}
