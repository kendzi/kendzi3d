package kendzi.kendzi3d.editor.example.objects;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SphereSelection;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditorImp;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.SelectEvent;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener.EditorChangeListener;

import org.apache.log4j.Logger;

public class Box implements EditableObject, EditorChangeListener {

    private static final Logger LOG = Logger.getLogger(Box.class);

    private final Point3d position = new Point3d(0, 0, -2);

    private double size = 1;

    private boolean selected = false;

    private List<Selection> selections;

    /**
     * Constructor.
     */
    public Box() {
        createSelections();
    }

    private void createSelections() {

        ArrowEditorImp editor = new ArrowEditorImp(position, new Vector3d(0, 1, 0), size);
        editor.setOffset(0.1);
        editor.addChangeListener(this);

        final List<Editor> editors = Arrays.asList((Editor) editor);

        selections = Arrays.asList((Selection) new SphereSelection(position, size) {

            @Override
            public List<Editor> getEditors() {
                return editors;
            }

            @Override
            public void onSelectEvent(SelectEvent event) {
                selected = event.isSelected();
            }

            @Override
            public Object getSource() {
                return Box.this;
            }

            @Override
            public double getRadius() {
                return size;
            }
        });
    }

    @Override
    public void onEditorChange(EditorChangeEvent event) {
        if (event instanceof ArrowEditorChangeEvent) {
            ArrowEditorChangeEvent arrow = (ArrowEditorChangeEvent) event;

            LOG.info("editor changed: " + arrow.getLength());
            setSize(arrow.getLength());
        }
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

    @Override
    public List<Selection> getSelection() {
        return selections;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}