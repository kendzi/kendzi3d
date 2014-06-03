package kendzi.geo.util;

/**
 * Utility for lat, lon conversions.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class LatLonUtil {

    /**
     * Radius of earth.
     */
    private static double R = 6371000d;

    /**
     * Calculate delta of lon for move of point for a distance.
     * 
     * @param lat
     *            lat coordinate where displacement take place
     * @param distance
     *            displacement of point in meters
     * @return difference in lon in degrees
     * 
     * @see "http://stackoverflow.com/questions/5632406/how-to-find-latitude-and-longitude"
     */
    public static double deltaLon(double lat, double distance) {
        return Math.toDegrees(distance / (R * Math.cos(Math.toRadians(lat))));
    }

    /**
     * Calculate delta of lat for move of point for a distance.
     * 
     * @param distance
     *            displacement of point in meters
     * @return difference in lat in degrees
     * @see "http://stackoverflow.com/questions/5632406/how-to-find-latitude-and-longitude"
     */
    public static double deltaLat(double distance) {
        return Math.toDegrees(distance / R);
    }
}
