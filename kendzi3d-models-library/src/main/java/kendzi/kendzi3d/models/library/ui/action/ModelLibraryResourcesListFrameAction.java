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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import kendzi.kendzi3d.models.library.dao.LibraryResourcesMemoryDao;
import kendzi.kendzi3d.models.library.service.ModelsLibraryService;
import kendzi.kendzi3d.models.library.ui.ModelLibraryResourcesListFrame;
import kendzi.kendzi3d.resource.inter.ResourceService;
import kendzi.util.StringUtil;
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
    private final ModelsLibraryService modelsLibraryService;

    public ModelLibraryResourcesListFrameAction(ModelsLibraryService modelsLibraryService) {
        super();
        this.modelsLibraryService = modelsLibraryService;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ResourceService urlReciverService = new UrlReciverServiceTest();
                ModelsLibraryService pms = new ModelsLibraryService(urlReciverService, new LibraryResourcesMemoryDao());
                pms.init();
                ModelLibraryResourcesListFrameAction frame = new ModelLibraryResourcesListFrameAction(pms);

                frame.loadTableData();
                frame.setVisible(true);
            } catch (Exception e) {
                log.error(e);
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
        NodeModelListFrameAction frame = NodeModelListFrameAction.buildFrame(ModelsLibraryService.GLOBAL, true,
                modelsLibraryService);

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

        NodeModelListFrameAction frame = NodeModelListFrameAction.buildFrame(fileKey, true, modelsLibraryService);

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

    @Override
    protected void onAddResourceFile() {
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ModelLibraryFilter());
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File selectedFile = fc.getSelectedFile();

            if (selectedFile != null) {
                String path = selectedFile.getAbsoluteFile().toURI().toString();

                this.modelsLibraryService.addResourcesPath(path);
                loadTableData();
            }
        }
    }

    @Override
    protected void onAddResourceUrl() {

        String ret = (String) JOptionPane.showInputDialog(this, "Enter url for models library", "Add URL resource",
                JOptionPane.PLAIN_MESSAGE, null, null, "http://foo.com/modelLibrary.xml");

        if (!StringUtil.isBlankOrNull(ret)) {
            URL url;
            try {
                url = new URL(ret);
                this.modelsLibraryService.addResourcesPath(url.toString());
                loadTableData();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ModelLibraryFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            if (f == null) {
                return true;
            }
            if (f.isDirectory()) {
                return true;
            }
            return f.getAbsolutePath().endsWith(".xml");
        }

        @Override
        public String getDescription() {
            return "Model library";
        }
    }

    @Override
    protected void onRemoveResourceFile() {
        String fileName = getSelected();

        modelsLibraryService.removeResourcesPath(fileName);
        loadTableData();
    }

    @Override
    protected void onDefaultResources() {
        modelsLibraryService.setDefaultResourcesPaths();
        loadTableData();
    }
}
