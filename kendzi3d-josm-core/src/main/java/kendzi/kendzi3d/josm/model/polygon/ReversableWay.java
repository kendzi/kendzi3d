package kendzi.kendzi3d.josm.model.polygon;

import org.openstreetmap.josm.data.osm.Way;

public class ReversableWay {
    private Way way;
    private boolean reversed;

    public ReversableWay(Way way, boolean reversed) {
        super();
        this.way = way;
        this.reversed = reversed;
    }

    /**
     * @return the way
     */
    public Way getWay() {
        return this.way;
    }

    /**
     * @param way
     *            the way to set
     */
    public void setWay(Way way) {
        this.way = way;
    }

    /**
     * @return the reversed
     */
    public boolean isReversed() {
        return this.reversed;
    }

    /**
     * @param reversed
     *            the reversed to set
     */
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

}
