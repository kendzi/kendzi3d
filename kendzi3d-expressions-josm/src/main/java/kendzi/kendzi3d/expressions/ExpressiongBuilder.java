package kendzi.kendzi3d.expressions;

import java.io.StringReader;

import kendzi.kendzi3d.expressions.exeption.ExpressionExeption;
import kendzi.kendzi3d.expressions.expression.Converter;
import kendzi.kendzi3d.expressions.expression.Expression;
import kendzi.kendzi3d.expressions.expression.ExpressionFactory;
import kendzi.kendzi3d.expressions.jj.ExpressionCalc;
import kendzi.kendzi3d.expressions.jj.TokenMgrError;

public class ExpressiongBuilder {
    public static void main(String args[]) throws ExpressionExeption {
        Expression exp = build("1 + 2 * 3");

        System.out.println(exp.evaluate(null));

    }

    public static Expression build(String in) throws ExpressionExeption {
        if (in == null) {
            return new ExpressionFactory.NullExpresion();
        }

        try {

            ExpressionCalc parser = new ExpressionCalc(new StringReader(in));

            Expression res = parser.expr();

            return res;
        } catch (TokenMgrError e) {
            throw new ExpressionExeption("can't parse expression: " + in, e);
        } catch (Exception e) {
            throw new ExpressionExeption("can't parse expression: " + in, e);
        }
    }

    public static double evaluateExpectedDouble(Expression expression, Context context, double defaultValue) {
        try {
            Object res = expression.evaluate(context);
            if (res == null) {
                return defaultValue;
            }

            return Converter.toDouble(res);
        } catch (Exception e) {
            throw new RuntimeException("error parsing expression: " + expression, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T evaluateExpectedDefault(Expression expression, Context context, T defaultValue) {
        Object res = expression.evaluate(context);
        if (res == null) {
            return defaultValue;
        }

        if (res.getClass().isInstance(defaultValue)) {
            return (T) res;
        }
        throw new RuntimeException("expression result is wrong class: " + res.getClass() + " but expected is class: " + defaultValue.getClass());
    }
}
