/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.jogl.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;

public class CameraMoveListener implements KeyListener, MouseMotionListener, MouseListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(CameraMoveListener.class);

    private final SimpleMoveAnimator kinematicsSimpleAnimator;

    public CameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator) {
        super();
        this.kinematicsSimpleAnimator = kinematicsSimpleAnimator;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    int lastX;
    int lastY;
    boolean move;
    private final boolean isRunning = true;

    /*
     * (non-Javadoc)
     * 
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
     * 
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        move = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {

        move = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //
    }

    /*
     * (non-Javadoc)
     * 
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

        moveCamera(lastX - e.getX(), lastY - e.getY());
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
        boolean start = true;

        if (isRunning) {

            moveAction(pEvent, start);
        }

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

        if (pDX != 0) { // left
            kinematicsSimpleAnimator.rotateHorizontally(Math.toRadians(-pDX * mouseMoveAngle));
        }

        if (pDY != 0) { // left
            kinematicsSimpleAnimator.rotateVertically(Math.toRadians(-pDY * mouseMoveAngle));
        }

        setLookAt();
    }

    private void setLookAt() {
        //
    }

}
