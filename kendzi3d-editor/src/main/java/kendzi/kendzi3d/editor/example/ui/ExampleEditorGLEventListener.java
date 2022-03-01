package kendzi.kendzi3d.editor.example.ui;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.drawer.AxisLabels;
import kendzi.jogl.drawer.TilesSurface;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.drawer.HighlightDrawer;
import kendzi.kendzi3d.editor.example.objects.Box;
import kendzi.kendzi3d.editor.example.objects.Roof;
import kendzi.kendzi3d.editor.example.objects.render.BoxDrawUtil;
import kendzi.kendzi3d.editor.example.objects.render.BoxHighlightDrawer;
import kendzi.kendzi3d.editor.example.objects.render.RoofDrawUtil;
import kendzi.kendzi3d.editor.example.objects.render.RoofHighlightDrawer;
import kendzi.kendzi3d.editor.ui.BaseEditorGLEventListener;

public class ExampleEditorGLEventListener extends BaseEditorGLEventListener {
    /**
     * Drawer for axis labels.
     */
    private final AxisLabels axisLabels = new AxisLabels();

    /**
     * Drawer for tiles floor.
     */
    private final TilesSurface floor = new TilesSurface();

    /**
     * Drawer for highlighted box.
     */
    private final BoxHighlightDrawer boxHighlightDrawer = new BoxHighlightDrawer();

    /**
     * Drawer for highlighted roof.
     */
    private final RoofHighlightDrawer roofHighlightDrawer = new RoofHighlightDrawer();

    @Override
    protected void drawBeforeEditorObjects(Viewport viewport2) {
        floor.draw();
    }

    @Override
    protected void drawAfterEditorObjects(Viewport viewport2) {
        axisLabels.draw();
    }

    @Override
    public void init() {
        axisLabels.init();
    }

    @Override
    protected void drawEditorObject(EditableObject editableObject, Viewport viewport) {
        //
        if (editableObject instanceof Box) {

            BoxDrawUtil.draw((Box) editableObject);
        } else if (editableObject instanceof Roof) {

            RoofDrawUtil.draw((Roof) editableObject);
        } else {
            throw new IllegalStateException("unsupported editor object: " + editableObject);
        }
    }

    @Override
    protected void drawHighlightEditorObject(Object editableObject) {

        if (editableObject instanceof Box) {

            boxHighlightDrawer.setBox((Box) editableObject);
            HighlightDrawer.drawHighlight(boxHighlightDrawer);
        } else if (editableObject instanceof Roof) {

            roofHighlightDrawer.setRoof((Roof) editableObject);
            HighlightDrawer.drawHighlight(roofHighlightDrawer);
        }
    }
}
