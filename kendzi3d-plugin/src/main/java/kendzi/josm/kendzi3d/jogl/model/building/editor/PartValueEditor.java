package kendzi.josm.kendzi3d.jogl.model.building.editor;

import kendzi.josm.kendzi3d.jogl.model.editor.TagValueArrowEditor;
import kendzi.kendzi3d.buildings.model.BuildingPart;

import org.openstreetmap.josm.data.osm.PrimitiveId;

public abstract class PartValueEditor extends TagValueArrowEditor {

    private BuildingPart buildingPart;

    public PartValueEditor(PrimitiveId primitiveId, String fildName) {
        super(primitiveId, fildName);
    }

    /**
     * @return the buildingPart
     */
    public BuildingPart getBuildingPart() {
        return buildingPart;
    }

    /**
     * @param buildingPart
     *            the buildingPart to set
     */
    public void setBuildingPart(BuildingPart buildingPart) {
        this.buildingPart = buildingPart;
    }
}