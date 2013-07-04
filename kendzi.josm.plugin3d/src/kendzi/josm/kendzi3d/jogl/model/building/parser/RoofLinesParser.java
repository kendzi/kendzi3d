package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.ModelUtil;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeKeys;
import kendzi.josm.kendzi3d.jogl.model.attribute.OsmAttributeValues;
import kendzi.josm.kendzi3d.jogl.model.building.model.roof.RoofLinesModel;
import kendzi.math.geometry.line.LineSegment2d;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

public class RoofLinesParser {
    public static RoofLinesModel parse(OsmPrimitive primitive, Perspective3D perspective) {

        Map<Point2d, Double> roofHeightMap = new HashMap<Point2d, Double>();

        Way way = (Way) primitive;

        Collection<Way> roofLinesWays = findRoofLinesWays(way);

        Double roofHeight = ModelUtil.parseHeight(OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(primitive), 1d);


        List<Way> edgesWay = new ArrayList<Way>();
        List<Way> ridgesWay = new ArrayList<Way>();
        List<Node> apexNode = new ArrayList<Node>();



        for (Way roofLine : roofLinesWays) {
            Double wayHeight = ModelUtil.parseHeight(OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(roofLine), null);



            if (isEdge(roofLine)) {
                edgesWay.add(roofLine);
            } else if (isRidge(roofLine)) {
                ridgesWay.add(roofLine);
            }

            for (int i = 0; i < way.getNodesCount(); i++) {
                Node node = way.getNode(i);
                if (isApex(node)) {
                    apexNode.add(node);
                }
            }
        }

        List<LineSegment2d> innerSegments = new ArrayList<LineSegment2d>();

        for (Way edgeWay : edgesWay) {
            // ?
            Point2d [] points = transform(edgeWay, perspective);
            for (int i = 1; i < points.length; i++) {
                innerSegments.add(new LineSegment2d(points[i-1], points[i]));
            }
        }

        for (Way ridgeWay : ridgesWay) {
            String height = OsmAttributeKeys.ROOF_HEIGHT.primitiveValue(ridgeWay);
            Double ridgeHeight = ModelUtil.parseHeight(height, 0d);

            Point2d [] points = transform(ridgeWay, perspective);
            for (int i = 0; i < points.length; i++) {
                roofHeightMap.put(points[i], ridgeHeight);
            }

            for (int i = 1; i < points.length; i++) {
                innerSegments.add(new LineSegment2d(points[i-1], points[i]));
            }
        }

        for (Node apexNodee : apexNode) {

            Point2d point = perspective.calcPoint(apexNodee);
            roofHeightMap.put(point, roofHeight);

        }

        return new RoofLinesModel(roofHeightMap, innerSegments, roofHeight);
    }


    private static Point2d [] transform(Way way, Perspective3D perspective) {

        Point2d [] ret = new Point2d[way.getNodesCount()];

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            ret[i] = perspective.calcPoint(node);
        }
        return ret;
    }



    private static Collection<Way> findRoofLinesWays(Way way) {


        List<Way> process = new ArrayList<Way>();
        process.add(way);

        Set<Way> processed = new HashSet<Way>();
//        processed.add(way);

        while (!process.isEmpty()) {
            Way proc = process.remove(0);

            List<Way> childWays = findChildRoofLines(proc);

            for(Way childWay : childWays) {
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
        //TODO

        return processed;
    }

    private static List<Way> findChildRoofLines(Way way) {
        List<Way> ret = new ArrayList<Way>();

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            List<OsmPrimitive> referrers = node.getReferrers();
            for(OsmPrimitive ref : referrers) {
                if (ref.equals(way)) {
                    continue;
                }
                if (!(ref instanceof Way)) {
                    continue;
                }
                if (isEdge(( Way)ref) || isRidge(( Way)ref)) {
                    ret.add((Way)ref);
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

//    boolean isRoofLines(OsmPrimitive way) {
//
//    }


}
