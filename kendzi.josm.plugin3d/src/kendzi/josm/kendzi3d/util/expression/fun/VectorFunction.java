/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression.fun;

import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.util.expression.CompileContext;
import kendzi.josm.kendzi3d.util.expression.Context;
import kendzi.josm.kendzi3d.util.expression.SimpleDoubleExpressionParser;
import kendzi.josm.kendzi3d.util.expression.Vector3dContext;

public class VectorFunction implements SimpleFunction<Vector3d> {

    Vector3dContext context;

    String[] args;

    public VectorFunction(Vector3dContext context, String[] args) {
        this.args = args;
        this.context = context;
    }

    @Override
    public Vector3d eval(Context context) {


        if (this.args == null || this.args.length != 3) {
            return new Vector3d();
        }

        return new Vector3d(
                SimpleDoubleExpressionParser.evalueDouble(this.args[0]),
                SimpleDoubleExpressionParser.evalueDouble(this.args[1]),
                SimpleDoubleExpressionParser.evalueDouble(this.args[2]));

    }

    @Override
    public CompileContext getContext() {
        return this.context;
    }

    @Override
    public String getDefaultName() {
        return "vector";
    }
}
