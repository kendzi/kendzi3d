/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.drawer;

import java.awt.Color;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.util.DrawUtil;

/**
 * Draws tiles surface in two colors.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class TilesSurface implements Gl2Draw {

    private static final Color BLUE_COLOR = new Color(0.0f, 0.1f, 0.4f);

    private static final Color GREEN_COLOR = new Color(0.0f, 0.5f, 0.1f);

    /**
     * Odd tiles color.
     */
    private float[] firstColor;

    /**
     * Not odd tiles color.
     */
    private float[] secondColor;

    /**
     * Creates tiles surface drawer with blue and green tiles colors.
     */
    public TilesSurface() {
        this(BLUE_COLOR, GREEN_COLOR);
    }

    /**
     * Creates tiles surface drawer with defined two colors.
     *
     * @param firstColor
     *            color of odd tiles
     * @param secondColor
     *            color of not odd tiles
     */
    public TilesSurface(Color firstColor, Color secondColor) {
        this.firstColor = firstColor.getRGBComponents(null);
        this.secondColor = secondColor.getRGBComponents(null);
    }

    public void init() {
        //
    }

    /**
     * Draws tiles surface in two colors.
     *
     * @param gl
     *            gl context
     */

    @Override
    public void draw(GL2 gl) {

        gl.glDisable(GLLightingFunc.GL_LIGHTING);

        // gl.glColor3f(0.0f, 0.1f, 0.4f);
        gl.glColor3fv(firstColor, 0);
        DrawUtil.drawTiles(gl, 50, true);
        // gl.glColor3f(0.0f, 0.5f, 0.1f);
        gl.glColor3fv(secondColor, 0);
        DrawUtil.drawTiles(gl, 50, false);

        gl.glEnable(GLLightingFunc.GL_LIGHTING);
    }
}
