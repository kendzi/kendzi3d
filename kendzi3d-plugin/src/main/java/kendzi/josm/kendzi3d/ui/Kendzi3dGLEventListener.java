package kendzi.josm.kendzi3d.ui;

import com.jogamp.opengl.GLAutoDrawable;

import java.util.Optional;

import javax.inject.Inject;
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
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

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
    private final AxisLabels axisLabels = new AxisLabels();

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

        axisLabels.init();
        compass.init();
        lightRender.init();
    }

    @Override
    protected void drawBeforeSetCamera(Viewport viewport) {
        lightRender.draw();
    }

    @Override
    protected void drawBeforeEditorObjects(Viewport viewport2) {

        modelRender.resetMaterials();
        modelRender.setupDefaultMaterial();

        skyBox.draw(viewport2.getPosition());

        ground.draw(viewport2.getPosition());

        if (modelRender.isDebugging()) {

            axisLabels.draw();

            floor.draw();

            // drawTextInfo(gl, simpleMoveAnimator.info());
        }

        compass.drawAtLeftBottom(viewport2);
    }

    /**
     * Check if all required openGl extensions are available.
     *
     * @param capabilities
     *            The capabilities of the current context
     * @return if there is no required extension
     */
    @Override
    protected boolean checkRequiredExtensions(GLCapabilities capabilities) {
        if (!Optional.ofNullable(capabilities).orElseGet(GL::getCapabilities).GL_ARB_multitexture) {

            /*
             * Check if the extension ARB_multitexture is supported by the Graphic card
             */
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "GL_ARB_multitexture OpenGL extension is not supported. Install correct graphic drivers!",
                    "Extension not supported", JOptionPane.ERROR_MESSAGE));

            return false;
        }

        return true;
    }

    @Override
    protected void drawEditorObject(EditableObject editableObject, Viewport viewport) {

        if (editableObject instanceof StaticModelWorldObject) {
            staticModelWorldObjectDrawer.draw((StaticModelWorldObject) editableObject, false);

        } else if (editableObject instanceof DrawableModel) {
            DrawableModel drawable = (DrawableModel) editableObject;
            // XXX Viewprot should be used. Need to be refactored.
            drawable.draw(camera);
        }
        if (editableObject instanceof WorldObjectDebugDrawable) {
            WorldObjectDebugDrawable drawable = (WorldObjectDebugDrawable) editableObject;
            drawable.drawDebug(camera);
        }
    }

    @Inject
    private StaticModelWorldObjectDrawer staticModelWorldObjectDrawer;

    @Override
    protected void drawHighlightEditorObject(Object editableObject) {

        if (editableObject instanceof StaticModelWorldObject) {
            staticModelWorldObjectDrawer.draw((StaticModelWorldObject) editableObject, true);

        } else if (editableObject instanceof DrawableModel) {

            highlightModelDrawer.set((DrawableModel) editableObject, camera);

            HighlightDrawer.drawHighlight(highlightModelDrawer);
        }
    }

}