package kendzi.josm.kendzi3d.jogl.layer.models;

import javax.vecmath.Vector3d;

import kendzi.kendzi3d.expressions.expression.Expression;

import org.openstreetmap.josm.actions.search.SearchCompiler.Match;

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
     * Model translation.
     */
    private Vector3d translate;

    /**
     * Model direction.
     */
    private Expression direction;

    /**
     * @return the model
     */
    public String getModel() {
        return this.model;
    }

    /**
     * @param model the model to set
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
     * @param matcher the matcher to set
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
     * @param simpleFunction the scale to set
     */
    public void setScale(Expression simpleFunction) {
        this.scale = simpleFunction;
    }

    /**
     * @return the translate
     */
    public Vector3d getTranslate() {
        return this.translate;
    }

    /**
     * @param translate the translate to set
     */
    public void setTranslate(Vector3d translate) {
        this.translate = translate;
    }

    /**
     * @return the direction
     */
    public Expression getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
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
     * @param modelParameter the modelParameter to set
     */
    public void setModelParameter(String modelParameter) {
        this.modelParameter = modelParameter;
    }

}
