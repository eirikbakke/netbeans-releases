/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.html.editor.hints;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Hint;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author marekfukala
 */
public abstract class HtmlValidatorRule extends HtmlRule {

    @Override
    public boolean isSpecialHtmlValidatorRule() {
        return true;
    }

    @Override
    protected void run(HtmlRuleContext context, List<Hint> result) {
        Snapshot snapshot = context.getSnapshot();

        List<? extends Error> diagnostics = context.getLeftDiagnostics();
        ListIterator<? extends Error> itr = diagnostics.listIterator();

        while (itr.hasNext()) {
            Error e = itr.next();
            if (!appliesTo(context, e)) {
                continue;
            }

            itr.remove(); //remove the processed element so the other rules won't see it

            //tweak the error position if close to embedding boundary
            int astFrom = e.getStartPosition();
            int astTo = e.getEndPosition();

            int from = snapshot.getOriginalOffset(astFrom);
            int to = snapshot.getOriginalOffset(astTo);

            if (from == -1 && to == -1) {
                //completely unknown position, give up
                continue;
            } else if (from == -1 && to != -1) {
                from = to;
            } else if (from != -1 && to == -1) {
                to = from;
            }

            Hint h = new Hint(this,
                    e.getDescription(),
                    e.getFile(),
                    new OffsetRange(from, to),
                    context.getDefaultFixes(),
                    20);

            if (isEnabled) {
                //if the rule is disabled it will just remove the processed error from the 
                //list but won't create a hint
                result.add(h);
            }


        }
    }

    protected abstract boolean appliesTo(HtmlRuleContext content, Error e);

    
}
