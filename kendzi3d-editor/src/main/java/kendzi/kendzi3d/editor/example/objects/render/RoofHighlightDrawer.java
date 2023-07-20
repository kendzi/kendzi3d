package kendzi.kendzi3d.editor.example.objects.render;

import kendzi.jogl.Gl2Draw;
import kendzi.kendzi3d.editor.example.objects.Roof;

public class RoofHighlightDrawer implements Gl2Draw {

    private Roof roof;

    public void setRoof(Roof roof) {
        this.roof = roof;
    }

    @Override
    public void draw() {
        RoofDrawUtil.draw(roof);
    }
}
