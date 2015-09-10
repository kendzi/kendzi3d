package kendzi.kendzi3d.editor.example.objects.render;

import com.jogamp.opengl.GL2;

import kendzi.jogl.Gl2Draw;
import kendzi.kendzi3d.editor.example.objects.Roof;

public class RoofHighlightDrawer implements Gl2Draw {

    private Roof roof;

    public void setRoof(Roof roof) {
        this.roof = roof;
    }

    @Override
    public void draw(GL2 gl) {
        RoofDrawUtil.draw(roof, gl);
    }
}
