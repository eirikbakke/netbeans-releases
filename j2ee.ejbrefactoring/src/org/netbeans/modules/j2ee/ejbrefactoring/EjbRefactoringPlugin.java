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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.j2ee.ejbrefactoring;

import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.RefactoringPlugin;

/**
 * A plugin for EJB refactorings, only displays a warning message.
 * 
 * @author Erno Mononen
 */
public class EjbRefactoringPlugin implements RefactoringPlugin{
    
    /**
     * The localized message to be displayed.
     */ 
    private final String message;

    public EjbRefactoringPlugin(String message) {
        this.message = message;
    }
    
    public Problem preCheck() {
        return new Problem(false, message);
    }
    
    public Problem checkParameters() {
        return null;
    }
    
    public Problem fastCheckParameters() {
        return null;
    }
    
    public void cancelRequest() {
    }
    
    public Problem prepare(RefactoringElementsBag refactoringElements) {
        return null;
    }
    
    
}
