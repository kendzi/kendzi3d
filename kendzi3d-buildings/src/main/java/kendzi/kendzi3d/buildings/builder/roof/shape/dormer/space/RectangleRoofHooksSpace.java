/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.kendzi3d.buildings.builder.roof.shape.dormer.space;

import kendzi.kendzi3d.buildings.builder.roof.shape.dormer.RoofHookPoint;
import kendzi.kendzi3d.buildings.model.roof.shape.DormerRow;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.point.TransformationMatrix2d;
import kendzi.math.geometry.point.TransformationMatrix3d;
import org.ejml.simple.SimpleMatrix;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RectangleRoofHooksSpace implements RoofHooksSpace {
    Vector2dc p1;
    Vector2dc v1;
    double b;
    SimpleMatrix transformationMatrix;
    // FIXME

    Plane3d plane;

    public RectangleRoofHooksSpace(Vector2dc p1, Vector2dc v1, double b, Plane3d pPlane) {
        super();
        // this.p1 = p1;
        // this.v1 = v1;
        this.b = b;

        double angle = Math.atan2(v1.y(), v1.x());
        // Math.toDegrees(angle);
        SimpleMatrix tr2d = TransformationMatrix2d.rotZA(-angle).mult(TransformationMatrix2d.tranA(-p1.x(), -p1.y()));
        SimpleMatrix tr3d = TransformationMatrix3d.rotYA(-angle).mult(TransformationMatrix3d.tranA(-p1.x(), 0, p1.y()));

        this.p1 = TransformationMatrix2d.transform(p1, tr2d, true);
        this.v1 = TransformationMatrix2d.transform(v1, tr2d, false);

        Vector3dc planePoint = TransformationMatrix3d.transform(pPlane.getPoint(), tr3d, true);
        Vector3dc planeNormal = TransformationMatrix3d.transform(pPlane.getNormal(), tr3d, false);

        this.plane = new Plane3d(planePoint, planeNormal);

        SimpleMatrix trBack = TransformationMatrix3d.rotYA(angle).mult(TransformationMatrix3d.tranA(-p1.x(), 0, p1.y()));

        this.transformationMatrix = trBack;
    }

    @Override
    public RoofHookPoint[] getRoofHookPoints(int pNumber, DormerRow dormerRow, int dormerRowNum) {

        Vector2dc v = new Vector2d(this.v1).div(pNumber + 1d);

        Vector2d p = new Vector2d(this.p1);

        RoofHookPoint[] ret = new RoofHookPoint[pNumber];
        for (int i = 0; i < pNumber; i++) {
            p.add(v);

            double y = this.plane.calcYOfPlane(p.x(), -p.y());

            double z = limitZToPolygon(p.y());

            Vector3dc pp = new Vector3d(p.x(), y, -z);

            RoofHookPoint hook = new RoofHookPoint(pp, Math.toRadians(0), this.b * 2d / 3d, Math.toRadians(0));

            ret[i] = hook;
        }

        return ret;

    }

    /**
     * @param y
     * @return
     */
    private double limitZToPolygon(double y) {

        return y;
    }

    @Override
    public SimpleMatrix getTransformationMatrix() {
        return this.transformationMatrix;
    }

}
