/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;
import kendzi.math.geometry.Triangulate;

import org.openstreetmap.josm.data.osm.Way;

/**
 * Represent flat roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class FlatRoof extends Roof {



    /**
     * List of roof triangles.
     * FIXME use model.
     */
    private List<Point2d> roofList;



    /** Flat Roof.
     * @param pBuilding building
     * @param pList list of building walls
     * @param pWay way
     * @param pPerspective perspective
     */
    public FlatRoof(Building pBuilding, List<Point2d> pList, Way pWay, Perspective3D pPerspective) {
        super(pBuilding, pList, pWay, pPerspective);


        //
        //        list = new ArrayList<Point2D.Double>();
        //
        //        for (int i = 0; i < pWay.getNodesCount(); i++) {
        //            Node node = pWay.getNode(i);
        //
        //            double x = pPerspective.calcX(node.getEastNorth().getX());
        //            double y = pPerspective.calcY(node.getEastNorth().getY());
        //
        //            list.add(new Point2D.Double(x, y));
        //        }

        //        height = ModelUtil.getHeight(pWay, 1);
        this.height = pBuilding.getHeight();
    }




    @Override
    public void buildModel() {

        //FIXME use model.
        this.roofList = new ArrayList<Point2d>();


        // pointList.remove(pointList.size()-1);
        List<Point2d> cleanPointList = Triangulate.removeClosePoints(this.list);
        this.roofList = Triangulate.process(cleanPointList);


    }


    @Override
    public void draw(GL2 pGl, Camera pCamera) {

        //FIXME use model.
        pGl.glBegin(GL2.GL_TRIANGLES);
//         draw building roof

        pGl.glColor3f((float) 147 / 255, (float) 123 / 255, (float) 89 / 255);
//        gl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);


        for (Point2d p : this.roofList) {

            pGl.glVertex3d(p.getX(), this.height, -p.getY());
        }
        pGl.glEnd();
    }

}
