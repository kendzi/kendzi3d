/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.editor.selection.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.math.geometry.ray.Ray3d;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public abstract class MouseSelectionListener extends MouseAdapter {

    /** Log. */
    private static final Logger LOG = Logger.getLogger(MouseSelectionListener.class);

    public MouseSelectionListener() {
        super();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            select(e.getX(), e.getY());
            e.consume();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (selectActiveEditor(e.getX(), e.getY())) {
                e.consume();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (moveActiveEditor(e.getX(), e.getY(), true)) {
                e.consume();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("mouseDragged");
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (moveActiveEditor(e.getX(), e.getY(), false)) {
                e.consume();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectHighlightedEditor(e.getX(), e.getY())) {
            e.consume();
        }
    }

    protected abstract boolean selectActiveEditor(int x, int y);

    protected abstract boolean selectHighlightedEditor(int x, int y);

    protected abstract boolean moveActiveEditor(int x, int y, boolean b);

    protected abstract Selection select(int x, int y);

    protected abstract Selection select(Ray3d selectRay);

}
