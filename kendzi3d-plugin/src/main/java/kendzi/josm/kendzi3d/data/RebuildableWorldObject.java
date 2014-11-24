package kendzi.josm.kendzi3d.data;

import kendzi.kendzi3d.josm.model.perspective.Perspective;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Allows to reuse existing world object when related osm primitive or
 * perspective was changed.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public interface RebuildableWorldObject {

    /**
     * Rebuild world object from changed initial data.
     *
     * @param primitive
     *            osm primitive
     * @param perspective
     *            perspective
     */
    void rebuildWorldObject(OsmPrimitive primitive, Perspective perspective);
}
