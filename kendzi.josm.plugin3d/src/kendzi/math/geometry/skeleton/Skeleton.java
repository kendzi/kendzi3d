/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.math.geometry.skeleton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.Algebra;
import kendzi.math.geometry.debug.DebugDisplay;
import kendzi.math.geometry.debug.DebugLayer;
import kendzi.math.geometry.debug.DisplayLineParametric2d;
import kendzi.math.geometry.debug.DisplayLineSegment2d;
import kendzi.math.geometry.debug.DisplayPoints;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.skeleton.debug.DisplayEventQueue;
import kendzi.math.geometry.skeleton.debug.DisplayFaceNode;
import kendzi.math.geometry.skeleton.debug.DisplayLav2;

public class Skeleton {

    static DebugLayer dv = DebugDisplay.getDebugDisplay().getDebugLayer();


    public static Output sk(List<Point2d> pBorder) {
        return sk(pBorder, null);
    }

    public static Output sk(List<Point2d> pBorder, List<List<Point2d>> innerList) {

        dv.clear();

        PriorityQueue<IntersectEntry> queue = new PriorityQueue<IntersectEntry>(3, distanseComparator);

        Set<CircularList<VertexEntry2>> sLav = new HashSet<CircularList<VertexEntry2>>();

        Set<FaceQueue> faces = new HashSet<FaceQueue>();

        Output output = new Output();


      /// STEP 1



        prepareData(pBorder, queue, sLav, faces);

        if (innerList != null) {
            for (List<Point2d> inner : innerList) {
                prepareData(inner, queue, sLav, faces);
            }
        }

        for (CircularList<VertexEntry2> lav : sLav) {


            /* new */
            for (VertexEntry2 v_i : lav) {
                VertexEntry2 v_ip1 = (VertexEntry2) v_i.previous();

               // calcBisection2(v_i, v_ip1, queue);

                computeIntersections(v_i, queue);

                dv.addDebug(new DisplayEventQueue(queue));

            }

            dv.addDebug(new DisplayEventQueue(queue));
            dv.addDebug(new DisplayLav2(lav, Color.ORANGE.darker().darker()));
        }

        dv.addDebug(new DisplayEventQueue(queue));
//        dv.addDebug(new DisplayLAV(LAV, Color.ORANGE));




        /// STEP 2

        while (!queue.isEmpty()) {

            dv.addDebug(new DisplayEventQueue(queue));
           // dv.addDebug(new DisplayLAV(LAV, Color.ORANGE));
//            dv.addDebug(new DisplayLav2(lav, Color.ORANGE.darker().darker()));

            for (CircularList l : sLav) {
                dv.addDebug(new DisplayLav2(l, Color.ORANGE.darker().darker()));
            }

            for (FaceQueue f : faces) {
                dv.addDebug(new DisplayFaceNode(f, Color.pink.darker()));
            }

            // a
            IntersectEntry I = queue.poll();

            dv.addDebug(new DisplayPoints(I.v));

            System.out.println(I);
//            System.out.println("I: " + I.v + " distance: " +  I.distance + " Va: " + I.Va.v+ " Vb: " + I.Vb.v);
            //dv.addDebug(new DisplayLineParametric2d(I.v.bisector));

            if (EventType.EDGE_EVENT.equals(I.eventType)) {
                edgeEvent((EdgeEvent) I, output, queue);

                continue;
            } else if (EventType.SPLIT_EVENT.equals(I.eventType)) {
                splitEvent((SplitEvent) I, output, sLav,  queue );
            }


        }

        for (FaceQueue f : faces) {
            dv.addDebug(new DisplayFaceNode(f, Color.pink.darker()));

        }

        addFacesToOutput(faces, output);

        return output;
    }

