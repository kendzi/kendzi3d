package kendzi.josm.kendzi3d.data;

import org.openstreetmap.josm.data.osm.PrimitiveId;

/**
 * Indicates connection to main osm primitive object. Not always 3d model have
 * clean master primitive! In that case one of them should be chosen to be
 * master. Chose have to be repeatable!
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public interface OsmPrimitiveWorldObject {

    /**
     * Gets master osm primitive object
     * 
     * @return root primitive object for 3d model
     */
    PrimitiveId getPrimitiveId();
}
