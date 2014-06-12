/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.tmp;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.kendzi3d.josm.model.perspective.Perspective;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public abstract class AbstractPointModel extends AbstractModel {

    protected Node node;

    public AbstractPointModel(Node node, Perspective perspective) {
        super(perspective);

        Point2d point = perspective.calcPoint(node);

        setPoint(new Point3d(point.x, 0, -point.y));

        this.node = node;

        this.radius = 1.0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see kendzi.josm.kendzi3d.jogl.model.AbstractModel#getOsmPrimitives()
     */
    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {

        Set<OsmPrimitive> set = new HashSet<OsmPrimitive>();
        set.add(this.node);

        return set;
    }
}
