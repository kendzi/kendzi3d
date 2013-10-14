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
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, this.lightPos, 0);


        //        // Clear the drawing area
        //        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        //
        //
        //
        //
        //        // Reset the current matrix to the "identity"
        //        gl.glLoadIdentity();
        //
        //        gl.glMatrixMode(GL2.GL_MODELVIEW);
        //        gl.glLoadIdentity();


        gl.glClearColor( 0,0,0, 0.0f );
        // clear colour and depth buffers
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        //      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // dla przezroczystosci tla
        gl.glEnable(GL.GL_BLEND);
        //        gl.glBlendFunc(GL2.GL_DST_ALPHA, GL2.GL_ONE_MINUS_DST_ALPHA);
        //        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl. glBlendFunc(GL.GL_ONE, GL.GL_ZERO);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();

        //
        setCamera(glu, camraCenter, cameraAngleX, cameraAngleY);


        gl.glEnable(GL.GL_MULTISAMPLE);


        //        this.ground.draw(gl, this.simpleMoveAnimator, this.renderJosm.getPerspective());

        if (this.modelRender.isDebugging()) {

            this.axisLabels.draw(gl);

            drawFloor(gl);

            // drawTextInfo(gl, this.simpleMoveAnimator.info());
        }

        this.renderJosm.draw(gl, this.simpleMoveAnimator);






        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }





    public void dispose(GLAutoDrawable pDrawable) {
        //
    }

    public void init(GLAutoDrawable pDrawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL2 gl = pDrawable.getGL().getGL2();

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glClearDepth(1.0);
        // sky blue colour
        //        gl.glClearColor(0.17f, 0.65f, 0.92f, 0.0f);
        gl.glClearColor(0f, 0f, 0f, 0.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        int[] depth_bits = new int[1];
        gl.glGetIntegerv(GL.GL_DEPTH_BITS, depth_bits, 0);

        gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        addLight(gl);

        float[] grayCol = { 0.8f, 0.8f, 0.8f, 1.0f };
        // float[] blueCol = {0.0f, 0.0f, 0.8f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, grayCol, 0);

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

        gl.glViewport(0, 0, width, height); // size of drawing area

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        //        glu.gluPerspective(45.0, (float)width / (float) height, 1.0, 1500.0); // 5


        double areaHeigth =  leftTopPoint.y - rightBottomPoint.y;
        double areaWidth = rightBottomPoint.x - leftTopPoint.x;

        //        this.cameraAngleX = Math.toRadians(0);
        //        this.cameraAngleY = Math.toRadians(-35);

        double ox = Math.cos(cameraAngleX) * areaWidth / 2d;
        double oy = Math.cos(cameraAngleY) * areaHeigth / 2d;

        double oz = Math.max(ox, oy);

        gl.glOrtho(- ox, ox , -oy, oy, -oz - 25, oz + 600);

        //        gl.glOrtho(- areaWidth/2, areaWidth/2 , -areaHeigth/2, areaHeigth/2, -10, 100);

    }



    /**
     * Set up a point source with ambient, diffuse, and specular colour.
     * components
     * @param pGl gl
     */
    private void addLight(GL2 pGl) {

        pGl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        // enable a single light source
        pGl.glEnable(GLLightingFunc.GL_LIGHTING);
        pGl.glEnable(GLLightingFunc.GL_LIGHT0);


        float gray = 0.5f;
        float[] grayLight = {gray, gray, gray, 1.0f }; // weak gray ambient
        pGl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, grayLight, 0);

        float[] whiteLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // bright white diffuse
        // & specular
        pGl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, whiteLight, 0);
        pGl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, whiteLight, 0);

        //      float lightPos[] = { 1.0f, 1.0f, 1.0f, 0.0f }; // top right front
        float [] lightPos = { 0.0f, 2.0f, 2.0f, 1.0f };
        // _direction_
        pGl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos, 0);


        //        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);

        //        float [] lmodel_ambient = { 1f, 1f, 1f, 1.0f };
        //        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
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
        gl.glDisable(GLLightingFunc.GL_LIGHTING);

        //blue
        gl.glColor3f(0.0f, 0.1f, 0.4f);
        DrawUtil.drawTiles(gl, 50, true);
        // green
        gl.glColor3f(0.0f, 0.5f, 0.1f);
        DrawUtil.drawTiles(gl, 50, false);

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
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
