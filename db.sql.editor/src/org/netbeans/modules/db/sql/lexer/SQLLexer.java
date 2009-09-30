/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.sql.lexer;

import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.db.api.sql.SQLKeywords;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Andrei Badea
 */
public class SQLLexer implements Lexer<SQLTokenId> {

    private final LexerRestartInfo<SQLTokenId> info;
    private final LexerInput input;
    private final TokenFactory<SQLTokenId> factory;
    private State state = State.INIT;
    private int startQuoteChar = -1;
    /** MySQL data type (#152751). */
    private static final String MEDIUMINT = "MEDIUMINT";  //NOI18N

    public SQLLexer(LexerRestartInfo<SQLTokenId> info) {
        this.info = info;
        this.input = info.input();
        this.factory = info.tokenFactory();
    }

    public Token<SQLTokenId> nextToken() {
        for (;;) {
            int actChar = input.read();
            if (actChar == LexerInput.EOF) {
                break;
            }
            switch (state) {
                // The initial state (start of a new token).
                case INIT:
                    switch (actChar) {
                        case '\'': // NOI18N
                            state = State.ISI_STRING;
                            break;
                        case '/':
                            state = State.ISA_SLASH;
                            break;
                        case '#':
                            state = State.ISA_HASH;
                            break;
                        case '=':
                        case '>':
                        case '<':
                        case '+':
                        case ';':
                        case '*':
                        case '!':
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.OPERATOR);
                        case '(':
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.LPAREN);
                        case ')':
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.RPAREN);
                        case ',':
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.COMMA);
                        case '-':
                            state = State.ISA_MINUS;
                            break;
                        case '0':
                            state = State.ISA_ZERO;
                            break;
                        case '.':
                            state = State.ISA_DOT;
                            break;
                        default:
                            // Check for whitespace.
                            if (Character.isWhitespace(actChar)) {
                                state = State.ISI_WHITESPACE;
                                break;
                            }

                            // Check for digit.
                            if (Character.isDigit(actChar)) {
                                state = State.ISI_INT;
                                break;
                            }

