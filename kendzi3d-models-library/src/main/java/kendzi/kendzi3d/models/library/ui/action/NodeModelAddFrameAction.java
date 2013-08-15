package kendzi.kendzi3d.models.library.ui.action;

import generated.NodeModel;

import java.awt.EventQueue;

import kendzi.josm.kendzi3d.ui.validate.ValidateUtil;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.models.library.ui.NodeModelAddFrame;
import kendzi.util.StringUtil;

import org.apache.log4j.Logger;

public class NodeModelAddFrameAction extends NodeModelAddFrame {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(NodeModelAddFrameAction.class);

    /**
     * Point model service.
     */
    private ModelsLibraryService modelsLibraryService;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    NodeModelAddFrameAction frame = new NodeModelAddFrameAction();
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

            NodeModel pointModel = save();
            //            modelsLibraryService.saveOrUpdate(fileKey, pointModel);
            //
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

    @Override
    protected void dictAction() {
        LocalModelsDictAction frame = new LocalModelsDictAction();
        frame.setUrlReciverService(modelsLibraryService.getUrlReciverService());
        frame.setModal(true);
        frame.initUi();
        frame.setVisible(true);

        if (frame.getModel() != null) {
            txtModel.setText(frame.getModel());
        }
    }

    void load(NodeModel pNodeModel ) {
        NodeModel pm = pNodeModel;

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

    NodeModel save() {

        NodeModel pm = new NodeModel();
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
     * @param modelsLibraryService the modelsLibraryService to set
     */
    public void setModelsLibraryService(ModelsLibraryService modelsLibraryService) {
        this.modelsLibraryService = modelsLibraryService;
    }

    public void setEditable(boolean editable) {

        txtId.setEditable(editable);
        txtMatcher.setEditable(editable);
        txtFilter.setEditable(editable);
        txtModel.setEditable(editable);
        txtScale.setEditable(editable);
        txtTranslatex.setEditable(editable);
        txtTranslatey.setEditable(editable);
        txtTranslatez.setEditable(editable);

        getBtnSave().setVisible(editable);
    }

}
