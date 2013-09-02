package kendzi.kendzi3d.expressions.functions;

import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.expressions.Context;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class HeightFunction extends OneParamFunction implements NamedFunction {

    @Override
    public double evalOneParam(Context context, double defaultValue) {
        OsmPrimitive primitive = Context.getRequiredContextVariable("osm", context, OsmPrimitive.class);

        Double height = ModelUtil.getHeight(primitive, null);
        //        if (height == null) {
        //            height = ModelUtil.parseHeight(defaultValue, null);
        //        }
        if (height == null) {
            return defaultValue;
        }
        return height;
    }

    @Override
    public String functionName() {
        return "height";
    }
}
