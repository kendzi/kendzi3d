package kendzi.math.geometry.Bool;

import java.util.ArrayList;

/**
 * Constructive Solid Geometry (CSG) is a modeling technique that uses Boolean
 * operations like union and intersection to combine 3D solids. This library
 * implements CSG operations on meshes elegantly and concisely using BSP trees,
 * and is meant to serve as an easily understandable implementation of the
 * algorithm. All edge cases involving overlapping coplanar polygons in both
 * solids are correctly handled.
 *
 * Example usage:
 *
 *   var cube = CSG.cube();
 *   var sphere = CSG.sphere({ radius: 1.3 });
 *   var polygons = cube.subtract(sphere).toPolygons();
 *
 * ## Implementation Details
 *
 * All CSG operations are implemented in terms of two functions, `clipTo()` and
 * `invert()`, which remove parts of a BSP tree inside another BSP tree and swap
 * solid and empty space, respectively. To find the union of `a` and `b`, we
 * want to remove everything in `a` inside `b` and everything in `b` inside `a`,
 * then combine polygons from `a` and `b` into one solid:
 *
 * <pre>
 *   a.clipTo(b);
 *   b.clipTo(a);
 *   a.build(b.allPolygons());
 * </pre>
 *
 * The only tricky part is handling overlapping coplanar polygons in both trees.
 * The code above keeps both copies, but we need to keep them in one tree and
 * remove them in the other tree. To remove them from `b` we can clip the
 * inverse of `b` against `a`. The code for union now looks like this:
 *
 * <pre>
 *   a.clipTo(b);
 *   b.clipTo(a);
 *   b.invert();
 *   b.clipTo(a);
 *   b.invert();
 *   a.build(b.allPolygons());
 * </pre>
 *
 * Subtraction and intersection naturally follow from set operations. If
 * union is `A | B`, subtraction is `A - B = ~(~A | B)` and intersection is
 * `A & B = ~(~A | ~B)` where `~` is the complement operator.
 *
 * ## License
 *
 * Copyright (c) 2011 Evan Wallace (http://madebyevan.com/), under the MIT license.
 *
 * # class CSG
 *
 * Holds a binary space partition tree representing a 3D solid. Two solids can
 * be combined using the `union()`, `subtract()`, and `intersect()` methods.
 */
public class CSG {

    ArrayList<Polygon> polygons = null;

    /** Construct a CSG solid from a list of `CSG.Polygon` instances.
     * @param polygons
     * @return
     */
    public static CSG fromPolygons(ArrayList<Polygon> polygons) {
        CSG csg = new CSG();
        csg.polygons = polygons;
        return csg;
    };

    @Override
    protected CSG clone() {

        CSG csg = new CSG();

        // Polygon [] polygons = new Polygon[this.polygons.size()];
        ArrayList<Polygon> polygons = new ArrayList<Polygon>(this.polygons.size());
        for (int i = 0; i < this.polygons.size(); i++) {
            // polygons.set(i, this.polygons.get(i).clone());
            polygons.add(this.polygons.get(i).clone());
        }
        csg.polygons = polygons;
        // csg.polygons = this.polygons.map(function(p) { return p.clone(); });
        return csg;
    }

    /**
     * @return
     */
    public ArrayList<Polygon> toPolygons() {
        return this.polygons;
    }

    /**
     * Return a new CSG solid representing space in either this solid or in the
     * solid `csg`. Neither this solid nor the solid `csg` are modified.
     *
     * <pre>
     *     A.union(B)
     *
     *     +-------+            +-------+
     *     |       |            |       |
     *     |   A   |            |       |
     *     |    +--+----+   =   |       +----+
     *     +----+--+    |       +----+       |
     *          |   B   |            |       |
     *          |       |            |       |
     *          +-------+            +-------+
     * </pre>
     *
     * @param csg
     * @return
     */
    CSG union(CSG csg) {
        Node a = new CSG.Node((this.clone().polygons));
        Node b = new CSG.Node((csg.clone().polygons));
        a.clipTo(b);
        b.clipTo(a);
        b.invert();
        b.clipTo(a);
        b.invert();
        a.build(b.allPolygons());
        return fromPolygons(a.allPolygons());
    }

