/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.openide.loaders;

import java.awt.datatransfer.Transferable;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.xml.XMLUtil;

/** Default implementation of a shortcut to another data object.
* Since 1.13 it extends MultiDataObject.
* @author Jan Jancura, Jaroslav Tulach
*/
public class DataShadow extends MultiDataObject implements DataObject.Container {
    /** generated Serialized Version UID */
    static final long serialVersionUID = 6305590675982925167L;

    /** original data object */
    private DataObject original;
    /** Listener attached to original DataObject. */
    private OrigL origL = null;
    /** List of nodes created for the DataShadow. */
    private LinkedList nodes = new LinkedList ();

    /** Extension name. */
    static final String SHADOW_EXTENSION = "shadow"; // NOI18N
    
    /** Map of all <FileObject, Set<DataShadow>>. Where the file object
     Is the original file */
    private static Map allDataShadows;
    
    private static Mutex MUTEX = new Mutex ();

    /** Getter for the Set that contains all DataShadows. */
    private static synchronized Map getDataShadowsSet() {
        if (allDataShadows == null) {
            allDataShadows = new HashMap();
        }
        return allDataShadows;
    }
    
    private static synchronized void enqueueDataShadow(DataShadow ds) {
        Map m = getDataShadowsSet ();
        
        FileObject prim = ds.original.getPrimaryFile ();
        Reference ref = new DSWeakReference(ds);
        Set s = (Set)m.get (prim);
        if (s == null) {
            s = Collections.singleton (ref);
            getDataShadowsSet ().put (prim, s);
        } else {
            if (! (s instanceof HashSet)) {
                s = new HashSet (s);
                getDataShadowsSet ().put (prim, s);
            }
            s.add (ref);
        }
    }

    /** @return all active DataShadows or null */
    private static synchronized List getAllDataShadows() {
        if (allDataShadows == null || allDataShadows.isEmpty()) {
            return null;
        }
        
        List ret = new ArrayList(allDataShadows.size());
        Iterator it = allDataShadows.values ().iterator();
        while (it.hasNext()) {
            Set ref = (Set) it.next();
            Iterator refs = ref.iterator ();
            while (refs.hasNext ()) {
                Reference r = (Reference)refs.next ();
                DataShadow shadow = (DataShadow)r.get ();
                if (shadow != null) {
                    ret.add (shadow);
                }
            }
        }
        
        return ret;
    }
    
    /** Checks whether a change of the given dataObject
     * does not hurt validity of a DataShadow
     */
    static void checkValidity(EventObject ev) {
        DataObject src = null;
        if (ev instanceof OperationEvent) {
            src = ((OperationEvent)ev).getObject();
        }

        Set shadows = null;
        synchronized (DataShadow.class) {
            if (allDataShadows == null || allDataShadows.isEmpty ()) return;
            
            if (src != null) {
                shadows = (Set)allDataShadows.get (src.getPrimaryFile ());
                if (shadows == null) {
                    // we know the source of the event and there are no
                    // shadows with such original
                    return;
                }
                // to prevent modifications
                shadows = new HashSet (shadows);
            }
        }
        
        DataObject changed = null;
        OperationEvent.Copy c;
        if (
            ev instanceof OperationEvent.Rename
            ||
            ev instanceof OperationEvent.Move
        ) {
            changed = ((OperationEvent)ev).getObject();
        }
        
        if (shadows != null) {
            //
            // optimized for speed, we have found the shadow(s) that
            // belong to this FileObject
            //
            Iterator refs = shadows.iterator ();
            while (refs.hasNext ()) {
                Reference r = (Reference)refs.next ();
                DataShadow shadow = (DataShadow)r.get ();
                if (shadow != null) {
                    shadow.refresh (shadow.original == changed);
                }
            }
            return;
        }
        
        List all = getAllDataShadows();
        if (all == null) {
            return;
        }
        
        
        int size = all.size();
        for (int i = 0; i < size; i++) {
            DataShadow obj = (DataShadow)all.get(i);
            // if original was renamed or moved update 
            // the file with the link
            obj.refresh (obj.original == changed);
        }
    }
    
