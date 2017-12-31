package kendzi.josm.kendzi3d.data.event;

import java.util.Collection;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class SelectionDataEvent implements DataEvent {

    final Collection<? extends OsmPrimitive> primitives;

    public SelectionDataEvent(Collection<? extends OsmPrimitive> primitives) {
        this.primitives = primitives;
    }

    public Collection<? extends OsmPrimitive> getPrimitives() {
        return primitives;
    }
}
