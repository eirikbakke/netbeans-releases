/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.spellchecker.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.text.JTextComponent;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;


/**
 *
 * @author hanz
 */
public class Spellchecker {

    public static void register (
        JTextComponent          textComponent
    ) {
        ClassLoader systemClassLoader = Lookup.getDefault ().lookup (ClassLoader.class);
        try {
            Class componentPeerClass = systemClassLoader.loadClass ("org.netbeans.modules.spellchecker.ComponentPeer");
            Method assureInstalledMethod = componentPeerClass.getMethod ("assureInstalled", Class.forName ("javax.swing.text.JTextComponent"));
            assureInstalledMethod.invoke (null, textComponent);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace (ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace (ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace (ex);
        } catch (ClassNotFoundException ex) {
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace (ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace (ex);
        }
    }
}
