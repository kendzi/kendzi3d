package kendzi.kendzi3d.models.library.service;

import generated.ModelsLibrary;
import generated.NodeModel;
import generated.WayNodeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesDao;
import kendzi.kendzi3d.models.library.dao.ModelLibraryXmlDao;
import kendzi.kendzi3d.models.library.exception.ModelLibraryLoadException;

import org.apache.log4j.Logger;

public class ModelsLibraryService {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelsLibraryService.class);

    public static final String GLOBAL = "global";

    private UrlReciverService urlReciverService;

    private ModelLibraryXmlDao modelLibraryXmlDao;

    private LibraryResourcesDao libraryResourcesDao;

    private List<ModelsLibraryDataChangeEvent> pointModelDataChange = new ArrayList<ModelsLibraryDataChangeEvent>();


    public void addPointModelDataChangeListener(ModelsLibraryDataChangeEvent pointModelDataChange) {
        this.pointModelDataChange.add(pointModelDataChange);
    }

    public void removePointModelDataChangeListener(ModelsLibraryDataChangeEvent ppointModelDataChange) {
        this.pointModelDataChange.remove(pointModelDataChange);
    }

    protected void firePointModelDataChange() {
        for (ModelsLibraryDataChangeEvent l : this.pointModelDataChange) {
            l.fireModelsLibraryDataChange();
        }
    }

    /** Constructor.
     * @param urlReciverService url reciver service
     */
    @Inject
    public ModelsLibraryService(UrlReciverService urlReciverService, LibraryResourcesDao libraryResourcesMemoryDao) {
        super();
        this.urlReciverService = urlReciverService;
        this.libraryResourcesDao = libraryResourcesMemoryDao;

        modelLibraryXmlDao = new ModelLibraryXmlDao(urlReciverService);

        init();
    }

    private Map<String,ModelsLibrary> modelLibrary = new HashMap<String, ModelsLibrary>();

    public List<NodeModel> findAllNodeModels() {

        List<NodeModel> ret = new ArrayList<NodeModel>();

        for (String fileKey : modelLibrary.keySet()) {
            ret.addAll(modelLibrary.get(fileKey).getNodeModel());
        }
        return ret;
    }

    public List<WayNodeModel> findAllWayNodeModels() {

        List<WayNodeModel> ret = new ArrayList<WayNodeModel>();

        for (String fileKey : modelLibrary.keySet()) {
            ret.addAll(modelLibrary.get(fileKey).getWayNodeModel());
        }
        return ret;
    }
    private void saveModelLibrary(String fileKey, ModelsLibrary models) {
        this.modelLibrary.put(fileKey, models);
    }

    /**
     * Initialize.
     */
    public void init() {

        for (String fileKey : libraryResourcesDao.loadResourcesPath()) {
            try {
                ModelsLibrary models = modelLibraryXmlDao.loadXml(fileKey);

                saveModelLibrary(fileKey, models);

            } catch (ModelLibraryLoadException e) {
                log.error("faild to load file key: " + fileKey, e);
            }
        }
    }

    /**
     * Clear all cache data and reload configuration.
     */
    public void clear() {
        modelLibrary.clear();
        init();
    }

    /**
     * @return the urlReciverService
     */
    public UrlReciverService getUrlReciverService() {
        return urlReciverService;
    }

    public List<String> loadResourcesPath() {
        return libraryResourcesDao.loadResourcesPath();
    }

    public void addResourcesPath(String path) {
        List<String> paths = libraryResourcesDao.loadResourcesPath();
        paths.add(path);
        libraryResourcesDao.saveResourcesPath(paths);

        firePointModelDataChange();
    }

    public void removeResourcesPath(String path) {
        List<String> paths = libraryResourcesDao.loadResourcesPath();
        paths.remove(path);
        libraryResourcesDao.saveResourcesPath(paths);

        firePointModelDataChange();
    }

    public List<NodeModel> findNodeModels(String fileKey) {
        if (GLOBAL.equals(fileKey)) {
            return findAllNodeModels();
        }
        return modelLibrary.get(fileKey).getNodeModel();
    }


    public void setDefaultResourcesPaths() {
        libraryResourcesDao.setDefaultResourcesPaths();

        firePointModelDataChange();
    }
}
