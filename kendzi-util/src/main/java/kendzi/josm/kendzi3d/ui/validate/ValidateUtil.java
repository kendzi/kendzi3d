package kendzi.josm.kendzi3d.ui.validate;

import java.awt.Color;

import javax.swing.JTextField;

import kendzi.util.StringUtil;

public class ValidateUtil {
    public static boolean validateTextString(JTextField pJTextField) {

        boolean valid = !StringUtil.isBlankOrNull(pJTextField.getText());
        setComponentError(pJTextField, !valid);
        return valid;

    }

    public static boolean validateTextEmptyDouble(JTextField pJTextField) {
        boolean valid = isEmptyDouble(pJTextField.getText());
        setComponentError(pJTextField, !valid);
        return valid;
    }

    public static boolean validateTextEmptyInteger(JTextField pJTextField) {
        boolean valid = isEmptyInteger(pJTextField.getText());
        setComponentError(pJTextField, !valid);
        return valid;
    }

    public static void setComponentError(JTextField pJTextField, boolean b) {
        if (b) {
            pJTextField.setBackground(Color.red.brighter());
        } else {
            pJTextField.setBackground(null);
        }
    }

    public static Integer parseInteger(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return null;
        }
        try {
            return Integer.parseInt(pStr);
        } catch (Exception e) {
            //
        }
        return null;

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

    public static boolean isEmptyDouble(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return true;
        }
        return isDouble(pStr);
    }

    public static boolean isEmptyInteger(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return true;
        }
        return isInteger(pStr);
    }

    public static boolean isDouble(String pStr) {
        return parseDouble(pStr) != null;
    }

    public static boolean isInteger(String pStr) {
        return parseInteger(pStr) != null;
    }


}
