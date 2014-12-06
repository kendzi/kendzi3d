package kendzi.kendzi3d.editor.selection;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.util.MeshTriangleUtil;
import kendzi.math.geometry.intersection.IntersectionUtil;
import kendzi.math.geometry.ray.Ray3d;

/**
 * Implementation of selectable model.
 */
public abstract class ModelSelection extends SphereSelection {

    /**
     * Constructor.
     *
     * @param center
     *            center for intersection prediction
     * @param radius
     *            radius for intersection prediction
     */
    public ModelSelection(Point3d center, double radius) {
        super(center, radius);
    }

    /**
     * Source of model.
     *
     * @return model
     */
    public abstract Model getModel();

    @Override
    public Double intersect(Ray3d ray) {

        Model model = getModel();

        Point3d point = ray.getPoint();
        Vector3d vector = new Vector3d(ray.getVector());
        vector.normalize();

        double minDistance = Double.MAX_VALUE;

        for (Mesh mesh : model.mesh) {
            List<Point3d> triangles = MeshTriangleUtil.toTriangles(mesh);

            for (int i = 0; i < triangles.size(); i = i + 3) {
                Point3d v0 = triangles.get(i);
                Point3d v1 = triangles.get(i + 1);
                Point3d v2 = triangles.get(i + 2);

                Double distance = IntersectionUtil.rayIntersectsTriangleDistance(point, vector, v0, v1, v2);
                if (distance != null && distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        if (minDistance == Double.MAX_VALUE) {
            return null;
        }
        return minDistance;
    }
}
