/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.Map;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.Measurement;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.measurement.MeasurementKey;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

/**
 * Rectangle roof builder parameters.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class RectangleRoofTypeConf {
    private PolygonWithHolesList2d buildingPolygon;
    private Point2d[] rectangleContur;
    private double recHeight;
    private double recWidth;
    private Integer roofTypeParameter;
    private Map<MeasurementKey, Measurement> measurements;
    private RoofMaterials roofTextureData;

    public RectangleRoofTypeConf(PolygonWithHolesList2d transfBuildingPolygon, Point2d[] rectangleContur, double recHeight,
            double recWidth, Integer roofTypeParameter, Map<MeasurementKey, Measurement> measurements,
            RoofMaterials roofTextureData) {
        super();
        this.buildingPolygon = transfBuildingPolygon;
        this.rectangleContur = rectangleContur;
        this.recHeight = recHeight;
        this.recWidth = recWidth;
        this.roofTypeParameter = roofTypeParameter;
        this.measurements = measurements;
        this.roofTextureData = roofTextureData;
    }

    /**
     * @return the rectangleContur
     */
    public Point2d[] getRectangleContur() {
        return rectangleContur;
    }

    /**
     * @param rectangleContur the rectangleContur to set
     */
    public void setRectangleContur(Point2d[] rectangleContur) {
        this.rectangleContur = rectangleContur;
    }

    /**
     * @return the recHeight
     */
    public double getRecHeight() {
        return recHeight;
    }

    /**
     * @param recHeight the recHeight to set
     */
    public void setRecHeight(double recHeight) {
        this.recHeight = recHeight;
    }

    /**
     * @return the recWidth
     */
    public double getRecWidth() {
        return recWidth;
    }

    /**
     * @param recWidth the recWidth to set
     */
    public void setRecWidth(double recWidth) {
        this.recWidth = recWidth;
    }

    /**
     * @return the roofTypeParameter
     */
    public Integer getRoofTypeParameter() {
        return roofTypeParameter;
    }

    /**
     * @param roofTypeParameter the roofTypeParameter to set
     */
    public void setRoofTypeParameter(Integer roofTypeParameter) {
        this.roofTypeParameter = roofTypeParameter;
    }

    /**
     * @return the measurements
     */
    public Map<MeasurementKey, Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * @param measurements the measurements to set
     */
    public void setMeasurements(Map<MeasurementKey, Measurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * @return the roofTextureData
     */
    public RoofMaterials getRoofTextureData() {
        return roofTextureData;
    }

    /**
     * @param roofTextureData the roofTextureData to set
     */
    public void setRoofTextureData(RoofMaterials roofTextureData) {
        this.roofTextureData = roofTextureData;
    }

    /**
     * @return the buildingPolygon
     */
    public PolygonWithHolesList2d getBuildingPolygon() {
        return buildingPolygon;
    }

    /**
     * @param buildingPolygon the buildingPolygon to set
     */
    public void setBuildingPolygon(PolygonWithHolesList2d buildingPolygon) {
        this.buildingPolygon = buildingPolygon;
    }
}
