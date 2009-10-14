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

package org.netbeans.modules.j2ee.ddloaders.multiview;

import org.netbeans.modules.j2ee.dd.api.ejb.CmpField;
import org.netbeans.modules.j2ee.ejbcore.api.methodcontroller.EntityMethodController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

import java.io.IOException;

/**
 * @author pfiala
 */
public class CmpFieldHelper {

    private EntityHelper entityHelper;
    private CmpField field;
    public static final String PROPERTY_FIELD_ROW_CHANGED = "FIELD_ROW_CHANGED"; // NOI18N

    public CmpFieldHelper(EntityHelper entityHelper, CmpField field) {
        this.entityHelper = entityHelper;
        this.field = field;
    }

//    public String getTypeString() {
//        Type type = getType();
//        return type == null ? null : type.getName();
//    }

//    public Type getType() {
//        Method getterMethod = entityHelper.getGetterMethod(getFieldName());
//        return getterMethod == null ? null : getterMethod.getType();
//    }

//    public void reloadType() {
//        setType(getTypeString());
//    }

//    public void setType(String typeString) {
//        setType(JMIUtils.resolveType(typeString));
//    }

//    public void setType(Type newType) {
//        EntityMethodController entityMethodController = entityHelper.getEntityMethodController();
//        entityMethodController.beginWriteJmiTransaction();
//        boolean rollback = true;
//        try {
//            if (newType instanceof PrimitiveType && isPrimary()) {
//                newType = JMIUtils.getPrimitiveTypeWrapper((PrimitiveType) newType);
//            }
//            String fieldName = getFieldName();
//            Method getterMethod = entityHelper.getGetterMethod(fieldName);
//            Method setterMethod = entityHelper.getSetterMethod(fieldName, getterMethod);
//            boolean primary = isPrimary();
//            JavaClass localBusinessInterfaceClass = entityHelper.getLocalBusinessInterfaceClass();
//            entityMethodController.registerClassForSave(localBusinessInterfaceClass);
//            JavaClass remoteBusinessInterfaceClass = entityHelper.getRemoteBusinessInterfaceClass();
//            entityMethodController.registerClassForSave(remoteBusinessInterfaceClass);
//            changeReturnType(Utils.getMethod(localBusinessInterfaceClass, getterMethod), newType);
//            Utils.changeParameterType(Utils.getMethod(localBusinessInterfaceClass, setterMethod), newType);
//            changeReturnType(Utils.getMethod(remoteBusinessInterfaceClass, getterMethod), newType);
//            Utils.changeParameterType(Utils.getMethod(remoteBusinessInterfaceClass, setterMethod), newType);
//            changeReturnType(getterMethod, newType);
//            Utils.changeParameterType(setterMethod, newType);
//            newType = JMIUtils.resolveType(getTypeString());
//            if (primary) {
//                entityHelper.setPrimKeyClass(newType);
//            }
//            JavaClass beanClass = entityHelper.getBeanClass();
//            if (beanClass != null) {
//                entityMethodController.registerClassForSave(beanClass);
//                Method[] methods = JMIUtils.getMethods(beanClass);
//                for (int i = 0; i < methods.length; i++) {
//                    Method method = methods[i];
//                    String name = method.getName();
//                    boolean isCreate = "ejbCreate".equals(name);
//                    boolean isPostCreate = "ejbPostCreate".equals(name);
//                    if (isCreate && primary) {
//                        changeReturnType(method, newType);
//                    }
//                    if (isCreate || isPostCreate) {
//                        List parameters = method.getParameters();
//                        for (Iterator it1 = parameters.iterator(); it1.hasNext();) {
//                            Parameter parameter = (Parameter) it1.next();
//                            if (fieldName.equals(parameter.getName())) {
//                                parameter.setType(newType);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            rollback = false;
//        } finally {
//            entityMethodController.endWriteJmiTransaction(rollback);
//        }
//        entityHelper.cmpFields.firePropertyChange(null);
//        modelUpdatedFromUI();
//    }

