package kendzi.math.geometry;

import javax.vecmath.Point3d;

public class Sphere3d {
    private Point3d center;
    private double radius;


    public Sphere3d() {
        this(new Point3d(), 1);
    }

    public Sphere3d(Point3d center, double radius) {
        super();
        this.center = center;
        this.radius = radius;
    }


    /**
     * @return the center
     */
    public Point3d getCenter() {
        return center;
    }
    /**
     * @param center the center to set
     */
    public void setCenter(Point3d center) {
        this.center = center;
    }
    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }
    /**
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }



}
