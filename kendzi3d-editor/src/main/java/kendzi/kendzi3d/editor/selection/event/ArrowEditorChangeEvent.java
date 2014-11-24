/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.event;

import javax.vecmath.Point3d;

import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;

public class ArrowEditorChangeEvent extends EditorChangeEvent {
    private double length;
    private Point3d closestPointOnBaseRay;

    public ArrowEditorChangeEvent(boolean end, ArrowEditor editor, double length, Point3d closestPointOnBaseRay) {
        super(end, editor);
        this.length = length;
        this.closestPointOnBaseRay = closestPointOnBaseRay;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return this.length;
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
