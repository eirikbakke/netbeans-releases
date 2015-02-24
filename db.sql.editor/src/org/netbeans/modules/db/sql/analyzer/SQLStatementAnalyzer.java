/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010-2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.sql.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import org.netbeans.api.db.sql.support.SQLIdentifiers.Quoter;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.db.sql.analyzer.SQLStatement.Context;
import org.netbeans.modules.db.sql.editor.StringUtils;
import org.netbeans.modules.db.sql.lexer.SQLTokenId;

/**
 *
 * @author Jiri Rechtacek, Jiri Skrivanek
 */
public class SQLStatementAnalyzer {

    protected TokenSequence<SQLTokenId> seq;
    protected Quoter quoter;
    protected int startOffset;
    protected Context context = Context.START;
    protected final SortedMap<Integer, Context> offset2Context = new TreeMap<Integer, Context>();
    protected final List<SelectStatement> subqueries = new ArrayList<SelectStatement>();

    protected SQLStatementAnalyzer(TokenSequence<SQLTokenId> seq, Quoter quoter) {
        this.seq = seq;
        this.quoter = quoter;
    }

    public static SQLStatement analyze(TokenSequence<SQLTokenId> seq, Quoter quoter) {
        SQLStatementKind kind = SQLStatementAnalyzer.analyzeKind(seq);
        if (kind == null) {
            return null;
        }
        switch (kind) {
            case SELECT:
                return SelectStatementAnalyzer.analyze(seq, quoter);
            case INSERT:
                return InsertStatementAnalyzer.analyze(seq, quoter);
            case DROP:
                return DropStatementAnalyzer.analyze(seq, quoter);
            case UPDATE:
                return UpdateStatementAnalyzer.analyze(seq, quoter);
            case DELETE:
                return DeleteStatementAnalyzer.analyze(seq, quoter);
            case CREATE:
                return CreateStatementAnalyzer.analyze(seq, quoter);
        }
        return null;
    }

    public static SQLStatementKind analyzeKind(TokenSequence<SQLTokenId> seq) {
        seq.moveStart();
        if (!nextToken(seq)) {
            return null;
        }
        if (isKeyword("DECLARE", seq) || isKeyword("SET", seq)) {
            // find SELECT keyword below
            while (isKeyword("DECLARE", seq) || isKeyword("SET", seq)) {
                if (!nextToken(seq, SQLTokenId.COMMA,
                        SQLTokenId.OPERATOR,
                        SQLTokenId.IDENTIFIER,
                        SQLTokenId.STRING,
                        SQLTokenId.LPAREN,
                        SQLTokenId.RPAREN)) {
                    return null;
                }
            }
            if (isKeyword("SELECT", seq)) { // NOI18N
                return SQLStatementKind.SELECT;
            }
        } else if (isKeyword("SELECT", seq)) { // NOI18N
            return SQLStatementKind.SELECT;
        } else if (isKeyword("INSERT", seq)) {  //NOI18N
            return SQLStatementKind.INSERT;
        } else if (isKeyword("DROP", seq)) {  //NOI18N
            return SQLStatementKind.DROP;
        } else if (isKeyword("UPDATE", seq)) {  //NOI18N
            return SQLStatementKind.UPDATE;
        } else if (isKeyword("DELETE", seq)) {  //NOI18N
            return SQLStatementKind.DELETE;
        } else if (isKeyword("CREATE", seq)) {  //NOI18N
            return SQLStatementKind.CREATE;
        }
        return null;
    }

    public static boolean isKeyword(CharSequence keyword, TokenSequence<SQLTokenId> seq) {
        return seq.token().id() == SQLTokenId.KEYWORD && StringUtils.textEqualsIgnoreCase(seq.token().text(), keyword);
    }

    /** Skip whitespace and comments and move to next token. Returns true, if
     * token is available, false otherwise. */
    private static boolean nextToken(TokenSequence<SQLTokenId> seq, SQLTokenId... toSkip) {
        boolean move;
        skip:
        while (move = seq.moveNext()) {
            SQLTokenId id = seq.token().id();
            // left paren found, skip to right paren
            if (toSkip != null && Arrays.asList(toSkip).contains(SQLTokenId.LPAREN) && SQLTokenId.LPAREN.equals(id)) {
                while (move = seq.moveNext()) {
                    id = seq.token().id();
                    if (SQLTokenId.RPAREN.equals(id)) {
                        break;
                    }
                }
                continue;
            }
            if (toSkip != null && Arrays.asList(toSkip).contains(id)) {
                continue;
            }
            switch (id) {
                case WHITESPACE:
                case LINE_COMMENT:
                case BLOCK_COMMENT:
                    break;
                default:
                    break skip;
            }
        }
        return move;
    }

    /** Skip whitespace and comments and move to next token. Parse SELECT
     * subquery if available and move after it. Returns true, if
     * token is available, false otherwise. */
    protected boolean nextToken() {
        boolean move = nextToken(seq);
        if (move) {
            // only if not beginning of SELECT statement
            if ((!(this instanceof SelectStatementAnalyzer) || context.isAfter(Context.SELECT)) && context != Context.BEGIN) {
                return parseSubquery();
            }
        }
        return move;
    }

