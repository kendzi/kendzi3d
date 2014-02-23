/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi3d.light.dto;

import java.awt.Color;

import kendzi3d.light.dao.LightDao;

/**
 * Dto for light source configuration.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public class LightConfiguration {

    /**
     * Direction of light source.
     */
    private double direction;

    /**
     * Angle from ground for light source
     */
    private double angle;

    /**
     * Ambient color of light source.
     */
    private Color ambientColor;

    /**
     * Diffuse color of light source.
     */
    private Color diffuseColor;

    public LightConfiguration() {
        this(LightDao.DEFAULT_DIRECTION, LightDao.DEFAULT_ANGLE, LightDao.DEFAULT_AMBIENT_COLOR, LightDao.DEFAULT_DIFFUSE_COLOR);
    }

    public LightConfiguration(double direction, double angle, Color ambientColor, Color diffuseColor) {
        super();
        this.direction = direction;
        this.angle = angle;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Color ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
}
