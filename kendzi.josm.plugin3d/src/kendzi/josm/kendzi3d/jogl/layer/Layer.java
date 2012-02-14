package kendzi.josm.kendzi3d.jogl.layer;

import java.util.List;

import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Layers group 3d object by type.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public interface Layer {

    /**
     * Matcher for nodes represented by layer.
     * @return matcher for nodes
     */
    Match getNodeMatcher();

    /**
     * Matcher for ways represented by layer.
     * @return matcher for ways
     */
    Match getWayMatcher();

    /**
     * Matcher for relations represented by layer.
     * @return matcher for relations
     */
    Match getRelationMatcher();

    /**
     * If layer is visible.
     * @return visible
     */
    boolean isVisible();

    /**
     * All 3d object represented by layer.
     * @return list of 3d object
     */
    List<Model> getModels();

    /**
     * Build 3d model for node.
     * @param node node representing 3d model
     * @param pPerspective3D perspective
     */
    void addModel(Node node, Perspective3D pPerspective3D);

    /**
     * Build 3d model for way.
     * @param way way representing 3d model
     * @param pPerspective3D perspective
     */
    void addModel(Way way, Perspective3D pPerspective3D);

    /**
     * Build 3d model for relation.
     * @param relation relation representing 3d model
     * @param pPerspective3D
     */
    void addModel(Relation relation, Perspective3D pPerspective3D);

    /**
     * When clear event is call.
     */
    void clear();
}
