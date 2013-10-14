package kendzi.jogl.model.loader;

import kendzi.jogl.model.geometry.Model;
import kendzi.kendzi3d.resource.inter.ResourceService;

public class ModelLoader {
    public static Model load(String source, ResourceService urlReciverService) throws ModelLoadException {
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
    public static Model load(String source, String replaceTextureMaterialName, String replaceTextureNewKey, ResourceService urlReciverService) throws ModelLoadException {
        return (new WaveFrontLoader(replaceTextureMaterialName, replaceTextureNewKey, urlReciverService)).load(source);
    }

}
