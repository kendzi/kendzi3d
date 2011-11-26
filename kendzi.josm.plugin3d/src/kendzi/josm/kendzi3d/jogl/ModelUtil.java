/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Util for jogl model.
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

    private static Double parseHeight(String pHeightStr, Double pDefault) {

        if (pHeightStr == null) {
            return pDefault;
        }

        String heightStr = pHeightStr.trim().toLowerCase();
        heightStr.replaceAll(",", ".");

        try {
            if (heightStr.endsWith("m")) {
                heightStr = heightStr.substring(0, heightStr.length() - 1);
                return new Double(" " + heightStr + " ");

            } else if (heightStr.endsWith("ft")) {

                // 1 foot = 0.3048 meters
                heightStr = heightStr.substring(0, heightStr.length() - 1);
                return 0.3048d * new Double(" " + heightStr + " ");

            } else if (heightStr.endsWith("feet")) {

                // 1 foot = 0.3048 meters
                heightStr = heightStr.substring(0, heightStr.length() - 4);
                return 0.3048d * new Double(" " + heightStr + " ");
            }

            // default we take in [m]
            return new Double(" " + heightStr + " ");

        } catch (Exception e) {
            log.info("Unsupportet height: " + heightStr);
        }
        return pDefault;

    }


    /** Get double value of attribute or return default value.
     * @param pWay object to take numerical value
     * @param pAttrName name of attribute
     * @param pDefaultValue default value of attribute
     * @return double value of attribute
     */
    public static Double getNumberAttribute(Way pWay, String pAttrName, Double pDefaultValue) {

        Double he = pDefaultValue;
        if (pWay.get(pAttrName) != null) {
            try {
                he = new Double(pWay.get(pAttrName));
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
        }
        return he;
    }

}
