package kendzi.josm.kendzi3d.data.producer;

import java.util.concurrent.LinkedBlockingQueue;

import kendzi.josm.kendzi3d.data.event.DataEvent;
import kendzi.josm.kendzi3d.data.event.NewDataEvent;
import kendzi.josm.kendzi3d.data.event.UpdateDataEvent;

public class DataEventQueue {
    // FIXME rewrite it
    private final LinkedBlockingQueue<DataEvent> eventQueue = new LinkedBlockingQueue<DataEvent>();

    public synchronized void add(DataEvent dataEvent) {
        addDbEvent(dataEvent);
    }

    private void addUpdateDataEvent(UpdateDataEvent dataEvent) {
        // XXX
        if (!eventQueue.isEmpty()) {
            for (DataEvent event : eventQueue) {
                if (event instanceof UpdateDataEvent) {
                    // no need to add second update event, skip
                    return;
                }
            }
        }
        eventQueue.add(dataEvent);
    }

    private void addNewDataEvent(NewDataEvent dataEvent) {
        eventQueue.clear();
        eventQueue.add(dataEvent);
    }

    private synchronized void addEdEvent(UpdateDataEvent updateDataEvent) {

        eventQueue.add(updateDataEvent);
    }

    private void addDbEvent(DataEvent dataEvent) {

        if (dataEvent instanceof NewDataEvent) {
            addNewDataEvent((NewDataEvent) dataEvent);
        } else if (dataEvent instanceof UpdateDataEvent) {
            addUpdateDataEvent((UpdateDataEvent) dataEvent);
        } else {
            throw new IllegalArgumentException("unknown type of argument: " + dataEvent);
        }
    }

    public DataEvent take() throws InterruptedException {
        return eventQueue.take();
    }
}
