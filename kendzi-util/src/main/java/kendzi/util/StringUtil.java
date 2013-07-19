/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.util;

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

    public static boolean equalsOrNulls(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 == null || s2 == null) {
            return false;
        } else {
            return s1.equals(s2);
        }
    }
}
