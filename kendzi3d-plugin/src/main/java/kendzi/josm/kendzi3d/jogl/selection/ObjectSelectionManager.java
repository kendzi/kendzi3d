/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditor;
import kendzi.josm.kendzi3d.jogl.selection.editor.Editor;
import kendzi.josm.kendzi3d.jogl.selection.event.ArrowEditorChangeEvent;
import kendzi.josm.kendzi3d.jogl.selection.event.SelectEditorEvent;
import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public abstract class ObjectSelectionManager extends ObjectSelectionListener {

    /** Log. */
    private static final Logger log = Logger.getLogger(ObjectSelectionManager.class);

    private Editor lastActiveEditor;

    private Ray3d lastSelectRay;

    private Selection lastSelection;

    private Point3d lastClosestPointOnBaseRay;

    /**
     * @param x
     * @param y
     * @return
     */
    public abstract Ray3d viewportPicking(int x, int y);

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.selection.MouseSelectionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (log.isTraceEnabled()) {
            log.trace("mouseDragged");
        }
        if (moveActiveEditor(e.getX(), e.getY(), false)) {
            e.consume();
        }
    }

    // public void onSelectEditor(SelectEditorEvent args) {
    // this.lastActiveEditor = args.getEditor();
    // }

    /**
     * @return the activeEditor
     */
    public Editor getActiveEditor() {
        return this.lastActiveEditor;
    }

    @Override
    protected boolean moveActiveEditor(int x, int y, boolean finish) {

        Editor activeEditor = this.lastActiveEditor;

        if (activeEditor == null) {
            return false;
        }

        if (!(activeEditor instanceof ArrowEditor)) {
            return false;
        }

        ArrowEditor arrow = (ArrowEditor) activeEditor;

        Ray3d moveRay = viewportPicking(x, y);

        Ray3d arrowRay = new Ray3d(arrow.getEditorOrigin(), arrow.getVector());
        Point3d closestPointOnBaseRay = Ray3dUtil.closestPointOnBaseRay(moveRay, arrowRay);

        double height = arrow.getEditorOrigin().distance(closestPointOnBaseRay);

        this.raiseEditorChange(new ArrowEditorChangeEvent(finish, arrow, height, closestPointOnBaseRay));

        this.lastClosestPointOnBaseRay = closestPointOnBaseRay;

        return true;
    }

    @Override
    protected void selectActiveEditor(int x, int y) {

        Ray3d selectRay = viewportPicking(x, y);

        Editor activeEditor = selectActiveEditor(selectRay, this.lastSelection);

        this.lastActiveEditor = activeEditor;
        this.lastSelectRay = selectRay;

        raiseSelectEditor(new SelectEditorEvent(activeEditor));
    }

    protected Editor selectActiveEditor(Ray3d selectRay, Selection selection) {

        if (selection == null) {
            return null;
        }

        List<Editor> editors = selection.getEditors();

        Editor selectedEditor = null;
        double min = Double.MAX_VALUE;

        for (Editor e : editors) {
            if (e instanceof ArrowEditor) {
                ArrowEditor ae = (ArrowEditor) e;

                //Double intersect = ae.intersect(selectRay);
                Double intersect = ae.intersect(selectRay, Editor.SELECTION_ETITOR_CAMERA_RATIO);

                if (intersect == null) {
                    continue;
                }
                if (intersect < min) {
                    selectedEditor = e;
                    min = intersect;
                }
            }
        }
        return selectedEditor;
    }

    @Override
    protected Selection select(int x, int y) {

        Selection selection = null;

        Ray3d selectRay = viewportPicking(x, y);

        Editor activeEditor = selectActiveEditor(selectRay, this.lastSelection);
        if (activeEditor != null) {
            // activeEditor.select(true);
            // this.activeEditor = activeEditor;

        } else {

            selection = select(selectRay);
        }

        this.lastSelectRay = selectRay;
        this.lastSelection = selection;

        return selection;
    }

    /**
     * @return the lastClosestPointOnBaseRay
     */
    public Point3d getLastClosestPointOnBaseRay() {
        return lastClosestPointOnBaseRay;
    }

    /**
     * @return the lastSelectRay
     */
    public Ray3d getLastSelectRay() {
        return lastSelectRay;
    }

    /**
     * @return the lastSelection
     */
    public Selection getLastSelection() {
        return lastSelection;
    }

    /**
     * @return the lastActiveEditor
     */
    public Editor getLastActiveEditor() {
        return lastActiveEditor;
    }

}
