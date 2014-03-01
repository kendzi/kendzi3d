/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.photos;

import javax.vecmath.Vector3d;

public class Photo {

    double lat;
    double lon;

    double height;


    //http://en.wikipedia.org/wiki/Coordinate_rotation
    /**
     * rotate on axe Y
     */
    double yaw;

    /**
     * rotate on axe X
     */
    double roll;

    /**
     * rotate on axe Z
     */
    double pitch;

    double angleWidth;
    double angleHeight;


    String path;

    double transparent;

    public Vector3d getRotate() {
        return new Vector3d(roll, yaw, pitch);
    }

    public void setRotate(double roll, double yaw, double pitch) {
        this.roll = roll;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return this.lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return this.lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the yaw
     */
    public double getYaw() {
        return yaw;
    }

    /**
     * @param yaw the yaw to set
     */
    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    /**
     * @return the roll
     */
    public double getRoll() {
        return roll;
    }

    /**
     * @param roll the roll to set
     */
    public void setRoll(double roll) {
        this.roll = roll;
    }

    /**
     * @return the pitch
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @param pitch the pitch to set
     */
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    /**
     * @return the angleWidth
     */
    public double getAngleWidth() {
        return angleWidth;
    }

    /**
     * @param angleWidth the angleWidth to set
     */
    public void setAngleWidth(double angleWidth) {
        this.angleWidth = angleWidth;
    }

    /**
     * @return the angleHeight
     */
    public double getAngleHeight() {
        return angleHeight;
    }

    /**
     * @param angleHeight the angleHeight to set
     */
    public void setAngleHeight(double angleHeight) {
        this.angleHeight = angleHeight;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the transparent
     */
    public double getTransparent() {
        return transparent;
    }

    /**
     * @param transparent the transparent to set
     */
    public void setTransparent(double transparent) {
        this.transparent = transparent;
    }


}
