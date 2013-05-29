package kendzi.josm.kendzi3d.jogl.selection.editor;

import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

public class ArrowEditorJosmImp extends ArrowEditorImp implements ArrowEditorJosm {

    private long primitiveId;
    private OsmPrimitiveType primitiveType;
    private String fildName;

    /**
     * @return the osmPrimitiveType
     */
    @Override
    public OsmPrimitiveType getPrimitiveType() {
        return this.primitiveType;
    }
    /**
     * @param osmPrimitiveType the osmPrimitiveType to set
     */
    public void setPrimitiveType(OsmPrimitiveType osmPrimitiveType) {
        this.primitiveType = osmPrimitiveType;
    }
    /**
     * @return the fildName
     */
    @Override
    public String getFildName() {
        return this.fildName;
    }
    /**
     * @param fildName the fildName to set
     */
    public void setFildName(String fildName) {
        this.fildName = fildName;
    }
    @Override
    public long getPrimitiveId() {
        return this.primitiveId;
    }



    @Override
    public void setValue(double value) {
        setLength(value);
    }

    /**
     * @param primitiveId the primitiveId to set
     */
    public void setPrimitiveId(long primitiveId) {
        this.primitiveId = primitiveId;
    }



}
