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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.java.editor.semantic;

import org.netbeans.modules.java.editor.semantic.HighlightImpl;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class ShowGoldenFilesPanel extends javax.swing.JPanel {

    private Component c;
    private File goldenFile;
    private File testFile;
    
    /** Creates new form ShowGoldenFilesPanel */
    public ShowGoldenFilesPanel(Component c) {
        this.c = c;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        goldenField = new javax.swing.JTextField();
        testField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jEditorPane1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jEditorPane1CaretUpdate(evt);
            }
        });

        jScrollPane1.setViewportView(jEditorPane1);

        jLabel1.setText("Golden:");

        jLabel2.setText("Test:");

        goldenField.setText("jTextField1");

        testField.setText("jTextField2");

        jButton1.setText("Accept");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Refuse");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(testField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                            .add(goldenField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(goldenField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(testField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        InputStream source = null;
        OutputStream target = null;
                
        try {
            source = new FileInputStream(testFile);
            target = new FileOutputStream(goldenFile);
            
            FileUtil.copy(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (target != null) {
                try {
                    target.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        c.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jEditorPane1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jEditorPane1CaretUpdate
        int pos = jEditorPane1.getCaretPosition();
        HighlightImpl goldenHighlight = null;
        HighlightImpl testHighlight   = null;
        
        for (HighlightImpl h : golden) {
            if (h.getStart() <= pos && pos <= h.getEnd()) {
                goldenHighlight = h;
                break;
            }
        }
        
        for (HighlightImpl h : test) {
            if (h.getStart() <= pos && pos <= h.getEnd()) {
                testHighlight = h;
                break;
            }
        }
        
        if (goldenHighlight != null)
            goldenField.setText(goldenHighlight.getHighlightTestData());
        else
            goldenField.setText("<none>");
        if (testHighlight != null )
            testField.setText(testHighlight.getHighlightTestData());
        else
            testField.setText("<none>");
    }//GEN-LAST:event_jEditorPane1CaretUpdate
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField goldenField;
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton2;
    public javax.swing.JEditorPane jEditorPane1;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextField testField;
    // End of variables declaration//GEN-END:variables
    
    private List<HighlightImpl> golden;
    private List<HighlightImpl> test;
    
    public void setDocument(StyledDocument doc, List<HighlightImpl> golden, List<HighlightImpl> test, File goldenFile, File testFile) {
        this.golden = golden;
        this.test = test;
        this.goldenFile = goldenFile;
        this.testFile = testFile;
        List<HighlightImpl> missing = new ArrayList<HighlightImpl>();
        List<HighlightImpl> added   = new ArrayList<HighlightImpl>();
        
        Map<String, HighlightImpl> name2Golden = new HashMap<String, HighlightImpl>();
        Map<String, HighlightImpl> name2Test   = new HashMap<String, HighlightImpl>();
        
        for (HighlightImpl g : golden) {
            name2Golden.put(g.getHighlightTestData(), g);
        }
        
        for (HighlightImpl t : test) {
            name2Test.put(t.getHighlightTestData(), t);
        }
        
        Set<String> missingNames = new HashSet<String>(name2Golden.keySet());
        
        missingNames.removeAll(name2Test.keySet());
        
        for (String m : missingNames) {
            missing.add(name2Golden.get(m));
        }
        
        Set<String> addedNames = new HashSet<String>(name2Test.keySet());
        
        addedNames.removeAll(name2Golden.keySet());
        
        for (String a : addedNames) {
            added.add(name2Test.get(a));
        }
        
        List<HighlightImpl> modified = new ArrayList<HighlightImpl>();
        
        modified.addAll(missing);
        modified.addAll(added);
        
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet as = sc.getEmptySet();
        
        as = sc.addAttribute(as, StyleConstants.Foreground, Color.RED);
        as = sc.addAttribute(as, StyleConstants.Bold, Boolean.TRUE);
        
        for (HighlightImpl h : modified) {
            doc.setCharacterAttributes(h.getStart(), h.getEnd() - h.getStart(), as, false);
        }
        
        jEditorPane1.setContentType("text/html");
        jEditorPane1.setDocument(doc);
    }
    
}
