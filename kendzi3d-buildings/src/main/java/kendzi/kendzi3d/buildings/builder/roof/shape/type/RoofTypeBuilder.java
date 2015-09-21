/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.type;

import javax.vecmath.Point2d;

import kendzi.kendzi3d.buildings.builder.dto.RoofMaterials;
import kendzi.kendzi3d.buildings.builder.dto.RoofTypeOutput;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRoofModel;
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
