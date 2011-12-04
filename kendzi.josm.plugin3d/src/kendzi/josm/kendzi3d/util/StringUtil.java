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
}