    /** Constructs new data shadow for given primary file and referenced original.
    * Method to allow subclasses of data shadow.
    *
    * @param fo the primary file
    * @param original original data object
    * @param loader the loader that created the object
    */
    protected DataShadow (
        FileObject fo, DataObject original, MultiFileLoader loader
    ) throws DataObjectExistsException {
        super (fo, loader);
        init(original);
    }

    /** Constructs new data shadow for given primary file and referenced original.
    * Method to allow subclasses of data shadow.
    *
    * @param fo the primary file
    * @param original original data object
    * @param loader the loader that created the object
    * @deprecated Since 1.13 do not use this constructor, it is for backward compatibility only
    */
    protected DataShadow (
        FileObject fo, DataObject original, DataLoader loader
    ) throws DataObjectExistsException {
        super (fo, loader);
        init(original);
    }
    
    /** Perform initialization after construction.
    * @param original original data object
    */
    private void init(DataObject original) {
        if (original == null)
            throw new IllegalArgumentException();
        setOriginal (original);
        enqueueDataShadow(this);
    }
    
    /** Constructs new data shadow for given primary file and referenced original.
    * @param fo the primary file
    * @param original original data object
    */
    private DataShadow (FileObject fo, DataObject original) throws DataObjectExistsException {
        this (fo, original, (MultiFileLoader)DataLoaderPool.getShadowLoader ());
    }

    /** Method that creates new data shadow in a folder. The name chosen is based
    * on the name of the original object.
    *
    * @param folder target folder to create data in
    * @param original orignal object that should be represented by the shadow
    */
    public static DataShadow create (DataFolder folder, DataObject original)
    throws IOException {
        return create (folder, null, original, SHADOW_EXTENSION);
    }

    /** Method that creates new data shadow in a folder. The default extension
    * is used.
    *
    * @param folder target folder to create data in
    * @param name name to give to the shadow
    * @param original object that should be represented by the shadow
    */
    public static DataShadow create (
        DataFolder folder,
        final String name,
        final DataObject original
    ) throws IOException {
        return create (folder, name, original, SHADOW_EXTENSION);
    }
    
    /** Method that creates new data shadow in a folder. All modifications are
    * done atomicly using {@link FileSystem#runAtomicAction}.
    *
    * @param folder target folder to create data in
    * @param name name to give to the shadow
    * @param original orignal object that should be represented by the shadow
    */
    public static DataShadow create (
        DataFolder folder,
        final String name,
        final DataObject original,
        final String ext
    ) throws IOException {
        final FileObject fo = folder.getPrimaryFile ();
        final DataShadow[] arr = new DataShadow[1];

        DataObjectPool.getPOOL().runAtomicAction (fo, new FileSystem.AtomicAction () {
                                                 public void run () throws IOException {
                                                     FileObject file = writeOriginal (name, ext, fo, original);
                                                     DataObject obj = DataObject.find (file);
                                                     if (obj instanceof DataShadow) {
                                                         arr[0] = (DataShadow)obj;
                                                     } else {
                                                         // wrong instance => shadow was not found
                                                         DataObjectNotFoundException dnfe = 
                                                             new DataObjectNotFoundException (obj.getPrimaryFile ());
                                                         ErrorManager errMan = ErrorManager.getDefault ();
                                                         errMan.annotate( dnfe, obj.getClass().toString());
                                                         errMan.annotate( dnfe, file == null ? null : file.getPath());
                                                         throw dnfe;
                                                     }
                                                 }
                                             });

        return arr[0];
    }
    
