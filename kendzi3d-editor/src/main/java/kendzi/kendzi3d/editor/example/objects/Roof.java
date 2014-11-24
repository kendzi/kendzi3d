package kendzi.kendzi3d.editor.example.objects;

import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.kendzi3d.editor.EditableObject;
import kendzi.kendzi3d.editor.selection.Selection;
import kendzi.kendzi3d.editor.selection.SphereSelection;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditorImp;
import kendzi.kendzi3d.editor.selection.editor.CachePoint3dProvider;
import kendzi.kendzi3d.editor.selection.editor.Editor;
import kendzi.kendzi3d.editor.selection.editor.EditorType;
import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;
import kendzi.kendzi3d.editor.selection.event.EditorChangeEvent;
import kendzi.kendzi3d.editor.selection.listener.ObjectSelectionListener.EditorChangeListener;

import org.apache.log4j.Logger;

public class Roof implements EditableObject {

    private static final Logger LOG = Logger.getLogger(Roof.class);

    private final Point3d position = new Point3d(0, 0, 1);

    private double heigth = 2;

    private double roofHeigth = 1;

    private double width = 1;

    private List<Selection> selections;

    /**
     * Constructor.
     */
    public Roof() {
        createSelections();
    }

    private void createSelections() {

        final CachePoint3dProvider heightPointProvider = new CachePoint3dProvider() {

            @Override
            public void beforeProvide(Point3d point) {
                point.set(position);
                point.y += heigth;
            }
        };

        final ArrowEditorImp editorHeight = new ArrowEditorImp(position, new Vector3d(0, 1, 0), heigth);
        editorHeight.setOffset(0.1);

        final ArrowEditorImp editorRoofHeight = new ArrowEditorImp();
        editorRoofHeight.setEditorOrigin(heightPointProvider);
        editorRoofHeight.setVector(new Vector3d(0, -1, 0));
        editorRoofHeight.setLength(roofHeigth);
        editorRoofHeight.setEditorType(EditorType.BOX_SMALL);

        editorHeight.addChangeListener(new EditorChangeListener() {

            @Override
            public void onEditorChange(EditorChangeEvent event) {
                if (event instanceof ArrowEditorChangeEvent) {
                    ArrowEditorChangeEvent arrow = (ArrowEditorChangeEvent) event;

                    double newHeight = arrow.getLength();
                    LOG.info("editor height changed: " + newHeight);

                    if (newHeight < 0) {
                        setHeigth(0);
                        setRoofHeigth(0);

                        editorHeight.setLength(0);
                        editorRoofHeight.setLength(0);

                    } else if (newHeight - roofHeigth < 0) {

                        setHeigth(newHeight);
                        setRoofHeigth(newHeight);

                        editorRoofHeight.setLength(newHeight);
                    } else {
                        setHeigth(newHeight);
                    }
                }
            }
        });

        editorRoofHeight.addChangeListener(new EditorChangeListener() {

            @Override
            public void onEditorChange(EditorChangeEvent event) {
                if (event instanceof ArrowEditorChangeEvent) {
                    ArrowEditorChangeEvent arrow = (ArrowEditorChangeEvent) event;

                    double length = arrow.getLength();
                    LOG.info("editor roof changed: " + length);

                    if (length < 0) {
                        double newHeigth = getHeigth() - length;

                        setHeigth(newHeigth);
                        setRoofHeigth(0);

                        editorHeight.setLength(newHeigth);
                        editorRoofHeight.setLength(0);
                    } else if (length > heigth) {

                        setRoofHeigth(heigth);

                        editorRoofHeight.setLength(heigth);
                    } else {

                        setRoofHeigth(length);
                    }
                }
            }
        });

        final List<Editor> editors = Arrays.asList((Editor) editorHeight, (Editor) editorRoofHeight);

        selections = Arrays.asList((Selection) new SphereSelection(position, heigth) {

            @Override
            public List<Editor> getEditors() {
                return editors;
            }

            @Override
            public Object getSource() {
                return Roof.this;
            }

            @Override
            public double getRadius() {
                return heigth;
            }
        });
    }

    @Override
    public Point3d getPosition() {
        return position;
    }

    @Override
    public List<Selection> getSelection() {
        return selections;
    }

    public double getWidth() {
        return width;
    }

    public double getRoofHeigth() {
        return roofHeigth;
    }

    public void setRoofHeigth(double roofHeigth) {
        this.roofHeigth = roofHeigth;
    }

    public double getHeigth() {
        return heigth;
    }

    public void setHeigth(double heigth) {
        this.heigth = heigth;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setWidth(double width) {
        this.width = width;
    }
}