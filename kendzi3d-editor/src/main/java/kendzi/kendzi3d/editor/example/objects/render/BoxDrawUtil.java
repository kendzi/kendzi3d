package kendzi.kendzi3d.editor.example.objects.render;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.drawer.SimpleOutlineDrawUtil;
import kendzi.kendzi3d.editor.example.objects.Box;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.opengl.GL11;

/**
 * Util to draw box.
 */
public final class BoxDrawUtil {

    private BoxDrawUtil() {
        //
    }

    /**
     * Draws box.
     *
     * @param box
     *            box
     */
    public static void draw(Box box) {

        double size = box.getSize();

        Vector3d max = new Vector3d(box.getPosition());
        max.x += size;
        max.y += size;
        max.z += size;

        Vector3d min = new Vector3d(box.getPosition());
        min.x -= size;
        min.y -= size;
        min.z -= size;

        DrawUtil.drawFullBox(max, min);
    }

    private static void drawSelected(Vector3dc max, Vector3dc min) {
        drawSelectedFill(max, min);
        drawGreenOutline(max, min);
    }

    private static void drawSelectedFill(Vector3dc max, Vector3dc min) {

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        // offset polygons to front
        GL11.glPolygonOffset(-2.0f, -2.0f);

        DrawUtil.drawFullBox(max, min);

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

    }

    private static void drawGreenOutline(Vector3dc max, Vector3dc min) {

        // green
        GL11.glColor3f(0.5f, 1.0f, 0.5f);

        SimpleOutlineDrawUtil.beginSimpleOutlineLine();

        DrawUtil.drawFullBox(max, min);

        SimpleOutlineDrawUtil.endSimpleOutline();

    }

}
