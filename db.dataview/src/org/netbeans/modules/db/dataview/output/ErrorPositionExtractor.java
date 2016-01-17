/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2016 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2016 Sun Microsystems, Inc.
 */
package org.netbeans.modules.db.dataview.output;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for extracting error positions out of SQLExceptions.
 */
public class ErrorPositionExtractor {

    private static final Logger LOG = Logger.getLogger(ErrorPositionExtractor.class.getName());

    private ErrorPositionExtractor() {
    }

    /**
     * Extract error location from the supplied input.
     *
     * <p>
     * Some DB vendors supply position information if errors are encountered.
     * What and how this is supported depends on the vendor. This class bundles
     * the approaches and offers a central place for the implementation.</p>
     *
     * <p>
     * For DB specific details please see the corresponding implementation.</p>
     *
     * @param con Connection that was in use or NULL if not available
     * @param stmt Statement that was in use or NULL if not available
     * @param ex Exception that was recorded an should be analysed
     * @param sql The SQL that was executed
     * @return integer describing the position as a zero-based offset into the
     * supplied sql
     */
    public static int extractErrorPosition(Connection con, Statement stmt, Throwable ex, String sql) {
        try {
            if (ex == null || con == null) {
                return -1;
            } else if (con.getMetaData().getDriverName().toLowerCase().contains("postgresql")) {
                return extractErrorPositionForPostgresql(con, stmt, ex, sql);
            } else if (con.getMetaData().getDriverName().toLowerCase().contains("informix")) {
                return extractErrorPositionForInformix(con, stmt, ex, sql);
            } else if (con.getMetaData().getDriverName().toLowerCase().contains("derby")) {
                return extractErrorPositionForDerby(con, stmt, ex, sql);
            } else if (con.getMetaData().getDriverName().toLowerCase().contains("h2 jdbc")) {
                return extractErrorPositionForH2(con, stmt, ex, sql);
            } else {
                return -1;
            }
        } catch (Exception innerEx) {
            LOG.log(Level.FINE, "Failed to extract ErrorPosition", innerEx);
            return -1;
        }
    }

