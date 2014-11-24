package kendzi.josm.kendzi3d.jogl.model.roof.mk.wall;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.SegmentHeight;

public interface HeightCalculator {
    List<SegmentHeight> height(Point2d p1, Point2d p2);
}