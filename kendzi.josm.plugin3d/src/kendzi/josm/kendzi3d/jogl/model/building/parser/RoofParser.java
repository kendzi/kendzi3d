package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeValues;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.Parser;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofDirection;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.util.BuildingRoofOrientation;
import kendzi.josm.kendzi3d.util.Direction;
import kendzi.josm.kendzi3d.util.DirectionParserUtil;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class RoofParser {

    /** Parse roof model from way.
     * @param primitive
     * @param perspective
     * @return roof model
     */
    public static RoofModel parse(OsmPrimitive primitive, Perspective3D perspective) {

        if (isRoofLines(primitive)) {
            return RoofLinesParser.parse(primitive, perspective);
        }

        return parseDormerRoof(primitive, perspective);
    }

    private static boolean isRoofLines(OsmPrimitive primitive) {
        if (!StringUtil.isBlankOrNull(parseRoofShape(primitive))) {
            return false;
        }
        // XXX fix for not tagged flat roofs!
        return true;
    }

    private static String parseRoofShape(OsmPrimitive primitive) {

        String type = OsmAttributeKeys._3DR_TYPE.primitiveValue(primitive);
        if (StringUtil.isBlankOrNull(type)) {
            type = OsmAttributeKeys.ROOF_SHAPE.primitiveValue(primitive);
        }
        if (StringUtil.isBlankOrNull(type)) {
            type = OsmAttributeKeys.BUILDING_ROOF_SHAPE.primitiveValue(primitive);
        }
        return type;
    }

    /**
     * @param perspective
     * @param primitive
     * @return
     */
    private static DormerRoofModel parseDormerRoof(OsmPrimitive primitive, Perspective3D perspective) {

        DormerRoofModel roof = new  DormerRoofModel();

        String type = parseRoofShape(primitive);

        RoofTypeAliasEnum roofType = Parser.parseRoofShape(type);
        if (roofType == null) {
            roofType = RoofTypeAliasEnum.FLAT;
        }

        roof.setRoofType(roofType);
        roof.setRoofTypeParameter(Parser.parseRoofTypeParameter(roofType, type));

        Map<String, String> keys = primitive.getKeys();

        String dormer = keys.get(OsmAttributeKeys._3DR_DORMERS.getKey());

        roof.setDormers(Parser.parseMultipleDormers(dormer));
        roof.setDormersFront(Parser.parseSiteDormers("front",keys));
        roof.setDormersLeft(Parser.parseSiteDormers("left",keys));
        roof.setDormersBack(Parser.parseSiteDormers("back",keys));
        roof.setDormersRight(Parser.parseSiteDormers("right",keys));

        Map<MeasurementKey, Measurement> measurements = Parser.parseMeasurements(keys);

        if (measurements.get(MeasurementKey.HEIGHT_1) == null) {

            Double roofHeight = ModelUtil.parseHeight(OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(primitive), null);
            Double roofAngle = ModelUtil.getNumberAttribute(primitive, OsmAttributeKeys.ROOF_ANGLE.getKey(), null);

            if (roofHeight != null) {
                measurements.put(MeasurementKey.HEIGHT_1, new Measurement(roofHeight, MeasurementUnit.METERS));
            } else if (roofAngle != null) {
                measurements.put(MeasurementKey.HEIGHT_1, new Measurement(roofAngle, MeasurementUnit.DEGREES));
            }

        }

        roof.setMeasurements(measurements);

        roof.setDirection(findDirection(primitive, perspective));
        BuildingRoofOrientation parseOrientation = Parser.parseOrientation(keys);
        if (parseOrientation == null) {
            parseOrientation = BuildingRoofOrientation.along;
        }

        roof.setOrientation(parseOrientation);


        return roof;
    }

    /**
     * Find roof direction saved in tag.
     *
     * @param pWay way
     * @return roof direction
     */
    private static RoofDirection findDirectionByDirectionTag(OsmPrimitive pWay) {

        RoofDirection roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_DIRECTION.primitiveValue(pWay), Ortagonal.RIGHT, true);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.DIRECTION.primitiveValue(pWay), Ortagonal.RIGHT, true);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_RIDGE_DIRECTION.primitiveValue(pWay), Ortagonal.NONE, true);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_SLOPE_DIRECTION.primitiveValue(pWay), Ortagonal.RIGHT, true);

        return roofDirection;

    }

    /**
     * @param directionValue
     * @param orthogonal
     * @param soft
     * @return
     */
    private static RoofDirection parseDirectionStr(String directionValue, Ortagonal orthogonal, boolean soft) {
        Direction direction = DirectionParserUtil.parse(directionValue);
        if (direction != null) {
            Vector2d directionVector = direction.getVector();
            if (Ortagonal.LEFT.equals(orthogonal)) {
                directionVector = new Vector2d(-directionVector.y, directionVector.x);
            } else if (Ortagonal.RIGHT.equals(orthogonal)) {
                directionVector = new Vector2d(directionVector.y, -directionVector.x);
            }
            return new RoofDirection(directionVector, soft);
        }
        return null;
    }

    enum Ortagonal {
        NONE,
        LEFT,
        RIGHT
    }

    private static RoofDirection findDirection(OsmPrimitive pWay, Perspective3D pPerspective) {

        Vector2d direction = null;
        boolean soft = false;

        direction = findDirectionByRelation(pWay);
        if (direction != null) {
            return new RoofDirection(direction, soft);
        }
        if (pWay instanceof Way) {
            direction = findDirectionByPoints((Way) pWay, pPerspective);
            if (direction != null) {
                return new RoofDirection(direction, soft);
            }
        } else {
            //TODO
        }

        return findDirectionByDirectionTag(pWay);

    }


    private static Vector2d findDirectionByRelation(OsmPrimitive pWay) {
        // TODO
        // XXX add support for relations
        return null;
    }

    private static Vector2d findDirectionByPoints(Way pWay, Perspective3D pPerspective) {
        Point2d directionBegin = findPoint(OsmAttributeKeys._3DR_DIRECTION.getKey(), OsmAttributeValues.BEGIN.getValue(), pWay, pPerspective);
        if (directionBegin == null) {
            directionBegin = pPerspective.calcPoint(pWay.getNode(0));
        }



        Point2d directionEnd = findPoint(OsmAttributeKeys._3DR_DIRECTION.getKey(), OsmAttributeValues.END.getValue(), pWay, pPerspective);
        if (directionBegin != null && directionEnd != null) {
            Vector2d direction = new Vector2d(directionEnd);
            direction.sub(directionBegin);
            return direction;
        }

        return null;
    }

    private static Point2d findPoint(String pKey, String pValue, Way pWay, Perspective3D pPerspective) {

        for (int i = 0; i < pWay.getNodesCount(); i++) {

            Node node = pWay.getNode(i);
            if (pValue.equals(node.get(pKey))) {
                return pPerspective.calcPoint(node);
            }
        }
        return null;
    }

}