    /**
     * @param pBorder
     * @param queue
     * @param sLav
     * @param faces
     */
    public static void prepareData(List<Point2d> pBorder, PriorityQueue<IntersectEntry> queue,
            Set<CircularList<VertexEntry2>> sLav, Set<FaceQueue> faces) {
        CircularList<VertexEntry2> lav = new CircularList<Skeleton.VertexEntry2>();
        sLav.add(lav);


        for (Point2d p : pBorder) {
            VertexEntry2 v2 = new VertexEntry2();
            v2.v = p;
            lav.addLast(v2);
        }


        CircularList<EdgeEntry> edgesList = new CircularList<EdgeEntry>();


        for (VertexEntry2 v_i : lav) {

            VertexEntry2 v_ip1 = (VertexEntry2) v_i.next();

            EdgeEntry e_i = new EdgeEntry(v_i.v, v_ip1.v);

            edgesList.addLast(e_i);

            v_i.e_b = e_i;
        }


        for (VertexEntry2 v_i : lav) {
            v_i.e_a = (EdgeEntry) v_i.e_b.previous;
        }
//        lav.first.


//        dv.addDebug(new DisplayLAV(LAV, Color.ORANGE));

        dv.addDebug(new DisplayLav2(lav, Color.ORANGE.darker()));






        /* new */
        for (VertexEntry2 v : lav) {

            v.bisector = calcBisector(v.v, v.e_a, v.e_b);
            v.bisector2 =  v.bisector.getLinearForm();
                //calcBisector2(v.v, e_im1, e_i);

            v.e_a.bisectorNext = v.bisector2;
            v.e_b.bisectorPrevious = v.bisector2;

            dv.addDebug(new DisplayLineParametric2d(v.bisector));



            FaceQueue leftFace = new FaceQueue();
            leftFace.border = true;
//            leftFace = new CircularList<Skeleton.FaceNode>();
            FaceNode fn = new FaceNode();
            fn.v = v;
            leftFace.addFirst(fn);
            v.leftFace = fn;

            faces.add(leftFace);

//            CircularList<Skeleton.FaceNode> rightFace = new CircularList<Skeleton.FaceNode>();
//            fn = new FaceNode();
//            fn.v = v;
//            rightFace.addLast(fn);
//            v.rightFace = fn;
//
//            faces.add(leftFace);
////            faces.add(rightFace);
        }


        /* new */
        for (VertexEntry2 v : lav) {

            VertexEntry2 next = (VertexEntry2) v.next;

            FaceNode leftFace = next.leftFace;

            FaceNode fn = new FaceNode();
            fn.v = v;
            leftFace.addPush(fn);
            v.rightFace = fn;
        }



    }

    private static void addFacesToOutput(Set<FaceQueue> faces, Output output) {
//        output.faces2 = new A
        for (FaceQueue face : faces) {
            if (face.size > 0) {
                List<Point2d> faceList = new ArrayList<Point2d>();

                for (FaceNode fn : face) {
                    faceList.add(fn.v.v);
                    output.distance.put(fn.v.v, fn.v.distance);
                }

                output.faces2.add(new PolygonList2d(faceList));
            }
        }
    }

    private static void addOutputFace(Output output, IntersectEntry i, VertexEntry2 va, VertexEntry2 vb) {
        addOutputFace(output, i.v, va, vb);
    }

