package kendzi.josm.kendzi3d.jogl.model.roof.mk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementParserUtil;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRow;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerType;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofType;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;
import kendzi.josm.kendzi3d.util.BuildingRoofOrientation;
import kendzi.josm.kendzi3d.util.StringUtil;

public class Parser {


    public static RoofTypeAliasEnum parseRoofShape(String pName) {

        if (pName == null) {
            return null;
        }

        String name = pName.trim().toLowerCase();
        name = name.replaceAll("-", " ");
        name = name.replaceAll("_", " ");


        for (RoofTypeAliasEnum type : RoofTypeAliasEnum.values()) {
            if (type.getKey().equals(name)) {
                return type;
            }
        }
        return null;

    }

    public static RoofType parseRoofType(String pKey) {
        // FIXME it should return roof shape enum not roof builder class!

        if (pKey == null) {
            return null;
        }
        for (RoofType rt : DormerRoofBuilder.roofTypes) {
            if (pKey.startsWith(rt.getPrefixKey().getKey())) {
                return rt;
            }
        }
        return null;
    }

    public static Integer parseRoofTypeParameter(RoofType pRoofType, String pKey) {
        // FIXME it should return roof shape enum not roof builder class!

        if (pRoofType == null) {
            return null;
        }

        if (pKey == null) {
            return null;
        }

        int l = pRoofType.getPrefixKey().getKey().length();
        if (pKey.length() < l + 1) {
            return null;
        }

        String key = pKey.substring(l + 1);
        try {
            return Integer.parseInt(key);
        } catch (Exception e) {
            //
        }

        return null;
    }

    public static List<List<DormerType>> parseMultipleDormers(String pKey) {

        List<List<DormerType>> ret = new ArrayList<List<DormerType>>();

        if (pKey == null) {
            return ret;
        }

        String[] split = pKey.split("\\.");

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
            String dormerKey = sideKeys.substring(i, i+1);

            site.add(parseDormer(dormerKey));

        }
        return site;
    }

    public static Map<DormerRow, List<DormerType>> parseSiteDormers(String pSite,  Map<String, String> pKeys) {

        Map<DormerRow, List<DormerType>> ret = new HashMap<DormerRow, List<DormerType>>();

        for (DormerRow dr : DormerRow.values()) {

            String dormerValues = null;

            dormerValues = pKeys.get("3dr:dormers:" + pSite + ":" + dr.getKey());

            if (dormerValues == null && DormerRow.ROW_1.equals(dr)) {
                dormerValues = pKeys.get("3dr:dormers:" + pSite);
            }

            if (dormerValues != null) {
                List<DormerType> site = dormers(dormerValues);

                ret.put(dr, site);
            }
        }
        return ret;
  }


    /**
     * Take measurements from way.
     * @param pKeys key
     * @return measurements
     */
    public static Map<MeasurementKey, Measurement> parseMeasurements(Map<String, String> pKeys) {

        Map<MeasurementKey, Measurement> ret = new HashMap<MeasurementKey, Measurement>();

        for (MeasurementKey key : MeasurementKey.values()) {

            String value = pKeys.get(key.getKey());
            Measurement measurement = MeasurementParserUtil.parse(value);

            ret.put(key, measurement);
        }

        return ret;
    }
    /*prefix parser
      private static List<char[]> getRoofExtensions(String pKey, String prefixKey, Integer prefixParameter) {

        if (pKey == null) {
            return null;
        }

        int start = prefixKey == null ? 0 : prefixKey.length() + 1;

        if (prefixParameter != null) {
            if (prefixParameter < 10) {
                start = start + 2;
            } else {
                start = start + 3;
            }
        }

        if (pKey.length() < start) {
            return null;
        }
     */



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

    private static Integer getPrefixParameter(String pKey) {
        // TODO Auto-generated method stub
        return null;

    }

    public static BuildingRoofOrientation parseOrientation(Map<String, String> pKeys) {
        try {

            String key = pKeys.get("roof:orientation");

            if (StringUtil.isBlankOrNull(key)) {
                key = pKeys.get("building:roof:orientation");
            }

            if (StringUtil.isBlankOrNull(key)) {
                return null;
            }

            return BuildingRoofOrientation.valueOf(key);

        } catch (java.lang.IllegalArgumentException e) {
            //
        }
        return null;
    }



}
