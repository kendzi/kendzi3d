/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.layer;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.search.SearchCompiler;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.search.SearchParseError;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

import com.google.inject.Inject;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.trees.Forest;
import kendzi.josm.kendzi3d.jogl.model.trees.Tree;
import kendzi.josm.kendzi3d.jogl.model.trees.TreeRow;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

/**
 * Layer for trees.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class TreeLayer implements Layer {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TreeLayer.class);

    /**
     * Model renderer.
     */
    @Inject
    private ModelRender modelRender;

    /**
     * model cache service.
     */
    @Inject
    private ModelCacheService modelCacheService;

    /**
     * Metadata cache service.
     */
    @Inject
    private MetadataCacheService metadataCacheService;

    private Match treesMatcher;
    private Match treesWayMatcher;

    {
        try {
            treesMatcher = SearchCompiler.compile("(natural=tree)");
        } catch (SearchParseError e) {
            treesMatcher = new SearchCompiler.Never();
            e.printStackTrace();
        }
        try {
            treesWayMatcher = SearchCompiler.compile("(natural=tree_row | natural=wood | landuse=forest)");
        } catch (SearchParseError e) {
            treesWayMatcher = new SearchCompiler.Never();
            e.printStackTrace();
        }

    }

    @Override
    public Match getNodeMatcher() {
        return treesMatcher;
    }

    @Override
    public Match getWayMatcher() {
        return treesWayMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public WorldObject buildModel(Node pNode, Perspective perspective) {
        return new Tree(pNode, perspective, modelRender, metadataCacheService, modelCacheService);

    }

    @Override
    public WorldObject buildModel(Way pWay, Perspective perspective) {
        if ("tree_row".equals(pWay.get("natural"))) {
            return new TreeRow(pWay, perspective, modelRender, modelCacheService, metadataCacheService);
        }
        return new Forest(pWay, perspective, modelRender, modelCacheService, metadataCacheService);

    }

    @Override
    public WorldObject buildModel(Relation pRelation, Perspective perspective) {
        return null;
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return modelRender;
    }

    /**
     * @param modelRender
     *            the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

}
