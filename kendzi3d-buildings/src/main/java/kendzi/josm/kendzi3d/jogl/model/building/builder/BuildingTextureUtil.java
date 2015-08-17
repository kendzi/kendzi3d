package kendzi.josm.kendzi3d.jogl.model.building.builder;

import kendzi.jogl.texture.dto.TextureData;
import kendzi.jogl.texture.library.BuildingElementsTextureManager;
import kendzi.jogl.texture.library.TextureFindCriteria;
import kendzi.jogl.texture.library.TextureFindCriteria.Type;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingModel;
import kendzi.josm.kendzi3d.jogl.model.building.model.BuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.NodeBuildingPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.Wall;
import kendzi.josm.kendzi3d.jogl.model.building.model.WallPart;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.BuildingNodeElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.EntranceBuildingElement;
import kendzi.josm.kendzi3d.jogl.model.building.model.element.WindowBuildingElement;

public class BuildingTextureUtil {
    public static TextureData takeWindowsColumnsTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w,
            WallPart wp, BuildingElementsTextureManager tm) {

        String mt = null;

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.WINDOWS, mt, null, null, null, false));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    public static TextureData takeFacadeTextureData(BuildingModel buildingModel, NodeBuildingPart bp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (bp.getFacadeMaterialType() != null) {
            mt = bp.getFacadeMaterialType();
        } else if (buildingModel.getFacadeMaterialType() != null) {
            mt = buildingModel.getFacadeMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FACADE, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    public static TextureData takeFacadeTextureData(BuildingModel buildingModel, BuildingPart bp, Wall w, WallPart wp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (wp != null && wp.getFacadeMaterialType() != null) {
            mt = wp.getFacadeMaterialType();
        } else if (w.getFacadeMaterialType() != null) {
            mt = w.getFacadeMaterialType();
        } else if (bp.getFacadeMaterialType() != null) {
            mt = bp.getFacadeMaterialType();
        } else if (buildingModel.getFacadeMaterialType() != null) {
            mt = buildingModel.getFacadeMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FACADE, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    public static TextureData takeFloorTextureData(BuildingModel buildingModel, BuildingPart bp,
            BuildingElementsTextureManager tm, boolean colorable) {

        String mt = null;

        if (bp.getFloorMaterialType() != null) {
            mt = bp.getFloorMaterialType();
        } else if (buildingModel.getFloorMaterialType() != null) {
            mt = buildingModel.getFloorMaterialType();
        }

        TextureData td = tm.findTexture(new TextureFindCriteria(Type.FLOOR, mt, null, null, null, colorable));

        if (td == null) {
            td = new TextureData(null, 1, 1);
        }

        return td;
    }

    public static TextureData findWindowTextureData(BuildingNodeElement be,
            BuildingElementsTextureManager pTextureMenager) {

        TextureData td = null;
        if (be instanceof WindowBuildingElement) {
            WindowBuildingElement wbe = (WindowBuildingElement) be;

            td = pTextureMenager.findTexture(new TextureFindCriteria(Type.WINDOW, wbe.getWindowType(), null, wbe
                    .getWidth(), wbe.getHeight(), false));
        } else if (be instanceof EntranceBuildingElement) {
            EntranceBuildingElement wbe = (EntranceBuildingElement) be;

            td = pTextureMenager.findTexture(new TextureFindCriteria(Type.ENTERENCE, wbe.getEntranceType(), null, wbe
                    .getWidth(), wbe.getHeight(), false));
        } else {
            throw new RuntimeException("unsuported buidlding element " + be);
        }

        if (td == null) {
            td = createEmptyTextureData();
        }
        return td;
    }

    private static TextureData createEmptyTextureData() {
        return new TextureData(null, 1, 1);
    }
}
