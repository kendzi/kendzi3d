package kendzi.jogl.camera;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.ray.Ray3d;
import org.junit.jupiter.api.Test;

class ViewportTest {

    private static final double DELTA = 0.0001;

    @Test
    void testUpdateViewport() {
        final Viewport viewport = new Viewport(520, 480);
        viewport.updateViewport(new CameraTest(new Point3d(0, 0, 0), new Vector3d(0, 0, 0)));
        assertAll(() -> assertEquals(new Point3d(0, 0, 0), viewport.getPosition(), "Position"),
                () -> assertEquals(new Vector3d(100, 0, 0), viewport.getLookAt(), "Look At"),
                () -> assertEquals(new Vector3d(0, 1, 0), viewport.getLookUp(), "Look Up"),
                () -> assertEquals(new Vector3d(0, 0, 0.44873136281967163), viewport.getScreenHorizontally(),
                        "Screen Horizontally"),
                () -> assertEquals(new Vector3d(0, 0.4142135679721832, 0), viewport.getScreenVertically(), "Screen Vertically"));
        viewport.updateViewport(new CameraTest(new Point3d(-3, -2, -1), new Vector3d(1, 1, 1)));
        assertAll(() -> assertEquals(new Point3d(-3, -2, -1), viewport.getPosition(), "Position"),
                () -> assertEquals(new Vector3d(26.192658172642886, 82.14709848078965, -46.46487134128409), viewport.getLookAt(),
                        "Look At"),
                () -> assertEquals(new Vector3d(-0.4546487134128409, 0.5403023058681398, 0.7080734182735712),
                        viewport.getLookUp(), "Look Up"),
                () -> assertEquals(new Vector3d(0.3775944217860587, 0.0, 0.24245059004682143), viewport.getScreenHorizontally(),
                        "Screen Horizontally"),
                () -> assertEquals(new Vector3d(-0.18832166575669537, 0.22380054589724005, 0.293293616969356),
                        viewport.getScreenVertically(), "Screen Vertically"));
    }

    @Test
    void testPicking() {
        final int distance = 70;
        final Viewport viewport = new Viewport(520, 480);
        viewport.updateViewport(new CameraTest(new Point3d(0, 0, 0), new Vector3d(0, 0, 0)));

        final Ray3d ray3d1 = viewport.picking(distance, viewport.getHeight() - distance);

        assertAll(() -> assertEquals(new Point3d(0, 0, 0), ray3d1.getPoint(), "Point"),
                () -> assertEquals(new Vector3d(1.0, -0.2934012690839456, -0.3279190666574969), ray3d1.getVector(), "Vector"));
        viewport.updateViewport(new CameraTest(new Point3d(-3, -2, -1), new Vector3d(1, 1, 1)));

        final Ray3d ray3d2 = viewport.picking(distance, viewport.getHeight() - distance);
        assertAll(() -> assertEquals(new Point3d(-3, -2, -1), ray3d2.getPoint(), "Point"),
                () -> assertEquals(new Vector3d(0.14938671127156944, 0.682945602577202, -0.839573780792088), ray3d2.getVector(),
                        "Vector"));
    }
}
