/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.data.RebuildableWorldObject;
import kendzi.josm.kendzi3d.jogl.model.export.ExportModel;
import kendzi.josm.kendzi3d.jogl.model.frame.GlobalFrame;
import kendzi.josm.kendzi3d.jogl.model.frame.ModelFrame;
import kendzi.josm.kendzi3d.jogl.model.tmp.OsmPrimitiveRender;
import kendzi.kendzi3d.editor.selection.Selectable;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.AbstractWorldObject;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

/**
 * in future create AbstractWayModel, AbstractNodeModel ?
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public abstract class AbstractModel extends AbstractWorldObject implements Selectable, DrawableModel, ModelFrame, GlobalFrame,
        OsmPrimitiveRender, ExportModel, RebuildableWorldObject {

    protected double radius;

    protected Perspective perspective;

    protected boolean buildModel;

    /**
     * If error occurred.
     */
    protected boolean error;

    public AbstractModel(Perspective perspective) {
        this.perspective = perspective;
        setPoint(new Point3d());
    }

    @Deprecated
    public AbstractModel(Way way, Perspective perspective) {
        this.perspective = perspective;
        setPoint(new Point3d());

        calcModelCenter(way);
        calcModelRadius(way);

    }

    public AbstractModel() {
        setPoint(new Point3d());
    }

    /**
     * Calculate model center.
     *
     * @param way
     *            list of points
     */
    protected void calcModelCenter(Way way) {
        double centerX = 0;
        double centerY = 0;
        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            Point2d point = perspective.calcPoint(node);

            centerX += point.x;
            centerY += point.y;
        }

        setPoint(new Point3d(centerX / way.getNodesCount(), 0, -(centerY / way.getNodesCount())));
    }

    /**
     * Calculate model center.
     *
     * @param pNodes
     *            list of points
     */
    protected void calcModelCenter(List<Node> pNodes) {
        double centerX = 0;
        double centerY = 0;
        for (Node node : pNodes) {
            Point2d point = perspective.calcPoint(node);
            centerX += point.x;
            centerY += point.y;
        }
        setPoint(new Point3d(centerX / pNodes.size(), 0, -(centerY / pNodes.size())));

    }

    /**
     * Calculate max distance from center do all of points.
     *
     * @param way
     *            list of points
     */
    protected void calcModelRadius(Way way) {

        double maxRadius = 0;

        double x = getPoint().x;
        double y = -getPoint().z;

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);
            Point2d point = perspective.calcPoint(node);

            double dx = x - point.x;
            double dy = y - point.y;

            double radius = dx * dx + dy * dy;
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }
        radius = maxRadius;
    }

    @Override
    @Deprecated
    public double getX() {
        return getPoint().x;
    }

    @Override
    @Deprecated
    public double getY() {
        return -getPoint().z;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public void buildWorldObject() {
        buildModel = true;
    }

    @Override
    public boolean isWorldObjectBuild() {
        return buildModel;
    }

    @Override
    public String toString() {
        return "AbstractModel [point=" + getPoint() + ", radius=" + radius + ", buildModel=" + buildModel + "]";
    }

    /**
     * @return the error
     */
    @Override
    public boolean isError() {
        return error;
    }

    /**
     * @param error
     *            the error to set
     */
    @Override
    public void setError(boolean error) {
        this.error = error;
    }

    /*
     * (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.GlobalFrame#getGlobalX()
     */
    @Override
    @Deprecated
    public double getGlobalX() {
        return getX();
    }

    /*
     * (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.GlobalFrame#getGlobalY()
     */
    @Override
    @Deprecated
    public double getGlobalY() {
        return getY();
    }

    /*
     * (non-Javadoc)
     * @see
     * kendzi.josm.kendzi3d.jogl.model.tmp.ModelFrame#toModelFrame(javax.vecmath
     * .Point2d)
     */
    @Override
    public Point2d toModelFrame(Node pNode) {
        Point2d calcPoint = perspective.calcPoint(pNode);
        calcPoint.x -= getGlobalX();
        calcPoint.y -= getGlobalY();
        return calcPoint;
    }

    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {
        // FIXME remove form abstract!
        throw new RuntimeException("TODO");
    }

    @Override
    public List<Selection> getSelection() {
        return Collections.<Selection> emptyList();
    }

}
