package kendzi.kendzi3d.editor.example.objects;

import java.util.Collections;
import java.util.List;

import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SphereSelection;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditorImp;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.SelectEvent;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener.EditorChangeListener;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Box implements EditableObject, EditorChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(Box.class);

    private final Vector3dc position = new Vector3d(0, 0, -2);

    private double size = 1;

    private boolean selected;

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

        final List<Editor> editors = Collections.singletonList(editor);

        selections = Collections.singletonList(new SphereSelection(position, size) {

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
    public Vector3dc getPosition() {
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