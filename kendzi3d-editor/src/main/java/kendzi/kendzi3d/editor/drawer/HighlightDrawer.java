package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL2;

import java.awt.*;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.util.ColorUtil;
import org.lwjgl.opengl.GL11;

/**
 * Simple highlight drawer for object.
 *
 */
public class HighlightDrawer {

    private static final float[] selectionColor = ColorUtil.colorToArray(new Color(0.5f, 1.0f, 0.5f));

    /**
     * Draw object with highlight.
     *
     * @param object
     *            object to draw
     * @param gl
     *            gl
     */
    public static void drawHighlight(Gl2Draw object, GL2 gl) {

        drawSelectedFill(object, gl);
        drawGreenOutline(object, gl);
    }

    private static void drawSelectedFill(Gl2Draw drawer, GL2 gl) {

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        // offset polygons to front
        GL11.glPolygonOffset(-2.0f, -2.0f);

        drawer.draw(gl);

        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

    }

    private static void drawGreenOutline(Gl2Draw drawer, GL2 gl) {

        // selection color
        GL11.glColor4fv(selectionColor);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        SimpleOutlineDrawUtil.beginSimpleOutlineLine(gl);
        drawer.draw(gl);

        SimpleOutlineDrawUtil.beginSimpleOutlinePoint(gl);
        drawer.draw(gl);

        SimpleOutlineDrawUtil.endSimpleOutline(gl);
    }
}
