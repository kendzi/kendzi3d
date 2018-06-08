package kendzi.josm.kendzi3d.data.producer;

import java.util.Collection;

import org.openstreetmap.josm.data.SelectionChangedListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListenerAdapter;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager;
import org.openstreetmap.josm.data.osm.event.DatasetEventManager.FireMode;
import org.openstreetmap.josm.gui.MainApplication;

import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.event.SelectionDataEvent;
import kendzi.josm.kendzi3d.data.event.UpdateDataEvent;

public class JosmDataEventSource extends DataSetListenerAdapter implements SelectionChangedListener {

    private final SelectionChangedListener selectionChangedListener;

    public JosmDataEventSource(DataEventListener dataEventListener) {
        super(e -> process(e, dataEventListener));
        selectionChangedListener = e -> process(e, dataEventListener);

        registerJosmEventSource();
    }

    @Override
    public void selectionChanged(Collection<? extends OsmPrimitive> primitives) {
        selectionChangedListener.selectionChanged(primitives);
    }

    public void registerJosmEventSource() {
        DatasetEventManager.getInstance().addDatasetListener(this, FireMode.IMMEDIATELY);
        DataSet.addSelectionListener(this);
    }

    public void deregisterJosmEventSource() {
        DatasetEventManager.getInstance().removeDatasetListener(this);
        DataSet.removeSelectionListener(this);
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

    public static void process(Collection<? extends OsmPrimitive> e, DataEventListener l) {
        l.add(new SelectionDataEvent(e));
    }
}