    /**
     * Return a new CSG solid representing space in this solid but not in the
     * solid `csg`. Neither this solid nor the solid `csg` are modified.
     * <pre>
     *     A.subtract(B)
     *
     *     +-------+            +-------+
     *     |       |            |       |
     *     |   A   |            |       |
     *     |    +--+----+   =   |    +--+
     *     +----+--+    |       +----+
     *          |   B   |
     *          |       |
     *          +-------+
     *
     * </pre>
     * @param csg
     * @return
     */
    public CSG subtract(CSG csg) {
        Node a = new CSG.Node((this.clone().polygons));
        Node b = new CSG.Node((csg.clone().polygons));
        a.invert();
        a.clipTo(b);
        b.clipTo(a);
        b.invert();
        b.clipTo(a);
        b.invert();
        a.build(b.allPolygons());
        a.invert();
        return fromPolygons(a.allPolygons());
    }

    /**
     * Return a new CSG solid representing space both this solid and in the
     * solid `csg`. Neither this solid nor the solid `csg` are modified.
     *
     * <pre>
     *     A.intersect(B)
     *
     *     +-------+
     *     |       |
     *     |   A   |
     *     |    +--+----+   =   +--+
     *     +----+--+    |       +--+
     *          |   B   |
     *          |       |
     *          +-------+
     * </pre>
     * @param csg
     * @return
     */
    CSG intersect(CSG csg) {
        Node a = new CSG.Node((this.clone().polygons));
        Node b = new CSG.Node((csg.clone().polygons));
        a.invert();
        b.clipTo(a);
        b.invert();
        a.clipTo(b);
        b.clipTo(a);
        a.build(b.allPolygons());
        a.invert();
        return fromPolygons(a.allPolygons());
    }

// Return a new CSG solid with solid and empty space switched. This solid is
// not modified.
    CSG inverse() {
        CSG csg = this.clone();

        throw new RuntimeException("TODO");
//        csg.polygons.map(function(p) { p.flip(); });
//        return csg;
    }


//Construct an axis-aligned solid cuboid. Optional parameters are `center` and
//`radius`, which default to `[0, 0, 0]` and `[1, 1, 1]`. The radius can be
//specified using a single number or a list of three numbers, one for each axis.
//
//Example code:
//
//  var cube = CSG.cube({
//    center: [0, 0, 0],
//    radius: 1
//  });
    class Parms {
        public Vector radius;
        public Vector center;
    }
//    CSG cube(Parms options) {
//        if (options == null){
//            options = null;
//        }
//
////        CSG.Vector c = new CSG.Vector(options.center || [0, 0, 0]);
////        var r = !options.radius ? [1, 1, 1] : options.radius.length ?
////                options.radius : [options.radius, options.radius, options.radius];
//
//        CSG.Vector c = new Vector(0, 0, 0);
//        if (options != null && options.center !=null) {
//            c = options.center;
//        }
//        Vector r = new Vector(1,1,1);
//        if (options != null && options.radius !=null) {
//            r = options.radius;
//        }
//
//        double [][][] tab = {
//            {{0, 4, 6, 2}, {-1, 0, 0}},
//            {{1, 3, 7, 5}, {+1, 0, 0}},
//            {{0, 1, 5, 4}, {0, -1, 0}},
//            {{2, 6, 7, 3}, {0, +1, 0}},
//            {{0, 2, 3, 1}, {0, 0, -1}},
//            {{4, 5, 7, 6}, {0, 0, +1}}
//            };
//
//        ArrayList<CSG.Vertex> ret = new ArrayList<CSG.Vertex>();
//        for (int j = 0; j<tab.length; j++) {
//
//            for (int d = 0; d < tab[j][0].length; d++) {
//           // double [][] i = tab[j];
//                int i = (int) tab[j][0][d];
//
//                Vector pos = new CSG.Vector(
//                        c.x + r.x * (2 * !!(i & 1) - 1),
//                        c.y + r.y * (2 * !!(i & 2) - 1),
//                        c.z + r.z * (2 * !!(i & 4) - 1)
//                      );
//                ret.add(CSG.Vertex(pos, new CSG.Vector(info[1])));
//            }
//        }
//        return ret;
//
////        return CSG.fromPolygons(
////                [
////         [[0, 4, 6, 2], [-1, 0, 0]],
////         [[1, 3, 7, 5], [+1, 0, 0]],
////         [[0, 1, 5, 4], [0, -1, 0]],
////         [[2, 6, 7, 3], [0, +1, 0]],
////         [[0, 2, 3, 1], [0, 0, -1]],
////         [[4, 5, 7, 6], [0, 0, +1]]
////        ].map(function(info) {
////         return new CSG.Polygon(info[0].map(function(i) {
////           var pos = new CSG.Vector(
////             c.x + r[0] * (2 * !!(i & 1) - 1),
////             c.y + r[1] * (2 * !!(i & 2) - 1),
////             c.z + r[2] * (2 * !!(i & 4) - 1)
////           );
////           return new CSG.Vertex(pos, new CSG.Vector(info[1]));
////         }));
////        }));
//    };

//    ArrayList<CSG.Vertex> ravToPolygons(double[][][] tab) {
//
//
//    }

//Construct a solid sphere. Optional parameters are `center`, `radius`,
//`slices`, and `stacks`, which default to `[0, 0, 0]`, `1`, `16`, and `8`.
//The `slices` and `stacks` parameters control the tessellation along the
//longitude and latitude directions.
//
//Example usage:
//
//  var sphere = CSG.sphere({
//    center: [0, 0, 0],
//    radius: 1,
//    slices: 16,
//    stacks: 8
//  });
    public static  CSG sphere(Vector c, Double r, Double pSlices, Double pStacks) {

        ArrayList<Polygon> polygons = new ArrayList<Polygon> ();
//        options = options || {};
//        var c = new CSG.Vector(options.center || [0, 0, 0]);
//        var r = options.radius || 1;

        if (c == null) {
            c = new Vector(0,0,0);
        }
        if (r == null) {
            r = 1d;
        }

        double slices = 16d;
        if (pSlices != null) {
            slices = pSlices;
        }

        double stacks = 8d;
        if (pStacks != null) {
            stacks = pStacks;
        }

//        var slices = options.slices || 16;
//        var stacks = options.stacks || 8;
       //var polygons = [], vertices;

        for (double i = 0; i < slices; i++) {
         for (double j = 0; j < stacks; j++) {
             ArrayList<Vertex> vertices = new ArrayList<Vertex>();
           vertex(i / slices, j / stacks, c, r, vertices);
           if (j > 0) vertex((i + 1d) / slices, j / stacks, c, r, vertices);
           if (j < stacks - 1d) vertex((i + 1d) / slices, (j + 1d) / stacks, c, r, vertices);
           vertex(i / slices, (j + 1d) / stacks, c, r, vertices);
           polygons.add(new CSG.Polygon(vertices.toArray(new Vertex[0]), false));
         }
        }
        return CSG.fromPolygons(polygons);
    }

