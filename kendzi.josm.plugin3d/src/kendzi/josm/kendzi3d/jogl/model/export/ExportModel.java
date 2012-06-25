package kendzi.josm.kendzi3d.jogl.model.export;

import java.util.List;


public interface ExportModel {
    List<ExportItem> export(ExportModelConf conf);
}
