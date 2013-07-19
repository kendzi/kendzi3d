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
 * rysuje mape ?!
 * @author
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


//		setLayout(new GridBagLayout());
//
//		jb = new JButton("test");
//		this.add(jb, GridBagLayout.LINE_END);

		JButton b1 = new JButton("menu");

		this.add(b1);

		Insets insets = this.getInsets();
		Dimension size = b1.getPreferredSize();
		b1.setBounds(5 + insets.left, 5 + insets.top,
		             size.width, size.height);

		b1.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				showMenuDialog();
			}

		});


	}

	void showMenuDialog() {
//		JDialog jd = new JDialog() {
//
//		};
//
//		jd.setVisible(true);

		JDialog jd = new JDialog();
		jd.setVisible(true);
		jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		jd.add(getMenu());
		jd.pack();
	}


	JComponent getMenu() {
//		JPanel jp = new JPanel();
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

//	Shape calcMapCell() {
//		Shape mapCell;
//		int size = obscitleMap.getShapeList()[0].length;
//		int [] x = new int[size];
//		int [] y = new int[size];
//		int count = 0;
//		for (Point2D p : obscitleMap.getShapeList()[0]) {
//			x[count] = (int)(xPositionToPixel(p.getX())-xPositionToPixel(0));
//			y[count] = (int)(yPositionToPixel(p.getY())-yPositionToPixel(0));
//			count++;
//		}
//
//		mapCell = new Polygon(x,y,size) ;
//		return mapCell;
//	}
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="p"
	 */
//	Point2D.Double p = new Point2D.Double(0.5,0);
//	Double alfa = Math.toRadians(new Double(90.0));


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

//		g2d.fillOval(-10, -10, 20, 20);
//
//		g2d.setColor(Color.BLUE.brighter());


		g2d.dispose();
	}

//	void drawPoint(Graphics g) {
//
//		if (lokalizacja == null) {
//			return;
//		}
//
//		Graphics2D g2d = (Graphics2D)g.create();
//
////		g2d.setColor(Color.WHITE);
////		g2d.fillRect(0, 0, 30, 30);
//
//		g2d.setColor(Color.RED.brighter());
//
//		int x = (int) xPositionToPixel(lokalizacja.getX());
//		int y = (int) yPositionToPixel(lokalizacja.getY());
//		g2d.translate(x,y);
//		g2d.rotate(-lokalizacja.getTh());
//		g2d.fillOval(-10, -10, 20, 20);
//
//		g2d.setColor(Color.BLUE.brighter());
//		g2d.fillRect(0, -3, 15, 6);
//
//		g2d.dispose();
//	}

//	void drawObscitleMap(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g.create();
//
//		Shape mapCell = calcMapCell();
//
////		Shape
//		double x2 = xPixelToPosition(getWidth());
//		double y2= -yPixelToPosition(0);
//
//		double x1 = xPixelToPosition(0);
//		double y1 = -yPixelToPosition(getHeight());
//
//
//
////		int xf = (int) xPositionToPixel(0);
////		int yf = (int) yPositionToPixel(0);
////		g2d.translate(xf,yf);
////		g2d.fillOval(-10, -10, 20, 20);
//
//		//g2d.translate(xPositionToPixel(0),yPositionToPixel(0));
////		g2d.setColor(Color.black);
////		g2d.fillOval(-10, -10, 20, 20);
////		g2d.setColor(Color.green);
////		g2d.fill(mapCell);
//
//
//		List<Point> cellsIndexFromArea = obscitleMap.getCellsIndexFromArea(x1, y1, x2, y2);
////		System.out.println("************");
//		for (Point p : cellsIndexFromArea ) {
//			double cellX = xPositionToPixel(obscitleMap.xCellCenterIndex(p.x, p.y));
//			double cellY = yPositionToPixel(obscitleMap.yCellCenterIndex(p.x, p.y));
////			System.out.println(p + " > x=" + obscitleMap.xCellCenterIndex(p.x, p.y) +
////					" y=" +obscitleMap.yCellCenterIndex(p.x, p.y) +" > x=" + cellX+"y="+cellY);
//
//			if ( ObscitleSquareMap.FULL_CELL ==  obscitleMap.getCelValueIndex(p)) {
//				g2d.setColor(Color.GRAY.brighter());
//				g2d.translate(cellX,cellY);
//				g2d.fill(mapCell);
//				g2d.translate(-cellX,-cellY);
//			} else if (drawEmpty ){
//				g2d.setColor(Color.WHITE.brighter());
//				g2d.translate(cellX,cellY);
//				g2d.fill(mapCell);
//				g2d.translate(-cellX,-cellY);
//			}
//
//
//		}
//
//		g2d.dispose();
//	}


    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);

//    	jb.paintComponents(g);
    	//if(true)
    	//return;

    	Graphics2D g2d;
    	g2d = (Graphics2D)g.create();


    	g2d.setColor(Color.WHITE);
    	//g2d.fillRect(0, 0, getWidth(), getHeight());



//    	BufferedImage bf = null;
//    	if (bf == null ) {
//             bf = getGraphicsConfiguration().createCompatibleImage(30, 30);
//             Graphics2D gb = (Graphics2D) bf.getGraphics();
//             gb.setColor(Color.WHITE);
//             gb.fillRect(0, 0, 30, 30);
//             gb.setColor(Color.RED.brighter());
//             gb.fillOval(5,  5, 20, 20);
//             gb.setColor(Color.BLUE.brighter());
//             gb.fillRect(15, 12, 15, 6);
//         }
//        g2d.drawImage(bf, 0-15, 0-15, null);


    	//g2d.translate(p.x, -p.y);

//    	drawObscitleMap(g2d);

    	drawGrid(g2d);
    	drawAxis(g2d);

//    	drawPoint(g2d);

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
//        f.add(new EquationDisplay(0.0, 0.5,
//                -0.1, 1.1, -0.1, 1.1,
//                0.2, 6,
//                0.2, 6));

        f.setVisible(true);
    }

//    public static void main(String args[]) {
//
//
//        Runnable doCreateAndShowGUI = new Runnable() {
//            public void run() {
//            	MapComponent mc = sampeleMapComponent();
//                mc.createAndShowGUI();
//            }
//        };
//        SwingUtilities.invokeLater(doCreateAndShowGUI);
//    }


//	public Point2D.Double getP() {
//		return p;
//	}
//
//
//
//	public void setP(Point2D.Double p) {
//		this.p = p;
//	}

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
