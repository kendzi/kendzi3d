/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.tmp;

import java.util.HashSet;
import java.util.Set;

import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public abstract class AbstractPointModel extends AbstractModel {

    protected Node node;

    public AbstractPointModel(Node node, Perspective3D pPerspective3d) {
        super(pPerspective3d);

        this.perspective = pPerspective3d;
      //  super(toPoint(node.getEastNorth()), null);

        this.x =  this.perspective.calcX(node.getEastNorth().getX());
        this.y =  this.perspective.calcY(node.getEastNorth().getY());

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

        HashSet<OsmPrimitive> set = new HashSet<OsmPrimitive>();

        set.add(this.node);

        return set;
    }

//    Point2d toPoint(EastNorth pEastNorth) {
//        if (pEastNorth == null) {
//            return null;
//        }
//        return new Point2d(pEastNorth.getX(), pEastNorth.getY());
//    }




}
