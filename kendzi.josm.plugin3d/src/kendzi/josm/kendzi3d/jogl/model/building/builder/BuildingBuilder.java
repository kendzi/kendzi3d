package kendzi.josm.kendzi3d.jogl.model.building.builder;

import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Model;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;

public class BuildingBuilder {
    Model buildModel(BuildingModel buildingModel) {

        ModelFactory mb = ModelFactory.modelBuilder();

        if (buildingModel.getParts() != null) {

            for (BuildingPart bp : buildingModel.getParts()) {
                buildPart(bp, mb);
             }
        } else {
            buildPart(buildingModel.getOutline(), mb);
        }
        return null;
    }


    private void buildPart(BuildingPart bp, ModelFactory mb) {
        MeshFactory mf = mb.addMesh();

        for (WallPart wp : bp.getWallParts()) {
            buildWallPart(wp, bp, mf);
        }
//        BuildingModelUtil.WallPartToOutline(wallParts)

    }

    private void buildWallPart(WallPart wp, BuildingPart bp, MeshFactory mf) {


    }
}
