/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.apt.impl.support.clank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.clang.basic.vfs.File;
import org.clang.basic.vfs.Status;
import org.clang.basic.vfs.directory_iterator;
import org.clank.java.std;
import org.clank.java.std_errors;
import org.clank.java.std_ptr;
import org.clank.support.NativePointer;
import org.llvm.adt.StringRef;
import org.llvm.adt.Twine;
import org.llvm.support.ErrorOr;
import org.llvm.support.MemoryBuffer;
import org.llvm.support.llvm;
import org.llvm.support.sys.TimeValue;
import org.llvm.support.sys.fs;
import org.llvm.support.sys.fs.UniqueID;
import org.llvm.support.sys.fs.file_type;
import org.llvm.support.sys.fs.perms;
import org.netbeans.modules.cnd.spi.utils.CndFileSystemProvider;
import org.netbeans.modules.cnd.utils.CndUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;


/**
 *
 * @author vkvashin
 */
public class ClankFileObjectBasedFileSystem extends org.clang.basic.vfs.FileSystem {

    private static final boolean TRACE_TIME = Boolean.getBoolean("clank.fs.trace.time"); //NOI18N
    private static final AtomicLong totalReadTime  = TRACE_TIME ? new AtomicLong() : null;
    private static final AtomicLong totalReadCount = TRACE_TIME ? new AtomicLong() : null;
    private static final AtomicLong totalStatTime  = TRACE_TIME ? new AtomicLong() : null;
    private static final AtomicLong totalStatCount = TRACE_TIME ? new AtomicLong() : null;

    private final org.openide.filesystems.FileSystem nbFS;
    
    public ClankFileObjectBasedFileSystem(org.openide.filesystems.FileSystem nbFS) {
        this.nbFS = nbFS;
    }

    private static void printStatistics(std.string Name, long readTime) {
        assert TRACE_TIME;
        StringBuilder sb = new StringBuilder();
        sb.append("Reading ").append(Name).append(" took ").append(readTime).append(" ms") //NOI18N
                .append("\nTotal reads: count=").append(totalReadCount.get()).append(" time=").append(totalReadTime.get()).append(" ms") //NOI18N
                .append("\nTotal stats: count=").append(totalStatCount.get()).append(" time=").append(totalStatTime.get()).append(" ms") //NOI18N
                .append("\n"); //NOI18N
        llvm.errs().$out(sb.toString());
    }

    public void destroy() {        
    }

    /// \brief Get the status of the entry at \p Path, if one exists.
    //<editor-fold defaultstate="collapsed" desc="clang::vfs::FileSystem::status">
    @Override
    public ErrorOr<Status> status(Twine Path) {
        long time = TRACE_TIME ? System.currentTimeMillis() : 0;
        FileObject fo = getFileObject(Path);
        ErrorOr<Status> status = getStatus(fo);
        if (TRACE_TIME) {
            totalStatTime.addAndGet(System.currentTimeMillis() - time);
            totalStatCount.incrementAndGet();
        }
        return status;
    }

    @Override
    public ErrorOr<std_ptr.unique_ptr<File>> openFileForRead(Twine Path) {
        long time = TRACE_TIME ? System.currentTimeMillis() : 0;
        ErrorOr<std_ptr.unique_ptr<File>> result;
        FileObject fo = getFileObject(Path);
        if (fo == null || !fo.isValid()) {
            result = new ErrorOr(std_errors.errc.no_such_file_or_directory);
        } else {
            ClankFileObjectBasedFile file = new ClankFileObjectBasedFile(fo);
            result = new ErrorOr(new std_ptr.unique_ptr<File>(file));
        }
        if (TRACE_TIME) {
            totalReadTime.addAndGet(System.currentTimeMillis() - time);
            totalReadCount.incrementAndGet();
        }
        return result;
    }

