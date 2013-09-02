package kendzi.kendzi3d.expressions.expression;

public class Converter {
    public static Double toDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
            //        } else if (value instanceof double) {
            //            return new Double(value);
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (Exception e) {
                throw new RuntimeException("expecting double in string vaule but get: " + value, e);
            }
        }
        throw new RuntimeException("unkonown converter to double for value: " + value);
    }

}
