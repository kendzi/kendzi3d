/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import javax.media.opengl.GL2;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import kendzi.jogl.model.factory.MaterialFactory;
import kendzi.jogl.model.factory.MeshFactory;
import kendzi.jogl.model.factory.ModelFactory;
import kendzi.jogl.model.geometry.Material;
import kendzi.jogl.model.geometry.Model;
import kendzi.jogl.model.render.ModelRender;
import kendzi.josm.kendzi3d.dto.TextureData;
import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.josm.kendzi3d.jogl.model.roof.mk.type.RoofTypeUtil;
import kendzi.josm.kendzi3d.jogl.model.tmp.AbstractWayModel;
import kendzi.math.geometry.Plane3d;
import kendzi.math.geometry.polygon.MultiPolygonList2d;
import kendzi.math.geometry.polygon.PolygonList2d;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Way;

/** Water model.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class Water extends AbstractWayModel {

    /** Log. */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Water.class);

	/**
     * Renderer of model.
     */
    private ModelRender modelRender;

    /**
     * Model of building.
     */
    private Model model;


	/** Water model.
	 *
	 * @param way way represent water
	 * @param pPerspective3D perspective
	 */
	public Water(Way way, Perspective3D pPerspective3D, ModelRender pModelRender) {
		super(way, pPerspective3D);

        this.modelRender = pModelRender;
	}

	@Override
	public void buildModel() {

	    ModelFactory model = ModelFactory.modelBuilder();
	    MeshFactory meshRoof = model.addMesh("water");

        TextureData roofTexture = new TextureData("#c=#008EFF", 1d, 1d);
        Material roofMaterial = MaterialFactory.createTextureMaterial(roofTexture.getFile());
        int roofMaterialIndex = model.addMaterial(roofMaterial);

        meshRoof.materialID = roofMaterialIndex;
        meshRoof.hasTexture = true;

        Vector3d nt = new Vector3d(0, 1, 0);

		Point3d planeRightTopPoint =  new Point3d(
	             0 ,
	             0.05,
	             0);

        MultiPolygonList2d topMP = new MultiPolygonList2d(new PolygonList2d(this.points));

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

	    pGl.glPushMatrix();
        pGl.glTranslated(this.getGlobalX(), 0, -this.getGlobalY());

        //pGl.glColor3f((float) 188 / 255, (float) 169 / 255, (float) 169 / 255);

        try {
            this.modelRender.render(pGl, this.model);

        } finally {
            pGl.glPopMatrix();
        }
	}

}
