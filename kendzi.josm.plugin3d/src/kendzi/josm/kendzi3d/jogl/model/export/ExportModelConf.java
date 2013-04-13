package kendzi.josm.kendzi3d.jogl.model.export;


public class ExportModelConf {

    private Object exportType;

    private String filePattern;

    private int numOfModels;

    /**
     * @return the exportType
     */
    public Object getExportType() {
        return exportType;
    }

    /**
     * @param exportType the exportType to set
     */
    public void setExportType(Object exportType) {
        this.exportType = exportType;
    }

    /**
     * @return the filePattern
     */
    public String getFilePattern() {
        return filePattern;
    }

    /**
     * @param filePattern the filePattern to set
     */
    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    /**
     * @return the numOfModels
     */
    public int getNumOfModels() {
        return numOfModels;
    }

    /**
     * @param numOfModels the numOfModels to set
     */
    public void setNumOfModels(int numOfModels) {
        this.numOfModels = numOfModels;
    }

}
