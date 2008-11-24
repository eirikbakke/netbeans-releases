/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html or
 * http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file and
 * include the License file at http://www.netbeans.org/cddl.txt. If applicable, add
 * the following below the CDDL Header, with the fields enclosed by brackets []
 * replaced by your own identifying information:
 *
 *     "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * $Id$
 *
 */

package org.netbeans.test.installer;

import java.util.logging.Logger;

/**
 *
 * @author Mikhail Vaysman
 */
public class InstallerAndUninstallerAllTest{

    @org.junit.Test
    public void testInstaller() {
        TestData data = new TestData(Logger.getLogger("global"));

        Utils.phaseOne(data, "all");

        //select apache
        Utils.stepChooseComponet("Apache Tomcat");

        Utils.phaseTwo(data);

        Utils.phaseThree(data);

        Utils.phaseFour(data);

        //TODO Dir removed test
        //TODO Clean up work dir
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.runClasses(InstallerAndUninstallerAllTest.class);
    }
}
