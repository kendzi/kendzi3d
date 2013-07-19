package kendzi.josm.kendzi3d.jogl.model.building.builder;

import java.util.List;

import kendzi.jogl.model.geometry.Model;

/**
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingOutput {

    Model model;

    List<BuildingPartOutput> BuildingPartOutput;

    /**
     * @return the model
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * @return the buildingPartOutput
     */
    public List<BuildingPartOutput> getBuildingPartOutput() {
        return this.BuildingPartOutput;
    }

    /**
     * @param buildingPartOutput the buildingPartOutput to set
     */
    public void setBuildingPartOutput(List<BuildingPartOutput> buildingPartOutput) {
        this.BuildingPartOutput = buildingPartOutput;
    }
}
