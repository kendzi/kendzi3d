package kendzi.kendzi3d.render.dataset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;

public class FileDataProvider implements DataSetProvider {

    String fileUrl;

    DataSet dataSet;

    public FileDataProvider(String fileUrl) {
        super();
        this.fileUrl = fileUrl;
        this.dataSet = null;
    }


    private DataSet loadDataSet(String file) throws IllegalDataException, FileNotFoundException {

        FileInputStream in = new FileInputStream(file);

        return OsmReader.parseDataSet(in, NullProgressMonitor.INSTANCE);
    }

    @Override
    public DataSet findData(LatLon leftTop, LatLon rightBottom)
            throws Exception {
        if (this.dataSet == null) {
            this.dataSet = loadDataSet(this.fileUrl);
        }

        return this.dataSet;
    }
}
