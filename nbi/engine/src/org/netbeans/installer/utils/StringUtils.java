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
 *
 * $Id$
 */
package org.netbeans.installer.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import org.netbeans.installer.utils.SystemUtils.Platform;

/**
 *
 * @author Kirill Sorokin
 */
public abstract class StringUtils {
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String BACK_SLASH = "\\"; //NOI18N
    public static final String FORWARD_SLASH = "/"; //NOI18N
    public static final String DOUBLE_BACK_SLASH = "\\\\"; //NOI18N
    
    public static final String CRLF = "\r\n";
    public static final String CRLFCRLF = CRLF + CRLF;
    ////////////////////////////////////////////////////////////////////////////
    // Static
    private static StringUtils instance;
    
    public static synchronized StringUtils getInstance() {
        if (instance == null) {
            instance = new GenericStringUtils();
        }
        
        return instance;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance
    public abstract String formatMessage(String message, Object[] arguments);
    
    public abstract String leftTrim(String string);
    
    public abstract String rightTrim(String string);
    
    public abstract char fetchMnemonic(String string);
    
    public abstract String stripMnemonic(String string);
    
    public abstract String capitalizeFirst(String string);
    
    public abstract String getGetterName(String propertyName);
    
    public abstract String getBooleanGetterName(String propertyName);
    
    public abstract String getSetterName(String propertyName);
    
    public abstract String asString(Throwable throwable);
    
    public abstract String getFilenameFromUrl(String urlString);
    
    public abstract String asString(List<Platform> platforms);
    
    public abstract String formatSize(long longBytes);
    
    public abstract String asHexString(byte[] bytes);
    
    public abstract String base64Encode(String string) throws UnsupportedEncodingException;
    
    public abstract String base64Encode(String string, String charset) throws UnsupportedEncodingException;
    
    public abstract String base64Decode(String string) throws UnsupportedEncodingException;
    
    public abstract String base64Decode(String string, String charset) throws UnsupportedEncodingException;
    
    ////////////////////////////////////////////////////////////////////////////
    // Inner Classes
    private static class GenericStringUtils extends StringUtils {
        ////////////////////////////////////////////////////////////////////////
        // Constants
        private static final String LEFT_WHITESPACE  = "^\\s+";
        private static final String RIGHT_WHITESPACE = "\\s+$";
        
        private static final char   MNEMONIC_CHAR    = '&';
        private static final String MNEMONIC         = "&";
        private static final char   NO_MNEMONIC      = '\u0000';
        
        private static final char[] BASE64_TABLE = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '+', '/'
        };
        
        private static final byte[] BASE64_REVERSE_TABLE = new byte[] {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, 62, -1, -1, -1, 63, 52, 53,
            54, 55, 56, 57, 58, 59, 60, 61, -1, -1,
            -1, -1, -1, -1, -1,  0,  1,  2,  3,  4,
            5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
            25, -1, -1, -1, -1, -1, -1, 26, 27, 28,
            29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
            39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
            49, 50, 51
        };
        
        private static final char BASE64_PAD = '=';
        
        private static final int BIN_11111111 = 0xff;
        private static final int BIN_00110000 = 0x30;
        private static final int BIN_00111100 = 0x3c;
        private static final int BIN_00111111 = 0x3f;
        
        public String formatMessage(String message, Object[] arguments) {
            return MessageFormat.format(message, arguments);
        }
        
        public String leftTrim(String string) {
            return string.replaceFirst(LEFT_WHITESPACE, "");
        }
        
        public String rightTrim(String string) {
            return string.replaceFirst(RIGHT_WHITESPACE, "");
        }
        
        public char fetchMnemonic(String string) {
            int index = string.indexOf(MNEMONIC_CHAR);
            if ((index != -1) && (index < string.length() - 1)) {
                return string.charAt(index + 1);
            }
            
            return NO_MNEMONIC;
        }
        
        public String stripMnemonic(String string) {
            return string.replaceFirst(MNEMONIC, "");
        }
        
        public String capitalizeFirst(String string) {
            return "" + Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }
        
        public String getGetterName(String propertyName) {
            return "get" + capitalizeFirst(propertyName);
        }
        
        public String getBooleanGetterName(String propertyName) {
            return "is" + capitalizeFirst(propertyName);
        }
        
        public String getSetterName(String propertyName) {
            return "set" + capitalizeFirst(propertyName);
        }
        
        public String asString(Throwable throwable) {
            StringWriter writer = new StringWriter();
            
            throwable.printStackTrace(new PrintWriter(writer));
            
            return writer.toString();
        }
        
        public String getFilenameFromUrl(String urlString) {
            String url = urlString.trim();
            int index = Math.max(
                    url.lastIndexOf(FORWARD_SLASH),
                    url.lastIndexOf(BACK_SLASH));
            int length = url.length();
            return (index > 0 && (index < length - 1)) ?
                url.substring(index + 1,  length) : null;
        }
        
        public String asString(List<Platform> platforms) {
            String result = "";
            
            for (int i = 0; i < platforms.size(); i++) {
                result += platforms.get(i).getName();
                
                if (i != platforms.size() - 1) {
                    result += " ";
                }
            }
            
            return result;
        }
        
        public String formatSize(long longBytes) {
            StringBuffer result = new StringBuffer();
            
            double bytes = (double) longBytes;
            
            // try as GB
            double gigabytes = bytes / 1024. / 1024. / 1024.;
            if (gigabytes > 1.) {
                return String.format("%.1f GB", gigabytes);
            }
            
            // try as MB
            double megabytes = bytes / 1024. / 1024.;
            if (megabytes > 1.) {
                return String.format("%.1f MB", megabytes);
            }
            
            // try as KB
            double kilobytes = bytes / 1024.;
            if (kilobytes > .5) {
                return String.format("%.1f KB", kilobytes);
            }
            
            // return as bytes
            return "" + longBytes + " B";
        }
        
        public String asHexString(byte[] bytes) {
            StringBuilder builder = new StringBuilder();
            
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                
                String byteHex = Integer.toHexString(b);
                if (byteHex.length() == 1) {
                    byteHex = "0" + byteHex;
                }
                if (byteHex.length() > 2) {
                    byteHex = byteHex.substring(byteHex.length() - 2);
                }
                
                builder.append(byteHex);
            }
            
            return builder.toString();
        }
        
