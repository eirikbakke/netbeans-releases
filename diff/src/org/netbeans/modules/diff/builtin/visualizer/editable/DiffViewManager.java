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
package org.netbeans.modules.diff.builtin.visualizer.editable;

import org.netbeans.api.diff.Difference;
import org.netbeans.editor.*;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.highlighting.HighlightsContainer;
import org.netbeans.spi.diff.DiffProvider;
import org.openide.util.Lookup;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;
import org.openide.util.RequestProcessor;

/**
 * Handles interaction among Diff components: editor panes, scroll bars, action bars and the split pane.
 * 
 * @author Maros Sandor
 */
class DiffViewManager implements ChangeListener {
    
    private final EditableDiffView master;
    
    private final DiffContentPanel leftContentPanel;
    private final DiffContentPanel rightContentPanel;

    /**
     * True when this class caused the current scroll event, false otherwise.
     */ 
    private boolean myScrollEvent;
    
    private int                     cachedDiffSerial;
    private DecoratedDifference []  decorationsCached = new DecoratedDifference[0];
    private HighLight []            secondHilitesCached = new HighLight[0];
    private HighLight []            firstHilitesCached = new HighLight[0];
    private final ScrollMapCached   scrollMap = new ScrollMapCached();
    private final RequestProcessor.Task highlightComputeTask;
    private Dimension leftScrollBarPrefSize;
    
    public DiffViewManager(EditableDiffView master) {
        this.master = master;
        this.leftContentPanel = master.getEditorPane1();
        this.rightContentPanel = master.getEditorPane2();
        highlightComputeTask = new RequestProcessor("DiffViewHighlightsComputer", 1, true, false).create(new HighlightsComputeTask());
    }
    
    void init() {
        initScrolling();
    }

    private void initScrolling() {
        rightContentPanel.getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftContentPanel.getScrollPane().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        leftContentPanel.getScrollPane().getVerticalScrollBar().getModel().addChangeListener(this);
        rightContentPanel.getScrollPane().getVerticalScrollBar().getModel().addChangeListener(this);
    }

    private final Boolean [] smartScrollDisabled = new Boolean[] { Boolean.FALSE };
    
