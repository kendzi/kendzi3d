/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

import javax.vecmath.Vector2d;

import org.junit.Test;


public class VectorTest {



    @Test
    public void vectorTest() {
        Vector2d v1 = new Vector2d(1,0.1);
        Vector2d v2 = new Vector2d(0,1);
        Vector2d v3 = new Vector2d(-1, 0);
        Vector2d v4 = new Vector2d(-1, -1);

        System.out.println(v1.dot(v2));
        System.out.println(v2.dot(v1));
        System.out.println(v1.dot(v1));

        System.out.println(v3.dot(v1));
        System.out.println(v1.dot(v3));

        System.out.println(v4.dot(v1));


    }

}
