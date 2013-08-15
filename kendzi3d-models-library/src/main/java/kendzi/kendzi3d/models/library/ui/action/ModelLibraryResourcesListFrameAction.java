/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.models.library.ui.action;

import java.awt.EventQueue;
import java.util.List;

import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesDao;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesMemoryDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.models.library.ui.ModelLibraryResourcesListFrame;

import org.apache.log4j.Logger;

/**
 * Ui for list of resource in models library.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class ModelLibraryResourcesListFrameAction extends ModelLibraryResourcesListFrame {

    /** Log. */
    private static final Logger log = Logger.getLogger(ModelLibraryResourcesListFrameAction.class);

    /**
     * Point model service.
     */
    private ModelsLibraryService modelsLibraryService;

    /**
     * Resources for library DAO.
     */
    private LibraryResourcesDao libraryResourcesDao;

    public ModelLibraryResourcesListFrameAction(ModelsLibraryService modelsLibraryService, LibraryResourcesDao libraryResourcesDao) {
        super();
        this.modelsLibraryService = modelsLibraryService;
        this.libraryResourcesDao = libraryResourcesDao;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UrlReciverService urlReciverService = new UrlReciverServiceTest();
                    ModelsLibraryService pms = new ModelsLibraryService(urlReciverService, new LibraryResourcesMemoryDao());
                    pms.init();
                    ModelLibraryResourcesListFrameAction frame
                    = new ModelLibraryResourcesListFrameAction(pms, new LibraryResourcesMemoryDao());


                    frame.loadTableData();
                    frame.setVisible(true);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }

    public void loadTableData() {
        List<String> all = this.modelsLibraryService.loadResourcesPath();

        this.dataModel.setData(all);
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.ui.ModelLibraryResourcesListFrame#viewFinalLibrary()
     */
    @Override
    protected void viewFinalLibrary() {
        NodeModelListFrameAction frame = NodeModelListFrameAction.buildFrame(
                ModelsLibraryService.GLOBAL, true, modelsLibraryService, libraryResourcesDao);
        frame.setVisible(true);

    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.models.library.ui.ModelLibraryResourcesListFrame#viewResourceDetails()
     */
    @Override
    protected void viewResourceDetails() {

        String fileKey = getSelected();

        if (fileKey == null) {
            return;
        }

        NodeModelListFrameAction frame = NodeModelListFrameAction.buildFrame(
                fileKey, true, modelsLibraryService, libraryResourcesDao);
        frame.setVisible(true);
    }

    private String getSelected() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        if (selectedRow >= this.dataModel.getRowCount()) {
            return null;
        }

        return this.dataModel.get(selectedRow);
    }
}
