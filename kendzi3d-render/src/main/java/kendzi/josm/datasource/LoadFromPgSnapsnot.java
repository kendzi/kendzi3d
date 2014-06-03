package kendzi.josm.datasource;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kendzi.math.geometry.bbox.Bbox2d;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.osmosis.hstore.PGHStore;
import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class LoadFromPgSnapsnot {

    private Connection connection;

    private static void initJOSMMinimal() {
        Main.pref = new Preferences();
        // org.openstreetmap.josm.gui.preferences.map.ProjectionPreference.setProjection();
        org.openstreetmap.josm.gui.preferences.projection.ProjectionPreference.setProjection();
    }

    public static void main(String[] args) throws Exception {

        Connection connection2 = PostgresqlConnection.createConnection("jdbc:postgresql://192.168.1.51:5432/osm2?loglevel=2",
                "osm", "osm");
        List<Long> ids = new ArrayList<Long>();
        ids.add(1l);
        ids.add(2l);
        selectNodesById(connection2, ids);

    }

    private void init(String hostname, String port, String dbname, String username, String password) {
        // BOX3D

        connection = null;
        try {
            connection = PostgresqlConnection.createConnection("url", username, password);

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        } finally {
            //
        }

    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static String NODE_SQL = " SELECT * " + " FROM nodes " + " WHERE " + " geom && ? ";

    static String NODE_BY_ID_SQL = " SELECT * " + " FROM nodes n " + " WHERE " + " n.id = ANY  ( ? ) ";
    // " SELECT * "
    // + " FROM nodes "
    // + " WHERE "
    // + " id in ( ? ) ";//'BOX3D(? ?, ? ?)'::box3d ";

    static String WAY_SQL = " SELECT * " + " FROM ways " + " WHERE " + " bbox && ? ";

    public static ResultSet selectWays(Connection connection, Bbox2d bbox) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(WAY_SQL);

        ps.setObject(1, new PGbox3d(new Point(bbox.getxMin(), bbox.getyMin()), new Point(bbox.getxMax(), bbox.getyMax())));

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static ResultSet selectNodesById(Connection connection, List<Long> id) throws SQLException {

        String sql = " SELECT * " + " FROM nodes n " + " WHERE " + " n.id = ANY  ( ? ) ";

        PreparedStatement ps = connection.prepareStatement(sql);
        Long[] array = id.toArray(new Long[id.size()]);

        Array idArray = connection.createArrayOf("bigint", array);

        ps.setArray(1, idArray);

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static ResultSet selectNodes(Connection connection, Bbox2d bbox) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(NODE_SQL);

        ps.setObject(1, new PGbox3d(new Point(bbox.getxMin(), bbox.getyMin()), new Point(bbox.getxMax(), bbox.getyMax())));

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    private void loadNodes(double lon_min, double lat_min, double lon_max, double lat_max) {

        try {

            // Statement stGetCount = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement(WAY_SQL);

            ps.setObject(1, new PGbox3d(new Point(lon_min, lat_min), new Point(lon_max, lat_max)));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Node loadNode = loadNode(rs);

                System.out.println(loadNode);
            }

            // return rs.getString(1);

        } catch (SQLException e) {
            System.out.println("Could not create statement in JDBC");
            e.printStackTrace();

        }

    }

    private Node loadNode(ResultSet rs) throws SQLException {

        long id = rs.getLong("id");
        int version = rs.getInt("version");

        Node ret = new Node(id, version);

        PGgeometry geom = (PGgeometry) rs.getObject("geom");
        org.postgis.Point point = (org.postgis.Point) geom.getGeometry();

        ret.setCoor(new LatLon(point.x, point.y));

        injectTags(rs, ret);

        return ret;
    }

    public static void injectTags(ResultSet rs, OsmPrimitive p) throws SQLException {

        Map<String, String> tags = new HashMap<String, String>();

        PGHStore dbTags = (PGHStore) rs.getObject("tags");
        if (dbTags != null) {

            for (Entry<String, String> tagEntry : dbTags.entrySet()) {
                tags.put(tagEntry.getKey(), tagEntry.getValue());
                // System.out.println("tag: " + tagEntry.getKey() + " => " +
                // tagEntry.getValue());
            }
        }

        p.setKeys(tags);
    }

    /**
     * Reads all relations ids for sets of members.
     * 
     * @param connection
     * @param nodesIds
     * @param waysIds
     * @param relationIds
     * @return
     * @throws SQLException
     */
    public static List<Long> selectRelationsIdsForMembers(Connection connection, Set<Long> nodesIds, Set<Long> waysIds,
            Set<Long> relationIds) throws SQLException {

        String sql = "" + " SELECT distinct relation_id " + " FROM relation_members " + " where "
                + "     (member_type='N' AND member_id = ANY( ? )) " + " OR  (member_type='W' AND member_id = ANY( ? )) "
                + " OR  (member_type='R' AND member_id = ANY( ? )) ";
        // + " n.id = ANY  ( ? ) ";

        if (relationIds == null) {
            relationIds = new HashSet<Long>();
        }

        PreparedStatement ps = connection.prepareStatement(sql);
        Long[] arrayNodes = nodesIds.toArray(new Long[nodesIds.size()]);
        Long[] arrayWays = waysIds.toArray(new Long[waysIds.size()]);
        Long[] arrayRelations = relationIds.toArray(new Long[relationIds.size()]);

        Array nIdArray = connection.createArrayOf("bigint", arrayNodes);
        Array wIdArray = connection.createArrayOf("bigint", arrayWays);
        Array rIdArray = connection.createArrayOf("bigint", arrayRelations);

        ps.setArray(1, nIdArray);
        ps.setArray(2, wIdArray);
        ps.setArray(3, rIdArray);

        ResultSet rs = ps.executeQuery();
        List<Long> relationsIds = new ArrayList<Long>();

        while (rs.next()) {
            Long relationId = rs.getLong("relation_id");
            relationsIds.add(relationId);
        }

        return relationsIds;
    }

    public static ResultSet selectRelations(Connection connection, List<Long> relationIds) throws SQLException {

        String sql = "" + " SELECT id, version, user_id, tstamp, changeset_id, tags " + " FROM relations "
                + " WHERE id = ANY (?); ";

        PreparedStatement ps = connection.prepareStatement(sql);

        Long[] arrayRelations = relationIds.toArray(new Long[relationIds.size()]);

        Array rIdArray = connection.createArrayOf("bigint", arrayRelations);

        ps.setArray(1, rIdArray);

        return ps.executeQuery();
    }

    public static ResultSet selectRelationMembers(Connection connection, List<Long> relationIds) throws SQLException {

        String sql = "" + " SELECT relation_id, member_id, member_type, member_role, sequence_id " + " FROM relation_members "
                + " where relation_id = ANY( ? ) ";

        PreparedStatement ps = connection.prepareStatement(sql);
        Long[] arrayRelations = relationIds.toArray(new Long[relationIds.size()]);

        Array rIdArray = connection.createArrayOf("bigint", arrayRelations);

        ps.setArray(1, rIdArray);

        return ps.executeQuery();
    }

}
