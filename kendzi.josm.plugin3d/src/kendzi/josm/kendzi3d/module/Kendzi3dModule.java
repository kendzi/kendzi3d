package kendzi.josm.kendzi3d.module;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.builder.BwFileTextureBuilder;
import kendzi.jogl.texture.builder.ColorTextureBuilder;
import kendzi.jogl.texture.library.TextureLibraryService;
import kendzi.jogl.texture.library.TextureLibraryStorageService;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.layer.FenceLayer;
import kendzi.josm.kendzi3d.jogl.layer.Layer;
import kendzi.josm.kendzi3d.jogl.layer.NewBuildingLayer;
import kendzi.josm.kendzi3d.jogl.layer.PointModelsLayer;
import kendzi.josm.kendzi3d.jogl.layer.RoadLayer;
import kendzi.josm.kendzi3d.jogl.layer.TreeLayer;
import kendzi.josm.kendzi3d.jogl.layer.WallLayer;
import kendzi.josm.kendzi3d.jogl.layer.WaterLayer;
import kendzi.josm.kendzi3d.jogl.photos.PhotoRenderer;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBox;
import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.josm.kendzi3d.service.WikiTextureLoaderService;
import kendzi.josm.kendzi3d.service.impl.FileUrlReciverService;
import kendzi.josm.kendzi3d.service.impl.PointModelService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLFrame;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class Kendzi3dModule extends AbstractModule {
    private final String pluginDirectory;

    /**
     * @param pPluginDirectory
     *            the URL of the foo server.
     */
    public Kendzi3dModule(String pPluginDirectory) {
        this.pluginDirectory = pPluginDirectory;
    }

    @Override
    protected void configure() {

        bindConstant().annotatedWith(Kendzi3dPluginDirectory.class).to(this.pluginDirectory);

        /*
         * This tells Guice that whenever it sees a dependency on a UrlReciverService, it should satisfy the dependency
         * using a FileUrlReciverService.
         */
        bind(UrlReciverService.class).to(FileUrlReciverService.class);

        bind(MetadataCacheService.class).in(Singleton.class);
        bind(WikiTextureLoaderService.class).in(Singleton.class);
        bind(PointModelService.class).in(Singleton.class);

        bind(ModelCacheService.class).in(Singleton.class);

//        bind(ModelRender.class).in(Singleton.class);

        bind(NewBuildingLayer.class);
        bind(RoadLayer.class);
        bind(WaterLayer.class);
        bind(TreeLayer.class);
        bind(PointModelsLayer.class);
        bind(FenceLayer.class);
        bind(WallLayer.class);

        bind(PhotoRenderer.class);
        bind(SkyBox.class);

        bind(Kendzi3dGLEventListener.class).in(Singleton.class);

        bind(Kendzi3dGLFrame.class);

    }

    @Provides @Singleton
    TextureLibraryStorageService provideTextureLibraryStorageService(UrlReciverService pUrlReciverService) {
        TextureLibraryService textureLibraryService = new TextureLibraryService(pUrlReciverService);
        return textureLibraryService;
    }

    @Provides @Singleton
    TextureCacheService provideTextureCacheService(UrlReciverService pUrlReciverService) {
        TextureCacheServiceImpl textureCacheService = new TextureCacheServiceImpl();
        textureCacheService.setFileUrlReciverService(pUrlReciverService);
        textureCacheService.addTextureBuilder(new ColorTextureBuilder());
        textureCacheService.addTextureBuilder(new BwFileTextureBuilder(pUrlReciverService));
        return textureCacheService;
    }

    @Provides @Singleton
    ModelRender provideModelRender(TextureCacheService pTextureCacheService) {
        ModelRender modelRender = new ModelRender();
        modelRender.setTextureCacheService(pTextureCacheService);
        return modelRender;
    }

//    @Provides
//    PointModelsLayer providePointModelsLayer(
//            UrlReciverService pUrlReciverService,
//            ModelRender pModelRender,
//            ModelCacheService modelCacheService) {
//
//        PointModelsLayer pointModelsLayer = new PointModelsLayer();
//        pointModelsLayer.setUrlReciverService(pUrlReciverService);
//        pointModelsLayer.setModelRender(pModelRender);
//        pointModelsLayer.setModelCacheService(modelCacheService);
//        pointModelsLayer.init();
//
//        return pointModelsLayer;
//    }

    @Provides @Singleton
    RenderJOSM provideRenderJOSM(
            ModelRender pModelRender,
            PointModelsLayer pointModelsLayer,
            NewBuildingLayer buildingLayer,
            RoadLayer roadLayer,
            WaterLayer waterLayer,
            TreeLayer treeLayer,
            FenceLayer fenceLayer,
            WallLayer wallLayer

    ) {

        // PointModelsLayer pointModelsLayer = new PointModelsLayer();
        // pointModelsLayer.setModelRender(modelRender);
        // pointModelsLayer.setFileUrlReciverService(fileUrlReciverService);
        // <bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
        // pointModelsLayer.init();

        // BuildingLayer buildingLayer = new BuildingLayer();
        // buildingLayer.setModelRender(modelRender);

        // RoadLayer roadLayer = new RoadLayer();
        // roadLayer.setModelRender(modelRender);
        //
        // WaterLayer waterLayer = new WaterLayer();
        // waterLayer.setModelRender(modelRender);

        // TreeLayer treeLayer = new TreeLayer();
        // treeLayer.setModelRender(modelRender);

        // FenceLayer fenceLayer = new FenceLayer();
        // fenceLayer.setModelRender(modelRender);

        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(buildingLayer);
        layerList.add(roadLayer);
        layerList.add(waterLayer);
        layerList.add(pointModelsLayer);
        layerList.add(treeLayer);
        layerList.add(fenceLayer);
        layerList.add(wallLayer);

        RenderJOSM renderJOSM = new RenderJOSM();
        renderJOSM.setModelRender(pModelRender);
        renderJOSM.setLayerList(layerList);

        return renderJOSM;
    }
}
