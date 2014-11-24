package kendzi.kendzi3d.editor;

import javax.vecmath.Point3d;

import kendzi.kendzi3d.editor.selection.Selectable;

/**
 * Basic editable object. FIXME change to editor object?!
 */
public interface EditableObject extends Selectable {

    /**
     * Position of editable object in the world corrdinates.
     *
     * @return position of editable object
     */
    Point3d getPosition();
}
