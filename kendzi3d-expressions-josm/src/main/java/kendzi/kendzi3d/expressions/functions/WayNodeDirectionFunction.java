package kendzi.kendzi3d.expressions.functions;

import javax.vecmath.Vector2d;

import kendzi.kendzi3d.expressions.Context;
import kendzi.math.geometry.AngleUtil;
import kendzi.math.geometry.point.Vector2dUtil;

public class WayNodeDirectionFunction extends ZeroParamFunction implements NamedFunction {

    @Override
    public Object evalZeroParam(Context context) {

        Vector2d bisector = Context.getRequiredContextVariable("bisector", context, Vector2d.class);

        return Math.toDegrees(AngleUtil.angle(Vector2dUtil.orthogonal(bisector)));
    }

    public String functionName() {
        return "wayNodeDirection";
    }
}
