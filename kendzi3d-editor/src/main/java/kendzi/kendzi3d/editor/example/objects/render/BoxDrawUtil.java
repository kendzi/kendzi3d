package kendzi.kendzi3d.editor.example.objects.render;

import com.jogamp.opengl.GL2;

import javax.vecmath.Point3d;

import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.drawer.SimpleOutlineDrawUtil;
import kendzi.kendzi3d.editor.example.objects.Box;
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
     * @param gl
     *            gl
     */
    public static void draw(Box box, GL2 gl) {

        double size = box.getSize();

        Point3d max = new Point3d(box.getPosition());
        max.x += size;
        max.y += size;
        max.z += size;

        Point3d min = new Point3d(box.getPosition());
        min.x -= size;
        min.y -= size;
        min.z -= size;

        DrawUtil.drawFullBox(gl, max, min);
    }

    private static void drawSelected(GL2 gl, Point3d max, Point3d min) {
        drawSelectedFill(gl, max, min);
        drawGreenOutline(gl, max, min);
    }

    private static void drawSelectedFill(GL2 gl, Point3d max, Point3d min) {

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        // offset polygons to front
        GL11.glPolygonOffset(-2.0f, -2.0f);

        DrawUtil.drawFullBox(gl, max, min);

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

    }

    private static void drawGreenOutline(GL2 gl, Point3d max, Point3d min) {

        // green
        GL11.glColor3f(0.5f, 1.0f, 0.5f);

        SimpleOutlineDrawUtil.beginSimpleOutlineLine(gl);

        DrawUtil.drawFullBox(gl, max, min);

        SimpleOutlineDrawUtil.endSimpleOutline(gl);

    }

}