    /** Writes the original DataObject into file of given name and extension.
     * Both parameters {@link name} and {@link ext} are ignored when the data file
     * is passed in as a {@link trg} parameter, in that case name and link can be <code>null</code>.
     * @param name name of the file to write original DataObject in
     * @param ext extension of the file to write original DataObject in
     * @param trg folder where FileObject of given name and ext will be created or
     * file which content is replaced
     * @param obj DataObject which link is stored into
     * @return the file with link
     * @exception IOException on I/O error
     */
    private static FileObject writeOriginal (
        final String name, final String ext, final FileObject trg, final DataObject obj
    ) throws IOException {
        try {
            return (FileObject) MUTEX.writeAccess (new Mutex.ExceptionAction () {
                public Object run () throws IOException {
                    FileObject fo;
                    if (trg.isData ()) {
                        fo = trg;
                    } else {
                         String n;
                         if (name == null) {
                             // #45810 - if obj is disk root then fix the filename
                             String baseName = obj.getName().replace(':', '_').replace('/', '_'); // NOI18N
                             n = FileUtil.findFreeFileName (trg, baseName, ext);
                         } else {
                             n = name;
                         }
                         fo = trg.createData (n, ext);
                    }
                    writeShadowFile(fo, obj.getPrimaryFile().getURL());
                    return fo;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException ();
        }
    }
    
    /** Overwrites existing file object with new link and fs.
     * @exception IOException on I/O error
     */
    static void writeOriginal (final FileObject shadow, final URL url) throws IOException {
        try {
            MUTEX.writeAccess (new Mutex.ExceptionAction () {
                public Object run () throws IOException {
                    writeShadowFile(shadow, url);
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException ();
        }
    }

    /**
     * Writes content of shadow file.
     * @param fo shadow file
     * @param url URL to original
     */
    private static void writeShadowFile(FileObject fo, URL url) throws IOException {
        FileLock lock = fo.lock();
        Writer os = new OutputStreamWriter(fo.getOutputStream(lock), "UTF-8");
        try {
            os.write(url.toExternalForm()); // NOI18N
        } finally {
            os.close();
            lock.releaseLock();
        }
    }
    
    /**
     * Tries to load the original file from a shadow.
     * Looks for file contents as well as the originalFile/originalFileSystem attributes.
     * @param fileObject a data shadow
     * @return the original <code>DataObject</code> referenced by the shadow
     * @throws IOException error during load or broken link
     */
    protected static DataObject deserialize(FileObject fileObject) throws IOException {
        String[] fileAndFileSystem = readOriginalFileAndFileSystem(fileObject);
        assert fileAndFileSystem[0] != null;
        FileObject target;
        URI u;
        try {
            u = new URI(fileAndFileSystem[0]);
        } catch (URISyntaxException e) {
            u = null;
        }
        if (u != null && u.isAbsolute()) {
            target = URLMapper.findFileObject(u.toURL());
        } else {
            FileSystem fs;
            if ("SystemFileSystem".equals(fileAndFileSystem[1])) { // NOI18N
                fs = Repository.getDefault().getDefaultFileSystem();
            } else {
                // Even if it is specified, we no longer have mounts, so we can no longer find it.
                fs = fileObject.getFileSystem();
            }
            target = fs.findResource(fileAndFileSystem[0]);
        }
        if (target != null) {
            return DataObject.find(target);
        } else {
            throw new FileNotFoundException(fileAndFileSystem[0] + ':' + fileAndFileSystem[1]);
        }
    }
    static URL readURL(FileObject fileObject) throws IOException {
        String[] fileAndFileSystem = readOriginalFileAndFileSystem(fileObject);
        assert fileAndFileSystem[0] != null;
        URI u;
        try {
            u = new URI(fileAndFileSystem[0]);
        } catch (URISyntaxException e) {
            u = null;
        }
        if (u != null && u.isAbsolute()) {
            return u.toURL();
        } else {
            FileSystem fs;
            if ("SystemFileSystem".equals(fileAndFileSystem[1])) { // NOI18N
                fs = Repository.getDefault().getDefaultFileSystem();
            } else {
                fs = fileObject.getFileSystem();
            }
            return new URL(fs.getRoot().getURL(), fileAndFileSystem[0]);
        }
    }
    private static String[] readOriginalFileAndFileSystem(final FileObject f) throws IOException {
        if ( f.getSize() == 0 ) {
            Object fileName = f.getAttribute("originalFile"); // NOI18N
            if ( fileName instanceof String ) {
                return new String[] {(String) fileName, (String) f.getAttribute("originalFileSystem")}; // NOI18N
            } else if (fileName instanceof URL) {
                return new String[] {((URL) fileName).toExternalForm(), null};
            } else {
                throw new FileNotFoundException(f.getPath());
            }
        } else {
            try {
                return (String[]) MUTEX.readAccess(new Mutex.ExceptionAction() {
                    public Object run() throws IOException {
                        BufferedReader ois = new BufferedReader(new InputStreamReader(f.getInputStream(), "UTF-8")); // NOI18N
                        try {
                            String s = ois.readLine();
                            String fs = ois.readLine();
                            return new String[] {s, fs};
                        } finally {
                            ois.close();
                        }
                    }
                });
            } catch (MutexException e) {
                throw (IOException) e.getException();
            }
        }
    }
    
    private FileObject checkOriginal (DataObject orig) throws IOException {
        if (orig == null)
            return null;
        return deserialize(getPrimaryFile()).getPrimaryFile();
    }

    /** Return the original shadowed object.
    * @return the data object
    */
    public DataObject getOriginal () {
        waitUpdatesProcessed();
        return original;
    }
    
    /** Implementation of Container interface.
     * @return array of one element, the original
     */
    public DataObject[] getChildren () {
        return new DataObject[] { original };
    }

    /* Creates node delegate.
    */
    protected Node createNodeDelegate () {
        return new ShadowNode (this);
    }

    /* Getter for delete action.
    * @return true if the object can be deleted
    */
    public boolean isDeleteAllowed () {
        return getPrimaryFile().canWrite();
    }

    /* Getter for copy action.
    * @return true if the object can be copied
    */
    public boolean isCopyAllowed ()  {
        return true;
    }

    /* Getter for move action.
    * @return true if the object can be moved
    */
    public boolean isMoveAllowed ()  {
        return getPrimaryFile().canWrite();
    }

    /* Getter for rename action.
    * @return true if the object can be renamed
    */
    public boolean isRenameAllowed () {
        return getPrimaryFile().canWrite();
    }

    /* Help context for this object.
    * @return help context
    */
    public HelpCtx getHelpCtx () {
        return original.getHelpCtx ();
    }

    /* Creates shadow for this object in specified folder. The current
    * implementation creates reference data shadow and pastes it into
    * specified folder.
    *
    * @param f the folder to create shortcut in
    * @return the shadow
    */
    protected DataShadow handleCreateShadow (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return original.handleCreateShadow (f);
    }

    /* Scans the orginal bundle */
    public Node.Cookie getCookie (Class c) {
        if (c.isInstance (this)) {
            return this;
        }
        return original.getCookie (this, c);
    }

    /* Try to refresh link to original file */
    public void refresh() {
        refresh(false);
    }
    
    private void refresh(boolean moved) {        
        try {
            /* Link isn't broken */            
            if (moved)
                tryUpdate();
            FileObject obj = checkOriginal(original);
            if (obj != null) {
                if (obj != this.original.getPrimaryFile ()) {
                    this.setOriginal (DataObject.find (obj));
                }
                return;
            }
        } catch (IOException e) {            
        }
        try {            
            /* Link is broken */
            this.setValid(false);            
        } catch (java.beans.PropertyVetoException e) {                        
        }         
    }
    
    private void tryUpdate() throws IOException {
        URL url = readURL(getPrimaryFile ());
        if (url.equals(original.getPrimaryFile().getURL())) {
            return;
        }
        writeOriginal (null, null, getPrimaryFile (), original);
    }
    
    private void setOriginal (DataObject o) {
        if (origL == null) {
            origL = new OrigL (this);
        }

        // set new original
        if (original != null) {
            original.removePropertyChangeListener (origL);
        }

        DataObject oldOriginal = original;
        
        o.addPropertyChangeListener (origL);
        original = o;

        // update nodes
        ShadowNode n [] = null;
        synchronized (nodes) {
            n = (ShadowNode [])nodes.toArray (new ShadowNode [nodes.size ()]);
        }
        
        try {
            for (int i = 0; i < n.length; i++) {
                n[i].originalChanged ();
            }
        }
        catch (IllegalStateException e) {
            System.out.println("Please reopen the bug #18998 if you see this message."); // NOI18N
            System.out.println("Old:"+oldOriginal + // NOI18N
                ((oldOriginal == null) ? "" : (" / " + oldOriginal.isValid() + " / " + System.identityHashCode(oldOriginal)))); // NOI18N
            System.out.println("New:"+original + // NOI18N
                ((original == null) ? "" : (" / " + original.isValid() + " / " + System.identityHashCode(original)))); // NOI18N
            throw e;
        }
    }
    
    private static RequestProcessor RP = new RequestProcessor("DataShadow validity check");
    private static volatile org.openide.util.Task lastTask = org.openide.util.Task.EMPTY;

    private static void updateShadowOriginal(DataShadow shadow) {
        class Updator implements Runnable {
            DataShadow sh;
            FileObject primary;
            
            public void run () {
                DataObject newOrig;

                try {
                    newOrig = DataObject.find (primary);
                } catch (DataObjectNotFoundException e) {
                    newOrig = null;
                }

                if (newOrig != null) {
                    sh.setOriginal (newOrig);
                } else {
                    checkValidity (new OperationEvent (sh.original));
                }
                
                primary = null;
                sh = null;
            }
        }
        Updator u = new Updator();
        u.sh = shadow;
        u.primary = u.sh.original.getPrimaryFile ();
        lastTask = RP.post(u, 100, Thread.MIN_PRIORITY + 1);
    }
    
    /** For use in tests that need to be sure that all updators have finished.
     */
    static final void waitUpdatesProcessed() {
        if (!RP.isRequestProcessorThread()) {
            lastTask.waitFinished();
        }
    }
    
    protected DataObject handleCopy (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return super.handleCopy(f);
    }
    
    protected FileObject handleMove (DataFolder f) throws IOException {
        if (original instanceof DataFolder) {
            DataFolder.testNesting(((DataFolder)original), f);
        }
        return super.handleMove(f);
    }

    private static class OrigL implements PropertyChangeListener {
        WeakReference shadow = null;
        public OrigL (DataShadow shadow) {
            this.shadow = new WeakReference (shadow);
        }
        public void propertyChange (PropertyChangeEvent evt) {
            final DataShadow shadow = (DataShadow) this.shadow.get ();

            if (shadow != null && DataObject.PROP_VALID.equals (evt.getPropertyName ())) {
                updateShadowOriginal(shadow);
            }
        }
    }

    /** Node for a shadow object. */
    protected static class ShadowNode extends FilterNode {
        /** message to create name of node */
        private static MessageFormat format;
        /** message to create short description of node */
        private static MessageFormat descriptionFormat;
        /** if true, the DataShadow name is used instead of original's name, 
         * affects DataShadows of filesystem roots only
         */
        private static final String ATTR_USEOWNNAME = "UseOwnName"; //NOI18N

        /** shadow */
        private DataShadow obj;

        /** the sheet computed for this node or null */
        private Sheet sheet;

        /** filesystem name property of original */
        private String originalFS;
        
        /** Create a shadowing node.
         * @param shadow the shadow
         */
        public ShadowNode (DataShadow shadow) {
            this (shadow, shadow.original.getNodeDelegate ());
        }

        /** Initializes it */
        private ShadowNode (DataShadow shadow, Node node) {
            super (node);
            this.obj = shadow;
            synchronized (this.obj.nodes) {
                this.obj.nodes.add (this);
            }
        }

        /* Clones the node
        */
        public Node cloneNode () {
            ShadowNode sn = new ShadowNode (obj);
            return sn;
        }

        /* Renames the shadow data object.
        * @param name new name for the object
        * @exception IllegalArgumentException if the rename failed
        */
        public void setName (String name) {
            try {
                if (!name.equals (obj.getName ())) {
                    obj.rename (name);
                    if (obj.original.getPrimaryFile ().isRoot ()) {
                        obj.getPrimaryFile ().setAttribute (ATTR_USEOWNNAME, Boolean.TRUE);
                    }
                    fireDisplayNameChange (null, null);
                    fireNameChange (null, null);
                }
            } catch (IOException ex) {
                throw new IllegalArgumentException (ex.getMessage ());
            }
        }

        /** The name of the shadow.
        * @return the name
        */
        public String getName () {
            return obj.getName ();
        }

        /* Creates name based on the original one.
        */
        public String getDisplayName () {
            if (format == null) {
                format = new MessageFormat (NbBundle.getBundle (DataShadow.class).getString ("FMT_shadowName"));
            }
            String n = format.format (createArguments ());
            try {
                return obj.getPrimaryFile().getFileSystem().getStatus().annotateName(n, obj.files());
            } catch (FileStateInvalidException fsie) {
                // ignore
                return n;
            }
        }
        public String getHtmlDisplayName() {
            if (format == null) {
                format = new MessageFormat(NbBundle.getBundle(DataShadow.class).getString("FMT_shadowName"));
            }
            try {
                String n = XMLUtil.toElementContent(format.format(createArguments()));
                FileSystem.Status s = obj.getPrimaryFile().getFileSystem().getStatus();
                if (s instanceof FileSystem.HtmlStatus) {
                    return ((FileSystem.HtmlStatus) s).annotateNameHtml(n, obj.files());
                }
            } catch (IOException e) {
                // ignore, OK
            }
            return null;
        }

        /** Creates arguments for given shadow node */
        private Object[] createArguments () {
            String origDisp;
            String shadowName = obj.getName ();
            if (obj.original.isValid()) {
                origDisp = obj.original.getNodeDelegate().getDisplayName();
            } else {
                // We will soon be a broken data shadow, in the meantime...
                origDisp = ""; // NOI18N
            }
            Boolean useOwnName = (Boolean)obj.getPrimaryFile ().getAttribute (ATTR_USEOWNNAME);
            if (obj.original.getPrimaryFile ().isRoot () && 
                (useOwnName == null || !useOwnName.booleanValue ())) {
                try {
                    shadowName = obj.original.getPrimaryFile ().getFileSystem ().getDisplayName ();
                } catch (FileStateInvalidException e) {
                    // ignore
                }
            }
            return new Object[] {
                       shadowName, // name of the shadow
                       super.getDisplayName (), // name of original
                       systemNameOrFileName (obj.getPrimaryFile ()), // full name of file for shadow
                       systemNameOrFileName (obj.original.getPrimaryFile ()), // full name of original file
                       origDisp, // display name of original
                   };
        }

        /** System name of file name
        */
        private static String systemNameOrFileName (FileObject fo) {
            return FileUtil.getFileDisplayName(fo);
        }

        /* Creates description based on the original one.
        */
        public String getShortDescription () {
            if (descriptionFormat == null) {
                descriptionFormat = new MessageFormat (
                                        NbBundle.getBundle (DataShadow.class).getString ("FMT_shadowHint")
                                    );
            }
            return descriptionFormat.format (createArguments ());
        }
        
        /* Show filesystem icon if it is a root.
         */
        public Image getIcon(int type) {
            Image i = rootIcon(type);
            if (i != null) {
                return i;
            } else {
                return super.getIcon(type);
            }
        }
        public Image getOpenedIcon(int type) {
            Image i = rootIcon(type);
            if (i != null) {
                return i;
            } else {
                return super.getOpenedIcon(type);
            }
        }
        private Image rootIcon(int type) {
            FileObject orig = obj.original.getPrimaryFile();
            if (orig.isRoot()) {
                try {
                    FileSystem fs = orig.getFileSystem();
                    try {
                        Image i = Introspector.getBeanInfo(fs.getClass()).getIcon(type);
                        return fs.getStatus().annotateIcon(i, type, obj.original.files());
                    } catch (IntrospectionException ie) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ie);
                        // ignore
                    }
                } catch (FileStateInvalidException fsie) {
                    // ignore
                }
            }
            return null;
        }

        /* @return obj.isDeleteAllowed () */
        public boolean canDestroy () {
            return obj.isDeleteAllowed ();
        }

        /* Destroyes the node
        */
        public void destroy () throws IOException {
            synchronized (obj.nodes) {
                obj.nodes.remove (this);
            }
            obj.delete ();
            //      super.destroy ();
        }

        /** @return true if shadow can be renamed
        */
        public final boolean canRename () {
            return obj.isRenameAllowed ();
        }

        /* Returns true if this object allows copying.
        * @returns true if so
        */
        public final boolean canCopy () {
            return obj.isCopyAllowed ();
        }

        /* Returns true if this object allows cutting.
        * @returns true if so
        */
        public final boolean canCut () {
            return obj.isMoveAllowed ();
        }

        /* First of all the DataObject.getCookie method is
        * called. If it produces non-null result, it is returned.
        * Otherwise the value returned from super.getCookie
        * method is returned.
        *
        * @return the cookie or null
        */
        public Node.Cookie getCookie (Class cl) {
            Node.Cookie c = obj.getCookie (cl);
            if (c != null) {
                return c;
            } else {
                return super.getCookie (cl);
            }
        }

        /** Returns modified properties of the original node.
        * @return property sets 
        */
        public PropertySet[] getPropertySets () {
            Sheet s = sheet;
            if (s == null) {
                s = sheet = cloneSheet ();
            }
            return s.toArray ();
        }

        /** Copy this node to the clipboard.
        *
        * @return {@link org.openide.util.datatransfer.ExTransferable.Single} with one copy flavor
        * @throws IOException if it could not copy
        * @see NodeTransfer
        */
        public Transferable clipboardCopy () throws IOException {
            ExTransferable t = ExTransferable.create (super.clipboardCopy ());
            t.put (LoaderTransfer.transferable (
                obj, 
                LoaderTransfer.CLIPBOARD_COPY)
            );
            return t;
        }

        /** Cut this node to the clipboard.
        *
        * @return {@link org.openide.util.datatransfer.ExTransferable.Single} with one cut flavor
        * @throws IOException if it could not cut
        * @see NodeTransfer
        */
        public Transferable clipboardCut () throws IOException {
            ExTransferable t = ExTransferable.create (super.clipboardCut ());
            t.put (LoaderTransfer.transferable (
                obj, 
                LoaderTransfer.CLIPBOARD_CUT)
            );
            return t;
        }
        /**
        * This implementation only calls clipboardCopy supposing that 
        * copy to clipboard and copy by d'n'd are similar.
        *
        * @return transferable to represent this node during a drag
        * @exception IOException when the
        *    cut cannot be performed
        */
        public Transferable drag () throws IOException {
            return clipboardCopy ();
        }

        /** Creates a node listener that allows listening on the
        * original node and propagating events to the proxy.
        * <p>Intended for overriding by subclasses, as with {@link #createPropertyChangeListener}.
        *
        * @return a {@link org.openide.nodes.FilterNode.NodeAdapter} in the default implementation
        */
        protected org.openide.nodes.NodeListener createNodeListener () {
            return new PropL (this);
        }

        /** Equal if the o is ShadowNode to the same shadow object.
        */
        public boolean equals (Object o) {
            if (o instanceof ShadowNode) {
                ShadowNode sn = (ShadowNode)o;
                return sn.obj == obj;
            }
            return false;
        }

        /** Hashcode is computed by the represented shadow.
        */
        public int hashCode () {
            return obj.hashCode ();
        }


        /** Clones the property sheet of original node.
        */
        private Sheet cloneSheet () {
            PropertySet[] sets = this.getOriginal().getPropertySets ();

            Sheet s = new Sheet ();
            for (int i = 0; i < sets.length; i++) {
                Sheet.Set ss = new Sheet.Set ();
                ss.put (sets[i].getProperties ());
                ss.setName (sets[i].getName ());
                ss.setDisplayName (sets[i].getDisplayName ());
                ss.setShortDescription (sets[i].getShortDescription ());

                // modifies the set if it contains name of object property
                modifySheetSet (ss);

                s.put (ss);
            }

            return s;
        }

        /** Modifies the sheet set to contain name of property and name of
        * original object.
        */
        private void modifySheetSet (Sheet.Set ss) {
            Property p = ss.remove (DataObject.PROP_NAME);
            if (p != null) {
                p = new PropertySupport.Name (this);
                ss.put (p);

                p = new Name ();
                ss.put (p);
            }
        }

        private void originalChanged () {
            DataObject ori = obj.original;
            if (ori.isValid()) {
                changeOriginal (ori.getNodeDelegate(), true);
            } else {
                updateShadowOriginal(obj);
            }
        }

        /** Class that renames the orginal object and also updates
        * the link
        */
        private final class Name extends PropertySupport.ReadWrite {
            public Name () {
                super (
                    "OriginalName", // NOI18N
                    String.class,
                    DataObject.getString ("PROP_ShadowOriginalName"),
                    DataObject.getString ("HINT_ShadowOriginalName")
                );
            }

            public Object getValue () {
                return obj.original.getName();
            }

            public void setValue (Object val) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
                if (!canWrite())
                    throw new IllegalAccessException();
                if (!(val instanceof String))
                    throw new IllegalArgumentException();

                try {
                    DataObject orig = obj.original;
                    orig.rename ((String)val);
                    writeOriginal (null, null, obj.getPrimaryFile (), orig);
                } catch (IOException ex) {
                    throw new InvocationTargetException (ex);
                }
            }

            public boolean canWrite () {
                return obj.original.isRenameAllowed();
            }
        }
        
        /** Property listener on data object that delegates all changes of
        * properties to this node.
        */
        private static class PropL extends FilterNode.NodeAdapter {
            public PropL (ShadowNode sn) {
                super (sn);
            }

            protected void propertyChange (FilterNode fn, PropertyChangeEvent ev) {
              if (Node.PROP_PROPERTY_SETS.equals(ev.getPropertyName ())) {
                // clear the sheet
                ShadowNode sn = (ShadowNode)fn;
                sn.sheet = null;
              }
              
              super.propertyChange (fn, ev);
              }
        }
    }
    
    static final class DSWeakReference extends WeakReference 
    implements Runnable {
        private int hash;
        private FileObject original;
        
        DSWeakReference(DataObject o) {
            super(o, org.openide.util.Utilities.activeReferenceQueue());
            this.hash = o.hashCode();
            if (o instanceof DataShadow) {
                DataShadow s = (DataShadow)o;
                this.original = s.original.getPrimaryFile ();
            }
        }
        
        public int hashCode() {
            return hash;
        }
        
        public boolean equals(Object o) {
            Object mine = get();
            if (mine == null) {
                return false;
            }
            
            if (o instanceof DSWeakReference) {
                DSWeakReference him = (DSWeakReference) o;
                return mine.equals(him.get());
            }
            
            return false;
        }

        public void run() {
            if (original != null) {
                synchronized (getDataShadowsSet ()) {
                    getDataShadowsSet().remove(original);
                }            
            } else {
                synchronized (BrokenDataShadow.getDataShadowsSet()) {
                    BrokenDataShadow.getDataShadowsSet().remove(this);
                }
            }
        }
    }
}
