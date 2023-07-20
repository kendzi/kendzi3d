package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kendzi.josm.kendzi3d.util.ModelUtil;
import kendzi.kendzi3d.buildings.model.roof.lines.RoofLinesModel;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeKeys;
import kendzi.kendzi3d.josm.model.attribute.OsmAttributeValues;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.josm.model.polygon.RelationUtil;
import kendzi.math.geometry.line.LineSegment2d;
import org.joml.Vector2dc;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

public class RoofLinesParser {
    private static final double MAX_HEIGHT = Double.MAX_VALUE;
    private static final double MIN_HEIGHT = 0d;// -Double.MAX_VALUE;

    public static boolean hasRoofLines(OsmPrimitive primitive) {

        if (primitive instanceof Way) {
            Way way = (Way) primitive;

            return hasWayRoofLines(way);
        } else if (primitive instanceof Relation && primitive.isMultipolygon()) {
            Relation mp = (Relation) primitive;

            for (RelationMember relationMember : mp.getMembers()) {
                Way way = relationMember.getWay();
                if (way == null) {
                    continue;
                }

                if (hasWayRoofLines(way)) {
                    return true;
                }
            }
        } else {
            // TODO for relations
            // PolygonWithHolesUtil.findPolygonsWithHoles(pRelation, pPerspective)
        }
        return false;

    }