    public void runWithSmartScrollingDisabled(Runnable runnable) {
        synchronized (smartScrollDisabled) {
            smartScrollDisabled[0] = true;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            Logger.getLogger(DiffViewManager.class.getName()).log(Level.SEVERE, "", e);
        } finally {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (smartScrollDisabled) {
                        smartScrollDisabled[0] = false;
                    }
                }
            });
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        
        // show/hide left scrollbar depending on rights visibility
        if(rightContentPanel.getScrollPane().getVerticalScrollBar().isVisible()) {
            Dimension d = leftContentPanel.getScrollPane().getVerticalScrollBar().getSize();
            if(d.getHeight() > 0 && d.getWidth() > 0) {
                leftScrollBarPrefSize = d;
            } 
            // The left vertical scroll bar must be there for mouse wheel to work correctly.
            // However it's not necessary to be seen (but must be visible so that the wheel will work).
            leftContentPanel.getScrollPane().getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
            leftContentPanel.getScrollPane().getVerticalScrollBar().setSize(new Dimension(0, 0));
            leftContentPanel.getScrollPane().getVerticalScrollBar().repaint();
        } else if(leftScrollBarPrefSize != null) {
            leftContentPanel.getScrollPane().getVerticalScrollBar().setPreferredSize(leftScrollBarPrefSize);
            leftContentPanel.getScrollPane().getVerticalScrollBar().setSize(leftScrollBarPrefSize);
            leftContentPanel.getScrollPane().getVerticalScrollBar().repaint();
        }       
        
        JScrollBar leftScrollBar = leftContentPanel.getScrollPane().getVerticalScrollBar();
        JScrollBar rightScrollBar = rightContentPanel.getScrollPane().getVerticalScrollBar();
        boolean repaint = true;
        if (e.getSource() == leftContentPanel.getScrollPane().getVerticalScrollBar().getModel()) {
            int value = leftScrollBar.getValue();
            leftContentPanel.getActionsScrollPane().getVerticalScrollBar().setValue(value);
            if (myScrollEvent) return;
            myScrollEvent = true;
        } else {
            int value = rightScrollBar.getValue();
            boolean valueChanged = value != rightContentPanel.getActionsScrollPane().getVerticalScrollBar().getValue();
            rightContentPanel.getActionsScrollPane().getVerticalScrollBar().setValue(value);
            if (myScrollEvent) return;
            myScrollEvent = true;
            boolean doSmartScroll;
            synchronized (smartScrollDisabled) {
                doSmartScroll = !smartScrollDisabled[0];
            }
            if (doSmartScroll && valueChanged) {
                // let the viewport update its visible area
                final Rectangle prevVisRect = rightContentPanel.getScrollPane().getViewport().getViewRect();
                repaint = false;
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run () {
                        smartScroll(true);
                        Rectangle visRect = rightContentPanel.getScrollPane().getViewport().getViewRect();
                        Boolean down = null;
                        if (visRect.y > prevVisRect.y) {
                            down = true;
                        } else if (visRect.y < prevVisRect.y) {
                            down = false;
                        }
                        master.updateCurrentDifference(down);
                        master.getMyDivider().repaint();
                    }
                });
            }
        }
        if (repaint) {
            master.getMyDivider().repaint();
        }
        myScrollEvent = false;
    }
    
    void scroll (boolean checkFileEdge) {
        myScrollEvent = true;
        smartScroll(checkFileEdge);
        master.getMyDivider().repaint();
        myScrollEvent = false;
    }
    
    EditableDiffView getMaster() {
        return master;
    }
    
    private int rightHeightCached, leftHeightCached;
    private void updateDifferences() {
        assert EventQueue.isDispatchThread();
        int mds = master.getDiffSerial();
        int currentLeftHeight = getHeight(leftContentPanel.getEditorPane()), currentRightHeight = getHeight(rightContentPanel.getEditorPane());
        if (mds <= cachedDiffSerial && currentRightHeight == rightHeightCached && currentLeftHeight == leftHeightCached) {
            return;
        }
        rightHeightCached = currentRightHeight;
        leftHeightCached = currentLeftHeight;
        cachedDiffSerial = mds;
        computeDecorations();
        master.getEditorPane1().getLinesActions().repaint();
        master.getEditorPane2().getLinesActions().repaint();
        firstHilitesCached = secondHilitesCached = new HighLight[0];
        // interrupt running highlight scan and start new one outside of AWT
        highlightComputeTask.cancel();
        highlightComputeTask.schedule(0);
    }

    public DecoratedDifference [] getDecorations() {
        if (EventQueue.isDispatchThread()) {
            updateDifferences();
        }
        return decorationsCached;
    }

    public HighLight[] getSecondHighlights() {
        return secondHilitesCached;
    }

    public HighLight[] getFirstHighlights() {
        return firstHilitesCached;
    }
    
    private void computeFirstHighlights() {
        List<HighLight> hilites = new ArrayList<HighLight>();
        Document doc = leftContentPanel.getEditorPane().getDocument();
        DecoratedDifference[] decorations = decorationsCached;
        for (DecoratedDifference dd : decorations) {
            if (Thread.interrupted()) {
                return;
            }
            Difference diff = dd.getDiff();
            if (dd.getBottomLeft() == -1) continue;
            int start = getRowStartFromLineOffset(doc, diff.getFirstStart() > 0 ? diff.getFirstStart() - 1 : 0);
            if (isOneLineChange(diff)) {
                CorrectRowTokenizer firstSt = new CorrectRowTokenizer(diff.getFirstText());
                CorrectRowTokenizer secondSt = new CorrectRowTokenizer(diff.getSecondText());
                for (int i = diff.getSecondStart(); i <= diff.getSecondEnd(); i++) {
                    String firstRow = firstSt.nextToken();                 
                    String secondRow = secondSt.nextToken();                 
                    List<HighLight> rowhilites = computeFirstRowHilites(start, firstRow, secondRow);
                    hilites.addAll(rowhilites);
                    start += firstRow.length() + 1;
                }
            } else {
                int end = getRowStartFromLineOffset(doc, diff.getFirstEnd());
                if (end == -1) {
                    end = doc.getLength();
                }
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setBackground(attrs, master.getColor(diff));
                attrs.addAttribute(HighlightsContainer.ATTR_EXTENDS_EOL, Boolean.TRUE);
                hilites.add(new HighLight(start, end, attrs));
            }
        }
        firstHilitesCached = hilites.toArray(new HighLight[hilites.size()]);
    }
    
    static int getRowStartFromLineOffset(Document doc, int lineIndex) {
        if (doc instanceof BaseDocument) {
            return Utilities.getRowStartFromLineOffset((BaseDocument) doc, lineIndex);
        } else {
            // TODO: find row start from line offet
            Element element = doc.getDefaultRootElement();
            Element line = element.getElement(lineIndex);
            return line.getStartOffset();
        }
    }
    
    private void computeSecondHighlights() {
        List<HighLight> hilites = new ArrayList<HighLight>();
        Document doc = rightContentPanel.getEditorPane().getDocument();
        DecoratedDifference[] decorations = decorationsCached;
        for (DecoratedDifference dd : decorations) {
            if (Thread.interrupted()) {
                return;
            }
            Difference diff = dd.getDiff();
            if (dd.getBottomRight() == -1) continue;
            int start = getRowStartFromLineOffset(doc, diff.getSecondStart() > 0 ? diff.getSecondStart() - 1 : 0);
            if (isOneLineChange(diff)) {
                CorrectRowTokenizer firstSt = new CorrectRowTokenizer(diff.getFirstText());
                CorrectRowTokenizer secondSt = new CorrectRowTokenizer(diff.getSecondText());
                for (int i = diff.getSecondStart(); i <= diff.getSecondEnd(); i++) {
                    try {
                        String firstRow = firstSt.nextToken();
                        String secondRow = secondSt.nextToken();
                        List<HighLight> rowhilites = computeSecondRowHilites(start, firstRow, secondRow);
                        hilites.addAll(rowhilites);
                        start += secondRow.length() + 1;
                    } catch (Exception e) {
                        //
                    }
                }
            } else {
                int end = getRowStartFromLineOffset(doc, diff.getSecondEnd());
                if (end == -1) {
                    end = doc.getLength();
                }
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setBackground(attrs, master.getColor(diff));
                attrs.addAttribute(HighlightsContainer.ATTR_EXTENDS_EOL, Boolean.TRUE);
                hilites.add(new HighLight(start, end, attrs));
            }
        }
        secondHilitesCached = hilites.toArray(new HighLight[hilites.size()]);
    }

    private List<HighLight> computeFirstRowHilites(int rowStart, String left, String right) {
        List<HighLight> hilites = new ArrayList<HighLight>(4);
        
        String leftRows = wordsToRows(left);  
        String rightRows = wordsToRows(right);

        DiffProvider diffprovider = Lookup.getDefault().lookup(DiffProvider.class);
        if (diffprovider == null) {
            return hilites;
        }

        Difference[] diffs;
        try {
            diffs = diffprovider.computeDiff(new StringReader(leftRows), new StringReader(rightRows));
        } catch (IOException e) {
            return hilites;
        }

        // what we can hilite in first source
        for (Difference diff : diffs) {
            if (diff.getType() == Difference.ADD) continue;
            int start = rowOffset(leftRows, diff.getFirstStart());
            int end = rowOffset(leftRows, diff.getFirstEnd() + 1);
            
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBackground(attrs, master.getColor(diff));
            hilites.add(new HighLight(rowStart + start, rowStart + end, attrs));
        }
        return hilites;
    }    
    
    private List<HighLight> computeSecondRowHilites(int rowStart, String left, String right) {
        List<HighLight> hilites = new ArrayList<HighLight>(4);
        
        String leftRows = wordsToRows(left);  
        String rightRows = wordsToRows(right);

        DiffProvider diffprovider = Lookup.getDefault().lookup(DiffProvider.class);
        if (diffprovider == null) {
            return hilites;
        }

        Difference[] diffs;
        try {
            diffs = diffprovider.computeDiff(new StringReader(leftRows), new StringReader(rightRows));
        } catch (IOException e) {
            return hilites;
        }

        // what we can hilite in second source
        for (Difference diff : diffs) {
            if (diff.getType() == Difference.DELETE) continue;
            int start = rowOffset(rightRows, diff.getSecondStart());
            int end = rowOffset(rightRows, diff.getSecondEnd() + 1);
            
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setBackground(attrs, master.getColor(diff));
            hilites.add(new HighLight(rowStart + start, rowStart + end, attrs));
        }
        return hilites;
    }

    /**
     * 1-based row index.
     * 
     * @param row
     * @param rowIndex
     * @return
     */
    private int rowOffset(String row, int rowIndex) {
        if (rowIndex == 1) return 0; 
        int newLines = 0;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '\n') {
                newLines++;
                if (--rowIndex == 1) {
                    return i + 1 - newLines;
                }
            }
        }
        return row.length();
    }

    private String wordsToRows(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        StringTokenizer st = new StringTokenizer(s, " \t\n[]{};:'\",.<>/?-=_+\\|~!@#$%^&*()", true); // NOI18N
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.length() == 0) continue;
            sb.append(token);
            sb.append('\n');
        }
        return sb.toString();
    }

    private boolean isOneLineChange(Difference diff) {
        return diff.getType() == Difference.CHANGE && 
                diff.getFirstEnd() - diff.getFirstStart() == diff.getSecondEnd() - diff.getSecondStart();
    }

    private void computeDecorations() {
        
        Document document = master.getEditorPane2().getEditorPane().getDocument();
        View rootLeftView = Utilities.getDocumentView(leftContentPanel.getEditorPane());
        View rootRightView = Utilities.getDocumentView(rightContentPanel.getEditorPane());
        if (rootLeftView == null || rootRightView == null) return;
        
        Difference [] diffs = master.getDifferences();
        DecoratedDifference[] decorations = new DecoratedDifference[diffs.length];
        for (int i = 0; i < diffs.length; i++) {
            Difference difference = diffs[i];
            DecoratedDifference dd = new DecoratedDifference(difference, canRollback(document, difference));
            Rectangle leftStartRect = getRectForView(leftContentPanel.getEditorPane(), rootLeftView, difference.getFirstStart() - 1, false);
            Rectangle leftEndRect = difference.getType() == Difference.ADD 
                    ? getRectForView(leftContentPanel.getEditorPane(), rootLeftView, difference.getFirstStart(), false)
                    : getRectForView(leftContentPanel.getEditorPane(), rootLeftView, difference.getFirstEnd() - 1, true);
            Rectangle rightStartRect = getRectForView(rightContentPanel.getEditorPane(), rootRightView, difference.getSecondStart() - 1, false);
            Rectangle rightEndRect = difference.getType() == Difference.DELETE
                    ? getRectForView(rightContentPanel.getEditorPane(), rootRightView, difference.getSecondStart(), false)
                    : getRectForView(rightContentPanel.getEditorPane(), rootRightView, difference.getSecondEnd() - 1, true);
            if (leftStartRect == null || leftEndRect == null || rightStartRect == null || rightEndRect == null) {
                decorations = new DecoratedDifference[0];
                break;
            }
            if (difference.getType() == Difference.ADD) {
                dd.topRight = rightStartRect.y;
                dd.bottomRight = rightEndRect.y + rightEndRect.height;
                dd.topLeft = leftEndRect.y == 0 ? leftStartRect.y + leftStartRect.height : leftEndRect.y;
                dd.floodFill = true;
            } else if (difference.getType() == Difference.DELETE) {
                dd.topLeft = leftStartRect.y;
                dd.bottomLeft = leftEndRect.y + leftEndRect.height;
                dd.topRight = rightEndRect.y == 0 ? rightStartRect.y + rightStartRect.height : rightEndRect.y;
                dd.floodFill = true;
            } else {
                dd.topRight = rightStartRect.y;
                dd.bottomRight = rightEndRect.y + rightEndRect.height;
                dd.topLeft = leftStartRect.y;
                dd.bottomLeft = leftEndRect.y + leftEndRect.height;
                dd.floodFill = true;
            }
            decorations[i] = dd;
        }
        decorationsCached = decorations;
    }

    private Rectangle getRectForView (final JTextComponent comp, final View rootView, final int lineNumber, final boolean endOffset) {
        final Rectangle[] rect = new Rectangle[1];
        Utilities.runViewHierarchyTransaction(comp, true, new Runnable() {
            @Override
            public void run() {
                if (lineNumber == -1 || lineNumber >= rootView.getViewCount()) {
                    rect[0] = new Rectangle();
                    return;
                }
                View view = rootView.getView(lineNumber);
                try {
                    rect[0] = view == null ? null : comp.modelToView(endOffset ? view.getEndOffset() - 1 : view.getStartOffset());
                } catch (BadLocationException ex) {
                    //
                }
            }
        });
        return rect[0];
    }

    private boolean canRollback(Document doc, Difference diff) {
        if (!(doc instanceof GuardedDocument)) return true;
        GuardedDocument document = (GuardedDocument) doc;
        int start, end;
        if (diff.getType() == Difference.DELETE) {
            start = end = Utilities.getRowStartFromLineOffset(document, diff.getSecondStart());
        } else {
            start = Utilities.getRowStartFromLineOffset(document, diff.getSecondStart() > 0 ? diff.getSecondStart() - 1 : 0);
            end = Utilities.getRowStartFromLineOffset(document, diff.getSecondEnd());
        }
        MarkBlockChain mbc = ((GuardedDocument) document).getGuardedBlockChain();
        return (mbc.compareBlock(start, end) & MarkBlock.OVERLAP) == 0;
    }
    
    /**
     * 1. find the difference whose top (first line) is closest to the center of the screen. If there is no difference on screen, proceed to #5
     * 2. find line offset of the found difference in the other document
     * 3. scroll the other document so that the difference starts on the same visual line
     * 
     * 5. scroll the other document proportionally
     */ 
    private void smartScroll (boolean checkFileEdge) {
        DiffContentPanel rightPane = master.getEditorPane2();
        DiffContentPanel leftPane = master.getEditorPane1();        
        
        int [] map = scrollMap.getScrollMap(rightPane.getEditorPane().getSize().height, master.getDiffSerial());
        
        int rightOffet = rightPane.getScrollPane().getVerticalScrollBar().getValue();
        if (checkFileEdge && rightOffet == 0) {
            leftPane.getScrollPane().getVerticalScrollBar().setValue(0);
        } else {
            int halfScreen = rightPane.getScrollPane().getVerticalScrollBar().getVisibleAmount() / 2;
            rightOffet += halfScreen;
            if (checkFileEdge && rightOffet + halfScreen >= rightPane.getScrollPane().getVerticalScrollBar().getMaximum()) {
                rightOffet = map.length - 1;
            }
            if (rightOffet >= map.length) return;
            leftPane.getScrollPane().getVerticalScrollBar().setValue(map[rightOffet]
                    - leftPane.getScrollPane().getVerticalScrollBar().getVisibleAmount() / 2);
        }
    }

    double getScrollFactor() {
        BoundedRangeModel m1 = leftContentPanel.getScrollPane().getVerticalScrollBar().getModel();
        BoundedRangeModel m2 = rightContentPanel.getScrollPane().getVerticalScrollBar().getModel();
        return ((double) m1.getMaximum() - m1.getExtent()) / (m2.getMaximum() - m2.getExtent());
    }


    /**
     * The split pane needs to be repainted along with editor.
     * 
     * @param decoratedEditorPane the pane that is currently repainting
     */ 
    void editorPainting(DecoratedEditorPane decoratedEditorPane) {
        if (!decoratedEditorPane.isFirst()) {
            JComponent mydivider = master.getMyDivider();
            mydivider.paint(mydivider.getGraphics());
        }
    }

    private int getHeight (final DecoratedEditorPane editorPane) {
        final int height[] = new int[1];
        editorPane.getDocument().render(new Runnable () {
            @Override
            public void run() {
                try {
                    Rectangle rec = editorPane.modelToView(editorPane.getDocument().getLength());
                    if (rec != null) {
                        height[0] = (int) (rec.getY() + rec.getHeight());
                    }
                } catch (BadLocationException ex) {
                    //
                }
            }
        });
        return height[0];
    }
    
    public static class DifferencePosition {
        
        private Difference  diff;
        private boolean     isStart;

        public DifferencePosition(Difference diff, boolean start) {
            this.diff = diff;
            isStart = start;
        }

        public Difference getDiff() {
            return diff;
        }

        public boolean isStart() {
            return isStart;
        }
    }    

    public static class DecoratedDifference {
        private final Difference    diff;
        private final boolean       canRollback;
        private int         topLeft;            // top line in the left pane
        private int         bottomLeft = -1;    // bottom line in the left pane, -1 for ADDs
        private int         topRight;
        private int         bottomRight = -1;   // bottom line in the right pane, -1 for DELETEs
        private boolean     floodFill;          // should the whole difference be highlited

        public DecoratedDifference(Difference difference, boolean canRollback) {
            diff = difference;
            this.canRollback = canRollback;
        }

        public boolean canRollback() {
            return canRollback;
        }

        public Difference getDiff() {
            return diff;
        }

        public int getTopLeft() {
            return topLeft;
        }

        public int getBottomLeft() {
            return bottomLeft;
        }

        public int getTopRight() {
            return topRight;
        }

        public int getBottomRight() {
            return bottomRight;
        }

        public boolean isFloodFill() {
            return floodFill;
        }
    }

    public static class HighLight {
        
        private final int           startOffset;
        private final int           endOffset;
        private final AttributeSet  attrs;

        public HighLight(int startOffset, int endOffset, AttributeSet attrs) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.attrs = attrs;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public AttributeSet getAttrs() {
            return attrs;
        }
    }

    /**
     * Java StringTokenizer does not work if the very first character is a delimiter.
     */
    private static class CorrectRowTokenizer {
        
        private final String s;
        private int idx;

        public CorrectRowTokenizer(String s) {
            this.s = s;
        }

        public String nextToken() {
            String token = null;
            for (int end = idx; end < s.length(); end++) {
                if (s.charAt(end) == '\n') {
                    token = s.substring(idx, end);
                    idx = end + 1;
                    break;
                }
            }
            return token;
        }
    }

    private class ScrollMapCached {
        
        private int     rightPanelHeightCached;
        private int []  scrollMapCached;
        private int     diffSerialCached;

        public synchronized int[] getScrollMap(int rightPanelHeight, int diffSerial) {
            if (rightPanelHeight != rightPanelHeightCached || diffSerialCached != diffSerial || scrollMapCached == null) {
                diffSerialCached = diffSerial;
                rightPanelHeightCached = rightPanelHeight;
                scrollMapCached = compute();
            }
            return scrollMapCached;
        }

        private int [] compute() {
            int [] scrollMap = new int[rightPanelHeightCached];
            if (rightPanelHeightCached == 0) {
                return scrollMap;
            }

            EditorUI editorUI = org.netbeans.editor.Utilities.getEditorUI(leftContentPanel.getEditorPane());
            if (editorUI == null) return scrollMap;

            View rootLeftView = Utilities.getDocumentView(leftContentPanel.getEditorPane());
            View rootRightView = Utilities.getDocumentView(rightContentPanel.getEditorPane());
            if (rootLeftView == null || rootRightView == null) return scrollMap;

            DecoratedDifference [] diffs = getDecorations();
            
            scrollMap[0] = 0;
            scrollMap[rightPanelHeightCached - 1] = master.getEditorPane1().getEditorPane().getSize().height;
            int lastOffset = 0;
            boolean lastDelete = false;
            for (int i = 0; i < diffs.length; ++i) {
                DecoratedDifference ddiff = diffs[i];
                int topLeft = ddiff.getTopLeft();
                int topRight = ddiff.getTopRight();
                int bottomLeft = ddiff.getBottomLeft();
                int bottomRight = ddiff.getBottomRight();
                scrollMap[topRight] = topLeft;
                if (bottomLeft == -1) {
                    bottomLeft = topLeft;
                }
                if (bottomRight == -1) {
                    // DELETE, handle separately to make the transition smooth
                    scrollMap[topRight] = topLeft;
                    interpolate(scrollMap, lastOffset, topRight);
                    lastOffset = Math.max(lastOffset, lastDelete
                            ? topRight - 50
                            : (topRight + lastOffset) / 2);
                    int newLastOffset = rightPanelHeightCached - 1;
                    if (i < diffs.length - 1) {
                        newLastOffset = diffs[i + 1].topRight;
                        scrollMap[newLastOffset] = diffs[i + 1].topLeft;
                    }
                    scrollMap[topRight] = (topLeft + bottomLeft) / 2;
                    interpolate(scrollMap, topRight, newLastOffset);
                    
                    newLastOffset = Math.min((topRight + newLastOffset) / 2, topRight + 50);
                    interpolate(scrollMap, lastOffset, newLastOffset);
                    lastOffset = newLastOffset;
                    lastDelete = true;
                } else {
                    scrollMap[bottomRight] = bottomLeft;
                    interpolate(scrollMap, lastOffset, topRight);
                    interpolate(scrollMap, topRight, bottomRight);
                    lastOffset = bottomRight;
                }
            }
            interpolate(scrollMap, lastOffset, rightPanelHeightCached - 1);
            return scrollMap;
        }

        private void interpolate (int[] scrollMap, int start, int end) {
            if (end > start) {
                int rightHeight = end - start;
                int leftHeight = scrollMap[end] - scrollMap[start];
                for (int pos = 1; pos < end - start; ++pos) {
                    scrollMap[pos + start] = (leftHeight * pos) / rightHeight + scrollMap[start];
                }
            }
        }
    }

    /**
     * Counts differences for rows
     */
    private class HighlightsComputeTask implements Runnable {
        private int diffSerial;

        @Override
        public void run() {
            diffSerial = cachedDiffSerial;
            computeSecondHighlights();
            if (diffSerial != cachedDiffSerial) {
                return;
            }
            computeFirstHighlights();
            if (diffSerial == cachedDiffSerial) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        master.getEditorPane1().fireHilitingChanged();
                        master.getEditorPane2().fireHilitingChanged();
                    }
                });
            }
        }
    }
}
