/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.josm.kendzi3d.jogl.model.editor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveId;

import kendzi.josm.kendzi3d.jogl.model.building.Building;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditorImp;
import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;

/**
 * The simple arrow like editor for primitives.
 */
public abstract class TagValueArrowEditor extends ArrowEditorImp {

    /** Log. */
    private static final Logger LOG = Logger.getLogger(Building.class);

    private PrimitiveId primitiveId;

    private String fieldName;

    /**
     * Instantiates a new tag value arrow editor.
     *
     * @param primitiveId
     *            the primitive id
     * @param fieldName
     *            the field name which should be modified
     */
    public TagValueArrowEditor(PrimitiveId primitiveId, String fieldName) {
        super();
        this.primitiveId = primitiveId;
        this.fieldName = fieldName;

    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the primitive id
     */
    public PrimitiveId getPrimitiveId() {
        return primitiveId;
    }

    /**
     * @param primitiveId
     *            the primitiveId to set
     */
    public void setPrimitiveId(PrimitiveId primitiveId) {
        this.primitiveId = primitiveId;
    }

    /**
     * Generate preview for current editor state.
     *
     * @param value
     *            the value
     */
    public abstract void preview(double value);

    @Override
    protected void raiseEditorChange(ArrowEditorChangeEvent event) {
        super.raiseEditorChange(event);

        preview(event.getLength());

        if (event.isEnd()) {
            saveEvent(event);
        }
    }

    /**
     * Save event.
     *
     * @param event
     *            the event to save
     */
    protected void saveEvent(ArrowEditorChangeEvent event) {

        double length = event.getLength();

        AbstractMap<String, String> tags = new HashMap<>();
        DecimalFormat formater = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        String value = formater.format(length);
        // save new editor value
        tags.put(fieldName, value);

        // save additional required tags values
        updateTags(tags);

        saveTagToPrimitive(tags);
    }

    /**
     * Is some additional tags have to be updated when editor value is saved.
     *
     * @param tags
     *            tags to save
     */
    protected void updateTags(AbstractMap<String, String> tags) {
        //
    }

    /**
     * Saves tags to primitive.
     *
     * @param tags
     *            the tags to save
     */
    protected void saveTagToPrimitive(AbstractMap<String, String> tags) {

        DataSet dataSet = Main.getLayerManager().getEditDataSet();

        OsmPrimitive primitive = dataSet.getPrimitiveById(primitiveId);

        Main.main.undoRedo.add(new ChangePropertyCommand(Arrays.asList(primitive), tags));
        LOG.info("primitive value was saved, id: " + primitiveId);
    }
}