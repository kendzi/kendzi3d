package kendzi.kendzi3d.editor.drawer;

import org.lwjgl.opengl.GL11;

public class SimpleOutlineDrawUtil {

    public static void endSimpleOutline() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_POINT);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void beginSimpleOutlineLine() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(9);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        // offset polygons to back
        GL11.glPolygonOffset(1.0f, 1.0f);
        // bold line

        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void beginSimpleOutlinePoint() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);

        GL11.glPointSize(6);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_POINT);

        // offset polygons to back
        GL11.glPolygonOffset(2.0f, 2.0f);

        GL11.glDisable(GL11.GL_LIGHTING);
    }

}
