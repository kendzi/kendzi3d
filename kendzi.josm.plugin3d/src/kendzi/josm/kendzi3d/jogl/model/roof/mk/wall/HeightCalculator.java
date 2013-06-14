package kendzi.josm.kendzi3d.jogl.model.roof.mk.wall;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;

public interface HeightCalculator {
    SegmentHeight [] height(Point2d p1, Point2d p2);
}