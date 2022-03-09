/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public class TriangulateTest {

    public static void main(String[] args) {
        Triangulate t = new Triangulate();

        List<Vector2dc> pContour = new ArrayList<>();
        pContour.add(new Vector2d(1.895497121847469, -43.471537857796115));
        pContour.add(new Vector2d(-0.9242217038848146, -42.12385244107362));
        pContour.add(new Vector2d(-3.743943196278786, -40.77616574981941));
        pContour.add(new Vector2d(2.2851311803030017, -28.168042068609704));

        List<Integer> processIndex = t.processIndex(pContour);

    }

}
