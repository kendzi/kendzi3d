package kendzi.kendzi3d.models.library.dao;

import java.util.List;

public interface LibraryResourcesDao {

    void setDefaultResourcesPaths();

    List<String> loadResourcesPath();

    void saveResourcesPath(List<String> fileKeys);

}
