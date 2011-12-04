/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.tmp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

public abstract class AbstractWayModel extends AbstractModel {

    protected List<Point2d> points;

    protected Way way;

    public AbstractWayModel(Way pWay, Perspective3D pPerspective3D) {
        super(pPerspective3D);

        calcModelCenter(pWay);
//        calcModelRadius(pWay);

        List<Point2d> pointsList = new ArrayList<Point2d>();

        double maxRadius = 0;

        for (int i = 0; i < pWay.getNodesCount(); i++) {
            Node node = pWay.getNode(i);

            Point2d p  = toModelFrame(node);
            pointsList.add(p);
            double dx = p.x;//this.x - this.perspective.calcX(node.getEastNorth().getX());
            double dy = p.y;//this.y - this.perspective.calcY(node.getEastNorth().getY());

            double radius = dx*dx + dy*dy;
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }

        this.way = pWay;

        this.radius = maxRadius;
        this.points = pointsList;
    }


    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getOsmPrimitives()
     */
    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {

        HashSet<OsmPrimitive> set = new HashSet<OsmPrimitive>();

        set.add(this.way);

        return set;
    }


}
