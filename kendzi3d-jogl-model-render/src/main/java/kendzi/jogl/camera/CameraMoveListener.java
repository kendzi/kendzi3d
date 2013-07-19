/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
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

    private SimpleMoveAnimator kinematicsSimpleAnimator;



    public CameraMoveListener(SimpleMoveAnimator kinematicsSimpleAnimator) {
        super();
        this.kinematicsSimpleAnimator = kinematicsSimpleAnimator;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }



    int lastX;
    int lastY;
    boolean move;
    private boolean isRunning = true;

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {

        this.lastX = e.getX();
        this.lastY = e.getY();
        this.move = true;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //log.error("mouseReleased");

        this.move = false;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        //log.error("mouseExited");

        this.move = false;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.err.println("mouseMoved");
        //        if (move) {
        //            moveCamera(lastX - e.getX() , lastY - e.getY());
        //
        //            lastX = e.getX();
        //            lastY = e.getY();
        //        }
    }

    /* (non-Javadoc)
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

        moveCamera(this.lastX - e.getX() , this.lastY - e.getY());
        this.lastX = e.getX();
        this.lastY = e.getY();

    }

    @Override
    public void keyPressed(KeyEvent pEvent) {
        boolean start = true;

        if (this.isRunning) {

            moveAction(pEvent, start);
        }
    }


    @Override
    public void keyTyped(KeyEvent pEvent) {
        boolean start = true;

        if (this.isRunning) {

            moveAction(pEvent, start);
        }

    }

    @Override
    public void keyReleased(KeyEvent pEvent) {
        boolean start = false;

        if (this.isRunning) {

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
                this.kinematicsSimpleAnimator.translateLeft(start);
                this.kinematicsSimpleAnimator.rotateLeft(false);
            } else { // turn left
                this.kinematicsSimpleAnimator.translateLeft(false);
                this.kinematicsSimpleAnimator.rotateLeft(start);
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) { // right
            if (pEvent.isControlDown()) {
                // translate right
                this.kinematicsSimpleAnimator.translateRight(start);
                this.kinematicsSimpleAnimator.rotateRight(false);
            } else { // turn right
                this.kinematicsSimpleAnimator.translateRight(false);
                this.kinematicsSimpleAnimator.rotateRight(start);
            }
        } else if (keyCode == KeyEvent.VK_D) {
            // translate right
            this.kinematicsSimpleAnimator.translateRight(start);

        } else if (keyCode == KeyEvent.VK_A) {
            // translate left
            this.kinematicsSimpleAnimator.translateLeft(start);

        } else if (keyCode == KeyEvent.VK_W) {
            // move forward
            this.kinematicsSimpleAnimator.moveForward(start);

        } else if (keyCode == KeyEvent.VK_S) {
            // move backwards
            this.kinematicsSimpleAnimator.moveBackwards(start);

        } else if (keyCode == KeyEvent.VK_UP) {
            // move forward
            this.kinematicsSimpleAnimator.moveForward(start);

        } else if (keyCode == KeyEvent.VK_DOWN) {
            // move backwards
            this.kinematicsSimpleAnimator.moveBackwards(start);

        } else if (keyCode == KeyEvent.VK_PAGE_UP
                || keyCode == KeyEvent.VK_E) {
            // move up
            this.kinematicsSimpleAnimator.moveUp(start);

        } else if (keyCode == KeyEvent.VK_PAGE_DOWN
                || keyCode == KeyEvent.VK_Q) {
            // move down
            this.kinematicsSimpleAnimator.moveDown(start);

        }
    }


    /** Rotate camera.
     * @param pDX number of pixels mouse moved in X axe
     * @param pDY number of pixels mouse moved in Y axe
     */
    protected void moveCamera(int pDX, int pDY) {

        double mouseMoveAngle = 0.25;

        if (pDX != 0) { // left
            this.kinematicsSimpleAnimator.rotateHorizontally(Math.toRadians(-pDX * mouseMoveAngle));
            //                this.camera.xViewAngle += (pDX * mouseMoveAngle);
            //
            //                this.camera.xStep = Math.cos(Math.toRadians(this.camera.xViewAngle));
            //                this.camera.zStep = Math.sin(Math.toRadians(this.camera.xViewAngle));
        }

        if (pDY != 0) { // left
            this.kinematicsSimpleAnimator.rotateVertically(Math.toRadians(-pDY * mouseMoveAngle));
            //            this.camera.yViewAngle -= (pDY * mouseMoveAngle);
            //
            //            if (this.camera.yViewAngle > 90) {
            //                this.camera.yViewAngle = 90;
            //            }
            //            if (this.camera.yViewAngle < -90) {
            //                this.camera.yViewAngle = -90;
            //            }
            //
            //            this.camera.yStep = Math.sin(Math.toRadians(this.camera.yViewAngle));
        }

        setLookAt();
    }



    private void setLookAt() {
        // TODO Auto-generated method stub

    }

}
