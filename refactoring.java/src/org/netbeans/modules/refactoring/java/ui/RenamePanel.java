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
package org.netbeans.modules.refactoring.java.ui;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePathScanner;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.java.RefactoringModule;
import org.netbeans.modules.refactoring.java.RefactoringUtils;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.netbeans.spi.gototest.TestLocator;
import org.netbeans.spi.gototest.TestLocator.LocationResult;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;


/**
 * Rename refactoring parameters panel
 *
 * @author  Pavel Flaska
 */
public class RenamePanel extends JPanel implements CustomRefactoringPanel {

    private final transient String oldName;
    private final transient ChangeListener parent;
    private final transient TreePathHandle handle;
    
    /** Creates new form RenamePanelName */
    public RenamePanel(TreePathHandle handle, String oldName, ChangeListener parent, String name, boolean editable, boolean showUpdateReferences) {
        setName(name);
        this.oldName = oldName;
        this.parent = parent;
        this.handle = handle;
        initComponents();
        updateReferencesCheckBox.setVisible(showUpdateReferences);
        nameField.setEnabled(editable);
        //parent.setPreviewEnabled(false);        
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent event) {
                RenamePanel.this.parent.stateChanged(null);
            }
            @Override
            public void insertUpdate(DocumentEvent event) {
                RenamePanel.this.parent.stateChanged(null);
            }
            @Override
            public void removeUpdate(DocumentEvent event) {
                RenamePanel.this.parent.stateChanged(null);
            }
        });
        
        renameGettersAndCheckersCheckBox.setVisible(false);
        renameTestClassCheckBox.setVisible(false);
	renameTestClassMethodCheckBox.setVisible(false);
    }
    
    private boolean initialized = false;
    
    @Override
    public void initialize() {
        if (initialized) {
            return;
        }

        if (handle!=null && handle.getKind() != Tree.Kind.LABELED_STATEMENT &&
                handle.getElementHandle() != null 
                && (handle.getElementHandle().getKind() == ElementKind.FIELD
                || handle.getElementHandle().getKind() == ElementKind.CLASS
		|| handle.getElementHandle().getKind() == ElementKind.METHOD)) {
            JavaSource source = JavaSource.forFileObject(handle.getFileObject());
            CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {

                @Override
                public void cancel() {
                    throw new UnsupportedOperationException("Not supported yet."); // NOI18N
                }

                @Override
                public void run(CompilationController info) throws Exception {
                    if(handle.getElementHandle().getKind() == ElementKind.FIELD) {
                        VariableElement element = (VariableElement) handle.resolveElement(info);
                        TypeElement parent = (TypeElement) element.getEnclosingElement();
                    boolean hasGetters = false;
                        for (ExecutableElement method : ElementFilter.methodsIn(parent.getEnclosedElements())) {
                            if (RefactoringUtils.isGetter(info, method, element) || RefactoringUtils.isSetter(info, method, element)) {
                                hasGetters = true;
                                break;
                            }
                        }

                        if (hasGetters) {
                            SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    renameGettersAndCheckersCheckBox.setVisible(true);
                                }
                            });
                        }
                    }
                    
                    if(handle.getElementHandle().getKind() == ElementKind.CLASS || handle.getElementHandle().getKind() == ElementKind.METHOD) {
			final Element methodElement = handle.resolveElement(info);
                        final FileObject fileObject = handle.getFileObject();
                        Collection<? extends TestLocator> testLocators = Lookup.getDefault().lookupAll(TestLocator.class);
                        for (final TestLocator testLocator : testLocators) {
                            if(testLocator.appliesTo(fileObject)) {
                                if(testLocator.asynchronous()) {
                                    testLocator.findOpposite(fileObject, -1, new TestLocator.LocationListener() {

                                        @Override
                                        public void foundLocation(FileObject fo, LocationResult location) {
					    if(handle.getElementHandle().getKind() == ElementKind.CLASS) {
						addTestFile(location, testLocator);
					    } else if(handle.getElementHandle().getKind() == ElementKind.METHOD) {
						addTestMethod(location, testLocator, methodElement);
					    }
                                        }
                                    });
                                } else {
				    if(handle.getElementHandle().getKind() == ElementKind.CLASS) {
					addTestFile(testLocator.findOpposite(fileObject, -1), testLocator);
				    } else if (handle.getElementHandle().getKind() == ElementKind.METHOD) {
					addTestMethod(testLocator.findOpposite(fileObject, -1), testLocator, methodElement);
				    }
                                }
                            }
                        }
                    }
                }
            };
            try {
                source.runUserActionTask(task, true);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        
        initialized = true;
    }

    private class TestClassMethodsVisitor extends TreePathScanner<Void, Void> {

	private CompilationInfo info;
	private List<ExecutableElement> testClassMethods;

	public TestClassMethodsVisitor(CompilationInfo info) {
	    this.info = info;
	}

	@Override
	public Void visitClass(ClassTree t, Void v) {
	    Element el = info.getTrees().getElement(getCurrentPath());
	    if (el != null) {
		testClassMethods = ElementFilter.methodsIn(((TypeElement) el).getEnclosedElements());
	    }
	    return null;
	}

	public boolean containsTestMethod(Element methodElement) {
            if (testClassMethods != null) {
                String methodName = RefactoringUtils.getTestMethodName(methodElement.getSimpleName().toString());
                for (ExecutableElement testClassMethod : testClassMethods) {
                    if (testClassMethod.getSimpleName().contentEquals(methodName) 
                            && testClassMethod.getReturnType().getKind().equals(TypeKind.VOID)) { // test method should at least be void
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void addTestMethod(LocationResult location, TestLocator locator, final Element methodElement) {
        if (!renameTestClassMethodCheckBox.isVisible()) {
            if (location != null && location.getFileObject() != null) {
                if(locator.getFileType(location.getFileObject()) == TestLocator.FileType.TEST) {
		    JavaSource js = JavaSource.forFileObject(location.getFileObject());
		    try {
			js.runUserActionTask(new Task<CompilationController>() {
			    @Override
			    public void run(CompilationController parameter) throws IOException {
				parameter.toPhase(Phase.ELEMENTS_RESOLVED);
				TestClassMethodsVisitor testClassVisitor = new TestClassMethodsVisitor(parameter);
				testClassVisitor.scan(parameter.getCompilationUnit(), null);

				if (testClassVisitor.containsTestMethod(methodElement)) {
				    SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
					    renameTestClassMethodCheckBox.setVisible(true);
					}
				    });
				}
			    }
			}, true);
		    } catch (IOException e) {
			Logger.getLogger(RenamePanel.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		    }
                }
            }
        }
    }
    
    private void addTestFile(LocationResult location, TestLocator locator) {
        if (!renameTestClassCheckBox.isVisible()) {
            if (location != null && location.getFileObject() != null) {
                if(locator.getFileType(location.getFileObject()) == TestLocator.FileType.TEST) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            renameTestClassCheckBox.setVisible(true);
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        if(nameField.isEnabled()) {
            nameField.requestFocusInWindow();
        } else {
            textCheckBox.requestFocusInWindow();
        }
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        label = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        textCheckBox = new javax.swing.JCheckBox();
        updateReferencesCheckBox = new javax.swing.JCheckBox();
        renameGettersAndCheckersCheckBox = new javax.swing.JCheckBox();
        renameTestClassCheckBox = new javax.swing.JCheckBox();
        renameTestClassMethodCheckBox = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 11, 11));
        setLayout(new java.awt.GridBagLayout());

        label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label.setLabelFor(nameField);
        org.openide.awt.Mnemonics.setLocalizedText(label, org.openide.util.NbBundle.getMessage(RenamePanel.class, "LBL_NewName")); // NOI18N
        add(label, new java.awt.GridBagConstraints());

        nameField.setText(oldName);
        nameField.selectAll();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(nameField, gridBagConstraints);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/refactoring/java/ui/Bundle"); // NOI18N
        nameField.getAccessibleContext().setAccessibleDescription(bundle.getString("ACSD_nameField")); // NOI18N

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        textCheckBox.setSelected(((Boolean) RefactoringModule.getOption("searchInComments.rename", Boolean.FALSE)).booleanValue());
        org.openide.awt.Mnemonics.setLocalizedText(textCheckBox, org.openide.util.NbBundle.getBundle(RenamePanel.class).getString("LBL_RenameComments")); // NOI18N
        textCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                textCheckBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(textCheckBox, gridBagConstraints);
        textCheckBox.getAccessibleContext().setAccessibleDescription(textCheckBox.getText());

        org.openide.awt.Mnemonics.setLocalizedText(updateReferencesCheckBox, org.openide.util.NbBundle.getBundle(RenamePanel.class).getString("LBL_RenameWithoutRefactoring")); // NOI18N
        updateReferencesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 0, 4));
        updateReferencesCheckBox.setMargin(new java.awt.Insets(2, 2, 0, 2));
        updateReferencesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateReferencesCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(updateReferencesCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(renameGettersAndCheckersCheckBox, org.openide.util.NbBundle.getMessage(RenamePanel.class, "RenamePanel.renameGettersAndCheckersCheckBox.text")); // NOI18N
        renameGettersAndCheckersCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameGettersAndCheckersCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(renameGettersAndCheckersCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(renameTestClassCheckBox, org.openide.util.NbBundle.getMessage(RenamePanel.class, "RenamePanel.renameTestClassCheckBox.text")); // NOI18N
        renameTestClassCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                renameTestClassCheckBoxStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(renameTestClassCheckBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(renameTestClassMethodCheckBox, org.openide.util.NbBundle.getMessage(RenamePanel.class, "RenamePanel.renameTestClassMethodCheckBox.text")); // NOI18N
        renameTestClassMethodCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                renameTestClassMethodCheckBoxStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(renameTestClassMethodCheckBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void updateReferencesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateReferencesCheckBoxActionPerformed
        textCheckBox.setEnabled(!updateReferencesCheckBox.isSelected());
        parent.stateChanged(null);
    }//GEN-LAST:event_updateReferencesCheckBoxActionPerformed

    private void textCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_textCheckBoxItemStateChanged
        // used for change default value for searchInComments check-box.                                                  
        // The value is persisted and then used as default in next IDE run.
        Boolean b = evt.getStateChange() == ItemEvent.SELECTED ? Boolean.TRUE : Boolean.FALSE;
        RefactoringModule.setOption("searchInComments.rename", b); // NOI18N
    }//GEN-LAST:event_textCheckBoxItemStateChanged

    private void renameGettersAndCheckersCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameGettersAndCheckersCheckBoxActionPerformed
        parent.stateChanged(null);
    }//GEN-LAST:event_renameGettersAndCheckersCheckBoxActionPerformed

private void renameTestClassCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_renameTestClassCheckBoxStateChanged
    parent.stateChanged(null);
}//GEN-LAST:event_renameTestClassCheckBoxStateChanged

    private void renameTestClassMethodCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_renameTestClassMethodCheckBoxStateChanged
        parent.stateChanged(null);
    }//GEN-LAST:event_renameTestClassMethodCheckBoxStateChanged
                                                             
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label;
    private javax.swing.JTextField nameField;
    private javax.swing.JCheckBox renameGettersAndCheckersCheckBox;
    private javax.swing.JCheckBox renameTestClassCheckBox;
    private javax.swing.JCheckBox renameTestClassMethodCheckBox;
    private javax.swing.JCheckBox textCheckBox;
    private javax.swing.JCheckBox updateReferencesCheckBox;
    // End of variables declaration//GEN-END:variables

    public String getNameValue() {
        return nameField.getText();
    }
    
    public boolean searchJavadoc() {
        return textCheckBox.isSelected();
    }
    
    public boolean isUpdateReferences() {
        if (updateReferencesCheckBox.isVisible() && updateReferencesCheckBox.isSelected()) {
            return false;
        }
        return true;
    }
    
    public boolean isRenameGettersSetters() {
        return renameGettersAndCheckersCheckBox.isSelected();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    boolean isRenameTestClass() {
        return renameTestClassCheckBox.isSelected();
    }

    boolean isRenameTestClassMethod() {
        return renameTestClassMethodCheckBox.isSelected();
    }
}
