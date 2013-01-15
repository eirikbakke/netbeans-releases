/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.web.jsf.editor.facelets;

import com.sun.faces.config.processor.AbstractConfigProcessor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.faces.application.ProjectStage;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * Since JSF 2.2 {@link AbstractConfigProcessor#loadClass} requires to obtain ServletContext. For needs of tag
 * libraries scanning almost empty {@code ServletContext} should be sufficient. This class is created in the way
 * to prevent raising exceptions from the JSF 2.2 binaries.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
class EmptyServletContext implements ServletContext {

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public ServletContext getContext(String string) {
        return this;
    }

    @Override
    public int getMajorVersion() {
        return -1;
    }

    @Override
    public int getMinorVersion() {
        return -1;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return -1;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return -1;
    }

    @Override
    public String getMimeType(String string) {
        return "";
    }

    @Override
    public Set<String> getResourcePaths(String string) {
        return Collections.emptySet();
    }

    @Override
    public URL getResource(String string) throws MalformedURLException {
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String string) {
        return null;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String string) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String string) {
        return null;
    }

    @Override
    public Servlet getServlet(String string) throws ServletException {
        return null;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return null;
    }

    @Override
    public Enumeration<String> getServletNames() {
        return null;
    }

    @Override
    public void log(String string) {
    }

    @Override
    public void log(Exception excptn, String string) {
    }

    @Override
    public void log(String string, Throwable thrwbl) {
    }

    @Override
    public String getRealPath(String string) {
        return "";
    }

    @Override
    public String getServerInfo() {
        return "";
    }

    @Override
    public String getInitParameter(String string) {
        return "";
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }

    @Override
    public boolean setInitParameter(String string, String string1) {
        return false;
    }

    @Override
    public Object getAttribute(String attribute) {
        final String projectStageKey = AbstractConfigProcessor.class.getName() + ".PROJECTSTAGE";
        if (attribute.equals(projectStageKey)) {
            return ProjectStage.Development;
        }
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public void setAttribute(String string, Object o) {
    }

    @Override
    public void removeAttribute(String string) {
    }

    @Override
    public String getServletContextName() {
        return "";
    }

    @Override
    public Dynamic addServlet(String string, String string1) {
        return null;
    }

    @Override
    public Dynamic addServlet(String string, Servlet srvlt) {
        return null;
    }

    @Override
    public Dynamic addServlet(String string, Class<? extends Servlet> type) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> type) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String string) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String string, String string1) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String string, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String string, Class<? extends Filter> type) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> type) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String string) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.emptyMap();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return Collections.emptySet();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return Collections.emptySet();
    }

    @Override
    public void addListener(String string) {
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
    }

    @Override
    public void addListener(Class<? extends EventListener> type) {
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> type) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... strings) {
    }

}
