/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.git.ui.commit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import org.netbeans.modules.spellchecker.api.Spellchecker;
import org.netbeans.modules.versioning.util.UndoRedoSupport;

/**
 *
 * @author Ondrej Vrabec
 */
public class MessageArea extends JTextArea {
    private UndoRedoSupport um;
    private int messageWidth;
    
    @Override
    public void paint (Graphics g) {
        super.paint(g);
        if (messageWidth > 0) {
            int x = messageWidth * g.getFontMetrics().charWidth(' ');
            Rectangle rect = getVisibleRect();
            if (rect.contains(new Point(x, rect.y))) {
                g.setColor(Color.red);
                g.drawLine(x, rect.y, x, rect.y + rect.height);
            }
        }
    }

    /**
     * Must be called when the area is brought to visible.
     */
    public void open () {
        if (um == null) {
            um = UndoRedoSupport.register(this);
            Spellchecker.register(this);
        }
    }

    /**
     * Must be called when the area is being discarded,
     */
    public void close () {
        if (um != null) {
            um.unregister();
            um = null;
        }
    }

    void setNumberOfChars (int numberOfChars) {
        this.messageWidth = numberOfChars;
    }
    
}
