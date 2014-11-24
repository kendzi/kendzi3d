package kendzi.josm.kendzi3d.data.producer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import kendzi.josm.kendzi3d.data.DataSetFilterUtil;
import kendzi.josm.kendzi3d.data.Kendzi3dCore;
import kendzi.josm.kendzi3d.data.OsmId;
import kendzi.josm.kendzi3d.data.RebuildableWorldObject;
import kendzi.josm.kendzi3d.data.event.DataEvent;
import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.perspective.Perspective3D;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.josm.model.perspective.Perspective;
import kendzi.kendzi3d.world.BuildableWorldObject;
import kendzi.kendzi3d.world.WorldObject;
import kendzi.kendzi3d.world.quad.layer.Layer;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.DataSource;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.projection.Projection;

public class EditorObjectsProducer implements Runnable, DataEventListener {

    private static final Logger LOG = Logger.getLogger(EditorObjectsProducer.class);

    private final DataEventQueue eventQueue;

    private final Kendzi3dCore core;

    private LatLon center;

    @Inject
    public EditorObjectsProducer(Kendzi3dCore core, DataConsumersMonitor dataConsumersMonitor) {
        this.core = core;

        eventQueue = new DataEventQueue();

        center = new LatLon(0, 0);

        registerEventSource(eventQueue, dataConsumersMonitor);
    }

    private void registerEventSource(DataEventQueue eventQueue, DataConsumersMonitor dataConsumersMonitor) {

        JosmDataEventSource listener = new JosmDataEventSource(this, dataConsumersMonitor);

        listener.registerJosmEventSource();
    }

    @Override
    public void run() {

        while (true) {
            try {
                DataEvent event = null;
                while ((event = eventQueue.take()) != null) {
                    process(event);
                }
                // eventQueue.wait();
            } catch (InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOG.error("error when editor object was build", e);
            }
        }

    }

    private DataSet getDataSet(DataEvent event) {
        // always take the newest data set form JOSM
        return Main.main.getCurrentDataSet();
    }

    private void process(DataEvent event) {
        DataSet dataSet = getDataSet(event);

        Perspective3D perspective = core.getPerspective3d();

        if (perspective == null || event instanceof NewDataEvent) {
            Projection proj = Main.getProjection();
            center = calculateCenter(dataSet, proj);
            perspective = calculatePerspective(center, proj);
            core.setPerspective3d(perspective);
        }

        for (Layer layer : core.getLayers()) {

            Set<OsmId> currentIds = core.getOsmIds(layer);
            List<OsmId> filteredIds = DataSetFilterUtil.filter(layer, dataSet, perspective);

            RebuildStatus status = combine(currentIds, filteredIds);

            createNewEditorObjects(dataSet, status.getNewIds(), layer, perspective);

            updateEditorObjects(dataSet, status.getUpdateIds(), layer, perspective);

            removeEditorObjects(status.getRemoveIds(), layer);

        }
    }

    private Perspective3D calculatePerspective(LatLon centerLatLon, Projection proj) {
        EastNorth center = proj.latlon2eastNorth(centerLatLon);

        LatLon l1 = proj.eastNorth2latlon(center.add(1, 0));
        LatLon l2 = proj.eastNorth2latlon(center.add(-1, 0));

        double dist = l1.greatCircleDistance(l2);
        // XXX
        double scale = dist / 2d;

        return new Perspective3D(scale, center.getX(), center.getY());
    }

    private LatLon calculateCenter(DataSet dataset, Projection proj) {

        double maxX = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;

        double maxY = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        // Bbox2d

        if (dataset.dataSources.size() > 0) {
            // it is dataset connected with OSM. Get bounds.
            for (DataSource source : dataset.dataSources) {
                // create area from data bounds
                LatLon min = source.bounds.getMin();
                LatLon max = source.bounds.getMax();

                if (minX > min.getX()) {
                    minX = min.getX();
                }
                if (minY > min.getY()) {
                    minY = min.getY();
                }
                if (maxX < max.getX()) {
                    maxX = max.getX();
                }
                if (maxY < max.getY()) {
                    maxY = max.getY();
                }
            }
        } else {
            // it is newly created dataset not connected with OSM
            for (Node n : dataset.getNodes()) {
                // create area from data bounds
                LatLon cord = n.getCoor();

                if (cord == null) {
                    continue;
                }

                if (minX > cord.getX()) {
                    minX = cord.getX();
                }
                if (minY > cord.getY()) {
                    minY = cord.getY();
                }
                if (maxX < cord.getX()) {
                    maxX = cord.getX();
                }
                if (maxY < cord.getY()) {
                    maxY = cord.getY();
                }
            }

        }
        return new LatLon((maxY + minY) / 2d, (maxX + minX) / 2d);

    }

