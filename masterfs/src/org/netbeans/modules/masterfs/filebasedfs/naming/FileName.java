/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.masterfs.filebasedfs.naming;


import java.io.File;
import java.io.IOException;
import org.netbeans.modules.masterfs.filebasedfs.utils.FileChangedManager;
import org.netbeans.modules.masterfs.providers.ProvidedExtensions;
import org.openide.util.CharSequences;

/**
 * @author Radek Matous
 */
public class FileName implements FileNaming {
    private final CharSequence name;
    private final FileNaming parent;
    private final Integer id;
    private CharSequence currentName;

    protected FileName(final FileNaming parent, final File file) {
        this.parent = parent;
        this.name = CharSequences.create(parseName(parent, file));
        id = NamingFactory.createID(file);
        this.currentName = name;
    }

    private static String parseName(final FileNaming parent, final File file) {
        return parent == null ? file.getPath() : file.getName();
    }

    @Override
    public FileNaming rename(String name, ProvidedExtensions.IOHandler handler) throws IOException {
        boolean success = false;
        final File f = getFile();

        if (FileChangedManager.getInstance().exists(f)) {
            File newFile = new File(f.getParentFile(), name);
            if (handler != null) {
                handler.handle();
                success = true;
            } else {
                success = f.renameTo(newFile);
            }
            if (success) {
                FolderName.freeCaches();
                return NamingFactory.fromFile(getParent(), newFile, true);
            }
        }
        return this;
    }

    public final boolean isRoot() {
        return (getParent() == null);
    }


    public File getFile() {
        final FileNaming myParent = this.getParent();
        return (myParent != null) ? new File(myParent.getFile(), getName()) : new File(getName());
    }


    public final String getName() {
        return currentName.toString();
    }

    public FileNaming getParent() {
        return parent;
    }

    public final Integer getId() {
        return id;
    }

    public final boolean equals(final Object obj) {
        if (obj instanceof FileName ) {
            FileName fn = (FileName)obj;
            if (obj.hashCode() != hashCode()) {
                return false;
            }
            if (!name.equals(fn.name)) {
                return false;
            }
            if (parent == null || fn.parent == null) {
                return parent == null && fn.parent == null;
            }
            return parent.equals(fn.parent);
        }
        return (obj instanceof FileNaming && obj.hashCode() == hashCode());
    }


    public final String toString() {
        return getFile().getAbsolutePath();
    }

    public final int hashCode() {
        return id.intValue();
    }

    public boolean isFile() {
        return true;
    }

    public boolean isDirectory() {
        return !isFile();
    }

    void updateCase(String name) {
        assert String.CASE_INSENSITIVE_ORDER.compare(name, this.name.toString()) == 0: "Only case can be changed. Was: " + this.name + " name: " + name;
        this.currentName = CharSequences.create(name);
    }
}
