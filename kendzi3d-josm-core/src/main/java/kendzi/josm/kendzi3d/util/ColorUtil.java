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

    public static Color parseColor(String colorName) {

        Color color = NamedColorsEnum.getColorByName(colorName);
        if (color == null) {
            color = getColorByJava(colorName);
        }

        if (color != null) {
            color = brighter(color);
            //color = color.brighter().brighter().brighter().brighter();
        }

        if (color == null) {
            try {
                color = Color.decode(colorName);
            } catch (Exception e) {
                //
            }
        }

        return color;
    }

    private static Color brighter(Color color) {
        // brighter black give value 7,7,7
        float factor = 0.97265625f;

        // into white color
        float r = 256f - factor * (256 - color.getRed());
        float g = 256f - factor * (256 - color.getGreen());
        float b = 256f - factor * (256 - color.getBlue());

        return new Color((int) r, (int) g, (int) b);
    }

    /**
     * Returns a Color based on 'colorName' which must be one of the predefined colors in
     * java.awt.Color. Returns null if colorName is not valid.
     * @param colorName
     * @return
     */
    public static Color getColorByJava(String colorName) {
        try {
            // Find the field and value of colorName
            Field field = Class.forName("java.awt.Color").getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
