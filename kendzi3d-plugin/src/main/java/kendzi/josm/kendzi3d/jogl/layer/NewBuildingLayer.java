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
import kendzi.josm.kendzi3d.jogl.model.building.Building;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

/**
 * Layer for buildings.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class NewBuildingLayer implements Layer {

    /** Log. */
    private static final Logger log = Logger.getLogger(NewBuildingLayer.class);

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

    private Match buildingNodeMatcher;
    private Match buildingMatcher;
    private Match buildingRelationMatcher;

    {
        try {
            buildingNodeMatcher = SearchCompiler.compile(
                    "building\\:shape=* & ((building=* & -building=no & -building\\:parts=*) | (building\\:part=* & -building\\:part=no)) & -child type=building & (-child (type=multipolygon & (building=* |  building\\:part=*)))");

        } catch (SearchParseError e) {
            buildingNodeMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }
        try {
            buildingMatcher = SearchCompiler.compile(
                    "((building=* & -building=no & -building\\:parts=*) | (building\\:part=* & -building\\:part=no)) & -child type=building & (-child (type=multipolygon & (building=* |  building\\:part=*)))");

        } catch (SearchParseError e) {
            buildingMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

        try {
            buildingRelationMatcher = SearchCompiler.compile(
                    "(type=multipolygon  & ((building=* & -building=no & -building\\:parts=*) | (building\\:part=* & -building\\:part=no))  & -child type=building) | type=building");

        } catch (SearchParseError e) {
            buildingMatcher = new SearchCompiler.Never();
            log.error(e, e);
        }

    }

    @Override
    public Match getNodeMatcher() {
        return buildingNodeMatcher;
    }

    @Override
    public Match getWayMatcher() {
        return buildingMatcher;
    }

    @Override
    public Match getRelationMatcher() {
        return buildingRelationMatcher;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public WorldObject buildModel(Node node, Perspective perspective) {
        return new Building(node, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Way way, Perspective perspective) {
        return new Building(way, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
    }

    @Override
    public WorldObject buildModel(Relation relation, Perspective perspective) {
        return new Building(relation, perspective, modelRender, metadataCacheService, textureLibraryStorageService);
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
