package kendzi.josm.kendzi3d.ui.pointModel.action;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JTextField;

import kendzi.josm.kendzi3d.dto.xsd.PointModel;
import kendzi.josm.kendzi3d.service.impl.PointModelService;
import kendzi.josm.kendzi3d.ui.pointModel.PointModelAddFrame;
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
        valid &= validateTextString(txtMatcher);
        valid &= validateTextString(txtModel);
        valid &= validateTextString(txtFilter);
        valid &= validateTextString(txtScale);

        valid &= validateTextEmptyDouble(txtTranslatex);
        valid &= validateTextEmptyDouble(txtTranslatey);
        valid &= validateTextEmptyDouble(txtTranslatez);
        return valid;
    }

    private boolean validateTextString(JTextField pJTextField) {

        boolean valid = !StringUtil.isBlankOrNull(pJTextField.getText());
        setComponentError(pJTextField, !valid);
        return valid;

    }

    private boolean validateTextEmptyDouble(JTextField pJTextField) {
        boolean valid = isEmptyDouble(pJTextField.getText());
        setComponentError(pJTextField, !valid);
        return valid;

    }

    private void setComponentError(JTextField pJTextField, boolean b) {
        if (b) {
            pJTextField.setBackground(Color.red.brighter());
        } else {
            pJTextField.setBackground(null);
        }
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
        pm.setTranslateX(parseDouble(txtTranslatex.getText()));
        pm.setTranslateY(parseDouble(txtTranslatey.getText()));
        pm.setTranslateZ(parseDouble(txtTranslatez.getText()));

        return pm;
    }

    Double parseDouble(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return null;
        }
        try {
            return Double.parseDouble(pStr);
        } catch (Exception e) {
            //
        }
        return null;

    }

    boolean isEmptyDouble(String pStr) {
        if (StringUtil.isBlankOrNull(pStr)) {
            return true;
        }
        return isDouble(pStr);
    }

    boolean isDouble(String pStr) {
        return parseDouble(pStr) != null;
    }

    /**
     * @param pointModelService the pointModelService to set
     */
    public void setPointModelService(PointModelService pointModelService) {
        this.pointModelService = pointModelService;
    }

}
