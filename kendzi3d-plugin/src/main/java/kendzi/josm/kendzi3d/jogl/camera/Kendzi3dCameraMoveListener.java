package kendzi.josm.kendzi3d.jogl.camera;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import kendzi.jogl.camera.SimpleMoveAnimator;

public class Kendzi3dCameraMoveListener extends kendzi.jogl.camera.CameraMoveListener {

    private final Kendzi3dViewport viewport;

    public Kendzi3dCameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator, Kendzi3dViewport viewport) {
        super(kinematicsSimpleAnimator);
        this.viewport = viewport;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.isConsumed()) {
            resumeCanvasAnimator(e);
            return;
        }

        if (SwingUtilities.isMiddleMouseButton(e)) {
            if (e.getClickCount() == 2) {
                resumeCanvasAnimator(e);
            } else {
                if (e.isControlDown()) {
                    viewport.setZFar(viewport.PERSP_FAR_CLIPPING_PLANE_DISTANCE.getDefaultValue());
                } else {
                    viewport.setFovy(viewport.PERSP_VIEW_ANGLE.getDefaultValue());
                }
                reshapeCanvas(e);
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                viewport.setZFar(viewport.getZFar() + 1000 * -e.getWheelRotation());
            } else {
                viewport.setZFar(viewport.getZFar() + 100 * -e.getWheelRotation());
            }
        } else {
            if (e.isShiftDown()) {
                viewport.setFovy(viewport.getFovy() + 10 * e.getWheelRotation());
            } else {
                viewport.setFovy(viewport.getFovy() + 1 * e.getWheelRotation());
            }
        }
        reshapeCanvas(e);
    }

    /**
     * Rotate camera.
     *
     * @param pDX
     *            number of pixels mouse moved in X axe
     * @param pDY
     *            number of pixels mouse moved in Y axe
     */
    @Override
    protected void moveCamera(int pDX, int pDY) {

        double mouseMoveAngle = 0.25;

        mouseMoveAngle *= viewport.PERSP_VIEW_ANGLE.get() / viewport.PERSP_VIEW_ANGLE.getDefaultValue();

        if (pDX != 0) { // left-right
            kinematicsSimpleAnimator.rotateHorizontally(Math.toRadians(-pDX * mouseMoveAngle));
        }

        if (pDY != 0) { // up-down
            kinematicsSimpleAnimator.rotateVertically(Math.toRadians(-pDY * mouseMoveAngle));
        }

        setLookAt();
    }
}
