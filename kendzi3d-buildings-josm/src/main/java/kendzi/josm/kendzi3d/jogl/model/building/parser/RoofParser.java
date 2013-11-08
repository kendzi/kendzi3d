package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofOrientation;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.Parser;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementUnit;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.RoofFrontDirection;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeValues;
import kendzi.kendzi3d.josm.model.direction.AngleDirection;
import kendzi.kendzi3d.josm.model.direction.Direction;
import kendzi.kendzi3d.josm.model.direction.DirectionParserUtil;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.util.StringUtil;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
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
    public static RoofModel parse(OsmPrimitive primitive, Perspective perspective) {

        if (hasRoofLines(primitive)) {
            return RoofLinesParser.parse(primitive, perspective);
        }

        return parseDormerRoof(primitive, perspective);
    }

    private static boolean hasRoofLines(OsmPrimitive primitive) {
        if (!StringUtil.isBlankOrNull(parseRoofShape(primitive))) {
            return false;
        }
        // not tagged flat roofs should be resolved as regular flat roofs!
        return RoofLinesParser.hasRoofLines(primitive);
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
    private static DormerRoofModel parseDormerRoof(OsmPrimitive primitive, Perspective perspective) {

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
            if (roofHeight == null) {
                roofHeight = ModelUtil.parseHeight(OsmAttributeKeys.BUILDING_ROOF_HEIGHT.primitiveValue(primitive), null);
            }

            Double roofAngle = ModelUtil.getNumberAttribute(primitive, OsmAttributeKeys.ROOF_ANGLE.getKey(), null);

            if (roofHeight != null) {
                measurements.put(MeasurementKey.HEIGHT_1, new Measurement(roofHeight, MeasurementUnit.METERS));
            } else if (roofAngle != null) {
                measurements.put(MeasurementKey.HEIGHT_1, new Measurement(roofAngle, MeasurementUnit.DEGREES));
            }

        }

        roof.setMeasurements(measurements);

        roof.setDirection(findDirection(primitive, perspective));
        RoofOrientation parseOrientation = Parser.parseOrientation(keys);
        if (parseOrientation == null) {
            parseOrientation = RoofOrientation.along;
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
    private static RoofFrontDirection findDirectionByDirectionTag(OsmPrimitive pWay) {

        RoofFrontDirection roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_DIRECTION.primitiveValue(pWay), Ortagonal.NONE);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.DIRECTION.primitiveValue(pWay), Ortagonal.NONE);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_RIDGE_DIRECTION.primitiveValue(pWay), Ortagonal.LEFT);

        if (roofDirection != null) {
            return roofDirection;
        }

        roofDirection = parseDirectionStr(
                OsmAttributeKeys.ROOF_SLOPE_DIRECTION.primitiveValue(pWay), Ortagonal.NONE);

        return roofDirection;

    }

    /**
     * @param directionValue
     * @param orthogonal
     * @param soft
     * @return
     */
    private static RoofFrontDirection parseDirectionStr(String directionValue, Ortagonal orthogonal) {
        boolean soft = false;
        Direction direction = DirectionParserUtil.parse(directionValue);
        if (direction != null) {
            Vector2d directionVector = direction.getVector();
            soft = !(direction instanceof AngleDirection);

            if (Ortagonal.LEFT.equals(orthogonal)) {
                directionVector = new Vector2d(-directionVector.y, directionVector.x);
            } else if (Ortagonal.RIGHT.equals(orthogonal)) {
                directionVector = new Vector2d(directionVector.y, -directionVector.x);
            }
            return new RoofFrontDirection(directionVector, soft);
        }
        return null;
    }

    enum Ortagonal {
        NONE,
        LEFT,
        RIGHT
    }

    private static RoofFrontDirection findDirection(OsmPrimitive pWay, Perspective pPerspective) {

        Vector2d direction = null;
        boolean soft = false;

        direction = findDirectionByRelation(pWay, pPerspective);
        if (direction != null) {
            return new RoofFrontDirection(direction, false);
        }
        if (pWay instanceof Way) {
            direction = findDirectionByPoints((Way) pWay, pPerspective);
            if (direction != null) {
                return new RoofFrontDirection(direction, soft);
            }
        } else {
            //TODO
        }

        return findDirectionByDirectionTag(pWay);

    }


    private static Vector2d findDirectionByRelation(OsmPrimitive osmPrimitive, Perspective perspective) {
        if (osmPrimitive instanceof Relation) {
            Relation rel = (Relation) osmPrimitive;

            Node begin = null;
            Node end = null;

            for (int i = rel.getMembersCount() - 1; i >= 0; i--) {

                RelationMember member = rel.getMember(i);

                if (member.isNode()) {

                    if (OsmAttributeKeys.ROOF_DIRECTION_BEGIN.getKey().equals(member.getRole())) {
                        begin = member.getNode();
                    }
                    if (OsmAttributeKeys.ROOF_DIRECTION_END.getKey().equals(member.getRole())) {
                        end = member.getNode();
                    }
                }
            }

            if (begin != null && end != null) {

                Point2d directionBegin = perspective.calcPoint(begin);
                Point2d directionEnd = perspective.calcPoint(end);

                Vector2d direction = new Vector2d(directionEnd);
                direction.sub(directionBegin);

                return direction;
            }
        }
        return null;
    }

    private static Vector2d findDirectionByPoints(Way pWay, Perspective pPerspective) {
        Point2d direction3drBegin = null;
        Point2d direction3drEnd = null;

        Point2d directionBegin = null;
        Point2d directionEnd = null;

        for (int i = pWay.getNodesCount() - 1; i >=0; i--) {
            Node node = pWay.getNode(i);
            if (OsmAttributeKeys._3DR_DIRECTION.primitiveKeyHaveValue(node, OsmAttributeValues.BEGIN)) {
                direction3drBegin = pPerspective.calcPoint(node);
            }
            if (OsmAttributeKeys._3DR_DIRECTION.primitiveKeyHaveValue(node, OsmAttributeValues.END)) {
                direction3drEnd = pPerspective.calcPoint(node);
            }

            if (OsmAttributeKeys.ROOF_DIRECTION.primitiveKeyHaveValue(node, OsmAttributeValues.BEGIN)) {
                directionBegin = pPerspective.calcPoint(node);
            }
            if (OsmAttributeKeys.ROOF_DIRECTION.primitiveKeyHaveValue(node, OsmAttributeValues.END)) {
                directionEnd = pPerspective.calcPoint(node);
            }
        }

        if (direction3drBegin == null) {
            direction3drBegin = pPerspective.calcPoint(pWay.getNode(0));
        }

        if (direction3drBegin != null && direction3drEnd != null) {
            Vector2d direction = new Vector2d(direction3drEnd);
            direction.sub(direction3drBegin);
            return Vector2dUtil.ortagonalRight(direction);
        }

        if (directionBegin != null && directionEnd != null) {
            Vector2d direction = new Vector2d(directionEnd);
            direction.sub(directionBegin);

            return direction;
        }

        return null;
    }

    private static Point2d findPoint(String pKey, String pValue, Way pWay, Perspective pPerspective) {

        for (int i = 0; i < pWay.getNodesCount(); i++) {

            Node node = pWay.getNode(i);
            if (pValue.equals(node.get(pKey))) {
                return pPerspective.calcPoint(node);
            }
        }
        return null;
    }

}
