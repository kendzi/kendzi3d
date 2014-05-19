package kendzi.kendzi3d.world;

import java.util.List;

import javax.vecmath.Point3d;

// XXX name
public interface MultiPointWorldObject extends StaticModelWorldObject {

    /**
     * @return the points
     */
    public List<Point3d> getPoints();

}
