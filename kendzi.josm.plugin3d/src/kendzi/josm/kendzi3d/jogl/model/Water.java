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

import kendzi.josm.kendzi3d.jogl.Camera;
import kendzi.math.geometry.Triangulate;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

public class Water extends AbstractModel {

    /** Log. */
    private static final Logger log = Logger.getLogger(Water.class);

	List<Point2d> list;
	private Way way;
	private ArrayList<Point2d> waterList;

	public Water(Way way, Perspective3D pres) {
		super(way, pres);
		this.way = way;
	}

	@Override
	public void buildModel() {

		List<Point2d> pointList = new ArrayList<Point2d>();
		for (int i = 0; i < way.getNodesCount(); i++) {
			Node node = way.getNode(i);

			double x = perspective.calcX(node.getEastNorth().getX());
			double y = perspective.calcY(node.getEastNorth().getY());

			// node.getCoor()
			pointList.add(new Point2d(x, y));
			log.info("d x: " + x + " y: " + y);
		}

		this.list = pointList;

		waterList = new ArrayList<Point2d>();

		Triangulate t = new Triangulate();
		List<Point2d> cleanPointList = t.removeClosePoints(pointList);
		t.process(cleanPointList, waterList);


		this.buildModel = true;
	}

	@Override
	public void draw(GL2 gl, Camera camera) {
		gl.glDisable(GL2.GL_LIGHTING);

		// gl.glColor3b((byte)188, (byte)169, (byte)169);
		gl.glColor3f((float) 0 / 255, (float) 142 / 255, (float) 255 / 255);
		gl.glBegin(GL2.GL_POLYGON);

		for (Point2d p : list) {

			gl.glVertex3d(p.getX(), 0.05, -p.getY());
		}
		gl.glEnd();
	}

}
