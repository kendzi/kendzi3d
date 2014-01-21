package kendzi.math.geometry.skeleton;

import static kendzi.math.geometry.TestUtil.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.skeleton.LavUtil.SplitSlavs;
import kendzi.math.geometry.skeleton.Skeleton.EdgeEntry;
import kendzi.math.geometry.skeleton.Skeleton.EdgeEvent;
import kendzi.math.geometry.skeleton.Skeleton.SkeletonEvent;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;
import kendzi.math.geometry.skeleton.events.PickEvent;

import org.junit.Test;

public class SkeletonInternalTest {

    public void testCalcB2() {

        VertexEntry2 vertex = new VertexEntry2();
        vertex.v = new Point2d(-1.0, -1.0);
        vertex.e_a = new EdgeEntry(p(-2.0, 0.0), p(-1.0, -1.0));
        // vertex.e_a.b
        vertex.e_b = new EdgeEntry(p(-1.0, -1.0), p(0.0, 0.0));
        vertex.bisector = new Ray2d(p(-1.0, -1.0), v(0.0, 1.414213562373095));

        EdgeEntry edge = new EdgeEntry(p(1.0, 1.0), p(-1.0, 1.0));
        // edge.bisectorNext = new
        // LineLinear2d(-1.7071067811865475,-0.7071067811865475,-1.0);
        // edge.bisectorPrevious = new
        // LineLinear2d(-1.7071067811865475,0.7071067811865475,1.0);

        // Point2d calcB2 = Skeleton.calcB2(vertex, edge);

        fail("Not yet implemented");
    }

    @Test
    public void testEdgeBehindBisector_1() {

        Ray2d bisector = new Ray2d(p(0, -1), v(0, 1));

        LineLinear2d edge = new LineLinear2d(p(-1, 0), p(1, 0));

        assertFalse(Skeleton.edgeBehindBisector(bisector, edge));

    }

    @Test
    public void testEdgeBehindBisector_2() {

        Ray2d bisector = new Ray2d(p(0, 0), v(1, 0));

        LineLinear2d edge = new LineLinear2d(p(-1, 0), p(1, 0));

        assertTrue(Skeleton.edgeBehindBisector(bisector, edge));

    }

    @Test
    public void testEdgeBehindBisector_3() {

        Ray2d bisector = new Ray2d(p(0, 0), v(0, 1));

        LineLinear2d edge = new LineLinear2d(p(0, 1), p(0, -1));

        assertTrue(Skeleton.edgeBehindBisector(bisector, edge));

    }

    @Test
    public void testEdgeBehindBisector_4() {

        Ray2d bisector = new Ray2d(p(-1, 0.0000001), v(1, 0));

        LineLinear2d edge = new LineLinear2d(p(-1, 0), p(1, 0));

        assertTrue(Skeleton.edgeBehindBisector(bisector, edge));

    }

