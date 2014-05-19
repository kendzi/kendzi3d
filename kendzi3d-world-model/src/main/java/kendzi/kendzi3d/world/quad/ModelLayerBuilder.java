package kendzi.kendzi3d.world.quad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class ModelLayerBuilder {

    public static List<? extends WorldObject> bulid(List<Layer> layerMatchers, DataSet dataSet, Perspective perspective) {

        if (dataSet == null) {
            return Collections.emptyList();
        }

        List<WorldObject> ret = new ArrayList<WorldObject>();

        for (Node node : dataSet.getNodes()) {

            if (node.isDeleted()) {
                continue;
            }

            // layers
            for (Layer layer : layerMatchers) {
                if (layer.getNodeMatcher() != null && layer.getNodeMatcher().match(node)) {
                    WorldObject worldObject = layer.buildModel(node, perspective);
                    if (worldObject != null) {
                        ret.add(worldObject);
                    }
                }
            }
        }

        for (Way way : dataSet.getWays()) {

            if (way.isDeleted()) {
                continue;
            }

            // layers
            for (Layer layer : layerMatchers) {
                if (layer.getWayMatcher() != null && layer.getWayMatcher().match(way)) {
                    WorldObject worldObject = layer.buildModel(way, perspective);
                    if (worldObject != null) {
                        ret.add(worldObject);
                    }
                }
            }
        }

        for (org.openstreetmap.josm.data.osm.Relation relation : dataSet.getRelations()) {

            if (relation.isDeleted()) {
                continue;
            }

            // layers
            for (Layer layer : layerMatchers) {
                if (layer.getRelationMatcher() != null && layer.getRelationMatcher().match(relation)) {
                    WorldObject worldObject = layer.buildModel(relation, perspective);
                    if (worldObject != null) {
                        ret.add(worldObject);
                    }
                }
            }
        }
        return ret;
    }

}
