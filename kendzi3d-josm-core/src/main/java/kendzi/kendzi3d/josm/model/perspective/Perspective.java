package kendzi.kendzi3d.josm.model.perspective;

import org.joml.Vector2d;
import org.openstreetmap.josm.data.osm.Node;

public interface Perspective {

    Vector2d calcPoint(Node node);

}
