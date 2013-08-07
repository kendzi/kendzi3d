package kendzi.josm.kendzi3d.ui.pointModel.action;

import generated.PointModel;

import java.util.List;

import kendzi.josm.kendzi3d.service.impl.PointModelService;
import kendzi.josm.kendzi3d.ui.pointModel.PointModelListFrame;

import org.apache.log4j.Logger;

public class PointModelListFrameAction extends PointModelListFrame {

    /** Log. */
    private static final Logger log = Logger.getLogger(PointModelListFrameAction.class);

    /**
     * Point model service.
     */
    private PointModelService pointModelService;

    //    /**
    //     * Launch the application.
    //     */
    //    public static void main(String[] args) {
    //        EventQueue.invokeLater(new Runnable() {
    //            @Override
    //            public void run() {
    //                try {
    //                    PointModelListFrameAction frame = new PointModelListFrameAction();
    //                    frame.setPointModelService(new PointModelService(new FileUrlReciverService(".")));
    //                    frame.loadTableData();
    //                    frame.setVisible(true);
    //                } catch (Exception e) {
    //                    log.error(e);
    //                }
    //            }
    //        });
    //    }



    public void loadTableData() {
        List<PointModel> all = this.pointModelService.findAll();

        this.dataModel.setData(all);

    }

    @Override
    public void addPointModel() {
        PointModelAddFrameAction frame = new PointModelAddFrameAction();
        frame.setPointModelService(this.pointModelService);
        frame.setModal(true);

        frame.setVisible(true);

        loadTableData();
    }

    @Override
    protected void editPointModel() {
        PointModelAddFrameAction frame = new PointModelAddFrameAction();
        frame.setPointModelService(this.pointModelService);
        frame.setModal(true);

        Long id = getSelectedId();
        if (id == null) {
            return;
        }

        frame.load(id);
        frame.setVisible(true);


        loadTableData();
    }

    private Long getSelectedId() {
        int selectedRow = this.table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        if (selectedRow >= this.dataModel.getRowCount()) {
            return null;
        }

        return this.dataModel.getId(selectedRow);
    }

    @Override
    protected void removePointModel() {

        Long id = getSelectedId();
        if (id == null) {
            return;
        }
        this.pointModelService.remove(id);

        loadTableData();
    }

    /**
     * @param pointModelService the pointModelService to set
     */
    public void setPointModelService(PointModelService pointModelService) {
        this.pointModelService = pointModelService;
    }
}