    private void modelUpdatedFromUI() {
        entityHelper.modelUpdatedFromUI();
    }

//    private void changeReturnType(Method method, Type type) {
//        if (method != null) {
//            method.setType(type);
//        }
//    }

//    public boolean hasLocalGetter() {
//        return getLocalGetter() != null;
//    }
//
//    private Method getLocalGetter() {
//        return entityHelper.getEntityMethodController().getGetterMethod(getFieldName(), true);
//    }
//
//    public boolean hasLocalSetter() {
//        return getLocalSetter() != null;
//    }
//
//    private Method getLocalSetter() {
//        return entityHelper.getEntityMethodController().getSetterMethod(getFieldName(), true);
//    }
//
//    public boolean hasRemoteGetter() {
//        return getRemoteGetter() != null;
//    }
//
//    private Method getRemoteGetter() {
//        return entityHelper.getEntityMethodController().getGetterMethod(getFieldName(), false);
//    }
//
//    public boolean hasRemoteSetter() {
//        return getRemoteSetter() != null;
//    }
//
//    private Method getRemoteSetter() {
//        return entityHelper.getEntityMethodController().getSetterMethod(getFieldName(), false);
//    }

    public void setLocalGetter(boolean create) {
        entityHelper.updateFieldAccessor(getFieldName(), true, true, create);
    }

    public void setLocalSetter(boolean create) {
        entityHelper.updateFieldAccessor(getFieldName(), false, true, create);
    }

    public void setRemoteGetter(boolean create) {
        entityHelper.updateFieldAccessor(getFieldName(), true, false, create);
    }

    public void setRemoteSetter(boolean create) {
        entityHelper.updateFieldAccessor(getFieldName(), false, false, create);
    }

    public boolean deleteCmpField() {
        String message = NbBundle.getMessage(CmpFieldHelper.class, "MSG_ConfirmDeleteField", field.getFieldName());
        String title = NbBundle.getMessage(CmpFieldHelper.class, "MSG_ConfirmDeleteFieldTitle");
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(message, title, NotifyDescriptor.YES_NO_OPTION);
        if (NotifyDescriptor.YES_OPTION.equals(DialogDisplayer.getDefault().notify(desc))) {
            EntityMethodController entityMethodController = entityHelper.getEntityMethodController();
            try {
                entityMethodController.deleteField(field, entityHelper.ejbJarFile);
                modelUpdatedFromUI();
                return true;
            } catch (IOException e) {
                Utils.notifyError(e);
            }
        }
        return false;
    }


//    private static void removeMethod(JavaClass interfaceClass, Method method) {
//        if (Utils.getMethod(interfaceClass, method) != null) {
//            Utils.removeMethod(interfaceClass, method);
//        }
//    }

