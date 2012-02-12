/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.service;

import java.net.URL;

/**
 * Receive files Urls.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public interface UrlReciverService {

    /**
     * Try to get file URL. It is looking at:<br>
     * 1. directory: {PLUGIN_DIR_NAME}/ <br>
     * 2. resources from jar in directory: {PLUGIN_JAR}/ <br>
     *
     *
     * @param pFileName file name
     * @return file url
     */
    public abstract URL reciveFileUrl(String pFileName);

    /**
     * @return the pluginDir
     */
    public abstract String getPluginDir();

}
