package kendzi.kendzi3d.render.dataset;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;

public interface DataSetProvider {
    DataSet findData(LatLon leftTop, LatLon rightBottom) throws Exception;

}
