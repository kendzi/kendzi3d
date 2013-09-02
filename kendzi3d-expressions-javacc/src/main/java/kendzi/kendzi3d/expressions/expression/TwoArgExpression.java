package kendzi.kendzi3d.expressions.expression;


public abstract class TwoArgExpression<T> extends ArgExpression<T> {

    protected Expression arg1;
    protected Expression arg2;

    public TwoArgExpression(Expression arg1, Expression arg2, Class<T> expectedParamType) {
        super(expectedParamType);
        this.arg1= arg1;
        this.arg2= arg2;
    }
}
