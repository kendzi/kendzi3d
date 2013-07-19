/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util.expression.fun;

import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.josm.kendzi3d.util.expression.CompileContext;
import kendzi.josm.kendzi3d.util.expression.Context;
import kendzi.josm.kendzi3d.util.expression.ModelScaleContext;
import kendzi.josm.kendzi3d.util.expression.SimpleDoubleExpressionParser;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class NormalHeightFunction implements SimpleFunction {

    ModelScaleContext context;

    String[] args;

    public NormalHeightFunction(ModelScaleContext context, String[] args) {
        this.args = args;
        this.context = context;
    }

    @Override
    public Double eval(Context context) {
        if (this.args == null || this.args.length != 2) {
            throw new RuntimeException("fun normalHeight() require two arguments!");
        }

        OsmPrimitive osm = (OsmPrimitive) context.getVariable("osm");
        Double normal = (Double) context.getVariable("normal");

        Double height = ModelUtil.getHeight(osm, null);
        if (height == null) {
            height = ModelUtil.parseHeight(this.args[1], null);
        }
        if (height == null) {
            return null;
        }
        return normal * height * SimpleDoubleExpressionParser.evalueDouble(this.args[0]);
    }

    @Override
    public CompileContext getContext() {

        return this.context;
    }
}