    @Override
    public directory_iterator dir_begin(Twine Dir, std_errors.error_code EC) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: implement?
    }


    private static String toCharSequence(Twine twine) {
        std.string str = twine.str();
        return str.toJavaString();
    }
    
    private void assertFS(FileObject fo) {
        try {
            CndUtils.assertTrue(fo.getFileSystem() == nbFS);
        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private UniqueID getUniqueID(FileObject fo) {
        assertFS(fo);
        CndFileSystemProvider.CndStatInfo stat = CndFileSystemProvider.getStatInfo(fo);
        if (stat.isValid()) {
            return new fs.UniqueID(stat.device, stat.inode);
        } else {
            return new fs.UniqueID(System.identityHashCode(nbFS), System.identityHashCode(fo));
        }
    }

    private FileObject getFileObject(Twine Path) {
        String strPath = toCharSequence(Path);
        FileObject fo = nbFS.findResource(strPath);
        return fo;
    }

    private ErrorOr<Status> getStatus(ClankFileObjectBasedFile file) {
        long time = TRACE_TIME ? System.currentTimeMillis() : 0;
        ErrorOr<Status> result = getStatus(file.getFileObject());
        if (TRACE_TIME) {
            totalStatTime.addAndGet(System.currentTimeMillis() - time);
            totalStatCount.incrementAndGet();
        }
        return result;
    }

    private ErrorOr<Status> getStatus(FileObject fo) {
        if (fo == null || !fo.isValid()) {
            // TODO: should we set errno?
            return new ErrorOr(std_errors.errc.no_such_file_or_directory);
        } else {
            StringRef name = new StringRef(fo.getPath());
            UniqueID uid = getUniqueID(fo);
            long ms = fo.lastModified().getTime();
            TimeValue time = new TimeValue(ms/1000, (int) (ms%1000)*1000000);
            int user = 0; // TODO: provide access to user if needed
            int group = 0; // TODO: provide access to group if needed
            perms permissions = perms.valueOf(perms.all_all); // TODO: get real permissions
            file_type type;
            boolean isLink;
            try {
                isLink = fo.isSymbolicLink();
            } catch (IOException ex) {
                ex.printStackTrace();
                isLink=false; // TODO: better error processing?
            }

            if (isLink) {
                type = file_type.symlink_file;
            } else if (fo.isFolder()) {
                type = file_type.directory_file;
            } else if (fo.isData()) {
                type = file_type.regular_file;
            } else {
                type = file_type.type_unknown;
            }
            //= fo.isFolder() ? fs.file_type.directory_file : fs.file_type.;
            Status st = new Status(name, name, uid, time, user, group, fo.getSize(), type, permissions);
            return new ErrorOr<Status>(st);
        }
    }

    private class ClankFileObjectBasedFile extends org.clang.basic.vfs.File {

        private final FileObject fo;

        public ClankFileObjectBasedFile(FileObject fo) {
            this.fo = fo;
        }

        /*package*/ FileObject getFileObject() {
            return fo;
        }

        @Override
        public ErrorOr<Status> status() {
            return ClankFileObjectBasedFileSystem.this.getStatus(this);
        }

        @Override
        public ErrorOr<std_ptr.unique_ptr<MemoryBuffer>> getBuffer(Twine Name) {
            long time = TRACE_TIME ? System.currentTimeMillis() : 0;
            ErrorOr<std_ptr.unique_ptr<MemoryBuffer>> buf;
            if (!fo.getPath().contentEquals(Name.str().toJavaString())) {
                CndUtils.assertTrueInConsole(false, "Unexpected Name parameter: '" + Name.str() + " expected " + fo.getPath()); //NOI18N
            }
            try {
                ClankMemoryBufferImpl mb = ClankMemoryBufferImpl.create(fo);
                std_ptr.unique_ptr<MemoryBuffer> p = new std_ptr.unique_ptr<MemoryBuffer>(mb);
                buf = new ErrorOr<std_ptr.unique_ptr<MemoryBuffer>>(p);
            } catch (FileNotFoundException ex) {
                buf = new ErrorOr(std_errors.errc.no_such_file_or_directory);
            } catch (IOException ex) {
                buf = new ErrorOr(std_errors.errc.io_error);
            }
            if (TRACE_TIME) {
                time = System.currentTimeMillis() - time;
                totalReadCount.incrementAndGet();
                totalReadTime.addAndGet(time);
                printStatistics(Name.str(), time);
            }
            return buf;
        }

        @Override
        public ErrorOr<std_ptr.unique_ptr<MemoryBuffer>> getBuffer(Twine Name, long FileSize) {
            return getBuffer(Name);
        }

        @Override
        public ErrorOr<std_ptr.unique_ptr<MemoryBuffer>> getBuffer(Twine Name, long FileSize, boolean RequiresNullTerminator) {
            return getBuffer(Name);
        }

        @Override
        public ErrorOr<std_ptr.unique_ptr<MemoryBuffer>> getBuffer(Twine Name, long FileSize, boolean RequiresNullTerminator, boolean IsVolatile) {
            return getBuffer(Name);
        }

        @Override
        public std_errors.error_code close() {
            return new std.error_code();
        }

        @Override
        public void setName(StringRef Name) {
            new UnsupportedOperationException("Not supported").printStackTrace(System.err); //NOI18N
        }
    }

}
