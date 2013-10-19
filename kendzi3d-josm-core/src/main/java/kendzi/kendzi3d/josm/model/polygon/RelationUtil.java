package kendzi.kendzi3d.josm.model.polygon;

import java.util.ArrayList;
import java.util.List;

import kendzi.util.StringUtil;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Util for relation processing.
 * 
 * @author Tomasz Kedziora (Kendzi)
 * 
 */
public final class RelationUtil {

    private RelationUtil() {
        //
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterInnerWays(Relation pRelation) {
        return filterWayByRole(pRelation, "inner", null);
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterOuterWays(Relation pRelation) {
        List<Way> outersParts = filterWayByRole(pRelation, "outer", null);
        outersParts.addAll(filterWayByRole(pRelation, null, null));

        return outersParts;
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterInnerOpenWay(Relation pRelation) {
        return filterWayByRole(pRelation, "inner", false);
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterInnerClosedWay(Relation pRelation) {
        return filterWayByRole(pRelation, "inner", true);
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterOuterOpenWays(Relation pRelation) {
        List<Way> outersParts = filterWayByRole(pRelation, "outer", false);
        outersParts.addAll(filterWayByRole(pRelation, null, false));
        return outersParts;
    }

    /**
     * @param pRelation
     * @return
     */
    public static List<Way> filterOuterClosedWays(Relation pRelation) {
        List<Way> outersClosed = filterWayByRole(pRelation, "outer", true);
        outersClosed.addAll(filterWayByRole(pRelation, null, true));
        return outersClosed;
    }

    private static List<Way> filterWayByRole(Relation pRelation, String role, Boolean closed) {
        List<Way> ret = new ArrayList<Way>();

        for (int i = 0; i < pRelation.getMembersCount(); i++) {
            RelationMember member = pRelation.getMember(i);

            if (!member.isWay()) {
                continue;
            }

            Way way = member.getWay();

            if (closed != null && way.isClosed() != closed) {
                continue;
            }

            if (StringUtil.isBlankOrNull(member.getRole()) && StringUtil.isBlankOrNull(role)) {
                ret.add(way);
                continue;
            }

            if (StringUtil.equalsOrNulls(role, member.getRole())) {
                ret.add(way);
            }
        }
        return ret;
    }
}
