package kendzi.josm.kendzi3d.data;

import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveId;

public class OsmId implements PrimitiveId {
    // / XXX SimplePrimitiveId
    private long uniqueId;

    private OsmPrimitiveType type;

    public OsmId(long uniqueId, OsmPrimitiveType type) {
        super();
        this.uniqueId = uniqueId;
        this.type = type;
    }

    /**
     * @return the uniqueId
     */
    @Override
    public long getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueId
     *            the uniqueId to set
     */
    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the type
     */
    @Override
    public OsmPrimitiveType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(OsmPrimitiveType type) {
        this.type = type;
    }

    @Override
    public boolean isNew() {
        return uniqueId < 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (type == null ? 0 : type.hashCode());
        result = prime * result + (int) (uniqueId ^ uniqueId >>> 32);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OsmId other = (OsmId) obj;
        if (type != other.type) {
            return false;
        }
        if (uniqueId != other.uniqueId) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OsmId [uniqueId=" + uniqueId + ", type=" + type + "]";
    }

}
