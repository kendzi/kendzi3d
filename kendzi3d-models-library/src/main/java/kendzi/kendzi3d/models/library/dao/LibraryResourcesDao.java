package kendzi.kendzi3d.models.library.dao;

import java.util.List;

public interface LibraryResourcesDao {

    public abstract List<String> loadResourcesPath();

    public abstract void saveResourcesPath(List<String> fileKeys);

}
