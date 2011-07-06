/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;


/** in future create AbstractWayModel, AbstractNodeModel ?
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public abstract class AbstractModel implements Model {

    protected double x;
    protected double y;
    protected double radius;
    protected Perspective3D perspective;
    protected boolean buildModel;

    /**
     * If error occurred.
     */
    protected boolean error;

    public AbstractModel(Node node, Perspective3D pers) {
        this.perspective = pers;

        this.x =  this.perspective.calcX(node.getEastNorth().getX());
        this.y =  this.perspective.calcY(node.getEastNorth().getY());

        this.radius = 1.0;
    }

    public AbstractModel(Way way, Perspective3D pPerspective) {
        this.perspective = pPerspective;
        calcModelCenter(way);
        calcModelRadius(way);
    }

    /** Calculate model gravity center.
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

    /** Calculate max distance from gravity center do all of points.
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



}
