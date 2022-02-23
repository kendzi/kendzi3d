/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.drawer;

import com.jogamp.opengl.GL2;

import java.awt.*;

import kendzi.jogl.Gl2Draw;
import kendzi.jogl.util.DrawUtil;
import org.lwjgl.opengl.GL11;

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
    private final float[] firstColor;

    /**
     * Not odd tiles color.
     */
    private final float[] secondColor;

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

        GL11.glDisable(GL11.GL_LIGHTING);

        // GL11.glColor3f(0.0f, 0.1f, 0.4f);
        GL11.glColor3fv(firstColor);
        DrawUtil.drawTiles(gl, 50, true);
        // GL11.glColor3f(0.0f, 0.5f, 0.1f);
        GL11.glColor3fv(secondColor);
        DrawUtil.drawTiles(gl, 50, false);

        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
