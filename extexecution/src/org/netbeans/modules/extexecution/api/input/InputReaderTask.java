/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.extexecution.api.input;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Parameters;

/**
 * Task consuming data from the certain reader, processing them with the given
 * processor.
 * <p>
 * When exception occurs while running the task is terminated.
 * Task is responsive to interruption. InputReader is closed on finish (includes
 * both thrown exception and interruption).
 * <p>
 * Task is <i>not</i> finished implicitly by reaching the end of the reader.
 * <div class="nonnormative">
 * <p>
 * Sample usage - reading standard output of the process (throwing the data away):
 * <pre>
 *     Process process = ...
 *     Runnable runnable = InputReaderTask.newTask(
 *         InputReaders.forStream(process.getInputStream(), Charset.defaultCharset()));
 *     executorService.submit(runnable);
 *
 *     ...
 *
 *     executorService.shutdownNow();
 * </pre>
 * Sample usage - forwarding data to standard input of the process:
 * <pre>
 *     Process process = ...
 *     Runnable runnable = InputReaderTask.newTask(
 *         InputReaders.forStream(System.in, Charset.defaultCharset()),
 *         InputProcessors.copying(new OutputStreamWriter(process.getOutputStream())));
 *     executorService.submit(runnable);
 *
 *     ...
 *
 *     executorService.shutdownNow();
 * </pre>
 * </div>
 *
 * @author Petr Hejl
 */
public final class InputReaderTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(InputReaderTask.class.getName());

    private static final int DELAY = 50;

    private final InputReader inputReader;

    private final InputProcessor inputProcessor;

    private InputReaderTask(InputReader inputReader) {
        this(inputReader, null);
    }

    private InputReaderTask(InputReader inputReader,
            InputProcessor inputProcessor) {

        Parameters.notNull("inputReader", inputReader);

        this.inputReader = inputReader;
        this.inputProcessor = inputProcessor;
    }

    /**
     * Creates the new task. The task will read the data from reader
     * throwing them away.
     *
     * @param reader data producer
     * @return task handling the read process
     */
    public static InputReaderTask newTask(InputReader reader) {
        return new InputReaderTask(reader);
    }

    /**
     * Creates the new task. The task will read the data from reader processing
     * them through processor (if any).
     *
     * @param reader data producer
     * @param processor processor consuming the data, may be <code>null</code>
     * @return task handling the read process
     */
    public static InputReaderTask newTask(InputReader reader, InputProcessor processor) {
        return new InputReaderTask(reader, processor);
    }

    /**
     * Task repeatedly reads the data from the InputReader, passing the content
     * to InputProcessor (if any).
     */
    public void run() {
        boolean interrupted = false;
        try {
            while (true) {
                if (Thread.interrupted()) {
                    interrupted = true;
                    break;
                }

                inputReader.readInput(inputProcessor);

                try {
                    // give the producer some time to write the output
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    interrupted = true;
                    break;
                }
            }

            inputReader.readInput(inputProcessor);
        } catch (Exception ex) {
            if (!interrupted && !Thread.currentThread().isInterrupted()) {
                LOGGER.log(Level.FINE, null, ex);
            }
        } finally {
            // perform cleanup
            try {
                inputReader.close();
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, null, ex);
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}