/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vmd.game.nbdialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.modules.vmd.game.dialog.PartialImageGridPreview;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author  kherink
 */
public class SelectImageForLayerDialog extends javax.swing.JPanel {
	
	public static final boolean DEBUG = false;

	private static final Icon ICON_ERROR = new ImageIcon(Utilities.loadImage("org/netbeans/modules/vmd/midp/resources/error.gif"));
	
	private String path;
	private Collection<FileObject> images;
	private DialogDescriptor dd;
	private PartialImageGridPreview imagePreview = new PartialImageGridPreview();
	
	private FileObject imgFile;
	
		
	public SelectImageForLayerDialog(String missingImagePath, Collection<FileObject> images) {
		this.path = missingImagePath;
		this.images = images;
		initComponents();
		init();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupLayers = new javax.swing.ButtonGroup();
        panelCustomizer = new javax.swing.JPanel();
        labelImageFile = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listImageFileName = new javax.swing.JList();
        panelPreview = new javax.swing.JPanel();
        labelImagePreview = new javax.swing.JLabel();
        panelImage = new javax.swing.JPanel();
        panelError = new javax.swing.JPanel();
        labelError = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaReason = new javax.swing.JTextArea();

        labelImageFile.setText("Select image:");

        listImageFileName.setModel(this.getImageListModel());
        listImageFileName.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(listImageFileName);

        org.jdesktop.layout.GroupLayout panelCustomizerLayout = new org.jdesktop.layout.GroupLayout(panelCustomizer);
        panelCustomizer.setLayout(panelCustomizerLayout);
        panelCustomizerLayout.setHorizontalGroup(
            panelCustomizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCustomizerLayout.createSequentialGroup()
                .add(labelImageFile)
                .addContainerGap(221, Short.MAX_VALUE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
        );
        panelCustomizerLayout.setVerticalGroup(
            panelCustomizerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelCustomizerLayout.createSequentialGroup()
                .add(labelImageFile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );

        labelImagePreview.setText("Image preview:");

        panelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        this.panelImage.add(this.imagePreview, BorderLayout.CENTER);
        panelImage.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout panelPreviewLayout = new org.jdesktop.layout.GroupLayout(panelPreview);
        panelPreview.setLayout(panelPreviewLayout);
        panelPreviewLayout.setHorizontalGroup(
            panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .add(panelPreviewLayout.createSequentialGroup()
                        .add(labelImagePreview)
                        .addContainerGap(206, Short.MAX_VALUE))))
        );
        panelPreviewLayout.setVerticalGroup(
            panelPreviewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelPreviewLayout.createSequentialGroup()
                .add(labelImagePreview)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelImage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );

        labelError.setForeground(new java.awt.Color(255, 0, 0));

        org.jdesktop.layout.GroupLayout panelErrorLayout = new org.jdesktop.layout.GroupLayout(panelError);
        panelError.setLayout(panelErrorLayout);
        panelErrorLayout.setHorizontalGroup(
            panelErrorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labelError, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
        );
        panelErrorLayout.setVerticalGroup(
            panelErrorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelErrorLayout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .add(labelError, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        textAreaReason.setColumns(20);
        textAreaReason.setRows(3);
        textAreaReason.setTabSize(4);
        jScrollPane2.setViewportView(textAreaReason);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, panelError, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(panelCustomizer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(panelPreview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(panelCustomizer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelError, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupLayers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelError;
    private javax.swing.JLabel labelImageFile;
    private javax.swing.JLabel labelImagePreview;
    private javax.swing.JList listImageFileName;
    private javax.swing.JPanel panelCustomizer;
    private javax.swing.JPanel panelError;
    private javax.swing.JPanel panelImage;
    private javax.swing.JPanel panelPreview;
    private javax.swing.JTextArea textAreaReason;
    // End of variables declaration//GEN-END:variables
	
	
	
	public void setDialogDescriptor(DialogDescriptor dd) {
		this.dd = dd;
	}
	
	private void init() {
		this.labelError.setText("Select image file.");
		
		this.textAreaReason.setBackground(this.getBackground());
		this.textAreaReason.setText(path);
		
		this.labelError.setIcon(ICON_ERROR);
		this.panelImage.add(this.imagePreview, BorderLayout.CENTER);
		
		this.listImageFileName.addListSelectionListener(new ImageListListener());
		this.listImageFileName.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList src, Object value, int index, boolean isSelected, boolean hasfocus) {
				FileObject entry = (FileObject) value;
                return super.getListCellRendererComponent(src, FileUtil.getFileDisplayName(entry), index, isSelected, hasfocus);
            }
		});
		
	}
	
	private List<FileObject> getImageList() {
		List<FileObject> list = new ArrayList<FileObject>();
		list.addAll(this.images);
		return list;
	}
	
	private DefaultListModel getImageListModel() {
		DefaultListModel dlm = new DefaultListModel();
		List<FileObject> images = this.getImageList();
		for (FileObject imageEntry : images) {
			dlm.addElement(imageEntry);
		}
		return dlm;
	}	
	
	public void setOKButtonEnabled(boolean enable) {
		if (!enable) {
			this.labelError.setIcon(ICON_ERROR);
		}
		else {
			this.labelError.setIcon(null);
		}
		this.dd.setValid(enable);
	}
	
	private class ImageListListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			this.handleImageSelectionChange();
		}
		
		private void handleImageSelectionChange() {
			try {
				loadImagePreview();
				labelError.setText(null);
				setOKButtonEnabled(true);
				FileObject entry = (FileObject) listImageFileName.getSelectedValue();
				imgFile = entry;
			} catch (MalformedURLException e) {
				setOKButtonEnabled(false);
				labelError.setText("Invalid image location.");
				e.printStackTrace();
			} catch (IllegalArgumentException iae) {
				setOKButtonEnabled(false);
				labelError.setText("Image file contents could not be loaded, image may be corrupt.");
				iae.printStackTrace();
			}
		}
	}
	
	private void loadImagePreview() throws MalformedURLException, IllegalArgumentException {
		if (DEBUG) System.out.println("load image preview");
		
		FileObject entry = (FileObject) this.listImageFileName.getSelectedValue();
		URL imageURL = null;
		try {
			imageURL = entry.getURL();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		assert(imageURL != null);
		
		this.imagePreview.setImageURL(imageURL);
		this.repaint();
	}
	
	public FileObject getValue() {
		return this.imgFile;
	}
	
}

