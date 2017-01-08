/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

import kendzi.jogl.texture.TextureCacheService;
import kendzi.josm.kendzi3d.data.Kendzi3dCore;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModel;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;
import kendzi.josm.kendzi3d.jogl.model.export.ExportWorker;
import kendzi.josm.kendzi3d.jogl.model.export.ui.action.ExportUiAction;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.world.WorldObject;

/**
 * Export action.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class ExportAction extends JosmAction {

    /** Log. */
    private static final Logger log = Logger.getLogger(ExportAction.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Texture cache service.
     */
    private TextureCacheService textureCacheService;

    /**
     * Rendering service.
     */
    private Kendzi3dCore kendzi3dCore;

    final JFileChooser fc = new JFileChooser();

    @Inject
    public ExportAction(Kendzi3dCore kendzi3dCore, TextureCacheService textureCacheService) {
        super(tr("Export models to files"), null, tr("Export models to files"), null, false);

        this.kendzi3dCore = kendzi3dCore;
        this.textureCacheService = textureCacheService;

    }

    @Override
    public void actionPerformed(ActionEvent pE) {
        export();
    }

    private void export() {

        ExportModelConf conf = new ExportModelConf();
        conf.setFilePattern("/multiText/out.dae");

        ExportUiAction frame = new ExportUiAction();
        frame.setModal(true);
        frame.setVisible(true);
        conf = frame.getExportModelConf();

        if (conf == null) {
            return;
        }

        exportService(conf);
    }

    private void exportService(ExportModelConf conf) {
        // it should be service?

        List<ExportItem> itemsToExport = new ArrayList<ExportItem>();

        List<EditableObject> allObjects = kendzi3dCore.getEditableObjects();

        List<WorldObject> modelList = new ArrayList<WorldObject>();
        for (EditableObject editableList : allObjects) {
            if (editableList instanceof WorldObject) {
                modelList.add((WorldObject) editableList);
            }
        }

        List<ExportItem> el = exportLayer(modelList, conf);

        if (el != null) {
            itemsToExport.addAll(el);
        }

        saveToFiles(itemsToExport, conf);

    }

    private void saveToFiles(List<ExportItem> itemsToExport, ExportModelConf conf) {
        ExportWorker ew = new ExportWorker(itemsToExport, conf, textureCacheService);
        ew.start();
    }

    public List<ExportItem> exportLayer(List<WorldObject> modelList, ExportModelConf conf) {

        List<ExportItem> ret = new ArrayList<ExportItem>();

        for (WorldObject model : modelList) {
            if (model instanceof ExportModel) {
                ExportModel em = (ExportModel) model;
                List<ExportItem> e = em.export(conf);
                if (e != null) {
                    ret.addAll(e);
                }
            }
        }
        return ret;
    }

    @Override
    protected void updateEnabledState() {
        // setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }

}
