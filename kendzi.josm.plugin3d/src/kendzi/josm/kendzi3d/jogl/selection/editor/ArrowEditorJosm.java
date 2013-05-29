package kendzi.josm.kendzi3d.jogl.selection.editor;

import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

public interface ArrowEditorJosm extends ArrowEditor {

    long getPrimitiveId();

    OsmPrimitiveType getPrimitiveType();

    String getFildName();

    void setValue(double value);
}
