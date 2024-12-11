/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.editor;

import java.util.LinkedList;
import java.util.List;

import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener.EditorChangeListener;
import kendzi.math.geometry.point.Vector3dUtil;
import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/**
 * Implementation of simple arrow editor.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class ArrowEditorImp extends AbstractEditor implements ArrowEditor, ChangeAwareEditor {

    /**
     * Provide origin of editor. Origin point could be re-calculated before it is
     * displayed so it need be wrapped with that provider.
     */
    private transient Point3dProvider editorOriginProvider;

    /**
     * Normalized vector with direction of editor.
     */
    private Vector3dc vector;

    /**
     * Distance from editor origin to editor end.
     */
    private double length;

    /**
     * Offset for displaying active spot of editor. Offset will be scaled related to
     * distance of camera.
     */
    private double offset;

    /**
     * Shape of editor active spot.
     */
    private EditorType editorType = EditorType.ARROW_HEAD;

    /**
     * Listeners for editor change event.
     */
    private final List<EditorChangeListener> editorChangeListeners = new LinkedList<>();

    /**
     * Constructor.
     */
    public ArrowEditorImp() {
        super();

        editorOriginProvider = new Point3dProvider(new Vector3d());
        vector = new Vector3d(0, 1, 0);
        length = 1;
        offset = 0;
    }

    /**
     * Constructor.
     *
     * @param origin
     *            arrow origin
     * @param vector
     *            direction of arrow
     * @param length
     *            arrow length
     */
    public ArrowEditorImp(Vector3dc origin, Vector3dc vector, double length) {
        super();

        editorOriginProvider = new Point3dProvider(new Vector3d(origin));
        this.vector = vector;
        this.length = length;
        offset = 0;
    }

    @Override
    public Vector3dc arrowEnd() {
        Vector3dc point = getEditorOrigin();

        return new Vector3d(//
                point.x() + vector.x() * length, //
                point.y() + vector.y() * length, //
                point.z() + vector.z() * length);
    }

    @Override
    public Vector3dc getActiveSpot() {
        return arrowEnd();
    }

    @Override
    public Vector3dc getActiveSpot(Vector3dc camera) {

        double scaledOffset = spotFloatingOffset(camera);

        double spotDistance = length + scaledOffset;

        Vector3dc point = getEditorOrigin();

        return new Vector3d(//
                point.x() + vector.x() * spotDistance, //
                point.y() + vector.y() * spotDistance, //
                point.z() + vector.z() * spotDistance);
    }

    /**
     * Calculates spot offset which depends on distance from camera. To have better
     * presentation of active editor spot, it should be moved along arrow vector to
     * be little above edited object. That small offset should be depend on distance
     * from camera.
     *
     * @param camera
     *            camera location
     * @return offset of active spot from real length
     */
    private double spotFloatingOffset(Vector3dc camera) {

        return offset * distanse(camera);
    }

    private double distanse(Vector3dc camera) {
        return arrowEnd().distance(camera);

    }

    @Override
    public Double intersect(Ray3d selectionRay) {
        /*
         * This implementation is depended on distance from ray center and not actual
         * location of camera!
         */
        Vector3dc camera = selectionRay.getPoint();

        double distanse = distanse(camera);

        // XXX add support for screen height (should change editor size)
        return Ray3dUtil.intersect(selectionRay, getActiveSpot(camera), getEditorRadius() * distanse);
    }

    @Override
    public Vector3dc getEditorOrigin() {
        return editorOriginProvider.provide();
    }

    /**
     * @param vector
     *            the vector to set
     */
    public void setVector(Vector3dc vector) {
        this.vector = vector;
    }

    @Override
    public void addChangeListener(EditorChangeListener listener) {
        editorChangeListeners.add(listener);
    }

    @Override
    public EditorChangeEvent move(Ray3d moveRay, boolean finish) {

        Vector3dc editorOrigin = getEditorOrigin();

        Vector3dc normal = new Vector3d(getVector()).normalize();

        Ray3d arrowRay = new Ray3d(editorOrigin, normal);

        Vector3dc closestPointOnBaseRay = Ray3dUtil.closestPointOnBaseRay(moveRay, arrowRay);

        Vector3dc moveVector = Vector3dUtil.fromTo(editorOrigin, closestPointOnBaseRay);

        double lenghtOnEditor = normal.dot(moveVector);

        double length = lenghtOnEditor - spotFloatingOffset(moveRay.getPoint());

        setLength(length);

        ArrowEditorChangeEvent event = new ArrowEditorChangeEvent(finish, this, length, closestPointOnBaseRay);

        raiseEditorChange(event);

        return event;
    }

    protected void raiseEditorChange(ArrowEditorChangeEvent event) {
        for (EditorChangeListener listener : editorChangeListeners) {
            listener.onEditorChange(event);
        }
    }

    /**
     * @param point
     *            the point to set
     */
    public void setEditorOrigin(Vector3dc point) {
        editorOriginProvider = new Point3dProvider(new Vector3d(point));
    }

    /**
     * @param editorOriginProvider
     *            the editorOriginProvider to set
     */
    public void setEditorOrigin(Point3dProvider editorOriginProvider) {
        this.editorOriginProvider = editorOriginProvider;
    }

    @Override
    public Vector3dc getVector() {
        return vector;
    }

    /**
     * @return the length
     */
    @Override
    public double getLength() {
        return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * @return the editorType
     */
    @Override
    public EditorType getEditorType() {
        return editorType;
    }

    /**
     * @param editorType
     *            the editorType to set
     */
    public void setEditorType(EditorType editorType) {
        this.editorType = editorType;
    }

    /**
     * @return the offset
     */
    public double getOffset() {
        return offset;
    }

    /**
     * @param offset
     *            the offset to set
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

}