    private static void addOutputFace(Output output, Point2d v, VertexEntry2 va, VertexEntry2 vb) {

        try {
            dv.addDebug(new DisplayPoints(v));
            dv.addDebug(new DisplayPoints(va.v));
            dv.addDebug(new DisplayPoints(vb.v));

        List<Point2d> edge = new ArrayList<Point2d>();

        List<Point2d> leftEdge = new ArrayList<Point2d>();
        if (!v.equals(va.v)) {
            leftEdge.add(va.v);
        }


        boolean lewa = true;

        boolean split;

        findFaceEdge(va, lewa, leftEdge);








        for (int j = leftEdge.size() - 1; j >= 0; j--) {
            edge.add(leftEdge.get(j));
        }
 //       dv.addDebug(new DisplayPoints(v_i.v));
        edge.add(v);

        if (!v.equals(vb.v)) {
            edge.add(vb.v);
        }

        findFaceEdge(vb, false, edge);

        output.faces.add(edge);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param va
     * @param lewa
     * @param leftEdge
     */
    public static void findFaceEdge(VertexEntry2 va, boolean lewa, List<Point2d> leftEdge) {

        VertexEntry2 leftEdgeVertex = va;
        boolean split = false;
        while (true) {

            if (leftEdgeVertex.split) {
                VertexEntry2 splitVertex = null;
                if (lewa) {
                    splitVertex = leftEdgeVertex.parentVb;
                } else {
                    splitVertex = leftEdgeVertex.parentVa;
                }

                if (splitVertex.split) {
                    // split vertex
                    // there are two vertex for each split event!
                    // skip first one

                    leftEdgeVertex = splitVertex;

                    split = true;
                    lewa = !lewa;
                } else {
//                    normal vertex don't do anyfing
                }
            }

            if (!split) {
                if (lewa && (leftEdgeVertex.parentVb != null)) {
                    leftEdgeVertex = leftEdgeVertex.parentVb;
                    leftEdge.add(leftEdgeVertex.v);
                } else if (!lewa && (leftEdgeVertex.parentVa != null)) {
                    leftEdgeVertex = leftEdgeVertex.parentVa;
                    leftEdge.add(leftEdgeVertex.v);
                } else {
                    break;
                }
                continue;
            } else {

                if (leftEdgeVertex.shrinks != null) {
                    leftEdgeVertex = leftEdgeVertex.shrinks;
                    leftEdge.add(leftEdgeVertex.v);

                    split = false;
                } else {
                    throw new RuntimeException("Face is not ended");
                }
                continue;
            }
        }
    }
    private static void addOutputFace(Output output, IntersectEntry i, VertexEntry2 va) {
        // only sysout
        System.out.println("adding edge on split event!: " + i.v);

    }

    private static void calcBisection(VertexEntry2 v_i, VertexEntry2 v_ip1, PriorityQueue<IntersectEntry> queue) {
        Ray2d b_i = v_i.bisector;
        Ray2d b_ip1 = v_ip1.bisector;
        EdgeEntry e_i = v_i.e_b;

        EdgeEvent I = new EdgeEvent();
        Point2d intersect = RayUtil.intersectRays2d(b_i, b_ip1, null, null);

        if (v_i.v.equals(intersect)
                || v_ip1.v.equals(intersect)) {
            // skip the same points
            return;
        }

        if (intersect != null) {
            I.v = intersect;

//            I.e_im1 = v_i.e_im1;
//            I.e_i = v_ip1.e_i;
            I.distance = calcDistance(intersect, e_i);

            I.Va = v_i;
            I.Vb = v_ip1;

            I.eventType = EventType.EDGE_EVENT;

            queue.add(I);
   //                quare.add(I);
        }
    }

    private static void calcBisection2(VertexEntry2 v_i, VertexEntry2 v_ip1, PriorityQueue<IntersectEntry> queue) {

        LineLinear2d b_i = v_i.bisector2;
        LineLinear2d b_ip1 = v_ip1.bisector2;

        EdgeEntry e_i = v_i.e_b;
        LineLinear2d edgeLine = e_i.lineLinear2d;

        EdgeEvent I = new EdgeEvent();

        Point2d intersect = b_i.collide(b_ip1);

        if (v_i.v.equals(intersect)
                || v_ip1.v.equals(intersect)) {
            // skip the same points
            return;
        }
// XXX FIXME odkomentowac bug !
//        if (edgeLine.pointIsUnder(v_i.v)) {
//            return;
//        }

        if (intersect != null) {
            I.v = intersect;

            I.distance = calcDistance(intersect, e_i);

            I.Va = v_i;
            I.Vb = v_ip1;

            I.eventType = EventType.EDGE_EVENT;

            queue.add(I);
        }
    }




    private static void computeIntersections(VertexEntry2 v_i, PriorityQueue<IntersectEntry> queue) {

        EdgeEntry e_i = v_i.e_b;

        VertexEntry2 v_ip1 = (VertexEntry2) v_i.next();
        VertexEntry2 v_im1 = (VertexEntry2) v_i.previous();

        Point2d intersectionBisectors1 = computeIntersectionBisectors(v_i, v_ip1);
        Point2d intersectionBisectors2 = computeIntersectionBisectors(v_im1, v_i);

        dv.addDebug(new DisplayPoints(v_i.v));

        Oposite oposite = calcOpositePointB(v_i);

        Point2d B = oposite.B;

        if (B != null) {
            dv.addDebug(new DisplayLineSegment2d(oposite.edge.p1, oposite.edge.p2, Color.GRAY));
        }


        int nirest = choseNirest(v_i.v, intersectionBisectors1, intersectionBisectors2, B);

        switch (nirest) {
        case 1: {
            EdgeEvent I = new EdgeEvent();

            I.v = intersectionBisectors1;

            I.distance = calcDistance(intersectionBisectors1, v_i.e_b);
            I.Va = v_i;
            I.Vb = v_ip1;

            queue.add(I);

            break;
        }
        case 2: {
            EdgeEvent I = new EdgeEvent();

            I.v = intersectionBisectors2;

            I.distance = calcDistance(intersectionBisectors2, v_i.e_a);
            I.Va = v_im1;
            I.Vb = v_i;

            queue.add(I);

            break;
        }
        case 3: {
            SplitEvent I = new SplitEvent();

            I.v = B;
            I.V = v_i;
            I.distance = calcDistance(B, oposite.edge);
            I.opositeEdge = oposite.edge;

            queue.add(I);
            break;
        }
        }
    }

    private static int choseNirest(Point2d v, Point2d p1, Point2d p2, Point2d p3) {

        if (p1 == null && p2 == null && p3 == null) {
            return -1;
        }

        double distance1 = Double.MAX_VALUE;
        double distance2 = Double.MAX_VALUE;
        double distance3 = Double.MAX_VALUE;

        if (p1 != null) {
            distance1 = v.distanceSquared(p1);
        }
        if (p2 != null) {
            distance2 = v.distanceSquared(p2);
        }
        if (p3 != null) {
            distance3 = v.distanceSquared(p3);
        }

        if (distance1 <= distance2 && distance1 <= distance3) {
            return 1;
        } else if (distance2 <= distance1 && distance2 <= distance3) {
            return 2;
        } else {
            return 3;
        }
    }

    public static class Oposite {

        private Oposite(Point2d b, EdgeEntry edge) {
            super();
            this.B = b;
            this.edge = edge;
        }
        public Point2d B;
        public EdgeEntry edge;
    }

    static boolean isVectorChangeDirection(Vector2d v1, Vector2d v2) {
        Vector2d v2ort = new Vector2d(v2.y, -v2.x);

        double dot = v1.dot(v2ort);

        if (dot >= 0) {
            return true;
        }
        return false;


    }

    private static Oposite calcOpositePointB(VertexEntry2 v_i) {

        // FIXME for polygons with holes should be commented out !
        if (isVectorChangeDirection(v_i.e_a.norm, v_i.e_b.norm)) {
            return new Oposite(null, null);
        }

        List<Point2d> bList = new ArrayList<Point2d>();

        Point2d candidateB = null;
        EdgeEntry candidateEdge = null;
        double candidateDistance = Double.MAX_VALUE;


        VertexEntry2 v_ip1 = (VertexEntry2) v_i.next();
        VertexEntry2 v_im1 = (VertexEntry2) v_i.previous();

        VertexEntry2 v1 = v_ip1;
        VertexEntry2 v2 = (VertexEntry2) v1.next();

        if (v_i.e_b.equals(v_i.e_a) ) {
            throw new RuntimeException("???");
        }

        EdgeEntry e_i = (EdgeEntry) v_i.e_b.next; //next


        while (!e_i.equals(v_i.e_a)) {
            dv.addDebug(new DisplayLineSegment2d(e_i.p1, e_i.p2, Color.GRAY));

            LineLinear2d edge = e_i.getLineLinear();

//            intersection test between the bisector starting at V and the whole line containing the
//            currently tested line segment ei rejects the line segments laying "behind" the vertex V

            Point2d collide = Ray2d.collide(v_i.bisector, edge);
            if (collide == null) {
                e_i = (EdgeEntry) e_i.next;
                continue;
            }

//            compute the coordinates of the candidate point Bi

            Vector2d vLine_m1 = new Vector2d(v_i.v);
            vLine_m1.sub(v_im1.v);
            vLine_m1.normalize();

            LineLinear2d l_a = new LineLinear2d(v_i.v, v_im1.v);



            LineLinear2d opositeLineLinear = e_i.getLineLinear();

            Vector2d bisector1 = calcVectorBisector(vLine_m1, e_i.norm);
            Point2d collide1 = l_a.collide(opositeLineLinear);

            if (collide1 == null) {
//                check should be performed to exclude the case when one of the line segments starting at V is
//                parallel to ei.


                e_i = (EdgeEntry) e_i.next;
                continue;
            }

            LineLinear2d bisector1Line = (new LineParametric2d(collide1, bisector1)).getLinearForm();


//            compute the coordinates of the candidate point Bi as the intersection between the bisector at V and
//            the axis of the angle between one of the edges starting at V and the tested line segment ei
            Point2d Bb = v_i.bisector2.collide(bisector1Line);

            if (Bb == null) {

                e_i = (EdgeEntry) e_i.next;
                continue;
            }

            ///
            if (!isPointOnRay(v_i.bisector, Bb)) {
                e_i = (EdgeEntry) e_i.next;
                continue;
            }

            if (e_i.bisectorPrevious.pointInFront(Bb)
                    && e_i.bisectorNext.pointInBack(Bb)) {

                bList.add(Bb);

                double distance = v_i.v.distanceSquared(Bb);
                if (distance < candidateDistance) {
                    candidateB = Bb;
                    candidateDistance = distance;
                    candidateEdge = e_i;
                }

            }

            e_i = (EdgeEntry) e_i.next;

        }

        return new Oposite(candidateB, candidateEdge);
    }

    /** Test if point is laying on direction of ray.
     * @param bisector bisector
     * @param point point
     * @return if point is laying on direction of ray.
     */
    private static boolean isPointOnRay(Ray2d bisector, Point2d point) {
        Vector2d vector = new Vector2d(point);
        vector.sub(bisector.A);

        return bisector.U.dot(vector) >= 0;
    }

    private static Point2d computeIntersectionBisectors(VertexEntry2 v_i, VertexEntry2 v_ip1) {

        LineLinear2d b_i = v_i.bisector2;
        LineLinear2d b_ip1 = v_ip1.bisector2;
        Ray2d bb_i = v_i.bisector;
        Ray2d bb_ip1 = v_ip1.bisector;

        EdgeEntry e_i = v_i.e_b;
        LineLinear2d edgeLine = e_i.lineLinear2d;

        EdgeEvent I = new EdgeEvent();

//        Point2d intersect = b_i.collide(b_ip1);
        Point2d intersect = RayUtil.intersectRays2d(bb_i, bb_ip1, null, null);

        if (v_i.v.equals(intersect)
                || v_ip1.v.equals(intersect)) {
            // skip the same points
            return null;
        }


     // XXX FIXME odkomentowac bug !
//        if (edgeLine.pointIsUnder(v_i.v)) {
//            return null;
//        }

        if (intersect != null) {
            return intersect;
        }
        return null;
    }

    public static void splitEvent(SplitEvent I, Output output, Set<CircularList<VertexEntry2>> sLav, PriorityQueue<IntersectEntry> queue) {
        // b
        VertexEntry2 va = I.V;
//        VertexEntry vb = I.Vb;

        if (va.processed
//                && vb.processed
                ) {
            return;
        } else if (va.processed
//                || vb.processed
                ) {
            // TODO ?
            return;
        }

//        // c
//        if (vb.equals(va.previous().previous())) {
//            VertexEntry vc = (VertexEntry) va.previous();
//            System.out.println("skeleton V_aI, V_bI and V_cI,      Va: " + va.v + " I: " + I.v + " Vb: " + vb.v + " Vc: " + vc.v);
//
//            addOutputFace(output, I, va, vb);
//            addOutputFace(output, I, vc, va);
//            addOutputFace(output, I, vb, vc);
//            System.out.println("---- end ?");
//
//            va.processed = true;
//            vb.processed = true;
//
//            return;
//
//        }

        //d
        System.out.println("skeleton V_aI and V_bI             Va: " + va.v + " I: " + I.v);
        addOutputFace(output, I, va);

        //e
        va.processed = true;

        VertexEntry2 v1 = new VertexEntry2();
        v1.v = I.v;
        v1.distance = I.distance;

        v1.e_a = va.e_a;
        v1.e_b = va.e_b;
        v1.split = true;
//        va.shrinks = v1;

        VertexEntry2 v2 = new VertexEntry2();
        v2.v = I.v;
        v2.distance = I.distance;

        v2.e_a = va.e_a;
        v2.e_b = va.e_b;
        v2.split = true;

//        v1.parentVa = v2;
//        v1.parentVb = va;
//
//        v2.parentVa = va;
//        v2.parentVb = v1;

        v2.parentVa = v1;
        v2.parentVb = va;

        v1.parentVa = va;
        v1.parentVb = v2;




        { // faces

//            // back face
//            FaceNode fn = new FaceNode();
//            fn.v = newVertex;
//            va.rightFace.addNext(fn);
//
//            connectList(fn, vb.leftFace);
//            addFace(fn);

            // left face
            FaceNode fn = new FaceNode();
            fn.v = v1;
            va.leftFace.addPush(fn);
            v1.leftFace = fn;
            v1.name = "v1";
            fn.name="v1 - left";

            // right face
            fn = new FaceNode();
            fn.v = v2;
            va.rightFace.addPush(fn);
            v2.rightFace = fn;
            v2.name = "v2";
            fn.name="v2 - right";

            FaceQueue shrinksList =  new FaceQueue();

            fn = new FaceNode();
            fn.v = v2;
            shrinksList.addFirst(fn);

            v1.rightFace = fn;
            v2.leftFace = fn;
            fn.name="v top";
        }







        breakLav(I, va, v2, v1, sLav, queue);

    }



    private static void breakLav(SplitEvent I, VertexEntry2 va, VertexEntry2 v1, VertexEntry2 v2, Set<CircularList<VertexEntry2>> sLav, PriorityQueue<IntersectEntry> queue) {
        // search the opposite edge in SLAV?
        EdgeEntry opositeEdge = I.opositeEdge;


        VertexEntry2 v = I.V;
        CircularList lav = v.list();

        int sizeLav = lav.size();

        CircularList<VertexEntry2> newLawA = new CircularList<VertexEntry2>();
        CircularList<VertexEntry2> newLawB = new CircularList<VertexEntry2>();

        v1.e_a = opositeEdge;
        v2.e_b = opositeEdge;

        newLawA.addLast(v1);
        newLawB.addLast(v2);

        boolean isLavA = true;
        VertexEntry2 vert = (VertexEntry2) v.next;
        for (int i = 0; i < sizeLav - 1; i++) {
            VertexEntry2 vertexMoving = vert;
            vert = (VertexEntry2) vert.next;

            vertexMoving.remove();

            if (vertexMoving.e_a.equals(opositeEdge)) {
                isLavA = false;
            }
            if (isLavA) {
                v1.addPrevious(vertexMoving);
            } else {
                v2.addPrevious(vertexMoving);
            }

        }

        sLav.remove(lav);
        sLav.add(newLawA);
        sLav.add(newLawB);

        dv.addDebug(new DisplayLav2(newLawA, Color.ORANGE.darker().darker()));
        dv.addDebug(new DisplayLav2(newLawB, Color.ORANGE.darker().darker()));


        v1.bisector = calcBisector(v1.v, v1.e_a, v1.e_b);
        v1.bisector2 =  v1.bisector.getLinearForm();
        dv.addDebug(new DisplayLineParametric2d(v1.bisector));
        v2.bisector = calcBisector(v2.v, v2.e_a, v2.e_b);
        v2.bisector2 =  v2.bisector.getLinearForm();
        dv.addDebug(new DisplayLineParametric2d(v2.bisector));
        computeIntersections(v1, queue);
        computeIntersections(v2, queue);

//        va.
//        lAV.
//        ddd

    }

    /**
     * @param I
     * @param output
     * @param queue
     */
    public static void edgeEvent(EdgeEvent I, Output output, PriorityQueue<IntersectEntry> queue) {


        // b
        VertexEntry2 va = I.Va;
        VertexEntry2 vb = I.Vb;

        if (va.processed && vb.processed) {
            return;
        } else if (va.processed || vb.processed) {
            // TODO ?
            return;
        }
//        while (va.shrinks != null) {
//            va = va.shrinks;
//        }

//        while (vb.shrinks != null) {
//            vb = vb.shrinks;
//        }
        boolean processed = false;
        //c
        if ((va).v.equals(I.v)
                ||((vb).v.equals(I.v))) {
            // special case
            // the colision point is in queue allredy
            // so we don't calc it, only draw sceleton edges.

            addOutputFace(output, I, va, vb);
            processed = true;
           // continue;


        } else  if (vb.equals(va.previous().previous())) {
            VertexEntry2 vc = (VertexEntry2) va.previous();
            System.out.println("skeleton V_aI, V_bI and V_cI,      Va: " + va.v + " I: " + I.v + " Vb: " + vb.v + " Vc: " + vc.v);

            VertexEntry2 center = new VertexEntry2();
            center.v = I.v;
            center.distance = I.distance;

            addOutputFace(output, I, va, vb);
            addOutputFace(output, I, vc, va);
            addOutputFace(output, I, vb, vc);
            System.out.println("---- end ?");

            addFaceBack(center, va, vb );
            addFaceBack(center, vc, va );
            addFaceBack(center, vb, vc );


            va.processed = true;
            vb.processed = true;

            return;
        } else {

            //d
            System.out.println("skeleton V_aI and V_bI             Va: " + va.v + " I: " + I.v + " Vb: " + vb.v);
            addOutputFace(output, I, va, vb);
        }
        //e
        va.processed = true;
        vb.processed = true;


//        VertexEntry2 newVertex = newEdgeVertex(I, va, vb);
        VertexEntry2 newVertex = new VertexEntry2();
        newVertex.v = I.v;
        newVertex.distance = I.distance;
        newVertex.parentVa = I.Va;
        newVertex.parentVb = I.Vb;

        I.Va.shrinks = newVertex;
        I.Vb.shrinks = newVertex;

        va.addNext(newVertex);
//        lav.addBefore(newVertex, va);

        newVertex.e_a = va.e_a;
        newVertex.e_b = vb.e_b;



        addFaces(newVertex,I.Va,I.Vb );



//        lav.remove(va);
//        lav.remove(vb);
        va.remove(); vb.remove();
        //f

        if (processed) {
            // ???
            //TESTME
            System.out.println(" point was processed before skiping: " + newVertex.v);
            return;
        }
        newVertex.bisector = calcBisector(newVertex.v, newVertex.e_a, newVertex.e_b);
        newVertex.bisector2 = newVertex.bisector.getLinearForm();
            //calcBisector2(newVertex.v, newVertex.e_a, newVertex.e_b);

        dv.addDebug(new DisplayLineParametric2d(newVertex.bisector));
        {

            VertexEntry2 v_i = newVertex;
            VertexEntry2 v_ip1 = (VertexEntry2) newVertex.next();
            VertexEntry2 v_im1 = (VertexEntry2) newVertex.previous();

//            calcBisection(newVertex, queue);
            calcBisection2(v_im1, v_i, queue);
            calcBisection2(v_i, v_ip1, queue);
        }
    }


//   private static void addFace(VertexEntry2 newVertex, VertexEntry2 va, VertexEntry2 vb) {
//
//
//   }

    private static void addFaceBack(VertexEntry2 newVertex, VertexEntry2 va, VertexEntry2 vb) {
     // back face
        FaceNode fn = new FaceNode();
        fn.v = newVertex;
        va.rightFace.addPush(fn);

        connectList(fn, vb.leftFace);
        addFace(fn);
    }

       private static void addFaces(VertexEntry2 newVertex, VertexEntry2 va, VertexEntry2 vb) {
       { // faces

           addFaceBack(newVertex, va, vb);


           // left face
           FaceNode fn = new FaceNode();
           fn.v = newVertex;
           va.leftFace.addPush(fn);
           newVertex.leftFace = fn;

           // right face
           fn = new FaceNode();
           fn.v = newVertex;
           vb.rightFace.addPush(fn);
           newVertex.rightFace = fn;
       }

    }

private static void addFace(FaceNode fn) {

        dv.addDebug(new DisplayFaceNode(fn, Color.red));

    }

private static void connectList(FaceNode rightFace, FaceNode leftFace) {

    dv.addDebug(new DisplayFaceNode((FaceQueue)rightFace.list(), Color.pink.darker()));
    dv.addDebug(new DisplayFaceNode((FaceQueue)leftFace.list(), Color.pink.darker()));

    if (((FaceQueue) leftFace.list()).border) {
        leftFace.addQueue(rightFace);
    }
    rightFace.addQueue(leftFace);


//        leftFace.list()
//
//       int size = leftFace.list().size();
//
//       FaceNode current = leftFace;
//       for (int i = 0; i < size; i++) {
//           current = (FaceNode) current.next;
//
//           current.remove();
//
//           rightFace.addNext(current);
//
//       }

    }

private static VertexEntry2 newEdgeVertex(EdgeEvent I, VertexEntry2 va, VertexEntry2 vb) {
        VertexEntry2 newVertex = new VertexEntry2();
        newVertex.v = I.v;
        newVertex.distance = I.distance;

        newVertex.parentVa = I.Va;
        newVertex.parentVb = I.Vb;

        I.Va.shrinks = newVertex;
        I.Vb.shrinks = newVertex;



        va.addNext(newVertex);
//        lav.addBefore(newVertex, va);

        newVertex.e_a = va.e_a;
        newVertex.e_b = vb.e_b;








        return newVertex;
    }



    /**
     *
     */
    public static Comparator<IntersectEntry> distanseComparator = new Comparator<IntersectEntry>() {
        @Override
        public int compare( IntersectEntry pV1, IntersectEntry pV2 ) {
            return Double.compare( pV1.distance, pV2.distance );
        }
    };

    private static double calcDistance(Point2d pIntersect, EdgeEntry e_i) {
        Vector2d edge = new Vector2d(e_i.p2);
        edge.sub(e_i.p1);

        Point2d intersect = new Point2d(pIntersect);
        intersect.sub(e_i.p1);

        Vector2d pointOnVector = Algebra.orthogonalProjection(edge, intersect);

        return distance(intersect, pointOnVector);
    }

    /**
     * Computes the distance between this point and point p1.
     *
     * @param p1
     *            the other point
     */
    private static double distance(Tuple2d p0, Tuple2d p1) {
        double dx, dy;

        dx = p0.x - p1.x;
        dy = p0.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param p
     * @param e1
     * @param e2
     * @return Bisector
     */
    public static Ray2d calcBisector(Point2d p, EdgeEntry e1, EdgeEntry e2) {

        Vector2d norm1 = e1.getNorm();
        Vector2d norm2 = e2.getNorm();


        Vector2d bisector = calcVectorBisector(norm1, norm2);

        return new Ray2d(p, bisector);
    }

    private static Vector2d calcVectorBisector(Vector2d norm1, Vector2d norm2) {

        return Vector2dUtil.bisectorNormalized(norm1, norm2);
    }



    /**
     * @author kendzi
     *
     */
    public static class VertexEntry2 extends CircularNode {

        public FaceNode leftFace;// = new CircularList<Skeleton.FaceNode>();
        public FaceNode rightFace;
        public FaceNode topFace;

        public LineLinear2d bisector2;

        public Ray2d bisector;

        public Point2d v;

        public String name;

        public double distance;

        /**
         * Previous edge.
         */
        EdgeEntry e_a;

        /**
         * Next edge.
         */
        EdgeEntry e_b;

        VertexEntry2 parentVa;
        VertexEntry2 parentVb;

        boolean processed;

        VertexEntry2 shrinks;

        boolean split;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "VertexEntry [v=" + this.v + ", processed=" + this.processed + ", bisector=" + this.bisector + ", e_a=" + this.e_a
                    + ", e_b=" + this.e_b
                    + ", parentVa=" + (this.parentVa != null ? this.parentVa.v : "null")
                    + ", parentVb=" + (this.parentVb != null ? this.parentVb.v : "null")
                    + ", shrinks=" + (this.shrinks != null ? this.shrinks.v : "null")
                    + ", bisector2=" + this.bisector2 + "]";
        }
    }

    /**
     * @author kendzi
     *
     */
    public static abstract class IntersectEntry {


        public Point2d v;


//        EdgeEntry e_im1;
//        EdgeEntry e_i;

        double distance;



        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "IntersectEntry [v=" + this.v
                + ", EventType=" + this.eventType
                + ", distance=" + this.distance + "]";
        }

        public EventType eventType;
    }

    public static class FaceQueue extends PathQueue<FaceNode> {
        boolean border;

        /**
         * @return the border
         */
        public boolean isBorder() {
            return border;
        }

        /**
         * @param border the border to set
         */
        public void setBorder(boolean border) {
            this.border = border;
        }
    }

    public static class FaceNode extends PathQueueNode {
        public VertexEntry2 v;
        boolean border;
        String name;

    }

    private static class EdgeEvent extends IntersectEntry{
        public Ray2d bisector;

        VertexEntry2 Va;
        VertexEntry2 Vb;

        EdgeEvent() {
            super();

            this.eventType = EventType.EDGE_EVENT;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "EdgeEvent [v=" + this.v
                + ", Va=" + (this.Va != null ? this.Va.v : "null")
                + ", Vb=" + (this.Vb != null ? this.Vb.v : "null")
                + ", distance=" + this.distance + "]";
        }
    }

    /**
     * @author kendzi
     *
     */
    public  static class SplitEvent extends IntersectEntry{
        public EdgeEntry opositeEdge;
        VertexEntry2 V;


        SplitEvent() {
            super();

            this.eventType = EventType.SPLIT_EVENT;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "SplitEvent [v=" + this.v
                + ", V=" + (this.V != null ? this.V.v : "null")
                + ", distance=" + this.distance + "]";
        }
    }






    private static enum EventType {
        EDGE_EVENT,
        SPLIT_EVENT
    }


//    private String nullObject(Object pObject) {
//        if (pObject == null) {
//            return "null";
//        }
//        return pObject.toString();
//    }

    /**
     * @author kendzi
     *
     */
    public static class EdgeEntry extends CircularNode{
        Point2d p1;
        Point2d p2;

//        EdgeEntry next;
//        EdgeEntry previous;

        LineLinear2d bisectorPrevious;
        LineLinear2d bisectorNext;



        LineLinear2d lineLinear2d;

        Vector2d norm;

        public EdgeEntry(Point2d pVertex1, Point2d pVertex2) {
            this.p1 = pVertex1;
            this.p2 = pVertex2;

            this.lineLinear2d = new LineLinear2d(this.p1, this.p2);

            this.norm = new Vector2d(this.p2);
            this.norm.sub(this.p1);
            this.norm.normalize();

        }

        public LineLinear2d getLineLinear() {
            return this.lineLinear2d;
        }

        Vector2d getNorm() {
            return this.norm;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "EdgeEntry [p1=" + this.p1 + ", p2=" + this.p2 + "]";
        }


    }

    public static void main(String[] args) {
        List<Point2d> polygon = new ArrayList<Point2d>();
        polygon.add(new Point2d(0,0));
        polygon.add(new Point2d(1,0));
        polygon.add(new Point2d(1,1));
        polygon.add(new Point2d(0,1));

        sk(polygon);
    }

    public static class Output {
        public List<List<Point2d>> faces = new ArrayList<List<Point2d>>();

        public List<PolygonList2d> faces2 = new ArrayList<PolygonList2d>();

        public Map<Point2d, Double> distance = new HashMap<Point2d, Double>();
    }

}
