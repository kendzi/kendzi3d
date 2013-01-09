package kendzi.kendzi3d.render.conf;

import java.util.Properties;

public class RenderEngineConfLoader {

    private final static double CAMERA_ANGLE_X = 0;

    private final static double CAMERA_ANGLE_Y = Math.toRadians(-30);

    private final static int WIDTH = 256;

    private final static int HEIGHT = 256;

    private final static String RESOURCE_DIR = "./";

    private final static String PREFIX = "k3dr.";

    public static RenderEngineConf load(Properties prop) {
        return load(prop, PREFIX);
    }

    public static RenderEngineConf load(Properties prop, String prefix) {
        RenderEngineConf c = new RenderEngineConf();
        c.setCameraAngleX(getDouble(prop, prefix + "camera.angle.x", CAMERA_ANGLE_X));
        c.setCameraAngleY(getDouble(prop, prefix + "camera.angle.y", CAMERA_ANGLE_Y));

        c.setWidth(getInt(prop, prefix + "width", WIDTH));
        c.setHeight(getInt(prop, prefix + "height", HEIGHT));
        c.setResDir(prop.getProperty(prefix + "resource.dir", RESOURCE_DIR));

        return c;

    }

    private static Double getDouble(Properties prop, String key, Double d) {
        String p = prop.getProperty(key, null);
        if (p == null) {
            return d;
        }
        try {
            return Double.parseDouble(p);
        } catch (Exception e) {
            //
        }
        return d;
    }
    private static Integer getInt(Properties prop, String key, Integer d) {
        String p = prop.getProperty(key, null);
        if (p == null) {
            return d;
        }
        try {
            return Integer.parseInt(p);
        } catch (Exception e) {
            //
        }
        return d;
    }
}

