/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.models.library.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Load and store resources path in memory.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class LibraryResourcesMemoryDao implements LibraryResourcesDao {

    List<String> resources = defaultResources();

    public List<String> loadDefaultResourcesPath() {
        return defaultResources();
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.dao.LibraryResourcesDao#loadResourcesPath()
     */
    @Override
    public List<String> loadResourcesPath() {
        return Collections.unmodifiableList(resources);
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.dao.LibraryResourcesDao#saveResourcesPath(java.util.List)
     */
    @Override
    public void saveResourcesPath(List<String> fileKeys) {
        resources.clear();
        resources.addAll(fileKeys);
    }

    private static List<String> defaultResources() {
        return Arrays.asList("plugin:/models/pointModelLayerInternal.xml", "plugin:/models/pointModelLayer.xml");
    }
}
