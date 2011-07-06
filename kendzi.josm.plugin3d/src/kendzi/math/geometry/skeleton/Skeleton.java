/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.math.geometry.skeleton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

import kendzi.math.geometry.Algebra;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.line.LinePoints2d;
import kendzi.math.geometry.skeleton.CircularLinkedList.CircularLinkedListItrerator;

public class Skeleton {

    public static Output sk(List<Point2d> pBorder) {

        PriorityQueue<IntersectEntry> queue = new PriorityQueue<IntersectEntry>(3, distanseComparator);

        CircularLinkedList<VertexEntry> LAV = new CircularLinkedList<VertexEntry>();

        Output output = new Output();

        // fell vertex
        for (Point2d p : pBorder) {
            VertexEntry v = new VertexEntry();
            v.v = p;
            LAV.add(v);
        }

        //fell edges
        CircularLinkedListItrerator LAVi = LAV.cirkularListIterator(0);

        for (int i = 0; i < LAV.size(); i++) {
       VertexEntry v = (VertexEntry) LAVi.next();
            VertexEntry vPrevious = (VertexEntry) LAVi.getPrevious();
            VertexEntry vNext = (VertexEntry) LAVi.getNext();

            EdgeEntry e_im1 = new EdgeEntry(vPrevious.v, v.v);
            EdgeEntry e_i = new EdgeEntry(v.v,vNext.v);

            v.e_a = e_im1;
            v.e_b = e_i;

            v.bisector = calcBisector(v.v, e_im1, e_i);
            v.bisector2 =  v.bisector.getLinearForm();
                //calcBisector2(v.v, e_im1, e_i);

        }

        LAVi = LAV.cirkularListIterator(0);

        for (int i = 0; i < LAV.size(); i++) {
            VertexEntry v_i = (VertexEntry) LAVi.next();
//            VertexEntry vm1 = (VertexEntry) LAVi.getPrevious();
            VertexEntry v_ip1 = (VertexEntry) LAVi.getNext();

            calcBisection2(v_i, v_ip1, queue);


        }


        /// STEP 2

        while (!queue.isEmpty()) {
            // a
            IntersectEntry I = queue.poll();
            System.out.println(I);
//            System.out.println("I: " + I.v + " distance: " +  I.distance + " Va: " + I.Va.v+ " Vb: " + I.Vb.v);

            if (EventType.EDGE_EVENT.equals(I.eventType)) {
                edgeEvent((EdgeEvent) I, output, LAV, queue);

                continue;
            } else if (EventType.SPLIT_EVENT.equals(I.eventType)) {
                splitEvent((SplitEvent) I, output, LAV, queue);
            }


        }
        return output;


//
//        LAVi = LAV.cirkularListIterator(0);
//
//        for (int i = 0; i < 10; i++) {
//            Point2d next = LAVi.next();
//            System.err.println(next);
//        }
    }

    private static void addOutputFace(Output output, IntersectEntry i, VertexEntry va, VertexEntry vb) {

        List<Point2d> edge = new ArrayList<Point2d>();

        List<Point2d> leftEdge = new ArrayList<Point2d>();
        if (!i.v.equals(va.v)) {
            leftEdge.add(va.v);
        }

        VertexEntry leftEdgeVertex = va;
        while (leftEdgeVertex.parentVb != null) {
            leftEdgeVertex = leftEdgeVertex.parentVb;
            leftEdge.add(leftEdgeVertex.v);
        }

        for (int j = leftEdge.size() - 1; j >= 0; j--) {
            edge.add(leftEdge.get(j));
        }

        edge.add(i.v);

        if (!i.v.equals(vb.v)) {
            edge.add(vb.v);
        }

        VertexEntry rightEdgeVertex = vb;
        while (rightEdgeVertex.parentVa != null) {
            rightEdgeVertex = rightEdgeVertex.parentVa;
            edge.add(rightEdgeVertex.v);
        }

        output.faces.add(edge);
    }
    private static void addOutputFace(Output output, IntersectEntry i, VertexEntry va) {
        // only sysout
        System.out.println("adding edge on split event!: " + i.v);

    }

