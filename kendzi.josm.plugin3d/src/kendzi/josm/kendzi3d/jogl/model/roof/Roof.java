/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof;

import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.AbstractModel;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.TextureData;
import kendzi.josm.kendzi3d.service.MetadataCacheService;
import kendzi.josm.kendzi3d.util.StringUtil;

import org.openstreetmap.josm.data.osm.Way;

public abstract class Roof extends AbstractModel {

    private static final String BUILDING_ROOF_RIDGE = "building:roof:ridge";
    private static final String BUILDING_ROOF_ORIENTATION_ACROSS = "across";
    private static final String BUILDING_ROOF_ORIENTATION_ALONG = "along";
    private static final String BUILDING_ROOF_ORIENTATION = "building:roof:orientation";
    private static final double EPSILON = 1E-10;
    private static final double EPSILON_SQRT = EPSILON * EPSILON;
    protected double height;
    protected Building building;
    protected List<Point2d> list;

    ModelRender modelRender;
    kendzi.jogl.model.geometry.Model model;

    protected Vector3d [] wallNormals;
    protected Way way;
    protected double minHeight;
    //    private Perspective3D pers;

    /** Create model of XXX roof.
     * @param pBuilding building of roof
     * @param pList list of wall points
     * @param pWay way which define building
     * @param pPers 3d perspective
     */
    public Roof(Building pBuilding, List<Point2d> pList, Way pWay,
            Perspective3D pPers) {
        super(pWay, pPers);

        this.building = pBuilding;
        this.list = pList;
        this.way = pWay;

        this.height = pBuilding.getHeight();
        this.minHeight = this.height;

        this.modelRender = ModelRender.getInstance();
    }

    /** Set walls normal vectors.
     * @param pWallNormals walls normal vectors
     */
    public void setWallNormals(Vector3d [] pWallNormals) {
        this.wallNormals = pWallNormals;
    }

    /**
     * Build model of the roof.
     */
    @Override
    public abstract void buildModel();



    /** Height of roof.
     * @return height of roof
     */
    public double getHeight() {
        return this.height;
    }

    /** Minimal height of roof.
     * @return minimal height of roof
     */
    public double getMinHeight() {
        return this.minHeight;
    }

    /**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.Model#draw(javax.media.opengl.GL2, kendzi.josm.kendzi3d.jogl.Camera)
     */
    @Override
    public void draw(GL2 pGl, Camera pCamera) {
        this.modelRender.render(pGl, this.model);
    }


    /** Get roof texture.
     * @return roof texture
     */
    protected TextureData getRoofTexture() {

        String roofMaterial = this.way.get("building:roof:material");

        String roofColor = this.way.get("building:roof:color");
        if (StringUtil.isBlankOrNull(roofColor)) {
            roofColor = this.way.get("roof:color");
        }
        if (StringUtil.isBlankOrNull(roofColor)) {
            roofColor = this.way.get("roof:colour");
        }

        if (!StringUtil.isBlankOrNull(roofMaterial) || StringUtil.isBlankOrNull(roofColor)) {

            String facadeTextureFile = MetadataCacheService.getPropertites(
                    "buildings.building_roof_material_{0}.texture.file", null, roofMaterial);

            double facadeTextureLenght = MetadataCacheService.getPropertitesDouble(
                    "buildings.building_roof_material_{0}.texture.lenght", 1d, roofMaterial);
            double facadeTextureHeight = MetadataCacheService.getPropertitesDouble(
                    "buildings.building_roof_material_{0}.texture.height", 1d, roofMaterial);

            return new TextureData(facadeTextureFile, facadeTextureLenght, facadeTextureHeight);

        } else {

            String facadeColorFile = "#c=" + roofColor;
            return new TextureData(facadeColorFile, 1d, 1d);
        }
    }

    protected TextureData getFasadeTexture() {
        return this.building.getFacadeTexture();
    }



}
