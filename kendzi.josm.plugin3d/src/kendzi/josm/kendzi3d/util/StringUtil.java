package kendzi.josm.kendzi3d.util;

public class StringUtil {
    public static boolean isBlankOrNull(String pString) {
        if (pString == null) {
            return true;
        }

        if (!"".equals(pString.trim())) {
            return false;
        }

        return true;
    }

    public static String blankOnNull(String pStr) {
        if (pStr == null) {
            return "";
        }
        return pStr;
    }

    public static Double parseDouble(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return null;
        }
        try {
            return Double.parseDouble(pStr);
        } catch (Exception e) {
            //
        }
        return null;
    }
}
