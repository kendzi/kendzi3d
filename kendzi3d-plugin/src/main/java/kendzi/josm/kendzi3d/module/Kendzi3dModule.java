package kendzi.josm.kendzi3d.module;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import kendzi.jogl.camera.Camera;
import kendzi.jogl.camera.CameraMoveListener;
import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.camera.Viewport;
import kendzi.jogl.camera.ViewportPicker;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.builder.BwFileTextureBuilder;
import kendzi.jogl.texture.builder.ColorTextureBuilder;
import kendzi.jogl.texture.library.TextureLibraryService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.data.Kendzi3dCore;
import kendzi.josm.kendzi3d.data.perspective.Perspective3dProvider;
import kendzi.josm.kendzi3d.data.producer.DataConsumersMonitor;
import kendzi.josm.kendzi3d.data.producer.DataEventListener;
import kendzi.josm.kendzi3d.data.producer.EditorObjectsProducer;
import kendzi.josm.kendzi3d.data.selection.SelectionSynchronizeManager;
import kendzi.josm.kendzi3d.jogl.layer.FenceLayer;
import kendzi.josm.kendzi3d.jogl.layer.NewBuildingLayer;
import kendzi.josm.kendzi3d.jogl.layer.RoadLayer;
import kendzi.josm.kendzi3d.jogl.layer.TestWallLayer;
import kendzi.josm.kendzi3d.jogl.layer.TreeLayer;
import kendzi.josm.kendzi3d.jogl.layer.WallLayer;
import kendzi.josm.kendzi3d.jogl.layer.WaterLayer;
import kendzi.josm.kendzi3d.jogl.layer.models.ModelsLibraryLayer;
import kendzi.josm.kendzi3d.jogl.model.ground.GroundDrawer;
import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround;
import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround.GroundType;
import kendzi.josm.kendzi3d.jogl.model.ground.StyledTitleGroundDrawer;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBox1Configuration;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBoxDrawer;
import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.service.WikiTextureLoaderService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.layer.CameraLayer;
import kendzi.kendzi3d.editor.EditableObjectProvider;
import kendzi.kendzi3d.editor.EditorCore;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.selection.ViewportProvider;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesDao;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesJosmDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.kendzi3d.resource.manager.PluginResourceService;
import kendzi.kendzi3d.resource.manager.ResourceManagerService;
import kendzi.kendzi3d.world.quad.layer.Layer;
import kendzi3d.light.dao.JosmLightDao;
import kendzi3d.light.dao.LightDao;
import kendzi3d.light.service.LightRenderService;
import kendzi3d.light.service.LightStorageService;
import kendzi3d.light.service.impl.LightService;

public class Kendzi3dModule extends AbstractModule {
    private final String pluginDirectory;
    private List<Layer> layers;

    /**
     * @param pPluginDirectory
     *            the URL of the foo server.
     */
    public Kendzi3dModule(String pPluginDirectory) {
        pluginDirectory = pPluginDirectory;
    }

    @Override
    protected void configure() {

        bindConstant().annotatedWith(Kendzi3dPluginDirectory.class).to(pluginDirectory);

        /*
         * This tells Guice that whenever it sees a dependency on a
         * UrlReciverService, it should satisfy the dependency using a
         * FileUrlReciverService.
         */
        bind(ResourceService.class).to(ResourceManagerService.class);

        bind(MetadataCacheService.class).in(Singleton.class);
        bind(WikiTextureLoaderService.class).in(Singleton.class);
        bind(ModelsLibraryService.class).in(Singleton.class);

        bind(ModelCacheService.class).in(Singleton.class);

        bind(LibraryResourcesDao.class).to(LibraryResourcesJosmDao.class).in(Singleton.class);

        bind(NewBuildingLayer.class);
        bind(RoadLayer.class);
        bind(WaterLayer.class);
        bind(TreeLayer.class);
        bind(ModelsLibraryLayer.class).in(Singleton.class);
        bind(FenceLayer.class);
        bind(TestWallLayer.class);

        bind(LightDao.class).to(JosmLightDao.class).in(Singleton.class);
        bind(LightService.class).in(Singleton.class);
        bind(LightStorageService.class).to(LightService.class);
        bind(LightRenderService.class).to(LightService.class);

        bind(Kendzi3dCore.class).in(Singleton.class);
        bind(Perspective3dProvider.class).to(Kendzi3dCore.class).in(Singleton.class);
        bind(ObjectSelectionManager.class).in(Singleton.class);
        bind(SimpleMoveAnimator.class).in(Singleton.class);
        bind(Camera.class).to(SimpleMoveAnimator.class).in(Singleton.class);

        bind(EditableObjectProvider.class).to(Kendzi3dCore.class).in(Singleton.class);
        bind(Viewport.class).in(Singleton.class);
        bind(ViewportPicker.class).to(Viewport.class).in(Singleton.class);

        bind(ViewportProvider.class).to(Kendzi3dGLEventListener.class);

        bind(EditorCore.class).to(Kendzi3dCore.class).in(Singleton.class);

        bind(DataEventListener.class).to(EditorObjectsProducer.class).in(Singleton.class);

        bind(DataConsumersMonitor.class).in(Singleton.class);

    }

