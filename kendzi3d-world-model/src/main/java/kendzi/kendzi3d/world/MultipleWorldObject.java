package kendzi.kendzi3d.world;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3dc;

public abstract class MultipleWorldObject implements WorldObject {

    private List<WorldObject> worldObjects = new ArrayList<>();

    @Override
    public Vector3dc getPoint() {
        if (!worldObjects.isEmpty()) {
            return worldObjects.get(0).getPoint();
        }
        return null;
    }

    @Override
    public Vector3dc getPosition() {
        // FIXME it is wrong!
        return getPoint();
    }

    @Override
    public void setPoint(Vector3dc point) {
        throw new IllegalAccessError();
    }

    /**
     * @return the worldObjects
     */
    public List<WorldObject> getWorldObjects() {
        return worldObjects;
    }

    /**
     * @param worldObjects
     *            the worldObjects to set
     */
    public void setWorldObjects(List<WorldObject> worldObjects) {
        this.worldObjects = worldObjects;
    }

}
