package kendzi.math.geometry.skeleton.events;

import javax.vecmath.Point2d;

import kendzi.math.geometry.skeleton.Skeleton.SkeletonEvent;
import kendzi.math.geometry.skeleton.events.chains.EdgeChain;

public class MultiEdgeEvent extends SkeletonEvent {
    private EdgeChain chain;
    private boolean obsolete;

    public MultiEdgeEvent(Point2d eventPoint, EdgeChain chain) {
        this.chain = chain;
        this.v = eventPoint;
    }

    @Override
    public boolean isObsolete() {
        return obsolete;
    }

    public EdgeChain getChain() {
        return chain;
    }
}