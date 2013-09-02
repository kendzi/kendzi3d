package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;


public abstract class OneParamFunction implements Function {
    public double eval(Context context, double ... e) {
        double param0 = FunctionUtil.getReqiredParam(0, e);

        return evalOneParam(context, param0);
    }

    public abstract double evalOneParam(Context context, double d);
}
