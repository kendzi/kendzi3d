/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.resource.inter;

import java.net.URL;

/**
 * Manage of resources.
 * 
 * @author Tomasz Kedziora (Kendzi)
 */
public interface ResourceService {

    public static final String PLUGIN_FILE_PREFIX = "plugin:";

    //    /**
    //     * Try to get file URL. It is looking at:<br>
    //     * 1. directory: {PLUGIN_DIR_NAME}/ <br>
    //     * 2. resources from jar in directory: {PLUGIN_JAR}/ <br>
    //     *
    //     *
    //     * @param pFileName
    //     *            file name
    //     * @return file url
    //     */
    //    @Deprecated
    //    URL receiveFileUrl(String pFileName);

    /**
     * Receive url from directory: {PLUGIN_DIR_NAME}
     * 
     * @param pFileName
     *            file name
     * @return file url
     */
    @Deprecated
    URL receivePluginDirUrl(String pFileName);

    /**
     * @return the pluginDir
     */
    @Deprecated
    String getPluginDir();

    URL resourceToUrl(String resourceName);

}
