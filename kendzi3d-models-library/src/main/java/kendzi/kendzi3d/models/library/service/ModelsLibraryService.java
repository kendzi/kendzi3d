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

    private LibraryResourcesDao libraryResourcesMemoryDao;

    private List<ModelsLibraryDataChangeEvent> pointModelDataChange = new ArrayList<ModelsLibraryDataChangeEvent>();


    public void addPointModelDataChangeListener(ModelsLibraryDataChangeEvent pPointModelDataChange) {
        this.pointModelDataChange.add(pPointModelDataChange);
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
        this.libraryResourcesMemoryDao = libraryResourcesMemoryDao;

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

    //    static Vector3d parseVector(String x, String y, String z) {
    //        return new Vector3d(
    //                parseDouble(x, 0d),
    //                parseDouble(y, 0d),
    //                parseDouble(z, 0d));
    //    }
    //
    //    private static Double parseDouble(String z, Double d) {
    //        if (z == null || "".equals(z)) {
    //            return d;
    //        }
    //
    //        try {
    //            return Double.parseDouble(z);
    //        } catch (Exception e) {
    //            log.error(e,e);
    //        }
    //        return d;
    //    }

    private void saveModelLibrary(String fileKey, ModelsLibrary models) {
        this.modelLibrary.put(fileKey, models);
    }

    /**
     * Initialize.
     */
    public void init() {

        for (String fileKey : libraryResourcesMemoryDao.loadResourcesPath()) {
            try {
                ModelsLibrary models = modelLibraryXmlDao.loadXml(fileKey);

                saveModelLibrary(fileKey, models);

            } catch (ModelLibraryLoadException e) {
                log.error("faild to load file key: "+ fileKey,e);
            }

        }
    }

    /**
     * @return the urlReciverService
     */
    public UrlReciverService getUrlReciverService() {
        return urlReciverService;
    }


    //    public void remove(String fileKey, PointModel pointModel) {
    //        throw new RuntimeException("TODO");
    //    }


    public List<String> loadResourcesPath() {
        return libraryResourcesMemoryDao.loadResourcesPath();
    }


    public List<NodeModel> findNodeModels(String fileKey) {
        if (GLOBAL.equals(fileKey)) {
            return findAllNodeModels();
        }
        return modelLibrary.get(fileKey).getNodeModel();
    }

}
