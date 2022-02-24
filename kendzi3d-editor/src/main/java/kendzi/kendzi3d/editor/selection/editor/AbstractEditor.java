/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.kendzi3d.editor.selection.editor;

public abstract class AbstractEditor implements Editor {

    @Override
    public double getEditorRadius() {
        return SELECTION_ETITOR_RADIUS * Editor.SELECTION_ETITOR_CAMERA_RATIO;
    }
}
