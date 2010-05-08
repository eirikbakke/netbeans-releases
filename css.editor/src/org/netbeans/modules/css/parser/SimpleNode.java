/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 4.1 */
/* JavaCCOptions:MULTI=false,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.netbeans.modules.css.parser;

public class SimpleNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Object value;
    protected CssParser parser;
    protected Token firstToken;
    protected Token lastToken;
    private String image;

    public SimpleNode(int i) {
        id = i;
    }

    public SimpleNode(CssParser p, int i) {
        this(i);
        parser = p;
    }

    public int kind() {
        return id;
    }

    public int startOffset() {
        return jjtGetFirstToken().offset;
    }

    public int endOffset() {
        //why this happens???
        if (jjtGetLastToken().image == null) {
            System.err.println("ERROR: lastToken image is null! : " + jjtGetLastToken());
            return jjtGetLastToken().offset;
        } else {
            //#181357 - At the end of the tokens sequence there is the EOF token (kind==0) with
            //zero lenght but WRONG start offset - it points at the previous offset (real file end - 1).
            //so tokens of simple code may look like:
            //Token( 0; 'h1')
            //Token( 2; ' ')
            //Token( 3; '{')
            //Token( 4; ' ')
            //Token( 5; '}')
            //Token( 5; '') <-- here the (EOF) token should apparently have offset set to 6.
            //
            //sometimes there are even two EOF tokens at the end of the sequence with the same wrong offset!
            //
            //why this happens is a mystery to me, maybe caused by some changes to the default javacc
            //lexing, but I am not sure so I'll workaround it here.
            if(jjtGetLastToken().kind == CssParserConstants.EOF) {
                return jjtGetLastToken().offset + 1;
            } else {
                return jjtGetLastToken().offset + jjtGetLastToken().image.length();
            }
        }
    }

    public String image() {
        return image(false);
    }

    public String image(int... filteredTokenKinds) {
        return image(false, filteredTokenKinds);
    }

    public String image(boolean debug, int... filteredTokenKinds) {
        //optimize filtered tokens kinds search speed a bit
        boolean[] filtered = new boolean[CssParserConstants.tokenImage.length];
        for(int i = 0; i < filteredTokenKinds.length; i++) {
            int kind = filteredTokenKinds[i];
            filtered[kind] = true;
        }
        //now just use if(filtered[node.kind]) { //filter }
        
        synchronized (this) {
            //do not cache or use cached if debugged
            String retVal = image;
            if (retVal == null || debug) {
                retVal = "";
                StringBuffer sb = new StringBuffer();
                if (jjtGetFirstToken() == jjtGetLastToken()) {
                    if(!filtered[jjtGetFirstToken().kind]) {
                        retVal = jjtGetFirstToken().image;
                    }
                } else {
                    Token t = jjtGetFirstToken();
                    Token last = jjtGetLastToken();
                    while (t != null && t.offset <= last.offset) { //also include the last token
                        if(debug) {
                            sb.append("<");
                            sb.append(t.offset);
                            sb.append(",");
                        }
                        if(!filtered[t.kind]) {
                            sb.append(t.image);
                        } else {
                            if(debug) {
                                sb.append("filtered node:");
                                sb.append(t.image);
                            }
                        }
                        if(debug) {
                            sb.append(">");
                        }
                        t = t.next;
                    }
                    retVal = sb.toString();
                }
            }

            if(!debug) {
                image = retVal;
            } else {
                return retVal; //do not cache if debug
            }

            return image;
        }
    }

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    public void jjtSetParent(Node n) {
        parent = n;
    }

    public Node jjtGetParent() {
        return parent;
    }

    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    public Node jjtGetChild(int i) {
        return children[i];
    }

    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    public void jjtSetValue(Object value) {
        this.value = value;
    }

    public Object jjtGetValue() {
        return value;
    }

    public Token jjtGetFirstToken() {
        return firstToken;
    }

    public void jjtSetFirstToken(Token token) {
        this.firstToken = token;
    }

    public Token jjtGetLastToken() {
        return lastToken;
    }

    public void jjtSetLastToken(Token token) {
        this.lastToken = token;
    }

    private String toString(boolean addImage) {
        return CssParserTreeConstants.jjtNodeName[id]
                + " [" 
                + startOffset()
                + " - " 
                + endOffset() 
                + "]"
                + (addImage ? " '" + image(true) + "'" : "");
                
    }
    
    @Override
    public String toString() {
        return toString(true);
    }

    private String toString(String prefix, boolean addTokenImage) {
        return prefix + toString(addTokenImage);
    }

    public String dump() {
        return dump("");
    }
    
    private String dump(String prefix) {
        StringBuilder str = new StringBuilder();
//        str.append(toString(prefix, true));
        str.append(toString(prefix, children == null || children.length == 0));
        str.append('\n');
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    str.append(n.dump(prefix + " "));
                }
            }
        }
        return str.toString();
    }

    public void visitChildren(NodeVisitor visitor) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    visitor.visit(n);
                    n.visitChildren(visitor);
                }
            }
        }
    }
}

/* JavaCC - OriginalChecksum=0f675acc04cfd880a8baf2208510fa8c (do not edit this line) */
