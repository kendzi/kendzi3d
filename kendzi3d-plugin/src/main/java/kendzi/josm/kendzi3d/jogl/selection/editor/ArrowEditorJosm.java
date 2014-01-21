/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.josm.kendzi3d.jogl.selection.editor;

import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

public interface ArrowEditorJosm extends ArrowEditor {

    long getPrimitiveId();

    OsmPrimitiveType getPrimitiveType();

    String getFildName();

    void setValue(double value);

    public void preview(double newValue);
}
