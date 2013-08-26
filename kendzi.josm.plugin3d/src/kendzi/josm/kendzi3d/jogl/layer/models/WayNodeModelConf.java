package kendzi.josm.kendzi3d.jogl.layer.models;

import javax.vecmath.Vector3d;

import kendzi.josm.kendzi3d.util.expression.fun.SimpleFunction;

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
    private SimpleFunction<Double> scale;

    /**
     * Model direction.
     */
    private SimpleFunction<Double> direction;

    /**
     * Model offset.
     */
    private SimpleFunction<Double> offset;

    /**
     * Model translation.
     */
    private SimpleFunction<Vector3d> translate;


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
    public SimpleFunction<Double> getScale() {
        return this.scale;
    }

    /**
     * @param simpleFunction the scale to set
     */
    public void setScale(SimpleFunction<Double> simpleFunction) {
        this.scale = simpleFunction;
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
    public SimpleFunction<Double> getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(SimpleFunction<Double> direction) {
        this.direction = direction;
    }

    /**
     * @return the offset
     */
    public SimpleFunction<Double> getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(SimpleFunction<Double> offset) {
        this.offset = offset;
    }

    /**
     * @return the translate
     */
    public SimpleFunction<Vector3d> getTranslate() {
        return translate;
    }

    /**
     * @param translate the translate to set
     */
    public void setTranslate(SimpleFunction<Vector3d> translate) {
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
