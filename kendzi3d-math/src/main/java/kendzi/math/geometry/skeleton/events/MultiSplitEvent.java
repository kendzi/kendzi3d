package kendzi.math.geometry.skeleton.events;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.math.geometry.skeleton.Skeleton.SkeletonEvent;
import kendzi.math.geometry.skeleton.events.chains.Chain;

public class MultiSplitEvent extends SkeletonEvent {

    private List<Chain> chains;

    private boolean obsolete;

    public MultiSplitEvent(Point2d eventPoint, List<Chain> chains) {
        this.chains = chains;
        this.v = eventPoint;
    }

    @Override
    public boolean isObsolete() {
        return obsolete;
    }

    public List<Chain> getChains() {
        return chains;
    }
}