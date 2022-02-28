package kendzi.josm.kendzi3d.jogl.layer.models;

import kendzi.kendzi3d.expressions.expression.Expression;
import org.openstreetmap.josm.data.osm.search.SearchCompiler.Match;

/**
 * Model for node configuration.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class NodeModelConf {

    /**
     * Model file key.
     */
    private String model;

    /**
     * Model parameter values.
     */
    private String modelParameter;

    /**
     * Model matcher.
     */
    private Match matcher;

    /**
     * Model scale.
     */
    private Expression scale;

    /**
     * Model direction.
     */
    private Expression direction;

    /**
     * Model translation after scale function is applied.
     */
    private Expression translate;

    /**
     * @return the model
     */
    public String getModel() {
        return this.model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the matcher
     */
    public Match getMatcher() {
        return this.matcher;
    }

    /**
     * @param matcher
     *            the matcher to set
     */
    public void setMatcher(Match matcher) {
        this.matcher = matcher;
    }

    /**
     * @return the scale
     */
    public Expression getScale() {
        return this.scale;
    }

    /**
     * @param simpleFunction
     *            the scale to set
     */
    public void setScale(Expression simpleFunction) {
        this.scale = simpleFunction;
    }

    /**
     * @return the direction
     */
    public Expression getDirection() {
        return direction;
    }

    /**
     * @param direction
     *            the direction to set
     */
    public void setDirection(Expression direction) {
        this.direction = direction;
    }

    /**
     * @return the modelParameter
     */
    public String getModelParameter() {
        return modelParameter;
    }

    /**
     * @param modelParameter
     *            the modelParameter to set
     */
    public void setModelParameter(String modelParameter) {
        this.modelParameter = modelParameter;
    }

    public Expression getTranslate() {
        return translate;
    }

    public void setTranslate(Expression translate) {
        this.translate = translate;
    }

}