    /** Parses possible subquery, fills subqueries list and returns true if
     * additional tokens available. */
    private boolean parseSubquery() {
        boolean move = true;  // expects previous seq.moveNext() returned true
        if (SQLStatementAnalyzer.isKeyword("SELECT", seq)) { // NOI18N
            // Looks like a subquery.
            int subStartOffset = seq.offset();
            int parLevel = 1;
            main:
            while (move = seq.moveNext()) {
                switch (seq.token().id()) {
                    case LPAREN:
                        parLevel++;
                        break;
                    case RPAREN:
                        if (--parLevel == 0) {
                            break main;
                        }
                        break;
                }
            }
            if (parLevel == 0 || (!move && parLevel > 0)) {
                int subEndOffset = seq.offset();
                if (!move && parLevel > 0) {
                    // looks like an unfinished subquery
                    subEndOffset += seq.token().length();
                }
                TokenSequence<SQLTokenId> subSeq = seq.subSequence(subStartOffset, subEndOffset);
                SelectStatement subquery = SelectStatementAnalyzer.analyze(subSeq, quoter);
                if (subquery != null) {
                    subqueries.add(subquery);
                }
            }
        }
        return move;
    }

    /** Returns fully qualified identifier or null. */
    protected QualIdent parseIdentifier() {
        List<String> parts = new ArrayList<String>();
        parts.add(getUnquotedIdentifier());
        boolean afterDot = false;
        main:
        while (nextToken()) {
            switch (seq.token().id()) {
                case DOT:
                    afterDot = true;
                    break;
                case IDENTIFIER:
                    if (afterDot) {
                        afterDot = false;
                        parts.add(getUnquotedIdentifier());
                    } else {
                        // next identifier
                        seq.movePrevious();
                        break main;
                    }
                    break;
                default:
                    seq.movePrevious();
                    break main;
            }
        }
        // Remove empty quoted identifiers, like in '"FOO".""."BAR"'.
        // Actually, the example above would obviously be an invalid identifier,
        // but safer and simpler to be forgiving.
        for (Iterator<String> i = parts.iterator(); i.hasNext();) {
            if (i.next().length() == 0) {
                i.remove();
            }
        }
        if (!parts.isEmpty()) {
            return new QualIdent(parts);
        }
        return null;
    }

    protected String getUnquotedIdentifier() {
        return quoter.unquote(seq.token().text().toString());
    }

    protected void moveToContext(Context context) {
        this.context = context;
        offset2Context.put(seq.offset() + seq.token().length(), context);
    }

    /** Parse table name and its alias in TableIdent instance or null. */
    TableIdent parseTableIdent() {
        QualIdent tableName = parseIdentifier();
        if (tableName == null) {
            return null;
        }
        String alias = parseAlias();
        return new TableIdent(tableName, alias);
    }

    /** Parse and returns alias of table or null (e.g. returns c1 if statement
     * is like "select * from customer as c1" or "select * from customer c1". */
    private String parseAlias() {
        String alias = null;
        main:
        while (nextToken()) {
            switch (seq.token().id()) {
                case IDENTIFIER:
                    alias = getUnquotedIdentifier();
                    break;
                case KEYWORD:
                    if (!SQLStatementAnalyzer.isKeyword("AS", seq)) { // NOI18N
                        seq.movePrevious();
                        break main;
                    }
                    break;
                default:
                    seq.movePrevious();
                    break main;
            }
        }
        if (alias != null && alias.length() == 0) {
            alias = null;
        }
        return alias;
    }

    /** Returns TablesClause instance which ease work with aliased tables. */
    protected TablesClause createTablesClause(List<TableIdent> tables) {
        Set<QualIdent> unaliasedTableNames = new TreeSet<QualIdent>();
        Map<String, QualIdent> aliasedTableNames = new HashMap<String, QualIdent>();
        for (TableIdent table : tables) {
            if (table.getAlias() == null) {
                unaliasedTableNames.add(table.getTableName());
            } else {
                if (!aliasedTableNames.containsKey(table.getAlias())) {
                    aliasedTableNames.put(table.getAlias(), table.getTableName());
                }
            }
        }
        return new TablesClause(Collections.unmodifiableSet(unaliasedTableNames), Collections.unmodifiableMap(aliasedTableNames));
    }

    /** Table name identifier and its alias if any. */
    public static class TableIdent implements Comparable<TableIdent> {

        private final QualIdent tableName;
        private final String alias;

        public TableIdent(QualIdent tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }

        public QualIdent getTableName() {
            return tableName;
        }

        public String getAlias() {
            return alias;
        }

        /** Compares aliases if both exist, table names otherwise. */
         public int compareTo(TableIdent that) {
            if (this.getAlias() != null && that.getAlias() != null) {
                return this.getAlias().compareToIgnoreCase(that.getAlias());
            } else {
                return this.getTableName().compareTo(that.getTableName());
            }
        }

        @Override
        public String toString() {
            String aliasText = getAlias() == null ? "" : getAlias() + " alias to ";  //NOI18N
            return aliasText + getTableName().toString();
        }
    }
}
