/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.layer.models;

import generated.NodeModel;
import generated.WayNodeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.DrawableMultipleWorldObject;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.models.library.service.ModelsLibraryDataChangeEvent;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.search.SearchCompiler;
import org.openstreetmap.josm.actions.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;

import com.google.inject.Inject;

/**
 * Layer allow loading custom models.
 * 
 * @author Tomasz Kędziora (kendzi)
 * 
 */
public class ModelsLibraryLayer implements Layer, ModelsLibraryDataChangeEvent {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelsLibraryLayer.class);

    /**
     * List of model definitions.
     */
    private List<NodeModelConf> nodeModelsList = new ArrayList<NodeModelConf>();

    /**
     * Model renderer.
     */
    private ModelRender modelRender;

    private ModelCacheService modelCacheService;

    private ModelsLibraryService modelsLibraryService;

    private Map<String, List<WayNodeModelConf>> wayNodeModelsMap;

    /**
     * Constructor.
     * 
     * @param modelRender
     * @param modelCacheService
     * @param urlReciverService
     * @param modelsLibraryService
     */
    @Inject
    public ModelsLibraryLayer(ModelRender modelRender, ModelCacheService modelCacheService, ResourceService urlReciverService,
            ModelsLibraryService modelsLibraryService) {
        this.modelRender = modelRender;
        this.modelCacheService = modelCacheService;
        this.modelsLibraryService = modelsLibraryService;

        init();
    }

    /**
     * Initialize layer.
     */
    public void init() {
        modelsLibraryService.removePointModelDataChangeListener(this);
        modelsLibraryService.addPointModelDataChangeListener(this);

        loadData();
    }

    private void loadData() {
        List<NodeModelConf> nodeModelsList = new ArrayList<NodeModelConf>();
        for (String configurationFile : modelsLibraryService.findAllConfigurationFiles()) {
            List<NodeModel> nodeModels = modelsLibraryService.findAllNodeModels(configurationFile);

            for (NodeModel nodeModel : nodeModels) {
                try {
                    NodeModelConf nodeModelConf = ModelsConvertUtil.convert(nodeModel);
                    nodeModelConf.setModel(ModelsConvertUtil.reciveModelPath(nodeModelConf.getModel(), configurationFile));

                    nodeModelsList.add(nodeModelConf);
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }

        this.nodeModelsList = nodeModelsList;

        List<WayNodeModelConf> wayNodeModelsList = new ArrayList<WayNodeModelConf>();

        for (String configurationFile : modelsLibraryService.findAllConfigurationFiles()) {
            List<WayNodeModel> wayNodeModels = modelsLibraryService.findAllWayNodeModels(configurationFile);
            for (WayNodeModel nodeModel : wayNodeModels) {
                try {
                    WayNodeModelConf nodeModelConf = ModelsConvertUtil.convert(nodeModel);
                    nodeModelConf.setModel(ModelsConvertUtil.reciveModelPath(nodeModelConf.getModel(), configurationFile));

                    wayNodeModelsList.add(nodeModelConf);

                } catch (Exception e) {
                    log.error("can't load configuration", e);
                }
            }
        }

        wayNodeModelsMap = new HashMap<String, List<WayNodeModelConf>>();
        for (WayNodeModelConf wayNodeModelConf : wayNodeModelsList) {
            String key = wayNodeModelConf.getMatcher().toString();
            if (!wayNodeModelsMap.containsKey(key)) {
                wayNodeModelsMap.put(key, new ArrayList<WayNodeModelConf>());
            }
            wayNodeModelsMap.get(key).add(wayNodeModelConf);
        }
    }

    @Override
    public Match getNodeMatcher() {
        List<Match> matchersList = new ArrayList<SearchCompiler.Match>();
        for (NodeModelConf nodeModel : this.nodeModelsList) {

            matchersList.add(nodeModel.getMatcher());
        }

        return new OrList(matchersList);
    }

    @Override
    public Match getWayMatcher() {
        return new SearchCompiler.Always();
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

        DrawableMultipleWorldObject ret = new DrawableMultipleWorldObject();

        for (NodeModelConf nodeModel : this.nodeModelsList) {

            if (nodeModel.getMatcher().match(pNode)) {

                ret.getWorldObjects().add(
                        new kendzi.josm.kendzi3d.jogl.model.PointModel(pNode, nodeModel, perspective, this.modelRender,
                                this.modelCacheService));
            }
        }
        if (ret.getWorldObjects().isEmpty()) {
            return null;
        }
        return ret;
    }

    @Override
    public WorldObject buildModel(Way way, Perspective perspective3D) {

        DrawableMultipleWorldObject ret = new DrawableMultipleWorldObject();
        for (Entry<String, List<WayNodeModelConf>> entry : wayNodeModelsMap.entrySet()) {
            List<WayNodeModelConf> wayNodeModelsList = entry.getValue();

            for (WayNodeModelConf nodeModel : wayNodeModelsList) {

                if (nodeModel.getMatcher().match(way)) {

                    List<Integer> selectedNodes = nodeFilter(nodeModel.getFilter(), way);
                    if (!selectedNodes.isEmpty()) {
                        ret.getWorldObjects().add(
                                new kendzi.josm.kendzi3d.jogl.model.WayNodeModel(way, selectedNodes, nodeModel, perspective3D,
                                        this.modelRender, this.modelCacheService));
                    }
                }
            }
        }
        if (ret.getWorldObjects().isEmpty()) {
            return null;
        }
        return ret;
    }

    /**
     * Finds indexes of nodes on way, match filter.
     * 
     * @param filter
     *            filter for nodes on way
     * @param way
     *            way
     * @return indexes of nodes on way
     */
    private List<Integer> nodeFilter(Match filter, Way way) {
        List<Integer> n = new ArrayList<Integer>();

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            if (filter.match(node)) {
                n.add(i);
            }
        }
        return n;
    }

    @Override
    public WorldObject buildModel(Relation pRelation, Perspective perspective) {
        return null;
    }

    public void cleanUp() {
        // re-download configuration data
        loadData();
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

    /**
     * @param modelCacheService
     *            the modelCacheService to set
     */
    public void setModelCacheService(ModelCacheService modelCacheService) {
        this.modelCacheService = modelCacheService;
    }

    /**
     * @param modelsLibraryService
     *            the modelsLibraryService to set
     */
    public void setModelsLibraryService(ModelsLibraryService modelsLibraryService) {
        this.modelsLibraryService = modelsLibraryService;
    }

    @Override
    public void fireModelsLibraryDataChange() {
        loadData();
    }

}
