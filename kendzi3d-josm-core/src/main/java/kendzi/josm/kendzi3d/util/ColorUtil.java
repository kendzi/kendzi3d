/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util;

import java.awt.Color;
import java.lang.reflect.Field;

/**
 * Util class for parsing osm colors.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class ColorUtil {

    public static Color parseColor(String pColor) {

        Color color = getColor(pColor);

        if (color != null) {
            color = color.brighter().brighter().brighter().brighter();
        }

        if (color == null) {
            try {
                color = Color.decode(pColor);
            } catch (Exception e) {
                //
            }
        }

        return color;
    }

    /**
     * Returns a Color based on 'colorName' which must be one of the predefined colors in
     * java.awt.Color. Returns null if colorName is not valid.
     * @param colorName
     * @return
     */
    public static Color getColor(String colorName) {
        try {
            // Find the field and value of colorName
            Field field = Class.forName("java.awt.Color").getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
