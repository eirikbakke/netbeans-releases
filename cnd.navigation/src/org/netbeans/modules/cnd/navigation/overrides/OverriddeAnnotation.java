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
package org.netbeans.modules.cnd.navigation.overrides;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmMethod;
import org.netbeans.modules.cnd.api.model.CsmOffsetableDeclaration;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.util.UIDs;
import org.netbeans.modules.cnd.modelutil.CsmImageLoader;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.netbeans.modules.cnd.utils.ui.PopupUtil;
import org.openide.text.Annotation;
import org.openide.text.NbDocument;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * @author Vladimir Kvashin
 */
public class OverriddeAnnotation extends Annotation {


    public enum AnnotationType {
        IS_OVERRIDDEN,
        OVERRIDES,
        COMBINED
    }

    /*package*/ static final Logger LOGGER = Logger.getLogger("cnd.overrides.annotations.logger"); // NOI18N

    private final StyledDocument document;
    private final Position pos;
    private final String shortDescription;
    private final AnnotationType type;
    private final Collection<CsmUID<CsmMethod>> baseMethodUIDs;
    private final Collection<CsmUID<CsmMethod>> descMethodUIDs;
    
    public OverriddeAnnotation(StyledDocument document, CsmFunction func,
            Collection<? extends CsmMethod> baseMethods,
            Collection<? extends CsmMethod> descMethods) {
        assert func != null;
        this.document = document;
        this.pos = new DeclarationPosition(func);
        if (baseMethods.isEmpty() && !descMethods.isEmpty()) {
            type = AnnotationType.IS_OVERRIDDEN;
            shortDescription = NbBundle.getMessage(getClass(), "LAB_IsOverriden");
        } else if (!baseMethods.isEmpty() && descMethods.isEmpty()) {
            type = AnnotationType.OVERRIDES;
            shortDescription = NbBundle.getMessage(getClass(), "LAB_Overrides", 
                    (baseMethods.size() == 1) ? baseMethods.iterator().next().getQualifiedName().toString() : "..."); //NOI18N
        } else if (!baseMethods.isEmpty() && !descMethods.isEmpty()) {
            type = AnnotationType.COMBINED;
            shortDescription = NbBundle.getMessage(getClass(), "LAB_Combined");
        } else { //both are empty
            throw new IllegalArgumentException("Either overrides or overridden should be non empty"); //NOI18N
        }
        baseMethodUIDs = new ArrayList<CsmUID<CsmMethod>>(baseMethods.size());
        for (CsmMethod m : baseMethods) {
            baseMethodUIDs.add(UIDs.get(m));
        }
        descMethodUIDs = new ArrayList<CsmUID<CsmMethod>>(descMethods.size());
        for (CsmMethod m : descMethods) {
            descMethodUIDs.add(UIDs.get(m));
        }
    }
    
    @Override
    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public String getAnnotationType() {
        switch(type) {
            case IS_OVERRIDDEN:
                return "org-netbeans-modules-cnd-is_overridden"; //NOI18N
            case OVERRIDES:
                return "org-netbeans-modules-cnd-overrides"; //NOI18N
            case COMBINED:
                return "org-netbeans-modules-cnd-override-is-overridden-combined"; //NOI18N
            default:
                throw new IllegalStateException("Currently not implemented: " + type); //NOI18N
        }
    }
    
    public void attach() {
        NbDocument.addAnnotation(document, pos, -1, this);
    }
    
    public void detachImpl() {
        NbDocument.removeAnnotation(document, this);
    }
    
    @Override
    public String toString() {
        return "[IsOverriddenAnnotation: " + shortDescription + "]"; //NOI18N
    }

    public Position getPosition() {
        return pos;
    }
    
    public String debugDump() {
        List<String> elementNames = new ArrayList<String>();
        for(CsmUID<CsmMethod> uid : baseMethodUIDs) {
            elementNames.add(uid.toString());
        }
        Collections.sort(elementNames);
        return "IsOverriddenAnnotation: type=" + type.name() + ", elements:" + elementNames.toString(); //NOI18N
    }

    void mouseClicked(JTextComponent c, Point p) {
        Point position = new Point(p);        
        SwingUtilities.convertPointToScreen(position, c);        
        performGoToAction(position);
    }

    public AnnotationType getType() {
        return type;
    }

//    public Collection<? extends CsmMethod> getDeclarations() {
//        return methods;
//    }
    
