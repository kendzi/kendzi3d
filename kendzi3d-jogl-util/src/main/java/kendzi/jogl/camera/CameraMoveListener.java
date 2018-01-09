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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.jogamp.opengl.awt.GLCanvas;

public class CameraMoveListener extends MouseAdapter implements KeyListener, ComponentListener {

    /**
     * Log
     */
    private static final Logger LOG = Logger.getLogger(CameraMoveListener.class);

    /**
     * Viewport
     */
    protected final Viewport viewport;

    /**
     * The class calculating decelerating or accelerating movements in response
     * to various mouse events.
     */
    protected SimpleMoveAnimator kinematicsSimpleAnimator;

    int lastX;
    int lastY;
    boolean move;

    public CameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator, Viewport viewport) {
        super();
        this.kinematicsSimpleAnimator = kinematicsSimpleAnimator;
        this.viewport = viewport;
    }

    public CameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator) {
        this(kinematicsSimpleAnimator, null);
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
                if (viewport != null) {
                    if (e.isControlDown()) {
                        viewport.resetZFar();
                    } else {
                        viewport.resetFovy();
                    }
                    reshapeCanvas(e);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.isConsumed()) {
            resumeCanvasAnimator(e);
            return;
        }

        lastX = e.getX();
        lastY = e.getY();
        move = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.isConsumed()) {
            resumeCanvasAnimator(e);
            return;
        }

        move = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    @Override
    public void mouseExited(MouseEvent e) {

        move = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (viewport != null) {
            if (e.isControlDown()) {
                viewport.setZFar(viewport.getZFar() + (e.isShiftDown() ? 1000 : 100) * -e.getWheelRotation());
            } else {
                viewport.setFovy(viewport.getFovy() + (e.isShiftDown() ? 10 : 1) * e.getWheelRotation());
            }
            reshapeCanvas(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("mouseDragged");
        }

        if (e.isConsumed()) {
            resumeCanvasAnimator(e);
            return;
        }

        moveCamera(lastX - e.getX(), lastY - e.getY());
        lastX = e.getX();
        lastY = e.getY();

        resumeCanvasAnimator(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if (e.isConsumed()) {
            resumeCanvasAnimator(e);
            return;
        }
    }

    @Override
    public void keyTyped(KeyEvent pEvent) {
        //
    }

    @Override
    public void keyPressed(KeyEvent pEvent) {
        moveAction(pEvent, true);
    }

    @Override
    public void keyReleased(KeyEvent pEvent) {
        moveAction(pEvent, false);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        resumeCanvasAnimator(e);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        resumeCanvasAnimator(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // resumeCanvasAnimator(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        pauseCanvasAnimator(e);
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
    protected void moveCamera(int pDX, int pDY) {

        double mouseMoveAngle = 0.25;

        if (viewport != null) {
            mouseMoveAngle *= viewport.getFovy() / viewport.getFovyDefault();
        }

        if (pDX != 0) { // left-right
            kinematicsSimpleAnimator.rotateHorizontally(Math.toRadians(-pDX * mouseMoveAngle));
        }

        if (pDY != 0) { // up-down
            kinematicsSimpleAnimator.rotateVertically(Math.toRadians(-pDY * mouseMoveAngle));
        }

        setLookAt();
    }

    protected void setLookAt() {
        //
    }

    protected void reshapeCanvas(ComponentEvent e) {
        if (e.getSource() instanceof GLCanvas) {
            GLCanvas canvas = (GLCanvas) e.getSource();

            canvas.reshape(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
            resumeCanvasAnimator(e);
        }
    }

    protected boolean resumeCanvasAnimator(ComponentEvent e) {
        if (e.getSource() instanceof GLCanvas) {
            GLCanvas canvas = (GLCanvas) e.getSource();

            kinematicsSimpleAnimator.resetAdditionalFrameCounter();
            if (canvas.getAnimator().isPaused()) {
                kinematicsSimpleAnimator.resetLastTime();
                canvas.getAnimator().resume();
                return true;
            }
        }
        return false;
    }

    protected void pauseCanvasAnimator(ComponentEvent e) {
        if (e.getSource() instanceof GLCanvas) {
            ((GLCanvas) e.getSource()).getAnimator().pause();
        }
    }

    protected void toggleCanvasAnimator(ComponentEvent e) {
        if (!resumeCanvasAnimator(e)) {
            pauseCanvasAnimator(e);
        }
    }
}
