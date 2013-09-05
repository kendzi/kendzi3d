package kendzi.josm.kendzi3d.jogl.layer.models;

import kendzi.kendzi3d.expressions.expression.Expression;

import org.openstreetmap.josm.actions.search.SearchCompiler.Match;

/**
 * Model for node on way configuration.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class WayNodeModelConf {

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
     * Way node matcher.
     */
    private Match filter;

    /**
     * Model scale.
     */
    private Expression scale;

    /**
     * Model direction.
     */
    private Expression direction;

    /**
     * Model offset.
     */
    private Expression offset;

    /**
     * Model translation.
     */
    private Expression translate;


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
    public void setScale(Expression expression) {
        this.scale = expression;
    }

    /**
     * @return the filter
     */
    public Match getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Match filter) {
        this.filter = filter;
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
     * @return the offset
     */
    public Expression getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(Expression offset) {
        this.offset = offset;
    }

    /**
     * @return the translate
     */
    public Expression getTranslate() {
        return translate;
    }

    /**
     * @param translate the translate to set
     */
    public void setTranslate(Expression translate) {
        this.translate = translate;
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
