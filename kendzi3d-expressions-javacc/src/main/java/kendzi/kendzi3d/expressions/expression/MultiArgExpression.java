package kendzi.kendzi3d.expressions.expression;

import java.util.List;

public abstract class MultiArgExpression <T> extends ArgExpression<T> {
    protected List<Expression> args;

    public MultiArgExpression(List<Expression> args, Class<T> expectedParamType) {
        super(expectedParamType);
        this.args = args;
    }

    public List<Expression> getArgs() {
        return args;
    }
}
