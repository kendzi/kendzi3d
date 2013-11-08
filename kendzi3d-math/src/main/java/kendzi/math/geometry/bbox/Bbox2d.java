/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.math.geometry.bbox;

import java.util.List;

import javax.vecmath.Point2d;

/**
 * BBox for 2d.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class Bbox2d {

    /**
     * x min
     */
    private double xMin;

    /**
     * x max
     */
    private double xMax;

    /**
     * y min
     */
    private double yMin;

    /**
     * y max
     */
    private double yMax;

    /**
     * Con.
     */
    public Bbox2d() {
        xMin = Double.MAX_VALUE;
        xMax = -Double.MAX_VALUE;
        yMax = Double.MAX_VALUE;
        yMin = -Double.MAX_VALUE;
    }

    public Bbox2d(List<Point2d> points) {
        this();

        addPoints(points);
    }

    public void addPoint(double x, double y) {

        xMin = Math.min(xMin, x);
        xMax = Math.max(xMax, x);

        yMin = Math.min(yMin, y);
        yMax = Math.max(yMax, y);
    }

    public void addPoint(Point2d p) {

        xMin = Math.min(xMin, p.x);
        xMax = Math.max(xMax, p.x);

        yMin = Math.min(yMin, p.y);
        yMax = Math.max(yMax, p.y);
    }

    public void addPoints(List<Point2d> points) {
        for (Point2d point2d : points) {
            addPoint(point2d);
        }
    }

    public boolean isInside(Point2d point) {
        return point.x >= xMin && point.x <= xMax && point.y >= yMin && point.y <= yMax;
    }

    public double getxMin() {
        return xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public double getyMax() {
        return yMax;
    }

    @Override
    public String toString() {
        return "Bbox2d [xMin=" + xMin + ", xMax=" + xMax + ", yMin=" + yMin + ", yMax=" + yMax + "]";
    }



}
