/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model;

import java.util.HashMap;
import java.util.Map;

import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.loader.LoaderFactory;
import net.java.joglutils.model.ModelLoadException;

public class ModelCache {
    private static Map<String, Model> modelCache = new HashMap<String, Model>();

    public static Model getModel(String pId) {
        return modelCache.get(pId);
    }

    public static Model loadModel(String pId) throws ModelLoadException {
        Model model = modelCache.get(pId);
        if (model == null) {
            model = LoaderFactory.load(pId);
            modelCache.put(pId, model);
        }
        if (model == null) {
            throw new ModelLoadException("can't find model id: " + pId);
        }
        return model;
    }

}
