/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.cnd.makeproject.api.platforms;

import org.netbeans.modules.cnd.toolchain.api.CompilerSet;
import org.netbeans.modules.cnd.api.utils.IpeUtils;
import org.netbeans.modules.cnd.makeproject.api.configurations.LibraryItem;

public class PlatformSolaris  extends Platform {
    private static final LibraryItem.StdLibItem[] standardLibrariesSolaris = {
        StdLibraries.getStandardLibary("Motif"), // NOI18N
        StdLibraries.getStandardLibary("Mathematics"), // NOI18N
        StdLibraries.getStandardLibary("Yacc"), // NOI18N
        StdLibraries.getStandardLibary("Lex"), // NOI18N
        StdLibraries.getStandardLibary("SocketsNetworkServices"), // NOI18N
        StdLibraries.getStandardLibary("SolarisThreads"), // NOI18N
        StdLibraries.getStandardLibary("PosixThreads"), // NOI18N
        StdLibraries.getStandardLibary("Posix4"), // NOI18N
        StdLibraries.getStandardLibary("Internationalization"), // NOI18N
        StdLibraries.getStandardLibary("PatternMatching"), // NOI18N
        StdLibraries.getStandardLibary("Curses"), // NOI18N
    };
    
    public PlatformSolaris(String name, String displayName, int id) {
        super(name, displayName, id);
    }
    
    public LibraryItem.StdLibItem[] getStandardLibraries() {
        return standardLibrariesSolaris;
    }
    
    public String getLibraryName(String baseName) {
        return "lib" + baseName + ".so"; // NOI18N
    }
    
    public String getLibraryLinkOption(String libName, String libDir, String libPath, CompilerSet compilerSet) {
        if (libName.endsWith(".so")) { // NOI18N
            int i = libName.indexOf(".so"); // NOI18N
            if (i > 0) {
                libName = libName.substring(0, i);
            }
            if (libName.startsWith("lib")) { // NOI18N
                libName = libName.substring(3);
            }
            return compilerSet.getDynamicLibrarySearchOption() + IpeUtils.escapeOddCharacters(libDir)
                    + " " + compilerSet.getLibrarySearchOption() + IpeUtils.escapeOddCharacters(libDir) // NOI18N
                    + " " + compilerSet.getLibraryOption() + IpeUtils.escapeOddCharacters(libName); // NOI18N
        } else {
            return IpeUtils.escapeOddCharacters(libPath);
        }
    }
}
