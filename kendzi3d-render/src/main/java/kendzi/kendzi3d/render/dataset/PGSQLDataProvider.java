package kendzi.kendzi3d.render.dataset;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import kendzi.josm.datasource.PgSqlReader;
import kendzi.kendzi3d.render.tile.LatLonUtil;
import kendzi.math.geometry.bbox.Bbox2d;

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

        Bbox2d bbox = new Bbox2d();
        bbox.addPoint(leftTop.lon(), leftTop.lat());
        bbox.addPoint(rightBottom.lon(), rightBottom.lat());

        double deltaLat = LatLonUtil.deltaLat(boxY);
        double deltaLon = LatLonUtil.deltaLon(bbox.getyMax(), boxX);

        bbox.addPoint(bbox.getxMax() + deltaLon, bbox.getyMax() + deltaLat);
        bbox.addPoint(bbox.getyMin() - deltaLat, bbox.getxMin() - deltaLon);

        try {

            return PgSqlReader.parseDataSet(getConnection(), bbox, NullProgressMonitor.INSTANCE);

        } catch (IllegalDataException e) {
            throw new RuntimeException("error loading data.", e);
        }

    }

    private Connection getConnection() throws SQLException {
        if (this.connection == null) { // || !this.connection.isValid(100)) {
            if (this.connection != null) {
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
