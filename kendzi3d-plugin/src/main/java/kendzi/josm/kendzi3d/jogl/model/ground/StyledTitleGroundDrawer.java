/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.ground;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Point3d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.visitor.paint.PaintColors;
import org.openstreetmap.josm.data.osm.visitor.paint.StyledMapRenderer;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.NavigatableComponent;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;

import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheService;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.josm.kendzi3d.data.perspective.Perspective3D;
import kendzi.josm.kendzi3d.data.perspective.Perspective3dProvider;

public class StyledTitleGroundDrawer extends GroundDrawer {

    /** Log. */
    private static final Logger log = Logger.getLogger(StyledTitleGroundDrawer.class);

    /**
     * Title size in meters.
     */
    private static final double TITLE_LENGTH = 500;

    /**
     * Half size of title.
     */
    private static final double TITLE_HALF_LENGTH = TITLE_LENGTH / 2d;

    /**
     * Title image size in pixels.
     */
    private static final int TITLE_IMAGE_SIZE = 512;

    /**
     * Total title generation time.
     */
    private long totalTitleGenerateTime;

    /**
     * Total count of frames.
     */
    private int totalTitleCount = 0;

    /**
     * How many titles was created per frame.
     */
    private int titleFrameCount;

    /**
     * Provider for perspective.
     */
    private final Perspective3dProvider perspective3dProvider;

    /**
     * Constructor.
     *
     * @param textureCacheService
     *            texture cache service
     * @param perspective3dProvider
     *            provider for current perspective
     */
    public StyledTitleGroundDrawer(TextureCacheService textureCacheService, Perspective3dProvider perspective3dProvider) {
        super(textureCacheService, null);
        this.perspective3dProvider = perspective3dProvider;
    }

    @Override
    public void draw(GL2 gl, Point3d cameraPosition, ModelRender modelRender) {

        Perspective3D perspective = perspective3dProvider.getPerspective3d();
        if (perspective == null) {
            return;
        }

        EastNorth en = perspective.toEastNorth(cameraPosition.x, -cameraPosition.z);

        double east = en.east();
        double north = en.north();

        int e = (int) Math.round(east / TITLE_LENGTH);
        int n = (int) Math.round(north / TITLE_LENGTH);

        titleFrameCount = 0;

        int titlesRows = 2;
        for (int ie = -titlesRows; ie <= titlesRows; ie++) {
            for (int in = -titlesRows; in <= titlesRows; in++) {
                drawTitle(gl, e + ie, n + in, perspective, modelRender);
            }
        }
    }

    /**
     * Draws title.
     *
     * @param gl
     *            gl
     * @param e
     *            east title key
     * @param n
     *            north title key
     * @param perspective3d
     *            perspective
     */
    private void drawTitle(GL2 gl, int e, int n, Perspective3D perspective3d, ModelRender modelRender) {

        double xCenter = e * TITLE_LENGTH;
        double yCenter = n * TITLE_LENGTH;

        double x1 = perspective3d.calcX(xCenter - TITLE_HALF_LENGTH);
        double z1 = -perspective3d.calcY(yCenter - TITLE_HALF_LENGTH);

        double x2 = perspective3d.calcX(xCenter + TITLE_HALF_LENGTH);
        double z2 = -perspective3d.calcY(yCenter - TITLE_HALF_LENGTH);

        double x3 = perspective3d.calcX(xCenter + TITLE_HALF_LENGTH);
        double z3 = -perspective3d.calcY(yCenter + TITLE_HALF_LENGTH);

        double x4 = perspective3d.calcX(xCenter - TITLE_HALF_LENGTH);
        double z4 = -perspective3d.calcY(yCenter + TITLE_HALF_LENGTH);

        String textName = "g_" + e + "_" + n;

        Texture texture = null;

        if (!textureCacheService.isTexture(textName)) {
            if (titleFrameCount == 0) {
                // if title don't exist we create it
                // but only one tile per frame

                titleFrameCount++;

                long t1 = System.currentTimeMillis();

                TextureRenderer bi = generateTitle(e * TITLE_LENGTH - TITLE_HALF_LENGTH, n * TITLE_LENGTH - TITLE_HALF_LENGTH);

                totalTitleCount++;

                texture = bi.getTexture();

                // FIXME
                ((TextureCacheServiceImpl) textureCacheService).setTexture(textName, texture);

                long t = System.currentTimeMillis() - t1;

                totalTitleGenerateTime += t;

                if (log.isInfoEnabled()) {
                    log.info("gen title: " + textName + " title generated: " + totalTitleCount + " gen time: " + t
                            + " [ms] total title generate time: " + totalTitleGenerateTime / 1000d);
                }

            } else {
                texture = textureCacheService.getTexture(gl, TextureCacheService.TEXTURES_UNDEFINED_PNG);
            }

        } else {
            texture = textureCacheService.getTexture(gl, textName);
        }

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GL.GL_TEXTURE_2D);

        texture.enable(gl);
        texture.bind(gl);

        gl.glBegin(GL2ES3.GL_QUADS);
        gl.glNormal3d(0d, modelRender.isDoubleSided()?-1d:1d, 0d);

        double h = -0.1d;

        gl.glTexCoord2d(0, 1);
        gl.glVertex3d(x1, h, z1);
        gl.glTexCoord2d(1, 1);
        gl.glVertex3d(x2, h, z2);
        gl.glTexCoord2d(1, 0);
        gl.glVertex3d(x3, h, z3);
        gl.glTexCoord2d(0, 0);
        gl.glVertex3d(x4, h, z4);
        gl.glEnd();

        texture.disable(gl);
    }

    /**
     * Generate title.
     *
     * @param e
     *            east title center
     * @param n
     *            north title center
     * @return title texture/image
     */
    private TextureRenderer generateTitle(double e, double n) {

        int size = TITLE_IMAGE_SIZE;

        double scale = TITLE_LENGTH / size;

        TextureRenderer img = new TextureRenderer(size, size, true, true);

        Graphics2D g = img.createGraphics();

        g.setClip(0, 0, size, size);

        Color backgroundColor = PaintColors.getBackgroundColor();

        g.setColor(backgroundColor);

        g.fillRect(0, 0, size, size);
        NavigatableComponent nc = new NavigatableComponent();

        nc.setSize(size, size);

        EastNorth newCenter = new EastNorth(e + TITLE_HALF_LENGTH, n + TITLE_HALF_LENGTH);

        nc.zoomTo(newCenter, scale);
        StyledMapRenderer sr = new StyledMapRenderer(g, nc, false);

        DataSet ds = MainApplication.getLayerManager().getEditDataSet();

        LatLon min = nc.getLatLon(0, 0);
        LatLon max = nc.getLatLon(size, size);

        Bounds bounds = new Bounds(min, max);
        if (ds == null) {
            System.err.println("no data !!!");
        } else {
            sr.render(ds, true, bounds);
        }

        g.dispose();

        return img;
    }
}
