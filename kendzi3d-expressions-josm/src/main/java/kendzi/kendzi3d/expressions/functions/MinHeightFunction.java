package kendzi.kendzi3d.expressions.functions;

import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.expressions.Context;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class MinHeightFunction extends OneParamFunction implements NamedFunction {

    @Override
    public Object evalOneParam(Context context, double defaultValue) {
        OsmPrimitive primitive = Context.getRequiredContextVariable("osm", context, OsmPrimitive.class);

        Double height = ModelUtil.getMinHeight(primitive, null);

        if (height == null) {
            return defaultValue;
        }
        return height;
    }

    @Override
    public String functionName() {
        return "minHeight";
    }
}
