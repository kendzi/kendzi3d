package kendzi.josm.kendzi3d.data.producer;

/**
 * Monitor number of data consumers. If no data consumer is active there is no
 * need to process data and transform it to 3d.
 */
public class DataConsumersMonitor {

    private transient int numOfConsumers = 0;

    public synchronized void addDataConsumer() {
        numOfConsumers++;
    }

    public synchronized void removeDataConsumer() {
        numOfConsumers--;
        if (numOfConsumers < 0) {
            numOfConsumers = 0;
            throw new IllegalStateException("number of active data consumers can't be below zero");
        }
    }

    public boolean isActiveConsumer() {
        return numOfConsumers > 0;
    }

}
