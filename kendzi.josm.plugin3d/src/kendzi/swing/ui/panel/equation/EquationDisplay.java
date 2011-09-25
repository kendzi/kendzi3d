/**
 * Copyright (c) 2006, Sun Microsystems, Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package kendzi.swing.ui.panel.equation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;


/**
 * @author  kendzi
 */
public class EquationDisplay extends JComponent implements PropertyChangeListener {
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_MAJOR_GRID = Color.GRAY.brighter();
    private static final Color COLOR_MINOR_GRID = new Color(220, 220, 220);
    private static final Color COLOR_AXIS = Color.BLACK;

    private static final float STROKE_AXIS = 1.2f;
    private static final float STROKE_GRID = 1.0f;

    private static final float COEFF_ZOOM = 1.1f;

    private List<DrawableEquation> equations;

    protected double minX;
    protected double maxX;
    protected double minY;
    protected double maxY;

    protected double originX;
    protected double originY;

    protected double majorX;
    protected int minorX;
    protected double majorY;
    protected int minorY;

    private double oldWidth;
    private double oldHeight;

    private boolean sameRatio;

    /**
	 * @uml.property  name="drawText"
	 */
    private boolean drawText = true;

    private Point dragStart;

    private NumberFormat formatter;
    /**
	 * @uml.property  name="zoomHandler"
	 * @uml.associationEnd
	 */
    private ZoomHandler zoomHandler;
    /**
	 * @uml.property  name="resizeHandler"
	 * @uml.associationEnd
	 */
    private ComponentResizeHandler resizeHandler;
    /**
	 * @uml.property  name="panMotionHandler"
	 * @uml.associationEnd
	 */
    private PanMotionHandler panMotionHandler;
    /**
	 * @uml.property  name="panHandler"
	 * @uml.associationEnd
	 */
    private PanHandler panHandler;
    private double startMinX;
    private double startMaxX;
    private double startMinY;
    private double startMaxY;
    private int startWidth;
    private int startHeight;

    /**
     * @param originX pozycja srodka mapy x
     * @param originY pozycja srodka mapy x
     * @param minX widoczna czesc
     * @param maxX widoczna czesc
     * @param minY widoczna czesc
     * @param maxY widoczna czesc
     * @param majorX wielkosc podzialu glownej siatki x
     * @param minorX ilosc lini podzednego podzialu x
     * @param majorY wielkosc podzialu glownej siatki y
     * @param minorY ilosc lini podzednego podzialu y
     */
    public EquationDisplay(double originX, double originY,
                           double minX, double maxX,
                           double minY, double maxY,
                           double majorX, int minorX,
                           double majorY, int minorY, boolean sameRatio) {

    	setParms(originX, originY, minX, maxX, minY, maxY, majorX, minorX, majorY, minorY);


        this.equations = new LinkedList<DrawableEquation>();

        this.formatter = NumberFormat.getInstance();
        this.formatter.setMaximumFractionDigits(2);

        this.oldWidth = getWidth();
        this.oldHeight = getHeight();

        panHandler = new PanHandler();
        addMouseListener(panHandler);
        panMotionHandler = new PanMotionHandler();
        addMouseMotionListener(panMotionHandler);
        zoomHandler = new ZoomHandler();
        addMouseWheelListener(zoomHandler);
        resizeHandler = new ComponentResizeHandler();
        addComponentListener(resizeHandler);

        this.sameRatio = sameRatio;
    }

