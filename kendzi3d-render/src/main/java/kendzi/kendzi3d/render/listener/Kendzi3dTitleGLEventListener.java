/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.render.listener;

import javax.inject.Inject;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.DrawUtil;
import kendzi.jogl.util.DrawUtil;
import kendzi.jogl.model.render.ModelRender;
import kendzi.jogl.texture.TextureCacheServiceImpl;
import kendzi.josm.kendzi3d.jogl.RenderJOSM;
import kendzi.josm.kendzi3d.jogl.model.ground.Ground;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.CameraMoveListener;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.ui.SimpleMoveAnimator;
import kendzi.josm.kendzi3d.service.TextureLibraryService;
import kendzi.josm.kendzi3d.ui.debug.AxisLabels;
import kendzi.math.geometry.point.PointUtil;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Draws 3d.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class Kendzi3dTitleGLEventListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(Kendzi3dTitleGLEventListener.class);


    /**
     * Position of sun. XXX
     */
    private float [] lightPos = new float[] { 0.0f, 1.0f, 1.0f, 0f };


    /**
     * Animator of camera movement.
     */
    private SimpleMoveAnimator simpleMoveAnimator;

    /**
     * Key and mouse listener for camera movement.
     */
    private CameraMoveListener cameraMoveListener;

    /**
     * Axis labels.
     */
    private AxisLabels axisLabels;

    /**
     * Model renderer.
     */
    @Inject
    private ModelRender modelRender;

    /**
     * Temporary. Renderer of josm opengl object.
     */
    @Inject
    private RenderJOSM renderJosm;

    /**
     * Ground.
     */
    private Ground ground;

    /**
     * Texture cache service.
     */
    @Inject
    private TextureCacheServiceImpl textureCacheService;

    /**
     * Texture library service.
     */
    @Inject
    private TextureLibraryService textureLibraryService;


    /**
     * Default constructor.
     */
    public Kendzi3dTitleGLEventListener() {


        this.ground  = new Ground(this.textureCacheService, this.textureLibraryService);

        this.axisLabels = new AxisLabels();

        this.simpleMoveAnimator = new SimpleMoveAnimator();

    }

    //    Point2d camraCenter;
    //    Point2d leftTopPoint;
    //    Point2d rightBottomPoint;


    //    private double cameraAngleX;
    //
    //
    //    private double cameraAngleY;


    public void display(
            GLAutoDrawable pDrawable,
            Point2d camraCenter,
            double cameraAngleX,
            double cameraAngleY) {

        GL2 gl = pDrawable.getGL().getGL2();
        // System.err.println("INIT GL IS: " + gl.getClass().getName());

        GLU glu = new GLU();

        // _direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, this.lightPos);


        //        // Clear the drawing area
        //        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //
        //
        //
        //
        //        // Reset the current matrix to the "identity"
        //        GL11.glLoadIdentity();
        //
        //        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        //        GL11.glLoadIdentity();


        GL11.glClearColor( 0,0,0, 0.0f );
        // clear colour and depth buffers
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // dla przezroczystosci tla
        GL11.glEnable(GL11.GL_BLEND);
        //        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
        //        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11. glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        //
        setCamera(glu, camraCenter, cameraAngleX, cameraAngleY);


        GL11.glEnable(GL13.GL_MULTISAMPLE);


        //        this.ground.draw(gl, this.simpleMoveAnimator, this.renderJosm.getPerspective());

        if (this.modelRender.isDebugging()) {

            this.axisLabels.draw(gl);

            drawFloor(gl);

            // drawTextInfo(gl, this.simpleMoveAnimator.info());
        }

        this.renderJosm.draw(gl, this.simpleMoveAnimator);






        // Flush all drawing operations to the graphics card
        GL11.glFlush();
    }





    public void dispose(GLAutoDrawable pDrawable) {
        //
    }

    public void init(GLAutoDrawable pDrawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL2 gl = pDrawable.getGL().getGL2();

        // Setup the drawing area and shading mode
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glClearDepth(1.0);
        // sky blue colour
        //        GL11.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);
        GL11.glClearColor(0f, 0f, 0f, 0.0f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        GL11.glGetIntegerv(GL11.GL_DEPTH_BITS, depth_bits);

        GL11.glShadeModel(GL11.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        addLight(gl);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE, grayCol);

        RenderJOSM.lod1 = Double.MAX_VALUE;
    }

    public void setupProjection(
            GLAutoDrawable drawable,
            int width,
            int height,
            Point2d leftTopPoint,
            Point2d rightBottomPoint,
            double cameraAngleX,
            double cameraAngleY

            ) {

        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!
            height = 1;
        }

        final float h = (float) width / (float) height;

        GL11.glViewport(0, 0, width, height); // size of drawing area

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        //        glu.gluPerspective(45.0, (float)width / (float) height, 1.0, 1500.0); // 5


        double areaHeight =  leftTopPoint.y - rightBottomPoint.y;
        double areaWidth = rightBottomPoint.x - leftTopPoint.x;

        //        this.cameraAngleX = Math.toRadians(0);
        //        this.cameraAngleY = Math.toRadians(-35);

        double ox = Math.cos(cameraAngleX) * areaWidth / 2d;
        double oy = Math.cos(cameraAngleY) * areaHeight / 2d;

        double oz = Math.max(ox, oy);

        GL11.glOrtho(- ox, ox , -oy, oy, -oz - 25, oz + 600);

        //        GL11.glOrtho(- areaWidth/2, areaWidth/2 , -areaHeight/2, areaHeight/2, -10, 100);

    }



    /**
     * Set up a point source with ambient, diffuse, and specular colour.
     * components
     * @param pGl gl
     */
    private void addLight(GL2 pGl) {

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        // enable a single light source
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);


        float gray = 0.5f;
        float[] grayLight = {gray, gray, gray, 1.0f }; // weak gray ambient
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, grayLight);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, whiteLight);

        //      float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPos);


        //        GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

        //        float [] lmodel_ambient = { 1f, 1f, 1f, 1.0f };
        //        GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
    }




    /**
     * Sets camera position and rotation.
     * @param pGlu GLU
     * @param camraCenter
     * @param cameraAngleY
     * @param cameraAngleX
     */
    private void setCamera(GLU pGlu, Point2d camraCenter, double cameraAngleX, double cameraAngleY) {

        //        Point3d pos = this.simpleMoveAnimator.getPoint();
        //        Vector3d rotate = this.simpleMoveAnimator.getAngle();

        Vector3d posLookAt = new Vector3d(0, 400, 0);
        Vector3d camVector= new Vector3d(0, 0, -1);

        posLookAt = PointUtil.rotateZ3d(posLookAt, -cameraAngleX);
        posLookAt = PointUtil.rotateX3d(posLookAt, -cameraAngleY);

        camVector = PointUtil.rotateZ3d(camVector, -cameraAngleX);
        camVector = PointUtil.rotateX3d(camVector, -cameraAngleY);
        //        posLookAt = PointUtil.rotateX3d(posLookAt, rotate.x);

        posLookAt.x += camraCenter.x;
        posLookAt.z -= camraCenter.y;

        //        pGlu.gluLookAt(pos.getX(), pos.getY(), pos.getZ(),
        //                posLookAt.getX(), posLookAt.getY(), posLookAt.getZ(),
        //                0, 1, 0);

        //        double distance = 50;


        //        pGlu.gluLookAt(0, 50, 25,
        pGlu.gluLookAt(posLookAt.x, posLookAt.y, posLookAt.z,
                camraCenter.x, 0d, -camraCenter.y,
                camVector.x, camVector.y, camVector.z);
        //        pGlu.gluLookAt(camraCenter.x, 50d, -camraCenter.y,
        //        		camraCenter.x, 0d, -camraCenter.y,
        //        		0d, 1d, 0d);
    }


    /**
     * @return the renderJosm
     */
    public RenderJOSM getRenderJosm() {
        return this.renderJosm;
    }





    private void drawFloor(GL2 gl) {
        GL11.glDisable(GL11.GL_LIGHTING);

        //blue
        GL11.glColor3f(0.0f, 0.1f, 0.4f);
        DrawUtil.drawTiles(gl, 50, true);
        // green
        GL11.glColor3f(0.0f, 0.5f, 0.1f);
        DrawUtil.drawTiles(gl, 50, false);

        GL11.glEnable(GL11.GL_LIGHTING);
    }




    /**
     * @return the ground
     */
    public Ground getGround() {
        return this.ground;
    }




    /**
     * @param ground the ground to set
     */
    public void setGround(Ground ground) {
        this.ground = ground;
    }






    //    /**
    //     * @return the camraCenter
    //     */
    //    public Point2d getCamraCenter() {
    //        return this.camraCenter;
    //    }
    //
    //
    //
    //
    //    /**
    //     * @param camraCenter the camraCenter to set
    //     */
    //    public void setCamraCenter(Point2d camraCenter) {
    //        this.camraCenter = camraCenter;
    //    }




    //    /**
    //     * @return the leftTopPoint
    //     */
    //    public Point2d getLeftTopPoint() {
    //        return this.leftTopPoint;
    //    }
    //
    //
    //
    //
    //    /**
    //     * @param leftTopPoint the leftTopPoint to set
    //     */
    //    public void setLeftTopPoint(Point2d leftTopPoint) {
    //        this.leftTopPoint = leftTopPoint;
    //    }
    //
    //
    //
    //
    //    /**
    //     * @return the rightBottomPoint
    //     */
    //    public Point2d getRightBottomPoint() {
    //        return this.rightBottomPoint;
    //    }
    //
    //
    //
    //
    //    /**
    //     * @param rightBottomPoint the rightBottomPoint to set
    //     */
    //    public void setRightBottomPoint(Point2d rightBottomPoint) {
    //        this.rightBottomPoint = rightBottomPoint;
    //    }




    //
    //    /**
    //     * @return the cameraAngleX
    //     */
    //    public double getCameraAngleX() {
    //        return cameraAngleX;
    //    }
    //
    //
    //
    //
    //
    //    /**
    //     * @param cameraAngleX the cameraAngleX to set
    //     */
    //    public void setCameraAngleX(double cameraAngleX) {
    //        this.cameraAngleX = cameraAngleX;
    //    }



    //
    //
    //    /**
    //     * @return the cameraAngleY
    //     */
    //    public double getCameraAngleY() {
    //        return cameraAngleY;
    //    }




    //
    //    /**
    //     * @param cameraAngleY the cameraAngleY to set
    //     */
    //    public void setCameraAngleY(double cameraAngleY) {
    //        this.cameraAngleY = cameraAngleY;
    //    }

}
