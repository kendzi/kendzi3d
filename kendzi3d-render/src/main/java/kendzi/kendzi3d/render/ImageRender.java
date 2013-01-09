package kendzi.kendzi3d.render;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;
import javax.vecmath.Point2d;

import jogamp.opengl.Debug;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.dataset.DataSetProvider;
import kendzi.kendzi3d.render.listener.Kendzi3dTitleGLEventListener;
import kendzi.kendzi3d.render.tile.Tile;
import kendzi.kendzi3d.render.tile.TitleToLatLon;
import kendzi.kendzi3d.render.tile.TitleToLatLon.BoundingBox;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;

import com.google.inject.Inject;
import com.jogamp.opengl.util.awt.Screenshot;

public class ImageRender {

    /** Log. */
    private static final Logger log = Logger.getLogger(ImageRender.class);

    @Inject
    Kendzi3dTitleGLEventListener ff;

    @Inject
    RenderEngineConf conf;

    @Inject
    DataSetProvider dataSetProvider;

    GLContext context = null;

    GLPbuffer buf = null;


    private static void initJOSMMinimal() {
        Main.pref = new Preferences();
        org.openstreetmap.josm.gui.preferences.projection.ProjectionPreference.setProjection();
    }

    public void init() {

        initJOSMMinimal();

        if (this.conf == null) {
            throw new RuntimeException("error configuration is not setup");
        }



        int width = 256;
        int height = 256;


        log.info("is set debug for GraphicsConfiguration: " + Debug.debug("GraphicsConfiguration"));

        final GLProfile gl2Profile = GLProfile.get(GLProfile.GL2);

        GLDrawableFactory factory = GLDrawableFactory.getFactory(gl2Profile );

        GLCapabilities glCap = new GLCapabilities(gl2Profile);

        // Without line below, there is an error on Windows.
        glCap.setDoubleBuffered(false);

        GLCapabilities capabilities = glCap;

        capabilities.setDepthBits(16);

        capabilities.setSampleBuffers(true);

        capabilities.setNumSamples(2);

        capabilities.setAlphaBits(1);
        //            capabilities.setAccumAlphaBits(8);

        log.info("GLCapabilities: " + capabilities);

        //makes a new buffer
        this.buf = factory.createGLPbuffer(null, glCap, null, width, height, null);

        //save size for later use in getting image

        //required for drawing to the buffer
        //            context =  buf.createContext(null);
        this.context = this.buf.getContext();

        //Disable the using of OpenGL or at least by way of a Pbuffer
        this.context.makeCurrent();

        this.ff.init(this.buf);
    }

    public RenderResult render(Tile t) {
        try {
            BoundingBox camraBoundingBox = TitleToLatLon.tile2boundingBox(t.getX(),  t.getY(), t.getZ());

            LatLon leftTop = new LatLon(camraBoundingBox.north, camraBoundingBox.west);
            LatLon rightBottom = new LatLon(camraBoundingBox.south, camraBoundingBox.east);

            DataSet dataSet = this.dataSetProvider.findData(leftTop, rightBottom);

            return render(dataSet, leftTop, rightBottom);

        } catch (Exception e) {
            throw new RuntimeException("error rendering Tile: " + t, e);
        }
    }

    public RenderResult render(LatLon leftTop, LatLon rightBottom) {
        try {

            DataSet dataSet = this.dataSetProvider.findData(leftTop, rightBottom);

            return render(dataSet, leftTop, rightBottom);

        } catch (Exception e) {
            throw new RuntimeException("error rendering bbox: " + leftTop + ", " + rightBottom, e);
        }
    }

    private RenderResult render(DataSet dataSet, LatLon leftTop, LatLon rightBottom) {

        try {

            this.ff.getRenderJosm().processDatasetEvent(new DataChangedEvent(dataSet));

            byte [] tile = generateTile(
                    leftTop,
                    rightBottom,
                    this.buf,
                    this.ff,
                    this.conf.getWidth(),
                    this.conf.getHeight(),
                    this.conf.getCameraAngleX(),
                    this.conf.getCameraAngleY()
                    //                    fileName
                    );


            return new RenderResult(tile);

        } catch (Exception e) {
            throw new RuntimeException("error rendering", e);
        }
    }



    /**
     * @param fileName
     * @param tile
     */
    public void saveFile(String fileName, byte [] tile) {
        try {



            File outFile = new File(fileName);

            File parentDir = outFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            ByteArrayInputStream in = new ByteArrayInputStream(tile);
            FileOutputStream out  = new FileOutputStream(outFile);

            int count;
            byte[] buffer = new byte[8192];
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();

            //            ImageIO.write(tile, "png", outFile);

            log.info("Generaded: " + fileName);
        } catch (IOException e) {
            log.error("Error writing to file: " + fileName, e);
        }
    }


    public static byte[] generateTile(LatLon leftTop, LatLon rightBottom, GLPbuffer buf,
            Kendzi3dTitleGLEventListener ff, int width, int height,
            double cameraAngleX, double cameraAngleY/*, String fileName*/ ) {

        Perspective3D perspective = ff.getRenderJosm().getPerspective();

        Point2d leftTopPoint = perspective.calcPoint(Main.getProjection().latlon2eastNorth(leftTop));
        Point2d rightBottomPoint = perspective.calcPoint(Main.getProjection().latlon2eastNorth(rightBottom));

        Point2d center = new Point2d(leftTopPoint);
        center.add(rightBottomPoint);
        center.scale(0.5);

        ff.setupProjection(
                buf,
                width,
                height,
                leftTopPoint,
                rightBottomPoint,
                cameraAngleX,
                cameraAngleY
                );

        //        ff.setCamraCenter(center);
        ff.display(
                buf,
                center,
                cameraAngleX,
                cameraAngleY);
        ff.dispose(buf);

        BufferedImage bufferedImage = Screenshot.readToBufferedImage(width, height, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error writing image to byte array", e);
        }
        return null;
    }

    public void release() {
        this.context.release();
        if (this.buf != null) {
            this.context.destroy();
            this.buf.destroy();
        }
    }




}

