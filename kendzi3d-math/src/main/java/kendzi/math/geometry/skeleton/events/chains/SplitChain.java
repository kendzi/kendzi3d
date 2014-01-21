package kendzi.math.geometry.skeleton.events.chains;

import kendzi.math.geometry.skeleton.Skeleton.EdgeEntry;
import kendzi.math.geometry.skeleton.Skeleton.SplitEvent;
import kendzi.math.geometry.skeleton.Skeleton.VertexEntry2;
import kendzi.math.geometry.skeleton.Skeleton.VertexSplitEvent;
public   class SplitChain extends Chain {

    private SplitEvent splitEvent;

    public SplitChain(SplitEvent event) {
        splitEvent = event;
    }

    @Override
    public EdgeEntry getPreviousEdge() {
        return splitEvent.getParent().e_a;
    }

    @Override
    public EdgeEntry getNextEdge() {
        return splitEvent.getParent().e_b;
    }

    @Override
    public VertexEntry2 getPreviousVertex() {
        return splitEvent.getParent().previous();
    }

    @Override
    public VertexEntry2 getNextVertex() {
        return splitEvent.getParent().next();
    }

    @Override
    public VertexEntry2 getCurrentVertex() {
        return splitEvent.getParent();
    }


    public SplitEvent getSplitEvent() {
        return splitEvent;
    }

    public EdgeEntry getOppositeEdge() {
        if (!(splitEvent instanceof VertexSplitEvent)) {
            return splitEvent.getOppositeEdge();
        }
        return null;
    }

    @Override
    public ChainType getType() {
        return ChainType.SPLIT;
    }
}