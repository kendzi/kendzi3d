/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.apache.log4j.Logger;

public class MouseSelectionListener implements  MouseListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(MouseSelectionListener.class);


    public MouseSelectionListener() {
        super();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
       //

    }

    int lastX;
    int lastY;

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        //
    }
}
