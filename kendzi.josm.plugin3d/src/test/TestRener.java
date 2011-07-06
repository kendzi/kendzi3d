/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.visitor.paint.StyledMapRenderer;
import org.openstreetmap.josm.gui.NavigatableComponent;

public class TestRener {
    public void test() throws IOException {

        int size = 600;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = img.createGraphics();
        g.setClip(0, 0, size, size);
        NavigatableComponent nc = new NavigatableComponent();
        nc.setSize(size, size);
        EastNorth newCenter = new EastNorth(1, 1);

        newCenter = Main.map.mapView.getCenter();
       // Main.map.mapView.scale;


        nc.zoomTo(newCenter, 1.9893042298985113);
        StyledMapRenderer sr = new StyledMapRenderer(g, nc, false);

        DataSet ds = Main.main.getCurrentDataSet();
        Main.map.mapView.getRealBounds();

        LatLon min = nc.getLatLon(0, 0);
        LatLon max = nc.getLatLon(size, size);

        Bounds bounds = new Bounds(min, max);

        sr.render(ds, true, bounds);

        g.dispose();

        ImageIO.write(img, "png", new File("/title.png"));

    }
}
