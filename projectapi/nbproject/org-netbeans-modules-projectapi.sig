#Signature file v4.0
#Version 

CLSS public java.lang.Object
cons public Object()
meth protected java.lang.Object clone() throws java.lang.CloneNotSupportedException
meth protected void finalize() throws java.lang.Throwable
meth public boolean equals(java.lang.Object)
meth public final java.lang.Class<?> getClass()
meth public final void notify()
meth public final void notifyAll()
meth public final void wait() throws java.lang.InterruptedException
meth public final void wait(long) throws java.lang.InterruptedException
meth public final void wait(long,int) throws java.lang.InterruptedException
meth public int hashCode()
meth public java.lang.String toString()

CLSS public org.netbeans.api.project.FileOwnerQuery
fld public final static int EXTERNAL_ALGORITHM_TRANSIENT = 0
meth public static org.netbeans.api.project.Project getOwner(java.net.URI)
meth public static org.netbeans.api.project.Project getOwner(org.openide.filesystems.FileObject)
meth public static void markExternalOwner(java.net.URI,org.netbeans.api.project.Project,int)
meth public static void markExternalOwner(org.openide.filesystems.FileObject,org.netbeans.api.project.Project,int)
supr java.lang.Object
hfds LOG,cache,implementations

CLSS public abstract interface org.netbeans.api.project.Project
intf org.openide.util.Lookup$Provider
meth public abstract org.openide.filesystems.FileObject getProjectDirectory()
meth public abstract org.openide.util.Lookup getLookup()

CLSS public abstract interface org.netbeans.api.project.ProjectInformation
fld public final static java.lang.String PROP_DISPLAY_NAME = "displayName"
fld public final static java.lang.String PROP_ICON = "icon"
fld public final static java.lang.String PROP_NAME = "name"
meth public abstract java.lang.String getDisplayName()
meth public abstract java.lang.String getName()
meth public abstract javax.swing.Icon getIcon()
meth public abstract org.netbeans.api.project.Project getProject()
meth public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener)

CLSS public final org.netbeans.api.project.ProjectManager
meth public boolean isModified(org.netbeans.api.project.Project)
meth public boolean isProject(org.openide.filesystems.FileObject)
meth public boolean isValid(org.netbeans.api.project.Project)
meth public java.util.Set<org.netbeans.api.project.Project> getModifiedProjects()
meth public org.netbeans.api.project.Project findProject(org.openide.filesystems.FileObject) throws java.io.IOException
meth public static org.netbeans.api.project.ProjectManager getDefault()
meth public static org.openide.util.Mutex mutex()
meth public void clearNonProjectCache()
meth public void saveAllProjects() throws java.io.IOException
meth public void saveProject(org.netbeans.api.project.Project) throws java.io.IOException
supr java.lang.Object
hfds DEFAULT,LOG,MUTEX,dir2Proj,factories,loadingThread,modifiedProjects,proj2Factory,projectDeletionListener,removedProjects
hcls LoadStatus,ProjectDeletionListener,ProjectStateImpl

CLSS public org.netbeans.api.project.ProjectUtils
meth public static boolean hasSubprojectCycles(org.netbeans.api.project.Project,org.netbeans.api.project.Project)
meth public static org.netbeans.api.project.ProjectInformation getInformation(org.netbeans.api.project.Project)
meth public static org.netbeans.api.project.Sources getSources(org.netbeans.api.project.Project)
supr java.lang.Object
hcls BasicInformation

CLSS public abstract interface org.netbeans.api.project.SourceGroup
fld public final static java.lang.String PROP_CONTAINERSHIP = "containership"
meth public abstract boolean contains(org.openide.filesystems.FileObject)
meth public abstract java.lang.String getDisplayName()
meth public abstract java.lang.String getName()
meth public abstract javax.swing.Icon getIcon(boolean)
meth public abstract org.openide.filesystems.FileObject getRootFolder()
meth public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener)

CLSS public abstract interface org.netbeans.api.project.Sources
fld public final static java.lang.String TYPE_GENERIC = "generic"
meth public abstract org.netbeans.api.project.SourceGroup[] getSourceGroups(java.lang.String)
meth public abstract void addChangeListener(javax.swing.event.ChangeListener)
meth public abstract void removeChangeListener(javax.swing.event.ChangeListener)

