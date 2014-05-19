package kendzi.kendzi3d.world.quad.layer;

import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Interface for model builders for node, way and relation.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public interface ModelBuilder {

    /**
     * Build 3d model for node.
     * 
     * @param node
     *            node representing 3d model
     * @param pPerspective3D
     *            perspective
     */
    WorldObject buildModel(Node node, Perspective perspective);

    /**
     * Build 3d model for way.
     * 
     * @param way
     *            way representing 3d model
     * @param pPerspective3D
     *            perspective
     */
    WorldObject buildModel(Way way, Perspective perspective);

    /**
     * Build 3d model for relation.
     * 
     * @param relation
     *            relation representing 3d model
     * @param pPerspective3D
     */
    WorldObject buildModel(Relation relation, Perspective perspective);

}