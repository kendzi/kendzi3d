/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.dormer;

import javax.vecmath.Point3d;

public class RoofHookPoint {

    /**
     * Hook point.
     */
    private Point3d point;

    /**
     * Extenstion start angle.
     */
    private double startAngle;

    /** XXX
     * Extenstion depth.
     */
    private double depth;

    /** XXX
     * Extenstion end angle.
     */
    private double endAngle;


    /**
     * Default constructor.
     *
     * @param pPoint hook point
     * @param pStartAngle extenstion start angle.
     * @param pDepth extenstion depth.
     * @param pEndAngle extenstion end angle.
     */
    public RoofHookPoint(Point3d pPoint, double pStartAngle, double pDepth, double pEndAngle) {
        super();
        this.point = pPoint;
        this.startAngle = pStartAngle;
        this.depth = pDepth;
        this.endAngle = pEndAngle;
    }

    /**
     * @return the point
     */
    public Point3d getPoint() {
        return this.point;
    }
    /**
     * @param pPoint the point to set
     */
    public void setPoint(Point3d pPoint) {
        this.point = pPoint;
    }

       /**
     * @return the depth
     */
    public double getDepth() {
        return this.depth;
    }

    /**
     * @param pDepth the depth to set
     */
    public void setDepth(double pDepth) {
        this.depth = pDepth;
    }

    /**
     * @return the startAngle
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * @param pStartAngle the startAngle to set
     */
    public void setStartAngle(double pStartAngle) {
        this.startAngle = pStartAngle;
    }

    /**
     * @return the endAngle
     */
    public double getEndAngle() {
        return this.endAngle;
    }

    /**
     * @param pEndAngle the endAngle to set
     */
    public void setEndAngle(double pEndAngle) {
        this.endAngle = pEndAngle;
    }

}
