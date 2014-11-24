/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.josm.kendzi3d.jogl.model.editor;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;

import kendzi.josm.kendzi3d.jogl.model.building.Building;
import kendzi.kendzi3d.editor.selection.editor.ArrowEditorImp;
import kendzi.kendzi3d.editor.selection.event.ArrowEditorChangeEvent;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.PrimitiveId;

public abstract class TagValueArrowEditor extends ArrowEditorImp {

    /** Log. */
    private static final Logger LOG = Logger.getLogger(Building.class);

    private PrimitiveId primitiveId;

    private String fildName;

    public TagValueArrowEditor(PrimitiveId primitiveId, String fildName) {
        super();
        this.primitiveId = primitiveId;
        this.fildName = fildName;

    }

    /**
     * @return the fildName
     */
    public String getFildName() {
        return fildName;
    }

    /**
     * @param fildName
     *            the fildName to set
     */
    public void setFildName(String fildName) {
        this.fildName = fildName;
    }

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

    public abstract void preview(double value);

    @Override
    protected void raiseEditorChange(ArrowEditorChangeEvent event) {
        super.raiseEditorChange(event);

        preview(event.getLength());

        if (event.isEnd()) {
            saveEvent(event);
        }
    }

    protected void saveEvent(ArrowEditorChangeEvent event) {

        double length = event.getLength();

        AbstractMap<String, String> tags = new HashMap<>();
        DecimalFormat formater = new DecimalFormat("#0.00");
        String value = formater.format(length);
        // save new editor value
        tags.put(fildName, value);

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

    protected void saveTagToPrimitive(AbstractMap<String, String> tags) {

        DataSet dataSet = Main.main.getCurrentDataSet();

        OsmPrimitive primitive = dataSet.getPrimitiveById(primitiveId);

        Main.main.undoRedo.add(new ChangePropertyCommand(Arrays.asList(primitive), tags));
        LOG.info("primitive value was saved, id: " + primitiveId);
    }

    protected void saveTagToPrimitive(double length, String fildName) {

        DecimalFormat formater = new DecimalFormat("#0.00");
        String value = formater.format(length);

        DataSet dataSet = Main.main.getCurrentDataSet();

        OsmPrimitive primitive = dataSet.getPrimitiveById(primitiveId);

        Main.main.undoRedo.add(new ChangePropertyCommand(primitive, fildName, value));
        LOG.info("primitive value was saved, id: " + primitiveId);
    }

}