package kendzi.josm.kendzi3d.jogl.model.ground;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;

import com.jogamp.opengl.GL2;

import kendzi.jogl.model.render.ModelRender;

public class SelectableGround {

    private final Map<GroundType, GroundDrawer> grounds = new HashMap<>();

    private GroundType selectedGroundType = GroundType.SINGLE_TEXTURE;

    public void selectGroundType(GroundType groundType) {

        if (groundType == null) {
            selectedGroundType = GroundType.SINGLE_TEXTURE;
            return;
        }

        selectedGroundType = groundType;
    }

    public void init(GL2 gl) {
        for (GroundType key : grounds.keySet()) {
            GroundDrawer ground = grounds.get(selectedGroundType);

            if (ground != null) {
                grounds.get(key).init();
            }
        }
    }

    public void draw(GL2 gl, Point3d cameraPosition, ModelRender modelRender) {

        GroundDrawer ground = grounds.get(selectedGroundType);

        if (ground != null) {
            ground.draw(gl, cameraPosition, modelRender);
        }
    }

    public static enum GroundType {
        SINGLE_TEXTURE, STYLED_TITLE
    }

    public void addGroundDrawer(GroundType groundType, GroundDrawer drawer) {
        grounds.put(groundType, drawer);
    }
}
