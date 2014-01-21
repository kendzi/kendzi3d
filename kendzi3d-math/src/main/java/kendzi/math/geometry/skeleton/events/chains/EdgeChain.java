package kendzi.math.geometry.skeleton.events.chains;

import java.util.List;

import kendzi.math.geometry.skeleton.Skeleton.EdgeEntry;
import kendzi.math.geometry.skeleton.Skeleton.EdgeEvent;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;

public class EdgeChain extends Chain {
    private boolean closed;
    @Deprecated
    private boolean split;
    private List<EdgeEvent> edgeList;
    private EdgeEntry oppositeEdge;

    public EdgeChain(List<EdgeEvent> edgeList) {
        this.edgeList = edgeList;
        closed = getPreviousVertex() == getNextVertex();
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public List<EdgeEvent> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<EdgeEvent> edgeList) {
        this.edgeList = edgeList;
    }

    public EdgeEntry getOppositeEdge() {
        return oppositeEdge;
    }

    public void setOppositeEdge(EdgeEntry oppositeEdge) {
        this.oppositeEdge = oppositeEdge;
    }

    @Override
    public EdgeEntry getPreviousEdge() {
        return edgeList.get(0).Va.e_a;
    }

    @Override
    public EdgeEntry getNextEdge() {
        return edgeList.get(edgeList.size() - 1).Vb.e_b;
    }

    @Override
    public VertexEntry2 getPreviousVertex() {
        return edgeList.get(0).Va;
    }

    @Override
    public VertexEntry2 getNextVertex() {
        return edgeList.get(edgeList.size() - 1).Vb;
    }

    @Override
    public VertexEntry2 getCurrentVertex() {
        return null;
    }

    @Override
    public ChainType getType() {
        if (closed && split) {
            throw new RuntimeException("chain can't be closed and split");
        }
        if (closed) {
            return ChainType.CLOSED_EDGE;
        }
        if (split) {
            return ChainType.SPLIT;
        }
        return ChainType.EDGE;
    }
}