    private void createNewEditorObjects(DataSet dataSet, Set<OsmId> newIds, Layer layer, Perspective perspective) {
        for (OsmId osmId : newIds) {
            WorldObject buildModel = prepareEditorObject(dataSet, layer, osmId, perspective);

            if (buildModel != null) {

                if (buildModel instanceof BuildableWorldObject) {

                    ((BuildableWorldObject) buildModel).buildWorldObject();
                }

                core.add(layer, osmId, buildModel);
            }
        }
    }

    private void updateEditorObjects(DataSet dataSet, Set<OsmId> updateIds, Layer layer, Perspective perspective) {
        for (OsmId osmId : updateIds) {

            OsmPrimitive primitive = dataSet.getPrimitiveById(osmId);

            EditableObject editableObject = core.load(layer, osmId);
            if (editableObject instanceof RebuildableWorldObject) {
                ((RebuildableWorldObject) editableObject).rebuildWorldObject(primitive, perspective);
            } else {
                throw new IllegalStateException("can't rebuild osm object: " + osmId + " for layer " + layer
                        + " because it don't support it");
            }
            // if (editableObject instanceof BuildableWorldObject) {
            // ((BuildableWorldObject) editableObject).buildWorldObject();
            // }
        }
    }

    private void removeEditorObjects(Set<OsmId> removeIds, Layer layer) {
        for (OsmId osmId : removeIds) {
            core.remove(layer, osmId);
        }
    }

    private WorldObject prepareEditorObject(DataSet dataSet, Layer layer, OsmId osmId, Perspective perspective) {

        OsmPrimitive primitive = dataSet.getPrimitiveById(osmId);
        WorldObject buildModel = null;

        if (primitive instanceof Node) {
            buildModel = layer.buildModel((Node) primitive, perspective);
        } else if (primitive instanceof Way) {
            buildModel = layer.buildModel((Way) primitive, perspective);
        } else if (primitive instanceof Relation) {
            buildModel = layer.buildModel((Relation) primitive, perspective);
        }

        return buildModel;
    }

    private RebuildStatus combine(Set<OsmId> currentIds, List<OsmId> filteredIds) {

        // Set<OsmId> currentIdsSet = new HashSet<OsmId>(currentIds);

        Set<OsmId> newIds = new HashSet<OsmId>(filteredIds);
        Set<OsmId> updateIds = new HashSet<OsmId>(filteredIds.size());
        Set<OsmId> removeIds = new HashSet<OsmId>(filteredIds.size());

        // find all ids which are not in filtered set
        for (OsmId osmId : currentIds) {
            if (!newIds.contains(osmId)) {
                removeIds.add(osmId);
            }
        }

        Iterator<OsmId> i = newIds.iterator();
        while (i.hasNext()) {
            OsmId newIdCandidate = i.next();

            if (currentIds.contains(newIdCandidate)) {
                // id id is in current data we need update it
                // remove id from new ids set
                i.remove();

                // add to update set
                updateIds.add(newIdCandidate);

            }
        }

        return new RebuildStatus(newIds, updateIds, removeIds);
    }

    private static class RebuildStatus {
        private Set<OsmId> newIds;
        private Set<OsmId> updateIds;
        private Set<OsmId> removeIds;

        public RebuildStatus(Set<OsmId> newIds, Set<OsmId> updateIds, Set<OsmId> removeIds) {
            super();
            this.newIds = newIds;
            this.updateIds = updateIds;
            this.removeIds = removeIds;
        }

        /**
         * @return the newIds
         */
        public Set<OsmId> getNewIds() {
            return newIds;
        }

        /**
         * @param newIds
         *            the newIds to set
         */
        public void setNewIds(Set<OsmId> newIds) {
            this.newIds = newIds;
        }

        /**
         * @return the updateIds
         */
        public Set<OsmId> getUpdateIds() {
            return updateIds;
        }

        /**
         * @param updateIds
         *            the updateIds to set
         */
        public void setUpdateIds(Set<OsmId> updateIds) {
            this.updateIds = updateIds;
        }

        /**
         * @return the removeIds
         */
        public Set<OsmId> getRemoveIds() {
            return removeIds;
        }

        /**
         * @param removeIds
         *            the removeIds to set
         */
        public void setRemoveIds(Set<OsmId> removeIds) {
            this.removeIds = removeIds;
        }

    }

    public void addDbEvent(DataEvent dataEvent) {
        eventQueue.add(dataEvent);
    }

    @Override
    public void add(DataEvent dataEvent) {
        eventQueue.add(dataEvent);
    }
}
