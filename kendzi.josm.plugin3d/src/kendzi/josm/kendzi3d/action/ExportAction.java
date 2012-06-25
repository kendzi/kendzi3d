/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.josm.kendzi3d.action;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.layer.Layer;
import kendzi.josm.kendzi3d.jogl.model.Model;
import kendzi.josm.kendzi3d.jogl.model.export.ExportItem;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModel;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModelConf;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.actions.JosmAction;

import com.google.inject.Inject;

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
     * Rendering service.
     */
    private RenderJOSM renderJOSM;


    final JFileChooser fc = new JFileChooser();


    @Inject
    public ExportAction(RenderJOSM pRenderJOSM) {
        super(
                tr("Export models to files"),
                null,
                tr("Export models to files"),
                null,
                false
        );

        this.renderJOSM = pRenderJOSM;
    }

//    /**
//     *
//     */
//    public void loadTextureLibraryFromFile() {
//        List<String> errors = null;
//        String timestamp = null;
//        try {
//            int returnVal = this.fc.showOpenDialog(null);
//
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                File file = this.fc.getSelectedFile();
//                //This is where a real application would open the file.
//                log.info("Opening: " + file.getName());
//
//                UrlTextureLibrary urlTextureLibrary = new UrlTextureLibrary();
//                urlTextureLibrary.setUrl(file.toURI().toURL());
//
//                boolean overwrite = showOverwriteDialog();
//
//                urlTextureLibrary.setOverwrite(overwrite);
//
//
//                this.textureLibraryService.loadUserFile(urlTextureLibrary);
//            } else {
//                log.info("Open command cancelled by user." );
//            }
//
//////            LoadRet load = this.textureLibraryService.load();
////            errors = load.getErrors();
////            timestamp = load.getTimestamp();
//
//        } catch (MalformedURLException e) {
//            log.error(e, e);
//            showError(e);
//        } catch (IOException e) {
//            log.error(e, e);
//            showError(e);
//        } catch (JAXBException e) {
//            log.error(e, e);
//            showError(e);
//        }


//    }





    @Override
    public void actionPerformed(ActionEvent pE) {
        export();
    }

    private void export() {
        // UI code

        exportService(null);
    }



    private void exportService(ExportModelConf conf) {
        // it should be service?

        List<ExportItem> itemsToExport = new ArrayList<ExportItem>();
        for (Layer layer : this.renderJOSM.getLayerList()) {

            List<Model> modelList = layer.getModels();

            List<ExportItem> el = exportLayer(modelList, conf);

            if (el != null) {
                itemsToExport.addAll(el);
            }
        }

        saveToFiles(itemsToExport, conf);

    }


    private void saveToFiles(List<ExportItem> itemsToExport, ExportModelConf conf) {
        // Tu trzeba by cos zrobic...
        for(ExportItem ei : itemsToExport) {
            // if (conf.type == "obj") {
            //     saveToObjFile(ei);
            // }
        }
    }

    public List<ExportItem> exportLayer(List<Model> modelList, ExportModelConf conf) {

        List<ExportItem> ret = new ArrayList<ExportItem>();

        for(Model model : modelList) {
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
//        setEnabled(Main.map != null && Main.main.getEditLayer() != null);
    }

    public static int showNegativeConfirmDialog(Component parentComponent, Object message, String title) {
        List<Object> options = new ArrayList<Object>();
        Object defaultOption;

        options.add(UIManager.getString("OptionPane.yesButtonText"));
        options.add(UIManager.getString("OptionPane.noButtonText"));
        defaultOption = UIManager.getString("OptionPane.noButtonText");

        return JOptionPane.showOptionDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options.toArray(), defaultOption);
    }

}
