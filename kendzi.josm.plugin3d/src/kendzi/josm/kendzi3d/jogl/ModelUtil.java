/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Util for jogl model.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public final class ModelUtil {

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
        //FIXME to suport all ways to describe height eg. [fit]
        Double he = pDefaultHeight;
        if (pOsmPrimitive.get("height") != null) {
            try {
                he = new Double(pOsmPrimitive.get("height"));
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
        }
        return he;
    }
    /** Get min_height of object. Return it in meters [m] or return default value.
     * @param pWay object to take height
     * @param pDefaultHeight default value of min_height
     * @return min_height of object
     */
    public static Double getMinHeight(Way pWay, Double pDefaultHeight) {
        //FIXME to suport all ways to describe height eg. [fit]
        Double he = pDefaultHeight;
        if (pWay.get("min_height") != null) {
            try {
                he = new Double(pWay.get("min_height"));
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
        }
        return he;
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
