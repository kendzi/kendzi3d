package kendzi.kendzi3d.editor.selection;

import java.util.List;

import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.event.SelectEvent;
import kendzi.math.geometry.ray.Ray3d;

public interface Selection {

    /**
     * The distance from ray start to closest selection point.
     * 
     * @param ray
     *            ray which intersect with selectable object
     * @return distance to selectable object or null
     */
    Double intersect(Ray3d ray);

    /**
     * Fast check if given object is candidate to be selected. Exact result can
     * be check with method intersect.
     * 
     * @see kendzi.kendzi3d.editor.selection.Selection#intersect(Ray3d)
     * 
     * @param ray
     *            ray which intersect with selectable object
     * @return distance to selectable object or null
     */
    boolean intersectCandidate(Ray3d ray);

    /**
     * Editors which can work on given selection.
     * 
     * @return list of aviliable editors for current selection
     */
    List<Editor> getEditors();

    /**
     * When object selection change.
     * 
     * @param event
     *            select event
     */
    void onSelectEvent(SelectEvent event);

    /**
     * Gets selection source object.
     * 
     * @return selection source object
     */
    Object getSource();
}
