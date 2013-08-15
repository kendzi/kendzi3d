/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression.fun;

import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.util.expression.CompileContext;
import kendzi.josm.kendzi3d.util.expression.Context;
import kendzi.math.geometry.AngleUtil;
import kendzi.math.geometry.point.Vector2dUtil;

public class WayNodeDirectionFunction extends AbstractSimpleFunction<Double> {


    public WayNodeDirectionFunction(CompileContext context, String[] args) {
        super(context, args);
    }

    @Override
    public Double eval(Context context) {
        Double argument = getOptionalArgument(0, Double.class);
        if (argument == null) {
            argument = 0d;
        }
        Vector2d bisector = getRequiredContextVariable("bisector", context, Vector2d.class);

        return Math.toDegrees(AngleUtil.angle(Vector2dUtil.orthogonal(bisector))) + argument;
    }

    @Override
    public String getDefaultName() {
        return "wayNodeDirection";
    }

}
