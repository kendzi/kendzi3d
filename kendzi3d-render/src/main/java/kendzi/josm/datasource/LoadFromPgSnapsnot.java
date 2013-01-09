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
        //        org.openstreetmap.josm.gui.preferences.map.ProjectionPreference.setProjection();
        org.openstreetmap.josm.gui.preferences.projection.ProjectionPreference.setProjection();
    }

    public static void main(String[] args) throws Exception {

        //        initJOSMMinimal();
        //
        //        LoadFromPgSnapsnot l = new LoadFromPgSnapsnot();
        //        l.init("192.168.1.51", "5432", "osm2", "osm", "osm");
        //
        //        //        geom && 'BOX3D(15.76107 52.13712, 15.76915 52.14258)'::box3d
        //        //        limit 10
        //        //
        //        //          SetSRID('BOX3D(x1 y1, x2 y2)'::box3d, 900913)
        //
        //        l.loadNodes(15.760067, 52.139158, 15.765367, 52.141634);
        //
        //
        //        l.close();

        Connection connection2 = PostgresqlConnection.createConnection("jdbc:postgresql://192.168.1.51:5432/osm2?loglevel=2", "osm", "osm");
        List<Long> ids = new ArrayList<Long>();
        ids.add(1l);
        ids.add(2l);
        selectNodesById(connection2, ids);

    }

    private void init(String hostname, String port,  String dbname, String username, String password) {
        // BOX3D

        //        id = resultSet.getLong("id");
        //        version = resultSet.getInt("version");
        //        timestamp = new Date(resultSet.getTimestamp("timestamp").getTime());
        //        user = readUserField(resultSet.getBoolean("data_public"), resultSet.getInt("user_id"), resultSet
        //                .getString("display_name"));
        //        changesetId = resultSet.getLong("changeset_id");
        //
        //        //node = new Node(id, version, timestamp, user, changesetId, latitude, longitude);
        //        entityData = new CommonEntityData(id, version, timestamp, user, changesetId);


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

        //






        //        Statement stmt) throws SQLException {
        //            ResultSet rs = null;
        //            try {
        //                rs = stmt.executeQuery(sql);
        //                ResultSet rsToUse = rs;
        //                if (nativeJdbcExtractor != null) {
        //                    rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
        //                }
        //                return rse.extractData(rsToUse);
        //            }
        //            finally {
        //                JdbcUtils.closeResultSet(rs);
        //            }
        //        }
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


    //
    //
    //    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
    //        Assert.notNull(action, "Callback object must not be null");
    //
    //        Connection con = DataSourceUtils.getConnection(getDataSource());
    //        Statement stmt = null;
    //        try {
    //            Connection conToUse = con;
    //            if (this.nativeJdbcExtractor != null &&
    //                    this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
    //                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
    //            }
    //            stmt = conToUse.createStatement();
    //            applyStatementSettings(stmt);
    //            Statement stmtToUse = stmt;
    //            if (this.nativeJdbcExtractor != null) {
    //                stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
    //            }
    //            T result = action.doInStatement(stmtToUse);
    //            handleWarnings(stmt);
    //            return result;
    //        }
    //        catch (SQLException ex) {
    //            // Release Connection early, to avoid potential connection pool deadlock
    //            // in the case when the exception translator hasn't been initialized yet.
    //            JdbcUtils.closeStatement(stmt);
    //            stmt = null;
    //            DataSourceUtils.releaseConnection(con, getDataSource());
    //            con = null;
    //            throw getExceptionTranslator().translate("StatementCallback", getSql(action), ex);
    //        }
    //        finally {
    //            JdbcUtils.closeStatement(stmt);
    //            DataSourceUtils.releaseConnection(con, getDataSource());
    //        }
    //    }

    static String NODE_SQL =
            " SELECT * "
                    + " FROM nodes "
                    + " WHERE "
                    + " geom && ? ";//'BOX3D(? ?, ? ?)'::box3d ";

    static String NODE_BY_ID_SQL =
            " SELECT * "
                    + " FROM nodes n "
                    + " WHERE "
                    + " n.id = ANY  ( ? ) ";
    //            " SELECT * "
    //                    + " FROM nodes "
    //                    + " WHERE "
    //                    + " id in ( ? ) ";//'BOX3D(? ?, ? ?)'::box3d ";

    static String WAY_SQL =
            " SELECT * "
                    + " FROM ways "
                    + " WHERE "
                    + " bbox && ? ";//'BOX3D(? ?, ? ?)'::box3d ";

    //lon_min lat_min, lon_max lat_max

    public static ResultSet selectWays(Connection connection, Bbox bbox) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(WAY_SQL);

        ps.setObject(1, new PGbox3d(
                new Point(bbox.getLon_min(), bbox.getLat_min()),
                new Point(bbox.getLon_max(), bbox.getLat_max())
                ));

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static ResultSet selectNodesById(Connection connection, List<Long> id) throws SQLException {

        String sql =  " SELECT * "
                + " FROM nodes n "
                + " WHERE "
                + " n.id = ANY  ( ? ) ";

        PreparedStatement ps = connection.prepareStatement(sql);//NODE_BY_ID_SQL);
        Long[] array = id.toArray(new Long[id.size()]);

        //        Long[] a2 = new Long[] {270336132l };

        Array idArray = connection.createArrayOf("bigint", array);

        ps.setArray(1, //new Array() id.toArray(new Long[id.size()])
                idArray);

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static ResultSet selectNodes(Connection connection, Bbox bbox) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(NODE_SQL);

        ps.setObject(1, new PGbox3d(
                new Point(bbox.getLon_min(), bbox.getLat_min()),
                new Point(bbox.getLon_max(), bbox.getLat_max())
                ));

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    private void loadNodes(double lon_min, double lat_min, double lon_max, double lat_max) {

        try {

            //            Statement stGetCount = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement(WAY_SQL);

            ps.setObject(1, new PGbox3d(
                    new Point(lon_min, lat_min),
                    new Point(lon_max, lat_max)
                    ));

            //            ps.setDouble(1, lon_min);
            //            ps.setDouble(2, lat_min);
            //            ps.setDouble(3, lon_max);
            //            ps.setDouble(4, lat_max);

            ResultSet rs = ps.executeQuery();

            //
            //            ResultSet rs =
            //                    stGetCount.executeQuery(NODE_SQL);
            //            //ResultSet rs = stGetCount.executeQuery("SELECT SUM(import_count -import_remaining) from  xx_queue_table") ;

            while (rs.next()) {
                //rs.next();

                //injectOsmPrimitive(rs, null);
                Node loadNode = loadNode(rs);

                System.out.println(loadNode);
            }


            //return rs.getString(1);


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

        //System.out.println("point: " + point.x + " , " +point.y);
        return ret;
    }

    public static void injectTags(ResultSet rs, OsmPrimitive p) throws SQLException {

        Map<String, String> tags = new HashMap<String, String>();

        PGHStore dbTags = (PGHStore) rs.getObject("tags");
        if (dbTags != null) {

            for (Entry<String, String> tagEntry : dbTags.entrySet()) {
                tags.put(tagEntry.getKey(), tagEntry.getValue());
                //System.out.println("tag: " + tagEntry.getKey() +  " => " + tagEntry.getValue());
            }
        }

        p.setKeys(tags);
    }
    private void injectOsmPrimitive(ResultSet rs, OsmPrimitive p) throws SQLException {

        PGgeometry geom = (PGgeometry) rs.getObject("geom");
        org.postgis.Point point = (org.postgis.Point) geom.getGeometry();

        System.out.println("point: " + point.x + " , " +point.y);

        long id = rs.getLong("id");

        // rs.getArray("hstore");


        //        Collection<Tag> tags;
        //
        //        entityData = new CommonEntityData(
        //                rs.getLong("id"),
        //                rs.getInt("version"),
        //                new Date(rs.getTimestamp("tstamp").getTime()),
        //                buildUser(rs),
        //                rs.getLong("changeset_id")
        //                );






    }

    /** Reads all relations ids for sets of members.
     * @param connection
     * @param nodesIds
     * @param waysIds
     * @param relationIds
     * @return
     * @throws SQLException
     */
    public static List<Long> selectRelationsIdsForMembers(
            Connection connection,
            Set<Long> nodesIds,
            Set<Long> waysIds,
            Set<Long> relationIds) throws SQLException {

        String sql = ""
                + " SELECT distinct relation_id "
                + " FROM relation_members "
                + " where "
                + "     (member_type='N' AND member_id = ANY( ? )) "
                + " OR  (member_type='W' AND member_id = ANY( ? )) "
                + " OR  (member_type='R' AND member_id = ANY( ? )) ";
        //                        + " n.id = ANY  ( ? ) ";

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

    public static ResultSet selectRelations(
            Connection connection,
            List<Long> relationIds) throws SQLException {

        String sql = ""
                + " SELECT id, version, user_id, tstamp, changeset_id, tags "
                + " FROM relations "
                + " WHERE id = ANY (?); ";

        PreparedStatement ps = connection.prepareStatement(sql);

        Long[] arrayRelations = relationIds.toArray(new Long[relationIds.size()]);

        Array rIdArray = connection.createArrayOf("bigint", arrayRelations);

        ps.setArray(1, rIdArray);

        return ps.executeQuery();
    }

    public static ResultSet selectRelationMembers(
            Connection connection,
            List<Long> relationIds) throws SQLException {

        String sql = ""
                + " SELECT relation_id, member_id, member_type, member_role, sequence_id "
                + " FROM relation_members "
                + " where relation_id = ANY( ? ) ";


        PreparedStatement ps = connection.prepareStatement(sql);
        Long[] arrayRelations = relationIds.toArray(new Long[relationIds.size()]);

        Array rIdArray = connection.createArrayOf("bigint", arrayRelations);

        ps.setArray(1, rIdArray);

        return ps.executeQuery();
    }

}
