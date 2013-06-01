/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.layer;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.Water;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.TextureLibraryService;

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
     * List of layer models.
     */
    private List<Model> modelList = new ArrayList<Model>();

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
    TextureLibraryService textureLibraryService;

    private Match waterMatcher;

    private Match waterRelationMatcher;

    {
        try {
            this.waterMatcher = SearchCompiler.compile("(natural=water) | (landuse=reservoir)| (waterway=riverbank)", false, false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e);
        }
        try {
            this.waterMatcher = SearchCompiler.compile("((natural=water) | (landuse=reservoir)| (waterway=riverbank))  -child type=multipolygon", false, false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e);
        }
        try {
            this.waterRelationMatcher = SearchCompiler.compile("type=multipolygon && ((natural=water) | (landuse=reservoir)| (waterway=riverbank))", false, false);
        } catch (ParseError e) {
            this.waterMatcher = new SearchCompiler.Never();
            log.error(e);
        }

    }

    @Override
    public
    Match getNodeMatcher() {
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
    public List<Model> getModels() {
        return this.modelList;
    }

    @Override
    public void addModel(Node node, Perspective3D pPerspective3D) {
        //
    }

    @Override
    public void addModel(Way way, Perspective3D pPerspective3D) {
        this.modelList.add(new Water(way, pPerspective3D, this.modelRender, this.metadataCacheService, this.textureLibraryService));
    }

    @Override
    public void addModel(Relation relation, Perspective3D pPerspective3D) {
        this.modelList.add(new Water(relation, pPerspective3D, this.modelRender, this.metadataCacheService, this.textureLibraryService));
    }

    @Override
    public void clear() {
        this.modelList.clear();
    }

    /**
     * @return the modelRender
     */
    public ModelRender getModelRender() {
        return this.modelRender;
    }

    /**
     * @param modelRender the modelRender to set
     */
    public void setModelRender(ModelRender modelRender) {
        this.modelRender = modelRender;
    }

}
