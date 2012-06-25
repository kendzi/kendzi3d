package kendzi.josm.kendzi3d.jogl.model.export;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.geometry.Model;

public class ExportItem {
    private Model model;
    private Point3d point;
    private Vector3d scale;


    public ExportItem(Model model, Point3d point, Vector3d scale) {
        super();
        this.model = model;
        this.point = point;
        this.scale = scale;
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the point
     */
    public Point3d getPoint() {
        return this.point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(Point3d point) {
        this.point = point;
    }

    /**
     * @return the scale
     */
    public Vector3d getScale() {
        return this.scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(Vector3d scale) {
        this.scale = scale;
    }


}
