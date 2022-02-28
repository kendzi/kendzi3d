package kendzi.josm.kendzi3d.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.quad.layer.LayerMatcher;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.SubclassFilteredCollection;

public class DataSetFilterUtil {

    public static Set<OsmId> filter(LayerMatcher layerMatcher, DataSet dataSet, Perspective perspective) {

        if (dataSet == null) {
            return Collections.emptySet();
        }

        Set<OsmId> ret = new HashSet<OsmId>(1000);

        Collection<OsmPrimitive> data = dataSet.allNonDeletedCompletePrimitives();

        for (Node node : new SubclassFilteredCollection<OsmPrimitive, Node>(data, Node.class::isInstance)) {

            if (node.isDisabledAndHidden()) {
                continue;
            }

            // layers
            // for (LayerMatcher layer : layerMatchers) {
            if (layerMatcher.getNodeMatcher() != null && layerMatcher.getNodeMatcher().match(node)) {
                ret.add(new OsmId(node.getUniqueId(), OsmPrimitiveType.NODE));
            }
            // }
        }

        for (Way way : new SubclassFilteredCollection<OsmPrimitive, Way>(data, Way.class::isInstance)) {

            if (way.isDisabledAndHidden()) {
                continue;
            }

            // layers
            // for (LayerMatcher layer : layerMatchers) {
            if (layerMatcher.getWayMatcher() != null && layerMatcher.getWayMatcher().match(way)) {
                ret.add(new OsmId(way.getUniqueId(), OsmPrimitiveType.WAY));
            }
            // }
        }

        for (Relation relation : new SubclassFilteredCollection<OsmPrimitive, Relation>(data, Relation.class::isInstance)) {

            if (relation.isDisabledAndHidden()) {
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