CLSS public abstract interface org.netbeans.spi.project.ActionProvider
fld public final static java.lang.String COMMAND_BUILD = "build"
fld public final static java.lang.String COMMAND_CLEAN = "clean"
fld public final static java.lang.String COMMAND_COMPILE_SINGLE = "compile.single"
fld public final static java.lang.String COMMAND_COPY = "copy"
fld public final static java.lang.String COMMAND_DEBUG = "debug"
fld public final static java.lang.String COMMAND_DEBUG_SINGLE = "debug.single"
fld public final static java.lang.String COMMAND_DEBUG_STEP_INTO = "debug.stepinto"
fld public final static java.lang.String COMMAND_DEBUG_TEST_SINGLE = "debug.test.single"
fld public final static java.lang.String COMMAND_DELETE = "delete"
fld public final static java.lang.String COMMAND_MOVE = "move"
fld public final static java.lang.String COMMAND_REBUILD = "rebuild"
fld public final static java.lang.String COMMAND_RENAME = "rename"
fld public final static java.lang.String COMMAND_RUN = "run"
fld public final static java.lang.String COMMAND_RUN_SINGLE = "run.single"
fld public final static java.lang.String COMMAND_TEST = "test"
fld public final static java.lang.String COMMAND_TEST_SINGLE = "test.single"
meth public abstract boolean isActionEnabled(java.lang.String,org.openide.util.Lookup)
meth public abstract java.lang.String[] getSupportedActions()
meth public abstract void invokeAction(java.lang.String,org.openide.util.Lookup)

CLSS public abstract interface org.netbeans.spi.project.AuxiliaryConfiguration
meth public abstract boolean removeConfigurationFragment(java.lang.String,java.lang.String,boolean)
meth public abstract org.w3c.dom.Element getConfigurationFragment(java.lang.String,java.lang.String,boolean)
meth public abstract void putConfigurationFragment(org.w3c.dom.Element,boolean)

CLSS public abstract interface org.netbeans.spi.project.CacheDirectoryProvider
meth public abstract org.openide.filesystems.FileObject getCacheDirectory() throws java.io.IOException

CLSS public abstract interface org.netbeans.spi.project.CopyOperationImplementation
intf org.netbeans.spi.project.DataFilesProviderImplementation
meth public abstract void notifyCopied(org.netbeans.api.project.Project,java.io.File,java.lang.String) throws java.io.IOException
meth public abstract void notifyCopying() throws java.io.IOException

CLSS public abstract interface org.netbeans.spi.project.DataFilesProviderImplementation
meth public abstract java.util.List<org.openide.filesystems.FileObject> getDataFiles()
meth public abstract java.util.List<org.openide.filesystems.FileObject> getMetadataFiles()

CLSS public abstract interface org.netbeans.spi.project.DeleteOperationImplementation
intf org.netbeans.spi.project.DataFilesProviderImplementation
meth public abstract void notifyDeleted() throws java.io.IOException
meth public abstract void notifyDeleting() throws java.io.IOException

CLSS public abstract interface org.netbeans.spi.project.FileOwnerQueryImplementation
meth public abstract org.netbeans.api.project.Project getOwner(java.net.URI)
meth public abstract org.netbeans.api.project.Project getOwner(org.openide.filesystems.FileObject)

CLSS public abstract interface org.netbeans.spi.project.LookupMerger<%0 extends java.lang.Object>
meth public abstract java.lang.Class<{org.netbeans.spi.project.LookupMerger%0}> getMergeableClass()
meth public abstract {org.netbeans.spi.project.LookupMerger%0} merge(org.openide.util.Lookup)

CLSS public abstract interface org.netbeans.spi.project.LookupProvider
meth public abstract org.openide.util.Lookup createAdditionalLookup(org.openide.util.Lookup)

CLSS public abstract interface org.netbeans.spi.project.MoveOperationImplementation
intf org.netbeans.spi.project.DataFilesProviderImplementation
meth public abstract void notifyMoved(org.netbeans.api.project.Project,java.io.File,java.lang.String) throws java.io.IOException
meth public abstract void notifyMoving() throws java.io.IOException

CLSS public abstract interface org.netbeans.spi.project.ProjectConfiguration
meth public abstract java.lang.String getDisplayName()

CLSS public abstract interface org.netbeans.spi.project.ProjectConfigurationProvider<%0 extends org.netbeans.spi.project.ProjectConfiguration>
fld public final static java.lang.String PROP_CONFIGURATIONS = "configurations"
fld public final static java.lang.String PROP_CONFIGURATION_ACTIVE = "activeConfiguration"
meth public abstract boolean configurationsAffectAction(java.lang.String)
meth public abstract boolean hasCustomizer()
meth public abstract java.util.Collection<{org.netbeans.spi.project.ProjectConfigurationProvider%0}> getConfigurations()
meth public abstract void addPropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void customize()
meth public abstract void removePropertyChangeListener(java.beans.PropertyChangeListener)
meth public abstract void setActiveConfiguration({org.netbeans.spi.project.ProjectConfigurationProvider%0}) throws java.io.IOException
meth public abstract {org.netbeans.spi.project.ProjectConfigurationProvider%0} getActiveConfiguration()

