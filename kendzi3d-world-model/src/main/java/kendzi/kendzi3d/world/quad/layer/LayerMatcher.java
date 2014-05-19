package kendzi.kendzi3d.world.quad.layer;

import org.openstreetmap.josm.actions.search.SearchCompiler.Match;

/**
 * Matchers for layers. Groups 3d models by type.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public interface LayerMatcher {

    /**
     * Matcher for nodes represented by layer.
     * 
     * @return matcher for nodes
     */
    Match getNodeMatcher();

    /**
     * Matcher for ways represented by layer.
     * 
     * @return matcher for ways
     */
    Match getWayMatcher();

    /**
     * Matcher for relations represented by layer.
     * 
     * @return matcher for relations
     */
    Match getRelationMatcher();

    /**
     * If layer is visible.
     * 
     * @return visible
     */
    boolean isVisible();

}