package kendzi.jogl.model.loader;

import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.service.UrlReciverService;

public class ModelLoader {
    public static Model load(String source, UrlReciverService urlReciverService) throws ModelLoadException {
        return (new WaveFrontLoader(urlReciverService)).load(source);
    }


    /**
     * @param source
     * @param replaceTextureMaterialName
     * @param replaceTextureNewKey
     * @param urlReciverService
     * @return
     * @throws ModelLoadException
     * @Deprecated it should be created new factory class for replacing filds in model
     * it should not be part of loading code.
     */
    @Deprecated
    public static Model load(String source, String replaceTextureMaterialName, String replaceTextureNewKey, UrlReciverService urlReciverService) throws ModelLoadException {
        return (new WaveFrontLoader(replaceTextureMaterialName, replaceTextureNewKey, urlReciverService)).load(source);
    }

}
