/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
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
import kendzi.josm.kendzi3d.jogl.layer.Layer;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.kendzi3d.models.library.service.ModelsLibraryDataChangeEvent;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.resource.inter.ResourceService;

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
     * List of layer models.
     */
    private List<Model> modelList = new ArrayList<Model>();

    /**
     * List of model definitions.
     */
    private List<NodeModelConf> nodeModelsList = new ArrayList<NodeModelConf>();

    /**
     * Model renderer.
     */
    // @Inject
    private ModelRender modelRender;

    // @Inject
    private ModelCacheService modelCacheService;

    // @Inject
    private ResourceService urlReciverService;

    // @Inject
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

        // for (WayNodeModel nodeModel :
        // modelsLibraryService.findAllWayNodeModels()) {
        // try {
        // wayNodeModelsList.add(ModelsConvertUtil.convert(nodeModel));
        // } catch (Exception e) {
        // log.error(e, e);
        // }
        // }

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
        // List<Match> matchersList = new ArrayList<SearchCompiler.Match>();
        // for (WayNodeModelConf nodeModel : this.wayNodeModelsList) {
        //
        // matchersList.add(nodeModel.getMatcher());
        // }
        //
        // return new OrList(matchersList);
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
    public List<Model> getModels() {
        return this.modelList;
    }

    @Override
    public void addModel(Node pNode, Perspective3D pPerspective3D) {

        for (NodeModelConf nodeModel : this.nodeModelsList) {

            if (nodeModel.getMatcher().match(pNode)) {

                this.modelList.add(new kendzi.josm.kendzi3d.jogl.model.PointModel(pNode, nodeModel, pPerspective3D,
                        this.modelRender, this.modelCacheService));
            }
        }
    }

    @Override
    public void addModel(Way way, Perspective3D perspective3D) {
        for (Entry<String, List<WayNodeModelConf>> entry : wayNodeModelsMap.entrySet()) {
            List<WayNodeModelConf> wayNodeModelsList = entry.getValue();

            for (WayNodeModelConf nodeModel : wayNodeModelsList) {

                if (nodeModel.getMatcher().match(way)) {

                    List<Integer> selectedNodes = nodeFilter(nodeModel.getFilter(), way);
                    if (!selectedNodes.isEmpty()) {
                        this.modelList.add(new kendzi.josm.kendzi3d.jogl.model.WayNodeModel(way, selectedNodes, nodeModel,
                                perspective3D, this.modelRender, this.modelCacheService));
                    }
                }
            }
        }
    }

    /**
     * Finds indexes of nodes on way, match filter.
     *
     * @param filter filter for nodes on way
     * @param way way
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
    public void addModel(Relation pRelation, Perspective3D pPerspective3D) {
        //
    }

    @Override
    public void clear() {
        // clean up model data
        this.modelList.clear();

        // don't re-download configuration data each time.
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

    /**
     * @param modelCacheService the modelCacheService to set
     */
    public void setModelCacheService(ModelCacheService modelCacheService) {
        this.modelCacheService = modelCacheService;
    }

    /**
     * @param urlReciverService the urlReciverService to set
     */
    public void setUrlReciverService(ResourceService urlReciverService) {
        this.urlReciverService = urlReciverService;
    }

    /**
     * @param modelsLibraryService the modelsLibraryService to set
     */
    public void setModelsLibraryService(ModelsLibraryService modelsLibraryService) {
        this.modelsLibraryService = modelsLibraryService;
    }

    @Override
    public void fireModelsLibraryDataChange() {
        loadData();
    }

}
