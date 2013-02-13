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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.tasklist.todo.settings;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author S. Aubrecht
 */
final public class Settings {
    
    public static final String PROP_PATTERN_LIST = "patternList"; //NOI18N
    public static final String PROP_SCAN_COMMENTS_ONLY = "scanCommentsOnly"; //NOI18N
    public static final String PROP_IDENTIFIERS_LIST = "identifiersList"; //NOI18N
    
    private static Settings theInstance;
    private static final String OBJECT_DELIMITER = "|"; //NOI18N
    private static final String FIELD_DELIMITER = ","; //NOI18N

    private final ArrayList<String> patterns = new ArrayList<String>( 10 );
    private Map<String, ExtensionIdentifier> ext2comments = new HashMap<String, ExtensionIdentifier>();
    private Map<String, MimeIdentifier> mime2comments = new HashMap<String, MimeIdentifier>();
    private final Map<String, ExtensionIdentifier> ext2commentsDefault = new HashMap<String, ExtensionIdentifier>(25);
    private final Map<String, MimeIdentifier> mime2commentsDefault = new HashMap<String, MimeIdentifier>(25);
    private boolean scanCommentsOnly = true;
    
    private PropertyChangeSupport propertySupport;
    private static final String MIME_IDENTIFIERS = "mimeidentifiers";
    private static final String EXT_IDENTIFIERS = "extidentifiers";
    
