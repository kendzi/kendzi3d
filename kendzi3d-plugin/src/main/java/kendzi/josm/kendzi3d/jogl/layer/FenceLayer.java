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
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.BarrierFence;
import kendzi.josm.kendzi3d.jogl.model.BarrierFenceRelation;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

/**
 * Layer for fence.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class FenceLayer implements Layer {

    /** Log. */
    private static final Logger log = Logger.getLogger(FenceLayer.class);

    /**
     * Model renderer.
     */
    @Inject
    private ModelRender modelRender;

    /**
     * Metadata cache service.
     */
    @Inject
    private MetadataCacheService metadataCacheService;

    /**
     * Texture library service.
     */
    @Inject
    private TextureLibraryStorageService textureLibraryStorageService;

    private Match fenceMatcher;

    private Match fenceRelationMatcher;

    {
        try {
            fenceMatcher = SearchCompiler.compile("(barrier=fence) | (barrier\\:part=fence)");
        } catch (SearchParseError e) {
            fenceMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            fenceRelationMatcher = SearchCompiler.compile("((type=way\\:3d) & (barrier=fence))");
        } catch (SearchParseError e) {
            fenceRelationMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

    }

    @Override
    public Match getNodeMatcher() {
        return null;
    }

    @Override
    public Match getWayMatcher() {
        return fenceMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return fenceRelationMatcher;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public WorldObject buildModel(Node node, Perspective perspective) {
        return null;
    }

    @Override
    public WorldObject buildModel(Way way, Perspective perspective) {
        return new BarrierFence(way, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Relation relation, Perspective perspective) {
        return new BarrierFenceRelation(relation, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
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
