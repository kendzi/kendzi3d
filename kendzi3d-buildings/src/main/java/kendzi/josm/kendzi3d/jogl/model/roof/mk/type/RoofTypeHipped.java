/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofMaterials;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;
import kendzi.math.geometry.RectangleUtil;
import kendzi.math.geometry.polygon.PolygonUtil;
import kendzi.math.geometry.polygon.PolygonWithHolesList2d;
import kendzi.math.geometry.rectangle.RectanglePointVector2d;

/**
 * Roof type hipped automated chose between complex and square like roof shape.
 * 
 * @author Tomasz Kędziora (Kendzi)
 * 
 */
public class RoofTypeHipped extends AbstractRoofTypeBuilder {

    @Override
    public RoofTypeOutput buildRoof(Point2d startPoint, PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof,
            double height, RoofMaterials roofTextureData) {

        if (isComplex(buildingPolygon, roof)) {
            return new RoofType9v0().buildRoof(startPoint, buildingPolygon, roof, height, roofTextureData);
        }

        return new RoofType2v4().buildRoof(startPoint, buildingPolygon, roof, height, roofTextureData);
    }

    private boolean isComplex(PolygonWithHolesList2d buildingPolygon, DormerRoofModel roof) {

        if (roof.getDirection() != null) {
            return false;
        }

        if (roof.getOrientation() != null) {
            return false;
        }

        if (buildingPolygon.getInner() != null && buildingPolygon.getInner().size() > 0) {
            // has any holes
            return false;
        }

        List<Point2d> points = buildingPolygon.getOuter().getPoints();

        RectanglePointVector2d orientedBBox = RectangleUtil.findRectangleContur(points);
        double orientedBBoxArea = orientedBBox.getHeight() * orientedBBox.getWidth();

        float polygonArea = Math.abs(PolygonUtil.area(points));

        return orientedBBoxArea > polygonArea * 1.2;
    }

}
