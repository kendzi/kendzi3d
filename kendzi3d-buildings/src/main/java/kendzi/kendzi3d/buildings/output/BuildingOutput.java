/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */
package kendzi.kendzi3d.buildings.output;

import java.util.List;

import kendzi.jogl.model.geometry.Model;

/**
 * Building model build by builder. Contains additional debug information.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public class BuildingOutput {

    /**
     * Building output model.
     */
    private Model model;

    /**
     * Debug information for all building parts.
     */
    private List<BuildingPartOutput> buildingPartOutput;

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
        return this.buildingPartOutput;
    }

    /**
     * @param buildingPartOutput the buildingPartOutput to set
     */
    public void setBuildingPartOutput(List<BuildingPartOutput> buildingPartOutput) {
        this.buildingPartOutput = buildingPartOutput;
    }
}
