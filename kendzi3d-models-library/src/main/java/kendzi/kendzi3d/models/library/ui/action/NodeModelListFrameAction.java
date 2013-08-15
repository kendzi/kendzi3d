package kendzi.kendzi3d.models.library.ui.action;

import generated.NodeModel;

import java.awt.EventQueue;
import java.util.List;

import kendzi.josm.kendzi3d.service.UrlReciverService;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesDao;
import kendzi.kendzi3d.models.library.dao.LibraryResourcesMemoryDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.models.library.ui.NodeModelListFrame;

import org.apache.log4j.Logger;

public class NodeModelListFrameAction extends NodeModelListFrame {


    /** Log. */
    private static final Logger log = Logger.getLogger(NodeModelListFrameAction.class);


    /**
     * Point model service.
     */
    private ModelsLibraryService modelsLibraryService;

    private String fileKey;

    private boolean readOnly;



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NodeModelListFrameAction frame = new NodeModelListFrameAction();
                    UrlReciverService urlReciverService = new UrlReciverServiceTest();
                    ModelsLibraryService temp = new ModelsLibraryService(urlReciverService, new LibraryResourcesMemoryDao());
                    temp.init();

                    frame.setNodeModelService(temp);
                    frame.loadTableData();
                    frame.setVisible(true);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }



    public void loadTableData() {
        List<NodeModel> all = this.modelsLibraryService.findNodeModels(fileKey);

        this.dataModel.setData(all);

    }

    @Override
    public void viewNodeModel() {
        NodeModelAddFrameAction frame = new NodeModelAddFrameAction();
        frame.setModelsLibraryService(this.modelsLibraryService);
        frame.setEditable(false);
        frame.setModal(true);
        frame.setVisible(true);

        loadTableData();
    }

    @Override
    public void addNodeModel() {
        NodeModelAddFrameAction frame = new NodeModelAddFrameAction();
        frame.setModelsLibraryService(this.modelsLibraryService);
        frame.setModal(true);

        frame.setVisible(true);

        loadTableData();
    }

    @Override
    protected void editNodeModel() {
        NodeModelAddFrameAction frame = new NodeModelAddFrameAction();
        frame.setModelsLibraryService(this.modelsLibraryService);
        frame.setModal(true);

        NodeModel id = getSelected();
        if (id == null) {
            return;
        }

        frame.load(id);
        frame.setVisible(true);


        loadTableData();
    }

    private NodeModel getSelected() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        if (selectedRow >= this.dataModel.getRowCount()) {
            return null;
        }

        return this.dataModel.get(selectedRow);
    }

    @Override
    protected void removeNodeModel() {

        //        NodeModel pointModel = getSelected();
        //        if (pointModel == null) {
        //            return;
        //        }
        //        this.modelsLibraryService.remove(fileKey, pointModel);

        loadTableData();
    }

    /**
     * @param modelsLibraryService the modelsLibraryService to set
     */
    public void setNodeModelService(ModelsLibraryService modelsLibraryService) {
        this.modelsLibraryService = modelsLibraryService;
    }


    public static NodeModelListFrameAction buildFrame(
            String fileKey,
            boolean readOnly,
            ModelsLibraryService modelsLibraryService,
            LibraryResourcesDao libraryResourcesDao) {

        NodeModelListFrameAction frame = new NodeModelListFrameAction();
        frame.setNodeModelService(modelsLibraryService);
        frame.setFileKey(fileKey);
        frame.setReadOnly(readOnly);

        frame.loadTableData();

        return frame;
    }



    /**
     * @return the fileKey
     */
    public String getFileKey() {
        return fileKey;
    }



    /**
     * @param fileKey the fileKey to set
     */
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
        this.getLblFileKey().setText(fileKey);
    }



    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
