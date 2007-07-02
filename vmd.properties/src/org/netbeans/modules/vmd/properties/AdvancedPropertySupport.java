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

package org.netbeans.modules.vmd.properties;

import java.lang.reflect.InvocationTargetException;
import org.netbeans.modules.vmd.api.properties.DesignPropertyEditor;
import org.netbeans.modules.vmd.api.properties.DesignPropertyDescriptor;
import org.netbeans.modules.vmd.api.properties.GroupValue;

/**
 *
 * @author Karol Harezlak
 */
public final class AdvancedPropertySupport extends DefaultPropertySupport {
    
    private String displayName;
    private GroupValue value;
    
    public AdvancedPropertySupport(final DesignPropertyDescriptor designerPropertyDescriptor, Class type) {
        super(designerPropertyDescriptor, type);
    }
    
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }
    
    public void setValue(final Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (getPropertyEditor() instanceof DesignPropertyEditor) {
            DesignPropertyEditor propertyEditor = (DesignPropertyEditor) getPropertyEditor();
            if (propertyEditor.canEditAsText() != null)
                setValue("canEditAsText", propertyEditor.canEditAsText()); //NOI18N
        }
        if (value instanceof GroupValue) {
            this.value = (GroupValue) value;
            if (getPropertyEditor() instanceof DesignPropertyEditor)
                SaveToModelSupport.saveToModel(getDesignPropertyDescriptor().getComponent(), this.value, (DesignPropertyEditor) getPropertyEditor());
            else
                SaveToModelSupport.saveToModel(getDesignPropertyDescriptor().getComponent(), this.value, null);
        } else
            throw new IllegalArgumentException("Wrong type"); //NOI18N
    }
    
    public String getHtmlDisplayName() {
        if (getDesignPropertyDescriptor().getPropertyNames().isEmpty())
            return getDesignPropertyDescriptor().getPropertyDisplayName();
        
        getDesignPropertyDescriptor().getComponent().getDocument().getTransactionManager().readAccess(new Runnable() {
            public void run() {
                boolean isDefault = false;
                
                for (String propertyName : getDesignPropertyDescriptor().getPropertyNames()) {
                    if (getDesignPropertyDescriptor().getComponent().isDefaultValue(propertyName)) {
                        isDefault = false;
                        return;
                    }
                }
                if (isDefault)
                    displayName = getDesignPropertyDescriptor().getPropertyDisplayName();
                else
                    displayName = "<b>" + getDesignPropertyDescriptor().getPropertyDisplayName()+"</b>";  // NOI18N
            }
        });
        
        return displayName;
    }

    protected void update() {
        this.value = new GroupValue(getDesignPropertyDescriptor().getPropertyNames());
        if (getDesignPropertyDescriptor().getPropertyNames() !=null &&! getDesignPropertyDescriptor().getPropertyNames().isEmpty()) {
            for (String propertyName : getDesignPropertyDescriptor().getPropertyNames()) {
                value.putValue(propertyName, readPropertyValue(getDesignPropertyDescriptor().getComponent(), propertyName));
            }
        }
        if (getPropertyEditor() instanceof DesignPropertyEditor) {
            DesignPropertyEditor propertyEditor = (DesignPropertyEditor)getPropertyEditor();
            propertyEditor.resolve(
                    getDesignPropertyDescriptor().getComponent(),
                    getDesignPropertyDescriptor().getPropertyNames(),
                    this.value,
                    this,
                    getDesignPropertyDescriptor().getPropertyDisplayName()
            );
            propertyEditor.resolveInplaceEditor(propertyEditor.getInplaceEditor());
            String title = propertyEditor.getCustomEditorTitle();
            if ( title != null)
                setValue(PROPERTY_CUSTOM_EDITOR_TITLE, title);
        }
    }
    
}
