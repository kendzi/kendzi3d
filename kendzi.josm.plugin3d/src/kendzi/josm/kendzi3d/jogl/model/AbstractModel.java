/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.frame.GlobalFrame;
import kendzi.josm.kendzi3d.jogl.model.frame.ModelFrame;
import kendzi.josm.kendzi3d.jogl.model.tmp.OsmPrimitiveRender;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;


/** in future create AbstractWayModel, AbstractNodeModel ?
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public abstract class AbstractModel implements Model, ModelFrame, GlobalFrame, OsmPrimitiveRender {


    protected double x;

    protected double y;

    protected double radius;
    protected Perspective3D perspective;
    protected boolean buildModel;


//    protected double globalX;
//    protected double globalY;
//
//    private double openGlX;
//    private double openGlY;



    /**
     * If error occurred.
     */
    protected boolean error;

    public AbstractModel(Perspective3D pPerspective) {
        this.perspective = pPerspective;
    }

    @Deprecated
    public AbstractModel(Node node, Perspective3D pers) {
        this.perspective = pers;

        this.x =  this.perspective.calcX(node.getEastNorth().getX());
        this.y =  this.perspective.calcY(node.getEastNorth().getY());

        this.radius = 1.0;
    }

    @Deprecated
    public AbstractModel(Way way, Perspective3D pPerspective) {
        this.perspective = pPerspective;
        calcModelCenter(way);
        calcModelRadius(way);
    }

    public AbstractModel() {

    }

    /** Calculate model center.
     * @param way list of points
     */
    protected void calcModelCenter(Way way) {
        double centerX = 0;
        double centerY = 0;
        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            double x = this.perspective.calcX(node.getEastNorth().getX());
            double y = this.perspective.calcY(node.getEastNorth().getY());

            centerX += x;
            centerY += y;
        }

        this.x = centerX / way.getNodesCount();
        this.y = centerY / way.getNodesCount();
    }

    /** Calculate model center.
     * @param pNodes list of points
     */
    protected void calcModelCenter(List<Node> pNodes) {
        double centerX = 0;
        double centerY = 0;
        for (Node node : pNodes) {
            double x = this.perspective.calcX(node.getEastNorth().getX());
            double y = this.perspective.calcY(node.getEastNorth().getY());

            centerX += x;
            centerY += y;
        }

        this.x = centerX / pNodes.size();
        this.y = centerY / pNodes.size();
    }

    /** Calculate max distance from center do all of points.
     * @param way list of points
     */
    protected void calcModelRadius(Way way) {

        double maxRadius = 0;

        for (int i = 0; i < way.getNodesCount(); i++) {
            Node node = way.getNode(i);

            double dx = this.x - this.perspective.calcX(node.getEastNorth().getX());
            double dy = this.y - this.perspective.calcY(node.getEastNorth().getY());

            double radius = dx*dx + dy*dy;
            if (radius > maxRadius) {
                maxRadius = radius;
            }
        }
        this.radius = maxRadius;
    }

    @Override
    public boolean isInCamraRange(double camraX, double camraY,
            double camraRange) {
        return distance(camraX, camraY) > this.radius + camraRange;
    }

    @Override
    public double distance(double pX, double pY) {
        double dx = pX - this.x;
        double dy = pY - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getRadius() {
        return this.radius;
    }

    @Override
    public void buildModel() {
        this.buildModel = true;
    }

    @Override
    public boolean isBuildModel() {
        return this.buildModel;
    }

    @Override
    public String toString() {
        return "AbstractModel [x=" + this.x + ", y=" + this.y + ", radius=" + this.radius
        + ", buildModel=" + this.buildModel
        //				 + ", perspective=" + perspective +
        + "]";
    }

    /**
     * @return the error
     */
    @Override
    public boolean isError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    @Override
    public void setError(boolean error) {
        this.error = error;
    }






    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.GlobalFrame#getGlobalX()
     */
    @Override
    public double getGlobalX() {
        return this.x;
    }

    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.GlobalFrame#getGlobalY()
     */
    @Override
    public double getGlobalY() {
        return this.y;
    }


    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.ModelFrame#toModelFrameX(double)
     */
    @Override
    public double toModelFrameX(double x) {
        return this.perspective.calcY(x) - getGlobalX();
    }

    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.ModelFrame#toModelFrameY(double)
     */
    @Override
    public double toModelFrameY(double y) {
        return this.perspective.calcY(y) - getGlobalY();
    }


    /* (non-Javadoc)
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.ModelFrame#toModelFrame(javax.vecmath.Point2d)
     */
    @Override
    public Point2d toModelFrame(Node pNode) {
        Point2d calcPoint = this.perspective.calcPoint(pNode);
        calcPoint.x -= getGlobalX();
        calcPoint.y -= getGlobalY();
        return calcPoint;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.tmp.OsmPrimitiveRender#getOsmPrimitives()
     */
    @Override
    public Set<OsmPrimitive> getOsmPrimitives() {
        // TODO Auto-generated method stub
        // FIXME remove form abstract!
        throw new RuntimeException("TODO");
    }

//    protected MetadataCacheService getMetadataCacheService() {
//        ApplicationContext context = ApplicationContextFactory.getContext();
//        // XXX rewrite with injections?
//        return (MetadataCacheService) context.getBean("metadataCacheService");
//    }

}
