/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;

/**
 * Interface for roof type buildier.
 * 
 * @author Tomasz Kędziora (Kendzi)
 */
public interface RoofTypeBuilder {

    public static double DEFAULT_ROOF_HEIGHT = 2.5d;

    /**
     * Build roof from given roof parameters.
     * 
     * @param pStartPoint
     *            roof starting point
     * @param wallPolygon
     *            building wall polygon
     * @param roof
     *            roof parameters
     * @param height
     *            roof height
     * @param roofMaterials
     *            roof material
     * @return builded roof
     */
    public RoofTypeOutput buildRoof(Point2d pStartPoint, PolygonWithHolesList2d wallPolygon, DormerRoofModel roof, double height,
            RoofMaterials roofMaterials);

}
