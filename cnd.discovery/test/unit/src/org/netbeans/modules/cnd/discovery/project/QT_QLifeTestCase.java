/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.discovery.project;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.openide.util.Utilities;

/**
 *
 * @author Alexander Simon
 */
public class QT_QLifeTestCase extends MakeProjectBase {

    public QT_QLifeTestCase() {
        super("QLife");
    }

    @Override
    protected List<String> requiredTools() {
        List<String> res = new ArrayList<String>(super.requiredTools());
        res.add("qmake");
        res.add("sed");
        res.add("mv");
        return res;
    }

    @Test
    public void testQLife(){
        List<String> list = new ArrayList<String>();
        //list.add("qmake qlife.pro");
        if (Utilities.isWindows()) {
            // There are troubles with generated Makefile on Windows - attempt to fix it.
            // Note: MSYS make is required to run patched Makefile.
            list.add("sed -e 's:\\\\\\(.\\):/\\1:g' Makefile >Makefile.tmp");
            list.add("mv Makefile.tmp Makefile");
        }
        performTestProject("http://personal.inet.fi/koti/rkauppila/projects/life/qlife-qt4-0.9.tar.gz", list);
    }
}
