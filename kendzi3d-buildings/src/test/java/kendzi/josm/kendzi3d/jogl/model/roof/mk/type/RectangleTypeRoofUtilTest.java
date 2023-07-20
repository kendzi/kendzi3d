package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import static org.junit.Assert.assertEquals;

import kendzi.kendzi3d.buildings.builder.roof.shape.type.RectangleTypeRoofUtil;
import kendzi.math.geometry.AngleUtil;
import kendzi.math.geometry.polygon.PolygonList2d;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.junit.Test;

public class RectangleTypeRoofUtilTest {

    private PolygonList2d createOutline1() {
        return new PolygonList2d(new Vector2d(-1, 1), new Vector2d(22, 1), new Vector2d(0, -1));
    }

    @Test
    public void test1() {
        PolygonList2d outerPolygon = createOutline1();

        Vector2dc alignedDirectionToOutline = RectangleTypeRoofUtil.snapsDirectionToOutline(new Vector2d(0, 1), outerPolygon);

        assertVectorAngle(90, alignedDirectionToOutline);
    }

    @Test
    public void test2() {
        PolygonList2d outerPolygon = createOutline1();

        Vector2dc alignedDirectionToOutline = RectangleTypeRoofUtil.snapsDirectionToOutline(new Vector2d(1, 0), outerPolygon);

        assertVectorAngle(26.5, alignedDirectionToOutline);
    }

    @Test
    public void test3() {
        PolygonList2d outerPolygon = createOutline1();

        Vector2dc alignedDirectionToOutline = RectangleTypeRoofUtil.snapsDirectionToOutline(new Vector2d(1, -1), outerPolygon);

        assertVectorAngle(-85, alignedDirectionToOutline);
    }

    private void assertVectorAngle(double angle, Vector2dc alignedDirectionToOutline) {
        assertEquals("angles not match", angle, Math.toDegrees(AngleUtil.angle(alignedDirectionToOutline)), 0.5);
    }
}
