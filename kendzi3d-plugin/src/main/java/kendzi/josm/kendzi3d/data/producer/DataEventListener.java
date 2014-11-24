package kendzi.josm.kendzi3d.data.producer;

import kendzi.josm.kendzi3d.data.event.DataEvent;

/**
 * Interface for data event storage for processing.
 */
public interface DataEventListener {

    /**
     * Adds new event to be processed.
     * 
     * @param dataEvent
     *            data event
     */
    public void add(DataEvent dataEvent);
}
