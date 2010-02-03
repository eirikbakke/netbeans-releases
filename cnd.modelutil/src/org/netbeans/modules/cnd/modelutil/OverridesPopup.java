/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.cnd.modelutil;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmOffsetableDeclaration;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.utils.ui.PopupUtil;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Vladimir Kvashin
 */

public class OverridesPopup extends JPanel implements FocusListener {

    private static enum Kind {
        BASE,
        DESC,
        MAIN
    }

    /*package-local for test purposes*/
    private static class Element implements Comparable<Element> {

        public final CsmOffsetableDeclaration declaration;
        public final Kind direction;

        public Element(CsmOffsetableDeclaration decl, Kind direction) {
            this.declaration = decl;
            this.direction = direction;
        }

        public String getDisplayName() {
            return declaration.getQualifiedName().toString();
        }

        private Image getBadge() {
            switch (direction) {
                case BASE:
                    return ImageUtilities.loadImage("org/netbeans/modules/cnd/modelutil/resources/overrides-badge.png");
                case DESC:
                    return ImageUtilities.loadImage("org/netbeans/modules/cnd/modelutil/resources/is-overridden-badge.png");
                default:
                    return null;
            }
        }

        public Icon getIcon() {
            ImageIcon icon = CsmImageLoader.getIcon(declaration);
            Image badge = getBadge();
            if (badge == null) {
                BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                Color transparent = new Color(0, 0, 0, 0);
                g2d.setColor(transparent);
                g2d.setComposite(AlphaComposite.Src);
                g2d.fill(new Rectangle2D.Float(0, 0, img.getWidth(), img.getHeight()));
                badge = img;
            }
            return ImageUtilities.image2Icon(ImageUtilities.mergeImages(icon.getImage(), badge, 16, 0));
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
                    return this.direction == Kind.BASE ? -1 : 1;
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

    private JLabel title;
    private JList list;
    private JScrollPane scrollPane;
    private final List<Element> elements;
    private final CsmOffsetableDeclaration mainDeclaration;
    private final boolean gotoDefinitions;

    public OverridesPopup(String caption,
            Collection<? extends CsmOffsetableDeclaration> baseDeclarations,
            Collection<? extends CsmOffsetableDeclaration> descendantDeclarations) {
        this(caption, null, baseDeclarations, descendantDeclarations, false);
    }

    public OverridesPopup(String caption, CsmOffsetableDeclaration mainDeclaration,
            Collection<? extends CsmOffsetableDeclaration> baseDeclarations,
            Collection<? extends CsmOffsetableDeclaration> descendantDeclarations,
            boolean gotoDefinitions) {

        super(new BorderLayout());
        this.mainDeclaration = mainDeclaration;
        this.gotoDefinitions = gotoDefinitions;
        
        this.elements = new ArrayList<Element>();

        title = new JLabel(caption);
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        add(title, BorderLayout.NORTH);

        list = new JList();
        list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        for (CsmOffsetableDeclaration decl : baseDeclarations) {
            elements.add(new Element(decl, Kind.BASE));
        }
        for (CsmOffsetableDeclaration decl : descendantDeclarations) {
            elements.add(new Element(decl, Kind.DESC));
        }
        Collections.sort(elements);

        DefaultListModel model = new DefaultListModel();
        if (mainDeclaration != null) {
            model.addElement(new Element(mainDeclaration, Kind.MAIN));
        }
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
            CsmOffsetableDeclaration decl = el.declaration;
            if (gotoDefinitions) {
                if (CsmKindUtilities.isFunctionDeclaration(decl)) {
                    CsmFunctionDefinition definition = ((CsmFunction) decl).getDefinition();
                    if (definition != null) {
                        decl = definition;
                    }
                }
            }
            CsmUtilities.openSource(decl);
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
