package kendzi.kendzi3d.buildings.model;

import java.awt.Color;

import org.joml.Vector2dc;

public class SphereNodeBuildingPart implements NodeBuildingPart {

    private Vector2dc point;

    private double height;

    private double radius;

    private Color facadeColor;

    private String facadeMaterialType;

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.buildings.model.NodeBuildingPart#getPoint()
     */
    @Override
    public Vector2dc getPoint() {
        return point;
    }

    /**
     * @param point
     *            the point to set
     */
    public void setPoint(Vector2dc point) {
        this.point = point;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.buildings.model.NodeBuildingPart#getHeight()
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @param radius
     *            the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @return the facadeColor
     */
    public Color getFacadeColor() {
        return facadeColor;
    }

    /**
     * @param facadeColor
     *            the facadeColor to set
     */
    public void setFacadeColor(Color facadeColor) {
        this.facadeColor = facadeColor;
    }

    /**
     * @return the facadeMaterialType
     */
    public String getFacadeMaterialType() {
        return facadeMaterialType;
    }

    /**
     * @param facadeMaterialType
     *            the facadeMaterialType to set
     */
    public void setFacadeMaterialType(String facadeMaterialType) {
        this.facadeMaterialType = facadeMaterialType;
    }
}
