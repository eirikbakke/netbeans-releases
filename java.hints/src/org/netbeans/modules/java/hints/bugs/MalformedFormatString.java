/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.hints.bugs;

import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.DuplicateFormatFlagsException;
import java.util.EnumSet;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.IllegalFormatPrecisionException;
import java.util.IllegalFormatWidthException;
import java.util.List;
import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.MissingFormatWidthException;
import java.util.UnknownFormatConversionException;
import java.util.UnknownFormatFlagsException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.java.hints.ArithmeticUtilities;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.java.hints.ConstraintVariableType;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.TriggerPattern;
import org.netbeans.spi.java.hints.TriggerPatterns;
import org.openide.util.NbBundle;

/**
 * Checks that a String passed to formatter is somehow malformed. It will also attempt to check whether
 * parameters are of acceptable type with respect to individual % format patterns.
 * 
 * The hint uses MessageFormat.format() to report most of the syntax errors. More detailed / precise
 * messages could be provided, if the hint parsed the format string, but that would greatly duplicate
 * processing in JDK for rather low added value.
 * 
 * @author sdedic
 */
@NbBundle.Messages({
    "# {0} - number of parameters in format string",
    "# {1} - actual number of passed parameters",
    "ERR_FormatMissingParameters=Too few parameters passed to format. Format string requires: {0}, actual: {1}",
    "# {0} - error message",
    "ERR_MalformedFormatString=Malformed format string: {0}",
    "# {0} - value type [string representation]",
    "# {1} - format specified",
    "# {2} - parameter index",
    "ERR_InvalidTypeForSpecifier=Invalid value type `{0}'' for format specifier `{1}'', parameter {2}",
    "ERR_NullFormatString=Format string must not be null",
    "# {0} - the specifier char",
    "ERR_UnknownFormatSpecifier=Unknown format specifier: `{0}''",
    "# {0} - flag name",
    "ERR_DuplicateFormatFlag=Duplicate flag: `{0}''",
    "# {0} - width specifier",
    "ERR_InvalidFormatWidth=Invalid formatted width: `{0}''",
    "# {0} - precision specifier",
    "ERR_InvalidFormatPrecision=Invalid formatted precision: `{0}''",
    "# {0} - format string",
    "ERR_FormatWidthRequired=Width must be specified in `{0}''",
    "# {0} - conversion spec",
    "# {1} - flags",
    "ERR_FormatConversionFlags=Flags `{1}'' do not match the conversion `{0}''",
    "# {0} - conversion flag",
    "ERR_UnkownFormatFlag=Unkown flag: `{0}''",
    "# {0} - argument index",
    "ERR_InvalidFormatArgumentIndex=Invalid argument number: `{0}''",
    "ERR_SyntaxError=Syntax error"
})
@Hint(
    displayName = "#DN_MalformedFormatString",
    description = "#DESC_MalformedFormatString",
    category = "bugs",
    enabled = true,
    options = Hint.Options.QUERY,
    suppressWarnings = { "MalformedFormatString" }
)
public class MalformedFormatString {
    /**
     * taken from JDK's Formatter
     */
    private static final String formatSpecifier
        = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"; // NOI18N
    
    private static final Pattern formatPattern = Pattern.compile(formatSpecifier);
    
    /**
     * Subclasses of these types are allowed as parameters to 'date/time' formatter.
     */
    private static final String[] CALENDAR_ALLOWED_TYPES = {
        "java.lang.Long", // NOI18N
        "java.util.Calendar", // NOI18N
        "java.util.Date" // NOI18N
    };