    public void setFieldName(String newName) {
//        final IllegalArgumentException ex = FieldCustomizer.validateFieldName(newName);
//        if (ex != null) {
//            ErrorManager.getDefault().notify(ex);
//            return;
//        }
//        String fieldName = getFieldName();
//        boolean primary = isPrimary();
//        final int oldFieldRow = entityHelper.cmpFields. getFieldRow(field);
//        RefactoringSession refactoringSession = RefactoringSession.create("Rename");
//        Method[] methods = JMIUtils.getMethods(entityHelper.getBeanClass());
//        Method ejbPostCreateMethod = (Method) findNamedElement(methods, "ejbPostCreate");
//        Method ejbCreateMethod = (Method) findNamedElement(methods, "ejbCreate");
//        if (ejbCreateMethod != null) {
//            Parameter[] parameters = (Parameter[]) ejbCreateMethod.getParameters().toArray(new Parameter[0]);
//            NamedElement parameter = findNamedElement(parameters, fieldName);
//            if (parameter != null) {
//                prepareRename(refactoringSession, parameter, newName);
//            }
//        }
//        if (ejbPostCreateMethod != null) {
//            Parameter[] parameters = (Parameter[]) ejbPostCreateMethod.getParameters().toArray(new Parameter[0]);
//            NamedElement parameter = findNamedElement(parameters, fieldName);
//            if (parameter != null) {
//                prepareRename(refactoringSession, parameter, newName);
//            }
//        }
//        Method getterMethod = entityHelper.getGetterMethod(fieldName);
//        Method setterMethod = entityHelper.getSetterMethod(fieldName, getterMethod);
//        String getterName = EntityMethodController.getMethodName(newName, true);
//        String setterName = EntityMethodController.getMethodName(newName, false);
//        prepareRename(refactoringSession, getterMethod, getterName);
//        prepareRename(refactoringSession, setterMethod, setterName);
//        prepareRename(refactoringSession, getLocalGetter(), getterName);
//        prepareRename(refactoringSession, getLocalSetter(), setterName);
//        prepareRename(refactoringSession, getRemoteGetter(), getterName);
//        prepareRename(refactoringSession, getRemoteSetter(), setterName);
//        refactoringSession.doRefactoring(true);
//        field.setFieldName(newName);
//        if (primary) {
//            entityHelper.setPrimkeyFieldName(newName);
//        }
//        final int newFieldRow = entityHelper.cmpFields.getFieldRow(field);
//        if (oldFieldRow != newFieldRow) {
//            entityHelper.cmpFields.firePropertyChange(new PropertyChangeEvent(entityHelper.cmpFields,
//                    PROPERTY_FIELD_ROW_CHANGED, new Integer(oldFieldRow), new Integer(newFieldRow)));
//        }
//        modelUpdatedFromUI();
    }

//    private static void prepareRename(RefactoringSession refactoringSession, NamedElement element, String newName) {
//        if (element != null) {
//            RenameRefactoring refactoring = new RenameRefactoring(element);
//            refactoring.setNewName(newName);
//            refactoring.prepare(refactoringSession);
//        }
//    }

//    private static NamedElement findNamedElement(NamedElement[] elements, String name) {
//        for (int i = 0; i < elements.length; i++) {
//            NamedElement element = elements[i];
//            if (name.equals(element.getName())) {
//                return element;
//            }
//        }
//        return null;
//    }

    public void setDescription(String s) {
        field.setDescription(s);
        modelUpdatedFromUI();
    }

    public String getDefaultDescription() {
        return field.getDefaultDescription();
    }

    public String getFieldName() {
        return field.getFieldName();
    }

    public boolean isPrimary() {
        return getFieldName().equals(entityHelper.getPrimkeyField());
    }

    public boolean edit() {
//        Field field = JavaModel.getDefaultExtent().getField().createField();
//        String fieldName = getFieldName();
//        field.setName(fieldName);
//        field.setType(JMIUtils.resolveType(getTypeString()));
//        FieldCustomizer customizer = new FieldCustomizer(field, getDefaultDescription(),
//                entityHelper.hasLocalInterface(), entityHelper.hasRemoteInterface(), hasLocalGetter(),
//                hasLocalSetter(), hasRemoteGetter(), hasRemoteSetter());
//        while (openEditCmpFieldDialog(customizer)) {
//            customizer.isOK();  // apply possible changes in dialog fields
//            String newFieldName = field.getName();
//            if (!fieldName.equals(newFieldName)) {
//                try {
//                    entityHelper.getEntityMethodController().validateNewCmpFieldName(newFieldName);
//                } catch (IllegalArgumentException ex) {
//                    Utils.notifyError(ex);
//                    continue;
//                }
//            }
//            Utils.beginJmiTransaction(true);
//            boolean rollback = true;
//            try {
//                setFieldName(newFieldName);
//                setType(field.getType());
//                setDescription(customizer.getDescription());
//                setLocalGetter(customizer.isLocalGetter());
//                setLocalSetter(customizer.isLocalSetter());
//                setRemoteGetter(customizer.isRemoteGetter());
//                setRemoteSetter(customizer.isRemoteSetter());
//                rollback = false;
//            } finally {
//                Utils.endJmiTransaction(rollback);
//            }
//            modelUpdatedFromUI();
//            return true;
//        }
        return false;
    }

//    private boolean openEditCmpFieldDialog(FieldCustomizer customizer) {
//        String title = Utils.getBundleMessage("LBL_EditCmpField");
//        NotifyDescriptor nd = new NotifyDescriptor(customizer, title, NotifyDescriptor.OK_CANCEL_OPTION,
//                NotifyDescriptor.PLAIN_MESSAGE, null, null);
//        return DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION;
//    }

}
