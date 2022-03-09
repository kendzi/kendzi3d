package kendzi.kendzi3d.editor;

import kendzi.kendzi3d.editor.selection.Selectable;
import org.joml.Vector3dc;

/**
 * Basic editable object. FIXME change to editor object?!
 */
public interface EditableObject extends Selectable {

    /**
     * Position of editable object in the world corrdinates.
     *
     * @return position of editable object
     */
    Vector3dc getPosition();
}
