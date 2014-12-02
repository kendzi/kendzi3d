package kendzi.kendzi3d.editor.selection;

import kendzi.kendzi3d.editor.EditableObject;

public interface SelectionCriteria {

    public boolean match(EditableObject editableObject);

    public boolean match(Selection selection);

}
