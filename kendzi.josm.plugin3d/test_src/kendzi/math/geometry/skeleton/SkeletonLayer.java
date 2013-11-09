/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.geometry.skeleton;

import java.awt.Graphics2D;

import kendzi.swing.ui.panel.equation.EquationDisplay;
import kendzi.swing.ui.panel.equation.EquationLayer;

public class SkeletonLayer extends EquationLayer {
	private Skeleton.SkeletonOutput output;

	public SkeletonLayer(Skeleton.SkeletonOutput output) {
		this.output = output;
	}



	@Override
    public void draw(Graphics2D g2d, EquationDisplay disp) {

		if (this.output == null) {
			return;
		}


//		g2d.setColor(Color.BLACK);
//		if (lokalizacja instanceof KalmanInt) {
//			KalmanInt kloc = (KalmanInt) lokalizacja;
//
//			KalmanLogInt kalmanLog = kloc.getKalmanLog();
//			if (kalmanLog != null) {
//
//				kalmanLog.draw(g2d, disp);
//
//			}
//
//		}
//
//		// Graphics2D g2d = (Graphics2D)g.create();
//
//		// g2d.setColor(Color.WHITE);
//		// g2d.fillRect(0, 0, 30, 30);
//		String str = "x: " + RoboUtil.ff(lokalizacja.getX()) + " y: "
//				+ RoboUtil.ff(lokalizacja.getY()) + " th: "
//				+ RoboUtil.ff(lokalizacja.getTh());
//
//		g2d.drawString(str, 100, 20);
//
//		g2d.setColor(Color.RED.brighter());
//
//		int x = (int) disp.xPositionToPixel(lokalizacja.getX());
//		int y = (int) disp.yPositionToPixel(lokalizacja.getY());
//		g2d.translate(x, y);
//		g2d.rotate(-lokalizacja.getTh());
//		g2d.fillOval(-10, -10, 20, 20);
//
//		g2d.setColor(Color.BLUE.brighter());
//		g2d.fillRect(0, -3, 15, 6);
//
//		// g2d.dispose();

	}

}
