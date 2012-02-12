package kendzi.josm.kendzi3d.context;


public class ApplicationContextFactory {

    private static ApplicationContext context = null;

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * This method mimic spring application context initiation.
     * @param pluginDir
     */
    public static void initContext(String pluginDir) {
        ApplicationContext context = new ApplicationContext();

//        // Services
//        FileUrlReciverService fileUrlReciverService
//            = new FileUrlReciverService();
//        fileUrlReciverService.setPluginDir(pluginDir);


//        context.addBean("fileUrlReciverService", fileUrlReciverService);



//        TextureCacheService textureCacheService = new TextureCacheService();
////        textureCacheService.setFileUrlReciverService(fileUrlReciverService);
//        textureCacheService.addTextureBuilder(new ColorTextureBuilder());
//
//        context.addBean("textureCacheService", textureCacheService);

//        MetadataCacheService metadataCacheService = new MetadataCacheService();
//        metadataCacheService.setFileUrlReciverService(fileUrlReciverService);
//
//        context.addBean("metadataCacheService", metadataCacheService);

//        WikiTextureLoaderService wikiTextureLoaderService = new WikiTextureLoaderService();
//        wikiTextureLoaderService.setPluginDir(pluginDir);
//        wikiTextureLoaderService.setMetadataCacheService(metadataCacheService);
//        wikiTextureLoaderService.setTextureCacheService(textureCacheService);
//        context.addBean("wikiTextureLoaderService", wikiTextureLoaderService);

//
//        // UI
//        // opengl
//        ModelRender modelRender = new ModelRender();
////        modelRender.setTextureCacheService(textureCacheService);
//        context.addBean("modelRender", modelRender);
//
////        PointModelsLayer pointModelsLayer = new PointModelsLayer();
////        pointModelsLayer.setModelRender(modelRender);
////        pointModelsLayer.setFileUrlReciverService(fileUrlReciverService);
////        <bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
////        pointModelsLayer.init();
//
////        BuildingLayer buildingLayer = new BuildingLayer();
////        buildingLayer.setModelRender(modelRender);
//
////        RoadLayer roadLayer = new RoadLayer();
////        roadLayer.setModelRender(modelRender);
////
////        WaterLayer waterLayer = new WaterLayer();
////        waterLayer.setModelRender(modelRender);
//
////        TreeLayer treeLayer = new TreeLayer();
////        treeLayer.setModelRender(modelRender);
//
////        FenceLayer fenceLayer = new FenceLayer();
////        fenceLayer.setModelRender(modelRender);
//
//        List<Layer> layerList = new ArrayList<Layer>();
//        layerList.add(pointModelsLayer);
//        layerList.add(buildingLayer);
//        layerList.add(roadLayer);
//        layerList.add(waterLayer);
//        layerList.add(treeLayer);
//        layerList.add(fenceLayer);
//
//        RenderJOSM renderJOSM = new RenderJOSM();
//        renderJOSM.setModelRender(modelRender);
//        renderJOSM.setLayerList(layerList);
//        context.addBean("renderJOSM", renderJOSM);

//        PhotoRenderer photoRenderer = new PhotoRenderer();
////        photoRenderer.setTextureCacheService(textureCacheService);
//
//        Kendzi3dGLEventListener kendzi3dGLEventListener = new Kendzi3dGLEventListener();
//        kendzi3dGLEventListener.setRenderJosm(renderJOSM);
//        kendzi3dGLEventListener.setModelRender(modelRender);
//        kendzi3dGLEventListener.setPhotoRenderer(photoRenderer);
//
//        context.addBean("kendzi3dGLEventListener", kendzi3dGLEventListener);

//        // menu actions
//        DebugToggleAction debugToggleAction = new DebugToggleAction();
//        debugToggleAction.setModelRender(modelRender);
//        debugToggleAction.init();
//        context.addBean("debugToggleAction", debugToggleAction);

        ApplicationContextFactory.context = context;
    }

}
