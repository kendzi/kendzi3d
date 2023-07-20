package kendzi.josm.kendzi3d.jogl.model.frame;

import org.joml.Vector2dc;
import org.openstreetmap.josm.data.osm.Node;

/**
 * Transform points from and to model frame.
 * 
 * @author Tomasz Kedziora
 */
public interface ModelFrame {

    Vector2dc toModelFrame(Node node);
}
