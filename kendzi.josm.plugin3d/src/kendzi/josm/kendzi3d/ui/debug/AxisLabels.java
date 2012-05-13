/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.ui.debug;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Creates axis labels.
 *
 * @author Tomasz Kędziora (Kendzi)
 *
 */
public class AxisLabels {

    /**
     * Default axis lenghts.
     */
    private static final int AXIS_LENGHT = 50;

    /**
     * Scale for the axis labels.
     */
    private static final float SCALE_FACTOR = 0.01f;

    /**
     * Font for axis.
     */
    private Font font;

    /**
     * Text renderer for axis labels.
     */
    private TextRenderer axisLabelRenderer;

    /**
     * Length of x axis labels.
     */
    private int lenghtX;

    /**
     * Length of y axis labels.
     */
    private int lenghtY;

    /**
     * Length of z axis labels.
     */
    private int lenghtZ;


    /**
     * Creates axis labels for AXIS_LENGHT unit.
     */
    public AxisLabels() {
        this(AXIS_LENGHT, AXIS_LENGHT, AXIS_LENGHT);
    }

    /** Creates axis labels for defined units.
     * @param pLenghtX length of x axis labels
     * @param pLenghtY length of y axis labels
     * @param pLenghtZ length of z axis labels
     */
    public AxisLabels(int pLenghtX, int pLenghtY, int pLenghtZ) {
        this.lenghtX = pLenghtX;
        this.lenghtY = pLenghtY;
        this.lenghtZ = pLenghtZ;
    }

    public void init() {

        this.font = new Font("SansSerif", Font.BOLD, 24);
        this.axisLabelRenderer = new TextRenderer(this.font );
        // FIXME don't work ?!
        this.axisLabelRenderer.setUseVertexArrays(false);
    }

    /**
     * Place numbers along the x- and z-axes at the integer positions.
     *
     * @param pGl gl2
     */
    public void draw(GL2 pGl) {

        pGl.glDisable(GL2.GL_LIGHTING);

        for (int i = -this.lenghtX / 2; i <= this.lenghtX / 2; i++) {
            drawAxisText(pGl, "x: " + i, i, 0.0f, 0.0f); // along x-axis
        }

        for (int i = -this.lenghtY / 2; i <= this.lenghtY / 2; i++) {
            drawAxisText(pGl, "z: " + i, 0.0f, 0.0f, i); // along z-axis
        }

        for (int i = -this.lenghtZ / 2; i <= this.lenghtZ / 2; i++) {
            drawAxisText(pGl, "y: " + i, 0.0f, i, 0.0f); // along y-axis
        }

        pGl.glEnable(GL2.GL_LIGHTING);
    }

    /**
     * Draw text at (x,y,z), with the text centered in the x-direction, facing
     * along the +z axis.
     *
     * @param pGl gl2
     * @param pText text to draw
     * @param x coordinate x
     * @param y coordinate y
     * @param z coordinate z
     */
    private void drawAxisText(GL2 pGl, String pText, float x, float y, float z) {

        Rectangle2D dim = this.axisLabelRenderer.getBounds(pText);
        float width = (float) dim.getWidth() * SCALE_FACTOR;

        this.axisLabelRenderer.begin3DRendering();
        this.axisLabelRenderer.draw3D(pText, x - width / 2, y, z, SCALE_FACTOR);
        this.axisLabelRenderer.end3DRendering();
    }

}
