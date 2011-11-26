/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof.mk.type;

import java.util.List;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTextureData;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.RoofTypeOutput;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.model.DormerRoofModel;

public interface RoofType {

    public String getPrefixKey();

    public boolean isPrefixParameter();

    public RoofTypeOutput buildRoof(Point2d pStartPoint, List<Point2d> pPolygon, DormerRoofModel pRoof, double height, RoofTextureData pRoofTextureData);
}
