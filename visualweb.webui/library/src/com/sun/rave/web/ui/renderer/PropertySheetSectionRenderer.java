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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package com.sun.rave.web.ui.renderer;

import com.sun.rave.web.ui.component.PropertySheetSection;
import com.sun.rave.web.ui.theme.Theme;
import com.sun.rave.web.ui.theme.ThemeStyles;
import com.sun.rave.web.ui.util.RenderingUtilities;
import com.sun.rave.web.ui.util.ThemeUtilities;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class PropertySheetSectionRenderer extends Renderer {

    /**
     * Creates a new instance of PropertySheetSectionRenderer.
     */
    public PropertySheetSectionRenderer() {
    }

    /**
     * This renderer renders the component's children.
     */
    public boolean getRendersChildren() {
	return true;
    }

    /**
     * Render a property sheet.
     * 
     * @param context The current FacesContext
     * @param component The PropertySheet object to render
     * @param writer The current ResponseWriter
     *
     * @exception IOException if an input/output error occurs
     */
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException();
        }

	if (!component.isRendered()) {
	    return;
	}

	ResponseWriter writer = context.getResponseWriter();

	PropertySheetSection propertySheetSection =
		(PropertySheetSection) component;

	// Get the theme
	//
	Theme theme = ThemeUtilities.getTheme(context);

	renderPropertySheetSection(context, propertySheetSection, theme,
	    writer);
    }    

    // There is an extensive use of the request map by the
    // template renderer.
    // The setAttribute handler places the key/value pair in the
    // request map.
    //
    /**
     * Render the property sheet sections.
     * 
     * @param context The current FacesContext
     * @param propertySheet The PropertySheet object to render
     * @param theme The Theme to reference.
     * @param writer The current ResponseWriter
     *
     * @exception IOException if an input/output error occurs
     */
    protected void renderPropertySheetSection(FacesContext context,
	    PropertySheetSection propertySheetSection, Theme theme, 
	    ResponseWriter writer) throws IOException {

	int numChildren = propertySheetSection.getSectionChildrenCount();
	if (numChildren <= 0) {
	    return;
	}

	writer.startElement("div", propertySheetSection);
	writer.writeAttribute("id",
		propertySheetSection.getClientId(context), "id");//NOI18N
	String propValue = RenderingUtilities.getStyleClasses(context,
		propertySheetSection,
		theme.getStyleClass(ThemeStyles.CONTENT_FIELDSET));
	writer.writeAttribute("class", propValue, null);

	// There was a distinction made between ie and other browsers.
	// If the browser was ie, fieldsets were used, and if not
	// divs were used. Why ? Just use divs here.
	//
	writer.startElement("div", propertySheetSection);
	writer.writeAttribute("class",
		theme.getStyleClass(ThemeStyles.CONTENT_FIELDSET_DIV), null);

	// Render the section label
	// Why isn't this a label facet on PropertySheetSection, too ?
	//
	propValue = propertySheetSection.getLabel();
	if (propValue != null) {
	    writer.startElement("div", propertySheetSection);
	    writer.writeAttribute("class",
		theme.getStyleClass(ThemeStyles.CONTENT_FIELDSET_LEGEND_DIV),
		null);
	    writer.writeText(propValue, null);
	    writer.endElement("div");
	}

	renderProperties(context, propertySheetSection, theme, writer);

	writer.endElement("div");
	writer.endElement("div");
    }

    /**
     * Render a required fields legend.
     * If <code>propertySheet.getRequiredFields</code> returns null
     * a spacer is rendered.
     * 
     * @param context The current FacesContext
     * @param propertySheet The PropertySheet object to render
     * @param theme The Theme to reference.
     * @param writer The current ResponseWriter
     *
     * @exception IOException if an input/output error occurs
     */
    protected void renderProperties(FacesContext context,
	    PropertySheetSection propertySheetSection, Theme theme,
	    ResponseWriter writer) throws IOException {


	List properties = propertySheetSection.getVisibleSectionChildren();

	writer.startElement("table", propertySheetSection);
	writer.writeAttribute("border", "0", null);
	writer.writeAttribute("cellspacing", "0", null);
	writer.writeAttribute("cellpadding", "0", null);
	writer.writeAttribute("title", "", null); //NOI18N

	// Unfortunately the PropertyRenderer needs to render
	// a TR and TD since we are opening a table context here.
	// This can't be changed easily unless a strategy like the
	// radio button and checkbox group renderer is used, where there is 
	// a table layout renderer. I'm not sure if that is sufficiently
	// robust to handle "properties".
	//

        Iterator propertiesIterator = properties.iterator();
	while (propertiesIterator.hasNext()) {
            UIComponent property = (UIComponent) propertiesIterator.next();
	    RenderingUtilities.renderComponent(property, context);
	}

	writer.endElement("table");
    }

    /**
     * Render a property sheet.
     * 
     * @param context The current FacesContext
     * @param component The PropertySheet object to render
     * @param writer The current ResponseWriter
     *
     * @exception IOException if an input/output error occurs
     */
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
    }    
}
