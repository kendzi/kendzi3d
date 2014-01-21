package kendzi.math.geometry.skeleton.events.chains;

import kendzi.math.geometry.skeleton.Skeleton.ChainEnds;

public abstract class Chain implements ChainEnds {
    // FIXME
    public abstract ChainType getType();

    public enum ChainType {
        EDGE, CLOSED_EDGE, SPLIT
    }
}