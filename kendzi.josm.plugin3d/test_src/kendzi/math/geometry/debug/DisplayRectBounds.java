package kendzi.math.geometry.debug;

import java.util.List;

import javax.vecmath.Point2d;

public class DisplayRectBounds {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

    boolean isBound = false;


    public void addPoint(Point2d p) {

        if (this.minX > p.x) {
            this.minX = p.x;
        }
        if (this.minY > p.y) {
            this.minY = p.y;
        }

        if (this.maxX < p.x) {
            this.maxX = p.x;
        }
        if (this.maxY < p.y) {
            this.maxY = p.y;
        }

        isBound = true;
    }

    public void addList(List<Point2d> list) {
        if (list == null ) {
            return;
        }

        for (Point2d p : list) {
            addPoint(p);
        }
    }

    public DisplayRectBounds toBount() {
        if (this.isBound) {
            return this;
        }
        return null;
    }

}