    static void vertex(double theta, double phi, Vector c, double r, ArrayList<Vertex> vertices) {
        theta *= Math.PI * 2d;
        phi *= Math.PI;
        Vector dir = new CSG.Vector(
                Math.cos(theta) * Math.sin(phi),
                Math.cos(phi),
                Math.sin(theta) * Math.sin(phi)
        );
        vertices.add(new CSG.Vertex(c.plus(dir.times(r)), dir));
    }

//Construct a solid cylinder. Optional parameters are `start`, `end`,
//`radius`, and `slices`, which default to `[0, -1, 0]`, `[0, 1, 0]`, `1`, and
//`16`. The `slices` parameter controls the tessellation.
//
//Example usage:
//
//  var cylinder = CSG.cylinder({
//    start: [0, -1, 0],
//    end: [0, 1, 0],
//    radius: 1,
//    slices: 16
//  });
    public static CSG cylinder(Vector s, Vector e, Double radius, Double pSlices) {
//    options = options || {};
//        var s = new CSG.Vector(options.start || [0, -1, 0]);
//        var e = new CSG.Vector(options.end || [0, 1, 0]);
        if (s == null) {
            s = new Vector(0, -1, 0);
        }
        if (e == null) {
            e = new Vector(0, 1, 0);
        }
        if (radius == null) {
            radius = 1d;
        }
        double slices = 16d;
        if (pSlices != null) {
            slices = pSlices;
        }

        Vector ray = e.minus(s);
//        var r = options.radius || 1;
//        var slices = options.slices || 16;
        Vector axisZ = ray.unit();
        boolean isY = (Math.abs(axisZ.y) > 0.5);
        Vector axisX = new CSG.Vector(isY ? 1 : 0, !isY ? 1 : 0, 0).cross(axisZ).unit();
        Vector axisY = axisX.cross(axisZ).unit();
        Vertex start = new CSG.Vertex(s, axisZ.negated());
        Vertex end = new CSG.Vertex(e, axisZ.unit());

//        var polygons = [];
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();

        for (double i = 0; i < slices; i++) {
         double t0 = i / slices, t1 = (i + 1d) / slices;
         polygons.add(new CSG.Polygon(asArray(
                 start,
                 point(0, t0, -1d, axisX, axisY, axisZ, ray, s, radius),
                 point(0, t1, -1d, axisX, axisY, axisZ, ray, s, radius)), false));
         polygons.add(new CSG.Polygon(asArray(
                 point(0, t1, 0, axisX, axisY, axisZ, ray, s, radius),
                 point(0, t0, 0, axisX, axisY, axisZ, ray, s, radius),
                 point(1d, t0, 0, axisX, axisY, axisZ, ray, s, radius),
                 point(1d, t1, 0, axisX, axisY, axisZ, ray, s, radius)), false));
         polygons.add(new CSG.Polygon(asArray(
                 end,
                 point(1d, t1, 1d, axisX, axisY, axisZ, ray, s, radius),
                 point(1, t0, 1, axisX, axisY, axisZ, ray, s, radius)), false));
        }
        return fromPolygons(polygons);
    }

