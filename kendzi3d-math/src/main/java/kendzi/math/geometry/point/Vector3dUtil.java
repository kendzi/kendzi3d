package kendzi.math.geometry.point;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;


public class Vector3dUtil {
    public static Vector3d fromTo(Tuple3d from, Tuple3d to) {
        Vector3d v = new Vector3d(to);
        v.sub(from);
        return v;
    }

}
