/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.text.DecimalFormat;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.point.PointUtil;

public class KinematicsSimpleAnimator {

    private static final double MAX_FORCE = 10;

    private static final double MAX_ROTATE_FORCE = 10;

    // 1kg
    double m = 1;

    // 1N
    private static final  double MOVE_FORCE = 1;

    Point3d p = new Point3d();

    Vector3d angle = new Vector3d();

    /**
     * Angular momentum.
     * (Moment obrotowy)
     * @see "http://en.wikipedia.org/wiki/Angular_momentum"
     */
    Vector3d L = new Vector3d();


    // distanse mas from rotate point
//    Vector3d r = new Vector3d(1d, 0, 0);
    double r = 1d;

    // moment bazwladnosci
    // moment of inertia
    double I = this.m * this.r * this.r;

//    Tarcie
//    Friction
//    coefficient of friction
    double u = 1d;
    double friction = u * m * 0.9;

//    sila
    Vector3d force = new Vector3d();
//    sila wymuszajaca obrot
    Vector3d forceRotate = new Vector3d();
    // Ped
    Vector3d momentum = new Vector3d();
    double lastTime = System.currentTimeMillis() / 1000d;

    void updateState() {
        double time = System.currentTimeMillis() / 1000d;
        double dt = time - this.lastTime;
        this.lastTime = time;

        Tuple3d dMomentum = new Vector3d(this.force);
        dMomentum.scale(dt);

        this.momentum.add(dMomentum);

        Point3d dPoint = new Point3d(this.momentum);
        dPoint.scale(dt / this.m);


        double ft = this.friction * dt;
        double momentumModule = this.momentum.length();
        double fs = Math.min(ft, momentumModule);

        if (momentumModule > 0) {
            Vector3d friction = new Vector3d(this.momentum);
            friction.negate();
            friction.scale(ft / momentumModule);

            this.momentum.add(friction);
        }


        this.p.add(dPoint);


        // rotation
        // Momet sily

//        Vector3d force = new Vector3d(pForward, 0, pSide);
//
//        force = PointUtil.rotateZ3d(force, this.rotate.getZ());
//        force = PointUtil.rotateY3d(force, this.rotate.getY());

        Vector3d r = new Vector3d(1d, 0, 0 );
//        r.cross(v1, v2)

        Vector3d torque = new Vector3d();
        torque.cross(r, this.forceRotate);

        Tuple3d dL = torque;
        dL.scale(dt);

        this.L.add(dL);

        Point3d dAngle = new Point3d(this.L);
        dAngle.scale(dt / this.I);

        this.angle.add(dAngle);



        //Friction
        this.force.scale(0.9);
        this.forceRotate.scale(0.9);
    }

    void setRotateForce(double pHorizontally, double pVertically) {
        Vector3d force = new Vector3d(0, pVertically, -pHorizontally);

        this.forceRotate.add(force);

        if (this.forceRotate.length() > MAX_ROTATE_FORCE) {
            double scale = MAX_ROTATE_FORCE / this.forceRotate.length();
            this.forceRotate.scale(scale);
        }
    }

    void setForce(double pForward, double pSide) {
        Vector3d force = new Vector3d(pForward, 0, pSide);

        force = PointUtil.rotateZ3d(force, this.angle.getZ());
        force = PointUtil.rotateY3d(force, this.angle.getY());

        this.force.add(force);

        if (this.force.length() > MAX_FORCE) {
            double scale = MAX_FORCE / this.force.length();
            this.force.scale(scale);
        }
    }

    public void translateLeft() {
        setForce(0, -MOVE_FORCE);
    }


    public void translateRight() {
        setForce(0, MOVE_FORCE);
    }

    public void moveLeft() {
        setRotateForce(-1, 0);
    }

    public void moveRight() {
        setRotateForce(1, 0);
    }

    public void moveForward() {
        setForce(MOVE_FORCE, 0);
    }

    public void moveBackwards() {
        setForce(-MOVE_FORCE, 0);
    }

    public void moveUp() {
        // TODO Auto-generated method stub

    }

    public void moveDown() {
        // TODO Auto-generated method stub

    }

    public void rotateHorizontally(double d) {
        setRotateForce(d, 0);

    }

    public void rotateVertically(double d) {
        setRotateForce(0, d);
    }


    @SuppressWarnings("unqualified-field-access")
    public String info() {

          DecimalFormat df = new DecimalFormat("0.##"); // 2 dp


        return "KinematicsSimpleAnimator [\n"
                + "\nm=" + df.format(m)
        		+ ",\n p=" + format(p)
        		+ ",\n angle=" + format(angle)
        		+ ",\n L=" + format(L)
        		+ ",\n r=" + df.format(r)
        		+ ",\n I=" + df.format(I)
        		+ ",\n force=" + format(force)
        		+ ",\n forceRotate=" + format(forceRotate)
        		+ ",\n momentum=" + format(momentum)
                + ",\n lastTime=" + df.format(lastTime) + "]";
    }

    String format(Tuple3d tuple) {
        DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
        return "( " + df.format(tuple.x) + ", " + df.format(tuple.y) + ", " + df.format(tuple.z) + " )";

    }
    String formatAngle(Tuple3d tuple) {
        DecimalFormat df = new DecimalFormat("0.##"); // 2 dp
        return "( " + df.format(Math.toDegrees(tuple.x)) + ", " + df.format(Math.toDegrees(tuple.y)) + ", " + df.format(Math.toDegrees(tuple.z)) + " )";

    }



}
