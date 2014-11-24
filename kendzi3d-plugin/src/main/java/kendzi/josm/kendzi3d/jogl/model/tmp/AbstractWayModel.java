/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.tmp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

public abstract class AbstractWayModel extends AbstractModel {

    protected List<Point2d> points;

    protected Way way;

    public AbstractWayModel(Way way, Perspective perspective) {
        super(perspective);

        calcModelCenter(way);

        List<Point2d> pointsList = new ArrayList<Point2d>();

        double maxRadius = 0;

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            Point2d p = toModelFrame(node);
            pointsList.add(p);
            double dx = p.x;
            double dy = p.y;

            double radius = dx * dx + dy * dy;
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }

        this.way = way;

        radius = maxRadius;
        points = pointsList;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getOsmPrimitives()
     */
    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {

        Set<OsmPrimitive> set = new HashSet<OsmPrimitive>();

        set.add(way);

        return set;
    }

    @Override
    public void rebuildWorldObject(OsmPrimitive primitive, Perspective perspective) {
        // clean up everything
        way = (Way) primitive;
        this.perspective = perspective;

        buildWorldObject();
    }

}
