package kendzi.jogl.model.loader;

import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.service.UrlReciverService;

public class ModelLoader {
    public static Model load(String source, UrlReciverService urlReciverService) throws ModelLoadException {
        return (new WaveFrontLoader(urlReciverService)).load(source);
    }
    public static Model load(String source, String replaceTextureMaterialName, String replaceTextureNewKey, UrlReciverService urlReciverService) throws ModelLoadException {
        return (new WaveFrontLoader(replaceTextureMaterialName, replaceTextureNewKey, urlReciverService)).load(source);
    }

}
