package kendzi.kendzi3d.expressions.expression;


public abstract class OneArgExpression<T> extends ArgExpression<T> {

    protected Expression arg1;

    public OneArgExpression(Expression arg1, Class<T> expectedParamType) {
        super(expectedParamType);
        this.arg1= arg1;
    }
}
