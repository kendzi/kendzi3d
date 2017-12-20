/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.util;

import kendzi.util.StringUtil;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Util for josm model.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public final class ModelUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelUtil.class);

    /**
     * It is Util no public constructor is allowed.
     */
    private ModelUtil() {
        // nop
    }

    /** Get height of object. Return it in meters [m] or return default value.
     * @param pOsmPrimitive object to take height
     * @param pDefaultHeight default value of height
     * @return height of object
     */
    public static Double getHeight(OsmPrimitive pOsmPrimitive, Double pDefaultHeight) {

        Double height = parseHeight(pOsmPrimitive.get("height"), null);
        if (height != null) {
            return height;
        }

        return parseHeight(pOsmPrimitive.get("est_height"), pDefaultHeight);
    }

    /** Get min_height of object. Return it in meters [m] or return default value.
     * @param pOsmPrimitive object to take height
     * @param pDefaultHeight default value of min_height
     * @return min_height of object
     */
    public static Double getMinHeight(OsmPrimitive pOsmPrimitive, Double pDefaultHeight) {

        return parseHeight(pOsmPrimitive.get("min_height"), pDefaultHeight);

    }

    public static Double parseHeight(String pHeightStr, Double pDefault) {

        if (pHeightStr == null) {
            return pDefault;
        }

        pHeightStr = pHeightStr.trim().toLowerCase().replaceAll(",", ".");

        try {
            // default we take in [m]
            double mult = 1d;
            int strlen = pHeightStr.length();

            switch (pHeightStr.replaceAll("[^a-z]", "")) {
            case "km": mult = 1000d;     strlen--; strlen--; break;
            case "dm": mult = .1d;       strlen--; strlen--; break;
            case "cm": mult = .01d;      strlen--; strlen--; break;
            case "mm": mult = .001d;     strlen--; strlen--; break;
            case "m":                              strlen--; break;
            case "feet":                 strlen--; strlen--;
            case "ft": mult = 0.3048d;   strlen--; strlen--; break;
            case "inch":                 strlen--; strlen--;
            case "in": mult = 1/0.0254d; strlen--; strlen--; break;
            }

            return mult * Double.valueOf(pHeightStr.substring(0, strlen));

        } catch (Exception e) {
            log.info("Unsupported height: " + pHeightStr);
        }
        return pDefault;

    }


    /** Get double value of attribute or return default value.
     * @param pOsmPrimitive object to take numerical value
     * @param pAttrName name of attribute
     * @param pDefaultValue default value of attribute
     * @return double value of attribute
     */
    public static Double getNumberAttribute(OsmPrimitive pOsmPrimitive, String pAttrName, Double pDefaultValue) {

        Double he = pDefaultValue;
        if (pOsmPrimitive.get(pAttrName) != null) {
            try {
                he = new Double(pOsmPrimitive.get(pAttrName));
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
        }
        return he;
    }

    public static Integer parseInteger(String pStr, Integer pDefault) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return pDefault;
        }

        try {
            return Integer.parseInt(pStr);
        } catch (Exception e) {
            //
        }
        return pDefault;
    }

    public static Double getObjHeight(OsmPrimitive pOsmPrimitive, Double pDefaultHeight) {
        return parseHeight(pOsmPrimitive.get("obj_height"), pDefaultHeight);
    }

}
