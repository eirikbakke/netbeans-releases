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

package org.netbeans.modules.web.project.test;

import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.XMLFileSystem;
import org.xml.sax.SAXException;

/**
 * Repository whose getDefaultFileSystem() returns a writeable FS containing
 * the layer of the websvc/jaxwsmodel. It is put in the default lookup,
 * thus it is returned by Repository.getDefault().
 *
 * @author Andrei Badea, Radko Najman
 */
public class RepositoryImpl extends Repository {
    
    private XMLFileSystem system;
    
    public RepositoryImpl() {
        super(createDefFs());
    }
    
    private static FileSystem createDefFs() {
        try
        {
            FileSystem writeFs = FileUtil.createMemoryFileSystem();
            FileSystem layerWebProject = new XMLFileSystem(RepositoryImpl.class.getClassLoader().getResource("org/netbeans/modules/websvc/jaxwsmodel/resources/layer.xml"));
            return new MultiFileSystem(new FileSystem[] {writeFs, layerWebProject});
        } catch (SAXException e) {
            return null;
        }
    }
    
}
