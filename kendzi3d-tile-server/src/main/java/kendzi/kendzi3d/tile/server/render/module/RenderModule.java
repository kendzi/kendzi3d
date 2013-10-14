package kendzi.kendzi3d.tile.server.render.module;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.jogl.texture.builder.BwFileTextureBuilder;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.layer.BuildingLayer;
import kendzi.josm.kendzi3d.jogl.layer.FenceLayer;
import kendzi.josm.kendzi3d.jogl.layer.Layer;
import kendzi.josm.kendzi3d.jogl.layer.NewBuildingLayer;
import kendzi.josm.kendzi3d.jogl.layer.RoadLayer;
import kendzi.josm.kendzi3d.jogl.layer.TreeLayer;
import kendzi.josm.kendzi3d.jogl.layer.WaterLayer;
import kendzi.josm.kendzi3d.jogl.layer.models.ModelsLibraryLayer;
import kendzi.josm.kendzi3d.jogl.photos.PhotoRenderer;
import kendzi.josm.kendzi3d.module.binding.Kendzi3dPluginDirectory;
import kendzi.josm.kendzi3d.service.ColorTextureBuilder;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.service.WikiTextureLoaderService;
import kendzi.josm.kendzi3d.service.impl.FileUrlReciverService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLFrame;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.dataset.DataSetProvider;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.kendzi3d.tile.server.render.worker.TitleJobRender;
import kendzi.kendzi3d.tile.server.render.worker.impl.Kendzi3dTileRenderWorker;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class RenderModule  extends AbstractModule {

    RenderEngineConf conf;
    DataSetProvider dsp;

    public RenderModule(RenderEngineConf renderEngineConf, DataSetProvider dsp) {
        this.conf = renderEngineConf;
        this.dsp = dsp;
    }

    @Override
    protected void configure() {

        bindConstant().annotatedWith(Kendzi3dPluginDirectory.class).to(this.conf.getResDir());

        /*
         * This tells Guice that whenever it sees a dependency on a TransactionLog, it should satisfy the dependency
         * using a DatabaseTransactionLog.
         */
        bind(ResourceService.class).to(FileUrlReciverService.class);

        //        bind(ColorTextureBuilder.class);

        bind(MetadataCacheService.class).in(Singleton.class);

        bind(WikiTextureLoaderService.class).in(Singleton.class);

        bind(ModelsLibraryService.class).in(Singleton.class);


        bind(ModelsLibraryLayer.class);
        bind(BuildingLayer.class);
        bind(RoadLayer.class);
        bind(WaterLayer.class);
        bind(TreeLayer.class);
        bind(FenceLayer.class);

        bind(PhotoRenderer.class);

        bind(Kendzi3dGLEventListener.class).in(Singleton.class);

        bind(Kendzi3dGLFrame.class);

        // server
        //        bind(DataSource.class).to
        bind(Kendzi3dTileRenderWorker.class);


        bind(TitleJobRender.class).to(Kendzi3dTileRenderWorker.class).in(Singleton.class);

    }

    @Provides @Singleton
    DataSetProvider provideDataSetProvider() {
        return this.dsp;
    }

    @Provides @Singleton
    RenderEngineConf provideRenderEngineConf() {
        return this.conf;
    }


    @Provides @Singleton
    ModelRender provideModelRender(TextureCacheServiceImpl textureCacheService) {

        ModelRender modelRender = new ModelRender();
        modelRender.setTextureCacheService(textureCacheService);

        boolean debug = false;
        modelRender.setDebugging(debug);
        modelRender.setDrawEdges(debug);
        modelRender.setDrawNormals(debug);
        return modelRender;
    }


    @Provides @Singleton
    TextureCacheServiceImpl provideTextureCacheService(ResourceService pUrlReciverService) {
        TextureCacheServiceImpl textureCacheService = new TextureCacheServiceImpl();
        textureCacheService.setFileUrlReciverService(pUrlReciverService);
        textureCacheService.addTextureBuilder(new ColorTextureBuilder());
        textureCacheService.addTextureBuilder(new BwFileTextureBuilder(pUrlReciverService));

        textureCacheService.setTextureFilter(true);

        return textureCacheService;
    }



    @Provides @Singleton
    RenderJOSM provideRenderJOSM(
            ModelRender pModelRender,
            ModelsLibraryLayer pointModelsLayer,
            NewBuildingLayer buildingLayer,
            RoadLayer roadLayer,
            WaterLayer waterLayer,
            TreeLayer treeLayer,
            FenceLayer fenceLayer

            ) {


        List<Layer> layerList = new ArrayList<Layer>();
        layerList.add(pointModelsLayer);
        layerList.add(buildingLayer);
        layerList.add(roadLayer);
        layerList.add(waterLayer);
        layerList.add(treeLayer);
        layerList.add(fenceLayer);

        RenderJOSM renderJOSM = new RenderJOSM();
        renderJOSM.setModelRender(pModelRender);
        renderJOSM.setLayerList(layerList);

        return renderJOSM;
    }
}
