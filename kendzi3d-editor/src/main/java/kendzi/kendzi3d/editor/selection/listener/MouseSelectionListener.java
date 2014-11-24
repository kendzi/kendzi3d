/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.editor.selection.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.math.geometry.ray.Ray3d;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public abstract class MouseSelectionListener implements MouseListener, MouseMotionListener {

    /**
     *
     */
    public MouseSelectionListener() {
        super();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        selectHighlightedEditor(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        select(e.getX(), e.getY());

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (moveActiveEditor(e.getX(), e.getY(), true)) {
            e.consume();
        }
    }

    protected abstract void selectActiveEditor(int x, int y);

    protected abstract void selectHighlightedEditor(int x, int y);

    protected abstract boolean moveActiveEditor(int x, int y, boolean b);

    protected abstract Selection select(int x, int y);

    protected abstract Selection select(Ray3d selectRay);

    @Override
    public void mousePressed(MouseEvent e) {
        selectActiveEditor(e.getX(), e.getY());
    }
}
