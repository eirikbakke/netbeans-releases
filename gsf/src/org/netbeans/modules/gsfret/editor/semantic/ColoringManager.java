/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
package org.netbeans.modules.gsfret.editor.semantic;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.spi.editor.highlighting.HighlightAttributeValue;
import static org.netbeans.modules.gsf.api.ColoringAttributes.*;
import org.openide.util.NbBundle;

/**
 * This file is originally from Retouche, the Java Support 
 * infrastructure in NetBeans. I have modified the file as little
 * as possible to make merging Retouche fixes back as simple as
 * possible. 
 *
 * The main modification to this file is to change the static methods
 * into instance methods, and change the ColoringManager from a singleton
 * (hardcoded to the Java mimetype) into a per-mimetype ColoringManager
 * stashed in each Language.
 * 
 *
 * @author Jan Lahoda
 */
public final class ColoringManager {
    private String mimeType;
    private final Map<Set<ColoringAttributes>, String> type2Coloring;
    
    //private static final Font ITALIC = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
    //private static final Font BOLD = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);

    
    public ColoringManager(String mimeType) {
        this.mimeType = mimeType;
        
        type2Coloring = new LinkedHashMap<Set<ColoringAttributes>, String>();
        
        put("mark-occurrences", MARK_OCCURRENCES);
        put("mod-abstract", ABSTRACT);
        put("mod-annotation-type", ANNOTATION_TYPE);
        put("mod-class", CLASS);
        put("mod-constructor", CONSTRUCTOR);
        put("mod-custom1", CUSTOM1);
        put("mod-custom2", CUSTOM2);
        put("mod-custom3", CUSTOM3);
        put("mod-declaration", DECLARATION);
        put("mod-deprecated", DEPRECATED);
        put("mod-enum", ENUM);
        put("mod-field", FIELD);
        put("mod-global", GLOBAL);
        put("mod-interface", INTERFACE);
        put("mod-local-variable", LOCAL_VARIABLE);
        put("mod-method", METHOD);
        put("mod-package-private", PACKAGE_PRIVATE);
        put("mod-parameter", PARAMETER);
        put("mod-private", PRIVATE);
        put("mod-protected", PROTECTED);
        put("mod-public", PUBLIC);
        put("mod-regexp", REGEXP);
        put("mod-static", STATIC);
        put("mod-type-parameter-declaration", TYPE_PARAMETER_DECLARATION);
        put("mod-type-parameter-use", TYPE_PARAMETER_USE);
        put("mod-undefined", UNDEFINED);
        put("mod-unused", UNUSED);
        
        COLORING_MAP.put(ColoringAttributes.UNUSED_SET, getColoring(ColoringAttributes.UNUSED_SET));
        COLORING_MAP.put(ColoringAttributes.FIELD_SET, getColoring(ColoringAttributes.FIELD_SET));
        COLORING_MAP.put(ColoringAttributes.STATIC_FIELD_SET, getColoring(ColoringAttributes.STATIC_FIELD_SET));
        COLORING_MAP.put(ColoringAttributes.PARAMETER_SET, getColoring(ColoringAttributes.PARAMETER_SET));
        COLORING_MAP.put(ColoringAttributes.CUSTOM1_SET, getColoring(ColoringAttributes.CUSTOM1_SET));
        COLORING_MAP.put(ColoringAttributes.CUSTOM2_SET, getColoring(ColoringAttributes.CUSTOM2_SET));
        COLORING_MAP.put(ColoringAttributes.CONSTRUCTOR_SET, getColoring(ColoringAttributes.CONSTRUCTOR_SET));
        COLORING_MAP.put(ColoringAttributes.METHOD_SET, getColoring(ColoringAttributes.METHOD_SET));
        COLORING_MAP.put(ColoringAttributes.CLASS_SET, getColoring(ColoringAttributes.CLASS_SET));
        COLORING_MAP.put(ColoringAttributes.GLOBAL_SET, getColoring(ColoringAttributes.GLOBAL_SET));
        COLORING_MAP.put(ColoringAttributes.REGEXP_SET, getColoring(ColoringAttributes.REGEXP_SET));
        COLORING_MAP.put(ColoringAttributes.STATIC_SET, getColoring(ColoringAttributes.STATIC_SET));
    }
    
    private void put(String coloring, ColoringAttributes... attributes) {
        Set<ColoringAttributes> attribs = EnumSet.copyOf(Arrays.asList(attributes));
        
        type2Coloring.put(attribs, coloring);
    }
    
    Map<Set<ColoringAttributes>,Coloring> COLORING_MAP = new IdentityHashMap<Set<ColoringAttributes>, Coloring>();
    
    public Coloring getColoring(Set<ColoringAttributes> attrs) {
        Coloring c = COLORING_MAP.get(attrs);
        if (c != null) {
            return c;
        }
        
        c = ColoringAttributes.empty();

        for (ColoringAttributes color : attrs) {
            c = ColoringAttributes.add(c, color);
        }
        
        return c;
    }
    
    public AttributeSet getColoringImpl(Coloring colorings) {
        FontColorSettings fcs = MimeLookup.getLookup(MimePath.get(mimeType)).lookup(FontColorSettings.class);
        
        if (fcs == null) {
            //in tests:
            return AttributesUtilities.createImmutable();
        }
        
        assert fcs != null;
        
        List<AttributeSet> attribs = new LinkedList<AttributeSet>();
        
        EnumSet<ColoringAttributes> es = EnumSet.noneOf(ColoringAttributes.class);
        
        es.addAll(colorings);
        
        if (colorings.contains(UNUSED)) {
            attribs.add(AttributesUtilities.createImmutable(EditorStyleConstants.Tooltip, new UnusedTooltipResolver()));
            attribs.add(AttributesUtilities.createImmutable("unused-browseable", Boolean.TRUE));
        }
        
        //colorings = colorings.size() > 0 ? EnumSet.copyOf(colorings) : EnumSet.noneOf(ColoringAttributes.class);
        
        for (Entry<Set<ColoringAttributes>, String> attribs2Colorings : type2Coloring.entrySet()) {
            if (es.containsAll(attribs2Colorings.getKey())) {
                String key = attribs2Colorings.getValue();
                
                es.removeAll(attribs2Colorings.getKey());
                
                if (key != null) {
                    AttributeSet colors = fcs.getTokenFontColors(key);
                    
                    if (colors == null) {
                        Logger.getLogger(ColoringManager.class.getName()).log(Level.SEVERE, "no colors for: {0} with mime" + mimeType, key);
                        continue;
                    }
                    
                    attribs.add(adjustAttributes(colors));
                }
            }
        }
        
        Collections.reverse(attribs);
        
        AttributeSet result = AttributesUtilities.createComposite(attribs.toArray(new AttributeSet[0]));
        
        return result;
    }
    
    private static AttributeSet adjustAttributes(AttributeSet as) {
        Collection<Object> attrs = new LinkedList<Object>();
        
        for (Enumeration<?> e = as.getAttributeNames(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object value = as.getAttribute(key);
            
            if (value != Boolean.FALSE) {
                attrs.add(key);
                attrs.add(value);
            }
        }
        
        return AttributesUtilities.createImmutable(attrs.toArray());
    }
    
    final class UnusedTooltipResolver implements HighlightAttributeValue<String> {

        public String getValue(JTextComponent component, Document document, Object attributeKey, int startOffset, final int endOffset) {
            return NbBundle.getMessage(ColoringManager.class, "LBL_UNUSED");
        }
    }
}
