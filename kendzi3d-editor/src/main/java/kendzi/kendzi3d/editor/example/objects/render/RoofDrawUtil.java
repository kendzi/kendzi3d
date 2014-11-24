package kendzi.kendzi3d.editor.example.objects.render;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.example.objects.Roof;

/**
 * Util to draw roof.
 */
public final class RoofDrawUtil {

    private RoofDrawUtil() {
        //
    }

    /**
     * Draws roof.
     *
     * @param box
     *            box
     * @param gl
     *            gl
     */
    public static void draw(Roof roof, GL2 gl) {

        double height = roof.getHeigth();
        double width = roof.getWidth();
        double roofHeigth = roof.getRoofHeigth();

        Point3d max = new Point3d(roof.getPosition());
        max.x += width;
        max.y += height - roofHeigth;
        max.z += width;

        Point3d min = new Point3d(roof.getPosition());
        min.x -= width;
        min.y -= 0;
        min.z -= width;

        // roof base
        DrawUtil.drawFullBox(gl, max, min);

        max.set(roof.getPosition());
        max.x += width / 2;
        max.y += height;
        max.z += width / 2;

        min.set(roof.getPosition());
        min.x -= width / 2;
        min.y -= height - roofHeigth;
        min.z -= width / 2;

        // roof top
        DrawUtil.drawFullBox(gl, max, min);
    }

}