    static Vertex [] asArray(Vertex ... v) {
        return v;
    }

    static Vertex point(double stack, double slice, double normalBlend,
            Vector axisX, Vector axisY, Vector axisZ, Vector ray, Vector s, double r) {
        double angle = slice * Math.PI * 2d;
        Vector out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
        Vector pos = s.plus(ray.times(stack)).plus(out.times(r));
        Vector normal = out.times(1d - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
        return new CSG.Vertex(pos, normal);
       }



    /**
     * Represents a 3D vector.
     *
     * Example usage:
     *
     *   new CSG.Vector(1, 2, 3);
     *   new CSG.Vector([1, 2, 3]);
     *   new CSG.Vector({ x: 1, y: 2, z: 3 });
     */
    public static class Vector {
        public double x; public double y; public double z;

        public Vector(double [] tab) {
            if (tab == null || tab.length < 3) {
                throw new RuntimeException("wrong parameter");
            }
            this.x = tab[0];
            this.y = tab[1];
            this.z = tab[2];
        }

        public Vector(double x, double y, double z) {
              this.x = x;
              this.y = y;
              this.z = z;
          };

        @Override
        public Vector clone() {
            return new CSG.Vector(this.x, this.y, this.z);
        }

        public Vector negated() {
            return new CSG.Vector(-this.x, -this.y, -this.z);
        }

        public Vector plus(Vector a) {
         return new CSG.Vector(this.x + a.x, this.y + a.y, this.z + a.z);
        }

        public Vector minus(Vector a) {
         return new CSG.Vector(this.x - a.x, this.y - a.y, this.z - a.z);
        }

        public  Vector times(double a) {
         return new CSG.Vector(this.x * a, this.y * a, this.z * a);
        }

        public  Vector dividedBy(double a) {
         return new CSG.Vector(this.x / a, this.y / a, this.z / a);
        }

        public  double dot(Vector a) {
         return this.x * a.x + this.y * a.y + this.z * a.z;
        }

        public  Vector lerp(Vector a, double t) {
         return this.plus(a.minus(this).times(t));
        }

        public  double length() {
         return Math.sqrt(this.dot(this));
        }

        public  Vector unit() {
         return this.dividedBy(this.length());
        }

        public  Vector cross(Vector a) {
             return new CSG.Vector(
               this.y * a.z - this.z * a.y,
               this.z * a.x - this.x * a.z,
               this.x * a.y - this.y * a.x
             );
        }
    };

    /**
     * Represents a vertex of a polygon. Use your own vertex class instead of this
     * one to provide additional features like texture coordinates and vertex
     * colors. Custom vertex classes need to provide a `pos` property and `clone()`,
     * `flip()`, and `interpolate()` methods that behave analogous to the ones
     * defined by `CSG.Vertex`. This class provides `normal` so convenience
     * functions like `CSG.sphere()` can return a smooth vertex normal, but `normal`
     * is not used anywhere else.
     */
    public interface VertextInt {

        /** Clone Vertext.
         * @return Clone of Vertext
         */
        Vertex clone();

        /**
         *  Invert all orientation-specific data (e.g. vertex normal). Called when the
         *  orientation of a polygon is flipped.
         */
        void flip();

        /**
         *  Create a new vertex between this vertex and `other` by linearly
         *  interpolating all properties using a parameter of `t`. Subclasses should
         *  override this to interpolate additional properties.
         *
         * @param other
         * @param t
         * @return interpolated vertex
         */
        Vertex interpolate(Vertex other, double t);
    }

    public  static class Vertex implements VertextInt {
        Vector pos;
        Vector normal;

        public Vertex(Vector pos, Vector normal) {
            this.pos = pos;
            this.normal = normal;
        }

        @Override
        public Vertex clone() {
            return new CSG.Vertex(this.pos.clone(), this.normal.clone());
        }

        /* (non-Javadoc)
         * @see kendzi.math.geometry.Bool.CSG.VertextInt#flip()
         */
        @Override
        public void flip() {
            this.normal = this.normal.negated();
        }

        /* (non-Javadoc)
         * @see kendzi.math.geometry.Bool.CSG.VertextInt#interpolate(kendzi.math.geometry.Bool.CSG.Vertex, double)
         */
        @Override
        public Vertex interpolate(Vertex other, double t) {
            return new CSG.Vertex(
                    this.pos.lerp(other.pos, t),
                    this.normal.lerp(other.normal, t)
                    );
        }

        /**
         * @return the pos
         */
        public Vector getPos() {
            return pos;
        }

        /**
         * @return the normal
         */
        public Vector getNormal() {
            return normal;
        }
    }

    // XXX
    public static Plane planeFromPoints(Vector a, Vector b, Vector c) {
        Vector n = b.minus(a).cross(c.minus(a)).unit();
        return new Plane(n, n.dot(a));
    }

    /**
     * Represents a plane in 3D space.
     */
    public static class Plane {
        Vector normal;
        double w;

        final int COPLANAR = 0;
        final int FRONT = 1;
        final int BACK = 2;
        final int SPANNING = 3;

        public Plane(Vector normal, double w) {
            this.normal = normal;
            this.w = w;
        }

    //CSG.Plane = function(normal, w) {
    //this.normal = normal;
    //this.w = w;
    //};

        /**
         * `CSG.Plane.EPSILON` is the tolerance used by `splitPolygon()` to decide if a
         * point is on the plane.
         */
        static double EPSILON = 1e-5;




    //CSG.Plane.prototype = {
        @Override
        public Plane clone() {
            return new CSG.Plane(this.normal.clone(), this.w);
        }

        void  flip() {
            this.normal = this.normal.negated();
            this.w = -this.w;
        }

        /**
         *  Split `polygon` by this plane if needed, then put the polygon or polygon
         *  fragments in the appropriate lists. Coplanar polygons go into either
         *  `coplanarFront` or `coplanarBack` depending on their orientation with
         *  respect to this plane. Polygons in front or in back of this plane go into
         *  either `front` or `back`.
         *
         * @param polygon
         * @param coplanarFront
         * @param coplanarBack
         * @param front
         * @param back
         */
        void splitPolygon(Polygon polygon,
                ArrayList<Polygon> coplanarFront, ArrayList<Polygon> coplanarBack,
                ArrayList<Polygon> front, ArrayList<Polygon> back) {


             // Classify each point as well as the entire polygon into one of the above
             // four classes.
             int polygonType = 0;
             //var types = [];
             ArrayList<Integer> types = new ArrayList<Integer>();

             for (int i = 0; i < polygon.vertices.length; i++) {
                 double t = this.normal.dot(polygon.vertices[i].pos) - this.w;
                 int type = (t < -CSG.Plane.EPSILON) ? BACK : (t > CSG.Plane.EPSILON) ? FRONT : COPLANAR;
                 polygonType |= type;
                 //types.push(type);
                 types.add(type);
             }

             // Put the polygon in the correct list, splitting it when necessary.
             switch (polygonType) {
             case COPLANAR:
                 (this.normal.dot(polygon.plane.normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
                 break;
             case FRONT:
                 front.add(polygon);
                 break;
             case BACK:
                 back.add(polygon);
                 break;
             case SPANNING:
                 //var f = [], b = [];
                 ArrayList<Vertex> f = new ArrayList<Vertex>();
                 ArrayList<Vertex> b = new ArrayList<Vertex>();

                 for (int i = 0; i < polygon.vertices.length; i++) {
                     int j = (i + 1) % polygon.vertices.length;
                     int ti = types.get(i);
                     int tj = types.get(j);
                     Vertex vi = polygon.vertices[i];
                     Vertex vj = polygon.vertices[j];

                     if (ti != BACK) f.add(vi);
                     if (ti != FRONT) b.add(ti != BACK ? vi.clone() : vi);
                     if ((ti | tj) == SPANNING) {
                         double t = (this.w - this.normal.dot(vi.pos)) / this.normal.dot(vj.pos.minus(vi.pos));
                         Vertex v = vi.interpolate(vj, t);
                         f.add(v);
                         b.add(v.clone());
                     }
                 }
                 //XXX
                 if (f.size() >= 3) front.add(new Polygon(f.toArray(new Vertex[f.size()]), polygon.shared));
                 if (b.size() >= 3) back.add(new Polygon(b.toArray(new Vertex[b.size()]), polygon.shared));
                 break;
             }
        }
    }

//# class Polygon


    /**
     * Represents a convex polygon. The vertices used to initialize a polygon must
     * be coplanar and form a convex loop. They do not have to be `CSG.Vertex`
     * instances but they must behave similarly (duck typing can be used for
     * customization).
     *
     * Each convex polygon has a `shared` property, which is shared between all
     * polygons that are clones of each other or were split from the same polygon.
     * This can be used to define per-polygon properties (such as surface color).
     */
    public static class Polygon {
//CSG.Polygon = function(vertices, shared) {
//this.vertices = vertices;
//this.shared = shared;
//this.plane = CSG.Plane.fromPoints(vertices[0].pos, vertices[1].pos, vertices[2].pos);
//};
        Vertex [] vertices;
        boolean shared;
        Plane plane;


        public Polygon(Vertex [] vertices) {
            this(vertices, false);
        }

        public Polygon(Vertex [] vertices, boolean shared) {
            this.vertices = vertices;
            this.shared = shared;
            this.plane = CSG.planeFromPoints(vertices[0].pos, vertices[1].pos, vertices[2].pos);
        };


//CSG.Polygon.prototype = {
        @Override
        protected Polygon clone() {
            Vertex [] vertices = new Vertex[this.vertices.length];
            for (int i = 0; i < this.vertices.length; i++) {
                vertices[i] =  this.vertices[i].clone();
            }
            //Vertex [] vertices = this.vertices.map(function(v) { return v.clone(); });
            return new CSG.Polygon(vertices, this.shared);
        }

        void flip() {
            int size = this.vertices.length;
            Vertex [] vertices = new Vertex[size];
            int sizeEnd = size - 1;
            for (int i = 0; i < size; i++) {
//            vertices[size - i] =
                this.vertices[i].flip();
                vertices[sizeEnd - i] = this.vertices[i];

            }
            this.vertices = vertices;
            //this.vertices.reverse().map(function(v) { v.flip(); });
            this.plane.flip();
        }


        /**
         * @return the vertices
         */
        public Vertex[] getVertices() {
            return vertices;
        }


    };

//# class Node


    /**
     * Holds a node in a BSP tree. A BSP tree is built from a collection of polygons
     * by picking a polygon to split along. That polygon (and all other coplanar
     * polygons) are added directly to that node and the other polygons are added to
     * the front and/or back subtrees. This is not a leafy BSP tree since there is
     * no distinction between internal and leaf nodes.
     */
    class Node {
        //CSG.Node = function(polygons) {
        //this.plane = null;
        //this.front = null;
        //this.back = null;
        //this.polygons = [];
        //if (polygons) this.build(polygons);
        //};
        Plane plane;
        Node front;
        Node back;
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();



        public Node() {
        }

        public Node(ArrayList<Polygon>  polygons) {
            this.plane = null;
            this.front = null;
            this.back = null;
            this.polygons = new ArrayList<Polygon>();//Polygon[0];
            if (polygons != null) {
                this.build(polygons);
            }
        }


        @Override
        protected Node clone() {
            Node node = new CSG.Node();
            node.plane = this.plane == null ? null : this.plane.clone();
            node.front = this.front == null ? null : this.front.clone();
            node.back = this.back == null ? null : this.back.clone();

            ArrayList<Polygon> polygons = new ArrayList<Polygon>(this.polygons.size());
            for (int i = 0; i < this.polygons.size(); i++) {
                polygons.set(i, this.polygons.get(i).clone());
            }


            node.polygons = polygons;//this.polygons.map(function(p) { return p.clone(); });
            return node;
        }

        /**
         * Convert solid space to empty space and empty space to solid space.
         */
        void invert() {
            for (int i = 0; i < this.polygons.size(); i++) {
                this.polygons.get(i).flip();
            }
            this.plane.flip();
            if (this.front != null) this.front.invert();
            if (this.back != null) this.back.invert();
            Node temp = this.front;
            this.front = this.back;
            this.back = temp;
        }

        /**
         * Recursively remove all polygons in `polygons` that are inside this BSP
         * tree.
         *
         * @param polygons
         * @return
         */
        ArrayList<Polygon> clipPolygons(ArrayList<Polygon> polygons) {
            if (this.plane == null) return slice(polygons);
            ArrayList<Polygon> front = new ArrayList<Polygon>();// = [];
            ArrayList<Polygon> back = new ArrayList<Polygon>();// = [];

            for (int i = 0; i < polygons.size(); i++) {
                this.plane.splitPolygon(polygons.get(i), front, back, front, back);
            }
            if (this.front != null) front = this.front.clipPolygons(front);
            if (this.back != null) {
                back = this.back.clipPolygons(back);
            } else {
                back = new ArrayList<Polygon>();// = [];
            }
            return concat(front,back);
        }

        /**
         *  Remove all polygons in this BSP tree that are inside the other BSP tree
         *  `bsp`.
         *
         * @param bsp
         */
        void clipTo(Node bsp) {
            this.polygons = bsp.clipPolygons(this.polygons);
            if (this.front != null) this.front.clipTo(bsp);
            if (this.back != null) this.back.clipTo(bsp);
        }

        /**
         *  Return a list of all polygons in this BSP tree.
         *
         * @return
         */
        ArrayList<Polygon> allPolygons() {
            ArrayList<Polygon> polygons = slice(this.polygons);
            if (this.front != null) polygons = concat(polygons, this.front.allPolygons());
            if (this.back != null) polygons = concat(polygons, this.back.allPolygons());
            return polygons;
        }

        Polygon [] slice(Polygon [] polygons){
            int length = polygons.length;
            Polygon [] dest = new Polygon [length];
            System.arraycopy(polygons, 0, dest, 0, length);
            return dest;
        }

        ArrayList<Polygon> slice(ArrayList<Polygon> polygons){
            // XXX
            return new ArrayList<CSG.Polygon>(polygons);
        }

        Polygon [] concat(Polygon [] polygons, Polygon [] polygons2){
            int length = polygons.length;
            int length2 = polygons2.length;
            Polygon [] dest = new Polygon [length+ length2];
            System.arraycopy(polygons, 0, dest, 0, length);
            System.arraycopy(polygons2, 0, dest, length, length2);
            return dest;
        }

        ArrayList<Polygon> concat(ArrayList<Polygon> polygons, ArrayList<Polygon> polygons2){
            ArrayList<CSG.Polygon> ret = new ArrayList<CSG.Polygon>(polygons.size() + polygons2.size());
            ret.addAll(polygons);
            ret.addAll(polygons2);
            return ret;
        }


    /**
     *  Build a BSP tree out of `polygons`. When called on an existing tree, the
     *  new polygons are filtered down to the bottom of the tree and become new
     *  nodes there. Each set of polygons is partitioned using the first polygon
     *  (no heuristic is used to pick a good split).
     *
     * @param polygons
     */
    void build(ArrayList<Polygon> polygons) {
     if (polygons.size() == 0) return;
     if (this.plane == null) this.plane = polygons.get(0).plane.clone();
     ArrayList<Polygon> front = new ArrayList<Polygon>();//null;//[],
     ArrayList<Polygon> back = new ArrayList<Polygon>();//null;//[];

     for (int i = 0; i < polygons.size(); i++) {
       this.plane.splitPolygon(polygons.get(i), this.polygons, this.polygons, front, back);
     }
     if (front.size()>0) {
       if (this.front == null) this.front = new CSG.Node();
       this.front.build(front);
     }
     if (back.size()>0) {
       if (this.back == null) this.back = new CSG.Node();
       this.back.build(back);
     }
    }
}
    }
