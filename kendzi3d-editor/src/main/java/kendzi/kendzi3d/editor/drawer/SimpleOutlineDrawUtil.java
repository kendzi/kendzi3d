package kendzi.kendzi3d.editor.drawer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;

public class SimpleOutlineDrawUtil {

    public static void endSimpleOutline() {
        GL11C.glEnable(GL11.GL_LIGHTING);
        GL11C.glDisable(GL11C.GL_POLYGON_OFFSET_LINE);
        GL11C.glDisable(GL11C.GL_POLYGON_OFFSET_POINT);
        GL11C.glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL11C.GL_FILL);

        GL11C.glDisable(GL11C.GL_CULL_FACE);
    }

    public static void beginSimpleOutlineLine() {
        GL11C.glEnable(GL11C.GL_CULL_FACE);
        GL11C.glCullFace(GL11C.GL_FRONT);

        GL11C.glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL11C.GL_LINE);
        GL11C.glLineWidth(9);

        GL11C.glEnable(GL11C.GL_POLYGON_OFFSET_LINE);
        // offset polygons to back
        GL11C.glPolygonOffset(1.0f, 1.0f);
        // bold line

        GL11C.glDisable(GL11.GL_LIGHTING);
    }

    public static void beginSimpleOutlinePoint() {
        GL11C.glEnable(GL11C.GL_CULL_FACE);
        GL11C.glCullFace(GL11C.GL_FRONT);

        GL11C.glPolygonMode(GL11C.GL_FRONT_AND_BACK, GL11C.GL_POINT);

        GL11C.glPointSize(6);
        GL11C.glEnable(GL11C.GL_POLYGON_OFFSET_POINT);

        // offset polygons to back
        GL11C.glPolygonOffset(2.0f, 2.0f);

        GL11C.glDisable(GL11.GL_LIGHTING);
    }

}
