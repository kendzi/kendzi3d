package kendzi.josm.kendzi3d.jogl.selection.event;

import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditor;

public class ArrowEditorChangeEvent extends EditorChangeEvent {
    private double height;
    private Point3d closestPointOnBaseRay;

    public ArrowEditorChangeEvent(boolean end, ArrowEditor editor, double height, Point3d closestPointOnBaseRay) {
        super(end, editor);
        this.height = height;
        this.closestPointOnBaseRay = closestPointOnBaseRay;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * @return the closestPointOnBaseRay
     */
    public Point3d getClosestPointOnBaseRay() {
        return this.closestPointOnBaseRay;
    }

    public ArrowEditor getArrowEditor() {
        return (ArrowEditor) this.getEditor();
    }
}
