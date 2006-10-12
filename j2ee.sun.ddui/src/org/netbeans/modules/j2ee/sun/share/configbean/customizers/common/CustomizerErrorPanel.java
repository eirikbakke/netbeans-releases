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
/*
 * CustomizerErrorPanel.java
 *
 * Created on March 1, 2004, 12:22 PM
 */

package org.netbeans.modules.j2ee.sun.share.configbean.customizers.common;

import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import java.text.MessageFormat;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.netbeans.modules.j2ee.sun.share.configbean.Base;
import org.netbeans.modules.j2ee.sun.share.configbean.ErrorMessageDB;
import org.netbeans.modules.j2ee.sun.share.configbean.ValidationError;

/**
 *
 * @author Peter Williams
 */
public class CustomizerErrorPanel extends JPanel {
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(
		"org.netbeans.modules.j2ee.sun.share.configbean.customizers.common.Bundle");	// NOI18N
	
	private ErrorClient errorClient;
	
	/** Creates new form CustomizerErrorPanel 
	 *
	 * @param client The error support client, typically the customizer instance.
	 */
	public CustomizerErrorPanel(ErrorClient client) {
		errorClient = client;
		
		initComponents();
		initUserComponents();
	}
	
	public GridBagConstraints getConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.insets = new Insets(6,12,11,11);
		
		return constraints;
	}	
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(bundle.getString("ACSN_ErrorTextArea"));
        getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_ErrorTextArea"));
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

	
	private void initUserComponents() {
	}
	
	/** Shows the errors at the top of dialog panel.
	 *  Set focus to the focused component.
	 */    
	public void showErrors(Base bean) {
		String errorString;
		List errorList = null;
		
        removeAll();
        
		// Get error list, if any,
		ErrorMessageDB messageDB = ErrorMessageDB.getMessageDB(bean);
		if(messageDB != null) {
			ValidationError.Partition visiblePartition = errorClient.getPartition();
			errorList = messageDB.getErrors(visiblePartition);
		}
		
        if(errorList != null && errorList.size() > 0) {
			Object [] args = new Object[2];
            
            for(Iterator iter = errorList.iterator(); iter.hasNext();) {
				ValidationError error = (ValidationError) iter.next();

				args[0] = error.getFieldId();
				args[1] = error.getMessage();
                
                String message = MessageFormat.format(bundle.getString("MSG_ErrorDisplayFormat"), args); // NOI18N
                
                // Add error message
                JLabel label = new JLabel();
                label.setIcon(BaseCustomizer.errorMessageIcon);
                label.setText(message); // NOI18N
                label.getAccessibleContext().setAccessibleName(bundle.getString("ASCN_ErrorMessage")); // NOI18N
                label.getAccessibleContext().setAccessibleDescription(message);
                label.setForeground(errorClient.getErrorMessageForegroundColor());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.weightx = 1.0;
                add(label, constraints);
            }
        }

        Container parent = getParent();
        if(parent != null) {
            parent.validate();
        }
	}

	
	/** Interface to allow customization of error panel by owner.
	 */
	public static interface ErrorClient {
		
		/** Foreground color to use with error messages.
		 *
		 *  @return Foreground color for error messages.
		 */
		public Color getErrorMessageForegroundColor();
		
//		/** Foreground color to use with warning messages.
//		 *
//		 *  @return Foreground color for warning messages.
//		 */
//		public Color getWarningMessageForegroundColor();
		
		/** Gets the current panel descriptor.
		 *
		 *  @return Current panel descriptor.
		 */
		public ValidationError.Partition getPartition();
		
	}	
}

