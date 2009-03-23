/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.tasklist.impl;

import java.io.IOException;
import java.util.Collection;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author sa
 */
public class TaskIndexerFactory extends CustomIndexerFactory {

    public TaskIndexerFactory() {
        System.out.println("factory created");
    }

    @Override
    public String getIndexerName() {
        return "TaskListIndexer";
    }

    @Override
    public int getIndexVersion() {
        return 1;
    }

    @Override
    public CustomIndexer createIndexer() {
        return new TaskIndexer( TaskManagerImpl.getInstance().getTasks() );
    }

    @Override
    public void filesDeleted(Collection<? extends Indexable> deleted, Context context) {
        try {
            IndexingSupport is = IndexingSupport.getInstance(context);
            for( Indexable idx : deleted ) {
                System.out.println("deleted: " + idx.getURL().toString());
                is.removeDocuments(idx);
                //TODO remove files from task list
            }
        } catch( IOException ex ) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void filesDirty(Collection<? extends Indexable> dirty, Context context) {
        try {
            IndexingSupport is = IndexingSupport.getInstance(context);
            for( Indexable idx : dirty ) {
                System.out.println("dirty: " + idx.getURL().toString());
                is.markDirtyDocuments(idx);

                //TODO remove files from task list
            }
        } catch( IOException ex ) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean supportsEmbeddedIndexers() {
        return true;
    }

}
