/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.jogl.model.geometry;

public class TextCoord {
	public double u;
	public double v;


	/**
	 * @param u x
	 * @param v y
	 */
	public TextCoord(double u, double v) {
		this.u = u;
		this.v = v;
	}

	public TextCoord() {
		this(0, 0);
	}


}
