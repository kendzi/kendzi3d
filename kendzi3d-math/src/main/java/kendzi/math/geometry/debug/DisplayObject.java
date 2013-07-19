/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.debug;

import java.awt.Graphics2D;

import kendzi.swing.ui.panel.equation.EquationDisplay;

abstract public class DisplayObject {

	abstract public void draw(Graphics2D g2d, EquationDisplay disp, boolean selected);

	abstract public Object drawObject();

	abstract public DisplayRectBounds getBounds();

}
