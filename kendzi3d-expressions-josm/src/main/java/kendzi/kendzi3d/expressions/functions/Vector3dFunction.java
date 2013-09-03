package kendzi.kendzi3d.expressions.functions;

import javax.vecmath.Vector3d;

import kendzi.kendzi3d.expressions.Context;

public class Vector3dFunction implements AnyParamFunction, Function, NamedFunction {

    @Override
    public String functionName() {
        return "vector";
    }

    @Override
    public Object eval(Context context, double... e) {
        if (e.length != 0 && e.length != 3) {
            throw new IllegalArgumentException("wrong number of parameters, expected 0 or 3");
        }

        if (e.length == 0) {
            return new Vector3d();
        }

        return new Vector3d(e[0], e[1], e[2]);
    }
}
