package kendzi.kendzi3d.render.dataset;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import kendzi.josm.datasource.Bbox;
import kendzi.josm.datasource.PgSqlReader;
import kendzi.kendzi3d.render.tile.LatLonUtil;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;

public class PGSQLDataProvider implements DataSetProvider {

    Connection connection = null;

    DataSource dataSource;


    public PGSQLDataProvider(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    @Override
    public DataSet findData(LatLon leftTop, LatLon rightBottom) throws SQLException {

        double boxY = 200d;
        double boxX = 50d;

        Bbox bbox = new Bbox(leftTop.lon(), leftTop.lat());
        bbox.addPoint(rightBottom.lon(), rightBottom.lat());

        //        Bbox bbox = new Bbox(leftTop.lon(), rightBottom.lon(), leftTop.lat(), rightBottom.lat());
        //        ""
        double deltaLat = Math.toDegrees(LatLonUtil.deltaLat(boxY));
        double deltaLot = Math.toDegrees(LatLonUtil.deltaLon(bbox.getLat_max(), boxX));
        //        LatLon max = new LatLon(bbox.getLat_max(), bbox.getLon_max());
        //        LatLon min = new LatLon(bbox.getLat_min(), bbox.getLon_min());
        //        LatLon maxD = new LatLon(bbox.getLat_max() + deltaLat, bbox.getLon_max() + deltaLot);
        //        LatLon minD = new LatLon(bbox.getLat_min() - deltaLat, bbox.getLon_min() - deltaLot);
        //        System.out.println(max.greatCircleDistance(maxD));
        //        System.out.println(min.greatCircleDistance(minD));
        //
        //        System.out.println(min.greatCircleDistance(max));
        //        System.out.println(maxD.greatCircleDistance(minD));


        bbox.setLat_max(bbox.getLat_max() + deltaLat);
        bbox.setLat_min(bbox.getLat_min() - deltaLat);
        bbox.setLon_max(bbox.getLon_max() + deltaLot);
        bbox.setLon_min(bbox.getLon_min() - deltaLot);

        try {

            return PgSqlReader.parseDataSet(getConnection(), bbox, NullProgressMonitor.INSTANCE);

        } catch (IllegalDataException e) {
            throw new RuntimeException("error loading data.", e);
        }

    }

    private Connection getConnection() throws SQLException {
        if (this.connection == null) { // || !this.connection.isValid(100)) {
            if (this.connection != null ) {
                try {
                    this.connection.close();
                } catch (Exception e) {
                    //
                }
            }

            this.connection = this.dataSource.getConnection();

        }
        return this.connection;
    }

}
