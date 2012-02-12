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

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.loader.LoaderFactory;
import net.java.joglutils.model.ModelLoadException;

import com.google.inject.Inject;

public class ModelCacheService {

    @Inject
    UrlReciverService urlReciverService;

    private Map<String, Model> modelCache = new HashMap<String, Model>();

    public Model getModel(String pId) {
        return this.modelCache.get(pId);
    }

    public Model loadModel(String pId) throws ModelLoadException {
        Model model = this.modelCache.get(pId);
        if (model == null) {
            model = LoaderFactory.load(pId, urlReciverService);
            this.modelCache.put(pId, model);
        }
        if (model == null) {
            throw new ModelLoadException("can't find model id: " + pId);
        }
        return model;
    }

}
