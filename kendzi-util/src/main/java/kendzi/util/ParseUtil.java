/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.util;

import java.awt.Color;

/**
 * Util for most used parsers.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public final class ParseUtil {

    private ParseUtil() {
        //
    }

    /**
     * Parse string to double, if any error return null.
     * 
     * @param string
     * @return parsed double or null if can't be parsed
     */
    public static Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            //
        }
        return null;
    }

    /**
     * Parse string to color, if any error return null.
     * 
     * @param colorHex
     *            string with color hex
     * @return parsed double or null if can't be parsed
     */
    public static Color parseHexColor(String colorHex) {
        try {
            return Color.decode(colorHex);
        } catch (Exception e) {
            //
        }
        return null;
    }

}
