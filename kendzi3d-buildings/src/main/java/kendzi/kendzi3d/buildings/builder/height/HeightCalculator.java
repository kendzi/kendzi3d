package kendzi.kendzi3d.buildings.builder.height;

import java.util.List;

import javax.vecmath.Point2d;

public interface HeightCalculator {
    List<SegmentHeight> height(Point2d p1, Point2d p2);
}