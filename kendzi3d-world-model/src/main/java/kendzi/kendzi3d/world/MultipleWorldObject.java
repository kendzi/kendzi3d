package kendzi.kendzi3d.world;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

public abstract class MultipleWorldObject implements WorldObject {

    private List<WorldObject> worldObjects = new ArrayList<WorldObject>();

    @Override
    public Point3d getPoint() {
        if (!worldObjects.isEmpty()) {
            return worldObjects.get(0).getPoint();
        }
        return null;
    }

    @Override
    public Point3d getPosition() {
        // FIXME it is wrong!
        return getPoint();
    }

    @Override
    public void setPoint(Point3d point) {
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
