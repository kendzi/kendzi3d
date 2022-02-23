package kendzi.kendzi3d.editor.drawer;

import java.awt.*;

import javax.vecmath.Point3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.util.ColorUtil;
import kendzi.jogl.util.LineDrawUtil;
import kendzi.kendzi3d.editor.drawer.ActiveSpotDrawer.EditorMode;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.editor.EditorType;
import org.lwjgl.opengl.GL11;

/**
 * Drawer for arrow editor.
 */
public class ArrowEditorDrawer {

    private static final int MEASURE_LINE_WIDTH = 2;
    private static final double MEASURE_HORIZONTAL_DISTANCE = 1d;
    private static final double MEASURE_ARROW_HEIGHT = 0.25;
    private static final double MEASURE_ARROW_WIDTH = 0.075;
    private static final double DISTANCE_RATIO_SCALE = 0.1;
    private static final double DOTTED_LINE_SEGMENT_LENGTH = 0.3;
    private static final int DOTTED_LINE_WIDTH = 4;
    private final ActiveSpotDrawer activeSpotDrawer = new ActiveSpotDrawer();
    private final MeasureDrawer measureDrawer = new MeasureDrawer();
    private final float[] arrowEditorDottedLines = ColorUtil.colorToArray(Color.GRAY);
    private final float[] measureColor = ColorUtil.colorToArray(Color.GRAY.darker());

    /**
     * Initiate drawer.
     *
     */
    public void init() {
        activeSpotDrawer.init();
    }

    /**
     * Draws arrow editor.
     *
     * @param ae
     *            arrow editor to draw
     * @param isActiveEditor
     *            if editor is active
     * @param isHighlightedEditor
     *            if editor is highlighted
     * @param viewport
     *            viewport
     */
    public void draw(ArrowEditor ae, boolean isActiveEditor, boolean isHighlightedEditor, Viewport viewport) {

        Point3d cameraPoint = viewport.getPosition();

        Point3d activeSpot = ae.getActiveSpot(cameraPoint);

        double distanceRatio = distanceRatio(ae, viewport);

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);

        drawActiveSpot(activeSpot, isHighlightedEditor, ae.getEditorType(), distanceRatio);

        drawDottedLine(ae);

        if (isHighlightedEditor) {
            drawMeasure(ae, viewport, distanceRatio);
        }

        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }

    private double distanceRatio(ArrowEditor ae, Viewport viewport) {

        Point3d point = ae.getEditorOrigin();
        Point3d cameraPoint = viewport.getPosition();

        return cameraPoint.distance(point) * 480d / viewport.getHeight();
    }

    private void drawMeasure(ArrowEditor ae, Viewport viewport, double distanceRatio) {
        GL11.glColor3fv(measureColor);

        Point3d origin = ae.getEditorOrigin();
        Point3d arrowEnd = ae.arrowEnd();

        double distanceScale = DISTANCE_RATIO_SCALE * distanceRatio;

        double arrowWidth = MEASURE_ARROW_WIDTH * distanceScale;
        double arrowHeight = MEASURE_ARROW_HEIGHT * distanceScale;
        double horizontalDistance = MEASURE_HORIZONTAL_DISTANCE;
        float lineWidth = MEASURE_LINE_WIDTH;

        measureDrawer.drawYMeasureWithArrows(origin, arrowEnd, ae.getLength(), viewport, horizontalDistance, arrowHeight,
                arrowWidth, lineWidth);
    }

    private void drawActiveSpot(Point3d activeSpot, boolean isHighlightedEditor, EditorType editorType, double distanceRatio) {
        GL11.glPushMatrix();
        GL11.glTranslated(activeSpot.x, activeSpot.y, activeSpot.z);

        EditorMode highlight = EditorMode.HIGHLIGHT_2;
        if (isHighlightedEditor) {
            highlight = EditorMode.HIGHLIGHT_1;
        }

        double camDistanceRatio = distanceRatio * Editor.SELECTION_ETITOR_CAMERA_RATIO;

        activeSpotDrawer.drawEditor(camDistanceRatio, editorType, highlight);
        GL11.glPopMatrix();
    }

    private void drawDottedLine(ArrowEditor ae) {
        GL11.glColor3fv(arrowEditorDottedLines);

        GL11.glLineWidth(DOTTED_LINE_WIDTH);
        Point3d editorOrigin = ae.getEditorOrigin();
        Point3d arrowEnd = ae.arrowEnd();
        LineDrawUtil.drawDottedLine(editorOrigin, arrowEnd, DOTTED_LINE_SEGMENT_LENGTH);
    }
}
