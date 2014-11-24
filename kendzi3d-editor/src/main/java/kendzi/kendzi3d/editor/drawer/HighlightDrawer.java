package kendzi.kendzi3d.editor.drawer;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.util.ColorUtil;

/**
 * Simple highlight drawer for object.
 *
 */
public class HighlightDrawer {

    private static float[] selectionColor = ColorUtil.colorToArray(new Color(0.5f, 1.0f, 0.5f));

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

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);

        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        // offset polygons to front
        gl.glPolygonOffset(-2.0f, -2.0f);

        drawer.draw(gl);

        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);

    }

    private static void drawGreenOutline(Gl2Draw drawer, GL2 gl) {

        // selection color
        gl.glColor4fv(selectionColor, 0);
        gl.glDisable(GL.GL_TEXTURE_2D);

        SimpleOutlineDrawUtil.beginSimpleOutlineLine(gl);
        drawer.draw(gl);

        SimpleOutlineDrawUtil.beginSimpleOutlinePoint(gl);
        drawer.draw(gl);

        SimpleOutlineDrawUtil.endSimpleOutline(gl);
    }
}
