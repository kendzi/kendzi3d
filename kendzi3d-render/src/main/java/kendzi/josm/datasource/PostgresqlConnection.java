package kendzi.josm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresqlConnection {

    /**
     * @param dburl
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static Connection createConnection(
            //String hostname, String port, String dbname,
            String dburl, String username, String password) throws SQLException {
        try {
            try {

                Class.forName("org.postgresql.Driver");

            } catch (ClassNotFoundException e) {
                throw new Exception("Where is your PostgreSQL JDBC Driver? Include in your library path!", e);
            }

            Connection conn = null;
            try {
                conn = DriverManager.getConnection(dburl, username, password);
                //"jdbc:postgresql://" + hostname + ":" + port+"/" + dbname,

            } catch (SQLException e) {
                throw new Exception("Connection Failed! Check output console", e);
            }


            if (conn == null) {
                throw new Exception("Failed to make connection!");
            }

            /*
             * Add the geometry types to the connection. Note that you
             * must cast the connection to the pgsql-specific connection
             * implementation before calling the addDataType() method.
             */
            try {
                ((org.postgresql.PGConnection)conn).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
                ((org.postgresql.PGConnection)conn).addDataType("box3d",Class.forName("org.postgis.PGbox3d"));
                ((org.postgresql.PGConnection)conn).addDataType("hstore",Class.forName("org.openstreetmap.osmosis.hstore.PGHStore"));
            } catch (SQLException e) {
                throw new Exception("Faild to add postgresql extenstion!", e);
            }

            return conn;
        } catch (Exception e) {
            throw new SQLException("error creating connection", e);
        }
    }
}
