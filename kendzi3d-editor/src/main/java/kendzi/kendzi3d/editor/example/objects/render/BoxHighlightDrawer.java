package kendzi.kendzi3d.editor.example.objects.render;

import kendzi.jogl.Gl2Draw;
import kendzi.kendzi3d.editor.example.objects.Box;

public class BoxHighlightDrawer implements Gl2Draw {

    private Box box;

    public void setBox(Box box) {
        this.box = box;
    }

    @Override
    public void draw() {
        BoxDrawUtil.draw(box);
    }
}
