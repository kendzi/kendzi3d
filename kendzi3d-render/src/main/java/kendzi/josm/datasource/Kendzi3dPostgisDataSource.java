package kendzi.josm.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class Kendzi3dPostgisDataSource implements javax.sql.DataSource {

    /** Log. */
    private static final Logger log = Logger
            .getLogger(Kendzi3dPostgisDataSource.class);


    private String username;
    private String password;
    private String url;
    //    private String driverClassName;



    /** My log writer. */
    protected PrintWriter logWriter = null;

    @Override
    public PrintWriter getLogWriter() throws SQLException {

        return this.logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new RuntimeException("not implemented");
    }

    Connection conn = null;

    @Override
    public Connection getConnection() throws SQLException {

        return PostgresqlConnection.createConnection(this.url, this.username, this.password);
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {

        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the log
     */
    public static Logger getLog() {
        return log;
    }

}
