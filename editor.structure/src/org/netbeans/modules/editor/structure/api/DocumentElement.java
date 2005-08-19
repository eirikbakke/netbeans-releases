/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.editor.structure.api;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;


/**
 * DocumentElement is a building block of the document model tree-based hierarchy.
 * <br>
 * DocumentElement represents a piece of a {@link javax.swing.text.Document} with following behaviour
 * <ul>
 * <li>DocumentElement can contain other elements (elements can nest),
 * <li>Boundaries of each two elements cannot cross,
 * <li>Two elements cannot have the same boundaries.
 * <li>DocumentElement boundaries cannot be the same (startoffset==endoffset)
 * </ul>
 * <br>
 * The DocumentElement holds a set of attributes which can contain an arbitrary metadata related to the element.
 * <br>
 * It is possible to attach a {@link DocumentElementListener} to each DocumentElement.
 * The listener can notify about children elements added, removed, reordered or when the content or attributes of the element have been changed.
 * <br>
 * Each DocumentModel which contains a tree of elements has one root element. This is a special
 * element which is not created by model providers, but is created by default.
 *
 *
 * @author Marek Fukala
 * @version 1.0
 */
public final class DocumentElement {
    
    private String name;
    private String type;
    private Position startPos, endPos;
    private int startSectionLength, endSectionLength;
    private DocumentModel model;
    private Attributes attributes;
    
    //stores DocumentElement listeners
    private HashSet deListeners = new HashSet();
    
    DocumentElement(String name, String type, Map attrsMap,
            int startOffset, int endOffset, DocumentModel model) throws BadLocationException {
        this.name = name;
        this.model = model;
        this.startSectionLength = startSectionLength;
        this.endSectionLength = endSectionLength;
        this.type = type;
        this.attributes = new Attributes(this, attrsMap);
        
        //create positions for start and end offsets
        setStartPosition(startOffset);
        setEndPosition(endOffset);
    }
    
    /**
     * Returns a collection of attributes this element contains.
     *
     * @return the attributes for the element
     */
    public AttributeSet getAttributes() {
        return this.attributes;
    }
    
    /**
     * Returns the document associated with this element.
     *
     * @return the document
     */
    public Document getDocument() {
        return model.getDocument();
    }
    
    /**
     * Returns the child element at the given index.
     *
     * @param index the specified index >= 0
     * @return the child element
     */
    public DocumentElement getElement(int index) {
        return (DocumentElement)getChildren().get(index);
    }
    
    /**
     * Gets the number of child elements contained by this element.
     * If this element is a leaf, a count of zero is returned.
     *
     * @return the number of child elements >= 0
     */
    public int getElementCount() {
        return getChildren().size();
    }
    
    /**
     * Returns the offset from the beginning of the document
     * that this element begins at.
     *
     * @return the starting offset >= 0 and < getEndOffset();
     * @see javax.swing.text.Document
     */
    public int getStartOffset() {
        return startPos.getOffset();
    }
    
    /**
     * Returns the offset from the beginning of the document
     * that this element ends at.
     *
     * @return the ending offset >= getDocument().getLength() and > getStartOffset();
     * @see javax.swing.text.Document
     */
    public int getEndOffset() {
        return endPos.getOffset();
    }
    
    /**
     * Gets the child element index closest to the given offset.
     * The offset is specified relative to the beginning of the
     * document.  Returns <code>-1</code> if the
     * <code>Element</code> is a leaf, otherwise returns
     * the index of the <code>Element</code> that best represents
     * the given location.  Returns <code>0</code> if the location
     * is less than the start offset. Returns
     * <code>getElementCount() - 1</code> if the location is
     * greater than or equal to the end offset.
     *
     * @param offset the specified offset >= 0
     * @return the element index >= 0
     */
    public int getElementIndex(int offset) {
        //find a child closes to the given offset
        //The javadoc seems to be quite vague in definition of the bahaviour
        //of this method. What is closest? What if the offset falls right
        //between two elements?
        Iterator children = getChildren().iterator();
        int min_delta = Integer.MAX_VALUE;
        DocumentElement nearest = null;
        while(children.hasNext()) {
            DocumentElement de = (DocumentElement)children.next();
            
            //test if the offset falls directly to a child
            if(de.getStartOffset() <= offset && de.getEndOffset() > offset) {
                nearest = de;
                break;
            } else {
                //no child element on offset -> try to find nearest child
                int start_delta = Math.abs(de.getStartOffset() - offset);
                int end_delta = Math.abs(de.getEndOffset() - offset);
                int delta = Math.min(start_delta, end_delta);
                
                if(min_delta > delta) {
                    nearest = de;
                    min_delta = delta;
                }
            }
        }
        
        if(nearest == null) return -1;
        else return getChildren().indexOf(nearest);
        
    }
    
    /** Returns the name of the element. 
     *
     * @return the element name
     */
    public String getName() {
        return name;
    }
    
    /** Returns parent DocumentElement for this element. Returns null when the DocumentElement is a root element.
     *
     * @return the parent element
     */
    public DocumentElement getParentElement() {
        return model.getParent(this);
    }
    
