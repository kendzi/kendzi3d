package kendzi.kendzi3d.editor.example;

import java.util.List;

import com.jogamp.opengl.GL2;

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
