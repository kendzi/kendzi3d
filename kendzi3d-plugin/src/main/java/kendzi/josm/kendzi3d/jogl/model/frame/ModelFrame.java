package kendzi.josm.kendzi3d.jogl.model.frame;

import javax.vecmath.Point2d;

import org.openstreetmap.josm.data.osm.Node;

/**
 * Transform points from and to model frame.
 * 
 * @author Tomasz Kedziora
 */
public interface ModelFrame {

    Point2d toModelFrame(Node node);
}
