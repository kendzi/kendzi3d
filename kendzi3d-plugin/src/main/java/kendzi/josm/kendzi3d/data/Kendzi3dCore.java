package kendzi.josm.kendzi3d.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import kendzi.jogl.camera.Camera;
import kendzi.josm.kendzi3d.data.perspective.Perspective3D;
import kendzi.josm.kendzi3d.data.perspective.Perspective3dProvider;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditorCore;
import kendzi.kendzi3d.world.quad.layer.Layer;

/**
 * Implementation of editor core.
 *
 */
public class Kendzi3dCore implements EditorCore, Perspective3dProvider {

    private List<EditableObject> objects = new ArrayList<>(1000);

    private final Map<Layer, Map<OsmId, EditableObject>> layerObjects = new IdentityHashMap<Layer, Map<OsmId, EditableObject>>();

    @Inject
    private List<Layer> layers;

    private volatile boolean objectsHasChanged = true;

    private transient Perspective3D perspective3d;

    /**
     * Adds new editable object to given layer.
     *
     * @param layer
     *            the layer
     * @param id
     *            the osm object identifier
     * @param obj
     *            the editable object
     */
    public void add(Layer layer, OsmId id, EditableObject obj) {
        getOrAddLayer(layer).put(id, obj);
        objectsHasChanged = true;
    }

    /**
     * Removes editable object from given layer.
     *
     * @param layer
     *            the layer
     * @param id
     *            the osm object identifier
     *
     */
    public void remove(Layer layer, OsmId id) {
        getOrAddLayer(layer).remove(id);
        objectsHasChanged = true;
    }

    /**
     * Clears all objects from given layer.
     *
     * @param layer
     *            the layer
     */
    public void clean(Layer layer) {
        getOrAddLayer(layer).clear();
        objectsHasChanged = true;
    }

    /**
     * Loads object with given layer.
     *
     * @param layer
     *            the layer
     * @param osmId
     *            the osm object identifier
     * @return the editable object
     */
    public EditableObject load(Layer layer, OsmId osmId) {

        Map<OsmId, EditableObject> objects = layerObjects.get(layer);

        if (objects == null) {
            return null;
        }

        return objects.get(osmId);
    }

    private Map<OsmId, EditableObject> getOrAddLayer(Layer layer) {
        Map<OsmId, EditableObject> layerCache = layerObjects.get(layer);
        if (layerCache == null) {
            layerCache = new HashMap<OsmId, EditableObject>(300);
            layerObjects.put(layer, layerCache);
        }
        return layerCache;
    }

    @Override
    public List<EditableObject> getEditableObjects() {
        if (objectsHasChanged) {
            // to speed up, render is made on local array not synchronized map.
            try {
                List<EditableObject> localCopy = makeCopyOfObjects();
                objects = localCopy;
                objectsHasChanged = false;
            } catch (ConcurrentModificationException e) {
                /*
                 * Don't care about concurrent exception, new object will be
                 * processed in next frame.
                 */
            }
        }
        return objects;
    }

    private List<EditableObject> makeCopyOfObjects() {
        List<EditableObject> objects = new ArrayList<>(1000);

        for (Layer layer : layers) {
            Map<OsmId, EditableObject> layerCashe = layerObjects.get(layer);
            if (layerCashe == null) {
                continue;
            }
            for (OsmId osmId : layerCashe.keySet()) {
                EditableObject editableObject = layerCashe.get(osmId);

                objects.add(editableObject);
            }
        }
        return objects;
    }

    @Override
    public Camera getCamera() {
        throw new RuntimeException("TODO");
    }

    /**
     * Gets all layers.
     *
     * @return all layers
     */
    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * Gets all osm identifiers per layer.
     *
     * @param layer
     *            the layer
     * @return all osm identifiers per layer
     */
    public Set<OsmId> getOsmIds(Layer layer) {

        Map<OsmId, EditableObject> objects = layerObjects.get(layer);

        if (objects == null) {
            return Collections.emptySet();
        }

        return objects.keySet();
    }

    /**
     * @return the perspective3d
     */
    @Override
    public Perspective3D getPerspective3d() {
        return perspective3d;
    }

    /**
     * @param perspective3d
     *            the perspective3d to set
     */
    public void setPerspective3d(Perspective3D perspective3d) {
        this.perspective3d = perspective3d;
    }

}
