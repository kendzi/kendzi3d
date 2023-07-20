package kendzi.kendzi3d.editor.example.objects.render;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.example.objects.Roof;
import org.joml.Vector3d;

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
     */
    public static void draw(Roof roof) {

        double height = roof.getHeigth();
        double width = roof.getWidth();
        double roofHeigth = roof.getRoofHeigth();

        Vector3d max = new Vector3d(roof.getPosition()).add(width, height - roofHeigth, width);

        Vector3d min = new Vector3d(roof.getPosition()).sub(width, 0, width);

        // roof base
        DrawUtil.drawFullBox(max, min);

        max.set(roof.getPosition());
        max.x += width / 2;
        max.y += height;
        max.z += width / 2;

        min.set(roof.getPosition());
        min.x -= width / 2;
        min.y -= height - roofHeigth;
        min.z -= width / 2;

        // roof top
        DrawUtil.drawFullBox(max, min);
    }

}
