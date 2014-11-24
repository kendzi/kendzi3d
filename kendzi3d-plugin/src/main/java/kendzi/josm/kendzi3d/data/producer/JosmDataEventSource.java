package kendzi.josm.kendzi3d.data.producer;

import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.event.UpdateDataEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager.FireMode;

public class JosmDataEventSource implements DataSetListenerAdapter.Listener {

    private final DataEventListener queue;

    private final DataConsumersMonitor dataConsumerMonitor;

    public JosmDataEventSource(DataEventListener queue, DataConsumersMonitor dataConsumerMonitor) {
        super();
        this.queue = queue;
        this.dataConsumerMonitor = dataConsumerMonitor;
    }

    public void registerJosmEventSource() {
        DatasetEventManager.getInstance().addDatasetListener(new DataSetListenerAdapter(this), FireMode.IMMEDIATELY);
    }

    @Override
    public void processDatasetEvent(AbstractDatasetChangedEvent pEvent) {

        if (!dataConsumerMonitor.isActiveConsumer()) {
            // no consumer, no open window, no data processing
            return;
        }

        if (Main.map == null) {
            // No map data, exit.
            return;
        }

        if (isDataChangeEvent(pEvent)) {
            queue.add(new NewDataEvent());
            return;
        }

        queue.add(new UpdateDataEvent());
    }

    private boolean isDataChangeEvent(AbstractDatasetChangedEvent event) {
        // XXX
        return event instanceof DataChangedEvent;
    }

}
