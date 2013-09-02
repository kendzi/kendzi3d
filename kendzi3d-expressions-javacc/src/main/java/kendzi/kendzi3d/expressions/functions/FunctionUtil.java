package kendzi.kendzi3d.expressions.functions;

import kendzi.kendzi3d.expressions.exeption.FunctionExeption;

public class FunctionUtil {

    public static void validateNumOfReqiredParams(int i, double[] e) {
        int params = 0;
        if (e != null) {
            params = e.length;
        }

        if (e == null || params != i) {
            throw new FunctionExeption(String.format("function require %s parameter, but recived %s", i, params));
        }
    }


    public static double getReqiredParam(int i, double[] e) {
        int params = 0;
        if (e != null) {
            params = e.length;
        }

        if (i >= params) {
            throw new FunctionExeption(String.format("can't get function required parameter %s , function recive only %s parameters", i, params));
        }
        return e[i];
    }
}
