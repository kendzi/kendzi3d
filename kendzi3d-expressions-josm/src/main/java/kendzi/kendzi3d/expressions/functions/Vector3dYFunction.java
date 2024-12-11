package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;
import org.joml.Vector3d;

public class Vector3dYFunction extends OneParamFunction<Vector3d> implements NamedFunction<Vector3d> {

    @Override
    public Vector3d evalOneParam(Context context, double param0) {

        return new Vector3d(0, param0, 0);
    }

    @Override
    public String functionName() {
        return "vectorY";
    }
}
