/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeUtil;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Way;

public class Water extends AbstractModel {

    /** Log. */
    private static final Logger log = Logger.getLogger(Water.class);

	List<Point2d> list;
	private Way way;
	private List<Point2d> waterList;

	/**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Model of building.
     */
    private Model model;


	public Water(Way way, Perspective3D pres) {
		super(way, pres);
		this.way = way;


        this.modelRender = ModelRender.getInstance();
	}

	@Override
	public void buildModel() {

	    ModelFactory model = ModelFactory.modelBuilder();
	    MeshFactory meshRoof = model.addMesh("weater");

	       //XXX move it
	       TextureData roofTexture = new TextureData("#c=#008EFF", 1d, 1d);
//	       Material facadeMaterial = MaterialFactory.createTextureMaterial(facadeTexture.getFile());
	       Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
	       // XXX move material
//	       int facadeMaterialIndex = model.addMaterial(facadeMaterial);
	       int roofMaterialIndex = model.addMaterial(roofMaterial);
//
//	       meshBorder.materialID = facadeMaterialIndex;
//	       meshBorder.hasTexture = true;

	       meshRoof.materialID = roofMaterialIndex;
	       meshRoof.hasTexture = true;

		List<Point2d> pointList = new ArrayList<Point2d>();
		for (int i = 0; i < this.way.getNodesCount(); i++) {

			Point2d p = this.perspective.calcPoint(this.way.getNode(i));

			pointList.add(p);
		}

		this.list = pointList;
//
//
//
//		List<Point2d> cleanPointList = Triangulate.removeClosePoints(pointList);
//		waterList = Triangulate.process(cleanPointList);
//
//
//		this.buildModel = true;
		 Vector3d nt = new Vector3d(0, 1  , 0);

		Point3d planeRightTopPoint =  new Point3d(
	             0 ,
	             0.05,
	             0);

	       MultiPolygonList2d topMP = new MultiPolygonList2d(new PolygonList2d(this.list));

	       Plane3d planeTop = new Plane3d(
	               planeRightTopPoint,
	               nt);

	       Vector3d roofTopLineVector = new Vector3d(
	               -1,
	               0,
	               0);



	    RoofTypeUtil.addPolygonToRoofMesh(meshRoof, topMP, planeTop, roofTopLineVector, roofTexture);

	    this.model = model.toModel();
        this.model.setUseLight(true);
        this.model.setUseTexture(true);

        this.buildModel = true;
	}

	@Override
	public void draw(GL2 pGl, Camera camera) {
//		gl.glDisable(GL2.GL_LIGHTING);
//
//		// gl.glColor3b((byte)188, (byte)169, (byte)169);
//		gl.glColor3f((float) 0 / 255, (float) 142 / 255, (float) 255 / 255);
//		gl.glBegin(GL2.GL_POLYGON);
//
//		for (Point2d p : this.list) {
//
//			gl.glVertex3d(p.getX(), 0.05, -p.getY());
//		}
//		gl.glEnd();


        this.modelRender.render(pGl, this.model);
	}

}
