package kendzi.math.geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

/**
 * Based on:
 * http://www.flipcode.com/archives/Efficient_Polygon_Triangulation.shtml
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class Triangulate {

    /** Log. */
    private static final Logger log = Logger.getLogger(Triangulate.class);

	public static final float EPSILON = 0.0000000001f;

	public static float area(List<Point2d> contour) {

		int n = contour.size();

		float A = 0.0f;

		for (int p = n - 1, q = 0; q < n; p = q++) {
			A += contour.get(p).getX() * contour.get(q).getY()
					- contour.get(q).getX() * contour.get(p).getY();
		}
		return A * 0.5f;
	}

	public static float area2(List<Point2d> contour) {

		int n = contour.size();

		float A = 0.0f;

		for (int p = n - 1, q = 0; q < n; p = q++) {
			A += contour.get(p).getX() * contour.get(q).getY()
			- contour.get(q).getX() * contour.get(p).getY();
		}
		return A * 0.5f;
	}

	public static float area(Point2D.Double [] contour) {

		int n = contour.length;

		float A = 0.0f;

		for (int p = n - 1, q = 0; q < n; p = q++) {
			A += contour[p].getX() * contour[q].getY()
			- contour[q].getX() * contour[p].getY();
		}
		return A * 0.5f;
	}

	/*
	 * InsideTriangle decides if a point P is Inside of the triangle defined by
	 * A, B, C.
	 */
	static boolean insideTriangle(double Ax, double Ay, double Bx, double By,
			double Cx, double Cy, double Px, double Py)

	{
		double ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
		double cCROSSap, bCROSScp, aCROSSbp;

		ax = Cx - Bx;
		ay = Cy - By;
		bx = Ax - Cx;
		by = Ay - Cy;
		cx = Bx - Ax;
		cy = By - Ay;
		apx = Px - Ax;
		apy = Py - Ay;
		bpx = Px - Bx;
		bpy = Py - By;
		cpx = Px - Cx;
		cpy = Py - Cy;

		aCROSSbp = ax * bpy - ay * bpx;
		cCROSSap = cx * apy - cy * apx;
		bCROSScp = bx * cpy - by * cpx;

		return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
	};

	static boolean snip(List<Point2d> contour, int u, int v, int w, int n,/* ??? */
	int[] V) {
		int p;
		double Ax, Ay, Bx, By, Cx, Cy, Px, Py;

		Ax = contour.get(V[u]).getX();
		Ay = contour.get(V[u]).getY();

		Bx = contour.get(V[v]).getX();
		By = contour.get(V[v]).getY();

		Cx = contour.get(V[w]).getX();
		Cy = contour.get(V[w]).getY();

		if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax))))
			return false;

		for (p = 0; p < n; p++) {
			if ((p == u) || (p == v) || (p == w))
				continue;
			Px = contour.get(V[p]).getX();
			Py = contour.get(V[p]).getY();
			if (insideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py))
				return false;
		}

		return true;
	}

	public List<Point2d> removeClosePoints1(List<Point2d> pPoints) {

	    return removeClosePoints(pPoints, EPSILON);
	}



	public static List<Point2d> removeClosePoints(List<Point2d> points) {
	    // XXX change to point2d !
	    return removeClosePoints(points, EPSILON);
	}


	public static List<Point2d> removeClosePoints(List<Point2d> points,
			double epsilon) {
	    // XXX change to point2d
		ArrayList<Point2d> ret = new ArrayList<Point2d>();

		if (points.size() == 0) {
			return ret;
		}
		// the first point have to be always added to list
		ret.add(points.get(0));

		if (points.size() == 1) {
			return ret;
		}

		double sqrtEpsilon = epsilon * epsilon;

		Point2d lastPoint = points.get(0);
		int max = points.size();
		for (int i = 1; i < max; i++) {
		    Point2d p = points.get(i);

			double px = p.x - lastPoint.x;
			double py = p.y - lastPoint.y;

			double sqrtDistance = px * px + py * py;

			if (sqrtDistance > sqrtEpsilon) {
				ret.add(p);
				lastPoint = p;
			} else {
				// we skip that point
			}
		}
		// last point we have to chack from both sides
		Point2d firstPoint = points.get(0);
		lastPoint = points.get(ret.size() - 1);

		double px = firstPoint.x - lastPoint.x;
		double py = firstPoint.y - lastPoint.y;

		double sqrtDistance = px * px + py * py;

		if (sqrtDistance < sqrtEpsilon) {
			ret.remove(ret.size() - 1);
		}
		return ret;
	}




	public static List<Point2d> process(List<Point2d> contour) {

        List<Point2d> result = new ArrayList<Point2d>();
        process(contour, result);

        return result;
	}

	public static boolean process(List<Point2d> contour,
			List<Point2d> result) {
		/* allocate and initialize list of Vertices in polygon */

		int n = contour.size();
		if (n < 3)
			return false;

		int[] V = new int[n];

		/* we want a counter-clockwise polygon in V */

		if (0.0f < area(contour)) {
			for (int v = 0; v < n; v++) {
				V[v] = v;
			}
		} else {
			for (int v = 0; v < n; v++) {
				V[v] = (n - 1) - v;
			}
		}

		int nv = n;

		/* remove nv-2 Vertices, creating 1 triangle every time */
		int count = 2 * nv; /* error detection */

		for (int m = 0, v = nv - 1; nv > 2;) {
			/* if we loop, it is probably a non-simple polygon */
			if (0 >= (count--)) {
				// ** Triangulate: ERROR - probable bad polygon!
				return false;
			}

			/* three consecutive vertices in current polygon, <u,v,w> */
			int u = v;
			if (nv <= u)
				u = 0; /* previous */
			v = u + 1;
			if (nv <= v)
				v = 0; /* new v */
			int w = v + 1;
			if (nv <= w)
				w = 0; /* next */

			if (snip(contour, u, v, w, nv, V)) {
				int a, b, c, s, t;

				/* true names of the vertices */
				a = V[u];
				b = V[v];
				c = V[w];

				/* output Triangle */
				result.add(contour.get(a));
				result.add(contour.get(b));
				result.add(contour.get(c));

				m++;

				/* remove v from remaining polygon */
				for (s = v, t = v + 1; t < nv; s++, t++)
					V[s] = V[t];
				nv--;

				/* resest error detection counter */
				count = 2 * nv;
			}
		}

		// delete V;

		return true;
	}

	public List<Integer> processIndex(List<Point2d> pContour
			) {


		List<Integer> result = new ArrayList<Integer>();

		int size = pContour.size();
		if (size > 1) {

		    if (pContour.get(0).equals(pContour.get(pContour.size()-1))) {
		        // removing dubled point.
		        log.error("triangulation error start and end point is dubled");
		        size--;
		    }
		}

		/* allocate and initialize list of Vertices in polygon */
		List<Point2d> contour = new ArrayList<Point2d>();
		for (int i = 0; i < size; i++) {
		    Point2d p = pContour.get(i);
			contour.add(new Point2d(p.x, p.y));
		}



		int n = contour.size();
		if (n < 3) {
//			return false;
			throw new RuntimeException("not enouth vertex. Put at last 3");
		}

		int[] V = new int[n];

		/* we want a counter-clockwise polygon in V */

		if (0.0f < area(contour)) {
			for (int v = 0; v < n; v++) {
				V[v] = v;
			}
		} else {
			for (int v = 0; v < n; v++) {
				V[v] = (n - 1) - v;
			}
		}

		int nv = n;

		/* remove nv-2 Vertices, creating 1 triangle every time */
		int count = 2 * nv; /* error detection */

		for (int m = 0, v = nv - 1; nv > 2;) {
			/* if we loop, it is probably a non-simple polygon */
			if (0 >= (count--)) {
				// ** Triangulate: ERROR - probable bad polygon!
			//XXX	throw new RuntimeException(" Triangulate: ERROR - probable bad polygon!");
				//FIXME
				return null;
//				return false;
			}

			/* three consecutive vertices in current polygon, <u,v,w> */
			int u = v;
			if (nv <= u)
				u = 0; /* previous */
			v = u + 1;
			if (nv <= v)
				v = 0; /* new v */
			int w = v + 1;
			if (nv <= w)
				w = 0; /* next */

			if (snip(contour, u, v, w, nv, V)) {
				int a, b, c, s, t;

				/* true names of the vertices */
				a = V[u];
				b = V[v];
				c = V[w];

				/* output Triangle */
//				result.add(contour.get(a));
//				result.add(contour.get(b));
//				result.add(contour.get(c));
				result.add((a));
				result.add((b));
				result.add((c));


				m++;

				/* remove v from remaining polygon */
				for (s = v, t = v + 1; t < nv; s++, t++) {
					V[s] = V[t];
				}
				nv--;

				/* resest error detection counter */
				count = 2 * nv;
			}
		}

		// delete V;

		return result;
	}
}