    /** @return true if the element has no children */
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }
    
    /* EOF j.s.t.Element methods */
    
    /** Returns an instance of DocumentModel within which hierarchy the element lives.
     * @return the DocumentModel which holds this element 
     */
    public DocumentModel getDocumentModel() {
        return model;
    }
    
    /** Returns a type of the element.
     * Each DocumentModelProvider should create a set of elements types and the pass these 
     * types when elements of corresponding types are created. 
     * Clients of the API then uses the element's type to determine the element type.
     *
     * @return the element type
     */
    public String getType() {
        return type;
    }
    
    /** @return a list of the element's children */
    public List/*<DocumentElement>*/ getChildren() {
        return model.getChildren(this);
    }
    
    /** Adds a DocumentElementListener to this DocumentElement instance */
    public void addDocumentElementListener(DocumentElementListener del) {
        deListeners.add(del);
    }
    
    /** Removes a DocumentElementListener to this DocumentElement instance */
    public void removeDocumentElementListener(DocumentElementListener del) {
        deListeners.remove(del);
    }
    
    /* <<< EOF public methods */
    
    void setStartPosition(int offset) throws BadLocationException {
        startPos = model.getDocument().createPosition(offset);
    }
    
    void setEndPosition(int offset) throws BadLocationException {
        endPos = model.getDocument().createPosition(offset);
    }
    
    String getContent() throws BadLocationException {
        return model.getDocument().getText(getStartOffset(), getEndOffset() - getStartOffset());
    }
    
    private void fireDocumentElementEvent(DocumentElementEvent dee) {
        Iterator cListeners = deListeners.iterator();
        while(cListeners.hasNext()) {
            DocumentElementListener cl = (DocumentElementListener)cListeners.next();
            switch(dee.getType()) {
                case DocumentElementEvent.CHILD_ADDED: cl.elementAdded(dee);break;
                case DocumentElementEvent.CHILD_REMOVED: cl.elementRemoved(dee);break;
                case DocumentElementEvent.CONTENT_CHANGED: cl.contentChanged(dee);break;
                case DocumentElementEvent.ATTRIBUTES_CHANGED: cl.contentChanged(dee);break;
            }
        }
    }
    
    //called by model when a new DocumentElement was added to this element
    void childAdded(DocumentElement de) {
//        System.out.println("[event] " + this + ": child added:" + de);
        fireDocumentElementEvent(new DocumentElementEvent(DocumentElementEvent.CHILD_ADDED, this, de));
    }
    
    //called by model when one of the children of this element was removed
    void childRemoved(DocumentElement de) {
//        System.out.println("[event] " + this + ": child removed:" + de);
        fireDocumentElementEvent(new DocumentElementEvent(DocumentElementEvent.CHILD_REMOVED, this, de));
    }
    
    //called by model when element content changed
    void contentChanged() {
//        System.out.println("[event] " + this + ": content changed");
        fireDocumentElementEvent(new DocumentElementEvent(DocumentElementEvent.CONTENT_CHANGED, this, null));
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof DocumentElement)) return false;
        
        DocumentElement de = (DocumentElement)o;
        
        return (de.getName().equals(getName()) &&
                de.getType().equals(getType()) &&
                de.getStartOffset() == getStartOffset() &&
                de.getEndOffset() == getEndOffset());
    }
    
    public String toString() {
        String elementContent = "";
        try {
            elementContent = getContent().trim().length() > PRINT_MAX_CHARS ?
                getContent().trim().substring(0, PRINT_MAX_CHARS) + "..." :
                getContent().trim();
        }catch(BadLocationException e) {
            elementContent = "error:" + e.getMessage();
        }
        return "DE (" + hashCode() + ")[\"" + getName() +
                "\" (" + getType() +
                ") <" + getStartOffset() +
                "-" + getEndOffset() +
                "> '" + encodeNewLines(elementContent) +
                "']";
    }
    
    private String encodeNewLines(String s) {
        StringBuffer encoded = new StringBuffer();
        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '\n') encoded.append("\\n"); else encoded.append(s.charAt(i));
        }
        return encoded.toString();
    }
    
    /** AttributeSet implementation. */
    
    private static final class Attributes implements AttributeSet {
        private Map attrs;
        private DocumentElement de;
        
        Attributes(DocumentElement element, Map/*<String>*/ m) {
            de = element;
            attrs = m;
        }
        
        public int getAttributeCount() {
            return attrs.size();
        }
        
        public boolean isDefined(Object attrName) {
            return attrs.containsKey(attrName);
        }
        
        public boolean isEqual(AttributeSet attr) {
            if(getAttributeCount() != attr.getAttributeCount()) return false;
            return containsAttributes(attr);
        }
        
        public AttributeSet copyAttributes() {
            HashMap clone = new HashMap(getAttributeCount());
            clone.putAll(attrs);
            return new Attributes(de, clone);
        }
        
        public Object getAttribute(Object key) {
            return attrs.get(key);
        }
        
        public Enumeration getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }
        
        public boolean containsAttribute(Object name, Object value) {
            return value.equals(getAttribute(name));
        }
        
        public boolean containsAttributes(AttributeSet attributes) {
            Enumeration e = attributes.getAttributeNames();
            while(e.hasMoreElements()) {
                Object key = e.nextElement();
                Object value = attributes.getAttribute(key);
                if(!containsAttribute(key, value)) return false;
            }
            return true;
        }
        
        public AttributeSet getResolveParent() {
            return de.getParentElement() != null ? de.getParentElement().getAttributes() : null;
        }
        
    }
    
    private final int PRINT_MAX_CHARS = 10;
}
