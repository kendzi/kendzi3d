/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */


package kendzi.math.geometry.skeleton;

import static kendzi.math.geometry.skeleton.LavUtil.*;

import java.util.ArrayList;
import java.util.Collections;
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
import kendzi.math.geometry.AngleUtil;
import kendzi.math.geometry.line.LineLinear2d;
import kendzi.math.geometry.line.LineParametric2d;
import kendzi.math.geometry.line.LineSegment2d;
import kendzi.math.geometry.point.Vector2dUtil;
import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.skeleton.LavUtil.SplitSlavs;
import kendzi.math.geometry.skeleton.RayUtil.IntersectPoints;
import kendzi.math.geometry.skeleton.debug.DV;
import kendzi.math.geometry.skeleton.events.MultiEdgeEvent;
import kendzi.math.geometry.skeleton.events.MultiSplitEvent;
import kendzi.math.geometry.skeleton.events.PickEvent;
import kendzi.math.geometry.skeleton.events.chains.Chain;
import kendzi.math.geometry.skeleton.events.chains.Chain.ChainType;
import kendzi.math.geometry.skeleton.events.chains.EdgeChain;
import kendzi.math.geometry.skeleton.events.chains.SingleEdgeChain;
import kendzi.math.geometry.skeleton.events.chains.SplitChain;

import org.apache.log4j.Logger;

public class Skeleton {
    /** Log. */
    private static final Logger log = Logger.getLogger(Skeleton.class);
    private static final double SPLIT_EPSILON = 1E-10;

    private static boolean debug = true;

