package kendzi.kendzi3d.render.conf;

public class RenderDataSourceConf {

    //pgsql
    //file //
    private InputSource inputSource;

    private String jdbcUsername;

    private String jdbcPassword;

    private String jdbcUrl;

    private String fileUrl;
    //    String username = this.environment.getProperty("jdbc.username");
    //    String password = this.environment.getProperty("jdbc.password");
    //    String url = this.environment.getProperty("jdbc.url");

    public enum InputSource {
        FILE(),
        PGSQL()
    }

    /**
     * @return the jdbcUsername
     */
    public String getJdbcUsername() {
        return this.jdbcUsername;
    }

    /**
     * @param jdbcUsername the jdbcUsername to set
     */
    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    /**
     * @return the jdbcPassword
     */
    public String getJdbcPassword() {
        return this.jdbcPassword;
    }

    /**
     * @param jdbcPassword the jdbcPassword to set
     */
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * @return the jdbcUrl
     */
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    /**
     * @param jdbcUrl the jdbcUrl to set
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * @return the fileUrl
     */
    public String getFileUrl() {
        return this.fileUrl;
    }

    /**
     * @param fileUrl the fileUrl to set
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * @return the inputSource
     */
    public InputSource getInputSource() {
        return inputSource;
    }

    /**
     * @param inputSource the inputSource to set
     */
    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }
}
