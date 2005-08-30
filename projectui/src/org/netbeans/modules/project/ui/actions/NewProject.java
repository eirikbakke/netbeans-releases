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

package org.netbeans.modules.project.ui.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.project.ui.NewProjectWizard;
import org.netbeans.modules.project.ui.OpenProjectList;
import org.netbeans.modules.project.ui.ProjectUtilities;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

public class NewProject extends BasicAction {
        
    private static final Icon ICON = new ImageIcon( Utilities.loadImage( "org/netbeans/modules/project/ui/resources/newProject.gif" ) ); //NOI18N    
    private static final String NAME = NbBundle.getMessage( NewProject.class, "LBL_NewProjectAction_Name" ); // NOI18N
    
    private boolean isPreselect = false;
    
    private RequestProcessor.Task bodyTask;

    public NewProject() {
        super( NAME, ICON );
        putValue("iconBase","org/netbeans/modules/project/ui/resources/newProject.gif"); //NOI18N
        bodyTask = new RequestProcessor( "NewProjectBody" ).create( new Runnable () { // NOI18N
            public void run () {
                doPerform ();
            }
        });
    }
    
    public static NewProject newSample() {
        NewProject np = new NewProject();
        np.setDisplayName( "New Sample" ); 
        np.isPreselect = true;
        return np;
    }

    public void actionPerformed( ActionEvent evt ) {
        bodyTask.schedule( 0 );
        
        if ( "waitFinished".equals( evt.getActionCommand() ) ) {
            bodyTask.waitFinished();
        }
    }    
        
    /*T9Y*/ NewProjectWizard prepareWizardDescriptor(FileObject fo) {
        NewProjectWizard wizard = new NewProjectWizard(fo);
            
        if ( isPreselect ) {
            // XXX make the properties public ?
            wizard.putProperty( "PRESELECT_CATEGORY", getValue ("PRESELECT_CATEGORY")); 
            wizard.putProperty( "PRESELECT_TEMPLATE", getValue ("PRESELECT_TEMPLATE")); 
        }
        else {
            wizard.putProperty( "PRESELECT_CATEGORY", null ); 
            wizard.putProperty( "PRESELECT_TEMPLATE", null ); 
        }

        FileObject folder = (FileObject) getValue(CommonProjectActions.EXISTING_SOURCES_FOLDER);
        if (folder != null) {
            wizard.putProperty(CommonProjectActions.EXISTING_SOURCES_FOLDER, folder);
        }
        return wizard;
    }
    
    private void doPerform () {
        
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource( "Templates/Project" ); //NOI18N                
        NewProjectWizard wizard = prepareWizardDescriptor(fo);
        
        try {
                        
            final Set newObjects = wizard.instantiate ();            
            Object mainProperty = wizard.getProperty( /* XXX Define somewhere */ "setAsMain" ); // NOI18N
            boolean setFirstMain = true;
            if ( mainProperty instanceof Boolean ) {
                setFirstMain = ((Boolean)mainProperty).booleanValue();
            }
            final boolean setFirstMainFinal = setFirstMain; 
            
            SwingUtilities.invokeLater( new Runnable() {
            
                public void run() {
                    ProjectUtilities.WaitCursor.show();
                    
                    if ( newObjects != null && !newObjects.isEmpty() ) { 
                        // First. Open all returned projects in the GUI.

                        LinkedList filesToOpen = new LinkedList();

                        Project p = null;        
                        for( Iterator it = newObjects.iterator(); it.hasNext(); ) {
                            Object obj = it.next ();
                            FileObject newFo;
                            DataObject newDo;
                            if (obj instanceof DataObject) {
                                newDo = (DataObject) obj;
                                newFo = newDo.getPrimaryFile();
                            } else if (obj instanceof FileObject) {
                                newFo = (FileObject) obj;
                                try {
                                    newDo = DataObject.find(newFo);
                                } catch (DataObjectNotFoundException e) {
                                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                                    continue;
                                }
                            } else {
                                ErrorManager.getDefault().log(ErrorManager.WARNING, "Found unrecognized object " + obj + " in result set from instantiate()");
                                continue;
                            }
                            boolean mainProjectSet = false;
                            // check if it's a project directory
                            if (newFo.isFolder()) {
                                try {
                                    p = ProjectManager.getDefault().findProject(newFo);
                                    if (p != null) {
                                        // It is a project, so open it
                                        OpenProjectList.getDefault().open(p);
                                        if (setFirstMainFinal && !mainProjectSet) {
                                            OpenProjectList.getDefault().setMainProject( p );
                                            mainProjectSet = true;
                                        }
                                    } else {
                                        // Just a folder to expand
                                        filesToOpen.add(newDo);
                                    }
                                } catch (IOException e) {
                                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                                    continue;
                                }
                            } else {
                                filesToOpen.add(newDo);
                            }
                        }
                        
                        // Show the project tab to show the user we did something
                        ProjectUtilities.makeProjectTabVisible( true );
                        
                        // Second open the files                
                        if ( filesToOpen.isEmpty() && p != null) {
                            // Just select and expand the project node
                            ProjectUtilities.selectAndExpandProject( p );
                        }
                        else {
                            for( Iterator it = filesToOpen.iterator(); it.hasNext(); ) { // Open the files
                                ProjectUtilities.openAndSelectNewObject( (DataObject)it.next() );
                            }
                        }                                                

                    }
                    ProjectUtilities.WaitCursor.hide();
                }
            } );
        }
        catch ( IOException e ) {
            ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, e );
        }
        
        
    }
    
}