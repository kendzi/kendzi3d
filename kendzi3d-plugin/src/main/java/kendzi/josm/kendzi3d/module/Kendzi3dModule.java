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
import kendzi.josm.kendzi3d.jogl.layer.RoadLayer;
import kendzi.josm.kendzi3d.jogl.layer.TestWallLayer;
import kendzi.josm.kendzi3d.jogl.layer.TreeLayer;
import kendzi.josm.kendzi3d.jogl.layer.WallLayer;
import kendzi.josm.kendzi3d.jogl.layer.WaterLayer;
import kendzi.josm.kendzi3d.jogl.layer.models.ModelsLibraryLayer;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.photos.PhotoRenderer;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBox;
import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.ModelCacheService;
import kendzi.josm.kendzi3d.service.WikiTextureLoaderService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLFrame;
import kendzi.josm.kendzi3d.ui.layer.CameraLayer;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesDao;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesJosmDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.kendzi3d.resource.manager.PluginResourceService;
import kendzi.kendzi3d.resource.manager.ResourceManagerService;

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

        // bind(ModelRender.class).in(Singleton.class);

        bind(NewBuildingLayer.class);
        bind(RoadLayer.class);
        bind(WaterLayer.class);
        bind(TreeLayer.class);
        bind(ModelsLibraryLayer.class).in(Singleton.class);
        bind(FenceLayer.class);
        bind(TestWallLayer.class);

        bind(PhotoRenderer.class);
        bind(SkyBox.class);

        bind(Kendzi3dGLEventListener.class).in(Singleton.class);

        bind(Kendzi3dGLFrame.class);

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
    CameraLayer provideCameraLayer(final Kendzi3dGLEventListener kendzi3dGLEventListener) {

        // XXX TODO FIXME Temporary !!!!
        Perspective3D p = new Perspective3D(0, 0, 0) {
            @Override
            public org.openstreetmap.josm.data.coor.EastNorth toEastNorth(double x, double y) {
                RenderJOSM renderJosm = kendzi3dGLEventListener.getRenderJosm();

                return renderJosm.getPerspective().toEastNorth(x, y);
            };
        };

        CameraLayer cl = new CameraLayer(kendzi3dGLEventListener.getCamera(), p);

        return cl;
    }

    @Provides
    @Singleton
    RenderJOSM provideRenderJOSM(ModelRender pModelRender, ModelsLibraryLayer pointModelsLayer, NewBuildingLayer buildingLayer,
            RoadLayer roadLayer, WaterLayer waterLayer, TreeLayer treeLayer, FenceLayer fenceLayer, WallLayer wallLayer,
            TestWallLayer testWallLayer) {

        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(buildingLayer);
        layerList.add(roadLayer);
        layerList.add(waterLayer);
        layerList.add(pointModelsLayer);
        layerList.add(treeLayer);
        layerList.add(fenceLayer);
        layerList.add(wallLayer);
        layerList.add(testWallLayer);

        RenderJOSM renderJOSM = new RenderJOSM();
        renderJOSM.setModelRender(pModelRender);
        renderJOSM.setLayerList(layerList);

        return renderJOSM;
    }

    @Provides
    @Singleton
    PluginResourceService providePluginResourceService() {
        return new PluginResourceService(pluginDirectory);
    }
}
