package kendzi.kendzi3d.editor.example.objects.render;

import javax.media.opengl.GL2;

import kendzi.jogl.Gl2Draw;
import kendzi.kendzi3d.editor.example.objects.Box;

public class BoxHighlightDrawer implements Gl2Draw {

    private Box box;

    public void setBox(Box box) {
        this.box = box;
    }

    @Override
    public void draw(GL2 gl) {
        BoxDrawUtil.draw(box, gl);
    }
}
