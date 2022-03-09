package kendzi.kendzi3d.world;

import java.util.List;

import org.joml.Vector3dc;

// XXX name
public interface MultiPointWorldObject extends StaticModelWorldObject {

    /**
     * @return the points
     */
    List<Vector3dc> getPoints();

}
