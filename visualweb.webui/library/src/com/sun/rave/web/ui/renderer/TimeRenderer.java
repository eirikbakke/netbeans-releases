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

import java.io.IOException;

import javax.faces.FacesException; 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer; 

import com.sun.rave.web.ui.component.DropDown; 
import com.sun.rave.web.ui.component.Time;
import com.sun.rave.web.ui.model.ClockTime;
import com.sun.rave.web.ui.theme.Theme;
import com.sun.rave.web.ui.theme.ThemeStyles;
import com.sun.rave.web.ui.util.MessageUtil;
import com.sun.rave.web.ui.util.RenderingUtilities; 
import com.sun.rave.web.ui.util.ThemeUtilities; 

/**
 *
 * @author avk
 */
public class TimeRenderer extends Renderer {
    
    private static final boolean DEBUG = false;
    
    public void encodeEnd(FacesContext context, UIComponent component) 
    throws IOException {
        
        if(DEBUG) log("encodeEnd() START"); 
        
        if(!(component instanceof Time)) {
            Object[] params = { component.toString(),
                    this.getClass().getName(),
                    Time.class.getName() };
                    String message = MessageUtil.getMessage
                            ("com.sun.rave.web.ui.resources.LogMessages", //NOI18N
                            "Renderer.component", params);              //NOI18N
                    throw new FacesException(message);
        }
        Theme theme = ThemeUtilities.getTheme(context); 
        Time time = (Time)component;
        DropDown hourMenu = time.getHourMenu(); 
        DropDown minuteMenu = time.getMinutesMenu();
        
        // If there is no submitted value, set the values of 
        // the DropDowns to the actual value... If we have 
        // a submitted value, the DropDown will remember it 
        // so we do nothing in that case. 
        // FIXME: have to round this to the nearest five minutes!       
        if(time.getSubmittedValue() == null) {
            
            if(DEBUG) log("No submitted value"); 
            Object object = time.getValue();
            if(DEBUG) log("Got the ClockTime"); 
            
            ClockTime value = null;
            if(object != null && object instanceof ClockTime) {
                value = (ClockTime)object;
                if(DEBUG) log("\tValue is " + String.valueOf(value));
            }           
            if(value != null) {
                hourMenu.setValue(value.getHour());
            } else {
                hourMenu.setValue(new Integer(-1));
            }
            
            if(value != null) {
                minuteMenu.setValue(value.getMinute());
            } else {
                minuteMenu.setValue(new Integer(-1));
            }
        }
        else if(DEBUG) log("Found submitted value"); 

        String key = time.getHourTooltipKey();
        if(key != null) {
            hourMenu.setToolTip(theme.getMessage(key));
        }    
        
        key = time.getMinutesTooltipKey();
        if(key != null) {
            minuteMenu.setToolTip(theme.getMessage(key));
        }
        
        ResponseWriter writer = context.getResponseWriter(); 
        
        writer.startElement("table", time); //NOI18N
        writer.writeAttribute("cellspacing", "0", null); // NOI18N
        writer.writeAttribute("cellpadding", "0", null); // NOI18N
        writer.writeText("\n", null); //NOI18N
        writer.startElement("tr", time); //NOI18N
        writer.writeText("\n", null); //NOI18N
        
        // hour menu
        writer.startElement("td", time);    //NOI18N
        writer.writeText("\n", null); //NOI18N
        RenderingUtilities.renderComponent(hourMenu, context); 
        writer.writeText("\n", null); //NOI18N
        writer.endElement("td");   //NOI18N
        writer.writeText("\n", null); //NOI18N
        
        // colon
        writer.startElement("td", time);
        writer.write(":"); 
        writer.endElement("td");
        writer.writeText("\n", null); //NOI18N
      
        // minutes menu
        writer.startElement("td", time); //NOI18N
        writer.writeText("\n", null); //NOI18N
        RenderingUtilities.renderComponent(minuteMenu, context); 
        writer.writeText("\n", null); //NOI18N
        
        // Should use another cell for this, and a width attribute instad
        //writer.write("&nbsp;");
        
        writer.startElement("span", time); //NOI18N
         
        String string =
            theme.getStyleClass(ThemeStyles.DATE_TIME_ZONE_TEXT);        
        writer.writeAttribute("class", string, null); // NOI18N    
        writer.writeText(theme.getMessage("Time.gmt"), null); 
        writer.writeText(time.getOffset(), null); 
        writer.endElement("span"); //NOI18N
        writer.writeText("\n", null); //NOI18N
        
        writer.endElement("td");   //NOI18N
        writer.writeText("\n", null); //NOI18N
        writer.endElement("tr");   //NOI18N
        writer.writeText("\n", null); //NOI18N
        writer.endElement("table"); //NOI18N
        writer.writeText("\n", null); //NOI18N
        
        if(DEBUG) log("encodeEnd() END"); 
    }

    public void encodeChildren(FacesContext context, UIComponent component) {
        return;
    }

    public void encodeBegin(FacesContext context, UIComponent component) {
        return;
    }

    public String toString() {
        return this.getClass().getName(); 
    }

    public boolean getRendersChildren() {
        return true;
    }
    
    private void log(String s) {
        System.out.println(this.getClass().getName() + "::" + s);
    }
}
