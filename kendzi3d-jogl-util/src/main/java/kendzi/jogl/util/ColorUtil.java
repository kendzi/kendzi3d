package kendzi.jogl.util;

import java.awt.Color;

/**
 * Util with openGl color methods.
 */
public class ColorUtil {

    /**
     * Converts java color to float array.
     *
     * @param color
     *            color
     * @return array with color chanels
     */
    public static float[] colorToArray(Color color) {
        return color.getRGBComponents(new float[4]);
    }
}
