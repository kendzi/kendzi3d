package kendzi.kendzi3d.editor.example;

import java.util.ArrayList;
import java.util.List;

import kendzi.jogl.camera.Camera;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditorCore;
import kendzi.kendzi3d.editor.example.objects.Box;
import kendzi.kendzi3d.editor.example.objects.Roof;

public class ExampleCore implements EditorCore {

    private final List<EditableObject> objects = new ArrayList<>();

    public ExampleCore() {
        objects.add(new Box());
        objects.add(new Roof());
    }

    @Override
    public Camera getCamera() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EditableObject> getEditableObjects() {
        return objects;
    }

}
