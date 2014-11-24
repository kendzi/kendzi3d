/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.camera;

import java.text.DecimalFormat;
import java.util.EnumMap;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.point.PointUtil;

/**
 * Simple camera move animator.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class SimpleMoveAnimator implements Camera {

    /**
     * Convert from kilometers per hour to meters per second.
     */
    private static final double KMETERS = 3600d / 1000d;

    /**
     * Up/down move acceleration.
     */
    private static final double UP_ACCELERATRION = 10;

    /**
     * Up/down move speed.
     */
    private static final double UP_SPEED = 10;

    /**
     * Side move acceleration.
     */
    private static final double SIDE_ACCELERATRION = 10;

    /**
     * Side move speed.
     */
    private static final double SIDE_SPEED = 10;

    /**
     * Forward move speed 1.
     */
    private static final double FORWARD_SPEED_1 = toMps(60); // 15 (54);

    /**
     * Forward move speed 2.
     */
    private static final double FORWARD_SPEED_2 = toMps(250);// 45; (160)

    /**
     * Forward move acceleration.
     */
    private static final double FORWARD_ACCELERATION = 10;

    /**
     * Forward stop acceleration.
     */
    private static final double FORWARD_STOP_ACCELERATION = 30;

    /**
     * Forward speed 2 timeout.
     */
    private static final double FORWARD_SPEED_2_TIMEOUT = 3;

    /**
     * Rotate stop acceleration.
     */
    private static final double ROTATE_STOP_ACCELERATRION = Math.toRadians(360);

    /**
     * Rotate move acceleration.
     */
    private static final double ROTATE_ACCELERATRION = Math.toRadians(180);

    /**
     * Rotate angular speed.
     */
    private static final double ROTATE_SPEED = Math.toRadians(180);

    private static double toKmph(double mps) {
        return 3.6 * mps;
    }

    private static double toMps(double kmph) {
        return kmph * 10.0 / 36.0;
    }

    /**
     * Speed directions.
     *
     * @author Tomasz Kędziora (Kendzi)
     *
     */
    private enum Speeds {
        FORWARD, BACKWARD, MOVE_LEFT, MOVE_RIGHT, ROTATE_LEFT, ROTATE_RIGHT, MOVE_UP, MOVE_DOWN
    }

    /**
     * Speeds directional data.
     *
     * @author Tomasz Kędziora (Kendzi)
     *
     */
    private class SpeedData {

        /**
         * Speed change start time.
         */
        double start;

        /**
         * Speed last call time.
         */
        double last;

        /**
         * If speed is active currently.
         */
        boolean active;
    }

    /**
     * Location of camera.
     */
    private Point3d point = new Point3d(-8, 1.8, 0);

    /**
     * Rotate angles vector of camera.
     */
    private Vector3d angle = new Vector3d();

    /**
     * Last time.
     */
    private double lastTime = System.currentTimeMillis() / 1000d;

    /**
     * Keys pressed.
     */
    private EnumMap<Speeds, SpeedData> speeds;

    /**
     * Speed forward.
     */
    private double vf = 0;

    /**
     * Speed side.
     */
    private double vs = 0;

    /**
     * Speed up/down.
     */
    private double vu = 0;

    /**
     * Angular speed horizontal.
     */
    private double wh = 0;

    /**
     * Number formater.
     */
    private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

    public void updateState() {
        double time = System.currentTimeMillis() / 1000d;
        double dt = time - this.lastTime;
        this.lastTime = time;

        this.vf = calcForwardSpeed(this.vf, dt, time);
        this.vs = calcSideSpeed(this.vs, dt, time);
        this.vu = calcUpSpeed(this.vu, dt, time);

        this.wh = calcHorizontallySpeed(this.wh, dt, time);

        Vector3d speed = new Vector3d(this.vf, this.vu, this.vs);

        speed = PointUtil.rotateZ3d(speed, this.angle.z);
        speed = PointUtil.rotateY3d(speed, this.angle.y);

        Vector3d dx = speed;
        dx.scale(dt);

        this.point.add(dx);

        Vector3d angleSpeed = new Vector3d(0, this.wh, 0);
        Vector3d dOmega = angleSpeed;
        dOmega.scale(dt);

        this.angle.add(dOmega);

    }

    /**
     * Calculate forward speed.
     *
     * @param vf
     *            current forward speed
     * @param dt
     *            diff of time
     * @param time
     *            current time
     * @return new forward speed
     */
    private double calcForwardSpeed(double vf, double dt, double time) {

        double desiredSpeed = 0;

        // Acceleration
        double a = FORWARD_STOP_ACCELERATION;
        if (this.speeds.get(Speeds.FORWARD).active) {
            double take = time - this.speeds.get(Speeds.FORWARD).start;

            if (take < FORWARD_SPEED_2_TIMEOUT) {
                desiredSpeed = FORWARD_SPEED_1;
            } else {
                desiredSpeed = FORWARD_SPEED_2;
            }

            a = FORWARD_ACCELERATION;

        } else if (this.speeds.get(Speeds.BACKWARD).active) {
            double take = time - this.speeds.get(Speeds.BACKWARD).start;

            if (take < FORWARD_SPEED_2_TIMEOUT) {
                desiredSpeed = -FORWARD_SPEED_1;
            } else {
                desiredSpeed = -FORWARD_SPEED_2;
            }

            a = FORWARD_ACCELERATION;
        }

        return speedFollower(vf, dt, desiredSpeed, a);
    }

    /**
     * Calculate side speed.
     *
     * @param vs
     *            current side speed speed
     * @param dt
     *            diff of time
     * @param time
     *            current time
     * @return new forward speed
     */
    private double calcSideSpeed(double vs, double dt, double time) {

        double desiredSpeed = 0;

        if (this.speeds.get(Speeds.MOVE_LEFT).active) {

            desiredSpeed = -SIDE_SPEED;

        } else if (this.speeds.get(Speeds.MOVE_RIGHT).active) {

            desiredSpeed = SIDE_SPEED;

        }

        // Acceleration
        double a = SIDE_ACCELERATRION;

        return speedFollower(vs, dt, desiredSpeed, a);
    }

    /**
     * Calculate up/down speed.
     *
     * @param vu
     *            current up/down speed
     * @param dt
     *            diff of time
     * @param time
     *            current time
     * @return new forward speed
     */
    private double calcUpSpeed(double vu, double dt, double time) {

        double desiredSpeed = 0;

        if (this.speeds.get(Speeds.MOVE_UP).active) {

            desiredSpeed = UP_SPEED;

        } else if (this.speeds.get(Speeds.MOVE_DOWN).active) {

            desiredSpeed = -UP_SPEED;

        }

        // Acceleration
        double a = UP_ACCELERATRION;
        return speedFollower(vu, dt, desiredSpeed, a);
    }

    /**
     * @param v
     *            current speed
     * @param dt
     *            diff of time
     * @param desiredSpeed
     *            desired speed
     * @param a
     *            acceleration
     * @return new speed
     */
    private double speedFollower(double v, double dt, double desiredSpeed, double a) {

        if (v > desiredSpeed) {
            v -= a * dt;
            if (v < desiredSpeed) {
                v = desiredSpeed;
            }
        } else if (v < desiredSpeed) {
            v += a * dt;
            if (v > desiredSpeed) {
                v = desiredSpeed;
            }
        }

        return v;
    }

    /**
     * Calculate horizontal rotation speed.
     *
     * @param wh
     *            current horizontal rotation speed
     * @param dt
     *            diff of time
     * @param time
     *            current time
     * @return new horizontal rotation
     */
    private double calcHorizontallySpeed(double wh, double dt, double time) {

        double desiredSpeed = 0;

        double a = ROTATE_STOP_ACCELERATRION;

        if (this.speeds.get(Speeds.ROTATE_LEFT).active) {

            desiredSpeed = ROTATE_SPEED;
            a = ROTATE_ACCELERATRION;

        } else if (this.speeds.get(Speeds.ROTATE_RIGHT).active) {

            desiredSpeed = -ROTATE_SPEED;
            a = ROTATE_ACCELERATRION;

        }

        // Acceleration
        return speedFollower(wh, dt, desiredSpeed, a);
    }

    /**
     * Default constructor.
     */
    public SimpleMoveAnimator() {

        this.speeds = new EnumMap<SimpleMoveAnimator.Speeds, SimpleMoveAnimator.SpeedData>(SimpleMoveAnimator.Speeds.class);

        for (Speeds s : Speeds.values()) {
            this.speeds.put(s, new SpeedData());
        }
    }

    /**
     * Set speed active.
     *
     * @param speedDirection
     *            speed direction
     * @param active
     *            if is active
     */
    private void setSpeed(Speeds speedDirection, boolean active) {

        SpeedData speedData = this.speeds.get(speedDirection);
        if (speedData.active != active) {
            speedData.start = System.currentTimeMillis() / 1000d;
        }
        speedData.active = active;

        speedData.last = System.currentTimeMillis() / 1000d;

    }

    private void setRotateAngle(double h, double v) {
        // FIXME use speeds
        this.angle.y += h;
        this.angle.z += v;

    }

    public void rotateLeft(boolean start) {
        setSpeed(Speeds.ROTATE_LEFT, start);
    }

    public void rotateRight(boolean start) {
        setSpeed(Speeds.ROTATE_RIGHT, start);
    }

    public void moveForward(boolean start) {
        setSpeed(Speeds.FORWARD, start);
    }

    public void moveBackwards(boolean start) {
        setSpeed(Speeds.BACKWARD, start);
    }

    public void moveUp(boolean start) {
        setSpeed(Speeds.MOVE_UP, start);
    }

    public void moveDown(boolean start) {
        setSpeed(Speeds.MOVE_DOWN, start);
    }

    public void translateLeft(boolean start) {
        setSpeed(Speeds.MOVE_LEFT, start);
    }

    public void translateRight(boolean start) {
        setSpeed(Speeds.MOVE_RIGHT, start);
    }

    public void rotateHorizontally(double h) {
        setRotateAngle(h, 0);
    }

    public void rotateVertically(double v) {
        setRotateAngle(0, v);
    }

    @SuppressWarnings("unqualified-field-access")
    public String info() {

        String speedsStr = "";
        for (Speeds s : Speeds.values()) {
            SpeedData speedData = this.speeds.get(s);

            speedsStr += "" + s + ", active: " + speedData.active + ", last: " + speedData.last + ", start: " + speedData.start
                    + "\n";

        }

        return "KinematicsSimpleAnimator [\n" + "\np=" + format(this.point) + ",\n angle=" + formatAngle(this.angle)
                + ",\n lastTime=" + this.df.format(this.lastTime) + ",\n vf=" + this.df.format(this.vf) + ",\n vs="
                + this.df.format(this.vs) + ",\n wh=" + this.df.format(Math.toDegrees(this.wh)) + ",\n speeds:\n" + speedsStr

                + "]";
    }

    String format(Tuple3d tuple) {
        return "( " + this.df.format(tuple.x) + ", " + this.df.format(tuple.y) + ", " + this.df.format(tuple.z) + " )";

    }

    String formatAngle(Tuple3d tuple) {
        return "( " + this.df.format(Math.toDegrees(tuple.x)) + ", " + this.df.format(Math.toDegrees(tuple.y)) + ", "
                + this.df.format(Math.toDegrees(tuple.z)) + " )";

    }

    /**
     * @return the point
     */
    @Override
    public Point3d getPoint() {
        return this.point;
    }

    /**
     * @return the angle
     */
    @Override
    public Vector3d getAngle() {
        return this.angle;
    }

    /**
     * TODO move animator.
     *
     * @param pCamPosX
     * @param pCamPosY
     * @param pCamPosZ
     */
    public void setPoint(double pCamPosX, double pCamPosY, double pCamPosZ) {
        this.point.x = pCamPosX;
        this.point.y = pCamPosY;
        this.point.z = pCamPosZ;
    }

}
