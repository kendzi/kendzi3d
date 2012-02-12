/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kendzi.josm.kendzi3d.util.StringUtil;
import kendzi.josm.kendzi3d.util.expression.fun.DoubleFunction;
import kendzi.josm.kendzi3d.util.expression.fun.SimpleFunction;

/**
 * Simple expression parser. It expects double value or function defined in context.
 * After function evalue it always return double as result.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class SimpleDoubleExpressionParser {

    static Pattern functionName = Pattern.compile("^\\s*(\\w*)\\((.*)\\)\\s*$");

    /**
     * @param expression expression it can be double or function defined in context
     * @param pContext functions definition
     * @return compiled functions
     * @throws Exception on errors
     */
    public static SimpleFunction compile(String expression, CompileContext pContext) throws Exception {
        try {
            if (StringUtil.isBlankOrNull(expression)) {
                return null;
            }

            SimpleFunction function = getFunction(expression, pContext);
            if (function != null) {
                return function;
            }
            return new DoubleFunction(pContext, evalueDouble(expression));

        } catch (Exception e) {
            throw new Exception("error parsing expression: " + expression + " context: " + pContext, e);
        }
    }

    public static Double evalueDouble(String expression) {
        // XXX
        try {
            return Double.parseDouble(expression);
        } catch (Exception e) {
            throw new RuntimeException("unknown function or double value: " + expression, e);
        }
    }

    private static SimpleFunction getFunction(String expression, CompileContext pContext) {
        if (expression == null || "".equals(expression.trim())) {
            return null;
        }

        Matcher m = functionName.matcher(expression);

        boolean b = m.matches() && true;
        if (b) {
            String funName = m.group(1);
            String [] parms = null;
            if (m.groupCount() > 1 ) {
                String parmsStr = m.group(2);
                if (!StringUtil.isBlankOrNull(parmsStr)) {
                    parms = parmsStr.split(",");
                }
            }

            for (String name : pContext.getFunctionsNames()) {
                if (name.equals(funName)) {

                    return pContext.build(funName, parms);
                }
            }
        }

        return null;
    }
}
