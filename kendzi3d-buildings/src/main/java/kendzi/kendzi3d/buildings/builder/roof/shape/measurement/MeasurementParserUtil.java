/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.measurement;

import org.apache.log4j.Logger;

/**
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class MeasurementParserUtil {


    /** Log. */
    private static final Logger log = Logger.getLogger(MeasurementParserUtil.class);

    public static Measurement parse(String pValue) {

        if (pValue == null || "".equals(pValue)) {
            return null;
        }

        String valueStr = pValue;

        MeasurementUnit unit = MeasurementUnit.UNKNOWN;
        for (MeasurementUnit mu : MeasurementUnit.values()) {
            if (pValue.endsWith(mu.getKey())) {
                unit = mu;
                valueStr = valueStr.substring(0, valueStr.length() - mu.getKey().length());

                break;
            }
        }

        Double value = null;
        try {
            value = Double.parseDouble(valueStr);
        } catch (Exception e) {
            log.warn("warn parsing value to double: " + pValue + ", " + value, e);
        }

        if (value == null) {
            return null;
        }

        return new Measurement(value, unit);

    }

    public static String getErrorMessage(MeasurementKey pMeasurementKey, Measurement pMeasurement) {
        String utilStr = null;
        if (pMeasurement != null) {
            utilStr = "" + pMeasurement.getUnit();
        }

        return "util: " + utilStr + " for key: " + pMeasurementKey;
    }


}
