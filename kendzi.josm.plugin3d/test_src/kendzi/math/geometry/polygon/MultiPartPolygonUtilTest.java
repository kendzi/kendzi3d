package kendzi.math.geometry.polygon;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.Edge;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.EdgeOut;
import kendzi.kendzi3d.josm.model.polygon.MultiPartPolygonUtil.Vertex;

import org.junit.Test;




public class MultiPartPolygonUtilTest {

    void init() {
	}


	Vertex<Object> vertex(final int id) {

	    return new Vertex<Object>(id) {
	        @Override
	        public String toString() {
	            if (getData() != null) {
	                return "v" + id;
	            }
	            return super.toString();
	        }
	    };
	}

	Edge<Object, Object> edge(Vertex<Object> v1, Vertex<Object> v2, final int i) {
		return new Edge<Object, Object>(v1, v2, i) {
		    @Override
    		public String toString() {
    		    return "e" + i + "(" + getV1().toString() + "," + getV2().toString()+")";
    		};
		} ;
	}


	void assertContein(Edge<Object, Object> e, List<EdgeOut<Object, Object>> polygon, boolean revert) {
		boolean ok = false;
		for (EdgeOut eo : polygon) {

			if (eo.getEdge().getData().equals(e.getData()) && eo.isReverted() == revert) {
				return;
			}
		}
		throw new RuntimeException("ret don't have edge: " + e + " in revert status: " + revert);
	}

	@Test
	public void test1() {
		Vertex<Object> v0 = vertex(0);
		Vertex<Object> v1 = vertex(1);

		Edge<Object, Object> e0 = edge(v0, v1, 0);
		Edge<Object, Object> e1 = edge(v1, v0, 1);

		List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(
		        Arrays.asList(e0, e1));

		assertEquals(1, ret.size());

		List<EdgeOut<Object, Object>> polygon = ret.get(0);

		assertEquals(polygon.size(), 2);

		assertContein(e0, polygon, false);
		assertContein(e1, polygon, false);
	}

    @Test
    public void test1_1() {
        Vertex<Object> v0 = vertex(0);
        Vertex<Object> v1 = vertex(1);
        Vertex<Object> v2 = vertex(2);

        Edge<Object, Object> e0 = edge(v0, v1, 0);
        Edge<Object, Object> e1 = edge(v1, v2, 1);
        Edge<Object, Object> e2 = edge(v2, v0, 2);

        List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(
                Arrays.asList(e0, e1, e2));

        assertEquals(1, ret.size());

        List<EdgeOut<Object, Object>> polygon = ret.get(0);

        assertEquals(3, polygon.size());

        assertContein(e0, polygon, false);
        assertContein(e1, polygon, false);
        assertContein(e2, polygon, false);
    }

	@Test
	public void test2() {
		Vertex<Object> v0 = vertex(0);
		Vertex<Object> v1 = vertex(1);
		Vertex<Object> v2 = vertex(2);
		Vertex<Object> v3 = vertex(3);

		Edge<Object, Object> e0 = edge(v0, v1, 0);
		Edge<Object, Object> e1 = edge(v1, v2, 1);
		Edge<Object, Object> e2 = edge(v2, v3, 2);
		Edge<Object, Object> e3 = edge(v0, v3, 3);

		List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(Arrays.asList(e0, e1, e2, e3));

		assertEquals(1, ret.size());

		List<EdgeOut<Object, Object>> polygon = ret.get(0);

		assertEquals(4, polygon.size());

		assertContein(e0, polygon, false);
		assertContein(e1, polygon, false);
		assertContein(e2, polygon, false);
		assertContein(e3, polygon, true);
	}

	@Test
	public void test3() {
		// one edge not connected
		Vertex<Object> v0 = vertex(0);
		Vertex<Object> v1 = vertex(1);
		Vertex<Object> v2 = vertex(2);
		Vertex<Object> v3 = vertex(3);

		Edge<Object, Object> e0 = edge(v0, v1, 0);
		Edge<Object, Object> e1 = edge(v1, v2, 1);
		Edge<Object, Object> e2 = edge(v2, v0, 2);
		Edge<Object, Object> e3 = edge(v0, v3, 3);

		List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(Arrays.asList(e0, e1, e2, e3));

		assertEquals(1, ret.size());

		List<EdgeOut<Object, Object>> polygon = ret.get(0);

		assertEquals(3, polygon.size());

		assertContein(e0, polygon, false);
		assertContein(e1, polygon, false);
		assertContein(e2, polygon, false);
	}

