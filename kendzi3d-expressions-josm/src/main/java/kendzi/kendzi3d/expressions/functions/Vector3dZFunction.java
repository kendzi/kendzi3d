package kendzi.kendzi3d.expressions.functions;

import javax.vecmath.Vector3d;

import kendzi.kendzi3d.expressions.Context;

public class Vector3dZFunction extends OneParamFunction implements NamedFunction {

    @Override
    public Object evalOneParam(Context context, double param0) {

        return new Vector3d(0, 0, param0);
    }

    @Override
    public String functionName() {
        return "vectorZ";
    }
}
