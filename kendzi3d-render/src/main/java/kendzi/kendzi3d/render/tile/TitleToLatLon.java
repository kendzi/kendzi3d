package kendzi.kendzi3d.render.tile;

public class TitleToLatLon {
    public static void main(String[] args) {
        int zoom = 10;
        double lat = 47.968056d;
        double lon = 7.909167d;
        System.out.println("http://tile.openstreetmap.org/"
                + getTileNumber(lat, lon, zoom) + ".png");
    }

    public static String getTileNumber(final double lat, final double lon,
            final int zoom) {
        //        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        //        int ytile = (int) Math
        //                .floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
        //                        / Math.cos(Math.toRadians(lat)))
        //                        / Math.PI)
        //                        / 2 * (1 << zoom));

        int xtile = lonToTile(lon, zoom);
        int ytile = latToTile(lat, zoom);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

    public static int lonToTile(final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));

        return xtile;
    }
    public static int latToTile(final double lat, final int zoom) {
        int ytile = (int) Math
                .floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
                        / Math.cos(Math.toRadians(lat)))
                        / Math.PI)
                        / 2 * (1 << zoom));
        return ytile;
    }

    /**
     * @see kendzi.josm.datasource.Bbox
     * @author Tomasz Kêdziora (Kendzi)
     */
    public static class BoundingBox {
        public double north;
        public double south;
        public double east;
        public double west;
    }

    public static BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
        BoundingBox bb = new BoundingBox();
        bb.north = tile2lat(y, zoom);
        bb.south = tile2lat(y + 1, zoom);
        bb.west = tile2lon(x, zoom);
        bb.east = tile2lon(x + 1, zoom);
        return bb;
    }

    static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
}
