package kendzi.kendzi3d.models.library.ui.action;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import kendzi.kendzi3d.models.library.ui.LocalModelsDict;
import kendzi.kendzi3d.resource.inter.ResourceService;

import org.apache.log4j.Logger;

public class LocalModelsDictAction extends LocalModelsDict {

    /** Log. */
    private static final Logger log = Logger.getLogger(LocalModelsDictAction.class);

    private ResourceService urlReciverService;

    private String model;

    private List<String> findLocalModels() {

        String pluginDirStr = this.urlReciverService.getPluginDir();

        File pluginDir = new File(pluginDirStr + "/models");

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".obj");
            }
        };
        log.info("pluginDir: " + pluginDir);
        return findRelativeFilesPath(pluginDir, "/models", fileFilter);
    }

    private List<String> findRelativeFilesPath(File dir, String parent, FileFilter pFileFilter) {

        List<String> ret = new ArrayList<String>();

        File[] files = dir.listFiles(pFileFilter);

        for (File file : files) {
            ret.add(parent + "/" + file.getName());
        }

        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        File[] dirs = dir.listFiles(directoryFilter);
        for (File subdir : dirs) {
            ret.addAll(findRelativeFilesPath(subdir, parent + "/" + subdir.getName(), pFileFilter));
        }
        return ret;
    }

    public void initUi() {
        List<String> models = findLocalModels();
        // this.listModel.removeAllElements();
        //
        // // listModel.setValues(models.toArray(new String[0]));
        // for (String string : models) {
        // this.listModel.addElement(string);
        // }
        // System.out.println(this.listModel.getSize());
        // this.listModels.setModel(listModel);
        ((FileListModel) this.listModel).setValues(models);
    }

    class FileListModel extends AbstractListModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        List<String> values = new ArrayList<String>();

        @Override
        public int getSize() {
            return this.values.size();
        }

        @Override
        public Object getElementAt(int index) {
            return this.values.get(index);
        }

        /**
         * @return the values
         */
        public List<String> getValues() {
            return this.values;
        }

        /**
         * @param values
         *            the values to set
         */
        public void setValues(List<String> values) {
            this.values = values;

            fireContentsChanged(this, 0, values.size());

            // fireIntervalRemoved(this, 0, index1);
        }
    }

    /**
     * @param urlReciverService
     *            the urlReciverService to set
     */
    public void setUrlReciverService(ResourceService urlReciverService) {
        this.urlReciverService = urlReciverService;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        ResourceService urlReciverService = new ResourceService() {

            @Override
            public URL receivePluginDirUrl(String pFileName) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getPluginDir() {
                return ".";
            }

            @Override
            public URL resourceToUrl(String resourceName) {
                // TODO Auto-generated method stub
                return null;
            }

        };

        try {
            LocalModelsDictAction dialog = new LocalModelsDictAction();

            dialog.setUrlReciverService(urlReciverService);

            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            dialog.initUi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see kendzi.josm.kendzi3d.ui.pointModel.LocalModelsDict#getListModel()
     */
    @Override
    protected AbstractListModel getListModel() {
        return new FileListModel();
    }

    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.ui.pointModel.LocalModelsDict#selectValue()
     */
    @Override
    protected boolean selectValueAndClose() {
        String val = (String) this.listModels.getSelectedValue();
        if (val == null) {
            JOptionPane.showMessageDialog(null,
                    "Error row is not selected!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        this.model = val;
        return true;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return this.model;
    }


}
