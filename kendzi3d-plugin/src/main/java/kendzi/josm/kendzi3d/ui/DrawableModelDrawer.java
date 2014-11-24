package kendzi.josm.kendzi3d.ui;

import javax.media.opengl.GL2;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.camera.Camera;
import kendzi.josm.kendzi3d.jogl.model.DrawableModel;

public class DrawableModelDrawer implements Gl2Draw {

    private DrawableModel drawableModel;

    private Camera camera;

    public void set(DrawableModel drawableModel, Camera camera) {
        this.drawableModel = drawableModel;
        this.camera = camera;
    }

    @Override
    public void draw(GL2 gl) {
        drawableModel.draw(gl, camera, true);
    }
}
