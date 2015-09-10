package kendzi.josm.kendzi3d.jogl.model;

import java.util.List;

import com.jogamp.opengl.GL2;

import kendzi.jogl.camera.Camera;
import kendzi.josm.kendzi3d.data.RebuildableWorldObject;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.world.MultipleWorldObject;
import kendzi.kendzi3d.world.WorldObject;

public abstract class DrawableMultipleWorldObject extends MultipleWorldObject implements DrawableModel, RebuildableWorldObject {

    private boolean build;

    private boolean error;

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getRadius() {
        return 0;
    }

    @Override
    public void buildWorldObject() {
        List<WorldObject> worldObjects2 = getWorldObjects();
        for (WorldObject worldObject : worldObjects2) {
            if (worldObject instanceof DrawableModel) {
                ((DrawableModel) worldObject).buildWorldObject();
            }
        }
        build = true;
    }

    @Override
    public boolean isWorldObjectBuild() {
        return build;
    }

    @Override
    public void draw(GL2 gl, Camera camera, boolean selected) {
        draw(gl, camera);
    }

    @Override
    public void draw(GL2 gl, Camera camera) {
        List<WorldObject> worldObjects2 = getWorldObjects();
        for (WorldObject worldObject : worldObjects2) {
            if (worldObject instanceof DrawableModel) {
                ((DrawableModel) worldObject).draw(gl, camera);
            }
        }
    }

    @Override
    public boolean isError() {
        return error;
    }

    @Override
    public void setError(boolean pError) {
        error = pError;
    }

    @Override
    public List<Selection> getSelection() {
        return null;
    }

}