package kendzi.math.geometry.skeleton;

import java.util.ArrayList;
import java.util.List;

import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;

import org.apache.log4j.Logger;

public class LavUtil {

    /** Log. */
    private static final Logger log = Logger.getLogger(LavUtil.class);

    /**
     * Check if two vertex are in the same lav.
     * 
     * @param v1
     *            vertex 1
     * @param v2
     *            vertex 2
     * @return if two vertex are in the same lav
     */
    public static boolean isSameLav(VertexEntry2 v1, VertexEntry2 v2) {
        if (v1.list() == null || v2.list() == null) {
            return false;
        }
        return v1.list() == v2.list();
    }

    public static void removeFromLav(VertexEntry2 vertex) {
        if (vertex == null || vertex.list() == null) {
            // if removed or not in list, skip
            return;
        }

        vertex.remove();
    }

    /**
     * Cuts all vertex after given startVertex and before endVertex. start and
     * and vertex are _included_ in cut result.
     * 
     * @param startVertex
     *            start vertex
     * @param endVertex
     *            end vertex
     * @return list of vertex in the middle between start and end vertex
     */
    public static List<VertexEntry2> cutLavPart(VertexEntry2 startVertex, VertexEntry2 endVertex) {

        if (log.isDebugEnabled()) {
            log.debug("cutLavPart: startVertex: " + startVertex.v + ", endVertex: " + endVertex.v + ", lav: "
                    + lavToString(startVertex));
        }

        // if (!isSameLav(startVertex, endVertex)) {
        // throw new
        // IllegalArgumentException("end vertex can't be found in start vertex lav");
        // }

        List<VertexEntry2> ret = new ArrayList<Skeleton.VertexEntry2>();

        int size = startVertex.list().size();

        VertexEntry2 next = startVertex;

        for (int i = 0; i < size - 1; i++) {

            VertexEntry2 current = next;

            next = current.next();


            current.remove();

            ret.add(current);

            if (current == endVertex) {
                return ret;
            }
        }

        throw new IllegalStateException("end vertex can't be found in start vertex lav");
    }

    public static String lavToString(VertexEntry2 startVertex) {
        StringBuffer sb = new StringBuffer();

        int size = startVertex.list().size();
        VertexEntry2 next = startVertex;

        for (int i = 0; i < size - 1; i++) {
            sb.append(next.v);
            sb.append(", ");

            next = next.next();
        }

        return sb.toString();
    }

    /**
     * Split given lav into two new lavs. Given vertex is not included in result
     * lavs. Split index is related from given vertex.
     * 
     * @param vertex
     *            vertex with lav
     * @param splitIndex
     *            split index
     * @return two new lavs created from split
     */
    public static SplitSlavs splitLav(VertexEntry2 vertex, int splitIndex) {

        CircularList<VertexEntry2> newLawLeft = new CircularList<VertexEntry2>();
        CircularList<VertexEntry2> newLawRight = new CircularList<VertexEntry2>();

        int sizeLav = vertex.list().size();

        if (splitIndex < 3 || splitIndex > sizeLav - 2) {
            throw new RuntimeException(String.format(
                    "After split each lav need to have at least two nodes! Split index: %s lav size: %s", splitIndex, sizeLav));
        }

        // skip first vertex, it will be skip in result
        VertexEntry2 nextVertex = vertex.next();

        for (int i = 1; i < sizeLav; i++) {

            VertexEntry2 currentVertex = nextVertex;
            nextVertex = nextVertex.next();

            currentVertex.remove();

            if (i < splitIndex) {
                newLawRight.addLast(currentVertex);
            } else {
                newLawLeft.addLast(currentVertex);
            }
        }

        return new SplitSlavs(newLawLeft, newLawRight);
    }

    public static class SplitSlavs {
        private CircularList<VertexEntry2> newLawLeft = new CircularList<VertexEntry2>();
        private CircularList<VertexEntry2> newLawRight = new CircularList<VertexEntry2>();

        public CircularList<VertexEntry2> getNewLawLeft() {
            return newLawLeft;
        }

        public CircularList<VertexEntry2> getNewLawRight() {
            return newLawRight;
        }

        public SplitSlavs(CircularList<VertexEntry2> newLawLeft, CircularList<VertexEntry2> newLawRight) {
            this.newLawLeft = newLawLeft;
            this.newLawRight = newLawRight;
        }
    }

    /**
     * Moves all nodes from given vertex lav, to new lav. All moved nodes are
     * added at the end of lav. The lav end is determined by first added vertex
     * to lav.
     * 
     * @param vertex
     *            vertex
     * @param newLaw
     *            new lav
     */
    public static void moveAllVertexToLavEnd(VertexEntry2 vertex, CircularList<VertexEntry2> newLaw) {
        int size = vertex.list().size();
        for (int i = 0; i < size; i++) {
            VertexEntry2 ver = vertex;
            vertex = vertex.next();
            ver.remove();
            newLaw.addLast(ver);
        }
    }

    /**
     * Add all vertex from "merged" lav into "base" lav. Vertex are added before
     * base vertex. Merged vertex order is reversed.
     * 
     * @param base
     *            vertex from lav where vertex will be added
     * @param merged
     *            vertex from lav where vertex will be removed
     */
    public static void mergeBeforeBaseVertex(VertexEntry2 base, VertexEntry2 merged) {

        if (log.isDebugEnabled()) {
            log.debug("base: " + base.v + ", merged: " + merged.v + ", lavs: base" + lavToString(base) + " merged"
                    + lavToString(merged));
        }

        int size = merged.list().size();

        for (int i = 0; i < size; i++) {
            VertexEntry2 nextMerged = merged.next();
            nextMerged.remove();

            base.addPrevious(nextMerged);
        }
    }

}
