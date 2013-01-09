package kendzi.kendzi3d.render.command;

import kendzi.kendzi3d.render.conf.RenderDataSourceConf.InputSource;

import com.lexicalscope.jewel.cli.Option;

public interface CliRenderOptions {

    /**
     * Pattern for resolution input argument.
     */
    public static final String RES_PATTERN = "([0-9]{1,9}),([0-9]{1,9})";

    public static final String DOUBLE = "[-]?\\d+(?:\\.\\d+)";

    public static final String BBOX_PATTERN = "(" + DOUBLE + "),(" + DOUBLE + "),(" + DOUBLE + "),(" + DOUBLE + ")";

    /**
     * Pattern for input source argument.
     */
    public static final String INPUT_PATTERN = "(FILE) | (PGSQL)";


    @Option(longName="input.source", defaultValue="FILE")
    InputSource getInputSource();

    @Option(longName="file.url")
    String getFileUrl();
    boolean isFileUrl();

    @Option(longName="db.username")
    String getDbUsername();
    boolean isDbUsername();

    @Option(longName="db.password")
    String getDbPassword();
    boolean isDbPassword();

    @Option(longName="db.url")
    String getDbUrl();
    boolean isDbUrl();

    @Option(longName="camera.angle.x", defaultValue="0")
    Double getCameraAngleX();
    boolean isCameraAngleX();

    @Option(longName="camera.angle.y", defaultValue="-30")
    Double getCameraAngleY();
    boolean isCameraAngleY();

    @Option(description="lat1,lon1,lat2,lon2",
            longName="oview.bbox", pattern=BBOX_PATTERN)
    String getOviewBbox();
    boolean isOviewBbox();

    @Option(longName="oview.tile")
    String getOviewTile();
    boolean isOviewTile();

    @Option(longName="output")
    String getOutput();

    @Option(description="output image size in pixels",
            longName="resolution", defaultValue="256,256", pattern=RES_PATTERN)
    String getResolution();

    @Option(helpRequest = true)
    boolean getHelp();
}
