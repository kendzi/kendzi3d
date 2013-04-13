package kendzi.josm.kendzi3d.jogl.model.export.ui.action;

import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.export.ui.ExportUi;
import kendzi.josm.kendzi3d.ui.validate.ValidateUtil;

public class ExportUiAction extends ExportUi {

    ExportModelConf exportModelConf = null;


    @Override
    protected void onExportAction() {
        // 1. validate
        // 2. save
        // 3. refresh & dispose

        if (validateData()) {

            this.exportModelConf = save();

            dispose();
        }

    }

    ExportModelConf save() {

        ExportModelConf c = new ExportModelConf();

        c.setExportType("collada");
        c.setNumOfModels(ValidateUtil.parseInteger(getTxtNumOfModels().getText()));
        c.setFilePattern(getTxtFilePattern().getText());

        return c;
    }


    public boolean validateData() {
        boolean valid = true;

        valid &= ValidateUtil.validateTextEmptyInteger(getTxtNumOfModels());
        valid &= ValidateUtil.validateTextString(getTxtFilePattern());

        return valid;
    }


    /**
     * @return the exportModelConf
     */
    public ExportModelConf getExportModelConf() {
        return exportModelConf;
    }


    /**
     * @param exportModelConf the exportModelConf to set
     */
    public void setExportModelConf(ExportModelConf exportModelConf) {
        this.exportModelConf = exportModelConf;
    }
}
