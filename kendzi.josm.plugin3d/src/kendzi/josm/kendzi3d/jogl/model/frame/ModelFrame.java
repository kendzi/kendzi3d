package kendzi.josm.kendzi3d.jogl.model.frame;

import javax.vecmath.Point2d;

import org.openstreetmap.josm.data.osm.Node;

/**
 * Transform points from and to model frame.
 *
 * @author Tomasz Kedziora
 */
public interface ModelFrame {

    double toModelFrameX(double x);

    double toModelFrameY(double y);

//    Point2d toModelFrame(Point2d pPoint);

    Point2d toModelFrame(Node pNode);
}
