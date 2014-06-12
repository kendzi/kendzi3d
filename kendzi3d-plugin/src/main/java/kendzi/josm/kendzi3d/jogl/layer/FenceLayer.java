/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.layer;

import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.BarrierFence;
import kendzi.josm.kendzi3d.jogl.model.BarrierFenceRelation;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.actions.search.SearchCompiler.ParseError;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

import com.google.inject.Inject;

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
            this.fenceMatcher = SearchCompiler.compile("(barrier=fence) | (barrier\\:part=fence)", false, false);
        } catch (ParseError e) {
            this.fenceMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            this.fenceRelationMatcher = SearchCompiler.compile("((type=way\\:3d) & (barrier=fence))", false, false);
        } catch (ParseError e) {
            this.fenceRelationMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

    }

    @Override
    public Match getNodeMatcher() {
        return null;
    }

    @Override
    public Match getWayMatcher() {
        return this.fenceMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return this.fenceRelationMatcher;
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
        return new BarrierFence(way, perspective, this.modelRender, this.metadataCacheService, this.textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Relation relation, Perspective perspective) {
        return new BarrierFenceRelation(relation, perspective, this.modelRender, this.metadataCacheService,
                this.textureLibraryStorageService);
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return this.modelRender;
    }

    /**
     * @param modelRender
     *            the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

}
