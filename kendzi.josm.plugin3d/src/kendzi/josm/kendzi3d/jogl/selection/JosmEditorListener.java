package kendzi.josm.kendzi3d.jogl.selection;

import java.text.DecimalFormat;

import kendzi.josm.kendzi3d.jogl.selection.editor.ArrowEditorJosm;
import kendzi.josm.kendzi3d.jogl.selection.event.ArrowEditorChangeEvent;
import kendzi.josm.kendzi3d.jogl.selection.event.EditorChangeEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;

public class JosmEditorListener implements kendzi.josm.kendzi3d.jogl.selection.ObjectSelectionListener.EditorChangeListener {

    DecimalFormat formater = new DecimalFormat( "#0.0" );

    @Override
    public void onEditorChange(EditorChangeEvent event) {

        if (event instanceof ArrowEditorChangeEvent) {
            ArrowEditorChangeEvent aece = (ArrowEditorChangeEvent) event;

            if ( aece.getArrowEditor() instanceof ArrowEditorJosm) {

                ArrowEditorJosm ae = (ArrowEditorJosm) aece.getArrowEditor();
                double newValue = aece.getHeight();

                if (aece.isEnd() ) {

                    OsmPrimitive primitive = Main.main.getCurrentDataSet().getPrimitiveById(ae.getPrimitiveId(), ae.getPrimitiveType());


                    if (primitive instanceof Way) {
                        Way newWay = new Way((Way) primitive);


                        newWay.put(ae.getFildName(), this.formater.format(newValue));

                        ae.setValue(newValue);

                        Main.main.undoRedo.add(new ChangeCommand(primitive, newWay));

                    } else {
                        throw new RuntimeException("TODO");
                    }
                } else {
                    ae.preview(newValue);
                }

                ae.setValue(newValue);

            } else {
                throw new RuntimeException("TODO");
            }
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
