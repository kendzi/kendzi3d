package kendzi.math.geometry.debug;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import kendzi.swing.ui.panel.equation.EquationDisplay;
import kendzi.swing.ui.panel.equation.EquationLayer;

public class DebugLayer extends EquationLayer {

    @Override
    public void draw(Graphics2D g2d, EquationDisplay disp) {
        try {

            for (Object key : this.objects.keySet()) {
                DisplayObject displayObject = this.objects.get(key);

                displayObject.draw(g2d, disp, false);
            }

            if (this.lastObjects != null) {
                this.lastObjects.draw(g2d, disp, true);
            }
        } catch (java.util.ConcurrentModificationException ce) {
            // FIXME !!!!!!
            //
        }

    }

    Map<Object, DisplayObject> objects = Collections.synchronizedMap(new LinkedHashMap<Object, DisplayObject>());
    volatile DisplayObject lastObjects = null;

    public void addDebug(String pKey, DisplayObject object) {
        addDebug((Object) pKey, object);
    }


    public void addDebug(Object pKey, DisplayObject object) {

        if (this.lastObjects == null) {
            makeFocus(object);
        }
        this.objects.remove(pKey);
        this.objects.put(pKey, object);
        this.lastObjects = object;

        // XXX do with events!
        DebugDisplay.getDebugDisplay().getMapComponent().repaint(300);

    }

    public void addDebug(DisplayObject object) {
        addDebug(object.drawObject(), object);
    }

    private void makeFocus(DisplayObject object) {
        DisplayRectBounds bounds = object.getBounds();

        if (bounds == null) {
            return;
        }

        double ratio = bounds.maxX - bounds.minX - (bounds.maxY - bounds.minY);
        if (ratio > 0) {
            bounds.maxY += ratio;
        } else {
            bounds.maxX -= ratio;
        }

        //XXX
        DebugDisplay.getDebugDisplay().getMapComponent().setParms(
                bounds.minX  + bounds.maxX / 2,bounds.minY + bounds.maxY  /2,
                bounds.minX - 1, bounds.maxX + 1, bounds.minY - 1, bounds.maxY + 1);

        //XXX
        //        DebugDisplay.getDebugDisplay().getMapComponent().setParms(
        //                bounds.minX  + bounds.maxX / 2,bounds.minY + bounds.maxY  /2,
        //                bounds.minX - 1, bounds.maxX + 1, bounds.minY - 1, bounds.maxY + 1);
        // XXX do with events!
        DebugDisplay.getDebugDisplay().getMapComponent().repaint(300);
    }

    public void clear() {
        this.objects = new LinkedHashMap<Object, DisplayObject>();
        this.lastObjects = null;

        // XXX do with events!
        DebugDisplay.getDebugDisplay().getMapComponent().repaint(300);

    }

}
