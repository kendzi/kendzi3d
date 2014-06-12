/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.layer;

import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.model.Water;
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
 * Layer for water.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class WaterLayer implements Layer {

    /** Log. */
    private static final Logger log = Logger.getLogger(WaterLayer.class);

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

    private Match waterMatcher;

    private Match waterRelationMatcher;

    {
        try {
            this.waterMatcher = SearchCompiler.compile("(natural=water) | (landuse=reservoir)| (waterway=riverbank)", false,
                    false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            this.waterMatcher = SearchCompiler.compile(
                    "((natural=water) | (landuse=reservoir)| (waterway=riverbank))  -child type=multipolygon", false, false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            this.waterRelationMatcher = SearchCompiler.compile(
                    "type=multipolygon && ((natural=water) | (landuse=reservoir)| (waterway=riverbank))", false, false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

    }

    @Override
    public Match getNodeMatcher() {
        return null;
    }

    @Override
    public Match getWayMatcher() {
        return this.waterMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return this.waterRelationMatcher;
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
        return new Water(way, perspective, this.modelRender, this.metadataCacheService, this.textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Relation relation, Perspective perspective) {
        return new Water(relation, perspective, this.modelRender, this.metadataCacheService, this.textureLibraryStorageService);
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
