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
import kendzi.josm.kendzi3d.jogl.model.Water;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

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
            waterMatcher = SearchCompiler.compile("(natural=water) | (landuse=reservoir)| (waterway=riverbank)");
        } catch (SearchParseError e) {
            waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            waterMatcher = SearchCompiler
                    .compile("((natural=water) | (landuse=reservoir)| (waterway=riverbank))  -child type=multipolygon");
        } catch (SearchParseError e) {
            waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            waterRelationMatcher = SearchCompiler
                    .compile("type=multipolygon && ((natural=water) | (landuse=reservoir)| (waterway=riverbank))");
        } catch (SearchParseError e) {
            waterMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

    }

    @Override
    public Match getNodeMatcher() {
        return null;
    }

    @Override
    public Match getWayMatcher() {
        return waterMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return waterRelationMatcher;
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
        return new Water(way, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Relation relation, Perspective perspective) {
        return new Water(relation, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
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
