/*
 * This software is provided "AS IS" without a warranty of any kind. You use it
 * on your own risk and responsibility!!! This file is shared under BSD v3
 * license. See readme.txt and BSD3 file for details.
 */
package kendzi.josm.kendzi3d.ui.layer;

import static org.openstreetmap.josm.tools.I18n.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.Icon;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.dialogs.LayerListDialog;
import org.openstreetmap.josm.gui.dialogs.LayerListPopup;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.ImageProvider;

import kendzi.jogl.camera.Camera;
import kendzi.josm.kendzi3d.data.perspective.Perspective3D;
import kendzi.josm.kendzi3d.data.perspective.Perspective3dProvider;

/**
 * Layer showing location of camera.
 *
 * @author Tomasz Kędziora (Kendzi)
 */
public class CameraLayer extends Layer implements LayerChangeListener {

    private final Camera camera;
    private final Perspective3dProvider perspective3dProvider;

    private double lastX = Double.MAX_VALUE;
    private double lastY = Double.MAX_VALUE;
    private double lastAngle;
    private final Timer timer = new Timer("kendzi3d.layer.refreash");

    /**
     * Constructor.
     *
     * @param perspective3dProvider
     *            perspective provider
     * @param camera
     *            camera location
     */
    public CameraLayer(Camera camera, Perspective3dProvider perspective3dProvider) {
        super(tr("Kendzi3d camera layer"));

        this.camera = camera;
        this.perspective3dProvider = perspective3dProvider;

        registerLayerChangeListener();

        initTimer();
    }

    private void registerLayerChangeListener() {
        MainApplication.getLayerManager().addLayerChangeListener(this);
    }

    private void initTimer() {
        int time = 600;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (MainApplication.getMap() != null && isChanged()) {
                    // XXX currently there is no change listener for camera, so
                    // we need redraw camera position from time to time.
                    // System.out.println(System.currentTimeMillis() +
                    // " force map to repaint");

                    // MainApplication.getMap().repaint(200);

                    if (isChanged()) {
                        invalidate();
                    }
                }
            }
        }, time, time);

    }

    /**
     * Return a static icon.
     */
    @Override
    public Icon getIcon() {
        return new ImageProvider("stock_3d-effects24").setMaxSize(16).get();
    }

    @Override
    public void paint(final Graphics2D g, final MapView mv, Bounds bounds) {

        drawCamera(camera, Color.RED, g, mv);
    }

    /**
     * Draws camera.
     *
     * @param camera
     *            camera location
     * @param color
     *            color of camera on layer
     * @param g
     *            graphics 2d
     * @param mv
     *            map view
     *
     */
    protected void drawCamera(Camera camera, Color color, final Graphics2D g, final MapView mv) {

        double cameraX = get2dX(camera);
        double cameraY = get2dY(camera);
        double cameraAngle = get2dAngle(camera);

        Perspective3D perspective3d = perspective3dProvider.getPerspective3d();

        if (perspective3d == null) {
            return;
        }

        EastNorth eastNorth = perspective3d.toEastNorth(cameraX, cameraY);

        Point2D point2d = mv.getPoint2D(eastNorth);

        int x = (int) point2d.getX();
        int y = (int) point2d.getY();

        g.setColor(color);

        double drawAngle1 = cameraAngle - Math.PI / 4d;
        double drawAngle2 = cameraAngle + Math.PI / 4d;

        int lenght = 30;
        int lenght2 = lenght + lenght;

        g.drawArc(x - lenght, y - lenght, lenght2, lenght2, (int) Math.toDegrees(drawAngle1), 90);

        int endX = x + (int) (Math.cos(drawAngle1) * lenght);
        int endY = y - (int) (Math.sin(drawAngle1) * lenght);
        g.drawLine(x, y, endX, endY);

        int end2X = x + (int) (Math.cos(drawAngle2) * lenght);
        int end2Y = y - (int) (Math.sin(drawAngle2) * lenght);
        g.drawLine(x, y, end2X, end2Y);

        // g.drawLine(endX, endY, end2X, end2Y);

        boolean selected = true;
        if (selected) {
            g.fillOval(x - 7, y - 7, 14, 14);
        } else {
            g.drawOval(x - 7, y - 7, 14, 14);
        }

        lastX = cameraX;
        lastY = cameraY;
        lastAngle = cameraAngle;
    }

    /**
     * @param camera
     * @return
     */
    private double get2dX(Camera camera) {
        return camera.getPoint().x;
    }

    /**
     * @param camera
     * @return
     */
    private double get2dY(Camera camera) {
        return -camera.getPoint().z;
    }

    private double get2dAngle(Camera camera) {
        return camera.getAngle().y;
    }

    @Override
    public String getToolTipText() {
        return "<html>" + tr("Kendzi3d camera location") + "</html>";
    }

    @Override
    public void mergeFrom(Layer from) {
        //
    }

    @Override
    public boolean isMergable(Layer other) {
        return false;
    }

    public boolean isChanged() {
        return lastX != get2dX(camera) || lastY != get2dY(camera) || lastAngle != get2dAngle(camera);
    }

    @Override
    public void visitBoundingBox(BoundingXYVisitor v) {
        System.out.println("!!!!! " + v);
    }

    @Override
    public Object getInfoComponent() {
        return getToolTipText();
    }

    @Override
    public boolean isBackgroundLayer() {
        return true;
    }

    @Override
    public Action[] getMenuEntries() {
        return new Action[] { LayerListDialog.getInstance().createShowHideLayerAction(),
                LayerListDialog.getInstance().createDeleteLayerAction(), SeparatorLayerAction.INSTANCE,
                // new RenameLayerAction(null, this),
                SeparatorLayerAction.INSTANCE, new LayerListPopup.InfoAction(this) };
    }

    @Override
    public void destroy() {
        //
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
    }

    @Override
    public void layerAdded(LayerAddEvent evt) {
        addCameraLayer();
    }

    /**
     * If layer is the OSM Data layer, remove all errors
     */
    @Override
    public void layerRemoving(LayerRemoveEvent evt) {
        if (isLastOsmDataLayerBeingRemoved(evt)) {
            // remove camera layer after all osm data layers have been removed
            if (MainApplication.getLayerManager().containsLayer(this)) {
                MainApplication.getLayerManager().removeLayer(this);
            }
        }
    }

    private boolean isLastOsmDataLayerBeingRemoved(LayerRemoveEvent evt) {
        return evt.getRemovedLayer() instanceof OsmDataLayer && !isOsmDataLayer();
    }

    private boolean isOsmDataLayer() {
        for (Layer layer : MainApplication.getLayerManager().getLayers()) {
            if (layer instanceof OsmDataLayer) {
                return true;
            }
        }
        return false;
    }

    public void addCameraLayer() {
        if (MainApplication.getMap() == null || Main.main == null || MainApplication.getMap().mapView == null) {
            return;
        }
        if (isOsmDataLayer()) {
            if (!MainApplication.getLayerManager().containsLayer(this)) {
                MainApplication.getLayerManager().addLayer(this);
            }
        }
    }
}