    private static void calcBisection(VertexEntry v_i, VertexEntry v_ip1, PriorityQueue<IntersectEntry> queue) {
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

    private static void calcBisection2(VertexEntry v_i, VertexEntry v_ip1, PriorityQueue<IntersectEntry> queue) {

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

        if (edgeLine.pointIsUnder(v_i.v)) {
            return;
        }

        if (intersect != null) {
            I.v = intersect;

            I.distance = calcDistance(intersect, e_i);

            I.Va = v_i;
            I.Vb = v_ip1;

            I.eventType = EventType.EDGE_EVENT;

            queue.add(I);
        }
    }



    private static void computeIntersections(VertexEntry v_i, VertexEntry v_ip1111, PriorityQueue<IntersectEntry> queue) {

        EdgeEntry e_i = v_i.e_b;

        VertexEntry v_ip1 = (VertexEntry) v_i.next();
        VertexEntry v_im1 = (VertexEntry) v_i.previous();

        Point2d intersectionBisectors1 = computeIntersectionBisectors(v_i, v_ip1);
        Point2d intersectionBisectors2 = computeIntersectionBisectors(v_im1, v_i);


        Point2d intersectionBisector = null;

        if (intersectionBisectors1 == null) {
            intersectionBisector = intersectionBisectors2;
        } else if (intersectionBisectors2 == null) {
            intersectionBisector = intersectionBisectors1;
        } else {
            intersectionBisector = v_i.v.distanceSquared(intersectionBisectors1) < v_i.v.distanceSquared(intersectionBisectors2) ?
                intersectionBisectors1 : intersectionBisectors2;
        }


        VertexEntry v1 = v_ip1;
        VertexEntry v2 = (VertexEntry) v1.next();

        Point2d B = null;
        double candidateBDistance = Double.MAX_VALUE;

        while (!v2.equals(v_i)) {

            if (!(v1.bisector2.pointInFront(v_i.v)
                    && v2.bisector2.pointInBack(v_i.v))) {
                // XXX get next
                continue;
            }

            LinePoints2d opositeLine = new LinePoints2d(v1.v, v2.v);

            if (!opositeLine.inFront(v_i.v)) {
                // XXX get next
                continue;
            }

            //XXX
            LineLinear2d opositeLineLinear = opositeLine.getLineParametric2d().getLinearForm();

            Vector2d opositeVector = new Vector2d(v2.v);
            opositeVector.sub(v1.v);
            opositeVector.normalize();



            Vector2d n_a = new Vector2d(v_i.v);
            n_a.sub(v_im1.v);
            n_a.normalize();
            LineLinear2d l_a = new LineLinear2d(v_i.v, v_im1.v);


            Vector2d n_b = new Vector2d(v_ip1.v);
            n_b.sub(v_i.v);
            n_b.normalize();
            LineLinear2d l_b = new LineLinear2d(v_ip1.v, v_i.v);



            Vector2d bisector1 = calcVectorBisector(n_a, opositeVector);
            Point2d collide1 = l_a.collide(opositeLineLinear);
            LineLinear2d bisector1Line = (new LineParametric2d(collide1, bisector1)).getLinearForm();


            Vector2d bisector2 = calcVectorBisector(opositeVector, n_b);
            Point2d collide2 = opositeLineLinear.collide(l_b);
            LineLinear2d bisector2Line = (new LineParametric2d(collide2, bisector1)).getLinearForm();


            Point2d B1 = bisector1Line.collide(v_i.bisector2);
            Point2d B2 = bisector2Line.collide(v_i.bisector2);

            //???
            Point2d candidateB = (v_i.v.distanceSquared(B1) < v_i.v.distanceSquared(B2) ? B1 : B2);

            if (v1.bisector2.pointIsOver(candidateB) && v2.bisector2.pointInBack(candidateB)) {

                double distance = v_i.v.distanceSquared(candidateB);
                if (distance< candidateBDistance) {
                    candidateBDistance  = distance;
                    B = candidateB;
                }
            }
            v1 = v2;
            v2 = (VertexEntry) v1.next();
        }




        if (intersectionBisector != null
                && (B == null || v_i.v.distanceSquared(intersectionBisector) < v_i.v.distanceSquared(B))) {

            EdgeEvent I = new EdgeEvent();

            I.v = intersectionBisector;

            I.distance = calcDistance(intersectionBisector, e_i);

            I.Va = v_i;
            I.Vb = v_ip1;

//            I.eventType = EventType.EDGE_EVENT;

            queue.add(I);


        } else if (B != null) {

            SplitEvent I = new SplitEvent();

            I.v = B;

            I.distance = calcDistance(B, e_i);

//            I.Va = v_i;
//            I.Vb = v_ip1;

//            I.eventType = EventType.SPLIT_EVENTEDGE_EVENT;

            queue.add(I);
        }








//        if (intersect != null) {
//            I.v = intersect;
//
//            I.distance = calcDistance(intersect, e_i);
//
//            I.Va = v_i;
//            I.Vb = v_ip1;
//
//            I.eventType = EventType.EDGE_EVENT;
//
//            queue.add(I);
//        }
    }

    private static Point2d computeIntersectionBisectors(VertexEntry v_i, VertexEntry v_ip1) {

        LineLinear2d b_i = v_i.bisector2;
        LineLinear2d b_ip1 = v_ip1.bisector2;

        EdgeEntry e_i = v_i.e_b;
        LineLinear2d edgeLine = e_i.lineLinear2d;

        EdgeEvent I = new EdgeEvent();

        Point2d intersect = b_i.collide(b_ip1);

        if (v_i.v.equals(intersect)
                || v_ip1.v.equals(intersect)) {
            // skip the same points
            return null;
        }

        if (edgeLine.pointIsUnder(v_i.v)) {
            return null;
        }

        if (intersect != null) {
            return intersect;
        }
        return null;
    }

    public static void splitEvent(SplitEvent I, Output output, CircularLinkedList<VertexEntry> LAV, PriorityQueue<IntersectEntry> queue) {
        // b
        VertexEntry va = I.V;
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

        VertexEntry v1 = new VertexEntry();
        v1.v = I.v;

        VertexEntry v2 = new VertexEntry();
        v2.v = I.v;

    }



    public static void edgeEvent(EdgeEvent I, Output output, CircularLinkedList<VertexEntry> LAV, PriorityQueue<IntersectEntry> queue) {


        // b
        VertexEntry va = I.Va;
        VertexEntry vb = I.Vb;

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
            VertexEntry vc = (VertexEntry) va.previous();
            System.out.println("skeleton V_aI, V_bI and V_cI,      Va: " + va.v + " I: " + I.v + " Vb: " + vb.v + " Vc: " + vc.v);

            addOutputFace(output, I, va, vb);
            addOutputFace(output, I, vc, va);
            addOutputFace(output, I, vb, vc);
            System.out.println("---- end ?");

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


        VertexEntry newVertex = new VertexEntry();
        newVertex.v = I.v;
        newVertex.parentVa = I.Va;
        newVertex.parentVb = I.Vb;

        I.Va.shrinks = newVertex;
        I.Vb.shrinks = newVertex;



        LAV.addBefore(newVertex, va);

        newVertex.e_a = va.e_a;
        newVertex.e_b = vb.e_b;

        LAV.remove(va);
        LAV.remove(vb);

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

        {

            VertexEntry v_i = newVertex;
            VertexEntry v_ip1 = (VertexEntry) newVertex.next();
            VertexEntry v_im1 = (VertexEntry) newVertex.previous();

//            calcBisection(newVertex, queue);
            calcBisection2(v_im1, v_i, queue);
            calcBisection2(v_i, v_ip1, queue);
        }
    }



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

    public static Ray2d calcBisector(Point2d p, EdgeEntry e1, EdgeEntry e2) {

        Vector2d norm1 = e1.getNorm();
        Vector2d norm2 = e2.getNorm();


        Vector2d bisector = calcVectorBisector(norm1, norm2);

        return new Ray2d(p, bisector);
    }

    private static Vector2d calcVectorBisector(Vector2d norm1, Vector2d norm2) {
        Vector2d e1v = orthogonal(norm1);
        Vector2d e2v = orthogonal(norm2);

//        e1v.normalize();
//        e2v.normalize();


        if (e1v.dot(e2v) >= 0) {

            e1v.add(e2v);

            if (equalsEpsilon(e2v.x) || equalsEpsilon(e2v.x)) {
                //???
            }

            if (e1v.x == 0 && e1v.y == 0) {
    //            edges are parnell. Chose any one
                // later we calc direction
                return new Vector2d(norm1);
            }



            return e1v;

        }


        e1v = new Vector2d(norm1);
        e2v = new Vector2d(norm2);

        if (norm1.dot(e1v) > 0) {
            e1v.negate();

        } else {
            e2v.negate();
        }
        e1v.add(e2v);
        return e1v;
    }

    /**
     * @param p
     * @param e1
     * @param e2
     * @return
     */
    @Deprecated
    static LineLinear2d calcBisector2(Point2d p, EdgeEntry e1, EdgeEntry e2) {

        LineLinear2d line1 = e1.getLineLinear();
        LineLinear2d line2 = e2.getLineLinear();

        double A1 = line1.A;
        double B1 = line1.B;
//        double C1 = line1.C;

        double A2 = line2.A;
        double B2 = line2.B;
//        double C2 = line2.C;

        double norm = Math.sqrt(A1 * A1 + B1 * B1)
                    * Math.sqrt(A2 * A2 + B2 * B2);

        double cosA = (A1 * A2 + B1 * B2) / norm;
        double sinA = (A1 * B2 - A2 * B1) / norm;

        double A = cosA;
        double B = sinA;

        double C = -A * p.x - B * p.y;

        return new LineLinear2d(A, B, C);
    }




    private static double EPSILON = 0.00000001;

    static boolean equalsEpsilon(double pNumber) {
        if((pNumber<0?-pNumber:pNumber) > EPSILON) {
            return false;
        }
        return false;

    }

    static Vector2d orthogonal(Vector2d v) {
       return new Vector2d(-v.y, v.x);
    }



    private static class VertexEntry extends CircularLinkedList.Entry {



        public LineLinear2d bisector2;

        public Ray2d bisector;

        Point2d v;

        EdgeEntry e_a;
        EdgeEntry e_b;

        VertexEntry parentVa;
        VertexEntry parentVb;

        boolean processed;

        VertexEntry shrinks;

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

    private static abstract class IntersectEntry {


        Point2d v;


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

    private static class EdgeEvent extends IntersectEntry{
        public Ray2d bisector;

        VertexEntry Va;
        VertexEntry Vb;

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

    private static class SplitEvent extends IntersectEntry{
        VertexEntry V;


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

    private static class EdgeEntry {
        Point2d p1;
        Point2d p2;
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
        List<List<Point2d>> faces = new ArrayList<List<Point2d>>();

        Map<Point2d, Double> distance = new HashMap<Point2d, Double>();
    }

}