    @TriggerPatterns({
        @TriggerPattern(value="$s.format($f, $vars1$)", constraints = {
            @ConstraintVariableType(type = "java.io.PrintStream", variable = "$s")
        }),
        @TriggerPattern(value="$s.format($f, $vars1$)", constraints = {
            @ConstraintVariableType(type = "java.io.PrintStream", variable = "$s")
        }),
        @TriggerPattern(value="$s.printf($f, $vars1$)", constraints = {
            @ConstraintVariableType(type = "java.io.PrintWriter", variable = "$s")
        }),
        @TriggerPattern(value="$s.format($f, $vars1$)", constraints = {
            @ConstraintVariableType(type = "java.io.PrintWriter", variable = "$s"),
        }),
        @TriggerPattern(value="java.text.MessageFormat.format($f, $vars1$)"),
        @TriggerPattern(value="$s.format($f, $vars1$)", constraints = {
            @ConstraintVariableType(type = "java.text.MessageFormat", variable = "$s")
        }),
        @TriggerPattern(value="java.lang.String.format($f, $vars1$)")
    })
    public static List<ErrorDescription> run(HintContext ctx) {
        TreePath format = ctx.getVariables().get("$f"); // NOI18N
        Collection<? extends TreePath> parameters = ctx.getMultiVariables().get("$vars1$"); // NOI18N
        if (format == null || parameters == null) {
            return null;
        }
        Object ret = ArithmeticUtilities.compute(ctx.getInfo(), format, true, true);
        if (ArithmeticUtilities.isNull(ret)) {
            return Collections.singletonList(ErrorDescriptionFactory.forTree(ctx, format, Bundle.ERR_NullFormatString()));
        }
        if (!(ret instanceof String)) {
            // format is not a constant String expr.
            return null;
        }
        if (parameters.size() == 1) {
            // check the parameter is not an Object[], such parameter will be passed as argument list and cannot
            // be checked
            TypeMirror tm = ctx.getInfo().getTrees().getTypeMirror(parameters.iterator().next());
            if (tm != null && tm.getKind() == TypeKind.ARRAY) {
                TypeMirror ct = ((ArrayType)tm).getComponentType();
                if (ct != null && ct.getKind() == TypeKind.DECLARED) {
                    TypeElement te = (TypeElement)((DeclaredType)ct).asElement();
                    if (te != null && te.getQualifiedName().contentEquals("java.lang.Object")) { // NOI18N
                        return null;
                    }
                }
            }
        }
        List<TreePath>  paramList = new ArrayList<TreePath>(parameters);
        String fmtString = (String)ret;
        String errorMsg = null;
        try {
            new Formatter().format(fmtString, new Object[] {});
        } catch (MissingFormatArgumentException ex) {
            // this is expected, no parameters were passed to trigger just the parsing.
        } catch (UnknownFormatConversionException ex) {
            errorMsg = Bundle.ERR_UnknownFormatSpecifier(ex.getConversion());
        } catch (DuplicateFormatFlagsException ex) {
            errorMsg = Bundle.ERR_DuplicateFormatFlag(ex.getFlags());
        } catch (IllegalFormatWidthException ex) {
            errorMsg = Bundle.ERR_InvalidFormatWidth(ex.getWidth());
        } catch (IllegalFormatPrecisionException ex) {
            errorMsg = Bundle.ERR_InvalidFormatPrecision(ex.getPrecision());
        } catch (MissingFormatWidthException ex) {
            errorMsg = Bundle.ERR_FormatWidthRequired(ex.getFormatSpecifier());
        } catch (FormatFlagsConversionMismatchException ex) {
            errorMsg = Bundle.ERR_FormatConversionFlags(ex.getConversion(), ex.getFlags());
        } catch (UnknownFormatFlagsException ex) {
            errorMsg = Bundle.ERR_UnkownFormatFlag(ex.getFlags());
        } catch (IllegalFormatException ex) {
            errorMsg = Bundle.ERR_SyntaxError();
        }
        
        if (errorMsg != null) {
            // syntax error in the format string:
            return Collections.singletonList(ErrorDescriptionFactory.forTree(ctx, format, Bundle.ERR_MalformedFormatString(errorMsg)));
        }
        
        Matcher matcher = formatPattern.matcher(fmtString);
        int ord = -1;
        int lastArg = -1;
        int requiredArgs = -1;
        List<ErrorDescription> descs = new ArrayList<ErrorDescription>();
        params: for (int i = 0, len = fmtString.length(); i < len; i = matcher.end()) {
            int argIndex = -1;
            if (matcher.find(i)) {
                String idx = matcher.group(1); // index
                String f = matcher.group(2); // flags, may contain < that specifies the last arg
                String t = matcher.group(5); // date/time marker
                String c = matcher.group(6); // normal fomat specifier
                if (f != null && f.contains("<")) { // NOI18N
                    // previous argument index
                    if (lastArg < 0) {
                        descs.add(ErrorDescriptionFactory.forTree(ctx, format, Bundle.ERR_InvalidFormatArgumentIndex("<")));
                        // the argument is not reachable, go on
                        continue;
                    }
                    argIndex = lastArg;
                } else if (idx != null) {
                    argIndex = Integer.parseInt(idx.substring(0, idx.length() - 1)) - 1;
                } else {
                    char cc = c.charAt(0);
                    // do not increment on no-arg format specs.
                    if (cc == '%' || cc == 'n') {
                        argIndex = -1;
                    } else {
                        argIndex = ++ord;
                    }
                }
                if (argIndex == -1) {
                    continue;
                }
                lastArg = argIndex;
                
                if (paramList.size() <= argIndex) {
                    requiredArgs = Math.max(requiredArgs, argIndex + 1);
                    continue;
                }
                TypeMirror tm = ctx.getInfo().getTrees().getTypeMirror(paramList.get(argIndex));
                if (tm == null) {
                    continue;
                }
                TypeKind tk = tm.getKind();
                if (t != null) {
                    // date/time
                    if (tk == TypeKind.LONG) {
                        // OK
                        continue;
                    } else if (tk == TypeKind.DECLARED) {
                        for (int j = CALENDAR_ALLOWED_TYPES.length - 1; j >= 0; j--) {
                            String fqn = CALENDAR_ALLOWED_TYPES[j];
                            Element el = ctx.getInfo().getElements().getTypeElement(fqn);
                            if (el == null || el.getKind() != ElementKind.CLASS) {
                                continue;
                            }
                            if (ctx.getInfo().getTypes().isAssignable(tm, el.asType())) {
                                continue params;
                            }
                        }
                    }
                    descs.add(ErrorDescriptionFactory.forTree(ctx, paramList.get(argIndex), 
                            Bundle.ERR_InvalidTypeForSpecifier(ctx.getInfo().getTypeUtilities().getTypeName(tm), matcher.group(0), argIndex + 1)));
                    continue;
                }
                if (tk == TypeKind.DECLARED) {
                    tk = unboxBoxed(tm);
                    if (tk == null || tk == TypeKind.PACKAGE) {
                        // some weird type, or Object - cannot identify
                        continue;
                    }
                } else if (tk == TypeKind.WILDCARD || tk == TypeKind.OTHER) {
                    continue;
                }
                if (c == null || c.isEmpty()) {
                    continue;
                }
                char spec = Character.toLowerCase(c.charAt(0));
                EnumSet<TypeKind> allowed = ALLOWED_TYPES.get(spec);
                if (allowed == null || !allowed.contains(tk)) {
                    descs.add(ErrorDescriptionFactory.forTree(ctx, paramList.get(argIndex), 
                            Bundle.ERR_InvalidTypeForSpecifier(ctx.getInfo().getTypeUtilities().getTypeName(tm), matcher.group(0), argIndex + 1)));
                }
            } else {
                break;
            }
        }
        if (requiredArgs > 0) {
            descs.add(0, ErrorDescriptionFactory.forTree(ctx, format, 
                    Bundle.ERR_FormatMissingParameters(requiredArgs, paramList.size())));
        }
        return descs;
    }
        
