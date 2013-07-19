package kendzi.kendzi3d.josm.model.polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kendzi.math.geometry.polygon.MultiPartPolygonUtil;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.Edge;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.EdgeOut;
import kendzi.math.geometry.polygon.MultiPartPolygonUtil.Vertex;
import kendzi.util.StringUtil;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

public class PolygonWithHolesUtil {

    public static List<AreaWithHoles> findAreaWithHoles(Relation pRelation) {

        // outers
        List<OsmPrimitive> outersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "outer", null);
        outersClosed.addAll(filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, null, null));

        List<OsmPrimitive> outersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "outer", null);
        outersParts.addAll(filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, null, null));

        List<OsmPrimitive> innersClosed = filterByRoleAndKey(pRelation, OsmPrimitiveType.CLOSEDWAY, "inner", null);
        List<OsmPrimitive> innersParts = filterByRoleAndKey(pRelation, OsmPrimitiveType.WAY, "inner", null);

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
    private static List<List<ReversableWay>> connectMultiPolygonParts(List<OsmPrimitive> outersParts) {
        List<Edge<Way, Node>> in = new ArrayList<MultiPartPolygonUtil.Edge<Way, Node>>();
        for (OsmPrimitive osmPrimitive : outersParts) {
            Way w = ((Way) osmPrimitive);
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

    private static List<OsmPrimitive> filterByRoleAndKey(Relation pRelation, OsmPrimitiveType type, String role, String key) {
        List<OsmPrimitive> ret = new ArrayList<OsmPrimitive>();

        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            if (!type.equals(member.getDisplayType())) {
                continue;
            }

            if (StringUtil.isBlankOrNull(member.getRole()) && StringUtil.isBlankOrNull(role)) {
                ret.add(member.getMember());
                continue;
            }

            if (StringUtil.equalsOrNulls(role, member.getRole())) {
                ret.add(member.getMember());
            }
        }
        return ret;
    }

    private static List<List<ReversableWay>> convertWay(List<OsmPrimitive> outersClosed) {
        List<List<ReversableWay>> ret = new ArrayList<List<ReversableWay>>();
        for (OsmPrimitive osmPrimitive : outersClosed) {
            ret.add(Arrays.asList(new ReversableWay((Way) osmPrimitive, false)));
        }
        return ret;
    }

    public static class AreaWithHoles {
        List<ReversableWay> outer;
        List<List<ReversableWay>> inner;

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
