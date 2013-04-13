package kendzi.josm.kendzi3d.ui.pointModel.action;

import java.awt.EventQueue;

import kendzi.josm.kendzi3d.dto.xsd.PointModel;
import kendzi.josm.kendzi3d.service.impl.PointModelService;
import kendzi.josm.kendzi3d.ui.pointModel.PointModelAddFrame;
import kendzi.josm.kendzi3d.ui.validate.ValidateUtil;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.apache.log4j.Logger;

public class PointModelAddFrameAction extends PointModelAddFrame {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PointModelAddFrameAction.class);

    /**
     * Point model service.
     */
    private PointModelService pointModelService;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    PointModelAddFrameAction frame = new PointModelAddFrameAction();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void saveAction() {
        // 1. validate
        // 2. save
        // 3. refresh & dispose

        if (validateData()) {

            PointModel pointModel = save();
            pointModelService.saveOrUpdate(pointModel);

            dispose();
        }

    }


    public boolean validateData() {
        boolean valid = true;
        valid &= ValidateUtil.validateTextString(txtMatcher);
        valid &= ValidateUtil.validateTextString(txtModel);
        valid &= ValidateUtil.validateTextString(txtFilter);
        valid &= ValidateUtil.validateTextString(txtScale);

        valid &= ValidateUtil.validateTextEmptyDouble(txtTranslatex);
        valid &= ValidateUtil.validateTextEmptyDouble(txtTranslatey);
        valid &= ValidateUtil.validateTextEmptyDouble(txtTranslatez);
        return valid;
    }



    public void load(Long id) {
        PointModel pointModel = pointModelService.load(id);
        load(pointModel);

    }

    @Override
    protected void dictAction() {
        LocalModelsDictAction frame = new LocalModelsDictAction();
        frame.setUrlReciverService(pointModelService.getUrlReciverService());
        frame.setModal(true);
        frame.initUi();
        frame.setVisible(true);

        if (frame.getModel() != null) {
            txtModel.setText(frame.getModel());
        }
    }

    void load(PointModel pPointModel ) {
        PointModel pm = pPointModel;

        txtId.setText("" + pm.getId());
        txtMatcher.setText(pm.getMatcher());
        txtModel.setText(pm.getModel());
        txtScale.setText(pm.getScale());
        txtTranslatex.setText(formatNumber(pm.getTranslateX()));
        txtTranslatey.setText(formatNumber(pm.getTranslateY()));
        txtTranslatez.setText(formatNumber(pm.getTranslateZ()));
    }

    String formatNumber(Number num) {
        if (num  == null) {
            return "";
        }
        return "" + num;
    }

    PointModel save() {

        PointModel pm = new PointModel();
        if (!StringUtil.isBlankOrNull(txtId.getText())) {
            pm.setId(Long.parseLong(txtId.getText()));
        }
        pm.setMatcher(txtMatcher.getText());
        pm.setModel(txtModel.getText());
        pm.setScale(txtScale.getText());
        pm.setTranslateX(ValidateUtil.parseDouble(txtTranslatex.getText()));
        pm.setTranslateY(ValidateUtil.parseDouble(txtTranslatey.getText()));
        pm.setTranslateZ(ValidateUtil.parseDouble(txtTranslatez.getText()));

        return pm;
    }


    /**
     * @param pointModelService the pointModelService to set
     */
    public void setPointModelService(PointModelService pointModelService) {
        this.pointModelService = pointModelService;
    }

}
