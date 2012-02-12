package kendzi.josm.kendzi3d.context;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.service.TextureCacheService;
import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.josm.kendzi3d.ui.Kendzi3dGLEventListener;

public class ApplicationContextUtil {

    public static Kendzi3dGLEventListener getKendzi3dGLEventListener() {
        ApplicationContext context = ApplicationContextFactory.getContext();
        // FIXME rewrite with injections?
        return (Kendzi3dGLEventListener) context.getBean("kendzi3dGLEventListener");
    }
    public static TextureCacheService getTextureCacheService() {
        ApplicationContext context = ApplicationContextFactory.getContext();
        // FIXME rewrite with injections?
        return (TextureCacheService) context.getBean("textureCacheService");
    }
    public static RenderJOSM getRenderJosm() {
        ApplicationContext context = ApplicationContextFactory.getContext();
        // FIXME rewrite with injections?
        return (RenderJOSM) context.getBean("renderJOSM");
    }

    public static UrlReciverService getFileUrlReciverService() {
        ApplicationContext context = ApplicationContextFactory.getContext();
        // FIXME rewrite with injections?
        return (UrlReciverService) context.getBean("fileUrlReciverService");
    }
    public static ModelRender getModelRender() {
        ApplicationContext context = ApplicationContextFactory.getContext();
        // FIXME rewrite with injections?
        return (ModelRender) context.getBean("modelRender");
    }


}