                            // Otherwise it's an identifier.
                            if (isStartQuoteChar(actChar)) {
                                startQuoteChar = actChar;
                            }
                            state = State.ISI_IDENTIFIER;
                            break;
                    }
                    break;

                // If we are currently in a whitespace token.
                case ISI_WHITESPACE:
                    if (!Character.isWhitespace(actChar)) {
                        state = State.INIT;
                        input.backup(1);
                        return factory.createToken(SQLTokenId.WHITESPACE);
                    }
                    break;

                // If we are currently in a line comment.
                case ISI_LINE_COMMENT:
                    if (actChar == '\n') {
                        state = State.INIT;
                        return factory.createToken(SQLTokenId.LINE_COMMENT);
                    }
                    break;

                // If we are currently in a block comment.
                case ISI_BLOCK_COMMENT:
                    if (actChar == '*') {
                        state = State.ISA_STAR_IN_BLOCK_COMMENT;
                    }
                    break;

                // If we are currently in a string literal.
                case ISI_STRING:
                    switch (actChar) {
                        case '\\': // NOI18N
                            // escape possible single quote #152325
                            state = State.ISA_BACK_SLASH_IN_STRING;
                            break;
                        case '\'': // NOI18N
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.STRING);
                    }
                    break;

                // If we are after a back slash (\) in string.
                case ISA_BACK_SLASH_IN_STRING:
                    state = State.ISI_STRING;
                    break;

                // If we are currently in an identifier (e.g. a variable name),
                // or a keyword.
                case ISI_IDENTIFIER:
                    if (startQuoteChar != -1) {
                        if (!isEndQuoteChar(startQuoteChar, actChar)) {
                            break;
                        }
                    } else {
                        if (Character.isLetterOrDigit(actChar) || actChar == '_') {
                            break;
                        } else {
                            input.backup(1);
                        }
                    }
                    state = State.INIT;
                    startQuoteChar = -1;
                    return factory.createToken(testKeyword(input.readText()));

                // If we are after a slash (/).
                case ISA_SLASH:
                    switch (actChar) {
                        case '*':
                            state = State.ISI_BLOCK_COMMENT;
                            break;
                        default:
                            state = State.INIT;
                            input.backup(1);
                            return factory.createToken(SQLTokenId.OPERATOR);
                    }
                    break;

                // If we are after a minus (-).
                case ISA_MINUS:
                    switch (actChar) {
                        case '-':
                            state = State.ISI_LINE_COMMENT;
                            break;
                        default:
                            state = State.INIT;
                            input.backup(1);
                            return factory.createToken(SQLTokenId.OPERATOR);
                    }
                    break;

                // If we are after a hash (#).
                case ISA_HASH:
                    if (Character.isWhitespace(actChar)) {
                        // only in MySQL # starts line comment
                        state = State.ISI_LINE_COMMENT;
                    } else {
                        // otherwise in can be identifier (issue 172904)
                        state = State.ISI_IDENTIFIER;
                    }
                    break;

                // If we are in the middle of a possible block comment end token.
                case ISA_STAR_IN_BLOCK_COMMENT:
                    switch (actChar) {
                        case '/':
                            state = State.INIT;
                            return factory.createToken(SQLTokenId.BLOCK_COMMENT);
                        default:
                            state = State.ISI_BLOCK_COMMENT;
                            break;
                    }
                    break;

                // If we are after a 0.
                case ISA_ZERO:
                    switch (actChar) {
                        case '.':
                            state = State.ISI_DOUBLE;
                            break;
                        default:
                            if (Character.isDigit(actChar)) {
                                state = State.ISI_INT;
                                break;
                            } else {
                                state = State.INIT;
                                input.backup(1);
                                return factory.createToken(SQLTokenId.INT_LITERAL);
                            }
                    }
                    break;

                // If we are after an integer.
                case ISI_INT:
                    switch (actChar) {
                        case '.':
                            state = State.ISI_DOUBLE;
                            break;
                        default:
                            if (Character.isDigit(actChar)) {
                                state = State.ISI_INT;
                                break;
                            } else {
                                state = State.INIT;
                                input.backup(1);
                                return factory.createToken(SQLTokenId.INT_LITERAL);
                            }
                    }
                    break;

                // If we are in the middle of what we believe is a floating point /number.
                case ISI_DOUBLE:
                    if (actChar >= '0' && actChar <= '9') {
                        state = State.ISI_DOUBLE;
                        break;
                    } else {
                        state = State.INIT;
                        input.backup(1);
                        return factory.createToken(SQLTokenId.DOUBLE_LITERAL);
                    }

                // If we are after a period.
                case ISA_DOT:
                    if (Character.isDigit(actChar)) {
                        state = State.ISI_DOUBLE;
                    } else { // only single dot
                        state = State.INIT;
                        input.backup(1);
                        return factory.createToken(SQLTokenId.DOT);
                    }
                    break;

            }
        }

        SQLTokenId id = null;
        PartType part = PartType.COMPLETE;
        switch (state) {
            case ISI_WHITESPACE:
                id = SQLTokenId.WHITESPACE;
                break;

            case ISI_IDENTIFIER:
                startQuoteChar = -1;
                id = testKeyword(input.readText());
                break;

            case ISI_LINE_COMMENT:
                id = SQLTokenId.LINE_COMMENT;
                break;

            case ISI_BLOCK_COMMENT:
            case ISA_STAR_IN_BLOCK_COMMENT:
                id = SQLTokenId.BLOCK_COMMENT;
                part = PartType.START;
                break;

            case ISI_STRING:
            case ISA_BACK_SLASH_IN_STRING:
                id = SQLTokenId.INCOMPLETE_STRING; // XXX or string?
                part = PartType.START;
                break;

            case ISA_ZERO:
            case ISI_INT:
                id = SQLTokenId.INT_LITERAL;
                break;

            case ISI_DOUBLE:
                id = SQLTokenId.DOUBLE_LITERAL;
                break;

            case ISA_DOT:
                id = SQLTokenId.DOT;
                break;

            case ISA_SLASH:
                id = SQLTokenId.OPERATOR;
                break;

            case ISA_MINUS:
                id = SQLTokenId.OPERATOR;
                break;
        }

        if (id != null) {
            state = State.INIT;
            return factory.createToken(id, input.readLength(), part);
        }

        if (state != State.INIT) {
            throw new IllegalStateException("Unhandled state " + state + " at end of file");
        }

        return null;
    }

    public Object state() {
        return null;
    }

    public void release() {
    }

    private static boolean isStartQuoteChar(int start) {
        return start == '\"' || // SQL-99
                start == '`' || // MySQL
                start == '[';    // MS SQL Server
    }

    private static boolean isEndQuoteChar(int start, int end) {
        return start == '\"' && end == start || // SQL-99
                start == '`' && end == start || // MySQL
                start == '[' && end == ']';      // MS SQL Server
    }

    private static SQLTokenId testKeyword(CharSequence value) {
        if (SQLKeywords.isSQL99Keyword(value.toString()) || MEDIUMINT.equalsIgnoreCase(value.toString())) {
            return SQLTokenId.KEYWORD;
        } else {
            return SQLTokenId.IDENTIFIER;
        }
    }

    private static enum State {

        INIT,
        ISI_WHITESPACE, // inside white space
        ISI_LINE_COMMENT, // inside line comment --
        ISI_BLOCK_COMMENT, // inside block comment /* ... */
        ISI_STRING, // inside string constant
        ISI_IDENTIFIER, // inside identifier
        ISA_SLASH, // slash char
        ISA_HASH, // hash char '#'
        ISA_MINUS,
        ISA_STAR_IN_BLOCK_COMMENT, // after '*' in a block comment
        // XXX is ISA_ZERO really needed?
        ISA_ZERO, // after '0'
        ISI_INT, // integer number
        ISI_DOUBLE, // double number
        ISA_DOT, // after '.'
        ISA_BACK_SLASH_IN_STRING // after \ in string
    }
}
