/*
 * LoaderFactory.java
 *
 * Created on February 27, 2008, 10:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kendzi.jogl.model.loader;

import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.service.UrlReciverService;

/**
 *
 * @author Brian Wood
 */
public class LoaderFactory {
    private static final int FILETYPE_UNKNOWN =    -1;
    private static final int FILETYPE_3DS  =	    1;
    private static final int FILETYPE_OBJ  =	    2;

    public static Model load(String source, UrlReciverService urlReciverService) throws ModelLoadException {
        iLoader loader = getLoader(source, urlReciverService);
        if (loader == null) {
            return null;
        }

        return loader.load(source);
    }

    private static iLoader getLoader(String path, UrlReciverService urlReciverService) {
        switch(determineFiletype(path)) {
        case FILETYPE_3DS:
            //                return new MaxLoader();

        case FILETYPE_OBJ:
            return new WaveFrontLoader(urlReciverService);

        default:
            return null;
        }
    }

    /**
     * Parses the file suffix to determine what file format the model is in.
     *
     * @param path File path info
     * @returns int Constant indicating file type
     */
    private static int determineFiletype(String path) {
        int type = FILETYPE_UNKNOWN;
        String tokens[] = path.split("\\.");

        if(tokens[tokens.length - 1].equals("3ds")) {
            type = FILETYPE_3DS;
        } else if(tokens[tokens.length - 1].equals("obj")) {
            type = FILETYPE_OBJ;
        }

        return type;
    }
}
