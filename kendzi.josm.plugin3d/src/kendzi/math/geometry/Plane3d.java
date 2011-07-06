/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Plane described by point and normal vector.
 *
 * @see "http://en.wikipedia.org/wiki/Plane_(geometry)"
 *
 * @author Tomasz Kêdziora (Kendzi)
 */
public class Plane3d {

    /**
     * Point on plane.
     */
    private Point3d point;

    /**
     * Normal vector of plane.
     */
    private Vector3d normal;

    /** Default constructor.
     * @param pPoint point on plane
     * @param pNormal normal vector of plane
     */
    public Plane3d(Point3d pPoint, Vector3d pNormal) {
        this.point = pPoint;
        this.normal = pNormal;
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
     * @return the normal
     */
    public Vector3d getNormal() {
        return this.normal;
    }
    /**
     * @param pNormal the normal to set
     */
    public void setNormal(Vector3d pNormal) {
        this.normal = pNormal;
    }


    /** Calculate Y value on given coordinates X, Z.
     * TODO when point is no exist.
     * @param pX coordinates X
     * @param pZ coordinates Z
     * @return Y value of plane
     */
    public double calcYOfPlane(double pX, double pZ) {
        double a = this.normal.x;
        double b = this.normal.y;
        double c = this.normal.z;
        double d = -a * this.point.x - b * this.point.y - c * this.point.z;

        return (-a * pX - c * pZ - d) / b;
    }

    /** Calculate Z value on given coordinates X, Y.
     * TODO when point is no exist.
     * @param pX coordinates X
     * @param pY coordinates Y
     * @return Z value of plane
     */
    public double calcZOfPlane(double pX, double pY) {
        double a = this.normal.x;
        double b = this.normal.y;
        double c = this.normal.z;
        double d = -a * this.point.x - b * this.point.y - c * this.point.z;

        return (-a * pX - b * pY - d) / c;
    }


}
