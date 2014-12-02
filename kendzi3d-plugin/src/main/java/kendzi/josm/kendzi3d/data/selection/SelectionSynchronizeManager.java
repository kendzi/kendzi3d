package kendzi.josm.kendzi3d.data.selection;

import java.util.Collection;

import kendzi.josm.kendzi3d.data.OsmPrimitiveWorldObject;
import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.selection.ObjectSelectionManager;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SelectionCriteria;

import org.openstreetmap.josm.data.SelectionChangedListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveId;

public class SelectionSynchronizeManager implements SelectionChangedListener {

    private ObjectSelectionManager objectSelectionManager;

    public SelectionSynchronizeManager(ObjectSelectionManager objectSelectionManager) {
        this.objectSelectionManager = objectSelectionManager;
    }

    public void register() {
        DataSet.addSelectionListener(this);
    }

    public void unregister() {
        DataSet.removeSelectionListener(this);
    }

    @Override
    public void selectionChanged(Collection<? extends OsmPrimitive> primitives) {

        SelectionCriteria criteria = new PrimitiveSelection(primitives);

        objectSelectionManager.select(criteria);
    }

    private static class PrimitiveSelection implements SelectionCriteria {

        private Collection<? extends PrimitiveId> primitives;

        public PrimitiveSelection(Collection<? extends PrimitiveId> primitives) {
            this.primitives = primitives;
        }

        @Override
        public boolean match(EditableObject editableObject) {
            if (editableObject instanceof OsmPrimitiveWorldObject) {
                OsmPrimitiveWorldObject obj = (OsmPrimitiveWorldObject) editableObject;

                return primitives.contains(obj.getPrimitiveId());

            }
            return false;
        }

        @Override
        public boolean match(Selection selection) {

            return true;
        }

    }

}
