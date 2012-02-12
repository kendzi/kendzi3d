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
import java.util.Map;

import javax.vecmath.Point2d;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.DormerRoofBuilder;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.Parser;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.alias.RoofTypeAliasEnum;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Represent flat roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class ShapeRoof extends DormerRoof {

    /** Log. */
    private static final Logger log = Logger.getLogger(ShapeRoof.class);

    /** Flat Roof.
     * @param pBuilding building
     * @param pList list of building walls
     * @param pWay way
     * @param pPerspective perspective
     */
    public ShapeRoof(Building pBuilding, List<Point2d> pList, Way pWay, Perspective3D pPerspective, ModelRender pModelRender) {
        super(pBuilding, pList, pWay, pPerspective, pModelRender);
    }




    @Override
    public void buildModel() {

        Map<String, String> keys = this.way.getKeys();


        DormerRoofModel roof = parseDormerRoof(keys);

        String shapeName = keys.get("building:roof:shape");
        if (shapeName == null) {
            shapeName = keys.get("roof:shape");
        }

        RoofTypeAliasEnum shape = Parser.parseRoofShape(shapeName);
        if (shape == null) {
            shape = RoofTypeAliasEnum.FLAT;
        }

        roof.setRoofType(Parser.parseRoofType(shape.getKey()));

        RoofTextureData rtd = new RoofTextureData();
        rtd.setFacadeTextrure(getFasadeTexture());
        rtd.setRoofTexture(getRoofTexture());


        RoofOutput roofOutput = DormerRoofBuilder.build(roof, this.height, rtd);

        this.debug = roofOutput.getDebug();

        this.minHeight = this.height - roofOutput.getHeight();
        this.model = roofOutput.getModel();

    }
}
