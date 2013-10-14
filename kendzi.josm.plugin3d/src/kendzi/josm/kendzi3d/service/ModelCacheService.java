/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.loader.ModelLoadException;
import kendzi.jogl.model.loader.ModelLoader;
import kendzi.kendzi3d.resource.inter.ResourceService;

import com.google.inject.Inject;

public class ModelCacheService {

    private static final String MATERIAL = "material.";

    @Inject
    ResourceService urlReciverService;

    private Map<String, Model> modelCache = new HashMap<String, Model>();

    public Model getModel(String pId) {
        return this.modelCache.get(pId);
    }

    public Model generateModel(String key, String parameter) throws ModelLoadException {
        String cacheKey = genaratedName(key, parameter);

        Model model = this.modelCache.get(cacheKey);
        if (model == null) {
//            model = ModelLoader.load(key, urlReciverService);

            model = generateParameters(key, parameter);

            this.modelCache.put(cacheKey, model);
        }
        if (model == null) {
            throw new ModelLoadException("can't find model key: " + cacheKey);
        }
        return model;
    }

    /**
     * @param key
     * @param parameter
     * @return
     */
    private String genaratedName(String key, String parameter) {
        String cacheKey = "#gen#" + key + "#" + parameter;
        return cacheKey;
    }

    private Model generateParameters(String key, String parameter) throws ModelLoadException {

        // Model ret = new Model();
        // ret.setBounds(model.getBounds());
        // ret.setSource(genaratedName(model.getSource(),parameter));
        // ret.setCenterPoint(model.getCenterPoint());
        // ret.setDrawEdges(model.isDrawEdges());
        // ret.setDrawNormals(model.isDrawNormals());
        // ret.setDrawVertex(model.isDrawVertex());
        // ret.setUseLight(model.isUseLight());
        // ret.setUseScale(model.isUseScale());
        // ret.setUseTexture(model.isUseTexture());
        //
        // ret.mesh = model.mesh;

        // if (parameter.startsWith(MATERIAL)) {
        // material.mat_sign.map_Kd=/textures/tree.png
        Pattern pattern = Pattern.compile("^material\\.(\\w*?)\\.texture0=([\\w/.]*?)$");
        Matcher matcher = pattern.matcher(parameter);
        if (matcher.find()) {
            String materialName = matcher.group(1);
            String texture0 = matcher.group(2);

            // ret.materials = replaceMapKd(materialName, map_Kd,
            // model.materials);

            return ModelLoader.load(key, materialName, texture0, urlReciverService);

        }
        return ModelLoader.load(key, urlReciverService);

    }

//    private List<Material> replaceMapKd(String materialName, String map_Kd, List<Material> materials) {
//        List<Material> ret = new ArrayList<Material>();
//        for (Material material : materials) {
//            if (materialName.equals(material.get)
//        }
//        // TODO Auto-generated method stub
//        return null;
//    }

    public Model loadModel(String key) throws ModelLoadException {
        Model model = this.modelCache.get(key);
        if (model == null) {
            model = ModelLoader.load(key, urlReciverService);
            this.modelCache.put(key, model);
        }
        if (model == null) {
            throw new ModelLoadException("can't find model key: " + key);
        }
        return model;
    }

    public void clear() {
        this.modelCache.clear();
    }

}
