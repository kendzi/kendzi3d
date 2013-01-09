package kendzi.kendzi3d.render.conf;

import java.util.Properties;

import kendzi.josm.kendzi3d.util.StringUtil;

public class RenderDataSourceConfLoader {

    private static final String INPUT_SOURCE = "file";
    private static final String FILE_URL = null;
    private static final String DB_URL = null;
    private static final String DB_USERNAME = null;
    private static final String DB_PASSWORD = null;


    private final static String PREFIX = "k3dr.";

    public static RenderDataSourceConf load(Properties prop) {
        return load(prop, PREFIX);
    }

    public static RenderDataSourceConf load(Properties prop, String prefix) {
        RenderDataSourceConf c = new RenderDataSourceConf();

        String inputSource = prop.getProperty(prefix + "input.source", INPUT_SOURCE);
        if (inputSource != null) {
            inputSource = inputSource.toUpperCase();
        }

        c.setInputSource(RenderDataSourceConf.InputSource.valueOf(inputSource));

        c.setFileUrl(prop.getProperty(prefix + "file.url", FILE_URL));

        c.setJdbcUrl(prop.getProperty(prefix + "db.url", DB_URL));
        c.setJdbcUsername(prop.getProperty(prefix + "db.username", DB_USERNAME));
        c.setJdbcPassword(prop.getProperty(prefix + "db.password", DB_PASSWORD));

        validate(c);

        return c;

    }

    private static void validate(RenderDataSourceConf c) {

        if (RenderDataSourceConf.InputSource.FILE.equals(c.getInputSource())) {
            if (StringUtil.isBlankOrNull(c.getFileUrl())) {
                throw new RuntimeException("for file input source parameter file.url is required");
            }

        } else if (RenderDataSourceConf.InputSource.PGSQL.equals(c.getInputSource())) {

            if (StringUtil.isBlankOrNull(c.getJdbcUrl())) {
                throw new RuntimeException("for pgsql input source parameter db.url is required");
            }

            if (StringUtil.isBlankOrNull(c.getJdbcUsername())) {
                throw new RuntimeException("for pgsql input source parameter db.username is required");
            }

            if (StringUtil.isBlankOrNull(c.getJdbcPassword())) {
                throw new RuntimeException("for pgsql input source parameter db.password is required");
            }
        } else {
            throw new RuntimeException("not allowed input source: " + c.getInputSource());
        }
    }
}

