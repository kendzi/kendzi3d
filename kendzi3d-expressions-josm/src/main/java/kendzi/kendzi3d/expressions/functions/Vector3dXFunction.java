package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;
import org.joml.Vector3d;

public class Vector3dXFunction extends OneParamFunction<Vector3d> implements NamedFunction<Vector3d> {

    @Override
    public Vector3d evalOneParam(Context context, double param0) {

        return new Vector3d(param0, 0, 0);
    }

    @Override
    public String functionName() {
        return "vectorX";
    }
}
