package kendzi.kendzi3d.expressions.expression;

import kendzi.kendzi3d.expressions.Context;


public abstract class ArgExpression <T> implements Expression {

    protected Class<T> expectedParamType;

    public ArgExpression(Class<T> expectedParamType) {
        super();
        this.expectedParamType = expectedParamType;
    }

    @SuppressWarnings("unchecked")
    public T convert(Expression expr, Context context) {
        if (expectedParamType == Double.class ||expectedParamType == double.class) {
            return (T) Converter.toDouble(expr.evaluate(context));
        }
        throw new RuntimeException("unkonown converter to class: " + expectedParamType + " for expr: " + expr);
    }


}