    private static final Map<Character, EnumSet<TypeKind>> ALLOWED_TYPES = new HashMap<>();
    
    /**
     * Some reference types are mapped to TypeKinds for simplicity. String => OTHER, BigInteger => WILDCARD, Object => PACKAGE
     */
    static {
        ALLOWED_TYPES.put('c', EnumSet.of(TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.CHAR, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('d', EnumSet.of(TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('o', EnumSet.of(TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('x', EnumSet.of(TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT, TypeKind.LONG, TypeKind.WILDCARD)); // NOi18N
        
        ALLOWED_TYPES.put('e', EnumSet.of(TypeKind.FLOAT, TypeKind.DOUBLE, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('g', EnumSet.of(TypeKind.FLOAT, TypeKind.DOUBLE, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('f', EnumSet.of(TypeKind.FLOAT, TypeKind.DOUBLE, TypeKind.WILDCARD)); // NOi18N
        ALLOWED_TYPES.put('a', EnumSet.of(TypeKind.FLOAT, TypeKind.DOUBLE, TypeKind.WILDCARD)); // NOi18N
        
        ALLOWED_TYPES.put('c', EnumSet.of(TypeKind.CHAR));

        // the following conversions apply to any data type
        ALLOWED_TYPES.put('s', EnumSet.allOf(TypeKind.class));
        ALLOWED_TYPES.put('b', EnumSet.allOf(TypeKind.class));
        ALLOWED_TYPES.put('h', EnumSet.allOf(TypeKind.class));
    }

    /**
     * Unbox a wrapper type into a TypeKind. Some additional types are mapped to TypeKinds which cannot really appear in 
     * expressions (at least I hope)
     * 
     * @param tm
     * @return 
     */
    private static TypeKind unboxBoxed(TypeMirror tm) {
        TypeElement te = (TypeElement)((DeclaredType)tm).asElement();
        String qn = te.getQualifiedName().toString();
        if (!qn.startsWith("java.lang.")) { // NO18N
            if (qn.equals("java.math.BigInteger")) { // NOI18N
                return TypeKind.WILDCARD;
            }
            return null;
        }
        switch (qn.substring(10)) {
            case "Short": return TypeKind.SHORT; // NOI18N
            case "Long": return TypeKind.LONG; // NOI18N
            case "Byte": return TypeKind.BYTE; // NOI18N
            case "Integer": return TypeKind.INT; // NOI18N
            case "Double": return TypeKind.DOUBLE; // NOI18N
            case "Float": return TypeKind.FLOAT; // NOI18N
            case "Character": return TypeKind.CHAR; // NOI18N
            case "String": return TypeKind.OTHER; // NOI18N
            case "Object": return TypeKind.PACKAGE; // NOI18N
        }
        return null;
    }
}