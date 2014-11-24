package kendzi.kendzi3d.editor;

import java.util.List;

/**
 * Data source for editable objects.
 *
 */
public interface EditableObjectProvider {

    /**
     * Gets list of editable object.
     * 
     * @return list of editable object
     */
    List<EditableObject> getEditableObjects();
}
