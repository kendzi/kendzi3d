package kendzi.kendzi3d.josm.model.polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.Edge;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.EdgeOut;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.Vertex;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Util for polygons with holes.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public final class PolygonWithHolesUtil {

    private PolygonWithHolesUtil() {
        //
    }

    /**
     * @param pRelation
     * @param pPerspective
     * @param ret
     * @return
     */
    public static List<PolygonWithHolesList2d> findPolygonsWithHoles(Relation pRelation, Perspective pPerspective) {

        List<PolygonWithHolesList2d> ret = new ArrayList<PolygonWithHolesList2d>();

        List<AreaWithHoles> waysPolygon = PolygonWithHolesUtil.findAreaWithHoles(pRelation);

        for (AreaWithHoles waysPolygon2 : waysPolygon) {
            List<PolygonList2d> inner = new ArrayList<PolygonList2d>();

            PolygonList2d outer = parse(waysPolygon2.getOuter(), pPerspective);

            if (waysPolygon2.getInner() != null) {

                for (List<ReversableWay> rwList : waysPolygon2.getInner()) {
                    inner.add(parse(rwList, pPerspective));
                }

            }
            ret.add(new PolygonWithHolesList2d(outer, inner));
        }
        return ret;
    }

    private static PolygonList2d parse(List<ReversableWay> outer, Perspective pPerspective) {
        List<Point2d> poly = new ArrayList<Point2d>();

        for (ReversableWay rw : outer) {

            Way way = rw.getWay();

            int size = way.getNodesCount();
            if (size > 0) {

                if (!rw.isReversed()) {

                    // do not take the last node, it is either first of next way or first of first way
                    for (int i = 0; i < size - 1; i++) {
                        Point2d p = pPerspective.calcPoint(way.getNode(i));

                        poly.add(p);
                    }
                } else {

                    // do not take the last node, it is either first of next way or first of first way
                    for (int i = size - 1; i > 0; i--) {
                        Point2d p = pPerspective.calcPoint(way.getNode(i));

                        poly.add(p);
                    }
                }
            }
        }
        return new PolygonList2d(poly);
    }

    public static List<AreaWithHoles> findAreaWithHoles(Relation pRelation) {

        // outers
        List<Way> outersClosed = RelationUtil.filterOuterClosedWays(pRelation);
        List<Way> outersParts = RelationUtil.filterOuterOpenWays(pRelation);

        List<Way> innersClosed = RelationUtil.filterInnerClosedWay(pRelation);
        List<Way> innersParts = RelationUtil.filterInnerOpenWay(pRelation);

        List<List<ReversableWay>> outers = convertWay(outersClosed);
        List<List<ReversableWay>> outerWallParts = connectMultiPolygonParts(outersParts);
        outers.addAll(outerWallParts);

        List<List<ReversableWay>> inners = convertWay(innersClosed);
        List<List<ReversableWay>> innersWallParts = connectMultiPolygonParts(innersParts);
        inners.addAll(innersWallParts);

        return connectPolygonHoles(outers, inners);
    }



    private static List<AreaWithHoles> connectPolygonHoles(List<List<ReversableWay>> outers, List<List<ReversableWay>> inners) {
        List<AreaWithHoles> ret = new ArrayList<AreaWithHoles>();
        for (List<ReversableWay> o : outers) {
            AreaWithHoles wp = new AreaWithHoles();
            wp.setOuter(o);

            // FIXME TODO filter out inners from outers!!
            wp.setInner(inners);

            ret.add(wp);
        }
        return ret;
    }

    /**
     * @param outersParts
     * @return
     */
    private static List<List<ReversableWay>> connectMultiPolygonParts(List<? extends OsmPrimitive> outersParts) {
        List<Edge<Way, Node>> in = new ArrayList<MultiPartPolygonUtil.Edge<Way, Node>>();
        for (OsmPrimitive osmPrimitive : outersParts) {
            Way w = (Way) osmPrimitive;
            if (w.getNodesCount() < 2) {
                // when relation is incomplete
                continue;
            }
            Vertex<Node> v1 = new Vertex<Node>(w.getNode(0));
            Vertex<Node> v2 = new Vertex<Node>(w.getNode(w.getNodesCount() - 1));

            Edge<Way, Node> e = new Edge<Way, Node>(v1, v2, w);
            in.add(e);
        }

        List<List<EdgeOut<Way, Node>>> connect = MultiPartPolygonUtil.connect(in);

        List<List<ReversableWay>> outerWallParts = convert(connect);
        return outerWallParts;
    }

    /**
     * @param connect
     * @return
     */
    private static List<List<ReversableWay>> convert(List<List<EdgeOut<Way, Node>>> connect) {

        List<List<ReversableWay>> outerWallParts = new ArrayList<List<ReversableWay>>();
        for (List<EdgeOut<Way, Node>> list : connect) {
            List<ReversableWay> wallParts = new ArrayList<ReversableWay>();
            for (EdgeOut<Way, Node> edgeOut : list) {
                wallParts.add(new ReversableWay(edgeOut.getEdge().getData(), edgeOut.isReverted()));
            }
            outerWallParts.add(wallParts);
        }
        return outerWallParts;
    }



    private static List<List<ReversableWay>> convertWay(List<? extends OsmPrimitive> outersClosed) {
        List<List<ReversableWay>> ret = new ArrayList<List<ReversableWay>>();
        for (OsmPrimitive osmPrimitive : outersClosed) {
            ret.add(Arrays.asList(new ReversableWay((Way) osmPrimitive, false)));
        }
        return ret;
    }

    public static class AreaWithHoles {
        private List<ReversableWay> outer;
        private List<List<ReversableWay>> inner;

        /**
         * @return the outer
         */
        public List<ReversableWay> getOuter() {
            return this.outer;
        }

        /**
         * @param outer the outer to set
         */
        public void setOuter(List<ReversableWay> outer) {
            this.outer = outer;
        }

        /**
         * @return the inner
         */
        public List<List<ReversableWay>> getInner() {
            return this.inner;
        }

        /**
         * @param inner the inner to set
         */
        public void setInner(List<List<ReversableWay>> inner) {
            this.inner = inner;
        }
    }
}
