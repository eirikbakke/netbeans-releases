/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.xml.tools.generator;

import java.util.*;
import java.text.DateFormat;

import org.openide.filesystems.FileObject;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.EditCookie;
import org.openide.loaders.*;
import org.openide.util.Utilities;

public class GenerateSupportUtils {
    
    /**
     * Use some heuristics and create valid java name.
     */
    public static String getJavaName (String name) {
        StringTokenizer st = new StringTokenizer (name, " -.:"); // NOI18N
        StringBuffer sb = new StringBuffer ();
        if (st.hasMoreTokens()) {
            sb.append (st.nextToken());
            while (st.hasMoreTokens()) {
                sb.append ("_").append (st.nextToken()); // NOI18N
            }
        }
        return sb.toString();
    }

    /** 
     * Obtain OpenCookie and if exist open.
     */
    public static void tryOpenFile (FileObject fo) {
        if (fo == null) {
            Util.debug ("FileObject must not be null.", new IllegalArgumentException());  // NOI18N
            return;            
        }
        
        try {
            DataObject DO = DataObject.find (fo);
            OpenCookie oc = (OpenCookie)DO.getCookie (OpenCookie.class);
            if (oc != null)
                oc.open();
        } catch (DataObjectNotFoundException e) {
        }
    }

    /** 
     * Obtain EditCookie and if exist open in editor.
     */    
    public static void tryEditFile (FileObject fo) {
        try {
            DataObject DO = DataObject.find (fo);
            EditCookie ec = (EditCookie)DO.getCookie (EditCookie.class);
            if (ec != null)
                ec.edit();
        } catch (DataObjectNotFoundException e) {
        }
    }

    /*
     * Generate default java header.
     * @param primFile a model used for generated code or null
     */
    public static String getJavaFileHeader (String name, FileObject primFile) {
        Date now = new Date();

        StringBuffer sb = new StringBuffer ();
        sb.append("/*\n * File:           ").append (name).append (".java"); // NOI18N
        if (primFile != null) {
            sb.append ("\n * Generated from: ").append (primFile.getName()).append (".").append (primFile.getExt()); // NOI18N
        }
        sb.append("\n * Date:           ").append (DateFormat.getDateInstance (DateFormat.LONG).format (now)); // NOI18N
        sb.append ("  ").append (DateFormat.getTimeInstance (DateFormat.SHORT).format (now)); // NOI18N
        sb.append("\n *"); // NOI18N
        sb.append("\n * @author  ").append(System.getProperty ("user.name")); // NOI18N
        sb.append("\n * @version generated by FFJ XML module"); // NOI18N
        sb.append("\n */"); // NOI18N
        return sb.toString();
    }
    
//      public static final void debug(String msg, Throwable ex) {
//          if (Boolean.getBoolean("netbeans.debug.xml")) { // NOI18N
//              System.err.println(msg);
//              ex.printStackTrace();
//          }
//      }

    
    /**
     * Test whether given identifier is valid Java return type
     */
    public static boolean isValidReturnType(String value) {
        
        if (value == null || "".equals(value)) return false; // NOI18N
        int length = value.length();
        
        // check for arrays e.g. "String[][]"
        while (length > 2 && value.charAt(length-2) == '[' && value.charAt(length-1) == ']') {
            value = value.substring(0, length-2);
            length -= 2;
        }
        
        if (value.charAt(0) == '.' || value.charAt(value.length()-1) == '.')
            return false;
        
        for(int i=1; i<length-2; i++) {
            if (value.charAt(i) == '.' && value.charAt(i+1) == '.')
                return false;
        }
        
                    
        StringTokenizer st = new StringTokenizer(value, "."); // NOI18N
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!!!isPrimitiveType(token) && !!!Utilities.isJavaIdentifier(token))
                return false;
        }
        return true;
    }

    /**
     * @return true if given value does represent Java primitive type.
     */
    public static boolean isPrimitiveType(String value) {

        String val[] = {"int", "char", "boolean", "long", "float", "double", "void"}; // NOI18N
        for(int i=0;i<val.length;i++) {
            if(val[i].equals(value))
                return true;
        }
        return false;
    }            
    
}
