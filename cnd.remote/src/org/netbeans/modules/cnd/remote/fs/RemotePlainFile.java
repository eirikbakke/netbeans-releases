/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.remote.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.netbeans.modules.nativeexecution.api.ExecutionEnvironment;
import org.netbeans.modules.nativeexecution.api.util.CommonTasksSupport;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Vladimir Kvashin
 */
public class RemotePlainFile extends RemoteFileObjectBase {

    private FileLock lock;

    public RemotePlainFile(RemoteFileSystem fileSystem, ExecutionEnvironment execEnv, 
            RemoteDirectory parent, String remotePath, File cache) {
        super(fileSystem, execEnv, parent, remotePath, cache);
    }

    @Override
    public final FileObject[] getChildren() {
        return new FileObject[0];
    }

    @Override
    public final boolean isFolder() {
        return false;
    }

    @Override
    public boolean isData() {
        return true;
    }

    @Override
    public final FileObject getFileObject(String name, String ext) {
        return null;
    }

    @Override
    public FileObject getFileObject(String relativePath) {
        return null;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        // TODO: check error processing
        try {
            getRemoteFileSupport().ensureFileSync(cache, remotePath);
        } catch (ConnectException ex) {
            return null;
        } catch (IOException ex) {             
            throwFileNotFoundException(ex);
        } catch (InterruptedException ex) {
            throwFileNotFoundException(ex);
        } catch (ExecutionException ex) {
            throwFileNotFoundException(ex);
        } catch (CancellationException ex) {
            // TODO: do we need this? unfortunately CancellationException is RuntimeException, so I'm not sure
            return null;
        }
        return new FileInputStream(cache);
    }

    private void throwFileNotFoundException(Exception cause) throws FileNotFoundException {
        FileNotFoundException ex = new FileNotFoundException(cache.getAbsolutePath());
        ex.initCause(cause);
        throw ex;
    }

    @Override
    public RemoteDirectory getParent() {
        return (RemoteDirectory) super.getParent();
    }        

    @Override
    public FileLock lock() throws IOException {
        synchronized (this) {
            if (lock == null) {
                lock = new FileLock();
            }
        }
        return lock;
    }

//    @Override
//    public boolean isLocked() {
//        return super.isLocked();
//    }

    @Override
    public boolean canWrite() {
        try {
            return getParent().canWrite(getNameExt());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return true;
        }
    }

    @Override
    public OutputStream getOutputStream(FileLock lock) throws IOException {
        if (!isValid()) {
            throw new FileNotFoundException("FileObject " + this + " is not valid."); //NOI18N
        }
        if (isFolder()) {
            throw new IOException(getPath());
        }
        return new DelegateOutputStream();
    }

    private class DelegateOutputStream extends OutputStream {

        FileOutputStream delegate;

        public DelegateOutputStream() throws IOException {
            delegate = new FileOutputStream(cache);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            delegate.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            delegate.write(b);
        }

        @Override
        public void close() throws IOException {
            delegate.close();
            flush();
        }

        @Override
        public void flush() throws IOException {
            delegate.flush();
            StringWriter sw = new StringWriter();
            Future<Integer> task = CommonTasksSupport.uploadFile(cache, execEnv, remotePath, 0777, sw);
            try {
                int rc = task.get().intValue();
                if (rc != 0) {
                    throw new IOException("Error writing file " + cache.getAbsolutePath() //NOI18N
                            +  " at " + execEnv.getDisplayName()  //NOI18N
                            + ": " + sw.toString()); // NOI18N
                }
            } catch (InterruptedException ex) {
                throw new IOException("Error writing file " + cache.getAbsolutePath()  //NOI18N
                        + " at " + execEnv.getDisplayName(), ex); //NOI18N
            } catch (ExecutionException ex) {
                throw new IOException("Error writing file " + cache.getAbsolutePath() //NOI18N
                        + " at " + execEnv.getDisplayName(), ex); //NOI18N
            }
        }
    }
}
