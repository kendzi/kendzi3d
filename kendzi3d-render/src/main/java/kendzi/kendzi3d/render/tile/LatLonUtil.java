package kendzi.kendzi3d.render.tile;


public class LatLonUtil {

    /**
     * Radius of earth.
     */
    private static double R = 6371000d;

    /** Move point a for distance dx.
     * @param aLat
     * @param dx
     * @return difference in lon
     * 
     * @see "http://stackoverflow.com/questions/5632406/how-to-find-latitude-and-longitude"
     */
    public static double deltaLon(double aLat, double dx) {
        double dlon = dx / (R * Math.cos (Math.toRadians(aLat)));
        return dlon;
    }


    /** Move point a for distance dy.
     * @param dy
     * @return difference in lat
     * @see "http://stackoverflow.com/questions/5632406/how-to-find-latitude-and-longitude"
     */
    public static double deltaLat(double dy) {
        double dlat = dy / R;
        return dlat;
    }
}