    /** Creates a new instance of Settings */
    private Settings() {
        patterns.addAll( decodePatterns( getPreferences().get( "patterns",  //NOI18N
                "@todo|TODO|FIXME|XXX|PENDING|<<<<<<<" )) ); //NOI18N
        
        scanCommentsOnly = getPreferences().getBoolean( "scanCommentsOnly", true ); //NOI18N
        
        ext2commentsDefault.put( "JAVA", new ExtensionIdentifier("JAVA", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "JS", new ExtensionIdentifier("JS", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "C", new ExtensionIdentifier("C", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "CPP", new ExtensionIdentifier("CPP", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "CXX", new ExtensionIdentifier("CXX", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "CC", new ExtensionIdentifier("CC", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "H", new ExtensionIdentifier("H", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "HPP", new ExtensionIdentifier("HPP", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "HTML", new ExtensionIdentifier("HTML", new CommentTags( "<!--", "-->"))); //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "HTM", new ExtensionIdentifier("HTM", new CommentTags( "<!--", "-->"))); //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "XML", new ExtensionIdentifier("XML", new CommentTags( "<!--", "-->"))); //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "JSP", new ExtensionIdentifier("JSP", new CommentTags( "<%--", "--%>"))); //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "CSS", new ExtensionIdentifier("CSS", new CommentTags( "/*", "*/"))); //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "PROPERTIES", new ExtensionIdentifier("PROPERTIES", new CommentTags("#"))); //NOI18N //NOI18N
        ext2commentsDefault.put( "SH", new ExtensionIdentifier("SH", new CommentTags("#"))); //NOI18N //NOI18N
        ext2commentsDefault.put( "RB", new ExtensionIdentifier("RB", new CommentTags("#"))); //NOI18N //NOI18N
        ext2commentsDefault.put( "PHP", new ExtensionIdentifier("PHP", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "SCALA", new ExtensionIdentifier("SCALA", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "GROOVY", new ExtensionIdentifier("GROOVY", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "FX", new ExtensionIdentifier("FX", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        ext2commentsDefault.put( "TWIG", new ExtensionIdentifier("TWIG", new CommentTags( "{#", "#}"))); //NOI18N //NOI18N //NOI18N
        
        mime2commentsDefault.put( "text/x-java", new MimeIdentifier("text/x-java", "Java Files", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        mime2commentsDefault.put( "text/html", new MimeIdentifier("text/html", "HTML Files", new CommentTags( "<!--", "-->"))); //NOI18N //NOI18N //NOI18N
        mime2commentsDefault.put( "application/x-httpd-eruby", new MimeIdentifier("application/x-httpd-eruby", "", new CommentTags( "<!--", "-->"))); //NOI18N //NOI18N //NOI18N
        mime2commentsDefault.put( "text/x-yaml", new MimeIdentifier("text/x-yaml", "Yaml Files", new CommentTags("#"))); //NOI18N //NOI18N
        mime2commentsDefault.put( "text/x-python", new MimeIdentifier("text/x-python", "Python Files", new CommentTags("#"))); //NOI18N //NOI18N
        mime2commentsDefault.put( "text/x-fx", new MimeIdentifier("text/x-fx", "JavaFX Files", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N

        // Ruby, PHP, etc have file extensions listed above, but they are listed here by mime type as well
        // because there are many other common file extensions used for them.
        mime2commentsDefault.put( "text/x-ruby", new MimeIdentifier("text/x-ruby", "Ruby Files", new CommentTags("#"))); //NOI18N //NOI18N
        mime2commentsDefault.put( "text/x-php5", new MimeIdentifier("text/x-php", "PHP Files", new CommentTags( "//", "/*", "*/"))); //NOI18N //NOI18N //NOI18N //NOI18N
        mime2commentsDefault.put( "text/sh", new MimeIdentifier("text/sh", "", new CommentTags("#"))); //NOI18N //NOI18N


        String encodedMime = getPreferences().get(MIME_IDENTIFIERS, "");
        if (encodedMime.isEmpty()) {
            mime2comments = new HashMap<String, MimeIdentifier>(mime2commentsDefault);
        } else {
            mime2comments = decodeMimeIdentifiers(encodedMime);
        }

        String encodedExt = getPreferences().get(EXT_IDENTIFIERS, "");
        if (encodedExt.isEmpty()) {
            ext2comments = new HashMap<String, ExtensionIdentifier>(ext2commentsDefault);
        } else {
            ext2comments = decodeExtIdentifiers(encodedExt);
        }
    }
    
    public static final Settings getDefault() {
        if( null == theInstance )
            theInstance = new Settings();
        return theInstance;
    }
    
    public Collection<String> getPatterns() {
        return Collections.unmodifiableCollection( patterns );
    }
    
    public void setPatterns( Collection<String> newPatterns ) {
        patterns.clear();
        patterns.addAll( newPatterns );
        getPreferences().put( "patterns", encodePatterns(newPatterns) ); //NOI18N
        if( null == propertySupport )
            propertySupport = new PropertyChangeSupport( this );
        propertySupport.firePropertyChange( PROP_PATTERN_LIST, null, getPatterns() );
    }

    public List<MimeIdentifier> getMimeIdentifiers() {
        ArrayList<MimeIdentifier> arrayList = new ArrayList<MimeIdentifier>(mime2comments.values());
        Collections.sort(arrayList);
        return arrayList;
    }

    public List<ExtensionIdentifier> getExtensionIdentifiers() {
        ArrayList<ExtensionIdentifier> arrayList = new ArrayList<ExtensionIdentifier>(ext2comments.values());
        Collections.sort(arrayList);
        return arrayList;
    }

    public void setIdentifiers(List<MimeIdentifier> mimeIdentifiers, List<ExtensionIdentifier> extensionIdentifiers) {
        mime2comments.clear();
        for (MimeIdentifier mimeIdentifier : mimeIdentifiers) {
            mime2comments.put(mimeIdentifier.getId(), mimeIdentifier);
        }
        getPreferences().put(MIME_IDENTIFIERS, encodeMimeIdentifiers(mimeIdentifiers)); //NOI18N

        ext2comments.clear();
        for (ExtensionIdentifier extensionIdentifier : extensionIdentifiers) {
            ext2comments.put(extensionIdentifier.getId(), extensionIdentifier);
        }
        getPreferences().put(EXT_IDENTIFIERS, encodeExtIdentifiers(extensionIdentifiers)); //NOI18N
        if (null == propertySupport) {
            propertySupport = new PropertyChangeSupport(this);
        }
        propertySupport.firePropertyChange(PROP_IDENTIFIERS_LIST, null, getPatterns());
    }
    
    public boolean isExtensionSupported( String fileExtension ) {
        return null != ext2comments.get( fileExtension.toUpperCase() );
    }
    
    public boolean isMimeTypeSupported( String mimeType ) {
        return null != mime2comments.get( mimeType );
    }
  
    public String getLineComment(String fileExtension, String mime) {
        CommentTags ct = ext2comments.get(fileExtension.toUpperCase()).getCommentTags();
        if (null == ct) {
            ct = mime2comments.get(mime).getCommentTags();
        }
        return null == ct ? null : ct.getLineComment();
    }

    public String getBlockCommentStart(String fileExtension, String mime) {
        CommentTags ct = ext2comments.get(fileExtension.toUpperCase()).getCommentTags();
        if (null == ct) {
            ct = mime2comments.get(mime).getCommentTags();
        }
        return null == ct ? null : ct.getBlockCommentStart();
    }

    public String getBlockCommentEnd(String fileExtension, String mime) {
        CommentTags ct = ext2comments.get(fileExtension.toUpperCase()).getCommentTags();
        if (null == ct) {
            ct = mime2comments.get(mime).getCommentTags();
        }
        return null == ct ? null : ct.getBlockCommentEnd();
    }
    
    public boolean isScanCommentsOnly() {
        return scanCommentsOnly;
    }
    
    public void setScanCommentsOnly( boolean val ) {
        boolean oldVal = scanCommentsOnly;
        this.scanCommentsOnly = val;
        getPreferences().putBoolean( "scanCommentsOnly", val ); //NOI18N
        if( null == propertySupport )
            propertySupport = new PropertyChangeSupport( this );
        propertySupport.firePropertyChange( PROP_SCAN_COMMENTS_ONLY, oldVal, val );
    }
    
    public void addPropertyChangeListener( PropertyChangeListener l ) {
        if( null == propertySupport )
            propertySupport = new PropertyChangeSupport( this );
        propertySupport.addPropertyChangeListener( l );
    }
    
    public void removePropertyChangeListener( PropertyChangeListener l ) {
        if( null != propertySupport )
            propertySupport.removePropertyChangeListener( l );
    }
    
    private Preferences getPreferences() {
        return NbPreferences.forModule( Settings.class );
    }
    
    private static Collection<String> decodePatterns( String encodedPatterns ) {
        StringTokenizer st = new StringTokenizer( encodedPatterns, OBJECT_DELIMITER, false );
        
        Collection<String> patterns = new ArrayList<String>();
        
        while( st.hasMoreTokens() ) {
            String im = st.nextToken();
            patterns.add(im);
        }
        
        return patterns;
    }
    
    private static String encodePatterns( Collection<String> patterns ) {
        StringBuilder sb = new StringBuilder();
        
        for( String p : patterns ) {
            sb.append( p );
            sb.append( OBJECT_DELIMITER );
        }
        
        return sb.toString();
    }

    private static Map<String, MimeIdentifier> decodeMimeIdentifiers(String encodedIdentifiers) {
        StringTokenizer st = new StringTokenizer(encodedIdentifiers, OBJECT_DELIMITER, false);

        Map<String, MimeIdentifier> mimeIdentifiers = new HashMap<String, MimeIdentifier>(st.countTokens());
        while (st.hasMoreTokens()) {
            String im = st.nextToken();
            String[] fields = im.split(FIELD_DELIMITER, 5);
            MimeIdentifier mimeIdentifier = new MimeIdentifier(fields[0], fields[1], new CommentTags(fields[2], fields[3], fields[4]));
            mimeIdentifiers.put(mimeIdentifier.getId(), mimeIdentifier);
        }
        return mimeIdentifiers;
    }

    private static String encodeMimeIdentifiers(Collection<MimeIdentifier> identifiers) {
        StringBuilder sb = new StringBuilder();

        for (MimeIdentifier identifier : identifiers) {
            sb.append(identifier.getMimeType());
            sb.append(FIELD_DELIMITER);
            sb.append(identifier.getMimeName());
            sb.append(FIELD_DELIMITER);
            sb.append(encodeCommentTags(identifier.getCommentTags()));
            sb.append(OBJECT_DELIMITER);
        }
        return sb.toString();
    }

    private static Map<String, ExtensionIdentifier> decodeExtIdentifiers(String encodedIdentifiers) {
        StringTokenizer st = new StringTokenizer(encodedIdentifiers, OBJECT_DELIMITER, false);

        Map<String, ExtensionIdentifier> extensionIdentifiers = new HashMap<String, ExtensionIdentifier>(st.countTokens());
        while (st.hasMoreTokens()) {
            String im = st.nextToken();
            String[] fields = im.split(FIELD_DELIMITER, 4);
            ExtensionIdentifier extensionIdentifier = new ExtensionIdentifier(fields[0], new CommentTags(fields[1], fields[2], fields[3]));
            extensionIdentifiers.put(extensionIdentifier.getId(), extensionIdentifier);
        }
        return extensionIdentifiers;
    }

    private static String encodeExtIdentifiers(Collection<ExtensionIdentifier> identifiers) {
        StringBuilder sb = new StringBuilder();

        for (ExtensionIdentifier identifier : identifiers) {
            sb.append(identifier.getExtension());
            sb.append(FIELD_DELIMITER);
            sb.append(encodeCommentTags(identifier.getCommentTags()));
            sb.append(OBJECT_DELIMITER);
        }
        return sb.toString();
    }

    private static String encodeCommentTags(CommentTags tags) {
        StringBuilder sb = new StringBuilder();
        sb.append(tags.isLineCommentEnabled() ? tags.getLineComment() : "");
        sb.append(FIELD_DELIMITER);
        sb.append(tags.isBlockCommentEnabled() ? tags.getBlockCommentStart() : "");
        sb.append(FIELD_DELIMITER);
        sb.append(tags.isBlockCommentEnabled() ? tags.getBlockCommentEnd() : "");
        return sb.toString();
    }
}
