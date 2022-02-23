package kendzi.kendzi3d.editor.example;

import com.jogamp.opengl.GL2;

import java.util.List;

import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditorDrawer;

public class ExampleEditorDrawer implements EditorDrawer {

    ExampleCore exampleCore;

    public ExampleEditorDrawer(ExampleCore exampleCore) {
        this.exampleCore = exampleCore;
    }

    @Override
    public void draw(GL2 gl) {

        List<EditableObject> editableObjects = exampleCore.getEditableObjects();
        for (EditableObject editableObject : editableObjects) {

        }
    }

}
