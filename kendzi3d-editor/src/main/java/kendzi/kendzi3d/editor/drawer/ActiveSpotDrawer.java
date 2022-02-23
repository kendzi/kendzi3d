package kendzi.kendzi3d.editor.drawer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.awt.Color;

import kendzi.jogl.util.ColorUtil;
import kendzi.jogl.util.DrawUtil;
import kendzi.kendzi3d.editor.selection.editor.EditorType;
import org.lwjgl.opengl.GL11;

/**
 * Util for drawing editor active spots.
 */
public class ActiveSpotDrawer {

    private static final int NUMBER_OF_SECTIONS = 16;

    /**
     * Storage For Our Quadratic Objects
     */
    private GLUquadric quadratic;

    private final GLU glu = new GLU();

    private final float[] highlightOutlineColor = ColorUtil.colorToArray(Color.BLACK.brighter());

    private final float[] highlightFillColor1 = ColorUtil.colorToArray(Color.RED.darker().darker());

    private final float[] highlightFillColor2 = ColorUtil.colorToArray(Color.GRAY.darker().darker().darker());

    private final float[] normalColor = ColorUtil.colorToArray(Color.GRAY.brighter());

    /**
     * Type of editor highlighting.
     */
    public enum EditorMode {
        NORMAL, HIGHLIGHT_1, HIGHLIGHT_2
    }

    /**
     * Initiate drawer.
     *
     * @param gl
     *            gl
     */
    public void init(GL2 gl) {
        // Quadric for geometry
        quadratic = glu.gluNewQuadric();
        // Create smooth normals quadric
        glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);
    }

    /**
     * Draws editor. Editor center point is at origin.
     *
     * @param gl
     *            gl
     * @param editorRadius
     *            editor radius
     * @param type
     *            type of editor
     * @param mode
     *            editor mode
     */
    public void drawEditor(GL2 gl, double editorRadius, EditorType type, EditorMode mode) {

        switch (mode) {
        case HIGHLIGHT_2:

            drawHighlightEditor(gl, type, editorRadius, highlightFillColor2);
            break;

        case HIGHLIGHT_1:

            drawHighlightEditor(gl, type, editorRadius, highlightFillColor1);
            break;

        case NORMAL:

            GL11.glColor3fv(normalColor);
            drawEditor(gl, editorRadius, type);
            break;

        default:
            throw new IllegalArgumentException("Unknown editor mode: " + mode);
        }
        GL11.glEnable(GL11.GL_LIGHTING);

    }

    private void drawHighlightEditor(GL2 gl, EditorType type, double editorRadius, float[] fillColor) {
        GL11.glColor3fv(highlightOutlineColor);

        SimpleOutlineDrawUtil.beginSimpleOutlineLine(gl);
        drawEditor(gl, editorRadius, type);

        SimpleOutlineDrawUtil.beginSimpleOutlinePoint(gl);
        drawEditor(gl, editorRadius, type);
        SimpleOutlineDrawUtil.endSimpleOutline(gl);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3fv(fillColor);
        drawEditor(gl, editorRadius, type);
    }

    private void drawEditor(GL2 gl, double editorRadius, EditorType type) {

        switch (type) {
        case ARROW:
            drawArrow(gl, glu, quadratic, editorRadius);
            break;
        case ARROW_HEAD:
            drawArrowhead(gl, glu, quadratic, editorRadius);
            break;
        case SPHERE:
            drawSphere(glu, quadratic, editorRadius);
            break;
        case BOX:
            DrawUtil.drawBox(gl, editorRadius);
            break;
        case BOX_SMALL:
            DrawUtil.drawBox(gl, editorRadius / 2d);
            break;

        default:
            throw new IllegalArgumentException("Unknown editor type: " + type);
        }

    }

    private static void drawSphere(GLU glu, GLUquadric quadratic, double editorRadius) {
        glu.gluSphere(quadratic, editorRadius, NUMBER_OF_SECTIONS, NUMBER_OF_SECTIONS);
    }

    private static void drawArrowhead(GL2 gl, GLU glu, GLUquadric quadratic, double editorRadius) {

        double length = 2d * editorRadius;

        GL11.glPushMatrix();
        GL11.glTranslated(0, -editorRadius, 0);

        ArrowDrawUtil.drawArrowheadSimple(gl, glu, quadratic, length, editorRadius, NUMBER_OF_SECTIONS);
        GL11.glPopMatrix();
    }

    private static void drawArrow(GL2 gl, GLU glu, GLUquadric quadratic, double editorRadius) {

        double length = 2d * editorRadius;

        double arrowheadLength = 1d * editorRadius;
        double baseRadius = 0.1d * editorRadius;
        double arrowheadRadius = 0.6d * editorRadius;

        GL11.glPushMatrix();
        GL11.glTranslated(0, -length / 2d, 0);
        ArrowDrawUtil.drawArrow(gl, glu, quadratic, length, arrowheadLength, baseRadius, arrowheadRadius, NUMBER_OF_SECTIONS);
        GL11.glPopMatrix();
    }
}
