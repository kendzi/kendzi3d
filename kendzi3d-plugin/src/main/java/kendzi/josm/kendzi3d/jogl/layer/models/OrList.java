package kendzi.josm.kendzi3d.jogl.layer.models;

import java.util.List;

import org.openstreetmap.josm.data.osm.search.SearchCompiler.Match;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/**
 * Or match on list.
 *
 * @author Tomasz Kędziora (kendzi)
 *
 */
public class OrList extends Match {
    private final List<Match> lhs;

    /**
     * @param lhs list of match
     */
    public OrList(List<Match> lhs) {
        this.lhs = lhs;
    }

    @Override
    public boolean match(OsmPrimitive osm) {
        for (Match m : this.lhs) {
            if (m.match(osm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String ret = "";
        for (Match m : this.lhs) {
            ret += " || " + m;
        }
        return ret;
    }

    /**
     * @return list of match
     */
    public List<Match> getLhs() {
        return this.lhs;
    }
}