	@Test
	public void test4() {
		// two not connected polygons
		Vertex<Object> v0 = vertex(0);
		Vertex<Object> v1 = vertex(1);
		Vertex<Object> v2 = vertex(2);

		Vertex<Object> v3 = vertex(3);
		Vertex<Object> v4 = vertex(4);
		Vertex<Object> v5 = vertex(5);

		Edge<Object, Object> e0 = edge(v0, v1, 0);
		Edge<Object, Object> e1 = edge(v1, v2, 1);
		Edge<Object, Object> e2 = edge(v2, v0, 2);

		Edge<Object, Object> e3 = edge(v3, v4, 3);
		Edge<Object, Object> e4 = edge(v4, v5, 4);
		Edge<Object, Object> e5 = edge(v5, v3, 5);

		List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(Arrays.asList(e0, e1, e2, e3, e4, e5));

		assertEquals(2, ret.size());

		List<EdgeOut<Object, Object>> polygon = ret.get(0);

		assertEquals(3, polygon.size());

		assertContein(e0, polygon, false);
		assertContein(e1, polygon, false);
		assertContein(e2, polygon, false);

		polygon = ret.get(1);

		assertEquals(3, polygon.size());

		assertContein(e3, polygon, false);
		assertContein(e4, polygon, false);
		assertContein(e5, polygon, false);
	}

	@Test
	public void test5() {
		// tree connected polygons
		Vertex<Object> v0 = vertex(0);
		Vertex<Object> v1 = vertex(1);
		Vertex<Object> v2 = vertex(2);

		Vertex<Object> v3 = vertex(3);
		Vertex<Object> v4 = vertex(4);
		Vertex<Object> v5 = vertex(5);

		Edge<Object, Object> e0 = edge(v0, v1, 0);
		Edge<Object, Object> e1 = edge(v1, v2, 1);
		Edge<Object, Object> e2 = edge(v2, v0, 2);

		Edge<Object, Object> e3 = edge(v2, v3, 3);
		Edge<Object, Object> e4 = edge(v3, v4, 4);
		Edge<Object, Object> e5 = edge(v4, v0, 5);

		Edge<Object, Object> e6 = edge(v4, v5, 6);
		Edge<Object, Object> e7 = edge(v5, v3, 7);

		List<List<EdgeOut<Object, Object>>> ret =
		        MultiPartPolygonUtil.connect(
		                Arrays.asList(e0, e1, e2, e3, e4, e5, e6, e7));

		assertEquals(3, ret.size());

		List<EdgeOut<Object, Object>> polygon = ret.get(0);

		assertEquals(3,polygon.size());

		assertContein(e0, polygon, false);
		assertContein(e1, polygon, false);
		assertContein(e2, polygon, false);

		polygon = ret.get(1);

		assertEquals(4,polygon.size());

		assertContein(e3, polygon, false);
		assertContein(e4, polygon, false);
		assertContein(e5, polygon, false);
		assertContein(e2, polygon, true);

		polygon = ret.get(2);

		assertEquals(3,polygon.size());

		assertContein(e4, polygon, false);
		assertContein(e6, polygon, false);
		assertContein(e7, polygon, false);
	}

	@Test
    public void test6() {
        // one edge not connected
        Vertex<Object> v0 = vertex(-6);
        Vertex<Object> v1 = vertex(-4);
        Vertex<Object> v2 = vertex(-10);
        Vertex<Object> v3 = vertex(-8);

        Edge<Object, Object> e0 = edge(vertex(-6),  vertex(-4), 0);
        Edge<Object, Object> e1 = edge(vertex(-4),  vertex(-10), 1);
        Edge<Object, Object> e2 = edge(vertex(-10), vertex(-8), 2);
        Edge<Object, Object> e3 = edge(vertex(-8),  vertex(-6), 3);

        List<List<EdgeOut<Object, Object>>> ret = MultiPartPolygonUtil.connect(Arrays.asList(e0, e1, e2, e3));

        assertEquals(1, ret.size());

        List<EdgeOut<Object, Object>> polygon = ret.get(0);

        assertEquals(4, polygon.size());

        assertContein(e0, polygon, false);
        assertContein(e1, polygon, false);
        assertContein(e2, polygon, false);
        assertContein(e3, polygon, false);
    }

}