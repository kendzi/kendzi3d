/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.swing.ui.panel.equation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Displays some graphic on scalable screen.
 */
public class MapComponent extends EquationDisplay implements MapChangeListener {


	private boolean needByRefreash;


	List<Point2D> path = null;


	private boolean drawEmpty = false;



	public MapComponent( ) {
		super(75.0, 75.0,
              40.1, 110.1, 40.1, 110.1,
              10, 2,
              10, 2,
              true);

		JButton b1 = new JButton("menu");

		this.add(b1);

		Insets insets = this.getInsets();
		Dimension size = b1.getPreferredSize();
		b1.setBounds(5 + insets.left, 5 + insets.top,
		             size.width, size.height);

		b1.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				showMenuDialog();
			}
		});


	}

	void showMenuDialog() {
		JDialog jd = new JDialog();
		jd.setVisible(true);
		jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		jd.add(getMenu());
		jd.pack();
	}


	JComponent getMenu() {
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel("ggg");

		listPane.add(label);
		listPane.add(Box.createRigidArea(new Dimension(0,5)));

		listPane.add(new JLabel("ddd"));
		listPane.add(new JLabel("ddd"));
		listPane.add(new JLabel("ddd"));
		listPane.add(new JLabel("ddd"));

		listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		//Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(new JButton("ok"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(new JButton("anuluj"));

		return listPane;
	}


	public static void main(String[] args) {
		MapComponent mc = new MapComponent();
		mc.createAndShowGUI();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	void drawPath(Graphics g, List<Point2D> path) {

		Graphics2D g2d = (Graphics2D)g.create();

		g2d.setColor(Color.GREEN);

		if (path == null) {
			return;
		}

		int nPoints = path.size();
		int [] xPoints = new int[nPoints];
		int [] yPoints = new int[nPoints];
		int i = 0;
		for ( Point2D p : path) {
			xPoints[i] = (int) xPositionToPixel(p.getX());
			yPoints[i] = (int) yPositionToPixel(p.getY());
			i++;
		}
		g2d.drawPolyline(xPoints, yPoints, nPoints);

		for ( i =0; i <nPoints; i++ ) {
			g2d.fillOval(xPoints[i]-4, yPoints[i]-4, 8, 8);

		}

		g2d.dispose();
	}

    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);

    	Graphics2D g2d;
    	g2d = (Graphics2D)g.create();


    	g2d.setColor(Color.WHITE);

    	drawGrid(g2d);
    	drawAxis(g2d);

    	drawPath(g2d, path);

    	for (EquationLayer ml : mapLayer) {
    		ml.draw(g2d, this);
    	}

        // done with g2d, dispose it
        g2d.dispose();

    }

    public void createAndShowGUI() {
        JFrame f = new JFrame("Oval");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 600);
        f.add(this);

        f.setVisible(true);
    }

	@Override
	public void dispatchMapChange() {
		needByRefreash = true;
	}


	List<EquationLayer> mapLayer = new ArrayList<EquationLayer>();

	private JButton jb;
	public void addLayer(EquationLayer map) {
		mapLayer.add(map);
	}

    public void setParms(
            double originX, double originY,
            double minX, double maxX,
            double minY, double maxY) {
        setParms(originX, originY, minX, maxX, minY, maxY, this.majorX, this.minorX, this.majorY, this.minorY);
    }
}
