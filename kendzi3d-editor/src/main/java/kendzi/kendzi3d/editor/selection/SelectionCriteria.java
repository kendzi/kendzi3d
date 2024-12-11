package kendzi.kendzi3d.editor.selection;

import kendzi.kendzi3d.editor.EditableObject;

public interface SelectionCriteria {

    boolean match(EditableObject editableObject);

    boolean match(Selection selection);

}
