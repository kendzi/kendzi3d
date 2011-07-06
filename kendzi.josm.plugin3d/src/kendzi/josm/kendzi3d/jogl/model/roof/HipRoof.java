/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model.roof;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;

import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.Building;
import kendzi.josm.kendzi3d.jogl.model.Perspective3D;

import org.openstreetmap.josm.data.osm.Way;

/**
 * Represent flat roof.
 *
 * @author Tomasz Kedziora (Kendzi)
 */
public class HipRoof extends Roof {



    /**
     * List of roof triangles.
     * FIXME use model.
     */
    private ArrayList<Double> roofList;


    ModelRender modelRender;


    /** Flat Roof.
     * @param pBuilding building
     * @param pList list of building walls
     * @param pWay way
     * @param pPerspective perspective
     */
    public HipRoof(Building pBuilding, List<Point2d> pList, Way pWay, Perspective3D pPerspective) {
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

        this.modelRender = ModelRender.getInstance();
    }




    @Override
    public void buildModel() {
//
//        List<Double> lista = this.list;
//
//        if (!(0.0f < Triangulate.area(this.list))) {
//            lista = new ArrayList<Point2D.Double>();
//
//            for (int i = this.list.size() -1; i >= 0 ; i--) {
//                lista.add(this.list.get(i));
//            }
//        }
//
//
//        ModelFactory modelBuilder = ModelFactory.modelBuilder();
//        MeshFactory meshRoof = modelBuilder.addMesh("roof_top");
//        int mi2 = modelBuilder.addMaterial(MaterialFactory.emptyMaterial());
//
//
//
//
//
//        final LoopL<Edge> out = new LoopL();
//
//        // controls the gradient of the edge
//        Machine machine = new Machine (Math.PI/4);
//        {
//            Loop<Edge> loop = new Loop();
//            out.add(loop);
//
////            for ( Bar bar : lb )
//            for (int i =0; i < lista.size()-1; i++)        {
//                Double p1 = lista.get(i);
//                Double p2 = lista.get(i+1);
//
//                // 3D representation of 2D ui input
//                Edge e = new Edge(
//                        new Point3d( p1.x, p1.y, 0 ),
//                        new Point3d( p2.x, p2.y, 0 ),
//                        Math.PI / 4 );
//
//                e.machine = machine;
//
//                loop.append( e );
//            }
//
//            // the points defining the start and end of a loop must be the same object
//            for ( Loopable<Edge> le : loop.loopableIterator() )
//                le.get().end = le.getNext().get().start;
//        }
//            //DebugDevice.reset();
//            Skeleton skeleton = new Skeleton(out, true);
//            skeleton.skeleton();
//            Output output = skeleton.output;
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            int faceIndex = 0;
//
//            for ( Face face : output.faces.values() )
//            {
//                LoopL<Point3d> loopl = face.getLoopL();
//                int loopIndex = 0;
//                faceIndex++;
//
//
//                FaceFactory face2 = meshRoof.addFace(FaceType.TRIANGLE_FAN);
//
//                /**
//                 * First loop is the outer. Most skeleton faces will only have this.
//                 * Second+ loops are the holes in the face (if you need this, you're
//                 * a long way down a rabbit hole)
//                 */
//                for (Loop<Point3d> loop : loopl ) {
//
//
//                    Map<Point2d, Point3d> pointsMap = new HashMap<Point2d, Point3d>();
//                    List<Point2d> facePoints = new ArrayList<Point2d>();
//                    List<Point3d> loopPoints = new ArrayList<Point3d>();
//                    for ( Point3d p : loop ) {
//                        Point2d p2 = new Point2d(p.x, p.y);
//                        pointsMap.put(p2, p);
//
//                        facePoints.add(p2);
//
//                        loopPoints.add(p);
//
//                    }
//
////                    this.roofList = new ArrayList<Point2D.Double>();
//                    //
//                            Triangulate t = new Triangulate();
////                            List<Double> cleanPointList= new ;
//                            // pointList.remove(pointList.size()-1);
////                            List<Point2D.Double> cleanPointList = t.removeClosePoints(this.list);
//                            List<Integer> processIndex = t.processIndex(facePoints);
//
//                            for ( Integer i : processIndex) {
//                                Point3d point3d = loopPoints.get(i);
//
//                                int addVertex = meshRoof.addVertex(new Point3d(point3d.x, point3d.z, point3d.y));
//                                if ( loopIndex == 0 ) {
//                                    face2.addVertIndex(addVertex);
//                                } else {
//                                    System.err.println("123");
//                                }
//
//
//                            }
//
//
//
//                    loopIndex++;
//                }
//            }
//
//            meshRoof.materialID = mi2;
//            meshRoof.hasTexture = false;
//
//            this.model = modelBuilder.toModel();
//            this.model.useTexture = false;
//            this.model.useLigth = true;
//            this.model.drawVertex = true;
//            this.model.drawEdges = true;
//            this.model.drawNormals = true;
    }


    @Override
    public void draw(GL2 pGl, Camera pCamera) {

//        //FIXME use model.
//        pGl.glBegin(GL2.GL_TRIANGLES);
////         draw building roof
//
//        pGl.glColor3f((float) 147 / 255, (float) 123 / 255, (float) 89 / 255);
////        gl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);
//
//
//        for (Point2D p : this.roofList) {
//
//            pGl.glVertex3d(p.getX(), this.height, p.getY());
//        }
//        pGl.glEnd();
        this.modelRender.render(pGl, this.model);
    }

}
