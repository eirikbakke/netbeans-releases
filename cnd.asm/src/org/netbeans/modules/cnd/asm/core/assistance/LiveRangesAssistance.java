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


package org.netbeans.modules.cnd.asm.core.assistance;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;

import org.netbeans.modules.cnd.asm.core.dataobjects.AsmObjectUtilities;
import org.netbeans.modules.cnd.asm.model.AsmState;
import org.netbeans.modules.cnd.asm.model.lang.AsmOffsetable;
import org.netbeans.modules.cnd.asm.model.AsmModelAccessor;
import org.netbeans.modules.cnd.asm.model.lang.Register;


public class LiveRangesAssistance implements AsmModelAccessor.ParseListener,
                                             CaretListener,
                                             RegisterChooserListener    {
      
    private final CodeAnnotationSidebar sideBar;    
    private final LiveRangesAction action;
    private LiveRangesAccessor accessor;
    private RegisterChooser lastChooser;
    
    private AsmOffsetable lastRangeStart;
    private AsmOffsetable lastRangeEnd;
    
    private final BaseDocument doc;
    private final JEditorPane pane;
    private final EditorCookie cookie;
        
    public LiveRangesAssistance(BaseDocument doc) {                                
        this.doc = doc;                                       
        
        DataObject obj = NbEditorUtilities.getDataObject(doc);          
        cookie = obj.getCookie(EditorCookie.class);                
        
        pane = cookie.getOpenedPanes()[0];        
        pane.addCaretListener(this);                            
        action = new LiveRangesAction();
        
        sideBar = (CodeAnnotationSidebar) 
                pane.getClientProperty(LiveRangeSidebarFactory.LIVE_RANGE_SIDEBAR);
           
        AsmModelAccessor acc = AsmObjectUtilities.getAccessor(doc);
        if (acc == null) {
            return;
        }
        
        acc.addParseListener(this); 
    }    

    
    public void notifyParsed() {
        AsmState state = AsmObjectUtilities.getAccessor(doc).getState();
        accessor = action.calculateRanges(state); 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                calcLiveRanges(getCaretPosition());                
            }
        });
    }
    
    public void update(RegisterChooser ch) {
        lastChooser = ch;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                calcLiveRanges(getCaretPosition());                
            }
        });
    }
    
    private int getCaretPosition() {
        return pane.getCaretPosition();
    }        
    
    private void addAnnotation(AsmOffsetable start, AsmOffsetable end,
                               List<CodeAnnotationSidebar.AnnotationEntry> res) {
        try {
            int lnStart = Utilities.getLineOffset(doc, start.getStartOffset() + 1);
            int lnEnd = Utilities.getLineOffset(doc, end.getStartOffset() + 1);

            res.add(new CodeAnnotationSidebar.AnnotationEntry(java.awt.Color.GREEN, 
                                                              lnStart + 1, lnEnd + 1));
        } catch (BadLocationException ex) {
            // nothing
        }
    }
    
    private void calcLiveRanges(int curPos){                                               
        
        if (lastChooser == null)
             return;
        
        List<CodeAnnotationSidebar.AnnotationEntry> res = 
               new LinkedList<CodeAnnotationSidebar.AnnotationEntry>();
              
        AsmState state = AsmObjectUtilities.getAccessor(doc).getState();
        
        if (accessor == null) {            
            accessor = action.calculateRanges(state); 
        }                   
                       
        for (Register reg: lastChooser.getRegisters()) {
            lastRangeStart = lastRangeEnd = null;
            
            List<Integer> ranges = accessor.getRangesForRegister(reg);
            for (Iterator<Integer> iter = ranges.iterator(); iter.hasNext();) {
                AsmOffsetable offStart = state.getElements().
                                            getCompounds().get(iter.next());
                AsmOffsetable offEnd = state.getElements().
                                            getCompounds().get(iter.next());

                addAnnotation(offStart, offEnd, res);                
            }
        }   
         
        sideBar.setAnnotations(res);
    }               

    private boolean isInRange(AsmOffsetable offStart, AsmOffsetable offEnd, 
                               int pos){
        return isInRange(offStart.getStartOffset(), offEnd.getStartOffset(),
                         pos);
    }
   
    private boolean isInRange(int start, int end, int pos) {
        return start <= pos && pos < end; 
    }
           
    public void caretUpdate(CaretEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int curPos = getCaretPosition();
                if (lastRangeStart != null && lastRangeEnd != null &&
                     isInRange(lastRangeStart, lastRangeEnd, curPos)) {
                        return;
                 }
                
                calcLiveRanges(curPos);                
            }
        });
    }
}
