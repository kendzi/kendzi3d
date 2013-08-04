/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection;

import java.util.List;

import javax.vecmath.Point3d;

import kendzi.josm.kendzi3d.jogl.selection.editor.Editor;

public interface Selection {
    Point3d getCenter();
    double getRadius();

    void select(boolean selected);

    List<Editor> getEditors();
}
