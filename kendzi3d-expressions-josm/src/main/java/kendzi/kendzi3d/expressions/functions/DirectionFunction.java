package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.Context;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.kendzi3d.josm.model.direction.Direction;
import kendzi.kendzi3d.josm.model.direction.DirectionParserUtil;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class DirectionFunction extends OneParamFunction implements NamedFunction {

    @Override
    public Object evalOneParam(Context context, double defaultValue) {
        OsmPrimitive primitive = Context.getRequiredContextVariable("osm", context, OsmPrimitive.class);

        String directionValue = OsmAttributeKeys.DIRECTION.primitiveValue(primitive);

        Direction direction = DirectionParserUtil.parse(directionValue);

        if (direction == null) {
            return defaultValue;
        }

        return Math.toDegrees(direction.getAngle());
    }

    @Override
    public String functionName() {
        return "direction";
    }
}
