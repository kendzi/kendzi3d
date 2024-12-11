package kendzi.kendzi3d.editor.example;

import java.util.List;

import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditorDrawer;

public class ExampleEditorDrawer implements EditorDrawer {

    ExampleCore exampleCore;

    public ExampleEditorDrawer(ExampleCore exampleCore) {
        this.exampleCore = exampleCore;
    }

    @Override
    public void draw() {

        List<EditableObject> editableObjects = exampleCore.getEditableObjects();
        for (EditableObject editableObject : editableObjects) {

        }
    }

}
