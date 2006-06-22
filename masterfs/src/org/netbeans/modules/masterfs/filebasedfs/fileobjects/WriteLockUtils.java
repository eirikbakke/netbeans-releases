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

package org.netbeans.modules.masterfs.filebasedfs.fileobjects;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.openide.util.Exceptions;


/** 
 * @author Radek Matous
 */


public class WriteLockUtils {
    static final String PREFIX = ".LCK";
    static final String SUFFIX = "~";
        
    
    private WriteLockUtils(){}
            
    public static synchronized boolean hasActiveLockFileSigns(final String filePath) {
        return filePath.indexOf(WriteLockUtils.PREFIX) != -1 && filePath.indexOf(WriteLockUtils.SUFFIX) != -1;
    }
    
    public static synchronized boolean isActiveLockFile(final File file) {
        final String name = file.getName();
        boolean isActiveLockFile = hasActiveLockFileSigns(name);
        if (isActiveLockFile) {
            final String newName = name.substring(WriteLockUtils.PREFIX.length(), (name.length() - WriteLockUtils.SUFFIX.length()));
            isActiveLockFile = new File(file.getParentFile(), newName).exists();
        }
        
        return isActiveLockFile;
    }
    
    public static File getAssociatedLockFile(File file)  {
        try {
            file = file.getCanonicalFile();
        } catch (IOException iex) {
            Exceptions.printStackTrace(iex);            
        }
        
        final File parentFile = file.getParentFile();
        final StringBuilder sb = new StringBuilder();
        
        sb.append(WriteLockUtils.PREFIX);//NOI18N
        sb.append(file.getName());//NOI18N
        sb.append(WriteLockUtils.SUFFIX);//NOI18N
        
        final String lckName = sb.toString();
        final File lck = new File(parentFile, lckName);
        return lck;
    }
    
    static String getContentOfLckFile(File lckFile, FileChannel channel) throws IOException {
        final byte[] readContent = new byte[(int) lckFile.length()];
        channel.read(ByteBuffer.wrap(readContent));
        
        final String retVal = new String(readContent);
        return (new File(retVal).exists()) ? retVal : null;
    }
    
    static String writeContentOfLckFile(final File lck, FileChannel channel) throws IOException {
        final String absolutePath = lck.getAbsolutePath();
        final ByteBuffer buf = ByteBuffer.wrap(absolutePath.getBytes());
        channel.write(buf);
        return absolutePath;
    }    
}