CLSS public abstract interface org.netbeans.spi.project.ProjectFactory
meth public abstract boolean isProject(org.openide.filesystems.FileObject)
meth public abstract org.netbeans.api.project.Project loadProject(org.openide.filesystems.FileObject,org.netbeans.spi.project.ProjectState) throws java.io.IOException
meth public abstract void saveProject(org.netbeans.api.project.Project) throws java.io.IOException

CLSS public abstract interface org.netbeans.spi.project.ProjectState
meth public abstract void markModified()
meth public abstract void notifyDeleted()

CLSS public abstract interface org.netbeans.spi.project.SubprojectProvider
meth public abstract java.util.Set<? extends org.netbeans.api.project.Project> getSubprojects()
meth public abstract void addChangeListener(javax.swing.event.ChangeListener)
meth public abstract void removeChangeListener(javax.swing.event.ChangeListener)

CLSS public org.netbeans.spi.project.support.GenericSources
meth public static org.netbeans.api.project.SourceGroup group(org.netbeans.api.project.Project,org.openide.filesystems.FileObject,java.lang.String,java.lang.String,javax.swing.Icon,javax.swing.Icon)
meth public static org.netbeans.api.project.Sources genericOnly(org.netbeans.api.project.Project)
supr java.lang.Object
hcls GenericOnlySources,Group

CLSS public final org.netbeans.spi.project.support.LookupProviderSupport
meth public static org.netbeans.spi.project.LookupMerger<org.netbeans.api.project.Sources> createSourcesMerger()
meth public static org.openide.util.Lookup createCompositeLookup(org.openide.util.Lookup,java.lang.String)
supr java.lang.Object
hcls DelegatingLookupImpl,SourcesImpl,SourcesMerger

CLSS public final org.netbeans.spi.project.support.ProjectOperations
meth public static boolean isCopyOperationSupported(org.netbeans.api.project.Project)
meth public static boolean isDeleteOperationSupported(org.netbeans.api.project.Project)
meth public static boolean isMoveOperationSupported(org.netbeans.api.project.Project)
meth public static java.util.List<org.openide.filesystems.FileObject> getDataFiles(org.netbeans.api.project.Project)
meth public static java.util.List<org.openide.filesystems.FileObject> getMetadataFiles(org.netbeans.api.project.Project)
meth public static void notifyCopied(org.netbeans.api.project.Project,org.netbeans.api.project.Project,java.io.File,java.lang.String) throws java.io.IOException
meth public static void notifyCopying(org.netbeans.api.project.Project) throws java.io.IOException
meth public static void notifyDeleted(org.netbeans.api.project.Project) throws java.io.IOException
meth public static void notifyDeleting(org.netbeans.api.project.Project) throws java.io.IOException
meth public static void notifyMoved(org.netbeans.api.project.Project,org.netbeans.api.project.Project,java.io.File,java.lang.String) throws java.io.IOException
meth public static void notifyMoving(org.netbeans.api.project.Project) throws java.io.IOException
supr java.lang.Object

CLSS public abstract org.openide.util.Lookup
cons public Lookup()
fld public final static org.openide.util.Lookup EMPTY
innr public abstract interface static Provider
innr public abstract static Item
innr public abstract static Result
innr public final static Template
meth public <%0 extends java.lang.Object> java.util.Collection<? extends {%%0}> lookupAll(java.lang.Class<{%%0}>)
meth public <%0 extends java.lang.Object> org.openide.util.Lookup$Item<{%%0}> lookupItem(org.openide.util.Lookup$Template<{%%0}>)
meth public <%0 extends java.lang.Object> org.openide.util.Lookup$Result<{%%0}> lookupResult(java.lang.Class<{%%0}>)
meth public abstract <%0 extends java.lang.Object> org.openide.util.Lookup$Result<{%%0}> lookup(org.openide.util.Lookup$Template<{%%0}>)
meth public abstract <%0 extends java.lang.Object> {%%0} lookup(java.lang.Class<{%%0}>)
meth public static org.openide.util.Lookup getDefault()
supr java.lang.Object
hfds defaultLookup
hcls DefLookup,Empty

CLSS public abstract interface static org.openide.util.Lookup$Provider
meth public abstract org.openide.util.Lookup getLookup()

