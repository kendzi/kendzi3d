package kendzi.kendzi3d.josm.model.perspective;

import javax.vecmath.Point2d;

import org.openstreetmap.josm.data.osm.Node;

public interface Perspective {

    public abstract Point2d calcPoint(Node node);

}
