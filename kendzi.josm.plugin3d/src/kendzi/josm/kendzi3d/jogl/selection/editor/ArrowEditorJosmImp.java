package kendzi.josm.kendzi3d.jogl.selection.editor;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

public abstract class ArrowEditorJosmImp extends ArrowEditorImp implements ArrowEditorJosm {

    private long primitiveId;
    private OsmPrimitiveType primitiveType;
    private String fildName;

    public ArrowEditorJosmImp() {
        super();
    }

    public ArrowEditorJosmImp(Point3d point, Vector3d vector, double length, boolean selected, long primitiveId,
            OsmPrimitiveType primitiveType, String fildName) {
        super(point, vector, length, selected);
        this.primitiveId = primitiveId;
        this.primitiveType = primitiveType;
        this.fildName = fildName;
    }
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

    @Override
    public abstract void preview(double value);


}
