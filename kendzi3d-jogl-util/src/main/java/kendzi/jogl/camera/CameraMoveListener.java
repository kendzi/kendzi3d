/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.camera;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.jogamp.opengl.awt.GLCanvas;

public class CameraMoveListener implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(CameraMoveListener.class);

    int lastX;
    int lastY;
    boolean move;
    private boolean isRunning = true;

    private SimpleMoveAnimator kinematicsSimpleAnimator;

    public CameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator) {
        super();
        this.kinematicsSimpleAnimator = kinematicsSimpleAnimator;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.isShiftDown()) {
                Viewport.setZFar(Viewport.getZFar() + 1000 * -e.getWheelRotation());
            } else {
                Viewport.setZFar(Viewport.getZFar() + 100 * -e.getWheelRotation());
            }
        } else if (e.isShiftDown()){
            Viewport.setFovy(Viewport.getFovy() + 10 * e.getWheelRotation());
        } else {
            Viewport.setFovy(Viewport.getFovy() + 1 * e.getWheelRotation());
        }
        reshapeCanvas(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            if (e.getClickCount() == 2) {
                //toggleCanvasAnimator(e);
            } else {
                if (e.isControlDown()) {
                    Viewport.setZFar(Viewport.PERSP_FAR_CLIPPING_PLANE_DISTANCE.getDefaultValue());
                } else {
                    Viewport.setFovy(Viewport.PERSP_VIEW_ANGLE.getDefaultValue());
                }
                reshapeCanvas(e);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {

        lastX = e.getX();
        lastY = e.getY();
        move = true;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        move = false;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {

        move = false;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("mouseDragged");
        }

        if (e.isConsumed()) {
            return;
        }

        moveCamera(e, lastX - e.getX(), lastY - e.getY());
        lastX = e.getX();
        lastY = e.getY();

    }

    @Override
    public void keyPressed(KeyEvent pEvent) {
        boolean start = true;

        if (isRunning) {

            moveAction(pEvent, start);
        }
    }

    @Override
    public void keyTyped(KeyEvent pEvent) {
        //
    }

    @Override
    public void keyReleased(KeyEvent pEvent) {
        boolean start = false;

        if (isRunning) {

            moveAction(pEvent, start);
        }

    }

    /**
     * @param pEvent
     * @param start
     */
    public void moveAction(KeyEvent pEvent, boolean start) {
        int keyCode = pEvent.getKeyCode();
        // move based on the arrow key pressed
        if (keyCode == KeyEvent.VK_LEFT) { // left
            if (pEvent.isControlDown()) { // translate left
                kinematicsSimpleAnimator.translateLeft(start);
                kinematicsSimpleAnimator.rotateLeft(false);
            } else { // turn left
                kinematicsSimpleAnimator.translateLeft(false);
                kinematicsSimpleAnimator.rotateLeft(start);
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) { // right
            if (pEvent.isControlDown()) {
                // translate right
                kinematicsSimpleAnimator.translateRight(start);
                kinematicsSimpleAnimator.rotateRight(false);
            } else { // turn right
                kinematicsSimpleAnimator.translateRight(false);
                kinematicsSimpleAnimator.rotateRight(start);
            }
        } else if (keyCode == KeyEvent.VK_D) {
            // translate right
            kinematicsSimpleAnimator.translateRight(start);

        } else if (keyCode == KeyEvent.VK_A) {
            // translate left
            kinematicsSimpleAnimator.translateLeft(start);

        } else if (keyCode == KeyEvent.VK_W) {
            // move forward
            kinematicsSimpleAnimator.moveForward(start);

        } else if (keyCode == KeyEvent.VK_S) {
            // move backwards
            kinematicsSimpleAnimator.moveBackwards(start);

        } else if (keyCode == KeyEvent.VK_UP) {
            // move forward
            kinematicsSimpleAnimator.moveForward(start);

        } else if (keyCode == KeyEvent.VK_DOWN) {
            // move backwards
            kinematicsSimpleAnimator.moveBackwards(start);

        } else if (keyCode == KeyEvent.VK_PAGE_UP || keyCode == KeyEvent.VK_E) {
            // move up
            kinematicsSimpleAnimator.moveUp(start);

        } else if (keyCode == KeyEvent.VK_PAGE_DOWN || keyCode == KeyEvent.VK_Q) {
            // move down
            kinematicsSimpleAnimator.moveDown(start);

        }

        resumeCanvasAnimator(pEvent);
    }

    /**
     * Rotate camera.
     *
     * @param pDX
     *            number of pixels mouse moved in X axe
     * @param pDY
     *            number of pixels mouse moved in Y axe
     */
    protected void moveCamera(MouseEvent e, int pDX, int pDY) {

        double mouseMoveAngle = 0.25;

        mouseMoveAngle *= Viewport.PERSP_VIEW_ANGLE.get() / Viewport.PERSP_VIEW_ANGLE.getDefaultValue();

        if (pDX != 0) { // left-right
            kinematicsSimpleAnimator.rotateHorizontally(Math.toRadians(-pDX * mouseMoveAngle));
        }

        if (pDY != 0) { // up-down
            kinematicsSimpleAnimator.rotateVertically(Math.toRadians(-pDY * mouseMoveAngle));
        }

        setLookAt();

        resumeCanvasAnimator(e);
    }

    private void setLookAt() {
        //
    }

    private void toggleCanvasAnimatorImpl(ComponentEvent e, boolean resume, boolean pause) {
        if (e.getSource() instanceof GLCanvas) {
            GLCanvas canvas = (GLCanvas) e.getSource();
            if ((canvas.getAnimator().isPaused() || resume) && !pause) {
                canvas.getAnimator().resume();
                kinematicsSimpleAnimator.resetAdditionalFrameCounter();
            } else {
                canvas.getAnimator().pause();
            }
        }
    }

    public void toggleCanvasAnimator(ComponentEvent e) {
        toggleCanvasAnimatorImpl(e, false, false);
    }

    public void resumeCanvasAnimator(ComponentEvent e) {
        toggleCanvasAnimatorImpl(e, true, false);
    }

    public void pauseCanvasAnimator(ComponentEvent e) {
        toggleCanvasAnimatorImpl(e, false, true);
    }

    public void reshapeCanvas(ComponentEvent e) {
        if (e.getSource() instanceof GLCanvas) {
            GLCanvas canvas = (GLCanvas) e.getSource();
            canvas.reshape(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
            canvas.getAnimator().resume();
            kinematicsSimpleAnimator.resetAdditionalFrameCounter();
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        resumeCanvasAnimator(e);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        reshapeCanvas(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        resumeCanvasAnimator(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        resumeCanvasAnimator(e);
    }

}
