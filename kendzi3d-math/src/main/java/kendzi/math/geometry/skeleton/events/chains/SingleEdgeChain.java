package kendzi.math.geometry.skeleton.events.chains;

import kendzi.math.geometry.skeleton.Skeleton.EdgeEntry;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;

public class SingleEdgeChain  extends Chain {

    private EdgeEntry oppositeEdge;

    private VertexEntry2 nextVertex;

    private VertexEntry2 previousVertex;

    public SingleEdgeChain(EdgeEntry oppositeEdge, VertexEntry2 nextVertex) {
        this.oppositeEdge = oppositeEdge;
        this.nextVertex = nextVertex;
        /*
         * previous vertex for opposite edge event is valid only before
         * processing of multi split event start. We need to store vertex before
         * processing starts.
         */
        this.previousVertex = nextVertex.previous();
    }

    @Override
    public EdgeEntry getPreviousEdge() {
        return oppositeEdge;
    }

    @Override
    public EdgeEntry getNextEdge() {
        return oppositeEdge;
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
        return null;
    }

    @Override
    public ChainType getType() {
        return ChainType.SPLIT;
    }
}