    /**
     * Extract location information for PostgreSQL
     *
     * <p>
     * PostgreSQL derives its exception from java.sql.SQLException and adds a
     * ServerErrorMessage. This messages contains also the position.</p>
     *
     * <p>
     * In this case the data is extracted via reflection.</p>
     */
    private static int extractErrorPositionForPostgresql(Connection con, Statement stmt, Throwable ex, String sql) {
        if (ex == null) {
            return -1;
        }

        Class exceptionClass = ex.getClass();
        if (exceptionClass.getName().equals("org.postgresql.util.PSQLException")) {
            try {
                Method getServerErrorMessage = exceptionClass.getMethod("getServerErrorMessage");
                Object serverErrorMessage = getServerErrorMessage.invoke(ex);
                Class messageClass = serverErrorMessage.getClass();
                Method getPosition = messageClass.getMethod("getPosition");
                Integer result = (Integer) getPosition.invoke(serverErrorMessage);
                if (result != null && result > 0) {
                    return result - 1;
                } else {
                    return -1;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException innerEx) {
                LOG.log(Level.FINE, "Failed to parse PostgreSQL error", innerEx);
                return -1;
            }
        } else {
            LOG.log(Level.FINE, "Caught PostgreSQL exception, that is not subclass of PSQLException", ex);
            return -1;
        }
    }

    /**
     * Extract location information for Informix DB.
     *
     * <p>
     * For Informix the location information is not present in the exception
     * itself. It has to be extracted from the connection.</p>
     *
     * <p>
     * In this case the connection is accessed via reflection to call the
     * corresponding method.</p>
     *
     * <p>
     * In case connection gets wrapped later it needs to be unwrapped here.</p>
     */
    private static int extractErrorPositionForInformix(Connection con, Statement stmt, Throwable ex, String sql) {
        // try to get exact position from exception message
        if (ex == null) {
            return -1;
        }

        Class connectionClass = con.getClass();
        if (connectionClass.getName().startsWith("com.informix.jdbc")) {
            try {
                Method getSQLStatementOffset = connectionClass.getMethod("getSQLStatementOffset");
                int column = (Integer) getSQLStatementOffset.invoke(con);
                if (column <= 0) {
                    return -1;
                } else {
                    return column - 1;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException innerEx) {
                LOG.log(Level.FINE, "Failed to extract informix error location", innerEx);
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Extract location information for Derby DB.
     *
     * <p>
     * Based on the pattern '{@code at line LINE, column COLUMN.}' the message
     * is analysed and the resulting position is returned.</p>
     */
    private static final Pattern positionPatternDerby = Pattern.compile("at line (\\d+), column (\\d+)");

    private static int extractErrorPositionForDerby(Connection con, Statement stmt, Throwable ex, String sql) {
        if (!(ex instanceof SQLException)) {
            return -1;
        }

        SQLException se = (SQLException) ex;

        String msg = se.getMessage();

        Matcher matcher = positionPatternDerby.matcher(msg);

        if (matcher.find()) {
            int line = Integer.parseInt(matcher.group(1));
            int lineOffset = 0;
            int column = Integer.parseInt(matcher.group(2)) - 1;
            for (int toSkip = line - 1; toSkip > 0; toSkip--) {
                lineOffset = sql.indexOf("\n", lineOffset) + 1;
            }
            return lineOffset + column;
        }

        return -1;
    }

    /**
     * Extract location information for H2 DB.
     *
     * <p>
     * This is a hybrid - error location can only be extracted for the
     * errorcodes:</p>
     *
     * <dl>
     * <dt>42000</dt>
     * <dd>SYNTAX_ERROR_1 - he error with code 42000 is thrown when trying to
     * execute an invalid SQL statement. (Messageformat: '{@code Syntax error in SQL statement {0}}')</dd>
     * <dt>42001</dt>
     * <dd>SYNTAX_ERROR_2 - The error with code 42001 is thrown when trying to
     * execute an invalid SQL statement. (Messageformat: '{@code Syntax error in SQL statement {0}; expected {1}}')</dd>
     * </dl>
     * 
     * The error messages in H2 always contain the english localised version,
     * so string parsing can work. The sql in the message is identifier quoted.
     */
    private static final Pattern h2SyntaxPattern = Pattern.compile("Syntax error in SQL statement \"((([^\"])|(\"\"))*)\"");
    private static int extractErrorPositionForH2(Connection con, Statement stmt, Throwable ex, String sql) {
        if (!(ex instanceof SQLException)) {
            return -1;
        }

        SQLException se = (SQLException) ex;
        
        Matcher matcher;
        if(se.getErrorCode() == 42000 || se.getErrorCode() == 42001) {
            matcher = h2SyntaxPattern.matcher(se.getMessage());
        } else {
            return -1;
        }
        
        if (matcher.find()) {
            String errorSQL = matcher.group(1).replace("\"\"", "\"");
        
            String lowerReference = sql.toLowerCase();
            String lowerError = errorSQL.toLowerCase();
            
            int endIdx = Math.min(lowerReference.length(), lowerError.length());
            
            for(int i = 0; i < endIdx && i < (lowerError.length() - 3); i++) {
                if(lowerReference.charAt(i) != lowerError.charAt(i)) {
                    if("[*]".equals(errorSQL.substring(i, i + 3))) {
                        return i;
                    } else {
                        return -1;
                    }
                }
            }
            
            // Corner case: lastIdx is the problem => detect it
            if(lowerError.length() >= (endIdx + 2)) {
                if(lowerError.startsWith("[*]", endIdx)) {
                    return endIdx;
                }
            }
            
            return -1;
        }

        return -1;
    }
}
