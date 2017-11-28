/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.models.library.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.Main;

/**
 * Load and store resources path in memory.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class LibraryResourcesJosmDao implements LibraryResourcesDao {

    /**
     * Property key for list of resources used by models library.
     */
    private static final String KENDZI_3D_MODELS_LIBRARY_RESOURCES_URLS = "kendzi3d.models_library.resources_urls";

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.dao.LibraryResourcesDao#loadResourcesPath()
     */
    @Override
    public List<String> loadResourcesPath() {
        List<String> paths = new ArrayList<String>(Main.pref.getList(KENDZI_3D_MODELS_LIBRARY_RESOURCES_URLS));
        if (paths == null | paths.isEmpty()) {
            return defaultResources();
        }
        return paths;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.dao.LibraryResourcesDao#saveResourcesPath(java.util.List)
     */
    @Override
    public void saveResourcesPath(List<String> fileKeys) {
        Main.pref.putList(KENDZI_3D_MODELS_LIBRARY_RESOURCES_URLS, fileKeys);
    }

    @Override
    public void setDefaultResourcesPaths() {
        Main.pref.put(KENDZI_3D_MODELS_LIBRARY_RESOURCES_URLS, null);
    }

    private static List<String> defaultResources() {
        return new ArrayList<String>(Arrays.asList("plugin:/models/modelsLibraryInternalLayer.xml",
                "plugin:/models/modelsLibraryLayer.xml", "plugin:/models/trafficSignsLibraryInternalLayer.xml"));
    }
}
