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

package org.netbeans.api.languages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 *
 * @author Jan Jancura
 */
public abstract class ASTPath {

    ASTPath () {};
    public abstract ASTItem                 getLeaf ();
    public abstract int                     size ();
    public abstract ASTItem                 getRoot ();
    public abstract ListIterator<ASTItem>   listIterator ();
    public abstract ListIterator<ASTItem>   listIterator (int index);
    public abstract ASTItem                 get (int index);
    public abstract ASTPath                 subPath (int index);

    public static ASTPath create (ASTItem n, ASTItem t) {
        if (n == null) return create (t);
        if (t == null) throw new NullPointerException ();
        return new Token2Path (n.getPath (), t);
    }

    public static ASTPath create (ASTItem n) {
        if (n == null) throw new NullPointerException ();
        return new TokenPath (n);
    }
    
    
    // innerclasses ............................................................

    private static class TokenPath extends ASTPath {

        private ASTItem o;
        
        TokenPath (ASTItem o) {
            this.o = o;
        }
        
        public ASTItem getLeaf () {
            return o;
        }
        
        public int size () {
            return 1;
        }
        
        public ASTItem getRoot () {
            return o;
        }
        
        public ListIterator<ASTItem> listIterator () {
            return Collections.singletonList (o).listIterator ();
        }
        
        public ListIterator<ASTItem> listIterator (int index) {
            return Collections.singletonList (o).listIterator (index);
        }
        
        public ASTItem get (int index) {
            if (index == 0) return o;
            throw new ArrayIndexOutOfBoundsException ();
        }
        
        public ASTPath subPath (int index) {
            if (index == 0) return this;
            throw new ArrayIndexOutOfBoundsException ();
        }
        
        public String toString () {
            return "ASTPath " + o;
        }
    }

    private static class Token2Path extends ASTPath {

        private List<ASTItem> l = new ArrayList<ASTItem> ();
        private int s;
        
        Token2Path (ASTPath p, ASTItem t) {
            if (p instanceof Token2Path)
                l.addAll (((Token2Path) p).l);
            else
                l.add (((TokenPath) p).o);
            l.add (t);
            s = l.size ();
        }
        
        Token2Path (List<ASTItem> list) {
            l.addAll (list);
            s = l.size ();
        }
        
        public ASTItem getLeaf () {
            return l.get (s - 1);
        }
        
        public int size () {
            return s;
        }
        
        public ASTItem getRoot () {
            return l.get (0);
        }
        
        public ListIterator<ASTItem> listIterator () {
            return l.listIterator ();
        }
        
        public ListIterator<ASTItem> listIterator (int index) {
            return l.listIterator (index);
        }
        
        public ASTItem get (int index) {
            return l.get (index);
        }
        
        public ASTPath subPath (int index) {
            return new Token2Path (l.subList (0, index));
        }
        
        public String toString () {
            StringBuilder sb = new StringBuilder ("ASTPath ");
            Iterator<ASTItem> it = l.iterator ();
            if (it.hasNext ()) {
                ASTItem item = it.next ();
                if (item instanceof ASTNode)
                    sb.append (((ASTNode) item).getNT ());
                else
                    sb.append (item);
            }
            while (it.hasNext ()) {
                ASTItem item = it.next ();
                if (item instanceof ASTNode)
                    sb.append (", ").append (((ASTNode) item).getNT ());
                else
                    sb.append (", ").append (item);
            }
            return sb.toString ();
        }
    }
}
