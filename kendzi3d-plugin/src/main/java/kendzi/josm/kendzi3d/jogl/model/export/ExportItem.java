package kendzi.josm.kendzi3d.jogl.model.export;

import kendzi.jogl.model.geometry.Model;
import org.joml.Vector3dc;

public class ExportItem {
    private Model model;
    private Vector3dc point;
    private Vector3dc scale;

    public ExportItem(Model model, Vector3dc point, Vector3dc scale) {
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
     * @param model
     *            the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the point
     */
    public Vector3dc getPoint() {
        return this.point;
    }

    /**
     * @param point
     *            the point to set
     */
    public void setPoint(Vector3dc point) {
        this.point = point;
    }

    /**
     * @return the scale
     */
    public Vector3dc getScale() {
        return this.scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(Vector3dc scale) {
        this.scale = scale;
    }

}