    @Test
    public void testMergeLav_1() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);

        VertexEntry2 e1 = debugVertex("e1");
        VertexEntry2 e2 = debugVertex("e2");
        VertexEntry2 e3 = debugVertex("e3");
        VertexEntry2 e4 = debugVertex("e4");

        CircularList<VertexEntry2> lav2 = new CircularList<VertexEntry2>();
        lav2.addLast(e1);
        lav2.addLast(e2);
        lav2.addLast(e3);
        lav2.addLast(e4);

        CircularList<VertexEntry2> mergeLav = Skeleton.mergeLav(v1, e2);
        assertLavOrder(new VertexEntry2[] { e3, e4, e1, e2, v2, v3, v4, v1 }, mergeLav);

    }

    @Test
    public void testFindSplitIndex_1() {

        EdgeEntry anyEdge = debugEdge("anyEdge");
        EdgeEntry oppositeEdge = debugEdge("oppositeEdge");

        VertexEntry2 v1 = debugVertex("v1", anyEdge, anyEdge);
        VertexEntry2 v2 = debugVertex("v2", anyEdge, anyEdge);
        VertexEntry2 v3 = debugVertex("v3", oppositeEdge, anyEdge);
        VertexEntry2 v4 = debugVertex("v4", anyEdge, anyEdge);
        VertexEntry2 v5 = debugVertex("v5", anyEdge, anyEdge);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        int split = Skeleton.findSplitIndex(v1, oppositeEdge);

        assertEquals(2, split);
    }

    @Test
    public void testFindSplitIndex_2() {

        EdgeEntry anyEdge = debugEdge("anyEdge");
        EdgeEntry oppositeEdge = debugEdge("oppositeEdge");

        VertexEntry2 v1 = debugVertex("v1", anyEdge, anyEdge);
        VertexEntry2 v2 = debugVertex("v2", anyEdge, anyEdge);
        VertexEntry2 v3 = debugVertex("v3", anyEdge, oppositeEdge);
        VertexEntry2 v4 = debugVertex("v4", anyEdge, anyEdge);
        VertexEntry2 v5 = debugVertex("v5", anyEdge, anyEdge);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        int split = Skeleton.findSplitIndex(v1, oppositeEdge);

        assertEquals(3, split);
    }

    @Test
    public void testFindSplitIndex_3() {

        EdgeEntry anyEdge = debugEdge("anyEdge");
        EdgeEntry oppositeEdge = debugEdge("oppositeEdge");

        VertexEntry2 v1 = debugVertex("v1", oppositeEdge, anyEdge);
        VertexEntry2 v2 = debugVertex("v2", anyEdge, anyEdge);
        VertexEntry2 v3 = debugVertex("v3", anyEdge, anyEdge);
        VertexEntry2 v4 = debugVertex("v4", anyEdge, anyEdge);
        VertexEntry2 v5 = debugVertex("v5", anyEdge, anyEdge);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        int split = Skeleton.findSplitIndex(v1, oppositeEdge);

        assertEquals(0, split);
    }

    @Test
    public void testFindSplitIndex_4() {

        EdgeEntry anyEdge = debugEdge("anyEdge");
        EdgeEntry oppositeEdge = debugEdge("oppositeEdge");

        VertexEntry2 v1 = debugVertex("v1", anyEdge, anyEdge);
        VertexEntry2 v2 = debugVertex("v2", anyEdge, anyEdge);
        VertexEntry2 v3 = debugVertex("v3", anyEdge, anyEdge);
        VertexEntry2 v4 = debugVertex("v4", anyEdge, anyEdge);
        VertexEntry2 v5 = debugVertex("v5", anyEdge, oppositeEdge);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        int split = Skeleton.findSplitIndex(v1, oppositeEdge);

        assertEquals(0, split);
    }

    @Test
    public void testFindSplitLav_1() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        VertexEntry2 v5 = debugVertex("v5");

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        SplitSlavs splitLav = LavUtil.splitLav(v1, 3);

        assertLavOrder(new VertexEntry2[] { v2, v3 }, splitLav.getNewLawRight());
        assertLavOrder(new VertexEntry2[] { v4, v5 }, splitLav.getNewLawLeft());
    }

    @Test(expected = RuntimeException.class)
    public void testFindSplitLav_2() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        VertexEntry2 v5 = debugVertex("v5");

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        LavUtil.splitLav(v1, 1);

        fail();
    }

    @Test()
    public void testVertexOpositeEdge_1() {

        Point2d point = new Point2d(0, 2);

        EdgeEntry edge = debugEdge("edge");
        EdgeEntry edgePrevious = debugEdge("edgePrevious");
        EdgeEntry edgeNext = debugEdge("edgeNext");

        edgesCircularList(edge, edgeNext, edgePrevious);

        edge.bisectorPrevious = new Ray2d(new Point2d(-2, 0), new Vector2d(1, 1));
        edge.bisectorNext = new Ray2d(new Point2d(2, 0), new Vector2d(1, 1));

        EdgeEntry vertexOppositeEdge = Skeleton.vertexOpositeEdge(point, edge);

        assertEquals(edgePrevious, vertexOppositeEdge);
    }

    @Test()
    public void testVertexOpositeEdge_2() {

        Point2d point = new Point2d(0, 2);

        EdgeEntry edge = debugEdge("edge");
        EdgeEntry edgePrevious = debugEdge("edgePrevious");
        EdgeEntry edgeNext = debugEdge("edgeNext");

        edgesCircularList(edge, edgeNext, edgePrevious);

        edge.bisectorPrevious = new Ray2d(new Point2d(-3, 0), new Vector2d(1, 1));
        edge.bisectorNext = new Ray2d(new Point2d(2, 0), new Vector2d(-1, 1));

        EdgeEntry vertexOppositeEdge = Skeleton.vertexOpositeEdge(point, edge);

        assertEquals(edge, vertexOppositeEdge);
    }

    @Test()
    public void testVertexOpositeEdge_3() {

        Point2d point = new Point2d(0, 2);

        EdgeEntry edge = debugEdge("edge");
        EdgeEntry edgePrevious = debugEdge("edgePrevious");
        EdgeEntry edgeNext = debugEdge("edgeNext");

        edgesCircularList(edge, edgeNext, edgePrevious);

        edge.bisectorPrevious = new Ray2d(new Point2d(-2, 0), new Vector2d(1, 1));
        edge.bisectorNext = new Ray2d(new Point2d(2, 0), new Vector2d(-1, 1));

        EdgeEntry vertexOppositeEdge = Skeleton.vertexOpositeEdge(point, edge);

        assertEquals(edge, vertexOppositeEdge);
    }

    @Test
    public void createEdgeChain_1() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        VertexEntry2 v5 = debugVertex("v5");

        EdgeEvent e1 = debugEdgeEvent("e1", v1, v2);
        EdgeEvent e2 = debugEdgeEvent("e2", v3, v4);
        EdgeEvent e3 = debugEdgeEvent("e3", v2, v5);

        List<EdgeEvent> edgeCluster = new ArrayList<Skeleton.EdgeEvent>();
        edgeCluster.add(e1);
        edgeCluster.add(e2);
        edgeCluster.add(e3);

        ArrayList<EdgeEvent> chain = Skeleton.createEdgeChain(edgeCluster);

        assertListEquals(new EdgeEvent[] { e1, e3 }, chain);
    }

    @Test
    public void createEdgeChain_2() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");

        EdgeEvent e1 = debugEdgeEvent("e1", v1, v2);
        EdgeEvent e2 = debugEdgeEvent("e2", v3, v1);
        EdgeEvent e3 = debugEdgeEvent("e3", v2, v4);

        List<EdgeEvent> edgeCluster = new ArrayList<Skeleton.EdgeEvent>();
        edgeCluster.add(e1);
        edgeCluster.add(e2);
        edgeCluster.add(e3);

        ArrayList<EdgeEvent> chain = Skeleton.createEdgeChain(edgeCluster);

        assertListEquals(new EdgeEvent[] { e2, e1, e3 }, chain);
    }

    @Test
    public void groupLevelEvents_1() {
        //
        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        Point2d p1 = debugPoint2d("p1", 0, 0);

        EdgeEvent e1 = debugEdgeEvent("e1", v1, v2, p1);
        EdgeEvent e2 = debugEdgeEvent("e2", v2, v3, p1);
        EdgeEvent e3 = debugEdgeEvent("e3", v3, v4, p1);
        EdgeEvent e4 = debugEdgeEvent("e4", v4, v1, p1);

        List<SkeletonEvent> edgeCluster = new ArrayList<SkeletonEvent>();
        edgeCluster.add(e1);
        edgeCluster.add(e2);
        edgeCluster.add(e3);
        edgeCluster.add(e4);

        List<SkeletonEvent> chain = Skeleton.groupLevelEvents(edgeCluster);

        assertEquals(PickEvent.class, chain.get(0).getClass());
    }

    @Test()
    public void cutLavPart() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        VertexEntry2 v5 = debugVertex("v5");

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        List<VertexEntry2> cutLavPart = LavUtil.cutLavPart(v2, v3);

        assertListEquals(Arrays.asList(v2, v3), cutLavPart);
    }

    @Test()
    public void cutLavPart_2() {

        VertexEntry2 v1 = debugVertex("v1");
        VertexEntry2 v2 = debugVertex("v2");
        VertexEntry2 v3 = debugVertex("v3");
        VertexEntry2 v4 = debugVertex("v4");
        VertexEntry2 v5 = debugVertex("v5");

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);
        lav1.addLast(v3);
        lav1.addLast(v4);
        lav1.addLast(v5);

        List<VertexEntry2> cutLavPart = LavUtil.cutLavPart(v1, v1);

        assertListEquals(Arrays.asList(v1), cutLavPart);
    }

    public void choseOppositeEdgeLav() {

        Point2d p1 = debugPoint2d("p0", 0, 0);
        Point2d p2 = debugPoint2d("p1", 1, 0);
        EdgeEntry oppositeEdge = new EdgeEntry(p1, p2);

        VertexEntry2 v1 = debugVertex("v1", p1);
        VertexEntry2 v2 = debugVertex("v2", p2);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);

        List<VertexEntry2> edgeLavs = Arrays.asList(v1, v1);

        Point2d center = debugPoint2d("c", 0.5, 1);

        VertexEntry2 vertex = Skeleton.choseOppositeEdgeLav(edgeLavs, oppositeEdge, center);

        assertEquals(vertex, v1);
    }

    public void choseOppositeEdgeLav_2() {

        Point2d p1 = debugPoint2d("p1", 0, 0);
        Point2d p2 = debugPoint2d("p2", 0.2, 0);
        Point2d p3 = debugPoint2d("p3", 0.2, 0);
        Point2d p4 = debugPoint2d("p4", 1, 0);

        EdgeEntry oppositeEdge = new EdgeEntry(p1, p3);

        VertexEntry2 v1 = debugVertex("v1", p1);
        VertexEntry2 v2 = debugVertex("v2", p2);

        CircularList<VertexEntry2> lav1 = new CircularList<VertexEntry2>();
        lav1.addLast(v1);
        lav1.addLast(v2);

        VertexEntry2 v3 = debugVertex("v3", p3);
        VertexEntry2 v4 = debugVertex("v4", p4);

        CircularList<VertexEntry2> lav2 = new CircularList<VertexEntry2>();
        lav1.addLast(v3);
        lav1.addLast(v4);

        List<VertexEntry2> edgeLavs = Arrays.asList(v1, v3);

        Point2d center = debugPoint2d("c", 0.5, 1);

        VertexEntry2 vertex = Skeleton.choseOppositeEdgeLav(edgeLavs, oppositeEdge, center);

        assertEquals(v3, vertex);
    }

    private EdgeEvent debugEdgeEvent(final String name, VertexEntry2 v1, VertexEntry2 v2) {
        EdgeEvent event = new EdgeEvent() {
            @Override
            public String toString() {
                return name;
            };
        };

        event.Va = v1;
        event.Vb = v2;
        return event;
    }

    private EdgeEvent debugEdgeEvent(final String name, VertexEntry2 v1, VertexEntry2 v2, Point2d point) {
        EdgeEvent event = new EdgeEvent() {
            @Override
            public String toString() {
                return name;
            };
        };

        event.Va = v1;
        event.Vb = v2;
        event.v = point;
        return event;
    }

    private void edgesCircularList(EdgeEntry... edges) {
        CircularList<EdgeEntry> edgesList = new CircularList<EdgeEntry>();

        for (EdgeEntry edgeEntry : edges) {
            edgesList.addLast(edgeEntry);
        }
    }

    private EdgeEntry debugEdge(final String string) {
        return new EdgeEntry(new Point2d(), new Point2d()) {
            @Override
            public String toString() {
                return string;
            }
        };
    }

    private VertexEntry2 debugVertex(final String name1) {

        return new VertexEntry2() {
            @Override
            public String toString() {
                return name1;
            }
        };
    }

    private VertexEntry2 debugVertex(final String name1, EdgeEntry edgeLeft, EdgeEntry edgeRight) {

        VertexEntry2 v = new VertexEntry2() {
            @Override
            public String toString() {
                return name1;
            }
        };
        v.e_a = edgeLeft;
        v.e_b = edgeRight;

        return v;
    }

    private VertexEntry2 debugVertex(final String name1, Point2d p) {

        VertexEntry2 v = new VertexEntry2() {
            @Override
            public String toString() {
                return name1;
            }
        };
        v.v = p;
        return v;
    }

    private <T> void assertListEquals(List<T> expecteds, List<T> actuals) {
        assertArrayEquals(expecteds.toArray(), actuals.toArray());
    }

    private <T> void assertListEquals(T[] expecteds, List<T> actuals) {
        assertArrayEquals(expecteds, actuals.toArray());
    }

    private void assertLavOrder(VertexEntry2[] vertexEntry2s, CircularList<VertexEntry2> mergeLav) {
        int i = 0;
        for (VertexEntry2 vertexEntry2 : mergeLav) {
            if (!vertexEntry2s[i].equals(vertexEntry2)) {
                fail("as lav element [" + i + "] expected " + vertexEntry2s[i] + " but get " + vertexEntry2);
            }

            i++;
        }

    }

    public static Point2d debugPoint2d(final String name, double x, double y) {
        return new Point2d(x, y) {
            @Override
            public String toString() {
                return name;
            };
        };
    }
}
