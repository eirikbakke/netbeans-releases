/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008-2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.spiimpl.hints;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.api.java.source.support.EditorAwareJavaSourceTaskFactory;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.java.hints.providers.spi.PositionRefresherHelper;
import org.netbeans.modules.java.hints.providers.spi.PositionRefresherHelper.DocumentVersion;
import org.netbeans.modules.java.hints.spiimpl.options.HintsSettings;
import org.netbeans.modules.parsing.spi.TaskIndexingMode;
import org.netbeans.spi.editor.hints.Context;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.netbeans.spi.editor.hints.settings.FileHintPreferences;
import org.openide.filesystems.FileObject;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
public class HintsTask implements CancellableTask<CompilationInfo> {

    public static final String KEY_HINTS = HintsInvoker.class.getName() + "-hints";
    public static final String KEY_SUGGESTIONS = HintsInvoker.class.getName() + "-suggestions";

    private static final Logger TIMER = Logger.getLogger("TIMER");
    private static final Logger TIMER_EDITOR = Logger.getLogger("TIMER.editor");
    private static final Logger TIMER_CARET = Logger.getLogger("TIMER.caret");

    private final AtomicBoolean cancel = new AtomicBoolean();

    private final boolean caretAware;

    public HintsTask(boolean caretAware) {
        this.caretAware = caretAware;
    }
    
    public void run(CompilationInfo info) {
        cancel.set(false);

        if (org.netbeans.modules.java.hints.spiimpl.Utilities.disableErrors(info.getFileObject()).contains(Severity.VERIFIER)) {
            return;
        }

        Document doc = info.getSnapshot().getSource().getDocument(false);
        long version = doc != null ? DocumentUtilities.getDocumentVersion(doc) : 0;
        long startTime = System.currentTimeMillis();

        int caret = CaretAwareJavaSourceTaskFactory.getLastPosition(info.getFileObject());
        HintsSettings settings = HintsSettings.getSettingsFor(info.getFileObject());
        HintsInvoker inv = caretAware ? new HintsInvoker(settings, caret, cancel) : new HintsInvoker(settings, cancel);
        List<ErrorDescription> result = inv.computeHints(info);

        if (result == null || cancel.get()) {
            return;
        }

        HintsController.setErrors(info.getFileObject(), caretAware ? KEY_SUGGESTIONS : KEY_HINTS, result);

        if (caretAware) {
            SuggestionsPositionRefresherHelper.setVersion(doc, caret);
        } else {
            HintPositionRefresherHelper.setVersion(doc);
        }

        long endTime = System.currentTimeMillis();
        
        TIMER.log(Level.FINE, "Jackpot 3.0 Hints Task" + (caretAware ? " - Caret Aware" : ""), new Object[] {info.getFileObject(), endTime - startTime});

        Logger l = caretAware ? TIMER_CARET : TIMER_EDITOR;

        for (Entry<String, Long> e : inv.getTimeLog().entrySet()) {
            l.log(Level.FINE, e.getKey(), new Object[] {info.getFileObject(), e.getValue()});
        }
    }

    public void cancel() {
        cancel.set(true);
    }


    @ServiceProvider(service=JavaSourceTaskFactory.class)
    public static final class FactoryImpl extends EditorAwareJavaSourceTaskFactory implements ChangeListener {

        public FactoryImpl() {
            super(Phase.RESOLVED, Priority.LOW, TaskIndexingMode.ALLOWED_DURING_SCAN);
            FileHintPreferences.addChangeListener(WeakListeners.change(this, HintsSettings.class));
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new HintsTask(false);
        }

	@Override
	public void stateChanged(ChangeEvent e) {
	    for (FileObject file : getFileObjects()) {
		reschedule(file);
	    }
	}
        
    }

    @ServiceProvider(service=JavaSourceTaskFactory.class)
    public static final class CaretFactoryImpl extends CaretAwareJavaSourceTaskFactory implements ChangeListener {

        public CaretFactoryImpl() {
            super(Phase.RESOLVED, Priority.LOW);
            FileHintPreferences.addChangeListener(WeakListeners.change(this, HintsSettings.class));
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new HintsTask(true);
        }

	@Override
	public void stateChanged(ChangeEvent e) {
	    for (FileObject file : getFileObjects()) {
		reschedule(file);
	    }
	}

    }

    @MimeRegistration(mimeType="text/x-java", service=PositionRefresherHelper.class)
    public static final class HintPositionRefresherHelper extends PositionRefresherHelper<DocumentVersion> {

        public HintPositionRefresherHelper() {
            super(KEY_HINTS);
        }

        @Override
        protected boolean isUpToDate(Context context, Document doc, DocumentVersion oldVersion) {
            return true;
        }

        @Override
        public List<ErrorDescription> getErrorDescriptionsAt(CompilationInfo info, Context context, Document doc) throws BadLocationException {
            int rowStart = Utilities.getRowStart((BaseDocument) doc, context.getPosition());
            int rowEnd = Utilities.getRowEnd((BaseDocument) doc, context.getPosition());

            return new HintsInvoker(HintsSettings.getSettingsFor(info.getFileObject()), rowStart, rowEnd, context.getCancel()).computeHints(info);
        }

        private static void setVersion(Document doc) {
            for (PositionRefresherHelper h : MimeLookup.getLookup("text/x-java").lookupAll(PositionRefresherHelper.class)) {
                if (h instanceof HintPositionRefresherHelper) {
                    ((HintPositionRefresherHelper) h).setVersion(doc, new DocumentVersion(doc));
                }
            }
        }

    }

    @MimeRegistration(mimeType="text/x-java", service=PositionRefresherHelper.class)
    public static final class SuggestionsPositionRefresherHelper extends PositionRefresherHelper<SuggestionsDocumentVersion> {

        public SuggestionsPositionRefresherHelper() {
            super(KEY_SUGGESTIONS);
        }

        @Override
        protected boolean isUpToDate(Context context, Document doc, SuggestionsDocumentVersion oldVersion) {
            return oldVersion.suggestionsCaret == context.getPosition();
        }

        @Override
        public List<ErrorDescription> getErrorDescriptionsAt(CompilationInfo info, Context context, Document doc) throws BadLocationException {
            return new HintsInvoker(HintsSettings.getSettingsFor(info.getFileObject()), context.getPosition(), context.getCancel()).computeHints(info);
        }

        private static void setVersion(Document doc, int caret) {
            for (PositionRefresherHelper h : MimeLookup.getLookup("text/x-java").lookupAll(PositionRefresherHelper.class)) {
                if (h instanceof SuggestionsPositionRefresherHelper) {
                    ((SuggestionsPositionRefresherHelper) h).setVersion(doc, new SuggestionsDocumentVersion(doc, caret));
                }
            }
        }
    }

    private static class SuggestionsDocumentVersion extends DocumentVersion {

        private final int suggestionsCaret;
        
        public SuggestionsDocumentVersion(Document doc, int suggestionsCaret) {
            super(doc);
            this.suggestionsCaret = suggestionsCaret;
        }
    }

}
