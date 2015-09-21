package kendzi.josm.kendzi3d.jogl.model.building.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.Measurement;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementKey;
import kendzi.kendzi3d.buildings.builder.roof.shape.measurement.MeasurementParserUtil;
import kendzi.kendzi3d.buildings.model.roof.RoofOrientation;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRow;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerType;
import kendzi.kendzi3d.buildings.model.roof.shape.RoofTypeAliasEnum;
import kendzi.util.StringUtil;

/**
 * Utility for parsing roof parameters.
 */
public class RoofParserUtil {

    public static RoofTypeAliasEnum parseRoofShape(String name) {

        if (StringUtil.isBlankOrNull(name)) {
            return null;
        }

        name = name.trim().toLowerCase();
        name = name.replaceAll("-", " ");
        name = name.replaceAll("_", " ");

        for (RoofTypeAliasEnum type : RoofTypeAliasEnum.values()) {
            if (name.startsWith(type.getKey())) {
                return type;
            }
        }
        return null;
    }

    public static Integer parseRoofTypeParameter(RoofTypeAliasEnum roofType, String key) {

        if (roofType == null) {
            return null;
        }

        if (key == null) {
            return null;
        }

        int l = roofType.getKey().length();
        if (key.length() < l + 1) {
            return null;
        }

        key = key.substring(l + 1);
        try {
            return Integer.parseInt(key);
        } catch (Exception e) {
            //
        }

        return null;
    }

    public static List<List<DormerType>> parseMultipleDormers(String key) {

        List<List<DormerType>> ret = new ArrayList<List<DormerType>>();

        if (key == null) {
            return ret;
        }

        String[] split = key.split("\\.");

        for (String sideKeys : split) {
            List<DormerType> site = dormers(sideKeys);
            ret.add(site);
        }
        return ret;

    }

    /**
     * @param sideKeys
     * @return
     */
    public static List<DormerType> dormers(String sideKeys) {
        List<DormerType> site = new ArrayList<DormerType>();

        for (int i = 0; i < sideKeys.length(); i++) {
            String dormerKey = sideKeys.substring(i, i + 1);

            site.add(parseDormer(dormerKey));

        }
        return site;
    }

    public static Map<DormerRow, List<DormerType>> parseSiteDormers(String site, Map<String, String> keys) {

        Map<DormerRow, List<DormerType>> ret = new HashMap<DormerRow, List<DormerType>>();

        for (DormerRow dr : DormerRow.values()) {

            String dormerValues = null;

            dormerValues = keys.get("3dr:dormers:" + site + ":" + dr.getKey());

            if (dormerValues == null && DormerRow.ROW_1.equals(dr)) {
                dormerValues = keys.get("3dr:dormers:" + site);
            }

            if (dormerValues != null) {
                ret.put(dr, dormers(dormerValues));
            }
        }
        return ret;
    }

    /**
     * Take measurements from way.
     * 
     * @param keys
     *            key
     * @return measurements
     */
    public static Map<MeasurementKey, Measurement> parseMeasurements(Map<String, String> keys) {

        Map<MeasurementKey, Measurement> ret = new HashMap<MeasurementKey, Measurement>();

        for (MeasurementKey key : MeasurementKey.values()) {

            String value = keys.get(key.getKey());
            Measurement measurement = MeasurementParserUtil.parse(value);

            ret.put(key, measurement);
        }

        return ret;
    }

    private static DormerType parseDormer(String dormerKey) {
        if (dormerKey == null) {
            return DormerType.UKNOWN;
        }
        for (DormerType dt : DormerType.values()) {
            if (dormerKey.toLowerCase().equals(dt.getKey())) {
                return dt;
            }
        }
        return DormerType.UKNOWN;
    }

    public static RoofOrientation parseOrientation(Map<String, String> keys) {
        try {

            String key = keys.get("roof:orientation");

            if (StringUtil.isBlankOrNull(key)) {
                key = keys.get("building:roof:orientation");
            }

            if (StringUtil.isBlankOrNull(key)) {
                return null;
            }

            return RoofOrientation.valueOf(key);

        } catch (java.lang.IllegalArgumentException e) {
            //
        }
        return null;
    }

}
