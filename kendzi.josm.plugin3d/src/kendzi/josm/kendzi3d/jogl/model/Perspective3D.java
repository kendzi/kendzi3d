/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.model;

import javax.vecmath.Point2d;

import kendzi.josm.kendzi3d.perspective.Perspective;

import org.apache.log4j.Logger;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;

/**
 *  Describe how to translate coordinates from EastNorth perspective to openGl perspective.
 *
 *  Center coordinates are used because of openGl numerical errors in large distances from point (0,0)
 *  It is possible that I do something wrong and they are not needed.
 *
 * @author Tomasz Kedziora (Kendzi)
 *
 */
public class Perspective3D implements Perspective {

    /** Log. */
    private static final Logger log = Logger.getLogger(Perspective3D.class);

	private double scale;
	private double centerX;
	private double centerY;

	public Perspective3D(double scale, double centerX, double centerY) {
		this.scale = scale;
		this.centerX = centerX;
		this.centerY = centerY;
		log.info("**************-----> " + scale);
	}

	public double calcX(double x) {
		return (x - this.centerX) * this.scale;
	}

	public double calcY(double y) {
		return (y - this.centerY) * this.scale;
	}

	/**
     * {@inheritDoc}
     *
     * @see kendzi.josm.kendzi3d.jogl.model.Perspective#calcPoint(org.openstreetmap.josm.data.osm.Node)
     */
	@Override
    public Point2d calcPoint(Node node) {
		EastNorth eastNorth = node.getEastNorth();
		return calcPoint(eastNorth);
	}

	public Point2d calcPoint(EastNorth eastNorth) {
	    return new Point2d(calcX(eastNorth.getX()), calcY(eastNorth.getY()));
	}

	@Override
	public String toString() {
		return "Perspective3D [scale=" + this.scale + ", centerX=" + this.centerX + ", centerY="
				+ this.centerY + "]";
	}

    /** Backward projection from local camera coordinate system to global EastNorth coordinate system.
     * @param x coordinate x
     * @param y coordinate y
     * @return location in EastNorth coordinate system
     */
    public EastNorth toEastNorth(double x, double y) {
        return new EastNorth(x/this.scale + this.centerX, y/this.scale + this.centerY);
    }

}