    public void setParms(
    		double originX, double originY,
            double minX, double maxX,
            double minY, double maxY,
            double majorX, int minorX,
            double majorY, int minorY) {

    	if (originX < minX || originX > maxX) {
            throw new IllegalArgumentException("originX must be between minX and maxX");
        }

        if (minY >= maxY) {
            throw new IllegalArgumentException("minY must be < to maxY");
        }

        if (originY < minY || originY > maxY) {
            throw new IllegalArgumentException("originY must be between minY and maxY");
        }

        if (minorX <= 0) {
            throw new IllegalArgumentException("minorX must be > 0");
        }

        if (minorY <= 0) {
            throw new IllegalArgumentException("minorY must be > 0");
        }

        if (majorX <= 0.0) {
            throw new IllegalArgumentException("majorX must be > 0.0");
        }

        if (majorY <= 0.0) {
            throw new IllegalArgumentException("majorY must be > 0.0");
        }

        this.originX = originX;
        this.originY = originY;

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;

        this.startMinX = minX;
        this.startMaxX = maxX;
        this.startMinY = minY;
        this.startMaxY = maxY;

        this.startWidth = getWidth();
        this.startHeight = getHeight();


        this.majorX = majorX;
        this.minorX = minorX;
        this.majorY = majorY;
        this.minorY = minorY;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled) {
            //super.setEnabled(enabled);

            if (enabled) {
                addMouseListener(panHandler);
                addMouseMotionListener(panMotionHandler);
                addMouseWheelListener(zoomHandler);
            } else {
                removeMouseListener(panHandler);
                removeMouseMotionListener(panMotionHandler);
                removeMouseWheelListener(zoomHandler);
            }
        }
    }

    /**
	 * @return
	 * @uml.property  name="drawText"
	 */
    public boolean isDrawText() {
        return drawText;
    }

    /**
	 * @param drawText
	 * @uml.property  name="drawText"
	 */
    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public void addEquation(AbstractEquation equation, Color color) {
        if (equation != null && !equations.contains(equation)) {
            equation.addPropertyChangeListener(this);
            equations.add(new DrawableEquation(equation, color));
            repaint();
        }
    }

    public void removeEquation(AbstractEquation equation) {
        if (equation != null) {
            DrawableEquation toRemove = null;
            for (DrawableEquation drawable: equations) {
                if (drawable.getEquation() == equation) {
                    toRemove = drawable;
                    break;
                }
            }

            if (toRemove != null) {
                equation.removePropertyChangeListener(this);
                equations.remove(toRemove);
                repaint();
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    public double yPositionToPixel(double position) {
        double height = getHeight();
        return height - ((position - minY) * height / (maxY - minY));
    }

    public double xPositionToPixel(double position) {
        return (position - minX) * getWidth() / (maxX - minX);
    }

    public double xPixelToPosition(double pixel) {
        double axisV = xPositionToPixel(originX);
        return (pixel - axisV) * (maxX - minX) / getWidth();
    }

    public double yPixelToPosition(double pixel) {
        double axisH = yPositionToPixel(originY);
        return (getHeight() - pixel - axisH) * (maxY - minY) / getHeight();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        setupGraphics(g2);

        paintBackground(g2);
//        drawGrid(g2);
//        drawAxis(g2);
//
//        drawEquations(g2);

        paintInformation(g2);
    }

    protected void paintInformation(Graphics2D g2) {
    }

    private void drawEquations(Graphics2D g2) {
        for (DrawableEquation drawable: equations) {
            g2.setColor(drawable.getColor());
            drawEquation(g2, drawable.getEquation());
        }
    }

    private void drawEquation(Graphics2D g2, AbstractEquation equation) {
        float x = 0.0f;
        float y = (float) yPositionToPixel(equation.compute(xPixelToPosition(0.0)));

        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);

        for (x = 0.0f; x < getWidth(); x += 1.0f) {
            double position = xPixelToPosition(x);
            y = (float) yPositionToPixel(equation.compute(position));
            path.lineTo(x, y);
        }

        g2.draw(path);
    }

    public void drawGrid(Graphics2D g2) {
        Stroke stroke = g2.getStroke();

        drawVerticalGrid(g2);
        drawHorizontalGrid(g2);

        if (drawText) {
            drawVerticalLabels(g2);
            drawHorizontalLabels(g2);
        }

        g2.setStroke(stroke);
    }

    private void drawHorizontalLabels(Graphics2D g2) {
        double axisV = xPositionToPixel(originX);

        g2.setColor(COLOR_AXIS);
        for (double y = originY + majorY; y < maxY + majorY; y += majorY) {
            int position = (int) yPositionToPixel(y);
            g2.drawString(formatter.format(y), (int) axisV + 5, position);
        }

        for (double y = originY - majorY; y > minY - majorY; y -= majorY) {
            int position = (int) yPositionToPixel(y);
            g2.drawString(formatter.format(y), (int) axisV + 5, position);
        }
    }

    private void drawHorizontalGrid(Graphics2D g2) {
        double minorSpacing = majorY / minorY;
        double axisV = xPositionToPixel(originX);

        Stroke gridStroke = new BasicStroke(STROKE_GRID);
        Stroke axisStroke = new BasicStroke(STROKE_AXIS);

        for (double y = originY + majorY; y < maxY + majorY; y += majorY) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (int i = 0; i < minorY; i++) {
                int position = (int) yPositionToPixel(y - i * minorSpacing);
                g2.drawLine(0, position, getWidth(), position);
            }

            int position = (int) yPositionToPixel(y);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(0, position, getWidth(), position);

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine((int) axisV - 3, position, (int) axisV + 3, position);
        }

        for (double y = originY - majorY; y > minY - majorY; y -= majorY) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (int i = 0; i < minorY; i++) {
                int position = (int) yPositionToPixel(y + i * minorSpacing);
                g2.drawLine(0, position, getWidth(), position);
            }

            int position = (int) yPositionToPixel(y);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(0, position, getWidth(), position);

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine((int) axisV - 3, position, (int) axisV + 3, position);
        }
    }

    private void drawVerticalLabels(Graphics2D g2) {
        double axisH = yPositionToPixel(originY);
        FontMetrics metrics = g2.getFontMetrics();

        g2.setColor(COLOR_AXIS);

        for (double x = originX + majorX; x < maxX + majorX; x += majorX) {
            int position = (int) xPositionToPixel(x);
            g2.drawString(formatter.format(x), position, (int) axisH + metrics.getHeight());
        }

        for (double x = originX - majorX; x > minX - majorX; x -= majorX) {
            int position = (int) xPositionToPixel(x);
            g2.drawString(formatter.format(x), position, (int) axisH + metrics.getHeight());
        }
    }

    private void drawVerticalGrid(Graphics2D g2) {
        double minorSpacing = majorX / minorX;
        double axisH = yPositionToPixel(originY);

        Stroke gridStroke = new BasicStroke(STROKE_GRID);
        Stroke axisStroke = new BasicStroke(STROKE_AXIS);

        for (double x = originX + majorX; x < maxX + majorX; x += majorX) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (int i = 0; i < minorX; i++) {
                int position = (int) xPositionToPixel(x - i * minorSpacing);
                g2.drawLine(position, 0, position, getHeight());
            }

            int position = (int) xPositionToPixel(x);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(position, 0, position, getHeight());

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine(position, (int) axisH - 3, position, (int) axisH + 3);
        }

        for (double x = originX - majorX; x > minX - majorX; x -= majorX) {
            g2.setStroke(gridStroke);
            g2.setColor(COLOR_MINOR_GRID);
            for (int i = 0; i < minorX; i++) {
                int position = (int) xPositionToPixel(x + i * minorSpacing);
                g2.drawLine(position, 0, position, getHeight());
            }

            int position = (int) xPositionToPixel(x);
            g2.setColor(COLOR_MAJOR_GRID);
            g2.drawLine(position, 0, position, getHeight());

            g2.setStroke(axisStroke);
            g2.setColor(COLOR_AXIS);
            g2.drawLine(position, (int) axisH - 3, position, (int) axisH + 3);
        }
    }

    public void drawAxis(Graphics2D g2) {
        double axisH = yPositionToPixel(originY);
        double axisV = xPositionToPixel(originX);

        g2.setColor(COLOR_AXIS);
        Stroke stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(STROKE_AXIS));

        g2.drawLine(0, (int) axisH, getWidth(), (int) axisH);
        g2.drawLine((int) axisV, 0, (int) axisV, getHeight());

        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(formatter.format(0.0), (int) axisV + 5, (int) axisH + metrics.getHeight());

        g2.setStroke(stroke);
    }

    protected void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }

    protected void paintBackground(Graphics2D g2) {
        g2.setColor(COLOR_BACKGROUND);
        g2.fill(g2.getClipBounds());
    }

    /**
	 * @author  kendzi
	 */
    private class DrawableEquation {

        private AbstractEquation equation;

        private Color color;

        DrawableEquation(AbstractEquation equation, Color color) {
            this.equation = equation;
            this.color = color;
        }


        AbstractEquation getEquation() {
            return equation;
        }


        Color getColor() {
            return color;
        }
    }

    private class ZoomHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double distanceX = maxX - minX;
            double distanceY = maxY - minY;

            double cursorX = minX + distanceX / 2.0;
            double cursorY = minY + distanceY / 2.0;

            int rotation = e.getWheelRotation();
            if (rotation < 0) {
                distanceX /= COEFF_ZOOM;
                distanceY /= COEFF_ZOOM;
            } else {
                distanceX *= COEFF_ZOOM;
                distanceY *= COEFF_ZOOM;
            }

            minX = cursorX - distanceX / 2.0;
            maxX = cursorX + distanceX / 2.0;
            minY = cursorY - distanceY / 2.0;
            maxY = cursorY + distanceY / 2.0;

            repaint();
        }
    }

    private class PanHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            dragStart = e.getPoint();
        }
    }

    private class ComponentResizeHandler extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent arg0) {
			super.componentResized(arg0);

            int w = getWidth();
            int h = getHeight();
			double width = w;
			double height = h;

            if (( startWidth == 0) || (startHeight == 0)) {
				startWidth = w;
				startHeight = h;

                double dX = startMaxX - startMinX;
                double dY = startMaxY - startMinY;

                double radioX = (startMaxX - startMinX) / width;
                double radioY = (startMaxY - startMinY) / height;

                if (sameRatio) {
                    if (radioY > radioX) {
                        // powiekszamy obszar osi x tak aby zachowac radio
                        double dX2 = radioY * width;
                        double srX = startMinX + dX /2;

                        startMaxX = srX + dX2/2;
                        startMinX = srX - dX2/2;

                        maxX = startMaxX;
                        minX = startMinX;

                    }else {
                        // powiekszamy obszar osi x tak aby zachowac radio
                        double dY2 = radioX * height;
                        double srY = startMinY + dY /2;

                        startMaxY = srY + dY2/2;
                        startMinY = srY - dY2/2;

                        maxY = startMaxY;
                        minY = startMinY;

                    }
                }


			}

			if (( oldWidth == 0) || (oldHeight == 0)) {
				oldWidth = width;
				oldHeight = height;
			}

			double distanceX = (maxX - minX) * (width/oldWidth);
            double distanceY = (maxY - minY) * (height/oldHeight);

            if ((distanceX == 0) || ( distanceY == 0)) {
                distanceX = (startMaxX - startMinX) * (width/startWidth);
                distanceY = (startMaxY - startMinY) * (height/startHeight);
            }

            double cursorX = minX + distanceX / 2.0;
            double cursorY = maxY - distanceY / 2.0;

            minX = cursorX - distanceX / 2.0;
            maxX = cursorX + distanceX / 2.0;
            minY = cursorY - distanceY / 2.0;
            maxY = cursorY + distanceY / 2.0;

            oldWidth = width;
			oldHeight = height;


            repaint();


		}


    }

    private class PanMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            Point dragEnd = e.getPoint();

            double distance = xPixelToPosition(dragEnd.getX()) -
                              xPixelToPosition(dragStart.getX());
            minX -= distance;
            maxX -= distance;

            distance = yPixelToPosition(dragEnd.getY()) -
                       yPixelToPosition(dragStart.getY());
            minY -= distance;
            maxY -= distance;

            repaint();
            dragStart = dragEnd;
        }
    }

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getMajorX() {
		return majorX;
	}

	public void setMajorX(double majorX) {
		this.majorX = majorX;
	}

	public int getMinorX() {
		return minorX;
	}

	public void setMinorX(int minorX) {
		this.minorX = minorX;
	}

	public double getMajorY() {
		return majorY;
	}

	public void setMajorY(double majorY) {
		this.majorY = majorY;
	}

	public int getMinorY() {
		return minorY;
	}

	public void setMinorY(int minorY) {
		this.minorY = minorY;
	}
}
