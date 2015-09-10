package kendzi.josm.kendzi3d.ui;

import javax.inject.Inject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import kendzi.jogl.camera.SimpleMoveAnimator;
import kendzi.jogl.camera.Viewport;
import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.compas.CompassDrawer;
import kendzi.josm.kendzi3d.jogl.model.DrawableModel;
import kendzi.josm.kendzi3d.jogl.model.WorldObjectDebugDrawable;
import kendzi.josm.kendzi3d.jogl.model.ground.SelectableGround;
import kendzi.josm.kendzi3d.jogl.skybox.SkyBoxDrawer;
import kendzi.josm.kendzi3d.objects.drawer.StaticModelWorldObjectDrawer;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.drawer.HighlightDrawer;
import kendzi.kendzi3d.editor.ui.BaseEditorGLEventListener;
import kendzi.kendzi3d.world.StaticModelWorldObject;
import kendzi3d.light.render.LightRender;

public class Kendzi3dGLEventListener extends BaseEditorGLEventListener {

    /**
     * Light render.
     */
    @Inject
    private LightRender lightRender;

    /**
     * Drawer for skybox.
     */
    @Inject
    private SkyBoxDrawer skyBox;

    /**
     * Model renderer.
     */
    @Inject
    private ModelRender modelRender;

    /**
     * XXX Viewprot should be used. Need to be refactored.
     */
    @Inject
    private SimpleMoveAnimator camera;

    /**
     * Axis labels.
     */
    private final AxisLabels axisLabels = new AxisLabels();;

    /**
     * Drawer for tiles floor.
     */
    private final TilesSurface floor = new TilesSurface();

    /**
     * Drawer for compass.
     */
    private final CompassDrawer compass = new CompassDrawer();

    /**
     * Ground.
     */
    @Inject
    private SelectableGround ground;

    /**
     * Only for single thread!
     */
    private final DrawableModelDrawer highlightModelDrawer = new DrawableModelDrawer();

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        GL2 gl = drawable.getGL().getGL2();

        axisLabels.init();
        compass.init(gl);
        lightRender.init(gl);
    }

    @Override
    protected void drawBeforeSetCamera(GL2 gl, Viewport viewport) {
        lightRender.draw(gl);
    }

    @Override
    protected void drawBeforeEditorObjects(GL2 gl, Viewport viewport2) {

        modelRender.resetMaterials();
        ModelRender.setDefaultMaterial(gl);

        skyBox.draw(gl, viewport2.getPosition());

        ground.draw(gl, viewport2.getPosition());

        if (modelRender.isDebugging()) {

            axisLabels.draw(gl);

            floor.draw(gl);

            // drawTextInfo(gl, simpleMoveAnimator.info());
        }

        compass.drawAtLeftBottom(gl, viewport2);
    }

    /**
     * Check if all required openGl extensions are available.
     * 
     * @param gl
     *            gl
     * @return if there is no required extension
     */
    @Override
    protected boolean checkRequiredExtensions(GL2 gl) {

        if (!gl.isExtensionAvailable("GL_ARB_multitexture")) {

            /*
             * Check if the extension ARB_multitexture is supported by the
             * Graphic card
             */
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    JOptionPane.showMessageDialog(null,
                            "GL_ARB_multitexture OpenGL extension is not supported. Install correct graphic drivers!",
                            "Extension not supported", JOptionPane.ERROR_MESSAGE);
                }
            });

            return false;
        }

        return true;
    }

    @Override
    protected void drawEditorObject(GL2 gl, EditableObject editableObject, Viewport viewport) {

        if (editableObject instanceof StaticModelWorldObject) {
            staticModelWorldObjectDrawer.draw(gl, (StaticModelWorldObject) editableObject, false);

        } else if (editableObject instanceof DrawableModel) {
            DrawableModel drawable = (DrawableModel) editableObject;
            // XXX Viewprot should be used. Need to be refactored.
            drawable.draw(gl, camera);
        }
        if (editableObject instanceof WorldObjectDebugDrawable) {
            WorldObjectDebugDrawable drawable = (WorldObjectDebugDrawable) editableObject;
            drawable.drawDebug(gl, camera);
        }
    }

    @Inject
    private StaticModelWorldObjectDrawer staticModelWorldObjectDrawer;

    @Override
    protected void drawHighlightEditorObject(GL2 gl, Object editableObject) {

        if (editableObject instanceof StaticModelWorldObject) {
            staticModelWorldObjectDrawer.draw(gl, (StaticModelWorldObject) editableObject, true);

        } else if (editableObject instanceof DrawableModel) {

            highlightModelDrawer.set((DrawableModel) editableObject, camera);

            HighlightDrawer.drawHighlight(highlightModelDrawer, gl);
        }
    }

}