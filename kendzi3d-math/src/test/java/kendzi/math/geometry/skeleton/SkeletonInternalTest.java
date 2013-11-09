package kendzi.math.geometry.skeleton;

import static kendzi.math.geometry.TestUtil.*;
import static org.junit.Assert.*;

import javax.vecmath.Point2d;

import kendzi.math.geometry.skeleton.Skeleton.EdgeEntry;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;

import org.junit.Test;

public class SkeletonInternalTest {

    @Test
    public void testCalcB2() {


        VertexEntry2 vertex = new VertexEntry2();
        vertex.v = new Point2d(-1.0, -1.0);
        vertex.e_a = new EdgeEntry(p(-2.0, 0.0), p(-1.0, -1.0));
        //        vertex.e_a.b
        vertex.e_b = new EdgeEntry(p(-1.0, -1.0), p(0.0, 0.0));
        vertex.bisector = new Ray2d(p(-1.0, -1.0), v(0.0, 1.414213562373095));

        EdgeEntry edge = new EdgeEntry(p(1.0, 1.0), p(-1.0, 1.0));
        //        edge.bisectorNext = new LineLinear2d(-1.7071067811865475,-0.7071067811865475,-1.0);
        //        edge.bisectorPrevious = new LineLinear2d(-1.7071067811865475,0.7071067811865475,1.0);

        Point2d calcB2 = Skeleton.calcB2(vertex, edge);

        fail("Not yet implemented");
    }

}
