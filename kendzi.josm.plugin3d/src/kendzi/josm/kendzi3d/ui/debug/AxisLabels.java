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
import java.util.ArrayList;
import java.util.List;

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

    private List<TextToRender> textToRender;


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

        if (this.textToRender == null) {
            this.textToRender = createLabels();
        }

        drawAxisText(pGl, this.textToRender);

        pGl.glEnable(GL2.GL_LIGHTING);
    }

    /**
     * @return
     */
    private List<TextToRender> createLabels() {

        List<TextToRender> textToRender = new ArrayList<AxisLabels.TextToRender>();


        for (int i = -this.lenghtX / 2; i <= this.lenghtX / 2; i++) {
            // along x-axis
            //drawAxisText(pGl, "x: " + i, i, 0.0f, 0.0f);
            textToRender.add(new TextToRender("x: " + i, i, 0.0f, 0.0f));
        }

        for (int i = -this.lenghtY / 2; i <= this.lenghtY / 2; i++) {
            // along z-axis
            //drawAxisText(pGl, "z: " + i, 0.0f, 0.0f, i);
            textToRender.add(new TextToRender("z: " + i, 0.0f, 0.0f, i));
        }

        for (int i = -this.lenghtZ / 2; i <= this.lenghtZ / 2; i++) {
            // along y-axis
            //drawAxisText(pGl, "y: " + i, 0.0f, i, 0.0f);
            textToRender.add(new TextToRender("y: " + i, 0.0f, i, 0.0f));
        }

        return textToRender;
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

    /**
     * Draw list of texts described by pText. Each text have (x,y,z), with the
     * text centered in the x-direction, facing along the +z axis.
     *
     * @param pGl gl2
     * @param pText text to draw
     */
    private void drawAxisText(GL2 pGl, List<TextToRender> pText) {

        this.axisLabelRenderer.begin3DRendering();

        for (TextToRender textToRender : pText) {

            Rectangle2D dim = this.axisLabelRenderer.getBounds(textToRender.getText());
            float width = (float) dim.getWidth() * SCALE_FACTOR;

            this.axisLabelRenderer.draw3D(textToRender.getText(), textToRender.getX() - width / 2, textToRender.getY(), textToRender.getZ(), SCALE_FACTOR);
        }

        this.axisLabelRenderer.end3DRendering();
    }

    class TextToRender {
        private String text;
        private float x;
        private float y;
        private float z;

        public TextToRender(String text, float x, float y, float z) {
            super();
            this.text = text;
            this.x = x;
            this.y = y;
            this.z = z;
        }
        /**
         * @return the text
         */
        public String getText() {
            return text;
        }
        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }
        /**
         * @return the x
         */
        public float getX() {
            return x;
        }
        /**
         * @param x the x to set
         */
        public void setX(float x) {
            this.x = x;
        }
        /**
         * @return the y
         */
        public float getY() {
            return y;
        }
        /**
         * @param y the y to set
         */
        public void setY(float y) {
            this.y = y;
        }
        /**
         * @return the z
         */
        public float getZ() {
            return z;
        }
        /**
         * @param z the z to set
         */
        public void setZ(float z) {
            this.z = z;
        }


    }

}