    /**
     * @param way
     */
    private static boolean hasWayRoofLines(Way way) {
        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            List<OsmPrimitive> referrers = node.getReferrers();
            for (OsmPrimitive ref : referrers) {
                if (ref.equals(way)) {
                    continue;
                }
                if (!(ref instanceof Way)) {
                    continue;
                }
                if (isEdge((Way) ref) || isRidge((Way) ref)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static RoofLinesModel parse(OsmPrimitive primitive, Perspective perspective) {

        Map<Vector2dc, Double> roofHeightMap = new HashMap<>();

        List<Way> edgesWay = new ArrayList<>();
        List<Way> ridgesWay = new ArrayList<>();
        List<Node> apexNode = new ArrayList<>();

        Collection<Way> roofLinesWays = new ArrayList<>();
        Double roofHeight = 0d;

        List<Way> outerWay = new ArrayList<>();
        List<Way> innerWay = new ArrayList<>();

        if (primitive instanceof Way) {
            Way way = (Way) primitive;
            outerWay.add(way);

            roofLinesWays = findRoofLinesWays(way);
            findApexNodes(apexNode, way);

            roofHeight = parseRoofHeight(primitive);
        } else if (primitive instanceof Relation && primitive.isMultipolygon()) {
            Relation mp = (Relation) primitive;
            roofHeight = parseRoofHeight(primitive);

            // for (RelationMember relationMember : mp.getMembers()) {
            // Way way = relationMember.getWay();
            // if (way == null) {
            // continue;
            // }
            //
            // roofLinesWays.addAll(findRoofLinesWays(way));
            //
            // findApexNodes(apexNode, way);
            //
            // }

            outerWay = RelationUtil.filterOuterWays(mp);
            innerWay = RelationUtil.filterInnerWays(mp);

            for (Way way : outerWay) {
                roofLinesWays.addAll(findRoofLinesWays(way));
                findApexNodes(apexNode, way);
            }

            for (Way way : innerWay) {
                roofLinesWays.addAll(findRoofLinesWays(way));
                findApexNodes(apexNode, way);
            }

        } else {
            throw new RuntimeException("unsupported primitive");
        }

        for (Way roofLine : roofLinesWays) {
            // Double wayHeight =
            // ModelUtil.parseHeight(OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(roofLine),
            // null);

            if (isEdge(roofLine)) {
                edgesWay.add(roofLine);
            } else if (isRidge(roofLine)) {
                ridgesWay.add(roofLine);
            }

            findApexNodes(apexNode, roofLine);
        }

        List<LineSegment2d> innerSegments = new ArrayList<>();

        for (Way edgeWay : edgesWay) {
            // ?
            Vector2dc[] points = transform(edgeWay, perspective);
            for (int i = 1; i < points.length; i++) {
                innerSegments.add(new LineSegment2d(points[i - 1], points[i]));
            }
        }

        for (Way ridgeWay : ridgesWay) {
            String height = OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(ridgeWay);
            Double ridgeHeight = ModelUtil.parseHeight(height, roofHeight);

            Vector2dc[] points = transform(ridgeWay, perspective);
            for (Vector2dc point : points) {
                roofHeightMap.put(point, ridgeHeight);
            }

            for (int i = 1; i < points.length; i++) {
                innerSegments.add(new LineSegment2d(points[i - 1], points[i]));
            }
        }

        for (Node apexNodee : apexNode) {

            Vector2dc point = perspective.calcPoint(apexNodee);
            roofHeightMap.put(point, roofHeight);

        }

        for (Way way : outerWay) {
            setNodesAsMinHeight(perspective, roofHeightMap, way);
        }

        for (Way way : innerWay) {
            setNodesAsMinHeight(perspective, roofHeightMap, way);
        }

        return new RoofLinesModel(roofHeightMap, innerSegments, roofHeight);
    }

    /**
     * @param perspective
     * @param roofHeightMap
     * @param way
     */
    private static void setNodesAsMinHeight(Perspective perspective, Map<Vector2dc, Double> roofHeightMap, Way way) {
        Vector2dc[] points = transform(way, perspective);
        for (Vector2dc point2d : points) {
            roofHeightMap.putIfAbsent(point2d, MIN_HEIGHT);
        }
    }

    /**
     * @param apexNode
     * @param roofLine
     */
    private static void findApexNodes(List<Node> apexNode, Way roofLine) {
        for (int i = 0; i < roofLine.getNodesCount(); i++) {
            Node node = roofLine.getNode(i);
            if (isApex(node)) {
                apexNode.add(node);
            }
        }
    }

    /**
     * @param primitive
     * @return
     */
    private static Double parseRoofHeight(OsmPrimitive primitive) {
        Double roofHeight = ModelUtil.parseHeight(OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(primitive), null);
        if (roofHeight == null) {
            roofHeight = ModelUtil.parseHeight(OsmAttributeKeys.BUILDING_ROOF_HEIGHT.primitiveValue(primitive), null);
        }
        if (roofHeight == null) {
            roofHeight = 5d;
        }
        return roofHeight;
    }

    private static Vector2dc[] transform(Way way, Perspective perspective) {

        Vector2dc[] ret = new Vector2dc[way.getNodesCount()];

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            ret[i] = perspective.calcPoint(node);
        }
        return ret;
    }

    private static Collection<Way> findRoofLinesWays(Way way) {

        List<Way> process = new ArrayList<>();
        process.add(way);

        Set<Way> processed = new HashSet<>();
        // processed.add(way);

        while (!process.isEmpty()) {
            Way proc = process.remove(0);

            List<Way> childWays = findChildRoofLines(proc);

            for (Way childWay : childWays) {
                if (processed.contains(childWay)) {
                    continue;
                }

                if (isEdge(childWay) || isRidge(childWay)) {
                    processed.add(childWay);
                    process.add(childWay);
                }
            }
        }

        // test if it is inside outline!
        // TODO

        return processed;
    }

    private static List<Way> findChildRoofLines(Way way) {
        List<Way> ret = new ArrayList<>();

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            List<OsmPrimitive> referrers = node.getReferrers();
            for (OsmPrimitive ref : referrers) {
                if (ref.equals(way)) {
                    continue;
                }
                if (!(ref instanceof Way)) {
                    continue;
                }
                if (isEdge((Way) ref) || isRidge((Way) ref)) {
                    ret.add((Way) ref);
                }

            }
        }
        return ret;
    }

    private static boolean isEdge(Way way) {
        return OsmAttributeKeys.ROOF_EDGE.primitiveKeyHaveValue(way, OsmAttributeValues.YES);
    }

    private static boolean isRidge(Way way) {
        return OsmAttributeKeys.ROOF_RIDGE.primitiveKeyHaveValue(way, OsmAttributeValues.YES);
    }

    private static boolean isApex(Node node) {
        return OsmAttributeKeys.ROOF_APEX.primitiveKeyHaveValue(node, OsmAttributeValues.YES);
    }

    // boolean isRoofLines(OsmPrimitive way) {
    //
    // }

}
