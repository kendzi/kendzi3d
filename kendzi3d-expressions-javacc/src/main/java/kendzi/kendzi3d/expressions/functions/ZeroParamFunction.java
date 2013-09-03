package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;

public abstract class ZeroParamFunction implements Function {
    public Object eval(Context context, double ... e) {

        FunctionUtil.validateNumOfReqiredParams(0, e);

        return evalZeroParam(context);
    }

    public abstract Object evalZeroParam(Context context);
}
