package kendzi.kendzi3d.editor.selection;

import java.util.List;

import kendzi.jogl.model.geometry.Mesh;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.util.MeshTriangleUtil;
import kendzi.math.geometry.intersection.IntersectionUtil;
import kendzi.math.geometry.ray.Ray3d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

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
    public ModelSelection(Vector3dc center, double radius) {
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

        Vector3dc point = ray.getPoint();
        Vector3dc vector = new Vector3d(ray.getVector()).normalize();

        double minDistance = Double.MAX_VALUE;

        for (Mesh mesh : model.mesh) {
            List<Vector3dc> triangles = MeshTriangleUtil.toTriangles(mesh);

            for (int i = 0; i < triangles.size(); i = i + 3) {
                Vector3dc v0 = triangles.get(i);
                Vector3dc v1 = triangles.get(i + 1);
                Vector3dc v2 = triangles.get(i + 2);

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
