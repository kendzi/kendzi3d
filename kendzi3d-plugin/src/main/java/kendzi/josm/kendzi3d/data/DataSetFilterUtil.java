package kendzi.josm.kendzi3d.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.quad.layer.LayerMatcher;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Way;

public class DataSetFilterUtil {

    public static List<OsmId> filter(LayerMatcher layerMatcher, DataSet dataSet, Perspective perspective) {

        if (dataSet == null) {
            return Collections.emptyList();
        }

        List<OsmId> ret = new ArrayList<OsmId>(1000);

        for (Node node : dataSet.getNodes()) {

            if (node.isDeleted()) {
                continue;
            }

            // layers
            // for (LayerMatcher layer : layerMatchers) {
            if (layerMatcher.getNodeMatcher() != null && layerMatcher.getNodeMatcher().match(node)) {
                ret.add(new OsmId(node.getUniqueId(), OsmPrimitiveType.NODE));
            }
            // }
        }

        for (Way way : dataSet.getWays()) {

            if (way.isDeleted()) {
                continue;
            }

            // layers
            // for (LayerMatcher layer : layerMatchers) {
            if (layerMatcher.getWayMatcher() != null && layerMatcher.getWayMatcher().match(way)) {
                ret.add(new OsmId(way.getUniqueId(), OsmPrimitiveType.WAY));
            }
            // }
        }

        for (org.openstreetmap.josm.data.osm.Relation relation : dataSet.getRelations()) {

            if (relation.isDeleted()) {
                continue;
            }

            // layers
            // for (LayerMatcher layer : layerMatchers) {
            if (layerMatcher.getRelationMatcher() != null && layerMatcher.getRelationMatcher().match(relation)) {
                ret.add(new OsmId(relation.getUniqueId(), OsmPrimitiveType.RELATION));
            }
            // }
        }
        return ret;
    }

}
