package kendzi.josm.datasource;

public class Bbox {

    /**
     * X_min
     */
    private double lonMin;
    /**
     * X_max
     */
    private double lonMax;

    /**
     * Y_min
     */
    private double latMin;
    /**
     * Y_max
     */
    private double latMax;
    /**
     * @return the lon_min
     */
    public double getLon_min() {
        return this.lonMin;
    }




    /**
     */
    public Bbox(double lon, double lat) {
        this.lonMin = lon;
        this.lonMax = lon;
        this.latMin = lat;
        this.latMax = lat;
    }
    //
    //    /**
    //     * @param lon1 x1
    //     * @param lon2 x2
    //     * @param lat1 y1
    //     * @param lat2 y2
    //     */
    //    public Bbox(double lon1, double lon2, double lat1, double lat2) {
    //        this.lonMin = Math.min(lon1, lon2);
    //        this.lonMax = Math.max(lon1, lon2);
    //        this.latMin = Math.min(lat1, lat2);
    //        this.latMax = Math.max(lat1, lat2);
    //    }

    public void addPoint(double lon, double lat) {

        this.lonMin = Math.min(this.lonMin, lon);
        this.lonMax = Math.max(this.lonMax, lon);

        this.latMin = Math.min(this.latMin, lat);
        this.latMax = Math.max(this.latMax, lat);
    }



    /**
     * @param lon_min the lon_min to set
     */
    public void setLon_min(double lon_min) {
        this.lonMin = lon_min;
    }
    /**
     * @return the lat_min
     */
    public double getLat_min() {
        return this.latMin;
    }
    /**
     * @param lat_min the lat_min to set
     */
    public void setLat_min(double lat_min) {
        this.latMin = lat_min;
    }
    /**
     * @return the lon_max
     */
    public double getLon_max() {
        return this.lonMax;
    }
    /**
     * @param lon_max the lon_max to set
     */
    public void setLon_max(double lon_max) {
        this.lonMax = lon_max;
    }
    /**
     * @return the lat_max
     */
    public double getLat_max() {
        return this.latMax;
    }
    /**
     * @param lat_max the lat_max to set
     */
    public void setLat_max(double lat_max) {
        this.latMax = lat_max;
    }


}
