/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package org.netbeans.api.autoupdate;

import java.io.IOException;
import java.util.List;
import org.netbeans.modules.autoupdate.services.UpdateManagerImpl;

/**
 * @author Jirka Rechtacek
 */
public class RefreshItemsTest extends DefaultTestCase {
    
    public RefreshItemsTest (String testName) {
        super (testName);
    }
    
    public void testRefreshItems () throws IOException {
        List<UpdateUnitProvider> result = UpdateUnitProviderFactory.getDefault ().getUpdateUnitProviders (false);
        assertEquals(result.toString(), 2, result.size());
        int updateUnitsCount = UpdateManager.getDefault().getUpdateUnits(UpdateManager.TYPE.MODULE).size();
        
        UpdateUnit toTest = UpdateManagerImpl.getInstance ().getUpdateUnit ("org.yourorghere.refresh-providers-test");
        assertNotNull ("UpdateUnit for org.yourorghere.refresh-providers-test found.", toTest);
        UpdateElement toTestElement = toTest.getAvailableUpdates().get (0);
        assertNotNull ("UpdateElement for org.yourorghere.refresh-providers-test found.", toTestElement);
        assertTrue (toTestElement + " needs restart.", toTestElement.impl.getInstallInfo().needsRestart ());
        
        populateCatalog(TestUtils.class.getResourceAsStream("data/updates-subset.xml"));
        UpdateUnitProviderFactory.getDefault ().refreshProviders(null, true);
        assertEquals(UpdateManager.getDefault().getUpdateUnits(UpdateManager.TYPE.MODULE).toString(), 
                updateUnitsCount - 2, UpdateManager.getDefault().getUpdateUnits(UpdateManager.TYPE.MODULE).size());
        
        UpdateUnit toTestAgain = UpdateManagerImpl.getInstance ().getUpdateUnit ("org.yourorghere.refresh-providers-test");
        assertNotNull ("Unit for org.yourorghere.refresh-providers-test found.", toTestAgain);
        
        UpdateElement toTestAgainElement = toTestAgain.getAvailableUpdates().get (0);
        assertNotNull ("UpdateElement for org.yourorghere.refresh-providers-test found.", toTestAgainElement);
        
        assertFalse ("First unit is not as same as second unit.", 
                System.identityHashCode(toTest) == System.identityHashCode(toTestAgain));
        assertFalse ("First element is not as same as second element.",
                System.identityHashCode(toTestElement) == System.identityHashCode(toTestAgainElement));
        assertFalse ("IMPLS: First unit is not as same as second unit.", 
                System.identityHashCode(toTest.impl) == System.identityHashCode(toTestAgain.impl));
        assertFalse ("IMPLS: First element is not as same as second element.",
                System.identityHashCode(toTestElement.impl) == System.identityHashCode(toTestAgainElement.impl));
        
        //assertFalse ("First unit is not as same as second unit.", toTest.equals (toTestAgain));
        //assertFalse ("First element is not as same as second element.", toTestElement.equals (toTestAgainElement));
        
        assertFalse (toTestAgainElement + " doesn't need restart now.", toTestAgainElement.impl.getInstallInfo ().needsRestart ());
    }
}
