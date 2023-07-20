/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.event;

import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;
import org.joml.Vector3dc;

public class ArrowEditorChangeEvent extends EditorChangeEvent {
    private final double length;
    private final Vector3dc closestPointOnBaseRay;

    public ArrowEditorChangeEvent(boolean end, ArrowEditor editor, double length, Vector3dc closestPointOnBaseRay) {
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
    public Vector3dc getClosestPointOnBaseRay() {
        return this.closestPointOnBaseRay;
    }

    public ArrowEditor getArrowEditor() {
        return (ArrowEditor) this.getEditor();
    }
}