        public String base64Encode(String string) throws UnsupportedEncodingException {
            return base64Encode(string, "UTF-8");
        }
        
        public String base64Encode(String string, String charset) throws UnsupportedEncodingException {
            StringBuilder builder = new StringBuilder();
            
            byte[] bytes = string.getBytes(charset);
            
            int i;
            for (i = 0; i < bytes.length - 2; i += 3) {
                int byte1 = bytes[i] & BIN_11111111;
                int byte2 = bytes[i + 1] & BIN_11111111;
                int byte3 = bytes[i + 2] & BIN_11111111;
                
                builder.append(BASE64_TABLE[byte1 >> 2]);
                builder.append(BASE64_TABLE[((byte1 << 4) & BIN_00110000) | (byte2 >> 4)]);
                builder.append(BASE64_TABLE[((byte2 << 2) & BIN_00111100) | (byte3 >> 6)]);
                builder.append(BASE64_TABLE[byte3 & BIN_00111111]);
            }
            
            if (i == bytes.length - 2) {
                int byte1 = bytes[i] & BIN_11111111;
                int byte2 = bytes[i + 1] & BIN_11111111;
                
                builder.append(BASE64_TABLE[byte1 >> 2]);
                builder.append(BASE64_TABLE[((byte1 << 4) & BIN_00110000) | (byte2 >> 4)]);
                builder.append(BASE64_TABLE[(byte2 << 2) & BIN_00111100]);
                builder.append(BASE64_PAD);
            }
            
            if (i == bytes.length - 1) {
                int byte1 = bytes[i] & BIN_11111111;
                
                builder.append(BASE64_TABLE[byte1 >> 2]);
                builder.append(BASE64_TABLE[(byte1 << 4) & BIN_00110000]);
                builder.append(BASE64_PAD);
                builder.append(BASE64_PAD);
            }
            
            return builder.toString();
        }
        
        public String base64Decode(String string) throws UnsupportedEncodingException {
            return base64Decode(string, "UTF-8");
        }
        
        public String base64Decode(String string, String charset) throws UnsupportedEncodingException {
            int completeBlocksNumber = string.length() / 4;
            int missingBytesNumber = 0;
            
            if (string.endsWith("=")) {
                completeBlocksNumber--;
                missingBytesNumber++;
            }
            if (string.endsWith("==")) {
                missingBytesNumber++;
            }
            
            int decodedLength = (completeBlocksNumber * 3) + (3 - missingBytesNumber);
            byte[] decodedBytes = new byte[decodedLength];
            
            int encodedCounter = 0;
            int decodedCounter = 0;
            for (int i = 0; i < completeBlocksNumber; i++) {
                int byte1 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte2 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte3 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte4 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                
                decodedBytes[decodedCounter++] = (byte) ((byte1 << 2) | (byte2 >> 4));
                decodedBytes[decodedCounter++] = (byte) ((byte2 << 4) | (byte3 >> 2));
                decodedBytes[decodedCounter++] = (byte) ((byte3 << 6) | byte4);
            }
            
            if (missingBytesNumber == 1) {
                int byte1 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte2 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte3 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                
                decodedBytes[decodedCounter++] = (byte) ((byte1 << 2) | (byte2 >> 4));
                decodedBytes[decodedCounter++] = (byte) ((byte2 << 4) | (byte3 >> 2));
            }
            
            if (missingBytesNumber == 2) {
                int byte1 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                int byte2 = BASE64_REVERSE_TABLE[string.charAt(encodedCounter++)];
                
                decodedBytes[decodedCounter++] = (byte) ((byte1 << 2) | (byte2 >> 4));
            }
            
            return new String(decodedBytes, charset);
        }
    }
    
}
