package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;
import org.joml.Vector3d;

public class Vector3dFunction implements AnyParamFunction, Function<Vector3d>, NamedFunction<Vector3d> {

    @Override
    public String functionName() {
        return "vector";
    }

    @Override
    public Vector3d eval(Context context, double... e) {
        if (e.length != 0 && e.length != 3) {
            throw new IllegalArgumentException("wrong number of parameters, expected 0 or 3");
        }

        if (e.length == 0) {
            return new Vector3d();
        }

        return new Vector3d(e[0], e[1], e[2]);
    }
}
