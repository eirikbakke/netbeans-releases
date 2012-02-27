/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.inspect;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * Model of an inspected web-page.
 *
 * @author Jan Stola
 */
public abstract class PageModel {
    /** Name of the property that is fired when the set of selected elements is changed. */
    public static final String PROP_SELECTED_ELEMENTS = "selectedElements"; // NOI18N
    /** Property change support. */
    private PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);

    /**
     * Returns a simplified DOM of the page that can be used to determine
     * the structure of the elements only. It doesn't contain any other
     * information (like attributes or style information).
     * 
     * @return simplified DOM of the page.
     */
    public abstract Document getDocument();

    /**
     * Sets the selected elements in the page.
     * 
     * @param elements elements to select in the page.
     */
    public abstract void setSelectedElements(Collection<ElementHandle> elements);

    /**
     * Returns selected elements.
     * 
     * @return selected elements.
     */
    public abstract Collection<ElementHandle> getSelectedElements();

    /**
     * Returns attributes of the specified element.
     * 
     * @param element element whose attributes should be returned.
     * @return map with attribute information, it maps the name of
     * the attribute to its value.
     */
    public abstract Map<String,String> getAtrributes(ElementHandle element);

    /**
     * Returns computed style of the specified element.
     * 
     * @param element element whose computed style should be returned.
     * @return map with computed style information, it maps the name
     * of the style property to its value.
     */
    public abstract Map<String,String> getComputedStyle(ElementHandle element);

    /**
     * Returns resources (like scripts, images and style sheets) used by the page.
     * 
     * @return resources used by the page.
     */
    public abstract Collection<ResourceInfo> getResources();

    /**
     * Returns style rules that match the specified element.
     * 
     * @param element element whose matching style rules should be returned.
     * @return style rules that match the specified element.
     */
    public abstract List<RuleInfo> getMatchedRules(ElementHandle element);

    /**
     * Reloads the specified resource in the page.
     * 
     * @param resource resource to reload.
     */
    public abstract void reloadResource(ResourceInfo resource);

    /**
     * Adds a property change listener.
     * 
     * @param listener listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener.
     * 
     * @param listener listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Fires the specified property change.
     * 
     * @param propName name of the property.
     * @param oldValue old value of the property or {@code null}.
     * @param newValue new value of the property or {@code null}.
     */
    protected void firePropertyChange(String propName, Object oldValue, Object newValue) {
        propChangeSupport.firePropertyChange(propName, oldValue, newValue);
    }

    /**
     * Information about a resource (like script, image or style sheet) used by a page.
     */
    public static class ResourceInfo {
        /** Type of the resource. */
        private Type type;
        /** URL of the resource. */
        private String url;

        /**
         * Creates a new {@code ResourceInfo}.
         * 
         * @param type type of the resource.
         * @param url URL of the resource.
         */
        public ResourceInfo(Type type, String url) {
            this.type = type;
            this.url = url;
        }

        /**
         * Returns the type of the resource.
         * 
         * @return type of the resource.
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns the URL of the resource.
         * 
         * @return URL of the resource.
         */
        public String getURL() {
            return url;
        }

        /**
         * Type of a resource.
         */
        public enum Type {
            /** HTML of the page itself. */
            HTML("html"), // NOI18N
            /** Style sheet. */
            STYLESHEET("styleSheet"), // NOI18N
            /** Script. */
            SCRIPT("script"), // NOI18N
            /** Image. */
            IMAGE("image"); // NOI18N

            /** Code of the resource type. */
            private String code;

            /**
             * Creates a new {@code Type}.
             * 
             * @param code code of the resource type.
             */
            private Type(String code) {
                this.code = code;
            }

            /**
             * Returns the code of this resource type.
             * 
             * @return code of this resource type.
             */
            public String getCode() {
                return code;
            }

            /**
             * Returns type of a resource from its code.
             * 
             * @param code code of the resource type.
             * @return type of a resource from its code.
             */
            public static Type fromCode(String code) {
                Type result = null;
                for (Type type : values()) {
                    if (type.code.equals(code)) {
                        result = type;
                        break;
                    }
                }
                return result;
            }
        };
    }

    /**
     * Information about a CSS/style rule.
     */
    public static class RuleInfo {
        /** URL of the style sheet this rule comes from. */
        private String sourceURL;
        /** Selector of this rule. */
        private String selector;
        /**
         * Style information of the rule - maps the name of style attribute
         * (specified by this rule) to its value.
         */
        private Map<String,String> style;

        /**
         * Creates a new {@code RuleInfo}.
         * 
         * @param sourceURL URL of the style sheet the rule comes from.
         * @param selector selector of the rule.
         * @param style style information of the rule.
         */
        public RuleInfo(String sourceURL, String selector, Map<String,String> style) {
            this.sourceURL = sourceURL;
            this.selector = selector;
            this.style = style;
        }

        /**
         * Returns URL of the style sheet this rule comes from.
         * 
         * @return URL of the style sheet this rule comes from.
         */
        public String getSourceURL() {
            return sourceURL;
        }

        /**
         * Returns the selector of this rule.
         * 
         * @return selector of this rule.
         */
        public String getSelector() {
            return selector;
        }

        /**
         * Returns style information of the rule.
         * 
         * @return style information of the rule.
         */
        public Map<String,String> getStyle() {
            return style;
        }
        
    }
    
}
