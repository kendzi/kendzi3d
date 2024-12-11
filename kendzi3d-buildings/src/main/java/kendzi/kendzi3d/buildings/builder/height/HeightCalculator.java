package kendzi.kendzi3d.buildings.builder.height;

import java.util.List;

import org.joml.Vector2dc;

public interface HeightCalculator {
    List<SegmentHeight> height(Vector2dc p1, Vector2dc p2);
}