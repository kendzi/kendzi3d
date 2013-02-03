package kendzi.math.geometry.ray;


import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.math.geometry.Sphere3d;

import org.junit.Test;

import static org.junit.Assert.*;


public class Ray3dUtilTest {

    @Test
    public void intersection1() {
        Ray3d r = new Ray3d(new Point3d(), new Vector3d(1,0,0));
        Sphere3d s = new Sphere3d(new Point3d(2,0,0), 1);

        Double intersect = Ray3dUtil.intersect(r, s);

        assertNotNull(intersect);
    }

    @Test
    public void intersection2() {
        Ray3d r = new Ray3d(new Point3d(), new Vector3d(1,0,0));
        Sphere3d s = new Sphere3d(new Point3d(-2,0,0), 1);

        Double intersect = Ray3dUtil.intersect(r, s);

        assertNull(intersect);
    }

    @Test
    public void intersection3() {
        Ray3d r = new Ray3d(new Point3d(-2,0,0), new Vector3d(1,0,0));
        Sphere3d s = new Sphere3d(new Point3d(1,0,0), 1);

        Double intersect = Ray3dUtil.intersect(r, s);
        System.out.println(intersect);
        assertNotNull(intersect);
    }

}
