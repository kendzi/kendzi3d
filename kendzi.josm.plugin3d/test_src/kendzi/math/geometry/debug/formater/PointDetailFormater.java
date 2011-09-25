package kendzi.math.geometry.debug.formater;

import javax.vecmath.Point2d;

public class PointDetailFormater {

    /**
     *  Format for eclipse detail formater.
     *
     * @param point
     * @return
     */
    public static String format(Point2d point) {
        try {
            kendzi.math.geometry.debug.DebugDisplay.
            	getDebugDisplay().getDebugLayer().addDebug(
            	        new kendzi.math.geometry.debug.DisplayPoints(point));
        } catch (RuntimeException ee) {
            //
        } catch (Exception e) {
        	//
        }
        return (point != null ? point.toString() : "null");
    }
}