    @Provides
    @Singleton
    SelectableGround provideSelectableGround(TextureCacheService textureCacheService,
            TextureLibraryStorageService TextureLibraryStorageService, final Kendzi3dCore kendzi3dCore) {

        SelectableGround ground = new SelectableGround();

        ground.addGroundDrawer(GroundType.SINGLE_TEXTURE, new GroundDrawer(textureCacheService, TextureLibraryStorageService));
        ground.addGroundDrawer(GroundType.STYLED_TITLE, new StyledTitleGroundDrawer(textureCacheService, kendzi3dCore));

        return ground;
    }

    @Provides
    @Singleton
    EditorObjectsProducer provideEditorObjectsProducer(Kendzi3dCore core, DataConsumersMonitor dataConsumersMonitor) {

        EditorObjectsProducer producer = new EditorObjectsProducer(core, dataConsumersMonitor);

        // All objects should be produced in separate thread .
        Thread editorObjectsProducerThread = new Thread(producer, "Editor objects producer thread");
        editorObjectsProducerThread.start();

        return producer;
    }

    @Provides
    @Singleton
    TextureLibraryStorageService provideTextureLibraryStorageService(ResourceService pUrlReciverService) {
        TextureLibraryService textureLibraryService = new TextureLibraryService(pUrlReciverService);
        return textureLibraryService;
    }

    @Provides
    @Singleton
    TextureCacheService provideTextureCacheService(ResourceService pUrlReciverService) {
        TextureCacheServiceImpl textureCacheService = new TextureCacheServiceImpl();
        textureCacheService.setFileUrlReciverService(pUrlReciverService);
        textureCacheService.addTextureBuilder(new ColorTextureBuilder());
        textureCacheService.addTextureBuilder(new BwFileTextureBuilder(pUrlReciverService));
        return textureCacheService;
    }

    @Provides
    @Singleton
    ModelRender provideModelRender(TextureCacheService pTextureCacheService) {
        ModelRender modelRender = new ModelRender();
        modelRender.setTextureCacheService(pTextureCacheService);
        return modelRender;
    }

    @Provides
    @Singleton
    CameraLayer provideCameraLayer(Camera camera, final Kendzi3dCore kendzi3dCore) {

        return new CameraLayer(camera, kendzi3dCore);
    }

    @Provides
    @Singleton
    PluginResourceService providePluginResourceService() {
        return new PluginResourceService(pluginDirectory);
    }

    @Provides
    SkyBoxDrawer provideSkyBoxDrawer(TextureCacheService textureCacheService) {
        return new SkyBoxDrawer(new SkyBox1Configuration(), textureCacheService);
    }

    @Provides
    @Singleton
    List<Layer> provideLayers(ModelsLibraryLayer pointModelsLayer, NewBuildingLayer buildingLayer, RoadLayer roadLayer,
            WaterLayer waterLayer, TreeLayer treeLayer, FenceLayer fenceLayer, WallLayer wallLayer, TestWallLayer testWallLayer) {

        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(buildingLayer);
        layerList.add(roadLayer);
        layerList.add(waterLayer);
        layerList.add(pointModelsLayer);
        layerList.add(treeLayer);
        layerList.add(fenceLayer);
        layerList.add(wallLayer);
        layerList.add(testWallLayer);

        return layerList;
    }

    @Provides
    @Singleton
    CameraMoveListener provideCameraMoveListener(SimpleMoveAnimator simpleMoveAnimator) {
        return new CameraMoveListener(simpleMoveAnimator);
    }

    @Provides
    @Singleton
    SelectionSynchronizeManager provideSelectionSynchronizeManager(ObjectSelectionManager objectSelectionManager) {
        SelectionSynchronizeManager manager = new SelectionSynchronizeManager(objectSelectionManager);
        manager.register();

        return manager;
    }
}
