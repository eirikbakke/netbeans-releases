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
package org.netbeans.modules.xml.text.syntax.javacc.lib;

import java.io.*;

/** Support for JavaCC version 1.1. When JavaCC is required to read directly
* from string or char[].
*
* @author Petr Kuzel
* @version Aug 4, 2000 
*/
public class StringParserInput extends Object implements CharStream {
    /** the buffer */
    private char[] buffer;

    /** the position in the buffer*/
    private int pos;

    /** Begin of current token, for backup operation */
    private int begin;

    /** Length of whole buffer */
    private int len;
    
    /** buffer end. */
    private int end;

    public StringParserInput() {}

    
    public void setString(String s) {
        buffer = s.toCharArray();
        begin = pos = 0;
        len = s.length();
        end = len;
    }
    
    /** Share buffer with e.g. syntax coloring. */
    public void setBuffer(char[] buf, int offset, int len) {
        buffer = buf;
        begin = pos = offset;
        this.len = len;
        end = offset + len;
    }

    /**
     * Returns the next character from the selected input.  The method
     * of selecting the input is the responsibility of the class
     * implementing this interface.  Can throw any java.io.IOException.
     */
    public char readChar() throws java.io.IOException {
        if (pos >= end)
            throw new java.io.EOFException ();
        return buffer[pos++];
    }

    /**
     * Returns the column position of the character last read.
     * @deprecated
     * @see #getEndColumn
     */
    public int getColumn() {
        return 0;
    }

    /**
     * Returns the line number of the character last read.
     * @deprecated
     * @see #getEndLine
     */
    public int getLine() {
        return 0;
    }

    /**
     * Returns the column number of the last character for current token (being
     * matched after the last call to BeginTOken).
     */
    public int getEndColumn() {
        return 0;
    }

    /**
     * Returns the line number of the last character for current token (being
     * matched after the last call to BeginTOken).
     */
    public int getEndLine() {
        return 0;
    }

    /**
     * Returns the column number of the first character for current token (being
     * matched after the last call to BeginTOken).
     */
    public int getBeginColumn() {
        return 0;
    }

    /**
     * Returns the line number of the first character for current token (being
     * matched after the last call to BeginTOken).
     */
    public int getBeginLine() {
        return 0;
    }

    /**
     * Backs up the input stream by amount steps. Lexer calls this method if it
     * had already read some characters, but could not use them to match a
     * (longer) token. So, they will be used again as the prefix of the next
     * token and it is the implemetation's responsibility to do this right.
     */
    public void backup(int amount) {
        if (pos > 1)
            pos -= amount;
    }

    /**
     * Returns the next character that marks the beginning of the next token.
     * All characters must remain in the buffer between two successive calls
     * to this method to implement backup correctly.
     */
    public char BeginToken() throws java.io.IOException {
        begin = pos;
        return readChar ();
    }

    /**
     * Returns a string made up of characters from the marked token beginning
     * to the current buffer position. Implementations have the choice of returning
     * anything that they want to. For example, for efficiency, one might decide
     * to just return null, which is a valid implementation.
     */
    public String GetImage() {
        return new String(buffer, begin, pos-begin);
    }

    
    /** @return token length. */
    public int getLength() {
        return pos - begin;
    }
    
    /**
     * Returns an array of characters that make up the suffix of length 'len' for
     * the currently matched token. This is used to build up the matched string
     * for use in actions in the case of MORE. A simple and inefficient
     * implementation of this is as follows :
     *
     *   {
     *      String t = GetImage();
     *      return t.substring(t.length() - len, t.length()).toCharArray();
     *   }
     */
    public char[] GetSuffix(int l) {
        char[] ret = new char[l];
        System.arraycopy(buffer, pos - l, ret, 0, l);
        return ret;
    }

    /**
     * The lexer calls this function to indicate that it is done with the stream
     * and hence implementations can free any resources held by this class.
     * Again, the body of this function can be just empty and it will not
     * affect the lexer's operation.
     */
    public void Done() {
    }

    public String toString() {
        return "StringParserInput\n Pos:" + pos + " len:" + len + " #################\n" + buffer; // NOI18N
    }
}
