package kendzi.josm.kendzi3d.data.producer;

import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager.FireMode;
import org.openstreetmap.josm.gui.MainApplication;

import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.event.UpdateDataEvent;

public class JosmDataEventSource extends DataSetListenerAdapter {

    public JosmDataEventSource(DataEventListener dataEventListener) {
        super(e -> process(e, dataEventListener));
        registerJosmEventSource();
    }

    @Override
    protected void finalize() {
        deregisterJosmEventSource();
    }

    public void deregisterJosmEventSource() {
        DatasetEventManager.getInstance().removeDatasetListener(this);
    }

    public void registerJosmEventSource() {
        DatasetEventManager.getInstance().addDatasetListener(this, FireMode.IMMEDIATELY);
    }

    public static void process(AbstractDatasetChangedEvent e, DataEventListener l) {

        if (MainApplication.getMap() != null) {

            if (e instanceof DataChangedEvent) {
                l.add(new NewDataEvent());
            } else {
                l.add(new UpdateDataEvent());
            }
        }
    }
}
