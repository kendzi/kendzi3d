/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.vecmath.Point3d;

import kendzi.jogl.camera.Viewport;
import kendzi.jogl.camera.ViewportPicker;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.EditableObjectProvider;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditor;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.SelectEditorEvent;
import kendzi.kendzi3d.editor.selection.event.SelectEvent;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener;
import kendzi.math.geometry.ray.Ray3d;
import kendzi.math.geometry.ray.Ray3dUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class ObjectSelectionManager extends ObjectSelectionListener {

    /** Log. */
    private static final Logger LOG = Logger.getLogger(ObjectSelectionManager.class);

    private transient Editor lastActiveEditor;

    private transient Editor lastHighlightedEditor;

    private transient Ray3d lastSelectRay;

    private transient Selection lastSelection;

    private transient Point3d lastClosestPointOnBaseRay;

    private final ViewportPicker viewportPicker;

    private final EditableObjectProvider editableObjectProvider;

    private transient Viewport viewport;

    @Inject
    public ObjectSelectionManager(ViewportPicker viewportPicker, EditableObjectProvider editableObjectProvider) {
        this.viewportPicker = viewportPicker;
        this.editableObjectProvider = editableObjectProvider;
    }

    @Override
    protected Selection select(Ray3d selectRay) {
        List<EditableObject> editableObjects = editableObjectProvider.getEditableObjects();

        List<EditableObject> selectCandidates = new ArrayList<>();

        for (EditableObject object : editableObjects) {

            List<Selection> selection = object.getSelection();

            if (selection != null) {
                for (Selection s : selection) {
                    if (s.intersectCandidate(selectRay)) {
                        selectCandidates.add(object);
                        break;
                    }
                }
            }
        }

        Selection selection = null;
        double min = Double.MAX_VALUE;

        for (EditableObject object : selectCandidates) {
            List<Selection> selections = object.getSelection();
            for (Selection s : selections) {
                Double intersect = s.intersect(selectRay);

                if (intersect == null) {
                    continue;
                }
                if (intersect < min) {
                    selection = s;
                    min = intersect;
                }
            }
        }
        return selection;
    }

    /**
     * Translates 2d mouse coordinates in window space into 3d ray inside 3d
     * screen. Ray position and direction depends on mouse location and current
     * viewport settings.
     *
     * @param x
     *            mouse x location in window space
     * @param y
     *            mouse y location in window space
     * @return ray in 3d space of current viewport
     */
    public Ray3d viewportPicking(int x, int y) {
        return viewportPicker.picking(x, y);
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.kendzi3d.editor.selection.listener.jogl.selection.MouseSelectionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("mouseDragged");
        }
        if (moveActiveEditor(e.getX(), e.getY(), false)) {
            e.consume();
        }
    }

    /**
     * @return the activeEditor
     */
    public Editor getActiveEditor() {
        return lastActiveEditor;
    }

    @Override
    protected boolean moveActiveEditor(int x, int y, boolean finish) {
        // TODO
        Editor activeEditor = lastActiveEditor;
        EditorChangeEvent event = null;

        if (activeEditor == null) {
            return false;
        }

        if (!(activeEditor instanceof ArrowEditor)) {
            return false;
        }
        { // XXX
            Ray3d moveRay = viewportPicking(x, y);
            ArrowEditor arrow = (ArrowEditor) activeEditor;

            event = arrow.move(moveRay, finish);

            raiseEditorChange(event);

            Ray3d arrowRay = new Ray3d(arrow.getEditorOrigin(), arrow.getVector());
            Point3d closestPointOnBaseRay = Ray3dUtil.closestPointOnBaseRay(moveRay, arrowRay);

            lastClosestPointOnBaseRay = closestPointOnBaseRay;

        }
        return true;
    }

    @Override
    protected void selectActiveEditor(int x, int y) {

        Ray3d selectRay = viewportPicking(x, y);

        Editor activeEditor = selectEditor(selectRay, lastSelection);

        lastActiveEditor = activeEditor;
        lastSelectRay = selectRay;

        raiseSelectEditor(new SelectEditorEvent(activeEditor));
    }

    @Override
    protected void selectHighlightedEditor(int x, int y) {
        Selection selection = lastSelection;

        Ray3d selectRay = viewportPicking(x, y);

        Editor selectedEditor = selectEditor(selectRay, selection);

        lastHighlightedEditor = selectedEditor;
    }

    protected Editor selectEditor(Ray3d selectRay, Selection selection) {

        if (selection == null) {
            return null;
        }

        List<Editor> editors = selection.getEditors();
        if (editors == null) {
            return null;
        }

        Editor selectedEditor = null;
        double min = Double.MAX_VALUE;

        for (Editor e : editors) {
            if (e instanceof ArrowEditor) {
                ArrowEditor ae = (ArrowEditor) e;

                Double intersect = ae.intersect(selectRay);

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

        Editor activeEditor = selectEditor(selectRay, lastSelection);
        if (activeEditor == null) {

            selection = select(selectRay);

            boolean selectionChanged = lastSelection != selection;

            if (selectionChanged) {
                if (selection != null) {
                    LOG.debug("selected object: " + selection);
                    selection.onSelectEvent(new SelectEvent(true, x, y));
                }
                if (lastSelection != null) {
                    LOG.debug("selected object: " + selection);
                    lastSelection.onSelectEvent(new SelectEvent(false, x, y));
                }
            }
        }

        lastSelectRay = selectRay;
        lastSelection = selection;

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

    /**
     * @return the lastHighlightedEditor
     */
    public Editor getLastHighlightedEditor() {
        return lastHighlightedEditor;
    }

}