    public static SkeletonOutput skeleton(List<Point2d> polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("polygon can't be null");
        }
        if (polygon.get(0).equals(polygon.get(polygon.size() - 1))) {
            throw new IllegalArgumentException("polygon can't start and end with the same point");
        }
        return skeleton(polygon, null);
    }

    public static SkeletonOutput skeleton(List<Point2d> polygon, List<List<Point2d>> holes) {

        polygon = makeCounterClockwise(polygon);
        holes = makeClockwise(holes);

        if (debug) {
            polygon = addDebugNames(polygon, "p");
            if (holes != null) {
                List<List<Point2d>> newHoles = new ArrayList<List<Point2d>>();
                int h = 0;
                for (List<Point2d> hole : holes) {
                    newHoles.add(addDebugNames(hole, "h" + h+"_p"));
                    h++;
                }
                holes = newHoles;
            }
        }

        DV.clear();

        DV.debug(polygon);
        DV.debugNames(polygon);
        if (holes != null) {
            for (List<Point2d> hole : holes) {
                DV.debug(hole);
                DV.debugNames(hole);
            }
        }

        PriorityQueue<SkeletonEvent> queue = new PriorityQueue<SkeletonEvent>(3, distanseComparator);

        Set<CircularList<VertexEntry2>> sLav = new HashSet<CircularList<VertexEntry2>>();

        List<FaceQueue> faces = new ArrayList<FaceQueue>();

        SkeletonOutput output = new SkeletonOutput();

        // / STEP 1

        prepareBisectors(polygon, sLav, faces);

        if (holes != null) {
            for (List<Point2d> inner : holes) {
                prepareBisectors(inner, sLav, faces);
            }
        }

        List<EdgeEntry> edges = new ArrayList<Skeleton.EdgeEntry>();
        for (CircularList<VertexEntry2> lav : sLav) {
            for (VertexEntry2 v_i : lav) {
                edges.add(v_i.e_a);
            }
        }

        computeInitEvents(sLav, queue, edges);

        for (CircularList<VertexEntry2> lav : sLav) {


            /* new */
            for (VertexEntry2 v_i : lav) {


                DV.debug(queue);
            }

            DV.debug(queue);
            DV.debug(lav);
        }

        DV.debug(queue);


        int count = 0;

        /// STEP 2

        List<SkeletonEvent> processedEvents = new ArrayList<Skeleton.SkeletonEvent>();

        while (!queue.isEmpty()) {
            // start processing skeleton level
            count = assertMaxNumberOfInteraction(count);

            double levelHeight = queue.peek().distance;

            List<SkeletonEvent> levelEvents = loadAndGroupLevelEvents(queue);

            debugSteep(queue, sLav, faces);

            for (SkeletonEvent event : levelEvents) {

                DV.debug(event);

                if (event.isObsolete()) {
                    // event is outdated some of parent vertex was processed before
                    continue;
                }

                processedEvents.add(event);

                DV.debugProcessedEvents(processedEvents);

                if (event instanceof EdgeEvent) {

                    edgeEvent((EdgeEvent) event, output, sLav, queue, edges);
                    continue;

                } else if (event instanceof SplitEvent) {

                    splitEvent((SplitEvent) event, output, sLav, queue, edges);
                    continue;

                } else if (event instanceof MultiSplitEvent) {

                    multiSplitEvent((MultiSplitEvent) event, output, sLav, queue, edges);
                    continue;
                } else if (event instanceof PickEvent) {

                    pickEvent((PickEvent) event, output, sLav, queue, edges);
                    continue;
                } else if (event instanceof MultiEdgeEvent) {

                    multiEdgeEvent((MultiEdgeEvent) event, output, sLav, queue, edges);
                    continue;
                } else {
                    throw new RuntimeException("unknown event type: " + event.getClass());
                }
            }
            DV.debug(sLav);
            processTwoNodeLavs(sLav);

            removeEventsUnderHeight(queue, levelHeight);
            removeEmptyLav(sLav);

        }

        for (FaceQueue f : faces) {
            DV.debug(f);

        }

        addFacesToOutput(faces, output);


        // DV.debug(polygon);
        // DV.debug(output);

        return output;
    }

    private static void processTwoNodeLavs(Set<CircularList<VertexEntry2>> sLav) {
        DV.debug(sLav);
        for (CircularList<VertexEntry2> lav : sLav) {
            if (lav.size() == 2) {
                DV.debug(lav);

                VertexEntry2 first = lav.first();
                VertexEntry2 last = first.next();

                DV.debug(first.leftFace);
                DV.debug(last.rightFace);
                connectList(first.leftFace, last.rightFace);

                DV.debug(last.leftFace);
                DV.debug(first.rightFace);
                connectList(first.rightFace, last.leftFace);

                first.processed = true;
                last.processed = true;

                removeFromLav(first);
                removeFromLav(last);
            }
        }
        DV.debug(sLav);
    }

    private static void removeEmptyLav(Set<CircularList<VertexEntry2>> sLav) {
        // TODO Auto-generated method stub

    }

    private static void multiEdgeEvent(MultiEdgeEvent event, SkeletonOutput output, Set<CircularList<VertexEntry2>> sLav,
            PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {

        Point2d center = event.getPoint();
        List<EdgeEvent> edgeList = event.getChain().getEdgeList();

        VertexEntry2 previousVertex = event.getChain().getPreviousVertex();
        previousVertex.processed = true;

        VertexEntry2 nextVertex = event.getChain().getNextVertex();
        nextVertex.processed = true;

        VertexEntry2 edgeVertex = new VertexEntry2();
        edgeVertex.v = center;
        edgeVertex.distance = event.getDistance();
        edgeVertex.parentVa = previousVertex;
        edgeVertex.parentVb = nextVertex;
        edgeVertex.e_a = previousVertex.e_a;
        edgeVertex.e_b = nextVertex.e_b;
        edgeVertex.bisector = calcBisector(edgeVertex.v, edgeVertex.e_a, edgeVertex.e_b);
        edgeVertex.bisector2 = edgeVertex.bisector.getLinearForm();

        // left face
        addFaceLeft(edgeVertex, previousVertex);

        // right face
        addFaceRight(edgeVertex, nextVertex);

        DV.debug(edgeVertex.bisector);

        previousVertex.addPrevious(edgeVertex);

        // previousVertex.remove();
        // nextVertex.remove();

        addMultiBackFaces(edgeList, edgeVertex);

        computeIntersections(edgeVertex, queue, edges);
    }

    private static void addMultiBackFaces(List<EdgeEvent> edgeList, VertexEntry2 edgeVertex) {
        for (EdgeEvent edgeEvent : edgeList) {

            VertexEntry2 leftVertex = edgeEvent.getLeftVertex();
            leftVertex.processed = true;
            removeFromLav(leftVertex);

            VertexEntry2 rightVertex = edgeEvent.getRightVertex();
            rightVertex.processed = true;
            removeFromLav(rightVertex);

            addFaceBack(edgeVertex, leftVertex, rightVertex);
        }
    }

    private static void pickEvent(PickEvent event, SkeletonOutput output, Set<CircularList<VertexEntry2>> sLav,
            PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {

        Point2d center = event.getPoint();
        List<EdgeEvent> edgeList = event.getChain().getEdgeList();

        VertexEntry2 pickVertex = new VertexEntry2();
        pickVertex.v = center;
        pickVertex.processed = true;

        addMultiBackFaces(edgeList, pickVertex);
    }

    private static void multiSplitEvent(MultiSplitEvent event, SkeletonOutput output, Set<CircularList<VertexEntry2>> sLav,
            PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {

        List<Chain> chains = event.getChains();
        final Point2d center = event.v;
        createOppositeEdgeChains(sLav, chains, center);

        // List<ChainEnds> chains = new ArrayList<Skeleton.ChainEnds>();


        // for (SkeletonEvent skeletonEvent : chains2) {
        //
        // if (skeletonEvent instanceof VertexSplitEvent) {
        // SplitEvent splitEvent = (SplitEvent) skeletonEvent;
        // if (splitEvent.getParent().list().size() < 3) {
        // throw new
        // RuntimeException("split event is refering to less then 3 vertex lav");
        // }
        // dddd
        // edgesList.add(new ChainEndsImpl(splitEvent.getParent().e_a,
        // splitEvent.getParent().e_b, splitEvent.getParent()
        // .previous(), splitEvent.getParent().next(), splitEvent.getParent()));
        //
        // } else if (skeletonEvent instanceof SplitEvent) {
        // SplitEvent splitEvent = (SplitEvent) skeletonEvent;
        //
        // if (splitEvent.getParent().list().size() < 3) {
        // throw new
        // RuntimeException("split event is refering to les then 3 vertex lav");
        // }
        //
        // edgesList.add(new ChainEndsImpl(splitEvent.getParent().e_a,
        // splitEvent.getParent().e_b, splitEvent.getParent()
        // .previous(), splitEvent.getParent().next(), splitEvent.getParent()));
        //
        // // find lav vertex for opposite edge
        // EdgeEntry oppositeEdge = splitEvent.getOppositeEdge();
        //
        // // FIXME what when we share edge between two lavs?
        // VertexEntry2 nextVertex = findEdgeLav(sLav, oppositeEdge, null);
        // VertexEntry2 previousVertex = nextVertex.previous();
        //
        // edgesList.add(new ChainEndsImpl(splitEvent.getOppositeEdge(),
        // splitEvent.getOppositeEdge(), previousVertex, nextVertex, null));
        //
        // } else if (skeletonEvent instanceof EdgeEvent) {
        // EdgeEvent edgeEvent = (EdgeEvent) skeletonEvent;
        // edgesList.add(new ChainEndsImpl(edgeEvent.Va.e_a, edgeEvent.Vb.e_b,
        // edgeEvent.Va, edgeEvent.Vb, null));
        // }
        // }



        Comparator<ChainEnds> multiSplitSorter = new Comparator<Skeleton.ChainEnds>() {

            @Override
            public int compare(ChainEnds chain1, ChainEnds chain2) {
                // sort it by chain edges begins
                double angle1 = AngleUtil.angle(center, chain1.getPreviousEdge().p1);
                double angle2 = AngleUtil.angle(center, chain2.getPreviousEdge().p1);


                // XXX equals? assert?
                // FIXME clockwise or anti clockwise?!
                // XXX chosen CounterClockwise !!!
                return angle1 > angle2 ? 1 : -1;
            }
        };

        // sort list of chains clock wise
        Collections.sort(chains, multiSplitSorter);

        // face node for split event is shared between two chains
        FaceNode lastFaceNode = null;

        // connect all edges into new bisectors and lavs
        int edgeListSize = chains.size();
        for (int i = 0 ; i< edgeListSize;i++) {
            ChainEnds chainBegin = chains.get(i);
            ChainEnds chainEnd = chains.get((i + 1) % edgeListSize);

            // removeFromLav(chainBegin.getCurrentVertex());
            // removeFromLav(chainEnd.getCurrentVertex());

            VertexEntry2 newVertex = createMultiSplitVertex(chainBegin.getNextEdge(), chainEnd.getPreviousEdge(), center);

            // Split and merge lavs...
            // FIXME
            DV.debug(sLav);
            VertexEntry2 beginNextVertex = chainBegin.getNextVertex();
            VertexEntry2 endPreviousVertex = chainEnd.getPreviousVertex();

            if (isSameLav(beginNextVertex, endPreviousVertex)) {
                /*
                 * if vertex are in same lav we need to cut part of lav in the
                 * middle of vertex and create new lav from that points
                 */


                List<VertexEntry2> lavPart = cutLavPart(beginNextVertex, endPreviousVertex);

                CircularList<VertexEntry2> lav = new CircularList<Skeleton.VertexEntry2>();
                sLav.add(lav);

                lav.addLast(newVertex);

                System.out.print(newVertex.v + " ");

                for (VertexEntry2 vertex : lavPart) {
                    lav.addLast(vertex);
                    System.out.print(vertex.v + " ");
                }
                System.out.println();

                if (lav.size >= 8) {
                    DV.debug(lav);
                    throw new RuntimeException();
                }
                log.debug("after split: " + lavToString(lav.first()));
                DV.debug(sLav);
            } else {
                /*
                 * if vertex are in different lavs we need to merge them into
                 * one.
                 */
                DV.debug(sLav);
                mergeBeforeBaseVertex(beginNextVertex, endPreviousVertex);

                endPreviousVertex.addNext(newVertex);

                log.debug("after merge: " + lavToString(newVertex));
                DV.debug(sLav);
            }



            // FIXME add split event !!!


            if (chainBegin instanceof SingleEdgeChain) {
                /*
                 * When chain is generated by opposite edge we need to share
                 * face between two chains. Number of that chains shares is
                 * always odd.
                 */
                VertexEntry2 beginVertex = beginNextVertex;

                // right face
                if (lastFaceNode == null) {
                    beginVertex = createOppositeEdgeVertex(newVertex);

                    DV.debug(beginVertex.rightFace);
                    // createOppositeEdgeVertex(newVertex)
                    lastFaceNode = addFaceRight(newVertex, beginVertex);
                    DV.debug(beginVertex.rightFace);
                } else {
                    // addFaceRight(newVertex, beginVertex, lastFaceNode);
                    // connectList(beginVertex.rightFace, lastFaceNode);
                    // face queue exist simply assign it to new node
                    if (newVertex.rightFace != null) {
                        throw new RuntimeException();
                    }
                    newVertex.rightFace = lastFaceNode;
                    lastFaceNode = null;
                }

            } else {
                VertexEntry2 beginVertex = chainBegin.getCurrentVertex();
                DV.debug(beginVertex.rightFace);
                // right face
                addFaceRight(newVertex, beginVertex);
                DV.debug(beginVertex.rightFace);
            }

            if (chainEnd instanceof SingleEdgeChain) {
                VertexEntry2 endVertex = endPreviousVertex;

                // left face
                if (lastFaceNode == null) {
                    endVertex = createOppositeEdgeVertex(newVertex);

                    DV.debug(endVertex.leftFace);
                    lastFaceNode = addFaceLeft(newVertex, endVertex);
                    DV.debug(endVertex.leftFace);
                } else {
                    // do merge
                    // addFaceLeft(newVertex, endVertex, lastFaceNode);

                    // connectList(endVertex.leftFace, lastFaceNode);
                    if (newVertex.leftFace != null) {
                        throw new RuntimeException();
                    }
                    newVertex.leftFace = lastFaceNode;

                    lastFaceNode = null;
                }

            } else {
                VertexEntry2 endVertex = chainEnd.getCurrentVertex();
                DV.debug(endVertex.leftFace);
                // left face
                addFaceLeft(newVertex, endVertex);
                DV.debug(endVertex.leftFace);
            }
            DV.debug(sLav);
            // chainBegin.getNextVertex().remove();
            // chainEnd.getPreviousVertex().remove();
        }
        DV.debug(sLav);
        // remove all centers of events from lav
        edgeListSize = chains.size();
        for (int i = 0; i < edgeListSize; i++) {
            ChainEnds chainBegin = chains.get(i);
            ChainEnds chainEnd = chains.get((i + 1) % edgeListSize);

            removeFromLav(chainBegin.getCurrentVertex());
            removeFromLav(chainEnd.getCurrentVertex());

            if (chainBegin.getCurrentVertex() != null) {
                chainBegin.getCurrentVertex().processed = true;
            }
            if (chainEnd.getCurrentVertex() != null) {
                chainEnd.getCurrentVertex().processed = true;
            }
        }
        DV.debug(sLav);
    }

    private static VertexEntry2 createOppositeEdgeVertex(VertexEntry2 newVertex) {
        /*
         * When opposite edge is processed we need to create copy of vertex to
         * use in opposite face. When opposite edge chain occur vertex is shared
         * by additional output face.
         */

        VertexEntry2 vertex = new VertexEntry2();
        vertex.bisector = newVertex.bisector;
        vertex.bisector2 = newVertex.bisector2;
        vertex.v = newVertex.v;
        vertex.e_a = newVertex.e_a;
        vertex.e_b = newVertex.e_b;
        vertex.name = "oppositeEdge " + newVertex.name;

        // create new empty node queue
        FaceNode fn = new FaceNode();
        fn.v = vertex;
        vertex.leftFace = fn;
        vertex.rightFace = fn;

        // add one node for queue to present opposite site of edge split event
        FaceQueue rightFace = new FaceQueue();
        rightFace.border = false;
        rightFace.addFirst(fn);

        return vertex;
    }

    private static void createOppositeEdgeChains(Set<CircularList<VertexEntry2>> sLav, List<Chain> chains, Point2d center) {
        // add chain created from opposite edge, this chain have to be
        // calculated during processing event because lav could change during
        // processing another events on the same level
        Set<EdgeEntry> oppositeEdges = new HashSet<Skeleton.EdgeEntry>();

        for (Chain chain : chains) {
            // add opposite edges as chain parts
            if (chain instanceof SplitChain) {
                SplitChain splitChain = (SplitChain) chain;
                EdgeEntry oppositeEdge = splitChain.getOppositeEdge();
                if (oppositeEdge != null) {
                    // find lav vertex for opposite edge
                    oppositeEdges.add(oppositeEdge);
                }
            } else if (chain instanceof EdgeChain) {
                EdgeChain edgeChain = (EdgeChain) chain;
                if (ChainType.SPLIT.equals(edgeChain.getType())) {
                    EdgeEntry oppositeEdge = ((EdgeChain) chain).getOppositeEdge();
                    if (oppositeEdge != null) {
                        // find lav vertex for opposite edge
                        oppositeEdges.add(oppositeEdge);
                    }
                }
            }
        }

        for (EdgeEntry oppositeEdge : oppositeEdges) {
            // find lav vertex for opposite edge

            // FIXME what when we share edge between two lavs?
            VertexEntry2 nextVertex = findOppositeEdgeLav(sLav, oppositeEdge, center);
            chains.add(new SingleEdgeChain(oppositeEdge, nextVertex));
        }

        // List<Chain> oppositeEdgeChains = new ArrayList<Chain>();
        // chains.addAll(oppositeEdgeChains);
    }






    private static VertexEntry2 createMultiSplitVertex(EdgeEntry previousEdge, EdgeEntry nextEdge, Point2d center) {

        Point2d edgeEnd = previousEdge.p2;
        Vector2d bisectorPrediction = new Vector2d(edgeEnd.x - center.x, edgeEnd.y - center.y );

        Ray2d bisector = calcBisector(center, previousEdge, nextEdge);

        if (bisector.U.dot(bisectorPrediction) < 0) {
            // bisector is calculated in opposite direction to edges and center
            bisector.U.negate();
        }

        VertexEntry2 vertex = new VertexEntry2();
        vertex.bisector = bisector;
        vertex.bisector2 = bisector.getLinearForm();
        vertex.v = center;
        vertex.e_a = previousEdge;
        vertex.e_b  = nextEdge;
        vertex.name = "multi split: " + previousEdge + ", " + nextEdge;

        // TODO Auto-generated method stub
        return vertex;
    }

    private static List<EdgeEntry> findAllOppositeEdges(List<SkeletonEvent> cluster) {
        // TODO Auto-generated method stub
        return null;
    }






    /**
     * Create chains of events from cluster. Cluster is set of events which meet
     * in the same result point. Try to connect all event which share the same
     * vertex into chain. Events in chain are sorted. If events don't share
     * vertex, returned chains contains only one event.
     * 
     * @param cluster
     *            set of event which meet in the same result point
     * @return chains of events
     */
    private static List<Chain> createChains(List<SkeletonEvent> cluster) {
        List<EdgeEvent> edgeCluster = new ArrayList<EdgeEvent>();
        List<SplitEvent> splitCluster = new ArrayList<SplitEvent>();
        Set<VertexEntry2> vertexEventsParents = new HashSet<Skeleton.VertexEntry2>();

        for (SkeletonEvent skeletonEvent : cluster) {
            if (skeletonEvent instanceof EdgeEvent) {
                edgeCluster.add((EdgeEvent) skeletonEvent);
            } else {
                if (skeletonEvent instanceof VertexSplitEvent) {
                    // it will be processed in next loop to find unique split events for one parent
                    continue;

                } else if (skeletonEvent instanceof SplitEvent) {
                    SplitEvent splitEvent = (SplitEvent) skeletonEvent;
                    /*
                     * if vertex and split event exist for the same parent
                     * vertex and at the same level always prefer split
                     */
                    vertexEventsParents.add(splitEvent.getParent());
                    splitCluster.add(splitEvent);
                }
            }
        }

        for (SkeletonEvent skeletonEvent : cluster) {

            if (skeletonEvent instanceof VertexSplitEvent) {
                VertexSplitEvent vertexEvent = (VertexSplitEvent) skeletonEvent;
                if (!vertexEventsParents.contains(vertexEvent.getParent())) {

                    /*
                     * It can be created multiple vertex events for one parent.
                     * Its is caused because two edges share one vertex and new
                     * event will be added to both of them. When processing we
                     * need always group them into one per vertex. Always prefer
                     * split events over vertex events.
                     */

                    vertexEventsParents.add(vertexEvent.getParent());

                    splitCluster.add(vertexEvent);
                }
            }
        }

        List<EdgeChain> edgeChains = new ArrayList<EdgeChain>();

        while (edgeCluster.size() > 0) {
            /*
             * We need to find all connected edge events, and create chains from
             * them. Two event are assumed as connected if next parent of one
             * event is equal to previous parent of second event.
             */

            edgeChains.add(new EdgeChain(createEdgeChain(edgeCluster)));
        }

        List<Chain> chains = new ArrayList<Chain>(edgeChains);

        splitEventLoop: while (splitCluster.size() > 0) {
            SplitEvent split = splitCluster.remove(0);

            for (EdgeChain chain : edgeChains) {

                // check if chain is split type
                if (isInEdgeChain(split, chain)) {
                    // if we have edge chain it can't share split event

                    continue splitEventLoop;
                }
            }

            /*
             * split event is not part of any edge chain, it should be added as
             * new single element chain;
             */
            chains.add(new SplitChain(split));
        }

        /*
         * Return list of chains with type. Possible types are edge chain,
         * closed edge chain, split chain. Closed edge chain will produce pick
         * event. Always it can exist only one closed edge chain for point
         * cluster.
         */
        return chains;
    }

    private static boolean isInEdgeChain(SplitEvent split, EdgeChain chain) {

        VertexEntry2 splitParent = split.getParent();

        List<EdgeEvent> edgeList = chain.getEdgeList();
        for (EdgeEvent edgeEvent : edgeList) {
            if (edgeEvent.Va == splitParent || edgeEvent.Vb == splitParent) {
                return true;
            }
        }

        return false;
    }

    protected static ArrayList<EdgeEvent> createEdgeChain(List<EdgeEvent> edgeCluster) {

        ArrayList<EdgeEvent> edgeList = new ArrayList<Skeleton.EdgeEvent>();

        edgeList.add(edgeCluster.remove(0));

        // find all successors of edge event
        // find all predecessors of edge event

        // XXX check in future
        loop: do {

            VertexEntry2 beginVertex = edgeList.get(0).Va;
            VertexEntry2 endVertex = edgeList.get(edgeList.size() - 1).Vb;

            for (int i = 0; i < edgeCluster.size(); i++) {
                EdgeEvent edge = edgeCluster.get(i);
                if (edge.Va == endVertex) {
                    // edge should be added as last in chain
                    edgeCluster.remove(i);
                    edgeList.add(edge);
                    continue loop;
                } else if (edge.Vb == beginVertex) {
                    // edge should be added as first in chain
                    edgeCluster.remove(i);
                    edgeList.add(0, edge);
                    continue loop;
                }
            }
            break;

        } while (true);

        return edgeList;
    }

    public static interface ChainEnds {
        public EdgeEntry getPreviousEdge();

        public EdgeEntry getNextEdge();

        public VertexEntry2 getPreviousVertex();

        public VertexEntry2 getNextVertex();

        public VertexEntry2 getCurrentVertex();
    }

    private static class ChainEndsImpl implements ChainEnds {
        private EdgeEntry previousEdge;
        private EdgeEntry nextEdge;
        private VertexEntry2 previousVertex;
        private VertexEntry2 nextVertex;
        private VertexEntry2 currentVertex;

        public ChainEndsImpl(EdgeEntry previousEdge, EdgeEntry nextEdge, VertexEntry2 previousVertex, VertexEntry2 nextVertex,
                VertexEntry2 currentVertex) {
            super();
            this.previousEdge = previousEdge;
            this.nextEdge = nextEdge;
            this.previousVertex = previousVertex;
            this.nextVertex = nextVertex;
            this.currentVertex = currentVertex;
        }

        @Override
        public EdgeEntry getPreviousEdge() {
            return previousEdge;
        }

        @Override
        public EdgeEntry getNextEdge() {
            return nextEdge;
        }

        @Override
        public VertexEntry2 getPreviousVertex() {
            return previousVertex;
        }

        @Override
        public VertexEntry2 getNextVertex() {
            return nextVertex;
        }

        @Override
        public VertexEntry2 getCurrentVertex() {
            return currentVertex;
        }



    }

    private static void removeEventsUnderHeight(PriorityQueue<SkeletonEvent> queue, double levelHeight) {

        while (!queue.isEmpty()) {
            if (queue.peek().distance > levelHeight + SPLIT_EPSILON) {
                break;
            }
            queue.poll();
        }
    }

    private static List<SkeletonEvent> loadAndGroupLevelEvents(PriorityQueue<SkeletonEvent> queue) {

        List<SkeletonEvent> levelEvents = loadLevelEvents(queue);

        return groupLevelEvents(levelEvents);
    }

    protected static List<SkeletonEvent> groupLevelEvents(List<SkeletonEvent> levelEvents) {

        List<SkeletonEvent> ret = new ArrayList<Skeleton.SkeletonEvent>();

        while (levelEvents.size() > 0) {
            // for (int i = 0; i < levelEvents.size(); i++) {
            SkeletonEvent event = levelEvents.remove(0);
            Point2d eventCenter = event.v;

            List<SkeletonEvent> cluster = new ArrayList<Skeleton.SkeletonEvent>();
            cluster.add(event);
            for (int j = 0; j < levelEvents.size(); j++) {
                SkeletonEvent test = levelEvents.get(j);
                if (near(eventCenter, test.v, SPLIT_EPSILON)) {
                    // group all event when the result point are near each other
                    cluster.add(levelEvents.remove(j));
                    j--;
                }
            }


            // if (cluster.size() > 1) {
            // more then one event share the same result point, we need to
            // create new level event
            ret.add(createLevelEvent(cluster, eventCenter));
            // } else {
            // // regular single event
            // ret.add(event);
            // }
        }
        return ret;
    }

    /**
     * @param eventCluster
     *            list of events which meet in single point.
     * @param eventCenter
     * @return
     */
    private static SkeletonEvent createLevelEvent(List<SkeletonEvent> eventCluster, Point2d eventCenter) {

        List<Chain> chains = createChains(eventCluster);

        if (chains.size() == 1) {
            Chain chain = chains.get(0);
            if (ChainType.CLOSED_EDGE.equals(chain.getType())) {
                return new PickEvent(eventCenter, (EdgeChain) chain);
            } else if (ChainType.EDGE.equals(chain.getType())) {
                return new MultiEdgeEvent(eventCenter, (EdgeChain) chain);
            } else if (ChainType.SPLIT.equals(chain.getType())) {
                return new MultiSplitEvent(eventCenter, chains);
            }
        }

        for (Chain chain : chains) {
            if (ChainType.CLOSED_EDGE.equals(chain.getType())) {
                throw new RuntimeException("found closed chain of events for single point, but found more then one chain");
            }
        }

        return new MultiSplitEvent(eventCenter, chains);
    }

    static boolean  near(Point2d p1, Point2d p2, double epsilon) {
        //XXX
        return distance(p1, p2) < epsilon;
    }

    private static void debugSteep(PriorityQueue<SkeletonEvent> queue, Set<CircularList<VertexEntry2>> sLav, List<FaceQueue> faces) {
        DV.debug(queue);

        for (CircularList<VertexEntry2> l : sLav) {
            DV.debug(l);
        }

        for (FaceQueue f : faces) {
            DV.debug(f);
        }
    }

    /**
     * Loads all not obsolete event which are on one level. As level heigh is
     * taken epsilon.
     * 
     * @param queue
     * @return
     */
    private static List<SkeletonEvent> loadLevelEvents(PriorityQueue<SkeletonEvent> queue) {

        List<SkeletonEvent> level = new ArrayList<Skeleton.SkeletonEvent>();

        SkeletonEvent levelStart = null;
        do {
            levelStart = queue.poll();
            // skip all obsolete events in level
        } while (levelStart != null && levelStart.isObsolete());

        if (levelStart == null || levelStart.isObsolete()) {
            // all events obsolete
            return level;
        }

        double levelStartHeight = levelStart.distance;

        level.add(levelStart);

        SkeletonEvent event = null;
        while ((event = queue.peek()) != null && event.distance - levelStartHeight < SPLIT_EPSILON) {

            SkeletonEvent nextLevelEvent = queue.poll();
            if (!nextLevelEvent.isObsolete()) {
                level.add(nextLevelEvent);
            }
        }
        return level;
    }

    private static int assertMaxNumberOfInteraction(int count) {
        count++;
        if (count > 10000) {
            throw new RuntimeException("to many interaction: bug?");
        }
        return count;
    }

    private static class DebugPoint2d extends Point2d {
        String name;
        public DebugPoint2d(double x, double y, String name) {
            super(x, y);
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }


    }

    private static List<Point2d> addDebugNames(List<Point2d> polygon, String name) {
        List<Point2d> ret = new ArrayList<Point2d>();
        int i = 0;
        for (Point2d point : polygon) {
            ret.add(new DebugPoint2d(point.x, point.y, name + i));
            i++;
        }
        return ret;
    }

    private static List<List<Point2d>> makeClockwise(List<List<Point2d>> holes) {
        if (holes == null) {
            return null;
        }

        List<List<Point2d>> ret = new ArrayList<List<Point2d>>(holes.size());
        for (List<Point2d> hole : holes) {
            if (PolygonUtil.isClockwisePolygon(hole)) {
                ret.add(hole);
            } else {
                ret.add(PolygonUtil.reverse(hole));
            }
        }
        return ret;
    }

    private static List<Point2d> makeCounterClockwise(List<Point2d> polygon) {
        if (PolygonUtil.isClockwisePolygon(polygon)) {
            return PolygonUtil.reverse(polygon);
        }
        return polygon;
    }

    /**
     * @param pBorder
     * @param sLav
     * @param faces
     */
    private static void prepareBisectors(List<Point2d> pBorder,
            Set<CircularList<VertexEntry2>> sLav, List<FaceQueue> faces) {
        CircularList<VertexEntry2> lav = new CircularList<Skeleton.VertexEntry2>();
        sLav.add(lav);


        for (Point2d p : pBorder) {
            VertexEntry2 v2 = new VertexEntry2();
            v2.v = p;
            lav.addLast(v2);
        }


        CircularList<EdgeEntry> edgesList = new CircularList<EdgeEntry>();


        for (VertexEntry2 v_i : lav) {

            VertexEntry2 v_ip1 = v_i.next();

            EdgeEntry e_i = new EdgeEntry(v_i.v, v_ip1.v);

            edgesList.addLast(e_i);

            v_i.e_b = e_i;
        }


        for (VertexEntry2 v_i : lav) {
            v_i.e_a = (EdgeEntry) v_i.e_b.previous;
        }

        DV.debug(lav);





        /* new */
        for (VertexEntry2 v : lav) {

            v.bisector = calcBisector(v.v, v.e_a, v.e_b);
            v.bisector2 =  v.bisector.getLinearForm();
            //calcBisector2(v.v, e_im1, e_i);

            v.e_a.bisectorNext = v.bisector;
            v.e_b.bisectorPrevious = v.bisector;

            DV.debug(v.bisector);



            FaceQueue rightFace = new FaceQueue();
            rightFace.border = true;
            rightFace.edge = v.e_b;

            //            leftFace = new CircularList<Skeleton.FaceNode>();
            FaceNode fn = new FaceNode();
            fn.v = v;
            rightFace.addFirst(fn);
            v.rightFace = fn;

            faces.add(rightFace);

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

            VertexEntry2 next = (VertexEntry2) v.previous;

            FaceNode rightFace = next.rightFace;

            FaceNode fn = new FaceNode();
            fn.v = v;
            rightFace.addPush(fn);
            v.leftFace = fn;
        }
    }

    private static void addFacesToOutput(List<FaceQueue> faces, SkeletonOutput output) {

        for (FaceQueue face : faces) {
            if (face.size > 0) {
                List<Point2d> faceList = new ArrayList<Point2d>();

                for (FaceNode fn : face) {
                    faceList.add(fn.v.v);
                    output.distance.put(fn.v.v, fn.v.distance);
                }

                PolygonList2d polygon = new PolygonList2d(faceList);

                output.faces.add(polygon);
                output.edges.put(polygon, new LineSegment2d(face.edge.p1, face.edge.p2));

            }
        }
    }

    private static void addOutputFace(SkeletonOutput output, SkeletonEvent i, VertexEntry2 va, VertexEntry2 vb) {
        addOutputFace(output, i.v, va, vb);
    }

    private static void addOutputFace(SkeletonOutput output, Point2d v, VertexEntry2 va, VertexEntry2 vb) {
        return;
    }

    //    /**
    //     * @param va
    //     * @param lewa
    //     * @param leftEdge
    //     */
    //    public static void findFaceEdge(VertexEntry2 va, boolean lewa, List<Point2d> leftEdge) {
    //
    //        VertexEntry2 leftEdgeVertex = va;
    //        boolean split = false;
    //        while (true) {
    //
    //            if (leftEdgeVertex.split) {
    //                VertexEntry2 splitVertex = null;
    //                if (lewa) {
    //                    splitVertex = leftEdgeVertex.parentVb;
    //                } else {
    //                    splitVertex = leftEdgeVertex.parentVa;
    //                }
    //
    //                if (splitVertex.split) {
    //                    // split vertex
    //                    // there are two vertex for each split event!
    //                    // skip first one
    //
    //                    leftEdgeVertex = splitVertex;
    //
    //                    split = true;
    //                    lewa = !lewa;
    //                } else {
    //                    //                    normal vertex don't do anyfing
    //                }
    //            }
    //
    //            if (!split) {
    //                if (lewa && leftEdgeVertex.parentVb != null) {
    //                    leftEdgeVertex = leftEdgeVertex.parentVb;
    //                    leftEdge.add(leftEdgeVertex.v);
    //                } else if (!lewa && leftEdgeVertex.parentVa != null) {
    //                    leftEdgeVertex = leftEdgeVertex.parentVa;
    //                    leftEdge.add(leftEdgeVertex.v);
    //                } else {
    //                    break;
    //                }
    //                continue;
    //            } else {
    //
    //                if (leftEdgeVertex.shrinks != null) {
    //                    leftEdgeVertex = leftEdgeVertex.shrinks;
    //                    leftEdge.add(leftEdgeVertex.v);
    //
    //                    split = false;
    //                } else {
    //                    throw new RuntimeException("Face is not ended");
    //                }
    //                continue;
    //            }
    //        }
    //    }

    private static void addOutputFace(SkeletonOutput output, SkeletonEvent i, VertexEntry2 va) {
        // System.out.println("adding edge on split event!: " + i.v);
    }

    private static void computeInitEvents(Set<CircularList<VertexEntry2>> sLav, PriorityQueue<SkeletonEvent> queue,
            List<EdgeEntry> edges) {

        Map<VertexEntry2, List<SplitCandidate>> splitEventsSet = new HashMap<VertexEntry2, List<SplitCandidate>>();

        for (CircularList<VertexEntry2> lav : sLav) {

            for (VertexEntry2 vertex : lav) {
                List<SplitCandidate> calcOppositeEdges = calcOppositeEdges(vertex, edges);

                splitEventsSet.put(vertex, calcOppositeEdges);
                // check if it is vertex split event

                for (SplitCandidate oppositeEdge : calcOppositeEdges) {

                    // check if it is vertex split event
                    Point2d point = oppositeEdge.getPoint();
                    if (oppositeEdge.getOppositePoint() != null) {
                        // some of vertex event can share the same opposite
                        // point
                        queue.add(new VertexSplitEvent(point, oppositeEdge.getDistance(), vertex));
                        continue;
                    }

                    queue.add(new SplitEvent(point, oppositeEdge.getDistance(), vertex, oppositeEdge.getOppositeEdge()));
                    continue;
                }
            }
        }

        for (CircularList<VertexEntry2> lav : sLav) {

            for (VertexEntry2 vertex : lav) {

                VertexEntry2 nextVertex = vertex.next();
                Point2d point = computeIntersectionBisectors(vertex, nextVertex);
                if (point != null) {
                    EdgeEvent I = new EdgeEvent();
                    I.v = point;
                    I.distance = calcDistance(point, vertex.e_b);
                    I.Va = vertex;
                    I.Vb = nextVertex;

                    queue.add(I);
                }
            }
        }
    }

    private static void computeIntersections(VertexEntry2 v_i, PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges
            ) {

        VertexEntry2 v_ip1 = v_i.next();
        VertexEntry2 v_im1 = v_i.previous();

        Point2d intersectionBisectors1 = computeIntersectionBisectors(v_i, v_ip1);

        Point2d intersectionBisectors2 = computeIntersectionBisectors(v_i, v_im1);

        DV.debug(v_i.v);

        Opposite opposite = calcOppositePointB(v_i, edges);

        Point2d B = opposite.B;

        if (B != null) {
            DV.debug(new LineSegment2d(opposite.edge.p1, opposite.edge.p2));
        }

        int nirest = chooseNearest(v_i.v, intersectionBisectors1, intersectionBisectors2, B);

        switch (nirest) {
        case 1: {
            EdgeEvent I = new EdgeEvent();

            I.v = intersectionBisectors1;

            I.distance = calcDistance(intersectionBisectors1, v_i.e_b);
            I.Va = v_i;
            I.Vb = v_ip1;

            queue.add(I);
            if (I.v.epsilonEquals(new Point2d(-4.500000, 1.500000), 0.1)) {
                System.out.println("test");
            }
            break;
        }
        case 2: {
            EdgeEvent I = new EdgeEvent();

            I.v = intersectionBisectors2;

            I.distance = calcDistance(intersectionBisectors2, v_i.e_a);
            I.Va = v_im1;
            I.Vb = v_i;

            queue.add(I);
            if (I.v.epsilonEquals(new Point2d(-4.500000, 1.500000), 0.1)) {
                System.out.println("test");
            }
            break;
        }
        case 3: {

            // check if it is vertex split event
            EdgeEntry previousOpositeEdge = vertexOpositeEdge(B, opposite.edge);
            if (previousOpositeEdge != null) {

                double distance = calcDistance(B, previousOpositeEdge) + SPLIT_EPSILON;

                VertexSplitEvent I = new VertexSplitEvent(B, distance, v_i, opposite.edge);

                queue.add(I);

                break;

            }

            double distance = calcDistance(B, opposite.edge) + SPLIT_EPSILON;

            SplitEvent I = new SplitEvent(B, distance, v_i, opposite.edge);

            queue.add(I);

            break;
        }
        }
    }

    /**
     * Check if given point is on one of edge bisectors. If so this is vertex
     * split event. This event need two opposite edges to process but second
     * (next) edge can be take from edges list and it is next edge on list.
     * 
     * @param point
     *            point of event
     * @param edge
     *            candidate for opposite edge
     * @return previous opposite edge if it is vertex split event
     */
    protected static EdgeEntry vertexOpositeEdge(Point2d point, EdgeEntry edge) {

        if (RayUtil.isPointOnRay(point, edge.bisectorNext, SPLIT_EPSILON)) {
            return edge;
        }

        if (RayUtil.isPointOnRay(point, edge.bisectorPrevious, SPLIT_EPSILON) ) {
            return edge.previous();
        }
        return null;
    }

    /**
     * @param v vertex
     * @param p1 intersection point 1
     * @param p2 intersection point 2
     * @param p3 opposite point 3
     * @return nearest point
     */
    private static int chooseNearest(Point2d v, Point2d p1, Point2d p2, Point2d p3) {

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
            // for split event add epsilon to prefer edge events
            distance3 = v.distanceSquared(p3) + SPLIT_EPSILON;
        }

        if (distance1 <= distance2 && distance1 <= distance3) {
            return 1;
        } else if (distance2 <= distance1 && distance2 <= distance3) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Point and edge for split events.
     * 
     * @author Tomasz Kędziora (Kendzi)
     */
    public static class OppositeEdge {

        private double distance;
        private Point2d point;
        private EdgeEntry edge;
        private OppositeEdge(Point2d point, EdgeEntry edge, double distance) {
            super();
            this.point = point;
            this.edge = edge;
            this.distance = distance;
        }

        public double getDistance() {
            return distance;
        }

        public Point2d getPoint() {
            return point;
        }

        public EdgeEntry getEdge() {
            return edge;
        }

    }

    /**
     * Point and edge for split events.
     * 
     * @author Tomasz Kędziora (Kendzi)
     */
    public static class Opposite {

        private Opposite(Point2d b, EdgeEntry edge) {
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

    private static List<SplitCandidate> calcOppositeEdges(VertexEntry2 vertex, List<EdgeEntry> edges) {

        List<SplitCandidate> ret = new ArrayList<SplitCandidate>();

        for (EdgeEntry edgeEntry : edges) {

            LineLinear2d edge = edgeEntry.getLineLinear();

            // check if edge is behind bisector
            if (edgeBehindBisector(vertex.bisector, edge)) {
                continue;
            }

            // compute the coordinates of the candidate point Bi
            SplitCandidate candidatePoint = calcCandidatePointForSplit(vertex, edgeEntry);

            if (candidatePoint != null) {
                ret.add(candidatePoint);
            }
        }

        Collections.sort(ret, new Comparator<SplitCandidate>() {

            @Override
            public int compare(SplitCandidate o1, SplitCandidate o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });

        return ret;
    }

    private static Opposite calcOppositePointB(VertexEntry2 vertex, List<EdgeEntry> edges) {

        Point2d candidateB = null;
        EdgeEntry candidateEdge = null;
        double candidateDistance = Double.MAX_VALUE;

        EdgeEntry e_i = vertex.e_b.next(); //next

        for (EdgeEntry edgeEntry : edges) {
            e_i = edgeEntry;
            DV.debug(vertex.v);
            DV.debug(new LineSegment2d(e_i.p1, e_i.p2));

            LineLinear2d edge = e_i.getLineLinear();

            // check if edge is behind bisector
            if (edgeBehindBisector(vertex.bisector, edge)) {
                e_i = e_i.next();
                continue;
            }

            // FIXME
            // compute the coordinates of the candidate point Bi
            Point2d candidatePoint = null;
            // FIXME
            SplitCandidate calcCandidatePointForSplit = calcCandidatePointForSplit(vertex, e_i);

            if (calcCandidatePointForSplit != null) {
                // FIXME
                candidatePoint = calcCandidatePointForSplit.getPoint();
            }
            if (candidatePoint == null) { // FIXME
                System.out.println("Ups test me!!");
            }

            if (candidatePoint != null) {
                double distance = vertex.v.distanceSquared(candidatePoint);
                if (distance < candidateDistance) {
                    candidateB = candidatePoint;
                    candidateDistance = distance;
                    candidateEdge = e_i;
                }
            }



            e_i = e_i.next();

        }

        return new Opposite(candidateB, candidateEdge);
    }

    protected static boolean edgeBehindBisector(Ray2d bisector, LineLinear2d edge) {
        // Simple intersection test between the bisector starting at V and the
        // whole line containing the currently tested line segment ei
        // rejects the line segments laying "behind" the vertex V

        return Ray2d.collide(bisector, edge, SPLIT_EPSILON) == null;
    }

    private static class SplitCandidate {

        private double distance;
        private Point2d point;
        private EdgeEntry oppositeEdge;
        private Point2d oppositePoint;

        public SplitCandidate(Point2d point, double distance, EdgeEntry oppositeEdge, Point2d oppositePoint) {
            super();
            this.point = point;
            this.distance = distance;
            this.oppositeEdge = oppositeEdge;
            this.oppositePoint = oppositePoint;
        }

        public double getDistance() {
            return distance;
        }

        public Point2d getPoint() {
            return point;
        }

        public EdgeEntry getOppositeEdge() {
            return oppositeEdge;
        }

        public Point2d getOppositePoint() {
            return oppositePoint;
        }
    }

    protected static SplitCandidate calcCandidatePointForSplit(VertexEntry2 vertex, EdgeEntry edge) {

        EdgeEntry vertexEdge = choseLessParallelVertexEdge(vertex, edge);
        if (vertexEdge == null) {
            return null;
        }

        Vector2d vertexEdteNormNegate = vertexEdge.norm;

        Vector2d edgesBisector = calcVectorBisector(vertexEdteNormNegate, edge.norm);

        Point2d edgesCollide = vertexEdge.getLineLinear().collide(edge.getLineLinear());

        if (edgesCollide == null) {
            // check should be performed to exclude the case when one of the
            // line segments starting at V is
            // parallel to ei.

            //            return null;
            throw new RuntimeException("ups this should not happen");
        }

        LineLinear2d edgesBisectorLine = new LineParametric2d(edgesCollide, edgesBisector).getLinearForm();

        // compute the coordinates of the candidate point Bi as the intersection
        // between the bisector at V and the axis of the angle between one of
        // the edges starting at V and the tested line segment ei

        Point2d candidatePoint = Ray2d.collide(vertex.bisector, edgesBisectorLine, SPLIT_EPSILON);

        if (candidatePoint == null) {
            return null;
        }

        if (edge.bisectorPrevious.isOnRightSite(candidatePoint, SPLIT_EPSILON)
                && edge.bisectorNext.isOnLeftSite(candidatePoint, SPLIT_EPSILON)) {

            double distance = calcDistance(candidatePoint, edge);

            if (edge.bisectorPrevious.isOnLeftSite(candidatePoint, SPLIT_EPSILON)) {

                Point2d oppositePoint = edge.p1;
                return new SplitCandidate(candidatePoint, distance, null, oppositePoint);
            } else if (edge.bisectorNext.isOnRightSite(candidatePoint, SPLIT_EPSILON)) {

                Point2d oppositePoint = edge.p1;
                return new SplitCandidate(candidatePoint, distance, null, oppositePoint);
            }

            return new SplitCandidate(candidatePoint, distance, edge, null);
        }

        return null;
    }

    private static EdgeEntry choseLessParallelVertexEdge(VertexEntry2 vertex, EdgeEntry edge) {
        EdgeEntry edgeA = vertex.e_a;
        EdgeEntry edgeB = vertex.e_b;

        EdgeEntry vertexEdge = edgeA;

        double edgeADot = Math.abs(edge.norm.dot(edgeA.norm));
        double edgeBDot = Math.abs(edge.norm.dot(edgeB.norm));

        if (edgeADot + edgeBDot >= 2 - SPLIT_EPSILON) {
            // boath lines are parnel to given edge
            return null;
        }

        if (edgeADot > edgeBDot) {
            // simple check should be performed to exclude the case when one of
            // the line segments starting at V (vertex) is parallel to e_i
            // (edge)
            // we always chose edge which is less parallel

            vertexEdge = edgeB;
        }
        return vertexEdge;
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

        Ray2d bb_i = v_i.bisector;
        Ray2d bb_ip1 = v_ip1.bisector;

        IntersectPoints intersectRays2d = RayUtil.intersectRays2d(bb_i, bb_ip1);
        Point2d intersect = intersectRays2d.getIntersect();
        //        if (intersectRays2d.getIntersectEnd() !=null) {
        //
        //            Point2d v = v_i.v;
        //            // when two rays overlaps chose point which is farther from vertex
        //
        //            if (v.distanceSquared(intersect) < v.distanceSquared(intersectRays2d.getIntersectEnd())) {
        //                intersect = intersectRays2d.getIntersectEnd();
        //            }
        //
        //
        //        }

        if (v_i.v.equals(intersect) || v_ip1.v.equals(intersect)) {
            // skip the same points
            return null;
        }

        if (intersect != null) {
            return intersect;
        }
        return null;
    }

    /**
     * @param I
     * @param output
     * @param sLav
     * @param queue
     * @param edges
     */
    private static void splitEvent(SplitEvent I, SkeletonOutput output, Set<CircularList<VertexEntry2>> sLav, PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {
        // b
        VertexEntry2 va = I.getParent();

        if (va.processed) {
            return;
        }

        //d
        //        System.out.println("skeleton V_aI and V_bI             Va: " + va.v + " I: " + I.v);
        addOutputFace(output, I, va);

        //e
        va.processed = true;



        VertexEntry2 v1 = new VertexEntry2();
        v1.v = I.v;
        v1.distance = I.distance;

        v1.e_a = va.e_a;
        v1.e_b = va.e_b;
        //        v1.split = true;

        VertexEntry2 v2 = new VertexEntry2();
        v2.v = I.v;
        v2.distance = I.distance;

        v2.e_a = va.e_a;
        v2.e_b = va.e_b;
        //        v2.split = true;

        v2.parentVa = va; //FIXME don't create cyclic parents!
        v2.parentVb = va; //FIXME don't create cyclic parents!

        v1.parentVa = va; //FIXME don't create cyclic parents!
        v1.parentVb = va; //FIXME don't create cyclic parents!

        {
            // faces

            // left face
            FaceNode fn = new FaceNode();
            fn.v = v1;
            fn.name="v1 - left";

            va.leftFace.addPush(fn);
            v1.leftFace = fn;
            v1.name = "v1";

            // right face
            fn = new FaceNode();
            fn.v = v2;
            fn.name="v2 - right";

            va.rightFace.addPush(fn);
            v2.rightFace = fn;
            v2.name = "v2";


            // back face
            fn = new FaceNode();
            fn.v = v2;
            fn.name="v top";

            v1.rightFace = fn;
            v2.leftFace = fn;

            FaceQueue shrinksList =  new FaceQueue();
            shrinksList.addFirst(fn);
        }

        v1.parentVa = I.parent;
        v1.parentVb = I.parent;
        v2.parentVa = I.parent;
        v2.parentVb = I.parent;

        breakLav(I, va, v2, v1, sLav, queue, edges);

    }



    private static void breakLav(SplitEvent I, VertexEntry2 va, VertexEntry2 v1, VertexEntry2 v2, Set<CircularList<VertexEntry2>> sLav, PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {
        // search the opposite edge in SLAV?
        EdgeEntry oppositeEdge = I.oppositeEdge;


        VertexEntry2 v = I.getParent();
        Ray2d bisector = I.getParent().bisector;


        CircularList<VertexEntry2> lav = v.list();

        sLav.remove(lav);

        int splitIndex = findSplitIndex(v, oppositeEdge);

        if (splitIndex >=0) {

            DV.debug(lav);
            DV.debug(v.v);
            DV.debug(oppositeEdge);

            SplitSlavs splitLav = splitLav(v, splitIndex);
            if (debug && splitLav.getNewLawLeft().size() < 2 ||
                    splitLav.getNewLawRight().size() < 2) {
                throw new RuntimeException();

            }

            splitLav.getNewLawLeft().addLast(v1);
            splitLav.getNewLawRight().addLast(v2);

            sLav.add(splitLav.getNewLawLeft());
            sLav.add(splitLav.getNewLawRight());

        } else {
            //FIXME is it correct opposite vertex?
            VertexEntry2 oppVertex = findOppositeEdgeLav(sLav, oppositeEdge, v.v);
            DV.debug(v1.v);DV.debug(v2.v);DV.debug(v.v);
            sLav.remove(v.list());
            sLav.remove(oppVertex.list());
            v.addPrevious(v1);
            v.addNext(v2);

            CircularList<VertexEntry2> mergeLav = mergeLav(v, oppVertex);
            DV.debug(mergeLav);


            v.remove();
            DV.debug(mergeLav);
            sLav.add(mergeLav);
        }


        //        int sizeLav = lav.size();
        //
        //        CircularList<VertexEntry2> newLawA = new CircularList<VertexEntry2>();
        //        CircularList<VertexEntry2> newLawB = new CircularList<VertexEntry2>();
        //
        //
        //        EdgeEntry oppositeEdgeBegin = oppositeEdge;
        //        EdgeEntry oppositeEdgeEnd = oppositeEdge;
        //
        //        boolean isoppositeEdgeBegin = RayUtil.isPointOnRay(I.v, I.oppositeEdge.bisectorPrevious, SPLIT_EPSILON);
        //        boolean isoppositeEdgeEnd = RayUtil.isPointOnRay(I.v, I.oppositeEdge.bisectorNext, SPLIT_EPSILON);
        //        //        boolean isoppositeEdgeBegin = RayUtil.isPointOnRay(I.oppositeEdge.p1, bisector, SPLIT_EPSILON);
        //        //        boolean isoppositeEdgeEnd = RayUtil.isPointOnRay(I.oppositeEdge.p2, bisector, SPLIT_EPSILON);
        //
        //        DV.debug(bisector);
        //        DV.debug(I.oppositeEdge.p1);
        //        DV.debug(I.oppositeEdge.p2);
        //
        //        //        xxxxxx
        //        //        boolean isoppositeEdgeBegin = va.v.epsilonEquals(I.oppositeEdge.p1, SPLIT_EPSILON);
        //        //        boolean isoppositeEdgeEnd = va.v.epsilonEquals(I.oppositeEdge.p2, SPLIT_EPSILON);
        //        if (isoppositeEdgeBegin) {
        //            // special case when the neerest point is on edge vertex, in that case we need handle it when generating new split vertex
        //            oppositeEdgeBegin = (EdgeEntry) oppositeEdge.previous();
        //            oppositeEdgeEnd = oppositeEdge;
        //
        //
        //
        //        } else if (isoppositeEdgeEnd) {
        //            oppositeEdgeBegin = oppositeEdge;
        //            oppositeEdgeEnd = (EdgeEntry) oppositeEdge.next();
        //
        //        }
        //        DV.debug(oppositeEdgeBegin);
        //        DV.debug(oppositeEdgeEnd);
        //
        //        v1.e_a = oppositeEdgeBegin;
        //        v2.e_b = oppositeEdgeEnd;
        //
        //        newLawA.addLast(v1);
        //        newLawB.addLast(v2);
        //
        //        boolean isLavA = true;
        //        VertexEntry2 vert = (VertexEntry2) v.next;
        //        for (int i = 0; i < sizeLav - 1; i++) {
        //            VertexEntry2 vertexMoving = vert;
        //            vert = (VertexEntry2) vert.next;
        //
        //            vertexMoving.remove();
        //
        //            if (vertexMoving.e_a.equals(oppositeEdge)) {
        //                isLavA = false;
        //            }
        //            if (isLavA) {
        //                v1.addPrevious(vertexMoving);
        //            } else {
        //                v2.addPrevious(vertexMoving);
        //            }
        //
        //        }
        //
        //        sLav.remove(lav);
        //
        //        boolean isEmptyLeftA = newLawA.size() == 1;
        //        boolean isEmptyRightB = newLawB.size() == 1;
        //
        //
        //
        //        if (!isEmptyLeftA && isEmptyRightB) {
        //            v2.remove();
        //            v1.addPrevious(v2);
        //        }
        //
        //        if (!isEmptyRightB && isEmptyLeftA) {
        //            v1.remove();
        //            v2.addPrevious(v1);
        //        }

        Vector2d bisectorOrtagonal = Vector2dUtil.ortagonalRight(bisector.U);


        Ray2d bisectorLeft = calcBisector(v1.v, v1.e_a, v1.e_b);
        if (bisectorOrtagonal.dot(bisectorLeft.U) < 0) {
            bisectorLeft.U.negate();
        }


        v1.bisector =bisectorLeft;
        v1.bisector2 =  v1.bisector.getLinearForm();
        DV.debug(v1.bisector);
        DV.debug(v1.e_a);
        DV.debug(v1.e_b);


        Ray2d bisectorRight = calcBisector(v2.v, v2.e_a, v2.e_b);
        if (bisectorOrtagonal.dot(bisectorRight.U) > 0) {
            bisectorRight.U.negate();
        }

        v2.bisector = bisectorRight;
        v2.bisector2 =  v2.bisector.getLinearForm();
        DV.debug(v2.bisector);
        DV.debug(v2.e_a);
        DV.debug(v2.e_b);

        //        if (!isEmptyLeftA ) {
        //            sLav.add(newLawA);
        //            DV.debug(newLawA);

        computeIntersections(v1, queue, edges);

        //        }
        //        if (!isEmptyRightB) {
        //            sLav.add(newLawB);
        //
        //            DV.debug(newLawB);

        computeIntersections(v2, queue, edges);

        //        }

    }

    protected static CircularList<VertexEntry2> mergeLav(VertexEntry2 firstLav, VertexEntry2 secondLav) {

        VertexEntry2 firstNext = firstLav.next();
        VertexEntry2 secondNext = secondLav.next();

        DV.debug(firstNext.list());
        DV.debug(secondNext.list());

        CircularList<VertexEntry2> newLaw = new CircularList<VertexEntry2>();

        moveAllVertexToLavEnd(secondNext, newLaw);
        moveAllVertexToLavEnd(firstNext, newLaw);

        DV.debug(newLaw);
        return newLaw;
    }


    protected static VertexEntry2 findOppositeEdgeLav(Set<CircularList<VertexEntry2>> sLav, EdgeEntry oppositeEdge, Point2d center) {

        List<VertexEntry2> edgeLavs = findEdgeLavs(sLav, oppositeEdge, null);

        return choseOppositeEdgeLav(edgeLavs, oppositeEdge, center);
    }

    protected static VertexEntry2 choseOppositeEdgeLav(List<VertexEntry2> edgeLavs, EdgeEntry oppositeEdge, Point2d center) {
        if (edgeLavs.size() == 0) {
            return null;
        } else if (edgeLavs.size() == 1) {
            return edgeLavs.get(0);
        }

        Point2d edgeStart = oppositeEdge.p1;
        Vector2d edgeNorm = oppositeEdge.norm;
        Vector2d centerVector = new Vector2d(center);
        centerVector.sub(edgeStart);
        double centerDot = edgeNorm.dot(centerVector);

        for (VertexEntry2 end : edgeLavs) {
            VertexEntry2 begin = end.previous();

            Vector2d beginVector = new Vector2d(begin.v);
            Vector2d endVector = new Vector2d(end.v);

            beginVector.sub(edgeStart);
            endVector.sub(edgeStart);

            double beginDot = edgeNorm.dot(beginVector);
            double endDot = edgeNorm.dot(endVector);

            if (beginDot < centerDot && centerDot < endDot
                    || beginDot > centerDot && centerDot > endDot) {
                return begin;
            }

        }
        return null;
    }

    private static List<VertexEntry2> findEdgeLavs(Set<CircularList<VertexEntry2>> sLav, EdgeEntry oppositeEdge,
            CircularList<VertexEntry2> skippedLav) {

        List<VertexEntry2> edgeLavs = new ArrayList<Skeleton.VertexEntry2>();

        for (CircularList<VertexEntry2> lav : sLav) {
            if (lav.equals(skippedLav)) {
                continue;
            }

            VertexEntry2 vertexInLav = getEdgeInLav(lav, oppositeEdge);
            if (vertexInLav != null) {
                edgeLavs.add(vertexInLav);
            }
        }
        return edgeLavs;
    }





    /** Try to find index of last vertex after opposite edge is found.
     * Index is calculated relatively from given starting vertex.
     * @param vertex
     * @param oppositeEdge
     * @return
     */
    protected static int findSplitIndex(VertexEntry2 vertex, EdgeEntry oppositeEdge) {

        int sizeLav = vertex.list().size();

        VertexEntry2 nextVertex = vertex;

        for (int i = 0; i < sizeLav; i++) {

            VertexEntry2 currentVertex = nextVertex;

            if (oppositeEdge.equals(currentVertex.e_a) || oppositeEdge.equals(currentVertex.previous().e_b)) {
                return i;
            }

            nextVertex = nextVertex.next();
        }
        return -1;
    }

    private static boolean isEdgeInLav(CircularList<VertexEntry2> lav, EdgeEntry oppositeEdge) {
        return getEdgeInLav(lav, oppositeEdge) != null;
    }

    /** Take next lav vertex _AFTER_ given edge, Find vertex is always on RIGHT site of edge
     * @param lav
     * @param oppositeEdge
     * @return
     */
    private static VertexEntry2 getEdgeInLav(CircularList<VertexEntry2> lav, EdgeEntry oppositeEdge) {
        // FIXME jUNIT
        for (VertexEntry2 node : lav) {
            // XXX is it correct? not previous.e_b
            if (oppositeEdge.equals(node.e_a) || oppositeEdge.equals(node.previous().e_b)) {
                return node;
            }
        }
        return null;
    }

    /**
     * @param I
     * @param output
     * @param queue
     * @param edges
     */
    public static void edgeEvent(EdgeEvent I, SkeletonOutput output, Set<CircularList<VertexEntry2>> sLav, PriorityQueue<SkeletonEvent> queue, List<EdgeEntry> edges) {


        // b
        VertexEntry2 va = I.Va;
        VertexEntry2 vb = I.Vb;

        if (va.processed || vb.processed) {
            return;
        }
        if (!va.list().equals(vb.list())) {
            //XXX
            return;
        }

        boolean processed = false;
        //c
        if (va.v.equals(I.v) || vb.v.equals(I.v)) {
            // special case
            // the colision point is in queue allredy
            // so we don't calc it, only draw sceleton edges.
            addOutputFace(output, I, va, vb);
            processed = true;
            throw new RuntimeException("TODO ? myby delede if ?");
            // continue;

        } else if (vb.equals(va.previous())) {
            System.out.println("test me");
            VertexEntry2 center = new VertexEntry2();
            center.v = I.v;
            center.distance = I.distance;

            addOutputFace(output, I, va, vb);

            addFaceBack(center, va, vb );

            va.processed = true;
            vb.processed = true;

            va.remove();
            vb.remove();

            return;

        } else if (vb.equals(va.previous().previous())) {

            VertexEntry2 vc = va.previous();

            VertexEntry2 center = new VertexEntry2();
            center.v = I.v;
            center.distance = I.distance;

            addOutputFace(output, I, va, vb);
            addOutputFace(output, I, vc, va);
            addOutputFace(output, I, vb, vc);

            addFaceBack(center, va, vb );
            addFaceBack(center, vc, va );
            addFaceBack(center, vb, vc );


            va.processed = true;
            vb.processed = true;
            vc.processed = true;

            va.remove();
            vb.remove();
            vc.remove();

            return;
        } else {

            //d
            addOutputFace(output, I, va, vb);
        }
        //e
        va.processed = true;
        vb.processed = true;

        VertexEntry2 newVertex = new VertexEntry2();
        newVertex.v = I.v;
        newVertex.distance = I.distance;
        newVertex.parentVa = I.Va;
        newVertex.parentVb = I.Vb;

        //        I.Va.shrinks = newVertex;
        //        I.Vb.shrinks = newVertex;

        va.addNext(newVertex);

        newVertex.e_a = va.e_a;
        newVertex.e_b = vb.e_b;



        addFaces(newVertex,I.Va,I.Vb );

        va.remove(); vb.remove();
        //f

        if (processed) {
            // ???
            //TESTME
            //            System.out.println(" point was processed before skiping: " + newVertex.v);
            return;
        }
        newVertex.bisector = calcBisector(newVertex.v, newVertex.e_a, newVertex.e_b);
        newVertex.bisector2 = newVertex.bisector.getLinearForm();

        DV.debug(newVertex.bisector);

        VertexEntry2 v_i = newVertex;

        computeIntersections(v_i, queue, edges);
    }

    private static void addFaceBack(VertexEntry2 newVertex, VertexEntry2 va, VertexEntry2 vb) {
        // back face
        FaceNode fn = new FaceNode();
        fn.v = newVertex;
        va.rightFace.addPush(fn);

        connectList(fn, vb.leftFace);
        addFace(fn);
    }

    private static void addFaces(VertexEntry2 newVertex, VertexEntry2 va, VertexEntry2 vb) {
        // faces

        addFaceBack(newVertex, va, vb);

        // left face
        addFaceLeft(newVertex, va);

        // right face
        addFaceRight(newVertex, vb);

    }

    private static FaceNode addFaceRight(VertexEntry2 newVertex, VertexEntry2 vb) {
        FaceNode fn = new FaceNode();
        fn.v = newVertex;
        vb.rightFace.addPush(fn);
        newVertex.rightFace = fn;

        return fn;
    }

    private static void addFaceRight(VertexEntry2 newVertex, VertexEntry2 vb, FaceNode fn) {
        vb.rightFace.addPush(fn);
        newVertex.rightFace = fn;
    }

    private static FaceNode addFaceLeft(VertexEntry2 newVertex, VertexEntry2 va) {
        FaceNode fn = new FaceNode();
        fn.v = newVertex;
        va.leftFace.addPush(fn);
        newVertex.leftFace = fn;

        return fn;
    }

    private static void addFaceLeft(VertexEntry2 newVertex, VertexEntry2 va, FaceNode fn) {
        va.leftFace.addPush(fn);
        newVertex.leftFace = fn;
    }

    private static void addFace(FaceNode fn) {
        DV.debug(fn);
    }

    private static void connectList(FaceNode rightFace, FaceNode leftFace) {

        DV.debug((FaceQueue) rightFace.list());

        if (((FaceQueue) leftFace.list()).border) {
            leftFace.addQueue(rightFace);
        }
        rightFace.addQueue(leftFace);
    }

    private static VertexEntry2 newEdgeVertex(EdgeEvent I, VertexEntry2 va, VertexEntry2 vb) {
        VertexEntry2 newVertex = new VertexEntry2();
        newVertex.v = I.v;
        newVertex.distance = I.distance;

        newVertex.parentVa = I.Va;
        newVertex.parentVb = I.Vb;

        //        I.Va.shrinks = newVertex;
        //        I.Vb.shrinks = newVertex;

        va.addNext(newVertex);
        // lav.addBefore(newVertex, va);

        newVertex.e_a = va.e_a;
        newVertex.e_b = vb.e_b;

        return newVertex;
    }



    /**
     *
     */
    public static Comparator<SkeletonEvent> distanseComparator = new Comparator<SkeletonEvent>() {
        @Override
        public int compare( SkeletonEvent pV1, SkeletonEvent pV2 ) {
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
     * @param p0
     *
     * @param p1
     *            the other point
     * @return
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

        public FaceNode leftFace;
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
        public EdgeEntry e_a;

        /**
         * Next edge.
         */
        public EdgeEntry e_b;

        VertexEntry2 parentVa;
        VertexEntry2 parentVb;

        public boolean processed;

        //        VertexEntry2 shrinks;

        //        boolean split;



        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "VertexEntry [v=" + this.v + ", processed=" + this.processed + ", bisector=" + this.bisector + ", e_a=" + this.e_a
                    + ", e_b=" + this.e_b
                    + ", parentVa=" + (this.parentVa != null ? this.parentVa.v : "null")
                    + ", parentVb=" + (this.parentVb != null ? this.parentVb.v : "null")
                    //                    + ", shrinks=" + (this.shrinks != null ? this.shrinks.v : "null")
                    + ", bisector2=" + this.bisector2 + "]";
        }



        @Override
        public VertexEntry2 next() {
            return (VertexEntry2) super.next();
        }

        @Override
        public VertexEntry2 previous() {
            return (VertexEntry2) super.previous();
        }
    }

    /**
     * @author kendzi
     *
     */
    public static abstract class SkeletonEvent {

        public Point2d v;

        double distance;

        public abstract boolean isObsolete();

        public Point2d getPoint() {
            return v;
        }

        public double getDistance() {
            return distance;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "IntersectEntry [v=" + this.v
                    + ", distance=" + this.distance + "]";
        }
    }

    public static class FaceQueue extends PathQueue<FaceNode> {
        boolean border;
        EdgeEntry edge;

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

        /**
         * @return the edge
         */
        public EdgeEntry getEdge() {
            return edge;
        }

        /**
         * @param edge the edge to set
         */
        public void setEdge(EdgeEntry edge) {
            this.edge = edge;
        }
    }

    public static class FaceNode extends PathQueueNode {
        public VertexEntry2 v;
        boolean border;
        String name;
    }

    public static class EdgeEvent extends SkeletonEvent{

        public Ray2d bisector;

        public VertexEntry2 Va;
        public VertexEntry2 Vb;

        public VertexEntry2 getLeftVertex() {
            return Va;
        }

        public VertexEntry2 getRightVertex() {
            return Vb;
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

        @Override
        public boolean isObsolete() {
            return Va.processed || Vb.processed;
        }
    }

    /**
     * @author kendzi
     *
     */
    public static class SplitEvent extends SkeletonEvent {

        public SplitEvent(Point2d point, double distance, VertexEntry2 parent, EdgeEntry oppositeEdge) {
            super();

            v = point;
            this.distance = distance;

            this.parent = parent;
            this.oppositeEdge = oppositeEdge;
        }

        public EdgeEntry oppositeEdge;

        private VertexEntry2 parent;


        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "SplitEvent [v=" + this.v
                    + ", parent=" + (this.parent != null ? this.parent.v : "null")
                    + ", distance=" + this.distance + "]";
        }

        @Override
        public boolean isObsolete() {
            return parent.processed;
        }

        public EdgeEntry getOppositeEdge() {
            return oppositeEdge;
        }

        public VertexEntry2 getParent() {
            return parent;
        }

        @Override
        public Point2d getPoint() {
            return v;
        }
    }

    /**
     * @author kendzi
     *
     */
    public  static class VertexSplitEvent extends SplitEvent {

        public VertexSplitEvent(Point2d point, double distance, VertexEntry2 parent) {
            super(point, distance, parent, null);
        }

        @Deprecated
        public VertexSplitEvent(Point2d point, double distance, VertexEntry2 parent, EdgeEntry oppositeEdgePrevious) {
            super(point, distance, parent, oppositeEdgePrevious);
        }

        public EdgeEntry getOppositeEdgePrevious() {
            return oppositeEdge;
        }

        public EdgeEntry getOppositeEdgeNext() {
            return oppositeEdge.next();
        }


        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "VertexSplitEvent [v=" + this.v
                    + ", parent=" + (this.getParent() != null ? this.getParent().v : "null")
                    + ", distance=" + this.distance + "]";
        }


        @Override
        public EdgeEntry getOppositeEdge() {
            throw new RuntimeException("XXX");
        }

    }

    /**
     * @author kendzi
     *
     */
    public static class EdgeEntry extends CircularNode{
        public Point2d p1;
        public Point2d p2;

        Ray2d bisectorPrevious;
        Ray2d bisectorNext;

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

        @Override
        public EdgeEntry next() {
            return (EdgeEntry) super.next();
        }

        @Override
        public EdgeEntry previous() {
            return (EdgeEntry) super.previous();
        }

        public LineParametric2d getLineParametric2d() {
            return new LineParametric2d(p1, norm);
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

        skeleton(polygon);
    }

    /**
     * Output of Skeleton algorithm.
     *
     * @author Tomasz Kędziora (Kendzi)
     */
    public static class SkeletonOutput {

        /**
         * Edges of polygon.
         */
        private Map<PolygonList2d, LineSegment2d> edges = new HashMap<PolygonList2d, LineSegment2d>();

        /**
         * Faces generated by Skeleton algorithm.
         */
        private List<PolygonList2d> faces = new ArrayList<PolygonList2d>();

        /**
         * Distance points from edges.
         */
        private Map<Point2d, Double> distance = new HashMap<Point2d, Double>();

        /**
         * @return the edges
         */
        public Map<PolygonList2d, LineSegment2d> getEdges() {
            return edges;
        }

        /**
         * @param edges the edges to set
         */
        public void setEdges(Map<PolygonList2d, LineSegment2d> edges) {
            this.edges = edges;
        }

        /**
         * @return the faces
         */
        public List<PolygonList2d> getFaces() {
            return faces;
        }

        /**
         * @param faces the faces to set
         */
        public void setFaces(List<PolygonList2d> faces) {
            this.faces = faces;
        }

        /**
         * @return the distance
         */
        public Map<Point2d, Double> getDistance() {
            return distance;
        }

        /**
         * @param distance the distance to set
         */
        public void setDistance(Map<Point2d, Double> distance) {
            this.distance = distance;
        }

    }

}