    private void performGoToAction(Point position) {
        Collection<Element> elements = getElements();
        if (elements.size() == 1) {
            openSource(elements.iterator().next());
        } else if (elements.size() > 1) {
            String caption = getShortDescription();
            PopupUtil.showPopup(new Popup(caption, elements), caption, position.x, position.y, true, 0);
        } else {
            throw new IllegalStateException("method list should not be empty"); // NOI18N
        }
    }

    private static void openSource(Element element) {
        CsmUtilities.openSource(element.declaration);
    }


    /**
     * Gets a SORTED elements list
     * @return
     */
    /*package-local for test purposes*/ 
    List<Element> getElements() {
        List<Element> elements = new ArrayList<Element>(baseMethodUIDs.size() + descMethodUIDs.size());
        for (CsmUID<CsmMethod> uid : baseMethodUIDs) {
            elements.add(new Element(uid.getObject(), Direction.BASE));
        }
        for (CsmUID<CsmMethod> uid : descMethodUIDs) {
            elements.add(new Element(uid.getObject(), Direction.DESC));
        }
        Collections.sort(elements);
        return elements;
    }

    private static class DeclarationPosition implements Position {

        private final int offset;

        public DeclarationPosition(CsmOffsetableDeclaration decl) {
            this.offset = decl.getStartOffset();
        }

        @Override
        public int getOffset() {
            return offset;
        }

    }

    private static enum Direction {
        BASE,
        DESC
    }

    /*package-local for test purposes*/
    static class Element implements Comparable<Element> {

        public final CsmOffsetableDeclaration declaration;
        public final Direction direction;
        
        public Element(CsmMethod method, Direction direction) {
            this.declaration = method;
            this.direction = direction;
        }

        public Element(CsmClass cls, Direction direction) {
            this.declaration = cls;
            this.direction = direction;
        }

        public String getDisplayName() {
            return declaration.getQualifiedName().toString();
        }

        private Image getBadge() {
            switch (direction) {
                case BASE:
                    return ImageUtilities.loadImage("/org/netbeans/modules/cnd/navigation/resources/overrides-badge.png");
                case DESC:
                    return ImageUtilities.loadImage("/org/netbeans/modules/cnd/navigation/resources/is-overridden-badge.png");
                default:
                    return null;
            }
        }

        public Icon getIcon() {
            ImageIcon icon = CsmImageLoader.getIcon(declaration);
            Image badge = getBadge();
            if (badge == null) {
                return icon;
            } else {
                return ImageUtilities.image2Icon(ImageUtilities.mergeImages(icon.getImage(), badge, 16, 0));
            }
        }

        @Override
        public String toString() {
            return declaration.getQualifiedName().toString();
        }

        @Override
        public int compareTo(Element o) {
            if (o == null) {
                return -1;
            } else {
                if (o.direction == this.direction) {
                    return declaration.getQualifiedName().toString().compareTo(o.declaration.getQualifiedName().toString());
                } else {
                    return this.direction == Direction.BASE ? -1 : 1;
                }
            }
        }
    }

    private static class RendererImpl extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Element) {
                Element element = (Element) value;
                setIcon(element.getIcon());
                setText(element.getDisplayName());
            }

            return c;
        }
    }


    private static class Popup extends JPanel implements FocusListener {
        
        private JLabel title;
        private JList list;
        private JScrollPane scrollPane;
        private final Collection<Element> elements;

        public Popup(String caption, Collection<Element> elements) {
            super(new BorderLayout());
            this.elements = elements;

            title = new JLabel(caption);
            title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            title.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            add(title, BorderLayout.NORTH);

            list = new JList();
            list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            scrollPane = new JScrollPane(list);
            add(scrollPane, BorderLayout.CENTER);

            DefaultListModel model = new DefaultListModel();
            for (Element element : elements) {
                model.addElement(element);
            }
            list.setModel(model);
            list.setSelectedIndex(0);
            list.setCellRenderer(new RendererImpl());
            if (model.getSize() < 10) {
                list.setVisibleRowCount(model.getSize());
            }

            list.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER && evt.getModifiers() == 0) {
                        openSelected();
                    }
                }
            });
            list.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 1) {
                        openSelected();
                    }
                }
            });

            list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addFocusListener(this);
        }

        private void openSelected() {
            Element el = (Element) list.getSelectedValue();
            if (el != null) {
                openSource(el);
            }
            PopupUtil.hidePopup();
        }

        @Override
        public void focusGained(FocusEvent arg0) {
            list.requestFocus();
            list.requestFocusInWindow();
        }

        @Override
        public void focusLost(FocusEvent arg0) {
        }
    }

}
